/** 
 * @(#)MT130.java       1.0 06/12/14 *
 * 
 * Relay Status table Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.SM110Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class MT130 {
	
    private static Log log = LogFactory.getLog(MT130.class);
	private byte[] data;
	private int relay_status = 0;
	private int relay_activate_status = 0;

	/**
	 * Constructor .<p>
	 */
	public MT130(byte[] data) {
		this.data = data;
        parse();
	}
	
	public int getRelayStatus() {
        return this.relay_status;
	}
	
	public int getRelayActivateStatus() {
        return this.relay_activate_status;
	}

    public void parse() {

        try {
            if(data.length >= 2){
                this.relay_status = DataUtil.getIntToByte(data[0]);
                this.relay_activate_status = DataUtil.getIntToByte(data[1]);
            }else{
                log.warn("Not Valid Data!");
            }
        } catch(Exception e){
            log.warn("MT130 parse data error!: "+e.getMessage());
        }
    }
}
