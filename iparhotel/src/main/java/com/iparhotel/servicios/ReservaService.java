package com.iparhotel.servicios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iparhotel.dtos.HabitacionDto;
import com.iparhotel.dtos.ReservaDto;
import com.iparhotel.excepciones.ReservaException;
import com.iparhotel.excepciones.ReservaException.Motivo;
import com.iparhotel.modelo.Habitacion;
import com.iparhotel.modelo.Reserva;
import com.iparhotel.modelo.Usuario;
import com.iparhotel.repositorios.HabitacionRepository;
import com.iparhotel.repositorios.ReservaRepository;
import com.iparhotel.repositorios.UsuarioRepository;

/**
 * Reglas de negocio de las reservas. El email del usuario siempre llega de la sesion
 * o del token JWT (lo pasa el controlador), nunca de los datos que envia el cliente.
 * Lo usan tanto los controladores web (fase 2) como los de la API REST (fase 3).
 */
@Service
@Transactional
public class ReservaService {

	public static final int NOCHES_MINIMAS = 1;
	public static final int NOCHES_MAXIMAS = 14;

	private final HabitacionRepository habitacionRepository;
	private final ReservaRepository reservaRepository;
	private final UsuarioRepository usuarioRepository;

	// Inyeccion por constructor: las dependencias son final y obligatorias
	public ReservaService(HabitacionRepository habitacionRepository, ReservaRepository reservaRepository,
			UsuarioRepository usuarioRepository) {
		this.habitacionRepository = habitacionRepository;
		this.reservaRepository = reservaRepository;
		this.usuarioRepository = usuarioRepository;
	}

	@Transactional(readOnly = true)
	public List<HabitacionDto> listarDisponibles() {
		return habitacionRepository.findByDisponibleTrueOrderByPrecioNocheAsc().stream()
				.map(HabitacionDto::desde)
				.toList();
	}

	/** Habitacion disponible para mostrar el formulario de reserva. */
	@Transactional(readOnly = true)
	public HabitacionDto buscarDisponible(Long idHabitacion) {
		Habitacion habitacion = habitacionRepository.findById(idHabitacion)
				.orElseThrow(() -> noExiste(idHabitacion));
		comprobarDisponible(habitacion);
		return HabitacionDto.desde(habitacion);
	}

	public ReservaDto reservar(Long idHabitacion, LocalDate fechaEntrada, int noches, String emailUsuario) {
		if (noches < NOCHES_MINIMAS || noches > NOCHES_MAXIMAS) {
			throw new ReservaException("Las noches deben estar entre " + NOCHES_MINIMAS + " y " + NOCHES_MAXIMAS);
		}
		if (fechaEntrada == null || !fechaEntrada.isAfter(LocalDate.now())) {
			throw new ReservaException("La fecha de entrada debe ser posterior a hoy");
		}

		// Bloquea la fila: dos reservas simultaneas de la misma habitacion no pueden pasar las dos
		Habitacion habitacion = habitacionRepository.buscarParaReservar(idHabitacion)
				.orElseThrow(() -> noExiste(idHabitacion));
		comprobarDisponible(habitacion);

		Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
				.orElseThrow(() -> new IllegalStateException("No existe el usuario de la sesion: " + emailUsuario));

		// El importe se calcula de forma polimorfica dentro de Reserva con habitacion.calcularPrecio(noches)
		Reserva reserva = new Reserva(null, habitacion, usuario, fechaEntrada, noches);
		habitacion.setDisponible(false);

		return ReservaDto.desde(reservaRepository.save(reserva));
	}

	@Transactional(readOnly = true)
	public List<ReservaDto> misReservas(String emailUsuario) {
		return reservaRepository.findByUsuarioEmailOrderByFechaEntradaAsc(emailUsuario).stream()
				.map(ReservaDto::desde)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<ReservaDto> todasLasReservas() {
		return reservaRepository.findAllByOrderByFechaEntradaAsc().stream()
				.map(ReservaDto::desde)
				.toList();
	}

	private static void comprobarDisponible(Habitacion habitacion) {
		if (!habitacion.isDisponible()) {
			throw new ReservaException(Motivo.HABITACION_NO_DISPONIBLE, "La habitación " + habitacion.getNumero() + " no está disponible");
		}
	}

	private static ReservaException noExiste(Long idHabitacion) {
		return new ReservaException(Motivo.HABITACION_NO_ENCONTRADA, "No existe la habitación " + idHabitacion);
	}
}
