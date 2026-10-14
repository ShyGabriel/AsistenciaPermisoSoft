package com.control_asistencia.principal.modules.auth.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.control_asistencia.principal.common.exception.ResourceNotFoundException;
import com.control_asistencia.principal.modules.auth.dto.CreateUserRequest;
import com.control_asistencia.principal.modules.auth.dto.UserResponse;
import com.control_asistencia.principal.modules.auth.model.Role;
import com.control_asistencia.principal.modules.auth.model.User;
import com.control_asistencia.principal.modules.auth.repository.RoleRepository;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public List<UserResponse> findAll() {
		return userRepository.findAll().stream()
				.map(UserResponse::from)
				.toList();
	}

	@Override
	public UserResponse create(CreateUserRequest request) {
		if (request == null || request.nombres() == null || request.nombres().isBlank()) {
			throw new IllegalArgumentException("Los nombres son obligatorios");
		}
		if (request.apellidos() == null || request.apellidos().isBlank()) {
			throw new IllegalArgumentException("Los apellidos son obligatorios");
		}
		if (request.email() == null || request.email().isBlank()) {
			throw new IllegalArgumentException("El email es obligatorio");
		}
		if (request.password() == null || request.password().isBlank()) {
			throw new IllegalArgumentException("La contraseña es obligatoria");
		}
		if (request.roleId() == null) {
			throw new IllegalArgumentException("El identificador del rol es obligatorio");
		}
		if (userRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Ya existe un usuario con el email " + request.email());
		}
		Role role = roleRepository.findById(request.roleId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Rol no encontrado con id " + request.roleId()));
		User user = userRepository.save(new User(null, request.nombres(), request.apellidos(),
				request.email(), passwordEncoder.encode(request.password()), role));
		return UserResponse.from(user);
	}

}
