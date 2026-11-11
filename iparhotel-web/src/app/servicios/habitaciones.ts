import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Habitacion } from '../modelos/habitacion';

@Service()
export class HabitacionesService {
  private readonly http = inject(HttpClient);

  /** GET /habitaciones (publico, sin token) */
  disponibles(): Observable<Habitacion[]> {
    return this.http.get<Habitacion[]>(`${environment.apiUrl}/habitaciones`);
  }
}
