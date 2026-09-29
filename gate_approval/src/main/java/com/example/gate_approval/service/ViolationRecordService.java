package com.example.gate_approval.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import com.example.gate_approval.repository.ViolationRecordRepository;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import com.example.gate_approval.repository.StudentRepository;
import com.example.gate_approval.entity.*;
import com.example.gate_approval.dto.*;
import java.time.*;
import java.util.stream.Collectors;

@Service()
public class ViolationRecordService {

    @Autowired()
    private ViolationRecordRepository violationRecordRepository;

    public ViolationRecord createViolationRecord(ViolationRecord entity) {
        return violationRecordRepository.save(entity);
    }

    public List<ViolationRecord> getAllViolationRecords() {
        return violationRecordRepository.findAll();
    }

    public Optional<ViolationRecord> getViolationRecordById(String id) {
        return violationRecordRepository.findById(id);
    }

    public ViolationRecord updateViolationRecord(String id, ViolationRecord entity) {
        if (violationRecordRepository.existsById(id)) {
            entity.setId(id);
            return violationRecordRepository.save(entity);
        }
        return null;
    }

    public void deleteViolationRecord(String id) {
        violationRecordRepository.deleteById(id);
    }

    @Autowired()
    private StudentRepository studentRepository;

    /*
 * Operation    : View Student Violation History
 * Usecase ID   : UC-003
 * Usecase Name : View Student Violation History
 */
    public List<ViolationRecordResponse> viewStudentViolationHistory(ViewStudentViolationHistoryRequest request, Principal principal) {
        /*
 TODO: Body could not be parsed automatically. Raw logic:*/
String roll = request.getRollNumber();
String rfid = request.getRfidCardNumber();

Student student;
if (roll != null && !roll.isEmpty()) {
    student = studentRepository.findByRollNumberOrRfidCardNumber(roll, null)
        .orElseThrow(() -> new RuntimeException("Student not found"));
} else if (rfid != null && !rfid.isEmpty()) {
    student = studentRepository.findByRollNumberOrRfidCardNumber(null, rfid)
        .orElseThrow(() -> new RuntimeException("Student not found"));
} else {
    throw new IllegalArgumentException("Either rollNumber or rfidCardNumber must be provided");
}

List<ViolationRecord> records = violationRecordRepository.findByStudentId(student.getId());

return records.stream()
    .map(v -> new ViolationRecordResponse(v.getId(), v.getViolationTime(), v.getStatus()))
    .collect(Collectors.toList()); // Collectors import needed
    }

    /*
 * Operation    : Generate Daily Violation Report
 * Usecase ID   : UC-004
 * Usecase Name : Generate Daily Violation Report
 */
    public ViolationReportResponse generateDailyReport(GenerateDailyViolationReportRequest request) {
        LocalDate date = request.getDate();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        List<ViolationRecord> records = violationRecordRepository.findByViolationTimeBetween(start, end);
        int totalViolations = records.size();
        int distinctStudents = (int) records.stream().map(ViolationRecord::getStudent).filter(Objects::nonNull).map(Student::getId).distinct().count();
        return new ViolationReportResponse(totalViolations, distinctStudents);
    }
}
