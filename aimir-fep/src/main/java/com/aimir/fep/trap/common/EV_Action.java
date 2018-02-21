package com.aimir.fep.trap.common;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event Process Action Interface
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public interface EV_Action {

	/**
	 * OTA Upgrade type
	 */
	public enum OTA_UPGRADE_TYPE {
		  MODEM("1", TargetClass.Modem)
		, METER("2", TargetClass.EnergyMeter)
		, DCU_FW("3", TargetClass.DCU)
		, DCU_KERNEL("4", TargetClass.DCU)
		, DCU_COORDINATE("5", TargetClass.DCU)
		, UNKNOWN("", TargetClass.Unknown);

		private String code;
		private TargetClass targetClass;

		OTA_UPGRADE_TYPE(String code, TargetClass targetClass) {
			this.code = code;
			this.targetClass = targetClass;
		}

		public TargetClass getTargetClass() {
			return this.targetClass;
		}

		public static OTA_UPGRADE_TYPE getItem(String code) {
			for (OTA_UPGRADE_TYPE fc : OTA_UPGRADE_TYPE.values()) {
				if (fc.code.equals(code)) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	/**
	 * OTA Upgrade Code
	 */
	public enum OTA_UPGRADE_RESULT_CODE {
		/* DCU Event */
		  OTAERR_NOERROR(0, "Success")
		, OTAERR_BUSY(1, "Busy")
		, OTAERR_CONNECT_FAIL(2, "Network layer fail")
		, OTAERR_INVALID_MODEM(3, "Invalid mode")
		, OTAERR_METERING_FAIL(4, "Metering fail")
		, OTAERR_LINK_FAIL(5, "Link layer fail")
		, OTAERR_NEGO_FAIL(6, "Negotiation fail")
		, OTAERR_SECURITY_FAIL(7, "Security fail")
		, OTAERR_TIMEOUT(8, "Timeout")
		, OTAERR_INVALID_PARAM(9, "Invalid parameter")
		, OTAERR_UNKNOWN_PARSER(10, "Unknown parser")
		, OTAERR_NO_NETWORK(11, "No network")
		, OTAERR_NOT_SUPPORT(12, "Not support")
		, OTAERR_VERIFY_FAIL(13, "Verification fail")
		, OTAERR_INSTALL_FAIL(14, "Install fail")
		, OTAERR_WRITE_FAIL(15, "Write fail")
		, OTAERR_POWER_FAIL(16, "Power fail")
		, OTAERR_INVALID_METER(17, "Invalid meter")
		, OTAERR_INVALID_TYPE(18, "Invalid type")
		, OTAERR_INVALID_FILE(19, "Invalid file")
		, OTAERR_INITIATE_FAIL(20, "Initialize fail")
		, OTAERR_ONGOING(21, "Ongoing")
		, OTAERR_READY(22, "Ready")
		
		, OTAERR_REQ_UPGRADE(50, "Node Upgrade request fail.")
		
		/* HES Event */
		, OTAERR_BYPASS_TRN_FAIL(100, "Bypass Transaction fail")
		, OTAERR_TRN_FAIL(101, "ImageBlock Transfer fail")
		, OTAERR_NI_TRN_FAIL(101, "ImageBlock Transfer fail")
		, OTAERR_BYPASS_EXCEPTION_FAILE(102, "Bypass Exception fail")
		, OTAERR_BYPASS_EXCUTE_FAIL(102, "Bypass excute fail")
		
		/* Common Event */
		, Unknown(-1, "Unknown Error");

		private int code;
		private String desc;

		OTA_UPGRADE_RESULT_CODE(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return this.code;
		}
		
		public String getDesc(){
			return this.desc;
		}

		public static OTA_UPGRADE_RESULT_CODE getItem(int code) {
			for (OTA_UPGRADE_RESULT_CODE fc : OTA_UPGRADE_RESULT_CODE.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return Unknown;
		}
	}

	/**
	 * execute event action
	 *
	 * @param trap
	 *            - FMP Trap(MCU Event)
	 * @param event
	 *            - Event Alert Log Data
	 */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception;
}
