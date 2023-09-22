package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.app_issue_comment.AddAppIssueCommentDTO;
import com.cloudslip.usermanagement.dto.app_issue_comment.UpdateAppIssueCommentDTO;
import com.cloudslip.usermanagement.helper.app_issue_comment.*;
import com.cloudslip.usermanagement.model.User;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppIssueCommentService {

    private final Logger log = LoggerFactory.getLogger(AppIssueCommentService.class);

    @Autowired
    private AddAppIssueCommentHelper addAppIssueCommentHelper;

    @Autowired
    private UpdateAppIssueCommentHelper updateAppIssueCommentHelper;

    @Autowired
    private GetAppIssueCommentHelper getAppIssueCommentHelper;

    @Autowired
    private GetAllAppIssueCommentHelper getAllAppIssueCommentHelper;

    @Autowired
    private DeleteAppIssueCommentHelper deleteAppIssueCommentHelper;

    /**
     * Add a comment to an Application Issue
     *
     * @param input the entity to save
     * @return the persisted entity
     */
    public ResponseDTO add(AddAppIssueCommentDTO input, User requester, ObjectId actionId) {
        log.debug("REST request to create an Application Issue : {}", input);
        return (ResponseDTO) addAppIssueCommentHelper.execute(input, requester, actionId);
    }

    /**
     * Update a comment to an Application Issue
     *
     * @param input the entity to save
     * @return the persisted entity
     */
    public ResponseDTO update(UpdateAppIssueCommentDTO input, User requester, ObjectId actionId) {
        log.debug("REST request to create an Application Issue : {}", input);
        return (ResponseDTO) updateAppIssueCommentHelper.execute(input, requester, actionId);
    }

    /**
     * Get App Issue Comment by id
     *
     * @param appIssueCommentId to get
     * @return the persisted entity
     */
    public ResponseDTO find(ObjectId appIssueCommentId, User requester) {
        log.debug("REST request to get Application Issue");
        return (ResponseDTO) getAppIssueCommentHelper.execute(new GetObjectInputDTO(appIssueCommentId), requester);
    }

    /**
     * Get All Application Comment Issue List By Application Issue
     *
     * @param appIssueId to get
     * @return the persisted entity
     */
    public ResponseDTO findAll(ObjectId appIssueId, User requester) {
        log.debug("REST request to get Application Issue Comment List By App Issue");
        return (ResponseDTO) getAllAppIssueCommentHelper.execute(new GetObjectInputDTO(appIssueId), requester);
    }

    /**
     * Delete an Application Issue Comment
     *
     * @param appIssueCommentId the entity to save
     * @return the persisted entity
     */
    public ResponseDTO delete(ObjectId appIssueCommentId, User requester, ObjectId actionId) {
        log.debug("REST request to delete Application Issue: {}", appIssueCommentId);
        return (ResponseDTO) deleteAppIssueCommentHelper.execute(new GetObjectInputDTO(appIssueCommentId), requester, actionId);
    }
}
