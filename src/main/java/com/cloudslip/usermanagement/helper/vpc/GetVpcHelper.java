package com.cloudslip.usermanagement.helper.vpc;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Organization;
import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.model.Vpc;
import com.cloudslip.usermanagement.repository.OrganizationRepository;
import com.cloudslip.usermanagement.repository.VpcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetVpcHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(GetVpcHelper.class);

    @Autowired
    private VpcRepository vpcRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    private Optional<Vpc> vpc;

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
        vpc = vpcRepository.findByIdAndStatus(input.getId(), Status.V);

        if(!vpc.isPresent()) {
            output.generateErrorResponse("Vpc Not Found!");
            throw new ApiErrorException(this.getClass().getName());
        }
        if(!requester.getCompanyId().toString().equals(vpc.get().getCompanyId().toString())){
            output.generateErrorResponse("Company Not Found!");
            throw new ApiErrorException(this.getClass().getName());
        }
        if (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) &&
                !requester.hasAuthority(Authority.ROLE_ADMIN) &&
                !requester.hasAuthority(Authority.ROLE_DEV) &&
                !requester.hasAuthority(Authority.ROLE_OPS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }

        if(requester.getOrganizationId() != null && vpc.get().getOrganization().getObjectId() != null && !requester.getOrganizationId().equals(vpc.get().getOrganization().getObjectId())){
            output.generateErrorResponse("This vpc does not belong to requester's organization");
            throw new ApiErrorException(this.getClass().getName());
        }

        if(vpc.get().getTeamlist() != null && vpc.get().getTeamlist().size() > 0){
            if(!vpcBelongsToRequesterTeam()){
                output.generateErrorResponse("This vpc does not belong to requester's teams");
                throw new ApiErrorException(this.getClass().getName());
            }
        }

    }


    protected void doPerform() {
        output.generateSuccessResponse(vpc.get(), "Vpc Response");
    }



    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    private boolean vpcBelongsToRequesterTeam() {
        boolean vpcBelongsToRequesterTeam = false;
            for(Team team : vpc.get().getTeamlist()){
                try {
                    if(requester.getTeamIdList().contains(team.getObjectId())){
                        vpcBelongsToRequesterTeam = true;
                        break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        return vpcBelongsToRequesterTeam;
    }
}
