package com.example.gate_approval.dto;

import java.util.Map;

public class AccessibleFieldsRequest {

    private String workflowId;
    private String role;
    private Map<String,Object> workflowSpec;

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Map<String, Object> getWorkflowSpec() {
        return workflowSpec;
    }

    public void setWorkflowSpec(Map<String, Object> workflowSpec) {
        this.workflowSpec = workflowSpec;
    }
}