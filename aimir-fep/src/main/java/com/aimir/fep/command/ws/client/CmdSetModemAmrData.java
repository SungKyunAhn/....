
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdSetModemAmrData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdSetModemAmrData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AMRMask" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="AMRData" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdSetModemAmrData", propOrder = {
    "mcuId",
    "modemId",
    "amrMask",
    "amrData"
})
public class CmdSetModemAmrData {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "ModemId")
    protected String modemId;
    @XmlElement(name = "AMRMask")
    protected byte[] amrMask;
    @XmlElement(name = "AMRData")
    protected byte[] amrData;

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
     * Gets the value of the amrMask property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getAMRMask() {
        return amrMask;
    }

    /**
     * Sets the value of the amrMask property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setAMRMask(byte[] value) {
        this.amrMask = value;
    }

    /**
     * Gets the value of the amrData property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getAMRData() {
        return amrData;
    }

    /**
     * Sets the value of the amrData property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setAMRData(byte[] value) {
        this.amrData = value;
    }

}
