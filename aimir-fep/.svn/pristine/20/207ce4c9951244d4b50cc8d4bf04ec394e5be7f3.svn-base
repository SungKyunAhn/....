package com.aimir.fep.protocol.fmp.parser.alarm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.Hex;
import com.aimir.notification.VarBinds;

public class Menix implements AlarmParser,java.io.Serializable
{
    private Log log = LogFactory.getLog(Menix.class);

    public static final int TYPE_REMOCON_EMERGENCY=0;
    public static final int TYPE_REMOCON_DISARMS=1;
    public static final int TYPE_REMOCON_ARMS=2;

    public static final int TYPE_WINDOW=4;
    public static final int TYPE_IMPACT=6;
    public static final int TYPE_EMERGENCY_SWITCH=8;
    public static final int TYPE_DOOR=9;

    public static final int TYPE_TRESPASS=10;
    public static final int TYPE_TEMPERATURE=11;
    public static final int TYPE_FIRE=12;
    public static final int TYPE_GAS=13;

    public static final int SENSOR_STATUS_NORMAL=0;
    public static final int SENSOR_STATUS_CHECK=1;
    public static final int SENSOR_STATUS_LOWBAT=2;

    private int alarmVendor = 2;    
    private String alarmMessage = null;    
    private String alarmSensorId = null;    
    private int alarmSensorType = 0;    
    private int alarmSensorStatus = 0; 
    private int alarmSensorIndex = 0;

    public Menix()
    {
    }

    public void parse(byte[] data) throws Exception
    {
        if(data.length != 5)
        {
            log.warn("Menix.parser() :: data size abnormal["
                    + data.length+"]");
            throw new Exception(
                    "Menix.parser() :: data size abnormal["
                    + data.length+"]");
        }
        this.alarmMessage = Hex.decode(data);
        this.alarmSensorId = Hex.decode(data,0,3);
        /*
        this.alarmSensorId = 
            Integer.toString(DataUtil.getIntToByte(data[0]))
            +Integer.toString(DataUtil.getIntToByte(data[1]))
            +Integer.toString(DataUtil.getIntToByte(data[2]));
        */
        this.alarmSensorType = ((data[3] & 0xF0) >> 4);
        this.alarmSensorStatus = ((data[3] & 0x0C) >> 2);
        this.alarmSensorIndex =((data[3]& 0x01) << 4) 
            + ((data[4] & 0xF0) >> 4); 
    }

    public void getVarBinds(VarBinds vbs)
    {
        vbs.put("alarmVendor",Integer.toString(alarmVendor));
        vbs.put("alarmMessage",alarmMessage);
        vbs.put("alarmSensorId",alarmSensorId);
        vbs.put("alarmSensorType",Integer.toString(alarmSensorType));
        vbs.put("alarmSensorStatus",
                Integer.toString(alarmSensorStatus));
        vbs.put("alarmSensorIndex",
                Integer.toString(alarmSensorIndex));
    }

    public int getAlarmVendor() { return this.alarmVendor; }
    public String getAlarmMessage() { return this.alarmMessage; }
    public void setAlarmMessage(String data) { this.alarmMessage = data; }

    public String getAlarmSensorId() { return this.alarmSensorId; }
    public void setAlarmSensorId(String data) { this.alarmSensorId = data; }

    public int getAlarmSensorType() { return this.alarmSensorType; }
    public void setAlarmSensorType(int data) { this.alarmSensorType = data; }
    public int getAlarmSensorStatus() { return this.alarmSensorStatus; }
    public void setAlarmSensorStatus(int data) { 
        this.alarmSensorStatus = data; }
    public int getAlarmSensorIndex() { return this.alarmSensorIndex; }
    public void setAlarmSensorIndex(int data) { this.alarmSensorIndex = data; }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Menix Alarm::{")
            .append("alarmVendor=["+alarmVendor+"],")
            .append("alarmMessage=["+alarmMessage+"],")
            .append("alarmSensorId=["+alarmSensorId+"],")
            .append("alarmSensorType=["+alarmSensorType+"],")
            .append("alarmSensorStatus=["+alarmSensorStatus+"],")
            .append("alarmSensorIndex=["+alarmSensorIndex+"]")
            .append("}\n");
        return sb.toString();
    }
}
