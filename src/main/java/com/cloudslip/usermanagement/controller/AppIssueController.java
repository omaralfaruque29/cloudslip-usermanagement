package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.ApplicationProperties;
import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.core.CustomRestTemplate;
import com.cloudslip.usermanagement.dto.appIsssue.CreateAppIssueDTO;
import com.cloudslip.usermanagement.dto.appIsssue.UpdateAppIssueDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.ResponseStatus;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.AppIssueService;
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
public class AppIssueController {

    private final Logger log = LoggerFactory.getLogger(AppIssueController.class);

    @Autowired
    private CustomRestTemplate restTemplate;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private AppIssueService appIssueService;

    @RequestMapping(value = "/app-issue/create", method = RequestMethod.POST)
    public ResponseEntity<?> createAppIssue(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId,
                                               @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr,
                                               @Valid @RequestBody CreateAppIssueDTO input) throws URISyntaxException {
        log.debug("REST request to create an issue for an application : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        HttpHeaders headers = Utils.generateHttpHeaders(currentUserStr);
        HttpEntity<String> request = new HttpEntity<>("parameter", headers);
        if (input.getApplicationId() == null) {
            return new ResponseEntity<>(new ResponseDTO<>().generateErrorResponse("Application Id required!"), HttpStatus.OK);
        }
        ResponseEntity<ResponseDTO> response = restTemplate.exchange(applicationProperties.getPipelineServiceBaseUrl() + "api/application/get-app-team/" + input.getApplicationId(), HttpMethod.GET, request, ResponseDTO.class);
        if (response.getBody().getStatus() == ResponseStatus.error) {
            return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
        }
        ResponseDTO result = appIssueService.create(input, requester, actionId, response.getBody());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/app-issue/update", method = RequestMethod.PUT)
    public ResponseEntity<?> updateAppIssue(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId,
                                                    @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr,
                                                    @Valid @RequestBody UpdateAppIssueDTO input) throws URISyntaxException {
        log.debug("REST request to update an app issue : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = appIssueService.update(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/app-issue/get/{app-issue-id}", method = RequestMethod.GET)
    public ResponseEntity<?> getAppIssue(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("app-issue-id") ObjectId appIssueId) {
        log.debug("REST request to get App Issue");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = appIssueService.find(appIssueId, requester);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/app-issue/get-list/{app-id}", method = RequestMethod.GET)
    public ResponseEntity<?> getAppIssueListByApplication(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("app-id") ObjectId applicationId) {
        log.debug("REST request to get App Issue List By Application");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = appIssueService.findAll(applicationId, requester);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/app-issue/get-allowed-user-list/{app-id}", method = RequestMethod.GET)
    public ResponseEntity<?> getAllowedUserForTaggingByApplication(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("app-id") ObjectId applicationId) {
        log.debug("REST request to get all allowed user list for tagging By Application");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        HttpHeaders headers = Utils.generateHttpHeaders(currentUserStr);
        HttpEntity<String> request = new HttpEntity<>("parameter", headers);
        if (applicationId == null) {
            return new ResponseEntity<>(new ResponseDTO<>().generateErrorResponse("Application Id required!"), HttpStatus.OK);
        }
        ResponseEntity<ResponseDTO> response = restTemplate.exchange(applicationProperties.getPipelineServiceBaseUrl() + "api/application/get-app-team/" + applicationId, HttpMethod.GET, request, ResponseDTO.class);
        if (response.getBody().getStatus() == ResponseStatus.error) {
            return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
        }
        ResponseDTO result = appIssueService.getAllAllowedUserInfo(applicationId, requester, response.getBody());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/app-issue/delete/{app-issue-id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAppIssue(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("app-issue-id") ObjectId appIssueId) {
        log.debug("REST request to delete Application Issue: {}", appIssueId);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = appIssueService.delete(appIssueId, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
