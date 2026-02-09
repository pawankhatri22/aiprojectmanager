import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="nav">
      <a routerLink="/mentors">Mentors</a>
      <a routerLink="/graduate-dashboard">Graduate</a>
      <a routerLink="/mentor-dashboard">Mentor</a>
      <a routerLink="/admin-dashboard">Admin</a>
      <a routerLink="/chat">Chat</a>
      <button style="width:auto" (click)="auth.logout()">Logout</button>
    </div>
  `
})
export class NavbarComponent {
  constructor(public auth: AuthService) {}
}
