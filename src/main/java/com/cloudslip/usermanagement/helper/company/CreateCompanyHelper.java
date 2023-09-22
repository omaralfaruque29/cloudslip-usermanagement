package com.cloudslip.usermanagement.helper.company;

import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.ResponseStatus;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Company;
import com.cloudslip.usermanagement.model.EnvironmentOption;
import com.cloudslip.usermanagement.model.Organization;
import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.repository.CompanyRepository;
import com.cloudslip.usermanagement.repository.EnvironmentOptionRepository;
import com.cloudslip.usermanagement.repository.OrganizationRepository;
import com.cloudslip.usermanagement.repository.TeamRepository;
import com.cloudslip.usermanagement.service.EnvironmentOptionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class CreateCompanyHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(CreateCompanyHelper.class);

    private SaveCompanyDTO input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EnvironmentOptionRepository environmentOptionRepository;

    @Autowired
    private EnvironmentOptionService environmentOptionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private TeamRepository teamRepository;


    public void init(BaseInput input, Object... extraParams) {
        this.input = (SaveCompanyDTO) input;
        this.setOutput(output);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected void checkPermission() {
        if(requester != null && (requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) || requester.hasAuthority(Authority.ANONYMOUS))) {
            //continue
        } else {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
        this.checkRequiredFields();
        Optional<Company> existingCompanyWithSameName = companyRepository.findByNameIgnoreCase(input.getName());
        if (existingCompanyWithSameName.isPresent()) {
            output.generateErrorResponse(String.format("Company with the name '%s' already exists", input.getName()));
            throw new ApiErrorException(this.getClass().getName());
        }
        if (input.getWebsite() != null && !input.getWebsite().equals("") && !this.checkWebsitePattern(input.getWebsite())) {
            output.generateErrorResponse("Invalid Pattern for Website Url!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void doPerform() {
        Company company = new Company();
        company.setName(input.getName());
        company.setAddress(input.getAddress());
        company.setBusinessEmail(input.getBusinessEmail());
        company.setWebsite(input.getWebsite());
        company.setPhoneNo(input.getPhoneNo());
        company.setEnabled(true);
        company.setCreatedBy(requester.getUsername());
        company.setCreateDate(String.valueOf(LocalDateTime.now()));
        company.setCreateActionId(actionId);
        company = companyRepository.save(company);

        Organization organization = new Organization();
        organization.setName("default-org");
        organization.setCompany(company);
        organization.setDescription("Deafault organization of " + company.getName());
        organization.setCreatedBy("system");
        organization.setCreateDate(String.valueOf(LocalDateTime.now()));
        organizationRepository.save(organization);

        Team team = new Team();
        team.setName("default-team");
        team.setOrganization(organization);
        team.setCompanyId(company.getObjectId());
        team.setDescription("Default team of " + organization.getName());
        team.setCreatedBy("system");
        team.setCreateDate(String.valueOf(LocalDateTime.now()));
        teamRepository.save(team);

        output.generateSuccessResponse(company, String.format("Company '%s' has been created", company.getName()));
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    private boolean checkWebsitePattern(String websiteUrl) {
        String pattern = "^((ftp|http|https):\\/\\/)?(www.)?(?!.*[\\%\\/\\\\\\&\\?\\,\\'\\;\\:\\!\\-_]{2})(?!.*?##)(?!.*(ftp|http|https|www.))[a-zA-Z0-9][a-zA-Z0-9_-]+(\\.[a-zA-Z_-]{2,}+)+((\\/)[\\w#-_]*)*(\\/\\w+\\?[a-zA-Z0-9_]+=\\w+(&[a-zA-Z0-9_]+=\\w+)*)?$";
        Matcher m = Pattern.compile(pattern).matcher(websiteUrl);
        return m.matches();
    }

    private void checkRequiredFields() {
        if (input.getName() == null || input.getName().equals("")) {
            output.generateErrorResponse("Company Name is Required!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }
}
