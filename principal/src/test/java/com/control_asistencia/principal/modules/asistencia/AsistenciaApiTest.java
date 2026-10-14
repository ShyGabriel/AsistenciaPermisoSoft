package com.control_asistencia.principal.modules.asistencia;

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

import com.control_asistencia.principal.modules.asistencia.model.Asistencia;
import com.control_asistencia.principal.modules.asistencia.repository.AsistenciaRepository;
import com.control_asistencia.principal.modules.auth.model.Role;
import com.control_asistencia.principal.modules.auth.model.User;
import com.control_asistencia.principal.modules.auth.repository.RoleRepository;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AsistenciaApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AsistenciaRepository asistenciaRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private User empleado;

	@BeforeEach
	void setUp() {
		asistenciaRepository.deleteAll();
		userRepository.deleteAll();
		roleRepository.deleteAll();
		Role adminRole = roleRepository.save(new Role(null, "ADMIN"));
		Role empleadoRole = roleRepository.save(new Role(null, "EMPLEADO"));
		userRepository.save(new User(null, "Administrador", "Sistema", "admin@empresa.com",
				passwordEncoder.encode("Admin123!"), adminRole));
		empleado = userRepository.save(new User(null, "Juan", "Perez", "juan@empresa.com",
				passwordEncoder.encode("Empleado123!"), empleadoRole));
	}

	@Test
	@WithMockUser(username = "juan@empresa.com", roles = "EMPLEADO")
	void registrarTodasLasMarcasDejaLaAsistenciaCompleta() throws Exception {
		for (String marca : new String[] { "ENTRADA", "INICIO_REFRIGERIO", "FIN_REFRIGERIO", "SALIDA" }) {
			mockMvc.perform(post("/api/asistencia/marca")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"tipo\":\"" + marca + "\"}"))
					.andExpect(status().isOk());
		}

		mockMvc.perform(get("/api/asistencia/mi-resumen"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.completos").value(1))
				.andExpect(jsonPath("$.data.incompletos").value(0));
	}

	@Test
	@WithMockUser(username = "juan@empresa.com", roles = "EMPLEADO")
	void empleadoNoPuedeListarTodasLasAsistencias() throws Exception {
		mockMvc.perform(get("/api/asistencia"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin@empresa.com", roles = "ADMIN")
	void adminPuedeCorregirAsistencia() throws Exception {
		Asistencia asistencia = asistenciaRepository.save(new Asistencia(empleado, LocalDate.now()));

		mockMvc.perform(patch("/api/asistencia/" + asistencia.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"horaEntrada\":\"08:00:00\",\"estado\":\"AUSENTE\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.estado").value("AUSENTE"));
	}

}
