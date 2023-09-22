package com.cloudslip.usermanagement.helper.region;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.UpdateRegionInputDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Region;
import com.cloudslip.usermanagement.repository.RegionRepository;
import com.cloudslip.usermanagement.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class UpdateRegionHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(UpdateRegionHelper.class);

    private UpdateRegionInputDTO input;
    private ResponseDTO output = new ResponseDTO();
    private Region updatingRegion = null;

    @Autowired
    private RegionRepository regionRepository;


    public void init(BaseInput input, Object... extraParams) {
        this.input = (UpdateRegionInputDTO) input;
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
            output.generateErrorResponse("Region Id is missing in the input");
            throw new ApiErrorException(this.getClass().getName());
        }

        Optional<Region> region = regionRepository.findById(input.getId());

        if(!(region.isPresent())) {
            output.generateErrorResponse(String.format("No Region found with the id - %s", input.getId().toHexString()));
            throw new ApiErrorException(this.getClass().getName());
        }

        updatingRegion = region.get();

        Region existingRegion = regionRepository.findByNameIgnoreCase(input.getName());
        if (existingRegion != null && existingRegion.isValid() && !existingRegion.getId().equals(updatingRegion.getId())) {
            output.generateErrorResponse(String.format("Cannot update! Region with the name '%s' already exists.", input.getName()));
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() {
        updatingRegion.setName(input.getName());
        updatingRegion.setCode(generateRegionCode(input.getName()));
        updatingRegion.setDescription(input.getDescription());
        updatingRegion.setUpdatedBy(requester.getUsername());
        updatingRegion.setUpdateDate(String.valueOf(LocalDateTime.now()));
        updatingRegion.setLastUpdateActionId(actionId);
        updatingRegion = regionRepository.save(updatingRegion);
        output.generateSuccessResponse(updatingRegion.getId(), String.format("Region '%s' is updated", updatingRegion.getName()));
    }


    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    private String generateRegionCode(String regionName) {
        return Utils.removeAllSpaceWithDash(regionName).toLowerCase();
    }
}
