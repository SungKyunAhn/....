package com.aimir.fep.modem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * <pre>
 * &lt;complexType name="eventLog">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventDescr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eventMsg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eventStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eventType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="firmwareBuild" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firmwareVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gmtTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventLog", propOrder = {
    "eventDescr",
    "eventMsg",
    "eventStatus",
    "eventType",
    "firmwareBuild",
    "firmwareVersion",
    "gmtTime"
})
public class EventLog implements java.io.Serializable
{
	private static final long serialVersionUID = -2516960799403280334L;

	private static Log log = LogFactory.getLog(EventLog.class);
    
    private static String[] FFh = {"", "Line Missing", "Line Restore"};
    
    private static String[] XXh = {
                                   "Measuring QI",
                                   "Measuring QII",
                                   "Measuring QIII",
                                   "Measuring QIV",
                                   "L3",
                                   "L2",
                                   "L1",
                                   "Communication activity after power down"
                                   };
     private static String[] YYh = {
                                    "Reserved",
                                    "Block 1 CRC error",
                                    "Block 2 CRC error",
                                    "Block 3 CRC error",
                                    "Block 4 CRC error",
                                    "Block 5 CRC error",
                                    "Block 6 CRC error",
                                    "Block 7 CRC error"
                                    };
     private static String[] ZZh = {
                                    "Watchdog Activated",
                                    "EEPROM fault",
                                    "ADI fault",
                                    "Storage data corrupted",
                                    "Reversed phase sequence",
                                    "Import/export energy at the same time",
                                    "Reserved",
                                    "Reserved"
                                    };
     
    private String gmtTime;
    private int eventType;
    private String eventStatus;
    private String firmwareVersion;
    private String firmwareBuild;
    private String eventMsg;
    private String eventDescr;
    
    public EventLog() {}
    
    public EventLog(String firmwareVersion, String firmwareBuild) {
    	this.firmwareVersion = firmwareVersion;
    	this.firmwareBuild = firmwareBuild;
    }

    public String getEventDescr()
    {
    	if(this.eventDescr != null && !"".equals(this.eventDescr)){
    		return this.eventDescr;
    	}else{
    		return Event.getEvent(eventType, firmwareVersion, firmwareBuild).getDesc();
    	}        
    }

    public void setEventDescr(String eventDescr) {
		this.eventDescr = eventDescr;
	}

	public String getEventMsg()
    {
    	if(this.eventMsg != null && !"".equals(this.eventMsg)){
    		return this.eventMsg;
    	}else{
    		return Event.getEvent(eventType, firmwareVersion, firmwareBuild).getMsg(Hex.encode(eventStatus));
    	}        
    }

    public void setEventMsg(String eventMsg) {
		this.eventMsg = eventMsg;
	}

	public String getGmtTime()
    {
        return gmtTime;
    }

    public void setGmtTime(String gmtTime)
    {
        this.gmtTime = gmtTime;
    }

    public int getEventType()
    {
        return eventType;
    }

    public void setEventType(int eventType)
    {
        this.eventType = eventType;
    }

    public String getEventStatus()
    {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus)
    {
        this.eventStatus = eventStatus;
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
    
    public String getFirmwareBuild() {
        return firmwareBuild;
    }
    
    public void setFirmwareBuild(String firmwareBuild) {
        this.firmwareBuild = firmwareBuild;
    }
    
    public static String getMsg(int data) {
        byte[] _data = DataUtil.get4ByteToInt(data);
        DataUtil.convertEndian(_data);
        log.debug("Data[" + data + "] HEX[" + Hex.decode(_data) + "]");
        
        StringBuffer buf = new StringBuffer();
        // buf.append(FFh[DataUtil.getIntToByte(_data[0])]);
        
        // get XX msg
        for (int b = 0x01, cnt = 0; b <= 0x80; b <<= 1, cnt++) {
            if ((int)(_data[1] & b) != 0)
                buf.append("," + XXh[cnt]);
        }
        for (int b = 0x01, cnt = 0; b <= 0x80; b <<= 1, cnt++) {
            if ((int)(_data[2] & b) != 0)
                buf.append("," + YYh[cnt]);
        }
        for (int b = 0x01, cnt = 0; b <= 0x80; b <<= 1, cnt++) {
            if ((int)(_data[3] & b) != 0)
                buf.append("," + ZZh[cnt]);
        }
        
        log.debug("Msg[" + buf.toString().trim() + "]");
        return buf.toString().trim();
    }
    
    public static String getMsg(String data) {
        return getMsg(Integer.parseInt(data));
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("GMTTIME[" + gmtTime + "] EVENTTYPE[" + eventType + 
                   "] EVENTSTATUS[" + eventStatus + 
                   "] FIRMWARE_VERSION[" + firmwareVersion + 
                   "] FIRMWARE_BUILD[" + firmwareBuild + "]");
        
        return buf.toString();
    }
    
    public enum Event {
        Reason(0, "Reason", new String[]{"UNKNOWN",
                            "EXTERNAL",
                            "POWERON",
                            "WATCHDOG",
                            "BROWNOUT",
                            "JTAG",
                            "ASSERT",
                            "RSTACK",
                            "CSTACK",
                            "BOOTLOADER",
                            "PC_ROLLOVER",
                            "SOFTWARE",
                            "PROTFAULT",
                            "FLASH_VERIFY_FAIL",
                            "FLASH_WRITE_INHIBIT",
                            "BOOTLOADER_IMG_BAD",
                            "RESERVED",
                            "RESERVED",
                            "RESERVED",
                            "RESERVED",
                            "FACTORY_SETTING",
                            "REED_SW",
                            "FIXED_INTERNAL",
                            "SOFT_REMOTE",
                            "AMR_FAIL",
                            "FAIL_CLOSE_CONNECTION",
                            "DELETE_BIND_FAIL",
                            "SCAN_FAIL",
                            "NO_JOIN"}, "1.0", "1"),
        PowerOutage(1, "Power Outage", new String[] {"", "Power Fail", "Power Restore"}, "1.0", "1"),
        NetworkJoin(2, "Network Join", new String[]{"Success", "Fail"}, "1.0", "1"),
        NetworkLeave(3, "Network Leave", new String[]{"Success", "Fail"}, "1.0", "1"),
        FactorySetting(4, "Factory Setting", new String[]{}, "1.0", "1"),
        LPPeriodChange(5, "LP Period Change", new String[]{"", "60 mins", "30 mins", "", "15 mins"}, "1.0", "1"),
        TimeOffset(6, "Time Offset", new String[]{}, "1.0", "1"),
        LXMissing(7, "LX Missing", new String[]{}, "1.0", "1"),
        ScanFail(8, "Scan Fail", new String[]{}, "1.0", "1"),
        LowBattery(9, "Low Battery", new String[]{}, "1.0", "1"),
        CaseOpen(10, "Case Open", new String[]{}, "1.0", "1"),
        Tamper(11, "Tamper", new String[]{}, "1.0", "1"),
        ReservedStramCrack(12, "Reserved Sram Crack", new String[]{}, "1.0", "1"),
        KeepAliveFail(13, "Keep Alive Fail", new String[]{}, "1.0", "1"),
        Detachment(14, "Detachment", new String[]{}, "2.1", "10"),
        PulseM0(15, "Pluse M0", new String[]{}, "2.1", "10"),
        WakeUp(16, "Wake Up", new String[]{}, "2.1", "10"),
        NoParent(17, "No Parent", new String[]{}, "2.2", "1"),
        BattDischgEn(18, "Battery Discharge EN", new String[]{}, "2.2", "1"),
        NetworkType(19, "Network Type", new String[] {"Star Node", "Mesh Node"}, "2.4", "10"),
        Reserved(20, "Reserved", new String[]{}, "0", "0");
        
        private int eventType;
        private String desc;
        private String[] msg;
        private String fwVersion;
        private String buildVersion;
        
        Event(int eventType, String desc, String[] msg, String fwVersion, String buildVersion) {
            this.eventType = eventType;
            this.desc = desc;
            this.msg = msg;
            this.fwVersion = fwVersion;
            this.buildVersion = buildVersion;
        }
        
        String getDesc() {
            return desc;
        }
        
        public String[] getMsg() {
            return this.msg;
        }
        
        String getMsg(byte[] status) {
            if (eventType == LXMissing.eventType) {
                return EventLog.getMsg(DataUtil.getIntTo4Byte(status));
            }
            else if (eventType == TimeOffset.eventType) {
                int hour = DataUtil.getIntToByte(status[1]);
                int min = DataUtil.getIntToByte(status[2]);
                int sec = DataUtil.getIntToByte(status[3]);
                return (hour < 10? "0"+hour:""+hour) + ":" +
                (min < 10? "0"+min:""+min) + ":" +
                (sec < 10? "0"+sec:""+sec);
            }
            else {
                int istatus = DataUtil.getIntToByte(status[3]);
                if (msg.length > istatus && istatus > -1)
                    return msg[istatus];
                else
                    return ""+istatus;
            }
        }
        
        public static Event getEvent(int eventType, String fwVersion, String buildVersion) {
            for (int i = 0; i < Event.values().length; i++) {
                if (Event.values()[i].eventType == eventType) {
                    if (fwVersion.compareTo(Event.values()[i].fwVersion) > 0 || 
                            (fwVersion.compareTo(Event.values()[i].fwVersion) == 0 &&
                            Double.parseDouble(buildVersion) >= Double.parseDouble(Event.values()[i].buildVersion))) {
                        return Event.values()[i];
                    }
                    else {
                        return Event.Reserved;
                    }
                }
            }
            return Event.Reserved;
        }
    }
}
