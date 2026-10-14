package com.huertos.monitoring.web;

import com.huertos.monitoring.application.MonitoringApplicationService;
import com.huertos.monitoring.web.dto.CreateReadingRequest;
import com.huertos.monitoring.web.dto.CurrentStatusResponse;
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

@RestController
@RequestMapping(path = "/api/v1/conditions", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConditionController {

    private final MonitoringApplicationService service;

    public ConditionController(MonitoringApplicationService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CurrentStatusResponse> register(@Valid @RequestBody CreateReadingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
    }

    @GetMapping("/latest/{parcelId}")
    public CurrentStatusResponse latest(@PathVariable String parcelId) {
        return service.latest(parcelId);
    }
}
