package com.example.gate_approval.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import com.example.gate_approval.entity.Student;
import com.example.gate_approval.repository.StudentRepository;

@Service()
public class StudentService {

    @Autowired()
    private StudentRepository studentRepository;

    public Student createStudent(Student entity) {
        return studentRepository.save(entity);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(String id) {
        return studentRepository.findById(id);
    }

    public Student updateStudent(String id, Student entity) {
        if (studentRepository.existsById(id)) {
            entity.setId(id);
            return studentRepository.save(entity);
        }
        return null;
    }

    public void deleteStudent(String id) {
        studentRepository.deleteById(id);
    }
}
