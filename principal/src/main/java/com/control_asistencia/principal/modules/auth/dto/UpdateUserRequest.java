package com.control_asistencia.principal.modules.auth.dto;

public record UpdateUserRequest(String nombres, String apellidos, String email, String password, Long roleId,
		Boolean estado) {

}
