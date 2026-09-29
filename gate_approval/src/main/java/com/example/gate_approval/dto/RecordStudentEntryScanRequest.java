package com.example.gate_approval.dto;

import lombok.Builder;
import lombok.Data;

@Builder()
@Data()
public class RecordStudentEntryScanRequest {

    private String rfidCardNumber;

    public String getRfidCardNumber() {
        return this.rfidCardNumber;
    }

    public void setRfidCardNumber(String rfidCardNumber) {
        this.rfidCardNumber = rfidCardNumber;
    }

    private String gateLocation;

    public String getGateLocation() {
        return this.gateLocation;
    }

    public void setGateLocation(String gateLocation) {
        this.gateLocation = gateLocation;
    }

    public RecordStudentEntryScanRequest() {
    }

    public RecordStudentEntryScanRequest(String rfidCardNumber, String gateLocation) {
        this.rfidCardNumber = rfidCardNumber;
        this.gateLocation = gateLocation;
    }
}
