package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.util.DataFormat;

/**
 * CmdDataMiuRouteConfigTimeSync
 * 
 * Command Frame MIU route configuration & timesync
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 12:09:35$
 */
public class CmdDataMiuRouteConfigTimeSync extends AMUFramePayLoad{
	
	private Log log = LogFactory.getLog(CmdDataMiuRouteConfigTimeSync.class);
	
	byte miuType;
	byte[] nodeId = null;
	byte rfPower;
	byte receivedRSSI;
	byte receivedLQI;
	
	MiuTimeSyncCommandData mTimeSyncData;
	/**
	 * constructor
	 */
	public CmdDataMiuRouteConfigTimeSync(){
	}

	/**
	 * constructor
	 * 
	 * @param isRequest
	 * @param isWrite
	 * @param isResRequest
	 * @param data
	 */
	public CmdDataMiuRouteConfigTimeSync(boolean isRequest, boolean isWrite, boolean isResRequest , byte[] data){
		
		this.identifier = AMUFramePayLoadConstants.FrameIdentifier.CMD_MIU_ROUTE_CONFIG_AND_TIMESYNC;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isWrite){
			this.control |= (byte)0x20;
		}
		if(isResRequest)
			this.control |= (byte)0x02;
		
		this.responseTime = (byte)responseTime;
		
		if(isWrite){
			this.payLoadData = data;
		}
	}
	
	/**
	 * constructor ( write Requset)
	 *  
	 * @param isRequest
	 * @param isWrite
	 * @param isResRequest
	 * @param mainServerIP
	 * @param eventServerIP
	 * @param mainServerPort
	 * @param eventServerPort
	 * @param mcuMobileModuleNumber
	 * @param mcuEthernetModuleIP
	 * @param mcuCoordinatorMouduleEUI64ID
	 * @param timeZone
	 * @param dst
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 */
	public CmdDataMiuRouteConfigTimeSync(boolean isRequest, boolean isWrite, boolean isResRequest,
		byte[] mainServerIP, byte[] eventServerIP , byte[] mainServerPort, byte[] eventServerPort,
		byte[] mcuMobileModuleNumber, byte[] mcuEthernetModuleIP, byte[] mcuCoordinatorMouduleEUI64ID,
		byte[] timeZone , byte[] dst, byte[] year , 
		byte month,  byte day , byte hour , byte minute , byte second)
	{
		
		this.identifier = AMUFramePayLoadConstants.FrameIdentifier.CMD_MIU_ROUTE_CONFIG_AND_TIMESYNC;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isWrite)
			this.control |= (byte)0x20;
		if(isResRequest)
			this.control |= (byte)0x02;
		
		this.responseTime = (byte)responseTime;
		
		if(isWrite){
			mTimeSyncData = new MiuTimeSyncCommandData();
			
			mTimeSyncData.setMainServerIP(mainServerIP);
			mTimeSyncData.setEventServerIP(eventServerIP);
			mTimeSyncData.setMainServerPort(mainServerPort);
			mTimeSyncData.setEventServerPort(eventServerPort);
			mTimeSyncData.setMcuMobileModuleNumber(mcuMobileModuleNumber);
			mTimeSyncData.setMcuEthernetModuleIP(mcuEthernetModuleIP);
			mTimeSyncData.setMcuCoordinatorMouduleEUI64ID(mcuCoordinatorMouduleEUI64ID);
			mTimeSyncData.setTimeZone(timeZone);
			mTimeSyncData.setDst(dst);
			mTimeSyncData.setYear(year);
			mTimeSyncData.setMonth(month);
			mTimeSyncData.setDay(day);
			mTimeSyncData.setHour(hour);
			mTimeSyncData.setMinute(minute);
			mTimeSyncData.setSecond(second);
		}
	}

	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public CmdDataMiuRouteConfigTimeSync(byte[] framePayLoad) throws Exception{
		try {
			decode(framePayLoad);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/** ***************************   Getter && Setter *************************** */
	public byte getMiuType() {
		return miuType;
	}

	public void setMiuType(byte miuType) {
		this.miuType = miuType;
	}

	public byte[] getNodeId() {
		return nodeId;
	}

	public void setNodeId(byte[] nodeId) {
		this.nodeId = nodeId;
	}

	public byte getRfPower() {
		return rfPower;
	}

	public void setRfPower(byte rfPower) {
		this.rfPower = rfPower;
	}

	public byte getReceivedRSSI() {
		return receivedRSSI;
	}

	public void setReceivedRSSI(byte receivedRSSI) {
		this.receivedRSSI = receivedRSSI;
	}

	public byte getReceivedLQI() {
		return receivedLQI;
	}

	public void setReceivedLQI(byte receivedLQI) {
		this.receivedLQI = receivedLQI;
	}

	
	/* *************************************************************************** */
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
				
				// Write
				if(isWrite(this.control)){
					
					this.miuType = framePayLoad[pos];
					pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_TYPE;
					
					this.nodeId	 = DataFormat.select(framePayLoad, pos, 
							AMUFramePayLoadConstants.FormatLength.CMD_MIU_NODE_ID); 
					pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_NODE_ID;
					
					this.rfPower = framePayLoad[pos];
					pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_RF_POWER;
					
					this.receivedRSSI = framePayLoad[pos];
					pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_RECEIVED_RSSI;
					
					this.receivedLQI = framePayLoad[pos];
					pos += AMUFramePayLoadConstants.FormatLength.CMD_MIU_RECEIVED_LQI;
				}
				// Read
				else{
					
					this.mTimeSyncData = new MiuTimeSyncCommandData(
								DataFormat.select(framePayLoad, pos, 
												AMUFramePayLoadConstants.FormatLength.CMD_MIU_ROUTE_CONFIG_TIMESYNC_DATA));
					
				}
			}else{
				throw new Exception("MIU route configuration & timeSync Command decode failed, Not receive Response");
			}
		}catch(Exception e){
			log.error("MIU route configuration & timeSync Command decode failed : ", e);
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
					bao.write(this.payLoadData 		, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_ROUTE_CONFIG_TIMESYNC_DATA);
					
					/* 추후 CmdDataMiuRouteConfigTimeSync 생성시 data를 어떻게 Setting 할것인지 결정 할것
						bao.write(this.mTimeSyncData.encode(), 0, AMUFramePayLoadConstants.FormatLength.CMD_MIU_ROUTE_CONFIG_TIMESYNC_DATA);
					*/
				}
			}else{
				throw new Exception("MIU route configuration & timeSync Command encode failed, Not Request");
			}	
		}catch(Exception e){
			log.error("MIU route configuration & timeSync Command encode failed : ", e);
			throw e;
		}
		return bao.toByteArray();	
	}	
}


