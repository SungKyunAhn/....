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
 * 52 43 44 43 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 00 00 64 00
 * 00 00 00
 * There are six predefined control points for this meter. 
 * The six load control points are in order: 
 * 	Remote Connect/Disconnect Switch (RCDC), 
 * 	Emergency Conservation Period (ECP), 
 * 	Demand Limiting Period (DLP), 
 * 	Prepayment Mode (PPM), 
 * 	LOCKOUT (freezes remote disconnect switch in it’s current state), 
 * 	and RCDC manual acknowledgement. 
 * 
 * This is read-only table indicating which control points are currently active,. 
 * In the case of ECP it contains time remaining in the current ECP interval. 
 * In the case of RCDC control point, requested and output level  are last switch state request sent to switch control board by meter, 
 * and sensed level is actual switch state according to the last successful switch actuation requested by the meter 
 * with 100 being ON and OPEN, and 0 being OFF and CLOSED. 
 * 
 * Load Control Status Table
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class ST112 implements java.io.Serializable {

	private static final long serialVersionUID = -3488439344577723436L;
	private static Log log = LogFactory.getLog(ST112.class);
	private byte[] data;
	private int relay_status = -1;
	private int relay_activate_status = -1;
	
	private static final int CONTROL_POINTS_LENGTH = 27;

    private byte[] NAME = new byte[20];    
    private byte[] REQUESTED_LEVEL = new byte[1];
    private byte[] OUTPUT_LEVEL = new byte[1];
    private byte[] SENSED_LEVEL = new byte[1];
    private byte[] STATUS = new byte[1];
    private byte[] HOURS = new byte[1];
    private byte[] MINUTES = new byte[1];
    private byte[] SECONDS = new byte[1];
	

	public ST112() {}
	
	/**
	 * Constructor .<p>
	 */
	public ST112(byte[] data) 
    {
		this.data = data;
        parse();
	}
	

    public void parse() 
    {
    	int NBR_CONTROL_POINTS = data.length / CONTROL_POINTS_LENGTH;
    	int pos = 0;
    	
    	for(int i = 0; i < NBR_CONTROL_POINTS; i++){
    	    
			System.arraycopy(data, pos, NAME, 0, NAME.length);
			pos += NAME.length;			
			System.arraycopy(data, pos, REQUESTED_LEVEL, 0, REQUESTED_LEVEL.length);
			pos += REQUESTED_LEVEL.length;
			System.arraycopy(data, pos, OUTPUT_LEVEL, 0, OUTPUT_LEVEL.length);
			pos += OUTPUT_LEVEL.length;
			System.arraycopy(data, pos, SENSED_LEVEL, 0, SENSED_LEVEL.length);
			pos += SENSED_LEVEL.length;			
			System.arraycopy(data, pos, STATUS, 0, STATUS.length);
			pos += STATUS.length;
			System.arraycopy(data, pos, HOURS, 0, HOURS.length);
			pos += HOURS.length;
			System.arraycopy(data, pos, MINUTES, 0, MINUTES.length);
			pos += MINUTES.length;
			System.arraycopy(data, pos, SECONDS, 0, SECONDS.length);
			pos += SECONDS.length;
		    
			String name = new String(NAME).trim();
			if(name.equals("RCDC")){
				int sensedLevel = SENSED_LEVEL[0] & 0xFF;
				
				if(sensedLevel == 100){ //ON, OPEN
					this.relay_status = 1;
					this.relay_activate_status = 1;
				}else if(sensedLevel == 0){//OFF,CLOSED
					this.relay_status = 0;
					this.relay_activate_status = 0;
				}
			}
			
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
