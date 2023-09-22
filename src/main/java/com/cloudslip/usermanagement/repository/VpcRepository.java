package com.cloudslip.usermanagement.repository;

import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.enums.VpcStatus;
import com.cloudslip.usermanagement.model.Vpc;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VpcRepository extends MongoRepository<Vpc, ObjectId> {

    public List<Vpc> findAllByStatusAndVpcStatus(Status status, VpcStatus vpcStatus);

    public Page<Vpc> findAllByStatusAndVpcStatus(Pageable pageable, Status status, VpcStatus vpcStatus);

    public List<Vpc> findAllByStatus(Status status);

    public Page<Vpc> findAllByStatus(Pageable pageable, Status status);

    public Page<Vpc> findAllByCompanyIdAndStatusAndVpcStatus(Pageable pageable, ObjectId companyId, Status status, VpcStatus vpcStatus);

    public List<Vpc> findAllByCompanyIdAndStatusAndVpcStatus(ObjectId companyId, Status status, VpcStatus vpcStatus);

    public Page<Vpc> findAllByCompanyIdAndStatus(Pageable pageable, ObjectId companyId, Status status);

    public List<Vpc> findAllByCompanyIdAndStatus(ObjectId companyId, Status status);

    public Optional<Vpc> findByIdAndStatus(ObjectId id, Status status);

    public Optional<Vpc> findByNameIgnoreCaseAndCompanyIdAndStatus(String name, ObjectId companyId, Status status);

    public Optional<Vpc> findByNameIgnoreCaseAndCompanyIdAndStatusAndIdNotIn(String name, ObjectId companyId, Status status, ObjectId id);

    Optional<Vpc> findByIdAndCompanyIdAndStatus(ObjectId vpcId, ObjectId companyId, Status status);

//    public List<Vpc> findAllByCompanyEnvironmentIdAndStatus(ObjectId companyEnvId, Status status);
//
//    public Page<Vpc> findAllByCompanyEnvironmentIdAndStatus(Pageable pageable, ObjectId companyEnvId, Status status);
//
//    public List<Vpc> findAllByCompanyEnvironmentIdInAndStatus(List<ObjectId> companyEnvList, Status status);

//    public Optional<Vpc> findByNameIgnoreCaseAndCompanyEnvironmentIdAndStatus(String name, ObjectId comEnvId, Status status);

//    public Optional<Vpc> findByNameIgnoreCaseAndCompanyEnvironmentIdAndStatusAndIdNotIn(String name, ObjectId comEnvId, Status status, ObjectId id);

//    public Page<Vpc> findAllByCompanyEnvironmentIdInAndStatus(Pageable pageable, List<ObjectId> companyEnvList, Status status);
//
//    public Optional<Vpc> findTopByCompanyEnvironmentIdOrderByOrderNoDesc(ObjectId companyEnvironmentId);

//    public Vpc findByCompanyEnvironmentIdAndKubeClusterIdAndStatus (ObjectId companyEnvironmentId, ObjectId kubeClusterId, Status status);
//
//    public List<Vpc> findByCompanyEnvironmentIdAndKubeClusterIdNotInAndStatus(ObjectId companyEnvironmentId, List<ObjectId> kubeClusterId, Status status);

}
