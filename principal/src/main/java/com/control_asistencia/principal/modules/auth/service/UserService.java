package com.control_asistencia.principal.modules.auth.service;

import java.util.List;

import com.control_asistencia.principal.modules.auth.dto.CreateUserRequest;
import com.control_asistencia.principal.modules.auth.dto.UserResponse;

public interface UserService {

	List<UserResponse> findAll();

	UserResponse create(CreateUserRequest request);

}