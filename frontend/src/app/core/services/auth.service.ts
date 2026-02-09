import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ApiResponse, AuthResponse } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'apm_token';
  private readonly roleKey = 'apm_role';
  role = signal<string | null>(localStorage.getItem(this.roleKey));

  constructor(private http: HttpClient) {}

  register(body: { email: string; password: string; role: string }) {
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

  token(): string | null { return localStorage.getItem(this.tokenKey); }
  logout() { localStorage.clear(); this.role.set(null); }
}
