package com.iparhotel.modelo;

import java.math.BigDecimal;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** Habitacion doble. Precio = precioNoche * noches, mas 20 euros por noche si lleva cama supletoria. */
@Entity
@DiscriminatorValue("DOBLE")
public class Doble extends Habitacion {

	public static final BigDecimal SUPLEMENTO_CAMA_NOCHE = new BigDecimal("20.00");

	private boolean camaSupletoria;

	public Doble() {
		super();
	}

	public Doble(Long id, String numero, int capacidad, BigDecimal precioNoche, boolean disponible,
			boolean camaSupletoria) {
		super(id, numero, capacidad, precioNoche, disponible);
		this.camaSupletoria = camaSupletoria;
	}

	@Override
	public BigDecimal calcularPrecio(int noches) {
		BigDecimal precio = precioBase(noches);
		if (camaSupletoria) {
			precio = precio.add(SUPLEMENTO_CAMA_NOCHE.multiply(BigDecimal.valueOf(noches)));
		}
		return redondear(precio);
	}

	@Override
	public String getTipo() {
		return "Doble";
	}

	public boolean isCamaSupletoria() {
		return camaSupletoria;
	}

	public void setCamaSupletoria(boolean camaSupletoria) {
		this.camaSupletoria = camaSupletoria;
	}

	@Override
	public String toString() {
		return super.toString().replaceFirst("]$", ", camaSupletoria=" + camaSupletoria + "]");
	}
}
