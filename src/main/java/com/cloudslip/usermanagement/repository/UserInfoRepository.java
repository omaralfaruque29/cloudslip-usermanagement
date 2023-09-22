package com.cloudslip.usermanagement.repository;


import com.cloudslip.usermanagement.model.UserInfo;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInfoRepository extends MongoRepository<UserInfo, ObjectId> {

    UserInfo findByEmail(final String email);

    Optional<UserInfo> findByUserId(final ObjectId userId);

    Page<UserInfo> findAllByCompanyId(Pageable pageable, ObjectId companyId);

    List<UserInfo> findAllByCompanyId(ObjectId companyId);

    @Query(value = "{ 'teams._id' : ?0}")
    List<UserInfo> findAllByTeamsId(ObjectId teamId);
}