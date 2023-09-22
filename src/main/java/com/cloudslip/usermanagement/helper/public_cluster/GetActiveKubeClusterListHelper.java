package com.cloudslip.usermanagement.helper.public_cluster;

import com.cloudslip.usermanagement.constant.ListFetchMode;
import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.repository.KubeClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GetActiveKubeClusterListHelper extends AbstractHelper {

    private GetListFilterInput input;
    private ResponseDTO output = new ResponseDTO();
    private Pageable pageable;

    @Autowired
    KubeClusterRepository kubeClusterRepository;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetListFilterInput) input;
        this.setOutput(output);
        pageable = (Pageable) extraParams[0];
    }

    protected void checkPermission() {
        if(requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN, Authority.ROLE_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
    }

    protected void doPerform() {
        if(input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
            output.generateSuccessResponse(kubeClusterRepository.findAllByStatusAndIsEnabled(pageable, Status.V, true));
        } else if(input.getFetchMode() != null || input.getFetchMode().equals(ListFetchMode.ALL)) {
            output.generateSuccessResponse(kubeClusterRepository.findAllByStatusAndIsEnabled(Status.V, true));
        }
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
