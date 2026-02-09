import { Component, OnInit } from '@angular/core';
import { NgFor } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { SessionItem } from '../../core/models/api.models';

@Component({
  standalone: true,
  imports: [NgFor],
  template: `
    <div class="card">
      <h2>Mentor Dashboard</h2>
      <table class="table">
        <tr><th>ID</th><th>Graduate</th><th>Time</th><th>Status</th><th>Actions</th></tr>
        <tr *ngFor="let s of sessions">
          <td>{{s.id}}</td><td>{{s.graduateId}}</td><td>{{s.scheduledTime}}</td><td>{{s.status}}</td>
          <td class="row">
            <button (click)="approve(s.id)">Approve</button>
            <button class="secondary" (click)="reject(s.id)">Reject</button>
          </td>
        </tr>
      </table>
    </div>
  `
})
export class MentorDashboardComponent implements OnInit {
  sessions: SessionItem[] = [];
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.load(); }
  load() { this.api.mentorSessions().subscribe(r => this.sessions = r.data.content); }
  approve(id: number) { this.api.approveSession(id).subscribe(() => this.load()); }
  reject(id: number) { this.api.rejectSession(id).subscribe(() => this.load()); }
}
