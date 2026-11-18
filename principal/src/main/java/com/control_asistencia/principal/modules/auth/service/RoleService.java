package com.control_asistencia.principal.modules.auth.service;

import java.util.List;

import com.control_asistencia.principal.modules.auth.dto.CreateRoleRequest;
import com.control_asistencia.principal.modules.auth.dto.RoleResponse;
import com.control_asistencia.principal.modules.auth.dto.UpdateRoleRequest;

public interface RoleService {

	List<RoleResponse> findAll();

	RoleResponse findById(Long id);

	RoleResponse create(CreateRoleRequest request);

	RoleResponse update(Long id, UpdateRoleRequest request);

	RoleResponse delete(Long id);

}
