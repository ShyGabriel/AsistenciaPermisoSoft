package com.control_asistencia.principal.modules.auth.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey key;
	private final long expiration;

	public JwtService(@Value("${security.jwt.secret}") String secret,
			@Value("${security.jwt.expiration}") long expiration) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expiration = expiration;
	}

	public String generateToken(String email, String rol) {
		Date now = new Date();
		return Jwts.builder()
				.subject(email)
				.claim("rol", rol)
				.issuedAt(now)
				.expiration(new Date(now.getTime() + expiration))
				.signWith(key)
				.compact();
	}

	public String extractEmail(String token) {
		return parse(token).getSubject();
	}

	public String extractRol(String token) {
		return parse(token).get("rol", String.class);
	}

	public boolean isValid(String token) {
		try {
			parse(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	private Claims parse(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

}
