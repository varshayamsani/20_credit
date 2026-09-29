package com.example.gate_approval.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Builder()
@Data()
public class GenerateDailyViolationReportRequest {

    private LocalDate date;

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public GenerateDailyViolationReportRequest() {
    }

    public GenerateDailyViolationReportRequest(LocalDate date) {
        this.date = date;
    }
}
