/**
 * (@)# DlmsConstantsForMOE.java
 *
 * 2016. 4. 18.
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
package com.aimir.fep.bypass.decofactory.consts;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class DlmsConstantsForMOE {
	private static Logger logger = LoggerFactory.getLogger(DlmsConstantsForMOE.class);

	public enum DlmsPiece {
		CLIENT_SYSTEM_TITLE("4C534B0000000001") // Client System Title : LSK00001
		, C_TO_S(Hex.decode(getSecureRandomByte(8))); // CtoS(challenge) : Random 8byte

		private String value;

		private DlmsPiece(String value) {
			this.value = value;
		}

		public byte[] getBytes() {
			return DataUtil.readByteString(this.value);
		}
	}

	/*
	 * AARQ-apdu ::= [APPLICATION 0] IMPLICIT SEQUENCE
	   {
		-- [APPLICATION 0] == [ 60H ] = [ 96 ]
		protocol-version [0] IMPLICIT BIT STRING {version1 (0)} DEFAULT {version1},
		application-context-name [1] Application-context-name,
		called-AP-title [2] AP-title OPTIONAL,
		called-AE-qualifier [3] AE-qualifier OPTIONAL,
		called-AP-invocation-id [4] AP-invocation-identifier OPTIONAL,
		called-AE-invocation-id [5] AE-invocation-identifier OPTIONAL,
		calling-AP-title [6] AP-title OPTIONAL,
		calling-AE-qualifier [7] AE-qualifier OPTIONAL,
		calling-AP-invocation-id [8] AP-invocation-identifier OPTIONAL,
		calling-AE-invocation-id [9] AE-invocation-identifier OPTIONAL,
		-- The following field shall not be present if only the kernel is used.
		sender-acse-requirements [10] IMPLICIT ACSE-requirements OPTIONAL,
		-- The following field shall only be present if the authentication functional unit is selected.
		mechanism-name [11] IMPLICIT Mechanism-name OPTIONAL,		
		-- The following field shall only be present if the authentication functional unit is selected.
		calling-authentication-value [12] EXPLICIT Authentication-value OPTIONAL,
		implementation-information [29] IMPLICIT Implementation-data OPTIONAL,
		user-information [30] EXPLICIT Association-information OPTIONAL
		}
		-- The user-information field shall carry an InitiateRequest APDU encoded in A-XDR, and then
		-- encoding the resulting OCTET STRING in BER.
	 */
	public enum AARQ {
		PROTOCOL_VERSION(0, ""), APPLICATION_CONTEXT_NAME(1, "A109060760857405080101"), CALLED_AP_TITLE(2, ""), CALLED_AE_QUALIFIER(3, ""), CALLED_AP_INVOCATION_ID(4, ""), CALLED_AE_INVOCATION_ID(5, ""), CALLING_AP_TITLE(6, "A60A0408" + DlmsPiece.CLIENT_SYSTEM_TITLE.value) //  -- Client System Title  : LSK00001
		, CALLING_AE_QUALIFIER(7, ""), CALLING_AP_INVOCATION_ID(8, ""), CALLING_AE_INVOCATION_ID(9, ""), SENDER_ACSE_REQUIREMENTS(10, "8A020780"), MECHANISM_NAME(11, "8B0760857405080205"), CALLING_AUTHENTICATION_VALUE(12, "AC0A8008" + DlmsPiece.C_TO_S.value) //   -- CtoS(challenge)
		, IMPLEMENTATION_INFORMATION(29, ""), USER_INFORMATION(30, "BE10040E01000000065F1F0400007E1F0000"), APPLICATION(97, "60"), AARQ_LLC(98, "E6E600");

		private int code;
		private String value;

		private AARQ(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public byte[] getValue() {
			return Hex.encode(value);
		}

		public static AARQ getItem(int code) {
			for (AARQ fc : AARQ.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return null;
		}
	}

	/*
	 * 
		AARE-apdu ::= [APPLICATION 1] IMPLICIT SEQUENCE
		{
		-- [APPLICATION 1] == [ 61H ] = [ 97 ]
		protocol-version [0] IMPLICIT BIT STRING {version1 (0)} DEFAULT {version1},
		application-context-name [1] Application-context-name,
		result [2] Association-result,
		result-source-diagnostic [3] Associate-source-diagnostic,
		responding-AP-title [4] AP-title OPTIONAL,
		responding-AE-qualifier [5] AE-qualifier OPTIONAL,
		responding-AP-invocation-id [6] AP-invocation-identifier OPTIONAL,
		responding-AE-invocation-id [7] AE-invocation-identifier OPTIONAL,
		-- The following field shall not be present if only the kernel is used.
		responder-acse-requirements [8] IMPLICIT ACSE-requirements OPTIONAL,
		-- The following field shall only be present if the authentication functional unit is selected.
		mechanism-name [9] IMPLICIT Mechanism-name OPTIONAL,
		-- The following field shall only be present if the authentication functional unit is selected.
		responding-authentication-value [10] EXPLICIT Authentication-value OPTIONAL,
		implementation-information [29] IMPLICIT Implementation-data OPTIONAL,
		user-information [30] EXPLICIT Association-information OPTIONAL
		}
		-- The user-information field shall carry either an InitiateResponse (or, when the proposed -- xDLMS context is not accepted by the server, a confirmedServiceError APDU encoded in -- A-XDR, and then encoding the resulting OCTET STRING in BER.	 
	 */
	public enum AARE {
		PROTOCOL_VERSION(0x00), APPLICATION_CONTEXT_NAME(0xA1), RESULT(0xA2), RESULT_SOURCE_DIAGNOSTIC(0xA3), RESPONDING_AP_TITLE(0xA4), RESPONDING_AE_QUALIFIER(0x05), RESPONDING_AP_INVOCATION_ID(0x06), RESPONDING_AE_INVOCATION_ID(0x07), RESPONDER_ACSE_REQUIREMENTS(0x88), MECHANISM_NAME(0x89), RESPONDING_AUTHENTICATION_VALUE(0xAA), IMPLEMENTATION_INFORMATION(0x1D), USER_INFORMATION(0xBE);

		private byte code;

		private AARE(int code) {
			this.code = (byte) code;
		}

		public byte getValue() {
			return code;
		}

		public static AARE getItem(byte code) {
			for (AARE fc : AARE.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return null;
		}

	}

	public enum ASSOCIATION_LN {
		CLASS_ASSOCIATION_LN("000F") // class_id = 15
		, CURRENT_ASSOCIATION_LN("0000280000FF") // OBIS_CODE 
		, REPLY_TO_HLS_AUTHENTICATION("01"); // METHODS 

		private String code;

		private ASSOCIATION_LN(String code) {
			this.code = code;
		}

		public byte[] getByteValue() {
			return Hex.encode(code);
		}

		public static ASSOCIATION_LN getItem(String code) {
			for (ASSOCIATION_LN fc : ASSOCIATION_LN.values()) {
				if (fc.code.equals(code)) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum ActionRequest {
		ACTION_REQUEST("C3"), ACTION_REQUEST_LLC("E6E600"), ACTION_REQUEST_INVOKE_ID_AND_PRIORITY("40"), NORMAL("01"), NEXT_PBLOCK("02"), WITH_LIST("03"), WITH_FIRST_PBLOCK("04"), WITH_LIST_AND_FIRST_PBLOCK("05"), WITH_PBLOCK("06");

		private String code;

		private ActionRequest(String code) {
			this.code = code;
		}

		public byte[] getByteValue() {
			return Hex.encode(code);
		}

		public static ActionRequest getItem(String code) {
			for (ActionRequest fc : ActionRequest.values()) {
				if (fc.code.equals(code)) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum ActionResponse {
		NORMAL(0x01), WITH_PBLOCK(0x02), WITH_LIST(0x03), NEXT_PBLOCK(0x04);

		private byte code;

		private ActionResponse(int code) {
			this.code = (byte) code;
		}

		public byte getValue() {
			return code;
		}

		public static ActionResponse getItem(byte code) {
			for (ActionResponse fc : ActionResponse.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum ActionResult {
		SUCCESS(0x00), HARDWARE_FALT(0x01), TEMPORARY_FAIL(0x02), READ_WRITE_DENIED(0x03), OBJECT_UNDEFINED(0x04), OBJECT_CLASS_INCONSISTENT(0x09), OBJECT_UNAVAILABLE(0x0B), TYPE_UNMATCHED(0x0C), SCOPE_OF_ACCESS_VIOLATED(0x0D), DATA_BLOCK_UNAVAILABLE(0x0E), LONG_ACTION_ABORTED(0x0F), NO_LONG_ACTION_IN_PROGRESS(0x10), OTHER_REASON(0xFA);

		private byte code;

		private ActionResult(int code) {
			this.code = (byte) code;
		}

		public byte getValue() {
			return code;
		}

		public static ActionResult getItem(byte code) {
			for (ActionResult fc : ActionResult.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum GetRequest {
		GET_REQUEST("C0"), GET_REQUEST_LLC("E6E600"), GET_REQUEST_INVOKE_ID_AND_PRIORITY("40"), NORMAL("01"), NEXT("02"), WITH_LIST("03");

		private String code;

		private GetRequest(String code) {
			this.code = code;
		}

		public byte[] getByteValue() {
			return Hex.encode(code);
		}

		public static GetRequest getItem(String code) {
			for (GetRequest fc : GetRequest.values()) {
				if (fc.code.equals(code)) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum GetResponse {
		NORMAL(0x01), WITH_DATABLOCK(0x02), WITH_LIST(0x03);

		private byte code;

		private GetResponse(int code) {
			this.code = (byte) code;
		}

		public byte getValue() {
			return code;
		}

		public static GetResponse getItem(byte code) {
			for (GetResponse fc : GetResponse.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum GetDataResult {
		DATA(0x00), DATA_ACCESS_RESULT(0x01);

		private byte code;

		private GetDataResult(int code) {
			this.code = (byte) code;
		}

		public byte getValue() {
			return code;
		}

		public static GetDataResult getItem(byte code) {
			for (GetDataResult fc : GetDataResult.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum DataAccessResult {
		SUCCESS(0x00), HARDWARE_FALT(0x01), TEMPORARY_FAIL(0x02), READ_WRITE_DENIED(0x03), OBJECT_UNDEFINED(0x04), OBJECT_CLASS_INCONSISTENT(0x09), OBJECT_UNAVAILABLE(0x0B), TYPE_UNMATCHED(0x0C), SCOPE_OF_ACCESS_VIOLATED(0x0D), DATA_BLOCK_UNAVAILABLE(0x0E), LONG_GET_ABORTED(0x0F), NO_LONG_GET_IN_PROGRESS(0x10), LONG_SET_ABORTED(0x11), NO_LONG_SET_IN_PROGRESS(0x12), DATA_BLOCK_NUMBER_INVALID(
				0x13), OTHER_REASON(0xFA);

		private byte code;

		private DataAccessResult(int code) {
			this.code = (byte) code;
		}

		public byte getValue() {
			return code;
		}

		public static DataAccessResult getItem(byte code) {
			for (DataAccessResult fc : DataAccessResult.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum SetRequest {
		SET_REQUEST("C1"), SET_REQUEST_LLC("E6E600"), SET_REQUEST_INVOKE_ID_AND_PRIORITY("40"), NORMAL("01"), WITH_FIRST_DATABLOCK("02"), WITH_DATABLOCK("03"), WITH_LIST("04"), WITH_LIST_AND_FIRST_DATABLOCK("05");

		private String code;

		private SetRequest(String code) {
			this.code = code;
		}

		public byte[] getByteValue() {
			return Hex.encode(code);
		}

		public static SetRequest getItem(String code) {
			for (SetRequest fc : SetRequest.values()) {
				if (fc.code.equals(code)) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum SetResponse {
		NORMAL(0x01), DATABLOCK(0x02), LAST_DATABLOCK(0x03), LAST_DATABLOCK_WITH_LIST(0x04), WITH_LIST(0x05);

		private byte code;

		private SetResponse(int code) {
			this.code = (byte) code;
		}

		public byte getValue() {
			return code;
		}

		public static SetResponse getItem(byte code) {
			for (SetResponse fc : SetResponse.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return null;
		}
	}

	/* Image transfer
	   - class_id = 18 (0012)
	   - OBIS CODE = 0.0.44.0.0.255 (00002C0000FF)
	 */
	public enum ImageTransfer {
		CLASS_ID("0012"), OBIS_CODE("00002C0000FF"), OPTION_NOT_USE("00") // 00은 Option 사용 하지 않겠다는 뜻.
		, OPTION_USE("01"); //  01 Option사용하겠다는 뜻.

		private String code;

		private ImageTransfer(String code) {
			this.code = code;
		}

		public byte[] getByteValue() {
			return Hex.encode(code);
		}

		public static ImageTransfer getItem(String code) {
			for (ImageTransfer fc : ImageTransfer.values()) {
				if (fc.code.equals(code)) {
					return fc;
				}
			}
			return null;
		}
	}

	/*
	  Image transfer Attributes
	 */
	public enum ImageTransferAttributes {
		IMAGE_BLOCK_SIZE(0x02), IMAGE_TRANSFERRED_BLOCKS_STATUS(0x03), IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER(0x04), IMAGE_TRANSFER_ENABLED(0x05), IMAGE_TRANSFER_STATUS(0x06), IMAGE_TO_ACTIVATE_INFO(0x07);

		private byte code;

		private ImageTransferAttributes(int code) {
			this.code = (byte) code;
		}

		public byte[] getByteValue() {
			return new byte[] { code };
		}

		public static ImageTransferAttributes getItem(byte code) {
			for (ImageTransferAttributes fc : ImageTransferAttributes.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return null;
		}
	}

	/*
	 * Image transfer Methods
	 */
	public enum ImageTransferMethods {
		IMAGE_TRANSFER_INITIATE(0x01), IMAGE_BLOCK_TRANSFER(0x02), IMAGE_VERIFY(0x03), IMAGE_ACTIVATE(0x04);

		private byte code;

		private ImageTransferMethods(int code) {
			this.code = (byte) code;
		}

		public byte[] getByteValue() {
			return new byte[] { code };
		}

		public static ImageTransferMethods getItem(byte code) {
			for (ImageTransferMethods fc : ImageTransferMethods.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum ImageTransferStatus {
		IMAGE_TRANSFER_NOT_INITIATED(0), IMAGE_TRANSFER_INITIATED(1), IMAGE_VERIFICATION_INITIATED(2), IMAGE_VERIFICATION_SUCCESSFUL(3), IMAGE_VERIFICATION_FAILED(4), IMAGE_ACTIVATION_INITIATED(5), IMAGE_ACTIVATION_SUCCESSFUL(6), IMAGE_ACTIVATION_FAILED(7);

		private int code;

		private ImageTransferStatus(int code) {
			this.code = code;
		}

		public static ImageTransferStatus getItem(int code) {
			for (ImageTransferStatus fc : ImageTransferStatus.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return null;
		}
	}

	/*
	 * Meter F/W Attributes 
	 * - class_id = 1 (0001) 
	 * - OBIS CODE = 1.1.0.2.0.255(0101000200FF02)
	 * 
	 * @author simhanger
	 *
	 */
	public enum MeterFWInfoAttributes {
		CLASS_ID("0001"), OBIS_CODE("0101000200FF"), FW_VERSION("02"), OPTION_NOT_USE("00"); // 00은 Option 사용 하지 않겠다는 뜻.

		private String code;

		private MeterFWInfoAttributes(String code) {
			this.code = code;
		}

		public byte[] getByteValue() {
			return Hex.encode(code);
		}

		public static MeterFWInfoAttributes getItem(String code) {
			for (MeterFWInfoAttributes fc : MeterFWInfoAttributes.values()) {
				if (fc.code.equals(code)) {
					return fc;
				}
			}
			return null;
		}
	}

	/*
	 * Meter ParamSet Methods
	 * - class_id = 9 (0009) 
	 * - OBIS CODE = 0.0.10.0.0.255 (00000A0000FF)
	 * 
	 * @author simhanger
	 *
	 */
	public enum MeterParamSetMethods {
		CLASS_ID("0009"), OBIS_CODE("00000A0000FF"), METER_ALARM_RESET("01"), OPTION_USE("01") // 01은 Option 사용 하겠다는 뜻.
		, RESET_ALARM("0006");

		private String code;

		private MeterParamSetMethods(String code) {
			this.code = code;
		}

		public byte[] getByteValue() {
			return Hex.encode(code);
		}

		public static MeterParamSetMethods getItem(String code) {
			for (MeterParamSetMethods fc : MeterParamSetMethods.values()) {
				if (fc.code.equals(code)) {
					return fc;
				}
			}
			return null;
		}
	}

	/*
	 * Billing Cycle Attributes
	 * - class_id = 22 (0016) 
	 * - OBIS CODE = 0.0.15.0.0.255 (00000F0000FF)
	 * 
	 * @author simhanger
	 *
	 */
	public enum MeterBillingCycleAttributes {
		CLASS_ID("0016"), OBIS_CODE("00000F0000FF"), BILLING_CYCLE_EXECUTION_TIME("04"), OPTION_NOT_USE("00"); // 00은 Option 사용 하지 않겠다는 뜻.

		private String code;

		private MeterBillingCycleAttributes(String code) {
			this.code = code;
		}

		public byte[] getByteValue() {
			return Hex.encode(code);
		}

		public static MeterBillingCycleAttributes getItem(String code) {
			for (MeterBillingCycleAttributes fc : MeterBillingCycleAttributes.values()) {
				if (fc.code.equals(code)) {
					return fc;
				}
			}
			return null;
		}
	}

	/*
	 * Demand Period
	 * - class_id = 22 (0016) 
	 * - OBIS CODE = 0.0.15.0.0.255 (00000F0000FF)
	 * 
	 * @author simhanger
	 *
	 */
	public enum DemandPeriodAttributes {
		CLASS_ID("0005"), OBIS_DEMAND_PLUS_A_PERIOD("0100010400FF"), OBIS_DEMAND_PLUS_A_NUMBER("0100010400FF"), OBIS_DEMAND_MINUS_A_PERIOD("0100020400FF"), OBIS_DEMAND_MINUS_A_NUMBER("0100020400FF"), OBIS_DEMAND_PLUS_R_PERIOD("0100030400FF"), OBIS_DEMAND_PLUS_R_NUMBER("0100030400FF"), OBIS_DEMAND_MINUS_R_PERIOD("0100040400FF"), OBIS_DEMAND_MINUS_R_NUMBER("0100040400FF"), OBIS_DEMAND_R_QI_PERIOD(
				"0100050400FF"), OBIS_DEMAND_R_QI_NUMBER("0100050400FF"), OBIS_DEMAND_R_QIV_PERIOD("0100080400FF")
		, OBIS_DEMAND_R_QIV_NUMBER("0100080400FF"), OBIS_DEMAND_PLUS_PERIOD("0100090400FF"), OBIS_DEMAND_PLUS_NUMBER("0100090400FF"), OBIS_DEMAND_MINUS_PERIOD("01000A0400FF"), OBIS_DEMAND_MINUS_NUMBER("01000A0400FF"), PEROID("08"), NUMBER_OF_PERIODS("09"), OPTION_NOT_USE("00"); // 00은 Option 사용 하지 않겠다는 뜻.

		private String code;

		private DemandPeriodAttributes(String code) {
			this.code = code;
		}

		public byte[] getByteValue() {
			return Hex.encode(code);
		}

		public static DemandPeriodAttributes getItem(String code) {
			for (DemandPeriodAttributes fc : DemandPeriodAttributes.values()) {
				if (fc.code.equals(code)) {
					return fc;
				}
			}
			return null;
		}
	}

	/*
	 * TOU Set
	 * - class_id = 20 (0014) 
	 * - OBIS CODE = 0.0.13.0.0.255 (00000D0000FF)
	 * 
	 * @author simhanger
	 *
	 */
	public enum TOUAttributes {
		CLASS_ID("0014"), ACTIVITY_CALENDAR("00000D0000FF"), CALENDAR_NAME_PASSIVE("06"), SEASON_PROFILE_PASSIVE("07")
		, WEEK_PROFILE_TABLE_PASSIVE("08"), DAY_PROFILE_TABLE_PASSIVE("09"), ACTIVATE_PASSIVE_CALENDAR_TIME("0A") // 12byte 
		/*
		 * ACTIVATE_PASSIVE_CALENDAR_TIME : OCTET STRING (SIZE(12))
		{
			year highbyte,
			year lowbyte,
			month,
			day of month,
			day of week,
			hour,
			minute,
			second,
			hundredths of second,
			deviation highbyte,
			deviation lowbyte,
			clock status
		}
		 */
		, OPTION_NOT_USE("00"); // 00은 Option 사용 하지 않겠다는 뜻.

		private String code;

		private TOUAttributes(String code) {
			this.code = code;
		}

		public byte[] getByteValue() {
			return Hex.encode(code);
		}

		public static TOUAttributes getItem(String code) {
			for (TOUAttributes fc : TOUAttributes.values()) {
				if (fc.code.equals(code)) {
					return fc;
				}
			}
			return null;
		}
	}
	
	/*
	 * Current Load Limit Attributes
	 *  - Current over limit duration judge time : 01000B2C00FF
	 *  - Current over limit threshold : 01000B2300FF
	 */
	public enum CurrentLoadLimitAttributes {
		  CLASS_ID("0003"), DURATION_JUDGE_TIME_OBIS_CODE("01000B2C00FF"), THRESHOLD_OBIS_CODE("01000B2300FF"), VALUE("02"), OPTION_NOT_USE("00"); // 00은 Option 사용 하지 않겠다는 뜻.

		private String code;

		private CurrentLoadLimitAttributes(String code) {
			this.code = code;
		}

		public byte[] getByteValue() {
			return Hex.encode(code);
		}

		public static CurrentLoadLimitAttributes getItem(String code) {
			for (CurrentLoadLimitAttributes fc : CurrentLoadLimitAttributes.values()) {
				if (fc.code.equals(code)) {
					return fc;
				}
			}
			return null;
		}
	}


	/*
	 * Current Load Limit
	 * - class_id = 22 (0016) 
	 * 
	 * @author simhanger
	 *
	 */
	public enum CurrentLoadLimitdAttributes {
		  CLASS_ID("0003"), OBIS_CURRENT_OVER_LIMIT_DURATION_JUDGE_TIME("01000B2C00FF"), OBIS_CURRENT_OVER_LIMIT_THRESHOLD("01000B2300FF")
		, ATTRIBUTE("02"), OPTION_NOT_USE("00"); // 00은 Option 사용 하지 않겠다는 뜻.

		private String code;

		private CurrentLoadLimitdAttributes(String code) {
			this.code = code;
		}

		public byte[] getByteValue() {
			return Hex.encode(code);
		}

		public static CurrentLoadLimitdAttributes getItem(String code) {
			for (CurrentLoadLimitdAttributes fc : CurrentLoadLimitdAttributes.values()) {
				if (fc.code.equals(code)) {
					return fc;
				}
			}
			return null;
		}
	}
	
	public enum TOUInfoBlockType {
		FIRST_BLOCK(0), MIDDLE_BLOCK(1), LAST_BLOCK(2);

		private int code;

		private TOUInfoBlockType(int code) {
			this.code = code;
		}

		public static TOUInfoBlockType getItem(int code) {
			for (TOUInfoBlockType fc : TOUInfoBlockType.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return null;
		}
	}

	public enum DLMSCommonDataType {
		Null(0x00, 1), Array(0x01, 1), Structure(0x02, 1), Boolean(0x03, 1), BitString(0x04, 0), INT32(0x05, 4), // doublle-long
		UINT32(0x06, 4), // doublie-long-unsigned

		OctetString(0x09, 0), VisibleString(0x0A, 0),

		BCD(0x0D, 1),

		INT8(0x0F, 1), // integer
		INT16(0x10, 2), // long
		UINT8(0x11, 1), // unsigned
		UINT16(0x12, 2), // long-unsigned
		CompactArray(0x13, 1), INT64(0x14, 8), // long64          
		UINT64(0x15, 8), // long64-unsigned
		Enum(0x16, 1), FLOAT32(0x17, 4), // OCTET STRING(SIZE(4))
		FLOAT64(0x18, 8), // OCTET STRING(SIZE(8))
		Datetime(0x19, 12), // OCTET STRING(SIZE(12))
		Date(0x1A, 5), // OCTET STRING(SIZE(5))
		Time(0x1B, 4), // OCTET STRING(SIZE(4))

		DontCare(0xFF, 1);

		private byte value;
		private int len;

		DLMSCommonDataType(int value, int len) {
			this.value = (byte) value;
			this.len = len;
		}

		public byte getValue() {
			return this.value;
		}

		public int getLenth() {
			return this.len;
		}

		public static DLMSCommonDataType getItem(byte value) {
			for (DLMSCommonDataType fc : DLMSCommonDataType.values()) {
				if (fc.value == value) {
					return fc;
				}
			}
			return null;
		}
	}

	public static Object getValueByDLMSCommonDataType(DLMSCommonDataType dataType, byte[] data) {
		Object value = null;
		switch (dataType) {
		case Structure:
		case Enum:
		case BitString:
		case OctetString:
		case VisibleString:
		case BCD:
		case INT64:
		case UINT64:
		case Datetime:
		case Date:
		case Time:
		case Null:
			value = new OCTET(data);
			break;
		case FLOAT32:
			try {
				value = new Float(DataUtil.getFloat(data, 0));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case Array:
		case CompactArray:
		case INT8:
		case UINT8:
			value = new Integer(DataUtil.getIntToBytes(data));
			break;
		case INT16:
		case UINT16:
		case INT32:
		case UINT32:
			value = new Long(DataUtil.getLongToBytes(data));
			break;
		case Boolean:
			int val = 0xFF & data[0];
			if (val > 0) {
				value = Boolean.TRUE;
			} else {
				value = Boolean.FALSE;
			}
			break;
		default:
			value = new OCTET(data);
		}

		return value;
	}

	/**
	 * 랜덤한 byte배열 생산
	 * 
	 * @param byteSize
	 * @return
	 */
	public static byte[] getSecureRandomByte(int byteSize) {
		byte[] result = null;

		try {
			//			SecureRandom random = SecureRandom.getInstanceStrong();  
			//			result = random.generateSeed(byteSize);

			result = new byte[byteSize];
			Random random = new Random();
			random.nextBytes(result);

		} catch (Exception e) {
			logger.error("Create Random bytes Error - {}", e);
		}
		return result;
	}
}
