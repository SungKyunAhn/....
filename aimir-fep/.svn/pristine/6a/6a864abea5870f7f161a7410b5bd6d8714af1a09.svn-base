package com.aimir.fep.protocol.fmp.frame.service.entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.frame.service.Entry;


/**
 * 11.1 loadControlSchemeEntry
 * @author J.S Park
 * @version $Rev: 1 $, $Date: 2009-03-31 15:59:15 +0900 $,
 * <pre>
 * &lt;complexType name="loadLimitPropertyEntry">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}entry">
 *       &lt;sequence>
 *         &lt;element name="llpID" type="{http://server.ws.command.fep.aimir.com/}hex" minOccurs="0"/>
 *         &lt;element name="llpLimitType" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="llpLimit" type="{http://server.ws.command.fep.aimir.com/}uint" minOccurs="0"/>
 *         &lt;element name="llpIntervalNumber" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="llpRestorationTime" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "loadLimitPropertyEntry", propOrder = {
    "llpID",
    "llpLimitType",
    "llpLimit",
    "llpIntervalNumber",
    "llpRestorationTime"
})
public class loadLimitPropertyEntry extends Entry {

    public HEX llpID = new HEX(8); 
    public BYTE llpLimitType = new BYTE();
    public UINT llpLimit = new UINT(4);
    public BYTE llpIntervalNumber = new BYTE();
    public BYTE llpRestorationTime = new BYTE();

    @XmlTransient
    public HEX getLlpID()
    {
        return llpID;
    }

    public void setLlpID(HEX llpID)
    {
        this.llpID = llpID;
    }

    @XmlTransient
    public BYTE getLlpLimitType()
    {
        return llpLimitType;
    }

    public void setLlpLimitType(BYTE llpLimitType)
    {
        this.llpLimitType = llpLimitType;
    }

    @XmlTransient
    public UINT getLlpLimit()
    {
        return llpLimit;
    }

    public void setLlpLimit(UINT llpLimit)
    {
        this.llpLimit = llpLimit;
    }

    @XmlTransient
    public BYTE getLlpIntervalNumber()
    {
        return llpIntervalNumber;
    }

    public void setLlpIntervalNumber(BYTE llpIntervalNumber)
    {
        this.llpIntervalNumber = llpIntervalNumber;
    }

    @XmlTransient
    public BYTE getLlpRestorationTime()
    {
        return llpRestorationTime;
    }

    public void setLlpRestorationTime(BYTE llpRestorationTime)
    {
        this.llpRestorationTime = llpRestorationTime;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

		sb.append("CLASS["+this.getClass().getName()+"]\n");
        sb.append("llpID: " + llpID + "\n");
        sb.append("llpLimitType: " + llpLimitType + "\n");
        sb.append("llpLimit: " + llpLimit + "\n");
        sb.append("llpIntervalNumber: " + llpIntervalNumber + "\n");
        sb.append("llpRestorationTime: " + llpRestorationTime + "\n");

        return sb.toString();
    }
}
