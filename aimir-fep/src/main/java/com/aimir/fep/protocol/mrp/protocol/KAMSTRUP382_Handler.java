/** 
 * @(#)KAMSTRUP382_Handler.java        *
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
package com.aimir.fep.protocol.mrp.protocol;

import java.net.Socket;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DataSVC;
import com.aimir.constants.CommonConstants.MeterVendor;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.OPAQUE;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterDataEntry;
import com.aimir.fep.protocol.mrp.MeterProtocolHandler;
import com.aimir.fep.protocol.mrp.client.MRPClientProtocolHandler;
import com.aimir.fep.protocol.mrp.command.frame.sms.RequestFrame;
import com.aimir.fep.protocol.mrp.command.frame.sms.ResponseFrame;
import com.aimir.fep.protocol.mrp.exception.MRPError;
import com.aimir.fep.protocol.mrp.exception.MRPException;
import com.aimir.fep.util.Bcd;
import com.aimir.fep.util.ByteArray;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.util.TimeUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

/**
 * @author YK.Park
 */
	
public class KAMSTRUP382_Handler extends MeterProtocolHandler { 

    private static Log log = LogFactory.getLog(KAMSTRUP382_Handler.class);
    private String meterId;
    private String modemId;
    private String groupId = "";
    private String memberId = "";
    private String mcuSwVersion = "";

    private int retry = Integer.parseInt(FMPProperty.getProperty(
            "protocol.retry","6"));

    protected long sendBytes;
    
	public KAMSTRUP382_Handler() { }
	
	public void setModemNumber(String modemId){
		this.modemId = modemId;
	}
    public void setGroupId(String groupId){
    	this.groupId = groupId;
    }
    public void setMemberId(String memberId){
    	this.memberId = memberId;
    }
    public void setMcuSwVersion(String mcuSwVersion){
    	this.mcuSwVersion = mcuSwVersion;
    }
    public CommandData execute(MRPClientProtocolHandler handler,
                          IoSession session, 
                          CommandData command)
    throws MRPException
    {
        this.handler = handler;
        
        CommandData commandData = null;
        ByteArray ba = new ByteArray();
        String cmd = command.getCmd().getValue();
        SMIValue[] smiValue = null;
        smiValue = command.getSMIValue();
        int seq  = 0;
        String type = null;
        String[] param = null;
        int failCode = 0;
        
        if(smiValue != null && smiValue.length > 0){
        	cmd = ((OCTET)smiValue[0].getVariable()).toString();
        }
        
        log.debug("==============KAMSTRUP382_Handler start cmd:"+cmd+"================");

        for(int i = 0;smiValue != null && i < smiValue.length; i++)
        {
        	log.debug(smiValue[i]);
        }
        
        
        if(cmd.equals(RequestFrame.CMD_ONDEMAND))
        {
        	
            if(smiValue != null && smiValue.length >= 3){

            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
                
                if(smiValue.length >= 5){
                	param = new String[2];
                	param[0] = ((OCTET)smiValue[3].getVariable()).toString().substring(2,8);
                	param[1] = ((OCTET)smiValue[4].getVariable()).toString().substring(2,8);
                }
            }
            
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_ONDEMAND, param);                        
            ResponseFrame resFrame = request(session, reqFrame);
            
            String mdTime = null;
            int currValue = 0;
            if(resFrame != null){
            	log.debug("ResFrame Decode="+resFrame.toString());
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                	if(resultList != null && resultList.size() > 0 && type.equals("F")){
                		if(resultList.size() >=2){
                    		mdTime = (String)resultList.get(0);
                    		currValue = Integer.parseInt((String)resultList.get(1));
                		}else{
                			mdTime = TimeUtil.getCurrentDateUsingFormat("yyyyMMddHHmmss");
                    		currValue = Integer.parseInt((String)resultList.get(0));
                		}

                		log.debug("datetime="+mdTime+",currValue="+currValue);
                        byte[] gmttime = new byte[11];
                        int GMT = Calendar.ZONE_OFFSET , DST = 0;
                        byte[] gmt = DataUtil.get2ByteToInt(GMT);
                        byte[] dst = DataUtil.get2ByteToInt(DST);
                        byte[] datetime = Bcd.encodeTime(mdTime);
                        System.arraycopy(gmt,0,gmttime,0,2);
                        System.arraycopy(dst,0,gmttime,2,2);
                        System.arraycopy(datetime,0,gmttime,4,7);                        
                	    //ba.append(gmttime);
                	    ba.append(DataUtil.get4ByteToInt(currValue));
                	    //ba.append(new byte[]{0x00});
                	    //ba.append(new byte[]{0x00,0x00});
                	}
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }

            
            if(ba != null && ba.toByteArray().length > 0)
            {      

                log.debug("meterEntryData=>"+smiValue[0].getVariable().toString()+","+new OCTET(ba.toByteArray()).toHexString());
                meterDataEntry md = new meterDataEntry();
                md.setMdID(new OCTET(smiValue[0].getVariable().toString()));
                md.setMdSerial(new OCTET(this.meterId));
                md.setMdServiceType(new BYTE(Integer.parseInt(CommonConstants.getDataSvcCode(DataSVC.Electricity))));
                md.setMdTime(new TIMESTAMP(mdTime));
                md.setMdType(new BYTE(Integer.parseInt(CommonConstants.getModemTypeCode(ModemType.MMIU))));
                md.setMdVendor(new BYTE(MeterVendor.KAMSTRUP.getCode()[0].intValue()));//kamstrup
                md.setMdData(new OCTET(ba.toByteArray()));
                commandData = new CommandData();
                commandData.setCnt(new WORD(1));
                commandData.setTotalLength(ba.toByteArray().length);
                commandData.append(new SMIValue(new OID("10.1.0"),new OPAQUE(md)));
            }else{
                commandData = new CommandData();
                commandData.setErrCode(new BYTE(failCode));
                commandData.setCnt(new WORD(0));
            }
        }
        else if(cmd.equals(RequestFrame.CMD_GETCUTOFF))
        {
        	
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
            }
            
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_GETCUTOFF, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                	
                	
                	for(int i = 0; resultList != null && i < resultList.size(); i++){
                		String val = (String)resultList.get(i);
                		try{
                			if(i == 0){
                				ba.append(new byte[]{(byte)0x98});//CID
                			}                			
                        	ba.append(new byte[]{hexstr2byte(val)});
                		}catch(Exception e){
                			failCode = ErrorCode.IF4ERR_INVALID_VALUE;
                			log.debug("failCode="+failCode);
                		}

                	}
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            
            commandData = new CommandData();
            commandData.setCnt(new WORD(1));
            commandData.setTotalLength(ba.toByteArray().length);
            commandData.append(new SMIValue(new OID("1.19.0"),new OCTET(ba.toByteArray())));
        }
        else if(cmd.equals(RequestFrame.CMD_SETCUTOFF))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
                
                if(smiValue.length >= 4){
                	param = new String[1];
                	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
                }
            }
            
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_SETCUTOFF, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                	for(int i = 0; resultList != null && i < resultList.size(); i++){
                		String val = (String)resultList.get(i);
                		try{
                			if(i == 0){
                				ba.append(new byte[]{(byte)0x98});//CID
                			} 
                        	ba.append(new byte[]{hexstr2byte(val)});
                		}catch(Exception e){
                			failCode = ErrorCode.IF4ERR_INVALID_VALUE;
                			log.debug("failCode="+failCode);
                		}
                	}
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            
            commandData = new CommandData();
            commandData.setCnt(new WORD(1));
            commandData.setTotalLength(ba.toByteArray().length);
            commandData.append(new SMIValue(new OID("1.19.0"),new OCTET(ba.toByteArray())));
        }
        else if(cmd.equals(RequestFrame.CMD_SETSENDINTERVAL))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
                
                if(smiValue.length >= 4){
                	param = new String[1];
                	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
                }
            }
            
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_SETSENDINTERVAL, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else if(cmd.equals(RequestFrame.CMD_SETBAUDRATE))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
                
                if(smiValue.length >= 4){
                	param = new String[1];
                	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
                }
            }
            
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_SETBAUDRATE, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else if(cmd.equals(RequestFrame.CMD_SETSERVER))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
                
                if(smiValue.length == 4){
                	param = new String[1];
                	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
                }
                
                if(smiValue.length == 5){
                	param = new String[2];
                	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
                	param[1] = ((OCTET)smiValue[4].getVariable()).toString();
                }
            }
            
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_SETSERVER, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else if(cmd.equals(RequestFrame.CMD_SETMETERINGPERIOD))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
                
                if(smiValue.length >= 4){
                	param = new String[1];
                	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
                }
            }
            
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_SETMETERINGPERIOD, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else if(cmd.equals(RequestFrame.CMD_SETMETERSECURITY))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
                
                if(smiValue.length >= 5){
                	param = new String[2];
                	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
                	param[1] = ((OCTET)smiValue[4].getVariable()).toString();
                }
            }
            
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_SETMETERSECURITY, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else if(cmd.equals(RequestFrame.CMD_SETAPN))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
                
                if(smiValue.length == 4){
                	param = new String[1];
                	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
                }
                
                if(smiValue.length == 6){
                	param = new String[3];
                	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
                	param[1] = ((OCTET)smiValue[4].getVariable()).toString();
                	param[2] = ((OCTET)smiValue[5].getVariable()).toString();
                }
            }
            
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_SETAPN, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else if(cmd.equals(RequestFrame.CMD_SETMETERVENDORMODEL))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
                
                if(smiValue.length == 4){
                	param = new String[1];
                	//param[0] = ((OCTET)smiValue[3].getVariable()).toString();
                	param[0] = "01:01";
                }
                if(smiValue.length == 5){
                	param = new String[2];
                	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
                	param[1] = ((OCTET)smiValue[4].getVariable()).toString();
                }
            }
            
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_SETMETERVENDORMODEL, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else if(cmd.equals(RequestFrame.CMD_SETSMSSMSC))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
            }
            if(smiValue.length == 4){
            	param = new String[1];
            	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
            }
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_SETSMSSMSC, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else if(cmd.equals(RequestFrame.CMD_SETNTPSERVER))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
            }
            if(smiValue.length >= 4){
            	param = new String[1];
            	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
            }
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_SETNTPSERVER, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else if(cmd.equals(RequestFrame.CMD_RESET))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
            }
            
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_RESET, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else if(cmd.equals(RequestFrame.CMD_OTA))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
                
                if(smiValue.length >= 4){
                	param = new String[1];
                	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
                }
            }
            
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_OTA, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else if(cmd.equals(RequestFrame.CMD_DOTA))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
                
                if(smiValue.length >= 7){
                	param = new String[4];
                	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
                	param[1] = ((OCTET)smiValue[4].getVariable()).toString();
                	param[2] = ((OCTET)smiValue[5].getVariable()).toString();
                	param[3] = ((OCTET)smiValue[6].getVariable()).toString();
                }
            }
            
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_DOTA, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else if(cmd.equals(RequestFrame.CMD_SETPHONENUMBER))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
            }
            if(smiValue.length >= 4){
            	param = new String[1];
            	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
            }
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_SETPHONENUMBER, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else if(cmd.equals(RequestFrame.CMD_SETSENDINGFLAG))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
            }
            if(smiValue.length >= 4){
            	param = new String[1];
            	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
            }
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_SETSENDINGFLAG, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else if(cmd.equals(RequestFrame.CMD_SETGPRSATCOMMAND))
        {
            if(smiValue != null && smiValue.length >= 3){
            	seq = Integer.parseInt(((OCTET)smiValue[1].getVariable()).toString());
                type = ((OCTET)smiValue[2].getVariable()).toString();
            }
            if(smiValue.length >= 4){
            	param = new String[1];
            	param[0] = ((OCTET)smiValue[3].getVariable()).toString();
            }
            RequestFrame reqFrame = new RequestFrame((byte)seq, type, RequestFrame.CMD_SETGPRSATCOMMAND, param);            
            ResponseFrame resFrame = request(session, reqFrame);
            
            if(resFrame != null){
                if(resFrame.getResult()){
                	ArrayList resultList = resFrame.getParam();
                }else{
                	failCode = resFrame.getReason();
                	log.debug("failCode="+failCode);
                }
            }
            else
            {
            	failCode = ErrorCode.IF4ERR_CANNOT_GET;
            	log.debug("failCode="+failCode);
            }
            commandData = new CommandData();
            commandData.setErrCode(new BYTE(failCode));
            commandData.setCnt(new WORD(0));
        }
        else
        {
            throw new MRPException(MRPError.ERR_INVALID_PARAM,"Invalid parameters");
        }
        


        log.debug("==============KAMSTRUP382_Handler end ================");
        return commandData;
    }
    
    protected void asyncRequest(IoSession session, RequestFrame frame) throws MRPException
    {
        byte[] buf = frame.encode();
        session.write(buf); 
    }
    
    protected ResponseFrame request(IoSession session, RequestFrame frame) throws MRPException
    {
    	ResponseFrame rcvFrame = null;
    	ArrayList readList = null;
    	boolean goingOn = true;

    	try{
    		//sendFrame = "NT111Q,F,CM11,+46705008600";//TODO TEMP
    		//sendFrame = "NT100Q,F,CM17,01";//TODO TEMP
//    		sendFrame = "NT000Q,F,CM13"; //RESET
//    		sendFrame	=	"NT000Q,F,CM20,21,aimir,roqkf2010aimir,NG111";
//    		sendFrame	=	"NT000Q,F,CM20,21,aimir,roqkf2010aimir"+"filename";

    		String sendFrame = new String(frame.encode());
    		log.debug("RequestFrame=["+sendFrame+"]");
    		handler.sendSMS(session,modemId,sendFrame);
    		log.debug("#####################################################################");
            Thread.sleep(5000);
            for(int k = 0 ; k < retry && goingOn; k++)
            {
            	readList = handler.readSMS(session,modemId,sendFrame.substring(0,5),k);
                for(int i = 0; readList != null && i < readList.size() && goingOn; i++){
                	String message = (String)readList.get(i);
                	
                	if(message != null && !"".equals(message)){
                		int okIdx = message.indexOf("OK");
                		if(okIdx >= 0){
                			message = message.substring(0,okIdx);
                		}
                		
                		log.debug("ReceivedFrame=["+message+"]");
                		rcvFrame = new ResponseFrame(message);
                		rcvFrame.decode();
                    	if(frame.getSEQ() == rcvFrame.getSEQ()){
                    		goingOn = false;
                    	}
                	}else{
                		message = "";
                	}
                }
                Thread.sleep(3000);
            }
    	}catch(Exception e){
    		log.error(e,e);
    		//throw new MRPException(MRPError.ERR_INVALID_PARAM);
    	}finally{
    		try{
            	handler.deleteSMS(session);
    		}catch(Exception e){log.warn(e,e);}
    	}

        return rcvFrame;
    }

    public void initProcess(IoSession session) throws MRPException { }

    public ByteArray configureRead(IoSession session) throws MRPException { return null; }

	public ByteArray eventlogRead(IoSession session) throws MRPException { return null; }
	
    public ByteArray instRead(IoSession session) throws MRPException { return null; }
    
    public ByteArray lpRead(IoSession session, String startday, String endday, int lpCycle) throws MRPException { return null; }

    public ByteArray billRead(IoSession session) throws MRPException { return null; }
    
	public ByteArray currbillRead(IoSession session) throws MRPException { return null; }
	
    public ByteArray pqRead(IoSession session) throws MRPException { return null; }

    public void quit() throws MRPException {}

    public HashMap timeCheck(IoSession session) throws MRPException { return null; }

    public HashMap timeSync(IoSession session, int timethreshold) throws MRPException { return null; }
    
    public boolean checkCRC(byte[] src) throws MRPException { return false; }
    
    public long getSendBytes() throws MRPException{
        return this.sendBytes;// session.getWrittenBytes()
    }
    
	private byte hexstr2byte(String s) throws Exception {

		byte b;

		if(s.length() != 2)
			throw new Exception("String type error");

		int tmp1 = Character.getNumericValue(s.charAt(0));
		int tmp2 = Character.getNumericValue(s.charAt(1));

		if(tmp1 == -1 || tmp2 == -1)
			throw new Exception("Non numeric value");

		b = (byte) (tmp1 << 4);
		b |= tmp2;
		return b;
	}

	@Override
	public void setGroupNumber(String groupNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMemberNumber(String memberNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setModemId(String modemId) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public CommandData execute(Socket socket, CommandData command)
            throws MRPException {
        // TODO Auto-generated method stub
        return null;
    }

}
