package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.ApplicationService;
import com.cloudslip.usermanagement.util.Utils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    @Autowired
    ApplicationService applicationService;

    private final Logger log = LoggerFactory.getLogger(ApplicationController.class);

    @RequestMapping(value = "/application/create-app/{team-id}", method = RequestMethod.GET)
    public ResponseEntity<?> getCreateApplicationResponse(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr,
                                     @PathVariable("team-id") ObjectId input) {
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        log.debug("REST request to get Create Application Response by team id : {}", input);
        ResponseDTO response = applicationService.getResponse(input, requester);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/application/get-team/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getTeam(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr,
                                     @PathVariable("id") ObjectId input) {
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        log.debug("REST request to get Team by id : {}", input);
        ResponseDTO response = applicationService.getTeamByUser(input, requester);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/application/get-team-list", method = RequestMethod.GET)
    public ResponseEntity<?> getTeamList(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr) {
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        log.debug("REST request to get List Of Teams : {}", requester.getOrganizationId());
        ResponseDTO response = applicationService.getTeamList(requester);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/application/get-user-and-company-info", method = RequestMethod.POST)
    public ResponseEntity<?> getUserAndCompanyInfo(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr,
                                                   @Valid @RequestBody GetObjectInputDTO companyId) {
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        log.debug("REST request to get User by id : {}", requester.getId());
        ResponseDTO user = applicationService.getUserAndCompanyInfo(requester, companyId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
