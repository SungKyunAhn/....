package com.aimir.fep.meter.parser.DLMSLSPolandTable;

public class DLMSVARIABLE {

    public enum OBIS {
    	
        DEVICE_INFO("0000600100FF", "Device Information"),        
        MANUFACTURE_SERIAL("0000600101FF","Manufacturer serial"),
        //SERVICEPOINT_SERIAL("0000600102FF","SP serial number"),
        METER_MODEL("0000600103FF","Meter Model"),
        //FW_VERSION("0100000200FF","Firmware Version"),
        LOGICAL_NUMBER("00002A0000FF","Logical Device Number"),
        //PHASE_TYPE("0100000204FF","Meter Phase Type"),
        METER_TIME("0000010000FF", "Meter Time"),
        //CT_RATIO_NUM("0100000402FF","CT Ratio Number"),
        //VT_RATIO_NUM("0100000403FF","VT Ratio Number"),
        //CT_RATIO_DEN("0100000405FF","CT Ratio Den"),
        //VT_RATIO_DEN("0100000406FF","VT Ratio Den"),          
        //OVERAL_TRANS_NUM("0100000404FF","Overall transformer ratio (num)"),        
        VOLTAGE_L1("0100200700FF","L1 voltage"),
        VOLTAGE_L2("0100340700FF","L2 voltage"),
        VOLTAGE_L3("0100480700FF","L2 voltage"),
        CURRENT_L1("01001F0700FF","L1 current"),
        CURRENT_L2("0100330700FF","L2 current"),
        CURRENT_L3("0100470700FF","L3 current"),        
        //METER_STATUS("0000600A05FF","Meter Status"),        
        ENERGY_LOAD_PROFILE("0100630100FF","Energy Load Profile"),
        RELAY_STATUS("000060030AFF","Relay Status"),
        MONTHLY_ENERGY_PROFILE("0000620101FF", "Monthly Energy Profile"),
        MONTHLY_DEMAND_PROFILE("0000620102FF", "Monthly Demand Profile"),
        TIME_CHANGE_BEFORE("0100636203FF", "Time Change Before"),
        TIME_CHANGE_AFTER("0100636204FF", "Time Change After"),
        MANUAL_DEMAND_RESET("0100636206FF", "Manual Demand Reset"),
        POWER_FAILURE("0100636100FF","Power Failure"),
        POWER_RESTORE("0100636202FF","Power Restore");
        //BATTERY_FAILURE("0100636224FF","Battery failure event"),
        //LASTMONTH_ACTIVEENERGY_IMPORT("010001080001","Last month total import active energy (QI+QIV)"),
        //LASTMONTH_ACTIVEENERGY_EXPORT("010002080001","Last month total export active energy (QII+QIII)"),
        //LASTMONTH_REACTIVEENERGY_IMPORT("010003080001","Last month total import reactive energy (QI+QII)"),
        //LASTMONTH_REACTIVEENERGY_EXPORT("010004080001","Last month total export reactive energy (QIII+QIV)"),
        //CUMULATIVE_ACTIVEENERGY_IMPORT("0100010800FF","Cumulative active energy -import"),
        //CUMULATIVE_ACTIVEENERGY_EXPORT("0100020800FF","Cumulative active energy -export"),
        //CUMULATIVE_REACTIVEENERGY_IMPORT("0100030800FF","Cumulative reactive energy -import"),
        //CUMULATIVE_REACTIVEENERGY_EXPORT("0100040800FF","Cumulative reactive energy -export"),
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

    public enum ENERGY_LOAD_PROFILE {
        Date(1, "Date"),
        Status(2, "Status"),
        ActiveEnergyImport(3, "Active Energy -Import"),
        ActiveEnergyExport(4, "Active Energy -Export"),
        ReactiveEnergyImport(5, "Reactive Energy -Import"),
        ReactiveEnergyExport(6, "Reactive Energy -Export");

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
        RELAY_CLASS(70);
        
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
        PROFILE_GENERIC_ATTR04(4),        // value
        PROFILE_GENERIC_ATTR02(2), // buffer
        PROFILE_GENERIC_ATTR07(7), // entries in use
        CLOCK_ATTR02(2);
        
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

    public static String getDataName(OBIS obis, int cnt) {

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
            for (ENERGY_LOAD_PROFILE profile : ENERGY_LOAD_PROFILE.values()) {
                if (profile.getCode() == (cnt % 6))
                    return profile.name();
            }
        }
        
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
