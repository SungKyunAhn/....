package com.aimir.fep.meter.parser.rdata;


/**
 * 실시간 검침을 파싱하기 위한 코드 정의
 * 
 * @author kaze
 * 
 */
public class RDataConstant {
	/**
	 * 헤더의 구분자
	 * 
	 * @author kaze
	 */
	public enum HeaderType {
		Inventory(0x01),
		MeterConfiguration(0x02),
		MeteringData(0x03);

		private Integer code;

		HeaderType(Integer code) {
			this.code = code;
		}

		public Integer getCode() {
			return code;
		}
	}

	/**
	 * 장비 ID 타입
	 * 
	 * @author kaze
	 */
	public enum InventoryIdType {

		IpV4(0x01), IpV6(0x02), Mac(0x03), Eui64(0x04), SignedNumber(0x11), UnSignedNumber(0x12), ASCII(0x21), BCD(0x22), ByteStream(0x23);

		private Integer code;

		InventoryIdType(Integer code) {
			this.code = code;
		}

		public Integer getCode() {
			return code;
		}
	}

	/**
	 * 코드에 맞는 IDType Enum 타입을 리턴함
	 * 
	 * @param code
	 * @return
	 */
	public static InventoryIdType getInventoryIdType(int code) {
		for (InventoryIdType type : InventoryIdType.values()) {
			if (code == type.getCode()) {
				return type;
			}
		}
		return null;
	}

	/**
	 * 미터 ID 타입
	 * 
	 * @author kaze
	 */
	public enum InventoryMeterIdType {

		ASCII(0x21), BCD(0x22), ByteStream(0x23);

		private Integer code;

		InventoryMeterIdType(Integer code) {
			this.code = code;
		}

		public Integer getCode() {
			return code;
		}
	}

	/**
	 * 코드에 맞는 InventoryMeterIdType Enum 타입을 리턴함
	 * 
	 * @param code
	 * @return
	 */
	public static InventoryMeterIdType getInventoryMeterIdType(int code) {
		for (InventoryMeterIdType type : InventoryMeterIdType.values()) {
			if (code == type.getCode()) {
				return type;
			}
		}
		return null;
	}

	/**
	 * Meter Parser Type
	 * 
	 * @author kaze
	 */
	public enum InventoryParserType {

		Unknown(0x00), Pulse(0x01), Aidon(0x02), Ansi(0x03), Kamstrup(0x04), Repeater(0x05), MBus(0x06), Acd(0x07), SmokeSensor(0x08), DLMS(0x09), Ihd(0x0A), Hmu(0x0B), FireAlarm(0x0C), I210(0x0D), SX(0x0E), Osaki(0x0F), thirdParty(0x10);

		private Integer code;

		InventoryParserType(Integer code) {
			this.code = code;
		}

		public Integer getCode() {
			return code;
		}
	}

	/**
	 * 코드에 맞는 InventoryParserType Enum 타입을 리턴함
	 * 
	 * @param code
	 * @return
	 */
	public static InventoryParserType getInventoryParserType(int code) {
		for (InventoryParserType type : InventoryParserType.values()) {
			if (code == type.getCode()) {
				return type;
			}
		}
		return null;
	}

	/**
	 * 서비스 타입
	 * 
	 * @author kaze
	 * 
	 */
	public enum ServiceType {
		Electricity(1),
		Gas(2),
		Water(3),
		WarmWater(4),
		Cooling(5),
		Heat(6),
		VolumeCorrector(7),
		ColdWater(8),
		SmokeDetector(9);

		private Integer code;

		ServiceType(Integer code) {
			this.code = code;
		}

		public Integer getCode() {
			return this.code;
		}

		public int getIntValue() {
			return this.code.intValue();
		}
	}

	/**
	 * 코드에 맞는 ServiceType Enum 타입을 리턴함
	 * 
	 * @param code
	 * @return
	 */
	public static ServiceType getServiceType(int code) {
		for (ServiceType type : ServiceType.values()) {
			if (code == type.getCode()) {
				return type;
			}
		}
		return null;
	}

	/**
	 * VENDOR
	 * 
	 * @author kaze
	 * 
	 */
	public enum Vendor {
		UNKNOWN(0), GE(2), LANDISGYR(4), LS(6), ACTARIS(8), EDMI(10), NAMJUNSA(12), ABB(14), ILJIN(16), PSTEC(18), SEOCHANG(20), AMRTEC(22), AMSTECH(24), HANKOOKMICRONIC(26), MSM(28), YPP(30), AEG(32), SENSUS(34), KROMSCHRODER(36), MITSUBISHI(
				38), ETC(254), KAMSTRUP(1), ELSTER(3), AIDON(5), WIZIT(7), GASMETER(9), KETI(11), DAEHANJUNSUN(13), GUMHOJUNGI(15), TAEGWANGJUNGI(17), KT(19), CHUNILGAEJUN(21), DMPOWER(23), OMNISYSTEM(25), HYUPSINJUNGI(27), POWERPLUSCOM(29), PYUNGIL(
				31), ANSI(33), ITRON(35), SIEMENS(37), OSAKI(39), NURI(255);

		private Integer code;

		Vendor(Integer code) {
			this.code = code;
		}

		public Integer getCode() {
			return this.code;
		}

		public int getIntValue() {
			return this.code.intValue();
		}
	}

	/**
	 * 코드에 맞는 Vendor Enum 타입을 리턴함
	 * 
	 * @param code
	 * @return
	 */
	public static Vendor getVendor(int code) {
		for (Vendor type : Vendor.values()) {
			if (code == type.getCode()) {
				return type;
			}
		}
		return null;
	}

	/**
	 * Value 값의 종류
	 * 
	 * @author kaze
	 */
	public enum ConfigurationValueType {

		DifferenceValue(0x01, "DifferenceValue"), CurrentValue(0x02, "CurrentValue");
		private Integer code;
		private String name;

		ConfigurationValueType(Integer code,String name) {
			this.code = code;
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}
		public String getName() {
			return name;
		}
	}
	
	/**
	 * 대상 객체 타입
	 * @author kaze
	 *
	 */
	public enum ConfigurationObjectType {

		Abstract(0x00,"Abstract"), ElectricityRelated(0x01,"ElectricityRelated"), HeatCostAllocatorRelated(0x04,"HeatCostAllocatorRelated"), CoolingRelated(0x05,"CoolingRelated"),
		HeatRelated(0x06,"HeatRelated"), GasRelated(0x07,"GasRelated"), ColdWaterRelated(0x08,"ColdWaterRelated"), HotWaterRelated(0x09,"HotWaterRelated");
		private Integer code;
		private String name;

		ConfigurationObjectType(Integer code, String name) {
			this.code = code;
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}
		
		public String getName() {
			return name;
		}
	}
	
	/**
	 * 전기 계량기의 Channel 타입
	 * @author kaze
	 *
	 */
	public enum ConfigurationElectricityChannelType {
		Sum_Li_Active_Power_Q1_Q4(1,"Sum_Li_Active_Power_Q1_Q4"),
		Sum_Li_Active_Power_Q2_Q3(2,"Sum_Li_Active_Power_Q2_Q3"),
		Sum_Li_Reactive_Power_Q1_Q2(3,"Sum_Li_Reactive_Power_Q1_Q2"),
		Sum_Li_Reactive_Power_Q3_Q4(4,"Sum_Li_Reactive_Power_Q3_Q4"),
		Sum_Li_Reactive_Power_Q1(5,"Sum_Li_Reactive_Power_Q1"),
		Sum_Li_Reactive_Power_Q2(6,"Sum_Li_Reactive_Power_Q2"),
		Sum_Li_Reactive_Power_Q3(7,"Sum_Li_Reactive_Power_Q3"),
		Sum_Li_Reactive_Power_Q4(8,"Sum_Li_Reactive_Power_Q4"),
		Sum_Li_Apparent_Power_Q1_Q4(9,"Sum_Li_Apparent_Power_Q1_Q4"),
		Sum_Li_Apparent_Power_Q2_Q3(10,"Sum_Li_Apparent_Power_Q2_Q3"),
		Current_Any_Phase(11,"Current_Any_Phase"),
		Voltage_Any_Phase(12,"Voltage_Any_Phase"),
		Sum_Li_Power_Factor(13,"Sum_Li_Power_Factor"),
		Supply_Frequency(14,"Supply_Frequency"),		
		Sum_Li_Active_Power_Plus(15,"Sum_Li_Active_Power_Plus"),
		Sum_Li_Active_Power_Minus(16,"Sum_Li_Active_Power_Minus"),
		Sum_Li_Active_Power_Q1(17,"Sum_Li_Active_Power_Q1"),
		Sum_Li_Active_Power_Q2(18,"Sum_Li_Active_Power_Q2"),
		Sum_Li_Active_Power_Q3(19,"Sum_Li_Active_Power_Q3"),
		Sum_Li_Active_Power_Q4(20,"Sum_Li_Active_Power_Q4"),
		
		L1_Active_Power_Q1_Q4(21,"L1_Active_Power_Q1_Q4"),
		L1_Active_Power_Q2_Q3(22,"L1_Active_Power_Q2_Q3"),
		L1_Reactive_Power_Q1_Q2(23,"L1_Reactive_Power_Q1_Q2"),
		L1_Reactive_Power_Q3_Q4(24,"L1_Reactive_Power_Q3_Q4"),
		L1_Reactive_Power_Q1(25,"L1_Reactive_Power_Q1"),
		L1_Reactive_Power_Q2(26,"L1_Reactive_Power_Q2"),
		L1_Reactive_Power_Q3(27,"L1_Reactive_Power_Q3"),
		L1_Reactive_Power_Q4(28,"L1_Reactive_Power_Q4"),
		L1_Apparent_Power_Q1_Q4(29,"L1_Apparent_Power_Q1_Q4"),
		L1_Apparent_Power_Q2_Q3(30,"L1_Apparent_Power_Q2_Q3"),
		L1_Current(31,"L1_Current"),
		L1_Voltage(32,"L1_Voltage"),
		L1_Power_Factor(33,"L1_Power_Factor"),
		L1_Supply_Frequency(34,"L1_Supply_Frequency"),
		L1_Active_Power_Plus(35,"L1_Active_Power_Plus"),
		L1_Active_Power_Minus(36,"L1_Active_Power_Minus"),
		L1_Active_Power_Q1(37,"L1_Active_Power_Q1"),
		L1_Active_Power_Q2(38,"L1_Active_Power_Q2"),
		L1_Active_Power_Q3(39,"L1_Active_Power_Q3"),
		L1_Active_Power_Q4(40,"L1_Active_Power_Q4"),
		
		L2_Active_Power_Q1_Q4(41,"L2_Active_Power_Q1_Q4"),
		L2_Active_Power_Q2_Q3(42,"L2_Active_Power_Q2_Q3"),
		L2_Reactive_Power_Q1_Q2(43,"L2_Reactive_Power_Q1_Q2"),
		L2_Reactive_Power_Q3_Q4(44,"L2_Reactive_Power_Q3_Q4"),
		L2_Reactive_Power_Q1(45,"L2_Reactive_Power_Q1"),
		L2_Reactive_Power_Q2(46,"L2_Reactive_Power_Q2"),
		L2_Reactive_Power_Q3(47,"L2_Reactive_Power_Q3"),
		L2_Reactive_Power_Q4(48,"L2_Reactive_Power_Q4"),
		L2_Apparent_Power_Q1_Q4(49,"L2_Apparent_Power_Q1_Q4"),
		L2_Apparent_Power_Q2_Q3(50,"L2_Apparent_Power_Q2_Q3"),
		L2_Current(51,"L2_Current"),
		L2_Voltage(52,"L2_Voltage"),
		L2_Power_Factor(53,"L2_Power_Factor"),
		L2_Supply_Frequency(54,"L2_Supply_Frequency"),
		L2_Active_Power_Plus(55,"L2_Active_Power_Plus"),
		L2_Active_Power_Minus(56,"L2_Active_Power_Minus"),
		L2_Active_Power_Q1(57,"L2_Active_Power_Q1"),
		L2_Active_Power_Q2(58,"L2_Active_Power_Q2"),
		L2_Active_Power_Q3(59,"L2_Active_Power_Q3"),
		L2_Active_Power_Q4(60,"L2_Active_Power_Q4"),
		
		L3_Active_Power_Q1_Q4(61,"L3_Active_Power_Q1_Q4"),
		L3_Active_Power_Q2_Q3(62,"L3_Active_Power_Q2_Q3"),
		L3_Reactive_Power_Q1_Q2(63,"L3_Reactive_Power_Q1_Q2"),
		L3_Reactive_Power_Q3_Q4(64,"L3_Reactive_Power_Q3_Q4"),
		L3_Reactive_Power_Q1(65,"L3_Reactive_Power_Q1"),
		L3_Reactive_Power_Q2(66,"L3_Reactive_Power_Q2"),
		L3_Reactive_Power_Q3(67,"L3_Reactive_Power_Q3"),
		L3_Reactive_Power_Q4(68,"L3_Reactive_Power_Q4"),
		L3_Apparent_Power_Q1_Q4(69,"L3_Apparent_Power_Q1_Q4"),
		L3_Apparent_Power_Q2_Q3(70,"L3_Apparent_Power_Q2_Q3"),
		L3_Current(71,"L3_Current"),
		L3_Voltage(72,"L3_Voltage"),
		L3_Power_Factor(73,"L3_Power_Factor"),
		L3_Supply_Frequency(74,"L3_Supply_Frequency"),
		L3_Active_Power_Plus(75,"L3_Active_Power_Plus"),
		L3_Active_Power_Minus(76,"L3_Active_Power_Minus"),
		L3_Active_Power_Q1(77,"L3_Active_Power_Q1"),
		L3_Active_Power_Q2(78,"L3_Active_Power_Q2"),
		L3_Active_Power_Q3(79,"L3_Active_Power_Q3"),
		L3_Active_Power_Q4(80,"L3_Active_Power_Q4"),
		
		Angles(81,"Angles"),
		Unitless_Quantities(82,"Unitless_Quantities"),
		Sum_Li_Power_Factor_Minus(84,"Sum_Li_Power_Factor_Minus"),
		L1_Power_Factor_Minus(85,"L1_Power_Factor_Minus"),
		L2_Power_Factor_Minus(86,"L2_Power_Factor_Minus"),
		L3_Power_Factor_Minus(87,"L3_Power_Factor_Minus"),
		Sum_LiA2h_Q1_Q2_Q3_Q4(88,"Sum_LiA2h_Q1_Q2_Q3_Q4"),
		Sum_LiV2h_Q1_Q2_Q3_Q4(89,"Sum_LiV2h_Q1_Q2_Q3_Q4"),
		SigmaCurrent(90,"SigmaCurrent"),
		L0_Current(91,"L0_Current"),
		L0_Voltage(92,"L0_Voltage");
		
		private Integer code;
		private String name;

		ConfigurationElectricityChannelType(Integer code, String name) {
			this.code = code;
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}
		
		public String getName() {
			return name;
		}
	}
	
	/**
	 * 실시간 검침 데이터 포맷의 Unit Enum 타입
	 * @author kaze
	 *
	 */
	public enum ConfigurationUnit {
		YEAR(1,"a"),
		MONTH(2,"mo"),
		WEEK(3,"wk"),
		DAY(4,"d"),
		HOUR(5,"h"),
		MIN(6,"min"),
		SECOND(7,"s"),
		DEGREE(8,"degree"),
		DEGREECELSIUS(9,"dc"),
		CURRENCY(10,"currency"),
		METER(11,"m"),
		METERPERSEC(12,"m/s"),		
		CUBICMETER(14,"m3"),
		CUBICMETERPERHOUR(16,"m3/h"),
		LITRE(19,"l"),
		KILOGRAM(20,"kg"),
		NEWTON(21,"N"),
		NEWTONMETER(22,"Nm"),
		PASCAL(23,"Pa"),
		BAR(24,"bar"),
		JOULE(25,"J"),
		JOULEPERHOUR(26,"J/h"),
		WATT(27,"W"),
		VOLTAMPERE(28,"VA"),
		VAR(29,"var"),
		WATTHOUR(30,"Wh"),
		VOLTAMPEREHOUR(31,"VAh"),
		VARHOURAMPEREHOUR(32,"varh"),
		AMPERE(33,"A"),
		COULOMB(34,"C"),
		VOLT(35,"V"),
		VOLTPERMETER(36,"V/m"),
		FARAD(37,"F"),
		OHM(38,"ohm"),
		OHMPERMETER(39,"ohm2/m"),
		WEBER(40,"Wb"),
		TESLA(41,"T"),
		AMPEREPERMETRE(42,"A/m"),
		HENRY(43,"H"),
		HERTZ(44,"Hz"),
		ONEPERWH(45,"1/(Wh)"),
		ONEPERVARH(46,"1/(varh)"),
		ONEPERVAH(47,"1/(VAh)"),
		VOLTSQUAREDHOURS(48,"V2h"),
		AMPERESQUAREDHOURS(49,"A2h"),
		KILOGRAMPERSEC(50,"kg/s"),
		SIEMENS(51,"S,mho"),
		KELVIN(52,"K"),
		ONEPERV2H(53,"1/(V2h)"),
		ONEPERA2H(54,"1/(A2h)"),
		ONEPERM3(55,"1/m3"),
		PERCENT(56,"%"),
		AMPEREHOUR(57,"Ah"),
		WHPERM3(60,"Wh/m3"),
		JULEPERM3(61,"J/m3"),
		MOLEPERCENT(62,"Mol%"),
		GPERM(63,"g/m3"),
		PASCALSECOND(64,"Pa.s");
		
		private Integer code;
		private String name;

		ConfigurationUnit(Integer code, String name) {
			this.code = code;
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}
		public String getName() {
			return name;
		}
	}
	
	/**
	 * 코드에 맞는 ConfigurationValueType Enum 타입을 리턴함
	 * 
	 * @param code
	 * @return
	 */
	public static ConfigurationValueType getConfigurationValueType(int code) {
		for (ConfigurationValueType type : ConfigurationValueType.values()) {
			if (code == type.getCode()) {
				return type;
			}
		}
		return null;
	}
	
	/**
	 * 코드에 맞는 ConfigurationObjectType Enum 타입을 리턴함
	 * @param code
	 * @return
	 */
	public static ConfigurationObjectType getConfigurationObjectType(int code) {
		for (ConfigurationObjectType type : ConfigurationObjectType.values()) {
			if (code == type.getCode()) {
				return type;
			}
		}
		return null;
	}
	
	/**
	 * 코드에 맞는 Electricity Channel Type Enum 타입을 리턴함
	 * @param code
	 * @return
	 */
	public static ConfigurationElectricityChannelType getConfigurationElectricityChannelType(int code) {
		for (ConfigurationElectricityChannelType type : ConfigurationElectricityChannelType.values()) {
			if (code == type.getCode()) {
				return type;
			}
		}
		return null;
	}
	
	public static ConfigurationUnit getConfigurationUnit(int code) {
		for (ConfigurationUnit type : ConfigurationUnit.values()) {
			if (code == type.getCode()) {
				return type;
			}
		}
		return null;
	}

	/**
	 * Log의 유형
	 * 
	 * @author kaze
	 */
	public enum MeteringCategoryType {

	    NSI76(76,13),SX217(217,10),SX218(218,10),SX219(219,10),SX220(220,10),SX221(221,16),SX232(232,10);

        private Integer code;
        private Integer length;     

        MeteringCategoryType(Integer code, Integer length) {
            this.code = code;
            this.length = length;           
        }

        public Integer getCode() {
            return code;
        }
        public Integer getLength() {
            return length;
        }   
	}
	
	/**
	 * 코드에 맞는 ConfigurationValueType Enum 타입을 리턴함
	 * 
	 * @param code
	 * @return
	 */
	public static MeteringCategoryType getMeteringCategoryType(int code) {
		for (MeteringCategoryType type : MeteringCategoryType.values()) {
			if (code == type.getCode()) {
				return type;
			}
		}
		return null;
	}
}
