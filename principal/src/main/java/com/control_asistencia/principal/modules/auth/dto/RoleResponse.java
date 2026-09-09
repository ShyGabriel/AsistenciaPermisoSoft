package com.control_asistencia.principal.modules.auth.dto;

import com.control_asistencia.principal.modules.auth.model.Role;

public record RoleResponse(Long id, String name) {

	public static RoleResponse from(Role role) {
		return new RoleResponse(role.getId(), role.getName());
	}

}