/**
 * (@)# CommandNIProxyMBean.java
 *
 * 2016. 5. 30.
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
package com.aimir.fep.protocol.nip;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.model.device.Modem;

/**
 * @author simhanger
 *
 */
public interface CommandNIProxyMBean {

	/**
	 * Execute Command
	 */
	public Object execute(Modem modem, NIAttributeId command, HashMap<String, Object> param) throws Exception;
}
