package com.control_asistencia.principal.modules.auth.repository;

import java.util.List;
import java.util.Optional;

import com.control_asistencia.principal.modules.auth.model.Role;

public interface RoleRepository {

	List<Role> findAll();

	Optional<Role> findById(Long id);

	Role save(Role role);

	void reset();

}