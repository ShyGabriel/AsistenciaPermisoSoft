package com.control_asistencia.principal.common.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	private DateUtils() {
	}

	public static LocalDate hoy() {
		return LocalDate.now();
	}

	public static String formatearHora(LocalTime hora) {
		return hora == null ? null : hora.format(TIME_FORMATTER);
	}

	public static boolean rangoValido(LocalDate inicio, LocalDate fin) {
		return inicio != null && fin != null && !fin.isBefore(inicio);
	}

}
