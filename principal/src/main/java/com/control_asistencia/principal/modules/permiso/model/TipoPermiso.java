package com.control_asistencia.principal.modules.permiso.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Tipo_Permiso")
public class TipoPermiso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_tipo")
	private Long id;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	@Column(name = "descripcion")
	private String descripcion;

	@Column(name = "dias_maximos", nullable = false)
	private Integer diasMaximos = 0;

	public TipoPermiso() {
	}

	public TipoPermiso(Long id, String nombre, String descripcion, Integer diasMaximos) {
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.diasMaximos = diasMaximos;
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

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getDiasMaximos() {
		return diasMaximos;
	}

	public void setDiasMaximos(Integer diasMaximos) {
		this.diasMaximos = diasMaximos;
	}

}
