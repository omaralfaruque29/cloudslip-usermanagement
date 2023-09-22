package com.cloudslip.usermanagement.repository;

import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.model.AppIssue;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppIssueRepository extends MongoRepository<AppIssue, ObjectId> {
    Optional<AppIssue> findByIdAndStatus(ObjectId appIssueId, Status status);

    List<AppIssue> findAllByApplicationIdAndStatus(ObjectId applicationId, Status status);
}
