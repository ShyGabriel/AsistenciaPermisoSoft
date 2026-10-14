package com.control_asistencia.principal.modules.asistencia.dto;

import jakarta.validation.constraints.NotNull;

public record RegistrarMarcaDTO(@NotNull(message = "El tipo de marca es obligatorio") TipoMarca tipo) {

	public enum TipoMarca {
		ENTRADA,
		INICIO_REFRIGERIO,
		FIN_REFRIGERIO,
		SALIDA
	}

}
