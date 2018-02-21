
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdAuthSPModem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdAuthSPModem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SerialNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdAuthSPModem", propOrder = {
    "serialNo",
    "status"
})
public class CmdAuthSPModem {

    @XmlElement(name = "SerialNo")
    protected String serialNo;
    @XmlElement(name = "Status")
    protected int status;

    /**
     * Gets the value of the serialNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * Sets the value of the serialNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link int }
     *     
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link int }
     *     
     */
    public void setStatus(int status) {
        this.status = status;
    }
}
