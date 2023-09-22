package com.cloudslip.usermanagement.helper.region;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Region;
import com.cloudslip.usermanagement.repository.RegionRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeleteRegionHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(DeleteRegionHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private RegionRepository regionRepository;


    protected void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
    }

    protected void checkPermission() {
        if (requester == null || (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN))) {
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
        if(region.isPresent()) {
            region.get().setUpdatedBy(requester.getUsername());
            region.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
            region.get().setLastUpdateActionId(actionId);
            regionRepository.delete(region.get());

            deleteEverythingForRegion(region.get().getObjectId());

            output.generateSuccessResponse(null, String.format("Region '%s' has been deleted", region.get().getName()));
        } else {
            output.generateErrorResponse("Region not found to delete!");
        }
    }

    private void deleteEverythingForRegion(ObjectId regionId) {
        //TODO: Delete all team, application, application environment, app commits, app commit states, everything for the region after 24 hours.
    }

    protected void postPerformCheck() {

    }

    protected void doRollback() {

    }

}
