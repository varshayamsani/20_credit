package com.example.gate_approval.dto;

import java.util.Map;

public class AccessibleFormsRequest {

    private String workflowId;
    private String threadId;
    private String nodeId;
    private String role;
    private String userId;
    private Map<String,Object> workflowSpec;

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String,Object> getWorkflowSpec() {
        return workflowSpec;
    }

    public void setWorkflowSpec(Map<String,Object> workflowSpec) {
        this.workflowSpec = workflowSpec;
    }
}