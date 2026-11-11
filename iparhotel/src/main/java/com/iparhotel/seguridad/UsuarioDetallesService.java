package com.iparhotel.seguridad;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.iparhotel.modelo.Usuario;
import com.iparhotel.repositorios.UsuarioRepository;

/** Carga los usuarios de la base de datos para Spring Security. El email hace de nombre de usuario. */
@Service
public class UsuarioDetallesService implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioDetallesService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) {
		Usuario usuario = usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("No existe el usuario " + email));

		// roles("CLIENTE") crea la autoridad ROLE_CLIENTE, que es lo que comprueba hasRole(...)
		return User.withUsername(usuario.getEmail())
				.password(usuario.getPassword())
				.roles(usuario.getRol().name())
				.build();
	}
}
