package com.cloudslip.usermanagement.repository;

import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.model.VpcGroup;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VpcGroupRepository extends MongoRepository<VpcGroup, ObjectId> {
    Optional<VpcGroup> findByNameAndCompanyIdAndStatus(String name, ObjectId companyId, Status status);
    Optional<VpcGroup> findByNameAndCompanyIdAndStatusAndIdNotIn(String name, ObjectId companyId, Status status, ObjectId vpcGroupId);

    Optional<VpcGroup> findByIdAndStatus(ObjectId vpcGroupId, Status status);

    List<VpcGroup> findAllByCompanyIdAndStatus(ObjectId companyId, Status status);
    Page<VpcGroup> findAllByCompanyIdAndStatus(Pageable pageable, ObjectId companyId, Status status);

    List<VpcGroup> findAllByStatus(Status status);
    Page<VpcGroup> findAllByStatus(Pageable pageable, Status status);
}
