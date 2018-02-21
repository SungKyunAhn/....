
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdGetFFDList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdGetFFDList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FwVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FwBuild" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdGetFFDList", propOrder = {
    "mcuId",
    "parserName",
    "fwVersion",
    "fwBuild"
})
public class CmdGetFFDList {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "ParserName")
    protected String parserName;
    @XmlElement(name = "FwVersion")
    protected String fwVersion;
    @XmlElement(name = "FwBuild")
    protected String fwBuild;

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
     * Gets the value of the parserName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParserName() {
        return parserName;
    }

    /**
     * Sets the value of the parserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParserName(String value) {
        this.parserName = value;
    }

    /**
     * Gets the value of the fwVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFwVersion() {
        return fwVersion;
    }

    /**
     * Sets the value of the fwVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFwVersion(String value) {
        this.fwVersion = value;
    }

    /**
     * Gets the value of the fwBuild property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFwBuild() {
        return fwBuild;
    }

    /**
     * Sets the value of the fwBuild property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFwBuild(String value) {
        this.fwBuild = value;
    }

}
