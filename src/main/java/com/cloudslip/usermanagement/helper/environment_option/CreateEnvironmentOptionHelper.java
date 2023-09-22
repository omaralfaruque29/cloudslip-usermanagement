package com.cloudslip.usermanagement.helper.environment_option;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.CreateEnvironmentOptionDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.EnvironmentOption;
import com.cloudslip.usermanagement.repository.EnvironmentOptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CreateEnvironmentOptionHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(CreateEnvironmentOptionHelper.class);

    private CreateEnvironmentOptionDTO input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private EnvironmentOptionRepository environmentOptionRepository;

    private Optional<EnvironmentOption> environmentOption;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (CreateEnvironmentOptionDTO) input;
        this.setOutput(output);
    }

    protected void checkPermission() {
        if(requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
        environmentOption = environmentOptionRepository.findByShortNameIgnoreCaseAndStatus(input.getShortName(), Status.V);
        if (environmentOption.isPresent()) {
            output.generateErrorResponse(String.format(" Duplicate Environment Option Short Name '%s'!", input.getShortName()));
            throw new ApiErrorException(this.getClass().getName());
        }

        environmentOption = environmentOptionRepository.findByNameIgnoreCaseAndStatus(input.getName(), Status.V);
        if (environmentOption.isPresent()) {
            output.generateErrorResponse(String.format(" Duplicate Environment Option Name '%s'!", input.getName()));
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void doPerform() {
        Optional<EnvironmentOption> latestEnvironmentOption = environmentOptionRepository.findTopByOrderByOrderNoDesc();
        EnvironmentOption environmentOption = new EnvironmentOption();
        environmentOption.setName(input.getName());
        environmentOption.setShortName(input.getShortName());
        environmentOption.setDescription(input.getDescription());
        environmentOption.setEnabled(true);
        if(latestEnvironmentOption.isPresent()) {
            environmentOption.setOrderNo(latestEnvironmentOption.get().getOrderNo() + 1);
        } else {
            environmentOption.setOrderNo(1);
        }
        environmentOption.setCreatedBy(requester.getUsername());
        environmentOption.setCreateDate(String.valueOf(LocalDateTime.now()));
        environmentOption.setCreateActionId(actionId);
        environmentOptionRepository.save(environmentOption);
        output.generateSuccessResponse(environmentOption, String.format(" Environment Option '%s' successfully has been created", input.getName()));

    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {
    }

}
