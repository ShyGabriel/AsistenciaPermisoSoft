package com.control_asistencia.principal.config;

import org.springframework.context.annotation.Configuration;

/**
 * Punto central para la configuración de JPA. La aplicación usa el esquema
 * definido en {@code db/gestion_empleados.sql}, por lo que no se fuerza la
 * generación automática de tablas (spring.jpa.hibernate.ddl-auto=none).
 */
@Configuration
public class JpaConfig {

}
