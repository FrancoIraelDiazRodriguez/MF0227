package com.iparhotel.dtos;

import java.math.BigDecimal;

import com.iparhotel.modelo.Habitacion;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** DTO de salida: lo que las vistas necesitan de una habitacion. */
@Getter
@AllArgsConstructor
public class HabitacionDto {

	private final Long id;
	private final String numero;
	private final String tipo;
	private final int capacidad;
	private final BigDecimal precioNoche;

	public static HabitacionDto desde(Habitacion h) {
		return new HabitacionDto(h.getId(), h.getNumero(), h.getTipo(), h.getCapacidad(), h.getPrecioNoche());
	}
}
