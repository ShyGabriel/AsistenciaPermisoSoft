package com.control_asistencia.principal.modules.permiso.service;

import java.util.List;

import com.control_asistencia.principal.modules.permiso.dto.AprobarPermisoDTO;
import com.control_asistencia.principal.modules.permiso.dto.SolicitudPermisoDTO;
import com.control_asistencia.principal.modules.permiso.model.EstadoSolicitud;
import com.control_asistencia.principal.modules.permiso.model.Permiso;
import com.control_asistencia.principal.modules.permiso.model.TipoPermiso;

public interface PermisoService {

	List<TipoPermiso> listarTipos();

	Permiso solicitar(String email, SolicitudPermisoDTO dto);

	List<Permiso> misPermisos(String email);

	List<Permiso> listar(EstadoSolicitud estado);

	Permiso resolver(Long id, AprobarPermisoDTO dto, String adminEmail);

}
