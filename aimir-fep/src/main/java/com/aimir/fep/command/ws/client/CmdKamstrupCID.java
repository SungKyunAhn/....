
package com.aimir.fep.command.ws.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdKamstrupCID complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdKamstrupCID">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MeterSerialNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CommandKind" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ControlRequests" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdKamstrupCID", propOrder = {
    "mcuId",
    "meterSerialNo",
    "commandKind",
    "controlRequests"
})
public class CmdKamstrupCID {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "MeterSerialNo")
    protected String meterSerialNo;
    @XmlElement(name = "CommandKind")
    protected String commandKind;
    @XmlElement(name = "ControlRequests")
    protected List<String> controlRequests;

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
     * Gets the value of the meterSerialNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeterSerialNo() {
        return meterSerialNo;
    }

    /**
     * Sets the value of the meterSerialNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeterSerialNo(String value) {
        this.meterSerialNo = value;
    }

    /**
     * Gets the value of the commandKind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommandKind() {
        return commandKind;
    }

    /**
     * Sets the value of the commandKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommandKind(String value) {
        this.commandKind = value;
    }

    /**
     * Gets the value of the controlRequests property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the controlRequests property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getControlRequests().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getControlRequests() {
        if (controlRequests == null) {
            controlRequests = new ArrayList<String>();
        }
        return this.controlRequests;
    }

}
