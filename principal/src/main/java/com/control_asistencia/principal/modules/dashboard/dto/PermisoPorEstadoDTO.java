package com.control_asistencia.principal.modules.dashboard.dto;

import com.control_asistencia.principal.modules.permiso.model.EstadoSolicitud;

public record PermisoPorEstadoDTO(
		EstadoSolicitud estado,
		Long total) {

}
