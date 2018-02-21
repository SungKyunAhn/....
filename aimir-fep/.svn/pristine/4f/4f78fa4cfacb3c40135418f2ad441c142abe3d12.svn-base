/*
 * @(#)Mk10_PB.java       1.0 2011/08/12 *
 *
 * Previous Billing.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.meter.parser.Mk10Table;

import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.util.DataUtil;

public class Mk10_PB  implements java.io.Serializable{

	private static final long serialVersionUID = 4599490733019398268L;

	private byte[] rawData = null;
	
	private byte[] RESET_COUNT = new byte[4];
	private byte[] ACTIVE_RATE0_COUNT = new byte[1];
	private byte[] ACTIVE_RATE1_COUNT = new byte[1];
	private byte[] ACTIVE_RATE2_COUNT = new byte[1];
	private byte[] ACTIVE_RATE3_COUNT = new byte[1];

	private byte[] TOU = new byte[0];
	private TOU_BLOCK[] touBlock = null;
	
	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
	public Mk10_PB(byte[] rawData) {
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
		
		TOU = new byte[rawData.length - pos];
		System.arraycopy(rawData, pos, TOU, 0, TOU.length);
		pos += TOU.length;

		int rate0ChannelCount = DataUtil.getIntToBytes(ACTIVE_RATE0_COUNT);
		int rate1ChannelCount = DataUtil.getIntToBytes(ACTIVE_RATE1_COUNT);
		int rate2ChannelCount = DataUtil.getIntToBytes(ACTIVE_RATE2_COUNT);
		int rate3ChannelCount = DataUtil.getIntToBytes(ACTIVE_RATE3_COUNT);
		int rateCount = rate0ChannelCount+rate1ChannelCount+rate2ChannelCount+rate3ChannelCount;

		BillingData bill = new BillingData(BillingData.PB, TOU, "", rateCount, 0);
		this.touBlock = bill.getTOU_BLOCK();
		
	}
	
	public TOU_BLOCK[] getTOU_BLOCK(){
		return this.touBlock;
	}
}
