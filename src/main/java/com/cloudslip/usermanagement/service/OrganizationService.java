package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.helper.organization.*;
import com.cloudslip.usermanagement.model.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;

/**
 * Service Implementation for managing Organization.
 */
@Service
@Transactional
public class OrganizationService {

    private final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    @Autowired
    private AddOrganizationHelper addOrganizationHelper;

    @Autowired
    private UpdateOrganizationHelper updateOrganizationHelper;

    @Autowired
    private GetOrganizationListHelper getOrganizationListHelper;

    @Autowired
    private GetOrganizationHelper getOrganizationHelper;

    @Autowired
    private DeleteOrganizationHelper deleteOrganizationHelper;

    @Autowired
    private GetGitDirectoryListHelper getGitDirectoryListHelper;


    /**
     * Create a organization.
     *
     * @param input the entity to create
     * @return the persisted entity
     */
    public ResponseDTO create(SaveOrganizationDTO input, User requester, ObjectId actionId) {
        log.debug("Request to create Organization : {}", input);
        return (ResponseDTO) addOrganizationHelper.execute(input, requester, actionId);
    }

    /**
     * Update a organization.
     *
     * @param input the entity to update
     * @return the persisted entity
     */
    public ResponseDTO update(UpdateOrganizationDTO input, User requester, ObjectId actionId) {
        log.debug("Request to update Organization : {}", input);
        return (ResponseDTO) updateOrganizationHelper.execute(input, requester, actionId);
    }

    /**
     * Get all the organizations.
     *
     * @param input the list fetching filter inputs
     * @param requester the current user information
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public ResponseDTO findAll(GetListFilterInput input, User requester, Pageable pageable) {
        log.debug("Request to get all Organizations");
        return (ResponseDTO) getOrganizationListHelper.execute(input, requester, null, pageable);
    }


    /**
     * Get one organization by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ResponseDTO findById(ObjectId id, User requester) {
        log.debug("Request to get Organization : {}", id);
        return (ResponseDTO) getOrganizationHelper.execute(new GetObjectInputDTO(id), requester);
    }

    /**
     * Delete the organization by id.
     *
     * @param id the id of the entity
     */
    public ResponseDTO delete(ObjectId id, User requester, ObjectId actionId) {
        log.debug("Request to delete Organization : {}", id);
        return (ResponseDTO) deleteOrganizationHelper.execute(new GetObjectInputDTO(id), requester, actionId);
    }


    public ResponseDTO getGitDirectories(User requester) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return (ResponseDTO) getGitDirectoryListHelper.execute(requester);
    }
}
