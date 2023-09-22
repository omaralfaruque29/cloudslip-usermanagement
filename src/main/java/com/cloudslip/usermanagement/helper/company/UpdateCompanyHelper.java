package com.cloudslip.usermanagement.helper.company;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.SaveCompanyDTO;
import com.cloudslip.usermanagement.dto.UpdateCompanyDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Company;
import com.cloudslip.usermanagement.repository.CompanyRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class UpdateCompanyHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(UpdateCompanyHelper.class);
    private UpdateCompanyDTO input;
    private ResponseDTO output = new ResponseDTO();
    private Company updatingCompany = null;

    @Autowired
    private CompanyRepository companyRepository;


    public void init(BaseInput input, Object... extraParams) {
        this.input = (UpdateCompanyDTO) input;
        this.setOutput(output);
    }

    protected void checkPermission() {
        if (requester == null || !requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
        if(input.getId() == null) {
            output.generateErrorResponse("Company Id is missing in the input");
            throw new ApiErrorException(this.getClass().getName());
        }
        Optional<Company> company = companyRepository.findById(input.getId());
        if(!(company.isPresent())) {
            output.generateErrorResponse(String.format("No Company found with the id - %s", input.getId().toHexString()));
            throw new ApiErrorException(this.getClass().getName());
        }
        if (input.getWebsite() != null && !input.getWebsite().equals("") && !this.checkWebsitePattern(input.getWebsite())) {
            output.generateErrorResponse("Invalid Pattern for Website Url!");
            throw new ApiErrorException(this.getClass().getName());
        }
        updatingCompany = company.get();
    }

    protected void doPerform() {
        updatingCompany.setName(input.getName());
        updatingCompany.setAddress(input.getAddress());
        updatingCompany.setBusinessEmail(input.getBusinessEmail());
        updatingCompany.setWebsite(input.getWebsite());
        updatingCompany.setPhoneNo(input.getPhoneNo());
        updatingCompany.setEnabled(input.isEnabled());
        updatingCompany.setUpdatedBy(requester.getUsername());
        updatingCompany.setUpdateDate(String.valueOf(LocalDateTime.now()));
        updatingCompany.setLastUpdateActionId(actionId);
        updatingCompany = companyRepository.save(updatingCompany);
        output.generateSuccessResponse(updatingCompany.getId(), String.format("Company '%s' is updated", updatingCompany.getName()));
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
}
