package com.example.gate_approval.dto;

import lombok.Builder;
import lombok.Data;

@Builder()
@Data()
public class ViewStudentViolationHistoryRequest {

    private String rollNumber;

    public String getRollNumber() {
        return this.rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    private String rfidCardNumber;

    public String getRfidCardNumber() {
        return this.rfidCardNumber;
    }

    public void setRfidCardNumber(String rfidCardNumber) {
        this.rfidCardNumber = rfidCardNumber;
    }

    public ViewStudentViolationHistoryRequest() {
    }

    public ViewStudentViolationHistoryRequest(String rollNumber, String rfidCardNumber) {
        this.rollNumber = rollNumber;
        this.rfidCardNumber = rfidCardNumber;
    }
}
