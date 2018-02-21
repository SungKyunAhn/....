/**
 * (@)# NestedDLMSDecoratorForEVN.java
 *
 * 2016. 4. 15.
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
package com.aimir.fep.bypass.decofactory.decorator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.AARE;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.AARQ;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.ASSOCIATION_LN;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.ActionRequest;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.ActionResponse;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.ActionResult;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.CurrentLoadLimitAttributes;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.DLMSCommonDataType;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.DataAccessResult;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.DemandPeriodAttributes;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.DlmsPiece;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.GetDataResult;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.GetRequest;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.GetResponse;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.ImageTransfer;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.ImageTransferAttributes;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.ImageTransferMethods;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.MeterBillingCycleAttributes;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.MeterFWInfoAttributes;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.MeterParamSetMethods;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.SetRequest;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.SetResponse;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.TOUAttributes;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.TOUInfoBlockType;
import com.aimir.fep.bypass.decofactory.consts.HLSAuth;
import com.aimir.fep.bypass.decofactory.consts.HLSAuth.HLSSecurity;
import com.aimir.fep.bypass.decofactory.consts.HdlcConstants.HdlcObjectType;
import com.aimir.fep.bypass.decofactory.decoframe.EVN_DLMSFrame;
import com.aimir.fep.bypass.decofactory.decoframe.INestedFrame;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory.Procedure;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;

/**
 * @author kh.yoon
 *
 */
public class NestedDLMSDecoratorForEVN extends NestFrameDecorator {
	private static Logger logger = LoggerFactory.getLogger(NestedDLMSDecoratorForEVN.class);
	private final String bufferKey = "XDLMS_DATABLOCK_BUFFER";
	private byte[] gdDLMSFrame = null;
	
	private byte[] lpRawData; // Load Profile 정보를 수집하기위한 Procedure에서 사용.
	/**
	 * ActionReq 시 필요한 정보.
	 */
	private byte[] aareAuthenticationValue = null; // AARE로 받은 S to C
	static private byte[] aareRespondingAPtitle = null; // AARE로 받은 Server System Title. Action Response Validation시 필요.
	static private byte[] aareApplicationContextName = null; // AARE로 받은APPLICATION_CONTEXT_NAME. Action Response Validation시 필요.
	
	/**
	 * 호출할때마다 1씩 증가시킨값
	 * 
	 * @return
	 */
	private static int hdlcInvoCounter = 0;

	public static byte[] getInvoCounter() {
		return DataUtil.get4ByteToInt(++hdlcInvoCounter);
	}

	/*
	 * REQUEST_INVOKE_ID_AND_PRIORITY를 호출할때마다 1씩 증가시킨값. 
	 * 동일한 트렌젝션으로 묶일경우는 증가시키지 않는다.
	 */
	private int priorityCounter = 64;

	public byte[] getPriorityByteValue() {
		return getPriorityByteValue(false);
	}

	public byte[] getPriorityByteValue(boolean hasMorTransaction) {
		byte[] result = new byte[1];

		if (hasMorTransaction) {
			result[0] = DataUtil.getByteToInt(priorityCounter);
		} else {
			result[0] = DataUtil.getByteToInt(priorityCounter++);
		}

		return result;
	}

	/**
	 * @param nestedFrame
	 */
	public NestedDLMSDecoratorForEVN(INestedFrame nestedFrame) {
		super(nestedFrame);
	}

	@Override
	public byte[] encode(HdlcObjectType hdlcType, Procedure procedure, HashMap<String, Object> param) {
		logger.debug("## Excute NestedDLMSDecorator Encoding [{}]", hdlcType.name());
		gdDLMSFrame = new byte[] {};

		try {
			switch (hdlcType) {
			/*
			 *   공통 프로시져 
			 */
			case SNRM:
				byte[] snrmFrame = new byte[] { (byte) 0x81, // Format identifier
						(byte) 0x80, // Group identifier
						(byte) 0x14, // Length 20
						(byte) 0x05, // maximun length - transmit
						(byte) 0x02, // Length 2
						(byte) 0x00, (byte) 0x70, // 1024 bytes
						(byte) 0x06, // maximum length - receive
						(byte) 0x02, // Length 2
						(byte) 0x00, (byte) 0x70, // 1024 bytes
						(byte) 0x07, // window size - transmit
						(byte) 0x04, // Length 4
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1
						(byte) 0x08, // window size - receive
						(byte) 0x04, // Length 4
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 // 1
				};

				gdDLMSFrame = DataUtil.append(super.encode(hdlcType, null, null), snrmFrame);

				break;
			case AARQ:
				gdDLMSFrame = DataUtil.append(super.encode(hdlcType, null, null), gdDLMSFrame);

				// AARQ Info
				gdDLMSFrame = DataUtil.append(gdDLMSFrame, AARQ.AARQ_LLC.getValue());
				gdDLMSFrame = DataUtil.append(gdDLMSFrame, AARQ.APPLICATION.getValue());

				byte[] aarqResult = new byte[] {};
				aarqResult = DataUtil.append(aarqResult, new byte[1]); // application & application length 2바이트 제외
				aarqResult = DataUtil.append(aarqResult, AARQ.APPLICATION_CONTEXT_NAME.getValue());
				//aarqResult = DataUtil.append(aarqResult, AARQ.CALLING_AP_TITLE.getValue());
				aarqResult = DataUtil.append(aarqResult, AARQ.SENDER_ACSE_REQUIREMENTS.getValue());
				aarqResult = DataUtil.append(aarqResult, AARQ.MECHANISM_NAME.getValue());
				aarqResult = DataUtil.append(aarqResult, AARQ.CALLING_AUTHENTICATION_VALUE.getValue());
				aarqResult = DataUtil.append(aarqResult, AARQ.USER_INFORMATION.getValue());
				aarqResult[0] = DataUtil.getByteToInt(aarqResult.length - 1); // application length : -1은 length

				gdDLMSFrame = DataUtil.append(gdDLMSFrame, aarqResult);
				break;			
			case ACTION_REQ:
				gdDLMSFrame = DataUtil.append(super.encode(hdlcType, null, null), gdDLMSFrame);
				

				switch (procedure) {
				/*
				 *  공통 프로시저
				 */
				case HDLC_ASSOCIATION_LN:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ASSOCIATION_LN.CLASS_ASSOCIATION_LN.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ASSOCIATION_LN.CURRENT_ASSOCIATION_LN.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ASSOCIATION_LN.REPLY_TO_HLS_AUTHENTICATION.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x01, 0x09, 0x11 }); // param, octet-string, length 17
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, HLSSecurity.AUTHENTICATION.getValue());
					byte[] aReqIC = getInvoCounter();
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, aReqIC);

//					HLSAuth auth = new HLSAuth(HLSSecurity.AUTHENTICATION);
//					byte[] tagValue = auth.getTagValue(aReqIC, DlmsPiece.CLIENT_SYSTEM_TITLE.getBytes(), aareAuthenticationValue);
//					gdDLMSFrame = DataUtil.append(gdDLMSFrame, tagValue);
					
					System.out.println("gdDLMSFrame ------- " + Hex.decode(gdDLMSFrame));
					break;
				/*
				 *  Image Transfer 관련 프로시져 
				 */
				case ACTION_IMAGE_TRANSFER_INIT:
					/*
					 *  data ::= structure
						{
							image_identifier: octet-string,
							image_size: double-long-unsigned
						}
					 */
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferMethods.IMAGE_TRANSFER_INITIATE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_USE.getByteValue());

					byte[] image_identifier = ((String) param.get("image_identifier")).getBytes(); // F/W파일명

					byte[] structureA = new byte[4];
					structureA[0] = 0x02;
					structureA[1] = 0x02;
					structureA[2] = 0x09;
					structureA[3] = DataUtil.getByteToInt(image_identifier.length);

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, structureA);
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, image_identifier);

					byte[] structureB = new byte[] { 0x06 };
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, structureB);
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt((String) param.get("image_size"))));
					break;
				case ACTION_IMAGE_BLOCK_TRANSFER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferMethods.IMAGE_BLOCK_TRANSFER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_USE.getByteValue());

					byte[] imagePartA = new byte[3];
					imagePartA[0] = 0x02;
					imagePartA[1] = 0x02;
					imagePartA[2] = 0x06;
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, imagePartA);
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("image_block_number")))));

					byte[] imageBlock = (byte[]) param.get("image_block_value");

					/*
					 * Block Length 구하기
					 */
					byte[] imagePartB = null;
					if (128 <= imageBlock.length) {
						imagePartB = new byte[3];
						imagePartB[0] = 0x09;
						imagePartB[1] = (byte) 0x81;
						imagePartB[2] = DataUtil.getByteToInt(imageBlock.length);
					} else {
						imagePartB = new byte[2];
						imagePartB[0] = 0x09;
						imagePartB[1] = DataUtil.getByteToInt(imageBlock.length);
					}

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, imagePartB);
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, imageBlock);
					break;
				case ACTION_IMAGE_VERIFY:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferMethods.IMAGE_VERIFY.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_USE.getByteValue());

					byte[] verifyPartA = new byte[2];
					verifyPartA[0] = 0x0F;
					verifyPartA[1] = Byte.valueOf(String.valueOf(param.get("image_verify_data")));
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, verifyPartA);
					break;
				case ACTION_IMAGE_ACTIVATE:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferMethods.IMAGE_ACTIVATE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_USE.getByteValue());

					byte[] activatePartA = new byte[2];
					activatePartA[0] = 0x0F;
					activatePartA[1] = Byte.valueOf(String.valueOf(param.get("image_activate_data")));
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, activatePartA);
					break;
				/*
				 *  Meter Alarm Reset용 프로시져 
				 */
				case ACTION_METER_ALARM_RESET:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterParamSetMethods.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterParamSetMethods.OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterParamSetMethods.METER_ALARM_RESET.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterParamSetMethods.OPTION_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 }); // UINT16(0x12, 2), long-unsigned 2byte
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterParamSetMethods.RESET_ALARM.getByteValue());
					break;

				default:
					break;
				}

				break;
			case GET_REQ:
				gdDLMSFrame = DataUtil.append(super.encode(hdlcType, null, null), gdDLMSFrame);

				switch (procedure) {
				/*
				 *   Image Transfer 관련 프로시져 
				 */
				case GET_IMAGE_TRANSFER_ENABLE:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferAttributes.IMAGE_TRANSFER_ENABLED.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_NOT_USE.getByteValue());
					break;
				case GET_IMAGE_BLOCK_SIZE:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferAttributes.IMAGE_BLOCK_SIZE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_NOT_USE.getByteValue());
					break;
				case GET_IMAGE_TRANSFER_STATUS:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferAttributes.IMAGE_TRANSFER_STATUS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_NOT_USE.getByteValue());
					break;
				case GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferAttributes.IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_NOT_USE.getByteValue());
					break;
				case GET_IMAGE_TO_ACTIVATE_INFO:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferAttributes.IMAGE_TO_ACTIVATE_INFO.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_NOT_USE.getByteValue());
					break;

				/*
				 *   Meter F/W 버전 확인용 프로시져 
				 */
				case GET_FIRMWARE_VERSION:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterFWInfoAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterFWInfoAttributes.OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterFWInfoAttributes.FW_VERSION.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterFWInfoAttributes.OPTION_NOT_USE.getByteValue());
					break;

				/*
				 *   Meter BillingCycle information 확인 프로시져 
				 */
				case GET_BILLING_CYCLE:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.BILLING_CYCLE_EXECUTION_TIME.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.OPTION_NOT_USE.getByteValue());
					break;
				
				/*
				 *   Demand period information 확인 프로시져 
				 */
				case GET_DEMAND_PLUS_A_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x40 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_A_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DEMAND_PLUS_A_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x41 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_A_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DEMAND_MINUS_A_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());					
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x42 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_A_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DEMAND_MINUS_A_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x43 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_A_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DEMAND_PLUS_R_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x44 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_R_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DEMAND_PLUS_R_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x45 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_R_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DEMAND_MINUS_R_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x46 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_R_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DEMAND_MINUS_R_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x47 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_R_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DEMAND_R_QI_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x48 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_R_QI_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					
					break;
				case GET_DEMAND_R_QI_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x49 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_R_QI_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DEMAND_R_QIV_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4A });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_R_QIV_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DEMAND_R_QIV_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4B });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_R_QIV_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DEMAND_PLUS_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4C });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DEMAND_PLUS_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4D });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DEMAND_MINUS_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4E });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DEMAND_MINUS_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x50 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_CURRENT_LOAD_LIMIT_DURATION_JUDGE_TIME:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x40 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.DURATION_JUDGE_TIME_OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.VALUE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_CURRENT_LOAD_LIMIT_THRESHOLD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x41 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.THRESHOLD_OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.VALUE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_CALENDAR_NAME_PASSIVE:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x40 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVITY_CALENDAR.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CALENDAR_NAME_PASSIVE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_SEASON_PROFILE:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x41 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVITY_CALENDAR.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.SEASON_PROFILE_PASSIVE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_WEEK_PROFILE:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x42 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVITY_CALENDAR.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.WEEK_PROFILE_TABLE_PASSIVE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.OPTION_NOT_USE.getByteValue());
					break;
				case GET_DAY_PROFILE:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					
					if(param.get("isBlock") != null) {
						boolean isBlock = (boolean)param.get("isBlock");
						boolean isLast = (boolean)param.get("isLast");
						int blockNumber = (int)param.get("blockNumber");
						
						if(isBlock && !isLast) {
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NEXT.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x43 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(blockNumber));							
						}
					} else {
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x43 });

						gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CLASS_ID.getByteValue());
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVITY_CALENDAR.getByteValue());
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.DAY_PROFILE_TABLE_PASSIVE.getByteValue());
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.OPTION_NOT_USE.getByteValue());
					}
					break;				
				case GET_STARTING_DATE:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x44 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVITY_CALENDAR.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVATE_PASSIVE_CALENDAR_TIME.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.OPTION_NOT_USE.getByteValue());
					break;
				default:
					break;
				}

				break;
			case SET_REQ:
				gdDLMSFrame = DataUtil.append(super.encode(hdlcType, null, null), gdDLMSFrame);
				switch (procedure) {
				/*
				 *   Billing Cycle 관련 프로시져 
				 */
				case SET_BILLING_CYCLE:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.BILLING_CYCLE_EXECUTION_TIME.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.OPTION_NOT_USE.getByteValue());

					byte[] structureA = new byte[4];
					structureA[0] = (byte) 0x01;
					structureA[1] = (byte) 0x01;
					structureA[2] = (byte) 0x02;
					structureA[3] = (byte) 0x02;
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, structureA);

					String[] times = String.valueOf(param.get("time")).split(":");
					byte[] bilingPartA = new byte[6];
					bilingPartA[0] = (byte) 0x09;
					bilingPartA[1] = (byte) 0x04;
					bilingPartA[2] = (byte) DataUtil.getByteToInt(Integer.parseInt(times[0]));
					bilingPartA[3] = (byte) DataUtil.getByteToInt(Integer.parseInt(times[1]));
					bilingPartA[4] = (byte) DataUtil.getByteToInt(Integer.parseInt(times[2]));
					bilingPartA[5] = (byte) 0x00;
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, bilingPartA);

					int day = Integer.parseInt(String.valueOf(param.get("day")));
					byte[] bilingPartB = new byte[7];
					bilingPartB[0] = (byte) 0x09;
					bilingPartB[1] = (byte) 0x05;
					bilingPartB[2] = (byte) 0xFF;
					bilingPartB[3] = (byte) 0xFF;
					bilingPartB[4] = (byte) 0xFF;
					bilingPartB[5] = DataUtil.getByteToInt(day);
					bilingPartB[6] = (byte) 0xFF;
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, bilingPartB);
					break;
				/*
				 * Demand Period 관련 프로시저
				 */
				case SET_DEMAND_PLUS_A_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x40 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_A_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
					break;
				case SET_DEMAND_PLUS_A_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x41 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_A_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
					break;
				case SET_DEMAND_MINUS_A_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x42 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_A_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
					break;
				case SET_DEMAND_MINUS_A_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x43 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_A_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
 
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
					break;
				case SET_DEMAND_PLUS_R_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x44 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_R_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
					break;
				case SET_DEMAND_PLUS_R_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x45 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_R_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
					break;
				case SET_DEMAND_MINUS_R_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x46 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_R_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
					break;
				case SET_DEMAND_MINUS_R_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x47 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_R_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
					break;
				case SET_DEMAND_R_QI_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x48 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_R_QI_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
					break;
				case SET_DEMAND_R_QI_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x49 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_R_QI_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
					break;
				case SET_DEMAND_R_QIV_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4A });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_R_QIV_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
					break;
				case SET_DEMAND_R_QIV_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4B });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_R_QIV_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
					break;
				case SET_DEMAND_PLUS_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4C });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
					break;
				case SET_DEMAND_PLUS_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4D });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
					break;
				case SET_DEMAND_MINUS_PERIOD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4E });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_PERIOD.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
					break;
				case SET_DEMAND_MINUS_NUMBER:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x40 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_NUMBER.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
					break;
				/*
				 * TOU Setting 관련
				 */
				case SET_CALENDAR_NAME_PASSIVE:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					//gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[]{0x40});
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, getPriorityByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVITY_CALENDAR.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CALENDAR_NAME_PASSIVE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.OPTION_NOT_USE.getByteValue());

					byte[] calendarName = String.valueOf(param.get("calendarNamePassive")).getBytes();
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x09 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { DataUtil.getByteToInt(calendarName.length) });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, calendarName);
					break;
				case SET_SEASON_PROFILE:
				case SET_WEEK_PROFILE:
				case SET_DAY_PROFILE:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());

					if (param.get("infoBlockType") == TOUInfoBlockType.FIRST_BLOCK && !param.containsKey("blockNumber")) { // Single모드 전송인경우
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, getPriorityByteValue());
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CLASS_ID.getByteValue());
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVITY_CALENDAR.getByteValue());

						if (procedure == Procedure.SET_SEASON_PROFILE) {
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.SEASON_PROFILE_PASSIVE.getByteValue());
						} else if (procedure == Procedure.SET_WEEK_PROFILE) {
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.WEEK_PROFILE_TABLE_PASSIVE.getByteValue());
						} else if (procedure == Procedure.SET_DAY_PROFILE) {
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.DAY_PROFILE_TABLE_PASSIVE.getByteValue());
						}

						gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.OPTION_NOT_USE.getByteValue());
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, (byte[]) param.get("blockValue"));

					} else if (param.get("infoBlockType") == TOUInfoBlockType.FIRST_BLOCK && param.containsKey("blockNumber")) { // Multi 모드 전송인경우
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.WITH_FIRST_DATABLOCK.getByteValue());
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, getPriorityByteValue(true));
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CLASS_ID.getByteValue());
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVITY_CALENDAR.getByteValue());

						if (procedure == Procedure.SET_SEASON_PROFILE) {
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.SEASON_PROFILE_PASSIVE.getByteValue());
						} else if (procedure == Procedure.SET_WEEK_PROFILE) {
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.WEEK_PROFILE_TABLE_PASSIVE.getByteValue());
						} else if (procedure == Procedure.SET_DAY_PROFILE) {
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.DAY_PROFILE_TABLE_PASSIVE.getByteValue());
						}

						gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.OPTION_NOT_USE.getByteValue());
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, Boolean.valueOf(String.valueOf(param.get("isLastBlock"))) == true ? new byte[] { (byte) 0xFF } : new byte[] { 0x00 });
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("blockNumber")))));

						int bLength = Integer.parseInt(String.valueOf(param.get("blockLength")));
						byte[] blockLength;
						if (128 <= bLength) {
							blockLength = new byte[] { (byte) 0x81, DataUtil.getByteToInt(bLength) };
						} else {
							blockLength = new byte[] { DataUtil.getByteToInt(bLength) };
						}
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, blockLength);
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, (byte[]) param.get("blockValue"));

					} else if (param.get("infoBlockType") == TOUInfoBlockType.MIDDLE_BLOCK) {
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.WITH_DATABLOCK.getByteValue());
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, getPriorityByteValue(true));
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, Boolean.valueOf(String.valueOf(param.get("isLastBlock"))) == true ? new byte[] { (byte) 0xFF } : new byte[] { 0x00 });
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("blockNumber")))));

						int bLength = Integer.parseInt(String.valueOf(param.get("blockLength")));
						byte[] blockLength;
						if (128 <= bLength) {
							blockLength = new byte[] { (byte) 0x81, DataUtil.getByteToInt(bLength) };
						} else {
							blockLength = new byte[] { DataUtil.getByteToInt(bLength) };
						}
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, blockLength);
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, (byte[]) param.get("blockValue"));

					} else if (param.get("infoBlockType") == TOUInfoBlockType.LAST_BLOCK) {
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.WITH_DATABLOCK.getByteValue());
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, getPriorityByteValue());
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, Boolean.valueOf(String.valueOf(param.get("isLastBlock"))) == true ? new byte[] { (byte) 0xFF } : new byte[] { 0x00 });
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("blockNumber")))));

						int bLength = Integer.parseInt(String.valueOf(param.get("blockLength")));
						byte[] blockLength;
						if (128 <= bLength) {
							blockLength = new byte[] { (byte) 0x81, DataUtil.getByteToInt(bLength) };
						} else {
							blockLength = new byte[] { DataUtil.getByteToInt(bLength) };
						}
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, blockLength);
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, (byte[]) param.get("blockValue"));
					}

					break;
				case SET_STARTING_DATE:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, getPriorityByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVITY_CALENDAR.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVATE_PASSIVE_CALENDAR_TIME.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.OPTION_NOT_USE.getByteValue());

					String startingDate = String.valueOf(param.get("startingDate"));
					Calendar cal = DateTimeUtil.getCalendar(startingDate);
					byte[] dateTime = DataUtil.getDLMS_OCTETSTRING12ByDateTime(cal);

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x09 });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { DataUtil.getByteToInt(dateTime.length) });
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, dateTime);
					break;
				case SET_CURRENT_LOAD_LIMIT_DURATION_JUDGE_TIME:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x40 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.DURATION_JUDGE_TIME_OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.VALUE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
					
					String judgeTime = String.valueOf(param.get("judgeTime"));
					byte[] tempByptes = DataUtil.get4ByteToInt(Integer.parseInt(judgeTime));
					logger.debug("Set Current load limit judge time value = {}, HEX = {}", judgeTime, Hex.decode(tempByptes));
					
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, tempByptes);
					break;
				case SET_CURRENT_LOAD_LIMIT_THRESHOLD:
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x40 });

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.CLASS_ID.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.THRESHOLD_OBIS_CODE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.VALUE.getByteValue());
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, CurrentLoadLimitAttributes.OPTION_NOT_USE.getByteValue());

					gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
					
					String thresholdStr = String.valueOf(param.get("threshold"));
					thresholdStr = String.format("%.2f", Double.parseDouble(thresholdStr));
					String[] tempStr = thresholdStr.split("\\.");
					int tempValue = Integer.parseInt(tempStr[0] + tempStr[1]);
					byte[] tempBytes = DataUtil.get4ByteToInt(tempValue);
					logger.debug("Set Current load limit threshlod value = {}, HEX = {}", thresholdStr, Hex.decode(tempBytes));
					
					gdDLMSFrame = DataUtil.append(gdDLMSFrame, tempBytes);
					break;
				default:
					break;
				}
				break;
			case DISC:
				gdDLMSFrame = super.encode(hdlcType, null, null);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error("DLMS Encoding Error - {}", e);
			gdDLMSFrame = null;
		}

		return gdDLMSFrame;
	}

	@Override
	public boolean decode(IoSession session, byte[] frame, Procedure procedure) {
		logger.info("## Excute NestedDLMSDecorator Decoding...");
		boolean result = true;
		int pos = 0;
		int infoPos = 0;

		byte[] llc = null;
		byte[] information = null;

		try {
			//818014050200AF060200AF070400000001080400000001
			/*
			 * 81
			 * 80
			 * 14
			 * 	05
			 * 	02
			 * 	00AF
			 * 	06
			 * 	02
			 * 	00AF
			 * 	07
			 * 	04
			 * 	00000001
			 * 	08
			 * 	04
			 * 	00000001
			 */
			if (HdlcObjectType.getItem(getType()) == HdlcObjectType.UA) {
				byte[] formatIdentifier = new byte[1];
				System.arraycopy(frame, infoPos, formatIdentifier, 0, formatIdentifier.length);
				infoPos += formatIdentifier.length;
				logger.debug("[DLMS] FORMAT_IDENTIFIER = [{}]", Hex.decode(formatIdentifier));

				byte[] groupIdentifier = new byte[1];
				System.arraycopy(frame, infoPos, groupIdentifier, 0, groupIdentifier.length);
				infoPos += groupIdentifier.length;
				logger.debug("[DLMS] GROUP_IDENTIFIER = [{}]", Hex.decode(groupIdentifier));

				byte[] groupLength = new byte[1];
				System.arraycopy(frame, infoPos, groupLength, 0, groupLength.length);
				infoPos += groupLength.length;
				logger.debug("[DLMS] GROUP_LENGTH = [{}]", Hex.decode(groupLength));

				byte[] sendIdentifier = new byte[1];
				System.arraycopy(frame, infoPos, sendIdentifier, 0, sendIdentifier.length);
				infoPos += sendIdentifier.length;
				logger.debug("[DLMS] PARAM_SEND_IDENTIFIER = [{}]", Hex.decode(sendIdentifier));

				byte[] sendLength = new byte[1];
				System.arraycopy(frame, infoPos, sendLength, 0, sendLength.length);
				infoPos += sendLength.length;
				logger.debug("[DLMS] PARAM_SEND_LENGTH = [{}]", Hex.decode(sendLength));

				int totalLength = Integer.parseInt(String.valueOf(getResultData()));
				int maximumTransmitLength = 0;
				if(31 < totalLength){
					byte[] sendValue = new byte[2];
					System.arraycopy(frame, infoPos, sendValue, 0, sendValue.length);
					infoPos += sendValue.length;
					
					maximumTransmitLength = DataUtil.getIntTo2Byte(sendValue);
				}else{
					byte[] sendValue = new byte[1];
					System.arraycopy(frame, infoPos, sendValue, 0, sendValue.length);
					infoPos += sendValue.length;
					
					maximumTransmitLength = DataUtil.getIntToByte(sendValue[0]);
				}
				logger.debug("[DLMS] MAXIMUM_TRANSMIT_LENGTH = [{}]", maximumTransmitLength);

				byte[] receiveIdentifier = new byte[1];
				System.arraycopy(frame, infoPos, receiveIdentifier, 0, receiveIdentifier.length);
				infoPos += receiveIdentifier.length;
				logger.debug("[DLMS] PARAM_RECEIVE_IDENTIFIER = [{}]", Hex.decode(receiveIdentifier));

				byte[] receiveLength = new byte[1];
				System.arraycopy(frame, infoPos, receiveLength, 0, receiveLength.length);
				infoPos += receiveLength.length;
				logger.debug("[DLMS] PARAM_RECEIVE_LENGTH = [{}]", Hex.decode(receiveLength));

				int sendHdlcPacketMaxSize = 0;
				if(31 < totalLength){
					byte[] receiveValue = new byte[2];
					System.arraycopy(frame, infoPos, receiveValue, 0, receiveValue.length);
					infoPos += receiveValue.length;
					sendHdlcPacketMaxSize = DataUtil.getIntTo2Byte(receiveValue);
				}else{
					byte[] receiveValue = new byte[1];
					System.arraycopy(frame, infoPos, receiveValue, 0, receiveValue.length);
					infoPos += receiveValue.length;
					sendHdlcPacketMaxSize = DataUtil.getIntToByte(receiveValue[0]);
				}
				logger.debug("[DLMS] MAXIMUM_RECEIVE_LENGTH = [{}]", sendHdlcPacketMaxSize);
				setResultData(sendHdlcPacketMaxSize);	

				byte[] sendWindowIdentifier = new byte[1];
				System.arraycopy(frame, infoPos, sendWindowIdentifier, 0, sendWindowIdentifier.length);
				infoPos += sendWindowIdentifier.length;
				logger.debug("[DLMS] PARAM_WINDOW_SEND_IDENTIFIER = [{}]", Hex.decode(sendWindowIdentifier));

				byte[] sendWindowLength = new byte[1];
				System.arraycopy(frame, infoPos, sendWindowLength, 0, sendWindowLength.length);
				infoPos += sendWindowLength.length;
				logger.debug("[DLMS] PARAM_WINDOW_SEND_LENGTH = [{}]", Hex.decode(sendWindowLength));

				byte[] sendWindowValue = new byte[4];
				System.arraycopy(frame, infoPos, sendWindowValue, 0, sendWindowValue.length);
				infoPos += sendWindowValue.length;
				logger.debug("[DLMS] PARAM_WINDOW_SEND_VALUE = [{}]", DataUtil.getIntTo4Byte(sendWindowValue));

				byte[] receiveWindowIdentifier = new byte[1];
				System.arraycopy(frame, infoPos, receiveWindowIdentifier, 0, receiveWindowIdentifier.length);
				infoPos += receiveWindowIdentifier.length;
				logger.debug("[DLMS] PARAM_WINDOW_RECEIVE_IDENTIFIER = [{}]", Hex.decode(receiveWindowIdentifier));

				byte[] receiveWindowLength = new byte[1];
				System.arraycopy(frame, infoPos, receiveWindowLength, 0, receiveWindowLength.length);
				infoPos += receiveWindowLength.length;
				logger.debug("[DLMS] PARAM_WINDOW_RECEIVE_LENGTH = [{}]", Hex.decode(receiveWindowLength));

				byte[] receiveWindowValue = new byte[4];
				System.arraycopy(frame, infoPos, receiveWindowValue, 0, receiveWindowValue.length);
				infoPos += receiveWindowValue.length;
				logger.debug("[DLMS] PARAM_WINDOW_RECEIVE_LENGTH = [{}]", DataUtil.getIntTo4Byte(receiveWindowValue));

				logger.debug("[DLMS] UA = [{}]", Hex.decode(frame));
			} else {
				llc = new byte[3];
				pos = 0;

				System.arraycopy(frame, pos, llc, 0, llc.length);
				pos += llc.length;
				logger.debug("[DLMS] LLC = [{}]", Hex.decode(llc));

				information = new byte[frame.length - 3]; // 3 : llc
				System.arraycopy(frame, pos, information, 0, information.length);

				/**
				 * IFrame Type 파싱
				 */
				List<HashMap<AARE, byte[]>> aareList = new ArrayList<HashMap<AARE, byte[]>>();

				infoPos = 0;
				byte[] commandType = new byte[1];
				System.arraycopy(information, infoPos, commandType, 0, commandType.length);
				infoPos += commandType.length;

				// Command 타입설정
				setType(DataUtil.getIntToByte(commandType[0]));
				logger.debug("[DLMS] COMMAND_TYPE = [{}]", HdlcObjectType.getItem(commandType[0]).name());

				if (HdlcObjectType.getItem(commandType[0]) == HdlcObjectType.AARE) {
					/**
					 * AARE Parsing
					 */
					byte[] infoLength = new byte[1];
					System.arraycopy(information, infoPos, infoLength, 0, infoLength.length);
					infoPos += infoLength.length;

					for (int i = infoPos; i < DataUtil.getIntToByte(infoLength[0]); i = infoPos) {
						byte[] tagLength = new byte[2];
						System.arraycopy(information, infoPos, tagLength, 0, tagLength.length);
						infoPos += tagLength.length;

						AARE tag = AARE.getItem(tagLength[0]);

						// Server System Title or StoC 일 경우 한단계 더 들어감.
						if (AARE.getItem(tagLength[0]) == AARE.RESPONDING_AP_TITLE 
								|| AARE.getItem(tagLength[0]) == AARE.RESPONDING_AUTHENTICATION_VALUE 
								|| AARE.getItem(tagLength[0]) == AARE.APPLICATION_CONTEXT_NAME) {
							tagLength = new byte[2];
							System.arraycopy(information, infoPos, tagLength, 0, tagLength.length);
							infoPos += tagLength.length;
						}

						int valueLength = DataUtil.getIntToByte(tagLength[1]);
						byte[] value = new byte[valueLength];
						System.arraycopy(information, infoPos, value, 0, value.length);
						infoPos += value.length;

						HashMap<AARE, byte[]> item = new HashMap<AARE, byte[]>();
						item.put(tag, value);
						aareList.add(item);
					}

					Iterator<HashMap<AARE, byte[]>> aareIeter = aareList.iterator();
					while (aareIeter.hasNext()) {
						HashMap<AARE, byte[]> map = aareIeter.next();
						if (map.containsKey(AARE.RESPONDING_AP_TITLE)) { // responding-AP-title
							aareRespondingAPtitle = map.get(AARE.RESPONDING_AP_TITLE);
						} else if (map.containsKey(AARE.RESPONDING_AUTHENTICATION_VALUE)) { // Authentication value
							aareAuthenticationValue = map.get(AARE.RESPONDING_AUTHENTICATION_VALUE);
						} else if (map.containsKey(AARE.APPLICATION_CONTEXT_NAME)) { // Authentication value
							aareApplicationContextName = map.get(AARE.APPLICATION_CONTEXT_NAME);
						}
					}

					logger.debug("[DLMS] ## APPLICATION_CONTEXT_NAME = [{}]", Hex.decode(aareApplicationContextName));
					logger.debug("[DLMS] ## SERVER_SYSTEM_TITLE = [{}]", Hex.decode(aareRespondingAPtitle));
					logger.debug("[DLMS] ## S_TO_C = [{}]", Hex.decode(aareAuthenticationValue));

//					if (aareRespondingAPtitle == null || aareRespondingAPtitle.equals("") || aareAuthenticationValue == null || aareAuthenticationValue.equals("")) {
					if (aareApplicationContextName == null || aareApplicationContextName.equals("")) {
						setResultData(false); 
					} else {
						setResultData(true);
					}

					//					result = true;
				} else if (HdlcObjectType.getItem(commandType[0]) == HdlcObjectType.ACTION_RES) {
					/**
					 * Action response Parsing
					 */
					byte[] resonseType = new byte[1];
					System.arraycopy(information, infoPos, resonseType, 0, resonseType.length);
					infoPos += resonseType.length;
					logger.debug("[DLMS] ACTION-Response = [{}]", ActionResponse.getItem(resonseType[0]).name());

					switch (ActionResponse.getItem(resonseType[0])) {
					case NORMAL:
						byte[] idProperty = new byte[1];
						System.arraycopy(information, infoPos, idProperty, 0, idProperty.length);
						infoPos += idProperty.length;
						logger.debug("[DLMS] Invoke-Id-And-Priority = [{}]", Hex.decode(idProperty));

						byte[] actionResult = new byte[1];
						System.arraycopy(information, infoPos, actionResult, 0, actionResult.length);
						infoPos += actionResult.length;
						ActionResult aResult = ActionResult.getItem(actionResult[0]);
						//logger.debug("[DLMS] Action-Result = [{}]", aResult.name());

						// 결과 저장
						setResultData(aResult);

						if (aResult == ActionResult.SUCCESS) { // 성공
							if (procedure == Procedure.HDLC_ASSOCIATION_LN) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));

								byte[] data = new byte[1];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;
								logger.debug("[DLMS] Data = [{}]", Hex.decode(data));

								byte[] octetString = new byte[1];
								System.arraycopy(information, infoPos, octetString, 0, octetString.length);
								infoPos += octetString.length;
								logger.debug("[DLMS] OCTET_STRING = [{}]", Hex.decode(octetString));

								byte[] length = new byte[1];
								System.arraycopy(information, infoPos, length, 0, length.length);
								infoPos += length.length;
								logger.debug("[DLMS] LENGTH = [{}]", Hex.decode(length));

								byte[] securityControlByte = new byte[1];
								System.arraycopy(information, infoPos, securityControlByte, 0, securityControlByte.length);
								infoPos += securityControlByte.length;
								logger.debug("[DLMS] SECURITY_CONTROL = [{}]", Hex.decode(securityControlByte));

								byte[] invocationCounter = new byte[4];
								System.arraycopy(information, infoPos, invocationCounter, 0, invocationCounter.length);
								infoPos += invocationCounter.length;
								logger.debug("[DLMS] INVOCATION_COUNTER = [{}]", Hex.decode(invocationCounter));

								byte[] tagValue = new byte[12];
								System.arraycopy(information, infoPos, tagValue, 0, tagValue.length);
								infoPos += tagValue.length;
								logger.debug("[DLMS] TAG_VALUE = [{}]", Hex.decode(tagValue));

								/**
								 * Validation
								 */
								HLSAuth auth = new HLSAuth(HLSSecurity.AUTHENTICATION);
								result = auth.doValidation(aareRespondingAPtitle, invocationCounter, DlmsPiece.C_TO_S.getBytes(), tagValue);
								if (!result) {
									logger.debug("[ActionResponse Validation Fail~!! ㅡ,.ㅡ^] 관리자에 문의 바람");
									logger.debug("[ActionResponse Validation Fail~!! ㅡ,.ㅡ^] 관리자에 문의 바람");
									logger.debug("[ActionResponse Validation Fail~!! ㅡ,.ㅡ^] 관리자에 문의 바람");
									logger.debug("[ActionResponse Validation Fail~!! ㅡ,.ㅡ^] 관리자에 문의 바람");
								}
							} else if (procedure == Procedure.ACTION_IMAGE_TRANSFER_INIT) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							} else if (procedure == Procedure.ACTION_IMAGE_BLOCK_TRANSFER) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							} else if (procedure == Procedure.ACTION_IMAGE_VERIFY) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							} else if (procedure == Procedure.ACTION_IMAGE_ACTIVATE) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							} else if (procedure == Procedure.ACTION_METER_ALARM_RESET) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							} else {
								logger.error("### [{}] 해당 Procedure 아직 미구현..!!!", procedure.name());
								logger.error("### [{}] 해당 Procedure 아직 미구현..!!!", procedure.name());
								logger.error("### [{}] 해당 Procedure 아직 미구현..!!!", procedure.name());
								result = false;
							}

						} else {
							logger.debug("[DLMS] Action-Result Fail = [{}]", aResult.name());
							//throw new Exception("[ACTION_RES] Action Result = " + Hex.decode(actionResult) + " - Fail");
						}
						break;
					case WITH_PBLOCK:
						/*
						 * 추후 필요시 구현
						 */
						break;
					case WITH_LIST:
						/*
						 * 추후 필요시 구현
						 */
						break;
					case NEXT_PBLOCK:
						/*
						 * 추후 필요시 구현
						 */
						break;
					default:
						break;
					}

					//					result = true;
				} else if (HdlcObjectType.getItem(commandType[0]) == HdlcObjectType.GET_RES) {
					/**
					 * Action response Parsing
					 */
					byte[] getResponseType = new byte[1];
					System.arraycopy(information, infoPos, getResponseType, 0, getResponseType.length);
					infoPos += getResponseType.length;
					logger.debug("[DLMS] GET-Response = [{}]", GetResponse.getItem(getResponseType[0]).name());

					switch (GetResponse.getItem(getResponseType[0])) {
					case NORMAL:
						byte[] idProperty = new byte[1];
						System.arraycopy(information, infoPos, idProperty, 0, idProperty.length);
						infoPos += idProperty.length;
						logger.debug("[DLMS] invoke-id-and-priority = [{}]", Hex.decode(idProperty));

						byte[] getDataResult = new byte[1];
						System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
						infoPos += getDataResult.length;
						GetDataResult aResult = GetDataResult.getItem(getDataResult[0]);
						logger.debug("[DLMS] Get-Data-Result = [{}]", aResult.name());

						if (aResult == GetDataResult.DATA) {
							byte[] getDataResultType = new byte[1];
							System.arraycopy(information, infoPos, getDataResultType, 0, getDataResultType.length);
							infoPos += getDataResultType.length;

							logger.debug("[DLMS] Get-Data-Result-type = [{}]", DLMSCommonDataType.getItem(getDataResultType[0]).name());

							byte[] data = null;
							switch (DLMSCommonDataType.getItem(getDataResultType[0])) {
							case Boolean:
								data = new byte[DLMSCommonDataType.Boolean.getLenth()];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;
								boolean getResult = Boolean.valueOf(String.valueOf(DlmsConstantsForEVN.getValueByDLMSCommonDataType(DLMSCommonDataType.Boolean, data)));
								setResultData(getResult);

								logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", getResult);
								break;
							case FLOAT32:
								/*
								 * 추후 필요시 구현
								 */
								break;
							case UINT8:
								/*
								 * 추후 필요시 구현
								 */
								break;
							case UINT16:
								data = new byte[DLMSCommonDataType.UINT16.getLenth()];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;
								long long16Result = Long.parseLong(String.valueOf(DlmsConstantsForEVN.getValueByDLMSCommonDataType(DLMSCommonDataType.UINT16, data)));
								setResultData(long16Result);
								
								logger.debug("[DLMS] Get-Data-Result-type-data = [{}]" , long16Result);
								break;
							case UINT32:
								data = new byte[DLMSCommonDataType.UINT32.getLenth()];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;
								long longResult = Long.parseLong(String.valueOf(DlmsConstantsForEVN.getValueByDLMSCommonDataType(DLMSCommonDataType.UINT32, data)));
								setResultData(longResult);

								logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", longResult);
								break;
							case Enum:
								data = new byte[DLMSCommonDataType.Enum.getLenth()];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;

								setResultData(DataUtil.getIntToBytes(data));

								logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", DataUtil.getIntToBytes(data));
								break;
							case OctetString:
								byte[] octetLength = new byte[1];
								System.arraycopy(information, infoPos, octetLength, 0, octetLength.length);
								infoPos += octetLength.length;

								data = new byte[DataUtil.getIntToByte(octetLength[0])];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;
								
								if (procedure == Procedure.GET_CALENDAR_NAME_PASSIVE) {	
									setResultData(DataUtil.getString(data));
									logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", DataUtil.getString(data));
								} else if (procedure == Procedure.GET_STARTING_DATE) {									
									setResultData(getDateTime(data));									
									logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", getDateTime(data));	
								} else {
									setResultData(DataUtil.getString(data));
									logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", DataUtil.getString(data));
								}								
								break;
							case Array:
								/*
								 *  array image_to_activate_info_element
									image_to_activate_info_element ::= structure
									{
										image_to_activate_size: double-long-unsigned,
										image_to_activate_identification: octet-string,
										image_to_activate_signature: octet-string
									} 
								 */
								if (procedure == Procedure.GET_IMAGE_TO_ACTIVATE_INFO) {
									byte[] arrayLength = new byte[1];
									System.arraycopy(information, infoPos, arrayLength, 0, arrayLength.length);
									infoPos += arrayLength.length;

									byte[] structure = new byte[1];
									System.arraycopy(information, infoPos, structure, 0, structure.length);
									infoPos += structure.length;

									byte[] structureLength = new byte[1];
									System.arraycopy(information, infoPos, structureLength, 0, structureLength.length);
									infoPos += structureLength.length;

									byte[] eltype = new byte[1];
									System.arraycopy(information, infoPos, eltype, 0, eltype.length);
									infoPos += eltype.length;

									byte[] image_to_activate_sizeData = new byte[DLMSCommonDataType.UINT32.getLenth()];
									System.arraycopy(information, infoPos, image_to_activate_sizeData, 0, image_to_activate_sizeData.length);
									infoPos += image_to_activate_sizeData.length;
									long image_to_activate_size = Long.parseLong(String.valueOf(DlmsConstantsForEVN.getValueByDLMSCommonDataType(DLMSCommonDataType.UINT32, image_to_activate_sizeData)));
									logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", image_to_activate_size);

									byte[] eltype2 = new byte[1];
									System.arraycopy(information, infoPos, eltype2, 0, eltype2.length);
									infoPos += eltype2.length;

									byte[] eltype2Length = new byte[1];
									System.arraycopy(information, infoPos, eltype2Length, 0, eltype2Length.length);
									infoPos += eltype2Length.length;

									byte[] image_to_activate_identificationData = new byte[DataUtil.getIntToByte(eltype2Length[0])];
									System.arraycopy(information, infoPos, image_to_activate_identificationData, 0, image_to_activate_identificationData.length);
									infoPos += image_to_activate_identificationData.length;
									logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", Hex.decode(image_to_activate_identificationData));

									byte[] eltype3 = new byte[1];
									System.arraycopy(information, infoPos, eltype3, 0, eltype3.length);
									infoPos += eltype3.length;

									byte[] eltype3Length = new byte[1];
									System.arraycopy(information, infoPos, eltype3Length, 0, eltype3Length.length);
									infoPos += eltype3Length.length;

									HashMap<String, Object> resultDataMap = new HashMap<String, Object>();
									resultDataMap.put("image_to_activate_size", image_to_activate_size);
									resultDataMap.put("image_to_activate_identification", image_to_activate_identificationData);

									// image_to_activate_signature 가 없는경우도 있는것 같음.
									if (0 < DataUtil.getIntToByte(eltype3Length[0])) {
										byte[] image_to_activate_signatureData = new byte[DataUtil.getIntToByte(eltype3Length[0])];
										System.arraycopy(information, infoPos, image_to_activate_signatureData, 0, image_to_activate_signatureData.length);
										infoPos += image_to_activate_signatureData.length;
										logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", Hex.decode(image_to_activate_signatureData));

										resultDataMap.put("image_to_activate_signature", image_to_activate_signatureData);
									}

									setResultData(resultDataMap);
								} else if (procedure == Procedure.GET_BILLING_CYCLE) {
									byte[] arrayLength = new byte[1];
									System.arraycopy(information, infoPos, arrayLength, 0, arrayLength.length);
									infoPos += arrayLength.length;

									byte[] structure = new byte[1];
									System.arraycopy(information, infoPos, structure, 0, structure.length);
									infoPos += structure.length;

									byte[] structureLength = new byte[1];
									System.arraycopy(information, infoPos, structureLength, 0, structureLength.length);
									infoPos += structureLength.length;

									byte[] dataResultType1 = new byte[1];
									System.arraycopy(information, infoPos, dataResultType1, 0, dataResultType1.length);
									infoPos += dataResultType1.length;

									byte[] oLength1 = new byte[1];
									System.arraycopy(information, infoPos, oLength1, 0, oLength1.length);
									infoPos += oLength1.length;

									byte[] time = new byte[DataUtil.getIntToByte(oLength1[0])];
									System.arraycopy(information, infoPos, time, 0, time.length);
									infoPos += time.length;

									byte[] dataResultType2 = new byte[1];
									System.arraycopy(information, infoPos, dataResultType2, 0, dataResultType2.length);
									infoPos += dataResultType2.length;

									byte[] oLength2 = new byte[1];
									System.arraycopy(information, infoPos, oLength2, 0, oLength2.length);
									infoPos += oLength2.length;

									byte[] day = new byte[DataUtil.getIntToByte(oLength2[0])];
									System.arraycopy(information, infoPos, day, 0, day.length);
									infoPos += day.length;

									String times = String.format("%02d", DataUtil.getIntToByte(time[0])) + ":" + String.format("%02d", DataUtil.getIntToByte(time[1])) + ":" + String.format("%02d", DataUtil.getIntToByte(time[2]));
									int days = DataUtil.getIntToByte(day[3]);

									HashMap<String, String> r = new HashMap<String, String>();
									r.put("time", times);
									r.put("day", String.format("%02d", days));

									setResultData(r);

									logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", r.toString());
									
								} else if (procedure == procedure.GET_SEASON_PROFILE) {
									
									byte[] arrayLength = new byte[1];
									System.arraycopy(information, infoPos, arrayLength, 0, arrayLength.length);
									infoPos += arrayLength.length;
									
									List r = new ArrayList<HashMap<String, String>>();
									
									for(int i = 0; i < DataUtil.getIntToByte(arrayLength[0]); i++) {
										
										HashMap<String, String> map = new HashMap<String, String>();
										
										byte[] structure = new byte[1];
										System.arraycopy(information, infoPos, structure, 0, structure.length);
										infoPos += structure.length;
										
										byte[] structureLength = new byte[1];
										System.arraycopy(information, infoPos, structureLength, 0, structureLength.length);
										infoPos += structureLength.length;
										
										byte[] datatype = new byte[1];
										System.arraycopy(information, infoPos, datatype, 0, datatype.length);
										infoPos += datatype.length;
										
										byte[] dataLength = new byte[1];
										System.arraycopy(information, infoPos, dataLength, 0, dataLength.length);
										infoPos += dataLength.length;
										
										// season profile name
										byte[] dataSeasonProfileName = new byte[DataUtil.getIntToByte(dataLength[0])];
										System.arraycopy(information, infoPos, dataSeasonProfileName, 0, dataSeasonProfileName.length);
										infoPos += dataSeasonProfileName.length;										
										map.put("SeasonProfileName", DataUtil.getString(dataSeasonProfileName));
										logger.debug("[DLMS] season profile name = [{}]", DataUtil.getString(dataSeasonProfileName));
										
										datatype = new byte[1];
										System.arraycopy(information, infoPos, datatype, 0, datatype.length);
										infoPos += datatype.length;
										
										dataLength = new byte[1];
										System.arraycopy(information, infoPos, dataLength, 0, dataLength.length);
										infoPos += dataLength.length;
										
										// season start
										byte[] dataSeasonStart = new byte[DataUtil.getIntToByte(dataLength[0])];
										System.arraycopy(information, infoPos, dataSeasonStart, 0, dataSeasonStart.length);
										infoPos += dataSeasonStart.length;
										map.put("SeasonStart", getDateTime(dataSeasonStart));
										logger.debug("[DLMS] season start = [{}]", getDateTime(dataSeasonStart));
										
										datatype = new byte[1];
										System.arraycopy(information, infoPos, datatype, 0, datatype.length);
										infoPos += datatype.length;
										
										dataLength = new byte[1];
										System.arraycopy(information, infoPos, dataLength, 0, dataLength.length);
										infoPos += dataLength.length;
										
										// week name
										byte[] dataWeekName = new byte[DataUtil.getIntToByte(dataLength[0])];
										System.arraycopy(information, infoPos, dataWeekName, 0, dataWeekName.length);
										infoPos += dataWeekName.length;										
										map.put("SeasonWeekName", DataUtil.getString(dataWeekName));
										logger.debug("[DLMS] week name = [{}]", DataUtil.getString(dataWeekName));
										
										r.add(map);
									}
									setResultData(r);
									
								} else if (procedure == procedure.GET_WEEK_PROFILE) {
									
									String[] weekName = {"MON","TUE","WED","THD","FRI","SAT","SUN"};
									
									byte[] arrayLength = new byte[1];
									System.arraycopy(information, infoPos, arrayLength, 0, arrayLength.length);
									infoPos += arrayLength.length;
									
									List r = new ArrayList<HashMap<String, String>>();
									
									for(int i = 0; i < DataUtil.getIntToByte(arrayLength[0]); i++) {
										
										HashMap<String, String> map = new HashMap<String, String>();
										
										byte[] structure = new byte[1];
										System.arraycopy(information, infoPos, structure, 0, structure.length);
										infoPos += structure.length;
										
										byte[] structureLength = new byte[1];
										System.arraycopy(information, infoPos, structureLength, 0, structureLength.length);
										infoPos += structureLength.length;
										
										byte[] datatype = new byte[1];
										System.arraycopy(information, infoPos, datatype, 0, datatype.length);
										infoPos += datatype.length;
										
										byte[] dataLength = new byte[1];
										System.arraycopy(information, infoPos, dataLength, 0, dataLength.length);
										infoPos += dataLength.length;
										
										// week profile name
										byte[] dataWeekProfileName = new byte[DataUtil.getIntToByte(dataLength[0])];
										System.arraycopy(information, infoPos, dataWeekProfileName, 0, dataWeekProfileName.length);
										infoPos += dataWeekProfileName.length;										
										map.put("WeekProfileName", DataUtil.getString(dataWeekProfileName));
										logger.debug("[DLMS] WeekProfileName = [{}]", DataUtil.getString(dataWeekProfileName));
										
										for(int w = 0; w < 7; w++) {
										
											datatype = new byte[1];
											System.arraycopy(information, infoPos, datatype, 0, datatype.length);
											infoPos += datatype.length;
											
											byte[] week = new byte[1];
											System.arraycopy(information, infoPos, week, 0, week.length);
											infoPos += week.length;
											logger.debug("[DLMS] weekName = [{}]", weekName[w] + " " +DataUtil.getIntToByte(week[0]));
											map.put(weekName[w], String.valueOf(DataUtil.getIntToByte(week[0])));
											
										}										
										r.add(map);
										
									} // end for
									setResultData(r);
									
								} else if (procedure == procedure.GET_DAY_PROFILE) {
									
									byte[] arrayLength = new byte[1];
									System.arraycopy(information, infoPos, arrayLength, 0, arrayLength.length);
									infoPos += arrayLength.length;
									
									List r = new ArrayList<HashMap<String, String>>();
									
									for(int i = 0; i < DataUtil.getIntToByte(arrayLength[0]); i++) {
										
										byte[] structure = new byte[1];
										System.arraycopy(information, infoPos, structure, 0, structure.length);
										infoPos += structure.length;
										
										byte[] structureLength = new byte[1];
										System.arraycopy(information, infoPos, structureLength, 0, structureLength.length);
										infoPos += structureLength.length;
										
										byte[] datatype = new byte[1];
										System.arraycopy(information, infoPos, datatype, 0, datatype.length);
										infoPos += datatype.length;
										
										byte[] dayid = new byte[1];
										System.arraycopy(information, infoPos, dayid, 0, dayid.length);
										infoPos += dayid.length;
																				
										datatype = new byte[1];
										System.arraycopy(information, infoPos, datatype, 0, datatype.length);
										infoPos += datatype.length;
									
										byte[] arrayLength2 = new byte[1];
										System.arraycopy(information, infoPos, arrayLength2, 0, arrayLength2.length);
										infoPos += arrayLength2.length;
										
										for(int d = 0; d < DataUtil.getIntToByte(arrayLength2[0]); d++) {
											
											HashMap<String, String> map = new HashMap<String, String>();
											
											structure = new byte[1];
											System.arraycopy(information, infoPos, structure, 0, structure.length);
											infoPos += structure.length;
											
											structureLength = new byte[1];
											System.arraycopy(information, infoPos, structureLength, 0, structureLength.length);
											infoPos += structureLength.length;
											
											datatype = new byte[1];
											System.arraycopy(information, infoPos, datatype, 0, datatype.length);
											infoPos += datatype.length;
											
											byte[] dataLength = new byte[1];
											System.arraycopy(information, infoPos, dataLength, 0, dataLength.length);
											infoPos += dataLength.length;
											
											// start time
											byte[] dataStartTime = new byte[DataUtil.getIntToByte(dataLength[0])];
											System.arraycopy(information, infoPos, dataStartTime, 0, dataStartTime.length);
											infoPos += dataStartTime.length;									
											map.put("StartTime",  getStartTime(dataStartTime));
											logger.debug("[DLMS] startTime = [{}]", getStartTime(dataStartTime));
											
											datatype = new byte[1];
											System.arraycopy(information, infoPos, datatype, 0, datatype.length);
											infoPos += datatype.length;
											
											dataLength = new byte[1];
											System.arraycopy(information, infoPos, dataLength, 0, dataLength.length);
											infoPos += dataLength.length;
											
											// start time
											byte[] scriptLogicalName = new byte[DataUtil.getIntToByte(dataLength[0])];
											System.arraycopy(information, infoPos, scriptLogicalName, 0, scriptLogicalName.length);
											infoPos += scriptLogicalName.length;
											map.put("ScriptLogicalName", getObisCode(scriptLogicalName));
											logger.debug("[DLMS] scriptLogicalName = [{}]", getObisCode(scriptLogicalName));
											
											datatype = new byte[1];
											System.arraycopy(information, infoPos, datatype, 0, datatype.length);
											infoPos += datatype.length;
																				
											// start time
											byte[] timeBucketNo = new byte[2];
											System.arraycopy(information, infoPos, timeBucketNo, 0, timeBucketNo.length);
											map.put("timeBucketNo", String.valueOf(DataUtil.getIntTo2Byte(timeBucketNo)));
											infoPos += timeBucketNo.length;
											
											logger.debug("[DLMS] timeBucketNo = [{}]", DataUtil.getIntTo2Byte(timeBucketNo));
											
											map.put("dayid",  String.valueOf(DataUtil.getIntToByte(dayid[0])));
											
											r.add(map);
											
										} // end for
										setResultData(r);
									} //end for
								}

								break;
							default:
								break;
							}

						} else if (aResult == GetDataResult.DATA_ACCESS_RESULT) {
							byte[] getDataAccessResult = new byte[1];
							System.arraycopy(information, infoPos, getDataAccessResult, 0, getDataAccessResult.length);
							infoPos += getDataAccessResult.length;
							setResultData(DataAccessResult.getItem(getDataAccessResult[0]));
							logger.debug("[DLMS] Get-Data-Access-Result = [{}]", DataAccessResult.getItem(getDataAccessResult[0]).name());
						}

						break;
					case WITH_DATABLOCK:
						/*
						 * 추후 필요시 구현
						 */
						byte[] idPropertyB = new byte[1];
						System.arraycopy(information, infoPos, idPropertyB, 0, idPropertyB.length);
						infoPos += idPropertyB.length;
						logger.debug("[DLMS] invoke-id-and-priority = [{}]", Hex.decode(idPropertyB));

						byte[] lastBlock = new byte[1];
						System.arraycopy(information, infoPos, lastBlock, 0, lastBlock.length);
						infoPos += lastBlock.length;
						logger.debug("[DLMS] last-block = [{}]", lastBlock[0] == 0 ? "FALSE" : "TRUE");

						byte[] blockNumber = new byte[4];
						System.arraycopy(information, infoPos, blockNumber, 0, blockNumber.length);
						infoPos += blockNumber.length;
						logger.debug("[DLMS] block-number = [{}]", DataUtil.getIntTo4Byte(blockNumber));

						byte[] getResultChoice = new byte[1];
						System.arraycopy(information, infoPos, getResultChoice, 0, getResultChoice.length);
						infoPos += getResultChoice.length;
						aResult = GetDataResult.getItem(getResultChoice[0]);
						logger.debug("[DLMS] Get-Data-Result = [{}]", aResult.name());

						if (aResult == GetDataResult.DATA) {
							/*
							 * Block Length 구하기 : 사실 이 로직에서 길이는 별의미가 없음 단지 infoPos를 늘리기위함.
							 */
							byte[] length = new byte[1];
							System.arraycopy(information, infoPos, length, 0, length.length);
							infoPos += length.length;

							byte[] byteLength = null;
							if ((length[0] & 0x80) == 0x80) {
								byteLength = new byte[(length[0] & 0x7F)];
								System.arraycopy(information, infoPos, byteLength, 0, byteLength.length);
								infoPos += byteLength.length;
								logger.debug("[DLMS] this block byte Length= [{}]", Hex.decode(byteLength));

								length = byteLength;
							}

							// 
							lpRawData = new byte[information.length - infoPos];
							System.arraycopy(information, infoPos, lpRawData, 0, lpRawData.length);
							
							logger.debug("[DLMS] lpRawData.length ", lpRawData.length);

							//List<HashMap<String, Object>> returnList = new ArrayList<HashMap<String, Object>>();
							Map<String, Object> tempParamMap = new HashMap<String, Object>();
							tempParamMap.put("isBlock", true);
							tempParamMap.put("blockNumber", DataUtil.getIntTo4Byte(blockNumber));
							tempParamMap.put("isLast", lastBlock[0] == 0 ? false : true);
							//tempParamMap.put("dataSize", dataList == null ? 0 : dataList.size());							
							tempParamMap.put("rawData", lpRawData);
							
							Boolean isLast = (lastBlock[0] == 0) ? false : true;

							setResultData(tempParamMap);
							logger.debug("[DLMS] Get-Data-Result-data-info = [{}]", tempParamMap.toString());
							
							byte[] getDataResultType = new byte[1];
							System.arraycopy(information, infoPos, getDataResultType, 0, getDataResultType.length);
							infoPos += getDataResultType.length;
							
							logger.debug("### GET-RESPONSE-WITH-DATABLOCK - RAW Data 저장 ###");
							
							/*
							 * data block 처리
							 */
							if (!isLast) {
								byte[] informationBuffer = (byte[]) session.getAttribute(bufferKey);					
								if(informationBuffer == null) {
									informationBuffer = new byte[]{};
									informationBuffer = DataUtil.append(informationBuffer, lpRawData);									
									session.setAttribute(bufferKey, informationBuffer);						
								} else {
									 informationBuffer = DataUtil.append(informationBuffer, lpRawData);
								}

								logger.debug("[WITH_DATABLOCK] DATABLOCK frme buffering. size = {}byte, data = {}", informationBuffer.length, Hex.decode(informationBuffer));
								
							} else {
								
								logger.debug("has buffer?? = {}",  (session.getAttribute(bufferKey) != null ? "Y" : "N"));
								if(session.getAttribute(bufferKey) != null) { // 버퍼가 있을경우 붙여서 진행
									information = DataUtil.append((byte[])session.getAttribute(bufferKey), lpRawData);
									
									logger.debug("[WITH_DATABLOCK] Merged DATA BLOCK Information frme. size = {}byte, data = {}", information.length, Hex.decode(information));
									session.removeAttribute(bufferKey);
								}
								
								// customer decode
								customDecode(procedure, information);
							}
						}
						
						break;
					case WITH_LIST:
						/*
						 * 추후 필요시 구현
						 */
						break;
					default:
						break;
					}

					//					result = true;
				} else if (HdlcObjectType.getItem(commandType[0]) == HdlcObjectType.SET_RES) {
					/**
					 * Set response Parsing
					 */
					byte[] resonseType = new byte[1];
					System.arraycopy(information, infoPos, resonseType, 0, resonseType.length);
					infoPos += resonseType.length;
					logger.debug("[DLMS] Set-Response = [{}]", SetResponse.getItem(resonseType[0]).name());

					byte[] idProperty;
					byte[] dataAccessResult;
					DataAccessResult aResult;
					byte[] blockNumber;
					switch (SetResponse.getItem(resonseType[0])) {
					case NORMAL:
						idProperty = new byte[1];
						System.arraycopy(information, infoPos, idProperty, 0, idProperty.length);
						infoPos += idProperty.length;
						logger.debug("[DLMS] Invoke-Id-And-Priority = [{}]", Hex.decode(idProperty));

						dataAccessResult = new byte[1];
						System.arraycopy(information, infoPos, dataAccessResult, 0, dataAccessResult.length);
						infoPos += dataAccessResult.length;
						aResult = DataAccessResult.getItem(dataAccessResult[0]);

						// 결과 저장
						setResultData(aResult);
						logger.debug("[DLMS] Get-Data-Access-Result = [{}]", aResult.name());
						
						if (aResult != DataAccessResult.SUCCESS) { // 성공이 아닌것.
							result = false;
						}
						break;
					case DATABLOCK:
						idProperty = new byte[1];
						System.arraycopy(information, infoPos, idProperty, 0, idProperty.length);
						infoPos += idProperty.length;
						logger.debug("[DLMS] Invoke-Id-And-Priority = [{}]", Hex.decode(idProperty));

						blockNumber = new byte[4];
						System.arraycopy(information, infoPos, blockNumber, 0, blockNumber.length);
						infoPos += blockNumber.length;
						logger.debug("[DLMS] Block Number = [{}]", DataUtil.getIntTo4Byte(blockNumber));

						// 결과 저장
						setResultData(DataUtil.getIntTo4Byte(blockNumber));
						break;
					case LAST_DATABLOCK:
						idProperty = new byte[1];
						System.arraycopy(information, infoPos, idProperty, 0, idProperty.length);
						infoPos += idProperty.length;
						logger.debug("[DLMS] Invoke-Id-And-Priority = [{}]", Hex.decode(idProperty));

						dataAccessResult = new byte[1];
						System.arraycopy(information, infoPos, dataAccessResult, 0, dataAccessResult.length);
						infoPos += dataAccessResult.length;
						aResult = DataAccessResult.getItem(dataAccessResult[0]);

						// 결과 저장
						setResultData(aResult);
						logger.debug("[DLMS] Get-Data-Access-Result = [{}]", aResult.name());

						if (aResult != DataAccessResult.SUCCESS) { // 성공이 아닌것.
							result = false;
						}

						blockNumber = new byte[4];
						System.arraycopy(information, infoPos, blockNumber, 0, blockNumber.length);
						infoPos += blockNumber.length;
						logger.debug("[DLMS] Block Number = [{}]", DataUtil.getIntTo4Byte(blockNumber));
						break;
					case LAST_DATABLOCK_WITH_LIST:
						/*
						 * 추후 필요시 구현
						 */
						break;
					case WITH_LIST:
						/*
						 * 추후 필요시 구현
						 */
						break;
					default:
						break;
					}
				} else {

				}

			} // IFrame parsing close.

		} catch (Exception e) {
			logger.error("DLMS Decoding Error - {}", e);
			result = false;
		}

		return result;
	}

	@Override
	public String toByteString() {
		return Hex.decode(gdDLMSFrame);
	}	
	
	
	public String getDateTime(byte[] data) throws Exception {
    	
    	int year = DataFormat.getIntTo2Byte(DataFormat.select(data, 0, 2));
        int month = DataFormat.getIntToByte(data[2]);
        int day = DataFormat.getIntToByte(data[3]);
        int week = DataFormat.getIntToByte(data[4]);
        int hour = DataFormat.getIntToByte(data[5]);
        int min = DataFormat.getIntToByte(data[6]);
        int sec = DataFormat.getIntToByte(data[7]);
        
        DecimalFormat ydf = new DecimalFormat("0000");
        DecimalFormat df = new DecimalFormat("00");
        
        if(year >= 65535) {        	
        	year = 0;
        }
        if(month >= 255)
        	month = 0;
        if(day >= 255) 
        	day = 0;
        if(hour >= 255) 
        	hour = 0;
        if(min >= 255) 
        	min = 0;
        if(sec >= 255) 
        	sec = 0;
        	
        String str = ydf.format(year) + df.format(month) + df.format(day)
                + df.format(hour) + df.format(min) + df.format(sec);
        
        return str;
    }
	
	
	public String getStartTime(byte[] data) throws Exception {
    	
        int hour = DataFormat.getIntToByte(data[0]);
        int min = DataFormat.getIntToByte(data[1]);
        int sec = DataFormat.getIntToByte(data[2]);
        
        DecimalFormat df = new DecimalFormat("00");
        
        if(hour >= 255) 
        	hour = 0;
        if(min >= 255) 
        	min = 0;
        if(sec >= 255) 
        	sec = 0;
        	
        String str = df.format(hour) + ":" + df.format(min) + ":" + df.format(sec);
        
        return str;
    }
	
	
	public String getObisCode(byte[] data) throws Exception {
        	
        String str = "";
        
        if(data.length == 6) {
        	str += DataFormat.getIntToByte(data[0]); 
        	str += "." + DataFormat.getIntToByte(data[1]);
        	str += "." + DataFormat.getIntToByte(data[2]);
        	str += "." + DataFormat.getIntToByte(data[3]);
        	str += "." + DataFormat.getIntToByte(data[4]);
        	str += "." + DataFormat.getIntToByte(data[5]);
        }
        
        return str;
    }
	
	@Override
	public Object customDecode(Procedure procedure, byte[] information) throws Exception {
		
		if (procedure == procedure.GET_DAY_PROFILE) {
			int infoPos = 0;
			logger.debug("[WITH_DATABLOCK] data = {}", Hex.decode(information));
			byte[] datatype = new byte[1];
			System.arraycopy(information, infoPos, datatype, 0, datatype.length);
			infoPos += datatype.length;
			
			byte[] arrayLength = new byte[1];
			System.arraycopy(information, infoPos, arrayLength, 0, arrayLength.length);
			infoPos += arrayLength.length;
			
			List r = new ArrayList<HashMap<String, String>>();
			
			for(int i = 0; i < DataUtil.getIntToByte(arrayLength[0]); i++) {
				
				byte[] structure = new byte[1];
				System.arraycopy(information, infoPos, structure, 0, structure.length);
				infoPos += structure.length;
				
				byte[] structureLength = new byte[1];
				System.arraycopy(information, infoPos, structureLength, 0, structureLength.length);
				infoPos += structureLength.length;
				
				datatype = new byte[1];
				System.arraycopy(information, infoPos, datatype, 0, datatype.length);
				infoPos += datatype.length;
				
				byte[] dayid = new byte[1];
				System.arraycopy(information, infoPos, dayid, 0, dayid.length);
				infoPos += dayid.length;
														
				datatype = new byte[1];
				System.arraycopy(information, infoPos, datatype, 0, datatype.length);
				infoPos += datatype.length;
			
				byte[] arrayLength2 = new byte[1];
				System.arraycopy(information, infoPos, arrayLength2, 0, arrayLength2.length);
				infoPos += arrayLength2.length;
				
				for(int d = 0; d < DataUtil.getIntToByte(arrayLength2[0]); d++) {
					
					HashMap<String, String> map = new HashMap<String, String>();
					
					structure = new byte[1];
					System.arraycopy(information, infoPos, structure, 0, structure.length);
					infoPos += structure.length;
					
					structureLength = new byte[1];
					System.arraycopy(information, infoPos, structureLength, 0, structureLength.length);
					infoPos += structureLength.length;
					
					datatype = new byte[1];
					System.arraycopy(information, infoPos, datatype, 0, datatype.length);
					infoPos += datatype.length;
					
					byte[] dataLength = new byte[1];
					System.arraycopy(information, infoPos, dataLength, 0, dataLength.length);
					infoPos += dataLength.length;
					
					// start time
					byte[] dataStartTime = new byte[DataUtil.getIntToByte(dataLength[0])];
					logger.debug("[DLMS] startTime = [{}]", Hex.decode(dataStartTime));
					System.arraycopy(information, infoPos, dataStartTime, 0, dataStartTime.length);
					infoPos += dataStartTime.length;									
					map.put("StartTime",  getStartTime(dataStartTime));
					logger.debug("[DLMS] startTime = [{}]", getStartTime(dataStartTime));
					
					datatype = new byte[1];
					System.arraycopy(information, infoPos, datatype, 0, datatype.length);
					infoPos += datatype.length;
					
					dataLength = new byte[1];
					System.arraycopy(information, infoPos, dataLength, 0, dataLength.length);
					infoPos += dataLength.length;
					
					// start time
					byte[] scriptLogicalName = new byte[DataUtil.getIntToByte(dataLength[0])];
					System.arraycopy(information, infoPos, scriptLogicalName, 0, scriptLogicalName.length);
					infoPos += scriptLogicalName.length;
					map.put("ScriptLogicalName", getObisCode(scriptLogicalName));
					logger.debug("[DLMS] scriptLogicalName = [{}]", getObisCode(scriptLogicalName));
					
					datatype = new byte[1];
					System.arraycopy(information, infoPos, datatype, 0, datatype.length);
					infoPos += datatype.length;
														
					// start time
					byte[] timeBucketNo = new byte[2];
					System.arraycopy(information, infoPos, timeBucketNo, 0, timeBucketNo.length);
					map.put("timeBucketNo", String.valueOf(DataUtil.getIntTo2Byte(timeBucketNo)));
					infoPos += timeBucketNo.length;
					
					logger.debug("[DLMS] timeBucketNo = [{}]", DataUtil.getIntTo2Byte(timeBucketNo));
					
					map.put("dayid",  String.valueOf(DataUtil.getIntToByte(dayid[0])));
					
					r.add(map);
					
				} // end for
				setResultData(r);
			} //end for
		} // end if
		
		return null;
	}
}
