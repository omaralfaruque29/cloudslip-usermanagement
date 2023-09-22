package com.cloudslip.usermanagement.helper.notification;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.SaveNotificationDto;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.helper.organization.AddOrganizationHelper;
import com.cloudslip.usermanagement.model.Notification;
import com.cloudslip.usermanagement.repository.NotificationRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AddNotificationHelper extends AbstractHelper {

    @Autowired
    private NotificationRepository notificationRepository;

    private final Logger log = LoggerFactory.getLogger(AddOrganizationHelper.class);

    private SaveNotificationDto input;
    private ResponseDTO output = new ResponseDTO();

    public void init(BaseInput input, Object... extraParams) {
        this.input = (SaveNotificationDto) input;
        this.setOutput(output);

        if(extraParams.length > 0) {
            ObjectId actionId = (ObjectId) extraParams[0];
            System.out.println(actionId.toHexString());
        }
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
        Notification notification = new Notification();
        notification.setId(ObjectId.get());
        notification.setUserId(requester.getObjectId());
        notification.setCreateDate(String.valueOf(LocalDateTime.now()));
        notification.setText(input.getText());
        notification.setType(input.getType());
        notification.setCreateActionId(actionId);
        notification = notificationRepository.save(notification);
        output.generateSuccessResponse(notification.getId(), String.format("A new notification '%s' is created", notification.getText()));
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
