package com.control_asistencia.principal.modules.horario;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.control_asistencia.principal.modules.auth.model.Role;
import com.control_asistencia.principal.modules.auth.model.User;
import com.control_asistencia.principal.modules.auth.repository.RoleRepository;
import com.control_asistencia.principal.modules.auth.repository.UserRepository;
import com.control_asistencia.principal.modules.horario.repository.HorarioRepository;
import com.control_asistencia.principal.modules.horario.repository.TurnoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HorarioApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TurnoRepository turnoRepository;

	@Autowired
	private HorarioRepository horarioRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private Long empleadoId;

	@BeforeEach
	void setUp() {
		horarioRepository.deleteAll();
		turnoRepository.deleteAll();
		userRepository.deleteAll();
		roleRepository.deleteAll();
		Role adminRole = roleRepository.save(new Role(null, "ADMIN"));
		Role empleadoRole = roleRepository.save(new Role(null, "EMPLEADO"));
		userRepository.save(new User(null, "Administrador", "Sistema", "admin@empresa.com",
				passwordEncoder.encode("Admin123!"), adminRole));
		User empleado = userRepository.save(new User(null, "Juan", "Perez", "juan@empresa.com",
				passwordEncoder.encode("Empleado123!"), empleadoRole));
		empleadoId = empleado.getId();
	}

	@Test
	@WithMockUser(username = "admin@empresa.com", roles = "ADMIN")
	void adminCreaTurnoYAsignaHorario() throws Exception {
		mockMvc.perform(post("/api/horario/turnos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombre\":\"Manana\",\"horaEntrada\":\"08:00:00\",\"horaSalida\":\"17:00:00\","
								+ "\"horaIniRefrigerio\":\"13:00:00\",\"horaFinRefrigerio\":\"14:00:00\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.nombre").value("Manana"));

		Long turnoId = turnoRepository.findAll().get(0).getId();

		mockMvc.perform(post("/api/horario/asignar")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"usuarioId\":" + empleadoId + ",\"turnoId\":" + turnoId
								+ ",\"diaSemana\":\"LUNES\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.diaSemana").value("LUNES"));

		mockMvc.perform(get("/api/horario/asignaciones"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.length()").value(1));
	}

	@Test
	@WithMockUser(username = "juan@empresa.com", roles = "EMPLEADO")
	void empleadoNoPuedeCrearTurno() throws Exception {
		mockMvc.perform(post("/api/horario/turnos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombre\":\"Tarde\",\"horaEntrada\":\"14:00:00\",\"horaSalida\":\"22:00:00\"}"))
				.andExpect(status().isForbidden());
	}

}
