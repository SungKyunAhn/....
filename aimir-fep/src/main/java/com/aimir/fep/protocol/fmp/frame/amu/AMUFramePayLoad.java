package com.aimir.fep.protocol.fmp.frame.amu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.frame.AMUDataFrameStatus;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * FramePayLoad
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 18. 오후 1:20:20$
 */
public class AMUFramePayLoad implements java.io.Serializable{

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(AMUFramePayLoad.class);
	
	byte[] rawData			= null;
	
	byte identifier;
	byte control;
	
	byte responseTime;		// Command Frame
	
	/*
	 * Command Frame encode시에 사용하고 필드가 더 있을경우에는 
	 * 각 Frame 개별로 존재 
	 */
	byte[] payLoadData		= null; 
	
	AMUDataFrameStatus frameStatus;
	
	/**
	 * constructor
	 */
	public AMUFramePayLoad(){
	}
	
	/**
	 * constructor
	 * @param rawData
	 */
	public AMUFramePayLoad(byte[] rawData){
		this.rawData = rawData;
	}
	
	/**
	 * get rawData
	 * @return
	 */
	public byte[] getRawData() {
		return rawData;
	}

	/**
	 * set rawData
	 * @param rawData
	 */
	public void setRawData(byte[] rawData) {
		this.rawData = rawData;
	}

	/**
	 * get Identifier
	 * @return
	 */
	public byte getIdentifier() {
		return identifier;
	}

	/**
	 * set Identifier
	 * @param identifier
	 */
	public void setIdentifier(byte identifier) {
		this.identifier = identifier;
	}

	/**
	 * get Control
	 * @return
	 */
	public byte getControl() {
		return control;
	}

	/**
	 * set Control
	 * @param control
	 */
	public void setControl(byte control) {
		this.control = control;
	}

	public byte getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(byte responseTime) {
		this.responseTime = responseTime;
	}

	/**
	 * get Payload Data
	 * @return
	 */
	public byte[] getPayLoadData() {
		return payLoadData;
	}

	/**
	 * set Payload Data
	 * @param payLoadData
	 */
	public void setPayLoadData(byte[] payLoadData) {
		this.payLoadData = payLoadData;
	}

	/**
	 * constructor
	 * 
	 * @param frameType
	 * @param rawData
	 * @throws Exception
	 */
	public AMUFramePayLoad(byte frameType ,byte[] rawData) throws Exception {
		
		try{
			decode(frameType ,rawData);
		}catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * AMUFramePayLoad Decode
	 * 
	 * @param frameType
	 * @param data
	 * @return
	 */
	public static AMUFramePayLoad decode(byte frameType, byte[] framePayLoad) throws Exception{
		
		log.debug("###########  AMUFramePayLoad DECODE START ####################");
		log.debug("frameType [" +  Hex.decode(new byte[]{frameType})+"]" );
		log.debug("Frame PayLoad Data : " + Hex.decode(framePayLoad));
		
		AMUFramePayLoad fpldata = null;
		
		try{
			
			// LINK FRAME (Identifier 없음)
			if(frameType==AMUGeneralDataConstants.FRAMETYPE_LINK){
				log.debug("### Link Data Frame ###");
				fpldata = new LinkData(framePayLoad);
			}
			else 
			{
				if(framePayLoad != null){
					
					byte Identifier = framePayLoad[0];	// 1byte
											
					// EVENT FRAME
					if(frameType==AMUGeneralDataConstants.FRAMETYPE_EVENT){
						
						log.debug("### EventData Frame ### Identifier [" + AMUFramePayLoadConstants.getEventIdentifierName(Identifier)+ "]");
						
						if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.EVENT_STATUS_CHANGED){
							fpldata	= new EventDataStatusChanged(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.EVENT_POWER_OUTAGE){
							fpldata	= new EventDataPowerOutage(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.EVENT_BOOT_UP){
							fpldata	= new EventDataBootUp(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.EVENT_STACK_UP){
							fpldata	= new EventDataStackUp(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.EVENT_RX_RF_STATE){
							log.debug("Rx Rf Event Frame not existed");
							return null;
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.EVENT_JOIN_REQUEST){
							fpldata	= new EventDataJoinRequest(framePayLoad);
						}else{
							log.error("Event Frame Type Identifier Error");
							return null;
						}
					}
					// COMMAND FRAME
					else if(frameType==AMUGeneralDataConstants.FRAMETYPE_COMMAND){
						
						log.debug("### CommandData Frame ### Identifier [" + AMUFramePayLoadConstants.getCommandIdentifierName(Identifier)+ "]");
						
						if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.CMD_RESET){
							fpldata	= new CmdDataReset(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.CMD_CHANGE_AUTH_KEY){
							fpldata	= new CmdDataChangeAuthenKey(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.CMD_MODEM_INIT){
							fpldata	= new CmdDataModemInitialization(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.CMD_LAUNCH_BOOTLOADER){
							fpldata = new CmdDataLaunchBootLoader(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.CMD_OTA_ROM){
							fpldata	= new CmdDataOtaRom(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.CMD_VALIDATE_OTAROM){
							fpldata	= new CmdDataValidateOtaRom(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.CMD_UPGRADE_FW){
							fpldata = new CmdDataUpgradeFW(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.CMD_MIU_VERSION){
							fpldata = new CmdDataMiuVersion(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.CMD_MIU_ROUTE_CONFIG_AND_TIMESYNC){
							fpldata = new CmdDataMiuRouteConfigTimeSync(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.CMD_MIU_ROUTE_CONFIG){
							fpldata	= new CmdDataMiuRouteConfig(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.CMD_MIU_TIME_SYNC){
							fpldata	= new CmdDataMiuTimeSync(framePayLoad);
						}else if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.CMD_MIU_SCHEDULE_CONFIG){
							fpldata	= new CmdDataMiuScheduleConfig(framePayLoad);
						}else{
							log.error("AMU Command Frame Type Identifier Error");
							return null;
						}
					}// BYPASS FRAME
					else if(frameType==AMUGeneralDataConstants.FRAMETYPE_BYPASS){
						//fpldata = new BypassData(framePayLoad);
					}
					// METERING FRAME
					else if(frameType==AMUGeneralDataConstants.FRAMETYPE_METERING){
						
						log.debug("### METERING Data Frame ### Identifier [" + AMUFramePayLoadConstants.getMeteringIdentifierName(Identifier)+ "]");
						
						if(Identifier == AMUFramePayLoadConstants.FrameIdentifier.MD_INFO){
							log.debug("AMUFramePayLoad Decode Call MeteringInfo");
							fpldata = new MeteringDataInfo(framePayLoad);
						}else {
							log.debug("AMUFramePayLoad Decode Call Metering Data");
							return null;
						}
					}
				}else{
			
					throw new Exception("framePayLoad Length is 0");
				}
			}
			
		}catch(Exception e){
			log.error("AMU framePayLoad decode failed : ", e);
			throw e;
		}
		return fpldata;
	}
	
	/**`
	 * is Request
	 * 해당 field가 Request field인지 Response field인지 검사 
	 * 
	 * @param in
	 * @return boolean true or false
	 */
	public boolean isRequest(byte in){
		if(((byte)in & 0x80) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * is Opened
	 * 
	 * @return boolean true or false
	 */
	public boolean isOpened(byte in){
		if(((byte)in & 0x40) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * is Res.Request
	 * 
	 * @return boolean true or false
	 */
	public boolean isResRequest(byte in){
		if(((byte)in & 0x20) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * is SetLink Time Out
	 * 
	 * @return boolean true or false
	 */
	public boolean isSetLinkTimeOut(byte in){
		if(((byte)in & 0x10) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * is Sel | Del
	 * 
	 * @return boolean true or false
	 */
	public boolean isSetDel(byte in){
		if(((byte)in & 0x40) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * is Write
	 * 
	 * @return boolean true or false
	 */
	public boolean isWrite(byte in){
		if(((byte)in & 0x20) > 0){
			return true;
		}
		return false;
	}

	/**
	 * get Frame Status 
	 * @return
	 */
	public int getStatusCode() {
		if(!isRequest(this.control)){
			return  DataUtil.getIntTo2Byte(this.frameStatus.getStatusCode());
		}
		
		log.error("Not a response Status Code");
		return 0;
	}	
	
	/**
	 * set Frame Status
	 * @return
	 */
	public String getStatMessage() {
		if(!isRequest(this.control)){
			return this.frameStatus.getStatusMessage();
		}
		log.error("Not a response Status message");
		return null;
	}
}

