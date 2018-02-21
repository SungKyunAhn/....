package com.aimir.cms.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthCred", propOrder = {
    "userName",
    "password",
    "messageId",
    "messageTimestamp",
    "clientId",
    "regionCode",
    "meterCode"
})
public class AuthCred {
    @XmlElement(name = "userName")
    private String userName;
    @XmlElement(name = "password")
    private String password;
    @XmlElement(name = "messageID")
    private Long messageId;
    @XmlElement(name = "messageTimestamp")
    private String messageTimestamp;
    @XmlElement(name = "clientID")
    private String clientId;
    @XmlElement(name = "RegionCode")
    private String regionCode;
    @XmlElement(name = "MeterCode")
    private String meterCode;
    
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Long getMessageId() {
        return messageId;
    }
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    public String getMessageTimestamp() {
        return messageTimestamp;
    }
    public void setMessageTimestamp(String messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getRegionCode() {
        return regionCode;
    }
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
    public String getMeterCode() {
        return meterCode;
    }
    public void setMeterCode(String meterCode) {
        this.meterCode = meterCode;
    }
    
}
