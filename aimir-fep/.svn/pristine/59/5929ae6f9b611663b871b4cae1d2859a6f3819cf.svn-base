/**
 * (@)# EMnVAdapterMBen.java
 *
 * 2015. 4. 16.
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
package com.aimir.fep.protocol.emnv.server;

import com.aimir.fep.protocol.fmp.frame.service.CommandData;

/**
 * @author simhanger
 */
public interface EMnVAdapterMBean {
	public void start() throws Exception;

	public void stop();

	public String getName();

	public String getState();

	public int getPort();

	public CommandData cmdExecute(String targetId, CommandData cmdData) throws Exception;
}
