package com.control_asistencia.principal.modules.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.control_asistencia.principal.modules.auth.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	boolean existsByNameIgnoreCase(String name);

}
