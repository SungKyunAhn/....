/** 
 * @(#)MRPError.java       1.0 03/09/01 *
 * Copyright (c) 2003-2004 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelecom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */

package com.aimir.fep.protocol.mrp.exception;





/** 
 * Exception Class. 
 * 
 * @version     1.0 1 Sep 2003 
 * @author		Park YeonKyoung yeonkyoung@hanmail.net    
 */

public class MRPError {

	static public final int ERR_NOCARRIER  = 6;
	static public final int ERR_NODIALTONE = 7;
	static public final int ERR_MODEMBUSY  = 8;
	static public final int ERR_RING       = 9;
	static public final int ERR_MODEMERROR = 10;
	static public final int ERR_NOANSWER   = 4;
	
	static public final int ERR_CONNTOSCHED_CLASS     = 11;

    static public final int ERR_CONNECTTOSERVER_CLASS = 13;
    static public final int ERR_INITLOCALMODEM_CLASS  = 14;
    static public final int ERR_TODIALMODEM_CLASS     = 15;
    static public final int ERR_USERPASS_CLASS        = 16;
    static public final int ERR_REGEDIT_CLASS         = 17;
    static public final int ERR_READREG_CLASS         = 18;
    static public final int ERR_CONTROLREG_CLASS      = 19;
    static public final int ERR_LOOPBACK_CLASS        = 20;
    static public final int ERR_METERMODE_CLASS       = 21;
    static public final int ERR_INITMODEM_CLASS       = 22;
    static public final int ERR_RESETMODEM_CLASS      = 23;
    static public final int ERR_CONNECTTOMETER_CLASS  = 24;
    static public final int ERR_NOWREAD_CLASS         = 25;
    static public final int ERR_REGULREAD_CLASS       = 26;
    static public final int ERR_PROCESSEXIT_CLASS     = 27;    
    static public final int ERR_LPREAD_CLASS          = 28;    
	static public final int ERR_CONNTOBIZSVR_CLASS    = 33;	
    static public final int ERR_PROCESS_CLASS         = 32;

	static public final int ERR_METER_INIT          = 41;
	static public final int ERR_READ_METER_CLASS    = 42;
	static public final int ERR_TIMEOUT             = 43;
	static public final int ERR_UNSUPPORTED_METER   = 44;
	static public final int ERR_CRC_CHECK_ERROR     = 45;
	static public final int ERR_HEADER_FORMAT_ERROR = 46;
    
    static public final int ERR_INVALID_PARAM = 47;
    
    
    public MRPError() { } 

    public static String getMessage(int type) {
    	
        switch(type) {
            case ERR_INITLOCALMODEM_CLASS:
                return "Local modem initialization failure";
            case ERR_TODIALMODEM_CLASS:
                return "Dialing time out";
            case ERR_USERPASS_CLASS:
                return "Authentication failure";
            case ERR_REGEDIT_CLASS:
                return "Editing the registry failed";
            case ERR_READREG_CLASS:
                return "Failed to read registry";
            case ERR_CONTROLREG_CLASS:
                return "Manipulating the Registry failed";
            case ERR_LOOPBACK_CLASS:
                return "Loopback failure";
            case ERR_METERMODE_CLASS:
                return "Metering mode change failure";
            case ERR_INITMODEM_CLASS:
                return "Modem initialization failure";
            case ERR_RESETMODEM_CLASS:
                return "Modem reset failed";
            case ERR_CONNECTTOMETER_CLASS:
                return "Meter communication failure";    
            case ERR_PROCESSEXIT_CLASS:
                return "process exit error"; 
            case ERR_CONNTOBIZSVR_CLASS :
            	return "Not Connect to business server.";
			case ERR_METER_INIT:
				return "Meter init error.";
			case ERR_READ_METER_CLASS:
				return "meter class read error.";
			case ERR_TIMEOUT:
				return "time out error.";
			case ERR_UNSUPPORTED_METER:
				return "unsupported meter.";
			case ERR_CRC_CHECK_ERROR:
				return "meter class data crc check error.";
			case ERR_HEADER_FORMAT_ERROR:
				return "meter class header format error.";				
			case ERR_LPREAD_CLASS: 
				return "lp read error";   
			case ERR_PROCESS_CLASS:
				return "process error";
			case ERR_NOCARRIER:
				return "Remote modem no carrier";
			case ERR_NODIALTONE:
				return "Remote modem no dialtone";
			case ERR_MODEMBUSY:
				return "Modem is now busy"; 
			case ERR_RING:
				return "Ring";
			case ERR_MODEMERROR:
				return "Modem response error";
			case ERR_NOANSWER:
				return "Modem no answer";
        }
        return "not";
    }
}
