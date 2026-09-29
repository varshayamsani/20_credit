package com.example.gate_approval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import org.springframework.stereotype.Repository;
import com.example.gate_approval.entity.*;
import java.time.LocalDateTime;
@Repository()
public interface ViolationRecordRepository extends JpaRepository<ViolationRecord, String> {

    // repo_method_id: find_violation_by_student | Retrieve all violation records for the given student.
    List<ViolationRecord> findByStudentId(String studentId);

    // repo_method_id: find_violation_records_by_date
    List<ViolationRecord> findByViolationTimeBetween(LocalDateTime start, LocalDateTime end);
}
