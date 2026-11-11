import { Component, input, output } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { Habitacion } from '../../modelos/habitacion';

/**
 * Componente hijo: pinta una habitacion del listado.
 * Recibe la habitacion con input() y avisa al padre con output() cuando se pulsa "Reservar".
 * No sabe a donde se navega: eso lo decide la pagina principal.
 */
@Component({
  imports: [CurrencyPipe],
  selector: 'app-habitacion-fila',
  styleUrl: './habitacion-fila.css',
  templateUrl: './habitacion-fila.html',
})
export class HabitacionFila {
  readonly habitacion = input.required<Habitacion>();
  readonly reservar = output<Habitacion>();
}
