
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdMcuSetGpio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdMcuSetGpio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GPIOPortNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="GPIOValue" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdMcuSetGpio", propOrder = {
    "mcuId",
    "gpioPortNumber",
    "gpioValue"
})
public class CmdMcuSetGpio {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "GPIOPortNumber")
    protected int gpioPortNumber;
    @XmlElement(name = "GPIOValue")
    protected int gpioValue;

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
     * Gets the value of the gpioPortNumber property.
     * 
     */
    public int getGPIOPortNumber() {
        return gpioPortNumber;
    }

    /**
     * Sets the value of the gpioPortNumber property.
     * 
     */
    public void setGPIOPortNumber(int value) {
        this.gpioPortNumber = value;
    }

    /**
     * Gets the value of the gpioValue property.
     * 
     */
    public int getGPIOValue() {
        return gpioValue;
    }

    /**
     * Sets the value of the gpioValue property.
     * 
     */
    public void setGPIOValue(int value) {
        this.gpioValue = value;
    }

}
