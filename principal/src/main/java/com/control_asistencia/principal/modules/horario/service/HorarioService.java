package com.control_asistencia.principal.modules.horario.service;

import java.util.List;

import com.control_asistencia.principal.modules.horario.dto.AsignarHorarioRequest;
import com.control_asistencia.principal.modules.horario.dto.TurnoRequest;
import com.control_asistencia.principal.modules.horario.model.HorarioEmpleado;
import com.control_asistencia.principal.modules.horario.model.Turno;

public interface HorarioService {

	List<Turno> listarTurnos();

	Turno crearTurno(TurnoRequest request);

	List<HorarioEmpleado> listarAsignaciones();

	HorarioEmpleado asignar(AsignarHorarioRequest request);

}
