package com.hospital.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.entity.DoctorSchedule;
import com.hospital.repository.ScheduleRepository;

@Service
public class AppointmentService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Cacheable(value = "schedules")
    public List<DoctorSchedule> getAvailableSchedules(){
        System.out.println("Fetching fresh schedules from MySQL Database...");
        return scheduleRepository.findByIsBookedFalse();
    }

    @Transactional
    public synchronized String bookAppointment(Long scheduleId, String patientEmail, String illness) {
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule slot not found"));

        if (schedule.isBooked()) {
            throw new IllegalStateException("Validation Error: This time slot is already booked!");
        }

        // 🌟 1. Specialty Restriction Validation Logic
        String doctorName = schedule.getDoctorName();

        if (doctorName.contains("Ramesh Kumar") || doctorName.contains("Rajesh Joshi")) {
            if (!"Cancer".equalsIgnoreCase(illness)) {
                throw new IllegalArgumentException("Specialty Mismatch: Oncologists only accept Cancer Consultations.");
            }
        } 
        else if (doctorName.contains("Sneha Sharma") || doctorName.contains("Ananya Reddy")) {
            if ("Cancer".equalsIgnoreCase(illness)) {
                throw new IllegalArgumentException("Specialty Mismatch: General Physicians cannot accept Cancer Consultations.");
            }
        }
        else if (doctorName.contains("Ramcar Kumar")) {
            if (!"Kids Fever".equalsIgnoreCase(illness)) {
                throw new IllegalArgumentException("Specialty Mismatch: Pediatricians only accept Kids Fever / Cold cases.");
            }
        }
        else if (doctorName.contains("Amit Mishra")) {
            if (!"Heart Checkup".equalsIgnoreCase(illness)) {
                throw new IllegalArgumentException("Specialty Mismatch: Cardiologists only accept Heart Checkup cases.");
            }
        }
        else if (doctorName.contains("Priya Patel")) {
            if (!"Skin Rash".equalsIgnoreCase(illness)) {
                throw new IllegalArgumentException("Specialty Mismatch: Dermatologists only accept Skin Rash consults.");
            }
        }
        else if (doctorName.contains("Vikram Malhotra")) {
            if (!"Brain Injury / Nerve Pain".equalsIgnoreCase(illness)) {
                throw new IllegalArgumentException("Specialty Mismatch: Neurologists only accept Brain Specialist consultations.");
            }
        }

        // 🌟 2. Database persistence block (Crucial so it doesn't cut off!)
        schedule.setPatientEmail(patientEmail);
        schedule.setPatientIllness(illness);
        schedule.setBooked(true);
        scheduleRepository.save(schedule);

        // 🌟 3. Automated background email dispatcher runner
        try {
            sendConfirmationEmail(patientEmail, schedule.getDoctorName(), schedule.getAvailableTime());
        } catch(Exception e) {
            System.err.println("Email fail skipped: " + e.getMessage());
        }

        // 🌟 4. Required Return Statement
        return "Appointment booked successfully with " + schedule.getDoctorName();
    }

    private void sendConfirmationEmail(String toEmail, String doctorName, String time) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Appointment Confirmed - CarePlus Hospital");
            message.setText("Dear Patient,\n\nYour appointment with " + doctorName + " at " + time + " has been confirmed.");
//           mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Email fail skipped: " + e.getMessage());
        }
    }
    public List<DoctorSchedule> getBookedSchedules() {
        return scheduleRepository.findByIsBookedTrue();
    }
    
    @Transactional
    @CacheEvict(value = "schedules", allEntries = true)
    public synchronized void resetAllSchedules() {
        // Fast truncation clears records and snaps the sequence right back to 1
        scheduleRepository.truncateTable(); 
        
        DoctorSchedule slot1 = new DoctorSchedule(1L, "Dr. Ramesh Kumar", "10:00 AM - 10:30 AM", false);
        DoctorSchedule slot2 = new DoctorSchedule(2L, "Dr. Sneha Sharma", "11:30 AM - 12:00 PM", false);
        DoctorSchedule slot3 = new DoctorSchedule(3L, "Dr. Ramcar Kumar", "02:00 PM - 02:30 PM", false);
        DoctorSchedule slot4 = new DoctorSchedule(4L, "Dr. Amit Mishra", "09:00 AM - 09:30 AM", false);
        DoctorSchedule slot5 = new DoctorSchedule(5L, "Dr. Priya Patel", "03:10 PM - 03:40 PM", false);
        DoctorSchedule slot6 = new DoctorSchedule(6L, "Dr. Vikram Malhotra", "04:00 PM - 04:30 PM", false);
        DoctorSchedule slot7 = new DoctorSchedule(7L, "Dr. Ananya Reddy", "10:45 AM - 11:15 AM", false);
        DoctorSchedule slot8 = new DoctorSchedule(8L, "Dr. Rajesh Joshi", "01:15 PM - 01:45 PM", false);

        scheduleRepository.saveAll(Arrays.asList(slot1, slot2, slot3, slot4, slot5, slot6, slot7, slot8));
    }
}