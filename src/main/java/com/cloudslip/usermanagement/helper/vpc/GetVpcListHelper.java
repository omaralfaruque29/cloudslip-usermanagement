package com.cloudslip.usermanagement.helper.vpc;

import com.cloudslip.usermanagement.constant.ListFetchMode;
import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.enums.VpcStatus;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.repository.VpcRepository;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class GetVpcListHelper extends AbstractHelper {

    @Autowired
    private VpcRepository vpcRepository;

    private GetListFilterInput input;
    private ResponseDTO output = new ResponseDTO();
    private Pageable pageable;


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
        if (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) &&
                !requester.hasAuthority(Authority.ROLE_ADMIN) &&
                !requester.hasAuthority(Authority.ROLE_DEV) &&
                !requester.hasAuthority(Authority.ROLE_OPS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }

    }


    protected void doPerform() {
        if(input.getFilterParamsMap().containsKey("vpc-status")) {
            String vpcStatus;
            try {
                vpcStatus = input.getFilterParamsMap().get("vpc-status");
                if (!EnumUtils.isValidEnum(VpcStatus.class, vpcStatus)) {
                    output.generateErrorResponse("Invalid VPC status!");
                    throw new ApiErrorException(this.getClass().getName());
                }
                vpcListWithInitializedVpc(VpcStatus.valueOf(vpcStatus));
            } catch (Exception e) {
                output.generateErrorResponse("Invalid Parameter!");
                throw new ApiErrorException(this.getClass().getName());
            }
        } else {
            vpcList();
        }
    }


    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    private void vpcListWithInitializedVpc(VpcStatus vpcStatus) {
        if (requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            if(input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
                output.generateSuccessResponse(vpcRepository.findAllByStatusAndVpcStatus(pageable, Status.V, vpcStatus));
            } else if(input.getFetchMode() != null || input.getFetchMode().equals(ListFetchMode.ALL)) {
                output.generateSuccessResponse(vpcRepository.findAllByStatusAndVpcStatus(Status.V, vpcStatus));
            }
        } else if(requester.hasAuthority(Authority.ROLE_ADMIN)) {
            if (input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
                output.generateSuccessResponse(vpcRepository.findAllByCompanyIdAndStatusAndVpcStatus(pageable, requester.getCompanyId(), Status.V, vpcStatus));
            } else if (input.getFetchMode() != null || input.getFetchMode().equals(ListFetchMode.ALL)) {
                output.generateSuccessResponse(vpcRepository.findAllByCompanyIdAndStatusAndVpcStatus(requester.getCompanyId(), Status.V, vpcStatus));
            }
        } else {
            output.generateErrorResponse("Invalid Params In Fetch Mode");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    private void vpcList() {
        if (requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            if(input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
                output.generateSuccessResponse(vpcRepository.findAllByStatusAndVpcStatus(pageable, Status.V, VpcStatus.INITIALIZED));
            } else if(input.getFetchMode() != null || input.getFetchMode().equals(ListFetchMode.ALL)) {
                output.generateSuccessResponse(vpcRepository.findAllByStatusAndVpcStatus(Status.V, VpcStatus.INITIALIZED));
            }
        } else if(requester.hasAuthority(Authority.ROLE_ADMIN)) {
            if (input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
                output.generateSuccessResponse(vpcRepository.findAllByCompanyIdAndStatus(pageable, requester.getCompanyId(), Status.V));
            } else if (input.getFetchMode() != null || input.getFetchMode().equals(ListFetchMode.ALL)) {
                output.generateSuccessResponse(vpcRepository.findAllByCompanyIdAndStatus(requester.getCompanyId(), Status.V));
            }
        } else {
            output.generateErrorResponse("Invalid Params In Fetch Mode");
            throw new ApiErrorException(this.getClass().getName());
        }
    }
}
