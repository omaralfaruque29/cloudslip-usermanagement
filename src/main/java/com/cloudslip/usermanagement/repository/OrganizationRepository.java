package com.cloudslip.usermanagement.repository;

import com.cloudslip.usermanagement.model.Organization;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationRepository extends MongoRepository<Organization, ObjectId> {

    public Organization findByName(String name);

    public Organization findByNameIgnoreCase(String name);

    public Page<Organization> findAllByCompany_Id(Pageable pageable, ObjectId companyId);

    public List<Organization> findAllByCompany_Id(ObjectId companyId);

}