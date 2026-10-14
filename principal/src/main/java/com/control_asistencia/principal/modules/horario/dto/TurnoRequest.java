package com.control_asistencia.principal.modules.horario.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TurnoRequest(
		@NotBlank(message = "El nombre del turno es obligatorio") String nombre,
		@NotNull(message = "La hora de entrada es obligatoria") LocalTime horaEntrada,
		@NotNull(message = "La hora de salida es obligatoria") LocalTime horaSalida,
		LocalTime horaIniRefrigerio,
		LocalTime horaFinRefrigerio) {

}
