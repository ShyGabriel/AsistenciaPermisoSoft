package com.control_asistencia.principal.modules.dashboard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.control_asistencia.principal.modules.asistencia.model.EstadoAsistencia;
import com.control_asistencia.principal.modules.asistencia.repository.AsistenciaRepository;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;
import com.control_asistencia.principal.modules.dashboard.dto.DashboardResumenDTO;
import com.control_asistencia.principal.modules.permiso.model.EstadoSolicitud;
import com.control_asistencia.principal.modules.permiso.repository.PermisoRepository;

@Service
public class DashboardServiceImpl implements DashboardService {

	private final AsistenciaRepository asistenciaRepository;
	private final PermisoRepository permisoRepository;
	private final UserRepository userRepository;

	public DashboardServiceImpl(AsistenciaRepository asistenciaRepository, PermisoRepository permisoRepository,
			UserRepository userRepository) {
		this.asistenciaRepository = asistenciaRepository;
		this.permisoRepository = permisoRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public DashboardResumenDTO resumen() {
		return new DashboardResumenDTO(
				userRepository.contarPorRol("EMPLEADO"),
				asistenciaRepository.count(),
				asistenciaRepository.contarPorEstado(EstadoAsistencia.COMPLETA),
				asistenciaRepository.contarPorEstado(EstadoAsistencia.INCOMPLETA),
				asistenciaRepository.contarPorEstado(EstadoAsistencia.AUSENTE),
				permisoRepository.contarPorEstado(EstadoSolicitud.PENDIENTE),
				permisoRepository.contarPorEstado(EstadoSolicitud.APROBADO),
				permisoRepository.contarPorEstado(EstadoSolicitud.RECHAZADO),
				asistenciaRepository.asistenciasPorEmpleado(),
				permisoRepository.permisosPorEstado(),
				permisoRepository.permisosPorTipo());
	}

}
