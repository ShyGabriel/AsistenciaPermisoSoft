export type Rol = 'ADMIN' | 'EMPLEADO';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  rol: Rol;
  nombre: string;
}

export interface Session extends AuthResponse {
  exp?: number;
}

export interface User {
  id: number;
  nombres: string;
  apellidos: string;
  email: string;
  rolId: number | null;
  rolNombre: string | null;
}
