/** 
 * @(#)RequestFrame.java       1.0 2008-11-24 *
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelecom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */

package com.aimir.fep.protocol.mrp.command.frame.sms;

import com.aimir.fep.protocol.mrp.exception.MRPError;
import com.aimir.fep.protocol.mrp.exception.MRPException;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * RequestFrame 
 * 
 * @version     1.0 1 Nov 2008 
 * @author		YK.Park 
 */

public class RequestFrame {
	
	private static Log log = LogFactory.getLog(RequestFrame.class);

	public static final String CMD_ONDEMAND = "CM01";
	public static final String CMD_GETCUTOFF = "CM02";
	public static final String CMD_SETCUTOFF = "CM03";
	public static final String CMD_SETSENDINTERVAL = "CM04";
	public static final String CMD_SETBAUDRATE = "CM05";
	public static final String CMD_SETSERVER = "CM06";
	public static final String CMD_SETMETERINGPERIOD = "CM07";
	public static final String CMD_SETMETERSECURITY = "CM08";
	public static final String CMD_SETAPN = "CM09";
	public static final String CMD_SETMETERVENDORMODEL = "CM10";
	public static final String CMD_SETSMSSMSC = "CM11";

	public static final String CMD_SETNTPSERVER = "CM12";
	public static final String CMD_RESET = "CM13";
	public static final String CMD_CONNECTGPRS = "CM14";//deprecated
	public static final String CMD_OTA = "CM15";
	public static final String CMD_SETPHONENUMBER = "CM16";
	public static final String CMD_SETSENDINGFLAG = "CM17";
	public static final String CMD_SETGPRSATCOMMAND = "CM18";
	
	public static final String CMD_DOTA = "CM20";
	public static final String CMD_BYPASS = "CM28";
	public static final String CMD_METERTIMESYNC = "CM21";
	
	public static final String CMD_GPRSCONNECT = "CM30";
	public static final String CMD_GPRSCONNECT_OID = "244.0.0";
	
	public static final String STX = "NT";
	private String SEQ = "000";
	public static final String REQUEST = "Q";
	public static final String BG = "B";
	public static final String FG = "F";
	private String type = "F";
	private String command = null;
	private String[] param = null;
	public static final String DELIM = ",";
	public static final String PARAMDELIM = ",";
	private int sequence = 0;
	private StringBuffer frame = null;
	
	
	public RequestFrame()
	{
		
	}

    /**
     *  Constructor. <p>
     */ 
    public RequestFrame(byte seq, String type, String command, String[] param) 
    {
    	this.sequence = (int)(seq & 0xFF);
    	this.type = type;
    	if(sequence >= 10 && sequence < 100){
    		SEQ = "0"+sequence;
    	}else if(sequence < 10){
    		SEQ = "00"+sequence;
    	}else{
    		SEQ = ""+sequence;
    	}
    	this.command = command;
    	this.param = param;
    }
    
    /**
     *  Constructor. <p>
     */ 
    public RequestFrame(byte seq, String type, String command, String param) 
    {
    	this.sequence = (int)(seq & 0xFF);
    	this.type = type;
    	if(sequence >= 10 && sequence < 100){
    		SEQ = "0"+sequence;
    	}else if(sequence < 10){
    		SEQ = "00"+sequence;
    	}else{
    		SEQ = ""+sequence;
    	}
    	this.command = command;
    	this.param = new String[]{param};
    }
    
    public int getSEQ(){
    	return this.sequence;
    }
    
    public byte[] encode() throws MRPException {
    	
    	if(type == null || type.equals("")){
    		type = "F";
    	}
    	frame = new StringBuffer();
    	frame.append(STX);
    	frame.append(SEQ);
    	frame.append(REQUEST);
    	frame.append(DELIM);
    	frame.append(type);
    	frame.append(DELIM);
    	if(command != null && !command.equals(""))
    	{
    		frame.append(command);
    	}
    	else
    	{
    		throw new MRPException(MRPError.ERR_INVALID_PARAM);
    	}
    	if(param != null && param.length > 0){
        	frame.append(DELIM);
    		for(int i = 0; i < param.length; i++){
    			if(i != 0){
        	    	frame.append(PARAMDELIM);
    			}
    			frame.append(param[i]);
    		}
    	}
    	
    	log.debug("beforeEncode=["+frame.toString()+"]");
    	
    	//Encryption encrypt = new Encryption(frame);
    	
    	return frame.toString().getBytes();
    }
    
    public void decode(String message) throws MRPException 
    {
    	if(message.startsWith(STX)){
    		StringTokenizer st = new StringTokenizer(message,DELIM);
        	try{
                if(st.hasMoreTokens()){
                	 String header = (String)st.nextToken();
                	 this.sequence = Integer.parseInt(header.substring(STX.length(),STX.length()+3));
                }
                if(st.hasMoreTokens()){
                	this.type = (String)st.nextToken();
                }
                if(st.hasMoreTokens()){
                	this.command = (String)st.nextToken();
                }
                
                //String paramTemp = null;
                //if(st.hasMoreTokens()){
                //	paramTemp = (String)st.nextToken();
                //}
                //st = new StringTokenizer(paramTemp,PARAMDELIM);
                int idx = 0;
                param = new String[st.countTokens()];
                while(st.hasMoreTokens()){
                	param[idx++] = (String)st.nextToken();
                }
        	}catch(Exception e){
        		throw new MRPException(MRPError.ERR_INVALID_PARAM);
        	}
    	}
    }

    
    public String getType()
    {
    	return this.type;
    }
    
    public String getCommand()
    {
    	return this.command;
    }
    
    public String[] getParam()
    {
    	return this.param;
    }

}
