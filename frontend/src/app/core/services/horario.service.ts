import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { AsignarHorarioRequest, Horario, Turno, TurnoRequest } from '../models/horario.model';

@Injectable({ providedIn: 'root' })
export class HorarioService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/horario`;

  listarTurnos(): Observable<Turno[]> {
    return this.http
      .get<ApiResponse<Turno[]>>(`${this.baseUrl}/turnos`)
      .pipe(map((response) => response.data));
  }

  crearTurno(turno: TurnoRequest): Observable<Turno> {
    return this.http
      .post<ApiResponse<Turno>>(`${this.baseUrl}/turnos`, turno)
      .pipe(map((response) => response.data));
  }

  listarAsignaciones(): Observable<Horario[]> {
    return this.http
      .get<ApiResponse<Horario[]>>(`${this.baseUrl}/asignaciones`)
      .pipe(map((response) => response.data));
  }

  asignar(asignacion: AsignarHorarioRequest): Observable<Horario> {
    return this.http
      .post<ApiResponse<Horario>>(`${this.baseUrl}/asignar`, asignacion)
      .pipe(map((response) => response.data));
  }
}
