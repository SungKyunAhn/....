package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.util.DataFormat;

/**
 * CmdDataMiuTimeSync
 * 
 * Command Frame MIU timesync
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 1:51:24$
 */
public class CmdDataMiuTimeSync extends AMUFramePayLoad{
	
	private Log log = LogFactory.getLog(CmdDataMiuTimeSync.class);

	byte[] timeZone = null;
	byte[] dst		= null;
	byte[] year		= null;
	byte month;
	byte day;
	byte hour;
	byte minute;
	byte second;
	
	MiuTimeSyncCommandData mTimeSyncData;
	/**
	 * constructor
	 */
	public CmdDataMiuTimeSync(){
	}

	/**
	 * constructor
	 * 
	 * @param isRequest
	 * @param isWrite
	 * @param isResRequest
	 * @param data
	 */
	public CmdDataMiuTimeSync(boolean isRequest, boolean isWrite, boolean isResRequest , byte[] data){
		
		this.identifier = AMUFramePayLoadConstants.FrameIdentifier.CMD_MIU_TIME_SYNC;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isWrite)
			this.control |= (byte)0x20;
		if(isResRequest)
			this.control |= (byte)0x02;
		if(isWrite)
			this.payLoadData = data;
	}
	
	/**
	 * constructor
	 * @param isRequest
	 * @param isWrite
	 * @param isResRequest
	 * @param timeZone
	 * @param dst
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 */
	public CmdDataMiuTimeSync(boolean isRequest, boolean isWrite, boolean isResRequest ,
			byte[] timeZone , byte[] dst , byte[] year ,
			byte month, byte day , byte hour, byte minute , byte second){
		
		this.identifier = AMUFramePayLoadConstants.FrameIdentifier.CMD_MIU_TIME_SYNC;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isWrite)
			this.control |= (byte)0x20;
		if(isResRequest)
			this.control |= (byte)0x02;
		if(isWrite){
			this.timeZone 	= timeZone;
			this.dst 		= dst;
			this.year 		= year;
			this.month 		= month;
			this.day 		= day;
			this.hour 		= hour;
			this.minute 	= minute;
			this.second 	= second;
		}
	}
	

	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public CmdDataMiuTimeSync(byte[] framePayLoad) throws Exception{
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
					
					this.mTimeSyncData = new MiuTimeSyncCommandData(
											DataFormat.select(framePayLoad , pos , 
														AMUFramePayLoadConstants.FormatLength.CMD_MIU_ROUTE_CONFIG_TIMESYNC_DATA));
				
				}
			}else{
				throw new Exception("MIU Timesync Command decode failed, Not receive Response");
			}
		}catch(Exception e){
			log.error("MIU Timesync Command decode failed : ", e);
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
					bao.write(this.payLoadData 		, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_TIMESYC_REQ_DATA);
					/*
					bao.write(this.timeZone 			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_TIMEZONE);
					bao.write(this.dst 					, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_DST);
					bao.write(this.year 				, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_YEAR);
					bao.write(new byte[]{this.month} 	, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_MONTH);
					bao.write(new byte[]{this.day} 		, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_DAY);
					bao.write(new byte[]{this.hour} 	, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_HOUR);
					bao.write(new byte[]{this.minute} 	, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_MINUTE);
					bao.write(new byte[]{this.second} 	, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_SECOND);
					*/
				}
			}else{
				throw new Exception("MIU Timesync Command encode failed, Not Request");
			}	
		}catch(Exception e){
			log.error("MIU Timesync Command encode failed : ", e);
			throw e;
		}
		return bao.toByteArray();	
	}	
}