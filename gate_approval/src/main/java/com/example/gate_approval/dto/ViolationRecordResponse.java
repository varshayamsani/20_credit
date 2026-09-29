package com.example.gate_approval.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Builder()
@Data()
public class ViolationRecordResponse {

    private String id;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private LocalDateTime violationTime;

    public LocalDateTime getViolationTime() {
        return this.violationTime;
    }

    public void setViolationTime(LocalDateTime violationTime) {
        this.violationTime = violationTime;
    }

    private String status;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ViolationRecordResponse() {
    }

    public ViolationRecordResponse(String id, LocalDateTime violationTime, String status) {
        this.id = id;
        this.violationTime = violationTime;
        this.status = status;
    }
}
