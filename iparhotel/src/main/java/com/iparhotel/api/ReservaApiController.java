package com.iparhotel.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.iparhotel.dtos.ReservaDto;
import com.iparhotel.dtos.ReservaEntradaDto;
import com.iparhotel.servicios.ReservaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/reservas")
public class ReservaApiController {

	private final ReservaService reservaService;

	public ReservaApiController(ReservaService reservaService) {
		this.reservaService = reservaService;
	}

	/**
	 * Solo con token. 201 con la reserva; 400 si los datos no son validos;
	 * 404 si la habitacion no existe; 409 si no esta disponible.
	 * El usuario de la reserva es el "sub" (email) del token, nunca un dato del cuerpo.
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ReservaDto reservar(@Valid @RequestBody ReservaEntradaDto entrada, @AuthenticationPrincipal Jwt token) {
		return reservaService.reservar(entrada.idHabitacion(), entrada.fechaEntrada(), entrada.noches(),
				token.getSubject());
	}
}
