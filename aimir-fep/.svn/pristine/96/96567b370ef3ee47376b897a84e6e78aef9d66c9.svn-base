package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.util.DataFormat;

/**
 * CmdDataModemInitialization
 *
 * Command Fraem Modem Initialization
 * 
 * @author  : taeho Park 
 * @version : $REV $2, 2010. 2. 18. 오후 7:03:04$
 */
public class CmdDataModemInitialization extends AMUFramePayLoad{

	private Log log = LogFactory.getLog(CmdDataModemInitialization.class);
	
	/**
	 * constructor
	 */
	public CmdDataModemInitialization(){
	}
	
	/**
	 * constructor
	 * 
	 * @param isRequest
	 * @param isResRequest
	 */
	public CmdDataModemInitialization(boolean isRequest, boolean isResRequest , int responseTime){
		
		this.identifier = AMUFramePayLoadConstants.FrameIdentifier.CMD_MODEM_INIT;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isResRequest)
			this.control |= (byte)0x02;
		
		this.responseTime = (byte) responseTime;
		// 0x8788 - Modem Initialization Code Value
		this.payLoadData = AMUFramePayLoadConstants.FormatCode.CMD_CODE_MODEM_INIT;
	}	
	
	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public CmdDataModemInitialization(byte[] framePayLoad) throws Exception{
		try {
			decode(framePayLoad);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * decode
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public void decode(byte[] framePayLoad) throws Exception{
		
		try{
			int pos =0;
			this.identifier = framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.FRAME_IDENTIFIER;
			
			this.control	= framePayLoad[pos];
			pos += AMUFramePayLoadConstants.FormatLength.FRAME_CONTROL;
			
			// Response
			if(!isRequest(this.control)){
				byte[] statCode = DataFormat.select(framePayLoad, pos, 
						AMUFramePayLoadConstants.FormatLength.CMD_STATUS);
				
				this.frameStatus = new AMUDataFrameStatus(statCode);
			}else{
				throw new Exception("Modem Initialization Command decode failed, Not receive Response");
			}
		}catch(Exception e){
			log.error("Modem Initialization Command decode failed : ", e);
			throw e;
		}
	}
	
	/**
	 * encode
	 * 
	 * @return  byte[]
	 * @throws Exception
	 */
	public byte[] encode() throws Exception{
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		
		try{
			bao.write(new byte[]{this.identifier} 	, 0 , AMUFramePayLoadConstants.FormatLength.FRAME_IDENTIFIER);
			bao.write(new byte[]{this.control} 		, 0 , AMUFramePayLoadConstants.FormatLength.FRAME_CONTROL);
			bao.write(new byte[]{this.responseTime} , 0 , AMUFramePayLoadConstants.FormatLength.CMD_RESPONSE_TIME);
			// Request
			if(isRequest(this.control)){
				bao.write(this.payLoadData			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MODEM_INIT_CODE);
			}else{
				throw new Exception("Modem Initialization Command encode failed, Not Request");
			}
		}catch(Exception e){
			log.error("Modem Initialization Command encode failed : ", e);
			throw e;
		}
		return bao.toByteArray();	
	}	
	
}


