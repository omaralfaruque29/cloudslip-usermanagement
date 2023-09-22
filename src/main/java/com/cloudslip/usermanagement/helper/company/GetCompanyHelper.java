package com.cloudslip.usermanagement.helper.company;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Company;
import com.cloudslip.usermanagement.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetCompanyHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(GetCompanyHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO<Company> output = new ResponseDTO<Company>();

    @Autowired
    private CompanyRepository companyRepository;


    protected void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
    }

    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS) || requester.hasAuthority(Authority.ROLE_AGENT_SERVICE)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }
    }

    protected void checkValidity() {
        if(input.getId() == null) {
            output.generateErrorResponse("Id is missing in params!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }
    }

    protected void doPerform() {
        Optional<Company> company = companyRepository.findById(input.getId());
        if(!company.isPresent()) {
            output.generateErrorResponse(String.format("No Company found with the id - %s", input.getId().toHexString()));
        }
        if(!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requester.getCompanyId().equals(company.get().getObjectId())) {
            output.generateErrorResponse("Permission denied!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }
        output.generateSuccessResponse(company.get());
    }

    protected void postPerformCheck() {

    }

    protected void doRollback() {

    }

}
