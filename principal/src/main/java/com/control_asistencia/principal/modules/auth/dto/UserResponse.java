package com.control_asistencia.principal.modules.auth.dto;

import com.control_asistencia.principal.modules.auth.model.User;

public record UserResponse(Long id, String nombres, String apellidos, String email, Long rolId, String rolNombre) {

	public static UserResponse from(User user) {
		return new UserResponse(
				user.getId(),
				user.getNombres(),
				user.getApellidos(),
				user.getEmail(),
				user.getRol() != null ? user.getRol().getId() : null,
				user.getRol() != null ? user.getRol().getName() : null);
	}

}
