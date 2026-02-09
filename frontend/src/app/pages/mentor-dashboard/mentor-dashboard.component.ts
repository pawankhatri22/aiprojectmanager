import { Component, OnInit } from '@angular/core';
import { NgFor } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { SessionItem } from '../../core/models/api.models';

@Component({
  standalone: true,
  imports: [NgFor, ReactiveFormsModule],
  template: `
    <div class="card">
      <h2>Mentor Profile</h2>
      <form [formGroup]="profileForm" (ngSubmit)="saveProfile()">
        <input formControlName="fullName" placeholder="Full name">
        <input formControlName="company" placeholder="Company">
        <input formControlName="jobTitle" placeholder="Job title">
        <input formControlName="yearsExperience" type="number" placeholder="Years experience">
        <textarea formControlName="bio" placeholder="Bio"></textarea>
        <input formControlName="hourlyRate" type="number" placeholder="Hourly rate">
        <input formControlName="photoUrl" placeholder="Photo URL">
        <input formControlName="expertise" placeholder="Expertise tags comma separated">
        <button>Save Profile</button>
      </form>
      <p>{{message}}</p>
    </div>

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

  ngOnInit(): void { this.load(); }
  load() { this.api.mentorSessions().subscribe(r => this.sessions = r.data.content); }
  approve(id: number) { this.api.approveSession(id).subscribe(() => this.load()); }
  reject(id: number) { this.api.rejectSession(id).subscribe(() => this.load()); }

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
      next: () => this.message = 'Profile saved. Graduates can now discover you.',
      error: (e) => this.message = e.error?.message ?? 'Could not save profile'
    });
  }
}
