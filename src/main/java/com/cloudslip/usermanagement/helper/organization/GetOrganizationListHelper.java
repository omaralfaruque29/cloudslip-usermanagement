package com.cloudslip.usermanagement.helper.organization;

import com.cloudslip.usermanagement.constant.ListFetchMode;
import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.repository.OrganizationRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



@Service
public class GetOrganizationListHelper extends AbstractHelper {
    private final Logger log = LoggerFactory.getLogger(GetOrganizationListHelper.class);

    private GetListFilterInput input;
    private ResponseDTO output = new ResponseDTO();
    private Pageable pageable;
    private UserInfo requesterInfo;

    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private UserInfoRepository userInfoRepository;


    protected void init(BaseInput input, Object... extraParams) {
        this.input = (GetListFilterInput) input;
        this.setOutput(output);
        pageable = (Pageable) extraParams[0];
    }

    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {

    }


    protected void doPerform() {
        requesterInfo = userInfoRepository.findByUserId(requester.getObjectId()).get();
        if(input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
            findOrganizationListWithPagination();
        } else if(input.getFetchMode().equals(ListFetchMode.ALL)) {
            findAllOrganization();
        } else {
            output.generateErrorResponse("Invalid params in fetch mode");
        }
    }

    private void findOrganizationListWithPagination() {
        if(requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            output.generateSuccessResponse(organizationRepository.findAll(pageable));
        } else {
            output.generateSuccessResponse(organizationRepository.findAllByCompany_Id(pageable, requesterInfo.getCompany().getObjectId()));
        }
    }

    private void findAllOrganization() {
        if(requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            output.generateSuccessResponse(organizationRepository.findAll());
        } else {
            output.generateSuccessResponse(organizationRepository.findAllByCompany_Id(requesterInfo.getCompany().getObjectId()));
        }
    }

    protected void postPerformCheck() {

    }

    protected void doRollback() {

    }
}
