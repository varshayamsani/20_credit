package com.example.gate_approval.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Builder()
@Data()
public class RecordStudentEntryScanResponse {

    private String scanId;

    public String getScanId() {
        return this.scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    private LocalDateTime scanTime;

    public LocalDateTime getScanTime() {
        return this.scanTime;
    }

    public void setScanTime(LocalDateTime scanTime) {
        this.scanTime = scanTime;
    }

    private String gateLocation;

    public String getGateLocation() {
        return this.gateLocation;
    }

    public void setGateLocation(String gateLocation) {
        this.gateLocation = gateLocation;
    }

    private String studentId;

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    private String violationId;

    public String getViolationId() {
        return this.violationId;
    }

    public void setViolationId(String violationId) {
        this.violationId = violationId;
    }

    public RecordStudentEntryScanResponse() {
    }

    public RecordStudentEntryScanResponse(String scanId, LocalDateTime scanTime, String gateLocation, String studentId, String violationId) {
        this.scanId = scanId;
        this.scanTime = scanTime;
        this.gateLocation = gateLocation;
        this.studentId = studentId;
        this.violationId = violationId;
    }
}
