package com.control_asistencia.principal.modules.auth.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.control_asistencia.principal.modules.auth.model.User;

@Repository
public class InMemoryUserRepository implements UserRepository {

	private final Map<Long, User> store = new ConcurrentHashMap<>();
	private final AtomicLong ids = new AtomicLong(1);

	@Override
	public List<User> findAll() {
		return store.values().stream()
				.sorted(Comparator.comparing(User::getId))
				.toList();
	}

	@Override
	public Optional<User> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}

	@Override
	public User save(User user) {
		if (user.getId() == null) {
			user.setId(ids.getAndIncrement());
		}
		store.put(user.getId(), user);
		return user;
	}

	@Override
	public void reset() {
		store.clear();
		ids.set(1);
	}

}