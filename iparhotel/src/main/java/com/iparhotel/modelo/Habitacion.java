package com.iparhotel.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.iparhotel.excepciones.ReservaException;

/**
 * Habitacion generica del hotel. Cada tipo concreto (Individual, Doble, Suite)
 * decide como se calcula el precio de una estancia.
 */
public abstract class Habitacion {

	private Long id;
	private String numero;
	private int capacidad;
	private BigDecimal precioNoche;
	private boolean disponible;

	protected Habitacion() {
		this.disponible = true;
	}

	protected Habitacion(Long id, String numero, int capacidad, BigDecimal precioNoche, boolean disponible) {
		this.id = id;
		this.numero = numero;
		this.capacidad = capacidad;
		this.precioNoche = precioNoche;
		this.disponible = disponible;
	}

	/** Precio total de una estancia de {@code noches} noches. */
	public abstract BigDecimal calcularPrecio(int noches);

	/** Nombre del tipo concreto de habitacion: "Individual", "Doble" o "Suite". */
	public abstract String getTipo();

	/** Precio base comun a todos los tipos: precioNoche * noches. */
	protected BigDecimal precioBase(int noches) {
		validarNoches(noches);
		return precioNoche.multiply(BigDecimal.valueOf(noches));
	}

	protected static void validarNoches(int noches) {
		if (noches < 1) {
			throw new ReservaException("El numero de noches debe ser al menos 1 y es " + noches);
		}
	}

	/** Todos los importes se devuelven con dos decimales. */
	protected static BigDecimal redondear(BigDecimal importe) {
		return importe.setScale(2, RoundingMode.HALF_UP);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public BigDecimal getPrecioNoche() {
		return precioNoche;
	}

	public void setPrecioNoche(BigDecimal precioNoche) {
		this.precioNoche = precioNoche;
	}

	public boolean isDisponible() {
		return disponible;
	}

	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}

	/** Dos habitaciones son iguales si tienen el mismo id. Sin id, solo es igual a si misma. */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Habitacion otra)) {
			return false;
		}
		return id != null && id.equals(otra.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return getTipo() + " [id=" + id + ", numero=" + numero + ", capacidad=" + capacidad
				+ ", precioNoche=" + precioNoche + ", disponible=" + disponible + "]";
	}
}
