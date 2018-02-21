/** 
 * @(#)MRPException.java       1.0 03/09/01 *
 * Copyright (c) 2003-2004 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelecom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */

package com.aimir.fep.protocol.mrp.exception;

import com.aimir.fep.protocol.fmp.exception.FMPException;


/** 
 * Exception Class. 
 * 
 * @version     1.0 1 Sep 2003 
 * @author		Park YeonKyoung yeonkyoung@hanmail.net   
 */

public class MRPException extends FMPException {

    private static final long serialVersionUID = 1L;
    private int type = -1;

	/**
	 * constructor.<p>
	 */
	public MRPException() {
		super();
	}
	
	
	/**
	 * constructor.<p>
	 */
	public MRPException(int type) {
		super();
		this.type = type;
	}
	
	
	/**
	 * constructor.<p>
	 */
	public MRPException(int type,String msg) {
		super(msg);
		this.type = type;
	}
	
	public MRPException(int type, Throwable t) {
	    super(t);
	}
	/**
	 * return type of PowerTCP error.<p>
	 * @return int The type of error.<p>
	 */
	public int getType() { 
		return this.type; 
	}
	
	
	/**
	 * set type of PowerTCP error
	 * @param type The type of PowerTCP error
	 */
	public void setType(int type) { 
		this.type = type; 
	} 


	/**
	 * return error message according to type.<p>
	 * @return String The message of error.<p>
	 */
	public String getTypeMessage() {
		
		switch(this.type) {			
        case MRPError.ERR_INITLOCALMODEM_CLASS:
            return "Local modem initialization failure";
        case MRPError.ERR_TODIALMODEM_CLASS:
            return "Dialing time out";
        case MRPError.ERR_USERPASS_CLASS:
            return "Authentication failure";
        case MRPError.ERR_REGEDIT_CLASS:
            return "Editing the registry failed";
        case MRPError.ERR_READREG_CLASS:
            return "Failed to read registry";
        case MRPError.ERR_CONTROLREG_CLASS:
            return "Manipulating the Registry failed";
        case MRPError.ERR_LOOPBACK_CLASS:
            return "Loopback failure";
        case MRPError.ERR_METERMODE_CLASS:
            return "Metering mode change failure";
        case MRPError.ERR_INITMODEM_CLASS:
            return "Modem initialization failure";
        case MRPError.ERR_RESETMODEM_CLASS:
            return "Modem reset failed";
        case MRPError.ERR_CONNECTTOMETER_CLASS:
            return "Meter communication failure";    
        case MRPError.ERR_PROCESSEXIT_CLASS:
            return "process exit error"; 
        case MRPError.ERR_CONNTOBIZSVR_CLASS :
            return "Not Connect to business server.";
        case MRPError.ERR_METER_INIT:
            return "Meter init error.";
        case MRPError.ERR_READ_METER_CLASS:
            return "meter class read error.";
        case MRPError.ERR_TIMEOUT:
            return "time out error.";
        case MRPError.ERR_UNSUPPORTED_METER:
            return "unsupported meter.";
        case MRPError.ERR_CRC_CHECK_ERROR:
            return "meter class data crc check error.";
        case MRPError.ERR_HEADER_FORMAT_ERROR:
            return "meter class header format error.";              
        case MRPError.ERR_LPREAD_CLASS: 
            return "lp read error";   
        case MRPError.ERR_PROCESS_CLASS:
            return "process error";
        case MRPError.ERR_NOCARRIER:
            return "Remote modem no carrier";
        case MRPError.ERR_NODIALTONE:
            return "Modem no dialtone";
        case MRPError.ERR_MODEMBUSY:
            return "Modem is now busy"; 
        case MRPError.ERR_RING:
            return "Ring";
        case MRPError.ERR_MODEMERROR:
            return "Modem response error";
        case MRPError.ERR_NOANSWER:
            return "Modem no answer";
        case MRPError.ERR_INVALID_PARAM:
            return "Invalid command parameter";
        }  
        return "Unknown Error";
	}
	
}

