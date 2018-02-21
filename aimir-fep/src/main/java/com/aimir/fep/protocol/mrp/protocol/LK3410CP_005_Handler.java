/** 
 * @(#)LK3410CP_005.java       1.0 04/01/08 *
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
 * @author Park YeonKyoung  yeonkyoung@hanmail.net
 */
	
public class LK3410CP_005_Handler extends MeterProtocolHandler { 

    private static Log log = LogFactory.getLog(LK3410CP_005_Handler.class);
    private int controlcode;
    private String meterId;
    private String modemId;
    private String groupId = "";
    private String memberId = "";
    private String mcuSwVersion = "";

    private int DIR;
    private int PRM;
    private int FCB;
    private int RES;
    private int FCV;
    private int DFC;
        
    protected static final char GRP_SET_METER = 0x01FF;
    
    protected static final char SET_METER = 0x011F;
    
    protected static final char GRP_SET_METERING = 0x02FF;
    protected static final char SET_LP = 0x022F;    
    
    protected static final char GRP_METER_STAT = 0x04FF;
    protected static final char CONFIG = 0x041F;
    protected static final char METERING = 0x042F;
    protected static final char ERRORFLAG = 0x043F;
    protected static final char ERRORLOG = 0x044F;
    protected static final char COMMSPEED = 0x045F;
    protected static final char POWERFAIL = 0x046F;
    protected static final char INSTLOG = 0x047F;
    protected static final char LPCONFIG = 0x048F;
    protected static final char COMMLOG = 0x049F;
    
    protected static final char GRP_BILLING_DATA = 0x05FF;
    
    protected static final char BILL_ENERGY = 0x051F;
    protected static final char BILL_DEMAND = 0x052F;
    protected static final char BILL_CUMM_DEMAND = 0x053F;
    protected static final char BILL_CONT_DEMAND = 0x054F;
    protected static final char BILL_MAX_DEMAND = 0x055F;
    protected static final char BILL_MAX_CONT_DEMAND = 0x056F;
    protected static final char BILL_LEAD_PF = 0x057F;
    protected static final char BILL_LAG_PF = 0x058F;
    protected static final char BILL_AVG_LEAD_PF = 0x059F;
    protected static final char BILL_AVG_LAG_PF = 0x05AF;
    protected static final char BILL_PREV_DEMAND = 0x05BF;
    protected static final char BILL_PREV_MAX_DEMAND = 0x05CF;    
    
    protected static final char GRP_LP_DATA = 0x06FF;
    
    protected static final char LP_FIFTEEN = 0x061F;
    protected static final char LP_HOUR = 0x062F;
    protected static final char LP_DAY = 0x063F;
    protected static final char LP_WEEK = 0x064F;
    protected static final char LP_MONTH = 0x065F;
    protected static final char LP_THREE_MONTH = 0x066F;
    protected static final char LP_N = 0x067F;
    protected static final char LP_ALL = 0x06FF;    
    
    protected static final byte FUNC_ACK = 0x00;
    protected static final byte FUNC_NACK = 0x01;
    protected static final byte FUNC_AUTA = 0x02;
    protected static final byte FUNC_AUTR = 0x03;
    protected static final byte FUNC_LINKSTATE = 0x0B;

    protected static final byte DATA_CTRL_M_ACK = (byte)0x80;
    protected static final byte DATA_CTRL_M_NACK = (byte)0x81;
    protected static final byte DATA_CTRL_M_AUTA = (byte)0x82;
    protected static final byte DATA_CTRL_M_AUTR = (byte)0x83;
    protected static final byte DATA_CTRL_M_LINKSTATE = (byte)0x8B;
    protected static final byte DATA_CTRL_M_ACK2 = (byte)0x90;
    protected static final byte DATA_CTRL_M_NACK2 = (byte)0x91;
    protected static final byte DATA_CTRL_M_LINKSTATEREPLY = (byte)0x9B;
    protected static final byte DATA_CTRL_M_RESETLINK = (byte)0xC0;
    protected static final byte DATA_CTRL_M_UNCONFIRMEDUSERDATA = (byte)0xC4;
    protected static final byte DATA_CTRL_M_LINKSTATEREQUEST = (byte)0xC9;
    protected static final byte DATA_CTRL_M_AUTHENTICATION = (byte)0xD1;
    protected static final byte DATA_CTRL_M_TESTLINK = (byte)0xD2;
    protected static final byte DATA_CTRL_M_CONFIRMEDUSERDATA = (byte)0xD3;
    protected static final byte DATA_CTRL_M_RESETLINK2 = (byte)0xE0;
    protected static final byte DATA_CTRL_M_UNCONFIRMEDUSERDATA2 = (byte)0xE4;    
    protected static final byte DATA_CTRL_M_LINKSTATEREQUEST2 = (byte)0xE9;
    protected static final byte DATA_CTRL_M_AUTHENTICATION2 = (byte)0xF1;
    protected static final byte DATA_CTRL_M_TESTLINK2 = (byte)0xF2;
    protected static final byte DATA_CTRL_M_CONFIRMEDUSERDATA2 = (byte)0xF3;
    
    protected static final byte FUNC_RESET = 0x00;
    protected static final byte FUNC_AUTH = 0x01;
    protected static final byte FUNC_TEST = 0x02;
    protected static final byte FUNC_USERDATA = 0x03;
    protected static final byte FUNC_REQUEST = 0x09;
    
    protected byte[] des = new byte[6]; //destination

	/**
	 * Constructor.<p>
	 */
    protected long sendBytes;
    
    
	public LK3410CP_005_Handler() {	

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
        ByteArray ba = new ByteArray();
        int nOffset = 0;
        int nCount = 96;
        String from = "";
        String cmd = command.getCmd().getValue();
        log.debug("==============LK3410CP_005_Handler start cmd:"+cmd+"================");
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

                ba.append(new byte[]{'N','G','3','A','1'});
                ba.append(new byte[]{0x00});
                ba.append(mcuId);
                ba.append(new byte[]{(byte)0x86});
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{0x00,0x00,0x00,0x00});
                
                switch(kind){
                    case 0:
                        initProcess(session);
                        mti = configureRead(session).toByteArray();
                        //tpb = billRead(session).toByteArray();
                        tcb = currbillRead(session).toByteArray();
                        lpd = lpRead(session,from,"",15).toByteArray();
                        ist = instRead(session).toByteArray();
                        eld = eventlogRead(session).toByteArray();
                        ba.append(DataConstants.MTI);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(mti.length+1)));
                        ba.append(new byte[]{0x00});
                        ba.append(mti);

                        /*
                        ba.append(TPB);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(tpb.length+5)));
                        ba.append(tpb);
                        */  
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

                        break;
                    case 1:
                        initProcess(session);
                        mti = configureRead(session).toByteArray();
                        tcb = currbillRead(session).toByteArray();
                        lpd = lpRead(session,from,"",15).toByteArray();
                        ist = instRead(session).toByteArray();                        
                        ba.append(DataConstants.MTI);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(mti.length+1)));
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
                        break;
                    case 2:
                        initProcess(session);
                        mti = configureRead(session).toByteArray();
                        tpb = billRead(session).toByteArray();
                        lpd = lpRead(session,from,"",15).toByteArray();
                        ist = instRead(session).toByteArray();   
                        ba.append(DataConstants.MTI);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(mti.length+1)));
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
                        break;
                    case 10:
                        initProcess(session);
                        mti = configureRead(session).toByteArray();
                        eld = eventlogRead(session).toByteArray();
                        ba.append(DataConstants.MTI);
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(mti.length+1)));
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
                        ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(mti.length+1)));
                        ba.append(new byte[]{0x00});
                        ba.append(mti);
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
                log.debug("meterEntryData=>"+smiValue[0].getVariable().toString()+","+new OCTET(ba.toByteArray()).toHexString());
                meterDataEntry md = new meterDataEntry();
                md.setMdID(new OCTET(smiValue[0].getVariable().toString()));
                md.setMdSerial(new OCTET(this.meterId));
                md.setMdServiceType(new BYTE(1));//1: electric
                md.setMdTime(new TIMESTAMP(currTime));
                md.setMdType(new BYTE(11));//11:mmiu
                md.setMdVendor(new BYTE(6));//LS LK3410
                md.setMdData(new OCTET(ba.toByteArray()));
                commandData = new CommandData();
                commandData.setCnt(new WORD(1));
                commandData.setTotalLength(ba.toByteArray().length);
                commandData.append(new SMIValue(new OID("10.1.0"),new OPAQUE(md)));
            }

        }

        log.debug("==============LK3410CP_005_Handler end ================");
        return commandData;
    }
    
    protected ByteArray readChannelConfigure(IoSession session) throws MRPException
    {
        return null;
    }

    public ByteArray configureRead(IoSession session) throws MRPException
    {
        log.debug("========== Configure Read Start ===============");
        ByteArray ba = new ByteArray();

        OCTET destination = new OCTET(this.des);
        BYTE source = new BYTE(0x00);
        BYTE control = new BYTE(0xC4);
        OCTET send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x01,(byte) 0x1F});
 
        try{
            LK3410CP_005_RequestDataFrame frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   send_data, 0,0, null);
            LK3410CP_005_ReceiveDataFrame buf = read(session,frame);
            byte[] temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 0, 8, temp, 32, 9));            

            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x01,(byte) 0x2F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                               send_data, 0,0, null);
            buf = read(session,frame);
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 2, 5, temp, 0, 0));
            
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x02,(byte) 0x1F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                               send_data, 0,0, null);
            buf = read(session,frame);
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 1, 1, temp, 0, 0));
            
       
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x04,(byte) 0x1F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                               send_data, 0,0, null);
            buf = read(session,frame);
            temp = buf.encode();
            byte[] mserial = new byte[3];
            System.arraycopy(temp, 7, mserial, 0, 3);
            String pYear = "00"+(temp[7]&0xFF);
            String serial = "000000"+DataUtil.getIntToBytes(mserial);

            this.meterId = pYear.substring(pYear.length()-2, pYear.length()); 
            this.meterId += serial.substring(serial.length()-6, serial.length());            
            
            ba.append(DataUtil.arrayAppend(temp, 5, 6, temp, 11, 1));
            ba.append(DataUtil.arrayAppend(temp, 12, 7, temp, 27, 7));
            
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x04,(byte) 0x3F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                               send_data, 0,0, null);
            buf = read(session,frame);
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 0, 3, temp, 0, 0));

        }catch(Exception e){
            log.error("configure read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"configure read error");
        }
        log.debug("========== Configure Read End ===============");
        return ba;
    }
    
    protected void ack(IoSession session) throws MRPException
    {
        try
        {
            OCTET destination = new OCTET(this.des);
            BYTE source = new BYTE(0x00);
            BYTE control = new BYTE(0x80);
            OCTET send_data = new OCTET(0);
     
            LK3410CP_005_RequestDataFrame frame 
                = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                       send_data, 0,0, null);

            IoBuffer buf = frame.getIoBuffer();
            session.write(buf); 
        }
        catch (Exception e)
        {
            log.error("ack error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Meter no answer");
        } 
    }
    
    protected LK3410CP_005_ReceiveDataFrame read(IoSession session,LK3410CP_005_RequestDataFrame frame) throws MRPException
    {
        int sequence = 0;
        boolean isRemain = true;
        try
        {
            IoBuffer buf = frame.getIoBuffer();
            session.write(buf); 
            LK3410CP_005_ReceiveDataFrame rcvFrame = new LK3410CP_005_ReceiveDataFrame();
            while(isRemain){
                byte[] message = (byte[])handler.getMessage(session,2);
                if(message != null && message.length > 0){
                    log.debug("receive read =>"+new OCTET(message).toHexString());
                    ack(session);
                    if(!checkRemain(message[11])){
                        isRemain = false;
                    }
                    rcvFrame.append(message);
                }
            }
            return rcvFrame;

        }
        catch (Exception e)
        {
            log.error("Read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
        } 
    }
    
    protected boolean checkRemain(byte b)
    {
        return ( b >> 7 == 0) ? true : false;
    }
    
    protected boolean isFirstFrame(byte b)
    {
        return ((b & 0x7F) >> 6 == 0) ? false : true;
    }
    
    protected boolean checkSequence(byte prev, byte current)
    {
        int prev_sequence  = (int)(prev & 0x3F);
        int current_sequence = (int)(current & 0x3F);
        return prev_sequence + 1 == current_sequence ? true : false;
    }

    public ByteArray billRead(IoSession session) throws MRPException
    {
        log.debug("========== Prev Bill Read Start ===============");
        ByteArray ba = new ByteArray();
        OCTET destination = new OCTET(this.des);
        BYTE source = new BYTE(0x00);
        BYTE control = new BYTE(0xC4);
        OCTET send_data = null;
        byte[] temp = null;
        LK3410CP_005_RequestDataFrame frame = null;
        LK3410CP_005_ReceiveDataFrame buf = null;
        try{
            
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x05,(byte) 0x1F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 180, 180, temp, 0, 0));
            
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x05,(byte) 0x2F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 180, 180, temp, 0, 0));  
            
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x05,(byte) 0x3F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 180, 180, temp, 0, 0));
            
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x05,(byte) 0x5F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 315, 315, temp, 0, 0));

        }catch(Exception e){
            log.error("prev bill read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Billing data read error");
        }
        log.debug("========== Prev Bill Read End ===============");
        return ba;
    }

    public boolean checkCRC(byte[] src) throws MRPException
    {
        return false;
    }

    public ByteArray currbillRead(IoSession session) throws MRPException
    {
        log.debug("========== Current Bill Read Start ===============");
        ByteArray ba = new ByteArray();
        OCTET destination = new OCTET(this.des);
        BYTE source = new BYTE(0x00);
        BYTE control = new BYTE(0xC4);
        OCTET send_data = null;
        byte[] temp = null;
        LK3410CP_005_RequestDataFrame frame = null;
        LK3410CP_005_ReceiveDataFrame buf = null;
        try{
            
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x05,(byte) 0x1F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 0, 180, temp, 0, 0));
            
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x05,(byte) 0x2F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 0, 180, temp, 0, 0));  
            
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x05,(byte) 0x3F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 0, 180, temp, 0, 0));
            
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x05,(byte) 0x5F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   send_data, 0,0, null);
            buf = read(session,frame);        
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 0, 315, temp, 0, 0));            
            
        }catch(Exception e){
            log.error("curr bill read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Current billing data read error");
        }
        log.debug("========== Current Bill Read End ===============");
        return ba;
    }

    public ByteArray eventlogRead(IoSession session) throws MRPException
    {
        log.debug("========== Event Log Read Start ===============");
        ByteArray ba = new ByteArray();
        OCTET destination = new OCTET(this.des);
        BYTE source = new BYTE(0x00);
        BYTE control = new BYTE(0xC4);
        OCTET send_data = null;
        LK3410CP_005_RequestDataFrame frame = null;
        
        try{
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x04,(byte) 0x2F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   send_data, 0,0, null);
            LK3410CP_005_ReceiveDataFrame buf = read(session,frame);
            
            byte[] temp = buf.encode();
            byte[] tempdrlog = new byte[120];
            byte[] drlog = new byte[120];
            int drcnt = 0;
            System.arraycopy(temp, 187, tempdrlog, 0, 120);
            for(int i = 0; i < 15; i++){
                if(tempdrlog[i*8] != 0x00){
                    System.arraycopy(tempdrlog, i*8, drlog, 8*drcnt, 8);
                    drcnt++;
                }
            }
            ba.append(DataUtil.arrayAppend(new byte[]{(byte)drcnt}, 0, 1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(drlog, 0, drcnt*8, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, 307, 2, temp, 0, 0));
            
            
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x04,(byte) 0x4F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   send_data, 0,0, null);
            buf = read(session,frame);
            
            temp = buf.encode();
            
            byte[] temperrlog = new byte[80];
            byte[] errlog = new byte[80];
            int errlogcnt = 0;
            System.arraycopy(temp, 2, temperrlog, 0, 80);
            for(int i = 0; i < 10; i++){
                if(temperrlog[i*8] != 0x00){
                    System.arraycopy(temperrlog, i*8, errlog, 8*errlogcnt, 8);
                    errlogcnt++;
                }
            }
            
            byte[] temperrrecv = new byte[80];
            byte[] errrecv = new byte[80];
            int errrecvcnt = 0;
            System.arraycopy(temp, 82, temperrrecv, 0, 80);
            for(int i = 0; i < 10; i++){
                if(temperrrecv[i*8] != 0x00){
                    System.arraycopy(temperrrecv, i*8, errrecv, 8*errrecvcnt, 8);
                    errrecvcnt++;
                }
            }
                  
            byte[] tempphaseerrA = new byte[105];
            byte[] phaseerrA = new byte[105];
            int phaseerrAcnt = 0;
            System.arraycopy(temp, 162, tempphaseerrA, 0, 105);
            for(int i = 0; i < 15; i++){
                if(tempphaseerrA[i*7] != 0x00){
                    System.arraycopy(tempphaseerrA, i*7, phaseerrA, 7*phaseerrAcnt, 7);
                    phaseerrAcnt++;
                }
            }
            
            byte[] tempphaseerrB = new byte[105];
            byte[] phaseerrB = new byte[105];
            int phaseerrBcnt = 0;
            System.arraycopy(temp, 267, tempphaseerrB, 0, 105);
            for(int i = 0; i < 15; i++){
                if(tempphaseerrB[i*7] != 0x00){
                    System.arraycopy(tempphaseerrB, i*7, phaseerrB, 7*phaseerrBcnt, 7);
                    phaseerrBcnt++;
                }
            }
            
            byte[] tempphaseerrC = new byte[105];
            byte[] phaseerrC = new byte[105];
            int phaseerrCcnt = 0;
            System.arraycopy(temp, 372, tempphaseerrC, 0, 105);
            for(int i = 0; i < 15; i++){
                if(tempphaseerrC[i*7] != 0x00){
                    System.arraycopy(tempphaseerrC, i*7, phaseerrC, 7*phaseerrCcnt, 7);
                    phaseerrCcnt++;
                }
            }
            
            byte[] tempphaserevA = new byte[105];
            byte[] phaserevA = new byte[105];
            int phaserevAcnt = 0;
            System.arraycopy(temp, 477, tempphaserevA, 0, 105);
            for(int i = 0; i < 15; i++){
                if(tempphaserevA[i*7] != 0x00){
                    System.arraycopy(tempphaserevA, i*7, phaserevA, 7*phaserevAcnt, 7);
                    phaserevAcnt++;
                }
            }
            
            byte[] tempphaserevB = new byte[105];
            byte[] phaserevB = new byte[105];
            int phaserevBcnt = 0;
            System.arraycopy(temp, 582, tempphaserevB, 0, 105);
            for(int i = 0; i < 15; i++){
                if(tempphaserevB[i*7] != 0x00){
                    System.arraycopy(tempphaserevB, i*7, phaserevB, 7*phaserevBcnt, 7);
                    phaserevBcnt++;
                }
            }
            
            byte[] tempphaserevC = new byte[105];
            byte[] phaserevC = new byte[105];
            int phaserevCcnt = 0;
            System.arraycopy(temp, 687, tempphaserevC, 0, 105);
            for(int i = 0; i < 15; i++){
                if(tempphaserevC[i*7] != 0x00){
                    System.arraycopy(tempphaserevC, i*7, phaserevC, 7*phaserevCcnt, 7);
                    phaserevCcnt++;
                }
            }
            
            ba.append(DataUtil.arrayAppend(new byte[]{(byte)errlogcnt}, 0, 1, errlog, 0, errlogcnt*8));
            ba.append(DataUtil.arrayAppend(new byte[]{(byte)errrecvcnt}, 0, 1, errrecv, 0, errrecvcnt*8));
           
            ba.append(DataUtil.arrayAppend(new byte[]{(byte)phaseerrAcnt}, 0, 1, phaseerrA, 0, phaseerrAcnt*7));
            ba.append(DataUtil.arrayAppend(new byte[]{(byte)phaseerrBcnt}, 0, 1, phaseerrB, 0, phaseerrBcnt*7));
            ba.append(DataUtil.arrayAppend(new byte[]{(byte)phaseerrCcnt}, 0, 1, phaseerrC, 0, phaseerrCcnt*7));

            ba.append(DataUtil.arrayAppend(new byte[]{(byte)phaserevAcnt}, 0, 1, phaserevA, 0, phaserevAcnt*7));
            ba.append(DataUtil.arrayAppend(new byte[]{(byte)phaserevBcnt}, 0, 1, phaserevB, 0, phaserevBcnt*7));
            ba.append(DataUtil.arrayAppend(new byte[]{(byte)phaserevCcnt}, 0, 1, phaserevC, 0, phaserevCcnt*7));
            
            
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x04,(byte) 0x6F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   send_data, 0,0, null);
            buf = read(session,frame);
            
            temp = buf.encode();
            
            byte[] tempPowerFail = new byte[7*14];
            byte[] powerFail = new byte[7*14];
            int powerFailcnt = 0;
            System.arraycopy(temp, 19, tempPowerFail, 0, 7*14);
            for(int i = 0; i < 14; i++){
                if(tempPowerFail[i*7] != 0x00){
                    System.arraycopy(tempPowerFail, i*7, powerFail, 7*powerFailcnt, 7);
                    powerFailcnt++;
                }
            }
            
            byte[] tempPowerRestore = new byte[7*14];
            byte[] powerRestore = new byte[7*14];
            int powerRestorecnt = 0;
            System.arraycopy(temp, 7*14+19, tempPowerRestore, 0, 7*14);
            for(int i = 0; i < 14; i++){
                if(tempPowerRestore[i*7] != 0x00){
                    System.arraycopy(tempPowerRestore, i*7, powerRestore, 7*powerRestorecnt, 7);
                    powerRestorecnt++;
                }
            }
            
            ba.append(DataUtil.arrayAppend(temp, 5, 14, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(new byte[]{(byte)powerFailcnt}, 0, 1, powerFail, 0, powerFailcnt*7));
            ba.append(DataUtil.arrayAppend(new byte[]{(byte)powerRestorecnt}, 0, 1, powerRestore, 0, powerRestorecnt*7));

        }catch(Exception e){
            log.error("event log read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Eventlog data read error");
        }
        log.debug("========== Event Log Read End ===============");
        return ba;
    }

    public void initProcess(IoSession session) throws MRPException
    {
        reset(session);
        byte[] key1 = auth1(session);
        auth2(session,key1);
    }

    protected void reset(IoSession session) throws MRPException
    {
        
        OCTET destination = new OCTET(new byte[6]);
        BYTE source = new BYTE(0x00);
        BYTE control = new BYTE(0xc0);     
 
        LK3410CP_005_RequestDataFrame frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   new OCTET(0), 0,0, null);

        try
        {
            IoBuffer buf = frame.getIoBuffer();
            log.debug("send reset=>"+buf.getHexDump());
            session.write(buf); 

            byte[] message = (byte[])handler.getMsg(session,13);
            if(message != null && message.length > 0){
                log.debug("receive reset =>"+new OCTET(message).toHexString());
            }
        }
        catch (Exception e)
        {
            log.error("Reset error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Reset meter error");
        } 
    }
    
    protected byte[] auth1(IoSession session) throws MRPException
    {
        byte[] za1za2 = {0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,
                         0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        OCTET destination = new OCTET(6);
        BYTE source = new BYTE(0x00);
        BYTE control = new BYTE(0xD1); 
        OCTET send_data = new OCTET(za1za2);

        LK3410CP_005_RequestDataFrame frame 
            = new LK3410CP_005_RequestDataFrame(null, destination, source, control,
                                   send_data, 0,0, null); 
                
        try
        {
            IoBuffer buf = frame.getIoBuffer();
            log.debug("send auth1=>"+buf.getHexDump());
            session.write(buf); 

            byte[] message = (byte[])handler.getMsg(session,40,29);
            if(message != null && message.length == 29){
                log.debug("receive auth1 =>"+new OCTET(message).toHexString());
                return new OCTET(message).getValue();
            }else{
                log.debug("message receive failed=>"+new OCTET(message).toHexString());
                throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Meter authentication error");
            }
        }
        catch (Exception e)
        {
            log.error(e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Meter authentication error");
        }
 
    }
    
    protected void auth2(IoSession session, byte[] response) throws MRPException
    {
        byte[] za1za2 = new byte[16]; 

        OCTET destination = new OCTET(6);
        BYTE source = new BYTE(0x00);
        BYTE control = new BYTE(0x82); 
        System.arraycopy(response,11, za1za2, 8, 8);
        OCTET send_data = new OCTET(za1za2);

        LK3410CP_005_RequestDataFrame frame 
            = new LK3410CP_005_RequestDataFrame(null, destination, source, control,
                                   send_data, 0,0, null); 
                
        try
        {
            IoBuffer buf = frame.getIoBuffer();
            log.debug("send auth2=>"+buf.getHexDump());
            session.write(buf);

            byte[] message = (byte[])handler.getMsg(session,13,13);
            if(message != null && message.length ==13){
                log.debug("receive auth2 =>"+new OCTET(message).toHexString());
                System.arraycopy(message, 3, this.des, 0, this.des.length);
            }
            else
            {
                throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Meter authentication error");
            }
        }
        catch (Exception e)
        {
            log.error(e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Meter authentication error");
        }
    }
    
    public ByteArray instRead(IoSession session) throws MRPException
    {
        log.debug("========== Instrument Read Start ===============");
        ByteArray ba = new ByteArray();
        OCTET destination = new OCTET(this.des);
        BYTE source = new BYTE(0x00);
        BYTE control = new BYTE(0xC4);
        OCTET send_data = null;
        LK3410CP_005_RequestDataFrame frame = null;
        
        try{
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x04,(byte) 0x7F});
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   send_data, 0,0, null);
            LK3410CP_005_ReceiveDataFrame buf = read(session,frame);
            byte[] temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 0, 149, temp, 0, 0));
        }catch(Exception e){
            log.error("instrument read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Instrument read data error");
        }
        log.debug("========== Instrument Read End ===============");
        return ba;
    }

    public ByteArray lpRead(IoSession session, String startday, String endday, int lpCycle) throws MRPException
    {
        log.debug("========== LoadProfile Read Start ===============");
        ByteArray ba = new ByteArray();
        OCTET destination = new OCTET(this.des);
        BYTE source = new BYTE(0x00);
        BYTE control = new BYTE(0xC4);
        OCTET send_data = new OCTET();

        try
        {
            send_data = new OCTET(new byte[]{(byte) 0xC0,0x01,0x02,(byte) 0x2F});
            LK3410CP_005_RequestDataFrame frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                               send_data, 0,0, null);
            LK3410CP_005_ReceiveDataFrame buf = read(session,frame);
            byte[] temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 0, 5, temp, 0, 0));
            
            byte[] send = new byte[]{(byte) 0xC0,0x01,0x06,(byte) 0x7F,0x00,0x00};
            int dayLPCount = (60/lpCycle)*24;
            int days = TimeUtil.getDayDuration(startday, TimeUtil.getCurrentDay())*dayLPCount;
            send[4] = DataUtil.get2ByteToInt(days)[0];
            send[5] = DataUtil.get2ByteToInt(days)[1];
            send_data = new OCTET(send);
            
            frame 
            = new LK3410CP_005_RequestDataFrame(new BYTE(), destination, source, control,
                                   send_data, 0,0, null);
            
            buf = read(session,frame);            
            temp = buf.encode();

            int lpCnt = temp.length/14;

            ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(lpCnt)));
            ba.append(DataUtil.arrayAppend(temp, 0, temp.length, temp, 0, 0));
        }
        catch (ParseException e)
        {
            log.error("lp read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Loadprofile read data error");
        }
        catch (Exception e)
        {
            log.error("lp read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Loadprofile read data error");
        } 
        log.debug("========== LoadProfile Read End ===============");
        return ba;
    }

    public ByteArray pqRead(IoSession session) throws MRPException
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
