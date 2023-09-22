package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.KubeClusterService;
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
public class KubeClusterController {

    private final Logger log = LoggerFactory.getLogger(KubeClusterController.class);

    @Autowired
    private KubeClusterService kubeClusterService;


    @RequestMapping(value = "/kube-cluster/create", method = RequestMethod.POST)
    public ResponseEntity<?> createKubeCluster(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody CreateKubeClusterInputDTO input) throws URISyntaxException {
        log.debug("REST request to Create Public Vpc  : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = kubeClusterService.createKubeCluster(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/kube-cluster/update", method = RequestMethod.PUT)
    public ResponseEntity<?> updateKubeCluster(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Valid @RequestBody UpdateKubeClusterInputDTO input) throws URISyntaxException {
        log.debug("REST request to update Public Vpc : {}", input);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = kubeClusterService.updateKubeCluster(input, requester, actionId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/kube-cluster/delete/{kubeClusterId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteKubeCluster(@RequestHeader(value = HttpHeader.ACTION_ID) ObjectId actionId, @RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable ObjectId kubeClusterId) {
        log.debug("REST request to delete Public Vpc : {}", kubeClusterId);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = kubeClusterService.deleteKubeCluster(requester, actionId, kubeClusterId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/kube-cluster/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getKubeCluster(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("id") ObjectId id) {
        log.debug("REST request to get Public Vpc: {}", id);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = kubeClusterService.getKubeCluster(requester, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/kube-cluster/get-list", method = RequestMethod.GET)
    public ResponseEntity<?> getKubeClusterList(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Nullable GetListFilterInput input, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get a page of Kube Clusters");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = kubeClusterService.getKubeClusterList(requester, input, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/kube-cluster/get-active-list", method = RequestMethod.GET)
    public ResponseEntity<?> getActiveKubeClusterList(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @Nullable GetListFilterInput input, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get a page of active Kube Clusters");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = kubeClusterService.getActiveKubeClusterList(requester, input, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
