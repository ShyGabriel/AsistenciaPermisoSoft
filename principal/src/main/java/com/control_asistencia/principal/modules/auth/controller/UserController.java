package com.control_asistencia.principal.modules.auth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.control_asistencia.principal.modules.auth.dto.CreateUserRequest;
import com.control_asistencia.principal.modules.auth.dto.UserResponse;
import com.control_asistencia.principal.modules.auth.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public List<UserResponse> getAll() {
		return userService.findAll();
	}

	@PostMapping
	public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest request) {
		UserResponse created = userService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

}