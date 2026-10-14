package com.control_asistencia.principal.modules.asistencia.service;

import java.time.LocalDate;
import java.util.List;

import com.control_asistencia.principal.modules.asistencia.dto.CorreccionAsistenciaDTO;
import com.control_asistencia.principal.modules.asistencia.dto.RegistrarMarcaDTO;
import com.control_asistencia.principal.modules.asistencia.dto.ResumenAsistenciaDTO;
import com.control_asistencia.principal.modules.asistencia.model.Asistencia;

public interface AsistenciaService {

	Asistencia registrarMarca(String email, RegistrarMarcaDTO dto);

	ResumenAsistenciaDTO resumen(String email, LocalDate desde, LocalDate hasta);

	List<Asistencia> listar();

	List<Asistencia> listarPorUsuario(Long usuarioId);

	Asistencia corregir(Long id, CorreccionAsistenciaDTO dto);

}
