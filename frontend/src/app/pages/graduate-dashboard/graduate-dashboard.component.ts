import { Component, OnInit } from '@angular/core';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { SessionItem } from '../../core/models/api.models';

@Component({
  standalone: true,
  imports: [NgFor, NgIf, DatePipe, FormsModule],
  template: `
    <div class="grid grid-2">
      <div class="card">
        <h3>Graduate Panel</h3>
        <ul><li>Browse Mentors</li><li>My Sessions</li><li>Messages</li></ul>
      </div>
      <div>
        <div class="card">
          <h3>Stats</h3>
          <p>Total Sessions: {{sessions.length}}</p>
          <p>Pending Payment: {{pendingPaymentCount}}</p>
          <p *ngIf="nextSession">Next Paid Session: #{{nextSession.id}} at {{nextSession.scheduledTime | date:'short'}}</p>
        </div>
        <div class="card">
          <h3>Recent Sessions</h3>
          <p *ngIf="message">{{message}}</p>
          <table class="table">
            <tr><th>ID</th><th>Status</th><th>Time</th><th>Price</th><th>Links</th><th>Actions</th></tr>
            <tr *ngFor="let s of sessions">
              <td>{{s.id}}</td>
              <td>{{s.status}}</td>
              <td>{{s.scheduledTime | date:'short'}}</td>
              <td>{{s.price}}</td>
              <td>
                <a *ngIf="s.confirmationLink" [href]="s.confirmationLink" target="_blank">Confirm</a>
                <a *ngIf="s.meetingLink" [href]="s.meetingLink" target="_blank" style="margin-left:6px;">Join</a>
              </td>
              <td>
                <button *ngIf="s.status==='PENDING_PAYMENT'" (click)="pay(s.id)">Pay</button>
                <button *ngIf="s.status==='PAID'" (click)="complete(s.id)">Mark Complete</button>
                <button *ngIf="s.status==='REQUESTED'" class="secondary" (click)="cancel(s.id)">Cancel</button>
              </td>
            </tr>
          </table>

          <div class="card" *ngFor="let s of reviewEligibleSessions()">
            <h4>Leave review for Session #{{s.id}}</h4>
            <input type="number" min="1" max="5" [(ngModel)]="reviewRating[s.id]" placeholder="Rating (1-5)">
            <textarea [(ngModel)]="reviewComment[s.id]" placeholder="Comment"></textarea>
            <button (click)="submitReview(s.id)">Submit Review</button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class GraduateDashboardComponent implements OnInit {
  sessions: SessionItem[] = [];
  message = '';
  reviewRating: Record<number, number> = {};
  reviewComment: Record<number, string> = {};

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.load(); }

  get pendingPaymentCount(): number {
    return this.sessions.filter(s => s.status === 'PENDING_PAYMENT').length;
  }

  get nextSession(): SessionItem | undefined {
    return this.sessions
      .filter(s => s.status === 'PAID')
      .sort((a, b) => a.scheduledTime.localeCompare(b.scheduledTime))[0];
  }

  reviewEligibleSessions(): SessionItem[] {
    const now = Date.now();
    return this.sessions.filter(s => {
      const end = new Date(s.scheduledTime).getTime() + s.durationMinutes * 60 * 1000;
      return s.status === 'COMPLETED' || (s.status === 'PAID' && now > end);
    });
  }

  load() {
    this.api.mySessions().subscribe({
      next: (res) => this.sessions = res.data.content,
      error: (e) => this.message = e.error?.message ?? 'Could not load sessions'
    });
  }

  cancel(id: number) {
    this.api.cancelSession(id).subscribe({
      next: () => this.load(),
      error: (e) => this.message = e.error?.message ?? 'Cancel failed'
    });
  }

  complete(id: number) {
    this.api.completeSession(id).subscribe({
      next: () => { this.message = 'Session marked complete'; this.load(); },
      error: (e) => this.message = e.error?.message ?? 'Complete failed'
    });
  }

  pay(id: number) {
    this.api.pay(id).subscribe({
      next: () => {
        this.message = 'Payment successful. Meeting link generated.';
        this.load();
      },
      error: (e) => this.message = e.error?.message ?? 'Payment failed'
    });
  }

  submitReview(sessionId: number) {
    const rating = Number(this.reviewRating[sessionId] || 0);
    const comment = (this.reviewComment[sessionId] || '').trim();
    if (rating < 1 || rating > 5 || !comment) {
      this.message = 'Please provide rating (1-5) and comment';
      return;
    }
    this.api.createReview({ sessionId, rating, comment }).subscribe({
      next: () => { this.message = 'Review submitted'; this.load(); },
      error: (e) => this.message = e.error?.message ?? 'Review failed'
    });
  }
}
