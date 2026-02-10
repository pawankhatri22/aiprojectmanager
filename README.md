# AI Project Manager MVP

Runnable Spring Boot MVP implementing:
- Authentication with JWT (`/api/auth/register`, `/api/auth/login`)
- Mentor discovery (`/api/mentors`)
- Session booking workflow (`/api/sessions/*`, `/api/mentor/session/*`)
- Messaging (`/api/messages/*`)
- Simulated payments (`/api/payments/*`)
- Reviews (`/api/reviews/*`)
- Notifications (`/api/notifications/*`)
- Reschedule flow (`/api/sessions/{id}/reschedule-request`, `/api/reschedule/*`)
- Attendance/webhook tracking (`/api/webhooks/meeting/attendance`, `/api/attendance/*`)
- Admin oversight (`/api/admin/*`)

## Run
```bash
mvn spring-boot:run
```

## Seeded users
- Admin: `admin@local.com` / `admin123`
- Mentor: `khatri.pawankumar22@gmail.com` / `Mentor@123`
- Student: `khatri.catten22@gmail.com` / `Student@123`

## Meeting integration (Zoom)
Set env vars:
- `ZOOM_JWT_TOKEN`
- `ZOOM_USER_ID` (default `me`)
- `ZOOM_API_BASE_URL` (default `https://api.zoom.us/v2`)

H2 console: `/h2-console`
