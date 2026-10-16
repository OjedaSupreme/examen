package com.cmp_player.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmp_player.dto.StudentDto;
import com.cmp_player.dto.StudentRsDto;
import com.cmp_player.service.StudentStoreService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/student")
public class StudentController {

	private final StudentStoreService studentStoreService;

	public StudentController(StudentStoreService studentStoreService) {
		this.studentStoreService = studentStoreService;
	}

	@PostMapping
	public Mono<StudentRsDto> create(@RequestBody StudentDto studentDto) {
		return Mono.just(studentStoreService.create(studentDto));
	}

	@GetMapping
	public Flux<StudentDto> findAll() {
		return Flux.fromIterable(studentStoreService.findAll());
	}

}
