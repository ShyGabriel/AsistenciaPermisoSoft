import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import {
  Asistencia,
  CorreccionAsistencia,
  ResumenAsistencia,
  TipoMarca,
} from '../models/asistencia.model';

@Injectable({ providedIn: 'root' })
export class AsistenciaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/asistencia`;

  registrarMarca(tipo: TipoMarca): Observable<Asistencia> {
    return this.http
      .post<ApiResponse<Asistencia>>(`${this.baseUrl}/marca`, { tipo })
      .pipe(map((response) => response.data));
  }

  miResumen(desde?: string, hasta?: string): Observable<ResumenAsistencia> {
    let params = new HttpParams();
    if (desde) {
      params = params.set('desde', desde);
    }
    if (hasta) {
      params = params.set('hasta', hasta);
    }
    return this.http
      .get<ApiResponse<ResumenAsistencia>>(`${this.baseUrl}/mi-resumen`, { params })
      .pipe(map((response) => response.data));
  }

  listar(): Observable<Asistencia[]> {
    return this.http
      .get<ApiResponse<Asistencia[]>>(this.baseUrl)
      .pipe(map((response) => response.data));
  }

  listarPorUsuario(usuarioId: number): Observable<Asistencia[]> {
    return this.http
      .get<ApiResponse<Asistencia[]>>(`${this.baseUrl}/usuario/${usuarioId}`)
      .pipe(map((response) => response.data));
  }

  corregir(id: number, correccion: CorreccionAsistencia): Observable<Asistencia> {
    return this.http
      .patch<ApiResponse<Asistencia>>(`${this.baseUrl}/${id}`, correccion)
      .pipe(map((response) => response.data));
  }
}
