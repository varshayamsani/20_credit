package com.example.gate_approval.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

@Entity()
@Builder()
@Data()
@NoArgsConstructor()
@AllArgsConstructor()
@Getter()
@Setter()
@Table(name = "students")
public class Student {

    @Id()
    @GeneratedValue()
    @UuidGenerator()
    private String id;

    private String rollNumber;

    private String name;

    private String hostel;

    private String rfidCardNumber;
}
