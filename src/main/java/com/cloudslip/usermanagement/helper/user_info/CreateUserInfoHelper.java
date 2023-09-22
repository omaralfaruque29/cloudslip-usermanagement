package com.cloudslip.usermanagement.helper.user_info;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.SaveUserInfoDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Company;
import com.cloudslip.usermanagement.model.Organization;
import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.repository.CompanyRepository;
import com.cloudslip.usermanagement.repository.OrganizationRepository;
import com.cloudslip.usermanagement.repository.TeamRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class CreateUserInfoHelper extends AbstractHelper {
    private final Logger log = LoggerFactory.getLogger(CreateUserInfoHelper.class);

    private SaveUserInfoDTO input;

    private Company currentUserCompany;
    private Optional<Company> providedUserCompany;

    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private final UserInfoRepository userInfoRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private TeamRepository teamRepository;

    public CreateUserInfoHelper(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }


    public void init(BaseInput input, Object... extraParams) {
        this.input = (SaveUserInfoDTO) input;
        this.setOutput(output);
    }


    protected void checkPermission() {
        if (requester == null || (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requester.hasAuthority(Authority.ROLE_ADMIN))) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        Optional<UserInfo> requesterInfo = userInfoRepository.findByUserId(requester.getObjectId());
        if(!requesterInfo.isPresent()) {
            output.generateErrorResponse(String.format("User Info for the Requester doesn't exist!!!"));
            throw new ApiErrorException(this.getClass().getName());
        }
        if(userInfoRepository.findByEmail(input.getEmail()) != null) {
            output.generateErrorResponse(String.format("User with the email '%s' already exists", input.getEmail()));
            throw new ApiErrorException(this.getClass().getName());
        }
        currentUserCompany = requesterInfo.get().getCompany();

        if(currentUserCompany == null && requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && input.getCompanyId() != null) {
            providedUserCompany = companyRepository.findById(input.getCompanyId());
            if(!providedUserCompany.isPresent() && !providedUserCompany.get().isValid()) {
                output.generateErrorResponse("No company found with the given company id");
                throw new ApiErrorException(this.getClass().getName());
            }
        }
    }


    protected void doPerform() {
        UserInfo userInfo = new UserInfo();
        if(providedUserCompany != null){
            userInfo.setCompany(providedUserCompany.get());
        } else if(currentUserCompany != null){
            userInfo.setCompany(currentUserCompany);
        }
        userInfo.setEmail(input.getEmail());
        userInfo.setId(ObjectId.get());
        userInfo.setFirstName(input.getFirstName());
        userInfo.setLastName(input.getLastName());
        userInfo.setUserId(input.getUserId());
        userInfo.setUpdatedBy(requester.getId());
        userInfo.setCreateDate(String.valueOf(LocalDateTime.now()));
        if(input.getOrganizationId() != null){
            Organization organization = organizationRepository.findById(input.getOrganizationId()).get();
            if(organization != null) userInfo.setOrganization(organization);
        }
        if(input.getTeamIdList() != null){
            ArrayList<Team> teams = new ArrayList<>();
            for (ObjectId objectId : input.getTeamIdList()){
                Team team = teamRepository.findById(objectId).get();
                if (team != null ){
                    teams.add(team);
                }
            }
            userInfo.setTeams(teams);
        }
        userInfoRepository.save(userInfo);
        output.generateSuccessResponse(userInfo, String.format("User '%s' is created", userInfo.getEmail()));
    }


    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
