package com.cloudslip.usermanagement.helper.user_info;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetUserInfoHelper extends AbstractHelper {
    private final Logger log = LoggerFactory.getLogger(GetUserInfoHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO<UserInfo> output = new ResponseDTO<UserInfo>();
    private Optional<UserInfo> requesterInfo;

    @Autowired
    private UserInfoRepository userInfoRepository;


    protected void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
    }

    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
        if(input.getId() == null) {
            output.generateErrorResponse("Id is missing in params!");
            throw new ApiErrorException(this.getClass().getName());
        }
        requesterInfo = userInfoRepository.findByUserId(input.getId());
        if(!requesterInfo.isPresent()) {
            output.generateErrorResponse(String.format("No UserInfo found with the id - %s", input.getId().toHexString()));
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void doPerform() {
        output.generateSuccessResponse(requesterInfo.get());
    }

    protected void postPerformCheck() {

    }

    protected void doRollback() {

    }
}
