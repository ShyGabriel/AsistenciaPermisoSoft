package com.control_asistencia.principal.modules.horario.dto;

import java.time.LocalTime;

import com.control_asistencia.principal.modules.horario.model.Turno;

public record TurnoResponse(
		Long id,
		String nombre,
		LocalTime horaEntrada,
		LocalTime horaSalida,
		LocalTime horaIniRefrigerio,
		LocalTime horaFinRefrigerio) {

	public static TurnoResponse from(Turno turno) {
		return new TurnoResponse(turno.getId(), turno.getNombre(), turno.getHoraEntrada(), turno.getHoraSalida(),
				turno.getHoraIniRefrigerio(), turno.getHoraFinRefrigerio());
	}

}
