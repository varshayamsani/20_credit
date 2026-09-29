package com.example.gate_approval.dto;

import lombok.Builder;
import lombok.Data;

@Builder()
@Data()
public class StudentDTO {

    private String id;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String rollNumber;

    public String getRollNumber() {
        return this.rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String hostel;

    public String getHostel() {
        return this.hostel;
    }

    public void setHostel(String hostel) {
        this.hostel = hostel;
    }

    private String rfidCardNumber;

    public String getRfidCardNumber() {
        return this.rfidCardNumber;
    }

    public void setRfidCardNumber(String rfidCardNumber) {
        this.rfidCardNumber = rfidCardNumber;
    }

    public StudentDTO() {
    }

    public StudentDTO(String id, String rollNumber, String name, String hostel, String rfidCardNumber) {
        this.id = id;
        this.rollNumber = rollNumber;
        this.name = name;
        this.hostel = hostel;
        this.rfidCardNumber = rfidCardNumber;
    }
}
