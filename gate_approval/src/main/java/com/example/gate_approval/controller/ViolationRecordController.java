package com.example.gate_approval.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import com.example.gate_approval.service.ViolationRecordService;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Map;
import java.security.Principal;
import org.springframework.web.bind.annotation.*;
import com.example.gate_approval.entity.*;
import com.example.gate_approval.dto.*;

@RestController()
@RequestMapping(value = "/api/violationrecords")
public class ViolationRecordController {

    @Autowired()
    private ViolationRecordService violationRecordService;

    @PostMapping()
    public ResponseEntity<ViolationRecord> createViolationRecord(@RequestBody ViolationRecord entity) {
        return ResponseEntity.ok(violationRecordService.createViolationRecord(entity));
    }

    @GetMapping()
    public ResponseEntity<List<ViolationRecord>> getAllViolationRecords() {
        return ResponseEntity.ok(violationRecordService.getAllViolationRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViolationRecord> getViolationRecordById(@PathVariable String id) {
        return violationRecordService.getViolationRecordById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViolationRecord> updateViolationRecord(@PathVariable String id, @RequestBody ViolationRecord entity) {
        ViolationRecord updated = violationRecordService.updateViolationRecord(id, entity);
        if (updated != null)
            return ResponseEntity.ok(updated);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteViolationRecord(@PathVariable String id) {
        violationRecordService.deleteViolationRecord(id);
        return ResponseEntity.noContent().build();
    }

    /*
 * Operation    : View Student Violation History
 * Usecase ID   : UC-003
 * Usecase Name : View Student Violation History
 */
    @PostMapping(value = "/students/violations")
    @PreAuthorize("hasRole('SECURITY_OFFICER')")
    public ResponseEntity<List<ViolationRecordResponse>> viewStudentViolationHistory(@RequestBody ViewStudentViolationHistoryRequest request, Principal principal) {
        return ResponseEntity.ok(violationRecordService.viewStudentViolationHistory(request, principal));
    }

    /*
 * Operation    : Generate Daily Violation Report
 * Usecase ID   : UC-004
 * Usecase Name : Generate Daily Violation Report
 */
    @PostMapping(value = "/report/daily")
    @PreAuthorize("hasRole('SECURITY_OFFICER')")
    public ResponseEntity<ViolationReportResponse> generateDailyReport(@RequestBody GenerateDailyViolationReportRequest request) {
        return ResponseEntity.ok(violationRecordService.generateDailyReport(request));
    }
}
