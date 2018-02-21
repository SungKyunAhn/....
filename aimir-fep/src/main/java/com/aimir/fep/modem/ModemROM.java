package com.aimir.fep.modem;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.TimeUtil;

/**
 * <pre>
 * &lt;complexType name="modemROM">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="amrData" type="{http://server.ws.command.fep.aimir.com/}amrData" minOccurs="0"/>
 *         &lt;element name="batteryLog" type="{http://server.ws.command.fep.aimir.com/}batteryLog" minOccurs="0"/>
 *         &lt;element name="eventLog" type="{http://server.ws.command.fep.aimir.com/}eventLog" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lpData" type="{http://server.ws.command.fep.aimir.com/}lpData" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lpPeriod" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="modemNetwork" type="{http://server.ws.command.fep.aimir.com/}modemNetwork" minOccurs="0"/>
 *         &lt;element name="modemNode" type="{http://server.ws.command.fep.aimir.com/}modemNode" minOccurs="0"/>
 *         &lt;element name="networkType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pointer" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modemROM", propOrder = {
   "amrData",
   "batteryLog",
   "eventLog",
   "lpData",
   "lpPeriod",
   "modemNetwork",
   "modemNode",
   "networkType",
   "pointer",
   "fwVersion",
   "fwBuild",
   "data"
})
public class ModemROM implements java.io.Serializable
{
	private static final long serialVersionUID = -902777714466388888L;

	private static Log log = LogFactory.getLog(ModemROM.class);

    // Network Information (Rewrite)
    public static int OFFSET_MANUAL_ENABLE = 0x0002;
    public static int OFFSET_CHANNEL = 0x0003;
    public static int OFFSET_PANID = 0x0004;
    public static int OFFSET_TXPOWER = 0x0006;
    public static int OFFSET_SECURITY_ENABLE = 0x0007;
    public static int OFFSET_LINK_KEY = 0x0008;
    public static int OFFSET_NETWORK_KEY = 0x0018;
    public static int OFFSET_EXT_PANID = 0x0028;

    @XmlTransient
    protected byte[] BUFFER_MANUAL_ENABLE = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_CHANNEL = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_PANID = new byte[2];
    @XmlTransient
    protected byte[] BUFFER_TXPOWER = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_SECURITY_ENABLE = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_LINK_KEY = new byte[16];
    @XmlTransient
    protected byte[] BUFFER_NETWORK_KEY = new byte[16];
    @XmlTransient
    protected byte[] BUFFER_EXT_PANID = new byte[8];

    // Node Information (Read Only)
    public static int OFFSET_NODEKIND = 0x0100;
    public static int OFFSET_FIRMWAREVERSION = 0x011E;
    public static int OFFSET_FIRMWAREBUILD = 0x011F;
    public static int OFFSET_SOFTWAREVERSION = 0x0120;
    public static int OFFSET_HARDWAREVERSION = 0x0121;
    public static int OFFSET_PROTOCOLVERSION = 0x0122;
    public static int OFFSET_ZDZDINTERFACEVERSION = 0x0123;
    public static int OFFSET_RESETCOUNT = 0x0124;
    public static int OFFSET_RESETREASON = 0x0126;
    public static int OFFSET_SP_NETWORK = 0x0127;
    public static int OFFSET_SOLAR_AD_VOLT = 0x0128;
    public static int OFFSET_SOLAR_CHG_BATT_VOLT = 0x012A;
    public static int OFFSET_SOLAR_B_DC_VOLT = 0x012C;

    @XmlTransient
    protected byte[] BUFFER_NODEKIND = new byte[30];
    @XmlTransient
    protected byte[] BUFFER_FIRMWAREVERSION = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_FIRMWAREBUILD = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_SOFTWAREVERSION = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_HARDWAREVERSION = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_PROTOCOLVERSION = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_ZDZDINTERFACEVERSION = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_RESETCOUNT = new byte[2];
    @XmlTransient
    protected byte[] BUFFER_RESETREASON = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_SP_NETWORK = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_SOLAR_AD_VOLT = new byte[2];
    @XmlTransient
    protected byte[] BUFFER_SOLAR_CHG_BATT_VOLT = new byte[2];
    @XmlTransient
    protected byte[] BUFFER_SOLAR_B_DC_VOLT = new byte[2];

    // AMR Data (Rewrite)
    public static int OFFSET_METER_SERIAL_NUMBER = 0x0180;
    public static int OFFSET_CONSUMPTION_LOCATION = 0x0194;
    public static int OFFSET_VENDOR = 0x01B2;
    public static int OFFSET_CUSTOMER_NAME = 0x01C6;
    public static int OFFSET_FIXED_RESET = 0x01E4;
    public static int OFFSET_TEST_FLAG = 0x01E5;
    public static int OFFSET_METERING_DAY = 0x01E6;
    public static int OFFSET_METERING_HOUR = 0x01EA;
    public static int OFFSET_REPEATING_DAY = 0x01F6;
    public static int OFFSET_REPEATING_HOUR = 0x01FA;
    public static int OFFSET_REPEATING_SETUP_SEC = 0x0206;
    public static int OFFSET_NAZC_NUMBER = 0x0208;
    public static int OFFSET_LP_CHOICE = 0x020C;
    public static int OFFSET_ALARM_FLAG = 0x020D;
    public static int OFFSET_NO_JOIN_CNT = 0x020E;
    public static int OFFSET_PERMIT_MODE = 0x020F;
    public static int OFFSET_PERMIT_STATE = 0x0210;
    public static int OFFSET_ALARM_MASK = 0x0212;
    public static int OFFSET_NETWORK_TYPE = 0x0214;

    @XmlTransient
    protected byte[] BUFFER_METER_SERIAL_NUMBER = new byte[20];
    @XmlTransient
    protected byte[] BUFFER_CONSUMPTION_LOCATION = new byte[30];
    @XmlTransient
    protected byte[] BUFFER_VENDOR = new byte[20];
    @XmlTransient
    protected byte[] BUFFER_CUSTOMER_NAME = new byte[30];
    @XmlTransient
    protected byte[] BUFFER_FIXED_RESET = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_TEST_FLAG = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_METERING_DAY = new byte[4];
    @XmlTransient
    protected byte[] BUFFER_METERING_HOUR = new byte[12];
    @XmlTransient
    protected byte[] BUFFER_REPEATING_DAY = new byte[4];
    @XmlTransient
    protected byte[] BUFFER_REPEATING_HOUR = new byte[12];
    @XmlTransient
    protected byte[] BUFFER_REPEATING_SETUP_SEC = new byte[2];
    @XmlTransient
    protected byte[] BUFFER_NAZC_NUMBER = new byte[4];
    @XmlTransient
    protected byte[] BUFFER_LP_CHOICE = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_ALARM_FLAG = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_NO_JOIN_CNT = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_PERMIT_MODE = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_PERMIT_STATE = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_ALARM_MASK = new byte[2];
    @XmlTransient
    protected byte[] BUFFER_NETWORK_TYPE = new byte[1];

    // Battery Log (Read Only)
    public static int OFFSET_BATTERY_POINTER = 0x0500;
    public static int OFFSET_BATTERY_LOGDATA = 0x0501;

    @XmlTransient
    protected byte[] BUFFER_BATTERY_POINTER = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_BATTERY_VOLT = new byte[2];
    @XmlTransient
    protected byte[] BUFFER_CONSUMPTION_CURRENT = new byte[2];
    @XmlTransient
    protected byte[] BUFFER_BATTERY_OFFSET = new byte[1];

    // Event Log (Read Only)
    public static int OFFSET_EVENT_POINTER = 0x1000;
    public static int OFFSET_EVENT_LOGDATA = 0x1001;

    @XmlTransient
    protected byte[] BUFFER_EVENT_POINTER = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_EVENT_GMT_TIME = new byte[11];
    @XmlTransient
    protected byte[] BUFFER_EVENT_TYPE = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_EVENT_STATUS = new byte[4];

    // LP Log (Read Only)
    public static int OFFSET_METER_LPPERIOD = 0x2000;
    public static int OFFSET_METER_LPPOINTER = 0x2001;
    public static int OFFSET_METER_LPLOGDATA = 0x2002;

    @XmlTransient
    protected byte[] BUFFER_METER_LPPERIOD = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_METER_LPPOINTER = new byte[1];
    @XmlTransient
    protected byte[] BUFFER_METER_GMT = new byte[4];
    @XmlTransient
    protected byte[] BUFFER_METER_BASE_PULSE = new byte[4];
    @XmlTransient
    protected byte[] BUFFER_METER_LP = new byte[2]; // 48 ~ 192 bytes

    @XmlTransient
    private byte[] TIMEZONE = new byte[2];
    @XmlTransient
    private byte[] DST = new byte[2];
    @XmlTransient
    private byte[] YEAR = new byte[2];
    @XmlTransient
    private byte[] MONTH = new byte[1];
    @XmlTransient
    private byte[] DAY = new byte[1];
    @XmlTransient
    private byte[] HOUR = new byte[1];
    @XmlTransient
    private byte[] MINUTE = new byte[1];
    @XmlTransient
    private byte[] SECOND = new byte[1];

    protected ModemNetwork modemNetwork;
    protected ModemNode modemNode;
    protected AmrData amrData;
    protected BatteryLog batteryLog;
    // protected EventLog[] eventLog;
    @XmlElement(nillable = true)
    protected List<EventLog> eventLog;
    // protected LPData[] lpData;
    @XmlElement(nillable = true)
    protected List<LPData> lpData;
    protected byte[] data;
    protected int lpPeriod = 0;
    protected int pointer = 0;
    protected String fwVersion;
    protected String fwBuild;
    private static String FWVERSION = "2.1";
    private static String FWBUILD = "1.8";
    protected int networkType = 0;

    public ModemROM() {
        eventLog = new ArrayList<EventLog>();
        lpData = new ArrayList<LPData>();
    }
    
    public ModemROM(String fwVersion, String fwBuild)
    {
        this.fwVersion = fwVersion;
        this.fwBuild = fwBuild;
        eventLog = new ArrayList<EventLog>();
        lpData = new ArrayList<LPData>();
    }

    public int getLpPeriod()
    {
        return lpPeriod;
    }

    public void setLpPeriod(int lpPeriod)
    {
        this.lpPeriod = lpPeriod;
    }

    public int getPointer()
    {
        return pointer;
    }

    public void setPointer(int pointer)
    {
        this.pointer = pointer;
    }

    public int getNetworkType() {
		return networkType;
	}

	public void setNetworkType(int networkType) {
		this.networkType = networkType;
	}

	public int getNodeSize() {
        int size = BUFFER_NODEKIND.length + BUFFER_FIRMWAREVERSION.length +
        BUFFER_FIRMWAREBUILD.length + BUFFER_SOFTWAREVERSION.length +
        BUFFER_HARDWAREVERSION.length + BUFFER_PROTOCOLVERSION.length +
        BUFFER_ZDZDINTERFACEVERSION.length + BUFFER_RESETCOUNT.length +
        BUFFER_RESETREASON.length;
        if (fwVersion.compareTo(FWVERSION) >= 0 && fwBuild.compareTo(FWBUILD) >= 0) {
            size += BUFFER_SP_NETWORK.length + BUFFER_SOLAR_AD_VOLT.length +
            BUFFER_SOLAR_CHG_BATT_VOLT.length + BUFFER_SOLAR_B_DC_VOLT.length;
        }
        return size;
    }

    public int getNetworkSize() {
        return BUFFER_MANUAL_ENABLE.length + BUFFER_CHANNEL.length +
               BUFFER_PANID.length + BUFFER_TXPOWER.length +
               BUFFER_SECURITY_ENABLE.length + BUFFER_LINK_KEY.length +
               BUFFER_NETWORK_KEY.length + BUFFER_EXT_PANID.length;
    }

    public int getAmrSize() {
        int size = BUFFER_METER_SERIAL_NUMBER.length + BUFFER_CONSUMPTION_LOCATION.length +
        BUFFER_VENDOR.length + BUFFER_CUSTOMER_NAME.length +
        BUFFER_FIXED_RESET.length + BUFFER_TEST_FLAG.length +
        BUFFER_METERING_DAY.length + BUFFER_METERING_HOUR.length +
        BUFFER_REPEATING_DAY.length + BUFFER_REPEATING_HOUR.length +
        BUFFER_REPEATING_SETUP_SEC.length + BUFFER_NAZC_NUMBER.length +
        BUFFER_LP_CHOICE.length;
        if (fwVersion.compareTo(FWVERSION)>=0 && fwBuild.compareTo(FWBUILD) >= 0) {
            size += BUFFER_ALARM_FLAG.length + BUFFER_NO_JOIN_CNT.length +
            BUFFER_PERMIT_MODE.length + BUFFER_PERMIT_STATE.length +
            BUFFER_ALARM_MASK.length;
        }
        return size;
    }

    public static byte[] makeDayToByte(String strDay) {
        int[] day = new int[32];
        for (int i = 0; i < day.length; i++) {
            if (i + 1 == day.length) {
				day[i] = Integer.parseInt(strDay.substring(i));
			}
			else {
				day[i] = Integer.parseInt(strDay.substring(i, i+1));
			}
        }

        return makeDayToByte(day);
    }

    public static byte[] makeDayToByte(int[] day) {
        byte[] bDay = new byte[] {0x00, 0x00, 0x00, 0x00};

        for (int i = bDay.length-1, j = 0; i >= 0; i--) {
            for (int bit = 0, k = 1; bit < 8; bit++, k <<= 1, j++) {
                /*
            	if (j == 0)
                    k <<= 1;
                */
                if (day[j] == 0) {
                    bDay[i] |= 0x00;
                }
                else {
                    bDay[i] |= k;
                }
            }
        }

        log.debug(Hex.decode(bDay));
        return bDay;
    }

    public static int[] makeDayToInt(byte[] bDay) {

        List<Integer> list = new ArrayList<Integer>();
        int tDay = 0;
        for (int i = bDay.length-1; i >= 0; i--) {
            for (int k = 1, bit = 0; bit < 8; bit++, k <<= 1) {
                tDay = bDay[i] & k;
                if (tDay != 0) {
                    list.add(1);
                }
				else {
					list.add(0);
				}
            }
        }

        int[] day = new int[list.size()-1];
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < day.length; i++) {
            day[i] = (Integer)list.get(i+1);
            buf.append(day[i]+ ",");
        }
        log.debug(buf.toString());
        return day;
    }

    public static byte[] makeHourToByte(String strHour) {
        int[] hour = new int[96];
        for (int i = 0; i < hour.length; i++) {
            if (i + 1 == hour.length) {
				hour[i] = Integer.parseInt(strHour.substring(i));
			}
			else {
				hour[i] = Integer.parseInt(strHour.substring(i, i+1));
			}
        }

        return makeHourToByte(hour);
    }

    public static byte[] makeHourToByte(int[] hour) {
        byte[] bHour = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        for (int i = bHour.length-1, j = 0; i >= 0; i--) {
            for (int bit = 0, k = 1; bit < 8; bit++, k <<= 1, j++) {
                if (hour[j] == 0) {
                    bHour[i] |= 0x00;
                }
                else {
                    bHour[i] |= k;
                }
            }
        }

        log.debug(Hex.decode(bHour));
        return bHour;
    }

    public static int[] makeHourToInt(byte[] bHour) {

        List<Integer> list = new ArrayList<Integer>();
        int tHour = 0;
        for (int i = bHour.length-1; i >= 0; i--) {
            for (int k = 1, bit = 0; bit < 8; bit++, k <<= 1) {
                tHour = bHour[i] & k;
                if (tHour != 0) {
                    list.add(1);
                }
				else {
					list.add(0);
				}
            }
        }

        int[] hour = new int[list.size()];
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < hour.length; i++) {
            hour[i] = (Integer)list.get(i);
            buf.append(hour[i]+",");
        }

        log.debug(buf.toString());
        return hour;
    }

    public static byte[] makeByteArray(byte[] a,int len)
    {
        byte[] b = new byte[len];
        if(a.length==len) {
			return a;
		}
		else if(a.length>len) {
			System.arraycopy(a, a.length-len, b, 0, len);
		}
		else if(a.length<len)
        {
            System.arraycopy(a, 0,b,len-a.length,a.length);
        }
        return b;
    }
    public ModemNetwork getModemNetwork()
    {
        return modemNetwork;
    }

    public void setModemNetwork(ModemNetwork modemNetwork)
    {
        this.modemNetwork = modemNetwork;
    }

    public ModemNode getModemNode()
    {
        return modemNode;
    }

    public void setModemNode(ModemNode modemNode)
    {
        this.modemNode = modemNode;
    }

    public AmrData getAmrData()
    {
        return amrData;
    }

    public void setAmrData(AmrData amrData)
    {
        this.amrData = amrData;
    }

    public BatteryLog getBatteryLog()
    {
        return batteryLog;
    }

    public void setBatteryLog(BatteryLog batteryLog)
    {
        this.batteryLog = batteryLog;
    }

    public EventLog[] getEventLog()
    {
        return eventLog.toArray(new EventLog[0]);
    }

    public void setEventLog(List<EventLog> eventLog)
    {
        this.eventLog = eventLog;
    }

    public LPData[] getLpData()
    {
        return lpData.toArray(new LPData[0]);
    }

    public void setLpData(List<LPData> lpData)
    {
        this.lpData = lpData;
    }

    public void parseNetwork(byte[] data)
    {
        log.debug("Network Data Length[" + data.length + "]");
        modemNetwork = new ModemNetwork();
        int pos = 0;

        System.arraycopy(data, pos, BUFFER_MANUAL_ENABLE, 0, BUFFER_MANUAL_ENABLE.length);
        pos += BUFFER_MANUAL_ENABLE.length;
        modemNetwork.setManualEnable(DataUtil.getIntToByte(BUFFER_MANUAL_ENABLE[0]));

        System.arraycopy(data, pos, BUFFER_CHANNEL, 0, BUFFER_CHANNEL.length);
        pos += BUFFER_CHANNEL.length;
        modemNetwork.setChannel(DataUtil.getIntToByte(BUFFER_CHANNEL[0]));

        System.arraycopy(data, pos, BUFFER_PANID, 0, BUFFER_PANID.length);
        pos += BUFFER_PANID.length;
        modemNetwork.setPanId(DataUtil.getIntTo2Byte(BUFFER_PANID));

        System.arraycopy(data, pos, BUFFER_TXPOWER, 0, BUFFER_TXPOWER.length);
        pos += BUFFER_TXPOWER.length;
        modemNetwork.setTxPower(DataUtil.getIntToByte(BUFFER_TXPOWER[0]));

        System.arraycopy(data, pos, BUFFER_SECURITY_ENABLE, 0, BUFFER_SECURITY_ENABLE.length);
        pos += BUFFER_SECURITY_ENABLE.length;
        modemNetwork.setSecurityEnable(DataUtil.getIntToByte(BUFFER_SECURITY_ENABLE[0]));

        System.arraycopy(data, pos, BUFFER_LINK_KEY, 0, BUFFER_LINK_KEY.length);
        pos += BUFFER_LINK_KEY.length;
        modemNetwork.setLinkKey(Hex.decode(BUFFER_LINK_KEY));

        System.arraycopy(data, pos, BUFFER_NETWORK_KEY, 0, BUFFER_NETWORK_KEY.length);
        pos += BUFFER_NETWORK_KEY.length;
        modemNetwork.setNetworkKey(Hex.decode(BUFFER_NETWORK_KEY));

        System.arraycopy(data, pos, BUFFER_EXT_PANID, 0, BUFFER_EXT_PANID.length);
        pos += BUFFER_EXT_PANID.length;
        modemNetwork.setExtPanId(Hex.decode(BUFFER_EXT_PANID));

        log.debug(modemNetwork.toString());
    }

    public void parseNode(byte[] data)
    {
        modemNode = new ModemNode();
        int pos = 0;

        System.arraycopy(data, pos, BUFFER_NODEKIND, 0, BUFFER_NODEKIND.length);
        pos += BUFFER_NODEKIND.length;
        int count = 0;
        for (int i = 0; i < BUFFER_NODEKIND.length; i++, count++) {
            if (BUFFER_NODEKIND[i] == 0) {
				break;
			}
        }
        modemNode.setNodeKind(new String(BUFFER_NODEKIND,0,count));

        int version = 0;
        System.arraycopy(data, pos, BUFFER_FIRMWAREVERSION, 0, BUFFER_FIRMWAREVERSION.length);
        pos += BUFFER_FIRMWAREVERSION.length;
        //version = DataUtil.getIntToByte(BUFFER_FIRMWAREVERSION[0]);
        version = Integer.parseInt(Hex.decode(new byte[]{BUFFER_FIRMWAREVERSION[0]}));
        modemNode.setFirmwareVersion((version / 10) + "." + (version % 10));

        System.arraycopy(data, pos, BUFFER_FIRMWAREBUILD, 0, BUFFER_FIRMWAREBUILD.length);
        pos += BUFFER_FIRMWAREBUILD.length;
        modemNode.setFirmwareBuild(Hex.decode(new byte[] {BUFFER_FIRMWAREBUILD[0]}));

        System.arraycopy(data, pos, BUFFER_SOFTWAREVERSION, 0, BUFFER_SOFTWAREVERSION.length);
        pos += BUFFER_SOFTWAREVERSION.length;
        version = Integer.parseInt(Hex.decode(new byte[]{BUFFER_SOFTWAREVERSION[0]}));
        modemNode.setSoftwareVersion((version / 10) + "." + (version % 10));

        System.arraycopy(data, pos, BUFFER_HARDWAREVERSION, 0, BUFFER_HARDWAREVERSION.length);
        pos += BUFFER_HARDWAREVERSION.length;
        version = Integer.parseInt(Hex.decode(new byte[]{BUFFER_HARDWAREVERSION[0]}));
        modemNode.setHardwareVersion((version / 10) + "." + (version % 10));

        System.arraycopy(data, pos, BUFFER_PROTOCOLVERSION, 0, BUFFER_PROTOCOLVERSION.length);
        pos += BUFFER_PROTOCOLVERSION.length;
        version = Integer.parseInt(Hex.decode(new byte[]{BUFFER_PROTOCOLVERSION[0]}));
        modemNode.setProtocolVersion((version / 10) + "." + (version % 10));

        System.arraycopy(data, pos, BUFFER_ZDZDINTERFACEVERSION, 0, BUFFER_ZDZDINTERFACEVERSION.length);
        pos += BUFFER_ZDZDINTERFACEVERSION.length;
        version = Integer.parseInt(Hex.decode(new byte[]{BUFFER_ZDZDINTERFACEVERSION[0]}));
        modemNode.setZdzdInterfaceVersion((version / 10) + "." + (version % 10));

        System.arraycopy(data, pos, BUFFER_RESETCOUNT, 0, BUFFER_RESETCOUNT.length);
        pos += BUFFER_RESETCOUNT.length;
        modemNode.setResetCount(DataUtil.getIntTo2Byte(BUFFER_RESETCOUNT));

        System.arraycopy(data, pos, BUFFER_RESETREASON, 0, BUFFER_RESETREASON.length);
        pos += BUFFER_RESETREASON.length;
        modemNode.setResetReason(DataUtil.getIntToByte(BUFFER_RESETREASON[0]));
        int conditionLength=BUFFER_NODEKIND.length+
    						BUFFER_FIRMWAREVERSION.length+
    						BUFFER_FIRMWAREBUILD.length+
    						BUFFER_SOFTWAREVERSION.length+
    						BUFFER_HARDWAREVERSION.length+
    						BUFFER_PROTOCOLVERSION.length+
    						BUFFER_ZDZDINTERFACEVERSION.length+
    						BUFFER_RESETCOUNT.length+
    						BUFFER_RESETREASON.length;
        if (fwVersion.compareTo(FWVERSION) >= 0 && fwBuild.compareTo(FWBUILD) >= 0 && data.length>(conditionLength)) {
            System.arraycopy(data, pos, BUFFER_SP_NETWORK, 0, BUFFER_SP_NETWORK.length);
            pos += BUFFER_SP_NETWORK.length;
            modemNode.setSpNetwork(DataUtil.getIntToByte(BUFFER_SP_NETWORK[0]));

            System.arraycopy(data, pos, BUFFER_SOLAR_AD_VOLT, 0, BUFFER_SOLAR_AD_VOLT.length);
            pos += BUFFER_SOLAR_AD_VOLT.length;
            modemNode.setSolarADVolt(((double)DataUtil.getIntToByte(BUFFER_SOLAR_AD_VOLT[0])*2)/10000.0);

            System.arraycopy(data, pos, BUFFER_SOLAR_CHG_BATT_VOLT, 0, BUFFER_SOLAR_CHG_BATT_VOLT.length);
            pos += BUFFER_SOLAR_CHG_BATT_VOLT.length;
            modemNode.setSolarChgBattVolt(((double)DataUtil.getIntToByte(BUFFER_SOLAR_CHG_BATT_VOLT[0])/10000.0));

            System.arraycopy(data, pos, BUFFER_SOLAR_B_DC_VOLT, 0, BUFFER_SOLAR_B_DC_VOLT.length);
            pos += BUFFER_SOLAR_B_DC_VOLT.length;
            modemNode.setSolarBDCVolt(((double)DataUtil.getIntToByte(BUFFER_SOLAR_B_DC_VOLT[0])/10000.0));
        }

        log.debug(modemNode.toString());
    }

    public void parseAmrData(byte[] data)
    {
        amrData = new AmrData();
        int pos = 0;
        String ns = null;
        
        System.arraycopy(data, pos, BUFFER_METER_SERIAL_NUMBER, 0, BUFFER_METER_SERIAL_NUMBER.length);
        pos += BUFFER_METER_SERIAL_NUMBER.length;
        OCTET octet = new OCTET(BUFFER_METER_SERIAL_NUMBER.length);
        octet.decode(ns,BUFFER_METER_SERIAL_NUMBER, 0);
        amrData.setMeterSerialNumber(octet.toString());

        System.arraycopy(data, pos, BUFFER_VENDOR, 0, BUFFER_VENDOR.length);
        pos += BUFFER_VENDOR.length;
        octet.decode(ns,BUFFER_VENDOR, 0, BUFFER_VENDOR.length);

        int count = 0;
        for (int i = 0; i < BUFFER_VENDOR.length; i++, count++) {
            if (BUFFER_VENDOR[i] == 0) {
				break;
			}
        }
        amrData.setVendor(new String(BUFFER_VENDOR, 0, count));

        System.arraycopy(data, pos, BUFFER_CUSTOMER_NAME, 0, BUFFER_CUSTOMER_NAME.length);
        pos += BUFFER_CUSTOMER_NAME.length;
        octet.decode(ns,BUFFER_CUSTOMER_NAME, 0, BUFFER_CUSTOMER_NAME.length);
        amrData.setCustomerName(octet.toString());

        System.arraycopy(data, pos, BUFFER_CONSUMPTION_LOCATION, 0, BUFFER_CONSUMPTION_LOCATION.length);
        pos += BUFFER_CONSUMPTION_LOCATION.length;
        octet.decode(ns,BUFFER_CONSUMPTION_LOCATION, 0, BUFFER_CONSUMPTION_LOCATION.length);
        amrData.setConsumptionLocation(octet.toString());

        System.arraycopy(data, pos, BUFFER_FIXED_RESET, 0, BUFFER_FIXED_RESET.length);
        pos += BUFFER_FIXED_RESET.length;
        amrData.setFixedReset(DataUtil.getIntToByte(BUFFER_FIXED_RESET[0]));

        System.arraycopy(data, pos, BUFFER_TEST_FLAG, 0, BUFFER_TEST_FLAG.length);
        pos += BUFFER_TEST_FLAG.length;
        amrData.setTestFlag(DataUtil.getIntToByte(BUFFER_TEST_FLAG[0]));

        System.arraycopy(data, pos, BUFFER_METERING_DAY, 0, BUFFER_METERING_DAY.length);
        pos += BUFFER_METERING_DAY.length;
        amrData.setMeteringDay(makeDayToInt(BUFFER_METERING_DAY));

        System.arraycopy(data, pos, BUFFER_METERING_HOUR, 0, BUFFER_METERING_HOUR.length);
        pos += BUFFER_METERING_HOUR.length;
        amrData.setMeteringHour(makeHourToInt(BUFFER_METERING_HOUR));

        System.arraycopy(data, pos, BUFFER_REPEATING_DAY, 0, BUFFER_REPEATING_DAY.length);
        pos += BUFFER_REPEATING_DAY.length;
        amrData.setRepeatingDay(makeDayToInt(BUFFER_REPEATING_DAY));

        System.arraycopy(data, pos, BUFFER_REPEATING_HOUR, 0, BUFFER_REPEATING_HOUR.length);
        pos += BUFFER_REPEATING_HOUR.length;
        amrData.setRepeatingHour(makeHourToInt(BUFFER_REPEATING_HOUR));

        System.arraycopy(data, pos, BUFFER_REPEATING_SETUP_SEC, 0, BUFFER_REPEATING_SETUP_SEC.length);
        pos += BUFFER_REPEATING_SETUP_SEC.length;
        amrData.setRepeatingSetupSec(DataUtil.getIntTo2Byte(BUFFER_REPEATING_SETUP_SEC));

        System.arraycopy(data, pos, BUFFER_NAZC_NUMBER, 0, BUFFER_NAZC_NUMBER.length);
        pos += BUFFER_NAZC_NUMBER.length;

        System.arraycopy(data, pos, BUFFER_LP_CHOICE, 0, BUFFER_LP_CHOICE.length);
        pos += BUFFER_LP_CHOICE.length;
        amrData.setLpChoice(DataUtil.getIntToBytes(BUFFER_LP_CHOICE));
        int conditionLength=BUFFER_METER_SERIAL_NUMBER.length+
					        BUFFER_CONSUMPTION_LOCATION.length+
					        BUFFER_VENDOR.length+
					        BUFFER_CUSTOMER_NAME.length+
					        BUFFER_FIXED_RESET.length+
					        BUFFER_TEST_FLAG.length+
					        BUFFER_METERING_DAY.length+
					        BUFFER_METERING_HOUR.length+
					        BUFFER_REPEATING_DAY.length+
					        BUFFER_REPEATING_HOUR.length+
					        BUFFER_REPEATING_SETUP_SEC.length+
					        BUFFER_NAZC_NUMBER.length+
					        BUFFER_LP_CHOICE.length;

        if (fwVersion.compareTo(FWVERSION) >= 0 && fwBuild.compareTo(FWBUILD) >= 0 && data.length>conditionLength) {
            System.arraycopy(data, pos, BUFFER_ALARM_FLAG, 0, BUFFER_ALARM_FLAG.length);
            pos += BUFFER_ALARM_FLAG.length;
            amrData.setAlarmFlag(DataUtil.getIntToBytes(BUFFER_ALARM_FLAG));

            System.arraycopy(data, pos, BUFFER_NO_JOIN_CNT, 0, BUFFER_NO_JOIN_CNT.length);
            pos += BUFFER_NO_JOIN_CNT.length;
            amrData.setNoJoinCnt(DataUtil.getIntToBytes(BUFFER_NO_JOIN_CNT));

            System.arraycopy(data, pos, BUFFER_PERMIT_MODE, 0, BUFFER_PERMIT_MODE.length);
            pos += BUFFER_PERMIT_MODE.length;
            amrData.setPermitMode(DataUtil.getIntToBytes(BUFFER_PERMIT_MODE));

            System.arraycopy(data, pos, BUFFER_PERMIT_STATE, 0, BUFFER_PERMIT_STATE.length);
            pos += BUFFER_PERMIT_STATE.length;
            amrData.setPermitState(DataUtil.getIntToBytes(BUFFER_PERMIT_STATE));

            System.arraycopy(data, pos, BUFFER_ALARM_MASK, 0, BUFFER_ALARM_MASK.length);
            pos += BUFFER_ALARM_MASK.length;
            amrData.setAlarmMask(DataUtil.getIntToBytes(BUFFER_ALARM_MASK));
        }

        log.debug(amrData.toString());
    }

    public void parseBatteryLog(byte[] data)
    {
        batteryLog = new BatteryLog();

        for (int pos = 0; pos < data.length; ) {
            System.arraycopy(data, pos, BUFFER_BATTERY_VOLT, 0, BUFFER_BATTERY_VOLT.length);
            pos += BUFFER_BATTERY_VOLT.length;
            boolean isValid =true;
            if(DataUtil.getIntTo2Byte(BUFFER_BATTERY_VOLT) == 0xFFFF) {
				isValid =false;
			}
            if(isValid){
                batteryLog.addBatteryVolt(DataUtil.getIntTo2Byte(BUFFER_BATTERY_VOLT));
            }
            System.arraycopy(data, pos, BUFFER_CONSUMPTION_CURRENT, 0, BUFFER_CONSUMPTION_CURRENT.length);
            pos += BUFFER_CONSUMPTION_CURRENT.length;

            if(isValid){
                batteryLog.addConsumptionCurrent(DataUtil.getIntTo2Byte(BUFFER_CONSUMPTION_CURRENT));
            }
            System.arraycopy(data, pos, BUFFER_BATTERY_OFFSET, 0, BUFFER_BATTERY_OFFSET.length);
            pos += BUFFER_BATTERY_OFFSET.length;
            if(isValid){
                batteryLog.addOffset(DataUtil.getIntToByte(BUFFER_BATTERY_OFFSET[0]));
            }
        }

        log.debug(batteryLog.toString());
    }

    public void parseEventLog(byte[] data)
    {
        List<EventLog> list = new ArrayList<EventLog>();

        EventLog _eventLog = null;
        for (int pos = 0; pos < data.length; ) {
            _eventLog = new EventLog(fwVersion, fwBuild);

            System.arraycopy(data, pos, TIMEZONE, 0, TIMEZONE.length);
            pos += TIMEZONE.length;
            int timeZone = DataUtil.getIntTo2Byte(TIMEZONE);
            if (timeZone == 0xFF) {
            	pos += DST.length + YEAR.length + MONTH.length + DAY.length +
            	HOUR.length + MINUTE.length + SECOND.length +
            	BUFFER_EVENT_TYPE.length + BUFFER_EVENT_STATUS.length;
            	continue;
            }

            System.arraycopy(data, pos, DST,0, DST.length);
            pos += DST.length;
            int dst = DataUtil.getIntTo2Byte(DST);

            System.arraycopy(data, pos, YEAR, 0, YEAR.length);
            pos += YEAR.length;
            int year = DataUtil.getIntTo2Byte(YEAR);
            if (year == 0) {
            	pos += MONTH.length + DAY.length +
            	HOUR.length + MINUTE.length + SECOND.length +
            	BUFFER_EVENT_TYPE.length + BUFFER_EVENT_STATUS.length;
            	continue;
            }

            System.arraycopy(data, pos, MONTH, 0, MONTH.length);
            pos += MONTH.length;
            int month = DataUtil.getIntToBytes(MONTH);

            System.arraycopy(data, pos, DAY, 0, DAY.length);
            pos += DAY.length;
            int day = DataUtil.getIntToBytes(DAY);

            System.arraycopy(data, pos, HOUR, 0, HOUR.length);
            pos += HOUR.length;
            int hour = DataUtil.getIntToBytes(HOUR);

            System.arraycopy(data, pos, MINUTE, 0, MINUTE.length);
            pos += MINUTE.length;
            int minute = DataUtil.getIntToBytes(MINUTE);

            System.arraycopy(data, pos, SECOND, 0, SECOND.length);
            pos += SECOND.length;
            int second = DataUtil.getIntToBytes(SECOND);

            String timestamp = String.format("%04d%02d%02d%02d%02d%02d", year, month, day, hour, minute, second);

            _eventLog.setGmtTime(timestamp);

            System.arraycopy(data, pos, BUFFER_EVENT_TYPE, 0, BUFFER_EVENT_TYPE.length);
            pos += BUFFER_EVENT_TYPE.length;
            _eventLog.setEventType(DataUtil.getIntToByte(BUFFER_EVENT_TYPE[0]));

            System.arraycopy(data, pos, BUFFER_EVENT_STATUS, 0, BUFFER_EVENT_STATUS.length);
            pos += BUFFER_EVENT_STATUS.length;
            _eventLog.setEventStatus(Hex.decode(BUFFER_EVENT_STATUS));

            log.debug(_eventLog.toString());

            int currCentury;
            int dataCentury;
            try
            {
                currCentury = Integer.parseInt(TimeUtil.getCurrentDay().substring(0,2))*100;
                dataCentury = (year/100)*100;
                int diffYear = (currCentury - dataCentury) >  0 ? (currCentury - dataCentury) : (currCentury - dataCentury)*(-1);
                log.debug("currCentry = "+currCentury +" diffYear = "+diffYear);
                if(diffYear == 0 || diffYear ==1){
                    list.add(_eventLog);
                }
            }
            catch (NumberFormatException e)
            {
                log.warn(e,e);
            }
            catch (ParseException e)
            {
                log.warn(e,e);
            }

        }

        eventLog = list;
    }

    public void parseEventPointer(byte[] data)
    {
        int pos = 0;
        System.arraycopy(data, pos, BUFFER_EVENT_POINTER, 0, BUFFER_EVENT_POINTER.length);
        pos += BUFFER_EVENT_POINTER.length;
        pointer = DataUtil.getIntToBytes(BUFFER_EVENT_POINTER);
    }

    public void parseNetworkType(byte[] data)
    {
        int pos = 0;
        System.arraycopy(data, pos, BUFFER_NETWORK_TYPE, 0, BUFFER_NETWORK_TYPE.length);
        pos += BUFFER_NETWORK_TYPE.length;
        networkType = DataUtil.getIntToBytes(BUFFER_NETWORK_TYPE);
    }

    public void parseLPPeriodPointer(byte[] data)
    {
        int pos = 0;
        System.arraycopy(data, pos, BUFFER_METER_LPPERIOD, 0, BUFFER_METER_LPPERIOD.length);
        pos += BUFFER_METER_LPPERIOD.length;
        lpPeriod = DataUtil.getIntToBytes(BUFFER_METER_LPPERIOD);

        System.arraycopy(data, pos, BUFFER_METER_LPPOINTER, 0, BUFFER_METER_LPPOINTER.length);
        pos += BUFFER_METER_LPPOINTER.length;
        pointer = DataUtil.getIntToBytes(BUFFER_METER_LPPOINTER);
    }

    public void parseLP(int period)
    {
        this.setLpPeriod(period);

        byte[] year = new byte[2];
        byte[] month = new byte[1];
        byte[] day = new byte[1];
        byte[] basePulse = new byte[4];
        byte[] lp = new byte[2];

        List<LPData> list = new ArrayList<LPData>();
        LPData _lpData = null;
        int _year = 0;
        int _month = 0;
        int _day = 0;
        for (int pos = 0; pos < data.length; ) {
            System.arraycopy(data, pos, year, 0, year.length);
            pos += year.length;
            System.arraycopy(data, pos, month, 0, month.length);
            pos += month.length;
            System.arraycopy(data, pos, day, 0, day.length);
            pos += day.length;
            System.arraycopy(data, pos, basePulse, 0, basePulse.length);
            pos += basePulse.length;

            _year = DataUtil.getIntTo2Byte(year);
            _month = DataUtil.getIntToBytes(month);
            _day = DataUtil.getIntToBytes(day);

            _lpData = new LPData();
            _lpData.setPeriod(period);
            _lpData.setLpDate(_year + (_month < 10? "0"+_month:""+_month)
                                  + (_day < 10? "0"+_day:""+_day));
            _lpData.setBasePulse(DataUtil.getLongToBytes(basePulse));
            _lpData.setLp(new int[24*period]);
            for (int j = 0; j < _lpData.getLp().length; j++) {
                lp = new byte[2];
                System.arraycopy(data, pos, lp, 0, lp.length);
                pos += lp.length;
                _lpData.getLp()[j] = DataUtil.getIntTo2Byte(lp);
            }
            list.add(_lpData);
        }

        lpData = list;
    }

    public void parse(int address, byte[] data) {

        this.data = data;
        if (address == OFFSET_MANUAL_ENABLE) {
            parseNetwork(data);
        }
        else if (address == OFFSET_NODEKIND) {
            parseNode(data);
        }
        else if (address == OFFSET_METER_SERIAL_NUMBER) {
            parseAmrData(data);
        }
        else if (address > OFFSET_BATTERY_POINTER && address < OFFSET_EVENT_POINTER) {
            parseBatteryLog(data);
        }
        else if (address == OFFSET_EVENT_POINTER) {
            parseEventPointer(data);
        }
        else if (address >= OFFSET_EVENT_LOGDATA && address < OFFSET_METER_LPPERIOD) {
            parseEventLog(data);
        }
        else if (address == OFFSET_METER_LPPERIOD) {
            parseLPPeriodPointer(data);
        }
        else if (address >= OFFSET_METER_LPLOGDATA){
            this.data = data;
        }
        else if (address == OFFSET_NETWORK_TYPE) {
        	parseNetworkType(data);
        }
    }

    private byte[] getStream(int address, int size, byte[] value) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(DataUtil.get2ByteToInt(address),
                     0, 2);
        stream.write(DataUtil.get2ByteToInt(size),
                     0, 2);
        stream.write(value, 0, value.length);

        return stream.toByteArray();
    }

    public byte[] getManualEnableStream() {
        return getStream(OFFSET_MANUAL_ENABLE,
                         BUFFER_MANUAL_ENABLE.length,
                         new byte[]{DataUtil.getByteToInt(modemNetwork.getManualEnable())});
    }

    public byte[] getChannelStream() {
        return getStream(OFFSET_CHANNEL,
                         BUFFER_CHANNEL.length,
                         new byte[]{DataUtil.getByteToInt(modemNetwork.getChannel())});
    }

    public byte[] getPanIdStream() {
        return getStream(OFFSET_PANID,
                         BUFFER_PANID.length,
                         DataUtil.get2ByteToInt(modemNetwork.getPanId()));
    }

    public byte[] getTxPowerStream() {
        return getStream(OFFSET_TXPOWER,
                         BUFFER_TXPOWER.length,
                         new byte[]{DataUtil.getByteToInt(modemNetwork.getTxPower())});
    }

    public byte[] getSecurityEnableStream() {
        return getStream(OFFSET_SECURITY_ENABLE,
                         BUFFER_SECURITY_ENABLE.length,
                         new byte[]{DataUtil.getByteToInt(modemNetwork.getSecurityEnable())});
    }

    public byte[] getLinkKeyStream() {
        OCTET octet = new OCTET(16);
        octet.setValue(modemNetwork.getLinkKey());
        return getStream(OFFSET_LINK_KEY,
                         BUFFER_LINK_KEY.length,
                         octet.encode());
    }

    public byte[] getNetworkKeyStream() {
        OCTET octet = new OCTET(16);
        octet.setValue(modemNetwork.getNetworkKey());
        return getStream(OFFSET_NETWORK_KEY,
                         BUFFER_NETWORK_KEY.length,
                         octet.encode());
    }

    public byte[] getMeterSerialNumberStream() {
        OCTET octet = new OCTET(20);
        octet.setValue(amrData.getMeterSerialNumber());
        return getStream(OFFSET_METER_SERIAL_NUMBER,
                         BUFFER_METER_SERIAL_NUMBER.length,
                         octet.encode());
    }

    public byte[] getVendorStream() {
        OCTET octet = new OCTET(20);
        octet.setValue(amrData.getVendor());
        return getStream(OFFSET_VENDOR,
                         BUFFER_VENDOR.length,
                         octet.encode());
    }

    public byte[] getCustomerNameStream() {
        OCTET octet = new OCTET(30);
        octet.setValue(amrData.getCustomerName());
        return getStream(OFFSET_CUSTOMER_NAME,
                         BUFFER_CUSTOMER_NAME.length,
                         octet.encode());
    }

    public byte[] getConsumptionLocationStream() {
        OCTET octet = new OCTET(30);
        octet.setValue(amrData.getConsumptionLocation());
        return getStream(OFFSET_CONSUMPTION_LOCATION,
                         BUFFER_CONSUMPTION_LOCATION.length,
                         octet.encode());
    }

    public byte[] getFixedResetStream() {
        return getStream(OFFSET_FIXED_RESET,
                         BUFFER_FIXED_RESET.length,
                         new byte[]{DataUtil.getByteToInt(amrData.getFixedReset())});
    }

    public byte[] getTestFlagStream() {
        return getStream(OFFSET_TEST_FLAG,
                         BUFFER_TEST_FLAG.length,
                         new byte[]{DataUtil.getByteToInt(amrData.getTestFlag())});
    }

    public byte[] getMeteringDayStream() {
        return getStream(OFFSET_METERING_DAY,
                         BUFFER_METERING_DAY.length,
                         makeDayToByte(amrData.getMeteringDay()));
    }

    public byte[] getMeteringHourStream() {
        return getStream(OFFSET_METERING_HOUR,
                         BUFFER_METERING_HOUR.length,
                         makeHourToByte(amrData.getMeteringHour()));
    }

    public byte[] getRepeatingDayStream() {
        return getStream(OFFSET_REPEATING_DAY,
                         BUFFER_REPEATING_DAY.length,
                         makeDayToByte(amrData.getRepeatingDay()));
    }

    public byte[] getRepeatingHourStream() {
        return getStream(OFFSET_REPEATING_HOUR,
                         BUFFER_REPEATING_HOUR.length,
                         makeHourToByte(amrData.getRepeatingHour()));
    }

    public byte[] getRepeatingSetupSecStream() {
        return getStream(OFFSET_REPEATING_SETUP_SEC,
                         BUFFER_REPEATING_SETUP_SEC.length,
                         DataUtil.get2ByteToInt(amrData.getRepeatingSetupSec()));
    }

    public byte[] getLpChoiceStream() {
        return getStream(OFFSET_LP_CHOICE,
                         BUFFER_LP_CHOICE.length,
                         new byte[] {DataUtil.getByteToInt(amrData.getLpChoice())});
    }

    /**
     * @param append source of String
     * @param str   to append
     * @param length
     * @return
     */
    private static String frontAppendNStr(char append, String str, int length)
    {
        StringBuffer b = new StringBuffer("");

        try {
            if(str.length() < length)
            {
               for(int i = 0; i < length-str.length() ; i++) {
				b.append(append);
			}
               b.append(str);
            }
            else
            {
                b.append(str);
            }
        } catch(Exception e) {
        }
        return b.toString();
    }
}
