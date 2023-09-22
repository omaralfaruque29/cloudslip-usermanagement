package com.cloudslip.usermanagement.helper.organization;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Organization;
import com.cloudslip.usermanagement.repository.OrganizationRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeleteOrganizationHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(DeleteOrganizationHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private OrganizationRepository organizationRepository;


    protected void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
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
            output.generateErrorResponse("Id is missing in params!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void doPerform() {
        Optional<Organization> organization = organizationRepository.findById(input.getId());
        if(organization.isPresent()) {
            organization.get().setUpdatedBy(requester.getUsername());
            organization.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
            organization.get().setLastUpdateActionId(actionId);
            organizationRepository.delete(organization.get());

            deleteEverythingForOrganization(organization.get().getObjectId());

            output.generateSuccessResponse(null, String.format("Organization '%s' has been deleted", organization.get().getName()));
        } else {
            output.generateErrorResponse("Organization not found to delete!");
        }
    }

    private void deleteEverythingForOrganization(ObjectId organizationId) {
        //TODO: Delete all team, application, application environment, app commits, app commit states, everything for the organization after 24 hours.
    }

    protected void postPerformCheck() {

    }

    protected void doRollback() {

    }

}
