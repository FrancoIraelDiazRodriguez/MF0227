package com.iparhotel.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de POST /api/v2/reservas. Ni importe ni usuario:
 * el importe lo calcula el sistema y el usuario sale del token.
 */
public record ReservaEntradaDto(
		@NotNull(message = "Indica la habitación") Long idHabitacion,
		@NotNull(message = "Indica la fecha de entrada")
		@Future(message = "La fecha de entrada debe ser posterior a hoy") LocalDate fechaEntrada,
		@NotNull(message = "Indica el número de noches")
		@Min(value = 1, message = "Mínimo 1 noche")
		@Max(value = 14, message = "Máximo 14 noches") Integer noches) {
}
