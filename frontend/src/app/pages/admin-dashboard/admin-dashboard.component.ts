import { Component } from '@angular/core';
import { NgFor, JsonPipe, NgIf } from '@angular/common';
import { ApiService } from '../../core/services/api.service';

@Component({
  standalone: true,
  imports: [NgFor, JsonPipe, NgIf],
  template: `
    <div class="card">
      <h2>Admin Dashboard</h2>
      <div class="row">
        <button (click)="tab='users'; load()">Users</button>
        <button (click)="tab='sessions'; load()">Sessions</button>
        <button (click)="tab='payments'; load()">Payments</button>
      </div>
      <table class="table" *ngIf="tab==='users'">
        <tr><th>Data</th><th>Action</th></tr>
        <tr *ngFor="let i of items"><td><pre>{{i | json}}</pre></td><td><button (click)="disable(i.id)">Disable</button></td></tr>
      </table>
      <ng-container *ngIf="tab!=='users'">
        <div class="card" *ngFor="let i of items"><pre>{{i | json}}</pre></div>
      </ng-container>
    </div>
  `
})
export class AdminDashboardComponent {
  tab: 'users' | 'sessions' | 'payments' = 'users';
  items: any[] = [];
  constructor(private api: ApiService) { this.load(); }

  load() {
    if (this.tab === 'users') this.api.adminUsers().subscribe(r => this.items = r.data.content as any[]);
    if (this.tab === 'sessions') this.api.adminSessions().subscribe(r => this.items = r.data.content as any[]);
    if (this.tab === 'payments') this.api.adminPayments().subscribe(r => this.items = r.data.content as any[]);
  }

  disable(id: number) { this.api.disableUser(id).subscribe(() => this.load()); }
}
