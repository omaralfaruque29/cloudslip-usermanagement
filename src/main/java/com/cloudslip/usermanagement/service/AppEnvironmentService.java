package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.dto.app_environment.AddAppEnvironmentsDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.vpcresourceupdate.VpcResourceUpdateDTO;
import com.cloudslip.usermanagement.helper.app_environment.GetAppEnvironmentResponse;
import com.cloudslip.usermanagement.helper.app_environment.UpdateVpcResourceHelper;
import com.cloudslip.usermanagement.model.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppEnvironmentService {

    private final Logger log = LoggerFactory.getLogger(AppEnvironmentService.class);

    @Autowired
    GetAppEnvironmentResponse getAppEnvironmentResponse;

    @Autowired
    UpdateVpcResourceHelper updateVpcResourceHelper;


    /**
     * Get App Environment Response
     *
     * @param input fetching App Environment Response
     * @return the persisted entity
     */
    @Transactional(readOnly = true)
    public ResponseDTO getResponse(AddAppEnvironmentsDTO input, User requester) {
        log.debug("Request to get Company Env and Vpc list: {}", input);
        return (ResponseDTO) getAppEnvironmentResponse.execute(input, requester);
    }

    /**
     * Update Vpc Resource
     *
     * @param input fetching App Environment Response
     * @return the persisted entity
     */
    @Transactional(readOnly = true)
    public ResponseDTO updateVpcResource(VpcResourceUpdateDTO input, User requester, ObjectId actionId) {
        log.debug("Request to Update Vpc Resource : {}", input);
        return (ResponseDTO) updateVpcResourceHelper.execute(input, requester, actionId);
    }
}
