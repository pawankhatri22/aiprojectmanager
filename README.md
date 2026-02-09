# AI Project Manager MVP

Runnable Spring Boot MVP implementing:
- Authentication with JWT (`/api/auth/register`, `/api/auth/login`)
- Mentor discovery (`/api/mentors`)
- Session booking workflow (`/api/sessions/*`, `/api/mentor/session/*`)
- Messaging (`/api/messages/*`)
- Simulated payments (`/api/payments/*`)
- Admin oversight (`/api/admin/*`)

## Run
```bash
mvn spring-boot:run
```

Default seeded admin:
- email: `admin@local.com`
- password: `admin123`

H2 console: `/h2-console`
