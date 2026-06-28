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
            @RequestParam Long scheduleId,
            @RequestParam String email,
            @RequestParam String illness
    ) {
        try {
            String message = appointmentService.bookAppointment(scheduleId, email, illness);
            return ResponseEntity.ok(new ApiResponse(message, true));
        } catch (IllegalArgumentException | IllegalStateException e) {
            // 🌟 Catches our specific restriction messages and passes them gracefully down to frontend
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse("An unexpected error occurred.", false));
        }
    }

    @PutMapping("/reset")
    public ResponseEntity<String> resetSchedules() {
        appointmentService.resetAllSchedules();
        return ResponseEntity.ok("All appointment slots have been successfully reset to available!");
    }
}