package com.iparhotel.seguridad;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

/**
 * Dos cadenas de filtros:
 * 1) /api/** (fase 3): sin sesion, con token JWT en la cabecera Authorization y CORS para Angular.
 * 2) el resto (fase 2): la web Thymeleaf con login de formulario y sesion.
 */
@Configuration
@EnableWebSecurity
public class SeguridadConfig {

	private final SecretKey clave;

	/** El secreto llega de application.properties, que a su vez lo lee de la variable de entorno JWT_SECRETO. */
	public SeguridadConfig(@Value("${jwt.secreto}") String secreto) {
		byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
		if (bytes.length < 32) {
			throw new IllegalStateException("JWT_SECRETO debe tener al menos 32 caracteres (256 bits) para firmar con HS256");
		}
		this.clave = new SecretKeySpec(bytes, "HmacSHA256");
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/** El login de la API comprueba email y contrasena contra la base de datos (mismo UserDetailsService que la web). */
	@Bean
	AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider proveedor = new DaoAuthenticationProvider(userDetailsService);
		proveedor.setPasswordEncoder(passwordEncoder);
		return new ProviderManager(proveedor);
	}

	/** Firma los tokens con HS256 y la clave secreta. */
	@Bean
	JwtEncoder jwtEncoder() {
		return new NimbusJwtEncoder(new ImmutableSecret<>(clave));
	}

	/** Comprueba la firma HS256, la caducidad (exp) y que el emisor (iss) sea esta API. */
	@Bean
	JwtDecoder jwtDecoder(@Value("${jwt.emisor}") String emisor) {
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(clave).macAlgorithm(MacAlgorithm.HS256).build();
		decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(emisor));
		return decoder;
	}

	/** Convierte el claim "rol" del token (CLIENTE, ADMINISTRADOR) en la autoridad ROLE_CLIENTE / ROLE_ADMINISTRADOR. */
	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
		roles.setAuthoritiesClaimName("rol");
		roles.setAuthorityPrefix("ROLE_");

		JwtAuthenticationConverter convertidor = new JwtAuthenticationConverter();
		convertidor.setJwtGrantedAuthoritiesConverter(roles);
		return convertidor;
	}

	/** Fase 3: API REST protegida con JWT. Va primero (@Order(1)) y solo atiende /api/**. */
	@Bean
	@Order(1)
	SecurityFilterChain filtroApi(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter,
			RespuestasSeguridad respuestas) throws Exception {
		http
			.securityMatcher("/api/**")
			.cors(Customizer.withDefaults())
			// API sin sesiones ni cookies: el token viaja en cada peticion, asi que CSRF no aplica
			.csrf(csrf -> csrf.disable())
			.sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.GET, "/api/v2/habitaciones").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v2/usuarios/login").permitAll()
				.anyRequest().authenticated())
			.oauth2ResourceServer(oauth -> oauth
				.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
				.authenticationEntryPoint(respuestas))
			.exceptionHandling(errores -> errores
				.authenticationEntryPoint(respuestas)
				.accessDeniedHandler(respuestas));
		return http.build();
	}

	/** Fase 2: web Thymeleaf con login de formulario. */
	@Bean
	@Order(2)
	SecurityFilterChain filtroWeb(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(auth -> auth
				// Publico: listado de habitaciones, login, estilos y paginas de error
				.requestMatchers("/", "/login", "/css/**", "/error").permitAll()
				// Solo el administrador
				.requestMatchers("/admin", "/admin/**").hasRole("ADMINISTRADOR")
				// El resto (/habitacion/{id}, /reservas) exige haber iniciado sesion
				.anyRequest().authenticated())
			.formLogin(form -> form
				.loginPage("/login")          // pagina de login propia
				.usernameParameter("email")
				.defaultSuccessUrl("/", false)
				.failureUrl("/login?error")
				.permitAll())
			.logout(logout -> logout
				.logoutSuccessUrl("/login?salir")
				.permitAll());
		return http.build();
	}

	/** CORS: solo la aplicacion Angular (http://localhost:4200) puede llamar a la API desde el navegador. */
	@Bean
	CorsConfigurationSource corsConfigurationSource(@Value("${cors.origen-permitido}") String origen) {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of(origen));
		config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
		fuente.registerCorsConfiguration("/api/**", config);
		return fuente;
	}
}
