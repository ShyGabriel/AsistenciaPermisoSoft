package com.control_asistencia.principal.modules.asistencia.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.control_asistencia.principal.modules.asistencia.model.Asistencia;
import com.control_asistencia.principal.modules.asistencia.model.EstadoAsistencia;

public record AsistenciaResponse(
		Long id,
		Long usuarioId,
		String usuario,
		LocalDate fecha,
		LocalTime horaEntrada,
		LocalTime horaIniRefrigerio,
		LocalTime horaFinRefrigerio,
		LocalTime horaSalida,
		EstadoAsistencia estado) {

	public static AsistenciaResponse from(Asistencia asistencia) {
		return new AsistenciaResponse(
				asistencia.getId(),
				asistencia.getUsuario() != null ? asistencia.getUsuario().getId() : null,
				asistencia.getUsuario() != null ? asistencia.getUsuario().getNombreCompleto() : null,
				asistencia.getFecha(),
				asistencia.getHoraEntrada(),
				asistencia.getHoraIniRefrigerio(),
				asistencia.getHoraFinRefrigerio(),
				asistencia.getHoraSalida(),
				asistencia.getEstado());
	}

}
