package com.cloudslip.usermanagement.helper.user_info;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.UpdateUserInfoDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.repository.CompanyRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UpdateUserInfoHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(UpdateUserInfoHelper.class);

    private UpdateUserInfoDTO input;

    @Autowired
    private CompanyRepository companyRepository;

    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private final UserInfoRepository userInfoRepository;


    public UpdateUserInfoHelper(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }


    public void init(BaseInput input, Object... extraParams) {
        this.input = (UpdateUserInfoDTO) input;
        this.setOutput(output);

        if(extraParams.length > 0) {
            ObjectId actionId = (ObjectId) extraParams[0];
            System.out.println(actionId.toHexString());
        }
    }


    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
    }


    protected void doPerform() {
        Optional<UserInfo> existingUser = userInfoRepository.findByUserId(input.getId());
        if(!existingUser.isPresent()) {
            output.generateErrorResponse(String.format("User Info for the Requester doesn't exist!!!"));
            throw new ApiErrorException(this.getClass().getName());
        }
        existingUser.get().setFirstName(input.getFirstName());
        existingUser.get().setLastName(input.getLastName());
        if(input.getCompanyId() != null){
            existingUser.get().setCompany(companyRepository.findById(input.getCompanyId()).get());
        }
        existingUser.get().setUpdatedBy(requester.getUsername());
        existingUser.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
        userInfoRepository.save(existingUser.get());
        output.generateSuccessResponse(existingUser , String.format("User '%s' is updated", existingUser.get().getFirstName()));

    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}