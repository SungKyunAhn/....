/** 
 * @(#)EDMI_Mk6N_Handler.java       1.0 04/08/08 *
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

import com.aimir.constants.CommonConstants.DataSVC;
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
import com.aimir.fep.protocol.fmp.frame.service.entry.meterLPEntry;
import com.aimir.fep.protocol.mrp.MeterProtocolHandler;
import com.aimir.fep.protocol.mrp.client.MRPClientProtocolHandler;
import com.aimir.fep.protocol.mrp.exception.MRPError;
import com.aimir.fep.protocol.mrp.exception.MRPException;
import com.aimir.fep.util.ByteArray;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

import com.aimir.util.TimeUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * @author Kang, Soyi
 */
	
public class EDMI_Mk6N_Handler extends MeterProtocolHandler { 

	private static Log log = LogFactory.getLog(EDMI_Mk6N_Handler.class);

    private String meterId = "";
    private String modemId;
            
    private String groupId = "";
    private String memberId = "";
    private String mcuSwVersion = "";

	/**
	 * Constructor.<p>
	 */
    protected long sendBytes;
    
    
	public EDMI_Mk6N_Handler() {	

	}
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
        byte[] mti = null;
        byte[] tpb = null;
        byte[] tcb = null;
        byte[] lpd = null;
        byte[] ist = null;
        byte[] eld = null;
        byte[] pqm = null;
        ByteArray ba = new ByteArray();
        String fromDate = "";
        String toDate = "";
        String cmd = command.getCmd().getValue();
        log.debug("==============EDMI_Mk6N_Handler start cmd:"+cmd+"================");
        if(cmd.equals("104.6.0")||cmd.equals("104.13.0"))
        {
            SMIValue[] smiValue = command.getSMIValue();
            
            if(smiValue != null && smiValue.length >= 2){
                int kind = ((INT)smiValue[1].getVariable()).getValue();
                
                if(smiValue.length == 4){
                	try{
	                	fromDate = ((TIMESTAMP)smiValue[2].getVariable()).getValue();
	                    toDate =   ((TIMESTAMP)smiValue[3].getVariable()).getValue();
	                    
	                    if(toDate.endsWith("000000")){
	                    	System.out.println(toDate.substring(0, 8));
	                    	toDate = toDate.substring(0, 8) + "235959";
	                    }
	                    
	                    log.debug("fromDate :"+fromDate+", toDate:"+toDate);
                	}
                    catch (Exception e)
                    {

                    }
                }else{
                    try
                    {
                    	fromDate = TimeUtil.getPreDay(TimeUtil.getCurrentTime(),2);
                    }
                    catch (ParseException e)
                    {

                    }
                }

                byte[] mcuId = new byte[17];
                System.arraycopy(command.getMcuId().getBytes(), 0, mcuId, 0, command.getMcuId().length());

                ba.append(new byte[]{'N','G','4','A','1'});
                ba.append(new byte[]{0x00});
                ba.append(mcuId);
                ba.append(new byte[]{(byte)0x86});
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{0x00,0x00,0x00,0x00});
                session.setAttribute("isActive",Boolean.FALSE);

                switch(kind){
                    case 0:
                        initProcess(session);
                        mti = configureRead(session).toByteArray();
                        tcb = currbillRead(session).toByteArray();
                        lpd = lpRead(session,fromDate,toDate,15).toByteArray();
                        ist = instRead(session).toByteArray();
                        eld = eventlogRead(session).toByteArray();
                        pqm = pqRead(session).toByteArray();
                        
                        ba.append(DataConstants.MTI);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(mti.length+6)));
                        ba.append(new byte[]{0x00});
                        ba.append(mti);

                        ba.append(DataConstants.TCB);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(tcb.length+5)));
                        ba.append(tcb);
                      
                        ba.append(DataConstants.LPD);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(lpd.length+5)));
                        ba.append(lpd);

                        ba.append(DataConstants.IST);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(ist.length+5)));
                        ba.append(ist);

                        ba.append(DataConstants.ELD);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(eld.length+5)));
                        ba.append(eld);

                        ba.append(DataConstants.PQM);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(pqm.length+5)));
                        ba.append(pqm);
                        
                        break;
                    case 1:
                        initProcess(session);
                        mti = configureRead(session).toByteArray();
                        tcb = currbillRead(session).toByteArray();
                        lpd = lpRead(session,fromDate,toDate,15).toByteArray();
                        ist = instRead(session).toByteArray();  
                        pqm = pqRead(session).toByteArray();
                        
                        ba.append(DataConstants.MTI);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(mti.length+6)));
                        ba.append(new byte[]{0x00});
                        ba.append(mti);
                        ba.append(DataConstants.TCB);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(tcb.length+5)));
                        ba.append(tcb);                        
                        ba.append(DataConstants.LPD);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(lpd.length+5)));
                        ba.append(lpd);
                        
                        ba.append(DataConstants.IST);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(ist.length+5)));
                        ba.append(ist);
                        ba.append(DataConstants.PQM);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(pqm.length+5)));
                        ba.append(pqm);
                        break;
                    case 2:
                        initProcess(session);
                        mti = configureRead(session).toByteArray();
                        tpb = billRead(session).toByteArray();
                        lpd = lpRead(session,fromDate,toDate,15).toByteArray();
                        ist = instRead(session).toByteArray();  
                        pqm = pqRead(session).toByteArray();
                        
                        ba.append(DataConstants.MTI);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(mti.length+6)));
                        ba.append(new byte[]{0x00});
                        ba.append(mti);
                        ba.append(DataConstants.TPB);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(tpb.length+5)));
                        ba.append(tpb);                        
                        ba.append(DataConstants.LPD);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(lpd.length+5)));
                        ba.append(lpd);
                        ba.append(DataConstants.IST);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(ist.length+5)));
                        ba.append(ist);
                        ba.append(DataConstants.PQM);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(pqm.length+5)));
                        ba.append(pqm);
                        break;
                    case 10:
                        initProcess(session);
                        mti = configureRead(session).toByteArray();
                        eld = eventlogRead(session).toByteArray();
                        ba.append(DataConstants.MTI);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(mti.length+6)));
                        ba.append(new byte[]{0x00});
                        ba.append(mti);
                        ba.append(DataConstants.ELD);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(eld.length+5)));
                        ba.append(eld);
                        break;
                    case 12:
                        initProcess(session);
                        mti = configureRead(session).toByteArray();
                        //readChannelConfigure(session);
                        ba.append(DataConstants.MTI);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(mti.length+6)));
                        ba.append(new byte[]{0x00});
                        ba.append(mti);
                        break;
                    default:
                        throw new MRPException(MRPError.ERR_INVALID_PARAM,"Invalid parameters");
     
                }
                exit(session);
            }else{
                throw new MRPException(MRPError.ERR_INVALID_PARAM,"Invalid parameters");
            }
            
            if(ba != null && ba.toByteArray().length > 0)
            {      
                log.debug("meterEntryData=>"+smiValue[0].getVariable().toString()+","+new OCTET(ba.toByteArray()).toHexString());
                meterLPEntry md = new meterLPEntry();
                OCTET modemId = new OCTET(8);
                modemId.setIsFixed(true);
                modemId.setValue(Hex.encode(smiValue[0].getVariable().toString()));
                md.setMlpId(modemId);
                
                // meter id
				OCTET meterserial = new OCTET(20);
				meterserial.setIsFixed(true);
				meterserial.setValue(this.meterId);
				
                md.setMlpMid(meterserial);
                md.setMlpServiceType(new BYTE(DataSVC.Electricity.getCode()));//1: electric
                

                md.setMlpType(new BYTE(ModemType.Converter.getIntValue()));
                md.setMlpVendor(new BYTE(MeterVendor.EDMI.getCode()[0].intValue()));//nuri
                
    			// Measurement Data List
    			ByteArray measData = new ByteArray();
    			
    			// Time stamp
    			try {
    				String nowTime = TimeUtil.getCurrentTime();
    				measData.append(DataFormat.encodeTime(nowTime));
    			} catch (Exception e) {
    				e.printStackTrace();
    				throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR, e
    						.toString());
    			}
    			
    			measData.append(ba.toByteArray());
    			byte[] lpData = measData.toByteArray();
    			
    			OCTET octetLpData = new OCTET();
    			octetLpData.setValue(lpData);
    			md.setMlpData(octetLpData);
                md.setMlpDataCount(new WORD(1));
                commandData = new CommandData();
                commandData.setCnt(new WORD(1));
                commandData.setTotalLength(ba.toByteArray().length);
                commandData.append(new SMIValue(new OID("10.1.0"),new OPAQUE(md)));
            }
        }

        log.debug("==============EDMI_Mk6N_Handler end ================");
        return commandData;
    }
    
    protected ByteArray readChannelConfigure(IoSession session) throws MRPException
    {
        return null;
    }

    protected EDMI_Mk6N_ReceiveDataFrame read(IoSession session,EDMI_Mk6N_RequestDataFrame frame)
    throws MRPException
    {
    	return null;
    }
    protected EDMI_Mk6N_ReceiveDataFrame read(IoSession session,EDMI_Mk6N_RequestDataFrame frame,
    											byte[] commandKind, int responseLength, int reg_num )
    throws MRPException
    {
        int sequence = 0;
        boolean isRemain = true;
        try
        {
            IoBuffer buf = null;
            EDMI_Mk6N_ReceiveDataFrame rcvFrame = new EDMI_Mk6N_ReceiveDataFrame();
            
            int retry =3;
            byte[] message = null; 
            byte[] getCmd = null;
            int cnt=0;
            while(cnt<retry){ //|| (message==null || message.length<2)){
            	log.debug("cnt :"+cnt);
            	cnt ++;
            	try{
            		
            		buf = frame.getIoBuffer();
            		
	        		int result = handler.sendMsgUseFuture(session, buf);
	        		if(result<0){
	        			throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
	        		}
	        		
		            message = (byte[])handler.getMessageFromEDMIMeter(session);
		            
		            log.debug("message length :" +message.length);
		            
		            if(message!=null && message.length>=2){
		            	if(message[1]== EDMI_Mk6N_DataConstants.STX){
			            	getCmd = get_cmd(message, message.length);
			            	if(getCmd.length>0){
			            		if(responseLength>0 && responseLength!=getCmd.length){
			            			log.debug("Received length error : length is not "+responseLength);
			            			continue;
			            		}
			            		if(getCmd[0] == EDMI_Mk6N_DataConstants.ACK){
			            			break;
			            		}
			            		if(getCmd[0] == EDMI_Mk6N_DataConstants.CAN){
			            			log.debug("Received CAN");
			            			continue;
			            		}
			            		
			            		if(getCmd[0]!= commandKind[0]){
			            			log.debug("Received COMMAND error");
			            			continue;
			            		}
			            		if(commandKind.length>1 && getCmd[1]!= commandKind[1]){
			            			log.debug("Received COMMAND 2 error");
			            			continue;
			            		}
			            		int getRegNo = 0;
			            		if(commandKind.length>1)
			            			getRegNo = DataUtil.getIntTo4Byte(DataUtil.select(getCmd, 2, 5));
			            		else
			            			getRegNo = DataUtil.getIntTo4Byte(DataUtil.select(getCmd, 1, 4));
			            		if(reg_num != getRegNo){
			            			log.debug("Register No. error");
			            			continue;
			            		}
			            		break; //Normal
			            	}else{
			            		log.debug("CRC ERROR");
			            	}
		            	}else{
		            		log.debug("STX ERROR");
		            	}
		            }else{
		            	log.debug("No Response");
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
			    case EDMI_Mk6N_DataConstants.STX:
				    len = 0;
				    crc = EDMI_Mk6N_DataConstants.CalculateCharacterCRC16((char)0, c);
			    break;
			    case EDMI_Mk6N_DataConstants.ETX:
				    if ((crc == 0)&& (len >= 2)) {
				    	len -= 2; /* remove crc characters */
				    	byte[] resultBuf = new byte[len];
				    	System.arraycopy(cmd_data, 0, resultBuf, 0, len);
				    	return(resultBuf);
				    } else if (len==0)
				    	return new byte[0];
			    break;
			    case EDMI_Mk6N_DataConstants.DLE:
			    	DLE_last = true;
			    break;
			    default:
				    if (DLE_last)
					    c &= 0xBF;
					    DLE_last = false;
				    if (len >= max_len)
				    	break;
				    crc = EDMI_Mk6N_DataConstants.CalculateCharacterCRC16(crc,c);
				    cmd_data[len++] = c;
		    }
	    }
	    return null;
    }
    
    public ByteArray configureRead(IoSession session) throws MRPException
    {
        log.debug("========== Configure Read Start ===============");
        ByteArray ba = new ByteArray();
        
        try{
        	OCTET control = null;
        	OCTET send_data = null;
	        EDMI_Mk6N_RequestDataFrame frame = null;
	        EDMI_Mk6N_ReceiveDataFrame buf = null;
	        byte[] temp = null;
	        
        	//MODEL_ID_NO
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_MODEL_ID_NO));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 0, EDMI_Mk6N_DataConstants.REGISTER_MODEL_ID_NO);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_MODEL_ID_NO){
        		int srclen = EDMI_Mk6N_DataConstants.LEN_MODEL_ID_NO;
        		if(temp.length -5 < srclen)
        			srclen =temp.length -5;
        		byte[] modelResult = new byte[EDMI_Mk6N_DataConstants.LEN_MODEL_ID_NO];
        		System.arraycopy(temp, 5, modelResult, 0, srclen);
        		ba.append(modelResult);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get MODEL_ID_NO error");
        	}
        	
//        	METER_ID
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_METER_ID));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 0, EDMI_Mk6N_DataConstants.REGISTER_METER_ID);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_METER_ID){
        		int srclen = EDMI_Mk6N_DataConstants.LEN_METER_ID;
        		if(temp.length -5 < srclen)
        			srclen =temp.length -5;
        		byte[] idResult = new byte[EDMI_Mk6N_DataConstants.LEN_METER_ID];
        		System.arraycopy(temp, 5, idResult, 0, srclen);
        		ba.append(idResult);
        		//	ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_METER_ID);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get METER_ID error");
        	}
        	
//        	EZIO_STATUS
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_EZIO_STATUS));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 6, EDMI_Mk6N_DataConstants.REGISTER_EZIO_STATUS);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_EZIO_STATUS){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_EZIO_STATUS);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get EZIO_STATUS error");
        	}
        	
//        	CURRENT_STATUS
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_CURRENT_STATUS));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 0, EDMI_Mk6N_DataConstants.REGISTER_CURRENT_STATUS);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_CURRENT_STATUS){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_CURRENT_STATUS);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURRENT_STATUS error");
        	}
        	
//        	LATCHED_STATUS
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LATCHED_STATUS));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 0, EDMI_Mk6N_DataConstants.REGISTER_LATCHED_STATUS);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_LATCHED_STATUS){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_LATCHED_STATUS);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get LATCHED_STATUS error");
        	}
        	
//        	CURR_DATE_TIME
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_CURR_DATE_TIME));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 11, EDMI_Mk6N_DataConstants.REGISTER_CURR_DATE_TIME);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_CURR_DATE_TIME){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_CURR_DATE_TIME);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURR_DATE_TIME error");
        	}
        	
//        	DST_DATE_TIME
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_DST_DATE_TIME));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 11, EDMI_Mk6N_DataConstants.REGISTER_DST_DATE_TIME);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_DST_DATE_TIME){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_DST_DATE_TIME);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get DST_DATE_TIME error");
        	}
        	
//        	DST_ACTIVE
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_DST_ACTIVE));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 6, EDMI_Mk6N_DataConstants.REGISTER_DST_ACTIVE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_DST_ACTIVE){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_DST_ACTIVE);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get DST_ACTIVE error");
        	}
        	
//        	CT_RATIO_MUL
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_CT_RATIO_MUL));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_CT_RATIO_MUL);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_CT_RATIO_MUL){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_CT_RATIO_MUL);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CT_RATIO_MUL error");
        	}
        	
//        	PT_RATIO_MUL
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PT_RATIO_MUL));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PT_RATIO_MUL);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PT_RATIO_MUL){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PT_RATIO_MUL);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PT_RATIO_MUL error");
        	}
        	
//        	CT_RATIO_DIV
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_CT_RATIO_DIV));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_CT_RATIO_DIV);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_CT_RATIO_DIV){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_CT_RATIO_DIV);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CT_RATIO_DIV error");
        	}
        	
//        	PT_RATIO_DIV
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PT_RATIO_DIV));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PT_RATIO_DIV);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PT_RATIO_DIV){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PT_RATIO_DIV);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PT_RATIO_DIV error");
        	}
        	
//        	MEASURE_METHOD
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_MEASURE_METHOD));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 6, EDMI_Mk6N_DataConstants.REGISTER_MEASURE_METHOD);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_MEASURE_METHOD){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_MEASURE_METHOD);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get MEASURE_METHOD error");
        	}
        	
//        	MEASURE_OPTION
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_MEASURE_OPTION));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 7, EDMI_Mk6N_DataConstants.REGISTER_MEASURE_OPTION);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_MEASURE_OPTION){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_MEASURE_OPTION);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get MEASURE_OPTION error");
        	}        	
        	
//        	NUM_OF_PWR_UP
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_NUM_OF_PWR_UP));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_NUM_OF_PWR_UP);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_NUM_OF_PWR_UP){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_NUM_OF_PWR_UP);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get NUM_OF_PWR_UP error");
        	}
        	
//        	LAST_DT_OUTAGE
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LAST_DT_OUTAGE));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 11, EDMI_Mk6N_DataConstants.REGISTER_LAST_DT_OUTAGE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_LAST_DT_OUTAGE){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_LAST_DT_OUTAGE);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get LAST_DT_OUTAGE error");
        	}
        	
//        	LAST_DT_RECOVERY
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LAST_DT_RECOVERY));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 11, EDMI_Mk6N_DataConstants.REGISTER_LAST_DT_RECOVERY);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_LAST_DT_RECOVERY){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_LAST_DT_RECOVERY);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get LAST_DT_RECOVERY error");
        	}
        	log.debug("configure data => "+new OCTET(ba.toByteArray()).toHexString());
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
        	OCTET control = null;
        	OCTET send_data = null;
	        EDMI_Mk6N_RequestDataFrame frame = null;
	        EDMI_Mk6N_ReceiveDataFrame buf = null;
	        byte[] temp = null;
	        
        	//LAST_DT_DEMAND_RESET
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LAST_DT_DEMAND_RESET));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 11, EDMI_Mk6N_DataConstants.REGISTER_LAST_DT_DEMAND_RESET);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_LAST_DT_DEMAND_RESET){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_LAST_DT_DEMAND_RESET);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get LAST_DT_DEMAND_RESET error");
        	}
        	
        	//CURRENT_SEASON
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_CURRENT_SEASON));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 6, EDMI_Mk6N_DataConstants.REGISTER_CURRENT_SEASON);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_CURRENT_SEASON){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_CURRENT_SEASON);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURRENT_SEASON error");
        	}
        	
//        	NBR_DEMAND_RESETS
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_NBR_DEMAND_RESETS));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 6, EDMI_Mk6N_DataConstants.REGISTER_NBR_DEMAND_RESETS);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_NBR_DEMAND_RESETS){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_NBR_DEMAND_RESETS);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get NBR_DEMAND_RESETS error");
        	}
        	
//        	NBR_DM_CH
        //	ba.append(new byte[]{EDMI_Mk6N_DataConstants.NBR_DM_CH});
        	
//        	NBR_DM_CH, DEMAND
        	
        	int NBR_DM_CH =0;
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_ARRAY); 
	        byte[] reg_num = DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_DEMAND);
	        byte[] num_reg = new byte[]{(byte) EDMI_Mk6N_DataConstants.MAX_NBR_DM_CH};
	        send_data = new OCTET(DataUtil.arrayAppend(reg_num, 0, reg_num.length, num_reg, 0, num_reg.length));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_ARRAY, 
        				6+EDMI_Mk6N_DataConstants.MAX_NBR_DM_CH*4, EDMI_Mk6N_DataConstants.REGISTER_DEMAND);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_DEMAND){
        		NBR_DM_CH =0;
        		ByteArray demandMap = new ByteArray();
        		for(int k=0; k<EDMI_Mk6N_DataConstants.MAX_NBR_DM_CH; k++){
        			if(temp[9+(EDMI_Mk6N_DataConstants.LEN_DEMAND*k)]!= (byte)0xFF){ //checking the every last byte.
        				NBR_DM_CH++;
        				demandMap.append(DataUtil.select(temp, 6+(EDMI_Mk6N_DataConstants.LEN_DEMAND*k), EDMI_Mk6N_DataConstants.LEN_DEMAND));
        			}
        		}
        		ba.append(new byte[]{(byte)NBR_DM_CH});  //NBR_DM_CH
        		ba.append(demandMap.toByteArray());	 //REG_DEMAND
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get REG_DEMAND error");
        	}

        	// GET BILLING DATA
        	for(int j=0; j<EDMI_Mk6N_DataConstants.NBR_RATE; j++){
        		if(j==EDMI_Mk6N_DataConstants.NBR_RATE-1){
        			j=9;//the last one is Total rate
        		}
	//        	PREV_BILL_ENERGY
		        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
		        ByteArray reg_num_arr = new ByteArray();
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT));
		        for(int i=0; i< NBR_DM_CH ; i++){
		        	reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PREV_BILL_ENERGY_RATE+ (0x00000100*i) +j));
		        }
		        send_data = new OCTET(reg_num_arr.toByteArray());
		        
		        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED,
	        			5+8*NBR_DM_CH, EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT);  
	        	temp = buf.encode();
	        	
	        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT){
	        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_BILL_ENERGY*NBR_DM_CH);
	        	}else{
	        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PREV_BILL_ENERGY error");
	        	}
	//        	PREV_BILL_MAX_DM
		        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
		        reg_num_arr = new ByteArray();
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT));
		        for(int i=0; i<NBR_DM_CH; i++){
		        	reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PREV_BILL_MAX_DM_RATE+ (0x00000100*i)+j));
		        }
		        send_data = new OCTET(reg_num_arr.toByteArray());
		        
		        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 5+4*NBR_DM_CH, EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT);  
	        	temp = buf.encode();
	        	
	        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT){
	        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_BILL_MAX_DM*NBR_DM_CH);
	        	}else{
	        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PREV_BILL_MAX_DM_RATE error");
	        	}
	        	
	//        	REGISTER_PREV_BILL_MAX_DM_TIME
		        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
		        reg_num_arr = new ByteArray();
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT));
		        for(int i=0; i<NBR_DM_CH; i++){
		        	reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PREV_BILL_MAX_DM_TIME_RATE+ (0x00000100*i)+j));
		        }
		        send_data = new OCTET(reg_num_arr.toByteArray());
		        
		        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 5+6*NBR_DM_CH, EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT);  
	        	temp = buf.encode();
	        	
	        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT){
	        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_BILL_MAX_DM_TIME*NBR_DM_CH);
	        	}else{
	        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PREV_BILL_MAX_DM_TIME_RATE error");
	        	}
        	}
        	log.debug("Billing data => "+new OCTET(ba.toByteArray()).toHexString());
        }catch(Exception e){
            log.error("prev bill read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Billing data read error");
        }
        log.debug("========== Prev Bill Read End ===============");
        return ba;
    }

    public ByteArray currbillRead(IoSession session) throws MRPException
    {
        log.debug("========== Current Bill Read Start ===============");
        ByteArray ba = new ByteArray();
        try{
        	OCTET control = null;
        	OCTET send_data = null;
	        EDMI_Mk6N_RequestDataFrame frame = null;
	        EDMI_Mk6N_ReceiveDataFrame buf = null;
	        byte[] temp = null;
	        
        	//CURRENT_SEASON
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_CURRENT_SEASON));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 6, EDMI_Mk6N_DataConstants.REGISTER_CURRENT_SEASON);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_CURRENT_SEASON){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_CURRENT_SEASON);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURRENT_SEASON error");
        	}
     
        	//NBR_DM_CH
        	//ba.append(new byte[]{EDMI_Mk6N_DataConstants.NBR_DM_CH});
        	
//        	DEMAND
        	int NBR_DM_CH =0;
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_ARRAY); 
	        byte[] reg_num = DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_DEMAND);
	        byte[] num_reg = new byte[]{(byte) EDMI_Mk6N_DataConstants.MAX_NBR_DM_CH};
	        send_data = new OCTET(DataUtil.arrayAppend(reg_num, 0, reg_num.length, num_reg, 0, num_reg.length));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_ARRAY, 
        			6+EDMI_Mk6N_DataConstants.MAX_NBR_DM_CH*4, EDMI_Mk6N_DataConstants.REGISTER_DEMAND);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_DEMAND){
        		NBR_DM_CH =0;
        		ByteArray demandMap = new ByteArray();
        		for(int k=0; k<EDMI_Mk6N_DataConstants.MAX_NBR_DM_CH; k++){
        			if(temp[9+(EDMI_Mk6N_DataConstants.LEN_DEMAND*k)]!= (byte)0xFF){ //checking the every last byte.
        				NBR_DM_CH++;
        				demandMap.append(DataUtil.select(temp, 6+(EDMI_Mk6N_DataConstants.LEN_DEMAND*k), EDMI_Mk6N_DataConstants.LEN_DEMAND));
        			}
        		}
        		ba.append(new byte[]{(byte)NBR_DM_CH});  //NBR_DM_CH
        		ba.append(demandMap.toByteArray());	 //REG_DEMAND
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get REG_DEMAND error");
        	}
        	log.debug("NBR_DM_CH :"+NBR_DM_CH);
        	
        	// GET BILLING DATA
        	for(int j=0; j<EDMI_Mk6N_DataConstants.NBR_RATE; j++){
        		if(EDMI_Mk6N_DataConstants.NBR_RATE<10 && j==EDMI_Mk6N_DataConstants.NBR_RATE-1){
        			j=9;//last one is Total rate
        		}
	//        	CURR_BILL_ENERGY
		        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
		        ByteArray reg_num_arr = new ByteArray();
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT));
		        for(int i=0; i<NBR_DM_CH; i++){
		        //	log.debug(EDMI_Mk6N_DataConstants.REGISTER_CURR_BILL_ENERGY_RATE+ (0x00000100*i) +j);
		       // 	log.debug(new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_CURR_BILL_ENERGY_RATE+ (0x00000100*i) +j)).toHexString());
		        	reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_CURR_BILL_ENERGY_RATE+ (0x00000100*i) +j));
		        }
		        
		        send_data = new OCTET(reg_num_arr.toByteArray());
		        
		        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	        	buf = read(session,frame,  EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 5+8*NBR_DM_CH, EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT);  
	        	temp = buf.encode();
	        	
	        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT){
	        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_BILL_ENERGY*NBR_DM_CH);
	        	}else{
	        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURR_BILL_ENERGY error");
	        	}
	//        	CURR_BILL_MAX_DM 
		        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
		        reg_num_arr = new ByteArray();
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT));
		        for(int i=0; i<NBR_DM_CH; i++){
		        	reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_CURR_BILL_MAX_DM_RATE+ (0x00000100*i)+j));
		        }
		        send_data = new OCTET(reg_num_arr.toByteArray());
		        
		        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	        	buf = read(session,frame,  EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 5+4*NBR_DM_CH, EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT);  
	        	temp = buf.encode();
	        	
	        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT){
	        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_BILL_MAX_DM*NBR_DM_CH);
	        	}else{
	        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURR_BILL_MAX_DM_RATE error");
	        	}
	        	
	//        	REGISTER_CURR_BILL_MAX_DM_TIME
		        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
		        reg_num_arr = new ByteArray();
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT));
		        for(int i=0; i<NBR_DM_CH; i++){
		        	reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_CURR_BILL_MAX_DM_TIME_RATE+ (0x00000100*i)+j));
		        }
		        send_data = new OCTET(reg_num_arr.toByteArray());
		        
		        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	        	buf = read(session,frame,  EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 5+6*NBR_DM_CH, EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT);  
	        	temp = buf.encode();
	        	
	        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT){
	        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_BILL_MAX_DM_TIME*NBR_DM_CH);
	        	}else{
	        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURR_BILL_MAX_DM_TIME_RATE error");
	        	}
        	}
        	log.debug("Current Billing data => "+new OCTET(ba.toByteArray()).toHexString());
        }catch(Exception e){
            log.error("curr bill read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Current billing data read error");
        }
        log.debug("========== Current Bill Read End ===============");
        return ba;
    }

    public ByteArray lpRead(IoSession session, String startday, String endday, int lpCycle) throws MRPException
    {
        log.debug("========== LoadProfile Read Start ===============");
        ByteArray ba = new ByteArray();

        try
        {
        	OCTET control = null;
        	OCTET send_data = null;
	        EDMI_Mk6N_RequestDataFrame frame = null;
	        EDMI_Mk6N_ReceiveDataFrame buf = null;
	        byte[] temp = null;
	        
	        int INTERVAL_TIME=15;
	        int NBR_LP_CH =0;
	        int NBR_LP_ENTRIES =0;
	        int STORED_TOTAL_LP =0;
	        int start_record =0;
	        int ADDR_LP_OFFSET =0;
	        int RECORD_SIZE =0;
	        String STORED_START_TIME = null;
	        
//	        INTERVAL_TIME
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_INTERVAL_TIME));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_INTERVAL_TIME);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_INTERVAL_TIME){
        		byte[] intervaltime_by = DataUtil.select(temp, 5, EDMI_Mk6N_DataConstants.LEN_INTERVAL_TIME);
        		INTERVAL_TIME = DataUtil.getIntTo4Byte(intervaltime_by)/60;
        		ba.append(DataUtil.getReverseBytes(intervaltime_by));
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get INTERVAL_TIME error");
        	}
        	log.debug("INTERVAL_TIME :"+INTERVAL_TIME);
        	
//  	    NBR_LP_CH
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_NBR_LP_CH));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 6, EDMI_Mk6N_DataConstants.REGISTER_NBR_LP_CH);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_NBR_LP_CH){
        		NBR_LP_CH = temp[5];
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_NBR_LP_CH);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get NBR_LP_CH error");
        	}
        	log.debug("NBR_LP_CH :"+NBR_LP_CH);
        	
//  	    ENTRY_WIDTH
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_ENTRY_WIDTH));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 7, EDMI_Mk6N_DataConstants.REGISTER_ENTRY_WIDTH);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_ENTRY_WIDTH){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_ENTRY_WIDTH);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get ENTRY_WIDTH error");
        	}
        	
//  	    WIDEST_CHANNEL
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_WIDEST_CHANNEL));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 7, EDMI_Mk6N_DataConstants.REGISTER_WIDEST_CHANNEL);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_WIDEST_CHANNEL){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_WIDEST_CHANNEL);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get WIDEST_CHANNEL error");
        	}
	        
//  	    STORED_START_TIME
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_STORED_START_TIME));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 11, EDMI_Mk6N_DataConstants.REGISTER_STORED_START_TIME);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_STORED_START_TIME){
        		STORED_START_TIME = getYyyymmddhhmmss(DataUtil.select(temp, 5, EDMI_Mk6N_DataConstants.LEN_STORED_START_TIME));
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_STORED_START_TIME);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get STORED_START_TIME error");
        	}
        	
//  	    STORED_TOTAL_LP
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_STORED_TOTAL_LP));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_STORED_TOTAL_LP);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_STORED_TOTAL_LP){
        		STORED_TOTAL_LP  = DataUtil.getIntTo4Byte(DataUtil.select(temp, 5, EDMI_Mk6N_DataConstants.LEN_STORED_TOTAL_LP));
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_STORED_TOTAL_LP);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get STORED_TOTAL_LP error");
        	}
	        log.debug("STORED_TOTAL_LP :"+STORED_TOTAL_LP);
//	        LP_CH_STATUS_CONFIG
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        ByteArray reg_num_arr = new ByteArray();
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT));
	        
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LP_CH_REG));
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LP_CH_SIZE));
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LP_CH_TYPE));
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LP_CH_UNIT));
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LP_CH_RECORD_OFFSET));
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LP_CH_SCALING));
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LP_CH_SCALING_FACTOR));

	        send_data = new OCTET(reg_num_arr.toByteArray());
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED,
        			5+EDMI_Mk6N_DataConstants.LEN_LP_CH_STATUS_CONFIG, EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_LP_CH_STATUS_CONFIG);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PREV_BILL_ENERGY error");
        	}
        	
//	        LP_CH_DATA_CONFIG
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        reg_num_arr = new ByteArray();
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT));
	        
	        for(int k=1; k<=NBR_LP_CH; k++){
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LP_CH_REG  + k));
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LP_CH_SIZE + k));
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LP_CH_TYPE + k));
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LP_CH_UNIT + k));
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LP_CH_RECORD_OFFSET + k));
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LP_CH_SCALING + k));
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_LP_CH_SCALING_FACTOR + k));
	        }
	        
	        send_data = new OCTET(reg_num_arr.toByteArray());
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session, frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 
        			5+EDMI_Mk6N_DataConstants.LEN_LP_CH_STATUS_CONFIG*NBR_LP_CH, EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_LP_CH_STATUS_CONFIG*NBR_LP_CH);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PREV_BILL_ENERGY error");
        	}
        	
        	//REGISTER_CH_NAME
        	for(int k=1; k<=NBR_LP_CH; k++){
	        	control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
		        reg_num_arr = new ByteArray();
	        	reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_CH_NAME + k));
	        	
	        	send_data = new OCTET(reg_num_arr.toByteArray());
		        
		        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	        	buf = read(session, frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 
	        			0, EDMI_Mk6N_DataConstants.REGISTER_CH_NAME + k);  
	        	temp = buf.encode();
	        	
	        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_CH_NAME + k){
	        		byte[] chname = new byte[EDMI_Mk6N_DataConstants.LEN_CH_NAME]; 
	        		System.arraycopy(temp, 5, chname, 0, temp.length-5);
	        		ba.append(chname);
	        	}else{
	        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get REGISTER_CH_NAME error");
	        	}
        	}
//  	    LP_FILE_ACCESS_POINT
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_ACCESS_FILE_INFO); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.LP_FILE_ACCESS_POINT));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_ACCESS_FILE_INFO, 0, EDMI_Mk6N_DataConstants.LP_FILE_ACCESS_POINT);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 2, 5))==EDMI_Mk6N_DataConstants.LP_FILE_ACCESS_POINT){
        		start_record  = DataUtil.getIntTo4Byte(DataUtil.select(temp, 6, EDMI_Mk6N_DataConstants.LEN_FI_START_RECORD));
        		RECORD_SIZE = DataUtil.getIntTo2Byte(DataUtil.select(temp, 6+EDMI_Mk6N_DataConstants.OFS_RECORD_SIZE,
        																EDMI_Mk6N_DataConstants.LEN_RECORD_SIZE ));
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get LP_FILE_ACCESS_POINT error");
        	}
        	//Finding
        //	INTERVAL_TIME =  1;
        	
        	log.debug("STORED_START_TIME :"+STORED_START_TIME);
        	log.debug("startday :"+startday);
        	log.debug("endday :"+endday);
        	
        	long stored_start_time = TimeUtil.getLongTime(STORED_START_TIME);

        	if(stored_start_time%1000>0)
        		stored_start_time -= stored_start_time%1000;
        	
        	long startday_long = TimeUtil.getLongTime(startday);
        	long endday_long = TimeUtil.getLongTime(endday);
        	
        	if(startday_long%1000>0)
        		startday_long -= startday_long%1000;
        	if(endday_long%1000>0)
        		endday_long -= endday_long%1000;
        	
        	log.debug("startday_long ="+startday_long);
        	log.debug("endday_long ="+endday_long);
        	
        	/*
        	long day2 = 2*12*60*60*1000;
        	if ((endday_long - startday_long) > day2){
        		startday_long = endday_long - day2;
        	}
        	*/
        	
        	long interval_long = INTERVAL_TIME*60000;
        	long stored_last_time = stored_start_time +(interval_long* (STORED_TOTAL_LP-1));
        	
        	
        	if(startday_long<stored_start_time)
        		startday_long = stored_start_time;
        	if(endday_long>stored_last_time)
        		endday_long = stored_last_time;
        	
        	log.debug("startday_long 2 ="+startday_long);
        	log.debug("endday_long 2 ="+endday_long);
        	
        	
        	int endoffset 	= (int)((endday_long - stored_start_time)/interval_long);
        	int startoffset = (int)((startday_long - stored_start_time)/interval_long);
        	
        	log.debug("interval_long ="+interval_long);
        	log.debug("stored_start_time ="+stored_start_time);
        	log.debug("stored_last_time ="+stored_last_time);
        	log.debug("endoffset ="+endoffset);
        	log.debug("startoffset ="+startoffset);
        	
        	if (endoffset> (STORED_TOTAL_LP-1))
        		endoffset = STORED_TOTAL_LP-1;
        	
        	NBR_LP_ENTRIES = endoffset - startoffset+1;
        	ADDR_LP_OFFSET = startoffset;

        	log.debug("NBR_LP_ENTRIES == "+NBR_LP_ENTRIES);
        	int offset =0;
        	
        	ba.append(DataUtil.get2ByteToInt(ADDR_LP_OFFSET));
        	ba.append(DataUtil.get2ByteToInt(NBR_LP_ENTRIES));
        	
//  	    GET_LP
	        int cntRead = NBR_LP_ENTRIES/EDMI_Mk6N_DataConstants.EDMI_LP_READ_COUNT;
	        if (NBR_LP_ENTRIES%EDMI_Mk6N_DataConstants.EDMI_LP_READ_COUNT >0)
	        	cntRead ++;
	        
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_ACCESS_FILE_READ); 
	        for(int k=0; k<cntRead; k++){
	        	ByteArray lp_reg_num = new ByteArray();
		        lp_reg_num.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.LP_FILE_ACCESS_POINT));
		        lp_reg_num.append(DataUtil.get4ByteToInt(ADDR_LP_OFFSET+(k*EDMI_Mk6N_DataConstants.EDMI_LP_READ_COUNT)));
		        int readcount = EDMI_Mk6N_DataConstants.EDMI_LP_READ_COUNT;
		        if(k== (cntRead-1))
		        	readcount =NBR_LP_ENTRIES-(k*EDMI_Mk6N_DataConstants.EDMI_LP_READ_COUNT);
		        	
		        lp_reg_num.append(DataUtil.get2ByteToInt(readcount));
		        lp_reg_num.append(DataUtil.get2ByteToInt(offset));
		        lp_reg_num.append(DataUtil.get2ByteToInt(RECORD_SIZE));
		        
		        log.debug("RECORD_SIZE"+RECORD_SIZE);
		        log.debug("readcount"+readcount);
		        log.debug("response length :"+ (16+RECORD_SIZE*readcount));
		        send_data = new OCTET(lp_reg_num.toByteArray());
		        
		        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_ACCESS_FILE_READ,
	        			16+RECORD_SIZE*readcount, EDMI_Mk6N_DataConstants.LP_FILE_ACCESS_POINT);  
	        	temp = buf.encode();
	        	
	        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 2, 5))==EDMI_Mk6N_DataConstants.LP_FILE_ACCESS_POINT){
	        		ba.append(temp, 16, temp.length -16);
	        	}else{
	        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get Loadprofile read data error");
	        	}
	        }
	        log.debug("Loadprofile data => "+new OCTET(ba.toByteArray()).toHexString());
        }
        catch (Exception e)
        {
            log.error("lp read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Loadprofile read data error");
        } 
        log.debug("========== LoadProfile Read End ===============");
        return ba;
    }
    
    private String getYyyymmddhhmmss(byte[] b)
	throws Exception {
		
		int idx = 0;
		
		int dd = (b[idx++] & 0xff);
		int mm = b[idx++] & 0xff;
		int year = (b[idx++] & 0xff)+2000;
		int hh = b[idx++] & 0xff;
		int MM = b[idx++] & 0xff;
		int ss = b[idx++] & 0xff;
		
		StringBuffer ret = new StringBuffer();
		
		ret.append(frontAppendNStr('0',Integer.toString(year),4));
		ret.append(frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(frontAppendNStr('0',Integer.toString(MM),2));
		ret.append(frontAppendNStr('0',Integer.toString(ss),2));
		
		return ret.toString();
    }
    
    /**
	 * @param append source of String
	 * @param str	to append  
	 * @param length
	 * @return
	 */
	public String frontAppendNStr(char append, String str, int length)
	{
		StringBuffer b = new StringBuffer("");

		try {
			if(str.length() < length)
			{
			   for(int i = 0; i < length-str.length() ; i++)
				   b.append(append);
			   b.append(str);
			}
			else
			{
				b.append(str);
			}
		} catch(Exception e) {
			log.error("frontAppendNStr : " +e.getMessage());
		}
		return b.toString();
	}
	
    public ByteArray instRead(IoSession session) throws MRPException
    {
        log.debug("========== Instrument Read Start ===============");
        ByteArray ba = new ByteArray();

        try{
//        	REGISTER_ANGLE_VTA_B
        	OCTET control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
        	OCTET send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_ANGLE_VTA_B));
	        
        	EDMI_Mk6N_RequestDataFrame frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	EDMI_Mk6N_ReceiveDataFrame buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 
        										9, EDMI_Mk6N_DataConstants.REGISTER_ANGLE_VTA_B);  
        	byte[] temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_ANGLE_VTA_B){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get ANGLE_VTA_B error");
        	}
	        
        	//ANGLE_VTA_C
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_ANGLE_VTA_C));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_ANGLE_VTA_C);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_ANGLE_VTA_C){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get ANGLE_VTA_C error");
        	}
	        
        	//FREQUENCY
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_FREQUENCY));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_FREQUENCY);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_FREQUENCY){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get FREQUENCY error");
        	}
        	//PH_A_VOLTAGE
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_A_VOLTAGE));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_A_VOLTAGE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_A_VOLTAGE){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_VOLTAGE error");
        	}
//        	PH_B_VOLTAGE
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_B_VOLTAGE));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_B_VOLTAGE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_B_VOLTAGE){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_VOLTAGE error");
        	}
//        	PH_C_VOLTAGE
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_C_VOLTAGE));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_C_VOLTAGE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_C_VOLTAGE){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_VOLTAGE error");
        	}
//        	PH_A_CURRENT
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_A_CURRENT));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_A_CURRENT);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_A_CURRENT){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_CURRENT error");
        	}
//        	PH_B_CURRENT
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_B_CURRENT));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_B_CURRENT);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_B_CURRENT){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_CURRENT error");
        	}
//        	PH_C_CURRENT
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_C_CURRENT));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_C_CURRENT);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_C_CURRENT){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_CURRENT error");
        	}
//        	PH_A_ANGLE
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_A_ANGLE));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_A_ANGLE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_A_ANGLE){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_ANGLE error");
        	}
//        	PH_B_ANGLE
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_B_ANGLE));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_B_ANGLE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_B_ANGLE){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_ANGLE error");
        	}
//        	PH_C_ANGLE
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_C_ANGLE));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_C_ANGLE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_C_ANGLE){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_ANGLE error");
        	}
//        	PH_A_WAIT
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_A_WATTS));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_A_WATTS);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_A_WATTS){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_WAIT error");
        	}
//        	PH_B_WAIT
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_B_WATTS));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_B_WATTS);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_B_WATTS){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_WAIT error");
        	}
//        	PH_C_WAIT
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_C_WATTS));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_C_WATTS);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_C_WATTS){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_WAIT error");
        	}
//        	PH_A_VAR
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_A_VAR));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_A_VAR);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_A_VAR){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_VAR error");
        	}
//        	PH_B_VAR
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_B_VAR));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_B_VAR);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_B_VAR){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_VAR error");
        	}
//        	PH_C_VAR
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_C_VAR));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_C_VAR);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_C_VAR){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_VAR error");
        	}
//        	PH_A_VA
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_A_VA));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_A_VA);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_A_VA){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_VA error");
        	}
//        	PH_B_VA
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_B_VA));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_B_VA);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_B_VA){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_VA error");
        	}
//        	PH_C_VA
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_C_VA));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_C_VA);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_C_VA){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_VA error");
        	}
        	log.debug("Instrument data => "+new OCTET(ba.toByteArray()).toHexString());
        }catch(Exception e){
            log.error("instrument read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Instrument read data error");
        }
        log.debug("========== Instrument Read End ===============");
        return ba;
    }
    
    public ByteArray eventlogRead(IoSession session) throws MRPException
    {
        log.debug("========== Event Log Read Start ===============");
        ByteArray ba = new ByteArray();
        try{
        	OCTET control = null;
        	OCTET send_data = null;
	        EDMI_Mk6N_RequestDataFrame frame = null;
	        EDMI_Mk6N_ReceiveDataFrame buf = null;
	        byte[] temp = null;
	        
	        int NBR_EVT_CH =0;
	        int start_record  =0;
	        int RECORD_SIZE =0;
	        int NBR_EVT_ENTRIES =0;
	        int STORED_TOTAL_EVT =0;
	        
        	//NBR_EVT_CH
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_NBR_EVT_CH));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 6, EDMI_Mk6N_DataConstants.REGISTER_NBR_EVT_CH);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_NBR_EVT_CH){
        		NBR_EVT_CH = temp[5];
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_NBR_EVT_CH);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURRENT_SEASON error");
        	}
        	
//	        EV_CH_STATUS_CONFIG
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        ByteArray reg_num_arr = new ByteArray();
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT));
	        
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_EV_CH_REG));
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_EV_CH_SIZE));
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_EV_CH_TYPE));
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_EV_CH_UNIT));
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_EV_CH_RECORD_OFFSET));
	        
	        send_data = new OCTET(reg_num_arr.toByteArray());
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 
        			EDMI_Mk6N_DataConstants.LEN_EV_CH_STATUS_CONFIG, EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_EV_CH_STATUS_CONFIG);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PREV_BILL_ENERGY error");
        	}
        	
//	        EV_CH_DATA_CONFIG
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        reg_num_arr = new ByteArray();
	        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT));
	        
	        for(int k=1; k<=NBR_EVT_CH; k++){
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_EV_CH_REG  + k));
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_EV_CH_SIZE  + k));
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_EV_CH_TYPE  + k));
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_EV_CH_UNIT  + k));
		        reg_num_arr.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_EV_CH_RECORD_OFFSET  + k));
	        }
	        
	        send_data = new OCTET(reg_num_arr.toByteArray());
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session, frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED,
        			5+EDMI_Mk6N_DataConstants.LEN_EV_CH_STATUS_CONFIG*NBR_EVT_CH, EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.CMD_MULT_READ_EXT){
        		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_EV_CH_STATUS_CONFIG*NBR_EVT_CH);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PREV_BILL_ENERGY error");
        	}
        	
//  	    EV_FILE_ACCESS_POINT
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_ACCESS_FILE_INFO); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.EV_FILE_ACCESS_POINT));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_ACCESS_FILE_INFO, 0, EDMI_Mk6N_DataConstants.EV_FILE_ACCESS_POINT);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 2, 5))==EDMI_Mk6N_DataConstants.EV_FILE_ACCESS_POINT){
        		start_record  = DataUtil.getIntTo4Byte(DataUtil.select(temp, 6, EDMI_Mk6N_DataConstants.LEN_FI_START_RECORD));
        		STORED_TOTAL_EVT = DataUtil.getIntTo4Byte(DataUtil.select(temp, 6+ EDMI_Mk6N_DataConstants.OFS_FI_NBR_OF_RECORD,
						EDMI_Mk6N_DataConstants.LEN_FI_NBR_OF_RECORD ));
        		RECORD_SIZE = DataUtil.getIntTo2Byte(DataUtil.select(temp, 6+ EDMI_Mk6N_DataConstants.OFS_RECORD_SIZE,
        																EDMI_Mk6N_DataConstants.LEN_RECORD_SIZE ));
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get EV_FILE_ACCESS_POINT error");
        	}

        	log.debug("start_record :"+new OCTET(start_record).toHexString());
        	log.debug("STORED_TOTAL_EVT :"+STORED_TOTAL_EVT);
        	int requestEvtCount =EDMI_Mk6N_DataConstants.EDMI_EVT_MAX;
        	if(STORED_TOTAL_EVT<requestEvtCount){
        		requestEvtCount = STORED_TOTAL_EVT;
        	}else{
        		start_record += STORED_TOTAL_EVT - requestEvtCount;
        	}
        	log.debug("start_record :"+new OCTET(start_record).toHexString());
//  	    GET_EVT_LOG
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_ACCESS_FILE_READ); 
	        ByteArray lp_reg_num = new ByteArray();
	        lp_reg_num.append(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.EV_FILE_ACCESS_POINT));
	        lp_reg_num.append(DataUtil.get4ByteToInt(start_record));
	        lp_reg_num.append(DataUtil.get2ByteToInt(requestEvtCount));
	        lp_reg_num.append(DataUtil.get2ByteToInt(0));
	        lp_reg_num.append(DataUtil.get2ByteToInt(RECORD_SIZE));
	        
	        send_data = new OCTET(lp_reg_num.toByteArray());
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_ACCESS_FILE_READ, 
        			16+RECORD_SIZE*requestEvtCount, EDMI_Mk6N_DataConstants.EV_FILE_ACCESS_POINT);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 2, 5))==EDMI_Mk6N_DataConstants.EV_FILE_ACCESS_POINT){
        		NBR_EVT_ENTRIES = DataUtil.getIntTo2Byte(DataUtil.select(temp, 6+EDMI_Mk6N_DataConstants.LEN_FI_START_RECORD, 
        															EDMI_Mk6N_DataConstants.LEN_FI_NBR_OF_RECORD ));
        		log.debug("Actual Number of event : "+NBR_EVT_ENTRIES);
        		ba.append(temp, 6+EDMI_Mk6N_DataConstants.LEN_FI_START_RECORD, 
        						EDMI_Mk6N_DataConstants.LEN_NBR_EVT_ENTRIES );
        		ba.append(temp, 16, temp.length -16);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get GET_EVT_LOG error");
        	}
        	log.debug("Eventlog data => "+new OCTET(ba.toByteArray()).toHexString());
        }catch(Exception e){
            log.error("event log read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Eventlog data read error");
        }
        log.debug("========== Event Log Read End ===============");
        return ba;
    }
    
    public ByteArray pqRead(IoSession session) throws MRPException
    {
    	log.debug("========== Power Quality Read Start ===============");
        ByteArray ba = new ByteArray();
        try{
	    	OCTET control = null;
	    	OCTET send_data = null;
	        EDMI_Mk6N_RequestDataFrame frame = null;
	        EDMI_Mk6N_ReceiveDataFrame buf = null;
	        byte[] temp = null;
	        
	    	//PH_A_FUND_VOLTAGE
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_A_FUND_VOLTAGE));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_A_FUND_VOLTAGE);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_A_FUND_VOLTAGE){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_FUND_VOLTAGE error");
	    	}
	    	//PH_B_FUND_VOLTAGE
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_B_FUND_VOLTAGE));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_B_FUND_VOLTAGE);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_B_FUND_VOLTAGE){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_FUND_VOLTAGE error");
	    	}
	    	//PH_C_FUND_VOLTAGE
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_C_FUND_VOLTAGE));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_C_FUND_VOLTAGE);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_C_FUND_VOLTAGE){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_FUND_VOLTAGE error");
	    	}
	    	
	    	//PH_A_VOLTAGE_PQM
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_A_VOLTAGE_PQM));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_A_VOLTAGE_PQM);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_A_VOLTAGE_PQM){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_VOLTAGE_PQM error");
	    	}
	    	
	    	//PH_B_VOLTAGE_PQM
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_B_VOLTAGE_PQM));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_B_VOLTAGE_PQM);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_B_VOLTAGE_PQM){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_VOLTAGE_PQM error");
	    	}
	    	
	    	//PH_C_VOLTAGE_PQM
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_C_VOLTAGE_PQM));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_C_VOLTAGE_PQM);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_C_VOLTAGE_PQM){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_VOLTAGE_PQM error");
	    	}
	
	    	//VOLTAGE_Z_SEQ
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_VOLTAGE_Z_SEQ));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_VOLTAGE_Z_SEQ);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_VOLTAGE_Z_SEQ){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get VOLTAGE_Z_SEQ error");
	    	}
	    	//VOLTAGE_P_SEQ
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_VOLTAGE_P_SEQ));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_VOLTAGE_P_SEQ);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_VOLTAGE_P_SEQ){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get VOLTAGE_P_SEQ error");
	    	}
	    	//VOLTAGE_N_SEQ
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_VOLTAGE_N_SEQ));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_VOLTAGE_N_SEQ);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_VOLTAGE_N_SEQ){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get VOLTAGE_N_SEQ error");
	    	}

//	    	PH_A_FUND_CURRENT
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_A_FUND_CURRENT));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_A_FUND_CURRENT);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_A_FUND_CURRENT){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_FUND_CURRENT error");
	    	}
//	    	PH_B_FUND_CURRENT
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_B_FUND_CURRENT));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_B_FUND_CURRENT);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_B_FUND_CURRENT){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_FUND_CURRENT error");
	    	}
//	    	PH_C_FUND_CURRENT
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_C_FUND_CURRENT));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_C_FUND_CURRENT);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_C_FUND_CURRENT){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_FUND_CURRENT error");
	    	}
	    	
//	    	PH_A_CURRENT_PQM
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_A_CURRENT_PQM));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_A_CURRENT_PQM);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_A_CURRENT_PQM){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_CURRENT_PQM error");
	    	}
//	    	PH_B_CURRENT_PQM
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_B_CURRENT_PQM));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_B_CURRENT_PQM);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_B_CURRENT_PQM){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_CURRENT_PQM error");
	    	}
//	    	PH_C_CURRENT_PQM
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_PH_C_CURRENT_PQM));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_PH_C_CURRENT_PQM);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_PH_C_CURRENT_PQM){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_CURRENT_PQM error");
	    	}
	    	
//	    	CURRENT_Z_SEQ
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_CURRENT_Z_SEQ));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_CURRENT_Z_SEQ);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_CURRENT_Z_SEQ){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURRENT_Z_SEQ error");
	    	}
//	    	CURRENT_P_SEQ
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_CURRENT_P_SEQ));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_CURRENT_P_SEQ);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_CURRENT_P_SEQ){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURRENT_P_SEQ error");
	    	}
//	    	CURRENT_N_SEQ
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_CURRENT_N_SEQ));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_CURRENT_N_SEQ);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_CURRENT_N_SEQ){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURRENT_N_SEQ error");
	    	}
	    	
//	    	THD_PH_A_CURRENT
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_THD_PH_A_CURRENT));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_THD_PH_A_CURRENT);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_THD_PH_A_CURRENT){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get THD_PH_A_CURRENT error");
	    	}
//	    	THD_PH_B_CURRENT
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_THD_PH_B_CURRENT));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_THD_PH_B_CURRENT);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_THD_PH_B_CURRENT){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get THD_PH_B_CURRENT error");
	    	}
//	    	THD_PH_C_CURRENT
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_THD_PH_C_CURRENT));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_THD_PH_C_CURRENT);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_THD_PH_C_CURRENT){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get THD_PH_C_CURRENT error");
	    	}
	    	
//	    	THD_PH_A_VOLTAGE
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_THD_PH_A_VOLTAGE));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_THD_PH_A_VOLTAGE);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_THD_PH_A_VOLTAGE){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get THD_PH_A_VOLTAGE error");
	    	}
//	    	THD_PH_B_VOLTAGE
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_THD_PH_B_VOLTAGE));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_THD_PH_B_VOLTAGE);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_THD_PH_B_VOLTAGE){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get THD_PH_B_VOLTAGE error");
	    	}
//	    	THD_PH_C_VOLTAGE
	        control = new OCTET(EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        send_data = new OCTET(DataUtil.get4ByteToInt(EDMI_Mk6N_DataConstants.REGISTER_THD_PH_C_VOLTAGE));
	        
	        frame = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_READ_REGISTER_EXTENDED, 9, EDMI_Mk6N_DataConstants.REGISTER_THD_PH_C_VOLTAGE);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo4Byte(DataUtil.select(temp, 1, 4))==EDMI_Mk6N_DataConstants.REGISTER_THD_PH_C_VOLTAGE){
	    		ba.append(temp,5,EDMI_Mk6N_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get THD_PH_C_VOLTAGE error");
	    	}
	    	log.debug("Power Quality data => "+new OCTET(ba.toByteArray()).toHexString());
        }catch(Exception e){
            log.error("Power Quality read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Power Quality data read error");
        }
        log.debug("========== Power Quality Read End ===============");
        return ba;
    }

    public void initProcess(IoSession session) throws MRPException
    {
    	try
    	{
	    	//getAttention(session);
	    	logOn(session);
    	}catch (Exception e)
    	{
    		log.error("initProcess error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"initProcess - error");
    	} 
    }
    
    protected void getAttention(IoSession session) throws MRPException
    {
        
    	EDMI_Mk6N_RequestDataFrame frame 
    		= new EDMI_Mk6N_RequestDataFrame(null, new OCTET(), new OCTET(), 0,0, null); 
    	EDMI_Mk6N_ReceiveDataFrame buf = null;
         
    	try
    	{
    		buf = read(session,frame, null, 1, 0);    
    	}catch (Exception e)
    	{
    		log.error("getAttention error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"getAttention from meter - error");
    	} 
    }
  
    protected void logOn(IoSession session) throws MRPException
    {
        try
        {
            byte[] idNpW = {'E', 'D','M','I',',','I','M','D','E','I','M','D','E',0x00};
            OCTET control = new OCTET(EDMI_Mk6N_DataConstants.CMD_LOGON); 
            OCTET send_data = new OCTET(idNpW);
            
            EDMI_Mk6N_RequestDataFrame frame 
                = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null); 
            Thread.sleep(500);
            EDMI_Mk6N_ReceiveDataFrame buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_LOGON, 1, 0);    
        }
        catch (Exception e)
        {
            log.error(e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Meter Log-On error");
        }
    }
    
    protected void exit(IoSession session) throws MRPException
    {
        byte[] b = new byte[0];
        OCTET control = new OCTET(EDMI_Mk6N_DataConstants.CMD_EXIT); 
        OCTET send_data = new OCTET(b);

        EDMI_Mk6N_RequestDataFrame frame 
            = new EDMI_Mk6N_RequestDataFrame(null, control, send_data, 0,0, null); 
        EDMI_Mk6N_ReceiveDataFrame buf = null;
        
        try
        {
        	buf = read(session,frame, EDMI_Mk6N_DataConstants.CMD_EXIT, 1, 0);    
        }
        catch (Exception e)
        {
            log.error(e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Meter exit error");
        }
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
