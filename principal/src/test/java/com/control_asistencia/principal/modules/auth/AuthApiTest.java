package com.control_asistencia.principal.modules.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.control_asistencia.principal.modules.auth.model.Role;
import com.control_asistencia.principal.modules.auth.model.User;
import com.control_asistencia.principal.modules.auth.repository.RoleRepository;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
		roleRepository.deleteAll();
		Role adminRole = roleRepository.save(new Role(null, "ADMIN"));
		Role empleadoRole = roleRepository.save(new Role(null, "EMPLEADO"));
		userRepository.save(new User(null, "Administrador", "Sistema", "admin@empresa.com",
				passwordEncoder.encode("Admin123!"), adminRole));
		userRepository.save(new User(null, "Empleado", "Prueba", "empleado@empresa.com",
				passwordEncoder.encode("Empleado123!"), empleadoRole));
	}

	@Test
	void loginAdminReturnsTokenAndRol() throws Exception {
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\":\"admin@empresa.com\",\"password\":\"Admin123!\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").isNotEmpty())
				.andExpect(jsonPath("$.email").value("admin@empresa.com"))
				.andExpect(jsonPath("$.rol").value("ADMIN"))
				.andExpect(jsonPath("$.nombre").value("Administrador Sistema"));
	}

	@Test
	void loginEmpleadoReturnsTokenAndRol() throws Exception {
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\":\"empleado@empresa.com\",\"password\":\"Empleado123!\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").isNotEmpty())
				.andExpect(jsonPath("$.email").value("empleado@empresa.com"))
				.andExpect(jsonPath("$.rol").value("EMPLEADO"));
	}

	@Test
	void loginWithWrongPasswordReturnsUnauthorized() throws Exception {
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\":\"admin@empresa.com\",\"password\":\"incorrecta\"}"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void loginWithInvalidEmailReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\":\"no-es-email\",\"password\":\"Admin123!\"}"))
				.andExpect(status().isBadRequest());
	}

}
