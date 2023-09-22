package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.service.UserInfoService;
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
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api")
public class UserInfoController {

    private final Logger log = LoggerFactory.getLogger(UserInfoController.class);

    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/user-info/register", method = RequestMethod.POST)
    public ResponseDTO register(@Nullable @RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @Nullable @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody SaveUserInfoDTO input) throws URISyntaxException {
        log.debug("REST request to save User : {}", input);
        User requester = null;
        if(currentUserStr != null && currentUserStr != "") {
            requester = Utils.generateUserFromJsonStr(currentUserStr);
        }
        ResponseDTO response = userInfoService.register(input, requester, actionId);
        return new ResponseDTO(response.getData(),response.getStatus(),response.getMessage());
    }

    @RequestMapping(value = "/user-info", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody SaveUserInfoDTO input) throws URISyntaxException {
        log.debug("REST request to save User : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result;
        if (input.getUserId() == null) {
            result = new ResponseDTO().generateErrorResponse("A new user info must have a User ID");
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        result = userInfoService.create(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/user-info/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id, @Valid @RequestBody UpdateUserInfoDTO input) throws URISyntaxException {
        log.debug("REST request to update User : {}", input);
        if (id == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("UserInfo", "idnotexists", "An existing user must have an ID")).body(null);
        }
        input.setId(id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = userInfoService.update(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @RequestMapping(value = "/user-info/add-users-to-team", method = RequestMethod.POST)
    public ResponseEntity<?> addUsersToTeam(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody SaveUsersToTeamDTO input) throws URISyntaxException {
        log.debug("REST request to update User : {}", input);
        if (input.getTeamId() == null && input.getUserList() == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("UserInfo", "noidfound", "No Team id found")).body(null);
        }
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = userInfoService.addUsersToTeam(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/user-info/remove-users-from-team", method = RequestMethod.POST)
    public ResponseEntity<?> removeUsersFromTeam(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody RemoveUsersFromTeamDTO input) throws URISyntaxException {
        log.debug("REST request to update User : {}", input);
        if (input.getTeamId() == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("UserInfo", "noidfound", "No Team id found")).body(null);
        }
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = userInfoService.removeUsersFromTeam(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/user-info/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserInfo(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id) {
        log.debug("REST request to get User by id : {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO user = userInfoService.findById(id, requester);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @RequestMapping(value = "/user-info", method = RequestMethod.GET)
    public ResponseEntity<?> getList(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Nullable GetListFilterInput input, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get a page of User");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = userInfoService.findAll(input, requester, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/user-info/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable ObjectId id) {
        log.debug("REST request to delete User by currentUserId: {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = userInfoService.delete(id, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/user-info/update-teamList", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTeam(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody UpdateUserTeamListDTO input) throws URISyntaxException {
        log.debug("REST request to update User : {}", input);
        if (input.getUserId() == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("UserInfo", "noidfound", "No id found")).body(null);
        }
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = userInfoService.updateUserTeamList(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
