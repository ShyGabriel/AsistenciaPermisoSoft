package com.control_asistencia.principal.modules.horario;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalTime;

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
import com.control_asistencia.principal.modules.horario.model.DiaSemana;
import com.control_asistencia.principal.modules.horario.model.HorarioEmpleado;
import com.control_asistencia.principal.modules.horario.model.Turno;
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
	@WithMockUser(username = "admin@empresa.com", roles = "ADMIN")
	void adminCrudTurno() throws Exception {
		Turno turno = turnoRepository.save(new Turno(null, "Manana", LocalTime.of(8, 0), LocalTime.of(17, 0),
				LocalTime.of(13, 0), LocalTime.of(14, 0)));

		mockMvc.perform(get("/api/horario/turnos/" + turno.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.nombre").value("Manana"));

		mockMvc.perform(put("/api/horario/turnos/" + turno.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombre\":\"Tarde\",\"horaEntrada\":\"14:00:00\",\"horaSalida\":\"22:00:00\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.nombre").value("Tarde"))
				.andExpect(jsonPath("$.data.horaEntrada").value("14:00:00"));

		mockMvc.perform(delete("/api/horario/turnos/" + turno.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.nombre").value("Tarde"));

		org.junit.jupiter.api.Assertions.assertTrue(turnoRepository.findById(turno.getId()).isEmpty());
	}

	@Test
	@WithMockUser(username = "admin@empresa.com", roles = "ADMIN")
	void deleteTurnoAssignedReturnsConflict() throws Exception {
		Turno turno = turnoRepository.save(new Turno(null, "Manana", LocalTime.of(8, 0), LocalTime.of(17, 0),
				null, null));
		User empleado = userRepository.findById(empleadoId).orElseThrow();
		horarioRepository.save(new HorarioEmpleado(empleado, turno, DiaSemana.LUNES));

		mockMvc.perform(delete("/api/horario/turnos/" + turno.getId()))
				.andExpect(status().isConflict());
	}

	@Test
	@WithMockUser(username = "admin@empresa.com", roles = "ADMIN")
	void adminCrudAsignacion() throws Exception {
		Turno turno = turnoRepository.save(new Turno(null, "Manana", LocalTime.of(8, 0), LocalTime.of(17, 0),
				null, null));

		mockMvc.perform(post("/api/horario/asignar")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"usuarioId\":" + empleadoId + ",\"turnoId\":" + turno.getId()
								+ ",\"diaSemana\":\"LUNES\"}"))
				.andExpect(status().isOk());

		Long asignacionId = horarioRepository.findAllByOrderByIdAsc().get(0).getId();

		mockMvc.perform(get("/api/horario/asignaciones/" + asignacionId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.diaSemana").value("LUNES"));

		mockMvc.perform(put("/api/horario/asignaciones/" + asignacionId)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"usuarioId\":" + empleadoId + ",\"turnoId\":" + turno.getId()
								+ ",\"diaSemana\":\"MARTES\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.diaSemana").value("MARTES"));

		mockMvc.perform(delete("/api/horario/asignaciones/" + asignacionId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.diaSemana").value("MARTES"));

		org.junit.jupiter.api.Assertions.assertTrue(horarioRepository.findById(asignacionId).isEmpty());
	}

	@Test
	@WithMockUser(username = "admin@empresa.com", roles = "ADMIN")
	void crearTurnoConHorarioInvalidoReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/horario/turnos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombre\":\"Invalido\",\"horaEntrada\":\"17:00:00\",\"horaSalida\":\"08:00:00\"}"))
				.andExpect(status().isBadRequest());
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
