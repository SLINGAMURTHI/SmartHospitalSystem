# CarePlus Hospital - Smart Patient Appointment Booking Portal

CarePlus is a high-performance, full-stack hospital management web application designed to streamline doctor schedule tracking and patient appointment bookings. The platform features an automated scheduling engine, client-side real-time data filters, live analytical metric tracking, and multi-tier business validation logic.

## 🚀 Key Features

* **Smart Patient Booking Frontend:** Clean, highly interactive portal featuring responsive controls, dynamic processing state feedback, and input sanitation.
* **Real-Time Data Filtering Grid:** High-performance, client-side filtering engine on both the Patient Portal and Admin Dashboard for instant lookup across names, emails, and medical specialties.
* **Enterprise Business Constraint Engine:** Bulletproof backend validation rules matching slot assignments to appropriate medical specialties (e.g., Oncologists, Cardiologists, Neurologists).
* **Live Aggregated Metrics Badge:** Live data state analysis tracking system loads and highlighting active booked records on an interactive tracking pill badge.
* **Production-Ready Containerization:** Fully containerized setup leveraging multi-stage Docker configurations for streamlined deployment.

---

## 🛠️ Technical Architecture & Stack

### Frontend Layer
* **HTML5 / CSS3:** Premium, custom component styles tailored with clean letter-spacing, semantic cards, and responsive data tables.
* **JavaScript (ES6+):** Pure asynchronous AJAX execution mapping state handling, handling race conditions, and real-time DOM filtering manipulation.

### Backend Layer
* **Java 17 (LTS) & Spring Boot:** High-throughput REST API controller patterns utilizing Spring Web routing.
* **Spring Data JPA / Hibernate:** Relational persistence framework abstraction handles ORM mapping and safe object mutations.
* **MySQL:** Reliable ACID-compliant relational data schema maps persistent schedule and booking states.

---

## 🏗️ Backend System Business Logic Blueprint

The core scheduling engine implements transactional state lock structures (`synchronized`) inside `AppointmentService.java` to avoid booking collisions, alongside strict categorical validation constraints:

```java
if (doctorName.contains("Ramesh Kumar") || doctorName.contains("Rajesh Joshi")) {
    if (!"Cancer".equalsIgnoreCase(illness)) {
        throw new IllegalArgumentException("Specialty Mismatch: Oncologists only accept Cancer Consultations.");
    }
} else if (doctorName.contains("Vikram Malhotra")) {
    if (!"Brain Injury / Nerve Pain".equalsIgnoreCase(illness)) {
        throw new IllegalArgumentException("Specialty Mismatch: Neurologists only accept Brain Specialist consultations.");
    }
}
