 // INSERT START SP-677
package com.aimir.fep.command.ws.client;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdDmdNiGetRomRead complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdDmdNiGetRomRead">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MeterSerialNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FromDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ToDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdDmdNiGetRomReadMulti", propOrder = {
    "mcuId",
    "modemPort",
    "meterIdList",
    "modemIdList",
    "fromDate",
    "toDate"
})
public class CmdDmdNiGetRomReadMulti {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "ModemPort")
    protected Integer modemPort;
	@XmlElement(name = "MeterIdList")
    protected List<String> meterIdList;
	@XmlElement(name = "ModemIdList")
    protected List<String> modemIdList;
    @XmlElement(name = "FromDate")
    protected String fromDate;
    @XmlElement(name = "ToDate")
    protected String toDate;

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
	 * @return the modemPort
	 */
	public Integer getModemPort() {
		return modemPort;
	}

	/**
	 * @param modemPort the modemPort to set
	 */
	public void setModemPort(Integer modemPort) {
		this.modemPort = modemPort;
	}
    /**
     * Gets the value of the fromDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromDate() {
        return fromDate;
    }

    /**
     * Sets the value of the fromDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromDate(String value) {
        this.fromDate = value;
    }

    /**
     * Gets the value of the toDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToDate() {
        return toDate;
    }

    /**
     * Sets the value of the toDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToDate(String value) {
        this.toDate = value;
    }
    /**
     * Gets the value of the modemIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modemIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModemIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getModemIdList() {
        if (modemIdList == null) {
        	modemIdList = new ArrayList<String>();
        }
        return this.modemIdList;
    }
    public List<String> getMeterIdList() {
        if (meterIdList == null) {
        	meterIdList = new ArrayList<String>();
        }
        return this.meterIdList;
    }
}
