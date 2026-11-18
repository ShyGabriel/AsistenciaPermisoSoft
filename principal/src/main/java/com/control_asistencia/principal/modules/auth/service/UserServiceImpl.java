package com.control_asistencia.principal.modules.auth.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.control_asistencia.principal.common.exception.ResourceNotFoundException;
import com.control_asistencia.principal.modules.auth.dto.CreateUserRequest;
import com.control_asistencia.principal.modules.auth.dto.UpdateUserRequest;
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
	@Transactional(readOnly = true)
	public List<UserResponse> findAll() {
		return userRepository.findAll().stream()
				.map(UserResponse::from)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse findById(Long id) {
		return UserResponse.from(buscarUsuario(id));
	}

	@Override
	@Transactional
	public UserResponse create(CreateUserRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("El usuario no puede estar vacío");
		}
		validarDatos(request.nombres(), request.apellidos(), request.email(), request.roleId());
		if (request.password() == null || request.password().isBlank()) {
			throw new IllegalArgumentException("La contraseña es obligatoria");
		}
		if (userRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Ya existe un usuario con el email " + request.email());
		}
		Role role = buscarRol(request.roleId());
		User user = userRepository.save(new User(null, request.nombres(), request.apellidos(),
				request.email(), passwordEncoder.encode(request.password()), role));
		return UserResponse.from(user);
	}

	@Override
	@Transactional
	public UserResponse update(Long id, UpdateUserRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("El usuario no puede estar vacío");
		}
		if (Boolean.FALSE.equals(request.estado())) {
			throw new IllegalArgumentException("Use DELETE para desactivar un usuario");
		}
		validarDatos(request.nombres(), request.apellidos(), request.email(), request.roleId());
		User user = buscarUsuario(id);
		userRepository.findByEmailIgnoreCase(request.email())
				.filter(existente -> !existente.getId().equals(id))
				.ifPresent(existente -> {
					throw new ResponseStatusException(HttpStatus.CONFLICT,
							"Ya existe un usuario con el email " + request.email());
				});
		Role role = buscarRol(request.roleId());
		user.setNombres(request.nombres());
		user.setApellidos(request.apellidos());
		user.setEmail(request.email());
		user.setRol(role);
		if (request.password() != null && !request.password().isBlank()) {
			user.setPasswordHash(passwordEncoder.encode(request.password()));
		}
		if (Boolean.TRUE.equals(request.estado())) {
			user.setEstado(true);
		}
		return UserResponse.from(userRepository.save(user));
	}

	@Override
	@Transactional
	public UserResponse deactivate(Long id, String currentAdminEmail) {
		User user = buscarUsuario(id);
		if (currentAdminEmail != null && user.getEmail().equalsIgnoreCase(currentAdminEmail)) {
			throw new IllegalArgumentException("No puede desactivar su propia cuenta");
		}
		if (Boolean.TRUE.equals(user.getEstado()) && user.getRol() != null
				&& "ADMIN".equalsIgnoreCase(user.getRol().getName())
				&& userRepository.contarPorRolYEstado("ADMIN", true) <= 1) {
			throw new IllegalArgumentException("No se puede desactivar al último administrador activo");
		}
		user.setEstado(false);
		return UserResponse.from(userRepository.save(user));
	}

	private void validarDatos(String nombres, String apellidos, String email, Long roleId) {
		if (nombres == null || nombres.isBlank()) {
			throw new IllegalArgumentException("Los nombres son obligatorios");
		}
		if (apellidos == null || apellidos.isBlank()) {
			throw new IllegalArgumentException("Los apellidos son obligatorios");
		}
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("El email es obligatorio");
		}
		if (roleId == null) {
			throw new IllegalArgumentException("El identificador del rol es obligatorio");
		}
	}

	private User buscarUsuario(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
	}

	private Role buscarRol(Long roleId) {
		return roleRepository.findById(roleId)
				.orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id " + roleId));
	}

}
