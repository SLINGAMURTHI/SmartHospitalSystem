package com.hospital.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "doctor_schedules")
public class DoctorSchedule implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "doctor_name")
    private String doctorName;
    
    @Column(name = "available_time")
    private String availableTime;
    
    @Column(name = "is_booked")
    private boolean isBooked;

    @Column(name = "patient_email")
    private String patientEmail;
    
    @Column(name = "patient_illness")
    private String patientIllness;
    
    // Default Constructor
    public DoctorSchedule() {}

    // Parameterized Constructor
    public DoctorSchedule(Long id, String doctorName, String availableTime, boolean isBooked) {
        this.id = id;
        this.doctorName = doctorName;
        this.availableTime = availableTime;
        this.isBooked = isBooked;
    }
    
    // --- GETTERS AND SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getAvailableTime() { return availableTime; }
    public void setAvailableTime(String availableTime) { this.availableTime = availableTime; }

    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean booked) { this.isBooked = booked; }

    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }

    // 🌟 Newly integrated illness fields mapping methods
    public String getPatientIllness() { return patientIllness; }
    public void setPatientIllness(String patientIllness) { this.patientIllness = patientIllness; }
}