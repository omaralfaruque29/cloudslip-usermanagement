package com.cloudslip.usermanagement.helper.environment_option;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.EnvironmentOption;
import com.cloudslip.usermanagement.repository.EnvironmentOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetEnvironmentOptionHelper extends AbstractHelper {

    private GetObjectInputDTO input;

    private ResponseDTO output = new ResponseDTO();

    @Autowired
    EnvironmentOptionRepository environmentOptionRepository;

    private Optional<EnvironmentOption> environmentOption;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
    }

    protected void checkPermission() {
        if(requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
        environmentOption = environmentOptionRepository.findByIdAndStatus(input.getId(), Status.V);
        if(!environmentOption.isPresent()) {
            output.generateErrorResponse(String.format("No Environment Option found with the id'%s'!",input.getId()));
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void doPerform() {
        output.generateSuccessResponse(environmentOption, "Environment Option Response");
    }


    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
