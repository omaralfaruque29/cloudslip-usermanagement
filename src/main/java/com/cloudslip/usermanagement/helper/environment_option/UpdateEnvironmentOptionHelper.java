package com.cloudslip.usermanagement.helper.environment_option;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.UpdateEnvironmentOptionDTO;
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
public class UpdateEnvironmentOptionHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(UpdateEnvironmentOptionHelper.class);

    @Autowired
    private EnvironmentOptionRepository environmentOptionRepository;

    private UpdateEnvironmentOptionDTO input;
    private ResponseDTO output = new ResponseDTO();

    private Optional<EnvironmentOption> environmentOption;


    public void init(BaseInput input, Object... extraParams) {
        this.input = (UpdateEnvironmentOptionDTO) input;
        this.setOutput(output);
    }

    protected void checkPermission() {
        if (requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
        environmentOption = environmentOptionRepository.findByIdAndStatus(input.getId(), Status.V);
        if (!environmentOption.isPresent()) {
            output.generateErrorResponse(String.format("Environment Option not found with Id '%s'!", input.getId()));
            throw new ApiErrorException(this.getClass().getName());
        }

    }

    protected void doPerform() {
        if(input.isEnabled()) {
            environmentOption.get().setEnabled(false);
        } else {
            environmentOption.get().setEnabled(true);
        }
        environmentOption.get().setUpdatedBy(requester.getUsername());
        environmentOption.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
        environmentOption.get().setLastUpdateActionId(actionId);
        environmentOptionRepository.save(environmentOption.get());
        if(input.isEnabled()) {
            output.generateSuccessResponse(environmentOption.get(), String.format("Environment option '%s' successfully has been disabled", environmentOption.get().getName()));
        } else {
            output.generateSuccessResponse(environmentOption.get(), String.format("Environment option '%s' successfully has been enabled", environmentOption.get().getName()));
        }
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {
    }
}
