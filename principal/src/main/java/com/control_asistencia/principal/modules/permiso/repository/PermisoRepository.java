package com.control_asistencia.principal.modules.permiso.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.control_asistencia.principal.modules.permiso.model.EstadoSolicitud;
import com.control_asistencia.principal.modules.permiso.model.Permiso;

public interface PermisoRepository extends JpaRepository<Permiso, Long> {

	List<Permiso> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

	List<Permiso> findByEstadoOrderByFechaCreacionAsc(EstadoSolicitud estado);

	List<Permiso> findAllByOrderByFechaCreacionDesc();

}
