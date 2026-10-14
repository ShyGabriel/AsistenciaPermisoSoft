package com.control_asistencia.principal.modules.auth.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.control_asistencia.principal.modules.auth.model.User;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public AppUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
		return org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail())
				.password(user.getPasswordHash())
				.roles(user.getRol().getName())
				.disabled(!Boolean.TRUE.equals(user.getEstado()))
				.build();
	}

}
