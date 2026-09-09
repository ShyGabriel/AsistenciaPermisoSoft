package com.control_asistencia.principal.modules.auth.service;

import java.util.List;

import com.control_asistencia.principal.modules.auth.dto.CreateRoleRequest;
import com.control_asistencia.principal.modules.auth.dto.RoleResponse;

public interface RoleService {

	List<RoleResponse> findAll();

	RoleResponse create(CreateRoleRequest request);

}