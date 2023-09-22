package com.cloudslip.usermanagement.helper.vpc_group;

import com.cloudslip.usermanagement.constant.ListFetchMode;
import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.repository.VpcGroupRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GetAllVpcGroupHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(GetAllVpcGroupHelper.class);

    private GetListFilterInput input;
    private ResponseDTO output = new ResponseDTO();
    private Pageable pageable;

    @Autowired
    private VpcGroupRepository vpcGroupRepository;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetListFilterInput) input;
        this.pageable = (Pageable) extraParams[0];
        this.setOutput(output);
    }


    protected void checkPermission() {
        if (requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN, Authority.ROLE_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
    }


    protected void doPerform() {
        if (requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            if(input.getFilterParamsMap().containsKey("companyId")) {
                ObjectId companyId;
                try {
                    companyId = new ObjectId(input.getFilterParamsMap().get("companyId"));
                } catch (Exception e) {
                    output.generateErrorResponse("Invalid Parameter!");
                    throw new ApiErrorException(this.getClass().getName());
                }
                vpcGroupListWithCompanyId(companyId);
            } else {
                vpcGroupList();
            }
        } else if (requester.hasAuthority(Authority.ROLE_ADMIN)) {
            vpcGroupListWithCompanyId(requester.getCompanyId());
        }
    }



    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    private void vpcGroupListWithCompanyId(ObjectId companyId) {
        if(input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
            output.generateSuccessResponse(vpcGroupRepository.findAllByCompanyIdAndStatus(pageable, companyId, Status.V));
        } else if(input.getFetchMode() != null || input.getFetchMode().equals(ListFetchMode.ALL)) {
            output.generateSuccessResponse(vpcGroupRepository.findAllByCompanyIdAndStatus(companyId, Status.V));
        }
    }
    private void vpcGroupList() {
        if(input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
            output.generateSuccessResponse(vpcGroupRepository.findAllByStatus(pageable, Status.V));
        } else if(input.getFetchMode() != null || input.getFetchMode().equals(ListFetchMode.ALL)) {
            output.generateSuccessResponse(vpcGroupRepository.findAllByStatus(Status.V));
        }
    }
}
