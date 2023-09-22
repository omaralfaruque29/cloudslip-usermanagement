package com.cloudslip.usermanagement.repository;


import com.cloudslip.usermanagement.enums.ServerSettingKey;
import com.cloudslip.usermanagement.model.ServerSetting;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServerSettingRepository extends MongoRepository<ServerSetting, ObjectId> {

    Optional<ServerSetting> findByKey(ServerSettingKey key);

}