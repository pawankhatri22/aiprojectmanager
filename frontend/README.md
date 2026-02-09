# Angular Frontend (Integrated)

This is a full Angular 17 app integrated with the Spring Boot backend APIs.

## Covered integrations
- Auth: register/login
- Mentor listing + detail
- Graduate session list + cancel
- Session request flow
- Mentor approve/reject dashboard
- Messaging (send + load by session)
- Payments (service integrated)
- Admin users/sessions/payments + disable user

## Run
```bash
cd frontend
npm install
npm start
```

Backend API base URL is configured in:
- `src/environments/environment.ts`
