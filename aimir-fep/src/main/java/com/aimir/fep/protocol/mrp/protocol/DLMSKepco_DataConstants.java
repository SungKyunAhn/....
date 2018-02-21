package com.aimir.fep.protocol.mrp.protocol;

/**
 * RequestData Constants
 * 
 * @author goodjob@nuritelecom.com
 */
public class DLMSKepco_DataConstants
{

	public static final byte GET_REQUEST = (byte)0xc0;
	public static final byte SET_REQUEST = (byte)0xc1;
//  meter_cmd
    public static final byte LSLOW_SNRM		=(byte)0x00;
    public static final byte LSLOW_LL_AUTH 	=(byte)0x01;
    public static final byte LSLOW_DISC 	=(byte)0x02;
    public static final byte LSLOW_RR 		=(byte)0x03;
    public static final byte LSLOW_READ 	=(byte)0x04;
    public static final byte LSLOW_WRITE    =(byte)0x08;
    
//  meter_read_class
    public static final byte READ_METER_INFO		=(byte)0x00;			//Meter type and information
    public static final byte READ_LAST_BILL_INFO	=(byte)0x01;			//Energy/Demand of the last month check
    public static final byte READ_CURR_BILL			=(byte)0x02;			//Energy/Demand of the current month
    public static final byte READ_LAST_BILL			=(byte)0x03;			//Energy/Demand of the last month
    public static final byte READ_PF_INFO			=(byte)0x04;			//Meter Power Failure check
    public static final byte READ_POWER_FAILURE		=(byte)0x05;			//Meter Power Failure
    public static final byte READ_POWER_RESTORE		=(byte)0x06;			//Meter Power Restore
    public static final byte READ_LP_INFO			=(byte)0x07;			//Meter LP Information
    public static final byte READ_LP_READ			=(byte)0x08;			//Meter LP Struct
    public static final byte READ_LP_READ_CONTINUE	=(byte)0x09;			//Meter LP Struct
    
    
// meter_write_class
    public static final byte WRITE_DATETIME         =(byte)0x00;

//     Get Response Flag
    public static final byte GET_RESPONSE_NORMAL		=(byte)0x01;
    public static final byte GET_RESPONSE_WITH_DATABLOCK=(byte)0x02;
    public static final byte GET_RESPONSE_WITH_LIST		=(byte)0x03;

//  DLMS Message Def
    public static final byte  BILLING				=(byte)0x00;
    public static final byte  LOADLPROFILE			=(byte)0x01;

    public static final byte 	HDLC_FLAG			=(byte)0x7e;
    public static final byte 	HDLC_FRAME_FORMAT	=(byte)0xa0;

    public static final char 	MSAP_DEST_ADDRESS	=(char)0x0203;
    public static final byte 	MSAP_SRC_ADDRESS	=(byte)0x23; //한전표준
    //public static final byte 	MSAP_SRC_ADDRESS	=(byte)0x21; //민수용

    public static final byte 	LSAP_DEST_ADDRESS	=(byte)0xe6;
    public static final byte 	LSAP_SRC_ADDRESS	=(byte)0xe6;

    public static final byte 	HDLC_CTRL_I			=(byte)0x10;		//Information transfer command and response
    public static final byte 	HDLC_CTRL_RR		=(byte)0x11;		//Receive ready command and response
    public static final byte 	HDLC_CTRL_RNR		=(byte)0x15;		//Receive not ready command and response
    public static final byte 	HDLC_CTRL_SNRM		=(byte)0x93;		//Set normal response mode (SNRM) command
    public static final byte 	HDLC_CTRL_DISC		=(byte)0x53;		//Disconnect (DISC) command
    public static final byte 	HDLC_CTRL_UA		=(byte)0x73;		//Unnumbered acknowledge (UA) response
    public static final byte 	HDLC_CTRL_DM		=(byte)0x1f;		//Disconnected mode (DM) response
    public static final byte 	HDLC_CTRL_FRMR		=(byte)0x97;		//Frame reject (FRMR) response
    public static final byte 	HDLC_CTRL_UI		=(byte)0x13;		//Unnumbered information (UI) command and response

    public static final byte ACK = 0x06;
    public static final byte NACK = 0x15;
    /*
	Command and response frame

	Command				Response
	----------------------------
	I					I
	RR					RR
	RNR					RNR
	SNRW				UA
	DISC				DM
	UI					UI
						FRMR
*/  
    /*public static final byte[]	DLMS_ll_auth = 
		{(byte)0x00, (byte)0x60, (byte)0x1D, (byte)0xA1, (byte)0x09, (byte)0x06, (byte)0x07, (byte)0x60, (byte)0x85, (byte)0x74,
		 (byte)0x05, (byte)0x08, (byte)0x01, (byte)0x01, (byte)0xBE, (byte)0x10, (byte)0x04, (byte)0x0E, (byte)0x01, (byte)0x00, 
		 (byte)0x00, (byte)0x00, (byte)0x06, (byte)0x5F, (byte)0x1F, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x18, (byte)0x19, 
		 (byte)0xFF, (byte)0xFF //민수용
		};
*/
	public static final byte[]	DLMS_ll_auth = 
	{(byte)0x00, (byte)0x60, (byte)0x36, (byte)0xA1, (byte)0x09, (byte)0x06, (byte)0x07, (byte)0x60, (byte)0x85, (byte)0x74,
		(byte)0x05, (byte)0x08, (byte)0x01, (byte)0x01, (byte)0xBE, (byte)0x10, (byte)0x04, (byte)0x0E, (byte)0x01, (byte)0x00, 
		(byte)0x00, (byte)0x00, (byte)0x06, (byte)0x5F, (byte)0x1F, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x18, (byte)0x19, 
		(byte)0xFF, (byte)0xFF //한전 표준
	};

//Class_id + OBIS Code + Attribute : 2 Byte + 6 Byte + 1 Byte
		public static final byte[][]	DLMS_Read = 
		{ 
			{    (byte)0,    (byte)7,    (byte)1,  (byte)128,  (byte)128,  (byte)128,  (byte)129,  (byte)255,    (byte)2},			// Meter type and information
			{    (byte)0,    (byte)7,    (byte)1,  (byte)128,  (byte)128,  (byte)128,  (byte)128,  (byte)101,    (byte)7},			// Meter Energy / Demand of the Last Month state
			{    (byte)0,    (byte)7,    (byte)1,  (byte)128,  (byte)128,  (byte)128,  (byte)128,  (byte)100,    (byte)2},			// Meter Energy / Demand of the current Month
			{    (byte)0,    (byte)7,    (byte)1,  (byte)128,  (byte)128,  (byte)128,  (byte)128,  (byte)101,    (byte)2},			// Meter Energy / Demand of the Last Month
			{    (byte)0,    (byte)7,    (byte)1,  (byte)0,   (byte)99,   (byte)98,    (byte)1,  (byte)255,    (byte)7},			// Meter Power Failure State
			{    (byte)0,    (byte)7,    (byte)1,  (byte)0,   (byte)99,   (byte)98,    (byte)1,  (byte)255,    (byte)2},			// Meter Power Failure
			{    (byte)0,    (byte)7,    (byte)1,  (byte)0,   (byte)99,   (byte)98,    (byte)2,  (byte)255,    (byte)2},			// Meter Power Restore
			{    (byte)0,    (byte)7,    (byte)1,  (byte)0,   (byte)99,    (byte)1,    (byte)0,  (byte)255,    (byte)7},			// Meter LP Information
			{    (byte)0,    (byte)7,    (byte)1,  (byte)0,   (byte)99,    (byte)1,    (byte)0,  (byte)255,    (byte)2}				// Meter Select LP
		};
		
		
		public static final byte[][] DLMS_Write = 
		{
			{(byte)0,    (byte)8,    (byte)0,  (byte)0,   (byte)1,    (byte)0,    (byte)0,  (byte)255,    (byte)2} //calendar set 2. time
		};
		

		public static final byte[] WRITE_TIME = {
			(byte)0xC1 ,0x01 ,(byte)0x81 ,0x00 ,0x08 ,0x00 ,0x00 ,0x01 ,0x00 ,0x00 ,(byte)0xFF ,0x02 ,0x00,
			0x09 ,0x0C ,0x07 ,(byte)0xD9 ,0x05 ,0x07 ,(byte)0xFF ,0x11 ,0x32 ,0x2B ,(byte)0xFF ,(byte)0x80 ,(byte)0x00 ,0x00		//time
		};
		// year month day of month day of week hour minute second mille second deviation clock status


    public static long SLEEPTIME = 1 * 1000;

    /**
     * constructor
     */
    public DLMSKepco_DataConstants()
    {
    }
    
    public static char DLMS_Fcs16(char fcs, byte[] baData, int npos, int len )
    {
    	char wfcs = fcs;
        char[] fcstab = {
        	    0x0000,  0x1189,  0x2312,  0x329b,  0x4624,  0x57ad,  0x6536,  0x74bf,
        	    0x8c48,  0x9dc1,  0xaf5a,  0xbed3,  0xca6c,  0xdbe5,  0xe97e,  0xf8f7,
        	    0x1081,  0x0108,  0x3393,  0x221a,  0x56a5,  0x472c,  0x75b7,  0x643e,
        	    0x9cc9,  0x8d40,  0xbfdb,  0xae52,  0xdaed,  0xcb64,  0xf9ff,  0xe876,
        	    0x2102,  0x308b,  0x0210,  0x1399,  0x6726,  0x76af,  0x4434,  0x55bd,
        	    0xad4a,  0xbcc3,  0x8e58,  0x9fd1,  0xeb6e,  0xfae7,  0xc87c,  0xd9f5,
        	    0x3183,  0x200a,  0x1291,  0x0318,  0x77a7,  0x662e,  0x54b5,  0x453c,
        	    0xbdcb,  0xac42,  0x9ed9,  0x8f50,  0xfbef,  0xea66,  0xd8fd,  0xc974,
        	    0x4204,  0x538d,  0x6116,  0x709f,  0x0420,  0x15a9,  0x2732,  0x36bb,
        	    0xce4c,  0xdfc5,  0xed5e,  0xfcd7,  0x8868,  0x99e1,  0xab7a,  0xbaf3, 
        	    0x5285,  0x430c,  0x7197,  0x601e,  0x14a1,  0x0528,  0x37b3,  0x263a,
        	    0xdecd,  0xcf44,  0xfddf,  0xec56,  0x98e9,  0x8960,  0xbbfb,  0xaa72,
        	    0x6306,  0x728f,  0x4014,  0x519d,  0x2522,  0x34ab,  0x0630,  0x17b9,
        	    0xef4e,  0xfec7,  0xcc5c,  0xddd5,  0xa96a,  0xb8e3,  0x8a78,  0x9bf1,
        	    0x7387,  0x620e,  0x5095,  0x411c,  0x35a3,  0x242a,  0x16b1,  0x0738,
        	    0xffcf,  0xee46,  0xdcdd,  0xcd54,  0xb9eb,  0xa862,  0x9af9,  0x8b70,
        	    0x8408,  0x9581,  0xa71a,  0xb693,  0xc22c,  0xd3a5,  0xe13e,  0xf0b7,
        	    0x0840,  0x19c9,  0x2b52,  0x3adb,  0x4e64,  0x5fed,  0x6d76,  0x7cff,
        	    0x9489,  0x8500,  0xb79b,  0xa612,  0xd2ad,  0xc324,  0xf1bf,  0xe036,
        	    0x18c1,  0x0948,  0x3bd3,  0x2a5a,  0x5ee5,  0x4f6c,  0x7df7,  0x6c7e,
        	    0xa50a,  0xb483,  0x8618,  0x9791,  0xe32e,  0xf2a7,  0xc03c,  0xd1b5,
        	    0x2942,  0x38cb,  0x0a50,  0x1bd9,  0x6f66,  0x7eef,  0x4c74,  0x5dfd,
        	    0xb58b,  0xa402,  0x9699,  0x8710,  0xf3af,  0xe226,  0xd0bd,  0xc134,
        	    0x39c3,  0x284a,  0x1ad1,  0x0b58,  0x7fe7,  0x6e6e,  0x5cf5,  0x4d7c,
        	    0xc60c,  0xd785,  0xe51e,  0xf497,  0x8028,  0x91a1,  0xa33a,  0xb2b3,
        	    0x4a44,  0x5bcd,  0x6956,  0x78df,  0x0c60,  0x1de9,  0x2f72,  0x3efb,
        	    0xd68d,  0xc704,  0xf59f,  0xe416,  0x90a9,  0x8120,  0xb3bb,  0xa232,
        	    0x5ac5,  0x4b4c,  0x79d7,  0x685e,  0x1ce1,  0x0d68,  0x3ff3,  0x2e7a,
        	    0xe70e,  0xf687,  0xc41c,  0xd595,  0xa12a,  0xb0a3,  0x8238,  0x93b1,
        	    0x6b46,  0x7acf,  0x4854,  0x59dd,  0x2d62,  0x3ceb,  0x0e70,  0x1ff9,
        	    0xf78f,  0xe606,  0xd49d,  0xc514,  0xb1ab,  0xa022,  0x92b9,  0x8330,
        	    0x7bc7,  0x6a4e,  0x58d5,  0x495c,  0x3de3,  0x2c6a,  0x1ef1,  0x0f78 
    	};

    	while(0<len-- ){
    		wfcs = (char)((wfcs >> 8) ^ fcstab[((wfcs ^ baData[npos++]) & 0xff)]);
    		
    	}

    	return	wfcs;
    }
}
