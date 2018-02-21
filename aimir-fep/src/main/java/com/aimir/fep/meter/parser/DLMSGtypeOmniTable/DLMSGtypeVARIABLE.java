package com.aimir.fep.meter.parser.DLMSGtypeOmniTable;

public class DLMSGtypeVARIABLE {

    public enum OBIS {
    	
    	LP_COUNT("0100000100FF", "VZ"), // 검침횟수
    	
    	MANUFACTURER_METER_ID("0100000001FF", "Manufacturer Meter ID"),	// 전력량계ID
    	CUSTOMER_METER_ID("00002A0000FF","Customer Meter ID"),	// 계기 식별자
        ACTIVEPOWER_CONSTANT("0101000300FF","ActivePower Constant"),	// 유효전력량 계기 정수
        REACTIVEPOWER_CONSTANT("0101000301FF","ReactivePower Constant"),	// 무효전력량 계기 정수
        APPRENTPOWER_CONSTANT("0101000302FF","ApparentPower Constant"),	// 피상전력량 계기 정수
        METER_TIME("0000010000FF","Meter Time"),	// 전력량계 시간
        LP_CYCLE("0101000804FF","Lp Cycle"),	// LP 수집 주기
        MEASUREMENT_DATE("00000F0000FF","Measurement Date"),	// 정기검침일        
        CURRENT_ENERGY_PROFILE("0000620101FF","Monthly Energy Profile"),	// 전력량(현재검침)
        MONTHLY_ENERGY_PROFILE("000062010101","Monthly Energy Profile"),	// 전력량(전월검침)
        LOAD_PROFILE("0100630100FF", "load_profile"),	// 순방향
        MONTHLY_DEMAND_PROFILE("0000620102FF", "Monthly Demand Profile"), // 순방향 최대 수요전력(현월)
		POWER_FAILURE("0100636201FF", "Power Failure"), // 정전(Power failure)
		POWER_RESTORE("0100636202FF","Power Restore"),	// 복전(Power Power Restore)
		CURRENT_VOLTAGE("0101628000FF","Current Voltage"),	// 순시 전압/전류
		AVG_CURRENT_VOLTAGE("0100630D00FF","Avg Current Voltage"),	// 평균 전압/전류
		
		TDU_MEASURMENT_DATA("000000000000","TDU Measurement Data"),	// TDU 검침데이터
		METERINFO_DATA("0100000000FF","METER INFO"),	// 미터아이디
		METER_BEFORETIME("0000100000FF","METER BEFORE TIME"),	// before time
		METER_AFTERTIME("0000010000FF","METER AFTER TIME"),	// after time
		METER_RELAY_STATUS("010000F400FF","METER RELAY STAUTS"),	// meter relay status
		
		// 정기검침
		IMPORT_ACTIVE_ENERGY("0101010800FF","Import Active Energy"),	// 순방향 유효전력량[Q1+Q4]
		IMPORT_APPARENT_ENERGY("0101090800FF","Import Apparent Energy"),	// 순방향 피상전력량[Q1+Q4] 
		IMPORT_LAG_REACTIVE("0101050800FF","Import Lag Reactive"),	// 순방향 지상 무효전력량[Q1] 
		IMPORT_LEAD_REACTIVE("0101080800FF","Import Lead Reactive"),	// 순방향 진상 무효전력량[Q4]		
		IMPORT_POWER_FACTOR("01010D0900FF","Import Power Factor"),	// 순방향 평균 역률
		IMPORT_ACTIVE_ENERGY_A("0101010801FF","Import Active Energy A"),	// 순방향 유효전력량[Q1+Q4]
		IMPORT_APPARENT_ENERGY_A("0101090801FF","Import Apparent Energy A"),	// 순방향 피상전력량[Q1+Q4] 
		IMPORT_LAG_REACTIVE_A("0101050801FF","Import Lag Reactive A"),	// 순방향 지상 무효전력량[Q1] 
		IMPORT_LEAD_REACTIVE_A("0101080801FF","Import Lead Reactive A"),	// 순방향 진상 무효전력량[Q4] 
		IMPORT_POWER_FACTOR_A("01010D0901FF","Import Power Factor A"),	// 순방향 평균 역률		
		IMPORT_ACTIVE_ENERGY_B("0101010802FF","Import Active Energy B"),	// 순방향 유효전력량[Q1+Q4]
		IMPORT_APPARENT_ENERGY_B("0101090802FF","Import Apparent Energy B"),	// 순방향 피상전력량[Q1+Q4] 
		IMPORT_LAG_REACTIVE_B("0101050802FF","Import Lag Reactive B"),	// 순방향 지상 무효전력량[Q1] 
		IMPORT_LEAD_REACTIVE_B("0101080802FF","Import Lead Reactive B"),	// 순방향 진상 무효전력량[Q4] 
		IMPORT_POWER_FACTOR_B("01010D0902FF","Import Power Factor B"),	// 순방향 평균 역률		
		IMPORT_ACTIVE_ENERGY_C("0101010803FF","Import Active Energy C"),	// 순방향 유효전력량[Q1+Q4]
		IMPORT_APPARENT_ENERGY_C("0101090803FF","Import Apparent Energy C"),	// 순방향 피상전력량[Q1+Q4] 
		IMPORT_LAG_REACTIVE_C("0101050803FF","Import Lag Reactive C"),	// 순방향 지상 무효전력량[Q1] 
		IMPORT_LEAD_REACTIVE_C("0101080803FF","Import Lead Reactive C"),	// 순방향 진상 무효전력량[Q4] 
		IMPORT_POWER_FACTOR_C("01010D0903FF","Import Power Factor C"),	// 순방향 평균 역률		
		IMPORT_ACTIVE_ENERGY_D("0101010804FF","Import Active Energy D"),	// 순방향 유효전력량[Q1+Q4]
		IMPORT_APPARENT_ENERGY_D("0101090804FF","Import Apparent Energy D"),	// 순방향 피상전력량[Q1+Q4] 
		IMPORT_LAG_REACTIVE_D("0101050804FF","Import Lag Reactive D"),	// 순방향 지상 무효전력량[Q1] 
		IMPORT_LEAD_REACTIVE_D("0101080804FF","Import Lead Reactive D"),	// 순방향 진상 무효전력량[Q4] 
		IMPORT_POWER_FACTOR_D("01010D0904FF","Import Power Factor D"),	// 순방향 평균 역률
		
		// LP
		DATE_TIME("0000010000FF","date_time"),	// 일자/시간
		STATUS("0000616104FF","status"),	// 상태정보
		
		EXPORT_ACTIVE_ENERGY("0101020800FF","Export Active Energy"),	// 역방향 유효전력량[Q2+Q3]
		EXPORT_LAG_REACTIVE("0101060800FF","Export Lag Reactive"),	// 역방향 진상 무효전력량[Q2]
		EXPORT_LEAD_REACTIVE("0101070800FF","Export Lead Reactive"),	// 역방향 지상 무효전력량[Q3]
		EXPORT_APPARENT_ENERGY("01010A0800FF","Export Apparent Energy"),	// 역방향 피상전력량[Q2+Q3]
		
		// 최대수요전력		
		IMPORT_DMD_ACTIVE_ENERGY("0101010600FF","Import Dmd Active Energy"),	// 순방향 유효전력[Q1+Q4]		
		IMPORT_DMD_SUM_ACTIVE_ENERGY("0101010200FF","Import Dmd Sum Active Energy"),	// 누적 순방향 유효전력[Q1+Q4]
		IMPORT_DMD_APPARENT_ENERGY("0101090600FF","Import Dmd Apparent Energy"),	// 순방향 피상전력[Q1+Q4]
		IMPORT_DMD_SUM_APPARENT_ENERGY("0101090200FF","Import Dmd Sum Apparent Energy"),	// 누적 순방향 피상전력[Q1+Q4]
		
		IMPORT_DMD_ACTIVE_ENERGY_A("0101010601FF","Import Dmd Active Energy A"),	// 순방향 유효전력[Q1+Q4]		
		IMPORT_DMD_SUM_ACTIVE_ENERGY_A("0101010201FF","Import Dmd Sum Active Energy A"),	// 누적 순방향 유효전력[Q1+Q4]
		IMPORT_DMD_APPARENT_ENERGY_A("0101090601FF","Import Dmd Apparent Energy A"),	// 순방향 피상전력[Q1+Q4]
		IMPORT_DMD_SUM_APPARENT_ENERGY_A("0101090201FF","Import Dmd Sum Apparent Energy A"),	// 누적 순방향 피상전력[Q1+Q4]
		
		IMPORT_DMD_ACTIVE_ENERGY_B("0101010602FF","Import Dmd Active Energy B"),	// 순방향 유효전력[Q1+Q4]		
		IMPORT_DMD_SUM_ACTIVE_ENERGY_B("0101010202FF","Import Dmd Sum Active Energy B"),	// 누적 순방향 유효전력[Q1+Q4]
		IMPORT_DMD_APPARENT_ENERGY_B("0101090602FF","Import Dmd Apparent Energy B"),	// 순방향 피상전력[Q1+Q4]
		IMPORT_DMD_SUM_APPARENT_ENERGY_B("0101090202FF","Import Dmd Sum Apparent Energy B"),	// 누적 순방향 피상전력[Q1+Q4]
		
		IMPORT_DMD_ACTIVE_ENERGY_C("0101010603FF","Import Dmd Active Energy C"),	// 순방향 유효전력[Q1+Q4]		
		IMPORT_DMD_SUM_ACTIVE_ENERGY_C("0101010203FF","Import Dmd Sum Active Energy C"),	// 누적 순방향 유효전력[Q1+Q4]
		IMPORT_DMD_APPARENT_ENERGY_C("0101090603FF","Import Dmd Apparent Energy C"),	// 순방향 피상전력[Q1+Q4]
		IMPORT_DMD_SUM_APPARENT_ENERGY_C("0101090203FF","Import Dmd Sum Apparent Energy C"),	// 누적 순방향 피상전력[Q1+Q4]
		
		IMPORT_DMD_ACTIVE_ENERGY_D("0101010604FF","Import Dmd Active Energy D"),	// 순방향 유효전력[Q1+Q4]		
		IMPORT_DMD_SUM_ACTIVE_ENERGY_D("0101010204FF","Import Dmd Sum Active Energy D"),	// 누적 순방향 유효전력[Q1+Q4]
		IMPORT_DMD_APPARENT_ENERGY_D("0101090604FF","Import Dmd Apparent Energy D"),	// 순방향 피상전력[Q1+Q4]
		IMPORT_DMD_SUM_APPARENT_ENERGY_D("0101010204FF","Import Dmd Sum Apparent Energy D"),	// 누적 순방향 피상전력[Q1+Q4]
		
		POWER_FAILURE_NO("0000600700FF","Power Failure No"),	// 정전 횟수
		
		CURR_A("01001F0700FF","Current A"),	// A상 순시 전류 
		VOL_A("0100200700FF","Voltage A"),	// A상 순시 전압
		VOL_THD_A("010020077CFF","VOL THD A"),	// A상 순시 전압 THD 
		VOL_PF_A("0100210700FF","VOL PF A"),	// A상 순시 역률 
		VOL_CURR_ANGLE_A("0100510728FF","VOL PHASE ANGLE A"),	// A상 전압-전류 위상각
		
		CURR_B("0100330700FF","Current B"),	// B상 순시 전류 
		VOL_B("0100340700FF","Voltage B"),	// B상 순시 전압
		VOL_THD_B("010034077CFF","VOL THD B"),	// B상 순시 전압 THD 
		VOL_PF_B("0100350700FF","VOL PF B"),	// B상 순시 역률 
		VOL_CURR_ANGLE_B("0100510733FF","VOL CURR ANGLE B"),	// B상 전압-전류 위상각
		
		CURR_C("0100470700FF","Current C"),	// C상 순시 전류 
		VOL_C("0100480700FF","Voltage C"),	// C상 순시 전압
		VOL_THD_C("010048077CFF","VOL THD C"),	// C상 순시 전압 THD 
		VOL_PF_C("0100490700FF","VOL PF C"),	// C상 순시 역률 
		VOL_CURR_ANGLE_C("010051073EFF ","VOL CURR ANGLE C"),	// C상 전압-전류 위상각
		
		VOL_ANGLE_AB("010051070AFF","VOL_ANGLE_AB"),	// 전압 A상-전압 B상 위상각
		VOL_ANGLE_AC("0100510714FF","VOL_ANGLE_AC"),	//전압 A상-전압 C상 위상각
		TEMPERATURE("0000600900FF","TEMPERATURE"),	// 현재온도
		
		LINE_AB("0100208000FF","LINE AB"),	// A-B상 평균전압
		AVG_CURR_A("01001F0500FF","AVG CURR A"),	// A상 평균전류
		LINE_BC("0100348000FF","LINE BC"),	// B-C상 평균전압 
		AVG_CURR_B("0100330500FF","AVG CURR B"),	// B상 평균전류
		LINE_CA("0100488000FF","LINE CA"),	// C-A상 평균전압 
		AVG_CURR_C("0100470500FF","AVG CURR C");	// C상 평균전류
				
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
    
    public enum METER_INFORMATION {
        COSEM_ID,
        MANUFACTURE_DATE,
    	FW_VERSION;
    }

    public enum METER_CONSTANT {
        ActiveC,
        ReactiveC;
    }
    
    public enum DLMS_CLASS {
    	TDU_DATA(0),
    	DATA(1),
        REGISTER(3),
        EXTEND_REGISTER(4),
        DEMAND_REGISTER(5),
        REGISTER_ACTIVATION(6),
        PROFILE_GENERIC(7),
        CLOCK(8),
        SCRIPT_TABLE(9),
        SCHEDULE(10),
        SPECIAL_DAY(11),
        ACTIVITY_CALENDAR(20),
        ASSOCIATION_LN(15),
        ASSOCIATION_SN(12),
        REGISTER_MONITOR(21),
        SAP_ASSIGN(17),
        UTILITY_TABLE(26),
        SINGLE_ACTION_SCHEDULE(22),
        RELAY_CLASS(70),
    	LIMITER_CLASS(71),
    	MBUS_CLIENT_CLASS(72);
        
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
        REGISTER_ATTR04(4),        // status
        PROFILE_GENERIC_ATTR02(2), // buffer
        PROFILE_GENERIC_ATTR03(3),        // capture_objects array
        PROFILE_GENERIC_ATTR04(4),        // value
        PROFILE_GENERIC_ATTR07(7), // entries in use 
        CLOCK_ATTR01(1),//logical name
        CLOCK_ATTR02(2),//time
        CLOCK_ATTR03(3),//time_zone
        CLOCK_ATTR04(4),//status
        CLOCK_ATTR05(5),//dst_start
        CLOCK_ATTR06(6),//dst end
        CLOCK_ATTR07(7),//dst deviation
        CLOCK_ATTR08(8),//dst enable
        CLOCK_ATTR09(9),//clock base
        SCRIPT_TABLE_ATTR01(1),
        SCRIPT_TABLE_ATTR02(2),
    	SINGLE_ACTION_SCHEDULE_ATTR04(4), // execution_time, array
    	TDU_ATTR01(0);

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
    
    public static String getDataName(OBIS obis, int cnt, int structure) {
    	
    	if (obis == OBIS.LOAD_PROFILE) {  // LP          
    		for (LOAD_PROFILE profile : LOAD_PROFILE.values()) {
                if (profile.getCode() == (cnt % 7)) 
                    return profile.name();
            }
        } else if (obis == OBIS.CURRENT_ENERGY_PROFILE) { // 현재검침
        	for (CURRENT_ENERGY_PROFILE profile : CURRENT_ENERGY_PROFILE.values()) {        		
                if (profile.getCode() == (cnt % 26)) 
                    return profile.name();
            }
        } else if (obis == OBIS.MONTHLY_ENERGY_PROFILE) { // 정기검침
        	for (MONTHLY_ENERGY_PROFILE profile : MONTHLY_ENERGY_PROFILE.values()) {        		
                if (profile.getCode() == (cnt % 26)) 
                    return profile.name();
            }        	
        } else if (obis == OBIS.MONTHLY_DEMAND_PROFILE) { // 순방향 최대수요전력
        	for (MONTHLY_DEMAND_PROFILE profile : MONTHLY_DEMAND_PROFILE.values()) {
                if (profile.getCode() == (cnt % 31)) 
                    return profile.name();
            }	
        } else if (obis == OBIS.POWER_FAILURE || obis == OBIS.POWER_RESTORE) { // 정전 복전
        	for (EVENT profile : EVENT.values()) {
                if (profile.getCode() == (cnt % 3)) 
                    return profile.name();
            }
        } else if (obis == OBIS.CURRENT_VOLTAGE) {	// 순시 전압/전류
        	for (POWER_QUALITY_PROFILE profile : POWER_QUALITY_PROFILE.values()) {
                if (profile.getCode() == (cnt % 19)) 
                    return profile.name();
            }
        } else if (obis == OBIS.AVG_CURRENT_VOLTAGE) {	// 평균 전압/전류
        	for (POWER_QUALITY_AVG_PROFILE profile : POWER_QUALITY_AVG_PROFILE.values()) {
                if (profile.getCode() == (cnt % 11)) 
                    return profile.name();
            }
        } else if (obis == OBIS.MEASUREMENT_DATE) {	// 정기검침일
        	for (MEASUREMENT_DATE_INFO profile : MEASUREMENT_DATE_INFO.values()) {
                if (profile.getCode() == cnt) 
                    return profile.name();
            }
        }  else if (obis == OBIS.TDU_MEASURMENT_DATA) {	// TDU
        	for (TDU_PROFILE profile : TDU_PROFILE.values()) {
                if (profile.getCode() == (cnt % 35)) 
                    return profile.name();
            }
        }  	 	
    	return UNDEFINED;
    }
  
    // todo 변경
    public enum LOAD_PROFILE {    	
    	
    	Structure(0, "Structure"),              
        ImportActiveEnergy(1, "importActiveEnergy"),	// 순방향 유효
        ImportLagReactive(2, "importLagReactive"),	// 순방향 지상 무효
        ImportLeadReactive(3, "importLeadReactive"),	// 순방향 진상 무효
        ImportApprentEnergy(4, "importApprentEnergy"),	// 순방향 피상전력   
        Date(5, "Date"),
        Status(6, "Status"),	// 역방향 피상전력
    	PersianDate(7, "PersianDate"); // 페르시안날짜
    	
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
    

    public enum LOAD_STATUS_PROFILE {    
    	// BYTE0
    	SR(7, "SR"),	// SR
        POWER_FAILURE(6, "POWER_FAILURE"),	//정전
        TIME_CHANGE(5, "TIME_CHANGE"),	//시각변경
        MANUAL_MEATERING(4, "MANUAL_MEATERING"),	//수동검침
        DR(3, "DR"),	//DR
        BATTERY_OFF(2, "DR"),	//BATTERY없음
        VOL_IMANGE(1, "VOL_IMANGE"),	//전압결상
        PROGRAM_CHG(0, "PROGRAM_CHG"),	//프로그램변경
        
        // BYTE1
        SAG_SWELL(15, "SAG_SWELL"),	// sag/swell
        LINE(14, "LINE"),	//오결선/중성선
        TEMPERATURE(13, "TEMPERATURE"),	//TEMPERATURE
        DST(12, "DST"),	//DST
        MAGNETIC_DETECTION(11, "MAGNETIC_DETECTION"),	//자계감지
        CURRENT_INTERRUPT(10, "CURRENT_INTERRUPT"),	//전류제한차단
        TARRIFF1(9, "TARRIFF1"),	//tarriff
        TARRIFF2(8, "TARRIFF1"),	
        
        // BYTE2
        Overcurrent(23, "Overcurrent"),	// 과전류
        reserved1(22, "reserved1"),	//
        reserved2(21, "reserved2"),	//
        ModemCover(20, "ModemCover"),	//Modem Cover
        TerminalCover(19, "TerminalCover"),	//TerminalCover
        Latch_ON(18, "Latch_ON"),	//Latch_ON
        Latch_OFF(17, "Latch_OFF"),	//Latch_OFF
        Latch_Error(16, "TARRIFF1"), //Latch_Error
    	
    	STATUS(999, "STATUS");	// STATUS
        
        private int code;
        private String name;
        
        LOAD_STATUS_PROFILE(int code, String name) {
            this.code = code;
            this.name = name;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public String getName(){
            return this.name;
        }
        
        public static LOAD_STATUS_PROFILE getObis(int code) {
            for (LOAD_STATUS_PROFILE obis : values()) {
                if (obis.getCode() == code) return obis;
            }
            return null;
        }
    }
    
    public enum POWER_QUALITY_PROFILE {
    	
    	Structure(0, "Structure"),    			    	
    	CURR_A(1, "current A"),	//순시 전류
    	VOL_A(2, "Voltage A"),	// 순시 전압
    	VOL_THD_A(3, "Voltage threshold A"),	// 순시전압 thd
    	PF_A(4, "Power Factor A"),	//순시 역률
    	VOL_ANGLE_A(5, "Current Angle A"),	// 위상각
    	CURR_B(6, "current B"),	//순시 전류
    	VOL_B(7, "Voltage B"),	// 순시 전압
    	VOL_THD_B(8, "Voltage threshold B"),	// 순시전압 thd
    	PF_B(9, "Power Factor B"),	//순시 역률
    	VOL_ANGLE_B(10, "Current Angle B"),	// 위상각    	
    	CURR_C(11, "current C"),	//순시 전류
    	VOL_C(12, "Voltage C"),	// 순시 전압
    	VOL_THD_C(13, "Voltage threshold C"),	// 순시전압 thd
    	PF_C(14, "Power Factor C"),	//순시 역률
    	VOL_ANGLE_C(15, "Current Angle C"),	// 위상각    	
    	PH_FUND_VOL_A(16, "Voltage Angle AB"),
    	PH_FUND_VOL_B(17, "Voltage Angle AC"),
    	TEMPERATURE(18, "Temperature");
    	
        private int code;
        private String name;
        
        POWER_QUALITY_PROFILE(int code, String name) {
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
    
    public enum POWER_QUALITY_AVG_PROFILE {
    	
    	Structure(0, "Structure"),
    	Date(1, "Date"),			    	
    	LINE_AB(2, "Line AB"),	// A-B상 평균전압
    	VOL_THD_A(3, "VOL_THD_A"),	// A상 순시전압THD
    	CURR_HARMONIC_A(4, "Current Harmonic A"),	// A상 평균전류    	
    	LINE_BC(5, "Line BC"),    		// B-C상 평균전압
    	VOL_THD_B(6, "VOL_THD_B"),	// A상 순시전압THD
    	CURR_HARMONIC_B(7, "Current Harmonic B"),	// B상 평균전류
    	LINE_CA(8, "Line CA"),    		// C-A상 평균전압
    	VOL_THD_C(9, "Line AB"),	// A상 순시전압THD
    	CURR_HARMONIC_C(10, "Current Harmonic C");	// C상 평균전류
    	
        private int code;
        private String name;
        
        POWER_QUALITY_AVG_PROFILE(int code, String name) {
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
        
    	EventTime(1, "Event Time"),
        EventCount(2, "Event Count");
        
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
    
    /**
     * CURRENT_ENERGY_PROFILE (현재검침)
     * @author NURI
     */
    public enum CURRENT_ENERGY_PROFILE {
    	
    	ImportActiveEnergy(1, "Import Active Energy"),			//순방향 유효전력량[Q1+Q4]
    	ImportApparentEnergy(2, "Import Apparent Energy"),		//순방향 피상전력량[Q1+Q4] 	
    	ImportLagReactive(3, "Import Lag Reactive"),			//순방향 지상 무효전력량[Q1] 
    	ImportLeadReactive(4, "Import Lead Reactive"),			//순방향 진상 무효전력량[Q4] 
    	ImportPowerFactor(5, "Import Power Factor"),			//순방향 평균 역률
    	ImportActiveEnergyA(6, "Import Active Energy A"),
    	ImportApparentEnergyA(7, "Import Apparent Energy A"),
    	ImportLagReactiveA(8, "Import Lag Reactive A"),
    	ImportLeadReactiveA(9, "Import Lead Reactive A"),
    	ImportPowerFactorA(10, "Import Power Factor A"),
    	ImportActiveEnergyB(11, "Import Active Energy B"),
    	ImportApparentEnergyB(12, "Import Apparent Energy B"),
    	ImportLagReactiveB(13, "Import Lag Reactive B"),
    	ImportLeadReactiveB(14, "Import Lead Reactive B"),
    	ImportPowerFactorB(15, "Import Power Factor B"),
    	ImportActiveEnergyC(16, "Import Active Energy C"),
    	ImportApparentEnergyC(17, "Import Apparent Energy C"),
    	ImportLagReactiveC(18, "Import Lag Reactive C"),
    	ImportLeadReactiveC(19, "Import Lead Reactive C"),
    	ImportPowerFactorC(20, "Import Power Factor C"),
    	ImportActiveEnergyD(21, "Import Active Energy D"),
    	ImportApparentEnergyD(22, "Import Apparent Energy D"),
    	ImportLagReactiveD(23, "Import Lag Reactive D"),
    	ImportLeadReactiveD(24, "Import Lead Reactive D"),
    	ImportPowerFactorD(25, "Import Power Factor D");
        
        private int code;
        private String name;
                
        CURRENT_ENERGY_PROFILE(int code, String name) {
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
    
    /**
     * MONTHLY_ENERGY_PROFILE (정기검침)
     * @author NURI
     */
    public enum MONTHLY_ENERGY_PROFILE {
    	
    	ImportActiveEnergy(1, "Import Active Energy"),			//순방향 유효전력량[Q1+Q4]
    	ImportApparentEnergy(2, "Import Apparent Energy"),		//순방향 피상전력량[Q1+Q4] 	
    	ImportLagReactive(3, "Import Lag Reactive"),			//순방향 지상 무효전력량[Q1] 
    	ImportLeadReactive(4, "Import Lead Reactive"),			//순방향 진상 무효전력량[Q4] 
    	ImportPowerFactor(5, "Import Power Factor"),			//순방향 평균 역률
    	ImportActiveEnergyA(6, "Import Active Energy A"),
    	ImportApparentEnergyA(7, "Import Apparent Energy A"),
    	ImportLagReactiveA(8, "Import Lag Reactive A"),
    	ImportLeadReactiveA(9, "Import Lead Reactive A"),
    	ImportPowerFactorA(10, "Import Power Factor A"),
    	ImportActiveEnergyB(11, "Import Active Energy B"),
    	ImportApparentEnergyB(12, "Import Apparent Energy B"),
    	ImportLagReactiveB(13, "Import Lag Reactive B"),
    	ImportLeadReactiveB(14, "Import Lead Reactive B"),
    	ImportPowerFactorB(15, "Import Power Factor B"),
    	ImportActiveEnergyC(16, "Import Active Energy C"),
    	ImportApparentEnergyC(17, "Import Apparent Energy C"),
    	ImportLagReactiveC(18, "Import Lag Reactive C"),
    	ImportLeadReactiveC(19, "Import Lead Reactive C"),
    	ImportPowerFactorC(20, "Import Power Factor C"),
    	ImportActiveEnergyD(21, "Import Active Energy D"),
    	ImportApparentEnergyD(22, "Import Apparent Energy D"),
    	ImportLagReactiveD(23, "Import Lag Reactive D"),
    	ImportLeadReactiveD(24, "Import Lead Reactive D"),
    	ImportPowerFactorD(25, "Import Power Factor D");
        
        private int code;
        private String name;
                
        MONTHLY_ENERGY_PROFILE(int code, String name) {
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
    /**
     * MONTHLY_DEMAND_PROFILE (최대수요전력)
     * @author NURI
     */
    public enum MONTHLY_DEMAND_PROFILE {
    	
    	ImportDmdActiveEnergy(1, "Import Dmd Active Energy"),			// 순방향 유효전력[Q1+Q4]
    	ImportDmdActiveEnergyDate(2, "Import Dmd Active Energy Date"),	// 발생 일자/시간(최대순방향유효) 
    	ImportDmdSumActiveEnergy(3, "Import Dmd Sum Active Energy"),	// 누적 순방향 유효전력[Q1+Q4]  
    	ImportDmdApparentEnergy(4, "Import Dmd Apparent Energy"),	// 순방향 피상전력[Q1+Q4] 
    	ImportDmdApparentEnergyDate(5, "Import Dmd Apparent Energy Date"),	// 발생 일자/시간(최대순방향피상) 
    	ImportDmdSumApparentEnergy(6, "Import Dmd Sum Apparent Energy"),	// 누적 순방향 피상전력[Q1+Q4]     	
    	ImportDmdActiveEnergyA(7, "Import Dmd Active Energy A"),
    	ImportDmdActiveEnergyDateA(8, "Import Dmd Active Energy Date A"),
    	ImportDmdSumActiveEnergyA(9, "Import Dmd Sum Active Energy A"),
    	ImportDmdApparentEnergyA(10, "Import Dmd Apparent Energy A"),
    	ImportDmdApparentEnergyDateA(11, "Import Dmd Apparent Energy Date A"),
    	ImportDmdSumApparentEnergyA(12, "Import Dmd Sum Apparent Energy A"),    	
    	ImportDmdActiveEnergyB(13, "Import Dmd Active Energy B"),
    	ImportDmdActiveEnergyDateB(14, "Import Dmd Active Energy Date B"),
    	ImportDmdSumActiveEnergyB(15, "Import Dmd Sum Active Energy B"),
    	ImportDmdApparentEnergyB(16, "Import Dmd Apparent Energy B"),
    	ImportDmdApparentEnergyDateB(17, "Import Dmd Apparent Energy Date B"),
    	ImportDmdSumApparentEnergyB(18, "Import Dmd Sum Apparent Energy B"),    	
    	ImportDmdActiveEnergyC(19, "Import Dmd Active Energy C"),
    	ImportDmdActiveEnergyDateC(20, "Import Dmd Active Energy Date C"),
    	ImportDmdSumActiveEnergyC(21, "Import Dmd Sum Active Energy C"),
    	ImportDmdApparentEnergyC(22, "Import Dmd Apparent Energy C"),
    	ImportDmdApparentEnergyDateC(23, "Import Dmd Apparent Energy Date C"),
    	ImportDmdSumApparentEnergyC(24, "Import Dmd Sum Apparent Energy C"),    	
    	ImportDmdActiveEnergyD(25, "Import Dmd Active Energy D"),
    	ImportDmdActiveEnergyDateD(26, "Import Dmd Active Energy Date D"),
    	ImportDmdSumActiveEnergyD(27, "Import Dmd Sum Active Energy D"),
    	ImportDmdApparentEnergyD(28, "Import Dmd Apparent Energy D"),
    	ImportDmdApparentEnergyDateD(29, "Import Dmd Apparent Energy Date D"),
    	ImportDmdSumApparentEnergyD(30, "Import Dmd Sum Apparent Energy D");
        
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
    
    
    /**
     * TDU_PROFILE (TDU)
     * @author NURI
     */
    public enum TDU_PROFILE {
    	
    	RTime(1, "RTime"),
    	VolA(2, "Vol A"),
    	MAXVolA(3, "MAX Vol A"),
    	MINVolA(4, "MIN Vol A"),
    	VolB(5, "Vol B"),
    	MAXVolB(6, "MAX Vol B"),
    	MINVolB(7, "MIN Vol B"),
    	VolC(8, "Vol C"),
    	MAXVolC(9, "MAX Vol C"),
    	MINVolC(10, "MIN Vol C"),
    	CurA(11, "Cur A"),
    	MAXCurA(12, "MAX Cur A"),
    	MINCurA(13, "MIN Cur A"),
    	CurB(14, "Cur B"),
    	MAXCurB(15, "MAX CurB"),
    	MINCurB(16, "MIN CurB"),
    	CurC(17, "Cur C"),
    	MAXCurC(18, "MAX Cur C"),
    	MINCurC(19, "MIN Cur C"),
    	UseRateA(20, "Use Rate A"),
    	UseRateB(21, "Use Rate B"),
    	UseRateC(22, "Use Rate C"),
    	APT(23, "APT"),
    	RPT(24, "RPT"),
    	PFT(25, "PFT"),
    	APTA(26, "APTA"),
    	RPTA(27, "RPTA"),
    	RFTA(28, "RFTA"),
    	APTB(29, "APTB"),
    	RPTB(30, "RPTB"),
    	RFTB(31, "RFTB"),
    	APTC(32, "APTC"),
    	RPTC(33, "RPTC"),
    	RFTC(34, "RFTC");
        
        private int code;
        private String name;
                
        TDU_PROFILE(int code, String name) {
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
    
    public enum MEASUREMENT_DATE_INFO {
    				    	
    	TIME(2, "TIME"),	// 시간
    	DATE(3, "DATE");	// 날짜
    	
        private int code;
        private String name;
        
        MEASUREMENT_DATE_INFO(int code, String name) {
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
