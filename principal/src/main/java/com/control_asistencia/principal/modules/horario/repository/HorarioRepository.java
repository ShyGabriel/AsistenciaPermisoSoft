package com.control_asistencia.principal.modules.horario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.control_asistencia.principal.modules.horario.model.DiaSemana;
import com.control_asistencia.principal.modules.horario.model.HorarioEmpleado;

public interface HorarioRepository extends JpaRepository<HorarioEmpleado, Long> {

	Optional<HorarioEmpleado> findByUsuarioIdAndDiaSemana(Long usuarioId, DiaSemana diaSemana);

	List<HorarioEmpleado> findByUsuarioIdOrderByDiaSemanaAsc(Long usuarioId);

	List<HorarioEmpleado> findAllByOrderByIdAsc();

}
