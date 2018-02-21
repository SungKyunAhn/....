
package com.aimir.fep.command.ws.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdAsynchronousCall complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdAsynchronousCall">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MIUType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MIUClassName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MIUId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Command" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Option" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Day" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Nice" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NTry" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CommandOIDArgs" type="{http://jaxb.dev.java.net/array}stringArray" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ServiceType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Operator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdAsynchronousCall", propOrder = {
    "mcuId",
    "miuType",
    "miuClassName",
    "miuId",
    "command",
    "option",
    "day",
    "nice",
    "nTry",
    "commandOIDArgs",
    "serviceType",
    "operator"
})
public class CmdAsynchronousCall {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "MIUType")
    protected String miuType;
    @XmlElement(name = "MIUClassName")
    protected String miuClassName;
    @XmlElement(name = "MIUId")
    protected String miuId;
    @XmlElement(name = "Command")
    protected String command;
    @XmlElement(name = "Option")
    protected int option;
    @XmlElement(name = "Day")
    protected int day;
    @XmlElement(name = "Nice")
    protected int nice;
    @XmlElement(name = "NTry")
    protected int nTry;
    @XmlElement(name = "CommandOIDArgs")
    protected List<StringArray> commandOIDArgs;
    @XmlElement(name = "ServiceType")
    protected int serviceType;
    @XmlElement(name = "Operator")
    protected String operator;

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
     * Gets the value of the miuClassName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMIUClassName() {
        return miuClassName;
    }

    /**
     * Sets the value of the miuClassName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMIUClassName(String value) {
        this.miuClassName = value;
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
     * Gets the value of the command property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the value of the command property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommand(String value) {
        this.command = value;
    }

    /**
     * Gets the value of the option property.
     * 
     */
    public int getOption() {
        return option;
    }

    /**
     * Sets the value of the option property.
     * 
     */
    public void setOption(int value) {
        this.option = value;
    }

    /**
     * Gets the value of the day property.
     * 
     */
    public int getDay() {
        return day;
    }

    /**
     * Sets the value of the day property.
     * 
     */
    public void setDay(int value) {
        this.day = value;
    }

    /**
     * Gets the value of the nice property.
     * 
     */
    public int getNice() {
        return nice;
    }

    /**
     * Sets the value of the nice property.
     * 
     */
    public void setNice(int value) {
        this.nice = value;
    }

    /**
     * Gets the value of the nTry property.
     * 
     */
    public int getNTry() {
        return nTry;
    }

    /**
     * Sets the value of the nTry property.
     * 
     */
    public void setNTry(int value) {
        this.nTry = value;
    }

    /**
     * Gets the value of the commandOIDArgs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the commandOIDArgs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommandOIDArgs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StringArray }
     * 
     * 
     */
    public List<StringArray> getCommandOIDArgs() {
        if (commandOIDArgs == null) {
            commandOIDArgs = new ArrayList<StringArray>();
        }
        return this.commandOIDArgs;
    }

    /**
     * Gets the value of the serviceType property.
     * 
     */
    public int getServiceType() {
        return serviceType;
    }

    /**
     * Sets the value of the serviceType property.
     * 
     */
    public void setServiceType(int value) {
        this.serviceType = value;
    }

    /**
     * Gets the value of the operator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Sets the value of the operator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperator(String value) {
        this.operator = value;
    }

}
