package com.cloudslip.usermanagement.controller;

import com.cloudslip.usermanagement.constant.HttpHeader;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.service.GitService;
import com.cloudslip.usermanagement.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GitController {

    private final Logger log = LoggerFactory.getLogger(GitController.class);

    @Autowired
    private GitService gitService;

    @RequestMapping(value = "/git/get-repositories", method = RequestMethod.GET)
    public ResponseEntity<?> getGitRepositories(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr) {
        log.debug("REST request to get git repositories");
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = gitService.getGitRepositories(requester);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/git/get-branches/{repo}", method = RequestMethod.GET)
    public ResponseEntity<?> getGitBranches(@RequestHeader(value = HttpHeader.CURRENT_USER) String currentUserStr, @PathVariable("repo") String repo) {
        log.debug("REST request to get git branches", repo);
        User requester = Utils.generateUserFromJsonStr(currentUserStr);
        ResponseDTO result = gitService.getGitBranches(requester, repo);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
