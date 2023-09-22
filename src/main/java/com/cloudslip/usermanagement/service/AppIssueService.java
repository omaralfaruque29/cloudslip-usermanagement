package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.dto.appIsssue.CreateAppIssueDTO;
import com.cloudslip.usermanagement.dto.appIsssue.UpdateAppIssueDTO;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.helper.app_issue.*;
import com.cloudslip.usermanagement.model.User;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppIssueService {

    private final Logger log = LoggerFactory.getLogger(AppIssueService.class);

    @Autowired
    private CreateAppIssueHelper createAppIssueHelper;

    @Autowired
    private UpdateAppIssueHelper updateAppIssueHelper;

    @Autowired
    private GetAppIssueHelper getAppIssueHelper;

    @Autowired
    private GetAppIssueListByApplicationHelper getAppIssueListByApplicationHelper;

    @Autowired
    private GetAllAllowedUserInfoForTaggingHelper getAllAllowedUserInfoForTaggingHelper;

    @Autowired
    private DeleteAppIssueHelper deleteAppIssueHelper;

    /**
     * Create an Application Issue
     *
     * @param input the entity to save
     * @return the persisted entity
     */
    public ResponseDTO create(CreateAppIssueDTO input, User requester, ObjectId actionId, ResponseDTO response) {
        log.debug("REST request to create an Application Issue : {}", input);
        return (ResponseDTO) createAppIssueHelper.execute(input, requester, actionId, response);
    }

    /**
     * Update existing Application Issue
     *
     * @param input the entity to save
     * @return the persisted entity
     */
    public ResponseDTO update(UpdateAppIssueDTO input, User requester, ObjectId actionId) {
        log.debug("REST request to update an Application Issue : {}", input);
        return (ResponseDTO) updateAppIssueHelper.execute(input, requester, actionId);
    }

    /**
     * Get App Issue by id
     *
     * @param appIssueId to get
     * @return the persisted entity
     */
    public ResponseDTO find(ObjectId appIssueId, User requester) {
        log.debug("REST request to get Application Issue");
        return (ResponseDTO) getAppIssueHelper.execute(new GetObjectInputDTO(appIssueId), requester);
    }

    /**
     * Get All Application Issue List By Application
     *
     * @param applicationId to get
     * @return the persisted entity
     */
    public ResponseDTO findAll(ObjectId applicationId, User requester) {
        log.debug("REST request to get Application Issue List By Application");
        return (ResponseDTO) getAppIssueListByApplicationHelper.execute(new GetObjectInputDTO(applicationId), requester);
    }

    /**
     * Get All Allowed User list for tagging By Application
     *
     * @param applicationId to get
     * @return the persisted entity
     */
    public ResponseDTO getAllAllowedUserInfo(ObjectId applicationId, User requester, ResponseDTO response) {
        log.debug("REST request to get Application Issue List By Application");
        return (ResponseDTO) getAllAllowedUserInfoForTaggingHelper.execute(new GetObjectInputDTO(applicationId), requester, response);
    }

    /**
     * Delete an Application Issue
     *
     * @param appIssueId the entity to save
     * @return the persisted entity
     */
    public ResponseDTO delete(ObjectId appIssueId, User requester, ObjectId actionId) {
        log.debug("REST request to delete Application Issue: {}", appIssueId);
        return (ResponseDTO) deleteAppIssueHelper.execute(new GetObjectInputDTO(appIssueId), requester, actionId);
    }
}
