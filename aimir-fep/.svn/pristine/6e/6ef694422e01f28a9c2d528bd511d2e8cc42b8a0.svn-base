package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.util.DataFormat;

/**
 * CmdDataLaunchBootLoader
 * 
 * Command Frame Launch Bootloader
 *
 * @author  : taeho Park 
 * @version : $REV $2, 2010. 2. 18. 오후 7:12:16$
 */
public class CmdDataLaunchBootLoader extends AMUFramePayLoad {
	
	private Log log = LogFactory.getLog(CmdDataLaunchBootLoader.class);
	
	/**
	 * constructor
	 */
	public CmdDataLaunchBootLoader(){
	}
	
	/**
	 * constructor
	 * 
	 * @param isRequest
	 * @param isResRequest
	 */
	public CmdDataLaunchBootLoader(boolean isRequest, boolean isResRequest, int responseTime){
		
		this.identifier = AMUFramePayLoadConstants.FrameIdentifier.CMD_LAUNCH_BOOTLOADER;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isResRequest)
			this.control |= (byte)0x02;
		
		this.responseTime = (byte)responseTime;
		// 0x3811 - Lauch bootloader Code Value
		this.payLoadData = AMUFramePayLoadConstants.FormatCode.CMD_CODE_LAUNCH_BOOTLOADER;
	}	
	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public CmdDataLaunchBootLoader(byte[] framePayLoad) throws Exception{
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
				throw new Exception("Launch Bootloader Command decode failed, Not receive Response");
			}
		}catch(Exception e){
			log.error("Launch Bootloader Command decode failed : ", e);
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
				bao.write(this.payLoadData			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_LAUNCH_BOOTLOADER_CODE);
			}else{
				throw new Exception("Launch Bootloader Command encode failed, Not Request");
			}
		}catch(Exception e){
			log.error("Launch Bootloader Command encode failed : ", e);
			throw e;
		}
		return bao.toByteArray();	
	}	
}