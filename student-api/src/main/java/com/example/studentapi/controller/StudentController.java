package com.example.studentapi.controller;

import com.example.studentapi.entity.Student;
import com.example.studentapi.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Student> findAll(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "studentCode") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        return service.findAll(
                keyword, page, size, sortBy, direction);
    }

    @GetMapping("/{code}")
    public Student findById(@PathVariable String code) {
        return service.findById(code);
    }

    @PostMapping
    public Student create(@Valid @RequestBody Student student) {
        return service.create(student);
    }

    @PutMapping("/{code}")
    public Student update(
            @PathVariable String code,
            @Valid @RequestBody Student student) {
        return service.update(code, student);
    }

    @DeleteMapping("/{code}")
    public void delete(@PathVariable String code) {
        service.delete(code);
    }
}