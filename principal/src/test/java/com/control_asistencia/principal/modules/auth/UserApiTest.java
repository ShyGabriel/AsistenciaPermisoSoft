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
import com.control_asistencia.principal.modules.auth.repository.RoleRepository;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMIN")
class UserApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	private Long adminRoleId;
	private Long empleadoRoleId;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
		roleRepository.deleteAll();
		adminRoleId = roleRepository.save(new Role(null, "ADMIN")).getId();
		empleadoRoleId = roleRepository.save(new Role(null, "EMPLEADO")).getId();
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
				.andExpect(jsonPath("$.rolNombre").value("ADMIN"))
				.andExpect(jsonPath("$.estado").value(true));
	}

	@Test
	void getUsersReturnsCreatedUser() throws Exception {
		crearUsuario("Juan", "Perez", "juan@empresa.com", adminRoleId);

		mockMvc.perform(get("/api/user"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].email").value("juan@empresa.com"));
	}

	@Test
	void getUserByIdReturnsUser() throws Exception {
		Long id = crearUsuario("Juan", "Perez", "juan@empresa.com", adminRoleId);

		mockMvc.perform(get("/api/user/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.email").value("juan@empresa.com"));
	}

	@Test
	void getUserByIdWithUnknownIdReturnsNotFound() throws Exception {
		mockMvc.perform(get("/api/user/999"))
				.andExpect(status().isNotFound());
	}

	@Test
	void updateUserChangesDataAndRole() throws Exception {
		Long id = crearUsuario("Juan", "Perez", "juan@empresa.com", adminRoleId);

		mockMvc.perform(put("/api/user/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombres\":\"Juan Carlos\",\"apellidos\":\"Perez\",\"email\":\"juan@empresa.com\","
								+ "\"roleId\":" + empleadoRoleId + ",\"estado\":true}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombres").value("Juan Carlos"))
				.andExpect(jsonPath("$.rolNombre").value("EMPLEADO"))
				.andExpect(jsonPath("$.estado").value(true));
	}

	@Test
	void updateUserWithUnknownRoleReturnsNotFound() throws Exception {
		Long id = crearUsuario("Juan", "Perez", "juan@empresa.com", adminRoleId);

		mockMvc.perform(put("/api/user/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombres\":\"Juan\",\"apellidos\":\"Perez\",\"email\":\"juan@empresa.com\",\"roleId\":999}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void createUserWithDuplicateEmailReturnsConflict() throws Exception {
		crearUsuario("Juan", "Perez", "juan@empresa.com", adminRoleId);

		mockMvc.perform(post("/api/user")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombres\":\"Otro\",\"apellidos\":\"Perez\",\"email\":\"juan@empresa.com\",\"password\":\"secret123\",\"roleId\":" + adminRoleId + "}"))
				.andExpect(status().isConflict());
	}

	@Test
	void deactivateUserMarksEstadoFalse() throws Exception {
		Long id = crearUsuario("Juan", "Perez", "juan@empresa.com", empleadoRoleId);

		mockMvc.perform(delete("/api/user/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value(false));

		Boolean estado = userRepository.findById(id).orElseThrow().getEstado();
		org.junit.jupiter.api.Assertions.assertFalse(estado);
	}

	@Test
	@WithMockUser(username = "juan@empresa.com", roles = "ADMIN")
	void adminCannotDeactivateOwnAccount() throws Exception {
		Long id = crearUsuario("Juan", "Perez", "juan@empresa.com", adminRoleId);

		mockMvc.perform(delete("/api/user/" + id))
				.andExpect(status().isBadRequest());
	}

	@Test
	void cannotDeactivateLastActiveAdmin() throws Exception {
		Long id = crearUsuario("Maria", "Lopez", "maria@empresa.com", adminRoleId);

		mockMvc.perform(delete("/api/user/" + id))
				.andExpect(status().isBadRequest());
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

	@Test
	@WithMockUser(roles = "EMPLEADO")
	void getUsersWithoutAdminRoleReturnsForbidden() throws Exception {
		mockMvc.perform(get("/api/user"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "EMPLEADO")
	void deleteUserWithoutAdminRoleReturnsForbidden() throws Exception {
		mockMvc.perform(delete("/api/user/1"))
				.andExpect(status().isForbidden());
	}

	private Long crearUsuario(String nombres, String apellidos, String email, Long roleId) throws Exception {
		mockMvc.perform(post("/api/user")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombres\":\"" + nombres + "\",\"apellidos\":\"" + apellidos
								+ "\",\"email\":\"" + email + "\",\"password\":\"secret123\",\"roleId\":" + roleId + "}"))
				.andExpect(status().isCreated());
		return userRepository.findByEmailIgnoreCase(email).orElseThrow().getId();
	}

}
