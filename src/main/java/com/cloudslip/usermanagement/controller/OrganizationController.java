package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.SaveOrganizationDTO;
import com.cloudslip.usermanagement.dto.UpdateOrganizationDTO;
import com.cloudslip.usermanagement.model.GitInfo;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.OrganizationService;
import com.cloudslip.usermanagement.util.HeaderUtil;
import com.cloudslip.usermanagement.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api")
public class OrganizationController {

    private final Logger log = LoggerFactory.getLogger(OrganizationController.class);

    @Autowired
    private OrganizationService organizationService;


    @RequestMapping(value = "/organization", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody SaveOrganizationDTO input) throws URISyntaxException {
        log.debug("REST request to save Organization : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = organizationService.create(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/organization/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id, @Valid @RequestBody UpdateOrganizationDTO input) throws URISyntaxException {
        log.debug("REST request to update Organization : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        input.setId(id);
        ResponseDTO result = organizationService.update(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/organization/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id) {
        log.debug("REST request to get Organization : {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = organizationService.findById(id, requester);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/organization", method = RequestMethod.GET)
    public ResponseEntity<?> getList(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Nullable GetListFilterInput input, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get a page of Organization");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = organizationService.findAll(input, requester, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/organization/get-my-git-directories", method = RequestMethod.GET)
    public ResponseEntity<?> getMyGitDirectories(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr) throws URISyntaxException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        log.debug("REST request to get a page of Organization");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = organizationService.getGitDirectories(requester);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/organization/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable ObjectId id) {
        log.debug("REST request to delete Organization : {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = organizationService.delete(id, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
