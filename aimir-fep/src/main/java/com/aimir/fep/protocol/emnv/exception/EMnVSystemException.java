/**
 * (@)# EMnVSystemException.java
 *
 * 2015. 4. 23.
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
package com.aimir.fep.protocol.emnv.exception;

/**
 * @author simhanger
 *
 */
public class EMnVSystemException extends Exception {
	private static final long serialVersionUID = 1L;
	private final EMnVExceptionReason reason;

	public static enum EMnVExceptionReason {
		UNKNOWN_ADDR("1000", "Unknown Address"), 
		UNKNOWN_PAYLOAD_FRAME("2000", "Unknown payload frame"),
		UNREGISTERD_VENDOR("3000", "Unregistered Vendor"),
		UNREGISTERD_DEVICE_MODEL("4000", "Unregistered Device Model"),
		INVALID_DLMS_PROTOCOL("5000", "Invalid DLMS protocol"),
		INVALID_AUTH_PROTOCOL("6000", "Invalid Auth protocol"),
		INVALID_SECURITY_MODE("7000", "Invalid Security Mode"),
		METERING_DATA_STATUS_FAIL("8000", "Metering Data Status Fail"),
		INIT_ERROR("9000", "Initialize Fail"),
		UNREGISTERD_INVERTER_MODEL("3000", "Unregistered Inverter Model"),
		UNKNOWN("9999", "Unknown");

		private String code;
		private String desc;

		private EMnVExceptionReason(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public static EMnVExceptionReason getReason(String value) {
			for (EMnVExceptionReason reason : EMnVExceptionReason.values()) {
				if (reason.code.equals(value)) {
					return reason;
				}
			}
			return UNKNOWN;
		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}

	public EMnVSystemException(EMnVExceptionReason reason) {
		super(reason.toString());
		this.reason = reason;
	}

	public EMnVSystemException(EMnVExceptionReason reason, String message) {
		super(reason.toString() + "-" + message);
		this.reason = reason;
	}

	public EMnVSystemException(EMnVExceptionReason reason, String message, Throwable t) {
		super(reason.toString() + "-" + message, t);
		this.reason = reason;
	}

	public EMnVSystemException(EMnVExceptionReason reason, Throwable t) {
		super(reason.toString() + "-" + t.getMessage(), t);
		this.reason = reason;
	}

	public EMnVExceptionReason getReason() {
		return reason;
	}
}
