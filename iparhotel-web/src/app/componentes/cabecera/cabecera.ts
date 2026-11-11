import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { JwtService } from '../../servicios/jwt';
import { UsuariosService } from '../../servicios/usuarios';

/** Cambia sola segun la sesion porque lee los signals de JwtService. */
@Component({
  imports: [RouterLink],
  selector: 'app-cabecera',
  styleUrl: './cabecera.css',
  templateUrl: './cabecera.html',
})
export class Cabecera {
  protected readonly jwt = inject(JwtService);
  private readonly usuarios = inject(UsuariosService);
  private readonly router = inject(Router);

  protected cerrarSesion(): void {
    this.usuarios.cerrarSesion();
    this.router.navigate(['/']);
  }
}
