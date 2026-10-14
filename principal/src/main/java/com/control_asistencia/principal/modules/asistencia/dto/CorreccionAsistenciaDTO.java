package com.control_asistencia.principal.modules.asistencia.dto;

import java.time.LocalTime;

import com.control_asistencia.principal.modules.asistencia.model.EstadoAsistencia;

public record CorreccionAsistenciaDTO(
		LocalTime horaEntrada,
		LocalTime horaIniRefrigerio,
		LocalTime horaFinRefrigerio,
		LocalTime horaSalida,
		EstadoAsistencia estado) {

}
