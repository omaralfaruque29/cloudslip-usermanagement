package com.cloudslip.usermanagement.helper.organization;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
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

import java.util.Optional;

@Service
public class GetOrganizationHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(GetOrganizationHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO<Organization> output = new ResponseDTO<Organization>();
    private UserInfo requesterInfo;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;


    protected void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
    }

    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
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
        if(!organization.isPresent()) {
            output.generateErrorResponse(String.format("No Organization found with the id - %s", input.getId().toHexString()));
        }

        requesterInfo = userInfoRepository.findByUserId(requester.getObjectId()).get();

        if(requesterInfo.getCompany().getObjectId().equals(organization.get().getCompany().getObjectId())) {
            output.generateSuccessResponse(organization.get());
        } else {
            output.generateErrorResponse("Unauthorized user! You don't belong to the expected Company!");
        }
    }

    protected void postPerformCheck() {

    }

    protected void doRollback() {

    }

}
