package com.control_asistencia.principal.modules.asistencia.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.control_asistencia.principal.modules.asistencia.model.Asistencia;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

	Optional<Asistencia> findByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);

	List<Asistencia> findByUsuarioIdAndFechaBetweenOrderByFechaDesc(Long usuarioId, LocalDate desde, LocalDate hasta);

	List<Asistencia> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

	List<Asistencia> findAllByOrderByFechaDesc();

}
