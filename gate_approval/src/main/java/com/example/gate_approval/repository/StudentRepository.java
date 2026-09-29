package com.example.gate_approval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import org.springframework.stereotype.Repository;
import com.example.gate_approval.entity.*;

@Repository()
public interface StudentRepository extends JpaRepository<Student, String> {

    // repo_method_id: findByRfidCardNumber | Find student by RFID card number
    Optional<Student> findByRfidCardNumber(String rfidCardNumber);

    // repo_method_id: find_student_by_roll_or_rfid | Find a student by roll number or RFID card number.
    Optional<Student> findByRollNumberOrRfidCardNumber(String rollNumber, String rfidCardNumber);
}
