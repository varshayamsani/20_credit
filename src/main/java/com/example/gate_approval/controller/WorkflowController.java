package com.example.gate_approval.controller;
import com.example.gate_approval.dto.AccessibleFieldsRequest;
import com.example.gate_approval.dto.AccessibleFormsRequest;
import com.example.gate_approval.dto.InvokeRequest;
import com.example.gate_approval.dto.ResumeRequest;
import com.example.gate_approval.service.WorkflowService;
import org.springframework.web.bind.annotation.*;
import com.example.gate_approval.dto.NextFormRequest;
import java.util.Map;
//@RestController
//@RequestMapping("/workflow")
//public class WorkflowController {
//
//    private final WorkflowService workflowService;
//
//    public WorkflowController(
//            WorkflowService workflowService
//    ) {
//        this.workflowService = workflowService;
//    }
//    @GetMapping("/spec/{workflowId}")
//    public String getWorkflowSpec(
//            @PathVariable String workflowId
//    ) {
//        return workflowService.getWorkflowSpec(
//                workflowId
//        );
//    }
//    @PostMapping("/invoke")
//    public String invoke(
//            @RequestBody InvokeRequest request
//    ) {
//        return workflowService.invokeWorkflow(request);
//    }
//
//    @PostMapping("/resume")
//    public String resume(
//            @RequestBody ResumeRequest request
//    ) {
//        return workflowService.resumeWorkflow(request);
//    }
//
//
//}
@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(
            WorkflowService workflowService
    ) {
        this.workflowService = workflowService;
    }

    @PostMapping("/invoke")
    public String invoke(
            @RequestBody InvokeRequest request
    ) {
        return workflowService.invokeWorkflow(request);
    }

    @PostMapping("/resume")
    public String resume(
            @RequestBody ResumeRequest request
    ) {
        return workflowService.resumeWorkflow(request);
    }

    @PostMapping("/accessible-forms")
    public String accessibleForms(
            @RequestBody AccessibleFormsRequest request
    ) {

        return workflowService.getAccessibleForms(
                request
        );
    }

    @PostMapping("/accessible-fields")
    public String accessibleFields(
            @RequestBody AccessibleFieldsRequest request
    ) {

        return workflowService.getAccessibleFields(
                request
        );
    }
    @GetMapping("/spec/{workflowId}")
    public String getWorkflowSpec(
            @PathVariable String workflowId
    ) {
        return workflowService.getWorkflowSpec(workflowId);
    }
    @PostMapping("/next-form")
    public String nextForm(
            @RequestBody NextFormRequest request
    ) {

        return workflowService.getNextForm(
                request
        );
    }
}