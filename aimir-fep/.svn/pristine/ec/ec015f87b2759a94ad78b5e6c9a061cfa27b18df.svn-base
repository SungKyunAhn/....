package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.util.DataFormat;

/**
 * CmdDataMiuScheduleConfig
 * 
 * Command Frame MIU Schedule Configuration
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 2:11:09$
 */
public class CmdDataMiuScheduleConfig extends AMUFramePayLoad{
	
	private Log log = LogFactory.getLog(CmdDataMiuScheduleConfig.class);

	byte[] mainMeteringInterval = null;
	byte[] subMeteringInterval	= null;
	byte[] resetInterval		= null;
	
	/**
	 * constructor
	 */
	public CmdDataMiuScheduleConfig(){
	}

	/**
	 * constructor
	 * 
	 * @param isRequest
	 * @param isWrite
	 * @param isResRequest
	 * @param data
	 */
	public CmdDataMiuScheduleConfig(boolean isRequest, boolean isWrite, boolean isResRequest , byte[] data){
		
		this.identifier = AMUFramePayLoadConstants.FrameIdentifier.CMD_MIU_SCHEDULE_CONFIG;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isWrite)
			this.control |= (byte)0x20;
		if(isResRequest)
			this.control |= (byte)0x02;
		if(isWrite)
			this.payLoadData = data;
	}

	public CmdDataMiuScheduleConfig(boolean isRequest, boolean isWrite, boolean isResRequest,
			byte[] mainMeteringInterval , byte[] subMeteringInterval , byte[] resetInterval){
		
		this.identifier = AMUFramePayLoadConstants.FrameIdentifier.CMD_MIU_SCHEDULE_CONFIG;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isWrite)
			this.control |= (byte)0x20;
		if(isResRequest)
			this.control |= (byte)0x02;
		if(isWrite){
			this.mainMeteringInterval 	= mainMeteringInterval;
			this.subMeteringInterval 	= subMeteringInterval;
			this.resetInterval			= resetInterval;
		}
			
	}

	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public CmdDataMiuScheduleConfig(byte[] framePayLoad) throws Exception{
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
				pos +=  AMUFramePayLoadConstants.FormatLength.CMD_STATUS;
				
				this.frameStatus = new AMUDataFrameStatus(statCode);
				
				// Read
				if(!isWrite(this.control)){
					this.payLoadData = DataFormat.select(framePayLoad, pos, 
							AMUFramePayLoadConstants.FormatLength.CMD_MIU_SCHEDULE_CONFIG_RES_DATA);
					
					/*20 byte MIU Schedule configuration Command Data가 존재하나
					 * 문서상에 정의 되어 있지 않아서 멤버변수나 Class화 하지 못함 
					 * 추후 확인 필요 
					 * */
				}
			}else{
				throw new Exception("MIU Schedule Configuration Command decode failed, Not receive Response");
			}
		}catch(Exception e){
			log.error("MIU Schedule Configuration Command decode failed : ", e);
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
				// write
				if(isWrite(this.control)){
					bao.write(this.payLoadData 		, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_SCHEDULE_CONFIG_REQ_DATA);
					
					/* 추후 생성자를 어떻게 쓸것인지 확인하고 생성자에 따른 encode를 할것
					bao.write(this.mainMeteringInterval 	, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_MAIN_METERING_INTERVAL);
					bao.write(this.subMeteringInterval 		, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_SUB_METERING_INTERVAL);
					bao.write(this.resetInterval 			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_RESET_INTERVAL);
					*/
				}
			}else{
				throw new Exception("MIU Schedule Configuration Command encode failed, Not Request");
			}	
		}catch(Exception e){
			log.error("MIU Schedule Configuration Command encode failed : ", e);
			throw e;
		}
		return bao.toByteArray();	
	}	
}


