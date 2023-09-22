package com.cloudslip.usermanagement.helper.vpc;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.UpdateVpcDTO;
import com.cloudslip.usermanagement.dto.UpdateVpcStatusInputDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.enums.VpcStatus;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.model.Vpc;
import com.cloudslip.usermanagement.repository.RegionRepository;
import com.cloudslip.usermanagement.repository.VpcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UpdateVpcStatusHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(UpdateVpcStatusHelper.class);

    private UpdateVpcStatusInputDTO input;

    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private VpcRepository vpcRepository;


    private Optional<Vpc> vpc;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (UpdateVpcStatusInputDTO) input;
        this.setOutput(output);
        vpc = null;
    }


    protected void checkPermission() {

    }


    protected void checkValidity() {
        vpc = vpcRepository.findByIdAndStatus(input.getId(), Status.V);
        if (!vpc.isPresent()) {
            output.generateErrorResponse("Vpc not found!");
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
        vpc.get().setVpcStatus(input.getVpcStatus());
        if(vpc.get().getVpcStatus() == VpcStatus.TERMINATED){
            vpc.get().setStatus(Status.D);
        }
        vpcRepository.save(vpc.get());
        output.generateSuccessResponse(null, String.format("Vpc status of '%s' successfully has been updated to %s", vpc.get().getName(), input.getVpcStatus().toString()));
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
