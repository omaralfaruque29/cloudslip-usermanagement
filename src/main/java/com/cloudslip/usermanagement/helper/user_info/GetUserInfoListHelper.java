package com.cloudslip.usermanagement.helper.user_info;

import com.cloudslip.usermanagement.constant.ListFetchMode;
import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetUserInfoListHelper extends AbstractHelper {
    private final Logger log = LoggerFactory.getLogger(GetUserInfoListHelper.class);

    private GetListFilterInput input;
    private ResponseDTO output = new ResponseDTO();
    private Pageable pageable;
    private Optional<UserInfo> requesterInfo;

    @Autowired
    private UserInfoRepository userInfoRepository;


    protected void init(BaseInput input, Object... extraParams) {
        this.input = (GetListFilterInput) input;
        this.setOutput(output);
        pageable = (Pageable) extraParams[0];
    }

    protected void checkPermission() {
        if (requester == null || (requester == null || requester.hasAuthority(Authority.ANONYMOUS))) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
        requesterInfo = userInfoRepository.findByUserId(requester.getObjectId());
        if(!requesterInfo.isPresent()) {
            output.generateErrorResponse("No User Info found for the current User");
        }
    }


    protected void doPerform() {
         if(requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            if (input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
                output.generateSuccessResponse(userInfoRepository.findAll(pageable));
            } else if (input.getFetchMode().equals(ListFetchMode.ALL)) {
                output.generateSuccessResponse(userInfoRepository.findAll());
            } else {
                output.generateErrorResponse("Invalid fetch mode");
                throw new ApiErrorException(this.getClass().getName());
            }
        } else if (requester.hasAuthority(Authority.ROLE_ADMIN)){
            if (input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
                output.generateSuccessResponse(userInfoRepository.findAllByCompanyId(pageable, requesterInfo.get().getCompany().getObjectId()));
            } else if (input.getFetchMode().equals(ListFetchMode.ALL)) {
                output.generateSuccessResponse(userInfoRepository.findAllByCompanyId(requesterInfo.get().getCompany().getObjectId()));
            } else {
                output.generateErrorResponse("Invalid fetch mode");
                throw new ApiErrorException(this.getClass().getName());
            }
        }
    }


    protected void postPerformCheck() {

    }

    protected void doRollback() {

    }
}

