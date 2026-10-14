package com.control_asistencia.principal.modules.permiso.controller;

import java.security.Principal;
import java.util.List;

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
import com.control_asistencia.principal.modules.permiso.dto.AprobarPermisoDTO;
import com.control_asistencia.principal.modules.permiso.dto.PermisoResponse;
import com.control_asistencia.principal.modules.permiso.dto.SolicitudPermisoDTO;
import com.control_asistencia.principal.modules.permiso.dto.TipoPermisoResponse;
import com.control_asistencia.principal.modules.permiso.model.EstadoSolicitud;
import com.control_asistencia.principal.modules.permiso.service.PermisoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/permiso")
public class PermisoController {

	private final PermisoService permisoService;

	public PermisoController(PermisoService permisoService) {
		this.permisoService = permisoService;
	}

	@GetMapping("/tipos")
	public ApiResponse<List<TipoPermisoResponse>> listarTipos() {
		return ApiResponse.ok(permisoService.listarTipos().stream()
				.map(t -> new TipoPermisoResponse(t.getId(), t.getNombre(), t.getDescripcion(), t.getDiasMaximos()))
				.toList());
	}

	@PostMapping
	@PreAuthorize("hasRole('EMPLEADO')")
	public ApiResponse<PermisoResponse> solicitar(Principal principal, @Valid @RequestBody SolicitudPermisoDTO request) {
		return ApiResponse.ok("Solicitud registrada",
				PermisoResponse.from(permisoService.solicitar(principal.getName(), request)));
	}

	@GetMapping("/mis-permisos")
	@PreAuthorize("hasRole('EMPLEADO')")
	public ApiResponse<List<PermisoResponse>> misPermisos(Principal principal) {
		return ApiResponse.ok(permisoService.misPermisos(principal.getName()).stream()
				.map(PermisoResponse::from).toList());
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<List<PermisoResponse>> listar(@RequestParam(required = false) EstadoSolicitud estado) {
		return ApiResponse.ok(permisoService.listar(estado).stream().map(PermisoResponse::from).toList());
	}

	@PatchMapping("/{id}/resolver")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<PermisoResponse> resolver(Principal principal, @PathVariable Long id,
			@Valid @RequestBody AprobarPermisoDTO request) {
		return ApiResponse.ok("Solicitud resuelta",
				PermisoResponse.from(permisoService.resolver(id, request, principal.getName())));
	}

}
