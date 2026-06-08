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
        // Automatically inject sample entries only if your tables have been wiped completely clean
        if (scheduleRepository.count() == 0) {
            DoctorSchedule slot1 = new DoctorSchedule(1L, "Dr. Ramesh Kumar", "10:00 AM - 10:30 AM", false);
            DoctorSchedule slot2 = new DoctorSchedule(2L, "Dr. Sneha Sharma", "11:30 AM - 12:00 PM", false);
            DoctorSchedule slot3 = new DoctorSchedule(3L, "Dr. Ramcar Kumar", "02:00 PM - 02:30 PM", false);

            scheduleRepository.saveAll(Arrays.asList(slot1, slot2, slot3));
            System.out.println("🚀 System Baseline Check: Fresh database records initialized successfully!");
        }
    }
}