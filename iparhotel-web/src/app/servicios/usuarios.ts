import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { RespuestaLogin } from '../modelos/sesion';
import { JwtService } from './jwt';

@Service()
export class UsuariosService {
  private readonly http = inject(HttpClient);
  private readonly jwt = inject(JwtService);

  /** POST /usuarios/login. Si va bien guarda el token; si no, la API responde 401. */
  login(email: string, password: string): Observable<RespuestaLogin> {
    return this.http
      .post<RespuestaLogin>(`${environment.apiUrl}/usuarios/login`, { email, password })
      .pipe(tap((respuesta) => this.jwt.guardar(respuesta.accessToken)));
  }

  cerrarSesion(): void {
    this.jwt.cerrarSesion();
  }
}
