package com.aimir.fep.meter.parser.DLMSEMnVEtype_1_0_Table;

public class DLMSEMnVEType_1_0_VARIABLE {

	public enum OBIS {
		MANUFACTURER_METER_ID("0100000001FF", "Manufacturer Meter ID"), 
		CUSTOMER_METER_ID("0100000000FF", "Customer Meter ID"), 
		METER_TIME("0000010000FF", "MeterTime"), // 일자/시간
		METER_CONSTANT_ACTIVE("0101000300FF", "Meter Constant For Active Energy"), // 유효 전력량 계기정수
		LP_INTERVAL("0101000804FF", "LP Interval"), // Load Profile 기록간격
		IMPORT_ACTIVE_ENERGY("0101010800FF", "Import Active Energy"), // 유효 전력량(현월)
		LOAD_PROFILE("0100630100FF", "Load Profile"); // Load Profile(누적값)

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
				if (obis.getCode().equals(code))
					return obis;
			}
			return null;
		}
	}

	public final static String UNDEFINED = "undefined";

	public enum METER_CONSTANT {
		ActiveC, ReactiveC;
	}

	public enum METER_TIME {
		BeforeTime(0, "beforeTime"), AfterTime(1, "afterTime");

		private int code;
		private String name;

		METER_TIME(int code, String name) {
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

	public enum EVENT {
		EventTime(0, "Event Time"), EventCount(1, "Event Count");

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

		public static EVENT getItems(int code) {
			for (EVENT cl : EVENT.values()) {
				if (cl.getCode() == code)
					return cl;
			}
			return null;
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
		ProgramChange(8, "Program Change"),
		// ProgramChangeCnt(8);

		POWER_FAILURE(17, "Power Failure"); // 17은 METEREVENT Table의 VALUE컬럼에 값이 있어야함. Power Failure

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
		Structure(0, "Structure"), ImportActive(1, "Import Active Energy QI+QIV"), // 순방향 유효전력량  
		ImportLaggingReactive(2, "Import Lagging Reactive Energy QI"), // 순방향 지상 무효전력량
		ImportLeadingReactive(3, "Import Leading Reactive Energy QIV"), // 순방향 진상 무효전력량
		ImportApparentEnergy(4, "Import Apparent Energy QI+QIV"), // 순방향 피상 전력량 
		Date(5, "Date"), // 일자/시간
		Status(6, "Status"), // 상태정보
		ExportActive(7, "Export Active Energy QII+QIII"), // 역방향 유효 전력량
		ExportLaggingReactive(8, "Export Lagging Reactive Energy QII"), // 역방향 진상 무효전력량
		ExportLeadingReactive(9, "Export Leading Reactive Energy QIII"), // 역방향 지상 무효전력량
		ExportApparentEnergy(10, "Export Apparent Energy QII+QIII"); // 역방향 피상전력량
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

		public String getName() {
			return this.name;
		}
	}

	public enum DLMS_CLASS {
		DATA(1), REGISTER(3), PROFILE_GENERIC(7), CLOCK(8);

		private int clazz;

		DLMS_CLASS(int clazz) {
			this.clazz = clazz;
		}

		public int getClazz() {
			return this.clazz;
		}

		public static DLMS_CLASS getItems(int code) {
			for (DLMS_CLASS cl : DLMS_CLASS.values()) {
				if (cl.getClazz() == code)
					return cl;
			}
			return null;
		}
	}

	public enum DLMS_CLASS_ATTR {
		DATA_ATTR02(2), // value, CHOICE

		REGISTER_ATTR02(2), // value, CHOICE : 값
		REGISTER_ATTR03(3), // scaler_unit, scal_unit_type  : 단위 

		EXTENDED_REGISTER_ATTR02(2), // value, CHOICE  : 값
		EXTENDED_REGISTER_ATTR03(3), // scaler_unit, scal_unit_type   : 단위

		PROFILE_GENERIC_ATTR02(2), // buffer, octet-string : 값
		PROFILE_GENERIC_ATTR07(7), // entries_in_use, double-long-unsigned : 개수

		CLOCK_ATTR02(2); // TIME , octet-string

		private int attr;

		DLMS_CLASS_ATTR(int attr) {
			this.attr = attr;
		}

		public int getAttr() {
			return this.attr;
		}

		public static DLMS_CLASS_ATTR getItems(int code) {
			for (DLMS_CLASS_ATTR cl : DLMS_CLASS_ATTR.values()) {
				if (cl.getAttr() == code)
					return cl;
			}
			return null;
		}
	}

	public enum DLMS_TAG_TYPE {
		Null(0, 1), Array(1, 1), Structure(2, 1), Boolean(3, 1), BitString(4, 0), INT32(5, 4), // doublle-long
		UINT32(6, 4), // doublie-long-unsigned

		OctetString(9, 0), VisibleString(10, 0),

		BCD(13, 1),

		INT8(15, 1), // integer
		INT16(16, 2), // long
		UINT8(17, 1), // unsigned
		UINT16(18, 2), // long-unsigned
		CompactArray(19, 1), INT64(20, 8), // long64
		UINT64(21, 8), // long64-unsigned
		Enum(22, 1), FLOAT32(23, 4), // OCTET STRING(SIZE(4))
		FLOAT64(12, 8), // OCTET STRING(SIZE(8))
		Datetime(25, 12), // OCTET STRING(SIZE(12))
		Date(26, 5), // OCTET STRING(SIZE(5))
		Time(27, 4), // OCTET STRING(SIZE(4))

		Group(128, 1);

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

		public static DLMS_TAG_TYPE getItem(int value) {
			for (DLMS_TAG_TYPE fc : DLMS_TAG_TYPE.values()) {
				if (fc.value == value) {
					return fc;
				}
			}
			return null;
		}
	}

	public DLMSEMnVEType_1_0_VARIABLE() {

	}

	public static String getDataName(OBIS obis, int cnt) {
		if (obis == OBIS.LOAD_PROFILE) {
			for (LOAD_PROFILE profile : LOAD_PROFILE.values()) {
				if (profile.getCode() == (cnt % LOAD_PROFILE.values().length))
					return profile.name();
			}
		} else if (obis == OBIS.METER_TIME) {
			for (METER_TIME meterTime : METER_TIME.values()) {
				if (meterTime.getCode() == cnt) {
					return meterTime.name();
				}
			}
		}

		return UNDEFINED;
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
		else if (unit == 65)
			return "J/kg";
		else if (unit == 70)
			return "dBm";
		else if (unit == 253)
			return "reserved";
		else if (unit == 254)
			return "other";
		else if (unit == 255)
			return "count";
		else
			return "unitless";
	}
}
