package com.iparhotel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import com.iparhotel.modelo.Doble;
import com.iparhotel.modelo.Habitacion;
import com.iparhotel.modelo.Individual;
import com.iparhotel.modelo.Reserva;
import com.iparhotel.modelo.Suite;

/** Fase 1.3: polimorfismo y streams sobre las habitaciones del hotel. */
public class PruebaPolimorfismo {

	public static void main(String[] args) {
		List<Habitacion> habitaciones = List.of(
				new Individual(1L, "101", 1, new BigDecimal("60"), true),
				new Doble(2L, "201", 2, new BigDecimal("90"), true, false),
				new Doble(3L, "202", 3, new BigDecimal("95"), false, true),
				new Suite(4L, "301", 2, new BigDecimal("150"), true, true),
				new Individual(5L, "102", 1, new BigDecimal("55"), false));

		// Polimorfismo: cada habitacion sabe calcular su precio; sin instanceof ni switch por tipo
		System.out.println("=== Precio de 3 y 6 noches ===");
		for (Habitacion h : habitaciones) {
			System.out.printf("%-10s %s  3 noches: %8s EUR   6 noches: %8s EUR%n",
					h.getTipo(), h.getNumero(), h.calcularPrecio(3), h.calcularPrecio(6));
		}

		// Streams: disponibles ordenadas por precio por noche
		System.out.println();
		System.out.println("=== Habitaciones disponibles por precio/noche ===");
		habitaciones.stream()
				.filter(Habitacion::isDisponible)
				.sorted(Comparator.comparing(Habitacion::getPrecioNoche))
				.forEach(h -> System.out.printf("%-10s %s  %s EUR/noche%n",
						h.getTipo(), h.getNumero(), h.getPrecioNoche()));

		// Streams: importe total de una lista de reservas
		LocalDate hoy = LocalDate.now();
		List<Reserva> reservas = List.of(
				new Reserva(1L, habitaciones.get(0), hoy.plusDays(7), 3),
				new Reserva(2L, habitaciones.get(2), hoy.plusDays(10), 2),
				new Reserva(3L, habitaciones.get(3), hoy.plusDays(20), 6));

		System.out.println();
		System.out.println("=== Reservas ===");
		reservas.forEach(System.out::println);

		BigDecimal total = reservas.stream()
				.map(Reserva::getImporte)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		System.out.println("Importe total de las reservas: " + total + " EUR");
	}
}
