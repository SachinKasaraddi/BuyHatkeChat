package com.buyhatke.chat.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class MessageObject extends RealmObject {

    private String msgId;
    private boolean isGroupMsg;
    private String content;
    private boolean incoming;
    private String from;
    private String to;
    private String sender;
    private Date timestamp;
    private boolean isFile;
    private String fileType;
    private String fileUrl;
    private String localFileUrl;
    private int fileUDStatus;
    public MessageObject(String content, boolean incoming,
                         String from, String to, String msgId,
                         boolean isGroupMsg, String sender, Date timestamp, boolean isFile, String fileType, String fileUrl, String localFileUrl, int fileUDStatus) {
        this.content = content;
        this.incoming = incoming;
        this.from = from;
        this.to = to;
        this.msgId = msgId;
        this.isGroupMsg = isGroupMsg;
        this.sender = sender;
        this.timestamp = timestamp;
        this.fileType = fileType;
        this.isFile = isFile;
        this.fileUrl = fileUrl;
        this.localFileUrl = localFileUrl;
        this.fileUDStatus = fileUDStatus;
    }

    public MessageObject() {

    }


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public boolean isGroupMsg() {
        return isGroupMsg;
    }

    public void setIsGroupMsg(boolean isGroupMsg) {
        this.isGroupMsg = isGroupMsg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isIncoming() {
        return incoming;
    }

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getLocalFileUrl() {
        return localFileUrl;
    }

    public void setLocalFileUrl(String localFileUrl) {
        this.localFileUrl = localFileUrl;
    }

    public int getFileUDStatus() {
        return fileUDStatus;
    }

    public void setFileUDStatus(int fileUDStatus) {
        this.fileUDStatus = fileUDStatus;
    }
}

