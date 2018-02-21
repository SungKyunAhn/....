/** 
 * @(#)ST130.java       1.0 06/12/14 *
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

import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 04 00 04 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 
 * 00 00
 * This table contains the necessary data to describe the current load control operation. 
 * The field, ECP_OR_PPM_ACCUM is a shared between PPM and ECP Override, 
 * therefore a meter should be configured for one of these modes. 
 * When a meter is configured for prepayment operation it is recommended that ECP operation is set to ECP Ignore or ECP Normal, 
 * neither of which uses the accumulator. 
 * If meter is to be used for ECP Override All or ECP Override Delta operation, 
 * then it is recommended that prepayment operation never be activated for this meter. 
 * Alternating between ECP Override operation and prepayment mode will cause the shared accumulator to become unreliable.
 * 
 * Load Control Status Table
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class MT115 implements java.io.Serializable {

	private static final long serialVersionUID = 5490087588582714186L;
	private static Log log = LogFactory.getLog(MT115.class);
	private byte[] data;
	private int relay_status = 0;
	private int relay_activate_status = 0;

    private byte[] LC_STATUS_FLAGS = new byte[2];    
    private byte[] LC_STATUS_HISTORY = new byte[2];
    private byte[] RCDC_STATE = new byte[1];
    private byte[] LC_STATE = new byte[1];
    private byte[] LC_RECONNECT_ATTEMPT_COUNT = new byte[1];
    
    public enum RCDCSTATE {
   	
    	ACTUAL_SWITCH_STATE((byte)0x01), //0 = Open(off), 1 = Closed (on)
    	DESIRED_SWITCH_STATE((byte)0x02),//0 = Open(off), 1 = Closed (on)
    	OPEN_HOLD_FOR_COMMAND((byte)0x04),
    	WAITING_TO_ARM((byte)0x08),
    	ARMED_FOR_CLOSURE((byte)0x10), //1 = The meter received the arm for manual closure been has not been successful in sending request to arm to switch control board. It will continue to retry automatically. 0 = Indicates that there is no pending need to request the switch control board to arm for manual closure.
    	OUTAGE_OPEN_IN_EFFECT((byte)0x20),
    	LOCKOUT_IN_EFFECT((byte)0x40),
    	RESERVED((byte)0x80);
    	
    	private byte code;
    	
    	RCDCSTATE(byte code){
    		this.code = code;
    	}
    	
        public byte getCode() {
            return this.code;
        }
    }

	public MT115() {}
	
	/**
	 * Constructor .<p>
	 */
	public MT115(byte[] data) 
    {
		this.data = data;
        parse();
	}
	

    public void parse() 
    {
    	int pos = 0;
		System.arraycopy(data, pos, LC_STATUS_FLAGS, 0, LC_STATUS_FLAGS.length);
		pos += LC_STATUS_FLAGS.length;			
		System.arraycopy(data, pos, LC_STATUS_HISTORY, 0, LC_STATUS_HISTORY.length);
		pos += LC_STATUS_HISTORY.length;
		System.arraycopy(data, pos, RCDC_STATE, 0, RCDC_STATE.length);
		pos += RCDC_STATE.length;
		System.arraycopy(data, pos, LC_STATE, 0, LC_STATE.length);
		pos += LC_STATE.length;			
		System.arraycopy(data, pos, LC_RECONNECT_ATTEMPT_COUNT, 0, LC_RECONNECT_ATTEMPT_COUNT.length);
		pos += LC_RECONNECT_ATTEMPT_COUNT.length;

		if((RCDC_STATE[0] & RCDCSTATE.ACTUAL_SWITCH_STATE.getCode()) > 0){
			this.relay_status = 1;
		}else{
			this.relay_status = 0;
		}

		if((RCDC_STATE[0] & RCDCSTATE.ARMED_FOR_CLOSURE.getCode()) > 0){
			this.relay_activate_status = 1;
		}else{
			this.relay_activate_status = 0;
		}
    }
    
    public String getRelayStatusString() 
    {
        if(this.relay_status == 0){
            return "Off";
        }else {
            return "On";
        }
    }
    
    public String getRelayActivateStatusString() 
    {
        if(this.relay_activate_status == 0){
            return "Off";
        }else {
            return "On";
        }
    }
    
	public int getRelayStatus() 
    {
        return this.relay_status;
	}
	
	public int getRelayActivateStatus() 
    {
        return this.relay_activate_status;
	}

    public LinkedHashMap getData()
    {
        if(data == null || data.length < 20){
            return null;
        }else{
            LinkedHashMap res = new LinkedHashMap(2);            
            res.put("relay status"          , ""+this.relay_status);
            res.put("relay activate status" , ""+this.relay_activate_status);
            return res;
        }
    }

}
