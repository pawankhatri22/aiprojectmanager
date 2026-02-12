import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ApiResponse, AttendanceItem, GraduateProfile, MentorProfile, MessageItem, NotificationItem, Page, PaymentItem, RescheduleItem, ReviewItem, SessionItem } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  getMentors(search = '', page = 0, size = 10, sortBy = '') {
    return this.http.get<ApiResponse<Page<MentorProfile>>>(`${environment.apiUrl}/mentors?search=${encodeURIComponent(search)}&page=${page}&size=${size}&sortBy=${encodeURIComponent(sortBy)}`);
  }
  getMentor(id: number) { return this.http.get<ApiResponse<MentorProfile>>(`${environment.apiUrl}/mentors/${id}`); }
  getMentorByUserId(userId: number) { return this.http.get<ApiResponse<MentorProfile>>(`${environment.apiUrl}/mentors/user/${userId}`); }
  getMyMentorProfile() { return this.http.get<ApiResponse<MentorProfile>>(`${environment.apiUrl}/mentor/profile`); }
  getMentorReviews(mentorUserId: number) { return this.http.get<ApiResponse<ReviewItem[]>>(`${environment.apiUrl}/reviews/mentor/${mentorUserId}`); }
  upsertMentorProfile(body: unknown, create = false) { return create ? this.http.post<ApiResponse<MentorProfile>>(`${environment.apiUrl}/mentor/profile`, body) : this.http.put<ApiResponse<MentorProfile>>(`${environment.apiUrl}/mentor/profile`, body); }

  getGraduateProfile() { return this.http.get<ApiResponse<GraduateProfile>>(`${environment.apiUrl}/graduate/profile`); }
  updateGraduateProfile(body: unknown) { return this.http.put<ApiResponse<GraduateProfile>>(`${environment.apiUrl}/graduate/profile`, body); }

  requestSession(body: { mentorId: number; scheduledTime: string; durationMinutes: number }) { return this.http.post<ApiResponse<SessionItem>>(`${environment.apiUrl}/sessions/request`, body); }
  mySessions(page = 0, size = 10) { return this.http.get<ApiResponse<Page<SessionItem>>>(`${environment.apiUrl}/sessions/my?page=${page}&size=${size}`); }
  mentorSessions(page = 0, size = 10) { return this.http.get<ApiResponse<Page<SessionItem>>>(`${environment.apiUrl}/mentor/sessions?page=${page}&size=${size}`); }
  cancelSession(id: number) { return this.http.put<ApiResponse<SessionItem>>(`${environment.apiUrl}/sessions/${id}/cancel`, {}); }
  completeSession(id: number) { return this.http.put<ApiResponse<SessionItem>>(`${environment.apiUrl}/sessions/${id}/complete`, {}); }
  approveSession(id: number) { return this.http.put<ApiResponse<SessionItem>>(`${environment.apiUrl}/mentor/session/${id}/approve`, {}); }
  rejectSession(id: number) { return this.http.put<ApiResponse<SessionItem>>(`${environment.apiUrl}/mentor/session/${id}/reject`, {}); }

  requestReschedule(sessionId: number, body: { newScheduledTime: string; newDurationMinutes: number; reason: string }) { return this.http.post<ApiResponse<RescheduleItem>>(`${environment.apiUrl}/sessions/${sessionId}/reschedule-request`, body); }
  sessionReschedules(sessionId: number) { return this.http.get<ApiResponse<RescheduleItem[]>>(`${environment.apiUrl}/sessions/${sessionId}/reschedule-requests`); }
  acceptReschedule(id: number) { return this.http.put<ApiResponse<RescheduleItem>>(`${environment.apiUrl}/reschedule/${id}/accept`, {}); }
  rejectReschedule(id: number) { return this.http.put<ApiResponse<RescheduleItem>>(`${environment.apiUrl}/reschedule/${id}/reject`, {}); }

  createReview(body: { sessionId: number; rating: number; comment: string }) { return this.http.post<ApiResponse<ReviewItem>>(`${environment.apiUrl}/reviews`, body); }

  notifications() { return this.http.get<ApiResponse<NotificationItem[]>>(`${environment.apiUrl}/notifications/my`); }
  markNotificationRead(id: number) { return this.http.put<ApiResponse<void>>(`${environment.apiUrl}/notifications/${id}/read`, {}); }
  sessionAttendance(sessionId: number) { return this.http.get<ApiResponse<AttendanceItem[]>>(`${environment.apiUrl}/attendance/session/${sessionId}`); }

  sendMessage(body: { sessionId: number; messageText: string }) { return this.http.post<ApiResponse<MessageItem>>(`${environment.apiUrl}/messages/send`, body); }
  getMessages(sessionId: number) { return this.http.get<ApiResponse<MessageItem[]>>(`${environment.apiUrl}/messages/${sessionId}`); }

  pay(sessionId: number) { return this.http.post<ApiResponse<PaymentItem>>(`${environment.apiUrl}/payments/pay/${sessionId}`, {}); }
  myPayments(page = 0, size = 10) { return this.http.get<ApiResponse<Page<PaymentItem>>>(`${environment.apiUrl}/payments/my?page=${page}&size=${size}`); }

  adminUsers(page = 0, size = 10) { return this.http.get<ApiResponse<Page<unknown>>>(`${environment.apiUrl}/admin/users?page=${page}&size=${size}`); }
  disableUser(id: number) { return this.http.put<ApiResponse<unknown>>(`${environment.apiUrl}/admin/users/${id}/disable`, {}); }
  adminSessions(page = 0, size = 10) { return this.http.get<ApiResponse<Page<unknown>>>(`${environment.apiUrl}/admin/sessions?page=${page}&size=${size}`); }
  adminPayments(page = 0, size = 10) { return this.http.get<ApiResponse<Page<unknown>>>(`${environment.apiUrl}/admin/payments?page=${page}&size=${size}`); }
}
