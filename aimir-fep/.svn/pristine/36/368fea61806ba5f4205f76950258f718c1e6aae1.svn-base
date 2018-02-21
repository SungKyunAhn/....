package com.aimir.fep.protocol.fmp.frame;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.Hex;

/**
 * AMU Data Frame Status 
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오전 10:11:03$
 */
public class AMUDataFrameStatus implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(AMUDataFrameStatus.class);
	// code : 0x0000 의 frame result code. 
	byte[] statusCode;
	String statusMessage;
	
	/**
	 * constructor
	 */
	public AMUDataFrameStatus(){
	}
	
	/**
	 * constructor
	 * 
	 * @param statusCode
	 * @throws Exception 
	 */
	public AMUDataFrameStatus(byte[] statusCode) throws Exception{
		
		try {
			setStatusMessage(statusCode);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * get Status Code
	 * @return
	 */
	public byte[] getStatusCode() {
		return statusCode;
	}
	
	/**
	 * set Status Code
	 * @param statusCode
	 */
	public void setStatusCode(byte[] statusCode) {
		this.statusCode = statusCode;
	}
	
	/**
	 * get Status Message
	 * @return
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
	
	/**
	 * set Stauts Message
	 * @param statusMessage
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	/**
	 * set Status Code And set Message
	 * @param statusCode
	 * @throws Exception 
	 */
	public void setStatusMessage(byte[] statusCode) throws Exception{
		
		log.debug(" ## STATUS CODE : " + Hex.decode(statusCode));
		this.statusCode = statusCode;
			
		if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.GeneralStatus.STATUS_SUCCESS)){
			this.statusMessage = "SUCCESS";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.GeneralStatus.STATUS_FATAL_FAIL)){
			this.statusMessage = "FATAL FAIL";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.GeneralStatus.STATUS_BUSY)){
			this.statusMessage = "BUSY";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.GeneralStatusError.ERROR_AMU_RXFRAME_CRC16)){
			this.statusMessage = "ERROR_AMU_RXFRAME_CRC16";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.GeneralStatusError.ERROR_AMU_RXFRAME_PAYLOAD_SIZE)){
			this.statusMessage = "ERROR_AMU_RXFRAME_PAYLOAD_SIZE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.GeneralStatusError.ERROR_AMU_RXFRAME_UNKNOWN_DESTADDR)){
			this.statusMessage = "ERROR_AMU_RXFRAME_UNKNOWN_DESTADDR";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.GeneralStatusError.ERROR_AMU_RXFRAME_UNKNOWN_SRCADDR)){
			this.statusMessage = "ERROR_AMU_RXFRAME_UNKNOWN_SRCADDR";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.GeneralStatusError.ERROR_AMU_RXFRAME_UNKNOWN_FRAMETYPE)){
			this.statusMessage = "ERROR_AMU_RXFRAME_UNKNOWN_FRAMETYPE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.GeneralStatusError.ERROR_AMU_RXFRAME_INCOMING_RESPONSE)){
			this.statusMessage = "ERROR_AMU_RXFRAME_INCOMING_RESPONSE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.GeneralStatusError.ERROR_AMU_RXFRAME_UNMATCH_RXSEQ)){
			this.statusMessage = "ERROR_AMU_RXFRAME_UNMATCH_RXSEQ";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.GeneralStatusError.ERROR_AMU_RXFRAME_SERIAL_TIMEOUT)){
			this.statusMessage = "ERROR_AMU_RXFRAME_SERIAL_TIMEOUT";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.GeneralStatusError.ERROR_AMU_RXFRAME_NOT_SUPPORT_ROUTE)){
			this.statusMessage = "ERROR_AMU_RXFRAME_NOT_SUPPORT_ROUTE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.GeneralStatusError.ERROR_AMU_RXFRAME_UNKNOWN_EVENT_TYPE)){
			this.statusMessage = "ERROR_AMU_RXFRAME_UNKNOWN_EVENT_TYPE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.GeneralStatusError.ERROR_AMU_RXFRAME_UNKNOWN_COMMAND_TYPE)){
			this.statusMessage = "ERROR_AMU_RXFRAME_UNKNOWN_COMMAND_TYPE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.Event_MeteringFrameStatusError.ERROR_AMU_EVENT_INVALID_DATA_TYPE)){
			this.statusMessage = "ERROR_AMU_EVENT_INVALID_DATA_TYPE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.Event_MeteringFrameStatusError.ERROR_AMU_METERING_INCOMING_RESPONSE)){
			this.statusMessage = "ERROR_AMU_METERING_INCOMING_RESPONSE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.LinkFrameStatusError.ERROR_AMU_LINK_NOT_CONNECTED)){
			this.statusMessage = "ERROR_AMU_LINK_NOT_CONNECTED";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.LinkFrameStatusError.ERROR_AMU_LINK_AUTHENTICATION_CODE)){
			this.statusMessage = "ERROR_AMU_LINK_AUTHENTICATION_CODE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.LinkFrameStatusError.ERROR_AMU_LINK_ALREADY_OPENED)){
			this.statusMessage = "ERROR_AMU_LINK_ALREADY_OPENED";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.LinkFrameStatusError.ERROR_AMU_LINK_NOT_OPENED)){
			this.statusMessage = "ERROR_AMU_LINK_NOT_OPENED";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.LinkFrameStatusError.ERROR_AMU_LINK_NOT_OPENED_RESPONSE)){
			this.statusMessage = "ERROR_AMU_LINK_NOT_OPENED_RESPONSE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.LinkFrameStatusError.ERROR_AMU_LINK_NOT_UNMATCH_SRCADDR)){
			this.statusMessage = "ERROR_AMU_LINK_NOT_UNMATCH_SRCADDR";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.LinkFrameStatusError.ERROR_AMU_LINK_NOT_CLOSE_BY_TIMEOUT)){
			this.statusMessage = "ERROR_AMU_LINK_NOT_CLOSE_BY_TIMEOUT";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_NOT_NETWOR)){
			this.statusMessage = "ERROR_AMU_CMD_NOT_NETWOR";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_JOINING_NETWORK)){
			this.statusMessage = "ERROR_AMU_CMD_JOINING_NETWORK";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_JOINED_NETWORK)){
			this.statusMessage = "ERROR_AMU_CMD_JOINED_NETWORK";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_NETWORK_NO_PARENT)){
			this.statusMessage = "ERROR_AMU_CMD_NETWORK_NO_PARENT";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_LEAVING_NETWORK)){
			this.statusMessage = "ERROR_AMU_CMD_LEAVING_NETWORK";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_UNMATCH_RESET_CMD_CODE)){
			this.statusMessage = "ERROR_AMU_CMD_UNMATCH_RESET_CMD_CODE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_UNMATCH_INIT_CMD_CODE)){
			this.statusMessage = "ERROR_AMU_CMD_UNMATCH_INIT_CMD_CODE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_UNMATCH_BOOT_CMD_CODE)){
			this.statusMessage = "ERROR_AMU_CMD_UNMATCH_BOOT_CMD_CODE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_NOT_UBVAILD_NETPARA_VALUE)){
			this.statusMessage = "ERROR_AMU_CMD_NOT_UBVAILD_NETPARA_VALUE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_TOO_LARGE_MTOR_RADIUS)){
			this.statusMessage = "ERROR_AMU_CMD_TOO_LARGE_MTOR_RADIUS";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_NOT_SUPPORT_REQUEST_WRITE)){
			this.statusMessage = "ERROR_AMU_CMD_NOT_SUPPORT_REQUEST_WRITE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_INVAILD_OTA_IMAGE_LEN)){
			this.statusMessage = "ERROR_AMU_CMD_INVAILD_OTA_IMAGE_LEN";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_INVAILD_OTA_ADDRESS)){
			this.statusMessage = "ERROR_AMU_CMD_INVAILD_OTA_ADDRESS";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_TOO_LONG_ROM_LENGRTH)){
			this.statusMessage = "ERROR_AMU_CMD_TOO_LONG_ROM_LENGRTH";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_VALIDATE_OTAROM_FAIL)){
			this.statusMessage = "ERROR_AMU_CMD_VALIDATE_OTAROM_FAIL";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.CommandFrameStatusError.ERROR_AMU_CMD_UNKNWON_FWUPGRADE_TYPE)){
			this.statusMessage = "ERROR_AMU_CMD_UNKNWON_FWUPGRADE_TYPE";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.BypassFrameStatusError.ERROR_AMU_MTR_INCOMING_RESPONSE)){
			this.statusMessage = "ERROR_AMU_BYPASS_UNKNOWN_METER";
		}else if(Arrays.equals(statusCode, AMUDataFrameStatusConstants.BypassFrameStatusError.ERROR_AMU_MTR_INCOMING_RESPONSE)){
			this.statusMessage = "ERROR_AMU_MTR_INVALID_CHILDMIU_MTRDATA";
		}else{
			throw new Exception("Matching Status Code Not Founded ");
		}

	}
}