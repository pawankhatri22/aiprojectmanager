import { Component, OnInit } from '@angular/core';
import { NgFor } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { SessionItem } from '../../core/models/api.models';

@Component({
  standalone: true,
  imports: [NgFor],
  template: `
    <div class="grid grid-2">
      <div class="card">
        <h3>Sidebar</h3>
        <ul><li>Browse Mentors</li><li>My Sessions</li><li>Messages</li></ul>
      </div>
      <div>
        <div class="card"><h3>Stats Cards</h3><p>Total Sessions: {{sessions.length}}</p></div>
        <div class="card">
          <h3>Recent Sessions</h3>
          <table class="table"><tr><th>ID</th><th>Status</th><th>Time</th><th>Action</th></tr>
            <tr *ngFor="let s of sessions"><td>{{s.id}}</td><td>{{s.status}}</td><td>{{s.scheduledTime}}</td><td><button (click)="cancel(s.id)">Cancel</button></td></tr>
          </table>
        </div>
      </div>
    </div>
  `
})
export class GraduateDashboardComponent implements OnInit {
  sessions: SessionItem[] = [];
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.load(); }
  load() { this.api.mySessions().subscribe(res => this.sessions = res.data.content); }
  cancel(id: number) { this.api.cancelSession(id).subscribe(() => this.load()); }
}
