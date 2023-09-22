package com.cloudslip.usermanagement.repository;

import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.model.AppIssueComment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppIssueCommentRepository  extends MongoRepository<AppIssueComment, ObjectId> {

    Optional<AppIssueComment> findByIdAndStatus(ObjectId appIssueCommentId, Status status);

    List<AppIssueComment> findAllByAppIssueIdAndStatusAndParentAppIssueCommentIdIsNull(ObjectId appIssueId, Status status);
}
