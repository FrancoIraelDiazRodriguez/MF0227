package com.iparhotel.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.iparhotel.modelo.Habitacion;

import jakarta.persistence.LockModeType;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

	/** Consulta derivada: habitaciones disponibles, de la mas barata a la mas cara. */
	List<Habitacion> findByDisponibleTrueOrderByPrecioNocheAsc();

	/**
	 * Lee la habitacion bloqueando su fila hasta el final de la transaccion (SELECT ... FOR UPDATE).
	 * Si dos clientes reservan la misma habitacion a la vez, el segundo espera y ya la ve ocupada.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select h from Habitacion h where h.id = :id")
	Optional<Habitacion> buscarParaReservar(Long id);
}
