# AI-Powered Rugby Diet & Fitness Planner
**UTM Pirates — Universiti Teknologi Malaysia**
Author: Muhammad Danial Syafiq Bin Ermiza

---

## Tech Stack

| Layer     | Technology                              |
|-----------|-----------------------------------------|
| Frontend  | Vue 3, Vite, Pinia, Vue Router, Axios, Vuelidate |
| Backend   | Spring Boot 3.2, Spring Security, JWT   |
| Database  | MongoDB 6+                              |
| Language  | Java 17 (backend), JavaScript (frontend)|

---

## Prerequisites

| Tool        | Version  | Download |
|-------------|----------|----------|
| Java JDK    | 17+      | https://adoptium.net |
| Maven       | 3.8+     | https://maven.apache.org |
| MongoDB     | 6+       | https://www.mongodb.com/try/download/community |
| Node.js     | 18+      | https://nodejs.org |
| npm         | 9+       | Bundled with Node.js |

---

## Project Structure

```
rugby-planner/
├── backend/                         ← Spring Boot (Java)
│   ├── pom.xml
│   └── src/main/java/com/utm/rugbyplanner/
│       ├── RugbyPlannerApplication.java
│       ├── config/
│       │   └── SecurityConfig.java
│       ├── controller/
│       │   └── AuthController.java
│       ├── dto/
│       │   ├── ApiResponse.java
│       │   ├── LoginRequest.java
│       │   ├── LoginResponse.java
│       │   ├── RegisterRequest.java
│       │   └── RegisterResponse.java
│       ├── exception/
│       │   ├── DuplicateResourceException.java
│       │   ├── GlobalExceptionHandler.java
│       │   └── ValidationException.java
│       ├── model/
│       │   ├── Athlete.java
│       │   ├── Trainer.java
│       │   └── User.java
│       ├── repository/
│       │   ├── AthleteRepository.java
│       │   ├── TrainerRepository.java
│       │   └── UserRepository.java
│       ├── security/
│       │   ├── JwtAuthenticationFilter.java
│       │   ├── JwtUtils.java
│       │   └── UserDetailsServiceImpl.java
│       └── service/
│           ├── AuthService.java
│           └── RegisterService.java
│
└── frontend/                        ← Vue 3 (JavaScript)
    ├── index.html
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── App.vue
        ├── main.js
        ├── assets/
        │   └── main.css
        ├── layouts/
        │   ├── AppLayout.vue        ← sidebar + header (authenticated pages)
        │   └── AuthLayout.vue       ← full-screen (login / register)
        ├── router/
        │   └── index.js             ← routes + navigation guards
        ├── services/
        │   ├── authService.js       ← axios calls to /api/auth/*
        │   └── http.js              ← axios instance + JWT interceptor
        ├── stores/
        │   └── auth.js              ← Pinia auth store (login, register, logout)
        └── views/
            ├── auth/
            │   ├── LoginView.vue    ← UC001
            │   └── RegisterView.vue ← UC002 (3-step form)
            ├── DashboardView.vue
            ├── TrainerView.vue
            ├── MealPlannerView.vue
            ├── WorkoutView.vue
            ├── ChatbotView.vue
            ├── AppointmentView.vue
            ├── ProfileView.vue
            └── NotFoundView.vue
```

---

## Setup & Run

### Step 1 — Start MongoDB

```bash
# macOS / Linux
mongod --dbpath /data/db

# Windows
mongod --dbpath C:\data\db

# Or using MongoDB Compass — just connect to mongodb://localhost:27017
```

The database `rugby_planner_db` and collections (`users`, `athletes`, `trainers`)
are created automatically on first use.

### Step 2 — Run Spring Boot backend

```bash
cd backend
mvn spring-boot:run
```

Backend starts on **http://localhost:8080**

### Step 3 — Run Vue frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend starts on **http://localhost:5173**

Open your browser to **http://localhost:5173** — you will see the login page.

---

## API Endpoints (UC001 + UC002)

### UC002: Register
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "fullName":        "Muhammad Danial Syafiq",
  "email":           "danial@utm.my",
  "phoneNumber":     "+60123456789",
  "username":        "danial",
  "password":        "Password123",
  "confirmPassword": "Password123",
  "userRole":        "ATHLETE"
}
```

**Success (201):**
```json
{
  "success": true,
  "message": "Account created successfully! You can now sign in.",
  "data": {
    "userId":   "6637abc123...",
    "username": "danial",
    "fullName": "Muhammad Danial Syafiq",
    "email":    "danial@utm.my",
    "userRole": "ATHLETE"
  }
}
```

### UC001: Login
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "danial",
  "password": "Password123"
}
```

**Success (200):**
```json
{
  "success": true,
  "message": "Login successful. Welcome back!",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType":   "Bearer",
    "userId":      "6637abc123...",
    "username":    "danial",
    "fullName":    "Muhammad Danial Syafiq",
    "email":       "danial@utm.my",
    "userRole":    "ATHLETE"
  }
}
```

### Token verify (Vue router guard)
```
GET http://localhost:8080/api/auth/verify
Authorization: Bearer <token>
```

### Logout
```
POST http://localhost:8080/api/auth/logout
Authorization: Bearer <token>
```

---

## MongoDB Collections

After registering one ATHLETE and one TRAINER, MongoDB will contain:

**users** collection:
```json
{
  "_id": "ObjectId(...)",
  "username": "danial",
  "email": "danial@utm.my",
  "password": "$2a$12$...",
  "fullName": "Muhammad Danial Syafiq",
  "phoneNumber": "+60123456789",
  "userRole": "ATHLETE",
  "enabled": true,
  "createdAt": "2025-07-15T08:00:00",
  "updatedAt": "2025-07-15T08:00:00"
}
```

**athletes** collection (blank profile, filled by UC003):
```json
{
  "_id": "ObjectId(...)",
  "userId": "ObjectId(same as user._id)",
  "weight": null,
  "height": null,
  "rugbyPosition": null,
  "createdAt": "2025-07-15T08:00:00"
}
```

**trainers** collection (blank profile, filled by UC003):
```json
{
  "_id": "ObjectId(...)",
  "userId": "ObjectId(same as user._id)",
  "expertise": null,
  "certifications": null,
  "createdAt": "2025-07-15T08:00:00"
}
```

---

## Error Responses

| HTTP | Scenario | UC |
|------|----------|----|
| 400  | Password mismatch | UC002 AF1 |
| 401  | Wrong username/password | UC001 AF1 |
| 401  | Account disabled | UC001 |
| 409  | Email or username already taken | UC002 AF2 |
| 422  | Missing required fields (@Valid) | UC001/UC002 AF1 |
| 500  | Server error | — |

All errors follow the same shape:
```json
{ "success": false, "message": "Error description here" }
```

---

## Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
# MongoDB connection
spring.data.mongodb.uri=mongodb://localhost:27017/rugby_planner_db

# JWT — change this in production!
app.jwt.secret=RugbyPlannerUTMPiratesSecretKey2025SuperSecureBase64EncodedStringXYZ
app.jwt.expiration-ms=86400000   # 24 hours

# CORS — add your production domain here
app.cors.allowed-origins=http://localhost:5173,https://yourdomain.com
```
