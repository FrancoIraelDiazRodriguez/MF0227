package com.iparhotel.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Cuerpo de POST /api/v2/usuarios/login. */
public record LoginDto(@NotBlank @Email String email, @NotBlank String password) {
}
