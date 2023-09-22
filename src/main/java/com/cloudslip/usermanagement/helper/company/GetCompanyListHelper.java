package com.cloudslip.usermanagement.helper.company;

import com.cloudslip.usermanagement.constant.ListFetchMode;
import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class GetCompanyListHelper extends AbstractHelper {
    private final Logger log = LoggerFactory.getLogger(GetCompanyListHelper.class);

    private GetListFilterInput input;
    private ResponseDTO output = new ResponseDTO();
    private Pageable pageable;

    @Autowired
    private CompanyRepository companyRepository;


    protected void init(BaseInput input, Object... extraParams) {
        this.input = (GetListFilterInput) input;
        this.setOutput(output);
        pageable = (Pageable) extraParams[0];
    }

    protected void checkPermission() {
        if (requester == null || !requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {

    }


    protected void doPerform() {
        if(input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
            output.generateSuccessResponse(companyRepository.findAll(pageable));
        } else if(input.getFetchMode() != null || input.getFetchMode().equals(ListFetchMode.ALL)) {
            output.generateSuccessResponse(companyRepository.findAll());
        } else {
            output.generateErrorResponse("Invalid params in fetch mode");
        }
    }

    protected void postPerformCheck() {

    }

    protected void doRollback() {

    }
}
