package com.huertos.monitoring.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SensorReadingRepository extends MongoRepository<SensorReading, String> {

    Optional<SensorReading> findFirstByParcelIdOrderByRecordedAtDesc(String parcelId);
}
