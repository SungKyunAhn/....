
package com.aimir.fep.command.ws.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.datatype.SMIValue;


/**
 * <p>Java class for cmdGroupAsyncCall complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdGroupAsyncCall">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GroupKey" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Command" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nOption" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="nDay" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="nNice" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="nTry" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SMIValueList" type="{http://server.ws.command.fep.aimir.com/}smiValue" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdGroupAsyncCall", propOrder = {
    "mcuId",
    "groupKey",
    "command",
    "nOption",
    "nDay",
    "nNice",
    "nTry",
    "smiValueList"
})
public class CmdGroupAsyncCall {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "GroupKey")
    protected int groupKey;
    @XmlElement(name = "Command")
    protected String command;
    protected int nOption;
    protected int nDay;
    protected int nNice;
    protected int nTry;
    @XmlElement(name = "SMIValueList")
    protected List<SMIValue> smiValueList;

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
     * Gets the value of the groupKey property.
     * 
     */
    public int getGroupKey() {
        return groupKey;
    }

    /**
     * Sets the value of the groupKey property.
     * 
     */
    public void setGroupKey(int value) {
        this.groupKey = value;
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
     * Gets the value of the nOption property.
     * 
     */
    public int getNOption() {
        return nOption;
    }

    /**
     * Sets the value of the nOption property.
     * 
     */
    public void setNOption(int value) {
        this.nOption = value;
    }

    /**
     * Gets the value of the nDay property.
     * 
     */
    public int getNDay() {
        return nDay;
    }

    /**
     * Sets the value of the nDay property.
     * 
     */
    public void setNDay(int value) {
        this.nDay = value;
    }

    /**
     * Gets the value of the nNice property.
     * 
     */
    public int getNNice() {
        return nNice;
    }

    /**
     * Sets the value of the nNice property.
     * 
     */
    public void setNNice(int value) {
        this.nNice = value;
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
     * Gets the value of the smiValueList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the smiValueList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSMIValueList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SmiValue }
     * 
     * 
     */
    public List<SMIValue> getSMIValueList() {
        if (smiValueList == null) {
            smiValueList = new ArrayList<SMIValue>();
        }
        return this.smiValueList;
    }

}
