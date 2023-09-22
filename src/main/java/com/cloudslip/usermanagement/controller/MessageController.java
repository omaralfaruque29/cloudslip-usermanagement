package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.MessageService;
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
public class MessageController {

    private final Logger log = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(value = "/message/send", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody SendMessageDTO dto) throws URISyntaxException {
        log.debug("REST request to save Message : {}", dto);
        User currentUser = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = messageService.create(actionId, currentUser, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/message/reply", method = RequestMethod.POST)
    public ResponseEntity<?> createReply(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody ReplyMessageDTO dto) throws URISyntaxException {
        log.debug("REST request to save Message : {}", dto);
        User currentUser = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = messageService.createReply(actionId, currentUser, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/message/get-all-by-thread/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getAllByThread(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id) {
        log.debug("REST request to get Messages : {}", id);
        User currentUser = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = messageService.findAllByMessageThread(currentUser, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/message/get-list", method = RequestMethod.GET)
    public ResponseEntity<?> getList(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Nullable GetListFilterInput input, Pageable pageable) {
        log.debug("REST request to get list of Message Threads:");
        User currentUser = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = messageService.findAll(currentUser, input, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/message/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable ObjectId id) {
        log.debug("REST request to delete Organization : {}", id);
        User currentUser = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = messageService.delete(currentUser, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
