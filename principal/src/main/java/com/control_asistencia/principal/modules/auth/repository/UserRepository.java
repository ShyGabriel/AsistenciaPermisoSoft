package com.control_asistencia.principal.modules.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.control_asistencia.principal.modules.auth.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmailIgnoreCase(String email);

	boolean existsByRolId(Long roleId);

	@Query("SELECT COUNT(u) FROM User u WHERE u.rol.name = :rol")
	long contarPorRol(@Param("rol") String rol);

	@Query("SELECT COUNT(u) FROM User u WHERE u.rol.name = :rol AND u.estado = :estado")
	long contarPorRolYEstado(@Param("rol") String rol, @Param("estado") Boolean estado);

}
