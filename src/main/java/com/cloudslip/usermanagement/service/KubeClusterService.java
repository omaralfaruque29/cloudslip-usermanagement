package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.helper.public_cluster.*;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.repository.KubeClusterRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class KubeClusterService {

    private final Logger log = LoggerFactory.getLogger(KubeClusterService.class);

    @Autowired
    KubeClusterRepository kubeClusterRepository;

    @Autowired
    private CreateKubeClusterHelper createKubeClusterHelper;

    @Autowired
    private UpdateKubeClusterHelper updateKubeClusterHelper;

    @Autowired
    private DeleteKubeClusterHelper deleteKubeClusterHelper;

    @Autowired
    private GetAllKubeClusterHelper getAllKubeClusterHelper;

    @Autowired
    private GetKubeClusterHelper getKubeClusterHelper;

    @Autowired
    private GetActiveKubeClusterListHelper getActiveKubeClusterListHelper;

    /**
     * Create Public Vpc.
     *
     * @param input the entity to save
     * @return the persisted entity
     */
    public ResponseDTO createKubeCluster(CreateKubeClusterInputDTO input, User requester, ObjectId actionId) {
        log.debug("REST request to create Public Vpc : {}", input);
        return (ResponseDTO) createKubeClusterHelper.execute(input, requester, actionId);
    }

    /**
     * Update Public Vpc.
     *
     * @param input the entity to save
     * @return the persisted entity
     */
    public ResponseDTO updateKubeCluster(UpdateKubeClusterInputDTO input, User requester, ObjectId actionId) {
        log.debug("Request to update Public Vpc : {}", input);
        return (ResponseDTO) updateKubeClusterHelper.execute(input, requester, actionId);
    }

    /**
     * Delete Public Vpc.
     *
     * @param kubeClusterId the id of the entity
     */
    public ResponseDTO deleteKubeCluster(User requester, ObjectId actionId, ObjectId kubeClusterId) {
        log.debug("Request to delete Public Vpc : {}", kubeClusterId);
        return (ResponseDTO) deleteKubeClusterHelper.execute(new GetObjectInputDTO(kubeClusterId), requester, actionId);
    }

    /**
     * Get Public Vpc By Id.
     *
     * @param environmentOptionId the id of the entity
     */
    public ResponseDTO getKubeCluster(User requester, ObjectId environmentOptionId) {
        log.debug("Request to get Public Vpc : {}", environmentOptionId);
        return (ResponseDTO) getKubeClusterHelper.execute(new GetObjectInputDTO(environmentOptionId), requester);
    }

    /**
     * Get All Public Vpc List
     *
     * @param requester the id of the entity
     */
    public ResponseDTO getKubeClusterList(User requester, GetListFilterInput input, Pageable pageable) {
        log.debug("Request to get all Public Vpc : {}", requester);
        return (ResponseDTO) getAllKubeClusterHelper.execute(input, requester, null, pageable);
    }

    /**
     * Get Active Public Vpc List
     *
     * @param requester the id of the entity
     */
    public ResponseDTO getActiveKubeClusterList(User requester, GetListFilterInput input, Pageable pageable) {
        log.debug("Request to get all active Public Vpc : {}", requester);
        return (ResponseDTO) getActiveKubeClusterListHelper.execute(input, requester, null, pageable);
    }
}
