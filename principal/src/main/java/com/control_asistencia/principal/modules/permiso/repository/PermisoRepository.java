package com.control_asistencia.principal.modules.permiso.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.control_asistencia.principal.modules.dashboard.dto.PermisoPorEstadoDTO;
import com.control_asistencia.principal.modules.dashboard.dto.PermisoPorTipoDTO;
import com.control_asistencia.principal.modules.permiso.model.EstadoSolicitud;
import com.control_asistencia.principal.modules.permiso.model.Permiso;

public interface PermisoRepository extends JpaRepository<Permiso, Long> {

	List<Permiso> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

	List<Permiso> findByEstadoOrderByFechaCreacionAsc(EstadoSolicitud estado);

	List<Permiso> findAllByOrderByFechaCreacionDesc();

	@Query("SELECT COUNT(p) FROM Permiso p WHERE p.estado = :estado")
	long contarPorEstado(@Param("estado") EstadoSolicitud estado);

	@Query("""
			SELECT new com.control_asistencia.principal.modules.dashboard.dto.PermisoPorEstadoDTO(p.estado, COUNT(p))
			FROM Permiso p
			GROUP BY p.estado
			""")
	List<PermisoPorEstadoDTO> permisosPorEstado();

	@Query("""
			SELECT new com.control_asistencia.principal.modules.dashboard.dto.PermisoPorTipoDTO(t.nombre, COUNT(p))
			FROM Permiso p JOIN p.tipo t
			GROUP BY t.nombre
			ORDER BY COUNT(p) DESC
			""")
	List<PermisoPorTipoDTO> permisosPorTipo();

}
