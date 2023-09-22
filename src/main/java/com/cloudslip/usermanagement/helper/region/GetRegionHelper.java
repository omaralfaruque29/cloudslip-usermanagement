package com.cloudslip.usermanagement.helper.region;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Region;
import com.cloudslip.usermanagement.repository.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetRegionHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(GetRegionHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO<Region> output = new ResponseDTO<Region>();

    @Autowired
    private RegionRepository regionRepository;


    protected void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
    }

    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
        if(input.getId() == null) {
            output.generateErrorResponse("Id is missing in params!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void doPerform() {
        Optional<Region> region = regionRepository.findById(input.getId());
        if(!region.isPresent()) {
            output.generateErrorResponse(String.format("No Region found with the id - %s", input.getId().toHexString()));
        }
        output.generateSuccessResponse(region.get());
    }

    protected void postPerformCheck() {

    }

    protected void doRollback() {

    }

}
