package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.helper.company.*;
import com.cloudslip.usermanagement.model.User;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Company.
 */
@Service
@Transactional
public class CompanyService {

    private final Logger log = LoggerFactory.getLogger(CompanyService.class);

    @Autowired
    private CreateCompanyHelper createCompanyHelper;

    @Autowired
    private UpdateCompanyHelper updateCompanyHelper;

    @Autowired
    private SaveCompanyGitInfoHelper saveCompanyGitInfoHelper;

    @Autowired
    private SaveCompanyDockerhubInfoHelper saveCompanyDockerhubInfoHelper;

    @Autowired
    private GetCompanyListHelper getCompanyListHelper;

    @Autowired
    private GetCompanyHelper getCompanyHelper;

    @Autowired
    private DeleteCompanyHelper deleteCompanyHelper;


    /**
     * Create a company.
     *
     * @param input the entity to create
     * @return the persisted entity
     */
    public ResponseDTO create(SaveCompanyDTO input, User requester, ObjectId actionId) {
        log.debug("Request to create Company : {}", input);
        return (ResponseDTO) createCompanyHelper.execute(input, requester, actionId);
    }


    /**
     * Update a company.
     *
     * @param input the entity to update
     * @return the persisted entity
     */
    public ResponseDTO update(UpdateCompanyDTO input, User requester, ObjectId actionId ) {
        log.debug("Request to update Company : {}", input);
        return (ResponseDTO) updateCompanyHelper.execute(input, requester, actionId);
    }


    /**
     * Save company git info.
     *
     * @param input the git info to save
     * @return the persisted entity
     */
    public ResponseDTO saveCompanyGitInfo(SaveCompanyGitInfoDTO input, User requester, ObjectId actionId) {
        log.debug("Request to save Company Github Info : {}", input);
        return (ResponseDTO) saveCompanyGitInfoHelper.execute(input, requester, actionId);

    }


    /**
     * Save company docker hub info.
     *
     * @param input the docker hub info to save
     * @return the persisted entity
     */
    public ResponseDTO saveCompanyDockerhubInfo(SaveCompanyDockerHubInfoInputDTO input, User requester, ObjectId actionId) {
        log.debug("Request to save Company DockerHub Info : {}", input);
        return (ResponseDTO) saveCompanyDockerhubInfoHelper.execute(input, requester, actionId);

    }


    /**
     * Get all the companies.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public ResponseDTO findAll(GetListFilterInput input, User requester, Pageable pageable) {
        log.debug("Request to get all Companies");
        return (ResponseDTO) getCompanyListHelper.execute(input, requester, null, pageable);
    }


    /**
     * Get one company by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ResponseDTO findById(User requester, ObjectId id) {
        log.debug("Request to get Company : {}", id);
        return (ResponseDTO) getCompanyHelper.execute(new GetObjectInputDTO(id), requester);
    }


    /**
     * Delete the company by id.
     *
     * @param id the id of the entity
     */
    public ResponseDTO delete(ObjectId id, User requester, ObjectId actionId) {
        log.debug("Request to delete Company : {}", id);
        return (ResponseDTO) deleteCompanyHelper.execute(new GetObjectInputDTO(id), requester, actionId);
    }
}
