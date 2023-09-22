package com.cloudslip.usermanagement.helper.vpc_group;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.VpcGroup.CreateVpcGroupDTO;
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
public class CreateVpcGroupHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(CreateVpcGroupHelper.class);

    private CreateVpcGroupDTO input;
    private ResponseDTO output = new ResponseDTO();

    private Optional<Company> company;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private VpcRepository vpcRepository;

    @Autowired
    private VpcGroupRepository vpcGroupRepository;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (CreateVpcGroupDTO) input;
        this.setOutput(output);
        company = null;
    }


    protected void checkPermission() {
        if (requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN, Authority.ROLE_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        checkCompany();
        if (input.getName() == null || input.getName().equals("")) {
            output.generateErrorResponse("Vpc Group name required!");
            throw new ApiErrorException(this.getClass().getName());
        }
        Optional<VpcGroup> vpcGroupWithSameName = vpcGroupRepository.findByNameAndCompanyIdAndStatus(input.getName().trim().replaceAll(" +", " "), company.get().getObjectId(), Status.V);
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
        VpcGroup vpcGroup = new VpcGroup();
        vpcGroup.setCompanyId(company.get().getObjectId());
        vpcGroup.setName(input.getName().trim().replaceAll(" +", " "));
        vpcGroup.setVpcList(vpcList);
        vpcGroup.setStatus(Status.V);
        vpcGroup.setCreatedBy(requester.getUsername());
        vpcGroup.setCreateDate(String.valueOf(LocalDateTime.now()));
        vpcGroup.setCreateActionId(actionId);
        vpcGroup = vpcGroupRepository.save(vpcGroup);
        output.generateSuccessResponse(vpcGroup, String.format("Vpc Group '%s' has been created successfully", input.getName()));
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    /*
        Vpc List
     */
    private List<Vpc> getVpcList(List<ObjectId> vpcIdList) {
        List<Vpc> vpcList = new ArrayList<>();
        for (ObjectId vpcId : vpcIdList) {
            Optional<Vpc> vpc = vpcRepository.findByIdAndCompanyIdAndStatus(vpcId, company.get().getObjectId(), Status.V);
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

    /*
        Retrieving Company
     */
    private void checkCompany() {
        if (requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requester.hasAuthority(Authority.ROLE_ADMIN) && input.getCompanyId() == null) {
            output.generateErrorResponse("Company Id required!");
            throw new ApiErrorException(this.getClass().getName());
        }
        ObjectId companyId = null;
        if (requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            companyId = input.getCompanyId();
        } else {
            companyId = requester.getCompanyId();
        }
        company = companyRepository.findById(companyId);
        if(!company.isPresent()) {
            output.generateErrorResponse("Company not found!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }
    }
}
