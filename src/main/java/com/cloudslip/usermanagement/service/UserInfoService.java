package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.helper.user_info.*;
import com.cloudslip.usermanagement.model.*;
import com.cloudslip.usermanagement.repository.CompanyRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service Implementation for managing User.
 */
@Service
@Transactional
public class UserInfoService {

    private final Logger log = LoggerFactory.getLogger(UserInfoService.class);

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private RegisterUserInfoHelper registerUserInfoHelper;

    @Autowired
    private CreateUserInfoHelper createUserInfoHelper;

    @Autowired
    private UpdateUserInfoHelper updateUserInfoHelper;

    @Autowired
    private GetUserInfoListHelper getUserInfoListHelper;

    @Autowired
    private GetUserInfoHelper getUserInfoHelper;

    @Autowired
    private DeleteUserInfoHelper deleteUserInfoHelper;

    @Autowired
    private AddUsersToTeamHelper addUsersToTeamHelper;

    @Autowired
    private RemoveUsersToTeamHelper removeUsersToTeamHelper;

    @Autowired
    private UpdateUserTeamHelper updateUserTeamHelper;


    /**
     * Create a User Info.
     *
     * @param input the entity to create
     * @return the persisted entity
     */
    public ResponseDTO create(SaveUserInfoDTO input, User requester, ObjectId actionId) {
        log.debug("Request to create User Info : {}", input);
        return (ResponseDTO) createUserInfoHelper.execute(input, requester, actionId);
    }

    /**
     * Register a User Info.
     *
     * @param input the entity to register
     * @return the persisted entity
     */
    public ResponseDTO register(SaveUserInfoDTO input, User requester, ObjectId actionId) {
        log.debug("Request to create User Info : {}", input);
        return (ResponseDTO) registerUserInfoHelper.execute(input, requester, actionId);
    }


    /**
     * Update a User Info.
     *
     * @param input the entity to update
     * @return the persisted entity
     */
    public ResponseDTO update(UpdateUserInfoDTO input, User requester, ObjectId actionId) {
        log.debug("Request to update Organization : {}", input);
        return (ResponseDTO) updateUserInfoHelper.execute(input, requester, actionId);
    }

    /**
     * Get all the User Info.
     *
     * @param requester the current user information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public ResponseDTO findAll(GetListFilterInput input, User requester, Pageable pageable) {
        log.debug("Request to get all User Info");
        return (ResponseDTO) getUserInfoListHelper.execute(input, requester, null, pageable);
    }


    /**
     * Get one User Info by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ResponseDTO findById(ObjectId id, User requester) {
        log.debug("Request to get Organization : {}", id);
        return (ResponseDTO) getUserInfoHelper.execute(new GetObjectInputDTO(id), requester);
    }

    /**
     * Add User To team.
     *
     * @param input the entity to addUsersToTeam
     * @return the persisted entity
     */
    public ResponseDTO addUsersToTeam(SaveUsersToTeamDTO input, User requester, ObjectId actionId) {
        log.debug("Request to create User To Team : {}", input);
        return (ResponseDTO) addUsersToTeamHelper.execute(input,requester,actionId);
    }

    /**
     * Remove User From team.
     *
     * @param input the entity to removeUsersFromTeam
     * @return the persisted entity
     */
    public ResponseDTO removeUsersFromTeam(RemoveUsersFromTeamDTO input, User requester, ObjectId actionId) {
        log.debug("Request to Remove User From Team : {}", input);
        return (ResponseDTO) removeUsersToTeamHelper.execute(input, requester, actionId);
    }


    /**
     * Delete the User Info by id.
     *
     * @param id the id of the entity
     */
    public ResponseDTO delete(ObjectId id, User requester, ObjectId actionId) {
        log.debug("Request to delete Organization : {}", id);
        return (ResponseDTO) deleteUserInfoHelper.execute(new GetObjectInputDTO(id), requester, actionId);
    }

    /**
     * Update a User TeamList.
     *
     * @param input the entity to updateTeam
     * @return the persisted entity
     */
    public ResponseDTO updateUserTeamList(UpdateUserTeamListDTO input, User requester, ObjectId actionId) {
        log.debug("Request to update Organization : {}", input);
        return (ResponseDTO) updateUserTeamHelper.execute(input, requester, actionId);
    }

}
