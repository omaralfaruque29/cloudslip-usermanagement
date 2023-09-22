package com.cloudslip.usermanagement.repository;

import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.model.EnvironmentOption;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnvironmentOptionRepository extends MongoRepository<EnvironmentOption, ObjectId> {
    Optional<EnvironmentOption> findTopByOrderByOrderNoDesc();

    Optional<EnvironmentOption> findByName(String name);

    Optional<EnvironmentOption> findByShortNameIgnoreCaseAndStatus(String name, Status status);

    Optional<EnvironmentOption>findByIdAndStatus(ObjectId Id, Status status);

    Optional<EnvironmentOption>findByNameIgnoreCaseAndStatus(String name, Status status);

    List<EnvironmentOption> findAllByStatus(Status status);

    Page<EnvironmentOption> findAllByStatus(Pageable pageable, Status status);

    List<EnvironmentOption> findAllByStatusAndIsEnabled(Status status, Boolean isEnabled);

    Page<EnvironmentOption> findAllByStatusAndIsEnabled(Pageable pageable, Status status, Boolean isEnabled);


}
