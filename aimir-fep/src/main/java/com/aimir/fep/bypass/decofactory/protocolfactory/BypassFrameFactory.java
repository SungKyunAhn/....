/**
 * (@)# BypassFrameFactory.java
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
package com.aimir.fep.bypass.decofactory.protocolfactory;

import java.util.HashMap;

import org.apache.mina.core.session.IoSession;

import com.aimir.fep.protocol.nip.client.multisession.MultiSession;

/**
 * @author simhanger
 *
 */
public abstract class BypassFrameFactory {

	/**
	 * 각 Factory별로 프로세스 수행
	 * 
	 * @param session
	 *            세션
	 * @param rawFrame
	 *            bypass로 받은 원본 frame
	 * @return
	 * @throws Exception
	 */
	public enum Procedure {
		/*
		 *  HDLC Procedure
		 */
		HDLC_SNRM, HDLC_AARQ, HDLC_ASSOCIATION_LN

		/*
		 *  Meter F/W OTA Procedure
		 */
		, GET_IMAGE_TRANSFER_ENABLE, SET_IMAGE_TRANSFER_ENABLE
		, GET_IMAGE_BLOCK_SIZE, ACTION_IMAGE_TRANSFER_INIT
		, GET_IMAGE_TRANSFER_STATUS, ACTION_IMAGE_BLOCK_TRANSFER
		, GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER, ACTION_IMAGE_VERIFY
		, GET_IMAGE_TO_ACTIVATE_INFO, ACTION_IMAGE_ACTIVATE
		, HDLC_DISC

		/*
		 * Get Meter Firmware Information
		 */
		, GET_FIRMWARE_VERSION
		
		/*
		 * Get SORIA Meter Key
		 */
		, GET_SORIA_METER_KEY_A, GET_SORIA_METER_KEY_B, GET_SORIA_METER_KEY_C 

		/*
		 * Action Meter Alarm Reset 
		 */
		, ACTION_METER_ALARM_RESET

		/*
		 * Set Billing Cycle
		 */
		, SET_BILLING_CYCLE, GET_BILLING_CYCLE
		
		/*
		 * Demand Period
		 */
		, SET_DEMAND_PLUS_A_PERIOD, SET_DEMAND_PLUS_A_NUMBER
		, SET_DEMAND_MINUS_A_PERIOD, SET_DEMAND_MINUS_A_NUMBER
		, SET_DEMAND_PLUS_R_PERIOD, SET_DEMAND_PLUS_R_NUMBER
		, SET_DEMAND_MINUS_R_PERIOD, SET_DEMAND_MINUS_R_NUMBER
		, SET_DEMAND_R_QI_PERIOD, SET_DEMAND_R_QI_NUMBER
		, SET_DEMAND_R_QIV_PERIOD, SET_DEMAND_R_QIV_NUMBER
		, SET_DEMAND_PLUS_PERIOD, SET_DEMAND_PLUS_NUMBER
		, SET_DEMAND_MINUS_PERIOD, SET_DEMAND_MINUS_NUMBER
		
		, GET_DEMAND_PLUS_A_PERIOD, GET_DEMAND_PLUS_A_NUMBER
		, GET_DEMAND_MINUS_A_PERIOD, GET_DEMAND_MINUS_A_NUMBER
		, GET_DEMAND_PLUS_R_PERIOD, GET_DEMAND_PLUS_R_NUMBER
		, GET_DEMAND_MINUS_R_PERIOD, GET_DEMAND_MINUS_R_NUMBER
		, GET_DEMAND_R_QI_PERIOD, GET_DEMAND_R_QI_NUMBER
		, GET_DEMAND_R_QIV_PERIOD, GET_DEMAND_R_QIV_NUMBER
		, GET_DEMAND_PLUS_PERIOD, GET_DEMAND_PLUS_NUMBER
		, GET_DEMAND_MINUS_PERIOD, GET_DEMAND_MINUS_NUMBER

		/*
		 * TOU Set
		 * - Calendar Name Passive
		 * - Season Profile
		 * - Week Profile
		 * - Day Profile
		 * - TOU Starting Date
		 */
		, SET_CALENDAR_NAME_PASSIVE, SET_SEASON_PROFILE, SET_WEEK_PROFILE, SET_DAY_PROFILE, SET_STARTING_DATE
		
		/*
		 * TOU get
		 * - Calendar Name Passive
		 * - Season Profile
		 * - Week Profile
		 * - Day Profile
		 * - TOU Starting Date
		 */
		, GET_CALENDAR_NAME_PASSIVE, GET_SEASON_PROFILE, GET_WEEK_PROFILE, GET_DAY_PROFILE, GET_STARTING_DATE
		
		/*
		 * Current Load Limit
		 *  - Current over limit duration judge time
		 *  - Current over limit threshold
		 */
		, GET_CURRENT_LOAD_LIMIT_DURATION_JUDGE_TIME, GET_CURRENT_LOAD_LIMIT_THRESHOLD, SET_CURRENT_LOAD_LIMIT_DURATION_JUDGE_TIME, SET_CURRENT_LOAD_LIMIT_THRESHOLD
		
		/*
		 * Set Meter Time
		 * 
		 */
		, SET_METER_TIME
		
		/*
		 * Get Meter Time
		 * 
		 */
		, GET_METER_TIME
		
		/*
		 * Get Register Value
		 * 
		 */
		, GET_REGISTER_VALUE
		/*
		 * Set Register Value
		 * 
		 */
		, SET_REGISTER_VALUE
		/*
		 * Get Register Unit
		 * 
		 */
		, GET_REGISTER_UNIT
		/*
		 * Set Register Unit
		 * 
		 */
		, SET_REGISTER_UNIT
		/*
		 * Get PROFILE_BUFFER
		 */
		, GET_PROFILE_OBJECT, GET_PROFILE_BUFFER
		/*
		 * Get ProfilePeriod 
		 */
		, GET_PROFILE_PERIOD
		/*
		 * Set ProfilePeriod
		 */
		,SET_PROFILE_PERIOD
		/*
		 * Get ThresholdNormal
		 */
		,GET_THRESHOLD_NORMAL
		/*
		 * Set TrhesholdNormal
		 */
		,SET_THRESHOLD_NORMAL
		/*
		 * Get MinOverThresholdDuration
		 */
		,GET_MINOVER_THRESHOLD_DURATION
		/*
		 * Set MinOverThresholdDuration
		 */
		,SET_MINOVER_THRESHOLD_DURATION
		/*
		 * Get DISCONNECT CONTROL
		 */
		,GET_DISCONNECT_CONTROL
		/*
		 * Set DISCONNECT CONTROL
		 */
		,SET_DISCONNECT_CONTROL
		/*
		 * Action DISCONNECT CONTROL
		 */
		,ACTION_DISCONNECT_CONTROL
		/*
		 * Action M-Bus client  set_encryption_key(7) transfer_key(8)
		 */
		,ACTION_SET_ENCRYPTION_KEY
		,ACTION_TRANSFER_KEY
		/*
		 * Get Value
		 */
		,GET_VALUE
		/*
		 * Set Value
		 */
		,SET_VALUE
	}
	
	//public abstract BypassFrameResult receiveBypass(IoSession session, byte[] rawFrame) throws Exception;
	public abstract BypassFrameResult receiveBypass(MultiSession session, byte[] rawFrame) throws Exception;

	//public abstract boolean start(IoSession session, Object type) throws Exception;
	public abstract boolean start(MultiSession session, Object type) throws Exception;

	//public abstract void stop(IoSession session);
	public abstract void stop(MultiSession session);

    public abstract BypassFrameResult receiveBypass(IoSession session, byte[] rawFrame) throws Exception;

	public abstract boolean start(IoSession session, Object type) throws Exception;

	public abstract void setParam(HashMap<String, Object> params);
	
	public abstract void stop(IoSession session);
}
