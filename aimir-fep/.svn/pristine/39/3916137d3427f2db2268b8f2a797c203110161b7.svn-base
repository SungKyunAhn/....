package com.aimir.fep.protocol.fmp.frame.service.entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.frame.service.Entry;


/**
 * 11.1 loadControlSchemeEntry
 *
 * @author J.S Park
 * @version $Rev: 1 $, $Date: 2009-03-31 15:59:15 +0900 $,
 * <pre>
 * &lt;complexType name="loadLimitSchemeEntry">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}entry">
 *       &lt;sequence>
 *         &lt;element name="llsID" type="{http://server.ws.command.fep.aimir.com/}hex" minOccurs="0"/>
 *         &lt;element name="llsEntryNumber" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="llsLimitType" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="llsLimit" type="{http://server.ws.command.fep.aimir.com/}uint" minOccurs="0"/>
 *         &lt;element name="llsIntervalNumber" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="llsRestorationTime" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="llsSchemeType" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="llsStartTime" type="{http://server.ws.command.fep.aimir.com/}timestamp" minOccurs="0"/>
 *         &lt;element name="llsEndTime" type="{http://server.ws.command.fep.aimir.com/}timestamp" minOccurs="0"/>
 *         &lt;element name="llsWeekly" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "loadLimitSchemeEntry", propOrder = {
    "llsID",
    "llsEntryNumber",
    "llsLimitType",
    "llsLimit",
    "llsIntervalNumber",
    "llsRestorationTime",
    "llsSchemeType",
    "llsStartTime",
    "llsEndTime",
    "llsWeekly"
})
public class loadLimitSchemeEntry extends Entry {

    public HEX llsID = new HEX(8); 
    public BYTE llsEntryNumber = new BYTE();
    public BYTE llsLimitType = new BYTE();
    public UINT llsLimit = new UINT(4);
    public BYTE llsIntervalNumber = new BYTE();
    public BYTE llsRestorationTime = new BYTE();
    public BYTE llsSchemeType = new BYTE();
    public TIMESTAMP llsStartTime = new TIMESTAMP();
    public TIMESTAMP llsEndTime = new TIMESTAMP();
    public BYTE llsWeekly = new BYTE();

    @XmlTransient
    public HEX getLlsID()
    {
        return llsID;
    }

    public void setLlsID(HEX llsID)
    {
        this.llsID = llsID;
    }

    @XmlTransient
    public BYTE getLlsEntryNumber()
    {
        return llsEntryNumber;
    }

    public void setLlsEntryNumber(BYTE llsEntryNumber)
    {
        this.llsEntryNumber = llsEntryNumber;
    }

    @XmlTransient
    public BYTE getLlsLimitType()
    {
        return llsLimitType;
    }

    public void setLlsLimitType(BYTE llsLimitType)
    {
        this.llsLimitType = llsLimitType;
    }

    @XmlTransient
    public UINT getLlsLimit()
    {
        return llsLimit;
    }

    public void setLlsLimit(UINT llsLimit)
    {
        this.llsLimit = llsLimit;
    }

    @XmlTransient
    public BYTE getLlsIntervalNumber()
    {
        return llsIntervalNumber;
    }

    public void setLlsIntervalNumber(BYTE llsIntervalNumber)
    {
        this.llsIntervalNumber = llsIntervalNumber;
    }

    @XmlTransient
    public BYTE getLlsRestorationTime()
    {
        return llsRestorationTime;
    }

    public void setLlsRestorationTime(BYTE llsRestorationTime)
    {
        this.llsRestorationTime = llsRestorationTime;
    }

    @XmlTransient
    public BYTE getLlsSchemeType()
    {
        return llsSchemeType;
    }

    public void setLlsSchemeType(BYTE llsSchemeType)
    {
        this.llsSchemeType = llsSchemeType;
    }

    @XmlTransient
    public TIMESTAMP getLlsStartTime()
    {
        return llsStartTime;
    }

    public void setLlsStartTime(TIMESTAMP llsStartTime)
    {
        this.llsStartTime = llsStartTime;
    }

    @XmlTransient
    public TIMESTAMP getLlsEndTime()
    {
        return llsEndTime;
    }

    public void setLlsEndTime(TIMESTAMP llsEndTime)
    {
        this.llsEndTime = llsEndTime;
    }

    @XmlTransient
    public BYTE getLlsWeekly()
    {
        return llsWeekly;
    }

    public void setLlsWeekly(BYTE llsWeekly)
    {
        this.llsWeekly = llsWeekly;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

		sb.append("CLASS["+this.getClass().getName()+"]\n");
        sb.append("llsID: " + llsID + "\n");
        sb.append("llsEntryNumber: " + llsEntryNumber + "\n");
        sb.append("llsLimitType: " + llsLimitType + "\n");
        sb.append("llsLimit: " + llsLimit + "\n");
        sb.append("llsIntervalNumber: " + llsIntervalNumber + "\n");
        sb.append("llsRestorationTime: " + llsRestorationTime + "\n");
        sb.append("llsSchemeType: " + llsSchemeType + "\n");
        sb.append("llsStartTime: " + llsStartTime + "\n");
        sb.append("llsEndTime: " + llsEndTime + "\n");
        sb.append("llsWeekly: " + llsWeekly + "\n");

        return sb.toString();
    }
}
