package com.iparhotel.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import com.iparhotel.excepciones.ReservaException;

/**
 * Reserva de una habitacion. El importe NO se recibe nunca de fuera: lo calcula siempre
 * el sistema con {@link Habitacion#calcularPrecio(int)} y se recalcula al cambiar
 * la habitacion o las noches. Por eso no tiene setter ni aparece en el constructor.
 */
public class Reserva {

	private Long id;
	private Habitacion habitacion;
	private LocalDate fechaEntrada;
	private int noches;
	private BigDecimal importe;

	public Reserva(Long id, Habitacion habitacion, LocalDate fechaEntrada, int noches) {
		if (habitacion == null) {
			throw new ReservaException("La reserva necesita una habitacion");
		}
		if (fechaEntrada == null) {
			throw new ReservaException("La reserva necesita una fecha de entrada");
		}
		this.id = id;
		this.habitacion = habitacion;
		this.fechaEntrada = fechaEntrada;
		this.noches = noches;
		recalcularImporte();
	}

	private void recalcularImporte() {
		this.importe = habitacion.calcularPrecio(noches);
	}

	/** Dia de salida: la entrada mas las noches. */
	public LocalDate getFechaSalida() {
		return fechaEntrada.plusDays(noches);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Habitacion getHabitacion() {
		return habitacion;
	}

	public void setHabitacion(Habitacion habitacion) {
		if (habitacion == null) {
			throw new ReservaException("La reserva necesita una habitacion");
		}
		this.habitacion = habitacion;
		recalcularImporte();
	}

	public LocalDate getFechaEntrada() {
		return fechaEntrada;
	}

	public void setFechaEntrada(LocalDate fechaEntrada) {
		if (fechaEntrada == null) {
			throw new ReservaException("La reserva necesita una fecha de entrada");
		}
		this.fechaEntrada = fechaEntrada;
	}

	public int getNoches() {
		return noches;
	}

	public void setNoches(int noches) {
		// Se valida y calcula antes de asignar: si falla, la reserva queda como estaba
		BigDecimal nuevoImporte = habitacion.calcularPrecio(noches);
		this.noches = noches;
		this.importe = nuevoImporte;
	}

	/** Solo lectura: lo calcula el sistema. */
	public BigDecimal getImporte() {
		return importe;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Reserva otra)) {
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
		return "Reserva [id=" + id + ", habitacion=" + habitacion.getNumero() + " (" + habitacion.getTipo()
				+ "), fechaEntrada=" + fechaEntrada + ", noches=" + noches + ", importe=" + importe + "]";
	}
}
