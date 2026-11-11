package com.iparhotel.repositorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.iparhotel.modelo.Doble;
import com.iparhotel.modelo.Habitacion;
import com.iparhotel.modelo.Individual;
import com.iparhotel.modelo.Reserva;
import com.iparhotel.modelo.Rol;
import com.iparhotel.modelo.Suite;
import com.iparhotel.modelo.Usuario;

/** Prueba las consultas derivadas y el mapeo SINGLE_TABLE contra H2. */
@DataJpaTest
class RepositoriosTest {

	@Autowired
	private TestEntityManager em;
	@Autowired
	private HabitacionRepository habitacionRepository;
	@Autowired
	private ReservaRepository reservaRepository;

	@Test
	void disponiblesOrdenadasPorPrecioYConSuSubclase() {
		em.persist(new Suite(null, "301", 2, new BigDecimal("150"), true, true));
		em.persist(new Individual(null, "101", 1, new BigDecimal("55"), true));
		em.persist(new Doble(null, "201", 2, new BigDecimal("85"), false, true));
		em.flush();
		em.clear();

		List<Habitacion> disponibles = habitacionRepository.findByDisponibleTrueOrderByPrecioNocheAsc();

		assertEquals(List.of("101", "301"), disponibles.stream().map(Habitacion::getNumero).toList());
		// Al leer de la tabla unica, Hibernate crea la subclase correcta
		assertInstanceOf(Individual.class, disponibles.get(0));
		assertInstanceOf(Suite.class, disponibles.get(1));
	}

	@Test
	void reservasDeUnUsuario() {
		Usuario ana = em.persist(usuario("ana@iparhotel.com"));
		Usuario javier = em.persist(usuario("javier@iparhotel.com"));
		Habitacion h1 = em.persist(new Individual(null, "101", 1, new BigDecimal("55"), true));
		Habitacion h2 = em.persist(new Doble(null, "201", 2, new BigDecimal("85"), true, false));
		em.persist(new Reserva(null, h2, ana, LocalDate.now().plusDays(20), 2));
		em.persist(new Reserva(null, h1, ana, LocalDate.now().plusDays(5), 1));
		em.persist(new Reserva(null, h1, javier, LocalDate.now().plusDays(9), 3));
		em.flush();
		em.clear();

		List<Reserva> deAna = reservaRepository.findByUsuarioEmailOrderByFechaEntradaAsc("ana@iparhotel.com");

		assertEquals(List.of("101", "201"), deAna.stream().map(r -> r.getHabitacion().getNumero()).toList());
		assertEquals(new BigDecimal("170.00"), deAna.get(1).getImporte());
	}

	private static Usuario usuario(String email) {
		return Usuario.builder().nombre(email).email(email).password("x").rol(Rol.CLIENTE).build();
	}
}
