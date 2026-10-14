export type EstadoSolicitud = 'PENDIENTE' | 'APROBADO' | 'RECHAZADO';

export interface TipoPermiso {
  id: number;
  nombre: string;
  descripcion: string | null;
  diasMaximos: number | null;
}

export interface Permiso {
  id: number;
  usuarioId: number | null;
  usuario: string | null;
  tipoId: number | null;
  tipo: string | null;
  fechaInicio: string;
  fechaFin: string;
  motivo: string | null;
  estado: EstadoSolicitud;
  fechaCreacion: string | null;
  fechaRespuesta: string | null;
  adminResponde: string | null;
}

export interface SolicitudPermiso {
  idTipo: number;
  fechaInicio: string;
  fechaFin: string;
  motivo?: string;
}
