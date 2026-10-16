package com.cmp_student.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cmp_student.dto.StudentDto;
import com.cmp_student.dto.StudentRsDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StudentService {

    @Value("${base.url.player}")
    private String baseUrlPlayer;
    private final WebClient webClient;

    public StudentService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<StudentRsDto> create(StudentDto studentDto) {
        return webClient.post()
                .uri(baseUrlPlayer)
                .bodyValue(studentDto)
                .retrieve()
                .bodyToMono(StudentRsDto.class);
    }

    public Flux<StudentDto> findAll() {
        return webClient.get()
                .uri(baseUrlPlayer)
                .retrieve()
                .bodyToFlux(StudentDto.class);
    }

}
