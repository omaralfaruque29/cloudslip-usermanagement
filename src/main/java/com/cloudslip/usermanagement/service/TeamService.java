package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.constant.ListFetchMode;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.SaveTeamDTO;
import com.cloudslip.usermanagement.dto.UpdateTeamDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.model.*;
import com.cloudslip.usermanagement.repository.OrganizationRepository;
import com.cloudslip.usermanagement.repository.TeamRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing Team.
 */
@Service
@Transactional
public class TeamService {

    private final Logger log = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    /**
     * Save a team.
     *
     * @param team the entity to save
     * @return the persisted entity
     */
    public Team save(Team team) {
        log.debug("Request to save Team : {}", team);
        return teamRepository.save(team);
    }

    /**
     * Create a team.
     *
     * @param dto the entity to create
     * @return the persisted entity
     */
    public ResponseDTO create(User currentUser, SaveTeamDTO dto) {
        log.debug("Request to create Team : {}", dto);

        Team oldTeam = teamRepository.findByNameIgnoreCase(dto.getName());
        if(oldTeam != null && oldTeam.isValid()) {
            return new ResponseDTO().generateErrorResponse(String.format("Team with the name '%s' already exists", dto.getName()));
        }

        Team team = new Team();
        team.setId(ObjectId.get());
        team.setName(dto.getName());
        team.setDescription(dto.getDescription());

        if(dto.getOrganizationId() == null) {
            return new ResponseDTO().generateErrorResponse("Organization Id is required");
        }

        Optional<Organization> organization = organizationRepository.findById(dto.getOrganizationId());

        if(!organization.isPresent()) {
            return new ResponseDTO().generateErrorResponse(String.format("No Organization found with the id - %s", dto.getOrganizationId().toHexString()));
        }

        team.setCompanyId(organization.get().getCompany().getObjectId());
        team.setOrganization(organization.get());
        team.setCreatedBy(currentUser.getUsername());
        team.setCreateDate(String.valueOf(LocalDateTime.now()));
        return new ResponseDTO<Team>().generateSuccessResponse(save(team), String.format("A team '%s' is created", team.getName()));
    }

    /**
     * Update a team.
     *
     * @param dto the entity to update
     * @return the persisted entity
     */
    public ResponseDTO update(User currentUser, UpdateTeamDTO dto) {
        log.debug("Request to update Team : {}", dto);
        Optional<Team> team = teamRepository.findById(dto.getId());
        if(!team.isPresent()) {
            return new ResponseDTO().generateErrorResponse(String.format("No team found with the id - %s", dto.getId().toHexString()));
        } else {
            Team updatingTeam = team.get();
            Team oldTeam = teamRepository.findByNameIgnoreCase(dto.getName());
            if(oldTeam != null && oldTeam.isValid() && !oldTeam.getId().equals(updatingTeam.getId())) {
                return new ResponseDTO().generateErrorResponse(String.format("Team with the name '%s' already exists", dto.getName()));
            }
            updatingTeam.setName(dto.getName());
            updatingTeam.setDescription(dto.getDescription());

            if(dto.getOrganizationId() == null) {
                return new ResponseDTO().generateErrorResponse("Organization Id is required");
            }

            Optional<Organization> organization = organizationRepository.findById(dto.getOrganizationId());

            if(!organization.isPresent()) {
                return new ResponseDTO().generateErrorResponse(String.format("No Organization found with the id - %s", dto.getOrganizationId().toHexString()));
            }
            updatingTeam.setCompanyId(organization.get().getCompany().getObjectId());
            updatingTeam.setOrganization(organization.get());
            updatingTeam.setUpdatedBy(currentUser.getUsername());
            updatingTeam.setUpdateDate(String.valueOf(LocalDateTime.now()));
            return new ResponseDTO<Team>().generateSuccessResponse(save(updatingTeam), String.format("Team '%s' is updated", updatingTeam.getName()));
        }
    }

    /**
     * Get all the teams.
     *
     * @param currentUser the current user information
     * @param input the list fetching filters
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public ResponseDTO findAll(User currentUser, GetListFilterInput input, Pageable pageable) {
        log.debug("Request to get all Teams");
        UserInfo currentUserInfo = userInfoRepository.findByUserId(currentUser.getObjectId()).get();
        String organizationId = input.getFilterParamsMap() == null ? null : input.getFilterParamsMap().getOrDefault("organizationId", null);
        if(input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
            if(currentUser.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
                return new ResponseDTO<Page<Team>>().generateSuccessResponse(teamRepository.findAll(pageable));
            } else if(currentUser.hasAuthority(Authority.ROLE_ADMIN)) {
                if(organizationId != null) {
                    return new ResponseDTO<Page<Team>>().generateSuccessResponse(teamRepository.findAllByOrganization_Id(pageable, new ObjectId(organizationId)));
                } else {
                    return new ResponseDTO<Page<Team>>().generateSuccessResponse(teamRepository.findAllByOrganization_Company_Id(pageable, currentUserInfo.getCompany().getObjectId()));
                }
            } else if(currentUserInfo.getOrganization() != null) {
                return new ResponseDTO<Page<Team>>().generateSuccessResponse(teamRepository.findAllByOrganization_Id(pageable, currentUserInfo.getOrganization().getObjectId()));
            } else {
                return new ResponseDTO().generateErrorResponse("Requesting user must be under an Organization");
            }
        } else if(input.getFetchMode().equals(ListFetchMode.ALL)) {
            if(currentUser.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
                return new ResponseDTO<List<Team>>().generateSuccessResponse(teamRepository.findAll());
            } else if(currentUser.hasAuthority(Authority.ROLE_ADMIN)){
                if(organizationId != null) {
                    return new ResponseDTO<List<Team>>().generateSuccessResponse(teamRepository.findAllByOrganization_Id(new ObjectId(organizationId)));
                } else {
                    return new ResponseDTO<List<Team>>().generateSuccessResponse(teamRepository.findAllByOrganization_Company_Id(currentUserInfo.getCompany().getObjectId()));
                }
            } else if(currentUserInfo.getOrganization() != null){
                return new ResponseDTO<List<Team>>().generateSuccessResponse(teamRepository.findAllByOrganization_Id(currentUserInfo.getOrganization().getObjectId()));
            } else {
                return new ResponseDTO().generateErrorResponse("Requesting user must be under an Organization");
            }
        }
        return new ResponseDTO().generateErrorResponse("Invalid params in fetch mode");
    }


    /**
     * Get one team by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ResponseDTO findById(User currentUser, ObjectId id) {
        log.debug("Request to get Team : {}", id);
        Optional<Team> team = teamRepository.findById(id);
        if(!team.isPresent()) {
            return new ResponseDTO<Team>().generateErrorResponse(String.format("No team found with the id - %s", id.toHexString()));
        }
        return new ResponseDTO<Team>().generateSuccessResponse(team.get());
    }

    /**
     * Delete the team by id.
     *
     * @param id the id of the entity
     */
    public ResponseDTO delete(User currentUser, ObjectId id) {
        log.debug("Request to delete Team : {}", id);
        Optional<Team> team = teamRepository.findById(id);
        if(!team.isPresent()) {
            return new ResponseDTO().generateErrorResponse(String.format("No Team found to delete with the id - %s", id.toHexString()));
        }
        teamRepository.delete(team.get());
        return new ResponseDTO<Team>().generateSuccessResponse(null, String.format("Team '%s' has been deleted", team.get().getName()));
    }
}
