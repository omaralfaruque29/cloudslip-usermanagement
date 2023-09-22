package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.helper.application.CreateApplicationHelper;
import com.cloudslip.usermanagement.helper.application.GetTeamHelper;
import com.cloudslip.usermanagement.helper.application.GetTeamListHelper;
import com.cloudslip.usermanagement.helper.application.GetUserInfoApplicationHelper;
import com.cloudslip.usermanagement.model.User;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ApplicationService {

    @Autowired
    CreateApplicationHelper createApplicationHelper;

    @Autowired
    GetTeamHelper getTeamHelper;

    @Autowired
    GetTeamListHelper getTeamListHelper;

    @Autowired
    GetUserInfoApplicationHelper getUserInfoApplicationHelper;

    private final Logger log = LoggerFactory.getLogger(ApplicationService.class);

    /**
     * Get Create Application Response By Team Id.
     *
     * @param id fetching team
     * @return the persisted entity
     */
    @Transactional(readOnly = true)
    public ResponseDTO getResponse(ObjectId id, User requester) {
        log.debug("Request to get Team by id : {}", id);
        return (ResponseDTO) createApplicationHelper.execute(new GetObjectInputDTO(id), requester);
    }

    /**
     * Get Team By Team Id.
     *
     * @param id fetching team
     * @return the persisted entity
     */
    @Transactional(readOnly = true)
    public ResponseDTO getTeamByUser(ObjectId id, User requester) {
        log.debug("Request to get Team by id : {}", id);
        return (ResponseDTO) getTeamHelper.execute(new GetObjectInputDTO(id), requester);
    }

    /**
     * Get List Of Teams In A Company.
     *
     * @param requester fetching List Of Teams In Company
     * @return the persisted entity
     */
    @Transactional(readOnly = true)
    public ResponseDTO getTeamList(User requester) {
        log.debug("Request to get team list by user : {}", requester.getObjectId());
        return (ResponseDTO) getTeamListHelper.execute(requester);
    }

    @Transactional(readOnly = true)
    public ResponseDTO getUserAndCompanyInfo(User requester, GetObjectInputDTO companyId) {
        log.debug("Request to get User by userId : {}", requester.getObjectId());
        return (ResponseDTO) getUserInfoApplicationHelper.execute(companyId, requester);
    }
}
