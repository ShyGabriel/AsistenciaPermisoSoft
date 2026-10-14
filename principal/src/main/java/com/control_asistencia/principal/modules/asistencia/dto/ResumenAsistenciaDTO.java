package com.control_asistencia.principal.modules.asistencia.dto;

import java.time.LocalDate;

public record ResumenAsistenciaDTO(
		LocalDate desde,
		LocalDate hasta,
		long diasRegistrados,
		long completos,
		long incompletos,
		long ausentes) {

}
