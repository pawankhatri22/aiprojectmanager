export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

export interface AuthResponse { token: string; email: string; role: 'ADMIN' | 'MENTOR' | 'GRADUATE'; }
export interface MentorProfile { id: number; userId: number; fullName: string; company: string; jobTitle: string; yearsExperience: number; bio: string; hourlyRate: number; ratingAverage: number; photoUrl: string; expertise: string[]; }
export interface GraduateProfile { id: number; userId: number; fullName: string; education: string; careerGoal: string; resumeUrl: string; skills: string[]; }
export interface SessionItem { id: number; mentorId: number; graduateId: number; scheduledTime: string; durationMinutes: number; price: number; status: string; }
export interface MessageItem { id: number; sessionId: number; senderId: number; messageText: string; timestamp: string; }
export interface PaymentItem { id: number; sessionId: number; amount: number; currency: string; status: string; transactionReference: string; timestamp: string; }
export interface Page<T> { content: T[]; totalElements: number; totalPages: number; number: number; size: number; }
