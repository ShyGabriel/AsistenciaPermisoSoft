import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { Asistencia } from '../../core/models/asistencia.model';
import { DiaSemana, Horario, Turno } from '../../core/models/horario.model';
import { EstadoSolicitud, Permiso } from '../../core/models/permiso.model';
import { User } from '../../core/models/user.model';
import { AsistenciaService } from '../../core/services/asistencia.service';
import { AuthService } from '../../core/services/auth.service';
import { HorarioService } from '../../core/services/horario.service';
import { PermisoService } from '../../core/services/permiso.service';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-dashboard-admin',
  imports: [ReactiveFormsModule],
  templateUrl: './dashboard-admin.html',
  styleUrl: './dashboard-admin.css',
})
export class DashboardAdmin implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly asistenciaService = inject(AsistenciaService);
  private readonly permisoService = inject(PermisoService);
  private readonly horarioService = inject(HorarioService);
  private readonly userService = inject(UserService);
  private readonly formBuilder = inject(FormBuilder);

  readonly dias: DiaSemana[] = [
    'LUNES',
    'MARTES',
    'MIERCOLES',
    'JUEVES',
    'VIERNES',
    'SABADO',
    'DOMINGO',
  ];

  readonly session = this.auth.session;
  readonly message = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  readonly asistencias = signal<Asistencia[]>([]);
  readonly permisos = signal<Permiso[]>([]);
  readonly turnos = signal<Turno[]>([]);
  readonly asignaciones = signal<Horario[]>([]);
  readonly usuarios = signal<User[]>([]);

  readonly turnoForm = this.formBuilder.nonNullable.group({
    nombre: ['', Validators.required],
    horaEntrada: ['', Validators.required],
    horaSalida: ['', Validators.required],
    horaIniRefrigerio: [''],
    horaFinRefrigerio: [''],
  });

  readonly asignarForm = this.formBuilder.nonNullable.group({
    usuarioId: [0, Validators.min(1)],
    turnoId: [0, Validators.min(1)],
    diaSemana: ['LUNES' as DiaSemana, Validators.required],
  });

  ngOnInit(): void {
    this.cargarTodo();
  }

  cargarTodo(): void {
    this.error.set(null);
    this.asistenciaService.listar().subscribe({
      next: (data) => this.asistencias.set(data),
      error: () => this.error.set('No se pudieron cargar las asistencias'),
    });
    this.cargarPermisos();
    this.horarioService.listarTurnos().subscribe({
      next: (data) => this.turnos.set(data),
      error: () => this.error.set('No se pudieron cargar los turnos'),
    });
    this.horarioService.listarAsignaciones().subscribe({
      next: (data) => this.asignaciones.set(data),
      error: () => this.error.set('No se pudieron cargar las asignaciones'),
    });
    this.userService.listar().subscribe({
      next: (data) => this.usuarios.set(data),
      error: () => this.error.set('No se pudieron cargar los empleados'),
    });
  }

  cargarPermisos(): void {
    this.permisoService.listar('PENDIENTE').subscribe({
      next: (data) => this.permisos.set(data),
      error: () => this.error.set('No se pudieron cargar los permisos'),
    });
  }

  resolver(permiso: Permiso, estado: EstadoSolicitud): void {
    this.message.set(null);
    this.error.set(null);
    this.permisoService.resolver(permiso.id, estado).subscribe({
      next: () => {
        this.message.set(`Permiso ${estado.toLowerCase()} correctamente`);
        this.cargarPermisos();
      },
      error: () => this.error.set('No se pudo resolver el permiso'),
    });
  }

  crearTurno(): void {
    if (this.turnoForm.invalid) {
      this.turnoForm.markAllAsTouched();
      return;
    }
    const value = this.turnoForm.getRawValue();
    this.message.set(null);
    this.error.set(null);
    this.horarioService
      .crearTurno({
        nombre: value.nombre,
        horaEntrada: value.horaEntrada,
        horaSalida: value.horaSalida,
        horaIniRefrigerio: value.horaIniRefrigerio || null,
        horaFinRefrigerio: value.horaFinRefrigerio || null,
      })
      .subscribe({
        next: () => {
          this.message.set('Turno creado correctamente');
          this.turnoForm.reset({ nombre: '', horaEntrada: '', horaSalida: '', horaIniRefrigerio: '', horaFinRefrigerio: '' });
          this.horarioService.listarTurnos().subscribe((data) => this.turnos.set(data));
        },
        error: () => this.error.set('No se pudo crear el turno'),
      });
  }

  asignar(): void {
    const value = this.asignarForm.getRawValue();
    if (value.usuarioId < 1 || value.turnoId < 1) {
      this.asignarForm.markAllAsTouched();
      return;
    }
    this.message.set(null);
    this.error.set(null);
    this.horarioService
      .asignar({
        usuarioId: Number(value.usuarioId),
        turnoId: Number(value.turnoId),
        diaSemana: value.diaSemana,
      })
      .subscribe({
        next: () => {
          this.message.set('Horario asignado correctamente');
          this.horarioService.listarAsignaciones().subscribe((data) => this.asignaciones.set(data));
        },
        error: () => this.error.set('No se pudo asignar el horario'),
      });
  }

  logout(): void {
    this.auth.logout();
  }
}
