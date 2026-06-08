package com.hospital.controller;

import com.hospital.ApiResponse;
import com.hospital.entity.DoctorSchedule;
import com.hospital.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patient")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/schedules")
    public ResponseEntity<List<DoctorSchedule>> viewSchedules() {
        return ResponseEntity.ok(appointmentService.getAvailableSchedules());
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/booked")
    public ResponseEntity<List<DoctorSchedule>> viewBookedSchedules() {
        return ResponseEntity.ok(appointmentService.getBookedSchedules());
    
    }

    @PostMapping("/book")
    public ResponseEntity<ApiResponse> bookAppointment(
        @RequestParam @jakarta.validation.constraints.Min(value = 1, message = "Invalid Slot ID format") Long scheduleId,
        @RequestParam @jakarta.validation.constraints.Email(message = "Invalid email address format") String email) {
        
        // No try-catch blocks needed! If this throws an error, our Global Handler catches it.
        String message = appointmentService.bookAppointment(scheduleId, email);
        return ResponseEntity.ok(new ApiResponse(message, true));
    }

    @PutMapping("/reset")
    public ResponseEntity<String> resetSchedules() {
        appointmentService.resetAllSchedules();
        return ResponseEntity.ok("All appointment slots have been successfully reset to available!");
    }
}