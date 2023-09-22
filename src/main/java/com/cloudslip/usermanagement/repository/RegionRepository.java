package com.cloudslip.usermanagement.repository;

import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.model.Region;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RegionRepository extends MongoRepository<Region, ObjectId> {
    public Region findByNameIgnoreCase(String name);

    public Optional<Region> findByIdAndStatus(ObjectId id, Status status);
}
