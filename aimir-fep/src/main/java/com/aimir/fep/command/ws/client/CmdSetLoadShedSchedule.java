
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdSetLoadShedSchedule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdSetLoadShedSchedule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EntryNo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CheckInterval" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "cmdSetLoadShedSchedule", propOrder = {
    "mcuId",
    "entryNo",
    "checkInterval",
    "scheduleType",
    "startTime",
    "endTime",
    "weekly"
})
public class CmdSetLoadShedSchedule {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "EntryNo")
    protected int entryNo;
    @XmlElement(name = "CheckInterval")
    protected int checkInterval;
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
     * Gets the value of the checkInterval property.
     * 
     */
    public int getCheckInterval() {
        return checkInterval;
    }

    /**
     * Sets the value of the checkInterval property.
     * 
     */
    public void setCheckInterval(int value) {
        this.checkInterval = value;
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
