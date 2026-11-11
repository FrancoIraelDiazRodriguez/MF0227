package com.iparhotel.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.iparhotel.excepciones.ReservaException;

class HabitacionTest {

	private static BigDecimal euros(String valor) {
		return new BigDecimal(valor);
	}

	// --- Individual ---

	@Test
	void individualCobraPrecioNochePorNoches() {
		Individual h = new Individual(1L, "101", 1, euros("60"), true);
		assertEquals(euros("180.00"), h.calcularPrecio(3));
		assertEquals("Individual", h.getTipo());
	}

	// --- Doble ---

	@Test
	void dobleSinCamaSupletoria() {
		Doble h = new Doble(2L, "201", 2, euros("90"), true, false);
		assertEquals(euros("270.00"), h.calcularPrecio(3));
		assertEquals("Doble", h.getTipo());
	}

	@Test
	void dobleConCamaSupletoriaSuma20PorNoche() {
		Doble h = new Doble(3L, "202", 3, euros("90"), true, true);
		// 90*3 + 20*3
		assertEquals(euros("330.00"), h.calcularPrecio(3));
	}

	// --- Suite ---

	@Test
	void suiteSumaBienvenidaSinDescuentoPorDebajoDe5Noches() {
		Suite h = new Suite(4L, "301", 2, euros("150"), true, true);
		// 150*4 + 50
		assertEquals(euros("650.00"), h.calcularPrecio(4));
		assertEquals("Suite", h.getTipo());
	}

	@Test
	void suiteAplicaDescuentoDel10ApartirDe5Noches() {
		Suite h = new Suite(5L, "302", 2, euros("150"), true, false);
		// (150*5 + 50) * 0.90 = 800 * 0.90
		assertEquals(euros("720.00"), h.calcularPrecio(5));
	}

	@Test
	void suiteEsPromocionable() {
		Promocionable p = new Suite(6L, "303", 2, euros("150"), true, false);
		assertEquals(10, p.getPorcentajeDescuento());
		assertEquals(euros("90.00"), p.aplicarDescuento(euros("100")));
	}

	// --- Reglas comunes ---

	@Test
	void nochesMenoresQueUnoNoSonValidas() {
		Habitacion h = new Individual(7L, "102", 1, euros("60"), true);
		assertThrows(ReservaException.class, () -> h.calcularPrecio(0));
		assertThrows(ReservaException.class, () -> h.calcularPrecio(-2));
	}

	@Test
	void equalsYHashCodeSeBasanEnElId() {
		Habitacion a = new Individual(10L, "101", 1, euros("60"), true);
		Habitacion b = new Suite(10L, "999", 4, euros("300"), false, true);
		Habitacion c = new Individual(11L, "101", 1, euros("60"), true);

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a, c);
	}

	@Test
	void sinIdSoloEsIgualASiMisma() {
		Habitacion a = new Individual(null, "101", 1, euros("60"), true);
		Habitacion b = new Individual(null, "101", 1, euros("60"), true);
		assertEquals(a, a);
		assertNotEquals(a, b);
	}

	@Test
	void constructorVacioDejaLaHabitacionDisponible() {
		assertTrue(new Doble().isDisponible());
		assertFalse(new Doble().isCamaSupletoria());
	}

	@Test
	void toStringIncluyeTipoYAtributosPropios() {
		String texto = new Doble(2L, "201", 2, euros("90"), true, true).toString();
		assertTrue(texto.startsWith("Doble [id=2"));
		assertTrue(texto.contains("camaSupletoria=true"));
	}
}
