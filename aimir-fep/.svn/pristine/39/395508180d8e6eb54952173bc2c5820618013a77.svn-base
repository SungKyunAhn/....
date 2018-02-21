/**
 * (@)# Constant_GD.java
 *
 * 2016. 4. 6.
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
package com.aimir.fep.bypass.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author simhanger
 *
 */
public class Constant_GD {
	private static Log log = LogFactory.getLog(Constant_GD.class);

	/**
	 * BypassCommand
	 */
	public enum BypassCommand {
		SNRM(0), AARQ(1), AARE(2), ACTION_REQUEST(3), ACTION_RESPONSE(4), UNKNOWN(9999);

		private int type;

		private BypassCommand(int type) {
			this.type = type;
		}

		public int getValue() {
			return type;
		}

		public static BypassCommand getItem(int value) {
			for (BypassCommand fc : BypassCommand.values()) {
				if (fc.type == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	public enum ActionResultStatus {
		SUCCESS(0), HARDWARE_FALT(1), TEMPORARY_FAILURE(2), READ_WRITE_DENIED(3), OBJECT_UNDEFINED(4), OBJECT_CLASS_INCONSISTENT(9), OBJECT_UNAVAILABLE(11), TYPE_UNMATCHED(12), SCOPE_OF_ACCESS_VIOLATED(13), DATA_BLOCK_UNAVAILABLE(14), LONG_GET_ABORTED(15), NO_LONG_GET_IN_PROGRESS(16), LONG_SET_ABORTED(17), NO_LONG_SET_IN_PROGRESS(18), DATA_BLOCK_NUMBER_INVALID(19), OTHER_REASON(250), UNKNOWN(9999);

		private int code;

		ActionResultStatus(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public static ActionResultStatus getItem(int value) {
			for (ActionResultStatus fc : ActionResultStatus.values()) {
				if (fc.code == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	public enum MIUEventCode {
		BOOT_UP(0x1001, "Boot up"), MODEM_TIMESYNC(0x1002, "Modem Timesync"), MODEM_FACTORY_SETTING(0x1003, "Modem Factory Setting"), MODEM_POWER_OUTAGE(0x1004, "Modem Power Outage"), MODEM_POWER_RECOVERY(0x1005, "Modem Power Recovery"), MODEM_CASE_OPEN(0x1006, "Modem Case Open"), MODEM_CASE_CLOSE(0x1007, "Modem Case Close"), MODEM_RESET(0x1008, "Modem Reset"), VOLTAGE_LOW(0x1009, "Voltage Low"), CLI_STATUS(
				0x100A, "CLI Status"), OTA_STATUS(0x100B, "OTA Status"), NETWORK_SCAN_FAIL(0x2001, "Network Scan Fail") // NETWORK_SCAN_FAIL : Value값은 Modem측에 문의 할것.
		, NETWORK_JOIN_FAIL(0x2002, "Network Join Fail") // NETWORK_JOIN_FAIL : Value값은 Modem측에 문의 할것.
		, NETWORK_TRANSMIT_DATA_FAIL(0x2003, "Network Transmit Data Fail") // NETWORK_TRANSMIT_DATA_FAIL : Value값은 Modem측에 문의 할것.
		, NETWORK_STATUS(0x2004, "Network Status") // NETWORK_STATUS : Value값은 Modem측에 문의 할것.
		, PPP_STATUS(0x2005, "PPP Status"), METER_READ_FAIL(0x3001, "Meter Read Fail"), UPLOAD_METERING_DATA_FAIL(0x3002, "Upload Metering Data Fail"), MODULE_INIT_FAIL(0x4001, "Module Init Fail"), NETWORK_CONNECT_FAIL(0x4002, "Network Connect Fail"), TRANSMIT_DATA_FAIL(0x4003, "Transmit Data Fail"), AT_COMMAND_FAIL(0x4004, "AT Command Fail"), SMS_RECEIVE(0x4005, "SMS Receive") // SMS_RECEIVE :  : Value값은 Modem측에 문의 할것. (Obis 코드 3Byte 저장)
		, UNKNOWN(0xffff, "Unknown Code");

		private int code;
		private String desc;

		MIUEventCode(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}

		public static MIUEventCode getItem(byte[] value) {
			for (MIUEventCode fc : MIUEventCode.values()) {
				if (fc.code == ((value[0] & 0xff) << 8) + (value[1] & 0xff)) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	public enum MIUEventCode_BOOT_UP {
		POWER_ON_RESET(0x0000, "Power On Reset"), BROWN_OUT_DETECTOR_UNREGULATED_DOMAIN_RESET(0x0001, "Brown Out Detector Unregulated Domain Reset"), BROWN_OUT_DETECTOR_REGULATED_DOMAIN_RESET(0x0002, "Brown Out Detector Regulated Domain Reset"), EXTERNAL_PIN_RESET(0x0003, "External Pin Reset"), WATCHDOG_RESET(0x0004, "Watchdog Reset"), LOCKUP_RESET(0x0005, "LOCKUP Reset"), SYSTEM_REQUEST_RESET(
				0x0006, "System Request Reset"), EM4_RESET(0x0007, "EM4 Reset"), EM4_WAKE_UP_RESET(0x0008, "EM4 Wake-up Reset"), AVDD0_BOD_RESET(0x0009, "AVDD0 Bod Reset"), AVDD1_BOD_RESET(0x000A, "AVDD1 Bod Reset"), BACKUP_BROWN_OUT_DETECTOR_VDD_DREG(0x000B, "Backup Brown Out Detector, VDD_DREG"), BACKUP_BROWN_OUT_DETECTOR_BU_VIN(0x000C, "Backup Brown Out Detector, BU_VIN"), BACKUP_BROWN_OUT_DETECTOR_UNREGULATED_DOMAIN(
				0x000D, "Backup Brown Out Detector Unregulated Domain"), BACKUP_BROWN_OUT_DETECTOR_REGULATED_DOMAIN(0x000E, "Backup Brown Out Detector Regulated Domain"), BACKUP_MODE_RESET(0x000F, "Backup mode reset"), UNKNOWN(0xffff, "UnKnown");

		private int code;
		private String desc;

		MIUEventCode_BOOT_UP(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}

		public static MIUEventCode_BOOT_UP getItem(byte[] value) {
			for (MIUEventCode_BOOT_UP fc : MIUEventCode_BOOT_UP.values()) {
				if (fc.code == ((value[0] & 0xff) << 8) + (value[1] & 0xff)) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	public enum MIUEventCode_MODEM_RESET {
		BY_CLI(1, "By CLI"), BY_SCHEDULE(2, "By Schedule"), BY_HARDFAULT(3, "By Hardfault"), BY_ONDEMAND(4, "By Ondemand"), BY_MAC_COMMAND(5, "By Mac Command"), BY_BLACKLIST_ADD(6, "By Blacklist Add"), BY_NETWORK_DOWN(7, "By Network Down"), BY_NETWORK_SCAN_FAIL(8, "By Network Scan Fail(Max count)"), BY_FACTORY_SETTING(9, "By Factory Setting"), BY_NETWORK_TRANSMIT_DATA_FAIL(10,
				"By Network Transmit Data Fail(Max count)"), BY_MODEM_NO_NETWORK_ALIVE(11, "By Modem No Network Alive"), UNKNOWN(99, "UnKnown");

		private int code;
		private String desc;

		MIUEventCode_MODEM_RESET(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}

		public static MIUEventCode_MODEM_RESET getItem(int value) {
			for (MIUEventCode_MODEM_RESET fc : MIUEventCode_MODEM_RESET.values()) {
				if (fc.code == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	public enum MIUEventCode_CLI_STATUS {
		CLI_LOGIN(1, "CLI Login"), CLI_LOGOUT(2, "CLI Logout"), UNKNOWN(99, "UnKnown");

		private int code;
		private String desc;

		MIUEventCode_CLI_STATUS(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}

		public static MIUEventCode_CLI_STATUS getItem(int value) {
			for (MIUEventCode_CLI_STATUS fc : MIUEventCode_CLI_STATUS.values()) {
				if (fc.code == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	public enum MIUEventCode_OTA_STATUS {
		OTA_CRC_FAIL(1, "OTA CRC Fail"), OTA_TIMEOUT(2, "OTA Timeout"), UNKNOWN(99, "UnKnown");

		private int code;
		private String desc;

		MIUEventCode_OTA_STATUS(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}

		public static MIUEventCode_OTA_STATUS getItem(int value) {
			for (MIUEventCode_OTA_STATUS fc : MIUEventCode_OTA_STATUS.values()) {
				if (fc.code == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	public enum MIUEventCode_PPP_STATUS {
		UP(1, "Up"), DOWN(2, "Down"), UNKNOWN(99, "UnKnown");

		private int code;
		private String desc;

		MIUEventCode_PPP_STATUS(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}

		public static MIUEventCode_PPP_STATUS getItem(int value) {
			for (MIUEventCode_PPP_STATUS fc : MIUEventCode_PPP_STATUS.values()) {
				if (fc.code == value) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}
}
