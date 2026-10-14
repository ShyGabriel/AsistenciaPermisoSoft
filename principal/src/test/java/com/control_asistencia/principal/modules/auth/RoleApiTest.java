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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.control_asistencia.principal.modules.auth.repository.RoleRepository;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMIN")
class RoleApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
		roleRepository.deleteAll();
	}

	@Test
	void getRolesReturnsEmptyListWhenNone() throws Exception {
		mockMvc.perform(get("/api/role"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void createRoleReturnsCreated() throws Exception {
		mockMvc.perform(post("/api/role")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"ADMIN\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("ADMIN"));
	}

	@Test
	void getRolesReturnsCreatedRole() throws Exception {
		mockMvc.perform(post("/api/role")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"ADMIN\"}"))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/role"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].name").value("ADMIN"));
	}

	@Test
	void createRoleWithBlankNameReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/role")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(roles = "EMPLEADO")
	void createRoleWithoutAdminRoleReturnsForbidden() throws Exception {
		mockMvc.perform(post("/api/role")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"SUPERVISOR\"}"))
				.andExpect(status().isForbidden());
	}

}
