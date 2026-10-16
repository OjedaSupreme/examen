package com.cmp_player.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.cmp_player.dto.StudentDto;
import com.cmp_player.dto.StudentRsDto;

@Service
public class StudentStoreService {

	private final List<StudentDto> students = new CopyOnWriteArrayList<>();
	private final AtomicInteger ids = new AtomicInteger();

	public StudentRsDto create(StudentDto studentDto) {
		int id = ids.incrementAndGet();
		students.add(new StudentDto(id, studentDto.name(), studentDto.lastName(), studentDto.phone(), studentDto.eMail()));
		return new StudentRsDto("Student created", id);
	}

	public List<StudentDto> findAll() {
		return List.copyOf(students);
	}

}
