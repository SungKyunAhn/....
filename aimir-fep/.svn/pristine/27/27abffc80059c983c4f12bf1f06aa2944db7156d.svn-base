package com.aimir.fep.protocol.fmp.frame.service.entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.frame.service.Entry;



/**
 * 11.4 loadShedSchemeEntry
 *
 * @author YK Park
 * @version $Rev: 1 $, $Date: 2009-04-06 10:46:15 +0900 $,
 * <pre>
 * &lt;complexType name="loadShedSchemeEntry">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}entry">
 *       &lt;sequence>
 *         &lt;element name="lssEntryNumber" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="lssCheckInterval" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="lssSchemeType" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="lssStartTime" type="{http://server.ws.command.fep.aimir.com/}timestamp" minOccurs="0"/>
 *         &lt;element name="lssEndTime" type="{http://server.ws.command.fep.aimir.com/}timestamp" minOccurs="0"/>
 *         &lt;element name="lssWeekly" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "loadShedSchemeEntry", propOrder = {
    "lssEntryNumber",
    "lssCheckInterval",
    "lssSchemeType",
    "lssStartTime",
    "lssEndTime",
    "lssWeekly"
})
public class loadShedSchemeEntry extends Entry {

    public BYTE lssEntryNumber = new BYTE();
    //public UINT lssSupply = new UINT(4);
    //public UINT lssSupplyThreshold = new UINT(4);
    public BYTE lssCheckInterval = new BYTE();
    public BYTE lssSchemeType = new BYTE();
    public TIMESTAMP lssStartTime = new TIMESTAMP();
    public TIMESTAMP lssEndTime = new TIMESTAMP();
    public BYTE lssWeekly = new BYTE();
    
    @XmlTransient
    public BYTE getLssEntryNumber()
    {
        return lssEntryNumber;
    }

    public void setLssEntryNumber(BYTE lssEntryNumber)
    {
        this.lssEntryNumber = lssEntryNumber;
    }

    /*
    public UINT getLssSupply()
    {
        return lssSupply;
    }

    public void setLssSupply(UINT lssSupply)
    {
        this.lssSupply = lssSupply;
    }
    
    public UINT getLssSupplyThreshold()
    {
        return lssSupplyThreshold;
    }

    public void setLssSupplyThreshold(UINT lssSupplyThreshold)
    {
        this.lssSupplyThreshold = lssSupplyThreshold;
    }
    */

    @XmlTransient
    public BYTE getLssCheckInterval()
    {
        return lssCheckInterval;
    }

    public void setLssCheckInterval(BYTE lssCheckInterval)
    {
        this.lssCheckInterval = lssCheckInterval;
    }
    
    @XmlTransient
    public BYTE getLssSchemeType()
    {
        return lssSchemeType;
    }

    public void setLssSchemeType(BYTE lssSchemeType)
    {
        this.lssSchemeType = lssSchemeType;
    }

    @XmlTransient
    public TIMESTAMP getLssStartTime()
    {
        return lssStartTime;
    }

    public void setLssStartTime(TIMESTAMP lssStartTime)
    {
        this.lssStartTime = lssStartTime;
    }

    @XmlTransient
    public TIMESTAMP getLssEndTime()
    {
        return lssEndTime;
    }

    public void setLssEndTime(TIMESTAMP lssEndTime)
    {
        this.lssEndTime = lssEndTime;
    }

    @XmlTransient
    public BYTE getLssWeekly()
    {
        return lssWeekly;
    }

    public void setLssWeekly(BYTE lssWeekly)
    {
        this.lssWeekly = lssWeekly;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

		sb.append("CLASS["+this.getClass().getName()+"]\n");
        sb.append("lssEntryNumber: " + lssEntryNumber + "\n");
        //sb.append("lssSupply: " + lssSupply + "\n");
        //sb.append("lssSupplyThreshold: " + lssSupplyThreshold + "\n");
        sb.append("lssCheckInterval: " + lssCheckInterval + "\n");
        sb.append("lssSchemeType: " + lssSchemeType + "\n");
        sb.append("lssStartTime: " + lssStartTime + "\n");
        sb.append("lssEndTime: " + lssEndTime + "\n");
        sb.append("lssWeekly: " + lssWeekly + "\n");
        return sb.toString();
    }
}
