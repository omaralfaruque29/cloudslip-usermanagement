package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.helper.Git.GetGitBranchesHelper;
import com.cloudslip.usermanagement.helper.Git.GetGitRepositoriesHelper;
import com.cloudslip.usermanagement.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloudslip.usermanagement.dto.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GitService {

    private final Logger log = LoggerFactory.getLogger(GitService.class);

    @Autowired
    private GetGitRepositoriesHelper getGitRepositoriesHelper;

    @Autowired
    private GetGitBranchesHelper getGitBranchesHelper;

    /**
     * Get all repositories from current user's company git account.
     * @return the persisted entity
     */
    public ResponseDTO getGitRepositories(User requester) {
        log.debug("Request to get all repositories from current user's company git account");
        return (ResponseDTO) getGitRepositoriesHelper.execute(requester);
    }

    /**
     * Get all branches of a repository.
     * @return the persisted entity
     */
    public ResponseDTO getGitBranches(User requester, String repo) {
        log.debug("Request to get all branches of a repository", repo);
        return (ResponseDTO) getGitBranchesHelper.execute(requester, repo);
    }
}
