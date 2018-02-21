package com.aimir.fep.meter.parser;

import com.aimir.fep.util.DataUtil;

/**
 * parsing Status Data included in NAM Protocol ZEUPLS Meter Data
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-06-12 15:59:15 +0900 $,
 */
public class ZEUPLS_Status implements java.io.Serializable 
{
	private static final long serialVersionUID = 1889401047647386321L;
	public byte cost = (byte)0;
    public byte lqi = (byte)0;
    public byte[] batteryVolt =  new byte[2];
    public byte[] operatingDay = new byte[2];
    public byte[] activeTime = new byte[2];
    public byte[] resetCount = new byte[2];
    public byte resetReason = (byte)0;
    public byte neighborNode = (byte)0;

    public ZEUPLS_Status()
    {
    }

    public void parse(byte[] statusData) throws Exception
    {
        this.cost = statusData[0];
        this.lqi = statusData[1];
        System.arraycopy(statusData,2,this.batteryVolt,0,2);
        System.arraycopy(statusData,4,this.operatingDay,0,2);
        System.arraycopy(statusData,6,this.activeTime,0,2);
        System.arraycopy(statusData,8,this.resetCount,0,2);
        this.resetReason = statusData[10];
        this.neighborNode = statusData[11];
    }

    public double getCost()
    {
        return DataUtil.getIntToByte(this.cost);

    }

    public int getLqi()
    {
        double dval = 0.0;
        int ival = DataUtil.getIntToByte(this.lqi);
        if(ival <= 80)
            dval =  ival * 1.0;
        else
            dval = 80.0 + (ival - 80) * 0.125;

        return (new Double(dval)).intValue();
    }
    public double getBatteryVolt()
    {
        return DataUtil.getIntTo2Byte(this.batteryVolt) / 100.0;
    }
    public int getOperatingDay()
    {
        return DataUtil.getIntTo2Byte(this.operatingDay);
    }
    public int getActiveTime()
    {
        return DataUtil.getIntTo2Byte(this.activeTime);
    }
    public int getResetCount()
    {
        return DataUtil.getIntTo2Byte(this.resetCount);
    }
    public int getResetReason()
    {
        return DataUtil.getIntToByte(this.resetReason);
    }
    public int getNeighborNode()
    {
        return DataUtil.getIntToByte(this.neighborNode);
    }
    public String toString()
    {
        StringBuffer info = new StringBuffer();

        info.append("cost = " + getCost() +" \n" );
        info.append("lqi = " + getLqi() +" \n" );
        info.append("batteryVolt = " + getBatteryVolt() +" \n" );
        info.append("operatingDay = " + getOperatingDay() +" \n" );
        info.append("activeTime = " + getActiveTime() +" \n" );
        info.append("resetCount = " + getResetCount() +" \n" );
        info.append("resetReason = " + getResetReason() +" \n" );
        info.append("neighborNode = " + getNeighborNode() +" \n" );

        return info.toString();
    }

}
