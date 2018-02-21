package com.aimir.fep.protocol.security.frame;

public class AuthFrameConstants {
	
    public static int FRAME_MAX_SEQ = 255;
    public static final short VERSION = 0x01;
    
	// Field Length
	public static final int START_FLAG_LEN			= 2;
	public static final int FRAME_OPTION_LEN		= 1;
	public static final int VERSION_LEN				= 2;
	public static final int SEQUENCE_NUMBER_LEN		= 1;
	public static final int DEVICE_SERIAL_LEN		= 8;
	public static final int PAYLOAD_LENGTH_LEN		= 2;

	public static final int START_FLAG_OFFSET		= 0;
	public static final int FRAME_OPTION_OFFSET     = 2;
	public static final int VERSION_OFFSET			= 3;
	public static final int SEQUENCE_NUMBER_OFFSET	= 5;
	public static final int DEVICESERIAL_OFFSET		= 6;
	//public static final int PAYLOAD_LENGTH_OFFSET	= 6;
	public static final int PAYLOAD_LENGTH_OFFSET	= 14;
	
	//public static final int FRAME_PAYLOAD_OFFSET	= 8;
	public static final int FRAME_PAYLOAD_OFFSET	= 16;
	
	
    //public static int HEADER_LEN =  8; // START_FLAG_LEN + FRAME_OPTION_LEN + VERSION_LEN + SEQUENCE_NUMBER_LEN +  PAYLOAD_LENGTH_LEN
	public static int HEADER_LEN =  16; // START_FLAG_LEN + FRAME_OPTION_LEN + VERSION_LEN + SEQUENCE_NUMBER_LEN + DEVICE_SERIAL_LEN + PAYLOAD_LENGTH_LEN
	public static final int FRAME_CRC_LEN			= 2; 

    public static int TAIL_LEN = 2; // FRAME_CRC_LEN
     
	public static final char CRC_CODE				= (char)0x0000;

	// FH : FRAME HEADER
		
	// Start of header field.
	public static final byte[] SOH 					= {(byte)0x01, (byte)0x14};
	
	//=> INSERT START 2016.11.21 SP-318
	public static final int RENEW_CERT_LEN	= 2; 
	public static final int RENEW_KEY_LEN	= 2; 
	public static final int RENEW_SALT_LEN	= 2; 
	//=> INSERT END   2016.11.21 SP-318
}
