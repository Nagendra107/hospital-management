# 🏥 MediCare Pro — Hospital Management System

A full-stack Hospital Management System built with **Spring Boot (Java)** backend and a pure **HTML/CSS/JavaScript** frontend.

---

## 🏗️ Tech Stack

| Layer     | Technology                                     |
|-----------|------------------------------------------------|
| Backend   | Java 17, Spring Boot 3.2, Spring Security      |
| Auth      | JWT (JSON Web Tokens)                          |
| Database  | H2 (dev) / MySQL (production)                  |
| ORM       | Spring Data JPA + Hibernate                    |
| Frontend  | HTML5, CSS3, Vanilla JavaScript                |
| API Style | RESTful JSON API                               |

---

## 📁 Project Structure

```
hospital-management/
├── backend/                          # Spring Boot application
│   ├── pom.xml
│   └── src/main/java/com/hospital/
│       ├── HospitalManagementApplication.java
│       ├── config/
│       │   ├── SecurityConfig.java
│       │   └── DataSeeder.java          # Seeds sample data on startup
│       ├── controller/
│       │   ├── AuthController.java
│       │   ├── PatientController.java
│       │   ├── DoctorController.java
│       │   ├── AppointmentController.java
│       │   ├── BillController.java
│       │   └── DashboardController.java
│       ├── model/
│       │   ├── Patient.java
│       │   ├── Doctor.java
│       │   ├── Appointment.java
│       │   ├── Bill.java
│       │   └── User.java
│       ├── repository/           # Spring Data JPA Repositories
│       ├── service/              # Business logic layer
│       └── security/
│           ├── JwtUtils.java
│           └── JwtAuthFilter.java
│
└── frontend/                         # Static HTML frontend
    ├── login.html                    # Login page
    ├── index.html                    # Dashboard
    ├── patients.html                 # Patient management
    ├── doctors.html                  # Doctor management
    ├── appointments.html             # Appointment scheduling
    ├── billing.html                  # Billing & payments
    ├── css/
    │   └── style.css                 # Complete styles
    └── js/
        └── api.js                    # API client + utilities
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+ (JDK)
- Maven 3.6+
- (Optional) MySQL 8 for production

### Step 1 — Run the Backend

```bash
cd backend
mvn spring-boot:run
```

The server will start on **http://localhost:8080/api**

You'll see:
```
========================================
  Hospital Management System Started!
  API Base URL: http://localhost:8080/api
  H2 Console:   http://localhost:8080/api/h2-console
========================================
```

Sample data is automatically seeded on first run!

### Step 2 — Open the Frontend

Open `frontend/login.html` in your browser.

> ⚠️ **Important:** Use a local server for the frontend (not `file://`), so CORS works correctly:

```bash
# Python
cd frontend && python3 -m http.server 3000

# Or Node.js
cd frontend && npx serve .
```

Then visit: **http://localhost:3000/login.html**

### Step 3 — Login

| Role        | Username    | Password    |
|-------------|-------------|-------------|
| Admin       | admin       | admin123    |
| Doctor      | drpatel     | doctor123   |
| Receptionist| reception   | staff123    |
| Nurse       | nurse01     | nurse123    |

---

## 📡 API Endpoints

### Authentication
```
POST /api/auth/login          Login (returns JWT)
POST /api/auth/register       Register user
GET  /api/auth/me             Get current user
```

### Patients
```
GET    /api/patients           List all patients
POST   /api/patients           Create patient
GET    /api/patients/{id}      Get patient
PUT    /api/patients/{id}      Update patient
DELETE /api/patients/{id}      Delete patient
GET    /api/patients/search?q= Search patients
POST   /api/patients/{id}/admit    Admit patient
POST   /api/patients/{id}/discharge Discharge patient
GET    /api/patients/stats     Patient statistics
```

### Doctors
```
GET    /api/doctors            List all doctors
POST   /api/doctors            Create doctor
GET    /api/doctors/{id}       Get doctor
PUT    /api/doctors/{id}       Update doctor
DELETE /api/doctors/{id}       Delete doctor
GET    /api/doctors/search?q=  Search doctors
GET    /api/doctors/active     Active doctors
```

### Appointments
```
GET    /api/appointments        List all appointments
POST   /api/appointments        Create appointment
PUT    /api/appointments/{id}   Update appointment
DELETE /api/appointments/{id}   Delete appointment
GET    /api/appointments/today  Today's appointments
PATCH  /api/appointments/{id}/status  Update status
```

### Bills
```
GET    /api/bills               List all bills
POST   /api/bills               Create bill (auto-calculates GST)
PUT    /api/bills/{id}          Update bill
DELETE /api/bills/{id}          Delete bill
POST   /api/bills/{id}/pay      Process payment
GET    /api/bills/stats         Revenue stats
```

### Dashboard
```
GET    /api/dashboard/stats     All dashboard statistics
```

---

## 🗄️ Database

### Development (H2 In-Memory)
- **Console:** http://localhost:8080/api/h2-console
- **URL:** `jdbc:h2:mem:hospitaldb`
- **User:** `sa` | **Password:** `password`

### Production (MySQL)
Uncomment the MySQL config in `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hospitaldb
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

---

## ✨ Features

- 🔐 **JWT Authentication** — Secure login with role-based access
- 🧑‍⚕️ **Patient Management** — Register, search, admit, discharge patients
- 👨‍⚕️ **Doctor Management** — Doctor profiles, specializations, schedules
- 📅 **Appointment Scheduling** — Book, confirm, cancel appointments
- 🧾 **Billing System** — Generate bills, auto-calculate 5% GST, process payments
- 📊 **Dashboard** — Real-time stats, recent patients, today's appointments
- 🔍 **Search** — Full-text search across patients and doctors
- 🌱 **Auto Seed Data** — Demo data loaded on startup

---

## 🔧 Configuration

All configuration in `backend/src/main/resources/application.properties`:

```properties
# Change JWT secret for production!
jwt.secret=YourSuperSecureSecretKey

# Change expiration (milliseconds)
jwt.expiration=86400000   # 24 hours
```

---

## 🏗️ Extending the System

### Add a new module (e.g. Lab):
1. Create `LabTest.java` model in `model/`
2. Create `LabTestRepository.java` in `repository/`
3. Create `LabTestService.java` in `service/`
4. Create `LabTestController.java` in `controller/`
5. Add `lab.html` in the frontend

### Add a new user role:
1. Add role to `User.Role` enum
2. Add `@PreAuthorize("hasRole('YOUR_ROLE')")` on controller methods

---

## 📝 License

MIT License — Free to use and modify for educational/commercial purposes.
