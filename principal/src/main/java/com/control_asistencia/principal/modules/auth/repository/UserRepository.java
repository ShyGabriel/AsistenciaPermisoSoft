package com.control_asistencia.principal.modules.auth.repository;

import java.util.List;
import java.util.Optional;

import com.control_asistencia.principal.modules.auth.model.User;

public interface UserRepository {

	List<User> findAll();

	Optional<User> findById(Long id);

	User save(User user);

	void reset();

}