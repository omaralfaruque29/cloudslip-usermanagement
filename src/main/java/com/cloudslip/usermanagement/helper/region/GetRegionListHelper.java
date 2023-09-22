package com.cloudslip.usermanagement.helper.region;

import com.cloudslip.usermanagement.constant.ListFetchMode;
import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.repository.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class GetRegionListHelper extends AbstractHelper {
    private final Logger log = LoggerFactory.getLogger(GetRegionListHelper.class);

    private GetListFilterInput input;
    private ResponseDTO output = new ResponseDTO();
    private Pageable pageable;

    @Autowired
    private RegionRepository regionRepository;


    protected void init(BaseInput input, Object... extraParams) {
        this.input = (GetListFilterInput) input;
        this.setOutput(output);
        pageable = (Pageable) extraParams[0];
    }

    protected void checkPermission() {
        if (requester == null) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {

    }

    protected void doPerform() {
        if(input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
            output.generateSuccessResponse(regionRepository.findAll(pageable));
        } else if(input.getFetchMode().equals(ListFetchMode.ALL)) {
            output.generateSuccessResponse(regionRepository.findAll());
        } else {
            output.generateErrorResponse("Invalid params in fetch mode");
        }
    }

    protected void postPerformCheck() {

    }

    protected void doRollback() {

    }
}
