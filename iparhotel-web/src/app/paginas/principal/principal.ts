import { Component, OnInit, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HabitacionFila } from '../../componentes/habitacion-fila/habitacion-fila';
import { Habitacion } from '../../modelos/habitacion';
import { HabitacionesService } from '../../servicios/habitaciones';

/** Pagina principal: listado publico de habitaciones disponibles. */
@Component({
  imports: [HabitacionFila],
  selector: 'app-principal',
  styleUrl: './principal.css',
  templateUrl: './principal.html',
})
export class Principal implements OnInit {
  private readonly habitacionesService = inject(HabitacionesService);
  private readonly router = inject(Router);

  protected readonly habitaciones = signal<Habitacion[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);

  /** Ciclo de vida: al crearse el componente se piden las habitaciones a la API. */
  ngOnInit(): void {
    this.habitacionesService.disponibles().subscribe({
      next: (lista) => {
        this.habitaciones.set(lista);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se puede conectar con el servidor. Comprueba que la API está en marcha.');
        this.cargando.set(false);
      },
    });
  }

  /** Lo llama el output "reservar" del componente hijo. Si no hay sesion, el guard lleva al login. */
  protected irAReservar(habitacion: Habitacion): void {
    this.router.navigate(['/reservar', habitacion.id]);
  }
}
