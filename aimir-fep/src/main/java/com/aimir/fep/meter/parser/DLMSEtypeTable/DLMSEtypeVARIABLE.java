package com.aimir.fep.meter.parser.DLMSEtypeTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DLMSEtypeVARIABLE {
	private static Log log = LogFactory.getLog(DLMSEtypeVARIABLE.class);

    public enum OBIS {
    	TIME("0000010000FF", "Time"),
        DEVICE_INFO("00002A0000FF", "Device Information"),
        METER_INFO("0101000300VZ", "Meter Information"),
        OUTPUT_SIGNAL("0000600302FF", "Output Signal"),
        MONTHLY_ENERGY_PROFILE("0000620101FF", "Monthly Energy Profile"),
        MONTHLY_DEMAND_PROFILE("0000620102FF", "Monthly Demand Profile"),
        CUSTOMER_METER_ID("0100000000FF", "Customer Meter ID"),
        MANUFACTURER_METER_ID("0100000001FF", "Manufacturer Meter ID"),
        VZ("0100000100FF","VZ"),
        BILLING_PERIOD("0100000101FF","Billing Period"),
        LCD_INFO("010000F100FF","LCD Information"),
        LOAD_CONTROL_STATUS("010000F400FF","Load Control Status"),
        LOAD_PROFILE("0100630100FF","Load Profile"),
        POWER_FAILURE("0100636201FF","Power Failure"),
        POWER_RESTORE("0100636202FF","Power Restore"),
        TIME_CHANGE_FROM("0100636203FF", "Time Change From"),
        TIME_CHANGE_TO("0100636204FF", "Time Change To"),
        DEMAND_RESET("0100636205FF", "Demand Reset"),
        MANUAL_DEMAND_RESET("0100636206FF", "Manual Demand Reset"),
        SELF_READ("0100636207FF", "Self Read"),
        PROGRAM_CHANGE("0100636208FF", "Program Change"),
        METER_CONSTANT_ACTIVE("0101000300FF","Meter Constant For Active Energy"),
        METER_CONSTANT_REACTIVE("0101000301FF","Meter Constant For Reactive Energy"),
        CURRENT_MAX_DEMAND("018080808064","Current Energy/Max Demand"),
        PREVIOUS_MAX_DEMAND("018080808065","Previous Energy/Max Demand"),
        KEPCO_METER_INFO("0180808081FF","Meter Information"),
        POWER_FAILURE_COUNT("0000600700FF", "Power Failure Count"),        // 정전횟수 Data
        PROGRAM_CHANGE_COUNT("0000600200FF", "Program Change Count Count"),       // 프로그램 변경 횟수 Data
        PROGRAM_CHANGE_DATE("0000600201FF", "Program Change Date"),        // 프로그램 변경 일자/시간 Data
        PROGRAM_CHANGE_RESERVEDATE("0000600206FF", "Program Change Reserve Date"), // 프로그램 변경 예약 시간 Data
        BATTERY_USE_TIME("0000600600FF", "Battery Use Time"),           // 배터리 사용시간 Data
        SELF_CHECK_BATTERY("0000616100FF", "Self Check Battery"),         // 자기진단1[배터리 이상] Data
        SELF_CHECK_MEMORY("0000616101FF", "Self Check Memory"),          // 자기진단2[메모리 이상] Data
        SELF_CHECK_POWER("0000616102FF", "Self Check Power"),           // 자기진단3[전압결상 등] Data
        LOGICAL_NAME("0101010400FF", "Logical Name"),
        CURRENT_AVERAGE("0101010400FF", "Current Average"),
        LAST_AVERAGE("0101010400FF", "Last Average"),
        SCALAR_UNIT("0101010400FF", "Scalar Unit"),
        STATUS("0101010400FF", "Status"),
        CAPTURE_TIME("0101010400FF", "Capture Time"),
        START_TIME_CURRENT("0101010400FF", "Start Time Current"),
        PERIOD("0101010400FF", "Period"),
        NUMBER_OF_PERIODS("0101010400FF", "Number Of Periods"),
        POWER_QUALITY("0101628000FF", "Power Quality"),
        SAG("0100630A01FF", "Sag"),
        SWELL("0100630A02FF", "Swell"),
        LOAD_CONTROL("00000A0067FF", "Load Control"),
        LP_INTERVAL("0101000804FF", "LP Interval"),
        IMPORT_ACTIVE_ENERGY("0101010800FF", "Import Active Energy"),
        LAST_METERING_VALUE("00005E340001", "Last Metering Value"), // last metering value와 network info는 노르웨이 POC 용
        NETWORK_INFO("00005E340002", "Network Information"), // 노르웨이 POC용
        METERING_DATA("00005E340003", "Metering Data"); // 노르웨이 POC용
        
        private String code;
        private String name;
        
        OBIS(String code, String name) {
            this.code = code;
            this.name = name;
        }
        
        public String getCode() {
            return this.code;
        }
        
        public String getName() {
        	return this.name;
        }
        
        public static OBIS getObis(String code) {
            for (OBIS obis : values()) {
                if (obis.getCode().equals(code)) return obis;
            }
            return null;
        }
    }

    public final static String UNDEFINED = "undefined";

    public enum METER_CONSTANT {
        ActiveC,
        ReactiveC;
    }
    
    public enum OUTPUT_SIGNAL {
        NONE(0),            // 설정없음 
        REMOTE_CONTROL(4),  // 원격부하 개폐신호
        TIME_SWITCH(8),     // 타임스위치 개폐신호
        CURRENT_LIMIT(16);  // 전류제한 기능
        
        int code;
        
        OUTPUT_SIGNAL(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public static OUTPUT_SIGNAL getValue(int code) {
            for (OUTPUT_SIGNAL a : OUTPUT_SIGNAL.values()) {
                if (a.getCode() == code)
                    return a;
            }
            return null;
        }
    }
    
    public enum METER_TIME {
    	BeforeTime(0, "beforeTime"),
    	AfterTime(1, "afterTime");
    	
    	private int code;
        private String name;
        
        METER_TIME(int code, String name) {
            this.code = code;
            this.name = name;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public String getName(){
        	return this.name;
        }
    }

    public enum LOAD_PROFILE {
    	Structure(0, "Structure"),
        Time(1, "Time"),
        ImportActive(2, "Import Active"),
    	Status(3, "Status");
        // ExportActive(7, "Export Active Energy QII+QIII");
        
        private int code;
        private String name;
        
        LOAD_PROFILE(int code, String name) {
            this.code = code;
            this.name = name;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public String getName(){
        	return this.name;
        }
    }
	
    public enum DLMS_CLASS {
        DATA(1),
        REGISTER(3),
        DEMAND_REGISTER(5),
        PROFILE_GENERIC(7),
        CLOCK(8),
        SCRIPT_TABLE(9);
        
        private int clazz;
        
        DLMS_CLASS(int clazz) {
            this.clazz = clazz;
        }
        
        public int getClazz() {
            return this.clazz;
        }
    }
    
    public enum DLMS_CLASS_ATTR {
        DATA_ATTR01(2),            // value
        REGISTER_ATTR02(2),        // value
        REGISTER_ATTR03(3),        // scalar unit
        PROFILE_GENERIC_ATTR02(2), // buffer
        PROFILE_GENERIC_ATTR03(3),
        PROFILE_GENERIC_ATTR04(4),
        PROFILE_GENERIC_ATTR07(7), // entries in use
        DEMAND_REGISTER_ATTR01(1),
        DEMAND_REGISTER_ATTR02(2),
        DEMAND_REGISTER_ATTR03(3),
        DEMAND_REGISTER_ATTR04(4),
        DEMAND_REGISTER_ATTR05(5),
        DEMAND_REGISTER_ATTR06(6),
        DEMAND_REGISTER_ATTR07(7),
        DEMAND_REGISTER_ATTR08(8),
        DEMAND_REGISTER_ATTR09(9),
        CLOCK_ATTR02(2),
        SCRIPT_TABLE_ATTR01(1),
        SCRIPT_TABLE_ATTR02(2);
        
        private int attr;
        
        DLMS_CLASS_ATTR(int attr) {
            this.attr = attr;
        }
        
        public int getAttr() {
            return this.attr;
        }
    }

    public enum DLMS_TAG_TYPE {
        Null(0, 1),
        Array(1, 1),
        CompactArray(19, 1),
        Structure(2, 1),
        Enum(22, 1),
        Group(128, 1),
        BitString(4, 0),
        OctetString(9, 0),
        VisibleString(10, 0),
        BCD(13, 1),
        Boolean(3, 1),
        INT8(15, 1),
        UINT8(17, 1),
        INT16(16, 2),
        UINT16(18, 2),
        INT32(5, 4),
        UINT32(6, 4),
        FLOAT32(23, 4),
        INT64(20, 8),
        UINT64(21, 8),
        Datetime(25, 12),
        Date(26, 5),
        Time(27, 4);
        
        private int value;
        private int len;
        
        DLMS_TAG_TYPE(int value, int len) {
            this.value = value;
            this.len = len;
        }
        
        public int getValue() {
            return this.value;
        }
        
        public int getLenth() {
            return this.len;
        }
    }
	
	public DLMSEtypeVARIABLE() {

	}

	public static String getDataName(OBIS obis, int cnt) {
        if (obis == OBIS.LOAD_PROFILE) {
            for (LOAD_PROFILE profile : LOAD_PROFILE.values()) {
                if (profile.getCode() == (cnt % LOAD_PROFILE.values().length))
                    return profile.name();
            }
        }
        else if (obis == OBIS.TIME) {
    		for (METER_TIME meterTime : METER_TIME.values()) {
                if (meterTime.getCode() == cnt) {
                    return meterTime.name();
                }
            }
        }
		return UNDEFINED;
	}

	public static boolean getIncludeObis(OBIS obis) {

		if (obis == OBIS.MONTHLY_ENERGY_PROFILE) {
			return true;
		} 
		return false;
	}

	public static String getUnit(int unit) {

		if (unit == 1)
			return "a";
		else if (unit == 2)
			return "mo";
		else if (unit == 3)
			return "wk";
		else if (unit == 4)
			return "d";
		else if (unit == 5)
			return "h";
		else if (unit == 6)
			return "min.";
		else if (unit == 7)
			return "s";
		else if (unit == 8)
			return "°";
		else if (unit == 9)
			return "°C";
		else if (unit == 10)
			return "currency";
		else if (unit == 11)
			return "m";
		else if (unit == 12)
			return "m/s";
		else if (unit == 13)
			return "m3";
		else if (unit == 14)
			return "m3";
		else if (unit == 15)
			return "m3/h";
		else if (unit == 16)
			return "m3/h";
		else if (unit == 17)
			return "m3/d";
		else if (unit == 18)
			return "m3/d";
		else if (unit == 19)
			return "l";
		else if (unit == 20)
			return "kg";
		else if (unit == 21)
			return "N";
		else if (unit == 22)
			return "Nm";
		else if (unit == 23)
			return "Pa";
		else if (unit == 24)
			return "bar";
		else if (unit == 25)
			return "J";
		else if (unit == 26)
			return "J/h";
		else if (unit == 27)
			return "W";
		else if (unit == 28)
			return "VA";
		else if (unit == 29)
			return "var";
		else if (unit == 30)
			return "Wh";
		else if (unit == 31)
			return "VAh";
		else if (unit == 32)
			return "varh";
		else if (unit == 33)
			return "A";
		else if (unit == 34)
			return "C";
		else if (unit == 35)
			return "V";
		else if (unit == 36)
			return "V/m";
		else if (unit == 37)
			return "F";
		else if (unit == 38)
			return "Ω";
		else if (unit == 39)
			return "Ωm2/m";
		else if (unit == 40)
			return "Wb";
		else if (unit == 41)
			return "T";
		else if (unit == 42)
			return "A/m";
		else if (unit == 43)
			return "H";
		else if (unit == 44)
			return "Hz";
		else if (unit == 45)
			return "1/(Wh)";
		else if (unit == 46)
			return "1/(varh)";
		else if (unit == 47)
			return "1/(VAh)";
		else if (unit == 48)
			return "V2h";
		else if (unit == 49)
			return "A2h";
		else if (unit == 50)
			return "kg/s";
		else if (unit == 51)
			return "S, mho";
		else if (unit == 52)
			return "K";
		else if (unit == 53)
			return "1/(V2h)";
		else if (unit == 54)
			return "1/(A2h)";
		else if (unit == 55)
			return "1/m3";
		else if (unit == 56)
			return "percentage";
		else if (unit == 57)
			return "Ah";
		else if (unit == 60)
			return "Wh/m3";
		else if (unit == 61)
			return "J/m3";
		else if (unit == 62)
			return "Mol %";
		else if (unit == 63)
			return "g/m3";
		else if (unit == 64)
			return "Pa s";
		else if (unit == 253)
			return "reserved";
		else if (unit == 254)
			return "other";
		else if (unit == 255)
			return "unitless";
		else
			return "unitless";

	}
}
