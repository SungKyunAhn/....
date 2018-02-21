
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdCommandModem1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdCommandModem1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CommandType" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="FW" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="BuildNo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Force" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Data" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdCommandModem1", propOrder = {
    "mcuId",
    "modemId",
    "commandType",
    "fw",
    "buildNo",
    "force",
    "data"
})
public class CmdCommandModem1 {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "ModemId")
    protected String modemId;
    @XmlElement(name = "CommandType")
    protected byte commandType;
    @XmlElement(name = "FW")
    protected int fw;
    @XmlElement(name = "BuildNo")
    protected int buildNo;
    @XmlElement(name = "Force")
    protected boolean force;
    @XmlElement(name = "Data")
    protected byte[] data;

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
     * Gets the value of the commandType property.
     * 
     */
    public byte getCommandType() {
        return commandType;
    }

    /**
     * Sets the value of the commandType property.
     * 
     */
    public void setCommandType(byte value) {
        this.commandType = value;
    }

    /**
     * Gets the value of the fw property.
     * 
     */
    public int getFW() {
        return fw;
    }

    /**
     * Sets the value of the fw property.
     * 
     */
    public void setFW(int value) {
        this.fw = value;
    }

    /**
     * Gets the value of the buildNo property.
     * 
     */
    public int getBuildNo() {
        return buildNo;
    }

    /**
     * Sets the value of the buildNo property.
     * 
     */
    public void setBuildNo(int value) {
        this.buildNo = value;
    }

    /**
     * Gets the value of the force property.
     * 
     */
    public boolean isForce() {
        return force;
    }

    /**
     * Sets the value of the force property.
     * 
     */
    public void setForce(boolean value) {
        this.force = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setData(byte[] value) {
        this.data = value;
    }

}
