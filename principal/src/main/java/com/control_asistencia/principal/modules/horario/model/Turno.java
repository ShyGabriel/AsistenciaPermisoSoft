package com.control_asistencia.principal.modules.horario.model;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Turno")
public class Turno {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_turno")
	private Long id;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	@Column(name = "hora_entrada", nullable = false)
	private LocalTime horaEntrada;

	@Column(name = "hora_salida", nullable = false)
	private LocalTime horaSalida;

	@Column(name = "hora_ini_refrigerio")
	private LocalTime horaIniRefrigerio;

	@Column(name = "hora_fin_refrigerio")
	private LocalTime horaFinRefrigerio;

	public Turno() {
	}

	public Turno(Long id, String nombre, LocalTime horaEntrada, LocalTime horaSalida,
			LocalTime horaIniRefrigerio, LocalTime horaFinRefrigerio) {
		this.id = id;
		this.nombre = nombre;
		this.horaEntrada = horaEntrada;
		this.horaSalida = horaSalida;
		this.horaIniRefrigerio = horaIniRefrigerio;
		this.horaFinRefrigerio = horaFinRefrigerio;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public LocalTime getHoraEntrada() {
		return horaEntrada;
	}

	public void setHoraEntrada(LocalTime horaEntrada) {
		this.horaEntrada = horaEntrada;
	}

	public LocalTime getHoraSalida() {
		return horaSalida;
	}

	public void setHoraSalida(LocalTime horaSalida) {
		this.horaSalida = horaSalida;
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

}
