/**
 * (@)# EMnVTransactionDataLogging.java
 *
 * 2015. 9. 24.
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
package com.aimir.fep.protocol.emnv.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.util.Hex;

/**
 * @author nuri
 *
 */
public class EMnVTransactionDataLogging {
	private static Logger log = LoggerFactory.getLogger(EMnVTransactionDataLogging.class);
	
	public EMnVTransactionDataLogging() {
		log.debug("");
		log.debug("##### EMnVTransactionDataLogging Start. #####");
	}
	
	public static void writeRawMeteringData(String sourceAddress, byte[] rawByte) {
		log.info("[{}] => {}", sourceAddress, Hex.decode(rawByte));	
	}
}
