package com.control_asistencia.principal.modules.horario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.control_asistencia.principal.modules.horario.model.Turno;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

}
