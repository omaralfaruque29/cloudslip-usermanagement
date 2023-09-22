package com.cloudslip.usermanagement.helper.vpc_group;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.VpcGroup;
import com.cloudslip.usermanagement.repository.VpcGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeleteVpcGroupHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(DeleteVpcGroupHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    private Optional<VpcGroup> vpcGroup;

    @Autowired
    private VpcGroupRepository vpcGroupRepository;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
    }


    protected void checkPermission() {
        if (requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN, Authority.ROLE_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        vpcGroup = vpcGroupRepository.findByIdAndStatus(input.getId(), Status.V);
        if (!vpcGroup.isPresent()) {
            output.generateErrorResponse("Vpc group does not exists!");
            throw new ApiErrorException(this.getClass().getName());
        } else if (!validateAuthority()) {
            output.generateErrorResponse("User do not have permission to access!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() {
        vpcGroup.get().setStatus(Status.D);
        vpcGroup.get().setUpdatedBy(requester.getUsername());
        vpcGroup.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
        vpcGroup.get().setLastUpdateActionId(actionId);
        vpcGroupRepository.save(vpcGroup.get());
        output.generateSuccessResponse(null, "Vpc Group has been successfully deleted");
    }



    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
    /*
        Validate if user have authority to access the vpc group
    */
    private boolean validateAuthority() {
        if (requester.hasAuthority(Authority.ROLE_ADMIN) && !requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            return requester.getCompanyId().toString().equals(vpcGroup.get().getCompanyId());
        }
        return true;
    }
}
