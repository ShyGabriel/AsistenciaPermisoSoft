package com.control_asistencia.principal.modules.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	@Override
	public List<UserResponse> findAll() {
		return userRepository.findAll().stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	public UserResponse create(CreateUserRequest request) {
		if (request == null || request.username() == null || request.username().isBlank()) {
			throw new IllegalArgumentException("El nombre de usuario es obligatorio");
		}
		if (request.roleId() == null) {
			throw new IllegalArgumentException("El identificador del rol es obligatorio");
		}
		Role role = roleRepository.findById(request.roleId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Rol no encontrado con id " + request.roleId()));
		User user = userRepository.save(new User(null, request.username(), role.getId()));
		return toResponse(user, role);
	}

	private UserResponse toResponse(User user) {
		Role role = roleRepository.findById(user.getRoleId()).orElse(null);
		return toResponse(user, role);
	}

	private UserResponse toResponse(User user, Role role) {
		return UserResponse.from(user, role);
	}

}