/** 
 * @(#)A2RL_Handler.java        *
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
	
public class A2RL_Handler extends MeterProtocolHandler { 

    private static Log log = LogFactory.getLog(A2RL_Handler.class);
    private String meterId;

	/**
	 * Constructor.<p>
	 */
    protected long sendBytes;
    
	public A2RL_Handler() {	
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
        int nOffset = 0;
        int nCount = 96;
        String from = "";
        String cmd = command.getCmd().getValue();
        log.debug("==============A2RL_Handler start cmd:"+cmd+"================");
        if(cmd.equals("104.6.0")||cmd.equals("104.13.0"))
        {
            SMIValue[] smiValue = command.getSMIValue();
            
            if(smiValue != null && smiValue.length >= 2){
                int kind = ((BYTE)smiValue[1].getVariable()).getValue();
                
                if(smiValue.length == 4){
                    nOffset = ((INT)smiValue[2].getVariable()).getValue();
                    nCount = ((INT)smiValue[3].getVariable()).getValue();

                    try
                    {
                        from = TimeUtil.getPreDay(TimeUtil.getCurrentTime(),nOffset);
                    }
                    catch (ParseException e)
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

                ba.append(new byte[]{'N','G','3','A','2'});
                ba.append(new byte[]{0x00});
                ba.append(mcuId);
                ba.append(new byte[]{(byte)0x8C});//A2RL_Handler
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{0x00,0x00,0x00,0x00});
                
            	switch(kind){
	                case 1:
	                	initProcess(session);
	                	
		                mti = configureRead(session).toByteArray();
       //                 tpb = billRead(session).toByteArray();
                        tcb = currbillRead(session).toByteArray();
                        lpd = lpRead(session,nOffset,nCount).toByteArray();
                        ist = instRead(session).toByteArray();
                        eld = eventlogRead(session).toByteArray();
                       	pqm = pqRead(session).toByteArray();
                   
                        terminate(session);
                        
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
                    case 2:
                        initProcess(session);
                        mti = configureRead(session).toByteArray();
                        tpb = billRead(session).toByteArray();
                        lpd = lpRead(session,nOffset,nCount).toByteArray();
                        ist = instRead(session).toByteArray();   
                        eld = eventlogRead(session).toByteArray();
                        pqm = pqRead(session).toByteArray();
                        terminate(session);
                        
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
                        ba.append(DataConstants.ELD);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(eld.length+5)));
                        ba.append(eld);

                        ba.append(DataConstants.PQM);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(pqm.length+5)));
                        ba.append(pqm);
                        break;
                    case 10:
                        initProcess(session);
                        mti = configureRead(session).toByteArray();
                        ist = instRead(session).toByteArray();   
                        eld = eventlogRead(session).toByteArray();
                        pqm = pqRead(session).toByteArray();
                        terminate(session);
                        
                        ba.append(DataConstants.MTI);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(mti.length+6)));
                        ba.append(new byte[]{0x00});
                        ba.append(mti);
                        
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
                    case 12:
                        initProcess(session);
                        mti = configureRead(session).toByteArray();
                        terminate(session);
                        
                        ba.append(DataConstants.MTI);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(mti.length+6)));
                        ba.append(new byte[]{0x00});
                        ba.append(mti);
                        break;
                    case 0:
                    	initProcess(session);
	                	
		                mti = configureRead(session).toByteArray();
                        tpb = billRead(session).toByteArray();
                        tcb = currbillRead(session).toByteArray();
                        lpd = lpRead(session,nOffset,nCount).toByteArray();
                        ist = instRead(session).toByteArray();
                        eld = eventlogRead(session).toByteArray();
                       	pqm = pqRead(session).toByteArray();
                   
                        terminate(session);
                        
                        ba.append(DataConstants.MTI);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(mti.length+6)));
                        ba.append(new byte[]{0x00});
                        ba.append(mti);

                        ba.append(DataConstants.TPB);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(tpb.length+5)));
                        ba.append(tpb);
                        
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
                    default:
                        throw new MRPException(MRPError.ERR_INVALID_PARAM,"Invalid parameters");
             	}
                
            }else{
                throw new MRPException(MRPError.ERR_INVALID_PARAM,"Invalid parameters");
            }
            
            if(ba != null && ba.toByteArray().length > 0)
            {      
                String currTime = null;
                try{currTime = TimeUtil.getCurrentTime();}
                catch(Exception e){                 
                }
                log.debug("meterEntryData=>"+smiValue[0].getVariable().toString()+","+
                        new OCTET(ba.toByteArray()).toHexString());
                meterDataEntry md = new meterDataEntry();
                md.setMdID(new OCTET(smiValue[0].getVariable().toString()));
                md.setMdSerial(new OCTET(this.meterId));
                md.setMdServiceType(new BYTE(Integer.parseInt(CommonConstants.getDataSvcCode(DataSVC.Electricity))));
                md.setMdTime(new TIMESTAMP(currTime));
                md.setMdType(new BYTE(Integer.parseInt(CommonConstants.getModemTypeCode(ModemType.MMIU))));
                md.setMdVendor(new BYTE(MeterVendor.ELSTER.getCode()[0].intValue()));//elster
                md.setMdData(new OCTET(ba.toByteArray()));
                commandData = new CommandData();
                commandData.setCnt(new WORD(1));
                commandData.setTotalLength(ba.toByteArray().length);
                commandData.append(new SMIValue(new OID("10.1.0"),new OPAQUE(md)));
            }
        }

        log.debug("==============A2RL_Handler end ================");
        return commandData;
    }

    public ByteArray configureRead(IoSession session) throws MRPException
    {
        log.debug("========== Configure Read Start ===============");
        ByteArray ba = new ByteArray();
        
        BYTE control = new BYTE(A2RL_DataConstants.CB_CLASSREAD);
        BYTE function = null;
        BYTE pad = new BYTE(A2RL_DataConstants.PAD_NULL);
        
    	OCTET length = new OCTET(new byte[]{(byte) 0x00, (byte) 0x05});
    	OCTET send_data = new OCTET(new byte[]{(byte) 0x00, (byte) 0x05, A2RL_DataConstants.CLASS_10});

        byte[] temp = null;
        
        A2RL_RequestDataFrame frame = null;
        A2RL_ReceiveDataFrame buf = null;
        try{
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
             
         // ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_MTRSN, A2RL_DataConstants.LEN_MTRSN, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, 0, A2RL_DataConstants.LEN_MTRSN, temp, 0, 0));
            //
            length = new OCTET(new byte[]{(byte) 0x00, (byte) A2RL_DataConstants.LEN_CLASS9});
            send_data = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00, A2RL_DataConstants.CLASS_9});
            frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
             
     		byte[] stat = new byte[5];
     		stat[0] = temp[A2RL_DataConstants.OFS_SYSWARN];
    		System.arraycopy(temp,A2RL_DataConstants.OFS_SYSERR,stat,1,A2RL_DataConstants.LEN_SYSERR);
    		stat[4] = temp[A2RL_DataConstants.OFS_SYSSTAT];
    		
    		byte[] datetime = new byte[A2RL_DataConstants.LEN_TD]; 
    		System.arraycopy(temp, A2RL_DataConstants.OFS_TD, datetime, 0, A2RL_DataConstants.LEN_TD);
    		     
    	//	ba.append(b);
    	//	ba.append((byte)0x0F);//resolution
    	/*	
    		send_data = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00, A2RL_DataConstants.CLASS_6});
            frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            
            ba.append(parseXUME(temp[A2RL_DataConstants.OFS_XUME]));//meter element
         */ 
    		length = new OCTET(new byte[]{(byte) 0x00, (byte) A2RL_DataConstants.LEN_CLASS0});    		
            send_data = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00, A2RL_DataConstants.CLASS_0});
            frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
                        
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_UKE, A2RL_DataConstants.LEN_UKE, temp, 0, 0));
            ba.append(stat);
            ba.append(new byte[]{(byte)0x00});
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CTRATIO, A2RL_DataConstants.LEN_CTRATIO, temp, A2RL_DataConstants.OFS_VTRATIO, A2RL_DataConstants.LEN_VTRATIO));
            ba.append(new byte[]{(byte)0x00});
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_DPLOCD, A2RL_DataConstants.LEN_DPLOCD, temp, 0,0));
            ba.append(new byte[]{(byte)0x00});
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_DPLOCE, A2RL_DataConstants.LEN_DPLOCE, temp, 0,0));
    		ba.append(datetime);//datetime
    		
        }catch(Exception e){
            log.error("configure read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"configure read error");
        }
        log.debug("========== Configure Read End ===============");
        return ba;
    }
    
	public byte parseXUME(byte data) throws Exception {

		int xume = ((data & 0xFF) >> 6) & 0xFF;

		switch(xume){
			case 0 :
				return (byte)0x00;
			case 1 :
				return (byte)0x40;
			case 2 :
				return (byte)0x80;
			case 3 :
				return (byte)0xc0;
			default :
				throw new Exception("N/A XUME TYPE");
		}
	}
	
	/**
	 * Read Event Log.<p>
	 * @return
	 * @throws MeterException
	 */
	public ByteArray eventlogRead(IoSession session) throws MRPException
    {
		log.debug("========== Event Log Read Start ===============");
        
        BYTE control = new BYTE(A2RL_DataConstants.CB_CLASSREAD);
        BYTE function = null;
        BYTE pad = new BYTE(A2RL_DataConstants.PAD_NULL);
        
    	OCTET length = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00});
    	OCTET send_data = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00, A2RL_DataConstants.CLASS_16});

        byte[] temp = null;
        
        ByteArray ba = new ByteArray();     
        
        A2RL_RequestDataFrame frame = null;
        A2RL_ReceiveDataFrame buf = null;
        try{
        	
            frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();

            int eventLen = temp.length/7;
            
            log.debug("eventLen :"+eventLen);
            ba.append(new byte[]{(byte) eventLen});
            ba.append(temp);//eventlog
            
        }catch(Exception e){
            log.error("Event Log read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Event Log read error");
        }
        log.debug("========== Event Log Read End ===============");
        return ba;
    }
	
    protected A2RL_ReceiveDataFrame read(IoSession session, A2RL_RequestDataFrame frame, int a) throws MRPException
    {
        int sequence = 0;
        try
        {
            IoBuffer buf = null;
            A2RL_ReceiveDataFrame rcvFrame = null;

            byte[] message = null ; 
            int retryCnt = 10;
            int cnt =0;
        	while(cnt<retryCnt){
        		cnt++;
        		
	        	try{
	        		buf = frame.getIoBuffer();
	            	session.write(buf); 
	            	message = (byte[]) handler.getMessageA2RL(session,4);
	        	}catch(Exception e){
	        		break;
	        	}
	        	
	        	if(message != null && message.length > 0){
	               	if(message[0] != A2RL_DataConstants.STX || message[2] != A2RL_DataConstants.ACK)
	               	{
		               	log.debug("STX Error or not OK" );
		               	continue;
	               	}else if(message.length != (int)(message[4]&0x7f)+7){
		               	log.debug("LENGTH Error or not OK" );
		               	continue;
	               	}else if(!checkCRC(message)){
		               	log.debug("CRC Error" );
		               	continue;
	               	}else{
	               		rcvFrame = new A2RL_ReceiveDataFrame();
		               	rcvFrame.append(message);
			            cnt =retryCnt;
	               	}
		            
	            }else{
	            	continue;
	            }
        	}

            if(rcvFrame==null){
                throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
            }
            return rcvFrame;
        }
        catch (Exception e)
        {
            log.error("Read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
        } 
    }
    
    protected A2RL_ReceiveDataFrame read(IoSession session, A2RL_RequestDataFrame frame) throws MRPException
    {
        int sequence = 0;
        try
        {
            IoBuffer buf = null;
            A2RL_ReceiveDataFrame rcvFrame = new A2RL_ReceiveDataFrame();

            byte[] message = null ; 
            boolean isContinue =true;
        	while(isContinue){
        		isContinue =false;
	        	try{
	        		buf = frame.getIoBuffer();
	            	session.write(buf); 
	            	message = (byte[]) handler.getMessageA2RL(session,4);
	        	}catch(Exception e){
	        		rcvFrame = null;
	        		break;
	        	}
	        	
	        	if(message != null && message.length > 0){
	               	if(message[0] != A2RL_DataConstants.STX || message[2] != A2RL_DataConstants.ACK)
	               	{
		               	rcvFrame = null;
		               	log.debug("STX Error or not OK" );
		               	break;
	               	}else if(message.length != (int)(message[4]&0x7f)+7){
	               		rcvFrame = null;
		               	log.debug("LENGTH Error or not OK" );
		               	break;
	               	}else if(!checkCRC(message)){
	               		rcvFrame = null;
		               	log.debug("CRC Error" );
		               	break;
	               	}else{
	               		rcvFrame.append(message);
		            	if(isContinue(message[A2RL_DataConstants.POS_LEN])){
		            		frame = new A2RL_RequestDataFrame(new BYTE(), new BYTE(A2RL_DataConstants.CB_CONTINUEREAD), 0,0, null);
		            		isContinue = true;
		            	}
	               	}
	            }else{
	            	rcvFrame = null;
	         	   break;
	            }
        	}

            if(rcvFrame==null){
                throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
            }
            return rcvFrame;
        }
        catch (Exception e)
        {
            log.error("Read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
        } 
    }

    protected boolean checkSequence(byte prev, byte current)
    {
        int prev_sequence  = (int)(prev & 0x3F);
        int current_sequence = (int)(current & 0x3F);
        return prev_sequence + 1 == current_sequence ? true : false;
    }

    public boolean checkCRC(byte[] src) throws MRPException
    {
        return true;
    }

    public void initProcess(IoSession session) throws MRPException
    {
    	try{
    		byte[] meterkey = whoAreYou(session);
    		passwdCheck(session, meterkey);
    	}catch(Exception e)
        {
            log.error("initProcess error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"initProcess meter error");
        } 
    }

    protected byte[] whoAreYou(IoSession session) throws MRPException
    {
        BYTE control = new BYTE(A2RL_DataConstants.CB_WHOAREYOU);
        BYTE function = new BYTE(A2RL_DataConstants.FUNC_WHOAREYOU);
        BYTE pad = new BYTE(A2RL_DataConstants.PAD_TIMEOUT);
        OCTET length = new OCTET(new byte[]{(byte) 0x01});
        OCTET send_Data = new OCTET(new byte[]{(byte) 0x00});
        
        byte[] temp = null;
        
        A2RL_RequestDataFrame frame 
            	= new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_Data, 0,0, null);
   //     A2RL_ReceiveDataFrame buf = null;
        
        try
        {

            int i =0;
            byte[] message = null;
            
            while(i<3){
            	message = null;
                IoBuffer buf = frame.getIoBuffer();
                log.debug("send whoAreYou=>"+buf.getHexDump());
	            session.write(buf); 
	
	            message = (byte[])handler.getMsg( session, 5, 15);
	            if(message != null && message.length ==15){
	                log.debug("receive whoAreYou =>"+new OCTET(message).toHexString());
	                break;
	            }else{
	            	i++;
	            }
            }
            
            /* Receive Wrong Data Response. */
     		if ((message.length != 15) || (message[0] != A2RL_DataConstants.STX))
     			throw new MRPException(MRPError.ERR_METER_INIT,"whoAreYou meter error");
     		
    		byte[] e_key =  new byte[4];
     		System.arraycopy(message, 9, e_key, 0, 4);
    		
    		log.debug("WHO ARE YOU E_KEY : "+new OCTET(e_key).toHexString());
    		
     		return e_key;
        	/*
        	buf = read(session, frame);    
        	temp = buf.encode();
            
            byte[] password = new byte[4]; 
            System.arraycopy(temp, 9, password, 0, 4); ///////
            return password;
            */
        } catch (Exception e)
        {
            log.error("whoAreYou error",e);
            throw new MRPException(MRPError.ERR_METER_INIT,"identify meter error");
        } 
    }
    
    protected boolean passwdCheck(IoSession session, byte[] ekey) throws MRPException
    {
        BYTE control = new BYTE(A2RL_DataConstants.CB_WHOAREYOU);
        BYTE function = new BYTE(A2RL_DataConstants.FUNC_PASSWDCHECK);
        BYTE pad = new BYTE(A2RL_DataConstants.PAD_NULL);
        
    	OCTET length = new OCTET(new byte[]{(byte) 0x04});
    	OCTET send_data = new OCTET(keyCreateLevel1(ekey));

        A2RL_RequestDataFrame frame 
            = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null); 
     //   A2RL_ReceiveDataFrame  buf = null;
       
        try
        {
        	byte[] message = null;
        	IoBuffer buf = frame.getIoBuffer();
            log.debug("send passwdCheck=>"+buf.getHexDump());
            session.write(buf); 

            message = (byte[])handler.getMsg( session, 5, 6);
            
        	if(message!=null && message.length ==6 && message[0] == A2RL_DataConstants.STX && message[2]==A2RL_DataConstants.ACK)
        		return true;
        	else
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS, "Meter security error");
        }catch (Exception e)
        {
            log.error(e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS, "Meter security error");
        }
    }
    
    protected void terminate(IoSession session) throws MRPException
    {
    	 BYTE control = new BYTE(A2RL_DataConstants.CB_TERMINATE);
    	 
         A2RL_RequestDataFrame frame 
             = new A2RL_RequestDataFrame(new BYTE(), control, 0,0, null);
         try
         {
	         IoBuffer buf = frame.getIoBuffer();
	         log.debug("send passwdCheck=>"+buf.getHexDump());
	         session.write(buf); 
         }catch (Exception e)
         {
            log.error("terminate error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"terminate meter error");
         } 
    }
    
    public ByteArray instRead(IoSession session) throws MRPException
    {
        log.debug("========== Instrument Read Start ===============");
        
        BYTE control = new BYTE(A2RL_DataConstants.CB_INSTRUMENT);
        BYTE function = null;
        BYTE pad = new BYTE((byte)0x01);
        
    	OCTET length = new OCTET(new byte[]{(byte) 0x00, (byte) 0x03});
    	OCTET send_data = new OCTET(new byte[]{(byte) 0x06, (byte) 0x00, (byte) 0x00});

        byte[] temp = null;
        ByteArray ba = new ByteArray();     
        
        A2RL_RequestDataFrame frame = null;
        A2RL_ReceiveDataFrame buf = null;
        try{
        	
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame,1);        
            temp = buf.encode();
            ba.append(DataUtil.select(temp, 2, 4)); //voltage A
            log.debug("receive voltage A =>"+new OCTET(DataUtil.select(temp, 2, 4)).toHexString());
            
            send_data = new OCTET(new byte[]{(byte) 0x06, (byte) 0x02, (byte) 0x00});
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame,1);        
            temp = buf.encode();
            ba.append(DataUtil.select(temp, 2, 4)); //voltage B
            log.debug("receive voltage B =>"+new OCTET(DataUtil.select(temp, 2, 4)).toHexString());
            
            send_data = new OCTET(new byte[]{(byte) 0x06, (byte) 0x04, (byte) 0x00});
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame,1);        
            temp = buf.encode();
            ba.append(DataUtil.select(temp, 2, 4)); //voltage C
            log.debug("receive voltage C =>"+new OCTET(DataUtil.select(temp, 2, 4)).toHexString());
            
            send_data = new OCTET(new byte[]{(byte) 0x05, (byte) 0x00, (byte) 0x00});
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame,1);        
            temp = buf.encode();
            ba.append(DataUtil.select(temp, 2, 4)); //current A
            log.debug("receive current A =>"+new OCTET(DataUtil.select(temp, 2, 4)).toHexString());
            
            send_data = new OCTET(new byte[]{(byte) 0x05, (byte) 0x02, (byte) 0x00});
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame,1);        
            temp = buf.encode();
            ba.append(DataUtil.select(temp, 2, 4)); //current B
            log.debug("receive current B =>"+new OCTET(DataUtil.select(temp, 2, 4)).toHexString());
            
            send_data = new OCTET(new byte[]{(byte) 0x05, (byte) 0x04, (byte) 0x00});
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame,1);        
            temp = buf.encode();
            ba.append(DataUtil.select(temp, 2, 4)); //current C
            log.debug("receive current C =>"+new OCTET(DataUtil.select(temp, 2, 4)).toHexString());
            
            send_data = new OCTET(new byte[]{(byte) 0x0b, (byte) 0x00, (byte) 0x00});
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame,1);        
            temp = buf.encode();
            ba.append(DataUtil.select(temp, 2, 4)); //voltage angle A
            log.debug("receive voltage angle A =>"+new OCTET(DataUtil.select(temp, 2, 4)).toHexString());
            
            send_data = new OCTET(new byte[]{(byte) 0x0b, (byte) 0x02, (byte) 0x00});
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame,1);        
            temp = buf.encode();
            ba.append(DataUtil.select(temp, 2, 4)); //voltage angle B
            log.debug("receive voltage angle B =>"+new OCTET(DataUtil.select(temp, 2, 4)).toHexString());
            
            send_data = new OCTET(new byte[]{(byte) 0x0b, (byte) 0x04, (byte) 0x00});
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame,1);        
            temp = buf.encode();
            ba.append(DataUtil.select(temp, 2, 4)); //voltage angle C
            log.debug("receive voltage angle C =>"+new OCTET(DataUtil.select(temp, 2, 4)).toHexString());
            
            send_data = new OCTET(new byte[]{(byte) 0x0c, (byte) 0x00, (byte) 0x00});
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame,1);        
            temp = buf.encode();
            ba.append(DataUtil.select(temp, 2, 4)); //current angle A
            log.debug("receive current angle A =>"+new OCTET(DataUtil.select(temp, 2, 4)).toHexString());
            
            send_data = new OCTET(new byte[]{(byte) 0x0c, (byte) 0x02, (byte) 0x00});
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame,1);        
            temp = buf.encode();
            ba.append(DataUtil.select(temp, 2, 4)); //current angle B
            log.debug("receive current angle B =>"+new OCTET(DataUtil.select(temp, 2, 4)).toHexString());
            
            send_data = new OCTET(new byte[]{(byte) 0x0c, (byte) 0x04, (byte) 0x00});
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame,1);        
            temp = buf.encode();
            ba.append(DataUtil.select(temp, 2, 4)); //current angle C
            log.debug("receive current angle C =>"+new OCTET(DataUtil.select(temp, 2, 4)).toHexString());
            
        }catch(Exception e){
            log.error("Instrument read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Instrument data read error");
        }
    
        log.debug("========== Instrument Read End ===============");
        return ba;
    }
    
    public ByteArray lpRead(IoSession session, String startday, String endday, int lpCycle) throws MRPException
    {
    	return null;
    }

    public ByteArray lpRead(IoSession session, int nOffset, int nCount) throws MRPException
    {
        log.debug("========== LP Read Start ===============");
        
        BYTE control = new BYTE(A2RL_DataConstants.CB_CLASSREAD);
        BYTE function = null;
        BYTE pad = new BYTE(A2RL_DataConstants.PAD_NULL);
        
    	OCTET length = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00});
    	OCTET send_data = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00, A2RL_DataConstants.CLASS_12});

        byte[] temp = null;
        ByteArray ba = new ByteArray();     
        
        A2RL_RequestDataFrame frame = null;
        A2RL_ReceiveDataFrame buf = null;
        try{
        	//current value
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_ETKWH1, A2RL_DataConstants.LEN_ETKWH1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_ETKWH2, A2RL_DataConstants.LEN_ETKWH2, temp, 0, 0));
            
            //LP
            send_data = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00, A2RL_DataConstants.CLASS_14});
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
                  
            int channelCnt = temp[A2RL_DataConstants.OFS_CHANS];
            int dayCnt = temp[A2RL_DataConstants.OFS_LPMEM];
            int dasize  = DataUtil.getIntTo2Byte(DataUtil.select(temp, A2RL_DataConstants.OFS_DASIZE, A2RL_DataConstants.LEN_DASIZE));
           
            if(nCount>dayCnt){
            	nCount = dayCnt;
            }
            
            if(nCount>5){
            	nCount = 5;
            }
            
            log.debug("nOffset : "+ nOffset);
            log.debug("nCount : "+ nCount);
            log.debug("channelCnt : "+ channelCnt);
            log.debug("dayCnt : "+ dayCnt);
            log.debug("dasize : "+ dasize);
            
        	length = new OCTET(new byte[]{(byte) 0x00, (byte) (nCount & 0xff)});
        	send_data = new OCTET(new byte[]{(byte) 0x00, (byte) (nOffset & 0xff), A2RL_DataConstants.CLASS_17});
        	
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            
            /*
            int cntLP =0;
            int cntDayInData = temp.length/dasize;
            
            if(temp.length%dasize >0)
            	cntDayInData += 1;
            
            for(int j=0; j<cntDayInData; j++){
            	if(j==0){
            		
            		ba.append(DataUtil.select(temp, 0, A2RL_DataConstants.LEN_DASIZE));
            	}
            }           
            */
            ba.append(temp);
            
        }catch(Exception e){
            log.error("lp read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"lp data read error");
        }
    
        log.debug("========== LP Read End ===============");
        return ba;
    }

    public ByteArray billRead(IoSession session) throws MRPException
    {
        log.debug("========== Prev Bill Read Start ===============");
        
        BYTE control = new BYTE(A2RL_DataConstants.CB_CLASSREAD);
        BYTE function = null;
        BYTE pad = new BYTE(A2RL_DataConstants.PAD_NULL);
        
    	OCTET length = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00});
    	OCTET send_data = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00, A2RL_DataConstants.CLASS_9});

        byte[] temp = null;
        
        ByteArray ba = new ByteArray();     
        
        A2RL_RequestDataFrame frame = null;
        A2RL_ReceiveDataFrame buf = null;
        try{
        	
            frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CUMDR, A2RL_DataConstants.LEN_CUMDR,
            								temp, A2RL_DataConstants.OFS_DATATR, A2RL_DataConstants.LEN_DATATR));
            
            send_data = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00, A2RL_DataConstants.CLASS_12});
            frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_ETKWH1, A2RL_DataConstants.LEN_ETKWH1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_ETKWH2, A2RL_DataConstants.LEN_ETKWH2, temp, 0, 0));
            
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_AKWH1, A2RL_DataConstants.LEN_AKWH1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BKWH1, A2RL_DataConstants.LEN_BKWH1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CKWH1, A2RL_DataConstants.LEN_CKWH1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_AKWH2, A2RL_DataConstants.LEN_AKWH2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BKWH2, A2RL_DataConstants.LEN_BKWH2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CKWH2, A2RL_DataConstants.LEN_CKWH2, temp, 0, 0));
            
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_AKW1, A2RL_DataConstants.LEN_AKW1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_ATD1, A2RL_DataConstants.LEN_ATD1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BKW1, A2RL_DataConstants.LEN_BKW1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BTD1, A2RL_DataConstants.LEN_BTD1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CKW1, A2RL_DataConstants.LEN_CKW1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CTD1, A2RL_DataConstants.LEN_CTD1, temp, 0, 0));
            
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_AKW2, A2RL_DataConstants.LEN_AKW2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_ATD2, A2RL_DataConstants.LEN_ATD2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BKW2, A2RL_DataConstants.LEN_BKW2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BTD2, A2RL_DataConstants.LEN_BTD2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CKW2, A2RL_DataConstants.LEN_CKW2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CTD2, A2RL_DataConstants.LEN_CTD2, temp, 0, 0));
            
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_AKWCUM1, A2RL_DataConstants.LEN_AKWCUM1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BKWCUM1, A2RL_DataConstants.LEN_BKWCUM1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CKWCUM1, A2RL_DataConstants.LEN_CKWCUM1, temp, 0, 0));
            
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_AKWCUM2, A2RL_DataConstants.LEN_AKWCUM2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BKWCUM2, A2RL_DataConstants.LEN_BKWCUM2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CKWCUM2, A2RL_DataConstants.LEN_CKWCUM2, temp, 0, 0));
			
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_EAVGPF, A2RL_DataConstants.LEN_EAVGPF, temp, 0, 0));
            
        }catch(Exception e){
            log.error("prev bill read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Billing data read error");
        }
        log.debug("========== Prev Bill Read End ===============");
        return ba;
    }
    

	/**
	 * Read Cumulative Data.<p>
	 * @return
	 * @throws MeterException
	 */
	public ByteArray currbillRead(IoSession session) throws MRPException
	{
		log.debug("========== Curr Bill Read Start ===============");
		
        BYTE control = new BYTE(A2RL_DataConstants.CB_CLASSREAD);
        BYTE function = null;
        BYTE pad = new BYTE(A2RL_DataConstants.PAD_NULL);
        
    	OCTET length = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00});
    	OCTET send_data = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00, A2RL_DataConstants.CLASS_11});

        byte[] temp = null;
        
        ByteArray ba = new ByteArray();     
        
        A2RL_RequestDataFrame frame = null;
        A2RL_ReceiveDataFrame buf = null;
        try{

            frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_ETKWH1, A2RL_DataConstants.LEN_ETKWH1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_ETKWH2, A2RL_DataConstants.LEN_ETKWH2, temp, 0, 0));
            
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_AKWH1, A2RL_DataConstants.LEN_AKWH1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BKWH1, A2RL_DataConstants.LEN_BKWH1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CKWH1, A2RL_DataConstants.LEN_CKWH1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_AKWH2, A2RL_DataConstants.LEN_AKWH2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BKWH2, A2RL_DataConstants.LEN_BKWH2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CKWH2, A2RL_DataConstants.LEN_CKWH2, temp, 0, 0));
            
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_AKW1, A2RL_DataConstants.LEN_AKW1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_ATD1, A2RL_DataConstants.LEN_ATD1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BKW1, A2RL_DataConstants.LEN_BKW1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BTD1, A2RL_DataConstants.LEN_BTD1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CKW1, A2RL_DataConstants.LEN_CKW1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CTD1, A2RL_DataConstants.LEN_CTD1, temp, 0, 0));
            
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_AKW2, A2RL_DataConstants.LEN_AKW2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_ATD2, A2RL_DataConstants.LEN_ATD2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BKW2, A2RL_DataConstants.LEN_BKW2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BTD2, A2RL_DataConstants.LEN_BTD2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CKW2, A2RL_DataConstants.LEN_CKW2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CTD2, A2RL_DataConstants.LEN_CTD2, temp, 0, 0));
            
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_AKWCUM1, A2RL_DataConstants.LEN_AKWCUM1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BKWCUM1, A2RL_DataConstants.LEN_BKWCUM1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CKWCUM1, A2RL_DataConstants.LEN_CKWCUM1, temp, 0, 0));
            
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_AKWCUM2, A2RL_DataConstants.LEN_AKWCUM2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_BKWCUM2, A2RL_DataConstants.LEN_BKWCUM2, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_CKWCUM2, A2RL_DataConstants.LEN_CKWCUM2, temp, 0, 0));
			
            ba.append(DataUtil.arrayAppend(temp, A2RL_DataConstants.OFS_EAVGPF, A2RL_DataConstants.LEN_EAVGPF, temp, 0, 0));
            
        }catch(Exception e){
            log.error("curr bill read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Current Billing data read error");
        }
        log.debug("========== Curr Bill Read End ===============");
        return ba;
    }
	
    public ByteArray pqRead(IoSession session) throws MRPException
    {
		log.debug("========== Power Quality Read Start ===============");
		
        BYTE control = new BYTE(A2RL_DataConstants.CB_CLASSREAD);
        BYTE function = null;
        BYTE pad = new BYTE(A2RL_DataConstants.PAD_NULL);
        
    	OCTET length = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00});
    	OCTET send_data = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00, A2RL_DataConstants.CLASS_50});

        byte[] temp = null;
        
        ByteArray ba = new ByteArray();     
        
        A2RL_RequestDataFrame frame = null;
        A2RL_ReceiveDataFrame buf = null;
        try{
        	//pqm
            frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            
            ba.append(DataUtil.arrayAppend(temp, 0,54, temp, 0, 0));
            
            //sag
            send_data = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00, A2RL_DataConstants.CLASS_52});
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            
            ba.append(DataUtil.arrayAppend(temp, 9,18, temp, 0, 0));
            
            //sag event
            send_data = new OCTET(new byte[]{(byte) 0x00, (byte) 0x00, A2RL_DataConstants.CLASS_53});
        	frame = new A2RL_RequestDataFrame(new BYTE(), control, function, pad, length, send_data, 0,0, null);
        	buf = read(session,frame);
        	temp = buf.encode();
        	
        	int count =0;
        	for(int i=0; i< A2RL_DataConstants.MAX_BLOCK_COUNT; i++){
        		if(temp[i*A2RL_DataConstants.ONE_BLOCK_SIZE]==0x00)
        			break;
        		else count++;
        	}
        	ba.append(new byte[]{(byte)count});
        	ba.append(DataUtil.arrayAppend(temp, 0,count*A2RL_DataConstants.ONE_BLOCK_SIZE, temp, 0, 0));
            
        }catch(Exception e){
            log.error("Power Quality read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Power Quality data read error");
        }
        log.debug("========== Power Quality Read End ===============");
        return ba;
    }
	
	public void setModemNumber(String modemId){
		
	}
	
    public ByteArray getPowerFail(IoSession session, String startday, String endday, int lpCycle, byte seqNo) throws MRPException
    {
    	return null;
    }
    
    public ByteArray getPowerRecover(IoSession session, String startday, String endday, int lpCycle, byte seqNo) throws MRPException
    {
    	return null;
    }

    public void quit() throws MRPException
    {
        
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
    
	private boolean isContinue(byte len){
		
		if((int)((len & 0xFF) & 0x80) != 0)
			return false;

		return true;
	}
	
	/**
	 * ABBConnector.keyCreate<p>
	 * Get Password Key From WhoAreYou Command.
	 * @param data
	 * @return password
	 * 
	 * No password
	 * RESPONSE SEQUENCE FORMAT
	 * -----------------------------
	 * |STX(1)|KEY(12)|CRCH(1)|CRCL|
	 * -----------------------------
	 */
	private byte[] keyCreateLevel1(byte[] data){

		byte[] des = new byte[4];
		
		int dw;
		
		int cd = 0;
		int i = 0, c = 0, r = 0;

		des[0] = 0x00;
		des[1] = 0x00;
		des[2] = 0x00;
		des[3] = 0x00;		

		dw = (0xff000000 & (data[0] << 24))
				| (0x00ff0000 & (data[1] << 16))
				| (0x0000ff00 & (data[2] << 8))
				| (0x000000ff & (data[3] ));
							
		dw += 0x0000ab41;

		r = 0;

		i = (   ((0xff000000 & dw) >> 24)
			 +  ((0x00ff0000 & dw) >> 16)
			 +  ((0x0000ff00 & dw) >> 8 )
			 +  ((0x000000ff & dw))
			) & 0x0f; 
		
		for( ; i>=0 ; i--) {
			c = ((((0xff000000 & dw) >> 24) & 0x80) !=0 ) ? 1 : 0;
			dw = (dw << 1) + r;
			r = c;
			cd ^= dw;
		}

		des[0] = (byte)(0xff & (cd >> 24));
		des[1] = (byte)(0xff & (cd >> 16));
		des[2] = (byte)(0xff & (cd >> 8));
		des[3] = (byte)(0xff & cd);
		
		return des;
		
	}

	public byte[] keyCreateLevel2(byte[] e_key) {

		byte[] pwd = new byte[4];
		byte[] db  = new byte[4];
		
		long dw;
		
		long cd = 0;
		int i = 0, c = 0, r = 0;

		dw = (0xff000000 & (e_key[0] << 24))
				| (0x00ff0000 & (e_key[1] << 16))
				| (0x0000ff00 & (e_key[2] << 8))
				| (0x000000ff & (e_key[3] )) & 0xFFFFFFFF;

		dw += 0x0000ab41;
		r = 0;

		// PEA Password Level 2 (Read Only - Billing Data)
		cd = 0x25240520;

		i = (int) ((  ((0xff000000 & dw) >> 24)
			 +  ((0x00ff0000 & dw) >> 16)
			 +  ((0x0000ff00 & dw) >> 8 )
			 +  ((0x000000ff & dw))
			) & 0x0f); 

		for( ; i>=0 ; i--) {
			c = ((((0xff000000 & dw) >> 24) & 0x80) !=0 ) ? 1 : 0;
			dw = (dw << 1) + r;
			r = c;
			cd ^= dw;
		}

		pwd[0] = (byte)(0xff & (cd >> 24));
		pwd[1] = (byte)(0xff & (cd >> 16));
		pwd[2] = (byte)(0xff & (cd >> 8));
		pwd[3] = (byte)(0xff & cd);
		
		return pwd;		
	}

	public byte[] keyCreateLevel3(byte[] e_key)
	{
		byte[] pwd = new byte[4];
		
		short i = 0;
		char[] tmp = new char[3];

		char	j, k = 0;
		byte[] key = new byte[4];

		key[0] = e_key[0];
		key[1] = e_key[1];
		key[2] = e_key[2];
		key[3] = e_key[3];

		tmp[0] = 0x0041;
		tmp[1] = (char) (key[3] & 0xFF);
		tmp[2] = (char) (tmp[0] + tmp[1]);

		key[3] += 0x41;

		if((tmp[2] & 0xff00) != 0 )
		{
			tmp[2] = (char) ((0xff00 & tmp[2]) >> 8);
			key[2] = (byte) (((key[2] & 0xFF) + tmp[2]) & 0xFF);
			tmp[0] = tmp[2];
			tmp[1] = (char) (key[2] & 0xFF);
			tmp[2] = (char) (tmp[0] + tmp[1]);

			if((tmp[2] & 0xff00) != 0)
			{
				tmp[2] = (char) ((0xff00 & tmp[2]) >> 8);
				key[1] = (byte)(((key[1]&0xFF) + tmp[2]) & 0xFF);
				tmp[0] = tmp[2];
				tmp[1] = (char) (key[1] & 0xFF);
				tmp[2] = (char) (tmp[0] + tmp[1]);

				if((tmp[2] & 0xff00) != 0)
				{
					tmp[2] = (char) ((0xff00 & tmp[2]) >> 8);
					key[0] = (byte) (((key[0] & 0xFF)+tmp[2]) & 0xFF);
				}
			}
		}

		tmp[0] = 0x00ab;
		tmp[1] = (char) (key[2] & 0xFF);
		tmp[2] = (char) (tmp[0] + tmp[1]);
		key[2] = (byte)(((key[2]& 0xFF)+0xab) & 0xFF);

		if((tmp[2] & 0xff00) != 0)
		{
			tmp[2] = (char) ((0xff00 & tmp[2]) >> 8);
			key[1] = (byte)(((key[1] & 0xFF) + tmp[2]) & 0xFF);
			tmp[0] = tmp[2];
			tmp[1] = (char) (key[1] & 0xFF);
			tmp[2] = (char) (tmp[0] + tmp[1]);

			if((tmp[2] & 0xff00) != 0)
			{
				tmp[2] = (char) ((0xff00 & tmp[2]) >> 8);
				key[0] = (byte)(((key[0]&0xFF)+tmp[2]) & 0xFF);
			}
		}

		i += key[3] & 0xFF; 
		i += key[2] & 0xFF; 
		i += key[1] & 0xFF; 
		i += key[0] & 0xFF;
		i = (short) (i & 0x0f);
	
		// PEA Password Level 2 (Read Only - Billing Data)
		pwd[0] = 0x25;
		pwd[1] = 0x24;
		pwd[2] = 0x05;
		pwd[3] = 0x20;

		while(i >= 0)
		{
			if((key[3] & 0x80) != 0) j = 1;
			else j = 0;

			key[3] = (byte) (key[3] << 1);
			key[3] += k;
			k = j;

			if((key[2] & 0x80) != 0) j = 1;
			else j = 0;

			key[2] = (byte) (key[2] << 1);
			key[2] += k;
			k = j;

			if((key[1] & 0x80) != 0) j = 1;
			else j = 0;

			key[1] = (byte) (key[1] << 1);
			key[1] += k;
			k = j;

			if((key[0] & 0x80) != 0) j = 1;
			else j = 0;

			key[0] = (byte) (key[0] << 1);
			key[0] += k;
			k = j;

			pwd[0] ^= key[0];
			pwd[1] ^= key[1];
			pwd[2] ^= key[2];
			pwd[3] ^= key[3];

			i--;
		}
		
		return pwd;
	}

    @Override
    public void setGroupNumber(String groupNumber) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setMcuSwVersion(String mcuSwVersion) {
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
