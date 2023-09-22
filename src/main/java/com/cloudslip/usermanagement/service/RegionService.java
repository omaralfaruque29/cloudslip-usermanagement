package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.helper.region.*;
import com.cloudslip.usermanagement.model.User;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing Region.
 */
@Service
@Transactional
public class RegionService {

    private final Logger log = LoggerFactory.getLogger(RegionService.class);

    @Autowired
    private CreateRegionHelper createRegionHelper;

    @Autowired
    private UpdateRegionHelper updateRegionHelper;

    @Autowired
    private GetRegionListHelper getRegionListHelper;

    @Autowired
    private GetRegionHelper getRegionHelper;

    @Autowired
    private DeleteRegionHelper deleteRegionHelper;



    /**
     * Create a region.
     *
     * @param input the entity to create
     * @return the persisted entity
     */
    public ResponseDTO create(CreateRegionInputDTO input, User requester, ObjectId actionId) {
        log.debug("Request to create Region : {}", input);
        return (ResponseDTO) createRegionHelper.execute(input, requester, actionId);
    }

    /**
     * Update a region.
     *
     * @param input the entity to update
     * @return the persisted entity
     */
    public ResponseDTO update(UpdateRegionInputDTO input, User requester, ObjectId actionId) {
        log.debug("Request to update Region : {}", input);
        return (ResponseDTO) updateRegionHelper.execute(input, requester, actionId);
    }

    /**
     * Get all the regions.
     *
     * @param input the list fetching filter inputs
     * @param requester the current user information
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public ResponseDTO findAll(GetListFilterInput input, User requester, Pageable pageable) {
        log.debug("Request to get all Regions");
        return (ResponseDTO) getRegionListHelper.execute(input, requester, null, pageable);
    }


    /**
     * Get one region by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ResponseDTO findById(ObjectId id, User requester) {
        log.debug("Request to get Region : {}", id);
        return (ResponseDTO) getRegionHelper.execute(new GetObjectInputDTO(id), requester);
    }

    /**
     * Delete the region by id.
     *
     * @param id the id of the entity
     */
    public ResponseDTO delete(ObjectId id, User requester, ObjectId actionId) {
        log.debug("Request to delete Region : {}", id);
        return (ResponseDTO) deleteRegionHelper.execute(new GetObjectInputDTO(id), requester, actionId);
    }
}
