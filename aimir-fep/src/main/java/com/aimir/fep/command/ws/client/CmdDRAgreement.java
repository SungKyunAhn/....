
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.frame.service.entry.drLevelEntry;
/**
 * <p>Java class for cmdDRAgreement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdDRAgreement">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DRLevelEntry" type="{http://server.ws.command.fep.aimir.com/}drLevelEntry" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdDRAgreement", propOrder = {
    "mcuId",
    "drLevelEntry"
})
public class CmdDRAgreement {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "DRLevelEntry")
    protected drLevelEntry drLevelEntry;

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
     * Gets the value of the drLevelEntry property.
     * 
     * @return
     *     possible object is
     *     {@link DrLevelEntry }
     *     
     */
    public drLevelEntry getDRLevelEntry() {
        return drLevelEntry;
    }

    /**
     * Sets the value of the drLevelEntry property.
     * 
     * @param value
     *     allowed object is
     *     {@link DrLevelEntry }
     *     
     */
    public void setDRLevelEntry(drLevelEntry value) {
        this.drLevelEntry = value;
    }

}
