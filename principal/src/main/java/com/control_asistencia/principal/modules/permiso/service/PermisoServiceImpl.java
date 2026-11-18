package com.control_asistencia.principal.modules.permiso.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.control_asistencia.principal.common.exception.ResourceNotFoundException;
import com.control_asistencia.principal.common.util.DateUtils;
import com.control_asistencia.principal.modules.auth.model.User;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;
import com.control_asistencia.principal.modules.permiso.dto.AprobarPermisoDTO;
import com.control_asistencia.principal.modules.permiso.dto.SolicitudPermisoDTO;
import com.control_asistencia.principal.modules.permiso.dto.TipoPermisoRequest;
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
	@Transactional(readOnly = true)
	public TipoPermiso buscarTipo(Long id) {
		return buscarTipoEntidad(id);
	}

	@Override
	@Transactional
	public TipoPermiso crearTipo(TipoPermisoRequest request) {
		validarTipo(request);
		TipoPermiso tipo = new TipoPermiso(null, request.nombre(), request.descripcion(), request.diasMaximos());
		return tipoPermisoRepository.save(tipo);
	}

	@Override
	@Transactional
	public TipoPermiso actualizarTipo(Long id, TipoPermisoRequest request) {
		validarTipo(request);
		TipoPermiso tipo = buscarTipoEntidad(id);
		tipo.setNombre(request.nombre());
		tipo.setDescripcion(request.descripcion());
		tipo.setDiasMaximos(request.diasMaximos());
		return tipoPermisoRepository.save(tipo);
	}

	@Override
	@Transactional
	public TipoPermiso eliminarTipo(Long id) {
		TipoPermiso tipo = buscarTipoEntidad(id);
		if (permisoRepository.existsByTipoId(id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"No se puede eliminar el tipo de permiso porque tiene solicitudes asociadas");
		}
		tipoPermisoRepository.delete(tipo);
		return tipo;
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
		TipoPermiso tipo = buscarTipoEntidad(dto.idTipo());
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

	private void validarTipo(TipoPermisoRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("El tipo de permiso no puede estar vacío");
		}
		if (request.nombre() == null || request.nombre().isBlank()) {
			throw new IllegalArgumentException("El nombre del tipo de permiso es obligatorio");
		}
		if (request.diasMaximos() == null) {
			throw new IllegalArgumentException("Los días máximos son obligatorios");
		}
		if (request.diasMaximos() < 0) {
			throw new IllegalArgumentException("Los días máximos no pueden ser negativos");
		}
	}

	private TipoPermiso buscarTipoEntidad(Long id) {
		return tipoPermisoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Tipo de permiso no encontrado con id " + id));
	}

	private User buscarUsuario(String email) {
		return userRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
	}

}
