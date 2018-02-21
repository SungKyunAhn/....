
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.model.device.Modem;


/**
 * <p>Java class for cmdAddModem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdAddModem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModemObject" type="{http://server.ws.command.fep.aimir.com/}modem" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdAddModem", propOrder = {
    "mcuId",
    "modemObject"
})
public class CmdAddModem {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "ModemObject")
    protected Modem modemObject;

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
     * Gets the value of the modemObject property.
     * 
     * @return
     *     possible object is
     *     {@link Modem }
     *     
     */
    public Modem getModemObject() {
        return modemObject;
    }

    /**
     * Sets the value of the modemObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link Modem }
     *     
     */
    public void setModemObject(Modem value) {
        this.modemObject = value;
    }

}
