package com.control_asistencia.principal.modules.horario.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.control_asistencia.principal.common.exception.ResourceNotFoundException;
import com.control_asistencia.principal.modules.auth.model.User;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;
import com.control_asistencia.principal.modules.horario.dto.AsignarHorarioRequest;
import com.control_asistencia.principal.modules.horario.dto.TurnoRequest;
import com.control_asistencia.principal.modules.horario.model.HorarioEmpleado;
import com.control_asistencia.principal.modules.horario.model.Turno;
import com.control_asistencia.principal.modules.horario.repository.HorarioRepository;
import com.control_asistencia.principal.modules.horario.repository.TurnoRepository;

@Service
public class HorarioServiceImpl implements HorarioService {

	private final TurnoRepository turnoRepository;
	private final HorarioRepository horarioRepository;
	private final UserRepository userRepository;

	public HorarioServiceImpl(TurnoRepository turnoRepository, HorarioRepository horarioRepository,
			UserRepository userRepository) {
		this.turnoRepository = turnoRepository;
		this.horarioRepository = horarioRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Turno> listarTurnos() {
		return turnoRepository.findAll();
	}

	@Override
	@Transactional
	public Turno crearTurno(TurnoRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("El turno no puede estar vacío");
		}
		Turno turno = new Turno(null, request.nombre(), request.horaEntrada(), request.horaSalida(),
				request.horaIniRefrigerio(), request.horaFinRefrigerio());
		return turnoRepository.save(turno);
	}

	@Override
	@Transactional(readOnly = true)
	public List<HorarioEmpleado> listarAsignaciones() {
		return horarioRepository.findAllByOrderByIdAsc();
	}

	@Override
	@Transactional
	public HorarioEmpleado asignar(AsignarHorarioRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("La asignación no puede estar vacía");
		}
		User usuario = userRepository.findById(request.usuarioId())
				.orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + request.usuarioId()));
		Turno turno = turnoRepository.findById(request.turnoId())
				.orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con id " + request.turnoId()));
		HorarioEmpleado horario = horarioRepository
				.findByUsuarioIdAndDiaSemana(request.usuarioId(), request.diaSemana())
				.orElseGet(() -> new HorarioEmpleado(usuario, turno, request.diaSemana()));
		horario.setUsuario(usuario);
		horario.setTurno(turno);
		horario.setDiaSemana(request.diaSemana());
		return horarioRepository.save(horario);
	}

}
