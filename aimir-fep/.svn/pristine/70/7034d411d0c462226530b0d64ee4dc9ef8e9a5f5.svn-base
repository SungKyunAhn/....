package com.aimir.fep.meter.parser.DLMSKaifaTable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;



//import com.aimir.fep.command.conf.DLMSMeta.LOAD_CONTROL_STATUS;

public class DLMSVARIABLE {

    public enum OBIS {
    	
        DEVICE_INFO("0000600101FF", "Device Information"),         //Device ID
        METER_VENDOR("0000600103FF", "Meter Vendor"), //0-0:96.1.3.255 Location Information -> Vendor Information
        MANUFACTURE_ID("0000600104FF", "Manufacture ID"), //0-0:96.1.4.255 Manufacture Id 0000600104FF
        METER_MODEL("0000600107FF", "Meter Model"), //0-0:96.1.7.255  Meter Type -> Model
        MANUFACTURE_SERIAL("0000600101FF","Manufacturer serial"),
        LOGICAL_NUMBER("00002A0000FF","Logical Device Number"),
        METER_TIME("0000010000FF", "Meter Time"),
        ENERGY_LOAD_PROFILE("0100630100FF","Energy Load Profile"),// 1.0.99.1.0.255 : Load profile
        POWER_QUALITY_PROFILE("0100630200FF", "Power Quality Profile"),
        STANDARD_EVENT("0000636200FF","Standard Event"), // 0.0.99.98.0.255: standard events
        TAMPER_EVENT("0000636201FF","Tamper Event"),// 0.0.99.98.1.255: tampering logs
        POWERFAILURE_LOG("0100636100FF","Power Failure log"),  // 1.0.99.97.0.255: Power failure logs for single-phase/poly-phase      
        CONTROL_LOG("0000636202FF","Control Log"),  // 0.0.99.98.2.255: control log
        POWER_QUALITY_LOG("0000636203FF","Power Quality Log"), //0.0.99.98.3.255: power quality logs
        FIRMWARE_UPGRADE_LOG("0000636204FF","Firmware Upgrade log"), //0.0.99.98.4.255: firmware upgrade logs
    	MONTHLY_ENERGY_PROFILE("0000620101FF", "Monthly Energy Profile"),
    	FW_VERSION("0100000200FF","Firmware Version"),
    	ALARM_OBJECT("0000616200FF", "Alarm Object"),
        CT_RATIO("01000B237DFF","CT Ratio"),
        PT_RATIO("01000B237EFF","PT Ratio"),
        //TRANFORMER_RATIO("01000B237FFF", "Transformer Ratio"),
        RELAY_STATUS("000060030AFF","Relay Status"),
        //GRID_FREQUENCY("01000E0700FF","Grid Frequency"),
        //INSTANTANEOUS_PF("01000D0700FF", "Instantaneous Power Factor"),
        //CUMULATIVE_ACTIVEENERGY_IMPORT("0100010800FF","Cumulative Active Energy -Import"),
        INSTANTANEOUS_VOLTAGE_L1("0100200700FF", "Instantaneous voltage L1"),//1-0:32.7.0.255  SP-280
        INSTANTANEOUS_VOLTAGE_L2("0100340700FF", "Instantaneous voltage L2"),//1-0:52.7.0.255  SP-280
        INSTANTANEOUS_VOLTAGE_L3("0100480700FF", "Instantaneous voltage L3"),//1-0:72.7.0.255  SP-280
        LIMITER_INFO("0000110000FF", "Limiter Information"),
        MBUSMASTER_LOAD_PROFILE("0000180300FF", "M-Bus Master Load profileCannel"),
        MBUSMASTER1_LOAD_PROFILE("0001180300FF", "M-Bus Master Load profileCannel 1"),
        MBUSMASTER2_LOAD_PROFILE("0002180300FF", "M-Bus Master Load profileCannel 2"),
        MBUSMASTER3_LOAD_PROFILE("0003180300FF", "M-Bus Master Load profileCannel 3"),
        MBUSMASTER4_LOAD_PROFILE("0004180300FF", "M-Bus Master Load profileCannel 4"),
    	CLOCK("0000010000FF", "Clock"),
    	//SERVICEPOINT_SERIAL("0000600102FF","SP serial number"),
        
        //PHASE_TYPE("0100000204FF","Meter Phase Type"),
        //CT_RATIO_NUM("0100000402FF","CT Ratio Number"),
        //VT_RATIO_NUM("0100000403FF","VT Ratio Number"),
        //CT_RATIO_DEN("0100000405FF","CT Ratio Den"),
        //VT_RATIO_DEN("0100000406FF","VT Ratio Den"),          
        //OVERAL_TRANS_NUM("0100000404FF","Overall transformer ratio (num)"),        
        //CURRENT_L1("01001F0700FF","L1 current"),
        //CURRENT_L2("0100330700FF","L2 current"),
        //CURRENT_L3("0100470700FF","L3 current"),        
        //METER_STATUS("0000600A05FF","Meter Status"),        
        //MONTHLY_DEMAND_PROFILE("0000620102FF", "Monthly Demand Profile"),
        //TIME_CHANGE_BEFORE("0100636203FF", "Time Change Before"),
        //TIME_CHANGE_AFTER("0100636204FF", "Time Change After"),
        //MANUAL_DEMAND_RESET("0100636206FF", "Manual Demand Reset"),

        //POWER_RESTORE("0100636202FF","Power Restore");
        //BATTERY_FAILURE("0100636224FF","Battery failure event"),
        //LASTMONTH_ACTIVEENERGY_IMPORT("010001080001","Last month total import active energy (QI+QIV)"),
        //LASTMONTH_ACTIVEENERGY_EXPORT("010002080001","Last month total export active energy (QII+QIII)"),
        //LASTMONTH_REACTIVEENERGY_IMPORT("010003080001","Last month total import reactive energy (QI+QII)"),
        //LASTMONTH_REACTIVEENERGY_EXPORT("010004080001","Last month total export reactive energy (QIII+QIV)"),
        CUMULATIVE_ACTIVEENERGY_IMPORT("0100010800FF","Cumulative active energy -import"), // 1.0.1.8.0.255
        CUMULATIVE_ACTIVEENERGY_EXPORT("0100020800FF","Cumulative active energy -export"), // 1.0.2.8.0.255
        CUMULATIVE_REACTIVEENERGY_IMPORT("0100030800FF","Cumulative reactive energy -import"), // 1.0.3.8.0.255
        CUMULATIVE_REACTIVEENERGY_EXPORT("0100040800FF","Cumulative reactive energy -export"), // 1.0.3.8.0.255
        //TOTAL_ACTIVEENERGY_IMPORT("01000F0800FF","Total energy +A"),
        //TOTAL_ACTIVEENERGY_EXPORT("0100100800FF","Total energy -A"),
        //TOTAL_MAX_ACTIVEDEMAND_IMPORT("0100010600FF","Total max demand +A"),
        //TOTAL_MAX_ACTIVEDEMAND_EXPORT("0100020600FF","Total max demand -A"),
        //TOTAL_MAX_REACTIVEDEMAND_IMPORT("0100030600FF","Total max demand +R"),
        //TOTAL_MAX_REACTIVEDEMAND_EXPORT("0100040600FF","Total max demand -R"),
        //TOTAL_CUM_ACTIVEDEMAND_IMPORT  ("0100010200FF","Total cumulative demand +A"),
        //TOTAL_CUM_ACTIVEDEMAND_EXPORT  ("0100020200FF","Total cumulative demand -A"),
        //TOTAL_CUM_REACTIVEDEMAND_IMPORT("0100030200FF","Total cumulative demand +R"),
        //TOTAL_CUM_REACTIVEDEMAND_EXPORT("0100040200FF","Total cumulative demand -R");
    	MBUS_CLIENT_SETUP_CHANNEL1("0001180100FF", "M-Bus Client Setup Channel 1"), // 0.1.24.1.0.255
    	MBUS_CLIENT_SETUP_CHANNEL2("0002180100FF", "M-Bus Client Setup Channel 2"), // 0.2.24.1.0.255
    	MBUS_CLIENT_SETUP_CHANNEL3("0003180100FF", "M-Bus Client Setup Channel 3"), // 0.3.24.1.0.255
    	MBUS_CLIENT_SETUP_CHANNEL4("0004180100FF", "M-Bus Client Setup Channel 4"); // 0.4:24.1.0.255


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

    public enum METER_EVENT_LOG {
    	Poweroff(1, "Power off"),
    	PowerOn(2, "Power on"),
    	ChangeClock(3, "Change of clock"),
        ConfigurationChange(4, "Change of parameters configuration"),
    	SelfDiagnostic(5, "Self-diagnostic error"),
        L1VolInterrupt(6, "L1 voltage interrupt"),
        L2VolInterrupt(7, "L2 voltage interrupt"),
        L3VolInterrupt(8, "L3 voltage interrupt"),
        ClockInvalidEvent(10, "Clock invalid event"),
        ReplaceBattery(11, "Replace battery"),
        ProgramMemoryErr(20, "Program memory error"),
        EventRamErr(21, "Event RAM error"),
        NvMemoryErr(22, "NV Memory error"),
        MeasurementSysErr(23, "Measurement system error"),
        WatchDogErr(24, "WatchDog error"),
        EarthFaultStart(40, "Earth fault start"),
        EarthFaultEnd(41, "Earth fault end"),
        StrongDCFiledDetected(42, "Strong DC field detected"),
        MeterCoverRemoved(43, "Meter cover removed"),
        TerminalCoverRemove(44, "Terminal cover remove"),
        FailedLoginAttempt(45, "Failed login attempt"),
        MeterCoverClosed(46, "Meter cover closed"),
        TerminalCoverClosed(47, "Terminal cover closed"),
        BreakerPowerLimitation(60, "Open breaker due to power limitation"),
        BreakerControlCom(61, "Open breaker due to control commands"),
        BreakerPressButton(62, "Close breaker via press button"),
        BreakerViaControlCom(63, "Close breaker via control commands"),
        LocalDisconnection(64, "Local disconnection"),
        L1LongVoltageUnderStart(80,"L1 long term voltage under start"),
        L2LongVoltageUnderStart(81,"L2 long term voltage under start"),
        L3LongVoltageUnderStart(82,"L3 long term voltage under start"),
        L1LongVoltageUnderEnd(83,"L1 long term voltage under End"),
        L2LongVoltageUnderEnd(84,"L2 long term voltage under End"),
        L3LongVoltageUnderEnd(85,"L3 long term voltage under End"),
        L1LongVoltageOverStart(86,"L1 long term voltage over start"),
        L2LongVoltageOverStart(87,"L2 long term voltage over start"),
        L3LongVoltageOverStart(88,"L3 long term voltage over start"),
        L1LongVoltageOverEnd(89,"L1 long term voltage over end"),
        L2LongVoltageOverEnd(90,"L2 long term voltage over end"),
        L3LongVoltageOverEnd(91,"L3 long term voltage over end"),
        L1ShortVoltageUnderStart(92,"L1 short term voltage under start"),
        L2ShortVoltageUnderStart(93,"L2 short term voltage under start"),
        L3ShortVoltageUnderStart(94,"L3 short term voltage under start"),
        L1ShortVoltageUnderEnd(95,"L1 short term voltage under End"),
        L2ShortVoltageUnderEnd(96,"L2 short term voltage under End"),
        L3ShortVoltageUnderEnd(97,"L3 short term voltage under End"),
        L1ShortVoltageOverStart(98,"L1 short term voltage over start"),
        L2ShortVoltageOverStart(99,"L2 short term voltage over start"),
        L3ShortVoltageOverStart(100,"L3 short term voltage over start"),
        L1ShortVoltageOverEnd(101,"L1 short term voltage over end"),
        L2ShortVoltageOverEnd(102,"L2 short term voltage over end"),
        L3ShortVoltageOverEnd(103,"L3 short term voltage over end"),
        VoltageUnbalenceStart(104,"Voltage unbalence start"),
        VoltageUnbalenceEnd(105,"Voltage unbalence end"),
        L1THDStart(106,"L1 THD start"),
        L2THDStart(107,"L2 THD start"),
        L3THDStart(108,"L3 THD start"),
        L1THDEnd(109,"L1 THD end"),
        L2THDEnd(110,"L2 THD end"),
        L3THDEnd(111,"L3 THD end"),
        CommunicationErrMbus1(120,"1_Communication error Mbus"),
        NewMbusDeviceDisCover1(125,"1_New MBus device discover"),
        CommunicationErrMbus2(130,"2_Communication error Mbus"),
        NewMbusDeviceDisCover2(135,"2_New MBus device discover"),
        CommunicationErrMbus3(140,"3_Communication error Mbus"),
        NewMbusDeviceDisCover3(145,"3_New MBus device discover"),
        CommunicationErrMbus4(150,"4_Communication error Mbus"),
        NewMbusDeviceDisCover4(155,"5_New MBus device discover"),
        FirmwareReception(200, "FirmwareReception"),
        FirmwareVerificationFails(201, "Firmware verification fails"),
        FirmwareVerificationOk(202, "Firmware verification ok"),
        FirmwareActivationFails(203, "Firmware activation fails"),
        FirmwareActivationOk(204, "Firmware activation ok"),
        FirmwareRollback(205, "Firmware roll back"),
        AllVolInterruptStart(220, "All voltage interrruption start"),
        L1VolInterruptStart(221, "L1 voltage interruption Start"),
        L2VolInterruptStart(222, "L2 voltage interruption Start"),
        L3VolInterruptStart(223, "L3 voltage interruption Start"),       
        L1LongVolInterrupEnd(224, "L1 long voltage interrupt end"),
        L1ShortVolInterrupEnd(225, "L1 short voltage interrupt end"),        
        L2LongVolInterrupEnd(226, "L2 long voltage interrupt end"),
        L2ShortVolInterrupt(227, "L2 L2 short voltage interrupt end"),
        L3LongVolInterrupt(228, "L3 long voltage interrupt end"),
        L3ShortVolInterrupt(229, "L3 short voltage interrupt end"),
        AllLongPowerFail(230, "All Long power fail"),
        AllShortPowerFail(231, "All Short power fail"),
        EventLogClear(255, "Event log clear");
    	
        private int flag;
        private String msg;
        
        METER_EVENT_LOG(int flag, String msg) {
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
    
    
   
    public enum STANDARD_EVENT_TAGDATA {
    	Structure(0, "Structure"),
    	EventTime(1, "EventTime"),
    	EventCode(2, "EventCode");

        private int code;
        private String name;
        
        STANDARD_EVENT_TAGDATA(int code, String name) {
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
    
    public enum CONTROL_LOG_TAGDATA {
    	Structure(0, "Structure"),
    	EventTime(1, "EventTime"),
    	EventCode(2, "EventCode"),
    	ControlStatus1(3, "ControlStatus1"),
    	ControlStatus2(4, "ControlStatus2"); 	

        private int code;
        private String name;
        
        CONTROL_LOG_TAGDATA(int code, String name) {
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
    
    //SP-284
    public enum POWERQUALITY_LOG_TAGDATA {
    	Structure(0, "Structure"),
    	EventTime(1, "EventTime"),
    	EventCode(2, "EventCode"),
    	Value1(3, "Value1"),
    	Value2(4, "Value2"); 	

        private int code;
        private String name;
        
        POWERQUALITY_LOG_TAGDATA(int code, String name) {
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
    
    public enum TAMPEREVENT_LOG_TAGDATA {
    	Structure(0, "Structure"),
    	EventTime(1, "EventTime"),
    	EventCode(2, "EventCode");

        private int code;
        private String name;
        
        TAMPEREVENT_LOG_TAGDATA(int code, String name) {
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
    
    public enum FWUPGRADEVENT_LOG_TAGDATA {
    	Structure(0, "Structure"),
    	EventTime(1, "EventTime"),
    	EventCode(2, "EventCode");

        private int code;
        private String name;
        
        FWUPGRADEVENT_LOG_TAGDATA(int code, String name) {
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
    // SP-284
    
    public enum POWERFAILURE_LOG_TAGDATA {
    	Structure(0, "Structure"),
    	EventTime(1, "EventTime"),
    	EventCode(2, "EventCode"),
    	Status1(3, "Status1"),
    	Status2(4, "Status2"),
    	Status3(5, "Status3");

        private int code;
        private String name;
        
        POWERFAILURE_LOG_TAGDATA(int code, String name) {
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
    
    public enum ENERGY_LOAD_PROFILE {
    	Structure(0, "Structure"),
        Date(1, "Date"),
        Status(2, "Status"),
        ActiveEnergyImport(3, "Active Energy -Import"),
        ActiveEnergyExport(4, "Active Energy -Export"),
        ReactiveEnergyImport(5, "Reactive Energy -Import"),
        ReactiveEnergyExport(6, "Reactive Energy -Export");
//    	MeterTime(7, "Metering Time"),
//   	OperationTIme(8, "Operation TIme");

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
 
    public enum POWER_QUALITY_PROFILE {
    	Structure(0, "Structure"),
    	Date(1, "Date"),			    	// INSERT SP-204
        L1MaxVoltage(2, "Maximum Voltage L1"),
        L1MinVoltage(3, "Minumum Voltage L1"),
        L1AvgVoltage(4, "Average Voltage L1"),
        L2MaxVoltage(5, "Maximum Voltage L2"),
        L2MinVoltage(6, "Minumum Voltage L2"),
        L2AvgVoltage(7, "Average Voltage L2"),
        L3MaxVoltage(8, "Maximum Voltage L3"),
        L3MinVoltage(9, "Minumum Voltage L3"),
        L3AvgVoltage(10, "Average Voltage L3");

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
    
    public enum POWER_QUALITY_PROFILE_3P {
    	Structure(0, "Structure"),
    	Date(1, "Date"),			    	// INSERT SP-204
        L1MaxVoltage(2, "Maximum Voltage L1"),
        L1MinVoltage(3, "Minumum Voltage L1"),
        L1AvgVoltage(4, "Average Voltage L1"),
        L2MaxVoltage(5, "Maximum Voltage L2"),
        L2MinVoltage(6, "Minumum Voltage L2"),
        L2AvgVoltage(7, "Average Voltage L2"),
        L3MaxVoltage(8, "Maximum Voltage L3"),
        L3MinVoltage(9, "Minumum Voltage L3"),
        L3AvgVoltage(10, "Average Voltage L3");

        private int code;
        private String name;
        
        POWER_QUALITY_PROFILE_3P(int code, String name) {
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

    // INSERT START SP-204
    public enum POWER_QUALITY_PROFILE_1P {
    	Structure(0, "Structure"),
    	Date(1, "Date"),
        L1MaxVoltage(2, "Maximum Voltage L1"),
        L1MinVoltage(3, "Minumum Voltage L1"),
        L1AvgVoltage(4, "Average Voltage L1");

        private int code;
        private String name;
        
        POWER_QUALITY_PROFILE_1P(int code, String name) {
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
    // INSERT END SP-204
    
    
//    public enum MBUSMASTER_LOAD_PROFILE {
//    	Structure(0, "Structure"),
//        Date(1, "Date"),
////        DeviceType(2, "Device Type"),
////        CapturedValue(3, "Caputured Value"),
////        DeviceStatus(4, "DeviceStatus");
//        DeviceStatus(2, "DeviceStatus"),
//        DeviceType(3, "Device Type"),
//        CapturedValue(4, "Caputured Value"),
//        Volume(5, "Volume");
//
//        private int code;
//        private String name;
//        
//        MBUSMASTER_LOAD_PROFILE(int code, String name) {
//            this.code = code;
//            this.name = name;
//        }
//        
//        public int getCode() {
//            return this.code;
//        }
//        
//        public String getName(){
//            return this.name;
//        }
//    }

    public enum MBUSMASTER_LOAD_PROFILE {
    	Structure(0, "Structure"),
        Date(1, "Date"),
        Ch1DeviceStatus(2, "DeviceStatus"),
        Ch1DeviceType(3, "Device Type"),
        Ch1CapturedValue(4, "Caputured Value"),
        Ch1Volume(5, "Volume"),
        Ch2DeviceStatus(6, "DeviceStatus"),
        Ch2DeviceType(7, "Device Type"),
        Ch2CapturedValue(8, "Caputured Value"),
        Ch2Volume(9, "Volume"),
        Ch3DeviceStatus(10, "DeviceStatus"),
        Ch3DeviceType(11, "Device Type"),
        Ch3CapturedValue(12, "Caputured Value"),
        Ch3Volume(13, "Volume"),
        Ch4DeviceStatus(14, "DeviceStatus"),
        Ch4DeviceType(15, "Device Type"),
        Ch4CapturedValue(16, "Caputured Value"),
        Ch4Volume(17, "Volume");

        private int code;
        private String name;
        
        MBUSMASTER_LOAD_PROFILE(int code, String name) {
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
        DATA_ATTR04(4),
        DATA_ATTR06(6),
        DATA_ATTR07(7),
        DATA_ATTR09(9),
        DATA_ATTR12(12),
        REGISTER_ATTR02(2),        // value
        REGISTER_ATTR03(3),        // scalar unit
        REGISTER_ATTR04(4),        // status
        PROFILE_GENERIC_ATTR04(4),        // value
        PROFILE_GENERIC_ATTR02(2), // buffer
        PROFILE_GENERIC_ATTR03(3),	// capture object (for SAT)
        PROFILE_GENERIC_ATTR07(7), // entries in use
        PROFILE_GENERIC_ATTR08(8), // profile_entries
        CLOCK_ATTR02(2),
    	CLOCK_ATTR07(7),
    	LIMIT_ATTR04(4),
    	LIMIT_ATTR06(6),
    	LIMIT_ATTR07(7),
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
    
    public DLMSVARIABLE() {

    }

    public static String getDataName(OBIS obis, int cnt, int structure) {
/*
        if (obis == OBIS.POWER_FAILURE || obis == OBIS.POWER_RESTORE
                || obis == OBIS.TIME_CHANGE_BEFORE || obis == OBIS.TIME_CHANGE_AFTER
                || obis == OBIS.MANUAL_DEMAND_RESET) {
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
                    case MANUAL_DEMAND_RESET :
                        if (mod == 0) return EVENT_LOG.ManualDemandReset.name()+value;
                        //else if (mod == 1) return EVENT_LOG.ManualDemandResetCnt.name()+value;
                        break;
                    //case BATTERY_FAILURE:
                    	//if (mod == 0) return EVENT_LOG.
                    //	break;
                    default:
                    	break;
                }
            }
        }
         else if (obis == OBIS.ENERGY_LOAD_PROFILE) {
 */
    	if (obis == OBIS.STANDARD_EVENT) {
            for (STANDARD_EVENT_TAGDATA profile : STANDARD_EVENT_TAGDATA.values()) {
                if (profile.getCode() == (cnt % 3))
                    return profile.name();
            }
    	}
    	if (obis == OBIS.CONTROL_LOG) {
            for (CONTROL_LOG_TAGDATA profile : CONTROL_LOG_TAGDATA.values()) {
                if (profile.getCode() == (cnt % 5))
                    return profile.name();
            }
    	}
    	//SP-284
    	if (obis == OBIS.POWER_QUALITY_LOG) {
            for (POWERQUALITY_LOG_TAGDATA profile : POWERQUALITY_LOG_TAGDATA.values()) {
                if (profile.getCode() == (cnt % 5))
                    return profile.name();
            }
    	}  
    	
    	if (obis == OBIS.TAMPER_EVENT) {
            for (TAMPEREVENT_LOG_TAGDATA profile : TAMPEREVENT_LOG_TAGDATA.values()) {
                if (profile.getCode() == (cnt % 3))
                    return profile.name();
            }
    	}  
    	
       	if (obis == OBIS.FIRMWARE_UPGRADE_LOG) {
            for (FWUPGRADEVENT_LOG_TAGDATA profile : FWUPGRADEVENT_LOG_TAGDATA.values()) {
                if (profile.getCode() == (cnt % 3))
                    return profile.name();
            }
    	} 
    	   	
    	//SP-284
    	
    	if (obis == OBIS.POWERFAILURE_LOG) {
            for (POWERFAILURE_LOG_TAGDATA profile : POWERFAILURE_LOG_TAGDATA.values()) {
                if (profile.getCode() == (cnt % 6))
                    return profile.name();
            }
    	}
  		if (obis == OBIS.ENERGY_LOAD_PROFILE) {
            for (ENERGY_LOAD_PROFILE profile : ENERGY_LOAD_PROFILE.values()) {
                if (profile.getCode() == (cnt % 7))
                    return profile.name();
            }
        }
  		// INSERT START SP-204
  		if (obis == OBIS.POWER_QUALITY_PROFILE) {
  			if (structure == 10) {
	  			for(POWER_QUALITY_PROFILE_3P profile : POWER_QUALITY_PROFILE_3P.values()) {
	                if (profile.getCode() == (cnt % 11))
	                    return profile.name();
	  			}
  			}
  			if (structure == 4) {
	  			for(POWER_QUALITY_PROFILE_1P profile : POWER_QUALITY_PROFILE_1P.values()) {
	                if (profile.getCode() == (cnt % 5))
	                    return profile.name();
	  			}
  			}
  		}
  		if (obis == OBIS.MBUSMASTER_LOAD_PROFILE) {
            for (MBUSMASTER_LOAD_PROFILE profile : MBUSMASTER_LOAD_PROFILE.values()) {
                if (profile.getCode() == (cnt % 18))
                    return profile.name();
            }  			
  		}  			
  		// INSERT END SP-204
        
        return UNDEFINED+" ["+cnt+"]";
    }

    /*
    public static boolean getIncludeObis(OBIS obis) {

        if (obis == OBIS.MONTHLY_ENERGY_PROFILE) {
            return true;
        } 
        return false;
    }
    */
    
    public enum UNIT {
        YEAR(1,"year[a]"),
        MONTH(2,"month[mo]"),
        WEEK(3,"week[wk]"),
        DAY(4,"day[d]"),
        HOUR(5,"hour[h]"),
        MINUTE(6,"min[min.]"),
        SECOND(7,"second[s]"),
        PHASE_ANGLEGEGREE(8,"phase anglegegree[°]"),
        TEMPERATURE(9,"temperature[°C]"),
        LOCAL_CURRENCY(10,"local currency[currency]"),
        LENGTH(11,"length[m]"), 
        SPEED(12,"speed[m/s]"), 
        VOLUME_CUBIC_METER(13,"volume cubic meter[m3]"),
        CORRECTED_VOLUME(14,"corrected volume[m3]"),
        VOLUME_FLUX_HOUR(15,"volume flux hour[m3/h]"),
        CORRECTED_VOLUME_FLUX_HOUR(16,"corrected vlume flux[m3/h]"),
        VOLUME_FLUXDAY(17,"volume flux[m3/d]"),
        CORRECTE_VOLUME_FLUX_DAY(18,"corrected vlume flux[m3/d]"),
        VOLUME_LITER(19,"volume[l]"),
        MASS_KG(20,"mass[kg]"),
        FORCE(21,"force[N]"),
        ENERGY(22,"energy[Nm]"),
        PRESSURE_PASCAL(23,"pressure_pascal[Pa]"),
        PRESSURE_BAR(24,"pressure[bar]"),
        ENERGY_JOULE(25,"energy_joule[J]"), // Energy joule J = Nm = Ws
        THERMAL_POWER(26,"thermal power[J/h]"), // Thermal power J/60*60s
        ACTIVE_POWER(27,"active power[W]"), //Active power P watt W = J/s
        APPARENT_POWER(28,"apparent power[VA]"), // Apparent power S
        REACTIVE_POWER(29,"reactive power[var]"), //Reactive power Q
        ACTIVE_ENERGY(30,"active energy[Wh]"), // Active energy W*60*60s
        APPARENT_ENERGY(31,"apparent energy[VAh]"), // Apparent energy VA*60*60s
        REACTIVE_ENERGY(32,"reactive energy[varh]"), // Reactive energy var*60*60s
        CURRENT(33,"current[A]"), // Current I ampere A
        ELECTRICAL_CHARGE(34,"voltage[C]"), // Electrical charge Q coulomb C = As
        VOLTAGE(35,"voltage[V]"), // Voltage
        ELECTRICAL_FIELD_STRENGTH(36,"electric field strength[V/m]"), // Electrical field strength E V/m
        CAPACITY(37,"capacitance[F]"), // Capacity C farad C/V = As/V
        RESISTANCE(38,"resistance[Ω]"), // Resistance R ohm = V/A
        RESISTIVITY(39,"resistivity[Ωm2/m]"), // Resistivity
        MAGNETIC_FLUX(40,"magnetic flux[Wb]"), // Magnetic flux F weber Wb = Vs
        INDUCTION(41,"magnetic flux densty[T]"), // Induction T tesla Wb/m2
        MAGNETIC(42,"magnetic field strength[A/m]"), // Magnetic field strength H A/m
        INDUCTIVITY(43,"inductance[H]"), // Inductivity L henry H = Wb/A
        FREQUENCY(44,"frequency[Hz]"), // Frequency f
        ACTIVE(45,"active[1/(Wh)]"), // Active energy meter constant 1/Wh
        REACTIVE(46,"reactive[1/(varh)]"), // Reactive energy meter constant
        APPARENT(47,"apparent[1/(VAh)]"), // Apparent energy meter constant
        V260(48,"v260[V2h]"), // V260*60s
        A260(49,"a260[A2h]"), // A260*60s
        MASS_KG_PER_SECOND(50,"mass flux[kg/s]"),
        CONDUCTANCE(51,"conductance[S, mho]"),
        KELVIN(52,"temperature[K]"),
        V2H(53,"v2h[1/(V2h)]"),
        A2H(54,"a2h[1/(A2h)]"),
        CUBIC_METER_RV(55,"cubic meter rv[1/m3]"),
        PERCENTAGE(56,"percentage[percentage]"),
        AMPERE_HOURS(57,"ampere-hours[Ah]"),
        ENERGY_PER_VOLUME(60,"energy per volume[Wh/m3]"),
        WOBBE(61,"wobbe[J/m3]"),
        MOLE_PERCENT(62,"mole percent[Mol %]"),
        MASS_DENSITY(63,"mass density[g/m3]"),
        PASCAL_SECOND(64,"pascal second[Pa s]"),
        JOULE_KILOGRAM(65,"joule kilogram[J/kg]"),
        SIGNAL_STRENGTH(70,"signal strength[dBm]"),
        RESERVED(253,"reserved"),
        OTHER_UNIT(254,"other"), 
        NO_UNIT(255,"unitless"); 
        
        private int code;
        private String unit;
        
        UNIT(int code, String unit) {
            this.code = code;
            this.unit = unit;
        }
        
        public String getName(){
        	return this.unit;
        }
        
        public int getCode() {
            return this.code;
        }
        
		public static UNIT getItem(int code) {
			for (UNIT fc : UNIT.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return null;
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
    /**
     * <p>Java class for protocol.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;simpleType name="LOAD_CONTROL_STATUS_KAIFA">
     *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *     &lt;enumeration value="OFF"/>
     *     &lt;enumeration value="ON"/>
     *   &lt;/restriction>
     * &lt;/simpleType>
     * </pre>
     * 
     */
    @XmlType(name = "RELAY_STATUS_KAIFA")
    @XmlEnum
    public enum RELAY_STATUS_KAIFA {
        @XmlEnumValue("Disconnected")
        Disconnected(0),
        @XmlEnumValue("Connected")
        Connected(1);
        
        int code;
        
        RELAY_STATUS_KAIFA(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public static RELAY_STATUS_KAIFA getValue(int code) {
            for (RELAY_STATUS_KAIFA a : RELAY_STATUS_KAIFA.values()) {
                if (a.getCode() == code)
                    return a;
            }
            return null;
        }
    }
    
	public enum MBUS_DEVICE_TYPE {
		Other(0x00),
		Oil(0x01),
		Electricity(0x02),
		Gas(0x03),
		Heat(0x04),
		Steam(0x05),
		WarmWater(0x06),
		Water(0x07),
		HeatCostAllocator(0x08),
		CompressedAir(0x09),
		CoolingLoadTemperatureOutlet(0x0A),
		CoolingLoadTemperatureInlet(0x0B),
		HeatTemperatureInlet(0x0C),
		HeatCoolingLoad(0x0D),
		BusSystemComponent(0x0E),
		UnknownMedium(0x0F),
		HotWater(0x15),
		ColdWater(0x16),
		DualRegisterWater(0x17),
		Pressure(0x18),
		ADConverter(0x19);

		private byte code;
		
		MBUS_DEVICE_TYPE(int code) {
			this.code = (byte)code;
		}

		public byte getCode() {
			return this.code;
		}
		public static MBUS_DEVICE_TYPE getItem(byte code) {
			for (MBUS_DEVICE_TYPE fc : MBUS_DEVICE_TYPE.values()) {
				if (fc.code == code) {
					return fc;
				}
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
}
