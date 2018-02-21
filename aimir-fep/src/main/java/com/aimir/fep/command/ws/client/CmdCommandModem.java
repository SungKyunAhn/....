
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdCommandModem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdCommandModem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CommandType" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="CommandData" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdCommandModem", propOrder = {
    "mcuId",
    "modemId",
    "commandType",
    "commandData"
})
public class CmdCommandModem {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "ModemId")
    protected String modemId;
    @XmlElement(name = "CommandType")
    protected byte commandType;
    @XmlElement(name = "CommandData")
    protected byte[] commandData;

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
     * Gets the value of the commandData property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getCommandData() {
        return commandData;
    }

    /**
     * Sets the value of the commandData property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setCommandData(byte[] value) {
        this.commandData = value;
    }

}
