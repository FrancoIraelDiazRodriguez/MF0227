package com.iparhotel.seguridad;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Los 401 y 403 de la API los produce Spring Security en sus filtros, antes de llegar a los
 * controladores, asi que el ApiExceptionHandler no los ve. Aqui se escriben en JSON (ProblemDetail).
 */
@Component
public class RespuestasSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

	private final ObjectMapper objectMapper;
	// Pone el 401 y la cabecera estandar WWW-Authenticate: Bearer ...
	private final BearerTokenAuthenticationEntryPoint bearer = new BearerTokenAuthenticationEntryPoint();

	public RespuestasSeguridad(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/** 401: falta el token, esta mal formado, la firma no coincide o ha caducado. */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
			throws IOException {
		bearer.commence(request, response, e);
		escribir(response, HttpStatus.UNAUTHORIZED, "Necesitas un token válido para acceder a este recurso.");
	}

	/** 403: el token es valido pero el rol no tiene permiso. */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
			throws IOException {
		response.setStatus(HttpStatus.FORBIDDEN.value());
		escribir(response, HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta operación.");
	}

	private void escribir(HttpServletResponse response, HttpStatus estado, String detalle) throws IOException {
		response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getOutputStream(), ProblemDetail.forStatusAndDetail(estado, detalle));
	}
}
