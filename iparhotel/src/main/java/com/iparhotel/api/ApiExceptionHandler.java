package com.iparhotel.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.iparhotel.excepciones.ReservaException;

/** Errores de la API en formato ProblemDetail (application/problem+json). Solo para el paquete api. */
@RestControllerAdvice(basePackages = "com.iparhotel.api")
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	/** Reglas de negocio: 400 datos no validos, 404 habitacion inexistente, 409 habitacion no disponible. */
	@ExceptionHandler(ReservaException.class)
	public ProblemDetail reserva(ReservaException ex) {
		HttpStatus estado = switch (ex.getMotivo()) {
			case DATOS_NO_VALIDOS -> HttpStatus.BAD_REQUEST;
			case HABITACION_NO_ENCONTRADA -> HttpStatus.NOT_FOUND;
			case HABITACION_NO_DISPONIBLE -> HttpStatus.CONFLICT;
		};
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, ex.getMessage());
		problema.setProperty("motivo", ex.getMotivo().name());
		return problema;
	}

	/** Login con credenciales incorrectas: 401 (mismo mensaje exista o no el email, para no dar pistas). */
	@ExceptionHandler(AuthenticationException.class)
	public ProblemDetail autenticacion(AuthenticationException ex) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos");
	}

	/** @Valid sobre el cuerpo: 400 con el error de cada campo. */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, String> errores = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage()));

		ProblemDetail problema = ex.getBody();
		problema.setDetail("Los datos enviados no son válidos");
		problema.setProperty("errores", errores);
		return handleExceptionInternal(ex, problema, headers, status, request);
	}

	/** JSON mal formado o una fecha que no se puede leer: 400. */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				"El cuerpo de la petición no es un JSON válido (la fecha va como AAAA-MM-DD)");
		return handleExceptionInternal(ex, problema, headers, status, request);
	}
}
