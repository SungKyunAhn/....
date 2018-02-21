/** 
 * @(#)KDH_Handler.java        *
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

import com.aimir.util.TimeUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * @author Kang, Soyi
 */
	
public class KDH_Handler extends MeterProtocolHandler { 

    private static Log log = LogFactory.getLog(KDH_Handler.class);
    private String meterId;
    private String modemId;
    
    private String groupId = "";
    private String memberId = "";
    private String mcuSwVersion = "";

	/**
	 * Constructor.<p>
	 */
    protected long sendBytes;
    
	public KDH_Handler() {	
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
        byte[] lpd = null;
        byte[] evd = null;
        ByteArray ba = new ByteArray();
        int nOffset = 0;
        int nCount = 96;
        String from = "";
        String cmd = command.getCmd().getValue();
        log.debug("==============KDH_Handler start cmd:"+cmd+"================");
        if(cmd.equals("104.6.0")||cmd.equals("104.13.0"))
        {
            SMIValue[] smiValue = command.getSMIValue();
            
            if(smiValue != null && smiValue.length >= 2){
                int kind = ((INT)smiValue[1].getVariable()).getValue();
                
                if(smiValue.length == 4){
                    nOffset = ((INT)smiValue[2].getVariable()).getValue();
                    nCount = ((INT)smiValue[3].getVariable()).getValue();
                    if(nOffset>255) nOffset=255;
                    log.debug("nOffset :"+ nOffset);
                    log.debug("nCount :"+ nCount);
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

                ba.append(new byte[]{'N','C','5','A','1'});
                ba.append(new byte[]{0x00});
                ba.append(mcuId);
                ba.append(new byte[]{(byte)0x8B});//WIZIT HEAT METER
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00});//ZIGBEE ID
                ba.append(new byte[]{0x00,0x00,0x00,0x00});
              //  initProcess(session);
                
                try {
                    lpd = lpRead(session,nOffset,TimeUtil.getCurrentTime(),true, (byte)0x00).toByteArray();
                    ba.append(lpd);
                    lpd = lpRead(session,nOffset,TimeUtil.getCurrentTime(),false, (byte)0x01).toByteArray();
                    ba.append(lpd);
                    lpd = lpRead(session,nOffset,TimeUtil.getCurrentTime(),false, (byte)0x02).toByteArray();
                    ba.append(lpd);
                    evd= currbillRead(session).toByteArray();
                    ba.append(evd);
                }
                catch (Exception e) {
                    log.warn(e);
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
                md.setMdServiceType(new BYTE(6));//6: HEAT
                md.setMdTime(new TIMESTAMP(currTime));
                md.setMdType(new BYTE(13));//13:ieiu
                md.setMdVendor(new BYTE(7));//WIZIT
                md.setMdData(new OCTET(ba.toByteArray()));
                commandData = new CommandData();
                commandData.setCnt(new WORD(1));
                commandData.setTotalLength(ba.toByteArray().length);
                commandData.append(new SMIValue(new OID("10.1.0"),new OPAQUE(md)));
            }
        }

        log.debug("==============KDH_Handler end ================");
        return commandData;
    }

    public ByteArray configureRead(IoSession session) throws MRPException
    {
        return null;
    }
    
	/**
	 * Read Cumulative Data.<p>
	 * @return
	 * @throws MeterException
	 */
	public ByteArray currbillRead(IoSession session) throws MRPException
	{
    	log.debug("========== currbillRead Read Start ===============");
        ByteArray ba = new ByteArray();  
        byte[] temp = null;
        KDH_RequestDataFrame frame = null;
        KDH_ReceiveDataFrame buf = null;

		byte cmd = KDH_DataConstants.CONTROL_GET_CURRENT[0];
    	int ctrSeq =0;
    	byte[] send= new byte[2];
    	byte remainData =(byte) 0x00;
    	
    	try{
	    	while(remainData == (byte) 0x00){
	    		remainData ++;
	    		cmd = KDH_DataConstants.CONTROL_GET_CURRENT[ctrSeq%2];
	    		int idx =0;
	    		send[idx++]=cmd;
	    		send[idx++]=(byte)0x01;//a field
	    		OCTET send_data = new OCTET(send);
		        frame  = new KDH_RequestDataFrame(new BYTE(), send_data, 0, 0, null);
		        buf = read(session,frame,1);   
		        log.debug("test1");
	            if(buf!=null){
	            	temp = buf.encode();	
	            	if(temp !=null && temp.length>0 && temp[8]== (byte)0x00){
	            		ba.append(new byte[] {KDH_DataConstants.DATA_TYPE_CURRENT});
	            		int dataLen = temp.length-11;
			            ba.append(new byte[] {(byte)((dataLen & 0xff)>>8), (byte)(dataLen & 0xff) }); //length
			          //date & time
			            ba.append(parseYyyymmddhhmmss(TimeUtil.getCurrentTime()));
			            ba.append(DataUtil.arrayAppend(temp, 13, temp.length-15, temp, 0, 0)); 
			            
	            	}
	            	if(temp !=null && temp.length>0 && temp[4] == KDH_DataConstants.REP_UD_HAS_DATA){
	            		ba = eventlogRead(session, ba);
		            }
	            }else{ //retry
	            	ctrSeq++;
	            	remainData = (byte)0x00;
	            }
	    	}
    	}catch (Exception e) {
    		log.error(e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS, "currbillRead error");
		}
    	return ba;
    }
	
	public ByteArray eventlogRead(IoSession session, ByteArray ba) throws MRPException
    {
    	log.debug("========== eventlog Read Start ===============");
  //      ByteArray ba = new ByteArray();  
        byte[] temp = null;
        KDH_RequestDataFrame frame = null;
        KDH_ReceiveDataFrame buf = null;

    	try{
    		byte cmd = KDH_DataConstants.REQ_UD1_CTRL[0];
        	byte[] send= new byte[2];
        	boolean retry = true;
        	while(retry){
        		retry = false;
	        	int idx =0;
	    		send[idx++]=cmd;
	    		send[idx++]=(byte)0x01;//a field
	    		OCTET send_data = new OCTET(send);
	            frame  = new KDH_RequestDataFrame(new BYTE(), send_data, 0, 0, null);
	            buf = read(session,frame,1);  
	            if(buf!=null){
	            	temp = buf.encode();	
	            	if(temp !=null && temp.length>0 && temp[7]> (byte)0x00){
	            		ba.append(new byte[] {KDH_DataConstants.DATA_TYPE_EVENT});
			            int dataLen = temp.length-9;
			            ba.append(new byte[] {(byte)((dataLen & 0xff)>>8), (byte)(dataLen & 0xff) }); //length
			            ba.append(DataUtil.arrayAppend(temp, 7, temp.length-9, temp, 0, 0)); 
			            
			            if(temp[4]==(byte)0x28){
			            	retry = true;
			            }
	            	}
	            }
	            else{
	            	retry = true;
	            }
        	}
    	}catch (Exception e) {
    		log.error(e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS, "eventlogRead error");
		}
		return ba;
    }
	/**
	 * Read Event Log.<p>
	 * @return
	 * @throws MeterException
	 */
	public ByteArray eventlogRead(IoSession session) throws MRPException
    {
        return null;
    }
	
    public ByteArray lpRead(IoSession session, String startday, String endday, int lpCycle) throws MRPException
    {
    	return null;
    }
    
    public ByteArray lpRead(IoSession session, int nOffset, String endday, boolean isFirst, byte periodType) throws MRPException
    {
    	log.debug("========== LoadProfile Read Start ===============");
        ByteArray ba = new ByteArray();  
        byte[] temp = null;
        KDH_RequestDataFrame frame = null;
        KDH_ReceiveDataFrame buf = null;
        
        try{
        	byte cmd = KDH_DataConstants.CONTROL_GET_LP[0];
        	int ctrSeq =0;
        	byte[] send= new byte[14];
        	byte remainData =(byte) 0x00;
       
        	boolean sendAgain = false;
    		int idx =0;
    		send[idx++]=(byte)0x0B;
    		send[idx++]=(byte)0x0B;
    		send[idx++]=KDH_DataConstants.SOH_LONG;
    		
        	send[idx++] = cmd;
        	log.debug("cmd : "+cmd);
        	send[idx++] = (byte) 0x01; //address -- fixed
        	send[idx++] = (byte) 0x07;
        	send[idx++] = (byte) 0xFF;//item
        	send[idx++] = (byte) 0xFF;//item
        	send[idx++] = (byte)(periodType+1);
        	
        	byte cntData = 0x00;
        	String from = endday;
        	if(periodType==(byte)0x00){
        		if(nOffset*24 < 255)
        			cntData= (byte)(nOffset*24);
        		else cntData = (byte)255;
        		from = TimeUtil.getPreHour(TimeUtil.getCurrentTime(), (cntData & 0xff));
        	}else if(periodType==(byte)0x01){
        		cntData= (byte)nOffset;
        		from = TimeUtil.getPreDay(TimeUtil.getCurrentTime(), nOffset);
        	}else {
        		cntData= (byte)0x01; 
        		from = TimeUtil.getPreMonth(TimeUtil.getCurrentTime(), 1);
        	}
        	log.debug("from :"+from);
        	log.debug("cntData :"+cntData);
        	byte[] datetime = parseYyyymmddhhmmss(from);
         	System.arraycopy(datetime, 0, send, idx, 4);
        	idx+=4;
        	send[idx] = cntData;
        	int dataLen =0;
        	boolean firstPage =true;
        	ByteArray baTemp = new ByteArray();  
        	int cnt =0;
        	int retry =3;
	        while(remainData == (byte) 0x00 && cnt<retry){
	        	remainData =1;
	        	sendAgain = false;
	        	if(send==null){
	        		send = new byte[2];
	        		cmd = KDH_DataConstants.CONTROL_GET_LP[ctrSeq%2];
	        		send[0] = cmd;
		        	send[1] = (byte) 0x01;
	        	}
	        		
	        	OCTET send_data = new OCTET(send);
		        frame  = new KDH_RequestDataFrame(new BYTE(), send_data, 0, 0, null);
		        buf = read(session,frame,1);   
		        send = null;
		        cnt ++;
		        log.debug("test1");
	            if(buf!=null){
	            	temp = buf.encode();	
	            	if(temp !=null && temp.length>0 ){//&& temp[8]== (byte)0x00){
		            	if(isFirst){
			            	ba.append(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}); //meterId
			            	
			            	ba.append(new byte[]{(byte)KDH_DataConstants.METER_PULSE});//meter pulse
				            //ba.append(DataUtil.arrayAppend(temp, 7, 1, temp, 0, 0)); //meterType
			            	ba.append(new byte[]{(byte)0x07});//meter pulse
				            ba.append(DataUtil.arrayAppend(temp, 8, 1, temp, 0, 0)); //meter Status
				            ba.append(new byte[]{(byte)((Integer.parseInt(endday.substring(0,4))-2000) & 0xFF),
				            					(byte)(Integer.parseInt(endday.substring(4,6)) & 0xFF),
				            					(byte)(Integer.parseInt(endday.substring(6,8)) & 0xFF),
				            					(byte)(Integer.parseInt(endday.substring(8,10)) & 0xFF),
				            					(byte)(Integer.parseInt(endday.substring(10,12)) & 0xFF),
				            					(byte)(Integer.parseInt(endday.substring(12,14)) & 0xFF)
				            		});
				            log.debug("isFirst : "+new OCTET(ba.toByteArray()).toHexString());
		            	}
		            	isFirst=false;
			            
			            if(temp.length>=16){
			            	if(firstPage){
					            dataLen += (temp.length-12);
					            
					            baTemp.append(DataUtil.arrayAppend(temp, 10, temp.length-12, temp, 0, 0));
					            ctrSeq++;
					            remainData =(byte) ((temp[9] & (byte)0x80 )>>8);
					            log.debug("remainData : "+remainData);
					            firstPage = false;
			            	}else{
			                    dataLen += (temp.length-16);
					            
					            baTemp.append(DataUtil.arrayAppend(temp, 14, temp.length-16, temp, 0, 0));
					            ctrSeq++;
					            remainData =(byte) ((temp[9] & (byte)0x80 )>>8);
					            log.debug("remainData : "+remainData);
			            	}
			            }
					cnt = retry;
	            	}else{
		            	log.debug("temp==null");
		            	continue;
		            }
	            }else{
	            	log.debug("buf==null");
	            	continue;
	            }
        	}//while
	        if(dataLen>0){
	        	log.debug("dataLen : "+dataLen);
	        	ba.append(new byte[] {periodType});
	        	ba.append(new byte[] {(byte)(((dataLen & 0x0000ff00)>>8)), (byte)(dataLen) }); //length
	        	ba.append(baTemp.toByteArray());
	        }
        }catch (Exception e){
            log.error(e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS, "LoadProfile Read error");
        }
        
        return ba;
    }
    
    protected KDH_ReceiveDataFrame read(IoSession session,KDH_RequestDataFrame frame) throws MRPException
    {
    	return null;
    }
    protected KDH_ReceiveDataFrame read(IoSession session,KDH_RequestDataFrame frame, int kind) throws MRPException
    {
        int sequence = 0;
        try
        {
            IoBuffer buf = frame.getIoBuffer();
            session.write(buf); 
            KDH_ReceiveDataFrame rcvFrame = new KDH_ReceiveDataFrame();
            byte[] message = (byte[])handler.getMessage(session,2, MeterModel.WIZIT_KDH.getCode().intValue());
     //       rcvFrame.append(message);
            if(message != null && message.length > 0){
                log.debug("receive read =>"+new OCTET(message).toHexString());
                
                switch(kind){
	                case  0: //short
	                	if(message[0] != KDH_DataConstants.SOH_SHORT)
	        			{
	        				rcvFrame = null;
	        			}
	                break;
	                case 1: //long
	                	if(message[0] != KDH_DataConstants.SOH_LONG)
	        			{
	        				rcvFrame = null;
	        				log.debug("SOH ERROR");
	        			}
	                	
	                	int length = 0;
	                	length = (message[1] & 0xff);
	                	 
	                	if(message[5+length]!=KDH_DataConstants.SOH_LONG)
	                	{
	        				rcvFrame = null;
	        				log.debug("EOH ERROR");
	        			}
	        			/*
	        			if(checksumCheck(message[7+length])
	        			 */
	                	/*
	                	if(message[6] != KDH_DataConstants.CI_CODE_ERROR)
	        			{
	        				rcvFrame = null;
	        			}
	        			*/
	                break;
                }

    		//	int nLen = message.length;
    			
    			/*
    			char crc	=	KH_CRC16(message, 1, nLen-3);

    			//check sum
    			if ((message[nLen-2] != (byte)(crc>>8)) || (message[nLen-1] != (byte)(crc))){
    				rcvFrame = null;
    			}
    			*/
            }

            if(rcvFrame==null){
                throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
            }else{
            	 rcvFrame.append(message);
            }
            return rcvFrame;
        }
        catch (Exception e)
        {
            log.error("Read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
        } 
    }

    public boolean checkCRC(byte[] src) throws MRPException
    {
        return false;
    }

    public void initProcess(IoSession session) throws MRPException
    {
    }

    public ByteArray instRead(IoSession session) throws MRPException
    {
    	return null;
    }
    
    public ByteArray billRead(IoSession session) throws MRPException
    {
    	return null;
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
    
    public byte[] parseYyyymmddhhmmss(String date){
//	throws Exception {
    	log.debug(" parseYyyymmddhhmmss date : "+ date);
    	byte[] res = new byte[4];
    	try {
		int len = date.length();
		//log.debug(" date.length(); : "+ len);
		if(len < 8)
		throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+len);
		
		byte year = (byte)(((Integer.parseInt(date.substring(0,4)))-2000) & 0xff );
		byte mon  = (byte)((byte)(Integer.parseInt(date.substring(4,6))& 0xff ) & (byte) 0x0f);// & (byte) 0x0F;
		byte day  = (byte)((byte)(Integer.parseInt(date.substring(6,8))& 0xff ) & (byte)0x1F);
		byte hour = (byte) 0x00;
		if(len>8)
			hour = (byte)((byte)(Integer.parseInt(date.substring(8,10))& 0xff ) & (byte)0x1F);
		byte min = 0x00;
		if(len>10) 
			min  = (byte)((byte)(Integer.parseInt(date.substring(10,12))& 0xff )& (byte)0x3F);
		
	//	byte year_High = (byte)(year>>4);
	//	byte year_Low  = (byte)year;
		
		res[3] = (byte)(((year & 0x78)<<1) | mon);
		res[2] = (byte)(((year & 0x07)<<5) | day);
		res[1] = hour;
		res[0] = min;
		log.debug(" hex date :"+ new OCTET(res).toHexString());
    	} catch(Exception e) {
			log.error("parseYyyymmddhhmmss error : ",e);
		}
		return res;
	}

	/**
	 * @param append source of String
	 * @param str	to append  
	 * @param length
	 * @return
	 */
	public static String frontAppendNStr(char append, String str, int length)
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
			log.error("Util.frontAppendNStr : ",e);
		}
		return b.toString();
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
