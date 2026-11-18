package com.control_asistencia.principal.modules.publico.dto;

public record EstadoBackendResponse(
		String servicio,
		boolean backend,
		boolean jpa,
		long usuariosAdmin,
		long usuariosEmpleado) {

}
