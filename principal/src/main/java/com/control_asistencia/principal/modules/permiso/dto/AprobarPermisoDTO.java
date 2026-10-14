package com.control_asistencia.principal.modules.permiso.dto;

import com.control_asistencia.principal.modules.permiso.model.EstadoSolicitud;

import jakarta.validation.constraints.NotNull;

public record AprobarPermisoDTO(@NotNull(message = "El estado es obligatorio") EstadoSolicitud estado) {

}
