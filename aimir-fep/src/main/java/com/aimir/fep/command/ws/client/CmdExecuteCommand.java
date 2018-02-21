
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdExecuteCommand complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdExecuteCommand">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModemCommand" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="RWFlag" type="{http://www.w3.org/2001/XMLSchema}byte"/>
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
@XmlType(name = "cmdExecuteCommand", propOrder = {
    "mcuId",
    "modemId",
    "modemCommand",
    "rwFlag",
    "commandData"
})
public class CmdExecuteCommand {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "ModemId")
    protected String modemId;
    @XmlElement(name = "ModemCommand")
    protected byte modemCommand;
    @XmlElement(name = "RWFlag")
    protected byte rwFlag;
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
     * Gets the value of the modemCommand property.
     * 
     */
    public byte getModemCommand() {
        return modemCommand;
    }

    /**
     * Sets the value of the modemCommand property.
     * 
     */
    public void setModemCommand(byte value) {
        this.modemCommand = value;
    }

    /**
     * Gets the value of the rwFlag property.
     * 
     */
    public byte getRWFlag() {
        return rwFlag;
    }

    /**
     * Sets the value of the rwFlag property.
     * 
     */
    public void setRWFlag(byte value) {
        this.rwFlag = value;
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
