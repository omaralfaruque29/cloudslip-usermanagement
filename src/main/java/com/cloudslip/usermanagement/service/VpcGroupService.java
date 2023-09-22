package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.VpcGroup.CreateVpcGroupDTO;
import com.cloudslip.usermanagement.helper.vpc_group.*;
import com.cloudslip.usermanagement.dto.VpcGroup.UpdateVpcGroupDTO;
import com.cloudslip.usermanagement.model.User;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VpcGroupService {

    private final Logger log = LoggerFactory.getLogger(VpcGroupService.class);

    @Autowired
    private CreateVpcGroupHelper createVpcGroupHelper;

    @Autowired
    private UpdateVpcGroupHelper updateVpcGroupHelper;

    @Autowired
    private DeleteVpcGroupHelper deleteVpcGroupHelper;

    @Autowired
    private GetAllVpcGroupHelper getAllVpcGroupHelper;

    @Autowired
    private GetVpcGroupHelper getVpcGroupHelper;

    /**
     * Create a vpc group.
     *
     * @param input the entity to create
     * @return the persisted entity
     */
    public ResponseDTO create(CreateVpcGroupDTO input, User requester, ObjectId actionId) {
        log.debug("Request to create Vpc group: {}", input);
        return (ResponseDTO) createVpcGroupHelper.execute(input, requester, actionId);
    }

    /**
     * Update a vpc group.
     *
     * @param input the entity to create
     * @return the persisted entity
     */
    public ResponseDTO update(UpdateVpcGroupDTO input, User requester, ObjectId actionId) {
        log.debug("Request to update Vpc group: {}", input);
        return (ResponseDTO) updateVpcGroupHelper.execute(input, requester, actionId);
    }


    /**
     * Delete the vpc group by id.
     *
     * @param id the id of the entity
     */
    public ResponseDTO delete(User requester, ObjectId actionId, ObjectId id) {
        log.debug("Request to delete Vpc Group: {}", id);
        return (ResponseDTO) deleteVpcGroupHelper.execute(new GetObjectInputDTO(id), requester, actionId);
    }

    /**
     * Get all the Vpc Group.
     *
     * @param requester the current user information
     * @param input the list fetching filter inputs
     * @param pageable the pagination information
     * @return the list of entities
     */
    public ResponseDTO findAll(GetListFilterInput input, User requester, Pageable pageable) {
        log.debug("Request to get all VPCs");
        return (ResponseDTO) getAllVpcGroupHelper.execute(input, requester, pageable);
    }

    /**
     * Get one vpc group by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    public ResponseDTO findById(User requester, ObjectId id) {
        log.debug("Request to get Vpc Group: {}", id);
        return (ResponseDTO) getVpcGroupHelper.execute(new GetObjectInputDTO(id), requester);
    }
}
