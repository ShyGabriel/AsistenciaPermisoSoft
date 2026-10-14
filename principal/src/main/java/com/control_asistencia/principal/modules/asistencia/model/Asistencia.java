package com.control_asistencia.principal.modules.asistencia.model;

import java.time.LocalDate;
import java.time.LocalTime;

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
@Table(name = "Asistencia")
public class Asistencia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_asistencia")
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_usuario", nullable = false)
	private User usuario;

	@Column(name = "fecha", nullable = false)
	private LocalDate fecha;

	@Column(name = "hora_entrada")
	private LocalTime horaEntrada;

	@Column(name = "hora_ini_refrigerio")
	private LocalTime horaIniRefrigerio;

	@Column(name = "hora_fin_refrigerio")
	private LocalTime horaFinRefrigerio;

	@Column(name = "hora_salida")
	private LocalTime horaSalida;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false)
	private EstadoAsistencia estado = EstadoAsistencia.INCOMPLETA;

	public Asistencia() {
	}

	public Asistencia(User usuario, LocalDate fecha) {
		this.usuario = usuario;
		this.fecha = fecha;
		this.estado = EstadoAsistencia.INCOMPLETA;
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

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public LocalTime getHoraEntrada() {
		return horaEntrada;
	}

	public void setHoraEntrada(LocalTime horaEntrada) {
		this.horaEntrada = horaEntrada;
	}

	public LocalTime getHoraIniRefrigerio() {
		return horaIniRefrigerio;
	}

	public void setHoraIniRefrigerio(LocalTime horaIniRefrigerio) {
		this.horaIniRefrigerio = horaIniRefrigerio;
	}

	public LocalTime getHoraFinRefrigerio() {
		return horaFinRefrigerio;
	}

	public void setHoraFinRefrigerio(LocalTime horaFinRefrigerio) {
		this.horaFinRefrigerio = horaFinRefrigerio;
	}

	public LocalTime getHoraSalida() {
		return horaSalida;
	}

	public void setHoraSalida(LocalTime horaSalida) {
		this.horaSalida = horaSalida;
	}

	public EstadoAsistencia getEstado() {
		return estado;
	}

	public void setEstado(EstadoAsistencia estado) {
		this.estado = estado;
	}

	public boolean estaCompleta() {
		return horaEntrada != null && horaIniRefrigerio != null
				&& horaFinRefrigerio != null && horaSalida != null;
	}

}
