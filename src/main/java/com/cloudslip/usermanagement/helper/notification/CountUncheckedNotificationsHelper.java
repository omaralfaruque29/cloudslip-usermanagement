package com.cloudslip.usermanagement.helper.notification;
import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.helper.organization.AddOrganizationHelper;
import com.cloudslip.usermanagement.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountUncheckedNotificationsHelper extends AbstractHelper {

    @Autowired
    private NotificationRepository notificationRepository;

    private final Logger log = LoggerFactory.getLogger(AddOrganizationHelper.class);
    private ResponseDTO output = new ResponseDTO();


    protected void init(BaseInput input, Object... extraParams) {
        this.setOutput(output);
    }

    protected void checkPermission() {
        if (requester == null || (requester.hasAuthority(Authority.ANONYMOUS))) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
    }

    protected void doPerform() {
        int total = notificationRepository.countUncheckedNotifications(requester.getObjectId());
        output.generateSuccessResponse(total, String.format("number of unchecked notifications"));
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
