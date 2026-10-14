package com.control_asistencia.principal.modules.asistencia.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.control_asistencia.principal.common.dto.ApiResponse;
import com.control_asistencia.principal.modules.asistencia.dto.AsistenciaResponse;
import com.control_asistencia.principal.modules.asistencia.dto.CorreccionAsistenciaDTO;
import com.control_asistencia.principal.modules.asistencia.dto.RegistrarMarcaDTO;
import com.control_asistencia.principal.modules.asistencia.dto.ResumenAsistenciaDTO;
import com.control_asistencia.principal.modules.asistencia.service.AsistenciaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/asistencia")
public class AsistenciaController {

	private final AsistenciaService asistenciaService;

	public AsistenciaController(AsistenciaService asistenciaService) {
		this.asistenciaService = asistenciaService;
	}

	@PostMapping("/marca")
	@PreAuthorize("hasRole('EMPLEADO')")
	public ApiResponse<AsistenciaResponse> registrarMarca(Principal principal,
			@Valid @RequestBody RegistrarMarcaDTO request) {
		return ApiResponse.ok("Marca registrada",
				AsistenciaResponse.from(asistenciaService.registrarMarca(principal.getName(), request)));
	}

	@GetMapping("/mi-resumen")
	@PreAuthorize("hasRole('EMPLEADO')")
	public ApiResponse<ResumenAsistenciaDTO> miResumen(Principal principal,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
		return ApiResponse.ok(asistenciaService.resumen(principal.getName(), desde, hasta));
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<List<AsistenciaResponse>> listar() {
		return ApiResponse.ok(asistenciaService.listar().stream().map(AsistenciaResponse::from).toList());
	}

	@GetMapping("/usuario/{usuarioId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<List<AsistenciaResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
		return ApiResponse.ok(asistenciaService.listarPorUsuario(usuarioId).stream()
				.map(AsistenciaResponse::from).toList());
	}

	@PatchMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<AsistenciaResponse> corregir(@PathVariable Long id,
			@RequestBody CorreccionAsistenciaDTO request) {
		return ApiResponse.ok("Asistencia corregida",
				AsistenciaResponse.from(asistenciaService.corregir(id, request)));
	}

}
