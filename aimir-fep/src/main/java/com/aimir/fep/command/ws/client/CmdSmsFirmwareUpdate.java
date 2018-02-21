
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdSmsFirmwareUpdate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdSmsFirmwareUpdate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FilePath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdSmsFirmwareUpdate", propOrder = {
    "modemId",
    "filePath"
})
public class CmdSmsFirmwareUpdate {

    @XmlElement(name = "ModemId")
    protected String modemId;
    @XmlElement(name = "FilePath")
    protected String filePath;

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
     * Gets the value of the filePath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the value of the filePath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilePath(String value) {
        this.filePath = value;
    }

}
