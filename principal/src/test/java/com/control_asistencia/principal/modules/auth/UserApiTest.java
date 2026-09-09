package com.control_asistencia.principal.modules.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

	@BeforeEach
	void setUp() {
		userRepository.reset();
		roleRepository.reset();
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
		mockMvc.perform(post("/api/role")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"ADMIN\"}"))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/user")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"juan\",\"roleId\":1}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.username").value("juan"))
				.andExpect(jsonPath("$.roleId").value(1))
				.andExpect(jsonPath("$.roleName").value("ADMIN"));
	}

	@Test
	void getUsersReturnsCreatedUser() throws Exception {
		mockMvc.perform(post("/api/role")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"ADMIN\"}"))
				.andExpect(status().isCreated());
		mockMvc.perform(post("/api/user")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"juan\",\"roleId\":1}"))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/user"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].username").value("juan"));
	}

	@Test
	void createUserWithUnknownRoleReturnsNotFound() throws Exception {
		mockMvc.perform(post("/api/user")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"juan\",\"roleId\":999}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void createUserWithBlankUsernameReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/user")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"\",\"roleId\":1}"))
				.andExpect(status().isBadRequest());
	}

}