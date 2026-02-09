import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="card">
      <h2>Login</h2>
      <form [formGroup]="form" (ngSubmit)="submit()">
        <input placeholder="Email" formControlName="email"/>
        <input placeholder="Password" type="password" formControlName="password"/>
        <button>Login</button>
      </form>
      <a routerLink="/register">Register</a>
      <p>{{message}}</p>
    </div>
  `
})
export class LoginComponent {
  message = '';
  form = this.fb.group({ email: ['', [Validators.required, Validators.email]], password: ['', Validators.required] });
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {}
  submit() {
    if (this.form.invalid) return;
    this.auth.login(this.form.getRawValue() as { email: string; password: string }).subscribe({
      next: (res) => { this.auth.saveSession(res.data); this.router.navigateByUrl('/mentors'); },
      error: (e) => this.message = e.error?.message ?? 'Login failed'
    });
  }
}
