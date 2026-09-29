package com.example.gate_approval.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import com.example.gate_approval.service.RFIDScanService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.example.gate_approval.entity.*;
import com.example.gate_approval.dto.*;

@RestController()
@RequestMapping(value = "/api/rfidscans")
public class RFIDScanController {

    @Autowired()
    private RFIDScanService rFIDScanService;

    @PostMapping()
    public ResponseEntity<RFIDScan> createRFIDScan(@RequestBody RFIDScan entity) {
        return ResponseEntity.ok(rFIDScanService.createRFIDScan(entity));
    }

    @GetMapping()
    public ResponseEntity<List<RFIDScan>> getAllRFIDScans() {
        return ResponseEntity.ok(rFIDScanService.getAllRFIDScans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RFIDScan> getRFIDScanById(@PathVariable String id) {
        return rFIDScanService.getRFIDScanById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RFIDScan> updateRFIDScan(@PathVariable String id, @RequestBody RFIDScan entity) {
        RFIDScan updated = rFIDScanService.updateRFIDScan(id, entity);
        if (updated != null)
            return ResponseEntity.ok(updated);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRFIDScan(@PathVariable String id) {
        rFIDScanService.deleteRFIDScan(id);
        return ResponseEntity.noContent().build();
    }

    /*
 * Operation    : Record Student Entry Scan
 * Usecase ID   : UC-001
 * Usecase Name : Record Student Entry Scan
 */
    @PostMapping(value = "/scans")
    public ResponseEntity<RecordStudentEntryScanResponse> recordStudentEntryScan(@RequestBody RecordStudentEntryScanRequest request) {
        return ResponseEntity.ok(rFIDScanService.recordStudentEntryScan(request));
    }
}
