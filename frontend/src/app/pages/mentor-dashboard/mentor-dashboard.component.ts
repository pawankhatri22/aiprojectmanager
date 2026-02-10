import { Component, OnInit } from '@angular/core';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { SessionItem } from '../../core/models/api.models';

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
        <textarea formControlName="bio" placeholder="Write a short intro about your mentoring focus and background"></textarea>

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
      <h2>Mentor Sessions</h2>
      <table class="table">
        <tr><th>ID</th><th>Graduate</th><th>Time</th><th>Status</th><th>Price</th><th>Confirmation</th><th>Meeting</th><th>Actions</th></tr>
        <tr *ngFor="let s of sessions">
          <td>{{s.id}}</td>
          <td>{{s.graduateId}}</td>
          <td>{{s.scheduledTime | date:'short'}}</td>
          <td>{{s.status}}</td>
          <td>{{s.price}}</td>
          <td><a *ngIf="s.confirmationLink" [href]="s.confirmationLink" target="_blank">Sent</a></td>
          <td><a *ngIf="s.meetingLink" [href]="s.meetingLink" target="_blank">Open</a></td>
          <td class="row">
            <button (click)="approve(s.id)" *ngIf="s.status==='REQUESTED'">Approve</button>
            <button class="secondary" (click)="reject(s.id)" *ngIf="s.status==='REQUESTED'">Reject</button>
          </td>
        </tr>
      </table>
    </div>
  `
})
export class MentorDashboardComponent implements OnInit {
  sessions: SessionItem[] = [];
  message = '';
  profileForm = this.fb.group({
    fullName: ['', Validators.required],
    company: [''],
    jobTitle: [''],
    yearsExperience: [1],
    bio: [''],
    hourlyRate: [50, Validators.required],
    photoUrl: [''],
    expertise: ['']
  });

  constructor(private api: ApiService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.load();
    this.loadProfile();
  }

  load() {
    this.api.mentorSessions().subscribe(r => this.sessions = r.data.content);
  }

  loadProfile() {
    this.api.getMyMentorProfile().subscribe({
      next: (res) => {
        const p = res.data;
        this.profileForm.patchValue({
          fullName: p.fullName,
          company: p.company,
          jobTitle: p.jobTitle,
          yearsExperience: p.yearsExperience,
          bio: p.bio,
          hourlyRate: p.hourlyRate,
          photoUrl: p.photoUrl,
          expertise: (p.expertise ?? []).join(', ')
        });
      },
      error: () => {
        // mentor may not have profile yet
      }
    });
  }

  approve(id: number) {
    this.api.approveSession(id).subscribe({ next: () => this.load(), error: (e) => this.message = e.error?.message ?? 'Approve failed' });
  }

  reject(id: number) {
    this.api.rejectSession(id).subscribe({ next: () => this.load(), error: (e) => this.message = e.error?.message ?? 'Reject failed' });
  }

  saveProfile() {
    if (this.profileForm.invalid) return;
    const v = this.profileForm.getRawValue();
    const body = {
      ...v,
      yearsExperience: Number(v.yearsExperience || 0),
      hourlyRate: Number(v.hourlyRate || 0),
      expertise: (v.expertise ?? '').split(',').map(x => x.trim()).filter(Boolean)
    };
    this.api.upsertMentorProfile(body).subscribe({
      next: () => {
        this.message = 'Profile saved and persisted.';
        this.loadProfile();
      },
      error: (e) => this.message = e.error?.message ?? 'Could not save profile'
    });
  }
}
