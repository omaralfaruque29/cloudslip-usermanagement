package com.cloudslip.usermanagement.helper.user_info;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.SaveUserInfoDTO;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Company;
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
public class RegisterUserInfoHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(RegisterUserInfoHelper.class);

    private SaveUserInfoDTO input;

    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private CompanyRepository companyRepository;


    public void init(BaseInput input, Object... extraParams) {
        this.input = (SaveUserInfoDTO) input;
        this.setOutput(output);
    }


    protected void checkPermission() {
    }


    protected void checkValidity() {
    }


    protected void doPerform() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(ObjectId.get());
        userInfo.setEmail(input.getEmail());
        userInfo.setFirstName(input.getFirstName());
        userInfo.setLastName(input.getLastName());
        userInfo.setUserId(input.getUserId());
        userInfo.setCreateDate(String.valueOf(LocalDateTime.now()));
        if(requester != null) {
            userInfo.setCreatedBy(requester.getUsername());
        } else {
            userInfo.setCreatedBy("system");
        }
        if(input.getCompanyId() != null) {
            Optional<Company> company = companyRepository.findById(input.getCompanyId());
            if(company.isPresent()) {
                userInfo.setCompany(company.get());
            }
        }
        userInfo.setCreateActionId(actionId);
        userInfoRepository.save(userInfo);
        output.generateSuccessResponse(userInfo, String.format("User Info '%s' is created", userInfo.getEmail()));
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

}
