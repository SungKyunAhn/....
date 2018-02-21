
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.frame.service.entry.endDeviceEntry;
/**
 * <p>Java class for cmdSetDRLevel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdSetDRLevel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endDeviceEntry" type="{http://server.ws.command.fep.aimir.com/}endDeviceEntry" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdSetDRLevel", propOrder = {
    "mcuId",
    "endDeviceEntry"
})
public class CmdSetDRLevel {

    @XmlElement(name = "McuId")
    protected String mcuId;
    protected endDeviceEntry endDeviceEntry;

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
     * Gets the value of the endDeviceEntry property.
     * 
     * @return
     *     possible object is
     *     {@link EndDeviceEntry }
     *     
     */
    public endDeviceEntry getEndDeviceEntry() {
        return endDeviceEntry;
    }

    /**
     * Sets the value of the endDeviceEntry property.
     * 
     * @param value
     *     allowed object is
     *     {@link EndDeviceEntry }
     *     
     */
    public void setEndDeviceEntry(endDeviceEntry value) {
        this.endDeviceEntry = value;
    }

}
