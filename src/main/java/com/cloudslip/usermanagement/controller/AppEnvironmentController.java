package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.dto.app_environment.AddAppEnvironmentsDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.vpcresourceupdate.VpcResourceUpdateDTO;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.AppEnvironmentService;
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
public class AppEnvironmentController {

    private final Logger log = LoggerFactory.getLogger(AppEnvironmentController.class);

    @Autowired
    AppEnvironmentService appEnvironmentService;

    @RequestMapping(value = "/app-env/get-environments", method = RequestMethod.POST)
    public ResponseEntity<?> getEnvironments(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr,
                                             @Valid @RequestBody AddAppEnvironmentsDTO input) throws URISyntaxException {
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        log.debug("REST request to get Environments and VPCs : {}", input);
        ResponseDTO response = appEnvironmentService.getResponse(input, requester);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/app-env/update-vpc-resource", method = RequestMethod.POST)
    public ResponseEntity<?> updateVpcResourcesForEachEnv(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId,
                                                @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr,
                                                @Valid @RequestBody VpcResourceUpdateDTO input) throws URISyntaxException {
        log.debug("REST request to update Vpc after adding the environments : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = appEnvironmentService.updateVpcResource(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
