package com.aimir.fep.meter.parser.DLMSMoeTable;

public class DLMSVARIABLE {

    public enum OBIS {
    	/*  Meter Information */
        WEAK_LQI_VALUE("00001D0200FF","G3 PLC LQI VALUE"),
        DEVICE_INFO("0000600100FF", "Device Information"),        
        MANUFACTURE_SERIAL("0000600101FF","Manufacturer serial"),
        METER_TIME("0000010000FF", "Meter Time"),
        METER_MODEL("0000600103FF","Meter Model"),
        PHASE_TYPE("0100000204FF","Meter Phase Type"),
        LOGICAL_NUMBER("00002A0000FF","Logical Device Number"),
        FW_VERSION("0101000200FF","Firmware Version"),
        //FW_VERSION1("0100000200FF","Firmware Version1"),
        //HDLC_SETUP("0000160000FF","HDLC Setup"),
        //SERVICEPOINT_SERIAL("0000600102FF","SP serial number"),
        //CT_RATIO_NUM("0100000402FF","CT Ratio Number"),
        //VT_RATIO_NUM("0100000403FF","VT Ratio Number"),
        //CT_RATIO_DEN("0100000405FF","CT Ratio Den"),
        //VT_RATIO_DEN("0100000406FF","VT Ratio Den"),          
        //OVERAL_TRANS_NUM("0100000404FF","Overall transformer ratio (num)"),
        
        /* Load Profile */
        ENERGY_LOAD_PROFILE("0100630100FF","Energy Load Profile"),        
        DAILY_LOAD_PROFILE("0100620101FF","Daily load profile"),
        MONTHLY_BILLING("0000620100FF","Monthly biling event log"),        
        //MONTHLY_ENERGY_PROFILE("0000620101FF", "Monthly Energy Profile"),
        //MONTHLY_DEMAND_PROFILE("0000620102FF", "Monthly Demand Profile"),         

        /* Meter Alarm Information */
		MEASUREMENT_STATUS("0000600A07FF","Measurement Status"),
		DRIVE_STATUS("0000600A06FF","Drive status"),
        METER_STATUS("0000600A05FF","Meter Status"),
        RELAY_STATUS("000060030AFF","Relay Status"),  //0.0.96.3.10.255 Disconnect/reconnect 70 RW
        //FUNCTION_STATUS("","Function Status"), //TODO SET        
        //EXTERNAL_RELAY_STATUS("000360030AFF","External Relay Status"), //0.3.96.3.10.255 External Disconnect/reconnect
        
        /* Meter Event */
        STANDARD_EVENT("0000636200FF","Standard Event"), //0.0.99.98.0.255
        FRAUDDETECTIONLOGEVENT("0000636201FF","Fraud detection Log event"),//0.0.99.98.1.255        
        RELAY_EVENT("0000636202FF","Disconnector control log"), //0.0.99.98.2.255    
        MEASUREMENT_EVENT("0100630102FF", "Measurement event log"), //1.0.99.1.2.255
    	//TAMPER_EVENT("0100636215FF","Tamper event log"),        //1.0.99.98.21.255    	
    	//TIME_CHANGE_BEFORE("0100636203FF", "Time Change Before"),
    	//TIME_CHANGE_AFTER("0100636204FF", "Time Change After"),
    	//MANUAL_DEMAND_RESET("0100636206FF", "Manual Demand Reset"),
    	//POWER_FAILURE("0100636100FF","Power Failure"),
    	//POWER_RESTORE("0100636202FF","Power Restore"),
    	//BATTERY_FAILURE("0100636224FF","Battery failure event"),
        //RELAY_CONTROL_SCRIPT("00000A006AFF","Relay Status"),//0.0.10.0.106.255 Disconnect control script table 9 RW

        /* Channel Information */                
        VOLTAGE_L1("0100200700FF","L1 voltage"),
        VOLTAGE_L2("0100340700FF","L2 voltage"),
        VOLTAGE_L3("0100480700FF","L3 voltage"),
        CURRENT_L1("01001F0700FF","L1 current"),
        CURRENT_L2("0100330700FF","L2 current"),
        CURRENT_L3("0100470700FF","L3 current"),
        POWER_FACTOR_L1("0100210700FF","L1 power factor"),
        POWER_FACTOR_L2("0100350700FF","L2 power factor"),
        POWER_FACTOR_L3("0100490700FF","L3 power factor"),      
        ACTIVEPOWER_IMPORT("0100010700FF","Active Power Import"),
        ACTIVEPOWER_EXPORT("0100020700FF","Active Power Export"),
        REACTIVEPOWER_IMPORT("0100030700FF","Reactive Power Import"),
        REACTIVEPOWER_EXPORT("0100040700FF","Reactive Power Export"),        
        TOTAL_IMPORT_APPARENT_POWER("0100090700FF", "Total import apparent power (QI+QIV)"),
        TOTAL_EXPORT_APPARENT_POWER("01000A0700FF", "Total export apparent power (QII+QIII)"), 
        LASTMONTH_ACTIVEENERGY_IMPORT1("010001080001","Last month total import active energy (QI+QIV)1"),
        LASTMONTH_ACTIVEENERGY_IMPORT2("010001080065","Last month total import active energy (QI+QIV)2"),
        LASTMONTH_ACTIVEENERGY_EXPORT1("010002080001","Last month total export active energy (QII+QIII)1"),
        LASTMONTH_ACTIVEENERGY_EXPORT2("010002080065","Last month total export active energy (QII+QIII)2"),
        LASTMONTH_REACTIVEENERGY_IMPORT1("010003080001","Last month total import reactive energy (QI+QII)1"),
        LASTMONTH_REACTIVEENERGY_IMPORT2("010003080065","Last month total import reactive energy (QI+QII)2"),
        LASTMONTH_REACTIVEENERGY_EXPORT1("010004080001","Last month total export reactive energy (QIII+QIV)1"),
        LASTMONTH_REACTIVEENERGY_EXPORT2("010004080065","Last month total export reactive energy (QIII+QIV)2"),
        CUMULATIVE_ACTIVEENERGY_IMPORT1("0100010800FF","Cumulative active energy -import1"),
        CUMULATIVE_ACTIVEENERGY_IMPORT2("0100010800FF","Cumulative active energy -import2"),        
        CUMULATIVE_ACTIVEENERGY_EXPORT1("0100020800FF","Cumulative active energy -export1"),
        CUMULATIVE_ACTIVEENERGY_EXPORT2("0100020800FF","Cumulative active energy -export2"),        
        CUMULATIVE_REACTIVEENERGY_IMPORT1("0100030800FF","Cumulative reactive energy -import1"),
        CUMULATIVE_REACTIVEENERGY_IMPORT2("0100030800FF","Cumulative reactive energy -import2"),        
        CUMULATIVE_REACTIVEENERGY_EXPORT1("0100040800FF","Cumulative reactive energy -export1"),
        CUMULATIVE_REACTIVEENERGY_EXPORT2("0100040800FF","Cumulative reactive energy -export2"),        
        TOTAL_ACTIVEENERGY_IMPORT("01000F0800FF","Total energy +A"),
        TOTAL_ACTIVEENERGY_EXPORT("0100100800FF","Total energy -A"),
        TOTAL_MAX_ACTIVEDEMAND_IMPORT("0100010600FF","Total max demand +A"),
        TOTAL_MAX_ACTIVEDEMAND_EXPORT("0100020600FF","Total max demand -A"),
        TOTAL_MAX_REACTIVEDEMAND_IMPORT("0100030600FF","Total max demand +R"),
        TOTAL_MAX_REACTIVEDEMAND_EXPORT("0100040600FF","Total max demand -R"),
        TOTAL_CUM_ACTIVEDEMAND_IMPORT  ("0100010200FF","Total cumulative demand +A"),
        TOTAL_CUM_ACTIVEDEMAND_EXPORT  ("0100020200FF","Total cumulative demand -A"),
        TOTAL_CUM_REACTIVEDEMAND_IMPORT("0100030200FF","Total cumulative demand +R"),
        TOTAL_CUM_REACTIVEDEMAND_EXPORT("0100040200FF","Total cumulative demand -R");


        //CLOCK("0000010000FF","Clock"),
    	
    	
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
    
    /**
     * Drive(Device) Alarm Message
     * @author simhanger
     *
     */
    public enum DEVICE_STATUS_ALARM{
    	EPROMError(101,"EPROM error"),
    	ClockError(102,"Clock error"),
    	BatteryError(103,"Battery error"),
    	ReadCardError(104,"Read card error"),
    	DataAbnormal(105,"Data abnormal"),
    	// 2016.03.15 추가분 필요시 주석처리.
    	ExternalBatteryStatus(106, "External Battery Status"),
    	HighLowLevelInput(107, "High low level input"),
    	VoltageDetectInput(108, "Voltage detect input");

    	
        private int flag;
        private String msg;
        
        DEVICE_STATUS_ALARM(int flag, String msg) {
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
    
    /**
     * Function Alarm Message
     * @author simhanger
     *
     */
    public enum FUNCTION_STATUS_ALARM{
    	L1RelayError(205,"L1 relay error"),
    	L2RelayError(206,"L2 relay error"),
    	L3RelayError(207,"L3 relay error"),
    	ExternalRelayError(208,"External relay error"),
    	OpenTerminalCover(209,"Open terminal cover"),
    	OpenTerminalCoverInPowerOff(210,"Open terminal cover in power off"),    	
    	OpenTopCover(211,"Open top cover"),
    	OpenTopCoverInPowerOff(212,"Open top cover in power off"),    	
    	MagneticDetection1(213,"Magnetic detection 1"),
    	MagneticDetection2(214,"Magnetic detection 2"),
    	
    	
    	// 2016.03.15 추가분 필요시 주석처리.
    	L1RelayStatus(201, "L1 relay status"),
    	L2RelayStatus(202, "L2 relay status"),
    	L3RelayStatus(203, "L3 relay status"),
    	ExternalRelayStatus(204, "External relay status"),
    	Program(215 , "program"),
    	FactoryStatus(216,"Factory status");
    	
        private int flag;
        private String msg;
        
        FUNCTION_STATUS_ALARM(int flag, String msg) {
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

    /**
     * Measurement Alarm Message
     * @author simhanger
     *
     */
    public enum MEASUREMENT_STATUS_ALARM{    	
    	L1VoltageLoss(301,"L1 voltage loss"),
    	L2VoltageLoss(302,"L2 voltage loss"),
    	L3VoltageLoss(303,"L3 voltage loss"),
    	L1CurrentLoss(304,"L1 current loss"),
    	L2CurrentLoss(305,"L2 current loss"),
    	L3CurrentLoss(306,"L3 current loss"),
    	L1VoltageCut(307,"L1 voltage cut"),
    	L2VoltageCut(308,"L2 voltage cut"),
    	L3VoltageCut(309,"L3 voltage cut"),
    	VoltageReversePhaseSequence(310,"Voltage reverse phase sequence"),
    	CurrentReversePhaseSequence(311,"Current reverse phase sequence"),
    	VoltageAsymmetric(312,"Voltage asymmetric"),
    	CurrentAsymmetric(313,"Current asymmetric"),
    	L1OverCurrent(314,"L1 over current"),
    	L2OverCurrent(315,"L2 over current"),
    	L3OverCurrent(316,"L3 over current"),
    	L1CurrentCut(317,"L1 current cut"),
    	L2CurrentCut(318,"L2 current cut"),
    	L3CurrentCut(319,"L3 current cut"),
    	L1OverVoltage(320,"L1 over voltage"),
    	L2OverVoltage(321,"L2 over voltage"),
    	L3OverVoltage(322,"L3 over voltage"),
    	L1UnderVoltage(323,"L1 under voltage"),
    	L2UnderVoltage(324,"L2 under voltage"),
    	L3UnderVoltage(325,"L3 under voltage"),
    	AllPhasesVoltageLoss(326,"All phases voltage loss"),
    	L1OverLoad(327,"L1 over load"),
    	L2OverLoad(328,"L2 over load"),
    	L3OverLoad(329,"L3 over load"),
    	TotalPowerFactorExceeded(330,"Total power factor exceeded"),
    	L1VoltageSuperTopLimit(331,"L1 voltage super top limit"),
    	L2VoltageSuperTopLimit(332,"L2 voltage super top limit"),
    	L3VoltageSuperTopLimit(333,"L3 voltage super top limit"),

    	// 2016.03.15 추가분 필요시 주석처리.
    	L1VoltageQualification(334,"L1 voltage qualification"),
    	L2VoltageQualification(335,"L2 voltage qualification"),
    	L3VoltageQualification(336,"L3 voltage qualification"),
    	
    	L1VoltageSuperLowLimit(337,"L1 voltage super low limit"),
    	L2VoltageSuperLowLimit(338,"L2 voltage super low limit"),
    	L3VoltageSuperLowLimit(339,"L3 voltage super low limit"),
    	NeutralCurrentUnbalance(340,"Neutral current unbalance"),
    	L1ReverseCurrent(341,"L1 reverse current"),
    	L2ReverseCurrent(342,"L2 reverse current"),
    	L3ReverseCurrent(343,"L3 reverse current");
    	
        private int flag;
        private String msg;
        
        MEASUREMENT_STATUS_ALARM(int flag, String msg) {
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

    public enum EVENT_LOG {

        PowerFailure(1, "Power Down"),
        PowerRestore(2, "Power Up"),
        DSTSET(3,"Daylight saving time enabled or disabled"),
        TimeChangeFrom(4, "Clock adjusted old time"),
        TimeChangeTo(5, "Clock adjusted new time"),
        ClockInvalid(6,"Clock invalid"),
        ReplaceBattery(7,"Replace Battery"),
        BatteryVoltageLow(8,"Battery Voltage Low"),
        TariffShiftTimeActivated(9,"tariff shift times (TOU) activated"),
        
        ErrorRegisterCleared(10,"Error register cleared"),
        AlarmRegisterCleared(11,"Alarm register cleared"),
        
        ProgramMemoryError(12,"Program memory error"),
        RAMError(13,"RAM Error"),
        NVMemoryError(14,"NV memory error"),
        WatchdogError(15,"Watchdog error"),
        MeasurementSystemError(16,"Measurement system error"),
        
        FirmwareReadyForActivation(17,"Firmware ready for activation"),
        FirmwareActivated(18,"Firmware activated"),
        TariffShiftTime(19,"Tariff Shift Time (TOU)"),
        SuccessfullSelfcheckAfterFirmwareUpdate(20,"Succesfull selfcheck after Firmware update"),
        GPRSModemConnected(21,"GPRS modem connected"),
        GPRSModemDisconnected(22,"GPRS modem disconnected"),
        G3ModemConnected(23,"G3 modem connected"),
        G3ModemDisconnected(24,"G3 modem disconnected"),
        RFModemConnected(25,"RF modem connected"),
        RFModemDisconnected(26,"RF modem disconnected"),
        
        MeterTerminalCoverRemoved(40, "Meter terminal cover removed"),
        MeterTerminalCoverClosed(41, "Meter terminal cover closed"),
        StrongDCFieldDetected(42,"Indicates that the strong magnetic DC field has appeared."),
        NoStringDCField(43,"Indicates that the strong magnetic DC field has disappeared."),
        MeterCoverRemoved(44, "Meter cover removed"),
        MeterCoverClosed(45, "Meter cover closed"),
        FailedLoginAttempt(46,"Failed login attempt"),
        ConfigurationChange(47,"Configuration change"),
        SuccessfulLogin(48,"successful  login"),
        ManualDisconnection(60,"Manual disconnection"),
        ManualConnection(61,"Manual connection"),
        RemoteDisconnection(62,"Remote disconnection"),
        RemoteConnection(63,"Remote connection"),
        LocalDisconnection(64,"Local disconnection"),
        
        LimiterThresholdExceeded(65,"Limiter threshold exceeded"),
        LimiterThresholdOK(66,"Limiter threshold ok"),
        LimiterThresholdChanged(67,"Limiter threshold changed"),
        
        DeviceAlarm(101,"Device Alarm"),
        FunctionAlarm(201,"Function Alarm"),
        MeasurementAlarm(301,"Measurement Alarm");
        
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

    /**
     * 채널변경시 함께 수정해주어야 함.
     * @author simhanger
     *
     */
    public enum ENERGY_LOAD_PROFILE {
        DateTime(1, "Date Time"),
        ActiveEnergyImport(2, "Active Energy -Import"),
        ActiveEnergyExport(3, "Active Energy -Export"),
        ReactiveEnergyImport(4, "Reactive Energy -Import"),
        //ReactiveEnergyExport(5, "Reactive Energy -Export");
        Status(5, "Status");

        private int code;
        private String name;
        
        ENERGY_LOAD_PROFILE(int code, String name) {
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
    
    
    /*
    public enum METER_TIME {
        BeforeTime(0, "beforeTime"),
        AfterTime(1, "afterTime");
        
        private int code;
        private String name;
        
        METER_TIME(int code, String name) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public String getName(){
            return this.name;
        }
    }
    */
    
    public enum DLMS_CLASS {
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
        HDLC(23),
        RELAY_CLASS(70),
        G3_PLC_6LoWPAN(92);
        
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
        ADP_WEAK_LQI_VALUE(3),
        HDLC_ATTR01(1),
        HDLC_ATTR02(2),
        HDLC_ATTR03(3),
        HDLC_ATTR04(4),
        HDLC_ATTR05(5),
        HDLC_ATTR06(6),
        HDLC_ATTR07(7),
        HDLC_ATTR08(8),
        HDLC_ATTR09(9);

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
        
        public int getLen() {
            return this.len;
        }
    }
    
    public DLMSVARIABLE() {

    }

/*
    public static String getDataName(OBIS obis, int cnt) {

        if(obis == OBIS.STANDARD_EVENT 
                || obis == OBIS.FRAUDDETECTIONLOGEVENT 
                || obis == OBIS.RELAY_EVENT
        		|| obis == OBIS.POWER_FAILURE 
        		|| obis == OBIS.POWER_RESTORE
                || obis == OBIS.TIME_CHANGE_BEFORE 
                || obis == OBIS.TIME_CHANGE_AFTER
                || obis == OBIS.MANUAL_DEMAND_RESET
                || obis == OBIS.TAMPER_EVENT) {
            // 데이타 구조가 array, structure, date, cnt
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
                    case TIME_CHANGE_BEFORE :
                        if (mod == 0) return EVENT_LOG.TimeChangeFrom.name()+value;
                        //else if (mod == 1) return EVENT_LOG.TimeChangeFromCnt.name()+value;
                        break;
                    case TIME_CHANGE_AFTER :
                        if (mod == 0) return EVENT_LOG.TimeChangeTo.name()+value;
                        //else if (mod == 1) return EVENT_LOG.TimeChangeToCnt.name()+value;
                        break;
                    //case MANUAL_DEMAND_RESET :
                    //    if (mod == 0) return EVENT_LOG.ManualDemandReset.name()+value;
                        //else if (mod == 1) return EVENT_LOG.ManualDemandResetCnt.name()+value;
                    //   break;
                    case BATTERY_FAILURE:
                    	//if (mod == 0) return EVENT_LOG.
                    	break;
                    case TAMPER_EVENT:
                    	break;
                    case RELAY_EVENT:
                    	break;
                    case STANDARD_EVENT :
                    	System.out.println("STANDARD EVENT="+mod+","+value);
                    	break;
                    case FRAUDDETECTIONLOGEVENT : //TODO ADD
                    	break;
                    default:
                    	break;
                }
            }
        }
        
        return UNDEFINED+" ["+cnt+"]";
    }
    */

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
    
    public enum EXTERNAL_RELAY_STATUS {
        NONE(0),            // 설정없음 
        REMOTE_CONTROL(4),  // 원격부하 개폐신호
        TIME_SWITCH(8),     // 타임스위치 개폐신호
        CURRENT_LIMIT(16);  // 전류제한 기능
        
        int code;
        
        EXTERNAL_RELAY_STATUS(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public static EXTERNAL_RELAY_STATUS getValue(int code) {
            for (EXTERNAL_RELAY_STATUS a : EXTERNAL_RELAY_STATUS.values()) {
                if (a.getCode() == code)
                    return a;
            }
            return null;
        }
    }
    
    public static String[] LP_STATUS_BIT = new String[]{
    	"Power down", //bit 7
    	"Not used",
    	"Clock adjusted",
    	"Not used",
    	"Daylight saving",
    	"Data not valid",
    	"Clock invalid",
    	"Critical error" //  bit 0
    };
    
    /*
    (Byte0) Bit0	L1 voltage loss
    (Byte0) Bit1	L2 voltage loss
    (Byte0) Bit2	L3 voltage loss
    (Byte0) Bit3	L1 current loss
    (Byte0) Bit4	L2 current loss
    (Byte0) Bit5	L3 current loss
    (Byte0) Bit6	L1 voltage cut
    (Byte0) Bit7	L2 voltage cut    
    (Byte1) Bit0	L3 voltage cut
    (Byte1) Bit1	Voltage reverse phase sequence 
    (Byte1) Bit2	Current reverse phase sequence
    (Byte1) Bit3	Voltage asymmetric
    (Byte1) Bit4	Current asymmetric
    (Byte1) Bit5	L1 over current
    (Byte1) Bit6	L2 over current
    (Byte1) Bit7	L3 over current    
    (Byte2) Bit0	L1 current cut
    (Byte2) Bit1	L2 current cut
    (Byte2) Bit2	L3 current cut
    (Byte2) Bit3	L1 over voltage
    (Byte2) Bit4	L2 over voltage
    (Byte2) Bit5	L3 over voltage
    (Byte2) Bit6	L1 under voltage
    (Byte2) Bit7	L2 under voltage    
    (Byte3) Bit0	L3 under voltage
    (Byte3) Bit1	All phases voltage loss
    (Byte3) Bit2	L1 over load
    (Byte3) Bit3	L2 over load
    (Byte3) Bit4	L3 over load
    (Byte3) Bit5	Total power factor exceeded
    (Byte3) Bit6	L1 voltage super top limit
    (Byte3) Bit7	L2 voltage super top limit    
    (Byte4) Bit0	L3 voltage super top limit
    (Byte4) Bit1	L1 voltage qualification
    (Byte4) Bit2	L2 voltage qualification
    (Byte4) Bit3	L3 voltage qualification
    (Byte4) Bit4	L1 voltage super low limit
    (Byte4) Bit5	L2 voltage super low limit
    (Byte4) Bit6	L3 voltage super low limit
    (Byte4) Bit7	Neutral current unbalance
    (Byte5) Bit0	L1 reverse current
    (Byte5) Bit1	L2 reverse current
    (Byte5) Bit2	L3 reverse current
    */

    public static String[] MEASUREMENT_STATUS_BYTE_0 = new String[]{
    	"L2 voltage cut",
    	"L1 voltage cut",
    	"L3 current loss",
    	"L2 current loss",
    	"L1 current loss",
    	"L3 voltage loss",
    	"L2 voltage loss",
    	"L1 voltage loss"
    };
    
    public static String[] MEASUREMENT_STATUS_BYTE_1 = new String[]{
    	"L3 over current",
    	"L2 over current",
    	"L1 over current",
    	"Current asymmetric",
    	"Voltage asymmetric",
    	"Current reverse phase sequence",
    	"Voltage reverse phase sequence",
    	"L3 voltage cut"
    };
    
    public static String[] MEASUREMENT_STATUS_BYTE_2 = new String[]{
    	"L2 under voltage",
    	"L1 under voltage",
    	"L3 over voltage",
    	"L2 over voltage",
    	"L1 over voltage",
    	"L3 current cut",
    	"L2 current cut",
    	"L1 current cut"
    };
    
    public static String[] MEASUREMENT_STATUS_BYTE_3 = new String[]{
    	"L2 voltage super top limit",
    	"L1 voltage super top limit",
    	"Total power factor exceeded",
    	"L3 over load",
    	"L2 over load",
    	"L1 over load",
    	"All phases voltage loss",
    	"L3 under voltage"
    };
    
    public static String[] MEASUREMENT_STATUS_BYTE_4 = new String[]{
    	"Neutral current unbalance",
    	"L3 voltage super low limit",
    	"L2 voltage super low limit",
    	"L1 voltage super low limit",
    	"L3 voltage qualification",
    	"L2 voltage qualification",
    	"L1 voltage qualification",
    	"L3 voltage super top limit"
    };
    
    public static String[] MEASUREMENT_STATUS_BYTE_5 = new String[]{
    	"",
    	"",
    	"",
    	"",
    	"",
    	"L3 reverse current",
    	"L2 reverse current",
    	"L1 reverse current"
    };
    

    /*
    Function status，Function status config，Alarm Function status	Bit No	Function
	(Byte0) Bit0	L1 relay status
	(Byte0) Bit1	L2 relay status
	(Byte0) Bit2	L3 relay status
	(Byte0) Bit3	External relay status
	(Byte0) Bit4	L1 relay error
	(Byte0) Bit5	L2 relay error
	(Byte0) Bit6	L3 relay error
	(Byte0) Bit7	External relay error
	(Byte1) Bit0	Open terminal cover
	(Byte1) Bit1	Open terminal cover in power off 
	(Byte1) Bit2	Open top cover
	(Byte1) Bit3	Open top cover in power off
	(Byte1) Bit4	Magnetic detection 1
	(Byte1) Bit5	Magnetic detection 2
	(Byte1) Bit6	program
	(Byte1) Bit7	Factory status
	*/
    public static String[] FUNCTION_STATUS_BYTE_0 = new String[] {
    	"External relay error",
    	"L3 relay error",
    	"L2 relay error",
    	"L1 relay error",
    	"External relay status",
    	"L3 relay status",
    	"L2 relay status",
    	"L1 relay status"
    };
    public static String[] FUNCTION_STATUS_BYTE_1 = new String[] {
    	"Factory status",
    	"program",
    	"Magnetic detection 2",
    	"Magnetic detection 1",
    	"Open top cover in power off",
    	"Open top cover",
    	"Open terminal cover in power off ",
    	"Open terminal cover"
    };    

    /*
	Drive status，Drive status config，Alarm Drive status	Bit No	Function
	Bit0	EPROM error
	Bit1	Clock error
	Bit2	Battery error
	Bit3	Read card error
	Bit4	Data abnormal
	Bit5	External Battery Status
	Bit6	High low level input
	Bit7	Voltage detect input
	*/
    public static String[] DRIVE_STATUS_BYTE_0 = new String[] {
    	"Voltage detect input",
    	"High low level input",
    	"External Battery Status",
    	"Data abnormal",
    	"Read card error",
    	"Battery error",
    	"Clock error",
    	"EPROM error"
    };

}
