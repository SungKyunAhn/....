package com.aimir.fep.meter.parser.DLMSGtypeLSTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DLMSGtypeVARIABLE {
	private static Log log = LogFactory.getLog(DLMSGtypeVARIABLE.class);

    public enum OBIS {
    	METER_TIME("0000010000FF", "Meter Time"),
        DEVICE_INFO("00002A0000FF", "Device Information"),
        METER_INFO("0101000300VZ", "Meter Information"),
        OUTPUT_SIGNAL("0000600302FF", "Output Signal"),
        MONTHLY_ENERGY_PROFILE("0000620101FF", "Monthly Energy Profile"),
        MONTHLY_DEMAND_PROFILE("0000620102FF", "Monthly Demand Profile"),
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
        LOAD_CONTROL("00000A0067FF", "Load Control");
        
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
    
    public enum LOAD_CONTROL_STATUS {
        OPEN(0),
        CLOSE(1);
        
        int code;
        
        LOAD_CONTROL_STATUS(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public static LOAD_CONTROL_STATUS getValue(int code) {
            for (LOAD_CONTROL_STATUS a : LOAD_CONTROL_STATUS.values()) {
                if (a.getCode() == code)
                    return a;
            }
            return null;
        }
    }
    
    public enum POWER_QUALITY {
        Structure(1, "Structure"),
        CurrentA(2, "Current A"),
        VoltageA(3, "Voltage A"),
        THD_A(4, "THD A"),
        PowerFactorA(5, "Power Factor A"),
        AngleA(6, "Voltage Current Angle A"),
        CurrentB(7, "Current B"),
        VoltageB(8, "Voltage B"),
        THD_B(9, "THD B"),
        PowerFactorB(10, "Power Factor B"),
        AngleB(11, "Voltage Current Angle B"),
        CurrentC(12, "Current C"),
        VoltageC(13, "Voltage C"),
        THD_C(14, "THD C"),
        PowerFactorC(15, "Power Factor C"),
        AngleC(16, "Voltage Current Angle C"),
        VoltageAngleAB(17, "Voltage Angle AB"),
        VoltageAngleAC(18, "Voltage Angle AC"),
        Temperature(19, "Temperature");
        
        private int code;
        private String name;
        
        POWER_QUALITY(int code, String name) {
            this.code = code;
            this.name = name;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public String getName() {
            return this.name;
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

    public enum KEPCO_METER_INFO {
        MeterKind(2, "Meter Kind"),
        VendorNum(3, "Vendor"),
        MeterId(4, "Meter ID"),
        MeterDate(5, "Meter Date Time"),
        MeterStatusError(6, "Meter Error Status"),
        MeterStatusCaution(7, "Meter Caution Status"),
        MeterPulseConstant(8, "Meter Pulse Constant"),
        PTRatio(9, "PT Ratio"),
        CTRatio(10, "CT Ratio"),
        RegularReadDate(11, "Regular Read Date"),
        LoadProfileInterval(12, "Load Profile Interval"),
        RecentReadLoadProfileDate(13, "Recent Read Load Profile Date"),
        LastReadInfo(14, "Last Read Information");
        
        private int code;
        private String name;
        
        KEPCO_METER_INFO(int code, String name) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public String getName(){
        	return this.name;
        }
    }

    public enum MONTHLY_ENERGY_PROFILE {
        ActiveEnergy(2, "Monthly Active Energy"),
        ApparentEnergy(3, "Monthly Apparent Energy"), // 피상
        LaggingReactiveEnergy(4, "Monthly Lagging Reactive Energy"), // 지상
        LeadingReactiveEnergy(5, "Monthly Leading Reactive Energy"), // 진상
        AveragePowerFactor(6, "Monthly Average Power Factor"),
        
        T1ActiveEnergy(7, "Monthly Tariff1 Active Energy"),
        T1ApparentEnergy(8, "Monthly Tariff1 Apparent Energy"),
        T1LaggingReactiveEnergy(9, "Monthly Tariff1 Lagging Reactive Energy"),
        T1LeadingReactiveEnergy(10, "Monthly Tariff1 Leading Reactive Energy"),
        T1AveragePowerFactor(11, "Monthly Tariff1 Average Power Factor"),
        
        T2ActiveEnergy(12, "Monthly Tariff2 Active Energy"),
        T2ApparentEnergy(13, "Monthly Tariff2 Apparent Energy"),
        T2LaggingReactiveEnergy(14, "Monthly Tariff2 Lagging Reactive Energy"),
        T2LeadingReactiveEnergy(15, "Monthly Tariff2 Leading Reactive Energy"),
        T2AveragePowerFactor(16, "Monthly Tariff2 Average Power Factor"),
        
        T3ActiveEnergy(17, "Monthly Tariff3 Active Energy"),
        T3ApparentEnergy(18, "Monthly Tariff3 Apparent Energy"),
        T3LaggingReactiveEnergy(19, "Monthly Tariff3 Lagging Reactive Energy"),
        T3LeadingReactiveEnergy(20, "Monthly Tariff3 Leading Reactive Energy"),
        T3AveragePowerFactor(21, "Monthly Tariff3 Average Power Factor"),
        
        T4ActiveEnergy(22, "Monthly Tariff4 Active Energy"),
        T4ApparentEnergy(23, "Monthly Tariff4 Apparent Energy"),
        T4LaggingReactiveEnergy(24, "Monthly Tariff4 Lagging Reactive Energy"),
        T4LeadingReactiveEnergy(25, "Monthly Tariff4 Leading Reactive Energy"),
        T4AveragePowerFactor(26, "Monthly Tariff4 Average Power Factor");
        
        private int code;
        private String name;
        
        MONTHLY_ENERGY_PROFILE(int code, String name) {
            this.code = code;
            this.name = name;
        }
        
        public String getName(){
        	return this.name;
        }
        
        public int getCode() {
            return this.code;
        }
    }

    public enum MONTHLY_DEMAND_PROFILE {
        Active(2, "Active Demand"),
        ActiveDate(3, "Active Demand Date"),
        CummlativeActive(4, "Cummlative Active"),
        Apparent(5, "Apparent Demand"),
        ApparentDate(6, "Apparent Demand Date"),
        CummlativeApparent(7, "Cummlative Apparent Demand"),
        T1Active(8, "T1 Active Demand"),
        T1ActiveDate(9, "T1 Active Demand Date"),
        T1CummlativeActive(10, "T1 Cummlative Active"),
        T1Apparent(11, "T1 Apparent Demand"),
        T1ApparentDate(12, "T1 Apparent Demand Date"),
        T1CummlativeApparent(13, "T1 Cummlative Apparent Demand"),
        T2Active(14, "T2 Active Demand"),
        T2ActiveDate(15, "T2 Active Demand Date"),
        T2CummlativeActive(16, "T2 Cummlative Active"),
        T2Apparent(17, "T2 Apparent Demand"),
        T2ApparentDate(18, "T2 Apparent Demand Date"),
        T2CummlativeApparent(19, "T2 Cummlative Apparent Demand"),
        T3Active(20, "T3 Active Demand"),
        T3ActiveDate(21, "T3 Active Demand Date"),
        T3CummlativeActive(22, "T3 Cummlative Active"),
        T3Apparent(23, "T3 Apparent Demand"),
        T3ApparentDate(24, "T3 Apparent Demand Date"),
        T3CummlativeApparent(25, "T3 Cummlative Apparent Demand"),
        T4Active(26, "T4 Active Demand"),
        T4ActiveDate(27, "T4 Active Demand Date"),
        T4CummlativeActive(28, "T4 Cummlative Active"),
        T4Apparent(29, "T4 Apparent Demand"),
        T4ApparentDate(30, "T4 Apparent Demand Date"),
        T4CummlativeApparent(31, "T4 Cummlative Apparent Demand");
        
        private int code;
        private String name;
                
        MONTHLY_DEMAND_PROFILE(int code, String name) {
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

    public enum PREVIOUS_MAX_DEMAND {
        T1PreviousActive(2, "Rate1 Previous Active"),
        T1PreviousReActive(3, "Rate1 Previous Reactive"),
        T1PreviousActiveMax(4, "Rate1 Previous Active Max"),
        T1PreviousActiveSum(5, "Rate1 Previous Active Sum"),
        T1PreviousActiveMaxDate(6, "Rate1 Previous Active Max Date Time"),
        T2PreviousActive(7, "Rate2 Previous Active"),
        T2PreviousReActive(8, "Rate2 Previous Reactive"),
        T2PreviousActiveMax(9, "Rate2 Previous Active Max"),
        T2PreviousActiveSum(10, "Rate2 Previous Active Sum"),
        T2PreviousActiveMaxDate(11, "Rate2 Previous Active Max Date"),
        T3PreviousActive(12, "Rate3 Previous Active"),
        T3PreviousReActive(13, "Rate3 Previous Reactive"),
        T3PreviousActiveMax(14, "Rate3 Previous Active Max"),
        T3PreviousActiveSum(15, "Rate3 Previous Active Sum"),
        T3PreviousActiveMaxDate(16, "Rate3 Previous Active Max Date");
        
        private int code;
        private String name;
        
        PREVIOUS_MAX_DEMAND(int code, String name) {
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
	
    public enum CURRENT_MAX_DEMAND {
        T1CurrentActive(2, "Rate1 Current Active"),
        T1CurrentReActive(3, "Rate1 Current Reactive"),
        T1CurrentActiveMax(4, "Rate1 Current Active Max"),
        T1CurrentActiveSum(5, "Rate1 Current Active Sum"),
        T1CurrentActiveMaxDate(6, "Rate1 Current Active Max Date Time"),
        T2CurrentActive(7, "Rate2 Current Active"),
        T2CurrentReActive(8, "Rate2 Current Reactive"),
        T2CurrentActiveMax(9, "Rate2 Current Active Max"),
        T2CurrentActiveSum(10, "Rate2 Current Active Sum"),
        T2CurrentActiveMaxDate(11, "Rate2 Current Active Max Date Time"),
        T3CurrentActive(12, "Rate3 Current Active"),
        T3CurrentReActive(13, "Rate3 Current Reactive"),
        T3CurrentActiveMax(14, "Rate3 Current Active Max"),
        T3CurrentActiveSum(15, "Rate3 Current Active Sum"),
        T3CurrentActiveMaxDate(16, "Rate3 Current Active Max Date Time");
        
        private int code;
        private String name;
        
        CURRENT_MAX_DEMAND(int code, String name) {
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
    
    public enum EVENT {
        EventTime(0, "Event Time"),
        EventCount(1, "Event Count");
        
        private int code;
        private String name;
        
        EVENT(int code, String name) {
            this.code = code;
            this.name = name;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public String getName() {
            return this.name;
        }
    }

    public enum EVENT_LOG {
        PowerFailure(1, "Power Down"),
        // PowerFailureCnt(1),
        PowerRestore(2, "Power Up"),
        // PowerRestoreCnt(2),
        TimeChangeFrom(3, "Time Change From"),
        // TimeChangeFromCnt(3),
        TimeChangeTo(4, "Time Change To"),
        // TimeChangeToCnt(4),
        DemandReset(5, "Demand Reset"),
        // DemandResetCnt(5),
        ManualDemandReset(6, "Manual Demand Reset"),
        // ManualDemandResetCnt(6),
        SelfRead(7, "Self Read"),
        // SelfReadCnt(7),
        ProgramChange(8, "Program Change");
        // ProgramChangeCnt(8);
        
        private int flag;
        private String msg;
        
        EVENT_LOG(int flag, String msg) {
            this.flag = flag;
            this.msg = msg;
        }
        
        public int getFlag() {
            return this.flag; 
        }
        
        public String getMsg() {
            return this.msg;
        }
    }

    public enum LOAD_PROFILE {
    	Structure(0, "Structure"),
        ImportActive(1, "Import Active Energy QI+QIV"),
        ImportLaggingReactive(2, "Import Lagging Reactive Energy QI"),
        ImportLeadingReactive(3, "Import Leading Reactive Energy QIV"),
        ImportApparentEnergy(4, "Import Apparent Energy QI+QIV"),
        Date(5, "Date"),
        Status(6, "Status");
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
        BitString(4, 1),
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
	
	public DLMSGtypeVARIABLE() {

	}

	public static String getDataName(OBIS obis, int cnt) {
        if (obis == OBIS.KEPCO_METER_INFO) {
            for (KEPCO_METER_INFO meterInfo : KEPCO_METER_INFO.values()) {
                if (meterInfo.getCode() == cnt) {
                    return meterInfo.name();
                }
            }
        }
        else if (obis == OBIS.CURRENT_MAX_DEMAND) {
            for (CURRENT_MAX_DEMAND demand : CURRENT_MAX_DEMAND.values()) {
                if (demand.getCode() == cnt) {
                    return demand.name();
                }
            }
        }
        else if (obis == OBIS.PREVIOUS_MAX_DEMAND) {
            for (PREVIOUS_MAX_DEMAND demand : PREVIOUS_MAX_DEMAND.values()) {
                if (demand.getCode() == cnt)
                    return demand.name();
            }
        }
        else if (obis == OBIS.MONTHLY_ENERGY_PROFILE) {
            for (MONTHLY_ENERGY_PROFILE profile : MONTHLY_ENERGY_PROFILE.values()) {
                if (profile.getCode() == cnt) {
                	log.debug("MONTHLY_ENERGY_PROFILE[" + profile.name() + "]");
                    return profile.name();
                }
            }
        }
        else if (obis == OBIS.MONTHLY_DEMAND_PROFILE) {
            for (MONTHLY_DEMAND_PROFILE profile : MONTHLY_DEMAND_PROFILE.values()) {
                if (profile.getCode() == cnt)
                    return profile.name();
            }
        }
        else if (obis == OBIS.POWER_FAILURE || obis == OBIS.POWER_RESTORE
                || obis == OBIS.TIME_CHANGE_FROM || obis == OBIS.TIME_CHANGE_TO
                || obis == OBIS.DEMAND_RESET || obis == OBIS.MANUAL_DEMAND_RESET
                || obis == OBIS.SELF_READ || obis == OBIS.PROGRAM_CHANGE
                || obis == OBIS.SAG || obis == OBIS.SWELL) {
            // 데이타 구조가 array, structure, date, cnt
            for (EVENT profile : EVENT.values()) {
                if (profile.getCode() == cnt)
                    return profile.name();
            }
            /*
            int mod = (cnt+1) % 3;
            int value = (cnt+1)/ 3;
            
            if (value >= 1) {
                switch (obis) {
                    case POWER_FAILURE :
                        if (mod == 0) return EVENT_LOG.PowerFailure.name()+value;
                        //else if (mod == 1) return EVENT_LOG.PowerFailureCnt.name()+value;
                        break;
                    case POWER_RESTORE :
                        if (mod == 0) return EVENT_LOG.PowerRestore.name()+value;
                        //else if (mod == 1) return EVENT_LOG.PowerRestoreCnt.name()+value;
                        break;
                    case TIME_CHANGE_FROM :
                        if (mod == 0) return EVENT_LOG.TimeChangeFrom.name()+value;
                        //else if (mod == 1) return EVENT_LOG.TimeChangeFromCnt.name()+value;
                        break;
                    case TIME_CHANGE_TO :
                        if (mod == 0) return EVENT_LOG.TimeChangeTo.name()+value;
                        //else if (mod == 1) return EVENT_LOG.TimeChangeToCnt.name()+value;
                        break;
                    case DEMAND_RESET :
                        if (mod == 0) return EVENT_LOG.DemandReset.name()+value;
                        //else if (mod == 1) return EVENT_LOG.DemandResetCnt.name()+value;
                        break;
                    case MANUAL_DEMAND_RESET :
                        if (mod == 0) return EVENT_LOG.ManualDemandReset.name()+value;
                        //else if (mod == 1) return EVENT_LOG.ManualDemandResetCnt.name()+value;
                        break;
                    case SELF_READ :
                        if (mod == 0) return EVENT_LOG.SelfRead.name()+value;
                        //else if (mod == 1) return EVENT_LOG.SelfReadCnt.name()+value;
                        break;
                    case PROGRAM_CHANGE :
                        if (mod == 0) return EVENT_LOG.ProgramChange.name()+value;
                        //else if (mod == 1) return EVENT_LOG.ProgramChangeCnt.name()+value;
                        break;
                }
            }
            */
        }
        else if (obis == OBIS.LOAD_PROFILE) {
            for (LOAD_PROFILE profile : LOAD_PROFILE.values()) {
                if (profile.getCode() == cnt)
                    return profile.name();
            }
        }
        else if (obis == OBIS.METER_TIME) {
    		for (METER_TIME meterTime : METER_TIME.values()) {
                if (meterTime.getCode() == cnt) {
                    return meterTime.name();
                }
            }
        }
        else if (obis == OBIS.POWER_QUALITY) {
            for (POWER_QUALITY pq : POWER_QUALITY.values()) {
                if (pq.getCode() == cnt)
                    return pq.name();
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
