import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UsuariosService } from '../../servicios/usuarios';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {
  private readonly usuarios = inject(UsuariosService);
  private readonly router = inject(Router);

  protected readonly formulario = inject(FormBuilder).nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  protected readonly enviando = signal(false);
  protected readonly error = signal<string | null>(null);

  protected entrar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.error.set(null);

    const { email, password } = this.formulario.getRawValue();
    this.usuarios.login(email, password).subscribe({
      // El token ya lo ha guardado el servicio en JwtService: se vuelve a la principal
      next: () => this.router.navigate(['/']),
      error: (e) => {
        this.error.set(e.status === 401 ? 'Email o contraseña incorrectos.' : 'No se puede conectar con el servidor.');
        this.enviando.set(false);
      },
    });
  }

  protected invalido(campo: 'email' | 'password'): boolean {
    const control = this.formulario.controls[campo];
    return control.invalid && control.touched;
  }
}
