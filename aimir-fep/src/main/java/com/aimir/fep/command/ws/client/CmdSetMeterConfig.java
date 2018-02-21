
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdSetMeterConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdSetMeterConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Mask" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CurrentPulse" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SerialNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdSetMeterConfig", propOrder = {
    "mcuId",
    "modemId",
    "mask",
    "currentPulse",
    "serialNo"
})
public class CmdSetMeterConfig {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "ModemId")
    protected String modemId;
    @XmlElement(name = "Mask")
    protected int mask;
    @XmlElement(name = "CurrentPulse")
    protected int currentPulse;
    @XmlElement(name = "SerialNo")
    protected String serialNo;

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
     * Gets the value of the mask property.
     * 
     */
    public int getMask() {
        return mask;
    }

    /**
     * Sets the value of the mask property.
     * 
     */
    public void setMask(int value) {
        this.mask = value;
    }

    /**
     * Gets the value of the currentPulse property.
     * 
     */
    public int getCurrentPulse() {
        return currentPulse;
    }

    /**
     * Sets the value of the currentPulse property.
     * 
     */
    public void setCurrentPulse(int value) {
        this.currentPulse = value;
    }

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
    public void setSerialNo(String value) {
        this.serialNo = value;
    }

}
