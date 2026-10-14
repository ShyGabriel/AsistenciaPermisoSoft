package com.control_asistencia.principal.modules.dashboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.control_asistencia.principal.modules.asistencia.model.Asistencia;
import com.control_asistencia.principal.modules.asistencia.model.EstadoAsistencia;
import com.control_asistencia.principal.modules.asistencia.repository.AsistenciaRepository;
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
class DashboardApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AsistenciaRepository asistenciaRepository;

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

	private User empleado;

	@BeforeEach
	void setUp() {
		asistenciaRepository.deleteAll();
		permisoRepository.deleteAll();
		tipoPermisoRepository.deleteAll();
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
	@WithMockUser(username = "admin@empresa.com", roles = "ADMIN")
	void adminObtieneResumenDelDashboard() throws Exception {
		Asistencia asistencia = new Asistencia(empleado, LocalDate.now());
		asistencia.setHoraEntrada(LocalTime.of(8, 0));
		asistencia.setHoraIniRefrigerio(LocalTime.of(13, 0));
		asistencia.setHoraFinRefrigerio(LocalTime.of(14, 0));
		asistencia.setHoraSalida(LocalTime.of(17, 0));
		asistencia.setEstado(EstadoAsistencia.COMPLETA);
		asistenciaRepository.save(asistencia);

		TipoPermiso tipo = tipoPermisoRepository.save(new TipoPermiso(null, "Vacaciones", "Descanso anual", 15));
		permisoRepository.save(new Permiso(empleado, tipo, LocalDate.now(), LocalDate.now().plusDays(2), "Viaje"));

		mockMvc.perform(get("/api/dashboard/resumen"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.totalEmpleados").value(1))
				.andExpect(jsonPath("$.data.totalAsistencias").value(1))
				.andExpect(jsonPath("$.data.asistenciasCompletas").value(1))
				.andExpect(jsonPath("$.data.asistenciasIncompletas").value(0))
				.andExpect(jsonPath("$.data.permisosPendientes").value(1))
				.andExpect(jsonPath("$.data.permisosAprobados").value(0))
				.andExpect(jsonPath("$.data.asistenciasPorEmpleado.length()").value(1))
				.andExpect(jsonPath("$.data.asistenciasPorEmpleado[0].total").value(1))
				.andExpect(jsonPath("$.data.permisosPorEstado.length()").value(1))
				.andExpect(jsonPath("$.data.permisosPorTipo.length()").value(1))
				.andExpect(jsonPath("$.data.permisosPorTipo[0].tipo").value("Vacaciones"));
	}

	@Test
	@WithMockUser(username = "juan@empresa.com", roles = "EMPLEADO")
	void empleadoNoPuedeVerResumenDelDashboard() throws Exception {
		mockMvc.perform(get("/api/dashboard/resumen"))
				.andExpect(status().isForbidden());
	}

}
