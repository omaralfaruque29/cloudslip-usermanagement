package com.cloudslip.usermanagement.service;

import com.cloudslip.usermanagement.dto.GetListFilterInput;
import com.cloudslip.usermanagement.dto.ReplyMessageDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.SendMessageDTO;
import com.cloudslip.usermanagement.enums.MessageType;
import com.cloudslip.usermanagement.model.*;
import com.cloudslip.usermanagement.repository.MessageRepository;
import com.cloudslip.usermanagement.repository.MessageThreadRepository;
import com.cloudslip.usermanagement.model.Message;
import com.cloudslip.usermanagement.model.MessageThread;
import com.cloudslip.usermanagement.model.Recipient;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MessageService {

    private final Logger log = LoggerFactory.getLogger(VpcService.class);

    @Autowired
    private MessageRepository  messageRepository;

    @Autowired
    private MessageThreadRepository  messageThreadRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    public MessageService(MessageRepository messageRepository,MessageThreadRepository  messageThreadRepository) {
        this.messageRepository = messageRepository;
        this.messageThreadRepository = messageThreadRepository;
    }

    /**
     * Save a message.
     *
     * @param message the entity to save
     * @return the persisted entity
     */
    public Message save(Message message) {
        log.debug("Request to save Vpc : {}", message);
        return messageRepository.save(message);
    }

    /**
     * Save a message.
     *
     * @param messageThread the entity to save
     * @return the persisted entity
     */
    public MessageThread save(MessageThread messageThread) {
        log.debug("Request to save Vpc : {}", messageThread);
        return messageThreadRepository.save(messageThread);
    }

    /**
     * Create a Message.
     *
     * @param dto the entity to create
     * @return the persisted entity
     */
    public ResponseDTO create(ObjectId actionId, User currentUser, SendMessageDTO dto) {
        log.debug("Request to save Message : {}", dto);
        Optional<UserInfo> currentUserInfo = userInfoRepository.findByUserId(currentUser.getObjectId());
        if(!currentUserInfo.isPresent()) {
            return new ResponseDTO().generateErrorResponse(String.format("No User found with the id - %s", currentUser.getObjectId()));
        }
        MessageThread messageThread = new MessageThread();
        Message message = new Message();
        List<UserInfo> recipientUserInfoList = new ArrayList<>();
        for(int i = 0; i < dto.getRecipientIdList().size(); i++) {
            Optional<UserInfo> userInfo = userInfoRepository.findByUserId(dto.getRecipientIdList().get(i));
            if(!userInfo.isPresent()) {
                return new ResponseDTO().generateErrorResponse(String.format("No User found with the id - %s", dto.getRecipientIdList().get(i)));
            }
            recipientUserInfoList.add(userInfo.get());
        }
        message.setRecipientList(generateRecipientList(recipientUserInfoList));
        recipientUserInfoList.add(currentUserInfo.get());
        messageThread.setUserList(recipientUserInfoList);
        messageThread.setTitle(dto.getSubject());
        messageThread.setId(new ObjectId());
        messageThread.setCreateDate(String.valueOf(LocalDateTime.now()));

        message.setContent(dto.getContent());
        message.setSubject(dto.getSubject());
        message.setMessageThread(messageThread);
        message.setMessageType(MessageType.message);
        message.setSender(currentUserInfo.get());
        message.setCreatedBy(currentUser.getId());
        message.setCreateDate(String.valueOf(LocalDateTime.now()));
        messageRepository.save(message);
        messageThreadRepository.save(messageThread);
        return new ResponseDTO().generateSuccessResponse(String.format("Message Send Successfully !!"));
    }

    private List<Recipient> generateRecipientList(List<UserInfo> userInfoList) {
        List<Recipient> recipientList = new ArrayList<>();
        for(int i = 0; i < userInfoList.size(); i++) {
            Recipient recipient = new Recipient(userInfoList.get(i));
            recipientList.add(recipient);
        }
        return recipientList;
    }

    /**
     * Send Message.
     *
     * @param dto the entity to create
     * @return the persisted entity
     */
    public ResponseDTO createReply(ObjectId actionId, User currentUser, ReplyMessageDTO dto) {
        log.debug("Request to save Message : {}", dto);
        Optional<UserInfo> currentUserInfo = userInfoRepository.findByUserId(currentUser.getObjectId());
        if(!currentUserInfo.isPresent()) {
            return new ResponseDTO().generateErrorResponse(String.format("No User found with the id - %s", currentUser.getObjectId()));
        }
        Optional<MessageThread> messageThread = messageThreadRepository.findById(dto.getMessageThreadId());
        if(!messageThread.isPresent()) {
            return new ResponseDTO().generateErrorResponse(String.format("No MessageThread found with the id - %s", dto.getMessageThreadId()));
        }
        Message message = new Message();
        List<UserInfo> existingUserList = new ArrayList<>();
        existingUserList = messageThread.get().getUserList();
        for(int i = 0; i < existingUserList.size(); i++) {
            if (currentUserInfo.get().getUserId().equals(existingUserList.get(i).getUserId())) {
                existingUserList.remove(i);
                break;
            } else {
                continue;
            }
        }
        message.setRecipientList(generateRecipientList(existingUserList));
        message.setContent(dto.getContent());
        message.setMessageThread(messageThread.get());
        message.setMessageType(MessageType.reply);
        message.setSender(currentUserInfo.get());
        message.setCreatedBy(currentUser.getId());
        messageRepository.save(message);
        return new ResponseDTO().generateSuccessResponse(String.format("Reply Send Successfully !!"));
    }

    /**
     * Get one All by MessageThreadId.
     *
     * @param messageThreadId the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ResponseDTO findAllByMessageThread(User currentUser, ObjectId messageThreadId) {
        log.debug("Request to get Message : {}", messageThreadId);
        Optional<MessageThread> messageThread = messageThreadRepository.findById(messageThreadId);
        if(!messageThread.isPresent()) {
            return new ResponseDTO<Message>().generateErrorResponse(String.format("No message thread found with the id - %s", messageThreadId.toHexString()));
        }
        List<Message> messages = messageRepository.findAllByMessageThreadId(messageThreadId);
        if(messages == null) {
            return new ResponseDTO<Message>().generateErrorResponse(String.format("No message thread found with the id - %s", messageThreadId.toHexString()));
        }
        return new ResponseDTO<List<Message>>().generateSuccessResponse(messages);
    }

    /**
     * Get one All by MessageThreadId.
     *
     * @param input the ListFilterInput of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ResponseDTO findAll(User currentUser, GetListFilterInput input, Pageable pageable) {
        log.debug("Request to get Message List:");
        Page<Message> messageList = null;
        if(input.getFilterParamsMap().containsKey("type") && input.getFilterParamsMap().get("type").equals("SENT")) {
            messageList = messageRepository.findAllForSender(pageable, currentUser.getObjectId());
        } else if(input.getFilterParamsMap().containsKey("type") && input.getFilterParamsMap().get("type").equals("INBOX")) {
            messageList = messageRepository.findAllForRecipient(pageable, currentUser.getObjectId());
        }
        return new ResponseDTO<Page<Message>>().generateSuccessResponse(messageList);
    }

    /**
     * Delete the message by id.
     *
     * @param messageThreadId the id of the entity
     */
    public ResponseDTO delete(User currentUser, ObjectId messageThreadId) {
        log.debug("Request to delete Message : {}", messageThreadId);
        Optional<MessageThread> messageThread = messageThreadRepository.findById(messageThreadId);
        messageThreadRepository.delete(messageThread.get());
        if (!messageThread.isPresent()) {
            return new ResponseDTO().generateErrorResponse(String.format("No message thread found to delete with the id - %s", messageThreadId.toHexString()));
        }
        return new ResponseDTO<Message>().generateSuccessResponse(null, String.format("Message Thread '%s' has been deleted", messageThread.get().getTitle()));
    }

}
