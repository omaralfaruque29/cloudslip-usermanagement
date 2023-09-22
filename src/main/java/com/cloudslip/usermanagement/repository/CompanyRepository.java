package com.cloudslip.usermanagement.repository;

import com.cloudslip.usermanagement.model.Company;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends MongoRepository<Company, ObjectId> {
    Optional<Company> findByNameIgnoreCase(String name);
}