package com.example.gate_approval.dto;

import lombok.Data;

@Data
public class RegisterUserDto {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
