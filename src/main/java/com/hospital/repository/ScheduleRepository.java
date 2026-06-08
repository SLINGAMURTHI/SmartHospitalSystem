package com.hospital.repository;

import com.hospital.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    List<DoctorSchedule> findByIsBookedTrue();
    List<DoctorSchedule> findByIsBookedFalse();

    @Modifying
    @Query(value = "TRUNCATE TABLE doctor_schedules", nativeQuery = true)
    void truncateTable();
}