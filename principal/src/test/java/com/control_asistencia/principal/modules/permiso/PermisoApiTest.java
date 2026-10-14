package com.control_asistencia.principal.modules.permiso;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.control_asistencia.principal.modules.auth.model.Role;
import com.control_asistencia.principal.modules.auth.model.User;
import com.control_asistencia.principal.modules.auth.repository.RoleRepository;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;
import com.control_asistencia.principal.modules.permiso.model.Permiso;
import com.control_asistencia.principal.modules.permiso.model.TipoPermiso;
import com.control_asistencia.principal.modules.permiso.repository.PermisoRepository;
import com.control_asistencia.principal.modules.permiso.repository.TipoPermisoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PermisoApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PermisoRepository permisoRepository;

	@Autowired
	private TipoPermisoRepository tipoPermisoRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private TipoPermiso tipo;

	@BeforeEach
	void setUp() {
		permisoRepository.deleteAll();
		tipoPermisoRepository.deleteAll();
		userRepository.deleteAll();
		roleRepository.deleteAll();
		Role adminRole = roleRepository.save(new Role(null, "ADMIN"));
		Role empleadoRole = roleRepository.save(new Role(null, "EMPLEADO"));
		userRepository.save(new User(null, "Administrador", "Sistema", "admin@empresa.com",
				passwordEncoder.encode("Admin123!"), adminRole));
		userRepository.save(new User(null, "Juan", "Perez", "juan@empresa.com",
				passwordEncoder.encode("Empleado123!"), empleadoRole));
		tipo = tipoPermisoRepository.save(new TipoPermiso(null, "Vacaciones", "Vacaciones anuales", 15));
	}

	@Test
	@WithMockUser(username = "juan@empresa.com", roles = "EMPLEADO")
	void empleadoSolicitaPermiso() throws Exception {
		mockMvc.perform(post("/api/permiso")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"idTipo\":" + tipo.getId() + ",\"fechaInicio\":\"2026-10-20\","
								+ "\"fechaFin\":\"2026-10-22\",\"motivo\":\"Viaje\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.estado").value("PENDIENTE"))
				.andExpect(jsonPath("$.data.tipo").value("Vacaciones"));
	}

	@Test
	@WithMockUser(username = "admin@empresa.com", roles = "ADMIN")
	void adminResuelvePermiso() throws Exception {
		User usuario = userRepository.findByEmailIgnoreCase("juan@empresa.com").orElseThrow();
		Permiso permiso = permisoRepository.save(new Permiso(usuario, tipo, LocalDate.parse("2026-10-20"),
				LocalDate.parse("2026-10-22"), "Viaje"));

		mockMvc.perform(patch("/api/permiso/" + permiso.getId() + "/resolver")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"estado\":\"APROBADO\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.estado").value("APROBADO"));
	}

	@Test
	@WithMockUser(username = "juan@empresa.com", roles = "EMPLEADO")
	void empleadoNoPuedeResolverPermiso() throws Exception {
		mockMvc.perform(patch("/api/permiso/1/resolver")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"estado\":\"APROBADO\"}"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin@empresa.com", roles = "ADMIN")
	void listarPendientes() throws Exception {
		User usuario = userRepository.findByEmailIgnoreCase("juan@empresa.com").orElseThrow();
		permisoRepository.save(new Permiso(usuario, tipo, LocalDate.parse("2026-10-20"),
				LocalDate.parse("2026-10-22"), "Viaje"));

		mockMvc.perform(get("/api/permiso").param("estado", "PENDIENTE"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.length()").value(1));
	}

}
