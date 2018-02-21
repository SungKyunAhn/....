package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for getModemEventLog complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getModemEventLog">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rateValue" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getModemEventLog", propOrder = {
        "mdsId",
        "logCount",
        "logOffset"		// INSERT SP-681
})
public class GetModemEventLog {
    @XmlElement(name = "MdsId")
    protected String mdsId;

    @XmlElement(name = "LogCount")
    protected int logCount;

    // INSERT START SP-681
    @XmlElement(name = "LogOffset")
    protected int logOffset;
    // INSERT END SP-681

    public String getMdsId() {
        return mdsId;
    }

    public void setMdsId(String mdsId) {
        this.mdsId = mdsId;
    }

    public int getLogCount() {
        return logCount;
    }

    public void setLogCount(int logCount) {
        this.logCount = logCount;
    }
    
 // INSERT START SP-681
    public int getLogOffset() {
        return logOffset;
    }

    public void setLogOffset(int logOffset) {
        this.logOffset = logOffset;
    }
    
 // INSERT END SP-681
}
