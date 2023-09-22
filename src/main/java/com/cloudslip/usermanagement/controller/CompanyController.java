package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.CompanyService;
import com.cloudslip.usermanagement.util.Utils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.net.URISyntaxException;


@RestController
@RequestMapping("/api")
public class CompanyController {

    private final Logger log = LoggerFactory.getLogger(CompanyController.class);

    @Autowired
    private CompanyService companyService;

    @RequestMapping(value = "/company", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @RequestBody SaveCompanyDTO input) throws URISyntaxException {
        log.debug("REST request to save Company : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = companyService.create(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/company/save-git-info", method = RequestMethod.POST)
    public ResponseEntity<?> saveGitInfo(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @RequestBody SaveCompanyGitInfoDTO input) throws URISyntaxException {
        log.debug("REST request to save Company Github Info : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = companyService.saveCompanyGitInfo(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/company/save-docker-hub-info", method = RequestMethod.POST)
    public ResponseEntity<?> saveDockerHubInfo(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @RequestBody SaveCompanyDockerHubInfoInputDTO input) throws URISyntaxException {
        log.debug("REST request to save Company DockerHub Info : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = companyService.saveCompanyDockerhubInfo(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/company/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id, @Valid @RequestBody UpdateCompanyDTO input) throws URISyntaxException {
        log.debug("REST request to update Company : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        input.setId(id);
        ResponseDTO result = companyService.update(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/company/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id) {
        log.debug("REST request to get Company : {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = companyService.findById(requester, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/company", method = RequestMethod.GET)
    public ResponseEntity<?> getList(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Nullable GetListFilterInput input, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get a page of Company");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = companyService.findAll(input, requester, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/company/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable ObjectId id) {
        log.debug("REST request to delete Company : {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = companyService.delete(id, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
