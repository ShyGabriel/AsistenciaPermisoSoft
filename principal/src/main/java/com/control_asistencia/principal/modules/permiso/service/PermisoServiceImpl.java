package com.control_asistencia.principal.modules.permiso.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.control_asistencia.principal.common.exception.ResourceNotFoundException;
import com.control_asistencia.principal.common.util.DateUtils;
import com.control_asistencia.principal.modules.auth.model.User;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;
import com.control_asistencia.principal.modules.permiso.dto.AprobarPermisoDTO;
import com.control_asistencia.principal.modules.permiso.dto.SolicitudPermisoDTO;
import com.control_asistencia.principal.modules.permiso.model.EstadoSolicitud;
import com.control_asistencia.principal.modules.permiso.model.Permiso;
import com.control_asistencia.principal.modules.permiso.model.TipoPermiso;
import com.control_asistencia.principal.modules.permiso.repository.PermisoRepository;
import com.control_asistencia.principal.modules.permiso.repository.TipoPermisoRepository;

@Service
public class PermisoServiceImpl implements PermisoService {

	private final PermisoRepository permisoRepository;
	private final TipoPermisoRepository tipoPermisoRepository;
	private final UserRepository userRepository;

	public PermisoServiceImpl(PermisoRepository permisoRepository, TipoPermisoRepository tipoPermisoRepository,
			UserRepository userRepository) {
		this.permisoRepository = permisoRepository;
		this.tipoPermisoRepository = tipoPermisoRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TipoPermiso> listarTipos() {
		return tipoPermisoRepository.findAll();
	}

	@Override
	@Transactional
	public Permiso solicitar(String email, SolicitudPermisoDTO dto) {
		if (dto == null) {
			throw new IllegalArgumentException("La solicitud no puede estar vacía");
		}
		if (!DateUtils.rangoValido(dto.fechaInicio(), dto.fechaFin())) {
			throw new IllegalArgumentException("El rango de fechas no es válido");
		}
		User usuario = buscarUsuario(email);
		TipoPermiso tipo = tipoPermisoRepository.findById(dto.idTipo())
				.orElseThrow(() -> new ResourceNotFoundException("Tipo de permiso no encontrado con id " + dto.idTipo()));
		Permiso permiso = new Permiso(usuario, tipo, dto.fechaInicio(), dto.fechaFin(), dto.motivo());
		return permisoRepository.save(permiso);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Permiso> misPermisos(String email) {
		User usuario = buscarUsuario(email);
		return permisoRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuario.getId());
	}

	@Override
	@Transactional(readOnly = true)
	public List<Permiso> listar(EstadoSolicitud estado) {
		if (estado == null) {
			return permisoRepository.findAllByOrderByFechaCreacionDesc();
		}
		return permisoRepository.findByEstadoOrderByFechaCreacionAsc(estado);
	}

	@Override
	@Transactional
	public Permiso resolver(Long id, AprobarPermisoDTO dto, String adminEmail) {
		if (dto == null || dto.estado() == null) {
			throw new IllegalArgumentException("El estado es obligatorio");
		}
		if (dto.estado() == EstadoSolicitud.PENDIENTE) {
			throw new IllegalArgumentException("El estado debe ser APROBADO o RECHAZADO");
		}
		Permiso permiso = permisoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con id " + id));
		User admin = buscarUsuario(adminEmail);
		permiso.setEstado(dto.estado());
		permiso.setAdminResponde(admin);
		permiso.setFechaRespuesta(LocalDateTime.now());
		return permisoRepository.save(permiso);
	}

	private User buscarUsuario(String email) {
		return userRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
	}

}
