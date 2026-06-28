package com.hospital;

import com.hospital.entity.DoctorSchedule;
import com.hospital.repository.ScheduleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ScheduleRepository scheduleRepository;

    public DataInitializer(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔄 Clearing old database schedules for fresh initialization...");
        scheduleRepository.deleteAll();

        System.out.println("🚀 Seeding 8 distinct doctor schedule slots into MySQL database...");
        
        DoctorSchedule slot1 = new DoctorSchedule(1L, "Dr. Ramesh Kumar", "10:00 AM - 10:30 AM", false);
        DoctorSchedule slot2 = new DoctorSchedule(2L, "Dr. Sneha Sharma", "11:30 AM - 12:00 PM", false);
        DoctorSchedule slot3 = new DoctorSchedule(3L, "Dr. Ramcar Kumar", "02:00 PM - 02:30 PM", false);
        
        // 🌟 ADDED 5 MORE DISTINCT DOCTORS AND TIME WINDOWS TO TOTAL 8 SLOTS
        DoctorSchedule slot4 = new DoctorSchedule(4L, "Dr. Amit Mishra", "09:00 AM - 09:30 AM", false);
        DoctorSchedule slot5 = new DoctorSchedule(5L, "Dr. Priya Patel", "03:10 PM - 03:40 PM", false);
        DoctorSchedule slot6 = new DoctorSchedule(6L, "Dr. Vikram Malhotra", "04:00 PM - 04:30 PM", false);
        DoctorSchedule slot7 = new DoctorSchedule(7L, "Dr. Ananya Reddy", "10:45 AM - 11:15 AM", false);
        DoctorSchedule slot8 = new DoctorSchedule(8L, "Dr. Rajesh Joshi", "01:15 PM - 01:45 PM", false);

        scheduleRepository.saveAll(Arrays.asList(slot1, slot2, slot3, slot4, slot5, slot6, slot7, slot8));
        
        System.out.println("🚀 System Baseline Check: Fresh database records initialized successfully!");
    }
}