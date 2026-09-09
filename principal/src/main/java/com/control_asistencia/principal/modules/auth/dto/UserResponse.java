package com.control_asistencia.principal.modules.auth.dto;

import com.control_asistencia.principal.modules.auth.model.Role;
import com.control_asistencia.principal.modules.auth.model.User;

public record UserResponse(Long id, String username, Long roleId, String roleName) {

	public static UserResponse from(User user, Role role) {
		return new UserResponse(user.getId(), user.getUsername(), user.getRoleId(),
				role != null ? role.getName() : null);
	}

}