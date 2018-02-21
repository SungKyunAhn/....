
package com.aimir.fep.command.ws.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdSensorLPLogRecovery complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdSensorLPLogRecovery">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MeterSerialNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MeteringValue" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="LpInterval" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="LpList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdSensorLPLogRecovery", propOrder = {
    "meterSerialNo",
    "mcuId",
    "modemId",
    "meteringValue",
    "lpInterval",
    "lpList"
})
public class CmdSensorLPLogRecovery {

    @XmlElement(name = "MeterSerialNo")
    protected String meterSerialNo;
    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "ModemId")
    protected String modemId;
    @XmlElement(name = "MeteringValue")
    protected double meteringValue;
    @XmlElement(name = "LpInterval")
    protected int lpInterval;
    @XmlElement(name = "LpList", type = Integer.class)
    protected List<Integer> lpList;

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
     * Gets the value of the meteringValue property.
     * 
     */
    public double getMeteringValue() {
        return meteringValue;
    }

    /**
     * Sets the value of the meteringValue property.
     * 
     */
    public void setMeteringValue(double value) {
        this.meteringValue = value;
    }

    /**
     * Gets the value of the lpInterval property.
     * 
     */
    public int getLpInterval() {
        return lpInterval;
    }

    /**
     * Sets the value of the lpInterval property.
     * 
     */
    public void setLpInterval(int value) {
        this.lpInterval = value;
    }

    /**
     * Gets the value of the lpList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lpList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLpList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getLpList() {
        if (lpList == null) {
            lpList = new ArrayList<Integer>();
        }
        return this.lpList;
    }

}
