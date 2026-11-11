import { TestBed } from '@angular/core/testing';
import { JwtService } from './jwt';

/** Construye un JWT de prueba. La firma da igual: el navegador no la comprueba, eso lo hace la API. */
function token(payload: object): string {
  const b64 = (o: object) =>
    btoa(String.fromCharCode(...new TextEncoder().encode(JSON.stringify(o))))
      .replace(/=+$/, '')
      .replace(/\+/g, '-')
      .replace(/\//g, '_');
  return `${b64({ alg: 'HS256' })}.${b64(payload)}.firma`;
}

const ahora = () => Math.floor(Date.now() / 1000);

describe('JwtService', () => {
  let jwt: JwtService;

  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({});
    jwt = TestBed.inject(JwtService);
  });

  it('sin token no hay sesión', () => {
    expect(jwt.autenticado()).toBe(false);
    expect(jwt.nombre()).toBeNull();
  });

  it('con un token válido está autenticado y lee el nombre (con tildes)', () => {
    jwt.guardar(token({ sub: 'ana@iparhotel.com', nombre: 'Ana López', rol: 'CLIENTE', exp: ahora() + 3600 }));
    expect(jwt.autenticado()).toBe(true);
    expect(jwt.nombre()).toBe('Ana López');
  });

  it('un token caducado no cuenta como sesión', () => {
    jwt.guardar(token({ sub: 'ana@iparhotel.com', nombre: 'Ana López', rol: 'CLIENTE', exp: ahora() - 10 }));
    expect(jwt.autenticado()).toBe(false);
  });

  it('cerrar sesión borra el token', () => {
    jwt.guardar(token({ sub: 'ana@iparhotel.com', nombre: 'Ana', rol: 'CLIENTE', exp: ahora() + 3600 }));
    jwt.cerrarSesion();
    expect(jwt.autenticado()).toBe(false);
    expect(sessionStorage.getItem('iparhotel.token')).toBeNull();
  });
});
