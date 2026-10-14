package com.control_asistencia.principal.modules.horario.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.control_asistencia.principal.common.dto.ApiResponse;
import com.control_asistencia.principal.modules.horario.dto.AsignarHorarioRequest;
import com.control_asistencia.principal.modules.horario.dto.HorarioResponse;
import com.control_asistencia.principal.modules.horario.dto.TurnoRequest;
import com.control_asistencia.principal.modules.horario.dto.TurnoResponse;
import com.control_asistencia.principal.modules.horario.service.HorarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/horario")
@PreAuthorize("hasRole('ADMIN')")
public class HorarioController {

	private final HorarioService horarioService;

	public HorarioController(HorarioService horarioService) {
		this.horarioService = horarioService;
	}

	@GetMapping("/turnos")
	public ApiResponse<List<TurnoResponse>> listarTurnos() {
		return ApiResponse.ok(horarioService.listarTurnos().stream().map(TurnoResponse::from).toList());
	}

	@PostMapping("/turnos")
	public ApiResponse<TurnoResponse> crearTurno(@Valid @RequestBody TurnoRequest request) {
		return ApiResponse.ok("Turno creado", TurnoResponse.from(horarioService.crearTurno(request)));
	}

	@GetMapping("/asignaciones")
	public ApiResponse<List<HorarioResponse>> listarAsignaciones() {
		return ApiResponse.ok(horarioService.listarAsignaciones().stream().map(HorarioResponse::from).toList());
	}

	@PostMapping("/asignar")
	public ApiResponse<HorarioResponse> asignar(@Valid @RequestBody AsignarHorarioRequest request) {
		return ApiResponse.ok("Horario asignado", HorarioResponse.from(horarioService.asignar(request)));
	}

}
