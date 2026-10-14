package com.control_asistencia.principal.modules.auth.dto;

public record AuthResponse(String token, String email, String rol, String nombre) {

}
