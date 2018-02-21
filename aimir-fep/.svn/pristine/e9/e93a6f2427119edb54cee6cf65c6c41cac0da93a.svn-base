package com.aimir.fep.protocol.fmp.frame;
/**
 * AMU Data Frame Status Constants
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오전 10:11:20$
 */
public class AMUDataFrameStatusConstants {
	// 2 byte Status Code
	public static class GeneralStatus{
		
		public final static byte[] STATUS_SUCCESS 							= new byte[] {(byte)0x00, (byte)0x00};
		public final static byte[] STATUS_FATAL_FAIL 						= new byte[] {(byte)0x00, (byte)0x01};	
		public final static byte[] STATUS_BUSY 								= new byte[] {(byte)0x00, (byte)0x02};	
	}
	
	public static class GeneralStatusError{
		public final static byte[] ERROR_AMU_RXFRAME_CRC16					= new byte[] {(byte)0x20, (byte)0x00};
		public final static byte[] ERROR_AMU_RXFRAME_PAYLOAD_SIZE			= new byte[] {(byte)0x20, (byte)0x01};
		public final static byte[] ERROR_AMU_RXFRAME_UNKNOWN_DESTADDR		= new byte[] {(byte)0x20, (byte)0x02};
		public final static byte[] ERROR_AMU_RXFRAME_UNKNOWN_SRCADDR		= new byte[] {(byte)0x20, (byte)0x03};
		public final static byte[] ERROR_AMU_RXFRAME_UNKNOWN_FRAMETYPE		= new byte[] {(byte)0x20, (byte)0x04};
		public final static byte[] ERROR_AMU_RXFRAME_INCOMING_RESPONSE		= new byte[] {(byte)0x20, (byte)0x05};
		public final static byte[] ERROR_AMU_RXFRAME_UNMATCH_RXSEQ			= new byte[] {(byte)0x20, (byte)0x06};
		public final static byte[] ERROR_AMU_RXFRAME_SERIAL_TIMEOUT			= new byte[] {(byte)0x20, (byte)0x07};
		public final static byte[] ERROR_AMU_RXFRAME_NOT_SUPPORT_ROUTE		= new byte[] {(byte)0x20, (byte)0x08};
		public final static byte[] ERROR_AMU_RXFRAME_UNKNOWN_EVENT_TYPE		= new byte[] {(byte)0x20, (byte)0x10};
		public final static byte[] ERROR_AMU_RXFRAME_UNKNOWN_COMMAND_TYPE	= new byte[] {(byte)0x20, (byte)0x11};
	}
	
	public static class Event_MeteringFrameStatusError{
		public final static byte[] ERROR_AMU_EVENT_INVALID_DATA_TYPE		= new byte[] {(byte)0x20, (byte)0x40};
		public final static byte[] ERROR_AMU_METERING_INCOMING_RESPONSE		= new byte[] {(byte)0x20, (byte)0x41};
		//public final static byte[] ERROR_AMU_EVENT_UNMATCH_RXSEQ			= new byte[] {(byte)0x20, (byte)0x42};
	}
	
	public static class LinkFrameStatusError{
		public final static byte[] ERROR_AMU_LINK_NOT_CONNECTED				= new byte[] {(byte)0x20, (byte)0x60};
		public final static byte[] ERROR_AMU_LINK_AUTHENTICATION_CODE		= new byte[] {(byte)0x20, (byte)0x61};
		public final static byte[] ERROR_AMU_LINK_ALREADY_OPENED			= new byte[] {(byte)0x20, (byte)0x62};
		public final static byte[] ERROR_AMU_LINK_NOT_OPENED				= new byte[] {(byte)0x20, (byte)0x63};
		public final static byte[] ERROR_AMU_LINK_NOT_OPENED_RESPONSE		= new byte[] {(byte)0x20, (byte)0x64};
		public final static byte[] ERROR_AMU_LINK_NOT_UNMATCH_SRCADDR		= new byte[] {(byte)0x20, (byte)0x65};
		public final static byte[] ERROR_AMU_LINK_NOT_CLOSE_BY_TIMEOUT		= new byte[] {(byte)0x20, (byte)0x66};
	}

	public static class CommandFrameStatusError{
		public final static byte[] ERROR_AMU_CMD_NOT_NETWOR					= new byte[] {(byte)0x20, (byte)0x80};
		public final static byte[] ERROR_AMU_CMD_JOINING_NETWORK			= new byte[] {(byte)0x20, (byte)0x81};
		public final static byte[] ERROR_AMU_CMD_JOINED_NETWORK				= new byte[] {(byte)0x20, (byte)0x82};
		public final static byte[] ERROR_AMU_CMD_NETWORK_NO_PARENT			= new byte[] {(byte)0x20, (byte)0x83};
		public final static byte[] ERROR_AMU_CMD_LEAVING_NETWORK			= new byte[] {(byte)0x20, (byte)0x84};
		public final static byte[] ERROR_AMU_CMD_UNMATCH_RESET_CMD_CODE		= new byte[] {(byte)0x20, (byte)0x85};
		public final static byte[] ERROR_AMU_CMD_UNMATCH_INIT_CMD_CODE		= new byte[] {(byte)0x20, (byte)0x86};
		public final static byte[] ERROR_AMU_CMD_UNMATCH_BOOT_CMD_CODE		= new byte[] {(byte)0x20, (byte)0x87};
		public final static byte[] ERROR_AMU_CMD_NOT_UBVAILD_NETPARA_VALUE	= new byte[] {(byte)0x20, (byte)0x88};
		public final static byte[] ERROR_AMU_CMD_TOO_LARGE_MTOR_RADIUS		= new byte[] {(byte)0x20, (byte)0x89};
		public final static byte[] ERROR_AMU_CMD_NOT_SUPPORT_REQUEST_WRITE	= new byte[] {(byte)0x20, (byte)0x8A};
		public final static byte[] ERROR_AMU_CMD_INVAILD_OTA_IMAGE_LEN		= new byte[] {(byte)0x20, (byte)0x8B};
		public final static byte[] ERROR_AMU_CMD_INVAILD_OTA_ADDRESS		= new byte[] {(byte)0x20, (byte)0x8C};
		public final static byte[] ERROR_AMU_CMD_TOO_LONG_ROM_LENGRTH		= new byte[] {(byte)0x20, (byte)0x8D};
		public final static byte[] ERROR_AMU_CMD_VALIDATE_OTAROM_FAIL		= new byte[] {(byte)0x20, (byte)0x8E};
		public final static byte[] ERROR_AMU_CMD_UNKNWON_FWUPGRADE_TYPE		= new byte[] {(byte)0x20, (byte)0x8F};
	}
	
	public static class BypassFrameStatusError{
		public final static byte[] ERROR_AMU_MTR_INCOMING_RESPONSE			= new byte[] {(byte)0x20, (byte)0xC0};
		public final static byte[] ERROR_AMU_MTR_INVALID_CHILDMIU_MTRDATA 	= new byte[] {(byte)0x20, (byte)0xC1};
	}	
}