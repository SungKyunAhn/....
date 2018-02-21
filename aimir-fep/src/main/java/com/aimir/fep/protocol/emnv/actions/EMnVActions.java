/**
 * (@)# EMnVActions.java
 *
 * 2015. 6. 22.
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
package com.aimir.fep.protocol.emnv.actions;

import org.apache.mina.core.session.IoSession;

import com.aimir.fep.protocol.emnv.frame.EMnVGeneralDataFrame;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVGeneralFramePayLoad;

/**
 * @author simhanger
 *
 */
public abstract class EMnVActions {
	/**
	 * Action Type
	 */
	public enum EMnVActionType {
		  OTA(0), COMMAND(1), EOT(2), UNKNOWN(9999);

		private int type;

		private EMnVActionType(int type) {
			this.type = type;
		}

		public int getValue() {
			return type;
		}

		public static EMnVActionType getItem(int value) {
			for (EMnVActionType fc : EMnVActionType.values()) {
				if (fc.type == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	/**
	 * ActionCommand Type
	 */
	public enum ActionCommandType {
		  SERVER_IP(1, 4, "AMI server IP address 설정. Hex값 4byte")
		, SERVER_PORT(2, 2, "AMI server PORT 설정. Hex값 2byte")
		, LP_INTERVAL(3, 1, "검침주기 = 분/15분. Hex값 1byte")
		, HW_RESET_INTERVAL(4, 1, "H/W 리셋주기 = 분/60분. Hex값 1byte")
		, NV_RESET(7, 0, "모뎀 NV초기화")
		, M_NUMBER(8, 16, "Mobile number. 문자열 16자리. 상위 5개bit는 Hex값으로 0")
		, HW_RESET(12, 0, "H/W 리셋")
		, EVENT_LOG(16, -1, "이벤트 로그")                   // -1 : N
		, KEY_CHANGE(18, 16, "고정키 변경. 문자열 16자리")
		, ON_DEMAND(21, 3, "LTE모뎀 Selective LP")
		, METER_TIMESYNC(22, 2, "LTE모뎀 Meter Timesync")
		, METER_SCAN(23, 2, "LTE모뎀 Meter Scan")
		, INVERTER_INFO(24, -1, "Inverter Information")     // -1 : N
		, INVERTER_SETUP(25, 4, "Inverter Setup")
		, ON_DEMAND_INVERTER_LOG(26, 3, "Inverter 로그 Selective LP")
		, UNKNOWN(9999, 9999, "Unknown");

		private int value;		
		private int length;
		private String desc;

		private ActionCommandType(int value, int length, String desc) {
			this.value = value;
			this.length = length;
			this.desc = desc;
		}

		public int getValue() {
			return value;
		}
		
		public int getLengh() {
			return length;
		}

		public String getDesc() {
			return desc;
		}

		public static ActionCommandType getItem(int value) {
			for (ActionCommandType fc : ActionCommandType.values()) {
				if (fc.value == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}
	
	/**
	 * R/W/S Type
	 */
	public enum RWSType {
		R(0, "Read Command"), W(1, "Write Command"), S(2, "System Command"), UNKNOWN(9999, "Unknown");

		private int value;
		private String desc;

		private RWSType(int value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		public int getValue() {
			return value;
		}

		public String getDesc() {
			return desc;
		}

		public static RWSType getItem(int value) {
			for (RWSType fc : RWSType.values()) {
				if (fc.value == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}
	
	/**
	 * Error bit Type
	 */
	public enum ErrorBitType {
		  SUCCESS(0, "Success"), NOT_SUPPORTED_COMMAND(1, "Not supported command")
		, NOT_ACCESS_USER(2, "Not Access user"), FAIL(3, "Fail")
		, UNKNOWN(9999, "Unknown");

		private int value;
		private String desc;

		private ErrorBitType(int value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		public int getValue() {
			return value;
		}

		public String getDesc() {
			return desc;
		}

		public static ErrorBitType getItem(int value) {
			for (ErrorBitType fc : ErrorBitType.values()) {
				if (fc.value == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}
	
	public enum EventLogCode {
		  RSRQ_NOMAL((byte)0x10, "RSRQ 수신레벨 정상")
		, RSRQ_ABNOMAL((byte)0x20, "RSRQ 수신레벨 비정상")
		, MODEM_RESET((byte)0x30, "모뎀 리셋")
		, REMOTE_CONNECT((byte)0x40, "원격제어 접속")
		, LP_SEND_SUCCESS((byte)0x50, "검침 데이터 전송 성공")
		, UNKNOWN((byte)0x60, "Unknown");

		private byte value;
		private String desc;

		private EventLogCode(byte value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		public byte getValue() {
			return value;
		}

		public String getDesc() {
			return desc;
		}

		public static EventLogCode getItem(byte value) {
			for (EventLogCode fc : EventLogCode.values()) {
				if (fc.value == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}
	
	
	public abstract void execute(EMnVGeneralFramePayLoad gFramePayload, IoSession session, EMnVGeneralDataFrame generalDataFrame) throws Exception;
}
