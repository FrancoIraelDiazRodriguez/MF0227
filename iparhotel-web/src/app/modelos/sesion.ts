/** Respuesta de POST /api/v2/usuarios/login */
export interface RespuestaLogin {
  accessToken: string;
  tokenType: string;
}

/** Lo que va dentro del JWT (la parte del medio) */
export interface PayloadToken {
  sub: string; // email
  nombre: string;
  rol: 'CLIENTE' | 'ADMINISTRADOR';
  exp: number; // segundos desde 1970
}

/** Error que devuelve la API (ProblemDetail) */
export interface Problema {
  status: number;
  detail?: string;
  errores?: Record<string, string>;
}
