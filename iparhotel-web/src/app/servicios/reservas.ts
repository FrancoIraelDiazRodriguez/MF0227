import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Service, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { NuevaReserva, Reserva } from '../modelos/reserva';
import { JwtService } from './jwt';

@Service()
export class ReservasService {
  private readonly http = inject(HttpClient);
  private readonly jwt = inject(JwtService);

  /**
   * POST /reservas con la cabecera Authorization: Bearer <token>.
   * 201 con la reserva; 400 datos no validos; 404 no existe; 409 no disponible; 401 sin token valido.
   */
  reservar(reserva: NuevaReserva): Observable<Reserva> {
    const cabeceras = new HttpHeaders({ Authorization: `Bearer ${this.jwt.getToken()}` });
    return this.http.post<Reserva>(`${environment.apiUrl}/reservas`, reserva, { headers: cabeceras });
  }
}
