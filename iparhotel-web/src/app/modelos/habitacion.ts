/** Lo que devuelve GET /api/v2/habitaciones */
export interface Habitacion {
  id: number;
  numero: string;
  tipo: string; // Individual, Doble o Suite
  capacidad: number;
  precioNoche: number;
}
