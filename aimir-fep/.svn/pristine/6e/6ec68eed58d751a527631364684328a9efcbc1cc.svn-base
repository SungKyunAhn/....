
package com.aimir.fep.command.ws.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdBypassSensor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdBypassSensor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsLinkSkip" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="MultiFrameDataStream" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdBypassSensor", propOrder = {
    "mcuId",
    "modemId",
    "isLinkSkip",
    "multiFrameDataStream"
})
public class CmdBypassSensor {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "ModemId")
    protected String modemId;
    @XmlElement(name = "IsLinkSkip")
    protected boolean isLinkSkip;
    @XmlElement(name = "MultiFrameDataStream")
    protected List<Object> multiFrameDataStream;

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
     * Gets the value of the isLinkSkip property.
     * 
     */
    public boolean isIsLinkSkip() {
        return isLinkSkip;
    }

    /**
     * Sets the value of the isLinkSkip property.
     * 
     */
    public void setIsLinkSkip(boolean value) {
        this.isLinkSkip = value;
    }

    /**
     * Gets the value of the multiFrameDataStream property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the multiFrameDataStream property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMultiFrameDataStream().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getMultiFrameDataStream() {
        if (multiFrameDataStream == null) {
            multiFrameDataStream = new ArrayList<Object>();
        }
        return this.multiFrameDataStream;
    }

}
