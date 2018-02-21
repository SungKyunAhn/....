/** 
 * @(#)KAMSTRUP601_Handler.java       1.0 28/01/09 *
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
import java.text.ParseException;
import java.util.HashMap;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DataSVC;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.MeterVendor;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.OPAQUE;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterDataEntry;
import com.aimir.fep.protocol.mrp.MeterProtocolHandler;
import com.aimir.fep.protocol.mrp.client.MRPClientProtocolHandler;
import com.aimir.fep.protocol.mrp.exception.MRPError;
import com.aimir.fep.protocol.mrp.exception.MRPException;
import com.aimir.fep.util.ByteArray;
import com.aimir.fep.util.DataUtil;

import com.aimir.util.TimeUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * @author Kang, Soyi
 */
	
public class KAMSTRUP601_Handler extends MeterProtocolHandler { 

    private static Log log = LogFactory.getLog(KAMSTRUP601_Handler.class);
    private String meterId;
    
    private String groupNumber = "";
    private String memberNumber = "";
    private String mcuSwVersion = "";
    private int mdCount = 1;

	/**
	 * Constructor.<p>
	 */
    protected long sendBytes;    
    
	public KAMSTRUP601_Handler() {	

	}
	
    public void setGroupNumber(String groupNumber){
    	this.groupNumber = groupNumber;
    }
    public void setMemberNumber(String memberNumber){
    	this.memberNumber = memberNumber;
    }
    public void setMcuSwVersion(String mcuSwVersion){
    	this.mcuSwVersion = mcuSwVersion;
    }
    
    public CommandData execute(MRPClientProtocolHandler handler,
                          IoSession session, 
                          CommandData command) throws MRPException
    {
        this.handler = handler;
        
        CommandData commandData = null;
        ByteArray[] mddata = null;
        byte[] mti = null;
        byte[] tpb = null;
        byte[] day = null;
        byte[] tcb = null;
        byte[] lpd = null;
        byte[] ist = null;
        byte[] eld = null;
        byte[] pqm = null;
        byte[] bp = null;
        byte[] cum = null;
        
        ByteArray ba = new ByteArray();
        int nOffset = 0;
        int nCount = 96;
        String from = "";
        String cmd = command.getCmd().getValue();
        log.debug("==============KAMSTRUP601_Handler start cmd:"+cmd+"================");
        if(cmd.equals("104.6.0")||cmd.equals("104.13.0"))
        {
            SMIValue[] smiValue = command.getSMIValue();
            
            if(smiValue != null && smiValue.length >= 2){
                int kind = ((BYTE)smiValue[1].getVariable()).getValue();
                
                if(smiValue.length == 4){
                	try{
                		nOffset = ((INT)smiValue[2].getVariable()).getValue();
                        nCount = ((INT)smiValue[3].getVariable()).getValue();
                	}
                    catch (Exception e)
                    {

                    }
                }else{
                    try
                    {
                    	from = TimeUtil.getPreDay(TimeUtil.getCurrentTime(),2);
                    }
                    catch (ParseException e)
                    {

                    }
                }

                byte[] mcuId = new byte[17];
                System.arraycopy(command.getMcuId().getBytes(), 0, mcuId, 0, command.getMcuId().length());
                
                ba.append(mcuSwVersion.getBytes());
                ba.append(new byte[]{0x00});
                ba.append(mcuId);
                ba.append(new byte[]{(byte)0x90});
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00});//ZIGBEE ID
                ba.append(new byte[]{0x00,0x00,0x00,0x00});
                
                switch(kind){
                    case 0:
                        initProcess(session);

						if(mcuSwVersion.equals("NC5K1")||mcuSwVersion.equals("NC5K4"))
                        {
                            mti = configureRead(session).toByteArray();
                            tpb = billRead(session).toByteArray();
                            tcb = currbillRead(session).toByteArray();
                    
                          	day = dailyRead(session, nOffset, nCount).toByteArray();
                            lpd = lpRead(session, nOffset, nCount, 60).toByteArray();
                            eld = eventlogRead(session).toByteArray();
                        	//basepulse
    		                bp = basePulseRead(session, nOffset, nCount).toByteArray();
                        }
                        else if(mcuSwVersion.equals("NC5K2"))
                        {
                        	mddata = demandRomRead(session, nOffset, nCount);
                        }
                        else if(mcuSwVersion.equals("NC5K3"))
                        {
                        	mddata = demandRomRead(session, nOffset, nCount);
                        }
                        else
                        {
                            mti = configureRead(session).toByteArray();
                            tpb = billRead(session).toByteArray();
                            tcb = currbillRead(session).toByteArray();
                    
                          	day = dailyRead(session, nOffset, nCount).toByteArray();
                            lpd = lpRead(session, nOffset, nCount, 60).toByteArray();
                            eld = eventlogRead(session).toByteArray();
                        	//cummulated data
                        	cum = cummulatedDataRead(session, groupNumber, memberNumber, nOffset, nCount).toByteArray();
                        }


                        
                        if(mcuSwVersion.equals("NC5K1"))
                        {
                            ba.append(mti);
                            
                            ba.append(DataConstants.MONTHLY);
                            ba.append(DataUtil.get2ByteToInt(tpb.length));
                            ba.append(tpb);
                           
                            ba.append(DataConstants.CURRENT);
                            ba.append(DataUtil.get2ByteToInt(tcb.length));
                            ba.append(tcb);
                           
                            ba.append(DataConstants.DAILY);
                            ba.append(DataUtil.get2ByteToInt(day.length));
                            ba.append(day);
                            
                            ba.append(DataConstants.HOURLY);
                            ba.append(DataUtil.get2ByteToInt(lpd.length));
                            ba.append(lpd);

                            ba.append(DataConstants.EVENT);
                            ba.append(DataUtil.get2ByteToInt(eld.length));
                            ba.append(eld);
			                ba.append(DataConstants.BASEPULSE);
			                ba.append(DataUtil.get2ByteToInt(bp.length));
			                ba.append(bp);
                        }
                        else if(mcuSwVersion.equals("NC5K2"))
                        {
                            //ba.append(mddata);
                        	this.mdCount = mddata.length;
                        }
                        else if(mcuSwVersion.equals("NC5K3"))
                        {
                        	this.mdCount = mddata.length;
                            //ba.append(mddata);
                        }
                        else
                        {
                            ba.append(mti);
                            
                            ba.append(DataConstants.MONTHLY);
                            ba.append(DataUtil.get2ByteToInt(tpb.length));
                            ba.append(tpb);
                           
                            ba.append(DataConstants.CURRENT);
                            ba.append(DataUtil.get2ByteToInt(tcb.length));
                            ba.append(tcb);
                           
                            ba.append(DataConstants.DAILY);
                            ba.append(DataUtil.get2ByteToInt(day.length));
                            ba.append(day);
                            
                            ba.append(DataConstants.HOURLY);
                            ba.append(DataUtil.get2ByteToInt(lpd.length));
                            ba.append(lpd);

                            ba.append(DataConstants.EVENT);
                            ba.append(DataUtil.get2ByteToInt(eld.length));
                            ba.append(eld);
                        	ba.append(DataConstants.CUMMDATA);
			                ba.append(DataUtil.get2ByteToInt(bp.length));
			                ba.append(cum);
                        }
		                
                        break;
                    case 1:
                        initProcess(session);
                        mti = configureRead(session).toByteArray();
                  //      tcb = currbillRead(session).toByteArray();
                      
                        ba.append(DataConstants.MTI);
                        ba.append(DataUtil.get2ByteToInt(mti.length));
                        ba.append(mti);
                        
                        break;
                    default:
                        throw new MRPException(MRPError.ERR_INVALID_PARAM,"Invalid parameters");
                }
                
            }else{
                throw new MRPException(MRPError.ERR_INVALID_PARAM,"Invalid parameters");
            }
            
            commandData = new CommandData();
            commandData.setCnt(new WORD(1));
            ByteArray buff = new ByteArray();

            meterDataEntry md = new meterDataEntry();
            md.setMdID(new OCTET(smiValue[0].getVariable().toString()));
            md.setMdSerial(new OCTET(this.meterId));
            md.setMdServiceType(new BYTE(DataSVC.Heating.getCode()));
            String currTime = null;
            try{
            	currTime = TimeUtil.getCurrentTime();
            	Thread.sleep(10);
            }catch(Exception e){}
            md.setMdTime(new TIMESTAMP(currTime));
            md.setMdType(new BYTE(ModemType.IEIU.getCode()));
            md.setMdVendor(new BYTE((byte)MeterVendor.KAMSTRUP.getCode()[0].intValue()));//KAMSTRUP601
            
            buff.append(ba.toByteArray());
            if(mddata != null && mddata.length > 0){
            	try{
            		buff.append(DataUtil.select(mddata[mddata.length-1].toByteArray(),0,99));// meter serial,meter status
					int lpCount = 24*mddata.length;
					byte[] lpcnt = new byte[2];
					lpcnt[0] = (byte)(lpCount & 0xFF);
					lpcnt[1] = (byte)(lpCount >> 8);
					buff.append(lpcnt);
            	}catch(Exception e){
            		throw new MRPException(MRPError.ERR_INVALID_PARAM,"Meter Data Read Error");
            	}
            }


            for(int i = 0; i < mdCount; i++){
                if(ba != null && ba.toByteArray().length > 0)
                {      
                    if(mddata != null && mddata.length > 0){
                    	try{
                    		buff.append(DataUtil.select(mddata[mddata.length - (i+1)].toByteArray(),101,744));
                    	} catch (Exception e){
                    		throw new MRPException(MRPError.ERR_INVALID_PARAM,"Meter LP Data Read Error");
                    	}
                    }
                }
            }
            md.setMdData(new OCTET(buff.toByteArray()));
            log.debug("meterEntryData=>"+smiValue[0].getVariable().toString()+","+new OCTET(buff.toByteArray()).toHexString());
            commandData.setTotalLength(buff.toByteArray().length);
            commandData.append(new SMIValue(new OID("10.1.0"),new OPAQUE(md)));
            buff = null;
            md = null;

        }

        log.debug("==============KAMSTRUP601_Handler end ================");
        return commandData;
    }
    
    protected ByteArray readChannelConfigure(IoSession session) throws MRPException
    {
        return null;
    }

    protected KAMSTRUP601_ReceiveDataFrame read(IoSession session,KAMSTRUP601_RequestDataFrame frame) throws MRPException
    {
    	return null;
    }
    
    protected KAMSTRUP601_ReceiveDataFrame read(IoSession session, KAMSTRUP601_RequestDataFrame frame,
    											byte commandKind, int reg_num )
    throws MRPException
    {
        int sequence = 0;
        boolean isRemain = true;
        try
        {
            IoBuffer buf = null;
            KAMSTRUP601_ReceiveDataFrame rcvFrame = new KAMSTRUP601_ReceiveDataFrame();
            
            int retry =3;
            byte[] message = null; 
            byte[] getCmd = null;
            int cnt=0;
            while(cnt<retry){
            	log.debug("cnt :"+cnt);
            	cnt ++;
            	try{
            		
            		buf = frame.getIoBuffer();
	            	session.write(buf); 
		            message = (byte[])handler.getMessageToStopByte(session, KAMSTRUP601_DataConstants.ETX);
		            log.debug("message length :" +message.length);
		            
		            if(message!=null && message.length>=2){
		            	if(message[0]== KAMSTRUP601_DataConstants.STX_FROM_METER){
			            	getCmd = get_cmd(message, message.length);
			            	if(getCmd!=null && getCmd.length>0){

			            		int getRegNo = 0;			            		
			            		getRegNo = DataUtil.getIntTo2Byte(DataUtil.select(getCmd, 2, 2));
			            		if(reg_num != getRegNo){
			            			log.debug("Register No. error");
			            			continue;
			            		}
			            		break; //Normal
			            	}else{
			            		log.debug("CRC ERROR");
			            		continue;
			            	}
		            	}else{
		            		log.debug("STX ERROR");
		            		continue;
		            	}
		            }else{
		            	log.debug("No Response");
		            	continue;
		            }
            	}catch(Exception e){
            		log.error("Read error 1",e);
            	}
           }
           if(getCmd!=null && getCmd.length>0)
        	   rcvFrame.append(getCmd);
           else
        	   throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
           
           return rcvFrame;
        }
        catch (Exception e)
        {
            log.error("Read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
        } 
    }

    /*
    * Call get_cmd with a data buffer (cmd_data) and the maximum length of
    * the buffer (max_len). get_cmd will return FALSE until a complete
    * command is received. When this happens the length of the data is
    * returned in len. Packets with bad CRC's are discarded.
    */
    protected byte[] get_cmd(byte[] cmd_data, int max_len)
    {
	    byte c;
	    char crc =0;
	    boolean DLE_last = false;
	    int len = 0;

	    /*
	    * Get characters from the serial port while they are avialable
	    */
	    for(int i=0; i<cmd_data.length; i++){
	    	c = cmd_data[i];
		    switch(c) {
			    case KAMSTRUP601_DataConstants.STX_FROM_METER:
				    len = 0;
			    break;
			    case KAMSTRUP601_DataConstants.ETX:
				    if ((crc == 0)&& (len >= 2)) {
				    	len -= 2; /* remove crc characters */
				    	byte[] resultBuf = new byte[len];
				    	System.arraycopy(cmd_data, 0, resultBuf, 0, len);
				    	return(resultBuf);
				    } else if (len==0)
				    	return new byte[0];
			    break;
			    case KAMSTRUP601_DataConstants.DLE:
			    	DLE_last = true;
			    break;
			    default:
			    	if (len >= max_len)
				    	break;
				    if (DLE_last){
					    c = (byte) ~c;
						DLE_last = false;
				    }			    	
				    crc = KAMSTRUP601_DataConstants.CalculateCharacterCRC16(crc,c);
				    cmd_data[len++] = c;
		    }
	    }
	    return null;
    }
    
    public ByteArray basePulseRead(IoSession session, int nOffset, int nCount) throws MRPException
    {
        log.debug("========== Base Pulse Read Start ===============");
        
        ByteArray ba = new ByteArray();
        
        try
        {
		if(nCount>5)
			nCount =5;

		BYTE addr = null;
    	BYTE command = null;
    	OCTET send_data = null;
    	KAMSTRUP601_RequestDataFrame frame = null;
    	KAMSTRUP601_ReceiveDataFrame buf = null;
        byte[] temp = null;
        
        addr = new BYTE(KAMSTRUP601_DataConstants.DES_ADDR_HEAT_METER);
        command = new BYTE(KAMSTRUP601_DataConstants.CID_GETREGISTER);
        
		//get meter's Datetime

        send_data = new OCTET(DataUtil.append(new byte[]{(byte)0x01},DataUtil.get2ByteToInt(KAMSTRUP601_DataConstants.REG_DATE)));
        
		frame = new KAMSTRUP601_RequestDataFrame(null, addr, command, send_data, 0,0, null);
		buf = read(session,frame, KAMSTRUP601_DataConstants.CID_GETREGISTER, KAMSTRUP601_DataConstants.REG_DATE);
		temp = buf.encode();
		byte[] date = DataUtil.select(temp,5,4);
		
		send_data = new OCTET(DataUtil.append(new byte[]{(byte)0x01},DataUtil.get2ByteToInt(KAMSTRUP601_DataConstants.REG_CLOCK)));
		
		frame = new KAMSTRUP601_RequestDataFrame(null, addr, command, send_data, 0,0, null);
		buf = read(session,frame, KAMSTRUP601_DataConstants.CID_GETREGISTER, KAMSTRUP601_DataConstants.REG_CLOCK);
		temp = buf.encode();
		byte[] time = DataUtil.select(temp,5,4);
		
		String endDateTime = TimeUtil.getCurrentTime().substring(0,8)+"000000";
		log.debug("endDateTime :"+endDateTime);
		if(nOffset-nCount>0)
			endDateTime = TimeUtil.getPreDay(endDateTime, (nOffset-nCount));
		
		String meterDateTime = endDateTime;
		
		if(temp!=null){
			meterDateTime = getDateTime(date,time);
		}
		log.debug("meterDateTime :"+meterDateTime);
		log.debug("TimeUtil.getLongTime(endDateTime) : "+ TimeUtil.getLongTime(endDateTime));
		log.debug("TimeUtil.getLongTime(meterDateTime) : "+ TimeUtil.getLongTime(meterDateTime));
		if(TimeUtil.getLongTime(endDateTime)>TimeUtil.getLongTime(meterDateTime)){
			endDateTime = meterDateTime;
		}
		
	//	endDateTime = TimeUtil.getPreDay(endDateTime.substring(0, 8)+"000000", (nOffset);// resolution is 60 min.
		
	//	endDateTime = TimeUtil.getPreDay(endDateTime.substring(0, 10)+"0000", nOffset);// resolution is 60 min.
	//	endDateTime = endDateTime.substring(0, 10)+"0000";// resolution is 60 min.
		log.debug("request endDateTime :"+endDateTime);
		
		int meterdisconnectRetry = 3;
		int meterdisconnectCnt = 0;
		boolean meterdisconnect = false;
		while (!meterdisconnect && meterdisconnectCnt<meterdisconnectRetry){
			meterdisconnect = handler.disconnectMeter(session);
			log.debug("meter disconnect :"+meterdisconnect);
			meterdisconnectCnt ++;
		}
		
		if(meterdisconnect){
            byte[] year = DataUtil.get2ByteToInt(Integer.parseInt(endDateTime.substring(0,4))); 
            byte[] date1 = new byte[]{year[0], year[1], (byte)Integer.parseInt(endDateTime.substring(4,6)), 
            			(byte)Integer.parseInt(endDateTime.substring(6,8))};

            //new byte[]{(byte)0x07, (byte)0xD9, (byte)0x03, (byte)0x09};
            
            byte[] basepulse = null;
            basepulse= handler.getIEIUBasePulse(session, groupNumber, memberNumber, date1);
			if(basepulse!=null){
				if(basepulse.length==25){
            	log.debug("BasePulse data => "+new OCTET(basepulse).toHexString());
            	ba.append(DataUtil.select(basepulse, 11, 12)); 
				}else if (basepulse.length==21) {
            		log.debug("BasePulse data => "+new OCTET(basepulse).toHexString());
					ba.append(DataUtil.select(basepulse, 11, 8)); 
				}
			}

		}
        }catch(Exception e){
            log.error("Base Pulse read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"basePulse read error");
        }
        log.debug("========== Base Pulse Read End ===============");
        return ba;
    }
    
    public ByteArray[] demandRomRead(IoSession session, int nOffset, int nCount) throws MRPException
    {
        log.debug("========== demandRomData Read Start ===============");
        
        ByteArray[] ba = null;
        
        try{
        	
    		if(nCount>5)
    			nCount =5;
    		
    		ba = new ByteArray[nCount];

    		BYTE addr = null;
        	BYTE command = null;
        	OCTET send_data = null;
        	KAMSTRUP601_RequestDataFrame frame = null;
        	KAMSTRUP601_ReceiveDataFrame buf = null;
            byte[] temp = null;
            
            addr = new BYTE(KAMSTRUP601_DataConstants.DES_ADDR_HEAT_METER);
            command = new BYTE(KAMSTRUP601_DataConstants.CID_GETREGISTER);
    		
    		String endDateTime = TimeUtil.getCurrentTime();
    		endDateTime = TimeUtil.getPreDay(endDateTime, (nOffset-nCount));
    		
    		String meterDateTime = endDateTime;

    		log.debug("TimeUtil.getLongTime(endDateTime) : "+ TimeUtil.getLongTime(endDateTime));
    		log.debug("TimeUtil.getLongTime(meterDateTime) : "+ TimeUtil.getLongTime(meterDateTime));

    		int meterdisconnectRetry = 3;
    		int meterdisconnectCnt = 0;
    		

    		for(int i=0; i< nCount; i++){
    			
        		ByteArray ba2 = new ByteArray();
    			String datetime2 = TimeUtil.getPreDay(""+endDateTime,i+1);
    			log.debug("Request DateTime = "+datetime2);
    	        byte[] year = DataUtil.get2ByteToInt(Integer.parseInt(datetime2.substring(0,4))); 
    	        byte[] date1 = new byte[]{year[1], year[0],
    	        		(byte)Integer.parseInt(datetime2.substring(4,6)), (byte)Integer.parseInt(datetime2.substring(6,8))};
    	        
    	        byte[] demandData = null;
    	        demandData= handler.getIEIUDemandRomData(session, groupNumber, memberNumber, date1);
    	        if(demandData!=null && demandData.length > 845){
    	        	log.debug("demandRom data => "+new OCTET(demandData).toHexString());
    	        	ba2.append(DataUtil.select(demandData, 14, 845)); 
    	        }
    	        
        		if(ba2.toByteArray().length>0){
        			ba[i] = new ByteArray();
        			ba[i].append(ba2.toByteArray());
        		}
    		}


        }catch(Exception e){
            log.error("demandRom read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"demandRomData read error");
        }
        log.debug("========== demandRom Read End ===============");
        return ba;
    } 
 
    public ByteArray cummulatedDataRead(IoSession session, String grpId, String memberNumber, int nOffset, int nCount) throws MRPException
    {
        log.debug("========== cummulatedData Read Start ===============");
        
        ByteArray ba = new ByteArray();
        
        try{
		if(nCount>5)
			nCount =5;

		BYTE addr = null;
    	BYTE command = null;
    	OCTET send_data = null;
    	KAMSTRUP601_RequestDataFrame frame = null;
    	KAMSTRUP601_ReceiveDataFrame buf = null;
        byte[] temp = null;
        
        addr = new BYTE(KAMSTRUP601_DataConstants.DES_ADDR_HEAT_METER);
        command = new BYTE(KAMSTRUP601_DataConstants.CID_GETREGISTER);
        
		//get meter's Datetime

        send_data = new OCTET(DataUtil.append(new byte[]{(byte)0x01},DataUtil.get2ByteToInt(KAMSTRUP601_DataConstants.REG_DATE)));
        
		frame = new KAMSTRUP601_RequestDataFrame(null, addr, command, send_data, 0,0, null);
		buf = read(session,frame, KAMSTRUP601_DataConstants.CID_GETREGISTER, KAMSTRUP601_DataConstants.REG_DATE);
		temp = buf.encode();
		byte[] date = DataUtil.select(temp,5,4);
		
		send_data = new OCTET(DataUtil.append(new byte[]{(byte)0x01},DataUtil.get2ByteToInt(KAMSTRUP601_DataConstants.REG_CLOCK)));
		
		frame = new KAMSTRUP601_RequestDataFrame(null, addr, command, send_data, 0,0, null);
		buf = read(session,frame, KAMSTRUP601_DataConstants.CID_GETREGISTER, KAMSTRUP601_DataConstants.REG_CLOCK);
		temp = buf.encode();
		byte[] time = DataUtil.select(temp,5,4);
		
		String endDateTime = TimeUtil.getCurrentTime();
		endDateTime = TimeUtil.getPreDay(endDateTime, (nOffset-nCount));
		
		String meterDateTime = endDateTime;
		
		if(temp!=null){
			//from = TimeUtil.getPreDay(TimeUtil.getCurrentTime(),2);
			meterDateTime = getDateTime(date,time);
		}
		log.debug("meterDateTime :"+meterDateTime);
		log.debug("TimeUtil.getLongTime(endDateTime) : "+ TimeUtil.getLongTime(endDateTime));
		log.debug("TimeUtil.getLongTime(meterDateTime) : "+ TimeUtil.getLongTime(meterDateTime));
		
		if(TimeUtil.getLongTime(endDateTime)>TimeUtil.getLongTime(meterDateTime)){
			endDateTime = meterDateTime;
		}
		
	//	endDateTime = TimeUtil.getPreDay(endDateTime.substring(0, 10)+"0000", nOffset);// resolution is 60 min.
	//	endDateTime = endDateTime.substring(0, 10)+"0000";// resolution is 60 min.
		log.debug("endDateTime :"+endDateTime);
		
  //      log.debug("meter disconnect :"+ handler.disconnectMeter(session));

		int meterdisconnectRetry = 3;
		int meterdisconnectCnt = 0;
		boolean meterdisconnect = false;
		while (!meterdisconnect && meterdisconnectCnt<meterdisconnectRetry){
			meterdisconnect = handler.disconnectMeter(session);
			log.debug("meter disconnect :"+meterdisconnect);
			meterdisconnectCnt ++;
		}
		
		if(meterdisconnect){
			ByteArray ba2 = new ByteArray();
			for(int i=0; i< nCount; i++){
				String datetime2 = TimeUtil.getPreDay(""+endDateTime,i);
		        byte[] year = DataUtil.get2ByteToInt(Integer.parseInt(datetime2.substring(0,4))); 
		        byte[] date1 = new byte[]{year[0], year[1], 
		        		(byte)Integer.parseInt(datetime2.substring(4,6)), (byte)Integer.parseInt(datetime2.substring(6,8))};
		
		        //new byte[]{(byte)0x07, (byte)0xD9, (byte)0x03, (byte)0x09};
		        
		        byte[] basepulse = null;
		        basepulse= handler.getIEIUCummData(session, grpId, memberNumber, date1);
		        if(basepulse!=null && basepulse.length==21){
		        	log.debug("cummulatedData data => "+new OCTET(basepulse).toHexString());
		        	ba2.append(DataUtil.select(basepulse, 11, 8)); 
		        }
			}
			if(ba2.toByteArray().length>0){
				ba.append(new byte[]{(byte) (ba2.toByteArray().length/8)});
				ba.append(ba2.toByteArray());
			}
		}
        }catch(Exception e){
            log.error("cummulated Data read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"cummulatedData read error");
        }
        log.debug("========== cummulatedData Read End ===============");
        return ba;
    }    
        
    public ByteArray configureRead(IoSession session) throws MRPException
    {
        log.debug("========== Configure Read Start ===============");
        ByteArray ba = new ByteArray();
        
        try{
        	BYTE addr = null;
        	BYTE command = null;
        	OCTET send_data = null;
        	KAMSTRUP601_RequestDataFrame frame = null;
        	KAMSTRUP601_ReceiveDataFrame buf = null;
	        byte[] temp = null;
	        
//        	METER_ID
	        addr = new BYTE(KAMSTRUP601_DataConstants.DES_ADDR_HEAT_METER);
	        command = new BYTE(KAMSTRUP601_DataConstants.CID_GETREGISTER); 
	 //       send_data = new OCTET(DataUtil.append(new byte[]{(byte)0x01}, DataUtil.get2ByteToInt(KAMSTRUP601_DataConstants.REG_METER_ID)));
	        send_data = new OCTET(new byte[]{(byte)0x03, (byte)0x03, (byte)0xf2,(byte)0x00,(byte)0x70,(byte)0x00, (byte)0x9d});
	        frame = new KAMSTRUP601_RequestDataFrame(null, addr, command, send_data, 0,0, null);
        	buf = read(session,frame, KAMSTRUP601_DataConstants.CID_GETREGISTER, KAMSTRUP601_DataConstants.REG_METER_ID);  
      
        	temp = buf.encode();
        	
     //   	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==KAMSTRUP601_DataConstants.REG_METER_ID){
    		int srclen = KAMSTRUP601_DataConstants.LEN_METER_ID;
    		byte[] idResult = new byte[srclen];
    		System.arraycopy(temp, 5, idResult, 0, srclen);
    		ba.append(idResult);
    		ba.append(new byte[]{(byte)0x00});//status - no error
     //   		}
     //   	}else{
      //  		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get METER_ID error");
      //  	}
        	
        }catch(Exception e){
            log.error("configure read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"configure read error");
        }
        log.debug("========== Configure Read End ===============");
        return ba;
    }
    
    public ByteArray billRead(IoSession session) throws MRPException
    {
        log.debug("========== Prev Bill Read Start ===============");
        ByteArray ba = new ByteArray();
        try{
        	BYTE addr = null;
        	BYTE command = null;
        	OCTET send_data = null;
        	KAMSTRUP601_RequestDataFrame frame = null;
        	KAMSTRUP601_ReceiveDataFrame buf = null;
	        byte[] temp = null;
	        
	        int nCount =1;
	        
	        addr = new BYTE(KAMSTRUP601_DataConstants.DES_ADDR_HEAT_METER);
	        command = new BYTE(KAMSTRUP601_DataConstants.CID_GETMONTHLY);
	        
	        int[] datakinds = new int[]{KAMSTRUP601_DataConstants.REG_DATE,
	        							KAMSTRUP601_DataConstants.REG_E1, 
	        							KAMSTRUP601_DataConstants.REG_V1,
//	        							KAMSTRUP601_DataConstants.REG_T1,
//	        							KAMSTRUP601_DataConstants.REG_T2,
//	        							KAMSTRUP601_DataConstants.REG_P1,
	        							KAMSTRUP601_DataConstants.REG_M3_T1,
	        							KAMSTRUP601_DataConstants.REG_M3_T2
	        						//	,
//	        							KAMSTRUP601_DataConstants.REG_FLOW1
	        							}; 
	        
	        for(int i=0; i< datakinds.length; i++){
	        	send_data = new OCTET(DataUtil.append(DataUtil.get2ByteToInt(datakinds[i]), 
		        		new byte[]{(byte)0x00, (byte)0x01, (byte)nCount})); //for TEST
		        frame = new KAMSTRUP601_RequestDataFrame(null, addr, command, send_data, 0,0, null);
	        	buf = read(session,frame, KAMSTRUP601_DataConstants.CID_GETMONTHLY, datakinds[i]);
	        	temp = buf.encode();
	        	if(temp!=null){
	        		byte[] containCounter = new byte[temp.length +1];
	        		System.arraycopy(temp, 0, containCounter, 0, 5);
	        		containCounter[5]= (byte)nCount;
	        		System.arraycopy(temp, 5, containCounter, 6, temp.length-5);
	        		ba.append(containCounter);
	        	}
	        }

        	log.debug("Prev Billing data => "+new OCTET(ba.toByteArray()).toHexString());
        }catch(Exception e){
            log.error("prev bill read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Prev Billing data read error");
        }
        log.debug("========== Prev Bill Read End ===============");
        return ba;
    }

    public ByteArray currbillRead(IoSession session) throws MRPException
    {
    	ByteArray ba = new ByteArray();
        log.debug("========== Current Read Start ===============");
        try
        {
        	BYTE addr = null;
        	BYTE command = null;
        	OCTET send_data = null;
        	KAMSTRUP601_RequestDataFrame frame = null;
        	KAMSTRUP601_ReceiveDataFrame buf = null;
	        byte[] temp = null;

	        addr = new BYTE(KAMSTRUP601_DataConstants.DES_ADDR_HEAT_METER);
	        command = new BYTE(KAMSTRUP601_DataConstants.CID_GETREGISTER); 

	        int[] datakinds = new int[]{KAMSTRUP601_DataConstants.REG_DATE,
	        							KAMSTRUP601_DataConstants.REG_CLOCK,
	        							KAMSTRUP601_DataConstants.REG_E1, 
	        							KAMSTRUP601_DataConstants.REG_V1,
	        							KAMSTRUP601_DataConstants.REG_T1,
	        							KAMSTRUP601_DataConstants.REG_T2,
	        							KAMSTRUP601_DataConstants.REG_P1,
	        							KAMSTRUP601_DataConstants.REG_M3_T1,
	        							KAMSTRUP601_DataConstants.REG_M3_T2,
	        							KAMSTRUP601_DataConstants.REG_FLOW1
	        							}; 
	        
	        for(int i=0; i< datakinds.length; i++){
	        	send_data = new OCTET(DataUtil.append(new byte[]{(byte)0x01},DataUtil.get2ByteToInt(datakinds[i])));
		        frame = new KAMSTRUP601_RequestDataFrame(null, addr, command, send_data, 0,0, null);
	        	buf = read(session,frame, KAMSTRUP601_DataConstants.CID_GETREGISTER, datakinds[i]);
	        	temp = buf.encode();
	        	if(temp!=null){
	        		byte[] containCounter = new byte[temp.length +1];
	        		System.arraycopy(temp, 0, containCounter, 0, 5);
	        		containCounter[5]= (byte)0x01;
	        		System.arraycopy(temp, 5, containCounter, 6, temp.length-5);
	        		ba.append(containCounter);
	        	}
	        }
	        log.debug("Current data => "+new OCTET(ba.toByteArray()).toHexString());
        }
        catch (Exception e)
        {
            log.error("Current data read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Current read error");
        }
        log.debug("========== Current Read End ===============");
        return ba;
    }

    public ByteArray dailyRead(IoSession session, int nOffset, int nCount) throws MRPException
    {
        ByteArray ba = new ByteArray();
        log.debug("========== Daily Read Start ===============");
        try
        {
        	BYTE addr = null;
        	BYTE command = null;
        	OCTET send_data = null;
        	KAMSTRUP601_RequestDataFrame frame = null;
        	KAMSTRUP601_ReceiveDataFrame buf = null;
	        byte[] temp = null;

			nCount = nOffset;
		//	if(nCount> 5)
		//		nCount =5;

	        addr = new BYTE(KAMSTRUP601_DataConstants.DES_ADDR_HEAT_METER);
	        command = new BYTE(KAMSTRUP601_DataConstants.CID_GETDAILY); 

	        int[] datakinds = new int[]{KAMSTRUP601_DataConstants.REG_DATE,
	        							KAMSTRUP601_DataConstants.REG_E1, 
	        							KAMSTRUP601_DataConstants.REG_V1,
	        							KAMSTRUP601_DataConstants.REG_T1,
	        							KAMSTRUP601_DataConstants.REG_T2,
	        							KAMSTRUP601_DataConstants.REG_P1,
	        							KAMSTRUP601_DataConstants.REG_M3_T1,
	        							KAMSTRUP601_DataConstants.REG_M3_T2
	        						//	,
	        					//		KAMSTRUP601_DataConstants.REG_FLOW1
	        							}; 
	        
	        for(int i=0; i< datakinds.length; i++){
	        	send_data = new OCTET(DataUtil.append(DataUtil.get2ByteToInt(datakinds[i]), 
		        		new byte[]{(byte)0x00, (byte)0x01, (byte)nCount})); //for TEST
		        frame = new KAMSTRUP601_RequestDataFrame(null, addr, command, send_data, 0,0, null);
	        	buf = read(session,frame, KAMSTRUP601_DataConstants.CID_GETDAILY, datakinds[i]);
	        	temp = buf.encode();
	        	if(temp!=null){
	        		byte[] containCounter = new byte[temp.length +1];
	        		System.arraycopy(temp, 0, containCounter, 0, 5);
	        		containCounter[5]= (byte)nCount;
	        		System.arraycopy(temp, 5, containCounter, 6, temp.length-5);
	        		ba.append(containCounter);
	        	}
	        }

	        log.debug("Daily data => "+new OCTET(ba.toByteArray()).toHexString());
        }
        catch (Exception e)
        {
            log.error("Daily data read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Daily read error");
        }
        log.debug("========== Daily Read End ===============");
        return ba;
	}

    public ByteArray lpRead(IoSession session, String startday, String endday, int lpCycle) throws MRPException
    {
    	return null;
    }
// HourlyRead
// lpCycle : min
    public ByteArray lpRead(IoSession session, int nOffset, int nCount, int lpCycle) throws MRPException
    {
        ByteArray ba = new ByteArray();

        log.debug("========== Hourly Read Start ===============");
        try
        {
			//nCount += nOffset;
        	int nCountToGet = nCount;
			if(nCountToGet>5)
				nCountToGet =5;

			BYTE addr = null;
        	BYTE command = null;
        	OCTET send_data = null;
        	KAMSTRUP601_RequestDataFrame frame = null;
        	KAMSTRUP601_ReceiveDataFrame buf = null;
	        byte[] temp = null;
	        
	        addr = new BYTE(KAMSTRUP601_DataConstants.DES_ADDR_HEAT_METER);
	        command = new BYTE(KAMSTRUP601_DataConstants.CID_GETREGISTER);
	        
	        int[] datakinds = new int[]{
	        //		KAMSTRUP601_DataConstants.REG_DATE,
					KAMSTRUP601_DataConstants.REG_E1, 
					KAMSTRUP601_DataConstants.REG_V1,
					KAMSTRUP601_DataConstants.REG_T1,
					KAMSTRUP601_DataConstants.REG_T2,
					KAMSTRUP601_DataConstants.REG_P1
	        };

			//get meter's Datetime

	        send_data = new OCTET(DataUtil.append(new byte[]{(byte)0x01},DataUtil.get2ByteToInt(KAMSTRUP601_DataConstants.REG_DATE)));
	        
			frame = new KAMSTRUP601_RequestDataFrame(null, addr, command, send_data, 0,0, null);
			buf = read(session,frame, KAMSTRUP601_DataConstants.CID_GETREGISTER, KAMSTRUP601_DataConstants.REG_DATE);
			temp = buf.encode();
			byte[] date = DataUtil.select(temp,5,4);
			
			send_data = new OCTET(DataUtil.append(new byte[]{(byte)0x01},DataUtil.get2ByteToInt(KAMSTRUP601_DataConstants.REG_CLOCK)));
			
			frame = new KAMSTRUP601_RequestDataFrame(null, addr, command, send_data, 0,0, null);
			buf = read(session,frame, KAMSTRUP601_DataConstants.CID_GETREGISTER, KAMSTRUP601_DataConstants.REG_CLOCK);
			temp = buf.encode();
			byte[] time = DataUtil.select(temp,5,4);
			
			String endDateTime = TimeUtil.getCurrentTime();
			if(nOffset!=nCount) {
				endDateTime = TimeUtil.getCurrentTime().substring(0,8)+"000000";
			}
			
			log.debug("endDateTime :"+endDateTime);
			if(nOffset-nCountToGet>0)
				endDateTime = TimeUtil.getPreDay(endDateTime, (nOffset-nCountToGet));
			log.debug("endDateTime :"+endDateTime);
			
			String meterDateTime = endDateTime;
			
			if(temp!=null){
				//from = TimeUtil.getPreDay(TimeUtil.getCurrentTime(),2);
				meterDateTime = getDateTime(date,time);
			}
			log.debug("meterDateTime :"+meterDateTime);
			log.debug("TimeUtil.getLongTime(endDateTime) : "+ TimeUtil.getLongTime(endDateTime));
			log.debug("TimeUtil.getLongTime(meterDateTime) : "+ TimeUtil.getLongTime(meterDateTime));
			
			if(TimeUtil.getLongTime(endDateTime) > TimeUtil.getLongTime(meterDateTime)){
				endDateTime = meterDateTime;
			}
			
			if(nOffset==nCount)
				endDateTime = endDateTime.substring(0, 10)+"0000";
			else
				endDateTime = endDateTime.substring(0, 8)+"230000";// resolution is 60 min.
		//	endDateTime = endDateTime.substring(0, 10)+"0000";// resolution is 60 min.
			log.debug("endDateTime :"+endDateTime);
			
			//Hourly Data
			addr = new BYTE(KAMSTRUP601_DataConstants.DES_ADDR_TOP_MODULE);
			command = new BYTE(KAMSTRUP601_DataConstants.CID_GETHOURLY);
	//		ba.append(new byte[]{(byte)nCount});
			
			//date block
			ba.append(DataUtil.get2ByteToInt(KAMSTRUP601_DataConstants.REG_DATE));
			ba.append(new byte[]{(byte)0x30, (byte)0x04, (byte)0x00, (byte)0x01});
			ba.append(DataUtil.get4ByteToInt(Integer.parseInt(endDateTime.substring(2,8))));
			//time block
			ba.append(DataUtil.get2ByteToInt(KAMSTRUP601_DataConstants.REG_CLOCK));
			ba.append(new byte[]{(byte)0x2f, (byte)0x04, (byte)0x00, (byte)0x01});
			ba.append(DataUtil.get4ByteToInt(Integer.parseInt(endDateTime.substring(8,14))));
						
			//get data
			for(int i=0; i<datakinds.length; i++){
				log.debug("endDateTime :"+endDateTime);
				
				String datetimeTmp = endDateTime;
				for(int j=0; j< nCountToGet; j++){
					
					byte[] lpDatetimeForRequest = new byte[]{
							(byte)( Integer.parseInt(datetimeTmp.substring(2,4))),
							(byte)( Integer.parseInt(datetimeTmp.substring(4,6))),
							(byte)( Integer.parseInt(datetimeTmp.substring(6,8))),
							(byte)( Integer.parseInt(datetimeTmp.substring(8,10)))
							};
		        	send_data = new OCTET(DataUtil.append(DataUtil.get2ByteToInt(datakinds[i]), 
		        			lpDatetimeForRequest)); //for TEST
			        frame = new KAMSTRUP601_RequestDataFrame(null, addr, command, send_data, 0,0, null);
		        	buf = read(session,frame, KAMSTRUP601_DataConstants.CID_GETHOURLY, datakinds[i]);
		        	temp = buf.encode();
		        	if(temp!=null && temp.length>0){
		        		if(j==0){
			        		byte[] containCounter = new byte[temp.length];
			        		System.arraycopy(temp, 0, containCounter, 0, 5);
			        		containCounter[5]= (byte)(24*nCountToGet);
			        		System.arraycopy(temp, 6, containCounter, 6, temp.length-6);
			        		ba.append(containCounter);
		        		}else{
		        			ba.append(DataUtil.select(temp, 6, temp.length-6));
		        		}
		        	}
		        	datetimeTmp = TimeUtil.getPreDay(datetimeTmp);
		        }
			}

	        log.debug("Hourly data => "+new OCTET(ba.toByteArray()).toHexString());
        }
        catch (Exception e)
        {
            log.error("Hourly data read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Hourly read error");
        }
        log.debug("========== Hourly Read End ===============");
        return ba;
    }
    
    public String getDateTime(byte[] date, byte[]time) throws NumberFormatException, ParseException{
    	if(date.length !=4 || time.length !=4){
    		log.error("datetime error");
    		return null;
    	}
		String meterDate = ""+DataUtil.getIntTo4Byte(date);
		String meterTime = ""+DataUtil.getIntTo4Byte(time);
		
		if(meterDate.length()<6){
			for(int i=(6-meterDate.length()); i>0; i--)
				meterDate = "0"+meterDate;
		}
		if(meterTime.length()<6){
			for(int i=(6-meterTime.length()); i>0; i--)
				meterTime = "0"+meterTime;
		}
		log.debug("meterDate : "+ meterDate+", meterTime :"+ meterTime);
		
		int curryy = (Integer.parseInt(TimeUtil.getCurrentTime().substring(0,4))/100)*100;
		int year   = Integer.parseInt(meterDate.substring( 0,2));
		if(year != 0){
			year   = curryy+Integer.parseInt(meterDate.substring( 0,2));
		}
		
		return ""+year+""+meterDate.substring(2)+""+meterTime;
    }
        
    public void initProcess(IoSession session) throws MRPException
    {
    }
    
    public ByteArray instRead(IoSession session) throws MRPException
    {
        return null;
    }
    
    public ByteArray eventlogRead(IoSession session) throws MRPException
    {
    	ByteArray ba = new ByteArray();
        log.debug("========== Event Log Read Start ===============");
        try
        {
        	BYTE addr = null;
        	BYTE command = null;
        	OCTET send_data = null;
        	KAMSTRUP601_RequestDataFrame frame = null;
        	KAMSTRUP601_ReceiveDataFrame buf = null;
	        byte[] temp = null;

	        addr = new BYTE(KAMSTRUP601_DataConstants.DES_ADDR_HEAT_METER);
	        command = new BYTE(KAMSTRUP601_DataConstants.CID_GETEVENT); 

	        int[] datakinds = new int[]{KAMSTRUP601_DataConstants.REG_DATE,
	        						//	KAMSTRUP601_DataConstants.REG_CLOCK,
	        							KAMSTRUP601_DataConstants.REG_INFO
	        							}; 
	        
	        for(int i=0; i< datakinds.length; i++){
	        	send_data = new OCTET(DataUtil.append(DataUtil.get2ByteToInt(datakinds[i]), 
		        		new byte[]{(byte)0x00, (byte)0x01, (byte)0x10})); 
	        	
	        	frame = new KAMSTRUP601_RequestDataFrame(null, addr, command, send_data, 0,0, null);
	        	buf = read(session,frame, KAMSTRUP601_DataConstants.CID_GETEVENT, datakinds[i]);
	        	temp = buf.encode();
	        	if(temp!=null){
	        		byte[] containCounter = new byte[temp.length +1];
	        		System.arraycopy(temp, 0, containCounter, 0, 5);
	        		containCounter[5]= (byte)0x10;
	        		System.arraycopy(temp, 5, containCounter, 6, temp.length-5);
	        		ba.append(containCounter);
	        	}
	        }
	        log.debug("Event Log data => "+new OCTET(ba.toByteArray()).toHexString());
        }
        catch (Exception e)
        {
            log.error("Event Log Read read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Event Log Read error");
        }
        log.debug("========== Event Log Read End ===============");
        return ba;
    }
    
    public ByteArray pqRead(IoSession session) throws MRPException
    {
    	return null;
    }

    public void quit() throws MRPException
    {
        
    }
    public boolean checkCRC(byte[] src) throws MRPException
    {
        return false;
    }
    public HashMap timeCheck(IoSession session) throws MRPException
    {
        return null;
    }

    public HashMap timeSync(IoSession session, int timethreshold) throws MRPException
    {
        return null;
    }
    
    public long getSendBytes() throws MRPException{
        return this.sendBytes;// session.getWrittenBytes()
    }

    @Override
    public void setModemId(String modemId) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void setModemNumber(String modemNumber) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public CommandData execute(Socket socket, CommandData command)
            throws MRPException {
        // TODO Auto-generated method stub
        return null;
    }

}
