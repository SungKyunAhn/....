
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdSetLoadLimitScheme complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdSetLoadLimitScheme">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EntryNo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="LimitType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Limit" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="IntervalNo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OpenPeriod" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ScheduleType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="StartTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EndTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Weekly" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdSetLoadLimitScheme", propOrder = {
    "mcuId",
    "modemId",
    "entryNo",
    "limitType",
    "limit",
    "intervalNo",
    "openPeriod",
    "scheduleType",
    "startTime",
    "endTime",
    "weekly"
})
public class CmdSetLoadLimitScheme {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "ModemId")
    protected String modemId;
    @XmlElement(name = "EntryNo")
    protected int entryNo;
    @XmlElement(name = "LimitType")
    protected int limitType;
    @XmlElement(name = "Limit")
    protected long limit;
    @XmlElement(name = "IntervalNo")
    protected int intervalNo;
    @XmlElement(name = "OpenPeriod")
    protected int openPeriod;
    @XmlElement(name = "ScheduleType")
    protected int scheduleType;
    @XmlElement(name = "StartTime")
    protected String startTime;
    @XmlElement(name = "EndTime")
    protected String endTime;
    @XmlElement(name = "Weekly")
    protected int weekly;

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
     * Gets the value of the entryNo property.
     * 
     */
    public int getEntryNo() {
        return entryNo;
    }

    /**
     * Sets the value of the entryNo property.
     * 
     */
    public void setEntryNo(int value) {
        this.entryNo = value;
    }

    /**
     * Gets the value of the limitType property.
     * 
     */
    public int getLimitType() {
        return limitType;
    }

    /**
     * Sets the value of the limitType property.
     * 
     */
    public void setLimitType(int value) {
        this.limitType = value;
    }

    /**
     * Gets the value of the limit property.
     * 
     */
    public long getLimit() {
        return limit;
    }

    /**
     * Sets the value of the limit property.
     * 
     */
    public void setLimit(long value) {
        this.limit = value;
    }

    /**
     * Gets the value of the intervalNo property.
     * 
     */
    public int getIntervalNo() {
        return intervalNo;
    }

    /**
     * Sets the value of the intervalNo property.
     * 
     */
    public void setIntervalNo(int value) {
        this.intervalNo = value;
    }

    /**
     * Gets the value of the openPeriod property.
     * 
     */
    public int getOpenPeriod() {
        return openPeriod;
    }

    /**
     * Sets the value of the openPeriod property.
     * 
     */
    public void setOpenPeriod(int value) {
        this.openPeriod = value;
    }

    /**
     * Gets the value of the scheduleType property.
     * 
     */
    public int getScheduleType() {
        return scheduleType;
    }

    /**
     * Sets the value of the scheduleType property.
     * 
     */
    public void setScheduleType(int value) {
        this.scheduleType = value;
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartTime(String value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the endTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets the value of the endTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndTime(String value) {
        this.endTime = value;
    }

    /**
     * Gets the value of the weekly property.
     * 
     */
    public int getWeekly() {
        return weekly;
    }

    /**
     * Sets the value of the weekly property.
     * 
     */
    public void setWeekly(int value) {
        this.weekly = value;
    }

}
