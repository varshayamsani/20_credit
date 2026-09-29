package com.example.gate_approval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import org.springframework.stereotype.Repository;
import com.example.gate_approval.entity.*;

@Repository()
public interface CurfewConfigurationRepository extends JpaRepository<CurfewConfiguration, String> {

    // repo_method_id: findByActiveStatus | Retrieve active curfew configuration(s)
    List<CurfewConfiguration> findByActiveStatus(Boolean activeStatus);
}
