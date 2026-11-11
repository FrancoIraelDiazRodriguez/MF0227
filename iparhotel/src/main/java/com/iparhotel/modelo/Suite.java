package com.iparhotel.modelo;

import java.math.BigDecimal;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Suite. Precio = precioNoche * noches + 50 euros fijos de bienvenida.
 * A partir de 5 noches se descuenta un 10 % del total con el metodo default de {@link Promocionable}.
 */
@Entity
@DiscriminatorValue("SUITE")
public class Suite extends Habitacion implements Promocionable {

	public static final BigDecimal CARGO_BIENVENIDA = new BigDecimal("50.00");
	public static final int NOCHES_PARA_DESCUENTO = 5;
	public static final int PORCENTAJE_DESCUENTO = 10;

	private boolean jacuzzi;

	public Suite() {
		super();
	}

	public Suite(Long id, String numero, int capacidad, BigDecimal precioNoche, boolean disponible,
			boolean jacuzzi) {
		super(id, numero, capacidad, precioNoche, disponible);
		this.jacuzzi = jacuzzi;
	}

	@Override
	public BigDecimal calcularPrecio(int noches) {
		BigDecimal precio = precioBase(noches).add(CARGO_BIENVENIDA);
		if (noches >= NOCHES_PARA_DESCUENTO) {
			precio = aplicarDescuento(precio);
		}
		return redondear(precio);
	}

	@Override
	public int getPorcentajeDescuento() {
		return PORCENTAJE_DESCUENTO;
	}

	@Override
	public String getTipo() {
		return "Suite";
	}

	public boolean isJacuzzi() {
		return jacuzzi;
	}

	public void setJacuzzi(boolean jacuzzi) {
		this.jacuzzi = jacuzzi;
	}

	@Override
	public String toString() {
		return super.toString().replaceFirst("]$", ", jacuzzi=" + jacuzzi + "]");
	}
}
