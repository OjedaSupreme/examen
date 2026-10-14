package com.huertos.parcels.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ParcelRepository extends MongoRepository<Parcel, String> {

    boolean existsByNameNormalized(String nameNormalized);

    List<Parcel> findAllByOrderByCreatedAtDesc();
}
