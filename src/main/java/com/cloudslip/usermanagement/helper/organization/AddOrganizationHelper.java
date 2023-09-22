package com.cloudslip.usermanagement.helper.organization;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.SaveOrganizationDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Company;
import com.cloudslip.usermanagement.model.Organization;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.repository.CompanyRepository;
import com.cloudslip.usermanagement.repository.OrganizationRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class AddOrganizationHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(AddOrganizationHelper.class);

    private SaveOrganizationDTO input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private UserInfoRepository userInfoRepository;


    public void init(BaseInput input, Object... extraParams) {
        this.input = (SaveOrganizationDTO) input;
        this.setOutput(output);

        if(extraParams.length > 0) {
            ObjectId actionId = (ObjectId) extraParams[0];
            System.out.println(actionId.toHexString());
        }
    }


    protected void checkPermission() {
        if (requester == null || (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requester.hasAuthority(Authority.ROLE_ADMIN))) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        Organization existingOrganization = organizationRepository.findByNameIgnoreCase(input.getName());
        if (existingOrganization != null && existingOrganization.isValid()) {
            output.generateErrorResponse(String.format("Organization with the name '%s' already exists", input.getName()));
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() {
        Organization organization = new Organization();
        organization.setId(ObjectId.get());
        organization.setName(input.getName());
        organization.setDescription(input.getDescription());
        organization.setGitDirectory(input.getGitDirectory());

        Company company = getCompanyForNewOrganization();

        organization.setCompany(company);
        organization.setCreatedBy(requester.getUsername());
        organization.setCreateDate(String.valueOf(LocalDateTime.now()));
        organization.setCreateActionId(actionId);
        organization = organizationRepository.save(organization);
        output.generateSuccessResponse(organization.getId(), String.format("A new Organization '%s' is created", organization.getName()));
    }

    private Company getCompanyForNewOrganization() {
        ObjectId companyId = null;
        if(requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && input.getCompanyId() != null) {
            //If current user is a Super Admin, he can create a Organization under any Company
            companyId = input.getCompanyId();
        } else if(requester.hasAuthority(Authority.ROLE_ADMIN)) {
            //If current user is an Admin, he can create a Organization under his Company Only
            UserInfo currentUserInfo = userInfoRepository.findByUserId(new ObjectId(requester.getId())).get();
            companyId = currentUserInfo.getCompany().getObjectId();
        }
        Optional<Company> company = companyRepository.findById(companyId);
        if(company.isPresent()) {
            return company.get();
        }
        return null;
    }



    protected void postPerformCheck() {
    }


    protected void doRollback() {

    }
}
