# 🏥 SmartHospitalSystem: Full-Stack Enterprise Portal

An automated, modern, full-stack Hospital Management System designed to handle concurrent patient appointment scheduling with high-performance architectures. 

## 🏗️ System Architecture & Tech Stack
* **Frontend:** Single Page Application (SPA) built using HTML5, modern Tailwind CSS, and asynchronous JavaScript (Fetch API).
* **Backend Framework:** Java 17 / Spring Boot with Spring Security, Spring Data JPA, and Hibernate ORM.
* **In-Memory Cache Layer:** Redis 7.0 for low-latency database offloading using the Cache-Aside pattern.
* **Relational Database:** MySQL 8.0 handling persistent storage records.
* **Container Orchestration:** Multi-container runtime isolation managed via Docker & Docker Compose.

## 🚀 Key Engineering Features
* **Cache-Aside & Eviction Strategy:** Read requests query the Redis container memory layer first. Upon scheduling a slot via a `POST` request, custom cache eviction mechanisms (`@CacheEvict`) automatically purge stale entries to guarantee data consistency.
* **Concurrency Race-Condition Protections:** Leverages synchronized transaction boundaries to eliminate double-booking or over-scheduling anomalies during simultaneous peak user traffic.
* **Isolated Networking Mesh:** Services are containerized and connected over an isolated Docker network bridge, redirecting external host collision conflicts seamlessly.

## 🛠️ How to Run Locally

1. Clone the repository:
```bash
git clone [https://github.com/SLINGAMURTHI/SmartHospitalSystem.git](https://github.com/SLINGAMURTHI/SmartHospitalSystem.git)
cd SmartHospitalSystem

docker-compose up --build
