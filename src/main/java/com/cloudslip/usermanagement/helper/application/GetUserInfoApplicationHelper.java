package com.cloudslip.usermanagement.helper.application;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.UserInfoResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Company;
import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.repository.CompanyRepository;
import com.cloudslip.usermanagement.repository.TeamRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GetUserInfoApplicationHelper extends AbstractHelper {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    CompanyRepository companyRepository;

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    private Optional<UserInfo> userInfo;
    private Optional<Company> company;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
        userInfo = null;
    }


    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        userInfo = userInfoRepository.findByUserId(requester.getObjectId());
        if(!userInfo.isPresent()) {
            output.generateErrorResponse("UserInfo Not Found!");
            throw new ApiErrorException(this.getClass().getName());
        } else if (requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && input.getId() == null) {
            output.generateErrorResponse("Company Id Required!");
            throw new ApiErrorException(this.getClass().getName());
        }
        if (requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            company = companyRepository.findById(input.getId());
        } else {
            company = companyRepository.findById(userInfo.get().getCompany().getObjectId());
        }
        if (!company.isPresent()) {
            output.generateErrorResponse("Company Not Found!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() {
        List<Team> teamList = null;
        if (!requester.hasAuthority(Authority.ROLE_ADMIN) && !requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            teamList = userInfo.get().getTeams();
        } else {
            teamList = teamRepository.findAllByCompanyId(company.get().getObjectId());
        }
        output.generateSuccessResponse(new UserInfoResponseDTO(userInfo.get(), teamList, company.get()));
    }



    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
