import { Routes } from '@angular/router';
import { autenticadoGuard } from './guards/autenticado-guard';
import { Login } from './paginas/login/login';
import { Principal } from './paginas/principal/principal';
import { Reservar } from './paginas/reservar/reservar';

export const routes: Routes = [
  { path: '', component: Principal, title: 'Iparhotel · Habitaciones' },
  { path: 'login', component: Login, title: 'Iparhotel · Iniciar sesión' },
  // Protegida: sin sesion el guard lleva a /login
  { path: 'reservar/:id', component: Reservar, canActivate: [autenticadoGuard], title: 'Iparhotel · Reservar' },
  { path: '**', redirectTo: '' },
];
