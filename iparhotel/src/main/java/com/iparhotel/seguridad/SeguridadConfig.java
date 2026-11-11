package com.iparhotel.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SeguridadConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain filtroSeguridad(HttpSecurity http) throws Exception {
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
}
