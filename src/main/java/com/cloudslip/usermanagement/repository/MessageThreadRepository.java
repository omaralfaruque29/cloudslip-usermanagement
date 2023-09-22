package com.cloudslip.usermanagement.repository;

import com.cloudslip.usermanagement.model.MessageThread;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageThreadRepository extends MongoRepository<MessageThread, ObjectId> {
    
}