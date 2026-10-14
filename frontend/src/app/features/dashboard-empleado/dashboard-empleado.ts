import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { ResumenAsistencia, TipoMarca } from '../../core/models/asistencia.model';
import { Permiso, TipoPermiso } from '../../core/models/permiso.model';
import { AsistenciaService } from '../../core/services/asistencia.service';
import { AuthService } from '../../core/services/auth.service';
import { PermisoService } from '../../core/services/permiso.service';

@Component({
  selector: 'app-dashboard-empleado',
  imports: [ReactiveFormsModule],
  templateUrl: './dashboard-empleado.html',
  styleUrl: './dashboard-empleado.css',
})
export class DashboardEmpleado implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly asistenciaService = inject(AsistenciaService);
  private readonly permisoService = inject(PermisoService);
  private readonly formBuilder = inject(FormBuilder);

  readonly marcas: { tipo: TipoMarca; label: string }[] = [
    { tipo: 'ENTRADA', label: 'Registrar entrada' },
    { tipo: 'INICIO_REFRIGERIO', label: 'Iniciar refrigerio' },
    { tipo: 'FIN_REFRIGERIO', label: 'Terminar refrigerio' },
    { tipo: 'SALIDA', label: 'Registrar salida' },
  ];

  readonly session = this.auth.session;
  readonly message = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  readonly resumen = signal<ResumenAsistencia | null>(null);
  readonly tipos = signal<TipoPermiso[]>([]);
  readonly misPermisos = signal<Permiso[]>([]);

  readonly permisoForm = this.formBuilder.nonNullable.group({
    idTipo: [0, Validators.min(1)],
    fechaInicio: ['', Validators.required],
    fechaFin: ['', Validators.required],
    motivo: [''],
  });

  ngOnInit(): void {
    this.cargarResumen();
    this.permisoService.listarTipos().subscribe({
      next: (data) => this.tipos.set(data),
      error: () => this.error.set('No se pudieron cargar los tipos de permiso'),
    });
    this.cargarPermisos();
  }

  cargarResumen(): void {
    this.asistenciaService.miResumen().subscribe({
      next: (data) => this.resumen.set(data),
      error: () => this.error.set('No se pudo cargar tu resumen'),
    });
  }

  cargarPermisos(): void {
    this.permisoService.misPermisos().subscribe({
      next: (data) => this.misPermisos.set(data),
      error: () => this.error.set('No se pudieron cargar tus permisos'),
    });
  }

  registrarMarca(tipo: TipoMarca): void {
    this.message.set(null);
    this.error.set(null);
    this.asistenciaService.registrarMarca(tipo).subscribe({
      next: (asistencia) => {
        this.message.set(`Marca registrada (${asistencia.estado})`);
        this.cargarResumen();
      },
      error: () => this.error.set('No se pudo registrar la marca'),
    });
  }

  solicitar(): void {
    const value = this.permisoForm.getRawValue();
    if (value.idTipo < 1) {
      this.permisoForm.markAllAsTouched();
      return;
    }
    this.message.set(null);
    this.error.set(null);
    this.permisoService
      .solicitar({
        idTipo: Number(value.idTipo),
        fechaInicio: value.fechaInicio,
        fechaFin: value.fechaFin,
        motivo: value.motivo,
      })
      .subscribe({
        next: () => {
          this.message.set('Solicitud enviada correctamente');
          this.permisoForm.reset({ idTipo: 0, fechaInicio: '', fechaFin: '', motivo: '' });
          this.cargarPermisos();
        },
        error: () => this.error.set('No se pudo enviar la solicitud'),
      });
  }

  logout(): void {
    this.auth.logout();
  }
}
