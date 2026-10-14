export type DiaSemana =
  | 'LUNES'
  | 'MARTES'
  | 'MIERCOLES'
  | 'JUEVES'
  | 'VIERNES'
  | 'SABADO'
  | 'DOMINGO';

export interface Turno {
  id: number;
  nombre: string;
  horaEntrada: string;
  horaSalida: string;
  horaIniRefrigerio: string | null;
  horaFinRefrigerio: string | null;
}

export interface TurnoRequest {
  nombre: string;
  horaEntrada: string;
  horaSalida: string;
  horaIniRefrigerio?: string | null;
  horaFinRefrigerio?: string | null;
}

export interface Horario {
  id: number;
  usuarioId: number | null;
  usuario: string | null;
  turnoId: number | null;
  turno: string | null;
  diaSemana: DiaSemana;
}

export interface AsignarHorarioRequest {
  usuarioId: number;
  turnoId: number;
  diaSemana: DiaSemana;
}
