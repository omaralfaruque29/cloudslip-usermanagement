package com.cloudslip.usermanagement.helper.notification;

import com.cloudslip.usermanagement.constant.ListFetchMode;
import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.helper.organization.AddOrganizationHelper;
import com.cloudslip.usermanagement.model.Notification;
import com.cloudslip.usermanagement.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetNotificationListHelper extends AbstractHelper {

    @Autowired
    private NotificationRepository notificationRepository;

    private final Logger log = LoggerFactory.getLogger(AddOrganizationHelper.class);

    private GetListFilterInput input;
    private ResponseDTO output = new ResponseDTO();


    protected void init(BaseInput input, Object... extraParams) {
        this.input = (GetListFilterInput) input;
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
        if(input.getFetchMode() == null || input.getFetchMode().equals(ListFetchMode.PAGINATION)) {
            findNotificationListWithPagination();
        } else if(input.getFetchMode().equals(ListFetchMode.ALL)) {
            findAllNotification();
        } else {
            output.generateErrorResponse("Invalid params in fetch mode");
        }
    }

    private void findNotificationListWithPagination() {
        Pageable pageable = PageRequest.of(Integer.parseInt(input.getFilterParamsMap().get("page")), Integer.parseInt(input.getFilterParamsMap().get("size")), Sort.by("createDate").descending());
        Page<Notification> notifications = notificationRepository.findByUserIdAndStatus(pageable, requester.getObjectId(), Status.V);
        output.generateSuccessResponse(notifications, String.format("a notification page retrieved"));
    }
    private void findAllNotification() {
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        List<Notification> notifications = notificationRepository.findByUserIdAndStatus(requester.getObjectId(), Status.V, sort);
        output.generateSuccessResponse(notifications, String.format("all notifications retrieved"));
    }


    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
