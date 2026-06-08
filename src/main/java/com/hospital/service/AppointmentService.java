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
    @CacheEvict(value = "schedules", allEntries = true)
    public synchronized String bookAppointment(Long scheduleId, String patientEmail) {
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule slot not found"));

        if (schedule.isBooked()) {
            throw new IllegalStateException("Validation Error: This time slot is already booked!");
        }
     // <-- Add this critical missing line!
        schedule.setPatientEmail(patientEmail); 
        schedule.setBooked(true);
        scheduleRepository.save(schedule);
        sendConfirmationEmail(patientEmail, schedule.getDoctorName(), schedule.getAvailableTime());

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
        scheduleRepository.saveAll(Arrays.asList(slot1, slot2, slot3));
    }
}