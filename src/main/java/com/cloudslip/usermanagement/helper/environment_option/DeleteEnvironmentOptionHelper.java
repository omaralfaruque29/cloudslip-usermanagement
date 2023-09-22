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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeleteEnvironmentOptionHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(DeleteEnvironmentOptionHelper.class);

    private GetObjectInputDTO environmentOptionObject;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    EnvironmentOptionRepository environmentOptionRepository;

    private Optional<EnvironmentOption> environmentOption;


    public void init(BaseInput input, Object... extraParams) {
        this.environmentOptionObject = (GetObjectInputDTO) input;
        this.setOutput(output);
    }

    protected void checkPermission() {
        if (requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
        environmentOption = environmentOptionRepository.findByIdAndStatus(environmentOptionObject.getId(), Status.V);
        if(!environmentOption.isPresent()) {
            output.generateErrorResponse(String.format("No Environment Option found to delete with the id '%s'!",environmentOptionObject.getId()));
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void doPerform() throws ApiErrorException {

        environmentOption.get().setStatus(Status.D);
        environmentOption.get().setEnabled(false);
        environmentOption.get().setUpdatedBy(requester.getUsername());
        environmentOption.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
        environmentOption.get().setLastUpdateActionId(actionId);
        environmentOptionRepository.save(environmentOption.get());
        output.generateSuccessResponse(null, "Environment Options successfully has been deleted");
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {
    }
}
