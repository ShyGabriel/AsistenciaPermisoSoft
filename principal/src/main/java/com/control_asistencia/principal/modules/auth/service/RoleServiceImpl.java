package com.control_asistencia.principal.modules.auth.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.control_asistencia.principal.common.exception.ResourceNotFoundException;
import com.control_asistencia.principal.modules.auth.dto.CreateRoleRequest;
import com.control_asistencia.principal.modules.auth.dto.RoleResponse;
import com.control_asistencia.principal.modules.auth.dto.UpdateRoleRequest;
import com.control_asistencia.principal.modules.auth.model.Role;
import com.control_asistencia.principal.modules.auth.repository.RoleRepository;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;

@Service
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;

	public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RoleResponse> findAll() {
		return roleRepository.findAll().stream()
				.map(RoleResponse::from)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public RoleResponse findById(Long id) {
		return RoleResponse.from(buscarRol(id));
	}

	@Override
	@Transactional
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

	@Override
	@Transactional
	public RoleResponse update(Long id, UpdateRoleRequest request) {
		if (request == null || request.name() == null || request.name().isBlank()) {
			throw new IllegalArgumentException("El nombre del rol es obligatorio");
		}
		Role role = buscarRol(id);
		if (roleRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
			throw new IllegalArgumentException("Ya existe un rol con el nombre " + request.name());
		}
		role.setName(request.name());
		return RoleResponse.from(roleRepository.save(role));
	}

	@Override
	@Transactional
	public RoleResponse delete(Long id) {
		Role role = buscarRol(id);
		if (userRepository.existsByRolId(id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"No se puede eliminar el rol porque tiene usuarios asignados");
		}
		roleRepository.delete(role);
		return RoleResponse.from(role);
	}

	private Role buscarRol(Long id) {
		return roleRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id " + id));
	}

}
