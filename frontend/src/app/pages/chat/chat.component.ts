import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgFor } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { MessageItem } from '../../core/models/api.models';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, NgFor],
  template: `
    <div class="card">
      <h2>Chat</h2>
      <input [formControl]="sessionId" placeholder="Session ID" type="number"/>
      <button (click)="load()">Load History</button>
      <div class="card" *ngFor="let m of messages">{{m.timestamp}} - {{m.messageText}}</div>
      <form [formGroup]="form" (ngSubmit)="send()">
        <textarea formControlName="messageText" placeholder="Type message"></textarea>
        <button>Send</button>
      </form>
    </div>
  `
})
export class ChatComponent {
  messages: MessageItem[] = [];
  sessionId = this.fb.control(0, { nonNullable: true });
  form = this.fb.group({ messageText: ['', Validators.required] });
  constructor(private fb: FormBuilder, private api: ApiService) {}
  load() { this.api.getMessages(this.sessionId.value).subscribe(r => this.messages = r.data); }
  send() {
    if (this.form.invalid) return;
    this.api.sendMessage({ sessionId: this.sessionId.value, messageText: this.form.value.messageText ?? '' }).subscribe(() => this.load());
  }
}
