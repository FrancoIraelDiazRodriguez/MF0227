package com.iparhotel.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import com.iparhotel.excepciones.ReservaException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Reserva de una habitacion. El importe NO se recibe nunca de fuera: lo calcula siempre
 * el sistema con {@link Habitacion#calcularPrecio(int)} y se recalcula al cambiar
 * la habitacion o las noches. Por eso no tiene setter ni aparece en el constructor.
 *
 * Fase 2: entidad JPA con el usuario que hace la reserva.
 */
@Entity
@Table(name = "reservas")
public class Reserva {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "habitacion_id")
	private Habitacion habitacion;

	@NotNull
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	@NotNull
	@Column(nullable = false)
	private LocalDate fechaEntrada;

	@Min(1)
	@Column(nullable = false)
	private int noches;

	@NotNull
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal importe;

	/** Solo para JPA. */
	protected Reserva() {
	}

	/** Constructor de la fase 1 (sin usuario). */
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

	/** Constructor de la fase 2: la reserva pertenece a un usuario. */
	public Reserva(Long id, Habitacion habitacion, Usuario usuario, LocalDate fechaEntrada, int noches) {
		this(id, habitacion, fechaEntrada, noches);
		if (usuario == null) {
			throw new ReservaException("La reserva necesita un usuario");
		}
		this.usuario = usuario;
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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		if (usuario == null) {
			throw new ReservaException("La reserva necesita un usuario");
		}
		this.usuario = usuario;
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
		return id != null && id.equals(otra.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	/** No incluye el usuario para no forzar su carga perezosa. */
	@Override
	public String toString() {
		return "Reserva [id=" + id + ", habitacion=" + habitacion.getNumero() + " (" + habitacion.getTipo()
				+ "), fechaEntrada=" + fechaEntrada + ", noches=" + noches + ", importe=" + importe + "]";
	}
}
