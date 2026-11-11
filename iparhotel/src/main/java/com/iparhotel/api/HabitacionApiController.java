package com.iparhotel.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iparhotel.dtos.HabitacionDto;
import com.iparhotel.servicios.ReservaService;

@RestController
@RequestMapping("/api/v2/habitaciones")
public class HabitacionApiController {

	private final ReservaService reservaService;

	public HabitacionApiController(ReservaService reservaService) {
		this.reservaService = reservaService;
	}

	/** Publico: habitaciones disponibles. Reutiliza el servicio de la fase 2. */
	@GetMapping
	public List<HabitacionDto> disponibles() {
		return reservaService.listarDisponibles();
	}
}
