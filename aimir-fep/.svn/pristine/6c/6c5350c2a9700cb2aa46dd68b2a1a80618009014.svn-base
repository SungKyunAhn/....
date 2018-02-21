/**
 * (@)# EMnVFramePayLoad.java
 *
 * 2015. 4. 22.
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
package com.aimir.fep.protocol.emnv.frame.payload;

import java.io.Serializable;

/**
 * @author simhanger
 *
 */
//public interface EMnVGeneralFramePayLoad extends Serializable {

public abstract class EMnVGeneralFramePayLoad implements Serializable {
	private static final long serialVersionUID = 1L;

	private String modemImei;

	public abstract void decode() throws Exception;

	public abstract byte[] encode() throws Exception;

	public abstract boolean isValidation(Object obj) throws Exception;

	public String getModemImei() {
		return modemImei;
	}

	public void setModemImei(String modemImei) {
		this.modemImei = modemImei;
	}

}
