/**
 * (@)# ModbusInverterIS7Avariable.java
 *
 * 2015. 11. 10.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.meter.parser.ModbusInverterTable;


/**
 * @author simhanger
 *
 */
public class ModbusInverterIS7variable {
	/**
	 * 출력전류, 출력 주파수, 출력 전압만 정보로 이용하며
	 * 차후 더 필요한 정보가 있을경우 해당정보를 파싱하는 부분을 
	 * 추가로 구현하여 이용한다.
	 *  
	 * @author simhanger
	 */
    public enum MODBUS_LS_IS7_CODE {
    	// 이름, 단위, 유닛, 설명
    	INVERTER_MODEL("0000", null, null, "인버터 모델"),  
    	INVERTER_CAPACITY("0001", null, "kW", "인버터 용량"),  
        INVERTER_INPUT_VOLTAGE("0002", null, "V", "인버터 입력 전압"),
        VERSION("0003", null, "Ver", "버전"),
        FREQUENCY_DIRECTION("0005", "0.01", "Hz", "주파수 지령"),
        //0004 : Reserved
        DRIVING_DIRECTION("0006", null, null, "운전 지령"),
        ACCELERATION_TIME("0007", "0.1", "sec", "가속 시간"),
        DECELERATION_TIME("0008", "0.1", "sec", "감속 시간"),

        OUTPUT_CURRENT("0009", "0.1", "A", "출력 전류"),
        OUTPUT_FREQUENCY("000A", "0.01", "Hz", "출력 주파수"),
        OUTPUT_VOLTAGE("000B", "1", "V", "출력 전압"),
        
        DC_LINK_VOLTAGE("000C", "1", "V", "DC Link 전압"),
        OUTPUT_POWER("000D", "0.1", "Kw", "출력 파워"),
        DRIVING_STATE("000E", null, null, "운전 상태"),
        TRIP_INFO("000F", null, null, "트립 정보"),
        INPUT_TERMINAL_INFO("0010", null, null, "입력 단자 정보"),
        OUTPUT_TERMINAL_INFO("0011", null, null, "출력 단자 정보"),
        V1("0012", "0.01", "%", "V1 전압입력"),
        V2("0013", "0.01", "%", "V2 전압입력"),
        I1("0014", "0.01", "%", "I1 전류입력"),
        RPM("0015", "1", "rpm", "RPM"),
        // 0016 ~ 0019 : Reserved
        UNIT("001A", null, null, "Hz/rpm 선택"),
        POLE("001B", null, null, "선택된 모터 극수 표시"),
        
    	DATE("", null, null, "날짜");// Inverter는 LP가 없지만 시간 정보를 입력하기 위해 사용한다.
        
    	private String code;
        private String unitConst;
        private String unit;
        private String desc;
        
        MODBUS_LS_IS7_CODE(String code, String unitConst, String unit, String desc) {
            this.code = code;
            this.unitConst = unitConst;
            this.unit = unit;
            this.desc = desc;
        }
        
        public String getCode() {
			return code;
		}

		public String getUnitConst() {
			return unitConst;
		}

		public String getUnit() {
			return unit;
		}

		public String getDesc() {
			return desc;
		}

		public static MODBUS_LS_IS7_CODE getItem(String code) {
            for (MODBUS_LS_IS7_CODE mCode : values()) {
                if (mCode.getCode().equals(code)) return mCode;
            }
            return null;
        }
    }
	
    // 인버터 용량
    public enum INVERTER_CAPACITY {
        IC_0_75(0, "0.75"), 
        IC_1_5(1, "1.5"),
        IC_2_2(2, "2.2"),
        IC_3_7(3, "3.7"),
        IC_5_5(4, "5.5"),
        IC_7_5(5, "7.5"),
        IC_11(6, "11"),
        IC_15(7, "15"),
        IC_18_5(8, "18.5"),
        IC_22(9, "22"),
        IC_30(10, "30"),
        IC_37(11, "37"),
        IC_45(12, "45"),
        IC_55(13, "55"),
        IC_75(14, "75"),
        IC_90(15, "90"),
        IC_110(16, "110"),
        IC_132(17, "132"),
        IC_160(18, "160"),
        IC_200(19, "200"),
        IC_220(20, "220"),
        IC_280(21, "280"),
        IC_375(22, "375"),
        IC_0_4(65535, "0.4");
        
        int codeValue;
        String name;
        
        INVERTER_CAPACITY(int codeValue, String name) {
            this.codeValue = codeValue;
            this.name = name;
        }
        
        public int getCodeValue() {
			return codeValue;
		}

		public String getName() {
			return name;
		}

		public static INVERTER_CAPACITY getValue(int codeValue) {
            for (INVERTER_CAPACITY a : INVERTER_CAPACITY.values()) {
                if (a.getCodeValue() == codeValue)
                    return a;
            }
            return null;
        }
    }
    
    //인버터 입력 전압
    public enum INVERTER_INPUT_VOLTAGE {
        IIV_220(0, "220"), 
        IIV_440(1, "440");
        
        int codeValue;
        String name;
        
        INVERTER_INPUT_VOLTAGE(int codeValue, String name) {
            this.codeValue = codeValue;
            this.name = name;
        }
        
        public int getCodeValue() {
			return codeValue;
		}

		public String getName() {
			return name;
		}

		public static INVERTER_INPUT_VOLTAGE getValue(int codeValue) {
            for (INVERTER_INPUT_VOLTAGE a : INVERTER_INPUT_VOLTAGE.values()) {
                if (a.getCodeValue() == codeValue)
                    return a;
            }
            return null;
        }
    }
}
