package com.cloudslip.usermanagement.repository;

import com.cloudslip.usermanagement.model.Message;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, ObjectId> {

    List<Message> findAllByMessageThreadId(ObjectId messageThreadId);

    @Query(value = "{ 'sender.userId' : ?0 }")
    Page<Message> findAllForSender(Pageable pageable, ObjectId senderUserId);


    @Query(value = "{ 'recipientList.user.userId' : ?0 }")
    Page<Message> findAllForRecipient(Pageable pageable, Object recipientUserId);

    
}