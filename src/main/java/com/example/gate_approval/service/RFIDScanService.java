package com.example.gate_approval.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import com.example.gate_approval.repository.RFIDScanRepository;
import com.example.gate_approval.entity.*;
import com.example.gate_approval.dto.*;
import java.time.*;
import org.springframework.http.ResponseEntity;
import com.example.gate_approval.repository.StudentRepository;
import com.example.gate_approval.repository.CurfewConfigurationRepository;
import com.example.gate_approval.repository.ViolationRecordRepository;
@Service()
public class RFIDScanService {

    @Autowired()
    private RFIDScanRepository rfidScanRepository;
    @Autowired
    private ViolationRecordRepository violationRecordRepository;

    public RFIDScan createRFIDScan(RFIDScan entity) {
        return rfidScanRepository.save(entity);
    }

    public List<RFIDScan> getAllRFIDScans() {
        return rfidScanRepository.findAll();
    }

    public Optional<RFIDScan> getRFIDScanById(String id) {
        return rfidScanRepository.findById(id);
    }

    public RFIDScan updateRFIDScan(String id, RFIDScan entity) {
        if (rfidScanRepository.existsById(id)) {
            entity.setId(id);
            return rfidScanRepository.save(entity);
        }
        return null;
    }

    public void deleteRFIDScan(String id) {
        rfidScanRepository.deleteById(id);
    }

    @Autowired()
    private StudentRepository studentRepository;

    @Autowired()
    private CurfewConfigurationRepository curfewConfigurationRepository;

    /*
 * Operation    : Record Student Entry Scan
 * Usecase ID   : UC-001
 * Usecase Name : Record Student Entry Scan
 */
    public RecordStudentEntryScanResponse recordStudentEntryScan(RecordStudentEntryScanRequest request) {
        String rfid = request.getRfidCardNumber();
        Student student = studentRepository.findByRfidCardNumber(rfid).orElseThrow(() -> new RuntimeException("Student not found"));
        RFIDScan scan = new RFIDScan();
        //scan.setId(UUID.randomUUID().toString());
        scan.setScanTime(LocalDateTime.now());
        scan.setGateLocation(request.getGateLocation());
        scan.setStudent(student);
        //rfidScanRepository.save(scan);
        scan = rfidScanRepository.save(scan);
        List<CurfewConfiguration> configs = curfewConfigurationRepository.findByActiveStatus(true);
        String violationId = null;
        if (!configs.isEmpty()) {
            CurfewConfiguration config = configs.get(0);
            LocalTime curfew = LocalTime.parse(config.getCurfewTime());
            if (scan.getScanTime().toLocalTime().isAfter(curfew)) {
                ViolationRecord vr = new ViolationRecord();
                //vr.setId(UUID.randomUUID().toString());
                vr.setViolationTime(scan.getScanTime());
                vr.setStatus("Detected");
                vr.setStudent(student);
                vr.setRFIDScan(scan);
                violationRecordRepository.save(vr);
                violationId = vr.getId();
            }
        }
        return new RecordStudentEntryScanResponse(scan.getId(), scan.getScanTime(), scan.getGateLocation(), student.getId(), violationId);
    }
}
