package com.iparhotel.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.iparhotel.excepciones.ReservaException;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Habitacion generica del hotel. Cada tipo concreto (Individual, Doble, Suite)
 * decide como se calcula el precio de una estancia.
 *
 * Fase 2: entidad JPA. Toda la jerarquia se guarda en una sola tabla (SINGLE_TABLE)
 * y la columna tipo_habitacion indica la subclase de cada fila.
 */
@Entity
@Table(name = "habitaciones")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_habitacion", length = 20)
public abstract class Habitacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 10)
	@Column(nullable = false, unique = true, length = 10)
	private String numero;

	@Min(1)
	@Column(nullable = false)
	private int capacidad;

	@NotNull
	@DecimalMin("0.01")
	@Column(nullable = false, precision = 8, scale = 2)
	private BigDecimal precioNoche;

	@Column(nullable = false)
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
		return id != null && id.equals(otra.getId());
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
