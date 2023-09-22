package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.ApplicationProperties;
import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.core.CustomRestTemplate;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.app_issue_comment.AddAppIssueCommentDTO;
import com.cloudslip.usermanagement.dto.app_issue_comment.UpdateAppIssueCommentDTO;
import com.cloudslip.usermanagement.enums.ResponseStatus;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.AppIssueCommentService;
import com.cloudslip.usermanagement.util.Utils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api")
public class AppIssueCommentController {

    private final Logger log = LoggerFactory.getLogger(AppIssueCommentController.class);

    @Autowired
    private CustomRestTemplate restTemplate;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private AppIssueCommentService appIssueCommentService;


    @RequestMapping(value = "/app-issue-comment/add", method = RequestMethod.POST)
    public ResponseEntity<?> addAppIssueComment(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId,
                                                @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr,
                                                @Valid @RequestBody AddAppIssueCommentDTO input) throws URISyntaxException {
        log.debug("REST request to add comment for an issue for an application : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = appIssueCommentService.add(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/app-issue-comment/update", method = RequestMethod.PUT)
    public ResponseEntity<?> updateAppIssueComment(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId,
                                                    @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr,
                                                    @Valid @RequestBody UpdateAppIssueCommentDTO input) throws URISyntaxException {
        log.debug("REST request to update comment for an issue for an application  : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = appIssueCommentService.update(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/app-issue-comment/get/{issue-comment-id}", method = RequestMethod.GET)
    public ResponseEntity<?> getAppIssueComment(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("issue-comment-id") ObjectId appIssueCommentId) {
        log.debug("REST request to get App Issue Comment");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = appIssueCommentService.find(appIssueCommentId, requester);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/app-issue-comment/get-list/{app-issue-id}", method = RequestMethod.GET)
    public ResponseEntity<?> getAppIssueListByApplication(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("app-issue-id") ObjectId appIssueId) {
        log.debug("REST request to get Comment list for an App Issue");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = appIssueCommentService.findAll(appIssueId, requester);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/app-issue-comment/delete/{issue-comment-id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAppIssueComment(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("issue-comment-id") ObjectId appIssueCommentId) {
        log.debug("REST request to delete Application Issue Comment: {}", appIssueCommentId);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = appIssueCommentService.delete(appIssueCommentId, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
