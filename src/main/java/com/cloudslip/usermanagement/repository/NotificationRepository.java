package com.cloudslip.usermanagement.repository;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.model.Notification;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {

    Page<Notification> findByUserIdAndStatus(Pageable pageable, ObjectId userId, Status status);

    List<Notification> findByUserIdAndStatus(ObjectId userId, Status status, Sort sort);

    @Query(value = "{'userId': ?0, 'clicked': false}", count=true)
    int countUncheckedNotifications(ObjectId userId);

}