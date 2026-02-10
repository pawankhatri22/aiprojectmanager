import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AppRole, AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <div class="card">
      <h2>Register</h2>
      <form [formGroup]="form" (ngSubmit)="submit()">
        <input placeholder="Email" formControlName="email"/>
        <input placeholder="Password" type="password" formControlName="password"/>
        <select formControlName="role">
          <option value="GRADUATE">GRADUATE</option>
          <option value="MENTOR">MENTOR</option>
          <option value="ADMIN">ADMIN</option>
        </select>
        <button>Register</button>
      </form>
      <p>{{message}}</p>
    </div>
  `
})
export class RegisterComponent {
  message = '';
  form = this.fb.group({ email: ['', [Validators.required, Validators.email]], password: ['', Validators.required], role: ['GRADUATE' as AppRole, Validators.required] });
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {}

  submit() {
    if (this.form.invalid) return;
    this.auth.register(this.form.getRawValue() as { email: string; password: string; role: AppRole }).subscribe({
      next: () => {
        this.message = 'Registered. Please login.';
        this.router.navigateByUrl('/login');
      },
      error: (e) => this.message = e.error?.message ?? 'Register failed'
    });
  }
}
