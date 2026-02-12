import { Component, OnInit } from '@angular/core';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { AttendanceItem, NotificationItem, RescheduleItem, SessionItem } from '../../core/models/api.models';

@Component({
  standalone: true,
  imports: [NgFor, ReactiveFormsModule, NgIf, DatePipe],
  template: `
    <div class="card">
      <h2>Mentor Profile</h2>
      <form [formGroup]="profileForm" (ngSubmit)="saveProfile()">
        <label>Full Name</label>
        <input formControlName="fullName" placeholder="e.g. Sarah Johnson">
        <label>Company</label>
        <input formControlName="company" placeholder="e.g. Google">
        <label>Job Title</label>
        <input formControlName="jobTitle" placeholder="e.g. Senior Software Engineer">
        <label>Years of Experience</label>
        <input formControlName="yearsExperience" type="number" placeholder="e.g. 8">
        <label>Bio</label>
        <textarea formControlName="bio" placeholder="Write a short intro"></textarea>
        <label>Hourly Rate (USD)</label>
        <input formControlName="hourlyRate" type="number" placeholder="e.g. 75">
        <label>Photo URL</label>
        <input formControlName="photoUrl" placeholder="https://...">
        <label>Skills / Expertise (comma separated)</label>
        <input formControlName="expertise" placeholder="System Design, Java, Interview Prep">
        <button>Save Profile</button>
      </form>
      <p>{{message}}</p>
    </div>

    <div class="card">
      <h3>Notifications</h3>
      <div class="card" *ngFor="let n of notifications">
        <b>{{n.type}}</b>: {{n.message}} <small>{{n.createdAt | date:'short'}}</small>
        <button *ngIf="!n.read" (click)="read(n.id)">Mark Read</button>
      </div>
    </div>

    <div class="card">
      <h2>Mentor Sessions</h2>
      <table class="table">
        <tr><th>ID</th><th>Graduate</th><th>Time</th><th>Status</th><th>Price</th><th>Meeting</th><th>Actions</th></tr>
        <tr *ngFor="let s of sessions">
          <td>{{s.id}}</td>
          <td>{{s.graduateEmail || s.graduateId}}</td>
          <td>{{s.scheduledTime | date:'short'}}</td>
          <td>{{s.status}}</td>
          <td>{{s.price}}</td>
          <td><a *ngIf="s.meetingLink" [href]="s.meetingLink" target="_blank">Open</a></td>
          <td class="row">
            <button (click)="approve(s.id)" *ngIf="s.status==='REQUESTED'">Approve</button>
            <button class="secondary" (click)="reject(s.id)" *ngIf="s.status==='REQUESTED'">Reject</button>
            <button class="secondary" (click)="loadAttendance(s.id)">Attendance</button>
          </td>
        </tr>
      </table>
    </div>

    <div class="card" *ngIf="reschedules.length">
      <h3>Reschedule Requests</h3>
      <div class="card" *ngFor="let r of reschedules">
        Session #{{r.sessionId}} | {{r.oldScheduledTime | date:'short'}} -> {{r.newScheduledTime | date:'short'}}
        <p>{{r.reason}}</p>
        <button (click)="acceptReschedule(r.id)" *ngIf="r.status==='REQUESTED'">Accept</button>
        <button (click)="rejectReschedule(r.id)" *ngIf="r.status==='REQUESTED'">Reject</button>
      </div>
    </div>

    <div class="card" *ngIf="attendance.length">
      <h3>Attendance</h3>
      <div *ngFor="let a of attendance">{{a.participantEmail}} - {{a.status}} (Join: {{a.joinTime || '-'}} / Leave: {{a.leaveTime || '-'}})</div>
    </div>
  `
})
export class MentorDashboardComponent implements OnInit {
  sessions: SessionItem[] = [];
  notifications: NotificationItem[] = [];
  reschedules: RescheduleItem[] = [];
  attendance: AttendanceItem[] = [];
  message = '';
  profileForm = this.fb.group({
    fullName: ['', Validators.required], company: [''], jobTitle: [''], yearsExperience: [1], bio: [''], hourlyRate: [50, Validators.required], photoUrl: [''], expertise: ['']
  });

  constructor(private api: ApiService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.load();
    this.loadProfile();
    this.loadNotifications();
  }

  load() {
    this.api.mentorSessions().subscribe(r => {
      this.sessions = r.data.content;
      if (this.sessions.length) this.api.sessionReschedules(this.sessions[0].id).subscribe(x => this.reschedules = x.data);
    });
  }

  loadNotifications() { this.api.notifications().subscribe(r => this.notifications = r.data); }
  read(id: number) { this.api.markNotificationRead(id).subscribe(() => this.loadNotifications()); }

  loadProfile() {
    this.api.getMyMentorProfile().subscribe({
      next: (res) => {
        const p = res.data;
        this.profileForm.patchValue({
          fullName: p.fullName, company: p.company, jobTitle: p.jobTitle, yearsExperience: p.yearsExperience, bio: p.bio,
          hourlyRate: p.hourlyRate, photoUrl: p.photoUrl, expertise: (p.expertise ?? []).join(', ')
        });
      }
    });
  }

  approve(id: number) { this.api.approveSession(id).subscribe({ next: () => this.load(), error: (e) => this.message = e.error?.message ?? 'Approve failed' }); }
  reject(id: number) { this.api.rejectSession(id).subscribe({ next: () => this.load(), error: (e) => this.message = e.error?.message ?? 'Reject failed' }); }

  acceptReschedule(id: number) { this.api.acceptReschedule(id).subscribe(() => this.load()); }
  rejectReschedule(id: number) { this.api.rejectReschedule(id).subscribe(() => this.load()); }
  loadAttendance(sessionId: number) { this.api.sessionAttendance(sessionId).subscribe(r => this.attendance = r.data); }

  saveProfile() {
    if (this.profileForm.invalid) return;
    const v = this.profileForm.getRawValue();
    const body = { ...v, yearsExperience: Number(v.yearsExperience || 0), hourlyRate: Number(v.hourlyRate || 0), expertise: (v.expertise ?? '').split(',').map(x => x.trim()).filter(Boolean) };
    this.api.upsertMentorProfile(body).subscribe({ next: () => { this.message = 'Profile saved and persisted.'; this.loadProfile(); }, error: (e) => this.message = e.error?.message ?? 'Could not save profile' });
  }
}
