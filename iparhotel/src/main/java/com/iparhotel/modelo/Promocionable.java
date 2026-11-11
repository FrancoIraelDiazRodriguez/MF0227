package com.iparhotel.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Algo a lo que se le puede aplicar un descuento porcentual. */
public interface Promocionable {

	/** Porcentaje de descuento, por ejemplo 10 para un 10 %. */
	int getPorcentajeDescuento();

	/** Devuelve el importe con el porcentaje de descuento ya restado, con dos decimales. */
	default BigDecimal aplicarDescuento(BigDecimal importe) {
		BigDecimal descuento = importe.multiply(BigDecimal.valueOf(getPorcentajeDescuento()))
				.divide(BigDecimal.valueOf(100));
		return importe.subtract(descuento).setScale(2, RoundingMode.HALF_UP);
	}
}
