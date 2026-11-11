package com.iparhotel.configuracion;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iparhotel.modelo.Doble;
import com.iparhotel.modelo.Habitacion;
import com.iparhotel.modelo.Individual;
import com.iparhotel.modelo.Reserva;
import com.iparhotel.modelo.Rol;
import com.iparhotel.modelo.Suite;
import com.iparhotel.modelo.Usuario;
import com.iparhotel.repositorios.HabitacionRepository;
import com.iparhotel.repositorios.ReservaRepository;
import com.iparhotel.repositorios.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Datos de ejemplo cargados al arrancar si la base de datos esta vacia.
 * Se usa un CommandLineRunner para poder codificar las contrasenas con el PasswordEncoder.
 */
@Slf4j
@Component
public class DatosEjemplo implements CommandLineRunner {

	private final UsuarioRepository usuarioRepository;
	private final HabitacionRepository habitacionRepository;
	private final ReservaRepository reservaRepository;
	private final PasswordEncoder passwordEncoder;

	public DatosEjemplo(UsuarioRepository usuarioRepository, HabitacionRepository habitacionRepository,
			ReservaRepository reservaRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.habitacionRepository = habitacionRepository;
		this.reservaRepository = reservaRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void run(String... args) {
		if (usuarioRepository.count() > 0) {
			log.info("La base de datos ya tiene datos: no se cargan los de ejemplo");
			return;
		}

		usuario("Administración Iparhotel", "admin@iparhotel.com", "admin123", Rol.ADMINISTRADOR);
		Usuario ana = usuario("Ana López", "ana@iparhotel.com", "ana123", Rol.CLIENTE);
		usuario("Javier Etxeberria", "javier@iparhotel.com", "javier123", Rol.CLIENTE);

		habitacionRepository.save(new Individual(null, "101", 1, new BigDecimal("55.00"), true));
		habitacionRepository.save(new Individual(null, "102", 1, new BigDecimal("60.00"), true));
		habitacionRepository.save(new Doble(null, "201", 2, new BigDecimal("85.00"), true, false));
		Habitacion doble202 = habitacionRepository.save(new Doble(null, "202", 3, new BigDecimal("90.00"), true, true));
		habitacionRepository.save(new Suite(null, "301", 2, new BigDecimal("150.00"), true, true));
		habitacionRepository.save(new Suite(null, "302", 4, new BigDecimal("180.00"), true, false));

		// Una reserva de ejemplo para Ana: la 202 queda ocupada
		reservaRepository.save(new Reserva(null, doble202, ana, LocalDate.now().plusDays(10), 3));
		doble202.setDisponible(false);

		log.info("Datos de ejemplo cargados: {} usuarios, {} habitaciones, {} reservas",
				usuarioRepository.count(), habitacionRepository.count(), reservaRepository.count());
	}

	private Usuario usuario(String nombre, String email, String password, Rol rol) {
		return usuarioRepository.save(Usuario.builder()
				.nombre(nombre).email(email).password(passwordEncoder.encode(password)).rol(rol).build());
	}
}
