/*
 * @(#)Mk10_CB.java       1.0 2011/08/12 *
 *
 * Current Billing.
 * Copyright (c) 2011-2012 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.meter.parser.Mk10Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.Mk6NTable.DateTimeFormat;
import com.aimir.fep.util.DataUtil;

public class Mk10_CB  implements java.io.Serializable{

	private static final long serialVersionUID = 3111635574698037287L;

	private byte[] rawData = null;
	
	private byte[] RESET_COUNT = new byte[4];
	private byte[] ACTIVE_RATE0_COUNT = new byte[1];
	private byte[] ACTIVE_RATE1_COUNT = new byte[1];
	private byte[] ACTIVE_RATE2_COUNT = new byte[1];
	private byte[] ACTIVE_RATE3_COUNT = new byte[1];
	private byte[] CURRENT_TIME = new byte[6];

	private byte[] TOU = new byte[0];
	private TOU_BLOCK[] touBlock = null;
    private static Log log = LogFactory.getLog(Mk10_CB.class);
	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public Mk10_CB(byte[] rawData) {
		this.rawData = rawData;
		parse();
	}
	
	
	public void parse() {
		int pos = 0;
		
		System.arraycopy(rawData, pos, RESET_COUNT, 0, RESET_COUNT.length);
		pos += RESET_COUNT.length;		
		System.arraycopy(rawData, pos, ACTIVE_RATE0_COUNT, 0, ACTIVE_RATE0_COUNT.length);
		pos += ACTIVE_RATE0_COUNT.length;
		System.arraycopy(rawData, pos, ACTIVE_RATE1_COUNT, 0, ACTIVE_RATE1_COUNT.length);
		pos += ACTIVE_RATE1_COUNT.length;
		System.arraycopy(rawData, pos, ACTIVE_RATE2_COUNT, 0, ACTIVE_RATE2_COUNT.length);
		pos += ACTIVE_RATE2_COUNT.length;
		System.arraycopy(rawData, pos, ACTIVE_RATE3_COUNT, 0, ACTIVE_RATE3_COUNT.length);
		pos += ACTIVE_RATE3_COUNT.length;
		System.arraycopy(rawData, pos, CURRENT_TIME, 0, CURRENT_TIME.length);
		pos += CURRENT_TIME.length;

		TOU = new byte[rawData.length - pos];
		System.arraycopy(rawData, pos, TOU, 0, TOU.length);
		pos += TOU.length;
		int rate0ChannelCount = DataUtil.getIntToBytes(ACTIVE_RATE0_COUNT);
		int rate1ChannelCount = DataUtil.getIntToBytes(ACTIVE_RATE1_COUNT);
		int rate2ChannelCount = DataUtil.getIntToBytes(ACTIVE_RATE2_COUNT);
		int rate3ChannelCount = DataUtil.getIntToBytes(ACTIVE_RATE3_COUNT);
		
		String currentTime = "";
		try {
			currentTime =(new DateTimeFormat(CURRENT_TIME)).getDateTime();
		} catch (Exception e) {
			log.warn(e,e);
		}
		
		log.debug("ACTIVE_RATE0_COUNT :" + rate0ChannelCount);
		log.debug("ACTIVE_RATE1_COUNT :" + rate1ChannelCount);
		log.debug("ACTIVE_RATE2_COUNT :" + rate2ChannelCount);
		log.debug("ACTIVE_RATE3_COUNT :" + rate3ChannelCount);
		log.debug("current meter time :" + currentTime);
		int rateCount = rate0ChannelCount+rate1ChannelCount+rate2ChannelCount+rate3ChannelCount;
		
		
		BillingData bill = new BillingData(BillingData.CB, TOU, currentTime, rateCount, 0);
		this.touBlock = bill.getTOU_BLOCK();
	}
	
	public TOU_BLOCK[] getTOU_BLOCK(){
		return this.touBlock;
	}
}