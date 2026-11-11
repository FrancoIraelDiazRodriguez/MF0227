package com.iparhotel.seguridad;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.iparhotel.modelo.Usuario;

/** Genera el JWT firmado con HS256 que se entrega en el login de la API. */
@Service
public class TokenService {

	private final JwtEncoder jwtEncoder;
	private final String emisor;
	private final long caducidadMinutos;

	public TokenService(JwtEncoder jwtEncoder, @Value("${jwt.emisor}") String emisor,
			@Value("${jwt.caducidad-minutos}") long caducidadMinutos) {
		this.jwtEncoder = jwtEncoder;
		this.emisor = emisor;
		this.caducidadMinutos = caducidadMinutos;
	}

	public String generar(Usuario usuario) {
		Instant ahora = Instant.now();

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(emisor)                                               // iss
				.issuedAt(ahora)                                              // iat
				.expiresAt(ahora.plus(caducidadMinutos, ChronoUnit.MINUTES))  // exp
				.subject(usuario.getEmail())                                  // sub: el email
				.claim("nombre", usuario.getNombre())
				.claim("rol", usuario.getRol().name())                        // CLIENTE o ADMINISTRADOR
				.build();

		JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
		return jwtEncoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
	}
}
