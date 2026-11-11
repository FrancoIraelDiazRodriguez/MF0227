package com.iparhotel.excepciones;

/** Error de negocio del hotel: noches no validas, reserva sin habitacion, sin fecha... */
public class ReservaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReservaException(String mensaje) {
		super(mensaje);
	}

	public ReservaException(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}
