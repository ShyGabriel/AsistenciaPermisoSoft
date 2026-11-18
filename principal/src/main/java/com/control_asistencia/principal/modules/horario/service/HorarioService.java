package com.control_asistencia.principal.modules.horario.service;

import java.util.List;

import com.control_asistencia.principal.modules.horario.dto.AsignarHorarioRequest;
import com.control_asistencia.principal.modules.horario.dto.TurnoRequest;
import com.control_asistencia.principal.modules.horario.model.HorarioEmpleado;
import com.control_asistencia.principal.modules.horario.model.Turno;

public interface HorarioService {

	List<Turno> listarTurnos();

	Turno buscarTurno(Long id);

	Turno crearTurno(TurnoRequest request);

	Turno actualizarTurno(Long id, TurnoRequest request);

	Turno eliminarTurno(Long id);

	List<HorarioEmpleado> listarAsignaciones();

	HorarioEmpleado buscarAsignacion(Long id);

	HorarioEmpleado asignar(AsignarHorarioRequest request);

	HorarioEmpleado actualizarAsignacion(Long id, AsignarHorarioRequest request);

	HorarioEmpleado eliminarAsignacion(Long id);

}
