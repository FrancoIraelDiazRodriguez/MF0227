import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree, provideRouter } from '@angular/router';
import { JwtService } from '../servicios/jwt';
import { autenticadoGuard } from './autenticado-guard';

describe('autenticadoGuard', () => {
  const ejecutar = () =>
    TestBed.runInInjectionContext(() => autenticadoGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot));

  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({ providers: [provideRouter([])] });
  });

  it('sin sesión redirige a /login', () => {
    const resultado = ejecutar() as UrlTree;
    expect(TestBed.inject(Router).serializeUrl(resultado)).toBe('/login');
  });

  it('con sesión deja pasar', () => {
    const payload = btoa(JSON.stringify({ sub: 'a@a.com', nombre: 'A', rol: 'CLIENTE', exp: Date.now() / 1000 + 60 }));
    TestBed.inject(JwtService).guardar(`x.${payload}.y`);
    expect(ejecutar()).toBe(true);
  });
});
