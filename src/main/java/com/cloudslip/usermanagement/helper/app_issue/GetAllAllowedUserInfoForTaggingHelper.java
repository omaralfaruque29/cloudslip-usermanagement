package com.cloudslip.usermanagement.helper.app_issue;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.appIsssue.GetAllAllowedUserForTaggingResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllAllowedUserInfoForTaggingHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(GetAppIssueListByApplicationHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    private ResponseDTO response = new ResponseDTO();
    private Team applicationTeam;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserInfoRepository userInfoRepository;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
        this.response = (ResponseDTO) extraParams[0];
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.applicationTeam = objectMapper.convertValue(response.getData(), new TypeReference<Team>() { });
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
        List<UserInfo> userInfoList = userInfoRepository.findAllByTeamsId(applicationTeam.getObjectId());
        output.generateSuccessResponse(new GetAllAllowedUserForTaggingResponseDTO(userInfoList, applicationTeam), "Allowed user info list for tagging");
    }



    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
