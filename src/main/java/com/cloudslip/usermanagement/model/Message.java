package com.cloudslip.usermanagement.model;

import com.cloudslip.usermanagement.enums.MessageType;

import java.security.PrivateKey;
import java.util.List;

public class Message extends BaseEntity {

    private MessageThread messageThread;
    private UserInfo sender;
    private String content;
    private String subject;
    private List<Recipient> recipientList;
    private MessageType messageType;

    public Message() {
    }

    public MessageThread getMessageThread() {
        return messageThread;
    }

    public void setMessageThread(MessageThread messageThread) {
        this.messageThread = messageThread;
    }

    public UserInfo getSender() {
        return sender;
    }

    public void setSender(UserInfo sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<Recipient> getRecipientList() {
        return recipientList;
    }

    public void setRecipientList(List<Recipient> recipientList) {
        this.recipientList = recipientList;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
