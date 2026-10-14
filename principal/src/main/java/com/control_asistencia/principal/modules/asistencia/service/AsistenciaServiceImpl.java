package com.control_asistencia.principal.modules.asistencia.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.control_asistencia.principal.common.exception.ResourceNotFoundException;
import com.control_asistencia.principal.modules.asistencia.dto.CorreccionAsistenciaDTO;
import com.control_asistencia.principal.modules.asistencia.dto.RegistrarMarcaDTO;
import com.control_asistencia.principal.modules.asistencia.dto.RegistrarMarcaDTO.TipoMarca;
import com.control_asistencia.principal.modules.asistencia.dto.ResumenAsistenciaDTO;
import com.control_asistencia.principal.modules.asistencia.model.Asistencia;
import com.control_asistencia.principal.modules.asistencia.model.EstadoAsistencia;
import com.control_asistencia.principal.modules.asistencia.repository.AsistenciaRepository;
import com.control_asistencia.principal.modules.auth.model.User;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {

	private final AsistenciaRepository asistenciaRepository;
	private final UserRepository userRepository;

	public AsistenciaServiceImpl(AsistenciaRepository asistenciaRepository, UserRepository userRepository) {
		this.asistenciaRepository = asistenciaRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public Asistencia registrarMarca(String email, RegistrarMarcaDTO dto) {
		if (dto == null || dto.tipo() == null) {
			throw new IllegalArgumentException("El tipo de marca es obligatorio");
		}
		User usuario = buscarUsuario(email);
		LocalDate hoy = LocalDate.now();
		Asistencia asistencia = asistenciaRepository.findByUsuarioIdAndFecha(usuario.getId(), hoy)
				.orElseGet(() -> new Asistencia(usuario, hoy));

		LocalTime ahora = LocalTime.now().withNano(0);
		switch (dto.tipo()) {
			case ENTRADA -> asistencia.setHoraEntrada(ahora);
			case INICIO_REFRIGERIO -> asistencia.setHoraIniRefrigerio(ahora);
			case FIN_REFRIGERIO -> asistencia.setHoraFinRefrigerio(ahora);
			case SALIDA -> asistencia.setHoraSalida(ahora);
		}
		asistencia.setEstado(asistencia.estaCompleta() ? EstadoAsistencia.COMPLETA : EstadoAsistencia.INCOMPLETA);
		return asistenciaRepository.save(asistencia);
	}

	@Override
	@Transactional(readOnly = true)
	public ResumenAsistenciaDTO resumen(String email, LocalDate desde, LocalDate hasta) {
		User usuario = buscarUsuario(email);
		LocalDate inicio = desde != null ? desde : LocalDate.now().withDayOfMonth(1);
		LocalDate fin = hasta != null ? hasta : LocalDate.now();
		List<Asistencia> registros = asistenciaRepository
				.findByUsuarioIdAndFechaBetweenOrderByFechaDesc(usuario.getId(), inicio, fin);
		long completos = registros.stream().filter(a -> a.getEstado() == EstadoAsistencia.COMPLETA).count();
		long incompletos = registros.stream().filter(a -> a.getEstado() == EstadoAsistencia.INCOMPLETA).count();
		long ausentes = registros.stream().filter(a -> a.getEstado() == EstadoAsistencia.AUSENTE).count();
		return new ResumenAsistenciaDTO(inicio, fin, registros.size(), completos, incompletos, ausentes);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Asistencia> listar() {
		return asistenciaRepository.findAllByOrderByFechaDesc();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Asistencia> listarPorUsuario(Long usuarioId) {
		return asistenciaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
	}

	@Override
	@Transactional
	public Asistencia corregir(Long id, CorreccionAsistenciaDTO dto) {
		Asistencia asistencia = asistenciaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Asistencia no encontrada con id " + id));
		if (dto == null) {
			throw new IllegalArgumentException("La corrección no puede estar vacía");
		}
		if (dto.horaEntrada() != null) {
			asistencia.setHoraEntrada(dto.horaEntrada());
		}
		if (dto.horaIniRefrigerio() != null) {
			asistencia.setHoraIniRefrigerio(dto.horaIniRefrigerio());
		}
		if (dto.horaFinRefrigerio() != null) {
			asistencia.setHoraFinRefrigerio(dto.horaFinRefrigerio());
		}
		if (dto.horaSalida() != null) {
			asistencia.setHoraSalida(dto.horaSalida());
		}
		if (dto.estado() != null) {
			asistencia.setEstado(dto.estado());
		} else {
			asistencia.setEstado(asistencia.estaCompleta() ? EstadoAsistencia.COMPLETA : EstadoAsistencia.INCOMPLETA);
		}
		return asistenciaRepository.save(asistencia);
	}

	private User buscarUsuario(String email) {
		return userRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
	}

}
