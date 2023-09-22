package com.cloudslip.usermanagement.helper.organization;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.UpdateOrganizationDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Organization;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.repository.OrganizationRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class UpdateOrganizationHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(UpdateOrganizationHelper.class);

    private UpdateOrganizationDTO input;
    private ResponseDTO output = new ResponseDTO();
    private Organization updatingOrganization = null;
    private UserInfo requesterInfo = null;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;


    public void init(BaseInput input, Object... extraParams) {
        this.input = (UpdateOrganizationDTO) input;
        this.setOutput(output);
    }


    protected void checkPermission() {
        if (requester == null || (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requester.hasAuthority(Authority.ROLE_ADMIN))) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        if(input.getId() == null) {
            output.generateErrorResponse("Organization Id is missing in the input");
            throw new ApiErrorException(this.getClass().getName());
        }

        Optional<Organization> organization = organizationRepository.findById(input.getId());

        if(!(organization.isPresent())) {
            output.generateErrorResponse(String.format("No Organization found with the id - %s", input.getId().toHexString()));
            throw new ApiErrorException(this.getClass().getName());
        }

        updatingOrganization = organization.get();

        Organization existingOrganization = organizationRepository.findByNameIgnoreCase(input.getName());
        if (existingOrganization != null && existingOrganization.isValid() && !existingOrganization.getId().equals(updatingOrganization.getId())) {
            output.generateErrorResponse(String.format("Cannot update! Organization with the name '%s' already exists.", input.getName()));
            throw new ApiErrorException(this.getClass().getName());
        }

        requesterInfo = userInfoRepository.findByUserId(requester.getObjectId()).get();

        if(!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requesterInfo.getCompany().getObjectId().equals(updatingOrganization.getCompany().getObjectId())) {
            output.generateErrorResponse("Unauthorized user! You don't belong to the expected Company!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() {
        updatingOrganization.setName(input.getName());
        updatingOrganization.setDescription(input.getDescription());
        updatingOrganization.setGitDirectory(input.getGitDirectory());
        updatingOrganization.setUpdatedBy(requester.getUsername());
        updatingOrganization.setUpdateDate(String.valueOf(LocalDateTime.now()));
        updatingOrganization.setLastUpdateActionId(actionId);
        updatingOrganization = organizationRepository.save(updatingOrganization);
        output.generateSuccessResponse(updatingOrganization.getId(), String.format("Organization '%s' is updated", updatingOrganization.getName()));
    }


    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
