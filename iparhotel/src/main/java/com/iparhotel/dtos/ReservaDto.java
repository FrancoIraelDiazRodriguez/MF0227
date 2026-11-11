package com.iparhotel.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.iparhotel.modelo.Reserva;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** DTO de salida de una reserva. */
@Getter
@AllArgsConstructor
public class ReservaDto {

	private final Long id;
	private final String numeroHabitacion;
	private final String tipoHabitacion;
	private final String nombreUsuario;
	private final String emailUsuario;
	private final LocalDate fechaEntrada;
	private final LocalDate fechaSalida;
	private final int noches;
	private final BigDecimal importe;

	public static ReservaDto desde(Reserva r) {
		return new ReservaDto(r.getId(), r.getHabitacion().getNumero(), r.getHabitacion().getTipo(),
				r.getUsuario().getNombre(), r.getUsuario().getEmail(), r.getFechaEntrada(), r.getFechaSalida(),
				r.getNoches(), r.getImporte());
	}
}
