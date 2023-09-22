package com.cloudslip.usermanagement.helper.notification;
import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.helper.organization.AddOrganizationHelper;
import com.cloudslip.usermanagement.model.Notification;
import com.cloudslip.usermanagement.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ChangeNotificationStatusHelper extends AbstractHelper {

    @Autowired
    private NotificationRepository notificationRepository;

    private final Logger log = LoggerFactory.getLogger(AddOrganizationHelper.class);
    private ResponseDTO output = new ResponseDTO();
    private GetObjectInputDTO input;
    private Notification notification;


    protected void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
    }

    protected void checkPermission() {
        if (requester == null || (requester.hasAuthority(Authority.ANONYMOUS))) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
        notification = notificationRepository.findById(input.getId()).get();
        if (notification == null) {
            output.generateErrorResponse("notification not found!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void doPerform() {
        notification = notificationRepository.findById(input.getId()).get();
        notification.setClicked(notification.isClicked() ? false : true);
        notification.setClickedAt(String.valueOf(LocalDateTime.now()));
        notification.setUpdateDate(String.valueOf(LocalDateTime.now()));
        if(notificationRepository.save(notification) != null){
            int total = notificationRepository.countUncheckedNotifications(requester.getObjectId());
            output.generateSuccessResponse(total, String.format("notification status changed & number of unchecked notifications retrieved"));
        } else {
            output.generateErrorResponse("notification status change failed");
        }
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
