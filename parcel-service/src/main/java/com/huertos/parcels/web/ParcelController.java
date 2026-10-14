package com.huertos.parcels.web;

import com.huertos.parcels.application.ParcelApplicationService;
import com.huertos.parcels.web.dto.CreateParcelRequest;
import com.huertos.parcels.web.dto.ParcelResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/parcels", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParcelController {

    private final ParcelApplicationService service;

    public ParcelController(ParcelApplicationService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParcelResponse> create(@Valid @RequestBody CreateParcelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public List<ParcelResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ParcelResponse getById(@PathVariable String id) {
        return service.getById(id);
    }
}
