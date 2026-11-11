/** Cuerpo de POST /api/v2/reservas. No lleva importe ni usuario: los pone la API. */
export interface NuevaReserva {
  idHabitacion: number;
  fechaEntrada: string; // AAAA-MM-DD
  noches: number;
}

/** Respuesta 201 de POST /api/v2/reservas */
export interface Reserva {
  id: number;
  numeroHabitacion: string;
  tipoHabitacion: string;
  nombreUsuario: string;
  emailUsuario: string;
  fechaEntrada: string;
  fechaSalida: string;
  noches: number;
  importe: number;
}
