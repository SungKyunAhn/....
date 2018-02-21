package com.aimir.fep.protocol.fmp.frame;
/**
 * AMUGeneralDataConstants
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 19. 오전 9:58:09$
 */
public class AMUGeneralDataConstants{

	public static long WAITTIME_AFTER_LINK_FRAME 	= 90000;		// default 90초
	public static String RX_SEQ 					= "RxSeq";		// RECEIVER   (Client Seq)
	public static String TX_SEQ 					= "TxSeq";		// TRANMITTER (Sever Seq)
	public static String PENDING_FRAME	 			= "PendingFrame";
	public static String MCUID 						= "McuID";		// Link Open recevied MIUID
	public static String METERING_POOL	 			= "MeteringPool";
	
	
	// Field Length
	public static final int SOH_LEN					= 1;
	public static final int FRAME_CTRL_LEN			= 2;
	public static final int SEQ_LEN					= 1;
	public static final int PAYLOAD_LEN				= 2;
	public static final int FRAME_CRC_LEN			= 2;

	public static final int ADDRTYPE_NONE_LEN		= 0;
	public static final int ADDRTYPE_IP_LEN			= 4;
	public static final int ADDRTYPE_EUI64_LEN		= 8;
	public static final int ADDRTYPE_MOBILE_LEN		= 6;

	public static final String SERSOR				= "Sensor";
	public static final String MCU					= "MCU";
	
	public static final char CRC_CODE				= (char)0x0000;
	// FH : FRAME HEADER
		
	// Start of header field.
	public static final byte SOH 					= (byte)0xAA;
	
	// Gerneral Frame Control Field
	public static final byte FRAMETYPE_EVENT 		= (byte)0x00;
	public static final byte FRAMETYPE_LINK 		= (byte)0x01;
	public static final byte FRAMETYPE_BYPASS 		= (byte)0x02;
	public static final byte FRAMETYPE_COMMAND		= (byte)0x03;
	public static final byte FRAMETYPE_METERING		= (byte)0x04;
	
	public static final byte ATTR_SECURITY			= (byte)0x08;
	public static final byte ATTR_COMPRESS			= (byte)0x04; 
	public static final byte ATTR_ACK_REQUEST 		= (byte)0x02;
	public static final byte ATTR_FRAME_PENDING 	= (byte)0x01;
	
	public static final int  ATTR_DEST_ADDR			= 1;
	public static final int  ATTR_SRC_ADDR			= 2;
	public static final byte ATTR_ADDRTYPE_NONE 	= (byte)0x00;
	public static final byte ATTR_ADDRTYPE_IP		= (byte)0x01;
	public static final byte ATTR_ADDRTYPE_EUI64	= (byte)0x02;
	public static final byte ATTR_ADDRTYPE_MOBILE 	= (byte)0x03;
	
    // MoApater 시작 시 밑에 이름으로 Handlr를 등록하여 사용함
	// 추후 ProtocolHandler 의 상수로 같이 쓸수 있도록함
    public static final String SERVICE_AMU_EVENT	= "AMUEventData";
    public static final String SERVICE_AMU_METERING	= "AMUMDData";
	
}