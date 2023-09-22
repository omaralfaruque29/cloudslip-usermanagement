package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.CreateEnvironmentOptionDTO;
import com.cloudslip.usermanagement.dto.UpdateEnvironmentOptionDTO;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.EnvironmentOptionService;
import com.cloudslip.usermanagement.util.Utils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api")
public class EnvironmentOptionController {

    private final Logger log = LoggerFactory.getLogger(EnvironmentOptionController.class);

    private final EnvironmentOptionService environmentOptionService;

    public EnvironmentOptionController(EnvironmentOptionService environmentOptionService) {
        this.environmentOptionService = environmentOptionService;
    }

    @RequestMapping(value = "/environment-option", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody CreateEnvironmentOptionDTO input) throws URISyntaxException {
        log.debug("REST request to save EnvironmentOption : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = environmentOptionService.createEnvironmentOption(input,requester,actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/environment-option/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody UpdateEnvironmentOptionDTO input) throws URISyntaxException {
        log.debug("REST request to update EnvironmentOption : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = environmentOptionService.updateEnvironmentOption(input,requester,actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/environment-option/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id) {
        log.debug("REST request to get EnvironmentOption : {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = environmentOptionService.getEnvironmentOption(requester, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/environment-option", method = RequestMethod.GET)
    public ResponseEntity<?> getList(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Nullable GetListFilterInput input, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get a page of EnvironmentOption");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = environmentOptionService.getEnvironmentOptionList(requester, input, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/environment-option/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr,  @PathVariable ObjectId id) {
        log.debug("REST request to delete EnvironmentOption : {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = environmentOptionService.deleteEnvironmentOption(requester, actionId, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/active-environment-option/active", method = RequestMethod.GET)
    public ResponseEntity<?> getActiveEnvList(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Nullable GetListFilterInput input, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get a page of Active EnvironmentOption");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = environmentOptionService.getActiveEnvironmentOptionList(requester, input, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
