package com.control_asistencia.principal.modules.publico.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.control_asistencia.principal.common.dto.ApiResponse;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;
import com.control_asistencia.principal.modules.publico.dto.EstadoBackendResponse;

@RestController
@RequestMapping("/api/public")
public class PublicController {

	private final UserRepository userRepository;

	public PublicController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping(value = "/saludo", produces = MediaType.TEXT_PLAIN_VALUE)
	public String saludo() {
		return "techsupport - recurso";
	}

	@GetMapping("/estado")
	public ResponseEntity<ApiResponse<EstadoBackendResponse>> estado() {
		try {
			long usuariosAdmin = userRepository.contarPorRol("ADMIN");
			long usuariosEmpleado = userRepository.contarPorRol("EMPLEADO");
			EstadoBackendResponse estado = new EstadoBackendResponse(
					"techsupport - recurso", true, true, usuariosAdmin, usuariosEmpleado);
			return ResponseEntity.ok(ApiResponse.ok(estado));
		} catch (DataAccessException ex) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
					.body(ApiResponse.error("No se pudo conectar con la base de datos"));
		}
	}

}
