package com.control_asistencia.principal.modules.auth.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.control_asistencia.principal.modules.auth.model.Role;

@Repository
public class InMemoryRoleRepository implements RoleRepository {

	private final Map<Long, Role> store = new ConcurrentHashMap<>();
	private final AtomicLong ids = new AtomicLong(1);

	@Override
	public List<Role> findAll() {
		return store.values().stream()
				.sorted(Comparator.comparing(Role::getId))
				.toList();
	}

	@Override
	public Optional<Role> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}

	@Override
	public Role save(Role role) {
		if (role.getId() == null) {
			role.setId(ids.getAndIncrement());
		}
		store.put(role.getId(), role);
		return role;
	}

	@Override
	public void reset() {
		store.clear();
		ids.set(1);
	}

}