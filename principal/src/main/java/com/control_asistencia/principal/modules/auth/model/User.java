package com.control_asistencia.principal.modules.auth.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Usuario")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_usuario")
	private Long id;

	@Column(name = "nombres", nullable = false)
	private String nombres;

	@Column(name = "apellidos", nullable = false)
	private String apellidos;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password_hash", nullable = false)
	@JsonIgnore
	private String passwordHash;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_rol", nullable = false)
	private Role rol;

	@Column(name = "estado", nullable = false)
	private Boolean estado = Boolean.TRUE;

	@Column(name = "fecha_creacion", insertable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	public User() {
	}

	public User(Long id, String nombres, String apellidos, String email, String passwordHash, Role rol) {
		this.id = id;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.email = email;
		this.passwordHash = passwordHash;
		this.rol = rol;
	}

	public String getNombreCompleto() {
		return (nombres + " " + apellidos).trim();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Role getRol() {
		return rol;
	}

	public void setRol(Role rol) {
		this.rol = rol;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

}
