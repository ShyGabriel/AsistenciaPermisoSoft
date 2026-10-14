package com.control_asistencia.principal.modules.horario.dto;

import com.control_asistencia.principal.modules.horario.model.DiaSemana;
import com.control_asistencia.principal.modules.horario.model.HorarioEmpleado;

public record HorarioResponse(
		Long id,
		Long usuarioId,
		String usuario,
		Long turnoId,
		String turno,
		DiaSemana diaSemana) {

	public static HorarioResponse from(HorarioEmpleado horario) {
		return new HorarioResponse(
				horario.getId(),
				horario.getUsuario() != null ? horario.getUsuario().getId() : null,
				horario.getUsuario() != null ? horario.getUsuario().getNombreCompleto() : null,
				horario.getTurno() != null ? horario.getTurno().getId() : null,
				horario.getTurno() != null ? horario.getTurno().getNombre() : null,
				horario.getDiaSemana());
	}

}
