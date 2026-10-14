package com.control_asistencia.principal.modules.auth.service;

import com.control_asistencia.principal.modules.auth.dto.AuthResponse;
import com.control_asistencia.principal.modules.auth.dto.LoginRequest;

public interface AuthService {

	AuthResponse login(LoginRequest request);

}
