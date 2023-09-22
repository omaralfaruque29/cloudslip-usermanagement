package com.cloudslip.usermanagement.helper.environment_option;

import com.cloudslip.usermanagement.constant.ListFetchMode;
import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.repository.EnvironmentOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GetActiveEnvironmentOptionListHelper extends AbstractHelper {

    private GetListFilterInput input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    EnvironmentOptionRepository environmentOptionRepository;

    private Pageable pageable;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetListFilterInput) input;
        this.setOutput(output);
        pageable = (Pageable) extraParams[0];
    }

    protected void checkPermission() {
        if (requester == null || requester.hasAnyAuthority(Authority.ROLE_AGENT_SERVICE) || requester.hasAnyAuthority(Authority.ROLE_DEV) || requester.hasAnyAuthority(Authority.ROLE_OPS) || requester.hasAnyAuthority(Authority.ROLE_GIT_AGENT)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
    }

    protected void doPerform() {
        if(input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
            output.generateSuccessResponse(environmentOptionRepository.findAllByStatusAndIsEnabled(pageable, Status.V, true));
        } else if(input.getFetchMode() != null || input.getFetchMode().equals(ListFetchMode.ALL)) {
            output.generateSuccessResponse(environmentOptionRepository.findAllByStatusAndIsEnabled(Status.V,true));
        }
    }

    protected void postPerformCheck() {
    }


    protected void doRollback() {

    }
}
