import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { JsonPipe, NgFor, NgIf } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { MentorProfile } from '../../core/models/api.models';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, NgFor, NgIf, JsonPipe],
  template: `
    <div class="card">
      <h2>Mentor List</h2>
      <div class="row">
        <input [formControl]="search.controls.q" placeholder="Search mentor by name or skill"/>
        <button style="width:auto" (click)="load()">Search</button>
      </div>
      <p *ngIf="message">{{message}}</p>
    </div>

    <div class="card" *ngFor="let mentor of mentors">
      <p><strong>{{mentor.fullName}}</strong> - {{mentor.jobTitle}} at {{mentor.company}}</p>
      <p>Experience: {{mentor.yearsExperience}} years</p>
      <p>Hourly: {{mentor.hourlyRate}}</p>
      <input type="datetime-local" #dt>
      <input type="number" #dur placeholder="Duration (minutes)">
      <div class="row">
        <button (click)="view(mentor.id)">View Profile</button>
        <button (click)="request(mentor.userId, dt.value, dur.value)">Request Session</button>
      </div>
    </div>

    <div class="card" *ngIf="selected">
      <h3>Mentor Profile</h3>
      <pre>{{selected | json}}</pre>
    </div>
  `
})
export class MentorListComponent implements OnInit {
  mentors: MentorProfile[] = [];
  selected?: MentorProfile;
  message = '';
  search = this.fb.group({ q: '' });
  constructor(private fb: FormBuilder, private api: ApiService) {}

  ngOnInit(): void { this.load(); }

  load() {
    this.api.getMentors(this.search.value.q ?? '', 0, 50).subscribe({
      next: (res) => {
        this.mentors = res.data.content;
        this.message = this.mentors.length ? '' : 'No mentors found yet. Ask a mentor to complete their profile from Mentor Dashboard.';
      },
      error: (e) => {
        this.message = e.error?.message ?? 'Failed to load mentors';
      }
    });
  }

  view(id: number) { this.api.getMentor(id).subscribe(res => this.selected = res.data); }

  request(mentorId: number, scheduledTime: string, duration: string) {
    this.api.requestSession({ mentorId, scheduledTime, durationMinutes: Number(duration) || 60 }).subscribe({
      next: () => this.message = 'Session request submitted. Mentor will approve and send confirmation/payment link.',
      error: (e) => this.message = e.error?.message ?? 'Session request failed'
    });
  }
}
