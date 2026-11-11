package com.iparhotel.dtos;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada del formulario de reserva. No lleva importe ni usuario:
 * el importe lo calcula el sistema y el usuario sale de la sesion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaFormDto {

	@NotNull(message = "Indica la fecha de entrada")
	@Future(message = "La fecha de entrada debe ser posterior a hoy")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fechaEntrada;

	@NotNull(message = "Indica el número de noches")
	@Min(value = 1, message = "Mínimo 1 noche")
	@Max(value = 14, message = "Máximo 14 noches")
	private Integer noches = 1;
}
