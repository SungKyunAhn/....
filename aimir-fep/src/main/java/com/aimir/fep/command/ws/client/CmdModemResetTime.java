package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for cmdModemResetTime complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="cmdModemResetTime">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RequestType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ResetTime" type="{http://www.w3.org/2001/XMLSchema}int"  minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdModemResetTime", propOrder = {
        "modemId",
        "requestType",
        "resetTime"
})
public class CmdModemResetTime {
    @XmlElement(name = "ModemId")
    protected String modemId;

    @XmlElement(name = "RequestType")
    protected String requestType;

    @XmlElement(name = "ResetTime")
    protected int resetTime;

    /**
     * Get the value of the modemOd property.
     * @return String
     */
    public String getModemId() {
        return modemId;
    }

    /**
     * Set the value of the modemId property.
     * @param modemId
     */
    public void setModemId(String modemId) {
        this.modemId = modemId;
    }

    /**
     * Get the value of the requestType property.
     * @return String
     */
    public String getRequestType() {
        return requestType;
    }

    /**
     * Set the value of the requestType property.
     * @param requestType
     */
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    /**
     * Get the value of the resetTime property.
     * @return String
     */
    public int getResetTime() {
        return resetTime;
    }

    /**
     * Set the value of the resetTime property.
     * @param rateValue
     */
    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }
}
