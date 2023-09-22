package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.SaveTeamDTO;
import com.cloudslip.usermanagement.dto.UpdateTeamDTO;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.TeamService;
import com.cloudslip.usermanagement.util.HeaderUtil;
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
public class TeamController {

    private final Logger log = LoggerFactory.getLogger(TeamController.class);

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @RequestMapping(value = "/team", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody SaveTeamDTO dto) throws URISyntaxException {
        log.debug("REST request to save Team : {}", dto);
        User currentUser = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = teamService.create(currentUser, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/team/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id, @Valid @RequestBody UpdateTeamDTO dto) throws URISyntaxException {
        log.debug("REST request to update Team : {}", dto);
        User currentUser = Utils.generateUserFromJsonStr(currentUserStr);
        if (id == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("Team", "idexists", "An existing Team must have an ID")).body(null);
        }
        dto.setId(id);
        ResponseDTO result = teamService.update(currentUser, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/team/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id) {
        log.debug("REST request to get Team : {}", id);
        User currentUser = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = teamService.findById(currentUser, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/team", method = RequestMethod.GET)
    public ResponseEntity<?> getList(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Nullable GetListFilterInput input, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get a page of Team");
        User currentUser = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = teamService.findAll(currentUser, input, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/team/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable ObjectId id) {
        log.debug("REST request to delete Team : {}", id);
        User currentUser = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = teamService.delete(currentUser, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
