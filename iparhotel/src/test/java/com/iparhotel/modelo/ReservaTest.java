package com.iparhotel.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.iparhotel.excepciones.ReservaException;

class ReservaTest {

	private final LocalDate entrada = LocalDate.of(2026, 12, 1);
	private final Habitacion doble = new Doble(1L, "201", 2, new BigDecimal("90"), true, true);
	private final Habitacion suite = new Suite(2L, "301", 2, new BigDecimal("150"), true, false);

	@Test
	void elImporteLoCalculaElSistema() {
		Reserva r = new Reserva(1L, doble, entrada, 3);
		assertEquals(new BigDecimal("330.00"), r.getImporte());
		assertEquals(LocalDate.of(2026, 12, 4), r.getFechaSalida());
	}

	@Test
	void elImporteSeRecalculaAlCambiarNochesOHabitacion() {
		Reserva r = new Reserva(1L, doble, entrada, 3);
		r.setNoches(5);
		assertEquals(new BigDecimal("550.00"), r.getImporte());
		r.setHabitacion(suite);
		assertEquals(new BigDecimal("720.00"), r.getImporte());
	}

	@Test
	void datosNoValidosLanzanReservaException() {
		assertThrows(ReservaException.class, () -> new Reserva(1L, null, entrada, 3));
		assertThrows(ReservaException.class, () -> new Reserva(1L, doble, null, 3));
		assertThrows(ReservaException.class, () -> new Reserva(1L, doble, entrada, 0));
	}

	@Test
	void unCambioDeNochesNoValidoNoAlteraLaReserva() {
		Reserva r = new Reserva(1L, doble, entrada, 3);
		assertThrows(ReservaException.class, () -> r.setNoches(-1));
		assertEquals(3, r.getNoches());
		assertEquals(new BigDecimal("330.00"), r.getImporte());
	}

	@Test
	void importeTotalConStreams() {
		List<Reserva> reservas = List.of(new Reserva(1L, doble, entrada, 3), new Reserva(2L, suite, entrada, 5));
		BigDecimal total = reservas.stream().map(Reserva::getImporte).reduce(BigDecimal.ZERO, BigDecimal::add);
		assertEquals(new BigDecimal("1050.00"), total);
	}
}
