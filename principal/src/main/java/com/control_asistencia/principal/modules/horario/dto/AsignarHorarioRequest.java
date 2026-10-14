package com.control_asistencia.principal.modules.horario.dto;

import com.control_asistencia.principal.modules.horario.model.DiaSemana;

import jakarta.validation.constraints.NotNull;

public record AsignarHorarioRequest(
		@NotNull(message = "El usuario es obligatorio") Long usuarioId,
		@NotNull(message = "El turno es obligatorio") Long turnoId,
		@NotNull(message = "El día de la semana es obligatorio") DiaSemana diaSemana) {

}
