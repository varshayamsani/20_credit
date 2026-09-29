package com.example.gate_approval.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Builder()
@Data()
public class RFIDScanDTO {

    private String id;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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

    public RFIDScanDTO() {
    }

    public RFIDScanDTO(String id, LocalDateTime scanTime, String gateLocation) {
        this.id = id;
        this.scanTime = scanTime;
        this.gateLocation = gateLocation;
    }
}
