package com.cloudslip.usermanagement.service;
import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.SaveNotificationDto;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.helper.notification.*;;
import com.cloudslip.usermanagement.model.User;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing notification.
 */
@Service
@Transactional
public class NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private AddNotificationHelper addNotificationHelper;

    @Autowired
    private GetNotificationListHelper getNotificationListHelper;

    @Autowired
    private CountUncheckedNotificationsHelper countUncheckedNotificationsHelper;

    @Autowired
    private ChangeNotificationStatusHelper changeNotificationStatusHelper;

    @Autowired
    private DeleteNotificationHelper deleteNotificationHelper;

    /**
     * Create a notification.
     *
     * @param input the entity to create
     * @return the persisted entity
     */
    public ResponseDTO createNotification(SaveNotificationDto input, User requester, ObjectId actionId) {
        log.debug("Request to create notification : {}", input);
        return (ResponseDTO) addNotificationHelper.execute(input, requester, actionId);
    }


    /**
     * Get notification list.
     *
     * @param input the list fetching filter inputs
     * @param requester the current user information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public ResponseDTO getNotificationList(GetListFilterInput input, User requester) {
        log.debug("Request to get notification list");
        return (ResponseDTO) getNotificationListHelper.execute(input, requester);
    }


    /**
     * Get notification list.
     *
     * @param requester the current user information
     * @return the number of unchecked notifications of currently logged in user
     */
    @Transactional(readOnly = true)
    public ResponseDTO countUncheckedNotifications(User requester) {
        log.debug("Request to count unchecked notifications ");
        return (ResponseDTO) countUncheckedNotificationsHelper.execute(requester);
    }


    /**
     * Change notification status.
     *
     * @param id notification Id
     * @param requester the current user information
     * @return the number of unchecked notifications of currently logged in user
     */
    public ResponseDTO changeNotificationStatus(ObjectId id, User requester) {
        log.debug("Request to change notification status");
        return (ResponseDTO) changeNotificationStatusHelper.execute(new GetObjectInputDTO(id), requester);
    }


    /**
     * delete notification bu Id.
     *
     * @param id notification Id
     * @param requester the current user information
     * @return the number of unchecked notifications of currently logged in user
     */
    public ResponseDTO deleteNotification(ObjectId id, User requester) {
        log.debug("Request to delete notification");
        return (ResponseDTO) deleteNotificationHelper.execute(new GetObjectInputDTO(id), requester);
    }
}
