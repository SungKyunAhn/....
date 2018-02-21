/** 
 * @(#)kV2c.java       1.0 2008-03-31 *
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

import com.aimir.constants.CommonConstants.MeterModel;
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
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;

import com.aimir.util.TimeUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * @author Park YeonKyoung  yeonkyoung@hanmail.net
 */
	
public class kV2c_Handler extends MeterProtocolHandler { 

    private static Log log = LogFactory.getLog(kV2c_Handler.class);
    private String meterId;
    private String modemId;
    
    private String groupId = "";
    private String memberId = "";
    private String mcuSwVersion = "";

    private byte before_txd_control;
    private byte meter_length;
    
    private byte[][] keys = new byte[16][48];

	/**
	 * Constructor.<p>
	 */
    protected long sendBytes;
    
    
	public kV2c_Handler() {	

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
        log.debug("==============kV2c_Handler start cmd:"+cmd+"================");
        if(cmd.equals("104.6.0")||cmd.equals("104.13.0"))
        {
            SMIValue[] smiValue = command.getSMIValue();
            
            if(smiValue != null && smiValue.length >= 2){
                int kind = ((BYTE)smiValue[1].getVariable()).getValue();
                
                if(smiValue.length == 4)
                {
                    nOffset = ((INT)smiValue[2].getVariable()).getValue();
                    nCount = ((INT)smiValue[3].getVariable()).getValue();

                    try
                    {
                        from = TimeUtil.getPreDay(TimeUtil.getCurrentTime(),nOffset);
                    }
                    catch (ParseException e)
                    {

                    }
                }
                else
                {
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
                ba.append(new byte[]{'N','G','2','A','1'});
                ba.append(new byte[]{0x00});
                ba.append(mcuId);
                ba.append(new byte[]{(byte)0x86});
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{0x00,0x00,0x00,0x00});
                
                switch(kind){
                case KV2C_DataConstants.CMD_ALL://all
                    initProcess(session);
                    mti = configureRead(session).toByteArray();
                    tpb = billRead(session).toByteArray();
                    tcb = currbillRead(session).toByteArray();
                    lpd = lpRead(session,from,"",15).toByteArray();
                    ist = instRead(session).toByteArray();
                    eld = eventlogRead(session).toByteArray();
                    ba.append(DataConstants.MTI);
                    ba.append(DataUtil.getReverseBytes(DataUtil.get2ByteToInt(mti.length+1)));
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

                    break;
                case KV2C_DataConstants.CMD_LCB:
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
                case KV2C_DataConstants.CMD_LPB:
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
                case KV2C_DataConstants.CMD_EVT:
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
                case KV2C_DataConstants.CMD_CHANNELINFO:
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
                log.debug("meterEntryData=>"+smiValue[0].getVariable().toString()+","+new OCTET(ba.toByteArray()).toHexString());
                meterDataEntry md = new meterDataEntry();
                md.setMdID(new OCTET(smiValue[0].getVariable().toString()));
                md.setMdSerial(new OCTET(this.meterId));
                md.setMdServiceType(new BYTE(1));//1: electric
                try {
                    md.setMdTime(new TIMESTAMP(TimeUtil.getCurrentTime()));
                } catch (ParseException e) {
                    log.warn(e);
                }
                md.setMdType(new BYTE(11));//11:mmiu
                md.setMdVendor(new BYTE(6));//LS LK3410
                md.setMdData(new OCTET(ba.toByteArray()));
                commandData = new CommandData();
                commandData.setCnt(new WORD(1));
                commandData.setTotalLength(ba.toByteArray().length);
                commandData.append(new SMIValue(new OID("10.1.0"),new OPAQUE(md)));
            }

        }

        log.debug("==============kV2c_Handler end ================");
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

        try {
            byte[] st01 = classRead(session,ANSI.READ_ST_01);
            byte[] st03 = classRead(session,ANSI.READ_ST_03);
            byte[] st05 = classRead(session,ANSI.READ_ST_05);
            byte[] st11 = classRead(session,ANSI.READ_ST_11);
            byte[] st21 = classRead(session,ANSI.READ_ST_21);
            byte[] mt13 = classRead(session,KV2C_DataConstants.READ_MT_13);
            byte[] mt64 = classRead(session,KV2C_DataConstants.READ_MT_64);
            byte[] mt67 = classRead(session,KV2C_DataConstants.READ_MT_67);
            byte[] mt70 = classRead(session,KV2C_DataConstants.READ_MT_70);
            byte[] mt75 = classRead(session,KV2C_DataConstants.READ_MT_75);
            byte[] mt78 = classRead(session,KV2C_DataConstants.READ_MT_78);
            byte[] mt86 = classRead(session,KV2C_DataConstants.READ_MT_86);
            byte[] mt87 = classRead(session,KV2C_DataConstants.READ_MT_87);

            byte[] st55 = classRead(session,ANSI.READ_ST_55);
            byte[] st61 = classRead(session,ANSI.READ_ST_61);//lp configuration
            byte[] mt00 = classRead(session,KV2C_DataConstants.READ_MT_00);
            
            log.debug("st01="+new OCTET(st01).toHexString());
            log.debug("st03="+new OCTET(st03).toHexString());
            log.debug("st05="+new OCTET(st05).toHexString());
            log.debug("st11="+new OCTET(st11).toHexString());
            log.debug("st21="+new OCTET(st21).toHexString());
            log.debug("mt13="+new OCTET(mt13).toHexString());
            log.debug("mt64="+new OCTET(mt64).toHexString());
            log.debug("mt67="+new OCTET(mt67).toHexString());
            log.debug("mt70="+new OCTET(mt70).toHexString());
            log.debug("mt75="+new OCTET(mt75).toHexString());
            log.debug("mt78="+new OCTET(mt78).toHexString());
            log.debug("mt86="+new OCTET(mt86).toHexString());
            log.debug("mt87="+new OCTET(mt87).toHexString());
            log.debug("st55="+new OCTET(st55).toHexString());
            log.debug("st61="+new OCTET(st61).toHexString());
            log.debug("mt00="+new OCTET(mt00).toHexString());            
          
            ba.append(DataUtil.arrayAppend(st01, 0, 4, st01, 4, 8));
            ba.append(DataUtil.arrayAppend(st01, 16, 16, st01, 0, 0));
            ba.append(DataUtil.arrayAppend(st03, 0, 5, st03, 0, 0));
            ba.append(DataUtil.arrayAppend(st55, 0, 6, st55, 0, 0));
            ba.append(DataUtil.arrayAppend(mt75, 10, 3, mt75, 13, 2));
            ba.append(DataUtil.arrayAppend(mt70, 2, 1, mt70, 5, 4));            
            ba.append(DataUtil.arrayAppend(mt86, 1, 1, mt86, 2, 1));
            ba.append(DataUtil.arrayAppend(mt87, 5, 1, mt87, 0, 0));            
            ba.append(DataUtil.arrayAppend(mt67, 150, 2, mt67, 152, 2));
            ba.append(DataUtil.arrayAppend(mt67, 154, 2, mt67, 0, 0));            
            ba.append(DataUtil.arrayAppend(mt78, 15, 5, mt78, 20, 10));
            ba.append(DataUtil.arrayAppend(st21, 0, 10, st21, 0, 0));

        }catch(Exception e){
            log.error(e,e);
        }
        
        log.debug("========== Configure Read End ===============");
        return ba;
    }
    
    protected void ack(IoSession session) throws MRPException
    {

    }
    
    public byte[] classRead(IoSession session, char meter_read_class) 
    throws MRPException 
    {
        byte[] read = null;
        this.meter_length = 3;
        byte kind = 1;

        byte[] read_class = new byte[2];
        read_class[0] = (byte)(meter_read_class >> 8);
        read_class[1] = (byte)(meter_read_class);

        try {
            KV2C_RequestDataFrame frame 
                = new KV2C_RequestDataFrame(this.before_txd_control,kind, ANSI.READ, new OCTET(read_class), 12, this.meter_length);
            IoBuffer buf = frame.getIoBuffer();
            this.before_txd_control = frame.getTXDControl();
            log.debug("CLASSREAD: [" +(int)meter_read_class+"]"
                                             +buf.getHexDump());
            session.write(buf);
            Thread.sleep(150);
            byte[] temp = read(session,ANSI.READ);            
   
            byte seq_num = temp[ANSI.OFS_SEQ_NBR];          
            read = cutHeaderTail(temp);

            while(seq_num != 0){
            byte[] rrread = RR(session);
            seq_num = rrread[ANSI.OFS_SEQ_NBR-1];
            rrread = cutHeaderTail(rrread);
            byte[] buffer = new byte[read.length];             
            System.arraycopy(read,0,buffer,0,read.length);
            read = new byte[buffer.length+rrread.length];
            System.arraycopy(buffer,0,read,0,buffer.length);
            System.arraycopy(rrread,0,read,buffer.length,rrread.length);               
            }

            read = cutLenChkSum(read);

            //log.debug("LAST READ: "+Util.getHexString(read));
            
        }catch(Exception e)
        {
            log.warn(e.getMessage());
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
        }
        return read;
    }
    
    public byte[] classRead(IoSession session, char meter_read_class, int len) 
    throws MRPException 
    {
        byte[] read = null;
        this.meter_length = 3;
        byte kind = 1;

        byte[] read_class = new byte[2];

        read_class[0] = (byte)(meter_read_class >> 8);
        read_class[1] = (byte)(meter_read_class);

        try {

            KV2C_RequestDataFrame frame 
                = new KV2C_RequestDataFrame(this.before_txd_control,kind, ANSI.READ, new OCTET(read_class), 12, this.meter_length);

            IoBuffer buf = frame.getIoBuffer();
            this.before_txd_control = frame.getTXDControl();
            log.debug("CLASSREAD: [" +(int)meter_read_class+"]"
                                             +buf.getHexDump());
            session.write(buf);
            Thread.sleep(500);
            read = read(session,ANSI.READ,false);
            
        }catch(Exception e)
        {
            log.warn(e.getMessage());
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
        }

        return read;
    }
    
    public byte[] classPartialRead(IoSession session, char meter_read_class,char meter_count) 
    throws MRPException 
    {
        int defaultsize = 256;
        int defaultread = 0xE6&0xFF;

        byte[] read_data = new byte[defaultsize];

        int poffset = 0;            
        int count = meter_count/defaultread;            
        if(meter_count%defaultread  != 0){
        count += 1;
        }

        for(int i = 0; i < count; i++){
        byte[] pkt = classPartialRead(session,meter_read_class,(char)poffset,(char) defaultread);           

        int blen = read_data.length;
        int len = blen + pkt.length;

        byte[] obuf = new byte[blen];
        System.arraycopy(read_data,0,obuf,0,blen);
        read_data = new byte[len];
        System.arraycopy(obuf,0,read_data,0,blen);
        System.arraycopy(pkt,0,read_data,blen,pkt.length);

        poffset += defaultread;
        }

        return read_data;
    }
    
    public byte[] classPartialRead(IoSession session, char meter_read_class, char poffset, char meter_count) 
    throws MRPException 
    {
        byte[] read_data = null;
        this.meter_length = 8;
        byte kind = 1;

        byte[] read_function = new byte[7];

        read_function[0] = (byte)(meter_read_class >> 8);
        read_function[1] = (byte)(meter_read_class);

        read_function[2] = 0x00;
        read_function[3] = (byte)(poffset >> 8);
        read_function[4] = (byte)(poffset);

        read_function[5] = (byte)(meter_count >> 8);// read data length
        read_function[6] = (byte)(meter_count);

        try {

            KV2C_RequestDataFrame frame 
                = new KV2C_RequestDataFrame(this.before_txd_control,kind, ANSI.PREAD, new OCTET(read_function), 17, this.meter_length);

            IoBuffer buf = frame.getIoBuffer();
            this.before_txd_control = frame.getTXDControl();
            log.debug("CLASSPARTIALREAD: ["  +(int)meter_read_class+"]"
                                             +buf.getHexDump());
            session.write(buf);
            Thread.sleep(500);
            byte[] temp = read(session,ANSI.READ,false);
      
            byte seq_num = temp[ANSI.OFS_SEQ_NBR];          
            read_data = cutHeaderTail(temp);

            while(seq_num != 0){
            byte[] rrread = RR(session);
            seq_num = rrread[ANSI.OFS_SEQ_NBR-1];
            rrread = cutHeaderTail(rrread);
            byte[] buffer = new byte[read_data.length];                
            System.arraycopy(read_data,0,buf,0,read_data.length);
            read_data = new byte[buffer.length+rrread.length];
            System.arraycopy(buffer,0,read_data,0,buffer.length);
            System.arraycopy(rrread,0,read_data,buffer.length,rrread.length);              
            }

            read_data = cutLenChkSum(read_data);

            //log.debug("LAST READ: "+Util.getHexString(read_data));

        }catch(Exception e)
        {
            log.warn(e.getMessage());
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
        }

        return read_data;
    }
    
    /**
     * Continue Read
     * @param read
     * @return
     */
    public byte[] RR(IoSession session) throws Exception {

        session.write(new BYTE(ANSI.ACK));
        Thread.sleep(1000);
        byte[] temp = null;
        temp = read(session, ANSI.RR, true);
        return temp;
    }
    
    /**
     * Get Receive Class Read Data.<p>
     * @return
     */
    protected byte[] read(IoSession session,KV2C_RequestDataFrame frame,int size, byte meter_cmd) 
                        throws MRPException {

        byte[] read = null;
        try {
            read = (byte[])handler.getMsg(session,13,size);
            //log.debug("READ: "+Util.getHexString(read));
            if(!check_data_frame(read,size,meter_cmd)){
                throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
            }

        } catch(MRPException e){
            throw new MRPException(e.getType());
        } catch (Exception e) {
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
        }
        return read;        
    }
        
    protected byte[] read(IoSession session,byte meter_cmd) 
                        throws MRPException {

        byte[] read = null;
        long timeout = 15;

        try {

            int i = 0;
            int idx = 0;
            int headerlen = 0;
            int ofs_len = 0;

            ofs_len   = ANSI.OFS_LENGTH;
            read = (byte[])handler.getMessage(session,ofs_len,MeterModel.GE_KV2C.getCode().intValue());

            if(!check_data_frame(read,read.length,meter_cmd))
                throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
            } catch(MRPException e){
                log.warn(e,e);
                throw new MRPException(e.getType());  
            } catch (Exception e) {
                log.warn(e,e);
                throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
            }

            return read;
    }

    protected byte[] read(IoSession session,byte meter_cmd, boolean rr) 
                        throws MRPException {

        byte[] read = null;
        long timeout = 15;
       
        try {
            
            int i = 0;
            int idx = 0;
            int headerlen = 0;
            int ofs_len = 0;
            
            if(rr){             
                headerlen = ANSI.LEN_HEADER-1;
                ofs_len   = ANSI.OFS_LENGTH-1;
            }else {
                headerlen = ANSI.LEN_HEADER;
                ofs_len   = ANSI.OFS_LENGTH;
            }
            
            byte[] temp = new byte[headerlen];
                        
            read = (byte[])handler.getMsg(session,timeout,headerlen);
            
            /*
            int rlen = DataUtil.getIntTo2Byte(
                        DataUtil.select(temp,ofs_len,2));
            read = new byte[headerlen+rlen+ANSI.LEN_CRC];

            if(temp.length < headerlen+rlen+ANSI.LEN_CRC){    
                log.debug("read more");
                System.arraycopy(temp,0,read,0,temp.length);
                idx = temp.length;
                            
                i = 0;
                
                temp = (byte[])handler.getMsg(session,timeout,rlen+ANSI.LEN_CRC);
                System.arraycopy(temp,0,read,idx,temp.length);
            }else{
                log.debug("read done");
                read = temp;
            }*/

            if(!check_data_frame(read,read.length,meter_cmd))
                throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
        } catch(MRPException e){
            log.warn(e,e);
            throw new MRPException(e.getType());  
        } catch (Exception e) {
            log.warn(e,e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
        }

        return read;
    }
    
    protected byte[] cutHeaderTail(byte[] b) {
        
        byte[] data;

        try{

            if(b[0] == ANSI.ACK && b[1] == ANSI.STP){
                int rlen = b.length-(ANSI.LEN_HEADER+ANSI.LEN_CRC);
                data = new byte[rlen];
                System.arraycopy(b,ANSI.LEN_HEADER,data,0,rlen);
                return data;
            }else if(b[0] == ANSI.STP){
                int rlen = b.length-(ANSI.LEN_HEADER-1+ANSI.LEN_CRC);
                data = new byte[rlen];
                System.arraycopy(b,ANSI.LEN_HEADER-1,data,0,rlen);
                return data;
            }else {
                data = new byte[0];
                return data;
            }

        }catch(Exception e){
            log.warn("CUT HEADER TAIL ERROR : "+e.getMessage());
        }
        
        return null;

    }
    
    private byte[] cutLenChkSum(byte[] b) {
        
        byte[] data;

        try{
            int rlen = b.length-4;
            data = new byte[rlen];
            System.arraycopy(b,3,data,0,rlen);
            return data;

        }catch(Exception e){
            log.warn("CUT LEN CHECKSUM ERROR : "+e.getMessage());
        }
        return null;
    }
 
    public ByteArray billRead(IoSession session) throws MRPException
    {
        log.debug("========== Prev Bill Read Start ===============");
        ByteArray ba = new ByteArray();
        //classPartialRead(session,ANSI.READ_ST_15,
        //                                    (char)(st11.getNBR_CONSTANTS_ENTRIES()*ST15.BLK_SIZE)),
        //                                    st11.getNBR_CONSTANTS_ENTRIES();

        //byte[] st21 = classRead(session,ANSI.READ_ST_21);
        //byte[] st22 = classRead(session,ANSI.READ_ST_22);
        byte[] st25 = classPartialRead(session,ANSI.READ_ST_25,(char)732);
        ba.append(st25);

        log.debug("========== Prev Bill Read End ===============");
        return ba;
    }

    public boolean checkCRC(byte[] src) throws MRPException
    {
        return false;
    }
    
    private boolean check_data_frame(byte[] buf, int len, byte meter_cmd){
        
        log.debug("check_data_frame:"+new OCTET(buf).toHexString());
        boolean ret = true;
        
        if(len == 0) 
        {
            log.debug("length check error");
            ret = false;
        }
    
        if(buf == null || buf.length == 0) 
        {
            log.debug("length check error");
            ret = false;
        }

        if(meter_cmd == ANSI.RR) 
        {
            if(buf[0] != ANSI.STP) {
                log.debug("header format error");
                ret = false;        
            }
        }
        else 
        {
            if(buf[0] == ANSI.STP)
            {
                return true;
            }
            if(buf[0] == (byte)0xFF || buf[0] == (byte)0xFE)
            {
                if(buf[1] == ANSI.NAK || buf[2] != ANSI.STP){
                    log.debug("header format error");
                    return false;  
                }
            }            
            else if(buf[0] == ANSI.NAK || buf[1] != ANSI.STP)
            {
                log.debug("header format error");
                return false;       
            }
        }

        /*
        if(!ANSI.crc_check(buf, len, meter_cmd))
        {
            log.debug("crc check error");
            return false;
        }
        */

        return ret;
    }

    public ByteArray currbillRead(IoSession session) throws MRPException
    {
        log.debug("========== Current Bill Read Start ===============");
        ByteArray ba = new ByteArray();
        byte[] st23 = classPartialRead(session,ANSI.READ_ST_23,(char)731);
        ba.append(st23);
        log.debug("========== Current Bill Read End ===============");
        return ba;
    }

    public ByteArray eventlogRead(IoSession session) throws MRPException
    {
        log.debug("========== Event Log Read Start ===============");
        ByteArray ba = new ByteArray();
        byte eventCount = 0x00;
        byte[] st76 = classPartialRead(session,ANSI.READ_ST_76,(char)731);
        ba.append(new byte[]{eventCount});
        ba.append(st76);
        log.debug("========== Event Log Read End ===============");
        return ba;
    }

    public void initProcess(IoSession session) throws MRPException
    {
        
        String passwd = FMPProperty.getProperty("circuit.meter.security.password.KV2C",
                                                "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        
        byte[] pwd = Hex.encode(passwd);
        /*
        byte[] pwd //20 byte password
        = { (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
            (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
            (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
            (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};//Philippine PWD
            */
        /*
        byte[] pwd //20 byte password
        = { 0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,
            0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20};//TPC PWD
        */
        /*
        byte[] pwd //20 byte password
        = { 0x12,0x34,0x56,0x78,(byte) 0x90,0x12,0x34,0x56,0x78,(byte) 0x90,
            0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20};//PEA PWD
        */
        
        identRequest(session);
        negotiate(session);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
        login(session);
        authenticate(pwd,session);
       
    }
    
    /**
     * ident 0xee 0x00 0x00 0x00 0x00 0x01 0x20 0x13 0x10
     * @throws MRPException
     */
    protected void identRequest(IoSession session) throws MRPException {

        byte[] read   = new byte[26];
        byte[] passwd = new byte[8];
        
        this.before_txd_control = 0x00;
        this.meter_length       = 1;
        byte kind = 0;
        
        try {
            //Thread.sleep(500);
            KV2C_RequestDataFrame frame 
                = new KV2C_RequestDataFrame(this.before_txd_control,kind,ANSI.IDENT,9, this.meter_length);

            IoBuffer buf = frame.getIoBuffer();
            this.before_txd_control = frame.getTXDControl();
            log.debug("IDENTIFICATION: "+buf.getHexDump());
            session.write(buf);
            
            Thread.sleep(100);
            log.debug("READ IDENT RESPONSE");
            read = read(session,ANSI.IDENT,false);
        }catch(Exception e){
            log.warn(e,e);
            throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR);
        }

    }
    
    protected void negotiate(IoSession session) throws MRPException {
        
        byte read[] = new byte[14];
        this.meter_length = 5;
        //byte[] negotiate = {0x02, 0x00, (byte)0x08};
        byte[] negotiate = {0x01,0x00,0x01,0x06};
        byte kind = 1;
        
        try {
            KV2C_RequestDataFrame frame 
                = new KV2C_RequestDataFrame(this.before_txd_control,kind,ANSI.NEGO_B1, new OCTET(negotiate),14, this.meter_length);

            IoBuffer buf = frame.getIoBuffer();
            this.before_txd_control = frame.getTXDControl();
            log.debug("NEGOTIATE: "+buf.getHexDump());
            session.write(buf);
            Thread.sleep(100);
            read = read(session,ANSI.NEGO,false);
            
        }catch(Exception e){
            log.warn(e.getMessage());
            throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR);
        }
        
    }
    
    /**
     * user_id  2 byte
     * user    10 byte
     */
    protected byte[] login(IoSession session) throws MRPException {
        
        
        String login = FMPProperty.getProperty("Circuit.Meter.Security.LoginId.KV2C",
                                                "000261696D69720000000000");//XXaimirXX

        byte[] loginid = Hex.encode(login);
        
        
        byte read[] = new byte[10];
        this.meter_length = 13;
        //byte[] loginid 
        //    = { 0x00, 0x04, 'm','t', 'e','s', 't', 
        //        0x20, 0x20, 0x20, 0x20, 0x20};//new
        /*        
        byte[] loginid 
        = { 0x00, 0x02, 'a','i', 'm','i', 'r', 
            0x00, 0x00, 0x00, 0x00, 0x00};//new
            */
        
        byte kind = 1;
        
        try {
            KV2C_RequestDataFrame frame 
                = new KV2C_RequestDataFrame(this.before_txd_control,
                                            kind,ANSI.LOGON,new OCTET(loginid),22, this.meter_length);

            IoBuffer buf = frame.getIoBuffer();
            this.before_txd_control = frame.getTXDControl();
            log.debug("LOGIN: "+buf.getHexDump());
            session.write(buf);

            Thread.sleep(100);
            read = read(session,ANSI.LOGON,false);

            return null;
        }catch(Exception e){
            log.warn(e.getMessage());
            throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR);
        }
    }
    
    public void authenticate(byte[] meter_pwd, IoSession session) throws MRPException {
        
        String auth = FMPProperty.getProperty("Circuit.Meter.Security.AuthCode.KV2C",
                                              "6666666666666666666666666666666666666666");

        byte[] auth_code = Hex.encode(auth);
        byte[] read = new byte[20];
        this.meter_length = 0x15;
        byte kind = 1;
        
        /*
        byte[] auth_code = new byte[20];
        auth_code = new byte[]{0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,
                               0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66};
        */
        //auth_code[0] = 0x09;
        //auth_code[1] = 0x00;
        
        try {
            //System.arraycopy(meter_pwd,0,auth_code,0,meter_pwd.length);

            KV2C_RequestDataFrame frame 
                = new KV2C_RequestDataFrame(this.before_txd_control,
                                            kind,ANSI.OPT_AUTH,new OCTET(auth_code),30, this.meter_length);
            IoBuffer buf = frame.getIoBuffer();
            this.before_txd_control = frame.getTXDControl();
            log.debug("AUTUENTICATE: "+buf.getHexDump());
            session.write(buf);
            Thread.sleep(100);
            read = read(session,ANSI.AUTH,false);
        }catch(Exception e){
            log.warn(e.getMessage());
            throw new MRPException(MRPError.ERR_HEADER_FORMAT_ERROR);
        }
    }
    
    protected void xor(byte[] dst, byte[] src, int len){

        for(int i = 0; i < len ; i++) 
            dst[i] ^= src[i];
    }

    protected void sboxes(byte[] dst,int dstart, 
                        byte[] src, int sstart, 
                        byte[] sbox) {
    
        int i;

        i  = src[sstart+4];
        i |= src[sstart+3] << 1;
        i |= src[sstart+2] << 2;
        i |= src[sstart+1] << 3;
        i |= src[sstart+5] << 4;
        i |= src[sstart+0] << 5;

        i = sbox[i];

        dst[dstart+3] = (byte) (i & 1);
        dst[dstart+2] = (byte) (i >> 1 & 1);
        dst[dstart+1] = (byte) (i >> 2 & 1);
        dst[dstart+0] = (byte) (i >> 3 & 1);

    }

    protected void copy(byte[] dst, int dstart, byte[] src, 
                     int sstart, int len){

        for(int i = 0; i < len ; i++) 
            dst[dstart+i] = src[sstart+i];  
    }


    protected void permutation(byte[] dst, byte[] src,
                            int sstart, int len, byte[] perm_table) {
        
        byte[] tmp = new byte[64];
        
        if(src == null) {
            src = tmp;
            System.arraycopy(dst,0,src,sstart,64);
        }

        for(int i = 0; i < len; i++) {
            dst[i] = src[sstart+perm_table[i]-1];
        }

    }


    protected void des(byte[] key_data, byte[] ticket, int _encrypt)   {

        byte[] des_key    = new byte[64];
        byte[] des_data   = new byte[64];
        byte[] des_right  = new byte[64];

        int  i, j;

        permutation(des_key, key_data, 0, 56, ANSI.PERM1);
        
        byte[] NULL = null;
        
        for(i=1 ; i<=16 ; i++){

            permutation(des_key, NULL, 0, 56, ANSI.PERM2);
            
            if((i != 1) && (i != 2) && (i != 9) && (i != 16)) 
                permutation(des_key, NULL, 0, 56, ANSI.PERM2);

            int encrypt = 0;
            if(_encrypt != 0)
                encrypt = i-1;
            else
                encrypt = 16-i;

            permutation(keys[encrypt],des_key, 0, 48, ANSI.PERM3);
        }

        permutation(des_data,ticket, 0, 64, ANSI.PERM4);
        
        for(i=1 ; i<=16 ; i++) {

            permutation(des_right, des_data, 32, 48, ANSI.PERM5);

            xor(des_right, keys[i - 1], 48);

            for(j=0 ; j<8 ; j++) 
                sboxes(des_right, 4*j, des_right, 6 * j, ANSI.SBOXES[j]);

            permutation(des_right, NULL, 0, 32, ANSI.PERM6);    
            xor(des_right, des_data, 32);
            copy(des_data, 0,  des_data,  32, 32);
            copy(des_data, 32, des_right, 0 , 32);
        }
        copy(ticket, 0,  des_data, 32, 32);
        copy(ticket, 32, des_data, 0,  32);
        permutation(ticket,NULL, 0, 64, ANSI.PERM7);

    }

    protected byte[] encrypt_password(byte[] meter_password) {
        
        int i;
        byte[] ticket   = new byte[64];
        byte[] key_data = new byte[64];
        byte[] pwd      = new byte[8];
        byte[] e_key = {0x31, 0x32, 0x33, 0x34, 
                        0x32, 0x30, 0x30, 0x34};//12342004 new
        //byte[] e_key = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30};//old 0000
        //byte[] e_key = {0x30, 0x36, 0x31, 0x37, 0x34, 0x30, 0x33, 0x30};//test
        //byte[] e_key = {(byte) 0xCD, 0x38, (byte) 0xF8, 0x1F, (byte) 0x80, (byte) 0xFE, 0x23, 0x0E};//test
        
        for(i=0 ; i<8 ; i++) 
            ticket[ 0 + i] = (byte) ((meter_password[0] >> (7 - i)) & 0x01);
        for(i=0 ; i<8 ; i++)  
            ticket[ 8 + i] = (byte) ((meter_password[1] >> (7 - i)) & 0x01);
        for(i=0 ; i<8 ; i++) 
            ticket[16 + i] = (byte) ((meter_password[2] >> (7 - i)) & 0x01);
        for(i=0 ; i<8 ; i++) 
            ticket[24 + i] = (byte) ((meter_password[3] >> (7 - i)) & 0x01);
        for(i=0 ; i<8 ; i++) 
            ticket[32 + i] = (byte) ((meter_password[4] >> (7 - i)) & 0x01);
        for(i=0 ; i<8 ; i++) 
            ticket[40 + i] = (byte) ((meter_password[5] >> (7 - i)) & 0x01);
        for(i=0 ; i<8 ; i++)
            ticket[48 + i] = (byte) ((meter_password[6] >> (7 - i)) & 0x01);
        for(i=0 ; i<8 ; i++) 
            ticket[56 + i] = (byte) ((meter_password[7] >> (7 - i)) & 0x01);
        
        for(i=0 ; i<8 ; i++) 
            key_data[ 0 + i] = (byte) ((e_key[0] >> (7 - i)) & 0x01);
        for(i=0 ; i<8 ; i++) 
            key_data[ 8 + i] = (byte) ((e_key[1] >> (7 - i)) & 0x01);
        for(i=0 ; i<8 ; i++) 
            key_data[16 + i] = (byte) ((e_key[2] >> (7 - i)) & 0x01);
        for(i=0 ; i<8 ; i++) 
            key_data[24 + i] = (byte) ((e_key[3] >> (7 - i)) & 0x01);
        for(i=0 ; i<8 ; i++) 
            key_data[32 + i] = (byte) ((e_key[4] >> (7 - i)) & 0x01);
        for(i=0 ; i<8 ; i++) 
            key_data[40 + i] = (byte) ((e_key[5] >> (7 - i)) & 0x01);
        for(i=0 ; i<8 ; i++) 
            key_data[48 + i] = (byte) ((e_key[6] >> (7 - i)) & 0x01);
        for(i=0 ; i<8 ; i++) 
            key_data[56 + i] = (byte) ((e_key[7] >> (7 - i)) & 0x01);

        des(key_data, ticket, 1);

        for(i=0; i<8; i++) 
            pwd[0] |= ((ticket[i + 0 ] & 0x01) << (7 - i));
        for(i=0; i<8; i++) 
            pwd[1] |= ((ticket[i + 8 ] & 0x01) << (7 - i));
        for(i=0; i<8; i++) 
            pwd[2] |= ((ticket[i + 16] & 0x01) << (7 - i));
        for(i=0; i<8; i++) 
            pwd[3] |= ((ticket[i + 24] & 0x01) << (7 - i));
        for(i=0; i<8; i++) 
            pwd[4] |= ((ticket[i + 32] & 0x01) << (7 - i));
        for(i=0; i<8; i++) 
            pwd[5] |= ((ticket[i + 40] & 0x01) << (7 - i));
        for(i=0; i<8; i++) 
            pwd[6] |= ((ticket[i + 48] & 0x01) << (7 - i));
        for(i=0; i<8; i++) 
            pwd[7] |= ((ticket[i + 56] & 0x01) << (7 - i));
        
        return pwd;
    }
    
    protected void waitprocess(IoSession session) throws Exception {

        byte[] read = new byte[10];
        byte[] msg = new byte[]{0x0f};//sec
        byte kind = 1;
        this.meter_length = 2;

        KV2C_RequestDataFrame frame 
            = new KV2C_RequestDataFrame(this.before_txd_control,kind,ANSI.WAIT,new OCTET(msg),11,this.meter_length);

        IoBuffer buf = frame.getIoBuffer();
        this.before_txd_control = frame.getTXDControl();
        log.debug("Send\n "+buf.getHexDump());
        session.write(buf);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        read = read(session,ANSI.WAIT,false);
    }
    
    public void executeHealth(IoSession session) throws Exception {
        

        byte[] read = new byte[10];
        byte kind = 1;
        
        byte[] proc_id = new byte[] {(byte)0x0B,0x00};
        byte seq = 0x00;
        this.meter_length = 9;
        byte[] msg = new byte[7];
        
        int idx = 0;
        msg[idx++] = 0x00;
        msg[idx++] = 0x07;
        msg[idx++] = 0x00;
        msg[idx++] = 0x03;
        msg[idx++] = proc_id[0];
        msg[idx++] = proc_id[1];
        msg[idx++] = seq;

        KV2C_RequestDataFrame frame 
            = new KV2C_RequestDataFrame(this.before_txd_control,kind,ANSI.WRITE,new OCTET(msg),0,18,true,this.meter_length);

        IoBuffer buf = frame.getIoBuffer();
        this.before_txd_control = frame.getTXDControl();
        log.debug("Send\n "+buf.getHexDump());
        session.write(buf);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        read = read(session,ANSI.WRITE,false);
    }
    
    public void programcontrol(IoSession session, byte control, byte[] datetime) 
    throws Exception 
    {

        byte[] tableid = new byte[2];
        byte kind = 1;
        tableid[0] = (byte)(ANSI.READ_ST_07 >> 8);
        tableid[1] = (byte)ANSI.READ_ST_07;
        byte[] proc_id_m70 = new byte[]{(byte)0x46,0x08};//Program Control

        byte seq = 0x00;
        this.meter_length = 15;

        byte[] msg = new byte[13];

        int idx = 0;
        msg[idx++] = tableid[0];//table id
        msg[idx++] = tableid[1];//table id
        msg[idx++] = 0x00;//len
        msg[idx++] = 0x04;//len
        msg[idx++] = proc_id_m70[0];
        msg[idx++] = proc_id_m70[1];
        msg[idx++] = seq;
        msg[idx++] = datetime[0];
        msg[idx++] = datetime[1];
        msg[idx++] = datetime[2];
        msg[idx++] = datetime[3];
        msg[idx++] = datetime[4];

        KV2C_RequestDataFrame frame 
            = new KV2C_RequestDataFrame(this.before_txd_control,kind,ANSI.WRITE,new OCTET(msg),4,24,true,this.meter_length);

        IoBuffer buf = frame.getIoBuffer();
        this.before_txd_control = frame.getTXDControl();
        log.debug("Send\n "+buf.getHexDump());
        session.write(buf);
        try{ Thread.sleep(500); }catch(InterruptedException e){}

    }
    
    public void resetlistpointers(IoSession session, byte listtype) 
    throws Exception 
    {

        byte[] tableid = new byte[2];
        byte kind = 1;
        tableid[0] = (byte)(ANSI.READ_ST_07 >> 8);
        tableid[1] = (byte)ANSI.READ_ST_07;
        byte[] proc_id_s04 = new byte[]{(byte)0x04,0x00};//Reset List Pointers

        byte seq = 0x00;
        this.meter_length = 10;

        byte[] msg = new byte[8];

        int idx = 0;
        msg[idx++] = tableid[0];//table id
        msg[idx++] = tableid[1];//table id
        msg[idx++] = 0x00;//len
        msg[idx++] = 0x04;//len
        msg[idx++] = proc_id_s04[0];
        msg[idx++] = proc_id_s04[1];
        msg[idx++] = seq;
        msg[idx++] = listtype;

        KV2C_RequestDataFrame frame 
            = new KV2C_RequestDataFrame(this.before_txd_control,kind,ANSI.WRITE,new OCTET(msg),4,19,true,this.meter_length);

        IoBuffer buf = frame.getIoBuffer();
        this.before_txd_control = frame.getTXDControl();
        log.debug("Send\n "+buf.getHexDump());
        session.write(buf);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
    }
    
    protected byte[] remotereset(IoSession session, byte actionflag) 
    throws Exception {

        byte[] tableid = new byte[2];
        byte kind = 1;
        tableid[0] = (byte)(ANSI.READ_ST_07 >> 8);
        tableid[1] = (byte)ANSI.READ_ST_07;
        byte[] proc_id_s09 = new byte[]{(byte)0x09,0x00};//Remote Reset
        byte seq = 0x00;
        this.meter_length = 10;

        byte[] msg = new byte[8];

        int idx = 0;
        msg[idx++] = tableid[0];//table id
        msg[idx++] = tableid[1];//table id
        msg[idx++] = 0x00;//len
        msg[idx++] = 0x04;//len
        msg[idx++] = proc_id_s09[0];
        msg[idx++] = proc_id_s09[1];
        msg[idx++] = seq;
        msg[idx++] = actionflag;

        KV2C_RequestDataFrame frame 
            = new KV2C_RequestDataFrame(this.before_txd_control,kind,ANSI.WRITE,new OCTET(msg),4,19,true,this.meter_length);

        IoBuffer buf = frame.getIoBuffer();
        this.before_txd_control = frame.getTXDControl();
        log.debug("Send\n "+buf.getHexDump());
        session.write(buf);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        byte[] read = read(session,ANSI.WRITE,false);
        return  read;
    }
  
    public ByteArray instRead(IoSession session) throws MRPException
    {
        log.debug("========== Instrument Read Start ===============");
        ByteArray ba = new ByteArray();
        byte[] mt110 = classPartialRead(session,KV2C_DataConstants.READ_MT_110,
                                       (char)KV2C_DataConstants.LEN_MT_110);
        byte[] mt72 = classPartialRead(session,KV2C_DataConstants.READ_MT_72,
                                        (char)KV2C_DataConstants.LEN_MT_72);
        
        ba.append(mt110);
        ba.append(mt72);
        log.debug("========== Instrument Read End ===============");
        return ba;
    }

    public ByteArray lpRead(IoSession session, String startday, String endday, int lpCycle) 
            throws MRPException
    {
        log.debug("========== LoadProfile Read Start ===============");
        ByteArray ba = new ByteArray();
        byte[] st61 = classRead(session,ANSI.READ_ST_61);//lp configuration
        byte[] st62 = classRead(session,ANSI.READ_ST_62);//lp configuration
        byte[] st64 = classPartialRead(session,ANSI.READ_ST_64,
                                       (char)KV2C_DataConstants.LEN_MT_72);
        ba.append(st61);
        ba.append(st62);
        ba.append(st64);
        log.debug("========== LoadProfile Read End ===============");
        return ba;
    }

    public ByteArray pqRead(IoSession session) throws MRPException
    {
        log.debug("========== Instrument Read Start ===============");
        ByteArray ba = new ByteArray();
        byte[] mt72 = classPartialRead(session,KV2C_DataConstants.READ_MT_72,
                                       (char)KV2C_DataConstants.LEN_MT_72);
        byte[] mt78 = classPartialRead(session,KV2C_DataConstants.READ_MT_78,
                                       (char)KV2C_DataConstants.LEN_MT_78);
        ba.append(mt72);
        ba.append(mt78);
        log.debug("========== Instrument Read End ===============");
        return ba;
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
