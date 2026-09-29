package com.example.gate_approval.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import com.example.gate_approval.repository.CurfewConfigurationRepository;
import com.example.gate_approval.entity.*;
import com.example.gate_approval.dto.*;
import java.time.*;
import org.springframework.http.ResponseEntity;
import java.security.Principal;

@Service()
public class CurfewConfigurationService {

    @Autowired()
    private CurfewConfigurationRepository curfewConfigurationRepository;

    public CurfewConfiguration createCurfewConfiguration(CurfewConfiguration entity) {
        return curfewConfigurationRepository.save(entity);
    }

    public List<CurfewConfiguration> getAllCurfewConfigurations() {
        return curfewConfigurationRepository.findAll();
    }

    public Optional<CurfewConfiguration> getCurfewConfigurationById(String id) {
        return curfewConfigurationRepository.findById(id);
    }

    public CurfewConfiguration updateCurfewConfiguration(String id, CurfewConfiguration entity) {
        if (curfewConfigurationRepository.existsById(id)) {
            entity.setId(id);
            return curfewConfigurationRepository.save(entity);
        }
        return null;
    }

    public void deleteCurfewConfiguration(String id) {
        curfewConfigurationRepository.deleteById(id);
    }

    @Autowired()
    private KeycloakAuthService keycloakAuthService;

    /*
 * Operation    : Set Curfew Time
 * Usecase ID   : UC-002
 * Usecase Name : Set Curfew Time
 */
    public CurfewConfigurationResponse setCurfewTime(CurfewConfigurationRequest curfewConfigRequest, Principal principal) {
        String adminId = keycloakAuthService.getUserId(principal);
        List<CurfewConfiguration> configs = curfewConfigurationRepository.findAll();
        CurfewConfiguration config;
        if (configs.isEmpty()) {
            config = new CurfewConfiguration();
            // generate a UUID for the system‑managed id
            // import java.util.UUID needed
            config.setId(java.util.UUID.randomUUID().toString());
        } else {
            config = configs.get(0);
        }
        config.setCurfewTime(curfewConfigRequest.getCurfewTime());
        config.setActiveStatus(true);
        curfewConfigurationRepository.save(config);
        CurfewConfigurationResponse response = new CurfewConfigurationResponse();
        response.setId(config.getId());
        response.setCurfewTime(config.getCurfewTime());
        response.setActiveStatus(config.getActiveStatus());
        return response;
    }
}
