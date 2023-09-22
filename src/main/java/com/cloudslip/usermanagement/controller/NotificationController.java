package com.cloudslip.usermanagement.controller;
import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.NotificationService;
import com.cloudslip.usermanagement.util.Utils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.net.URISyntaxException;


@RestController
@RequestMapping("/api")
public class NotificationController {

    private final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "/notification/create", method = RequestMethod.POST)
    public ResponseEntity<?> createNotification(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody SaveNotificationDto input) throws URISyntaxException{
        log.debug("REST request to create notification : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO response = notificationService.createNotification(input, requester, actionId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/notification/get-list", method = RequestMethod.GET)
    public ResponseEntity<?> getNotificationList(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Nullable GetListFilterInput input) throws URISyntaxException{
        log.debug("REST request to get notification list :");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO response = notificationService.getNotificationList(input, requester);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/notification/count", method = RequestMethod.GET)
    public ResponseEntity<?> countUncheckedNotifications(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr){
        log.debug("REST request to count unchecked notifications : {}");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO response = notificationService.countUncheckedNotifications(requester);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/notification/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> changeNotificationStatus(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id){
        log.debug("REST request to change notification's status by notification Id", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO response = notificationService.changeNotificationStatus(id, requester);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/notification/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteNotification(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable ObjectId id){
        log.debug("REST request to delete notification by ID : {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO response = notificationService.deleteNotification(id, requester);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
