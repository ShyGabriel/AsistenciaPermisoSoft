package com.control_asistencia.principal.modules.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.control_asistencia.principal.modules.auth.model.Role;
import com.control_asistencia.principal.modules.auth.model.User;
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
		crearRol("ADMIN");

		mockMvc.perform(get("/api/role"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].name").value("ADMIN"));
	}

	@Test
	void getRoleByIdReturnsRole() throws Exception {
		Long id = crearRol("EMPLEADO");

		mockMvc.perform(get("/api/role/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.name").value("EMPLEADO"));
	}

	@Test
	void updateRoleChangesName() throws Exception {
		Long id = crearRol("EMPLEADO");

		mockMvc.perform(put("/api/role/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"SUPERVISOR\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("SUPERVISOR"));
	}

	@Test
	void updateRoleWithDuplicateNameReturnsBadRequest() throws Exception {
		crearRol("ADMIN");
		Long id = crearRol("EMPLEADO");

		mockMvc.perform(put("/api/role/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"ADMIN\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deleteRoleWithoutUsersReturnsDeletedRole() throws Exception {
		Long id = crearRol("EMPLEADO");

		mockMvc.perform(delete("/api/role/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id));

		org.junit.jupiter.api.Assertions.assertTrue(roleRepository.findById(id).isEmpty());
	}

	@Test
	void deleteRoleWithUsersReturnsConflict() throws Exception {
		Long id = crearRol("EMPLEADO");
		Role role = roleRepository.findById(id).orElseThrow();
		userRepository.save(new User(null, "Juan", "Perez", "juan@empresa.com", "hash", role));

		mockMvc.perform(delete("/api/role/" + id))
				.andExpect(status().isConflict());
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

	@Test
	@WithMockUser(roles = "EMPLEADO")
	void deleteRoleWithoutAdminRoleReturnsForbidden() throws Exception {
		mockMvc.perform(delete("/api/role/1"))
				.andExpect(status().isForbidden());
	}

	private Long crearRol(String name) throws Exception {
		mockMvc.perform(post("/api/role")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"" + name + "\"}"))
				.andExpect(status().isCreated());
		return roleRepository.findAll().stream()
				.filter(role -> role.getName().equals(name))
				.findFirst()
				.orElseThrow()
				.getId();
	}

}
