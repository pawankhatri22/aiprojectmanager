import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, NgIf],
  template: `
    <div class="nav" *ngIf="auth.isAuthenticated()">
      <a routerLink="/mentors" *ngIf="auth.currentRole()==='GRADUATE'">Browse Mentors</a>
      <a routerLink="/graduate-dashboard" *ngIf="auth.currentRole()==='GRADUATE'">My Sessions</a>

      <a routerLink="/mentor-dashboard" *ngIf="auth.currentRole()==='MENTOR'">Mentor Dashboard</a>

      <a routerLink="/admin-dashboard" *ngIf="auth.currentRole()==='ADMIN'">Admin Dashboard</a>

      <a routerLink="/chat" *ngIf="auth.currentRole()!=='ADMIN'">Chat</a>
      <button style="width:auto" (click)="logout()">Logout</button>
    </div>
  `
})
export class NavbarComponent {
  constructor(public auth: AuthService, private router: Router) {}

  logout() {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }
}
