package com.aimir.fep.modem;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class ModemCommandData
{
    public static final byte CMD_TYPE_VALIDATE_EEPROM_IMAGE = 0x00;
    public static final byte CMD_TYPE_BOOTLOADER_INSTALL_NEW_IMAGE = 0x01;
    public static final byte CMD_TYPE_RESET = 0x02;
    public static final byte CMD_TYPE_TIME = 0x03;
    public static final byte CMD_TYPE_CURRENT_PULSE = 0x04;
    public static final byte CMD_TYPE_LAST_PULSE = 0x05;
    public static final byte CMD_TYPE_ALARM_FLAG = 0x06;
    public static final byte CMD_TYPE_LP_PERIOD = 0x07;
    public static final byte CMD_TYPE_LP_CHOICE = 0x08;
    public static final byte CMD_TYPE_OPERATING_DAY = 0x09;
    public static final byte CMD_TYPE_ACTIVE_MIN = 0x0A;
    public static final byte CMD_TYPE_METERING_DAY = 0x0B;
    public static final byte CMD_TYPE_METERING_HOUR = 0x0C;
    public static final byte CMD_TYPE_HEARTBEAT_DAY = (byte)0x83;
    public static final byte CMD_TYPE_HEARTBEAT_HOUR = (byte)0x84;
    public static final byte CMD_TYPE_NONE = (byte)0x99;

    private byte cmdType = (byte)0x99;
    private byte[] validateEEPROMImage = null;
    private byte[] bootloaderInstallNewImage = null;
    private byte[] reset = null;
    private byte[] time = null; 
    private byte[] timeZone = null;
    private byte[] dst = null;
    private byte[] currentPulse = null;
    private byte[] lastPulse = null;
    private byte[] alarmFlag = null;
    private byte[] lpPeriod = null;
    private byte[] lpChoice = null;
    private byte[] operatingDay = null;
    private byte[] activeMin = null;
    private byte[] meteringDay = null;
    private byte[] meteringHour = null;
    private byte[] heartbeatDay = null;
    private byte[] heartbeatHour =  null;

    public String toString()
    {
        return "SensorCommandData{\n    cmdType : "
               + Integer.toHexString(cmdType ) + "\n    data : "
               + Hex.decode(getData()) + "\n}\n";
    }
    public byte getCmdType()
    {
        return cmdType;
    }
    public void setCmdType(byte cmdType)
    {
        this.cmdType = cmdType;
    }
    public byte[] getData()
    {
        switch(cmdType)
        {
            case CMD_TYPE_VALIDATE_EEPROM_IMAGE:
                return validateEEPROMImage;
            case CMD_TYPE_BOOTLOADER_INSTALL_NEW_IMAGE:
                return bootloaderInstallNewImage;
            case CMD_TYPE_RESET:
                return reset;
            case CMD_TYPE_TIME:
                return time;
            case CMD_TYPE_CURRENT_PULSE:
                return currentPulse;
            case CMD_TYPE_LAST_PULSE:
                return lastPulse;
            case CMD_TYPE_ALARM_FLAG:
                return alarmFlag;
            case CMD_TYPE_LP_PERIOD:
                return lpPeriod;
            case CMD_TYPE_LP_CHOICE:
                return lpChoice;
            case CMD_TYPE_OPERATING_DAY:
                return operatingDay;
            case CMD_TYPE_ACTIVE_MIN:
                return activeMin;
            case CMD_TYPE_METERING_DAY:
                return meteringDay;
            case CMD_TYPE_METERING_HOUR:
                return meteringHour;
            case CMD_TYPE_HEARTBEAT_DAY:
                return heartbeatDay;
            case CMD_TYPE_HEARTBEAT_HOUR:
                return heartbeatHour;
        }
        
        return null;
    }
    
    public void setData(byte[] data)
    {
        switch(cmdType)
        {
            case CMD_TYPE_VALIDATE_EEPROM_IMAGE:
                this.validateEEPROMImage = data;
            case CMD_TYPE_BOOTLOADER_INSTALL_NEW_IMAGE:
                this.bootloaderInstallNewImage = data;
            case CMD_TYPE_RESET:
                this.reset = data;
            case CMD_TYPE_TIME:
                this.time = data;
            case CMD_TYPE_CURRENT_PULSE:
                this.currentPulse = data;
            case CMD_TYPE_LAST_PULSE:
                this.lastPulse = data;
            case CMD_TYPE_ALARM_FLAG:
                this.alarmFlag = data;
            case CMD_TYPE_LP_PERIOD:
                this.lpPeriod = data;
            case CMD_TYPE_LP_CHOICE:
                this.lpChoice = data;
            case CMD_TYPE_OPERATING_DAY:
                this.operatingDay = data;
            case CMD_TYPE_ACTIVE_MIN:
                this.activeMin = data;
            case CMD_TYPE_METERING_DAY:
                this.meteringDay = data;
            case CMD_TYPE_METERING_HOUR:
                this.meteringHour = data;
            case CMD_TYPE_HEARTBEAT_DAY:
                this.heartbeatDay = data;
            case CMD_TYPE_HEARTBEAT_HOUR:
                this.heartbeatHour = data;
        }
    }
    
    public enum CMD {
        ValidateEEPROMImage((byte)0x00, true, false, "Validate EEPROM Image"),
        BootloaderInstallNewImage((byte)0x01, true, false, "Bootloader Install New Image"),
        Reset((byte)0x02, true, false, "Reset"),
        Time((byte)0x03, true, true, "Time"),
        CurrentPulse((byte)0x04, true, true, "Current Pulse"),
        LastPulse((byte)0x05, true, false, "Last Pulse"),
        AlarmFlag((byte)0x06, true, true, "Alarm Flag"),
        LPPeriod((byte)0x07, true, true, "LP Period"),
        LPChoice((byte)0x08, true, true, "LP Choice"),
        OperatingDay((byte)0x09, true, true, "Operating Day"),
        ActiveMin((byte)0x0A, true, true, "Active Min"),
        MeteringDay((byte)0x0B, true, true, "Metering Day"),
        MeteringHour((byte)0x0C, true, true, "Metering Hour"),
        ActiveKeepTime((byte)0x0D, true, true, "Active Keep Time"),
        RFPowerSetting((byte)0x0E, true, true, "RF Power Setting"),
        PermitMode((byte)0x0F, true, true, "Permit Mode"),
        PermitState((byte)0x10, true, true, "Permit State"),
        AlarmMask((byte)0x11, true, true, "Alarm Mask"),
        MeteringFailCnt((byte)0x12, true, true, "Metering Fail Cnt"),
        NetworkType((byte)0x13, true, true, "Network Type"),
        StartSiren((byte)0x80, true, false, "Start Siren"),
        StatusRequest((byte)0x81, false, true, "Status request"),
        TemperatureAlarmLevel((byte)0x82, true, true, "Temperature Alarm Level"),
        HeartBeatDay((byte)0x83, true, true, "Heart Beat Day"),
        HeartBeatHour((byte)0x84, true, true, "Heart Beat Hour");
        
        private byte type = 0x00;
        private String descr = null;
        private boolean readable = false;
        private boolean writable = false;
        
        CMD(byte type, boolean writable, boolean readable, String descr) {
            this.type = type;
            this.writable = writable;
            this.readable = readable;
            this.descr = descr;
        }

        public byte getType() {
            return type;
        }

        public void setType(byte type) {
            this.type = type;
        }

        public String getDescr() {
            return descr;
        }

        public void setDescr(String descr) {
            this.descr = descr;
        }

        public boolean isReadable() {
            return readable;
        }

        public void setReadable(boolean readable) {
            this.readable = readable;
        }

        public boolean isWritable() {
            return writable;
        }

        public void setWritable(boolean writable) {
            this.writable = writable;
        }
    }
    
    public static RFPower getRFPower(byte[] value) {
        for (RFPower rf : RFPower.values()) {
            if (rf.getValue() == value[0]) {
                return rf;
            }
        }
        
        RFPower rf = RFPower.Reserved;
        rf.setValue(value[0]);
        return rf;
    }
    
    public enum RFPower {
        PA10dBm((byte)0x00, "PA set to 10dBm"),
        PA20dBM((byte)0x01, "PA set to 20dBM"),
        Reserved((byte)0x02, "Reserved");
        
        private byte value = 0x00;
        private String descr = null;
        
        RFPower(byte value, String descr) {
            this.value = value;
            this.descr = descr;
        }

        public byte getValue() {
            return value;
        }

        public void setValue(byte value) {
            this.value = value;
        }

        public String getDescr() {
            return descr;
        }

        public void setDescr(String descr) {
            this.descr = descr;
        }
    }
    
    public static StartSiren getStartSiren(byte[] value) {
        for (StartSiren ss : StartSiren.values()) {
            if (ss.getValue() == value[0]) {
                return ss;
            }
        }
        
        StartSiren ss = StartSiren.Reserved;
        ss.setValue(value[0]);
        return ss;
    }
    
    public enum StartSiren {
        OFF((byte)0x00, "Siren is turned off"),
        ON((byte)0x01, "Siren is turned on"),
        Reserved((byte)0x02, "Reserved");
        
        private byte value = 0x00;
        private String descr = null;
        
        StartSiren(byte value, String descr) {
            this.value = value;
            this.descr = descr;
        }

        public byte getValue() {
            return value;
        }

        public void setValue(byte value) {
            this.value = value;
        }

        public String getDescr() {
            return descr;
        }

        public void setDescr(String descr) {
            this.descr = descr;
        }
    }
    
    public static String getTemperatureAlarmLevel(byte[] value) {
    	// temperature
        byte minusBit = 0x10;
        boolean minus = false;
        if ((minusBit & value[0]) != 0) {
            minus = true;
            value[0] = (byte)~value[0];
            value[1] = (byte)(~value[1] + 0x01);
        }
        double temperature = Double.valueOf(DataUtil.getIntTo2Byte(value))/10;
        return ((minus? "-":"") + temperature);
    }
    
    public enum AlarmMask {
        /*
        PulseCut(new byte[]{(byte)0x00, (byte)0x80}, "Pulse Cut"),
        LowBattery(new byte[]{(byte)0x00, (byte)0x40}, "Low Battery"),
        MRDetachement(new byte[]{(byte)0x00, (byte)0x20}, "MR Detachment"),
        MRTamper(new byte[]{(byte)0x00, (byte)0x10}, "MR Tamper"),
        CaseOpen(new byte[]{(byte)0x00, (byte)0x08}, "Case Open"),
        ;
        */
        Attachment(new byte[]{(byte)0x08, (byte)0x00}, "Attachment"),
        BackPulse(new byte[]{(byte)0x04, (byte)0x00}, "Back Pulse"),
        PulseCut(new byte[]{(byte)0x01, (byte)0x00}, "Pulse Cut"),
        LowBattery(new byte[]{(byte)0x00, (byte)0x80}, "Low Battery"),
        MRDetachment(new byte[]{(byte)0x00, (byte)0x40}, "MR Detachment"),
        MRTamper(new byte[]{(byte)0x00, (byte)0x20}, "MR Tamper"),
        CaseOpen(new byte[]{(byte)0x00, (byte)0x10}, "Case Open");
        
        private byte[] mask ;
        private String descr;
        
        AlarmMask(byte[] mask, String descr) {
            this.mask = mask;
            this.descr = descr;
        }

        public byte[] getMask() {
            return mask;
        }

        public void setMask(byte[] mask) {
            this.mask = mask;
        }

        public String getDescr() {
            return descr;
        }

        public void setDescr(String descr) {
            this.descr = descr;
        }
    }
    
    public static AlarmMask[] getAlarmMask(byte[] mask) {
        List<AlarmMask> list = new ArrayList<AlarmMask>();
        for (AlarmMask am : AlarmMask.values()) {
            if ((am.getMask()[0] & mask[0]) != 0 || (am.getMask()[1] & mask[1]) != 0) {
                list.add(am);
            }
        }
        return (AlarmMask[])list.toArray(new AlarmMask[0]);
    }
    
    public static Event getEvent(byte eventId) {
        for (Event event : Event.values()) {
            if (event.getId() == eventId)
                return event;
        }
        return Event.Unknown;
    }
    
    public enum Event {
        Power((byte)0x00, "Power Outage/Recovery", new byte[]{(byte)0x00, (byte)0x01}),
        LowBattery((byte)0x01, "Low Battery", new byte[]{(byte)0x00, (byte)0x02}),
        Reserved((byte)0x02, "Reserved", new byte[]{(byte)0x00, (byte)0x04}),
        LineMissing((byte)0x03, "Line Missing(Phase missing)", new byte[]{(byte)0x00, (byte)0x08}),
        TiltAlarm((byte)0x04, "Tilt Alarm", new byte[]{(byte)0x00, (byte)0x10}),
        CaseOpen((byte)0x05, "Case Open", new byte[]{(byte)0x00, (byte)0x20}),
        MagneticTamper((byte)0x06, "Magnetic Tamper", new byte[]{(byte)0x00, (byte)0x40}),
        MagneticDetachment((byte)0x07, "Magnetic Detachment/Attachment", new byte[]{(byte)0x00, (byte)0x10}),
        PulseCut((byte)0x08, "Pulse Cut", new byte[]{(byte)0x01, (byte)0x00}),
        BatteryDischargingEnable((byte)0x09, "Battery Discharging Enable", new byte[]{(byte)0x02, (byte)0x00}),
        BackPulseDetected((byte)0x0A, "Back Pulse Detected", new byte[]{(byte)0x00, (byte)0x00}),
        HeartBeat((byte)0x80, "Heart Beat", new byte[]{(byte)0xFF, (byte)0xFF}),
        FireAlarmSmokeDetected((byte)0x81,"Fire Alarm Smoke Detected", new byte[]{(byte)0x04, (byte)0x00}),
        FireAlarmTemperatureHigh((byte)0x82, "Fire Alarm Temperature High", new byte[]{(byte)0x08, (byte)0x00}),
        SystemAlarmSmokeDetector((byte)0x83, "System Alarm Smoke Detector", new byte[]{(byte)0x10, (byte)0x00}),
        SystemAlarmTemperatureSensor((byte)0x84, "System Alarm Temperature Sensor", new byte[]{(byte)0x20, (byte)0x00}),
        SilenceAlarm((byte)0x85, "Silence Alarm", new byte[]{(byte)0x40, (byte)0x00}),
        SmokeDetectorTest((byte)0x86, "Smoke Detector Test", new byte[]{(byte)0x00, (byte)0x04}),
        ConnectionFailure((byte) 0xFC, "Connection Failure", null),
        ConnectionRestored((byte) 0xFD, "Connection Restored", null),
        Unknown((byte)0xFE, "Unknown", new byte[] {(byte)0xFF, (byte)0xFF});
        
        private byte id = 0x00;
        private String descr = null;
        private byte[] status = new byte[2];
        
        Event(byte id, String descr, byte[] status) {
            this.id = id;
            this.descr = descr;
            this.status = status;
        }

        public byte getId() {
            return id;
        }

        public void setId(byte id) {
            this.id = id;
        }

        public String getDescr() {
            return descr;
        }

        public void setDescr(String descr) {
            this.descr = descr;
        }

        public byte[] getStatus() {
            return status;
        }

        public void setStatus(byte[] status) {
            this.status = status;
        }
        
    }
    
    /**
     * eventFrame is 16 size.
     * 11bytes : time, 1byte:event id, 4bytes:event status
     * @param eventFrame
     * @return
     */
    public static EventMessage getEventMessage(byte[] eventFrame) {
        EventMessage em = new EventMessage();
        
        IoBuffer bb = IoBuffer.allocate(16);
        bb.put(eventFrame);

        int offset = 0;
        
        byte[] timeZone = new byte[2];
        bb.get(timeZone, offset, timeZone.length);
        offset += timeZone.length;
        
        byte[] dst = new byte[2];
        bb.get(dst, offset, dst.length);
        offset += dst.length;
        
        byte[] year = new byte[2];
        bb.get(year, offset, year.length);
        offset += year.length;
        
        byte[] month = new byte[1];
        bb.get(month, offset, month.length);
        offset += month.length;
        
        byte[] day = new byte[1];
        bb.get(day, offset, day.length);
        offset += day.length;
        
        byte[] hour = new byte[1];
        bb.get(hour, offset, hour.length);
        offset += hour.length;
        
        byte[] minute = new byte[1];
        bb.get(minute, offset, minute.length);
        offset += minute.length;
        
        byte[] second = new byte[1];
        bb.get(second, offset, second.length);
        offset += second.length;
        
        String eventTime = ""+DataUtil.getIntTo2Byte(year) +  
                           (DataUtil.getIntToBytes(month) < 10? "0":"") +
                           DataUtil.getIntToBytes(month) +
                           (DataUtil.getIntToBytes(day) < 10? "0":"") +
                           DataUtil.getIntToBytes(day) +
                           (DataUtil.getIntToBytes(hour) < 10? "0":"") +
                           DataUtil.getIntToBytes(hour) +
                           (DataUtil.getIntToBytes(minute) < 10? "0":"") +
                           DataUtil.getIntToBytes(minute) +
                           (DataUtil.getIntToBytes(second) < 10? "0":"") +
                           DataUtil.getIntToBytes(second);
        em.setEventTime(eventTime);
        
        byte[] eventId = new byte[1];
        bb.get(eventId, offset, eventId.length);
        offset += eventId.length;
        
        byte[] status = new byte[4];
        bb.get(status, offset, status.length);
        offset += status.length;
        
        for(Event event : Event.values()) {
            if (eventId[0] == event.getId()) {
                em.setEvent(event);
                if ((event.getStatus()[0] & status[0]) != 0 || 
                        (event.getStatus()[1] & status[1]) != 0) {
                    // on
                    em.setEventStatus(true);
                }
                break;
            }
        }
        
        em.setTemperature(getTemperatureAlarmLevel(new byte[]{status[3], status[4]}));
        
        return em;
    }
    
    public static boolean isEventStatus(byte id, byte[] status) {
        for(Event event : Event.values()) {
            if (id == event.getId()) {
                if ((event.getStatus()[0] & status[0]) != 0 || 
                        (event.getStatus()[1] & status[1]) != 0) {
                    // on
                    return true;
                }
            }
        }
        return false;
    }
    
    public static byte[] makeEventStatus(byte eventId, boolean eventStatus, double temp) {
        byte[] status = {0x00, 0x00, 0x00, 0x00};
        Event event = getEvent(eventId);
        if (eventStatus) {
            status[0] = event.getStatus()[0];
            status[1] = event.getStatus()[1];
        }
        
        boolean minus = false;
        byte minusBit = 0x00;
        if (temp < 0) {
            minusBit = 0x10;
            minus = true;
        }
        
        double _temp = temp * 10;
        byte[] btemp = DataUtil.get2ByteToInt((int)_temp);
        if (minus) {
            btemp[0] = (byte)(minusBit | ~btemp[0]);
            btemp[1] = (byte)(~btemp[1] + 0x01);
        }
        
        status[2] = btemp[0];
        status[3] = btemp[1];
        
        return status;
    }
}

