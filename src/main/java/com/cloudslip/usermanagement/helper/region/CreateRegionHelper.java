package com.cloudslip.usermanagement.helper.region;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.CreateRegionInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Region;
import com.cloudslip.usermanagement.repository.RegionRepository;
import com.cloudslip.usermanagement.util.Utils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class CreateRegionHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(CreateRegionHelper.class);

    private CreateRegionInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private RegionRepository regionRepository;


    public void init(BaseInput input, Object... extraParams) {
        this.input = (CreateRegionInputDTO) input;
        this.setOutput(output);

        if(extraParams.length > 0) {
            ObjectId actionId = (ObjectId) extraParams[0];
            System.out.println(actionId.toHexString());
        }
    }


    protected void checkPermission() {
        if (requester == null || (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN))) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        Region existingRegion = regionRepository.findByNameIgnoreCase(input.getName());
        if (existingRegion != null && existingRegion.isValid()) {
            output.generateErrorResponse(String.format("Region with the name '%s' already exists", input.getName()));
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() {
        Region region = new Region();
        region.setId(ObjectId.get());
        region.setName(input.getName());
        region.setCode(generateRegionCode(input.getName()));
        region.setDescription(input.getDescription());
        region.setCreatedBy(requester.getUsername());
        region.setCreateDate(String.valueOf(LocalDateTime.now()));
        region.setCreateActionId(actionId);
        region = regionRepository.save(region);
        output.generateSuccessResponse(region.getId(), String.format("A new Region '%s' is created", region.getName()));
    }



    protected void postPerformCheck() {
    }


    protected void doRollback() {

    }

    private String generateRegionCode(String regionName) {
        return Utils.removeAllSpaceWithDash(regionName).toLowerCase();
    }
}
