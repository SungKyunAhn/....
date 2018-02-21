package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for cmdRealTimeMetering complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="cmdRealTimeMetering">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mdsId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="interval" type="{http://www.w3.org/2001/XMLSchema}int" />
 *         &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdRealTimeMetering", propOrder = {
        "mdsId",
        "interval",
        "duration"
})
public class CmdRealTimeMetering {
    @XmlElement(name = "MdsId")
    protected String mdsId;

    @XmlElement(name = "Interval")
    protected int interval;

    @XmlElement(name = "Duration")
    protected int duration;

    /**
     * Get the value of the mdsId property.
     * @return String
     */
    public String getMdsId() {
        return mdsId;
    }

    /**
     * Set the value of the mdsId property.
     * @param mdsId
     */
    public void setMdsId(String mdsId) {
        this.mdsId = mdsId;
    }

    /**
     * Get the value of the interval property.
     * @return int
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Set the value of the interval property.
     * @param interval
     */
    public void setIntervale(int interval) {
        this.interval = interval;
    }
    
    /**
     * Get the value of the Duration property.
     * @return int
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Set the value of the Duration property.
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    
}
