package com.iparhotel.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iparhotel.modelo.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByEmail(String email);
}
