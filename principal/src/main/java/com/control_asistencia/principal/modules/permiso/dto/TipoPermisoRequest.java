package com.control_asistencia.principal.modules.permiso.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TipoPermisoRequest(
		@NotBlank(message = "El nombre del tipo de permiso es obligatorio") String nombre,
		String descripcion,
		@NotNull(message = "Los días máximos son obligatorios")
		@Min(value = 0, message = "Los días máximos no pueden ser negativos") Integer diasMaximos) {

}
