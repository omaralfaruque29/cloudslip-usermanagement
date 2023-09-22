package com.cloudslip.usermanagement.repository;

import com.cloudslip.usermanagement.model.Team;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends MongoRepository<Team, ObjectId> {

    //@Query(value="{'name': ?0, 'status': 'V'}")
    public Team findByName(String name);

    public Team findByNameIgnoreCase(String name);

    public List<Team> findAllByOrganization_Id(ObjectId organizationId);

    public Page<Team> findAllByOrganization_Id(Pageable pageable, ObjectId organizationId);

    public Page<Team> findAllByOrganization_Company_Id(Pageable pageable, ObjectId companyId);

    public List<Team> findAllByOrganization_Company_Id(ObjectId companyId);

    public List<Team> findAllByCompanyId(ObjectId companyId);

    public List<Team> findAllByIdIn(List<ObjectId> teamIdList);
}