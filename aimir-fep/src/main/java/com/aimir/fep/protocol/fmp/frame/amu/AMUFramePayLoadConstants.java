package com.aimir.fep.protocol.fmp.frame.amu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AMUFramePayLoadConstants
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 18. 오후 1:21:30$
 */
public class AMUFramePayLoadConstants {

	private static Log log = LogFactory.getLog(AMUFramePayLoadConstants.class);
	
	public final static Object VARIABLE_OID = "1.11.0";
	
	/**
	 * PayLoad Frame Identifier Information
	 * 
	 */
	public static class FrameIdentifier {
		
		// Event
		public final static byte EVENT_STATUS_CHANGED				= (byte)0x01;
		public final static byte EVENT_POWER_OUTAGE					= (byte)0x02;
		public final static byte EVENT_BOOT_UP						= (byte)0x81;
		public final static byte EVENT_STACK_UP						= (byte)0x82;
		public final static byte EVENT_RX_RF_STATE					= (byte)0x83;
		public final static byte EVENT_JOIN_REQUEST					= (byte)0x84;
		
		// Command
		public final static byte CMD_RESET							= (byte)0x01;
		public final static byte CMD_CHANGE_AUTH_KEY				= (byte)0x02;
		public final static byte CMD_MODEM_INIT        		 		= (byte)0x03;
		public final static byte CMD_LAUNCH_BOOTLOADER  			= (byte)0x04;
		public final static byte CMD_OTA_ROM          				= (byte)0x10;
		public final static byte CMD_VALIDATE_OTAROM          		= (byte)0x11;
		public final static byte CMD_UPGRADE_FW         			= (byte)0x12;
		public final static byte CMD_MIU_VERSION        			= (byte)0x20;
		public final static byte CMD_MIU_ROUTE_CONFIG_AND_TIMESYNC	= (byte)0x21;
		public final static byte CMD_MIU_ROUTE_CONFIG              	= (byte)0x22;
		public final static byte CMD_MIU_TIME_SYNC      			= (byte)0x23;
		public final static byte CMD_MIU_SCHEDULE_CONFIG			= (byte)0x24;		
		
		// Metering
		public final static byte MD_INFO							= (byte)0xFF;   // Metering Information Frame
		public final static byte MD_ELECTRONIC						= (byte)0x01;		
		public final static byte MD_GAS								= (byte)0x02;
		public final static byte MD_WATER							= (byte)0x03;
		public final static byte MD_CALORY 							= (byte)0x04;	// 온수
		public final static byte MD_COOLING_METER					= (byte)0x05;	// 냉방 
		public final static byte MD_HEAT							= (byte)0x06;	// 열량
		public final static byte MD_COMPENSATOR						= (byte)0x07;   // 보정기(대용량가스)
		public final static byte MD_COLD_WATER						= (byte)0x08;	// 냉수
		public final static byte MD_SMOKE							= (byte)0x09;
		public final static byte MD_TEMPERATURE						= (byte)0x81;
		
	}
	
	/**
	 * Format Length Information
	 *
	 */
	public static class FormatLength {
		
		public final static int FRAME_IDENTIFIER					= 1;
		public final static int FRAME_CONTROL						= 1;
		
		public final static int BYPASS_PORT_NUMBER					= 1;
		public final static int BYPASS_TIME_OUT						= 2;
		
		public final static int LINK_RESPONSE_TIME					= 1;
		public final static int LINK_TIME_OUT						= 2;
		public final static int LINK_PARENT_MIU_ADDR				= 8;
		public final static int LINK_AUTHENTICATION_CODE			= 16;
		public final static int LINK_RES_STATUS						= 2;
		
		public final static int EVENT_STATUS						= 2;
		public final static int EVENT_MCU_ADDRESS_TYPE				= 1;
		public final static int EVENT_MCU_ADDRESS					= 8;
		public final static int EVENT_POWER_OUTAGE_TIME				= 11;
		public final static int EVENT_BOOTUP_INFO					= 11;
		public final static int EVENT_STACKUP_NETWORK_PARAM			= 16;
		public final static int EVENT_NEW_NODE_ID					= 2;
		public final static int EVENT_NEW_NODE_EUI64_ID				= 8;
		public final static int EVENT_DEVICE_UPDATE_STATUS			= 1;
		public final static int EVENT_PARENT_NODE_ID				= 2;
		
		public final static int CMD_STATUS							= 2;
		public final static int CMD_RESPONSE_TIME					= 1;
		public final static int CMD_MAX_LEN_ZIGBEE					= 48;
		public final static int CMD_MAX_LEN_CDMA					= 240;
		public final static int CMD_RESET_CODE						= 2;
		public final static int CMD_CHNG_AUTH_KEY					= 16;
		public final static int CMD_MODEM_INIT_CODE					= 2;
		public final static int CMD_LAUNCH_BOOTLOADER_CODE			= 2;
		public final static int CMD_OTAROM_ADDRESS					= 4;
		public final static int CMD_OTAROM_IMAGE_LENGTH				= 2;
		public final static int CMD_VALIDATE_OTAROM_IMAGE			= 4;
		public final static int CMD_VALIDATE_OTAROM_IMAGE_CRC		= 2;
		public final static int CMD_UPGRADE_FW_TYPE					= 1;
		public final static int CMD_MIU_FIRMWARE_VER				= 6;
		public final static int CMD_MIU_HARDWARE_VER				= 2;
		public final static int CMD_MOBILE_MODULE_FIRMWARE_VER		= 6;
		public final static int CMD_MOBILE_MODULE_HARDWARE_VER		= 6;
		public final static int CMD_ETHERNET_MODULE_FIRMWARE_VER	= 6;
		public final static int CMD_ETHERNET_MODULE_HARDWARE_VER	= 6;
		public final static int CMD_ZIGBEE_MODULE_FIRMWARE_VER		= 2;
		public final static int CMD_ZIGBEE_MODULE_HARDWARE_VER		= 2;
		public final static int CMD_ZIGBEE_MODULE_PROTOCOL_VER		= 2;
		public final static int CMD_ZIGBEE_MODULE_STACK_VER			= 2;
		public final static int CMD_MIU_TYPE						= 1;
		public final static int CMD_MIU_NODE_ID 					= 2;
		public final static int CMD_MIU_RF_POWER					= 1;
		public final static int CMD_MIU_RECEIVED_RSSI				= 1;
		public final static int CMD_MIU_RECEIVED_LQI				= 1;
		public final static int CMD_MIU_ROUTE_CONFIG_TIMESYNC_DATA  = 41;
		public final static int CMD_MIU_MAIN_SERVER_IP				= 4;			
		public final static int CMD_MIU_EVENT_SERVER_IP				= 4;	
		public final static int CMD_MIU_MAIN_SERVER_PORT			= 2;		
		public final static int CMD_MIU_EVENT_SERVER_PORT			= 2;		
		public final static int CMD_MIU_MCU_MOBILE_MODULE_NUMBER	= 6;		
		public final static int CMD_MIU_MCU_ETHERNET_MODULE_IP		= 4;		
		public final static int CMD_MIU_MCU_COORDINATOR_MOUDULE_EUI64ID	= 8;	
		public final static int CMD_MIU_TIMEZONE					= 2;	
		public final static int CMD_MIU_DST							= 2;	
		public final static int CMD_MIU_YEAR						= 2;	
		public final static int CMD_MIU_MONTH						= 1;	
		public final static int CMD_MIU_DAY							= 1;
		public final static int CMD_MIU_HOUR						= 1;	
		public final static int CMD_MIU_MINUTE						= 1;	
		public final static int CMD_MIU_SECOND						= 1;	
		public final static int CMD_MIU_ROUTE_CONFIG_DATA			= 30;
		public final static int CMD_MIU_AMR_SERVER_ID				= 4;
		public final static int CMD_MIU_AMR_SERVER_PORT				= 2;
		public final static int CMD_MIU_TIMESYC_REQ_DATA			= 11;
		public final static int CMD_MIU_SCHEDULE_CONFIG_REQ_DATA	= 6;
		public final static int CMD_MIU_MAIN_METERING_INTERVAL		= 2;
		public final static int CMD_MIU_SUB_METERING_INTERVAL		= 2;
		public final static int CMD_MIU_RESET_INTERVAL				= 2;
		public final static int CMD_MIU_SCHEDULE_CONFIG_RES_DATA	= 20;
		public final static int MD_FRAME_IDENTIFIER					= 2;
		public final static int MD_INFO_COUNT						= 1;
		public final static int MD_INFO_PORT						= 1;
		public final static int MD_INFO_SERVICE_TYPE				= 1;
		public final static int MD_INFO_SENSOR_TYPE					= 1;
		public final static int MD_INFO_VENDOR_TYPE					= 1;
		public final static int MD_INFO_MODEL_CODE					= 1;
	}
	
	/**
	 * PayLoad Format Code Information
	 *
	 */
	public static class FormatCode{
		public final static byte[] CMD_CODE_RESET 					= new byte[] {(byte)0x09, (byte)0x87};
		public final static byte[] CMD_CODE_MODEM_INIT 				= new byte[] {(byte)0x87, (byte)0x88};
		public final static byte[] CMD_CODE_LAUNCH_BOOTLOADER		= new byte[] {(byte)0x38, (byte)0x11};
				
	}
	
	/**
	 * get Event PayLoad Identifier Name
	 * 
	 * @param identifier
	 * @return
	 */
	public static String getEventIdentifierName(byte identifier){
		
		switch(identifier){
			case  FrameIdentifier.EVENT_STATUS_CHANGED :
				return "EVENT_ERROR_STATUS";
			case FrameIdentifier.EVENT_POWER_OUTAGE :
				return "EVENT_POWER_OUTAGE";
			case FrameIdentifier.EVENT_BOOT_UP :
				return "EVENT_BOOT_UP";
			case FrameIdentifier.EVENT_STACK_UP :
				return "EVENT_STACK_UP";
			case FrameIdentifier.EVENT_RX_RF_STATE :
				return "EVENT_RX_RF_STATE";
			case FrameIdentifier.EVENT_JOIN_REQUEST :
				return "EVENT_JOIN_REQUEST";
			default:
				log.warn("Can't Found Evnet FrameIdentifier Name ");
		}
		return "";
	}
	
	/**
	 * get Command PayLoad Identifier Name
	 * 
	 * @param identifier
	 * @return
	 */
	public static String getCommandIdentifierName(byte identifier){
		
		switch(identifier){
		
			case FrameIdentifier.CMD_RESET :
				return "CMD_RESET";
			case FrameIdentifier.CMD_CHANGE_AUTH_KEY :
				return "CMD_CHANGE_AUTH_KEY";
			case FrameIdentifier.CMD_MODEM_INIT :
				return "CMD_MODEM_INIT";
			case FrameIdentifier.CMD_LAUNCH_BOOTLOADER :
				return "CMD_LAUNCH_BOOTLOADER";
			case FrameIdentifier.CMD_OTA_ROM :
				return "CMD_OTA_ROM";
			case FrameIdentifier.CMD_VALIDATE_OTAROM :
				return "CMD_VALIDATE_OTAROM";
			case FrameIdentifier.CMD_UPGRADE_FW :
				return "CMD_UPGRADE_FW";
			case FrameIdentifier.CMD_MIU_VERSION :
				return "CMD_MIU_VERSION";
			case FrameIdentifier.CMD_MIU_ROUTE_CONFIG_AND_TIMESYNC :
				return "CMD_MIU_ROUTE_CONFIG_AND_TIMESYNC";
			case FrameIdentifier.CMD_MIU_ROUTE_CONFIG :
				return "CMD_MIU_ROUTE_CONFIG";
			case FrameIdentifier.CMD_MIU_TIME_SYNC :
				return "CMD_MIU_TIME_SYNC";
			case FrameIdentifier.CMD_MIU_SCHEDULE_CONFIG :
				return "CMD_MIU_SCHEDULE_CONFIG";
			default:
				log.warn("Can't Found Command FrameIdentifier Name ");
		}
		return "";
	}
	
	/**
	 * get Metering PayLoad Identifier Name
	 * 
	 * @param identifier
	 * @return
	 */
	public static String getMeteringIdentifierName(byte identifier){
		switch(identifier){
		case FrameIdentifier.MD_INFO :
		    return "MD_INF";
		case FrameIdentifier.MD_ELECTRONIC :
			return "MD_ELECTRONIC";
		case FrameIdentifier.MD_GAS :
			return "MD_GAS";
		case FrameIdentifier.MD_WATER :
			return "MD_WATER";
		case FrameIdentifier.MD_CALORY :
			return "MD_CALORY";
		case FrameIdentifier.MD_COOLING_METER :
		    return "MD_COOLING_METER";
		case FrameIdentifier.MD_HEAT :
		    return "MD_HEAT";
		case FrameIdentifier.MD_COMPENSATOR :
		    return "MD_COMPENSATOR";
		case FrameIdentifier.MD_COLD_WATER :
		    return "MD_COLD_WATER";
		case FrameIdentifier.MD_SMOKE :
		    return "MD_SMOKE";
		case FrameIdentifier.MD_TEMPERATURE :
			return "MD_TEMPERATURE";
		default:
			log.warn("Can't Found Metering FrameIdentifier Name ");
		}
		return "";
	}
}


