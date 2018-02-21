
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdBypassTimeSync complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdBypassTimeSync">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ModemSerialNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LoginId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdBypassTimeSync", propOrder = {
    "modemSerialNo",
    "loginId"
})
public class CmdBypassTimeSync {

    @XmlElement(name = "ModemSerialNo")
    protected String modemSerialNo;
    @XmlElement(name = "LoginId")
    protected String loginId;

    /**
     * Gets the value of the modemSerialNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModemSerialNo() {
        return modemSerialNo;
    }

    /**
     * Sets the value of the modemSerialNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModemSerialNo(String value) {
        this.modemSerialNo = value;
    }

    /**
     * Gets the value of the loginId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoginId() {
        return loginId;
    }

    /**
     * Sets the value of the loginId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoginId(String value) {
        this.loginId = value;
    }

}
