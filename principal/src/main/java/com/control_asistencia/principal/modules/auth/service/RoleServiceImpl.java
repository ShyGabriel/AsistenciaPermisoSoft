package com.control_asistencia.principal.modules.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.control_asistencia.principal.modules.auth.dto.CreateRoleRequest;
import com.control_asistencia.principal.modules.auth.dto.RoleResponse;
import com.control_asistencia.principal.modules.auth.model.Role;
import com.control_asistencia.principal.modules.auth.repository.RoleRepository;

@Service
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;

	public RoleServiceImpl(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Override
	public List<RoleResponse> findAll() {
		return roleRepository.findAll().stream()
				.map(RoleResponse::from)
				.toList();
	}

	@Override
	public RoleResponse create(CreateRoleRequest request) {
		if (request == null || request.name() == null || request.name().isBlank()) {
			throw new IllegalArgumentException("El nombre del rol es obligatorio");
		}
		if (roleRepository.existsByNameIgnoreCase(request.name())) {
			throw new IllegalArgumentException("Ya existe un rol con el nombre " + request.name());
		}
		Role role = roleRepository.save(new Role(null, request.name()));
		return RoleResponse.from(role);
	}

}