import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="container" style="max-width: 400px; margin-top: 50px;">
      <h2>Inscription</h2>
      <p>Fonctionnalité en cours de développement...</p>
      <a routerLink="/login">Retour à la connexion</a>
    </div>
  `
})
export class RegisterComponent {}
