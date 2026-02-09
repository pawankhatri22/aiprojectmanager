import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ApiResponse, AuthResponse } from '../models/api.models';

export type AppRole = 'ADMIN' | 'MENTOR' | 'GRADUATE';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'apm_token';
  private readonly roleKey = 'apm_role';
  role = signal<AppRole | null>(this.readRole());

  constructor(private http: HttpClient) {}

  register(body: { email: string; password: string; role: AppRole }) {
    return this.http.post<ApiResponse<AuthResponse>>(`${environment.apiUrl}/auth/register`, body);
  }

  login(body: { email: string; password: string }) {
    return this.http.post<ApiResponse<AuthResponse>>(`${environment.apiUrl}/auth/login`, body);
  }

  saveSession(auth: AuthResponse) {
    localStorage.setItem(this.tokenKey, auth.token);
    localStorage.setItem(this.roleKey, auth.role);
    this.role.set(auth.role);
  }

  token(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return Boolean(this.token());
  }

  currentRole(): AppRole | null {
    return this.role();
  }

  homeForRole(role: AppRole | null): string {
    if (role === 'ADMIN') return '/admin-dashboard';
    if (role === 'MENTOR') return '/mentor-dashboard';
    if (role === 'GRADUATE') return '/mentors';
    return '/login';
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.roleKey);
    this.role.set(null);
  }

  private readRole(): AppRole | null {
    const role = localStorage.getItem(this.roleKey);
    if (role === 'ADMIN' || role === 'MENTOR' || role === 'GRADUATE') return role;
    return null;
  }
}
