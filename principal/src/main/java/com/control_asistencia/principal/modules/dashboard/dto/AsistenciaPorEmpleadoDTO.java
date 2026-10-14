package com.control_asistencia.principal.modules.dashboard.dto;

public record AsistenciaPorEmpleadoDTO(
		Long usuarioId,
		String nombres,
		String apellidos,
		Long total) {

}
