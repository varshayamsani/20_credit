package com.example.gate_approval.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import com.example.gate_approval.service.CurfewConfigurationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.example.gate_approval.entity.*;
import com.example.gate_approval.dto.*;
import java.security.Principal;

@RestController()
@RequestMapping(value = "/api/curfewconfigurations")
public class CurfewConfigurationController {

    @Autowired()
    private CurfewConfigurationService curfewConfigurationService;

    @PostMapping()
    public ResponseEntity<CurfewConfiguration> createCurfewConfiguration(@RequestBody CurfewConfiguration entity) {
        return ResponseEntity.ok(curfewConfigurationService.createCurfewConfiguration(entity));
    }

    @GetMapping()
    public ResponseEntity<List<CurfewConfiguration>> getAllCurfewConfigurations() {
        return ResponseEntity.ok(curfewConfigurationService.getAllCurfewConfigurations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CurfewConfiguration> getCurfewConfigurationById(@PathVariable String id) {
        return curfewConfigurationService.getCurfewConfigurationById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CurfewConfiguration> updateCurfewConfiguration(@PathVariable String id, @RequestBody CurfewConfiguration entity) {
        CurfewConfiguration updated = curfewConfigurationService.updateCurfewConfiguration(id, entity);
        if (updated != null)
            return ResponseEntity.ok(updated);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurfewConfiguration(@PathVariable String id) {
        curfewConfigurationService.deleteCurfewConfiguration(id);
        return ResponseEntity.noContent().build();
    }

    /*
 * Operation    : Set Curfew Time
 * Usecase ID   : UC-002
 * Usecase Name : Set Curfew Time
 */
    @PostMapping(value = "/curfew")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CurfewConfigurationResponse> setCurfewTime(@RequestBody CurfewConfigurationRequest curfewConfigRequest, Principal principal) {
        return ResponseEntity.ok(curfewConfigurationService.setCurfewTime(curfewConfigRequest, principal));
    }
}
