package com.control_asistencia.principal.modules.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.control_asistencia.principal.modules.auth.dto.AuthResponse;
import com.control_asistencia.principal.modules.auth.dto.LoginRequest;
import com.control_asistencia.principal.modules.auth.model.User;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;
import com.control_asistencia.principal.modules.auth.security.JwtService;

@Service
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final JwtService jwtService;

	public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
			JwtService jwtService) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.jwtService = jwtService;
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password()));

		User user = userRepository.findByEmailIgnoreCase(authentication.getName())
				.orElseThrow(() -> new UsernameNotFoundException(
						"Usuario no encontrado: " + authentication.getName()));
		String rol = user.getRol().getName();
		String token = jwtService.generateToken(user.getEmail(), rol);
		return new AuthResponse(token, user.getEmail(), rol, user.getNombreCompleto());
	}

}
