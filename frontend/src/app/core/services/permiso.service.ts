import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import {
  EstadoSolicitud,
  Permiso,
  SolicitudPermiso,
  TipoPermiso,
} from '../models/permiso.model';

@Injectable({ providedIn: 'root' })
export class PermisoService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/permiso`;

  listarTipos(): Observable<TipoPermiso[]> {
    return this.http
      .get<ApiResponse<TipoPermiso[]>>(`${this.baseUrl}/tipos`)
      .pipe(map((response) => response.data));
  }

  solicitar(solicitud: SolicitudPermiso): Observable<Permiso> {
    return this.http
      .post<ApiResponse<Permiso>>(this.baseUrl, solicitud)
      .pipe(map((response) => response.data));
  }

  misPermisos(): Observable<Permiso[]> {
    return this.http
      .get<ApiResponse<Permiso[]>>(`${this.baseUrl}/mis-permisos`)
      .pipe(map((response) => response.data));
  }

  listar(estado?: EstadoSolicitud): Observable<Permiso[]> {
    let params = new HttpParams();
    if (estado) {
      params = params.set('estado', estado);
    }
    return this.http
      .get<ApiResponse<Permiso[]>>(this.baseUrl, { params })
      .pipe(map((response) => response.data));
  }

  resolver(id: number, estado: EstadoSolicitud): Observable<Permiso> {
    return this.http
      .patch<ApiResponse<Permiso>>(`${this.baseUrl}/${id}/resolver`, { estado })
      .pipe(map((response) => response.data));
  }
}
