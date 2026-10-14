package com.control_asistencia.principal.modules.permiso.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record SolicitudPermisoDTO(
		@NotNull(message = "El tipo de permiso es obligatorio") Long idTipo,
		@NotNull(message = "La fecha de inicio es obligatoria") LocalDate fechaInicio,
		@NotNull(message = "La fecha de fin es obligatoria") LocalDate fechaFin,
		String motivo) {

}
