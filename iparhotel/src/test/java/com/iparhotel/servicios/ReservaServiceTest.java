package com.iparhotel.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iparhotel.dtos.HabitacionDto;
import com.iparhotel.dtos.ReservaDto;
import com.iparhotel.excepciones.ReservaException;
import com.iparhotel.modelo.Doble;
import com.iparhotel.modelo.Habitacion;
import com.iparhotel.modelo.Reserva;
import com.iparhotel.modelo.Rol;
import com.iparhotel.modelo.Suite;
import com.iparhotel.modelo.Usuario;
import com.iparhotel.repositorios.HabitacionRepository;
import com.iparhotel.repositorios.ReservaRepository;
import com.iparhotel.repositorios.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

	private static final String EMAIL = "ana@iparhotel.com";

	@Mock
	private HabitacionRepository habitacionRepository;
	@Mock
	private ReservaRepository reservaRepository;
	@Mock
	private UsuarioRepository usuarioRepository;

	private ReservaService servicio;
	private final LocalDate manana = LocalDate.now().plusDays(1);
	private final Usuario ana = Usuario.builder().id(2L).nombre("Ana").email(EMAIL).password("x").rol(Rol.CLIENTE).build();

	@BeforeEach
	void preparar() {
		servicio = new ReservaService(habitacionRepository, reservaRepository, usuarioRepository);
	}

	@Test
	void reservarCalculaElImporteYOcupaLaHabitacion() {
		Habitacion suite = new Suite(5L, "301", 2, new BigDecimal("150"), true, true);
		when(habitacionRepository.buscarParaReservar(5L)).thenReturn(Optional.of(suite));
		when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(ana));
		when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));

		ReservaDto reserva = servicio.reservar(5L, manana, 5, EMAIL);

		// (150*5 + 50) - 10 % = 720
		assertEquals(new BigDecimal("720.00"), reserva.getImporte());
		assertEquals("Suite", reserva.getTipoHabitacion());
		assertEquals(EMAIL, reserva.getEmailUsuario());
		assertFalse(suite.isDisponible());
	}

	@Test
	void lasNochesDebenEstarEntre1y14() {
		assertThrows(ReservaException.class, () -> servicio.reservar(1L, manana, 0, EMAIL));
		assertThrows(ReservaException.class, () -> servicio.reservar(1L, manana, 15, EMAIL));
		verify(reservaRepository, never()).save(any());
	}

	@Test
	void laFechaDebeSerFutura() {
		assertThrows(ReservaException.class, () -> servicio.reservar(1L, LocalDate.now(), 2, EMAIL));
		assertThrows(ReservaException.class, () -> servicio.reservar(1L, LocalDate.now().minusDays(3), 2, EMAIL));
		assertThrows(ReservaException.class, () -> servicio.reservar(1L, null, 2, EMAIL));
	}

	@Test
	void habitacionInexistente() {
		when(habitacionRepository.buscarParaReservar(99L)).thenReturn(Optional.empty());
		ReservaException e = assertThrows(ReservaException.class, () -> servicio.reservar(99L, manana, 2, EMAIL));
		assertEquals("No existe la habitación 99", e.getMessage());
	}

	@Test
	void habitacionNoDisponible() {
		Habitacion ocupada = new Doble(3L, "202", 2, new BigDecimal("90"), false, false);
		when(habitacionRepository.buscarParaReservar(3L)).thenReturn(Optional.of(ocupada));
		assertThrows(ReservaException.class, () -> servicio.reservar(3L, manana, 2, EMAIL));
		verify(reservaRepository, never()).save(any());
	}

	@Test
	void listarDisponiblesDevuelveTipoYPrecio() {
		when(habitacionRepository.findByDisponibleTrueOrderByPrecioNocheAsc())
				.thenReturn(List.of(new Doble(1L, "201", 2, new BigDecimal("85"), true, false)));
		List<HabitacionDto> lista = servicio.listarDisponibles();
		assertEquals(1, lista.size());
		assertEquals("Doble", lista.get(0).getTipo());
		assertEquals(new BigDecimal("85"), lista.get(0).getPrecioNoche());
	}
}
