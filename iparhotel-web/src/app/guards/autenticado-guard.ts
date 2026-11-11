import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { JwtService } from '../servicios/jwt';

/** Solo deja entrar con sesion. Si no hay sesion, redirige a /login. */
export const autenticadoGuard: CanActivateFn = () => {
  if (inject(JwtService).autenticado()) {
    return true;
  }
  return inject(Router).createUrlTree(['/login']);
};
