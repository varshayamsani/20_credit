package com.example.gate_approval.dto;

import lombok.Builder;
import lombok.Data;

@Builder()
@Data()
public class CurfewConfigurationResponse {

    private String id;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String curfewTime;

    public String getCurfewTime() {
        return this.curfewTime;
    }

    public void setCurfewTime(String curfewTime) {
        this.curfewTime = curfewTime;
    }

    private Boolean activeStatus;

    public Boolean getActiveStatus() {
        return this.activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public CurfewConfigurationResponse() {
    }

    public CurfewConfigurationResponse(String id, String curfewTime, Boolean activeStatus) {
        this.id = id;
        this.curfewTime = curfewTime;
        this.activeStatus = activeStatus;
    }
}
