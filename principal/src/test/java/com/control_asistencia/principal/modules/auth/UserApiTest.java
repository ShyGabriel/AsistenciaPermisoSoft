package com.control_asistencia.principal.modules.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.control_asistencia.principal.modules.auth.model.Role;
import com.control_asistencia.principal.modules.auth.repository.RoleRepository;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class UserApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	private Long adminRoleId;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
		roleRepository.deleteAll();
		adminRoleId = roleRepository.save(new Role(null, "ADMIN")).getId();
	}

	@Test
	void getUsersReturnsEmptyListWhenNone() throws Exception {
		mockMvc.perform(get("/api/user"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void createUserWithValidRoleReturnsCreated() throws Exception {
		mockMvc.perform(post("/api/user")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombres\":\"Juan\",\"apellidos\":\"Perez\",\"email\":\"juan@empresa.com\",\"password\":\"secret123\",\"roleId\":" + adminRoleId + "}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.nombres").value("Juan"))
				.andExpect(jsonPath("$.apellidos").value("Perez"))
				.andExpect(jsonPath("$.email").value("juan@empresa.com"))
				.andExpect(jsonPath("$.rolNombre").value("ADMIN"));
	}

	@Test
	void getUsersReturnsCreatedUser() throws Exception {
		mockMvc.perform(post("/api/user")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombres\":\"Juan\",\"apellidos\":\"Perez\",\"email\":\"juan@empresa.com\",\"password\":\"secret123\",\"roleId\":" + adminRoleId + "}"))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/user"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].email").value("juan@empresa.com"));
	}

	@Test
	void createUserWithUnknownRoleReturnsNotFound() throws Exception {
		mockMvc.perform(post("/api/user")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombres\":\"Juan\",\"apellidos\":\"Perez\",\"email\":\"juan@empresa.com\",\"password\":\"secret123\",\"roleId\":999}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void createUserWithBlankNombresReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/user")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombres\":\"\",\"apellidos\":\"Perez\",\"email\":\"juan@empresa.com\",\"password\":\"secret123\",\"roleId\":" + adminRoleId + "}"))
				.andExpect(status().isBadRequest());
	}

}
