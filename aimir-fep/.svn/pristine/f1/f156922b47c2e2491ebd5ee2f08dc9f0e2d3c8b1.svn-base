package com.aimir.fep.protocol.mrp.protocol;

import java.net.Socket;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;

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
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

public class EDMI_Mk10_Handler extends MeterProtocolHandler{

    private static Log log = LogFactory.getLog(EDMI_Mk10_Handler.class);

    private String meterId;
    private String modemId;
            
    private String groupId = "";
    private String memberId = "";
    private String mcuSwVersion = "";
    private byte[] dest_addr = new byte[4];
    private byte[] source_addr = new byte[]{(byte)0x42, (byte)0xC0, (byte)0x64, (byte)0xE8};//PC ID , DEVICE ID FIX TO this  ID
    private int sequence = 0;
	/**
	 * Constructor.<p>
	 */
    protected long sendBytes;
    
    
	public EDMI_Mk10_Handler() {	

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

		session.setAttribute(MRPClientProtocolHandler.isActiveKey, false);
        CommandData commandData = null;
        byte[] mti = null;
        byte[] tpb = null;
        byte[] tcb = null;
        byte[] lpd = null;
        byte[] ist = null;
        byte[] eld = null;

        ByteArray ba = new ByteArray();
        String fromDate = "";
        String toDate = "";
        String cmd = command.getCmd().getValue();
        log.debug("==============EDMI_Mk10_Handler start cmd:"+cmd+"================");
        
        if(cmd.equals("105.2.0") || cmd.equals("105.5.0")){
        	
        	log.debug("EDMI Mk10 Meter Time Sync .............");
            initProcess(session);
            byte[] md = new byte[30];
            HashMap result = timeSync(session, 60);
            if(result !=null){
            	byte[] before = new byte[6];
            	byte[] after = new byte[6];
            	if(result.get("beforeTime")  != null ) {
            		before = ((OCTET)result.get("beforeTime")).getValue();

            		md[6] = (byte)DataFormat.hex2unsigned8(before[2]);
            		md[7] = (byte)DataFormat.hex2unsigned8(before[1]);
            		md[8] = (byte)DataFormat.hex2unsigned8(before[0]);
            		md[9] = (byte)DataFormat.hex2unsigned8(before[3]);
            		md[10] = (byte)DataFormat.hex2unsigned8(before[4]);
            		md[11] = (byte)DataFormat.hex2unsigned8(before[5]);
            	}
            	if(result.get("afterTime") != null){
            		after = ((OCTET)result.get("afterTime")).getValue();

            		md[21] = (byte)DataFormat.hex2unsigned8(after[2]);
            		md[22] = (byte)DataFormat.hex2unsigned8(after[1]);
            		md[23] = (byte)DataFormat.hex2unsigned8(after[0]);
            		md[24] = (byte)DataFormat.hex2unsigned8(after[3]);
            		md[25] = (byte)DataFormat.hex2unsigned8(after[4]);
            		md[26] = (byte)DataFormat.hex2unsigned8(after[5]);
            	}

            }

            commandData = new CommandData();
            commandData.setCnt(new WORD(1));
            commandData.setTotalLength(md.length);
            commandData.append(new SMIValue(new OID("1.12.0"),new OCTET(md)));
        }
        
        if(cmd.equals("104.6.0")||cmd.equals("104.13.0"))
        {
            SMIValue[] smiValue = command.getSMIValue();
            int kind = 0;
            
            if(smiValue != null && smiValue.length >= 2){
            	if(smiValue[1].getVariable() instanceof BYTE){
                    kind = ((BYTE)smiValue[1].getVariable()).getValue();
            	}
            	else if(smiValue[1].getVariable() instanceof INT){
                    kind = ((INT)smiValue[1].getVariable()).getValue();
            	}
                
                if(smiValue.length == 4){
                	try{
	                	fromDate = ((TIMESTAMP)smiValue[2].getVariable()).getValue();
	                    toDate =   ((TIMESTAMP)smiValue[3].getVariable()).getValue();
	                    if(toDate == null || "".equals(toDate)){
	                    	toDate = fromDate;
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

                //String currTime = null;
                //try{currTime = TimeUtil.getCurrentTime();}
                //catch(Exception e){                 
                //}

                //ba.append(DataFormat.encodeTime(currTime));
                
                switch(kind){
                    case 0:
                        initProcess(session);
                        mti = configureRead(session).toByteArray();
                        
                        try{
                            tcb = currbillRead(session).toByteArray();
                        }catch(Exception e){
                        	log.error(e,e);
                        }

                        try{
                            tpb = billRead(session).toByteArray();
                        }catch(Exception e) {
                        	log.error(e,e);
                        }
                        
                        try{
                            eld = eventlogRead(session).toByteArray();
                        }catch(Exception e){
                        	log.error(e,e);
                        }

                        try{
                            lpd = lpRead(session,fromDate,toDate,15).toByteArray();
                        }catch(Exception e){
                        	
                        }

                        try{
                            ist = instRead(session).toByteArray();
                        }catch(Exception e){
                        	
                        }

                        ba.append(DataConstants.MT);
                        ba.append(DataUtil.get2ByteToInt(mti.length));
                        ba.append(mti);

                        if(tcb != null){
                            ba.append(DataConstants.CB);
                            ba.append(DataUtil.get2ByteToInt(tcb.length));
                            ba.append(tcb);
                        }
                        if(tpb != null){
                            ba.append(DataConstants.PB);
                            ba.append(DataUtil.get2ByteToInt(tpb.length));
                            ba.append(tpb); 
                        }

                        if(eld != null){
                            ba.append(DataConstants.EL);
                            ba.append(DataUtil.get2ByteToInt(eld.length));
                            ba.append(eld); 
                        }

                        if(lpd != null){
                            ba.append(DataConstants.LD);
                            ba.append(DataUtil.get2ByteToInt(lpd.length));
                            ba.append(lpd);
                        }
                        if(ist != null){
                            ba.append(DataConstants.IS);
                            ba.append(DataUtil.get2ByteToInt(ist.length));
                            ba.append(ist);
                        }

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

                log.debug("meterEntryData=>"+smiValue[0].getVariable().toString()+","+
                        new OCTET(ba.toByteArray()).toHexString());
                log.debug("meterSerial=>"+this.meterId);
                log.debug("modemSerial=>"+smiValue[0].getVariable().toString());
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
    				byte[] encodeTime = DataFormat.encodeTime(nowTime);
    				//log.debug("Time Stamp : " + nowTime + " / Encode["
    				//		+ Hex.getHexDump(encodeTime) + "]");
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

        log.debug("==============EDMI_Mk10_Handler end ================");
        return commandData;
    }
    
    protected ByteArray readChannelConfigure(IoSession session) throws MRPException
    {
        return null;
    }
    

    protected EDMI_Mk10_ReceiveDataFrame read(IoSession session,EDMI_Mk10_RequestDataFrame frame,
			byte[] commandKind, int responseLength)
    throws MRPException
    {
    	int sequence = 0;
    	boolean isRemain = true;
    	try
    	{
    		IoBuffer buf = null;
    		EDMI_Mk10_ReceiveDataFrame rcvFrame = new EDMI_Mk10_ReceiveDataFrame();

    		int retry =3;
    		byte[] message = null; 
    		byte[] getCmd = null;
    		int cnt=0;
    		while(cnt<retry){ 
    			log.debug("cnt :"+cnt);
    			cnt++;
    			try{

    				buf = frame.getIoBuffer();
    				session.write(buf); 
    				//message = (byte[])handler.getMessageFromEDMIMeter(session);
    				message = readMessage(session);
    				//log.debug("message length :" +message.length);

    				if(message!=null && message.length>=2){
    					if(message[0]== EDMI_Mk10_DataConstants.STX){
    						getCmd = get_cmd(message, message.length);
    						log.debug("RETURN CMD="+new OCTET(getCmd).toHexString());
    						if(getCmd != null && getCmd.length>0){
    							//if(responseLength>0 && responseLength!=getCmd.length){
    							//	log.debug("Received length error : length is not "+responseLength);
    							//	continue;
    							//}
    							if(getCmd[0] == EDMI_Mk10_DataConstants.ACK){
    								break;
    							}
    							if(getCmd[0] == EDMI_Mk10_DataConstants.CAN){
    								log.debug("Received CAN");
    								continue;
    							}

    							if(getCmd[0]!= commandKind[0]){
    								log.debug("COMMAND error");
    								continue;
    							}
    							if(commandKind.length>1 && getCmd[1]!= commandKind[1]){
    								log.debug("CMMAND 2 error");
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
    			rcvFrame.append(DataUtil.select(getCmd, buf.array().length-4, getCmd.length - (buf.array().length-4)));
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

    protected byte[] readMessage(IoSession session) throws MRPException {
    	
		ReadFuture future;
		ByteArray message = new ByteArray();
		long TIMEOUT = 10L;
		session.getConfig().setUseReadOperation(true);
		boolean ACK = false;

		// 메시지를 다받았는지
		boolean isFinished = false;

		do {
			// 메시지를 받는다.
			future = session.read();

			// 메시지를 기다린다.
			future.awaitUninterruptibly(TIMEOUT * 1000);
			if (future.isRead()) {
				// 메시지를 받는다.
				byte[] receiveMessage = (byte[]) future.getMessage();

				message.append(receiveMessage);
				if(receiveMessage[receiveMessage.length-1] == EDMI_Mk10_DataConstants.ETX){
					isFinished = true;
				}
			} else {
				throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,
						future.getException().getMessage());
			}
		} while (!isFinished);

		byte[] messageArray = message.toByteArray();

		return messageArray;
    }
    
    protected EDMI_Mk10_ReceiveDataFrame read(IoSession session,EDMI_Mk10_RequestDataFrame frame,
    											byte[] commandKind, int responseLength, int reg_num )
    throws MRPException
    {
        int sequence = 0;
        boolean isRemain = true;
        try
        {
            IoBuffer buf = null;
            EDMI_Mk10_ReceiveDataFrame rcvFrame = new EDMI_Mk10_ReceiveDataFrame();
            
            int retry =3;
            byte[] message = null; 
            byte[] getCmd = null;
            int cnt=0;
            while(cnt<retry){ 
            	//log.debug("cnt :"+cnt);
            	cnt ++;
            	try{
            		
            		buf = frame.getIoBuffer();
	            	session.write(buf); 
	            	//Thread.sleep(100);
    				//message = (byte[])handler.getMessageFromEDMIMeter(session);
    				message = readMessage(session);
		            log.debug("message length :" +message.length);
		            
		            if(message!=null && message.length>=2){
		            	if(message[0]== EDMI_Mk10_DataConstants.STX){
			            	getCmd = get_cmd(message, message.length);
			            	if(getCmd != null && getCmd.length>0){
			            		if(responseLength>0 && responseLength!=getCmd.length){
			            			log.debug("Received length error : length is not "+responseLength);
			            			continue;
			            		}
			            		if(getCmd[0] == EDMI_Mk10_DataConstants.ACK){
			            			break;
			            		}
			            		if(getCmd[0] == EDMI_Mk10_DataConstants.CAN){
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
			            			getRegNo = DataUtil.getIntTo2Byte(DataUtil.select(getCmd, 2, 3));
			            		else
			            			getRegNo = DataUtil.getIntTo2Byte(DataUtil.select(getCmd, 1, 2));
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
            log.error(e,e);
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
			    case EDMI_Mk10_DataConstants.STX:
				    len = 0;
				    crc = EDMI_Mk10_DataConstants.CalculateCharacterCRC16((char)0, c);
			    break;
			    case EDMI_Mk10_DataConstants.ETX:
				    if ((crc == 0)&& (len >= 2)) {
				    	len -= 2; /* remove crc characters */
				    	byte[] resultBuf = new byte[len];
				    	System.arraycopy(cmd_data, 0, resultBuf, 0, len);
				    	return(resultBuf);
				    } else if (len==0)
				    	return new byte[0];
			    break;
			    case EDMI_Mk10_DataConstants.DLE:
			    	DLE_last = true;
			    break;
			    default:
				    if (DLE_last)
					    c &= 0xBF;
					    DLE_last = false;
				    //if (len >= max_len)
				    //	break;
				    crc = EDMI_Mk10_DataConstants.CalculateCharacterCRC16(crc,c);
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
	        EDMI_Mk10_RequestDataFrame frame = null;
	        EDMI_Mk10_ReceiveDataFrame buf = null;
	        byte[] temp = null;
	        
        	//MODEL_ID_NO
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_MODEL_ID_NO));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        log.debug("Request MODEL_ID_NO=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_MODEL_ID_NO);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_MODEL_ID_NO){
        		int srclen = EDMI_Mk10_DataConstants.LEN_MODEL_ID_NO;
        		if(temp.length -3 < srclen)
        			srclen =temp.length -3;
        		byte[] modelResult = new byte[EDMI_Mk10_DataConstants.LEN_MODEL_ID_NO];
        		System.arraycopy(temp, 3, modelResult, 0, srclen);
        		ba.append(modelResult);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get MODEL_ID_NO error");
        	}
        	buf = null;
        	temp = null;
        	frame = null;
        	
	        
//        	METER_ID
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        //send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_METER_ID));
	        send_data = new OCTET(new byte[] {(byte)0xF0, 0x02});
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        //log.debug("Request METER_ID_NO=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_METER_ID);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_METER_ID){
        		int srclength = EDMI_Mk10_DataConstants.LEN_METER_ID;
        		if(temp.length -3 < srclength)
        			srclength =temp.length -3;
        		byte[] idResult = new byte[EDMI_Mk10_DataConstants.LEN_METER_ID];
        		System.arraycopy(temp, 3, idResult, 0, srclength);
        		ba.append(idResult);
        		this.meterId = new String(idResult).trim();
        		this.dest_addr = DataUtil.get4ByteToInt(Long.parseLong(meterId));

        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get METER_ID error");
        	}
        	
        	buf = null;
        	temp = null;

        	
//        	SW VER
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_SW_VER));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        log.debug("Request SWVER=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_SW_VER);  
        	temp = buf.encode();
        	   	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_SW_VER){
        		int srclen = EDMI_Mk10_DataConstants.LEN_SW_VER;
        		if(temp.length -3 < srclen)
        			srclen =temp.length -3;
        		byte[] swVerResult = new byte[EDMI_Mk10_DataConstants.LEN_SW_VER];
        		System.arraycopy(temp, 3, swVerResult, 0, srclen);
        		ba.append(swVerResult);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get swVer error");
        	}
        	
        	buf = null;
        	temp = null;
        	
//        	SW REVISION NUMBER
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_SW_REV_NUMBER));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        log.debug("Request SW REV=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_SW_REV_NUMBER);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_SW_REV_NUMBER){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_SW_REV_NUMBER);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get EZIO_STATUS error");
        	}
        	buf = null;
        	temp = null;
        	
//        	CURRENT_STATUS
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_CURRENT_STATUS));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        log.debug("Request CURRENT STATUS=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_CURRENT_STATUS);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_CURRENT_STATUS){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_CURRENT_STATUS);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURRENT_STATUS error");
        	}
        	buf = null;
        	temp = null;
        	
//        	LATCHED_STATUS
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_LATCHED_STATUS));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        log.debug("Request LATCHED STATUS=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_LATCHED_STATUS);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_LATCHED_STATUS){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_LATCHED_STATUS);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get LATCHED_STATUS error");
        	}
        	buf = null;
        	temp = null;
        	
//        	CURR_DATE_TIME
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_CURR_DATE_TIME));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        log.debug("Request DATETIME=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_CURR_DATE_TIME);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_CURR_DATE_TIME){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_CURR_DATE_TIME);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURR_DATE_TIME error");
        	}
        	buf = null;
        	temp = null;
        	
//        	DST_DATE_TIME
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_DST_DATE_TIME));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        log.debug("Request DST DATETIME=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_DST_DATE_TIME);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_DST_DATE_TIME){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_DST_DATE_TIME);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get DST_DATE_TIME error");
        	}
        	buf = null;
        	temp = null;
        	
      	
//        	CT_RATIO_MUL
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_CT_RATIO_MUL));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        log.debug("Request CT RATIO MUL=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_CT_RATIO_MUL);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_CT_RATIO_MUL){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_CT_RATIO_MUL);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CT_RATIO_MUL error");
        	}
        	buf = null;
        	temp = null;
        	
//        	PT_RATIO_MUL
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PT_RATIO_MUL));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        log.debug("Request PT RATIO MUL=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PT_RATIO_MUL);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PT_RATIO_MUL){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_PT_RATIO_MUL);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PT_RATIO_MUL error");
        	}
        	buf = null;
        	temp = null;
        	
//        	CT_RATIO_DIV
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_CT_RATIO_DIV));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        log.debug("Request CT RATIO DIV=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_CT_RATIO_DIV);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_CT_RATIO_DIV){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_CT_RATIO_DIV);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CT_RATIO_DIV error");
        	}
        	
        	buf = null;
        	temp = null;
        	frame = null;
//        	PT_RATIO_DIV
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PT_RATIO_DIV));
	        log.debug("PT_RATIO_DIV="+send_data.toHexString());
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        log.debug("Request PT RATIO DIV=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PT_RATIO_DIV);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PT_RATIO_DIV){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_PT_RATIO_DIV);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PT_RATIO_DIV error");
        	}
        	
        	buf = null;
        	temp = null;
        	
//        	MEASURE_METHOD
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_MEASURE_METHOD));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_MEASURE_METHOD);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_MEASURE_METHOD){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_MEASURE_METHOD);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get MEASURE_METHOD error");
        	}
        	
        	buf = null;
        	temp = null;
        	
//        	MEASURE_OPTION
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_MEASURE_OPTION));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_MEASURE_OPTION);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_MEASURE_OPTION){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_MEASURE_OPTION);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get MEASURE_OPTION error");
        	}        
        	buf = null;
        	temp = null;
        	
//        	NUM_OF_PWR_UP
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_NUM_OF_PWR_UP));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_NUM_OF_PWR_UP);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_NUM_OF_PWR_UP){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_NUM_OF_PWR_UP);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get NUM_OF_PWR_UP error");
        	}
        	buf = null;
        	temp = null;
        	
//        	LAST_DT_OUTAGE
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_LAST_DT_OUTAGE));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_LAST_DT_OUTAGE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_LAST_DT_OUTAGE){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_LAST_DT_OUTAGE);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get LAST_DT_OUTAGE error");
        	}
        	buf = null;
        	temp = null;
        	
//        	LAST_DT_RECOVERY
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_LAST_DT_RECOVERY));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_LAST_DT_RECOVERY);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_LAST_DT_RECOVERY){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_LAST_DT_RECOVERY);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get LAST_DT_RECOVERY error");
        	}
        	buf = null;
        	temp = null;
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
    	/*
    		        02 45 0C 98 9A F6 42 C0 64 E8 01 C0 52 F7 32 2D F9 03 
    		        02 45 0C 98 9A F6 42 C0 64 E8 01 C1 52 F7 33 4B 6C 03 
    		        02 45 0C 98 9A F6 42 C0 64 E8 01 C2 52 F7 34 A0 57 03 
    		        02 45 0C 98 9A F6 42 C0 64 E8 01 C3 52 F7 35 C6 C2 03
    		        */ 
    	
        log.debug("========== Prev Bill Read Start ===============");
        ByteArray ba = new ByteArray();
        try{
        	OCTET control = null;
        	OCTET send_data = null;
	        EDMI_Mk10_RequestDataFrame frame = null;
	        EDMI_Mk10_ReceiveDataFrame buf = null;
	        byte[] temp = null;
	        
	        
//        	NBR_DEMAND_RESETS
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_NBR_DEMAND_RESETS));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_NBR_DEMAND_RESETS);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_NBR_DEMAND_RESETS){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_NBR_DEMAND_RESETS);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get NBR_DEMAND_RESETS error");
        	}
        	buf = null;
        	temp = null;
//        	REGISTER_TOU_RATE0
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_TOU_RATE0));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_TOU_RATE0);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_TOU_RATE0){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_TOU_RATE0);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get REGISTER_TOU_RATE0 error");
        	}
        	buf = null;
        	temp = null;
        	
//        	REGISTER_TOU_RATE1
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_TOU_RATE1));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_TOU_RATE1);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_TOU_RATE1){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_TOU_RATE0);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get REGISTER_TOU_RATE1 error");
        	}
        	
//        	REGISTER_TOU_RATE2
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_TOU_RATE2));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_TOU_RATE2);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_TOU_RATE2){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_TOU_RATE0);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get REGISTER_TOU_RATE2 error");
        	}
        	buf = null;
        	temp = null;
//        	REGISTER_TOU_RATE3
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_TOU_RATE3));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_TOU_RATE3);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_TOU_RATE3){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_TOU_RATE0);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get REGISTER_TOU_RATE3 error");
        	}
        	temp = null;
        	buf = null;
        	
        	//PREVIOUS TOU
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PREVIOUS_TOU));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PREVIOUS_TOU);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PREVIOUS_TOU){
        		//ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_PREVIOUS_TOU);
        		ba.append(temp,3,temp.length-3);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PREVIOUS TOU error");
        	}
        	buf = null;
        	temp = null;

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
        log.debug("========== Current Bill Read Start(Cummulative Total Read) ===============");
        ByteArray ba = new ByteArray();
        try{
        	OCTET control = null;
        	OCTET send_data = null;
	        EDMI_Mk10_RequestDataFrame frame = null;
	        EDMI_Mk10_ReceiveDataFrame buf = null;
	        byte[] temp = null;
  
//        	NBR_DEMAND_RESETS
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_NBR_DEMAND_RESETS));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_NBR_DEMAND_RESETS);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_NBR_DEMAND_RESETS){
        		
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_NBR_DEMAND_RESETS);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get NBR_DEMAND_RESETS error");
        	}
        	buf = null;
        	temp = null;
//        	REGISTER_TOU_RATE0
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_TOU_RATE0));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_TOU_RATE0);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_TOU_RATE0){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_TOU_RATE0);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get REGISTER_TOU_RATE0 error");
        	}
        	buf = null;
        	temp = null;
        	
//        	REGISTER_TOU_RATE1
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_TOU_RATE1));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_TOU_RATE1);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_TOU_RATE1){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_TOU_RATE0);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get REGISTER_TOU_RATE1 error");
        	}
        	
//        	REGISTER_TOU_RATE2
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_TOU_RATE2));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_TOU_RATE2);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_TOU_RATE2){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_TOU_RATE0);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get REGISTER_TOU_RATE2 error");
        	}
        	buf = null;
        	temp = null;
//        	REGISTER_TOU_RATE3
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_TOU_RATE3));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_TOU_RATE3);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_TOU_RATE3){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_TOU_RATE0);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get REGISTER_TOU_RATE3 error");
        	}
        	temp = null;
        	buf = null;

//        	CURR_DATE_TIME
            control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
            send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_CURR_DATE_TIME));
            
            frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
            log.debug("Request DATETIME=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_CURR_DATE_TIME);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_CURR_DATE_TIME){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_CURR_DATE_TIME);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURR_DATE_TIME error");
        	} 
        	buf = null;
        	temp = null;
        	
//        	TOU TOTAL
        	//int NBR_DM_CH =4;
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_TOU_TOTAL));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_TOU_TOTAL);  
        	temp = buf.encode();
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_TOU_TOTAL){
            	ba.append(temp,3,temp.length-3);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get REG_DEMAND error");
        	}
        	buf = null;
        	temp = null;
        	//log.debug("NBR_DM_CH :"+NBR_DM_CH);
        	

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
	        EDMI_Mk10_RequestDataFrame frame = null;
	        EDMI_Mk10_ReceiveDataFrame buf = null;
	        byte[] temp = null;
	        
	        int lpchannel = 0;
	        int INTERVAL_TIME=15;
	        //int NBR_LP_CH =15;
	        //int NBR_LP_ENTRIES =0;
	        //int STORED_TOTAL_LP =0;
	        //int start_record =0;
	        //int ADDR_LP_OFFSET =0;
	        //int RECORD_SIZE =0;
	        int lastEntryNumber = 0;
	        //String STORED_START_TIME = null;
	        String lp1date = "";
	        String lastLpTime = "";
//	        INTERVAL_TIME

	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_INTERVAL_TIME));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_INTERVAL_TIME);  
        	temp = buf.encode();
            
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))== EDMI_Mk10_DataConstants.REGISTER_INTERVAL_TIME){
        		byte[] intervalchannel = DataUtil.select(temp, 3, EDMI_Mk10_DataConstants.LEN_INTERVAL_TIME);
        		ba.append((byte)INTERVAL_TIME);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get INTERVAL_TIME error");
        	}
        	log.debug("INTERVAL_TIME :"+INTERVAL_TIME);
        	buf = null;
        	temp = null;

        	/*
	        for(int k=0; k<NBR_LP_CH; k++){
//		        LOAD SURVEY CH REG
		        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
		        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_LP_CH_REG+k));
		        
		        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_LP_CH_REG+k);  
	        	temp = buf.encode();
		        log.debug("LOAD SURVEY CH REG="+new OCTET(temp).toHexString());
	        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))== EDMI_Mk10_DataConstants.REGISTER_LP_CH_REG+k){
	        		ba.append(DataUtil.select(temp, 3, EDMI_Mk10_DataConstants.LEN_LP_CH_REG));
	        	}else{
	        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get LOAD SURVEY CH REG error");
	        	}
	        	buf = null;
	        	temp = null;
	        }
*/
        	
        	 //02 45 0C 98 9A F6 42 C0 64 E8 00 6E 52 F5 32 A9 E6 03 
        	 //02 45 0C 98 9A F6 42 C0 64 E8 00 6F 46 30 00 00 00 0A 5D 00 40 B9 66 03 
        	// 02 45 0C 98 9A F6 42 C0 64 E8 00 70 46 30 00 00 00 0A 9C 00 40 6A 86 03     

        	/*
        	 * Extended
			 * Command : E(Destination’s #)(Source’s #)[Sequence]/normal command/
			 * Response : E(Source’s #)(Destination’s #)[Sequence]/normal response/
        	 */

	        

	        //02 45 0C 98 9A F6 42 C0 64 E8 00 2C 52 F5 32 2A 12 03 
	        //02 45 0C 98 9A F6 42 C0 64 E8 00 2D 46 30 00 00 00 0C 9D 00 40 
        	//14 98 03  
	        
//	        LOAD SURVEY INFO

	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_LOADSURVEY_INFO));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_LOADSURVEY_INFO);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))== EDMI_Mk10_DataConstants.REGISTER_LOADSURVEY_INFO){
        		log.debug("LENGTH"+temp.length);
        		byte[] loadSurvey = DataUtil.select(temp, 3, temp.length -3);
        		lp1date = getDateTime(DataUtil.getLongToBytes(DataUtil.select(loadSurvey, 0, 4)));
        		log.debug("lp1date="+lp1date);
        		lpchannel = (DataUtil.getIntTo2Byte(DataUtil.select(loadSurvey, 4*2+4*2+4*2, 2))) >> 6;
        		lastEntryNumber = (DataUtil.getIntTo4Byte(DataUtil.select(loadSurvey, 4*2+4*2, 4)));
        		ba.append((byte)lpchannel);
        		ba.append(DataUtil.get2ByteToInt(lpchannel*2+1));//entry length
        		ba.append(DataUtil.get2ByteToInt(2));//channel length
        		ba.append(getDateTimeArray(lp1date));//stored start time
        		ba.append(DataUtil.get2ByteToInt(lastEntryNumber));
        		ba.append(DataUtil.select(loadSurvey, 115, 64));//lp channel info
        		

        		log.debug("lpchannel="+lpchannel);
        		log.debug("lastEntryNumber="+lastEntryNumber);        		
        		
        		lastLpTime = Util.addMinYymmdd(lp1date.substring(0,12), lastEntryNumber*INTERVAL_TIME);
        		log.debug("lastLpTime="+lastLpTime); 
        			
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get LEN_LOADSURVEY_INFO error");
        	}
        	buf = null;
        	temp = null;

	        
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER_EXTENDED); 
	        
	        
	        ByteArray b = new ByteArray();
	        //max length 2048
	        int max_length = 2048;
	        int number_of_record = 63;
	        int one_entry_size = lpchannel*2+1;
	        
	        b = new ByteArray();
	        b.append(dest_addr);
	        b.append(source_addr);
	        b.append(DataUtil.get2ByteToInt(++sequence));
	        b.append(EDMI_Mk10_DataConstants.CMD_READ_ACCESS_FILE);
	        b.append(EDMI_Mk10_DataConstants.OPTION_LOAD_SURVEYS_1);
	        b.append(new byte[]{0x00});
	        
	        int startEntryNumber = lastEntryNumber - number_of_record-1;
	        if(lastEntryNumber < number_of_record){
	        	number_of_record = lastEntryNumber-1;
	        	startEntryNumber = lastEntryNumber - number_of_record;
	        }
	        b.append(DataUtil.get4ByteToInt(startEntryNumber));
	        b.append(DataUtil.get2ByteToInt(number_of_record));
	        send_data = new OCTET(b.toByteArray());

	        byte[] lastlptime = new byte[6];
	        lastlptime[0] = (byte) Integer.parseInt(lastLpTime.substring(6,8));
	        lastlptime[1] = (byte) Integer.parseInt(lastLpTime.substring(4,6));
	        lastlptime[2] = (byte) (Integer.parseInt(lastLpTime.substring(0,4))%1000);
	        lastlptime[3] = (byte) Integer.parseInt(lastLpTime.substring(8,10));
	        lastlptime[4] = (byte) Integer.parseInt(lastLpTime.substring(10,12));
	        lastlptime[5] = 0;		
			
	        //ba.append(lastlptime);
	        ba.append(DataUtil.get2ByteToInt(number_of_record));
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        log.debug("REQUEST LOAD FILE ACCESS==>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER_EXTENDED, 0); 
        	temp = buf.encode();
        	ba.append(temp);
        	buf = null;
        	temp = null;
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
    
    private String getYyyymmddhhmmss(byte[] b)	throws Exception {
		
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
        	OCTET control = null;
        	OCTET send_data = null;
	        EDMI_Mk10_RequestDataFrame frame = null;
	        EDMI_Mk10_ReceiveDataFrame buf = null;
	        byte[] temp = null;

        	//PH_A_VOLTAGE
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_A_VOLTAGE));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_A_VOLTAGE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_A_VOLTAGE){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_VOLTAGE error");
        	}
        	buf = null;
        	temp = null;
//        	PH_B_VOLTAGE
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_B_VOLTAGE));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_B_VOLTAGE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_B_VOLTAGE){
        		ba.append(temp,2,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_VOLTAGE error");
        	}
        	buf = null;
        	temp = null;
//        	PH_C_VOLTAGE
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_C_VOLTAGE));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_C_VOLTAGE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_C_VOLTAGE){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_VOLTAGE error");
        	}
        	buf = null;
        	temp = null;
//        	PH_A_CURRENT
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_A_CURRENT));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_A_CURRENT);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_A_CURRENT){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_CURRENT error");
        	}
        	buf = null;
        	temp = null;
//        	PH_B_CURRENT
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_B_CURRENT));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_B_CURRENT);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_B_CURRENT){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_CURRENT error");
        	}
        	buf = null;
        	temp = null;
//        	PH_C_CURRENT
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_C_CURRENT));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_C_CURRENT);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_C_CURRENT){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_CURRENT error");
        	}
        	buf = null;
        	temp = null;
//        	PH_A_ANGLE
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_A_ANGLE));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_A_ANGLE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_A_ANGLE){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_ANGLE error");
        	}
        	buf = null;
        	temp = null;
//        	PH_B_ANGLE
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_B_ANGLE));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_B_ANGLE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_B_ANGLE){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_ANGLE error");
        	}
        	buf = null;
        	temp = null;
//        	PH_C_ANGLE
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_C_ANGLE));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_C_ANGLE);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_C_ANGLE){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_ANGLE error");
        	}
        	buf = null;
        	temp = null;
//        	PH_A_WAIT
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_A_WATTS));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_A_WATTS);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_A_WATTS){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_WAIT error");
        	}
//        	PH_B_WAIT
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_B_WATTS));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_B_WATTS);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_B_WATTS){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_WAIT error");
        	}
        	buf = null;
        	temp = null;
//        	PH_C_WAIT
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_C_WATTS));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_C_WATTS);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_C_WATTS){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_WAIT error");
        	}
        	buf = null;
        	temp = null;
//        	PH_A_VAR
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_A_VAR));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_A_VAR);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_A_VAR){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_VAR error");
        	}
        	buf = null;
        	temp = null;
//        	PH_B_VAR
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_B_VAR));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_B_VAR);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_B_VAR){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_VAR error");
        	}
        	buf = null;
        	temp = null;
//        	PH_C_VAR
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_C_VAR));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_C_VAR);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_C_VAR){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_VAR error");
        	}
        	buf = null;
        	temp = null;
//        	PH_A_VA
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_A_VA));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_A_VA);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_A_VA){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_VA error");
        	}
        	buf = null;
        	temp = null;
//        	PH_B_VA
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_B_VA));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_B_VA);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_B_VA){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_VA error");
        	}
        	buf = null;
        	temp = null;
//        	PH_C_VA
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_C_VA));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_C_VA);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_C_VA){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_VA error");
        	}
        	buf = null;
        	temp = null;
        	//FREQUENCY
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_FREQUENCY));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_FREQUENCY);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_FREQUENCY){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_INSTRUMENT_DATA);
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get FREQUENCY error");
        	}
        	buf = null;
        	temp = null;
	    	//PH_A_FUND_VOLTAGE
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_A_FUND_VOLTAGE));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_A_FUND_VOLTAGE);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_A_FUND_VOLTAGE){
	    		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_FUND_VOLTAGE error");
	    	}
	    	//PH_B_FUND_VOLTAGE
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_B_FUND_VOLTAGE));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_B_FUND_VOLTAGE);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_B_FUND_VOLTAGE){
	    		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_FUND_VOLTAGE error");
	    	}
        	buf = null;
        	temp = null;
	    	//PH_C_FUND_VOLTAGE
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_C_FUND_VOLTAGE));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_C_FUND_VOLTAGE);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_C_FUND_VOLTAGE){
	    		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_FUND_VOLTAGE error");
	    	}
        	buf = null;
        	temp = null;
	    	//PH_A_VOLTAGE_PQM
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_A_VOLTAGE_PQM));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_A_VOLTAGE_PQM);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_A_VOLTAGE_PQM){
	    		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_A_VOLTAGE_PQM error");
	    	}
        	buf = null;
        	temp = null;
	    	//PH_B_VOLTAGE_PQM
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_B_VOLTAGE_PQM));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_B_VOLTAGE_PQM);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_B_VOLTAGE_PQM){
	    		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_B_VOLTAGE_PQM error");
	    	}
        	buf = null;
        	temp = null;
	    	//PH_C_VOLTAGE_PQM
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_PH_C_VOLTAGE_PQM));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_PH_C_VOLTAGE_PQM);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_PH_C_VOLTAGE_PQM){
	    		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get PH_C_VOLTAGE_PQM error");
	    	}
        	buf = null;
        	temp = null;
	    	//VOLTAGE_Z_SEQ
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_VOLTAGE_Z_SEQ));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_VOLTAGE_Z_SEQ);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_VOLTAGE_Z_SEQ){
	    		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get VOLTAGE_Z_SEQ error");
	    	}
	    	//VOLTAGE_P_SEQ
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_VOLTAGE_P_SEQ));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_VOLTAGE_P_SEQ);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_VOLTAGE_P_SEQ){
	    		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get VOLTAGE_P_SEQ error");
	    	}
        	buf = null;
        	temp = null;
	    	//VOLTAGE_N_SEQ
	        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
	        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_VOLTAGE_N_SEQ));
	        
	        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	    	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_VOLTAGE_N_SEQ);  
	    	temp = buf.encode();
	    	
	    	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_VOLTAGE_N_SEQ){
	    		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_PQ_DATA);
	    	}else{
	    		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get VOLTAGE_N_SEQ error");
	    	}
        	buf = null;
        	temp = null;
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
	        EDMI_Mk10_RequestDataFrame frame = null;
	        EDMI_Mk10_ReceiveDataFrame buf = null;
	        byte[] temp = null;
        	
        	/*
        	02 45 0C 98 9A F6 42 C0 64 E8 16 9F 52 F0 90 E3 9D 03 
        	02 45 0C 98 9A F6 42 C0 64 E8 16 A0 52 FF 00 88 1D 03 
        	02 45 0C 98 9A F6 42 C0 64 E8 16 A1 52 F5 32 07 73 03 
        	02 45 0C 98 9A F6 42 C0 64 E8 16 A2 46 36 00 00 00 00 00 01 4D 15 D9 03 
        	*/
        	
        	/*
        	 * Extended
Command : E(Destination’s #)(Source’s #)[Sequence]/normal command/
Response : E(Source’s #)(Destination’s #)[Sequence]/normal response/
        	 */
	        
	        
	        
	        int[] eventstarttime = new int[5];
	        int EVENT_REGISTER_COUNT = 2; // read only system, setup event
	        ByteArray tempEventLog = new ByteArray();
	        
	        for(int i = 0; i < EVENT_REGISTER_COUNT; i++){
	        	
		        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
		        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_EVENTLOG_STARTDATES[i]));
		        
		        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_EVENTLOG_STARTDATES[i]);  
	        	temp = buf.encode();
	        	eventstarttime[i] = DataUtil.getIntTo4Byte(DataFormat.select(temp, 3, 4));
	        	log.debug("event_starttime => "+" index["+(i+1)+"] "+eventstarttime[i]);
	        	buf = null;
	        	temp = null;
	        	
		        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
		        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_EVENTLOG_FIRSTENTRYNUMBER[i]));
		        
		        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_EVENTLOG_FIRSTENTRYNUMBER[i]);  
	        	temp = buf.encode();
	        	int event_firstEntryNumber = DataUtil.getIntTo4Byte(DataFormat.select(temp, 3, 4));
	        	log.debug("eventlog firstentry number => "+event_firstEntryNumber);
	        	buf = null;
	        	temp = null;

		        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
		        send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_EVENTLOG_LASTENTRYNUMBER[i]));
		        
		        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_EVENTLOG_LASTENTRYNUMBER[i]);  
	        	temp = buf.encode();
	        	int event_lastEntryNumber = DataUtil.getIntTo4Byte(DataFormat.select(temp, 3, 4));
	        	log.debug("eventlog lastentry number => "+event_lastEntryNumber);
	        	buf = null;
	        	temp = null;
	        	
	        	
	        	byte[] option_event_surveys = new byte[2];
	        	
	        	switch(i){
	        		case 0: option_event_surveys = EDMI_Mk10_DataConstants.OPTION_EVENT_SURVEYS_1;
	        			break;
	        		case 1: option_event_surveys = EDMI_Mk10_DataConstants.OPTION_EVENT_SURVEYS_2;
	        			break;
	        		case 2: option_event_surveys = EDMI_Mk10_DataConstants.OPTION_EVENT_SURVEYS_3;
	        			break;
	        		case 3: option_event_surveys = EDMI_Mk10_DataConstants.OPTION_EVENT_SURVEYS_4;
	        			break;
	        		case 4: option_event_surveys = EDMI_Mk10_DataConstants.OPTION_EVENT_SURVEYS_5;
	        			break;
	        	}
	        	
		        control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER_EXTENDED); 

		        ByteArray b = new ByteArray();
		        b.append(dest_addr);
		        b.append(source_addr);
		        b.append(DataUtil.get2ByteToInt(sequence));
		        b.append(new byte[]{(byte)0x52,(byte)0xF0,(byte)0x90});
		        send_data = new OCTET(b.toByteArray());
		        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
	        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER_EXTENDED, 0);  
		        buf = null;
		        
		        int number_of_record = event_lastEntryNumber- event_firstEntryNumber;
		        
		        if(number_of_record == 0){
		        	return ba; //return null
		        }
		        if(number_of_record > 340){
		        	number_of_record = 340;
		        	event_firstEntryNumber = event_lastEntryNumber - 340;
		        }
		        
		        b = new ByteArray();
		        b.append(dest_addr);
		        b.append(source_addr);
		        b.append(DataUtil.get2ByteToInt(++sequence));
		        b.append(EDMI_Mk10_DataConstants.CMD_READ_ACCESS_FILE);
		        b.append(option_event_surveys);
		        
		        b.append(DataUtil.get4ByteToInt(event_firstEntryNumber));
		        b.append(DataUtil.get2ByteToInt(number_of_record));
		        send_data = new OCTET(b.toByteArray());
		        frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
		        log.debug("EV SURVEY READ:"+frame.getIoBuffer().getHexDump());
	        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER_EXTENDED, 0); 
	        	temp = buf.encode();
	        	log.debug("EV:"+new OCTET(temp).toHexString());
	        	log.debug("TEMP LENGTH="+temp.length);
	        	tempEventLog.append(temp);
	        	buf = null;
	        	temp = null;  
	        }
	        
	        byte[] temparray = tempEventLog.toByteArray();
        	
        	int evCount = temparray.length/EDMI_Mk10_DataConstants.LEN_EVENT_BLOCK;
        	log.debug("EV COUNT:"+evCount);
        	ba.append(DataUtil.get2ByteToInt(evCount));
        	ba.append(temparray);
     	

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
    	return null;
    }

    public void initProcess(IoSession session) throws MRPException
    {
    	try
    	{
	    	logOn(session);
    	}catch (Exception e)
    	{
    		log.error("initProcess error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"initProcess - error");
    	} 
    }

  
    protected void logOn(IoSession session) throws MRPException
    {
        try
        {
            byte[] idNpW = {'E', 'D','M','I',',','I','M','D','E','I','M','D','E',0x00};
            OCTET control = new OCTET(EDMI_Mk10_DataConstants.CMD_LOGON); 
            OCTET send_data = new OCTET(idNpW);
            
            EDMI_Mk10_RequestDataFrame frame 
                = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null); 
            log.debug("logon =>"+frame.getIoBuffer().getHexDump());

            EDMI_Mk10_ReceiveDataFrame buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_LOGON, 1, 0);    
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
        OCTET control = new OCTET(EDMI_Mk10_DataConstants.CMD_EXIT); 
        OCTET send_data = new OCTET(b);

        EDMI_Mk10_RequestDataFrame frame 
            = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null); 
        EDMI_Mk10_ReceiveDataFrame buf = null;
        
        try
        {
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_EXIT, 1, 0);    
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
	public HashMap timeCheck(IoSession session) throws MRPException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public HashMap timeSync(IoSession session, int timethreshold)
			throws MRPException {

    	HashMap<String, Object> map = new HashMap<String, Object>();
		log.debug("TimeSync Start");
        ByteArray ba = new ByteArray();
    	OCTET control = null;
    	OCTET send_data = null;
        EDMI_Mk10_RequestDataFrame frame = null;
        EDMI_Mk10_ReceiveDataFrame buf = null;
        byte[] temp = null;
        
        try{
//        	CURR_DATE_TIME
            control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
            send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_CURR_DATE_TIME));
            
            frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
            log.debug("Request DATETIME=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_CURR_DATE_TIME);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_CURR_DATE_TIME){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_CURR_DATE_TIME);
    	        map.put("beforeTime", new OCTET(ba.toByteArray()));
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURR_DATE_TIME error");
        	}
        }catch(Exception e){
        	
        }
        ba = new ByteArray();
        
        
        ByteArray b = new ByteArray();
        
        int year = Integer.parseInt(TimeUtil.getCurrentTimeMilli().substring(0, 4));
        int month = Integer.parseInt(TimeUtil.getCurrentTimeMilli().substring(4, 6));
        int day = Integer.parseInt(TimeUtil.getCurrentTimeMilli().substring(6, 8));
        int hour = Integer.parseInt(TimeUtil.getCurrentTimeMilli().substring(8, 10));
        int min = Integer.parseInt(TimeUtil.getCurrentTimeMilli().substring(10, 12));
        int sec = Integer.parseInt(TimeUtil.getCurrentTimeMilli().substring(12, 14));
           
//    	SET_DATE_TIME
        control = new OCTET(EDMI_Mk10_DataConstants.CMD_WRITE_REGISTER); 
        
        b.append(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_CURR_DATE_TIME));
        b.append((byte) day);
        b.append((byte) month);
        b.append((byte) ((year % 100)&0xFF));
        b.append((byte) hour);
        b.append((byte) min);
        b.append((byte) sec);        
        send_data = new OCTET(b.toByteArray());
        
        
        try{
            frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
            log.debug("Request DATETIME=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_WRITE_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_CURR_DATE_TIME);  
        	temp = buf.encode();
        	
//        	CURR_DATE_TIME
            control = new OCTET(EDMI_Mk10_DataConstants.CMD_READ_REGISTER); 
            send_data = new OCTET(DataUtil.get2ByteToInt(EDMI_Mk10_DataConstants.REGISTER_CURR_DATE_TIME));
            
            frame = new EDMI_Mk10_RequestDataFrame(null, control, send_data, 0,0, null);
            log.debug("Request DATETIME=>"+frame.getIoBuffer().getHexDump());
        	buf = read(session,frame, EDMI_Mk10_DataConstants.CMD_READ_REGISTER, 0, EDMI_Mk10_DataConstants.REGISTER_CURR_DATE_TIME);  
        	temp = buf.encode();
        	
        	if(DataUtil.getIntTo2Byte(DataUtil.select(temp, 1, 2))==EDMI_Mk10_DataConstants.REGISTER_CURR_DATE_TIME){
        		ba.append(temp,3,EDMI_Mk10_DataConstants.LEN_CURR_DATE_TIME);
    	        map.put("afterTime", new OCTET(ba.toByteArray()));
        	}else{
        		throw new MRPException(MRPError.ERR_READ_METER_CLASS,"get CURR_DATE_TIME error");
        	}

        }catch(Exception e){
            log.error("Meter Write Time error",e);
            throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"Meter Info Read error");
        }


		return map;
	}
	
	
	public byte[] getDateTimeArray(String datetime){
        byte[] ret = new byte[6];
        ret[0] = (byte) Integer.parseInt(datetime.substring(6,8));
        ret[1] = (byte) Integer.parseInt(datetime.substring(4,6));
        ret[2] = (byte) (Integer.parseInt(datetime.substring(0,4))%1000);
        ret[3] = (byte) Integer.parseInt(datetime.substring(8,10));
        ret[4] = (byte) Integer.parseInt(datetime.substring(10,12));
        ret[5] = 0;	
        return ret;
	}
	public String getDateTime(long sec) {

		String dateString = new String();

		try {
			Calendar c = Calendar.getInstance();

			int yy = 1996;
			int mm = 1;
			int day = 1;
			int HH = 0;
			int MM = 0;
			int SS = 0;

	/* why mm-1 because month start from 0 */
			c.set(yy, mm-1, day, HH, MM, SS);
			long temp = c.getTimeInMillis();
			c.setTimeInMillis(temp+sec*1000);
			dateString = DateTimeUtil.getDateString(c.getTime());

		} catch (Exception e) {
			log.warn(e,e);
		}
		return dateString;
	}
    @Override
    public CommandData execute(Socket socket, CommandData command)
            throws MRPException {
        // TODO Auto-generated method stub
        return null;
    }
}
