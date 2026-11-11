package com.iparhotel.excepciones;

/**
 * Error de negocio del hotel: noches no validas, habitacion inexistente u ocupada...
 *
 * Fase 3: lleva un motivo para que la API sepa que codigo HTTP devolver
 * (400, 404 o 409) sin que el servicio tenga que saber nada de HTTP.
 */
public class ReservaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public enum Motivo {
		DATOS_NO_VALIDOS,          // 400
		HABITACION_NO_ENCONTRADA,  // 404
		HABITACION_NO_DISPONIBLE   // 409
	}

	private final Motivo motivo;

	/** Constructor de la fase 1: por defecto son datos no validos. */
	public ReservaException(String mensaje) {
		this(Motivo.DATOS_NO_VALIDOS, mensaje);
	}

	public ReservaException(Motivo motivo, String mensaje) {
		super(mensaje);
		this.motivo = motivo;
	}

	public Motivo getMotivo() {
		return motivo;
	}
}
