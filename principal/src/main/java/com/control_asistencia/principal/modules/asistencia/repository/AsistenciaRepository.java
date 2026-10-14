package com.control_asistencia.principal.modules.asistencia.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.control_asistencia.principal.modules.asistencia.model.Asistencia;
import com.control_asistencia.principal.modules.asistencia.model.EstadoAsistencia;
import com.control_asistencia.principal.modules.dashboard.dto.AsistenciaPorEmpleadoDTO;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

	Optional<Asistencia> findByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);

	List<Asistencia> findByUsuarioIdAndFechaBetweenOrderByFechaDesc(Long usuarioId, LocalDate desde, LocalDate hasta);

	List<Asistencia> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

	List<Asistencia> findAllByOrderByFechaDesc();

	@Query("SELECT COUNT(a) FROM Asistencia a WHERE a.estado = :estado")
	long contarPorEstado(@Param("estado") EstadoAsistencia estado);

	@Query("""
			SELECT new com.control_asistencia.principal.modules.dashboard.dto.AsistenciaPorEmpleadoDTO(
				u.id, u.nombres, u.apellidos, COUNT(a))
			FROM Asistencia a JOIN a.usuario u
			GROUP BY u.id, u.nombres, u.apellidos
			ORDER BY COUNT(a) DESC
			""")
	List<AsistenciaPorEmpleadoDTO> asistenciasPorEmpleado();

}
