
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdShowTransactionList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdShowTransactionList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MIUType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MIUId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Build" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdShowTransactionList", propOrder = {
    "mcuId",
    "miuType",
    "miuId",
    "parserName",
    "version",
    "build"
})
public class CmdShowTransactionList {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "MIUType")
    protected String miuType;
    @XmlElement(name = "MIUId")
    protected String miuId;
    @XmlElement(name = "ParserName")
    protected String parserName;
    @XmlElement(name = "Version")
    protected String version;
    @XmlElement(name = "Build")
    protected String build;

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
     * Gets the value of the miuType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMIUType() {
        return miuType;
    }

    /**
     * Sets the value of the miuType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMIUType(String value) {
        this.miuType = value;
    }

    /**
     * Gets the value of the miuId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMIUId() {
        return miuId;
    }

    /**
     * Sets the value of the miuId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMIUId(String value) {
        this.miuId = value;
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
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the build property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuild() {
        return build;
    }

    /**
     * Sets the value of the build property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuild(String value) {
        this.build = value;
    }

}
