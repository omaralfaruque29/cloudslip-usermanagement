package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.VpcGroup.CreateVpcGroupDTO;
import com.cloudslip.usermanagement.dto.VpcGroup.UpdateVpcGroupDTO;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.VpcGroupService;
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
public class VpcGroupController {

    private final Logger log = LoggerFactory.getLogger(VpcGroupController.class);

    @Autowired
    private VpcGroupService vpcGroupService;

    @RequestMapping(value = "/vpc-group/create", method = RequestMethod.POST)
    public ResponseEntity<?> createVpcGroup(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody CreateVpcGroupDTO input) throws URISyntaxException {
        log.debug("REST request to Create Vpc  Group : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = vpcGroupService.create(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/vpc-group/update", method = RequestMethod.PUT)
    public ResponseEntity<?> updateVpc(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody UpdateVpcGroupDTO input) throws URISyntaxException {
        log.debug("REST request to Update Vpc  Group : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = vpcGroupService.update(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/vpc-group/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteVpcGroup(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable ObjectId id) {
        log.debug("REST request to delete Vpc Group : {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = vpcGroupService.delete(requester, actionId, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/vpc-group/get-list", method = RequestMethod.GET)
    public ResponseEntity<?> getVpcGroupList(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Nullable GetListFilterInput input, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get a page of Vpc group");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = vpcGroupService.findAll(input, requester, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/vpc-group/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getVpcGroup(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id) {
        log.debug("REST request to get Vpc Group: {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = vpcGroupService.findById(requester, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
