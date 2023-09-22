package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.dto.CreateRegionInputDTO;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.UpdateRegionInputDTO;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.RegionService;
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
public class RegionController {

    private final Logger log = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private RegionService regionService;


    @RequestMapping(value = "/region/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody CreateRegionInputDTO input) throws URISyntaxException {
        log.debug("REST request to save Region : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = regionService.create(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/region/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id, @Valid @RequestBody UpdateRegionInputDTO input) throws URISyntaxException {
        log.debug("REST request to update Region : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        input.setId(id);
        ResponseDTO result = regionService.update(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/region/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id) {
        log.debug("REST request to get Region : {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = regionService.findById(id, requester);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/region/get-list", method = RequestMethod.GET)
    public ResponseEntity<?> getList(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Nullable GetListFilterInput input, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get a page of Region");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = regionService.findAll(input, requester, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/region/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable ObjectId id) {
        log.debug("REST request to delete Region : {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = regionService.delete(id, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
