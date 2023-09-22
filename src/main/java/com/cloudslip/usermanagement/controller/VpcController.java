package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.VpcService;
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
public class VpcController {

    private final Logger log = LoggerFactory.getLogger(VpcController.class);

    @Autowired
    private VpcService vpcService;


    @RequestMapping(value = "/vpc/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getVpc(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id) {
        log.debug("REST request to get Vpc: {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = vpcService.findById(requester, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/vpc/get-list", method = RequestMethod.GET)
    public ResponseEntity<?> getVpcList(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Nullable GetListFilterInput input, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get a page of Vpc");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = vpcService.findAll(input, requester, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/vpc/create", method = RequestMethod.POST)
    public ResponseEntity<?> createVpc(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody SaveVpcDTO input) throws URISyntaxException {
        log.debug("REST request to Create Vpc  : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = vpcService.create(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/vpc/update", method = RequestMethod.PUT)
    public ResponseEntity<?> updateVpc(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody UpdateVpcDTO input) throws URISyntaxException {
        log.debug("REST request to update Vpc : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = vpcService.update(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/vpc/update-status", method = RequestMethod.POST)
    public ResponseEntity<?> updateVpc(@RequestBody UpdateVpcStatusInputDTO input) throws URISyntaxException {
        log.debug("REST request to update Vpc Status : {}", input);
        ResponseDTO result = vpcService.updateStatus(input, null, null);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/vpc/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteVpc(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable ObjectId id) {
        log.debug("REST request to delete Vpc : {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = vpcService.delete(requester, actionId, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
