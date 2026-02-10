import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { MentorProfile, ReviewItem } from '../../core/models/api.models';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, NgFor, NgIf, DatePipe],
  template: `
    <div class="card">
      <h2>Mentor List</h2>
      <div class="row">
        <input [formControl]="search.controls.q" placeholder="Search mentor by name or skill"/>
        <select [formControl]="search.controls.sortBy" style="max-width:220px;">
          <option value="">Default</option>
          <option value="topRated">Top Rated</option>
        </select>
        <button style="width:auto" (click)="load()">Search</button>
      </div>
      <p *ngIf="message">{{message}}</p>
    </div>

    <div class="card" *ngFor="let mentor of mentors">
      <p><strong>{{mentor.fullName}}</strong> - {{mentor.jobTitle}} at {{mentor.company}}</p>
      <p>Experience: {{mentor.yearsExperience}} years | Hourly: {{mentor.hourlyRate}} | ⭐ {{mentor.ratingAverage ?? 0}}</p>
      <p>Skills: {{mentor.expertise.join(', ')}}</p>
      <input type="datetime-local" #dt>
      <input type="number" #dur placeholder="Duration (minutes)">
      <div class="row">
        <button (click)="view(mentor.id, mentor.userId)">View Profile</button>
        <button (click)="request(mentor.userId, dt.value, dur.value)">Request Session</button>
      </div>
    </div>

    <div class="card" *ngIf="selected">
      <h3>{{selected.fullName}}</h3>
      <p><strong>Role:</strong> {{selected.jobTitle}} at {{selected.company}}</p>
      <p><strong>Experience:</strong> {{selected.yearsExperience}} years</p>
      <p><strong>Hourly Rate:</strong> {{selected.hourlyRate}}</p>
      <p><strong>Rating:</strong> ⭐ {{selected.ratingAverage ?? 0}}</p>
      <p><strong>Bio:</strong> {{selected.bio || 'No bio provided'}}</p>
      <p><strong>Skills:</strong> {{selected.expertise.join(', ') || 'N/A'}}</p>

      <h4>Reviews</h4>
      <p *ngIf="!reviews.length">No reviews yet</p>
      <div class="card" *ngFor="let r of reviews">
        ⭐ {{r.rating}} - {{r.comment}} <small>({{r.createdAt | date:'short'}})</small>
      </div>
    </div>
  `
})
export class MentorListComponent implements OnInit {
  mentors: MentorProfile[] = [];
  selected?: MentorProfile;
  reviews: ReviewItem[] = [];
  message = '';
  search = this.fb.group({ q: '', sortBy: '' });

  constructor(private fb: FormBuilder, private api: ApiService) {}

  ngOnInit(): void { this.load(); }

  load() {
    this.api.getMentors(this.search.value.q ?? '', 0, 50, this.search.value.sortBy ?? '').subscribe({
      next: (res) => {
        this.mentors = res.data.content;
        this.message = this.mentors.length ? '' : 'No mentors found yet. Ask a mentor to complete their profile from Mentor Dashboard.';
      },
      error: (e) => {
        this.message = e.error?.message ?? 'Failed to load mentors';
      }
    });
  }

  view(id: number, mentorUserId: number) {
    this.api.getMentor(id).subscribe(res => this.selected = res.data);
    this.api.getMentorReviews(mentorUserId).subscribe(res => this.reviews = res.data);
  }

  request(mentorId: number, scheduledTime: string, duration: string) {
    this.api.requestSession({ mentorId, scheduledTime, durationMinutes: Number(duration) || 60 }).subscribe({
      next: () => this.message = 'Session request submitted. Mentor will approve and send confirmation/payment link.',
      error: (e) => this.message = e.error?.message ?? 'Session request failed'
    });
  }
}
