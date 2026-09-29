package com.example.gate_approval.dto;

import lombok.Builder;
import lombok.Data;

@Builder()
@Data()
public class CurfewConfigurationRequest {

    private String curfewTime;

    public String getCurfewTime() {
        return this.curfewTime;
    }

    public void setCurfewTime(String curfewTime) {
        this.curfewTime = curfewTime;
    }

    public CurfewConfigurationRequest() {
    }

    public CurfewConfigurationRequest(String curfewTime) {
        this.curfewTime = curfewTime;
    }
}
