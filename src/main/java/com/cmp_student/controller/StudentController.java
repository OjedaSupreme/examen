package com.cmp_student.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmp_student.dto.StudentDto;
import com.cmp_student.dto.StudentRsDto;
import com.cmp_student.service.StudentService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public Mono<StudentRsDto> create(@Validated @RequestBody StudentDto studentDto) {
        return studentService.create(studentDto);
    }

    @GetMapping
    public Flux<StudentDto> findAll() {
        return studentService.findAll();
    }

}
