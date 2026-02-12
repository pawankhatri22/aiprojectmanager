import { Component, OnInit } from '@angular/core';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { MentorProfile, NotificationItem, SessionItem } from '../../core/models/api.models';

@Component({
  standalone: true,
  imports: [NgFor, NgIf, DatePipe, FormsModule],
  template: `
    <div class="grid grid-2">
      <div class="card">
        <h3>Graduate Panel</h3>
        <ul><li>Browse Mentors</li><li>My Sessions</li><li>Messages</li></ul>
        <h4>Notifications</h4>
        <div class="card" *ngFor="let n of notifications">
          <b>{{n.type}}</b>: {{n.message}}
          <small>{{n.createdAt | date:'short'}}</small>
          <button *ngIf="!n.read" (click)="read(n.id)">Mark Read</button>
        </div>
      </div>
      <div>
        <div class="card">
          <h3>Stats</h3>
          <p>Total Sessions: {{sessions.length}}</p>
          <p>Pending Payment: {{pendingPaymentCount}}</p>
          <p *ngIf="nextSession">Next Paid Session: #{{nextSession.id}} at {{nextSession.scheduledTime | date:'short'}}</p>
        </div>
        <div class="card">
          <h3>Recent Sessions (My Sessions)</h3>
          <p *ngIf="message">{{message}}</p>
          <table class="table">
            <tr><th>ID</th><th>Mentor</th><th>Status</th><th>Time</th><th>Price</th><th>Links</th><th>Actions</th></tr>
            <tr *ngFor="let s of sessions">
              <td>{{s.id}}</td>
              <td>
                {{s.mentorEmail || s.mentorId}}
                <button class="secondary" (click)="viewMentor(s.mentorId)">View</button>
              </td>
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

          <div class="card" *ngFor="let s of sessions">
            <h4>Request Reschedule for Session #{{s.id}}</h4>
            <input type="datetime-local" [(ngModel)]="rescheduleTime[s.id]">
            <input type="number" [(ngModel)]="rescheduleDuration[s.id]" placeholder="Duration in minutes">
            <input [(ngModel)]="rescheduleReason[s.id]" placeholder="Reason">
            <button (click)="reschedule(s.id)">Request Reschedule</button>
          </div>

          <div class="card" *ngFor="let s of reviewEligibleSessions()">
            <h4>Leave review for Session #{{s.id}}</h4>
            <input type="number" min="1" max="5" [(ngModel)]="reviewRating[s.id]" placeholder="Rating (1-5)">
            <textarea [(ngModel)]="reviewComment[s.id]" placeholder="Comment"></textarea>
            <button (click)="submitReview(s.id)">Submit Review</button>
          </div>
        </div>
      </div>
    </div>

    <div class="card" *ngIf="selectedMentor">
      <h3>Mentor Profile</h3>
      <p><b>{{selectedMentor.fullName}}</b> (⭐ {{selectedMentor.ratingAverage ?? 0}})</p>
      <p>{{selectedMentor.jobTitle}} at {{selectedMentor.company}}</p>
      <p>{{selectedMentor.bio}}</p>
      <p>Skills: {{selectedMentor.expertise.join(', ')}}</p>
      <button class="secondary" (click)="selectedMentor=undefined">Close</button>
    </div>
  `
})
export class GraduateDashboardComponent implements OnInit {
  sessions: SessionItem[] = [];
  notifications: NotificationItem[] = [];
  selectedMentor?: MentorProfile;
  message = '';
  reviewRating: Record<number, number> = {};
  reviewComment: Record<number, string> = {};
  rescheduleTime: Record<number, string> = {};
  rescheduleDuration: Record<number, number> = {};
  rescheduleReason: Record<number, string> = {};

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.load(); this.loadNotifications(); }

  get pendingPaymentCount(): number {
    return this.sessions.filter(s => s.status === 'PENDING_PAYMENT').length;
  }

  get nextSession(): SessionItem | undefined {
    return this.sessions.filter(s => s.status === 'PAID').sort((a, b) => a.scheduledTime.localeCompare(b.scheduledTime))[0];
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

  loadNotifications() { this.api.notifications().subscribe({ next: r => this.notifications = r.data }); }
  read(id: number) { this.api.markNotificationRead(id).subscribe(() => this.loadNotifications()); }

  viewMentor(mentorUserId: number) {
    this.api.getMentorByUserId(mentorUserId).subscribe({ next: r => this.selectedMentor = r.data });
  }

  cancel(id: number) { this.api.cancelSession(id).subscribe({ next: () => this.load(), error: (e) => this.message = e.error?.message ?? 'Cancel failed' }); }
  complete(id: number) { this.api.completeSession(id).subscribe({ next: () => { this.message = 'Session marked complete'; this.load(); }, error: (e) => this.message = e.error?.message ?? 'Complete failed' }); }

  pay(id: number) {
    this.api.pay(id).subscribe({
      next: () => { this.message = 'Payment successful. Meeting link generated.'; this.load(); },
      error: (e) => this.message = e.error?.message ?? 'Payment failed'
    });
  }

  reschedule(sessionId: number) {
    this.api.requestReschedule(sessionId, {
      newScheduledTime: this.rescheduleTime[sessionId],
      newDurationMinutes: Number(this.rescheduleDuration[sessionId] || 60),
      reason: this.rescheduleReason[sessionId] || 'Need to move due to schedule conflict'
    }).subscribe({ next: () => this.message = 'Reschedule requested', error: (e) => this.message = e.error?.message ?? 'Reschedule failed' });
  }

  submitReview(sessionId: number) {
    const rating = Number(this.reviewRating[sessionId] || 0);
    const comment = (this.reviewComment[sessionId] || '').trim();
    if (rating < 1 || rating > 5 || !comment) {
      this.message = 'Please provide rating (1-5) and comment';
      return;
    }
    this.api.createReview({ sessionId, rating, comment }).subscribe({ next: () => { this.message = 'Review submitted'; this.load(); }, error: (e) => this.message = e.error?.message ?? 'Review failed' });
  }
}
