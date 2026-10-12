package com.example.studentapi.service;

import com.example.studentapi.entity.Student;
import com.example.studentapi.repository.StudentRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Page<Student> findAll(
            String keyword,
            int page,
            int size,
            String sortBy,
            String direction) {

        if (page < 0 || size < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "page phải >= 0 và size phải >= 1");
        }

        if (!java.util.Set.of(
                "studentCode", "name", "dateOfBirth",
                "gender", "phone", "gpa"
        ).contains(sortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "sortBy không hợp lệ");
        }

        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "direction phải là ASC hoặc DESC");
        }

        Pageable pageable = PageRequest.of(
                page, size, Sort.by(sortDirection, sortBy));

        Specification<Student> spec = (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("studentCode")), pattern),
                    cb.like(cb.lower(root.get("name")), pattern)
            );
        };

        return repository.findAll(spec, pageable);
    }

    public Student findById(String code) {
        return repository.findById(code)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên"));
    }

    public Student create(Student student) {
        if (repository.existsById(student.getStudentCode())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Mã sinh viên đã tồn tại");
        }

        return repository.save(student);
    }

    public Student update(String code, Student student) {
        Student existing = findById(code);

        existing.setName(student.getName());
        existing.setDateOfBirth(student.getDateOfBirth());
        existing.setGender(student.getGender());
        existing.setPhone(student.getPhone());
        existing.setGpa(student.getGpa());

        return repository.save(existing);
    }

    public void delete(String code) {
        repository.delete(findById(code));
    }
}