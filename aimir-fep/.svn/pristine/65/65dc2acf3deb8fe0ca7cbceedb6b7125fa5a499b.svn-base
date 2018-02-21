package com.aimir.fep.protocol.emnv.frame;

import com.aimir.fep.util.DataUtil;

/**
 * (@)# EMnVGeneralDataConstants.java
 *
 * 2015. 4. 21.
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

/**
 * @author simhanger
 *
 */
public class EMnVConstants {
	/**
	 * Field Length (Byte)
	 */
	public static final int SOF_LEN = 3;
	public static final int AUTH_LEN = 15;
	public static final int FRAME_TYPE_LEN = 1;
	public static final int FRAME_CONTROL_LEN = 2;
	public static final int SEQUENCE_LEN = 1;
	public static final int LENGTH_LEN = 4;
	public static final int PAYLOAD_LEN = 4;
	public static final int CRC32_LEN = 4;
	
	public static final int OBIS_LIST_LEN = 1;      // 미터의 수집된 전체 OBIS의 개수를 담고있는 바이트배열의 길이
	public static final int OBIS_MDDATA_LEN = 2;    // 미터링데이터의 전체 Data 길이를 담고있는 바이트배열의 길이
	
	
	
	public static final int MODBUS_DATA_COUNT_LEN = 2;
	public static final int MODBUS_ADDRESS_LEN = 2;
	public static final int MODBUS_DATA_LEN = 2;     
	
	
	public static final int MODEBUS_PORT_NUM_LEN = 1;    //Port Number 길이
	public static final int MODBUS_VENDOR_TYPE_LEN = 1;
	public static final int MODEBUS_INVERTER_MODEL_LEN = 2;  // Inverter 데이터시트의 메모리주소 00번지 값. 인버터 모델 구분.
	public static final int MODBUS_STATION_BYTE_LEN = 1;
	public static final int MODEBUS_DATA_TIME_LEN = 6;   // Data Time 길이
	public static final int MODEBUS_FREQUENCY_LEN = 2;   // Inverter 평균주파수 길이
	public static final int MODEBUS_VOLTAGE_LEN = 2;     // Inverter 평균 출력 전압 길이
	public static final int MODEBUS_CURRENT_LEN = 2;     // Inverter 평균 출력 전류 길이
	
	public static final int COMPENSATOR_PORT_NUM_LEN = 1;   
	public static final int COMPENSATOR_VENDOR_TYPE_LEN = 1;
	public static final int COMPENSATOR_MODEL_LEN = 2; 
	public static final int COMPENSATOR_STATION_BYTE_LEN = 1;
	public static final int COMPENSATOR_DATA_TIME_LEN = 6;  
	public static final int COMPENSATOR_UNCORRECTED_ACCUMULATION_LEN = 4;   // 비보정적산
	public static final int COMPENSATOR_CORRECTED_ACCUMULATION_LEN = 4;     // 보정적산
	public static final int COMPENSATOR_PRESSURE_LEN = 4;                   // 압력
	public static final int COMPENSATOR_TEMPERATURE_LEN = 4;                // 온도
	public static final int COMPENSATOR_CORRECTION_FACTOR_LEN = 4;          // 보정계수
	

	public enum General {
		SOF(new byte[] { (byte) 0x4B, (byte) 0x45, (byte) 0x50 }, "KEP", 3), // byte배열, 값, 바이트수
		UNKNOWN(new byte[] { (byte) 0x55, (byte) 0x4E, (byte) 0x4B }, "UNK", 3);

		private byte[] data;
		private String name;
		private int length;

		private General(byte[] data, String name, int length) {
			this.data = data;
			this.name = name;
			this.length = length;
		}

		public byte[] getData() {
			return data;
		}

		public String getString() {
			return name;
		}

		public int getValue() {
			return length;
		}

		public static General getItem(String value) {
			for (General fc : General.values()) {
				if (fc.name.equals(value)) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	/**
	 * Frame Type
	 */
	public enum FrameType {
		  LINK_FRAME((byte) 0x00, 0), ACK_NAK_FRAME((byte) 0x01, 1)
		, COMMAND_FRAME((byte) 0x02, 2), METERING_DATA_FRAME((byte) 0x03, 3)
		, OTA_FRAME((byte) 0x04, 4), ZIGBEE_INTERFACE_FRAME((byte) 0x05, 5)
		, SUBGIGA_INTERFACE_FRAME((byte) 0x07, 7), UNKNOWN((byte) 0xff, 255);

		private byte data;
		private int length;

		private FrameType(byte data, int length) {
			this.data = data;
			this.length = length;
		}
		
		public byte getData() {
			return data;
		}

		public int getValue() {
			return length;
		}

		public static FrameType getItem(int value) {
			for (FrameType fc : FrameType.values()) {
				if (fc.length == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	/**
	 * Frame Control - ADDRESS TYPE
	 */
	public enum FrameControlAddr {
		NON_ADDRES(0, 0), IPv4(1, 4), IPv6(2, 16), MOBILE_NUMBER(3, 16), EUI64(4, 8), UNKNOWN(99999, 0); // ADDRESS TYPE 

		private int type;
		private int length;

		private FrameControlAddr(int type, int length) {
			this.type = type;
			this.length = length;
		}

		public int getValue() {
			return type;
		}

		public int getLength() {
			return length;
		}

		public static FrameControlAddr getItem(int value) {
			for (FrameControlAddr fc : FrameControlAddr.values()) {
				if (fc.type == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	/**
	 * Frame Control - SECURITY
	 */
	public enum FrameControlSecurity {
		SECURITY_DISABLE(0), SECURITY_ENABLE(1), UNKNOWN(99999);

		private int type;

		private FrameControlSecurity(int type) {
			this.type = type;
		}

		public int getValue() {
			return type;
		}

		public static FrameControlSecurity getItem(int value) {
			for (FrameControlSecurity fc : FrameControlSecurity.values()) {
				if (fc.type == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	/**
	 * Frame Control - ACK REQUEST
	 */
	public enum FrameControlAck {
		ACK_REQ_DISABLE(0), ACK_REQ_ENABLE(1), UNKNOWN(99999);

		private int type;

		private FrameControlAck(int type) {
			this.type = type;
		}

		public int getValue() {
			return type;
		}

		public static FrameControlAck getItem(int value) {
			for (FrameControlAck fc : FrameControlAck.values()) {
				if (fc.type == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}


	/****************************
	 * 3.3.3 Link Frame
	 ***************************/
	/**
	 * EMnV Sub Frame Type
	 */
	public enum EMnVLinkSubFrameType {
		LINK_REQUEST((byte) 0x00, 1), LINK_RESPONSE((byte) 0x01, 1); 
		

		private byte type;
		private int length;

		private EMnVLinkSubFrameType(byte type, int length) {
			this.type = type;
			this.length = length;
		}

		public int getValue() {
			return type;
		}

		public int getLength() {
			return length;
		}

		public static EMnVLinkSubFrameType getItem(byte value) {
			for (EMnVLinkSubFrameType fc : EMnVLinkSubFrameType.values()) {
				if (fc.type == value) {
					return fc;
				}
			}
			return null;
		}
	}
	
	/**
	 * EMnV OTA SubFrame Type
	 */
	public enum EMnVOTASubFrameType {
		  UPGRADE_START_REQUEST((byte) 0x00, "Upgrade Start Request")
		, UPGRADE_START_RESPONSE((byte) 0x01, "Upgrade Start Response")
		, UPGRADE_DATA((byte) 0x02, "Upgrade Data")
		, UPGRADE_END_REQUEST((byte) 0x03, "Upgrade End Request")
		, UPGRADE_END_RESPONSE((byte) 0x04, "Upgrade End Response")
		, UPGRADE_START_REQUEST_ROUTER_NODE((byte) 0x05, "Router Node Upgrade Start Request")
		, UPGRADE_START_RESPONSE_ROUTER_NODE((byte) 0x06, "Router Node Upgrade Start Response")
		, UPGRADE_END_REQUEST_ROUTER_NODE((byte) 0x07, "Router Node Upgrade End Request")
		, UPGRADE_END_RESPONSE_ROUTER_NODE((byte) 0x08, "Router Node Upgrade End Response")
		, UPGRADE_FINISHED_ROUTER_NODE((byte) 0x09, "Router Node Finished")
		, UNKNOWN((byte) 0xff, "Unknown");

		private byte data;
		private String desc;

		private EMnVOTASubFrameType(byte data, String desc) {
			this.data = data;
			this.desc = desc;
		}

		public byte getData() {
			return data;
		}

		public String getString() {
			return desc;
		}

		public static EMnVOTASubFrameType getItem(byte value) {
			for (EMnVOTASubFrameType fc : EMnVOTASubFrameType.values()) {
				if (fc.data == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}
	
	
	/**
	 * EMnV Commnad SubFrame Type
	 */
	public enum EMnVCommandSubFrameType {
		  COMMAND_REQUEST((byte) 0x00, "Upgrade Start Request")
		, COMMAND_RESPONSE((byte) 0x01, "Upgrade Start Response")
		, UNKNOWN((byte) 0xff, "Unknown");

		private byte data;
		private String desc;

		private EMnVCommandSubFrameType(byte data, String desc) {
			this.data = data;
			this.desc = desc;
		}

		public byte getData() {
			return data;
		}

		public String getString() {
			return desc;
		}

		public static EMnVCommandSubFrameType getItem(byte value) {
			for (EMnVCommandSubFrameType fc : EMnVCommandSubFrameType.values()) {
				if (fc.data == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}
	/****************************
	 * 3.3.6 Metering Data Frame
	 ***************************/
	/**
	 * EMnV Meter Type
	 */
	public enum EMnVMeterType {
		G_TYPE((byte) 0x03, 1), E_TYPE_1_1((byte) 0x04, 1), E_TYPE_1_0((byte) 0x05, 1), MODBUS((byte) 0x08, 1), INVERTER_LOG((byte) 0x09, 1), DCU((byte) 0xff, 1),
		COMPENSATOR((byte) 0x10,1),
		LTE_G_TYPE((byte) 0x13, 1), ZIGBEE_G_TYPE((byte) 0x23, 1), SUBGIGA_G_TYPE((byte) 0x33, 1), 
		LTE_E_TYPE_1_1((byte) 0x14, 1), ZIGBEE_E_TYPE_1_1((byte) 0x24, 1), SUBGIGA_E_TYPE_1_1((byte) 0x34, 1),
		LTE_E_TYPE_1_0((byte) 0x15, 1), ZIGBEE_E_TYPE_1_0((byte) 0x25, 1), SUBGIGA_E_TYPE_1_0((byte) 0x35, 1), 
		LTE_MODBUS((byte) 0x18, 1), ZIGBEE_MODBUS((byte) 0x28, 1), SUBGIGA_MODBUS((byte) 0x38, 1),
		LTE_INVERTER_LOG((byte) 0x19, 1), ZIGBEE_INVERTER_LOG((byte) 0x29, 1), SUBGIGA_INVERTER_LOG((byte) 0x39, 1); 		

		private byte type;
		private int length;

		private EMnVMeterType(byte type, int length) {
			this.type = type;
			this.length = length;
		}

		public int getValue() {
			return type;
		}

		public int getLength() {
			return length;
		}

		public static EMnVMeterType getItem(byte value) {
			for (EMnVMeterType fc : EMnVMeterType.values()) {
				if (fc.type == value) {
					return fc;
				}
			}
			return null;
		}
	}
	
	/**
	 * EMnV Modem Info Type
	 */
//	public enum EMnVModemInfoType {
//		DCU("DCU"), SUB_GIGA("SUB_GIGA"), ZIGBEE("ZIGBEE");
//
//		private String desc;
//
//		private EMnVModemInfoType(String desc) {
//			this.desc = desc;
//		}
//
//		public String getDesc() {
//			return desc;
//		}
//
//		public static EMnVModemInfoType getItem(String value) {
//			for (EMnVModemInfoType fc : EMnVModemInfoType.values()) {
//				if (fc.desc.equals(value)) {
//					return fc;
//				}
//			}
//			return null;
//		}
//	}

	/**
	 * EMnV Metering Data Type
	 */
	public enum EMnVMeteringDataType {
		BILLING((byte) 0x00, 1), LOAD_PROFILE((byte) 0x01, 1);

		private byte type;
		private int length;

		private EMnVMeteringDataType(byte type, int length) {
			this.type = type;
			this.length = length;
		}

		public int getValue() {
			return DataUtil.getIntToByte(type);
		}

		public int getLength() {
			return length;
		}

		public static EMnVMeteringDataType getItem(byte value) {
			for (EMnVMeteringDataType fc : EMnVMeteringDataType.values()) {
				if (fc.type == value) {
					return fc;
				}
			}
			return null;
		}
	}

	/**
	 * EMnV Metering Data Status
	 */
	public enum EMnVMeteringDataSatus {
		NOMAL((byte) 0x00, "모뎀정상", 1), METERING_FAILE((byte) 0x01, "검침실패", 1), BLACK_OUT((byte) 0x02, "정전발생", 1);

		private byte type;
		private String desc;
		private int length;

		private EMnVMeteringDataSatus(byte type, String desc, int length) {
			this.type = type;
			this.desc = desc;
			this.length = length;
		}

		public int getValue() {
			return type;
		}

		public String getDesc() {
			return desc;
		}
		
		public int getLength() {
			return length;
		}

		public static EMnVMeteringDataSatus getItem(byte value) {
			for (EMnVMeteringDataSatus fc : EMnVMeteringDataSatus.values()) {
				if (fc.type == value) {
					return fc;
				}
			}
			return null;
		}
	}
	
	
	public enum EMnVModebusVendorType{
		LS((byte)0x00, "LS 산전"), HYUNDAI((byte)0x01, "현대중공업"), ROCKWELL((byte)0x02, "로크웰"), DANFOSS((byte)0x03, "댄포스"), DEFAULT((byte)0x09, "표준");
		
		private byte type;
		private String desc;

		private EMnVModebusVendorType(byte type, String desc) {
			this.type = type;
			this.desc = desc;
		}

		public int getValue() {
			return type;
		}

		public String getDesc() {
			return desc;
		}
		
		public static EMnVModebusVendorType getItem(byte value) {
			for (EMnVModebusVendorType fc : EMnVModebusVendorType.values()) {
				if (fc.type == value) {
					return fc;
				}
			}
			return null;
		}
	}
	
	public enum EMnVModebusVendorModelType{
		LS_IP5A("0009", "SV-iP5A/iV5"), LS_IS7("000B", "SV-iS7"), DEFAULT("9999", "미등록 모델");
		// 0000, 0013으로 올라오는것도 있음.
		
		private String type;
		private String desc;

		private EMnVModebusVendorModelType(String type, String desc) {
			this.type = type;
			this.desc = desc;
		}

		public String getValue() {
			return type;
		}

		public String getDesc() {
			return desc;
		}
		
		public static EMnVModebusVendorModelType getItem(String value) {
			for (EMnVModebusVendorModelType fc : EMnVModebusVendorModelType.values()) {
				if (fc.type.equals(value)) {
					return fc;
				}
			}
			return EMnVModebusVendorModelType.DEFAULT;
		}
	}
	
	public enum EMnVCompensatorVendorType{
		GMC((byte)0x00, "GMC Global VMS Series");
		
		private byte type;
		private String desc;

		private EMnVCompensatorVendorType(byte type, String desc) {
			this.type = type;
			this.desc = desc;
		}

		public int getValue() {
			return type;
		}

		public String getDesc() {
			return desc;
		}
		
		public static EMnVCompensatorVendorType getItem(byte value) {
			for (EMnVCompensatorVendorType fc : EMnVCompensatorVendorType.values()) {
				if (fc.type == value) {
					return fc;
				}
			}
			return null;
		}
	}
	
	public enum EMnVCompensatorVendorModelType{
		DEFAULT("0000", "default model");
		// 0000, 0013으로 올라오는것도 있음.
		
		private String type;
		private String desc;

		private EMnVCompensatorVendorModelType(String type, String desc) {
			this.type = type;
			this.desc = desc;
		}

		public String getValue() {
			return type;
		}

		public String getDesc() {
			return desc;
		}
		
		public static EMnVCompensatorVendorModelType getItem(String value) {
			for (EMnVCompensatorVendorModelType fc : EMnVCompensatorVendorModelType.values()) {
				if (fc.type.equals(value)) {
					return fc;
				}
			}
			return EMnVCompensatorVendorModelType.DEFAULT;
		}
	}
	
	
}
