package com.control_asistencia.principal.modules.permiso.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.control_asistencia.principal.modules.auth.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Permiso")
public class Permiso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_permiso")
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_usuario", nullable = false)
	private User usuario;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_tipo", nullable = false)
	private TipoPermiso tipo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_admin_responde")
	private User adminResponde;

	@Column(name = "fecha_inicio", nullable = false)
	private LocalDate fechaInicio;

	@Column(name = "fecha_fin", nullable = false)
	private LocalDate fechaFin;

	@Column(name = "motivo")
	private String motivo;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false)
	private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

	@Column(name = "fecha_creacion", insertable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	@Column(name = "fecha_respuesta")
	private LocalDateTime fechaRespuesta;

	public Permiso() {
	}

	public Permiso(User usuario, TipoPermiso tipo, LocalDate fechaInicio, LocalDate fechaFin, String motivo) {
		this.usuario = usuario;
		this.tipo = tipo;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.motivo = motivo;
		this.estado = EstadoSolicitud.PENDIENTE;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	public TipoPermiso getTipo() {
		return tipo;
	}

	public void setTipo(TipoPermiso tipo) {
		this.tipo = tipo;
	}

	public User getAdminResponde() {
		return adminResponde;
	}

	public void setAdminResponde(User adminResponde) {
		this.adminResponde = adminResponde;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public EstadoSolicitud getEstado() {
		return estado;
	}

	public void setEstado(EstadoSolicitud estado) {
		this.estado = estado;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public LocalDateTime getFechaRespuesta() {
		return fechaRespuesta;
	}

	public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
		this.fechaRespuesta = fechaRespuesta;
	}

}
