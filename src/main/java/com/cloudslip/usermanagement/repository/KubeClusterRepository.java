package com.cloudslip.usermanagement.repository;

import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.model.KubeCluster;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KubeClusterRepository extends MongoRepository<KubeCluster, ObjectId> {

    Optional<KubeCluster> findByIdAndStatus(ObjectId kubeClusterId, Status status);

    List<KubeCluster> findAllByStatus(Status status);

    Page<KubeCluster> findAllByStatus(Pageable pageable, Status status);

    Optional<KubeCluster>findByNameIgnoreCaseAndStatus(String name, Status status);

    Page<KubeCluster> findAllByStatusAndIsEnabled(Pageable pageable, Status status, Boolean isEnabled);

    List<KubeCluster> findAllByStatusAndIsEnabled(Status status, Boolean isEnabled);

    List<KubeCluster> findAllByRegion_IdAndStatus(ObjectId regionId, Status status);
}
