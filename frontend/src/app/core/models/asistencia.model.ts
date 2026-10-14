export type TipoMarca = 'ENTRADA' | 'INICIO_REFRIGERIO' | 'FIN_REFRIGERIO' | 'SALIDA';

export type EstadoAsistencia = 'COMPLETA' | 'INCOMPLETA' | 'AUSENTE';

export interface Asistencia {
  id: number;
  usuarioId: number | null;
  usuario: string | null;
  fecha: string;
  horaEntrada: string | null;
  horaIniRefrigerio: string | null;
  horaFinRefrigerio: string | null;
  horaSalida: string | null;
  estado: EstadoAsistencia;
}

export interface ResumenAsistencia {
  desde: string;
  hasta: string;
  diasRegistrados: number;
  completos: number;
  incompletos: number;
  ausentes: number;
}

export interface CorreccionAsistencia {
  horaEntrada?: string | null;
  horaIniRefrigerio?: string | null;
  horaFinRefrigerio?: string | null;
  horaSalida?: string | null;
  estado?: EstadoAsistencia;
}
