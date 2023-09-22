package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.constant.ListFetchMode;
import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.helper.vpc.*;
import com.cloudslip.usermanagement.model.*;
import com.cloudslip.usermanagement.repository.VpcRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing Vpc.
 */
@Service
@Transactional
public class VpcService {

    private final Logger log = LoggerFactory.getLogger(VpcService.class);

    private final VpcRepository vpcRepository;

    @Autowired
    private CreateVpcHelper createVpcHelper;

    @Autowired
    private UpdateVpcHelper updateVpcHelper;

    @Autowired
    private UpdateVpcStatusHelper updateVpcStatusHelper;

    @Autowired
    private GetVpcHelper getVpcHelper;

    @Autowired
    private GetVpcListHelper getVpcListHelper;

    @Autowired
    private DeleteVpcHelper deleteVpcHelper;

    public VpcService(VpcRepository vpcRepository) {
        this.vpcRepository = vpcRepository;
    }

    /**
     * Save a vpc.
     *
     * @param vpc the entity to save
     * @return the persisted entity
     */
    public Vpc save(Vpc vpc) {
        log.debug("Request to save Vpc : {}", vpc);
        return vpcRepository.save(vpc);
    }


    /**
     * Create a vpc.
     *
     * @param input the entity to create
     * @return the persisted entity
     */
    public ResponseDTO create(SaveVpcDTO input, User requester, ObjectId actionId) {
        log.debug("Request to create Vpc : {}", input);
        return (ResponseDTO) createVpcHelper.execute(input, requester, actionId);
    }

    /**
     * Update a vpc.
     *
     * @param input the entity to create
     * @return the persisted entity
     */
    public ResponseDTO update(UpdateVpcDTO input, User requester, ObjectId actionId) {
        log.debug("Request to update Vpc : {}", input);
        return (ResponseDTO) updateVpcHelper.execute(input, requester, actionId);
    }

    /**
     * Update a vpc.
     *
     * @param input the entity to create
     * @return the persisted entity
     */
    public ResponseDTO updateStatus(UpdateVpcStatusInputDTO input, User requester, ObjectId actionId) {
        log.debug("Request to update Vpc : {}", input);
        return (ResponseDTO) updateVpcStatusHelper.execute(input, requester, actionId);
    }

    /**
     * Get all the clusters.
     *
     * @param requester the current user information
     * @param input the list fetching filter inputs
     * @param pageable the pagination information
     * @return the list of entities
     */
    public ResponseDTO findAll(GetListFilterInput input, User requester, Pageable pageable) {
        log.debug("Request to get all VPCs");
        return (ResponseDTO) getVpcListHelper.execute(input, requester, pageable);
    }

    @Transactional(readOnly = true)
    public ResponseDTO findAll(User currentUser, GetListFilterInput input, Pageable pageable) {
        log.debug("Request to get all VPCs");
        if (currentUser.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            if(input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
                return new ResponseDTO<Page<Vpc>>().generateSuccessResponse(vpcRepository.findAll(pageable));
            } else if(input.getFetchMode() != null || input.getFetchMode().equals(ListFetchMode.ALL)) {
                return new ResponseDTO<List<Vpc>>().generateSuccessResponse(vpcRepository.findAll());
            }
        }
        return new ResponseDTO().generateErrorResponse("Invalid params in fetch mode");
    }


    /**
     * Get one vpc by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    public ResponseDTO findById(User requester, ObjectId id) {
        log.debug("Request to get Vpc : {}", id);
        return (ResponseDTO) getVpcHelper.execute(new GetObjectInputDTO(id), requester);
    }

    /**
     * Delete the vpc by id.
     *
     * @param id the id of the entity
     */
    public ResponseDTO delete(User requester, ObjectId actionId, ObjectId id) {
        log.debug("Request to get Vpc : {}", id);
        return (ResponseDTO) deleteVpcHelper.execute(new GetObjectInputDTO(id), requester, actionId);
    }
}
