package com.aimir.fep.protocol.fmp.frame.service.entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.frame.service.Entry;


/**
 * 11.1 loadControlSchemeEntry
 *
 * @author J.S Park
 * @version $Rev: 1 $, $Date: 2009-03-31 15:59:15 +0900 $,
 * <pre>
 * &lt;complexType name="loadControlSchemeEntry">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}entry">
 *       &lt;sequence>
 *         &lt;element name="lcsID" type="{http://server.ws.command.fep.aimir.com/}hex" minOccurs="0"/>
 *         &lt;element name="lcsEntryNumber" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="lcsCmd" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="lcsDelayTime" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="lcsSchemeType" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="lcsStartTime" type="{http://server.ws.command.fep.aimir.com/}timestamp" minOccurs="0"/>
 *         &lt;element name="lcsEndTime" type="{http://server.ws.command.fep.aimir.com/}timestamp" minOccurs="0"/>
 *         &lt;element name="lcsWeekly" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "loadControlSchemeEntry", propOrder = {
    "lcsID",
    "lcsEntryNumber",
    "lcsCmd",
    "lcsDelayTime",
    "lcsSchemeType",
    "lcsStartTime",
    "lcsEndTime",
    "lcsWeekly"
})
public class loadControlSchemeEntry extends Entry {

    public HEX lcsID = new HEX(8); 
    public BYTE lcsEntryNumber = new BYTE();
    public BYTE lcsCmd = new BYTE();
    public BYTE lcsDelayTime = new BYTE();
    public BYTE lcsSchemeType = new BYTE();
    public TIMESTAMP lcsStartTime = new TIMESTAMP();
    public TIMESTAMP lcsEndTime = new TIMESTAMP();
    public BYTE lcsWeekly = new BYTE();

    @XmlTransient
    public HEX getLcsID()
    {
        return lcsID;
    }

    public void setLcsID(HEX lcsID)
    {
        this.lcsID = lcsID;
    }

    @XmlTransient
    public BYTE getLcsEntryNumber()
    {
        return lcsEntryNumber;
    }

    public void setLcsEntryNumber(BYTE lcsEntryNumber)
    {
        this.lcsEntryNumber = lcsEntryNumber;
    }

    @XmlTransient
    public BYTE getLcsCmd()
    {
        return lcsCmd;
    }

    public void setLcsCmd(BYTE lcsCmd)
    {
        this.lcsCmd = lcsCmd;
    }

    @XmlTransient
    public BYTE getLcsDelayTime()
    {
        return lcsDelayTime;
    }

    public void setLcsDelayTime(BYTE lcsDelayTime)
    {
        this.lcsDelayTime = lcsDelayTime;
    }

    @XmlTransient
    public BYTE getLcsSchemeType()
    {
        return lcsSchemeType;
    }

    public void setLcsSchemeType(BYTE lcsSchemeType)
    {
        this.lcsSchemeType = lcsSchemeType;
    }

    @XmlTransient
    public TIMESTAMP getLcsStartTime()
    {
        return lcsStartTime;
    }

    public void setLcsStartTime(TIMESTAMP lcsStartTime)
    {
        this.lcsStartTime = lcsStartTime;
    }

    @XmlTransient
    public TIMESTAMP getLcsEndTime()
    {
        return lcsEndTime;
    }

    public void setLcsEndTime(TIMESTAMP lcsEndTime)
    {
        this.lcsEndTime = lcsEndTime;
    }

    @XmlTransient
    public BYTE getLcsWeekly()
    {
        return lcsWeekly;
    }

    public void setLcsWeekly(BYTE lcsWeekly)
    {
        this.lcsWeekly = lcsWeekly;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

		sb.append("CLASS["+this.getClass().getName()+"]\n");
        sb.append("lcsID: " + lcsID + "\n");
        sb.append("lcsEntryNumber: " + lcsEntryNumber + "\n");
        sb.append("lcsCmd: " + lcsCmd + "\n");
        sb.append("lcsDelayTime: " + lcsDelayTime + "\n");
        sb.append("lcsSchemeType: " + lcsSchemeType + "\n");
        sb.append("lcsStartTime: " + lcsStartTime + "\n");
        sb.append("lcsEndTime: " + lcsEndTime + "\n");
        sb.append("lcsWeekly: " + lcsWeekly + "\n");

        return sb.toString();
    }
}
