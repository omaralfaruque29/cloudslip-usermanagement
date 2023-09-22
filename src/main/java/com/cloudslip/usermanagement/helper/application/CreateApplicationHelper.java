package com.cloudslip.usermanagement.helper.application;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.application.CreateApplicationResponseDTO;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.*;
import com.cloudslip.usermanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreateApplicationHelper extends AbstractHelper {

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    EnvironmentOptionRepository environmentOptionRepository;

    @Autowired
    CompanyRepository companyRepository;

    private Optional<Team> team;
    private Optional<EnvironmentOption> environmentOption;
    private Optional<Company> company;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
        company = null;
        team = null;
        environmentOption = null;
    }


    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        team = teamRepository.findById(this.input.getId());
        if (!team.isPresent()) {
            output.generateErrorResponse("Team Not Found!");
            throw new ApiErrorException(this.getClass().getName());
        } else if (!this.checkAuthority()) {
            output.generateErrorResponse("Unauthorized User");
            throw new ApiErrorException(this.getClass().getName());
        }
        company = companyRepository.findById(team.get().getCompanyObjectId());
        if (!company.isPresent()) {
            output.generateErrorResponse(String.format("Company does not exist for team - '%s'!", team.get().getName()));
            throw new ApiErrorException(this.getClass().getName());
        }
        environmentOption = environmentOptionRepository.findByShortNameIgnoreCaseAndStatus("dev", Status.V);
        if (!environmentOption.isPresent()) {
            output.generateErrorResponse("Currently There is not Development Environment");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() {
        CreateApplicationResponseDTO createApplicationResponseDTO = new CreateApplicationResponseDTO(company.get(), team.get(), environmentOption.get());
        output.generateSuccessResponse(createApplicationResponseDTO);
    }



    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    private boolean checkAuthority() {
        if (requester.hasAuthority(Authority.ROLE_ADMIN) && !requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            return requester.getCompanyId().toString().equals(team.get().getCompanyId());
        } else if (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requester.hasAuthority(Authority.ROLE_ADMIN)) {
            return team.get().existInTeamIdList(requester.getTeamIdList());
        }
        return true;
    }
}
