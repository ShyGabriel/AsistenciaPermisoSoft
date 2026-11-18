package com.control_asistencia.principal.modules.auth.service;

import java.util.List;

import com.control_asistencia.principal.modules.auth.dto.CreateUserRequest;
import com.control_asistencia.principal.modules.auth.dto.UpdateUserRequest;
import com.control_asistencia.principal.modules.auth.dto.UserResponse;

public interface UserService {

	List<UserResponse> findAll();

	UserResponse findById(Long id);

	UserResponse create(CreateUserRequest request);

	UserResponse update(Long id, UpdateUserRequest request);

	UserResponse deactivate(Long id, String currentAdminEmail);

}
