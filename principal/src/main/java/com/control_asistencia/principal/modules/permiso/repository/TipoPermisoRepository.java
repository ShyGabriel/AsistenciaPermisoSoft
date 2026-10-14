package com.control_asistencia.principal.modules.permiso.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.control_asistencia.principal.modules.permiso.model.TipoPermiso;

public interface TipoPermisoRepository extends JpaRepository<TipoPermiso, Long> {

}
