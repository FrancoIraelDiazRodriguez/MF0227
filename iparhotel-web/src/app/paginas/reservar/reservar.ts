import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Habitacion } from '../../modelos/habitacion';
import { Reserva } from '../../modelos/reserva';
import { Problema } from '../../modelos/sesion';
import { HabitacionesService } from '../../servicios/habitaciones';
import { JwtService } from '../../servicios/jwt';
import { ReservasService } from '../../servicios/reservas';

/** /reservar/:id — formulario de fecha y noches. El importe lo calcula la API. */
@Component({
  imports: [ReactiveFormsModule, CurrencyPipe, DatePipe, RouterLink],
  selector: 'app-reservar',
  styleUrl: './reservar.css',
  templateUrl: './reservar.html',
})
export class Reservar implements OnInit {
  private readonly habitacionesService = inject(HabitacionesService);
  private readonly reservasService = inject(ReservasService);
  private readonly jwt = inject(JwtService);
  private readonly router = inject(Router);

  /** El id se lee de la URL */
  private readonly id = Number(inject(ActivatedRoute).snapshot.paramMap.get('id'));

  protected readonly habitacion = signal<Habitacion | null>(null);
  protected readonly reserva = signal<Reserva | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly enviando = signal(false);

  protected readonly formulario = inject(FormBuilder).nonNullable.group({
    fechaEntrada: ['', Validators.required],
    noches: [1, [Validators.required, Validators.min(1), Validators.max(14)]],
  });

  /** Ciclo de vida: al entrar se busca la habitacion entre las disponibles para enseñar sus datos. */
  ngOnInit(): void {
    this.habitacionesService.disponibles().subscribe({
      next: (lista) => {
        const h = lista.find((x) => x.id === this.id);
        if (h) {
          this.habitacion.set(h);
        } else {
          this.error.set('Esta habitación no existe o ya no está disponible.');
        }
      },
      error: () => this.error.set('No se puede conectar con el servidor.'),
    });
  }

  protected enviar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.error.set(null);

    const { fechaEntrada, noches } = this.formulario.getRawValue();
    this.reservasService.reservar({ idHabitacion: this.id, fechaEntrada, noches }).subscribe({
      next: (reserva) => {
        this.reserva.set(reserva); // se muestra el importe calculado por la API
        this.enviando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.enviando.set(false);
        if (e.status === 401) {
          // Token caducado o no valido: se cierra la sesion y se vuelve a pedir
          this.jwt.cerrarSesion();
          this.router.navigate(['/login']);
          return;
        }
        this.error.set(mensajeDeError(e));
      },
    });
  }

  protected invalido(campo: 'fechaEntrada' | 'noches'): boolean {
    const control = this.formulario.controls[campo];
    return control.invalid && control.touched;
  }
}

/** Saca el texto del ProblemDetail de la API: 400 (con los errores de cada campo), 404 o 409. */
function mensajeDeError(e: HttpErrorResponse): string {
  const problema = e.error as Problema | null;
  if (problema?.errores) {
    return Object.values(problema.errores).join('. ');
  }
  return problema?.detail ?? 'No se ha podido hacer la reserva.';
}
