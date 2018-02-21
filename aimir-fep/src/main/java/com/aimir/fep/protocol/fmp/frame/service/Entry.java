package com.aimir.fep.protocol.fmp.frame.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.frame.service.entry.billingEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.boolEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.byteEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.charEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.cmdHistEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.cmdHistStruct;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiBindingEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiMemoryEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiNeighborEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.commLogEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.commLogStruct;
import com.aimir.fep.protocol.fmp.frame.service.entry.drLevelEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.endDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.ffdEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.gpioEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.idrEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadControlSchemeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadLimitPropertyEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadLimitSchemeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.loadShedSchemeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.memEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.mobileEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.pluginEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.procEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sensorInfoNewEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sysEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.timeEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.trInfoEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.varEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.varSubValueEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.varValueEntry;

/**
 * Abstract Entry
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
* <pre>
 * &lt;complexType name="entry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entry")
@XmlSeeAlso({
    billingEntry.class,
    boolEntry.class,
    byteEntry.class,
    charEntry.class,
    cmdHistEntry.class,
    cmdHistStruct.class,
    codiBindingEntry.class,
    codiDeviceEntry.class,
    codiEntry.class,
    codiMemoryEntry.class,
    codiNeighborEntry.class,
    commLogEntry.class,
    commLogStruct.class,
    drLevelEntry.class,
    endDeviceEntry.class,
    ffdEntry.class,
    gpioEntry.class,
    idrEntry.class,
    loadControlSchemeEntry.class,
    loadLimitPropertyEntry.class,
    loadLimitSchemeEntry.class,
    loadShedSchemeEntry.class,
    memEntry.class,
    mobileEntry.class,
    procEntry.class,
    pluginEntry.class,
    sensorInfoNewEntry.class,
    sysEntry.class,
    timeEntry.class,
    trInfoEntry.class,
    varEntry.class,
    varSubValueEntry.class,
    varValueEntry.class
})
public abstract class Entry implements java.io.Serializable
{
    /**
     * get MIB Name
     *
     * @return name <code>String</code> MIB Name
     */
    public final String getMIBName()
    {
        String clsName = this.getClass().getName();
        int idx = clsName.lastIndexOf(".");

        return clsName.substring(idx+1,clsName.length());
    }

    /**
     * getString
     *
     * @return result <code>String</code>
     */
    public abstract String toString();
}
