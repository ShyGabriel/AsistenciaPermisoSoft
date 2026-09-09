package com.control_asistencia.principal.modules.auth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.control_asistencia.principal.modules.auth.dto.CreateRoleRequest;
import com.control_asistencia.principal.modules.auth.dto.RoleResponse;
import com.control_asistencia.principal.modules.auth.service.RoleService;

@RestController
@RequestMapping("/api/role")
public class RoleController {

	private final RoleService roleService;

	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	@GetMapping
	public List<RoleResponse> getAll() {
		return roleService.findAll();
	}

	@PostMapping
	public ResponseEntity<RoleResponse> create(@RequestBody CreateRoleRequest request) {
		RoleResponse created = roleService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

}