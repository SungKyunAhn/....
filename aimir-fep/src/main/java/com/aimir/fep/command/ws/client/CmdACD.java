
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdACD complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdACD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OnOff" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DelayTime" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="RandomTime" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdACD", propOrder = {
    "mcuId",
    "modemId",
    "onOff",
    "delayTime",
    "randomTime"
})
public class CmdACD {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "ModemId")
    protected String modemId;
    @XmlElement(name = "OnOff")
    protected int onOff;
    @XmlElement(name = "DelayTime")
    protected int delayTime;
    @XmlElement(name = "RandomTime")
    protected int randomTime;

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
     * Gets the value of the onOff property.
     * 
     */
    public int getOnOff() {
        return onOff;
    }

    /**
     * Sets the value of the onOff property.
     * 
     */
    public void setOnOff(int value) {
        this.onOff = value;
    }

    /**
     * Gets the value of the delayTime property.
     * 
     */
    public int getDelayTime() {
        return delayTime;
    }

    /**
     * Sets the value of the delayTime property.
     * 
     */
    public void setDelayTime(int value) {
        this.delayTime = value;
    }

    /**
     * Gets the value of the randomTime property.
     * 
     */
    public int getRandomTime() {
        return randomTime;
    }

    /**
     * Sets the value of the randomTime property.
     * 
     */
    public void setRandomTime(int value) {
        this.randomTime = value;
    }

}
