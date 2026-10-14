package com.control_asistencia.principal.modules.dashboard.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.control_asistencia.principal.common.dto.ApiResponse;
import com.control_asistencia.principal.modules.dashboard.dto.DashboardResumenDTO;
import com.control_asistencia.principal.modules.dashboard.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

	private final DashboardService dashboardService;

	public DashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@GetMapping("/resumen")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<DashboardResumenDTO> resumen() {
		return ApiResponse.ok(dashboardService.resumen());
	}

}
