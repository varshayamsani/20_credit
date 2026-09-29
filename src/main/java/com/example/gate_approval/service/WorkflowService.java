package com.example.gate_approval.service;
import com.example.gate_approval.dto.*;
import com.example.gate_approval.dto.AccessibleFormsRequest;
import com.example.gate_approval.dto.InvokeRequest;
import com.example.gate_approval.dto.ResumeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkflowService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${workflow.base-url}")
    private String workflowBaseUrl;

    public WorkflowService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

//    public String invokeWorkflow() {
//
//        Map<String,Object> payload = new HashMap<>();
//
//        payload.put(
//                "workflow_id",
//                "w1_b7f6e948-7dfb-4fa0-ad98-d86f317757ed"
//        );
//
//        payload.put("user_id", "student1");
//        payload.put("role", List.of("student"));
//        payload.put("input", Map.of());
//        payload.put("jwt_token", "");
//
//        return restTemplate.postForObject(
//                workflowBaseUrl + "/api/invoke",
//                payload,
//                String.class
//        );
//    }
//public String invokeWorkflow() {
//
//    try {
//
//        Map<String,Object> payload = new HashMap<>();
//
//        payload.put(
//                "workflow_id",
//                "c13377d33e5c4f9f9ed3fcfb9f6fe0de"
//        );
//
//        payload.put("user_id", "student1");
//        payload.put("role", List.of(""));
//        payload.put("input", Map.of());
//        payload.put("jwt_token", "");
//        System.out.println(payload);
//
//        String response = restTemplate.postForObject(
//                workflowBaseUrl + "/api/invoke",
//                payload,
//                String.class
//        );
//
//        System.out.println("Workflow response: " + response);
//
//        return response;
//
//    } catch (Exception e) {
//
//        e.printStackTrace();
//
//        return "ERROR: " + e.getMessage();
//    }
//}
public String getWorkflowSpec(
        String workflowId
) {

    return restTemplate.getForObject(
            workflowBaseUrl +
                    "/api/workflowSpec/" +
                    workflowId,
            String.class
    );
}
public String invokeWorkflow(
        InvokeRequest request
) {

    try {

        Map<String,Object> payload = new HashMap<>();

        payload.put(
                "workflow_id",
                request.getWorkflowId()
        );

        payload.put(
                "user_id",
                request.getUserId()
        );

        payload.put(
                "role",
                request.getRoles()
        );

        payload.put(
                "input",
                Map.of()
        );

        payload.put(
                "jwt_token",
                ""
        );

        System.out.println(payload);

        String response = restTemplate.postForObject(
                workflowBaseUrl + "/api/invoke",
                payload,
                String.class
        );

        System.out.println(response);

        return response;

    } catch (Exception e) {

        e.printStackTrace();

        return "ERROR: " + e.getMessage();
    }
}
//    public String resumeWorkflow(
//            String threadId,
//            String amount
//    ) {
//
//        Map<String,Object> payload = new HashMap<>();
//
//        payload.put(
//                "workflow_id",
//                "c13377d33e5c4f9f9ed3fcfb9f6fe0de"
//        );
//
//        payload.put("thread_id", threadId);
//
//        payload.put("user_id", "finance1");
//
//        payload.put(
//                "role",
//                List.of("finance")
//        );
//
////        payload.put(
////                "input",
////                "{\"amount\":\"" + amount + "\"}"
////        );
//        Map<String,Object> form = new HashMap<>();
//        form.put("amount", amount);
//
//        payload.put("input", form);
//
//        payload.put("jwt_token", "");
//
//        return restTemplate.postForObject(
//                workflowBaseUrl + "/api/resume",
//                payload,
//                String.class
//        );
//    }
public String resumeWorkflow(
        ResumeRequest request
) {

    try {

        Map<String,Object> payload = new HashMap<>();

        payload.put(
                "workflow_id",
                request.getWorkflowId()
        );

        payload.put(
                "thread_id",
                request.getThreadId()
        );

        payload.put(
                "user_id",
                request.getUserId()
        );

        payload.put(
                "role",
                request.getRoles()
        );

        payload.put(
                "input",
                request.getInput()
        );

        payload.put(
                "jwt_token",
                ""
        );

        System.out.println(payload);

        String response = restTemplate.postForObject(
                workflowBaseUrl + "/api/resume",
                payload,
                String.class
        );

        System.out.println(response);

        return response;

    } catch (Exception e) {

        e.printStackTrace();

        return "ERROR: " + e.getMessage();
    }
}
    public String getAccessibleForms(
            AccessibleFormsRequest request
    ) {

        Map<String,Object> payload = new HashMap<>();

        payload.put("workflow_id", request.getWorkflowId());
        payload.put("thread_id", request.getThreadId());
        payload.put("node_id", request.getNodeId());
        payload.put("role", request.getRole());
        payload.put("user_id", request.getUserId());
        payload.put("workflow_spec", request.getWorkflowSpec());

        return restTemplate.postForObject(
                workflowBaseUrl + "/api/accessible_forms",
                payload,
                String.class
        );
    }

    public String getAccessibleFields(
            AccessibleFieldsRequest request
    ) {

        Map<String,Object> payload = new HashMap<>();

        payload.put(
                "role",
                request.getRole()
        );

        payload.put(
                "workflow_spec",
                request.getWorkflowSpec()
        );

        return restTemplate.postForObject(
                workflowBaseUrl + "/api/accessible_fields",
                payload,
                String.class
        );
    }
    public String getNextForm(
            NextFormRequest request
    ) {

        try {

            // STEP 1
            String workflowSpecResponse =
                    getWorkflowSpec(
                            request.getWorkflowId()
                    );

            JsonNode specJson =
                    objectMapper.readTree(
                            workflowSpecResponse
                    );

            JsonNode workflowSpec =
                    specJson
                            .get("data")
                            .get("workflow_spec");

            // STEP 2
            Map<String,Object> formsPayload =
                    new HashMap<>();

            formsPayload.put(
                    "workflow_id",
                    request.getWorkflowId()
            );

            formsPayload.put(
                    "thread_id",
                    request.getThreadId()
            );

            formsPayload.put(
                    "role",
                    request.getRole()
            );

            formsPayload.put(
                    "user_id",
                    request.getUserId()
            );

            formsPayload.put(
                    "workflow_spec",
                    objectMapper.convertValue(
                            workflowSpec,
                            Map.class
                    )
            );

            String formsResponse =
                    restTemplate.postForObject(
                            workflowBaseUrl +
                                    "/api/accessible_forms",
                            formsPayload,
                            String.class
                    );

            JsonNode formsJson =
                    objectMapper.readTree(
                            formsResponse
                    );

            JsonNode nodes =
                    formsJson.get(
                            "accessible_nodes"
                    );

            if (nodes == null || nodes.isEmpty()) {

                return """
                {
                    "fields":[]
                }
                """;
            }

            // STEP 3
            Map<String,Object> fieldsPayload =
                    new HashMap<>();

            fieldsPayload.put(
                    "workflow_id",
                    request.getWorkflowId()
            );

            fieldsPayload.put(
                    "role",
                    request.getRole()
            );

            fieldsPayload.put(
                    "workflow_spec",
                    objectMapper.convertValue(
                            workflowSpec,
                            Map.class
                    )
            );

            return restTemplate.postForObject(
                    workflowBaseUrl +
                            "/api/accessible_fields",
                    fieldsPayload,
                    String.class
            );

        } catch (Exception e) {

            e.printStackTrace();

            return "ERROR: " + e.getMessage();
        }
    }
}