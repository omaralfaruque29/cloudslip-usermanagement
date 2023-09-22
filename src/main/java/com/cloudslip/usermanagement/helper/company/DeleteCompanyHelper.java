package com.cloudslip.usermanagement.helper.company;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
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

@Service
public class DeleteCompanyHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(DeleteCompanyHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private CompanyRepository companyRepository;


    protected void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
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
            output.generateErrorResponse("Id is missing in params!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void doPerform() {
        Optional<Company> company = companyRepository.findById(input.getId());
        if(company.isPresent()) {
            company.get().setUpdatedBy(requester.getUsername());
            company.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
            company.get().setLastUpdateActionId(actionId);
            companyRepository.delete(company.get());

            deleteEverythingForCompany(company.get().getObjectId());

            output.generateSuccessResponse(null, String.format("Company '%s' has been deleted", company.get().getName()));
        } else {
            output.generateErrorResponse("Company not found to delete!");
        }
    }

    protected void doRollback() {

    }

    private void deleteEverythingForCompany(ObjectId companyId) {
        //TODO: Disable all the users of the Company instantly and Delete all organization, team, user, company environment, application, app commits, app commit states, everything for the company after 24 hours.
    }

    protected void postPerformCheck() {

    }

}
