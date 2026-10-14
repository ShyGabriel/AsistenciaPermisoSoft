package com.control_asistencia.principal.modules.auth.dto;

public record CreateUserRequest(String nombres, String apellidos, String email, String password, Long roleId) {

}
