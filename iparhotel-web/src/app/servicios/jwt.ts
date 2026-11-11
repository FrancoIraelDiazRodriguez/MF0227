import { Service, computed, signal } from '@angular/core';
import { PayloadToken } from '../modelos/sesion';

const CLAVE = 'iparhotel.token';

/**
 * Guarda el token y expone el estado de la sesion con signals.
 * La cabecera, el guard y los servicios leen la sesion de aqui.
 */
@Service()
export class JwtService {
  // Se guarda tambien en sessionStorage para no perder la sesion al recargar la pagina
  private readonly token = signal<string | null>(sessionStorage.getItem(CLAVE));

  private readonly payload = computed(() => decodificar(this.token()));

  /** Hay token y no ha caducado */
  readonly autenticado = computed(() => {
    const p = this.payload();
    return p !== null && p.exp * 1000 > Date.now();
  });

  /** Nombre del cliente (claim "nombre" del token) */
  readonly nombre = computed(() => (this.autenticado() ? this.payload()!.nombre : null));

  guardar(token: string): void {
    sessionStorage.setItem(CLAVE, token);
    this.token.set(token);
  }

  cerrarSesion(): void {
    sessionStorage.removeItem(CLAVE);
    this.token.set(null);
  }

  getToken(): string | null {
    return this.token();
  }
}

/** Un JWT son tres partes en base64url separadas por puntos: cabecera.payload.firma */
function decodificar(token: string | null): PayloadToken | null {
  if (!token) {
    return null;
  }
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    const bytes = Uint8Array.from(atob(base64), (c) => c.charCodeAt(0));
    // TextDecoder para que los nombres con tilde (Ana López) se lean bien
    return JSON.parse(new TextDecoder().decode(bytes)) as PayloadToken;
  } catch {
    return null;
  }
}
