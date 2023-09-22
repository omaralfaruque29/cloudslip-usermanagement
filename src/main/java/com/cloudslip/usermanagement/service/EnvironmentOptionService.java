package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.helper.environment_option.*;
import com.cloudslip.usermanagement.model.User;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing EnvironmentOption.
 */
@Service
@Transactional
public class EnvironmentOptionService {

    private final Logger log = LoggerFactory.getLogger(EnvironmentOptionService.class);

    @Autowired
    private CreateEnvironmentOptionHelper createEnvironmentOptionHelper;

    @Autowired
    private UpdateEnvironmentOptionHelper updateEnvironmentOptionHelper;

    @Autowired
    private DeleteEnvironmentOptionHelper deleteEnvironmentOptionHelper;

    @Autowired
    private GetAllEnvironmentOptionHelper getAllEnvironmentOptionHelper;

    @Autowired
    private GetEnvironmentOptionHelper getEnvironmentOptionHelper;

    @Autowired
    private GetActiveEnvironmentOptionListHelper getActiveEnvironmentOptionListHelper;
    /**
     * Create Environment Option.
     *
     * @param input the entity to save
     * @return the persisted entity
     */
    public ResponseDTO createEnvironmentOption(CreateEnvironmentOptionDTO input, User requester, ObjectId actionId) {
        log.debug("REST request to create Environments option : {}", input);
        return (ResponseDTO) createEnvironmentOptionHelper.execute(input, requester, actionId);
    }

    /**
     * Update Environment Option.
     *
     * @param input the entity to save
     * @return the persisted entity
     */
    public ResponseDTO updateEnvironmentOption(UpdateEnvironmentOptionDTO input, User requester, ObjectId actionId) {
        log.debug("Request to update environment option : {}", input);
        return (ResponseDTO) updateEnvironmentOptionHelper.execute(input, requester, actionId);
    }

    /**
     * Delete Environment Option.
     *
     * @param environmentOptionId the id of the entity
     */
    public ResponseDTO deleteEnvironmentOption(User requester, ObjectId actionId ,ObjectId environmentOptionId) {
        log.debug("Request to delete Environment Option : {}", environmentOptionId);
        return (ResponseDTO) deleteEnvironmentOptionHelper.execute(new GetObjectInputDTO(environmentOptionId), requester, actionId);
    }

    /**
     * Get Environment Option By Id.
     *
     * @param environmentOptionId the id of the entity
     */
    public ResponseDTO getEnvironmentOption(User requester, ObjectId environmentOptionId) {
        log.debug("Request to get Environment Option : {}", environmentOptionId);
        return (ResponseDTO) getEnvironmentOptionHelper.execute(new GetObjectInputDTO(environmentOptionId), requester);
    }

    /**
     * Get All Environment Option List
     *
     * @param requester the id of the entity
     */
    public ResponseDTO getEnvironmentOptionList(User requester, GetListFilterInput input, Pageable pageable) {
        log.debug("Request to get all Environment Options : {}", requester);
        return (ResponseDTO) getAllEnvironmentOptionHelper.execute(input, requester, null, pageable);
    }

    /**
     * Get Active Environment Option List
     *
     * @param requester the id of the entity
     */
    public ResponseDTO getActiveEnvironmentOptionList(User requester, GetListFilterInput input, Pageable pageable) {
        log.debug("Request to get all Active Environment Options : {}", requester);
        return (ResponseDTO) getActiveEnvironmentOptionListHelper.execute(input, requester, null, pageable);
    }

}
