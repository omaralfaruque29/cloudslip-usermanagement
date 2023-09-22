package com.cloudslip.usermanagement.helper.vpc_group;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.VpcGroup.CreateVpcGroupDTO;
import com.cloudslip.usermanagement.dto.VpcGroup.UpdateVpcGroupDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Company;
import com.cloudslip.usermanagement.model.Vpc;
import com.cloudslip.usermanagement.model.VpcGroup;
import com.cloudslip.usermanagement.repository.CompanyRepository;
import com.cloudslip.usermanagement.repository.VpcGroupRepository;
import com.cloudslip.usermanagement.repository.VpcRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UpdateVpcGroupHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(UpdateVpcGroupHelper.class);

    private UpdateVpcGroupDTO input;
    private ResponseDTO output = new ResponseDTO();

    private Optional<VpcGroup> vpcGroup;

    @Autowired
    private VpcRepository vpcRepository;

    @Autowired
    private VpcGroupRepository vpcGroupRepository;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (UpdateVpcGroupDTO) input;
        this.setOutput(output);
        vpcGroup = null;
    }


    protected void checkPermission() {
        if (requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN, Authority.ROLE_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        if (input.getVpcGroupId() == null) {
            output.generateErrorResponse("Vpc Group Id Required!");
            throw new ApiErrorException(this.getClass().getName());
        }
        vpcGroup = vpcGroupRepository.findByIdAndStatus(input.getVpcGroupId(), Status.V);
        if (!vpcGroup.isPresent()) {
            output.generateErrorResponse("Vpc group does not exists!");
            throw new ApiErrorException(this.getClass().getName());
        } else if (!validateAuthority()) {
            output.generateErrorResponse("User do not have permission to access!");
            throw new ApiErrorException(this.getClass().getName());
        }
        Optional<VpcGroup> vpcGroupWithSameName = vpcGroupRepository.findByNameAndCompanyIdAndStatusAndIdNotIn(input.getName().trim().replaceAll(" +", " "), vpcGroup.get().getCompanyObjectId(), Status.V, vpcGroup.get().getObjectId());
        if (vpcGroupWithSameName.isPresent()) {
            output.generateErrorResponse(String.format("Vpc Group with name '%s' already exists", input.getName()));
            throw new ApiErrorException(this.getClass().getName());
        }
        if (input.getVpcIdList() == null || input.getVpcIdList().isEmpty()) {
            output.generateErrorResponse("No Vpc Selected!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() {
        List<Vpc> vpcList = getVpcList(input.getVpcIdList());
        vpcGroup.get().setName(input.getName().trim().replaceAll(" +", " "));
        vpcGroup.get().setVpcList(vpcList);
        vpcGroup.get().setUpdatedBy(requester.getUsername());
        vpcGroup.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
        vpcGroup.get().setLastUpdateActionId(actionId);
        vpcGroupRepository.save(vpcGroup.get());
        output.generateSuccessResponse(vpcGroup.get(), String.format("Vpc Group '%s' has been updated successfully", input.getName()));
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

    /*
        Vpc List
    */
    private List<Vpc> getVpcList(List<ObjectId> vpcIdList) {
        List<Vpc> vpcList = new ArrayList<>();
        for (ObjectId vpcId : vpcIdList) {
            Optional<Vpc> vpc = vpcRepository.findByIdAndCompanyIdAndStatus(vpcId, vpcGroup.get().getCompanyObjectId(), Status.V);
            if (!vpc.isPresent()) {
                output.generateErrorResponse(String.format("Vpc not found with id - %s", vpcId));
                throw new ApiErrorException(output.getMessage(), this.getClass().getName());
            }
            if (!containsDuplicate(vpcList, vpc.get())) {
                vpcList.add(vpc.get());
            }
        }
        return vpcList;
    }

    /*
        Check If User Enters Duplicate Vpc Id Multiple Time
    */
    private boolean containsDuplicate(List<Vpc> vpcList, Vpc newVpc) {
        for (Vpc vpc : vpcList) {
            if (vpc.getId().equals(newVpc.getId())) {
                return true;
            }
        }
        return false;
    }
}
