package com.control_asistencia.principal.modules.permiso.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.control_asistencia.principal.modules.permiso.model.EstadoSolicitud;
import com.control_asistencia.principal.modules.permiso.model.Permiso;

public record PermisoResponse(
		Long id,
		Long usuarioId,
		String usuario,
		Long tipoId,
		String tipo,
		LocalDate fechaInicio,
		LocalDate fechaFin,
		String motivo,
		EstadoSolicitud estado,
		LocalDateTime fechaCreacion,
		LocalDateTime fechaRespuesta,
		String adminResponde) {

	public static PermisoResponse from(Permiso permiso) {
		return new PermisoResponse(
				permiso.getId(),
				permiso.getUsuario() != null ? permiso.getUsuario().getId() : null,
				permiso.getUsuario() != null ? permiso.getUsuario().getNombreCompleto() : null,
				permiso.getTipo() != null ? permiso.getTipo().getId() : null,
				permiso.getTipo() != null ? permiso.getTipo().getNombre() : null,
				permiso.getFechaInicio(),
				permiso.getFechaFin(),
				permiso.getMotivo(),
				permiso.getEstado(),
				permiso.getFechaCreacion(),
				permiso.getFechaRespuesta(),
				permiso.getAdminResponde() != null ? permiso.getAdminResponde().getNombreCompleto() : null);
	}

}
