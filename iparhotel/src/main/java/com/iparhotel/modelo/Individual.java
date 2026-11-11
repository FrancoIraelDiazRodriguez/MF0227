package com.iparhotel.modelo;

import java.math.BigDecimal;

/** Habitacion individual. Precio = precioNoche * noches. */
public class Individual extends Habitacion {

	public Individual() {
		super();
	}

	public Individual(Long id, String numero, int capacidad, BigDecimal precioNoche, boolean disponible) {
		super(id, numero, capacidad, precioNoche, disponible);
	}

	@Override
	public BigDecimal calcularPrecio(int noches) {
		return redondear(precioBase(noches));
	}

	@Override
	public String getTipo() {
		return "Individual";
	}
}
