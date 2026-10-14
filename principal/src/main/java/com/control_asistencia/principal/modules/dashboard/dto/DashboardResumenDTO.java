package com.control_asistencia.principal.modules.dashboard.dto;

import java.util.List;

public record DashboardResumenDTO(
		long totalEmpleados,
		long totalAsistencias,
		long asistenciasCompletas,
		long asistenciasIncompletas,
		long ausentes,
		long permisosPendientes,
		long permisosAprobados,
		long permisosRechazados,
		List<AsistenciaPorEmpleadoDTO> asistenciasPorEmpleado,
		List<PermisoPorEstadoDTO> permisosPorEstado,
		List<PermisoPorTipoDTO> permisosPorTipo) {

}
