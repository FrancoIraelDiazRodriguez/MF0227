package com.iparhotel.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.iparhotel.modelo.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

	/** Consulta derivada: reservas de un usuario, por fecha de entrada. Trae habitacion en la misma consulta. */
	@EntityGraph(attributePaths = { "habitacion", "usuario" })
	List<Reserva> findByUsuarioEmailOrderByFechaEntradaAsc(String email);

	/** Todas las reservas (vista de administracion). */
	@EntityGraph(attributePaths = { "habitacion", "usuario" })
	List<Reserva> findAllByOrderByFechaEntradaAsc();
}
