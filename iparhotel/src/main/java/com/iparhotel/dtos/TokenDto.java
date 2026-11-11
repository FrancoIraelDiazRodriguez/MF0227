package com.iparhotel.dtos;

/** Respuesta del login: {"accessToken":"...","tokenType":"Bearer"} */
public record TokenDto(String accessToken, String tokenType) {

	public static TokenDto bearer(String token) {
		return new TokenDto(token, "Bearer");
	}
}
