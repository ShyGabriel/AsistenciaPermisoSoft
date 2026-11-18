package com.control_asistencia.principal.modules.horario.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
	@Transactional(readOnly = true)
	public Turno buscarTurno(Long id) {
		return buscarTurnoEntidad(id);
	}

	@Override
	@Transactional
	public Turno crearTurno(TurnoRequest request) {
		validarTurno(request);
		Turno turno = new Turno(null, request.nombre(), request.horaEntrada(), request.horaSalida(),
				request.horaIniRefrigerio(), request.horaFinRefrigerio());
		return turnoRepository.save(turno);
	}

	@Override
	@Transactional
	public Turno actualizarTurno(Long id, TurnoRequest request) {
		validarTurno(request);
		Turno turno = buscarTurnoEntidad(id);
		turno.setNombre(request.nombre());
		turno.setHoraEntrada(request.horaEntrada());
		turno.setHoraSalida(request.horaSalida());
		turno.setHoraIniRefrigerio(request.horaIniRefrigerio());
		turno.setHoraFinRefrigerio(request.horaFinRefrigerio());
		return turnoRepository.save(turno);
	}

	@Override
	@Transactional
	public Turno eliminarTurno(Long id) {
		Turno turno = buscarTurnoEntidad(id);
		if (horarioRepository.existsByTurnoId(id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"No se puede eliminar el turno porque está asignado a horarios");
		}
		turnoRepository.delete(turno);
		return turno;
	}

	@Override
	@Transactional(readOnly = true)
	public List<HorarioEmpleado> listarAsignaciones() {
		return horarioRepository.findAllByOrderByIdAsc();
	}

	@Override
	@Transactional(readOnly = true)
	public HorarioEmpleado buscarAsignacion(Long id) {
		return buscarAsignacionEntidad(id);
	}

	@Override
	@Transactional
	public HorarioEmpleado asignar(AsignarHorarioRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("La asignación no puede estar vacía");
		}
		User usuario = buscarUsuario(request.usuarioId());
		Turno turno = buscarTurnoEntidad(request.turnoId());
		HorarioEmpleado horario = horarioRepository
				.findByUsuarioIdAndDiaSemana(request.usuarioId(), request.diaSemana())
				.orElseGet(() -> new HorarioEmpleado(usuario, turno, request.diaSemana()));
		horario.setUsuario(usuario);
		horario.setTurno(turno);
		horario.setDiaSemana(request.diaSemana());
		return horarioRepository.save(horario);
	}

	@Override
	@Transactional
	public HorarioEmpleado actualizarAsignacion(Long id, AsignarHorarioRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("La asignación no puede estar vacía");
		}
		HorarioEmpleado horario = buscarAsignacionEntidad(id);
		User usuario = buscarUsuario(request.usuarioId());
		Turno turno = buscarTurnoEntidad(request.turnoId());
		horarioRepository.findByUsuarioIdAndDiaSemana(request.usuarioId(), request.diaSemana())
				.filter(existente -> !existente.getId().equals(id))
				.ifPresent(existente -> {
					throw new ResponseStatusException(HttpStatus.CONFLICT,
							"Ya existe una asignación para ese usuario y día");
				});
		horario.setUsuario(usuario);
		horario.setTurno(turno);
		horario.setDiaSemana(request.diaSemana());
		return horarioRepository.save(horario);
	}

	@Override
	@Transactional
	public HorarioEmpleado eliminarAsignacion(Long id) {
		HorarioEmpleado horario = buscarAsignacionEntidad(id);
		horarioRepository.delete(horario);
		return horario;
	}

	private void validarTurno(TurnoRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("El turno no puede estar vacío");
		}
		if (request.nombre() == null || request.nombre().isBlank()) {
			throw new IllegalArgumentException("El nombre del turno es obligatorio");
		}
		if (request.horaEntrada() == null || request.horaSalida() == null) {
			throw new IllegalArgumentException("Las horas de entrada y salida son obligatorias");
		}
		if (!request.horaSalida().isAfter(request.horaEntrada())) {
			throw new IllegalArgumentException("La hora de salida debe ser posterior a la hora de entrada");
		}
		boolean refrigerioIncompleto = (request.horaIniRefrigerio() == null) != (request.horaFinRefrigerio() == null);
		if (refrigerioIncompleto) {
			throw new IllegalArgumentException("Debe indicar el inicio y el fin del refrigerio");
		}
		if (request.horaIniRefrigerio() != null) {
			if (!request.horaFinRefrigerio().isAfter(request.horaIniRefrigerio())) {
				throw new IllegalArgumentException("La hora de fin del refrigerio debe ser posterior al inicio");
			}
			if (request.horaIniRefrigerio().isBefore(request.horaEntrada())
					|| request.horaFinRefrigerio().isAfter(request.horaSalida())) {
				throw new IllegalArgumentException("El refrigerio debe estar dentro del horario laboral");
			}
		}
	}

	private Turno buscarTurnoEntidad(Long id) {
		return turnoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con id " + id));
	}

	private HorarioEmpleado buscarAsignacionEntidad(Long id) {
		return horarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Asignación no encontrada con id " + id));
	}

	private User buscarUsuario(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
	}

}
