package com.example.gate_approval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.gate_approval.entity.RFIDScan;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository()
public interface RFIDScanRepository extends JpaRepository<RFIDScan, String> {
}
