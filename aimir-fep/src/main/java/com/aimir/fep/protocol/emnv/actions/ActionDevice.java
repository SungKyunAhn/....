/**
 * (@)# ActionDevice.java
 *
 * 2015. 6. 23.
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

import java.util.HashMap;

import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.fep.protocol.emnv.actions.EMnVActions.EMnVActionType;

/**
 * @author simhanger
 *
 */
public class ActionDevice {
	private EMnVActionType type;
	private Long transactionId;
	private String command;
	private String modemId;
	private TR_STATE trState;

	private String fwPath;
	private String fwVersion;
	private String hwVersion;
	private Long fwSize;
	private byte[] fwCrc;

	private int packetSize;
	private int offset;
	private byte[] fwImgArray;
	private int sendCount;
	private int remainPackateLength;
	private HashMap<String, Object> params;

	public ActionDevice(EMnVActionType type) {
		this.type = type;
	}

	public EMnVActionType getType() {
		return type;
	}

	public void setType(EMnVActionType type) {
		this.type = type;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getModemId() {
		return modemId;
	}

	public void setModemId(String modemId) {
		this.modemId = modemId;
	}

	public TR_STATE getTrState() {
		return trState;
	}

	public void setTrState(TR_STATE trState) {
		this.trState = trState;
	}

	public String getFwPath() {
		return fwPath;
	}

	public void setFwPath(String fwPath) {
		this.fwPath = fwPath;
	}

	public String getFwVersion() {
		return fwVersion;
	}

	public void setFwVersion(String fwVersion) {
		this.fwVersion = fwVersion;
	}

	public String getHwVersion() {
		return hwVersion;
	}

	public void setHwVersion(String hwVersion) {
		this.hwVersion = hwVersion;
	}

	public Long getFwSize() {
		return fwSize;
	}

	public void setFwSize(Long fwSize) {
		this.fwSize = fwSize;
	}

	public byte[] getFwCrc() {
		return fwCrc;
	}

	public void setFwCrc(byte[] fwCrc) {
		this.fwCrc = fwCrc;
	}

	public int getPacketSize() {
		return packetSize;
	}

	public void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public byte[] getFwImgArray() {
		return fwImgArray;
	}

	public void setFwImgArray(byte[] fwImgArray) {
		this.fwImgArray = fwImgArray;
	}

	public int getSendCount() {
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

	public int getRemainPackateLength() {
		return remainPackateLength;
	}

	public void setRemainPackateLength(int remainPackateLength) {
		this.remainPackateLength = remainPackateLength;
	}

	public HashMap<String, Object> getParams() {
		return params;
	}

	public void setParams(HashMap<String, Object> params) {
		this.params = params;
	}

}
