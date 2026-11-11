import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Cabecera } from './componentes/cabecera/cabecera';

/** Plantilla comun: cabecera fija y el contenido de cada pagina en <router-outlet />. */
@Component({
  imports: [RouterOutlet, Cabecera],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {}
