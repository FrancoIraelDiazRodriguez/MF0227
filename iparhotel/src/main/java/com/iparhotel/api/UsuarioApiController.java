package com.iparhotel.api;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iparhotel.dtos.LoginDto;
import com.iparhotel.dtos.TokenDto;
import com.iparhotel.modelo.Usuario;
import com.iparhotel.repositorios.UsuarioRepository;
import com.iparhotel.seguridad.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/usuarios")
public class UsuarioApiController {

	private final AuthenticationManager authenticationManager;
	private final UsuarioRepository usuarioRepository;
	private final TokenService tokenService;

	public UsuarioApiController(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository,
			TokenService tokenService) {
		this.authenticationManager = authenticationManager;
		this.usuarioRepository = usuarioRepository;
		this.tokenService = tokenService;
	}

	/**
	 * Comprueba email y contrasena y devuelve el token.
	 * Si no son validas, el AuthenticationManager lanza BadCredentialsException
	 * y el ApiExceptionHandler responde 401.
	 */
	@PostMapping("/login")
	public TokenDto login(@Valid @RequestBody LoginDto login) {
		String email = login.email().trim().toLowerCase();
		authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(email, login.password()));

		Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
		return TokenDto.bearer(tokenService.generar(usuario));
	}
}
