package com.example.gate_approval.dto;

import lombok.Builder;
import lombok.Data;

@Builder()
@Data()
public class ViolationReportResponse {

    private Integer totalViolations;

    public Integer getTotalViolations() {
        return this.totalViolations;
    }

    public void setTotalViolations(Integer totalViolations) {
        this.totalViolations = totalViolations;
    }

    private Integer distinctStudents;

    public Integer getDistinctStudents() {
        return this.distinctStudents;
    }

    public void setDistinctStudents(Integer distinctStudents) {
        this.distinctStudents = distinctStudents;
    }

    public ViolationReportResponse() {
    }

    public ViolationReportResponse(Integer totalViolations, Integer distinctStudents) {
        this.totalViolations = totalViolations;
        this.distinctStudents = distinctStudents;
    }
}
