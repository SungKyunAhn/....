
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdSendMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdSendMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MessageId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="MessageType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Duration" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ErrorHandler" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="PreHandler" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="PostHandler" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="UserData" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pszData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdSendMessage", propOrder = {
    "mcuId",
    "modemId",
    "messageId",
    "messageType",
    "duration",
    "errorHandler",
    "preHandler",
    "postHandler",
    "userData",
    "pszData"
})
public class CmdSendMessage {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "ModemId")
    protected String modemId;
    @XmlElement(name = "MessageId")
    protected int messageId;
    @XmlElement(name = "MessageType")
    protected int messageType;
    @XmlElement(name = "Duration")
    protected int duration;
    @XmlElement(name = "ErrorHandler")
    protected int errorHandler;
    @XmlElement(name = "PreHandler")
    protected int preHandler;
    @XmlElement(name = "PostHandler")
    protected int postHandler;
    @XmlElement(name = "UserData")
    protected int userData;
    protected String pszData;

    /**
     * Gets the value of the mcuId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMcuId() {
        return mcuId;
    }

    /**
     * Sets the value of the mcuId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMcuId(String value) {
        this.mcuId = value;
    }

    /**
     * Gets the value of the modemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModemId() {
        return modemId;
    }

    /**
     * Sets the value of the modemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModemId(String value) {
        this.modemId = value;
    }

    /**
     * Gets the value of the messageId property.
     * 
     */
    public int getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     */
    public void setMessageId(int value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the messageType property.
     * 
     */
    public int getMessageType() {
        return messageType;
    }

    /**
     * Sets the value of the messageType property.
     * 
     */
    public void setMessageType(int value) {
        this.messageType = value;
    }

    /**
     * Gets the value of the duration property.
     * 
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     */
    public void setDuration(int value) {
        this.duration = value;
    }

    /**
     * Gets the value of the errorHandler property.
     * 
     */
    public int getErrorHandler() {
        return errorHandler;
    }

    /**
     * Sets the value of the errorHandler property.
     * 
     */
    public void setErrorHandler(int value) {
        this.errorHandler = value;
    }

    /**
     * Gets the value of the preHandler property.
     * 
     */
    public int getPreHandler() {
        return preHandler;
    }

    /**
     * Sets the value of the preHandler property.
     * 
     */
    public void setPreHandler(int value) {
        this.preHandler = value;
    }

    /**
     * Gets the value of the postHandler property.
     * 
     */
    public int getPostHandler() {
        return postHandler;
    }

    /**
     * Sets the value of the postHandler property.
     * 
     */
    public void setPostHandler(int value) {
        this.postHandler = value;
    }

    /**
     * Gets the value of the userData property.
     * 
     */
    public int getUserData() {
        return userData;
    }

    /**
     * Sets the value of the userData property.
     * 
     */
    public void setUserData(int value) {
        this.userData = value;
    }

    /**
     * Gets the value of the pszData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPszData() {
        return pszData;
    }

    /**
     * Sets the value of the pszData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPszData(String value) {
        this.pszData = value;
    }

}
