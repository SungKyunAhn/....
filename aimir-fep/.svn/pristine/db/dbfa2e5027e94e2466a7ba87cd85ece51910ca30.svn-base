package com.aimir.fep.protocol.fmp.frame.amu;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.util.DataFormat;

/**
 * CmdDataMiuRouteConfig
 * 
 * Command Frame MIU Configuration
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오후 1:33:22$
 */
public class CmdDataMiuRouteConfig extends AMUFramePayLoad{

	private Log log = LogFactory.getLog(CmdDataMiuRouteConfig.class);
	
	byte[] amrServerIP				= null;
	byte[] eventServerIP			= null;
	byte[] amrServerPort			= null;
	byte[] eventServerPort			= null;
	byte[] mobileModuleNumber 		= null;
	byte[] ethernetModuleIP			= null;
	byte[] coordinatorModuleEUI64ID	= null;
	
	/**
	 * constructor
	 */
	public CmdDataMiuRouteConfig(){
	}

	/**
	 * constructor
	 * 
	 * @param isRequest
	 * @param isWrite
	 * @param isResRequest
	 * @param data
	 */
	public CmdDataMiuRouteConfig(boolean isRequest, boolean isWrite, boolean isResRequest , byte[] data){
		
		this.identifier = AMUFramePayLoadConstants.FrameIdentifier.CMD_MIU_ROUTE_CONFIG;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isWrite)
			this.control |= (byte)0x20;
		if(isResRequest)
			this.control |= (byte)0x02;
		
		this.responseTime = (byte)responseTime;
		
		if(isWrite){
			this.payLoadData = data;
		}
	}
	
	/**
	 * constructor
	 * 
	 * @param isRequest
	 * @param isWrite
	 * @param isResRequest
	 * @param amrServerIP
	 * @param eventServerIP
	 * @param amrServerPort
	 * @param eventServerPort
	 * @param mobileModuleNumber
	 * @param ethernetModuleIP
	 * @param coordinatorModuleEUI64ID
	 */
	public CmdDataMiuRouteConfig(boolean isRequest, boolean isWrite, boolean isResRequest ,
			byte[] amrServerIP , byte[] eventServerIP , byte[] amrServerPort , byte[] eventServerPort ,
			byte[] mobileModuleNumber , byte[] ethernetModuleIP, byte[] coordinatorModuleEUI64ID)
	{
		
		this.identifier = AMUFramePayLoadConstants.FrameIdentifier.CMD_MIU_ROUTE_CONFIG;
		
		if(isRequest)
			this.control |= (byte)0x80;
		if(isWrite)
			this.control |= (byte)0x20;
		if(isResRequest)
			this.control |= (byte)0x02;
		
		this.responseTime = (byte)responseTime;
		
		if(isWrite){
			
			this.amrServerIP = amrServerIP;
			this.eventServerIP = eventServerIP;
			this.amrServerPort = amrServerPort;
			this.eventServerPort = eventServerPort;
			this.mobileModuleNumber = mobileModuleNumber;
			this.ethernetModuleIP = ethernetModuleIP;
			this.coordinatorModuleEUI64ID = coordinatorModuleEUI64ID;
			
		}
	}

	/**
	 * constructor
	 * 
	 * @param payLoadData
	 * @throws Exception
	 */
	public CmdDataMiuRouteConfig(byte[] framePayLoad) throws Exception{
		try {
			decode(framePayLoad);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/** ***************************   Getter && Setter *************************** */
	public byte[] getAmrServerIP() {
		return amrServerIP;
	}

	public void setAmrServerIP(byte[] amrServerIP) {
		this.amrServerIP = amrServerIP;
	}

	public byte[] getEventServerIP() {
		return eventServerIP;
	}

	public void setEventServerIP(byte[] eventServerIP) {
		this.eventServerIP = eventServerIP;
	}

	public byte[] getAmrServerPort() {
		return amrServerPort;
	}

	public void setAmrServerPort(byte[] amrServerPort) {
		this.amrServerPort = amrServerPort;
	}

	public byte[] getEventServerPort() {
		return eventServerPort;
	}

	public void setEventServerPort(byte[] eventServerPort) {
		this.eventServerPort = eventServerPort;
	}

	public byte[] getMobileModuleNumber() {
		return mobileModuleNumber;
	}

	public void setMobileModuleNumber(byte[] mobileModuleNumber) {
		this.mobileModuleNumber = mobileModuleNumber;
	}

	public byte[] getEthernetModuleIP() {
		return ethernetModuleIP;
	}

	public void setEthernetModuleIP(byte[] ethernetModuleIP) {
		this.ethernetModuleIP = ethernetModuleIP;
	}

	public byte[] getCoordinatorModuleEUI64ID() {
		return coordinatorModuleEUI64ID;
	}

	public void setCoordinatorModuleEUI64ID(byte[] coordinatorModuleEUI64ID) {
		this.coordinatorModuleEUI64ID = coordinatorModuleEUI64ID;
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
							AMUFramePayLoadConstants.FormatLength.CMD_MIU_ROUTE_CONFIG_DATA);
				
					/*
					this.amrServerIP = DataFormat.select(framePayLoad, pos, 
							AMUFramePayLoadConstants.FormatLength.CMD_MIU_AMR_SERVER_ID);
					pos +=  AMUFramePayLoadConstants.FormatLength.CMD_MIU_AMR_SERVER_ID;
					
					this.eventServerIP = DataFormat.select(framePayLoad, pos, 
							AMUFramePayLoadConstants.FormatLength.CMD_MIU_EVENT_SERVER_IP);
					pos +=  AMUFramePayLoadConstants.FormatLength.CMD_MIU_EVENT_SERVER_IP;
					
					this.amrServerPort = DataFormat.select(framePayLoad, pos, 
							AMUFramePayLoadConstants.FormatLength.CMD_MIU_AMR_SERVER_PORT);
					pos +=  AMUFramePayLoadConstants.FormatLength.CMD_MIU_AMR_SERVER_PORT;
					
					this.eventServerPort = DataFormat.select(framePayLoad, pos, 
							AMUFramePayLoadConstants.FormatLength.CMD_MIU_EVENT_SERVER_PORT);
					pos +=  AMUFramePayLoadConstants.FormatLength.CMD_MIU_EVENT_SERVER_PORT;
					
					this.mobileModuleNumber = DataFormat.select(framePayLoad, pos, 
							AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_MOBILE_MODULE_NUMBER);
					pos +=  AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_MOBILE_MODULE_NUMBER;
					
					this.ethernetModuleIP = DataFormat.select(framePayLoad, pos, 
							AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_ETHERNET_MODULE_IP);
					pos +=  AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_ETHERNET_MODULE_IP;
					
					this.coordinatorModuleEUI64ID = DataFormat.select(framePayLoad, pos, 
							AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_COORDINATOR_MOUDULE_EUI64ID);
					*/
				}
			}else{
				throw new Exception("MIU route configuration Command decode failed, Not receive Response");
			}
		}catch(Exception e){
			log.error("MIU route configuration Command decode failed : ", e);
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
					bao.write(this.payLoadData 		, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_ROUTE_CONFIG_DATA);
					
					/* 추후 생성자 Setting 확인 후 변경 할것
					bao.write(this.amrServerIP 				, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_AMR_SERVER_ID);
					bao.write(this.eventServerIP 			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_EVENT_SERVER_IP);
					bao.write(this.amrServerPort 			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_AMR_SERVER_PORT);
					bao.write(this.eventServerPort 			, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_EVENT_SERVER_PORT);
					bao.write(this.mobileModuleNumber 		, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_MOBILE_MODULE_NUMBER);
					bao.write(this.ethernetModuleIP 		, 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_ETHERNET_MODULE_IP);
					bao.write(this.coordinatorModuleEUI64ID , 0 , AMUFramePayLoadConstants.FormatLength.CMD_MIU_MCU_COORDINATOR_MOUDULE_EUI64ID);
					*/
				}
			}else{
				throw new Exception("MIU route configuration Command encode failed, Not Request");
			}	
		}catch(Exception e){
			log.error("MIU route configuration Command encode failed : ", e);
			throw e;
		}
		return bao.toByteArray();	
	}	
}