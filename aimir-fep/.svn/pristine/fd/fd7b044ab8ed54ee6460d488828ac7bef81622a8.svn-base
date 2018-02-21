package com.aimir.fep.protocol.fmp.frame.service;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.util.Hex;

/**
 * Alarm Service Data Class
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class AlarmData extends ServiceData
{
    public BYTE  vendor = new BYTE();
    public HEX srcId = new HEX(8);
    public TIMESTAMP mcuTimeStamp = new TIMESTAMP(7);
    public TIMESTAMP sensorTimeStamp = new TIMESTAMP(7);
    public BYTE idx = new BYTE();
    public OCTET alarmMessage = new OCTET();
    private final Integer serviceType = new Integer(4);

    /**
     * constructor
     */
    public AlarmData()
    {
    }

    /**
     * constructor
     *
     * @param vendor <code>BYTE</code> soruce type
     * @param srcId <code>HEX</code> soruce id
     * @param mcuTimeStamp <code>TIMESTAMP</code> time stamp
     * @param alarmMessage <code>OCTET</code> alarm message
     */
    public AlarmData(BYTE vendor, HEX srcId, TIMESTAMP mcuTimeStamp,
            OCTET alarmMessage)
    {
        this.vendor = vendor;
        this.srcId = srcId;
        this.mcuTimeStamp = mcuTimeStamp;
        this.alarmMessage = alarmMessage;
    }

    /**
     * get srouce type
     *
     * @return vendor <code>BYTE</code> source type
     */
    public BYTE getVendor()
    {
        return this.vendor;
    } 
    /**
     * set srouce type
     *
     * @param vendor <code>BYTE</code> source type
     */
    public void setVendor(BYTE vendor)
    {
        this.vendor = vendor;
    }
    
    /**
     * get srouce id
     *
     * @return srcId <code>HEX</code> source id
     */
    public HEX getSrcId()
    {
        return this.srcId;
    }
    /**
     * set srouce id
     *
     * @param srcId <code>HEX</code> source id
     */
    public void setSrcId(HEX srcId)
    {
        this.srcId = srcId;
    }

    /**
     * get time stamp 
     *
     * @return mcuTimeStamp <code>TIMESTAMP</code> time stamp
     */
    public TIMESTAMP getMcuTimeStamp()
    {
        return this.mcuTimeStamp;
    }
    /**
     * set time stamp 
     *
     * @param mcuTimeStamp <code>TIMESTAMP</code> time stamp
     */
    public void setMcuTimeStamp(TIMESTAMP mcuTimeStamp)
    {
        this.mcuTimeStamp = mcuTimeStamp;
    }

    /**
     * get time stamp 
     *
     * @return sensorTimeStamp <code>TIMESTAMP</code> time stamp
     */
    public TIMESTAMP getSensorTimeStamp()
    {
        return this.sensorTimeStamp;
    }

    /**
     * set time stamp 
     *
     * @param sensorTimeStamp <code>TIMESTAMP</code> time stamp
     */
    public void setSensorTimeStamp(TIMESTAMP sensorTimeStamp)
    {
        this.sensorTimeStamp = sensorTimeStamp;
    }

    /**
     * get idx
     *
     * @return idx <code>BYTE</code> idx
     */
    public BYTE getIdx()
    {
        return this.idx;
    } 
    /**
     * set idx
     *
     * @param idx <code>BYTE</code> idx
     */
    public void setIdx(BYTE idx)
    {
        this.idx = idx;
    }

    /**
     * get alarm message 
     *
     * @return alarmMessage <code>OCTET</code> alarm message
     */
    public OCTET getAlarmMessage()
    {
        return this.alarmMessage;
    }

    /**
     * set alarm message 
     *
     * @param alarmMessage <code>OCTET</code> alarm message
     */
    public void setAlarmMessage(OCTET alarmMessage)
    {
        this.alarmMessage = alarmMessage;
    }

    public Integer getServiceType() { return serviceType; }

    /**
     * get String
     *
     * @param result <code>String</code>
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Alarm Service Header\n");
        sb.append("mcuId=").append(getMcuId()).append(',');
        sb.append("vendor=").append(vendor).append(',');
        sb.append("srcId=").append(srcId).append(',');
        sb.append("sensorTimeStamp=")
            .append(sensorTimeStamp).append(',');
        sb.append("mcuTimeStamp=").append(mcuTimeStamp).append(',');
        sb.append("idx=").append(idx).append('\n');
        sb.append("Alarm Service Data\n");
        sb.append("alarmMessage : " + Hex.decode(
                    alarmMessage.getValue()) + "\n");

        return sb.toString();
    }
}
