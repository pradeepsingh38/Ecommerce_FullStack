# Render Deployment

This repo is configured for two Render services:

- `ecommerce-backend`: Docker web service for the Spring Boot API
- `ecommerce-frontend`: Static site for the Vite React app

## Important

Do not commit real passwords, tokens, or mail app passwords. Add secrets in the Render dashboard when prompted by the Blueprint.

Render does not provide managed MySQL. Use one of these database options:

- External MySQL provider: keep `SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect`.
- Render Postgres or another PostgreSQL provider: add the PostgreSQL JDBC driver first, then set `SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect`.

## Backend Environment Variables

Set these on the backend service:

```text
SPRING_DATASOURCE_URL=jdbc:mysql://HOST:3306/DATABASE
SPRING_DATASOURCE_USERNAME=your_database_user
SPRING_DATASOURCE_PASSWORD=your_database_password
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect
JWT_SECRET=a_random_secret_at_least_32_characters
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=a_strong_admin_password
FRONTEND_BASE_URL=https://YOUR_FRONTEND.onrender.com
CORS_ALLOWED_ORIGINS=https://YOUR_FRONTEND.onrender.com
```

Mail is optional for password reset links:

```text
MAIL_USERNAME=your_email@example.com
MAIL_PASSWORD=your_mail_app_password
MAIL_FROM=your_email@example.com
```

## Frontend Environment Variables

Set this on the frontend static site:

```text
VITE_API_BASE_URL=https://YOUR_BACKEND.onrender.com/api
```

## Render Steps

1. Push this repo to GitHub.
2. In Render, create a new Blueprint and select this repo.
3. Fill in the prompted environment variables.
4. Deploy the backend first, then copy its URL into `VITE_API_BASE_URL`.
5. Deploy the frontend, then copy its URL into `FRONTEND_BASE_URL` and `CORS_ALLOWED_ORIGINS`.
6. Redeploy the backend once after setting the final frontend URL.
