package com.cloudslip.usermanagement.helper.user_info;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeleteUserInfoHelper extends AbstractHelper {
    private final Logger log = LoggerFactory.getLogger(DeleteUserInfoHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private UserInfoRepository userInfoRepository;


    protected void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
    }

    protected void checkPermission() {
        if (requester == null || ( (requester.hasAuthority(Authority.ROLE_DEV) || requester.hasAuthority(Authority.ROLE_OPS)) || requester.hasAuthority(Authority.ROLE_AGENT_SERVICE))) {
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
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(input.getId());
        if(!userInfo.isPresent()) {
            output.generateErrorResponse("No User found to delete with the given id");
        }
        userInfoRepository.delete(userInfo.get());
        output.generateSuccessResponse(String.format("User '%s' has been deleted", userInfo.get().getFirstName()));

    }


    protected void postPerformCheck() {

    }

    protected void doRollback() {

    }
}
