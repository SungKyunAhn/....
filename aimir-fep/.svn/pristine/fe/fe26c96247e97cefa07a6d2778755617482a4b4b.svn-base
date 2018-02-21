package com.aimir.fep.protocol.mrp.protocol;

/**
 * RequestData Constants
 * 
 * @author Kang, Soyi
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class KAMSTRUP601_DataConstants
{
    public static final byte STX_TO_METER = (byte)0x80;
    public static final byte STX_FROM_METER = (byte)0x40;
    public static final byte ETX = (byte)0x0D;
    public static final byte ACK = (byte)0x06;
    public static final byte DLE = (byte)0x1B;
    
    public static final byte DES_ADDR_TOP_MODULE = (byte)0x7F;
    public static final byte DES_ADDR_HEAT_METER = (byte)0x3F;
    public static final byte DES_ADDR_BASE_MODULE = (byte)0xBF;
    
    public static final byte CID_GETTYPE = (byte)0x01;
    public static final byte CID_GETSERIALNO = (byte)0x02;
    public static final byte CID_GETREGISTER = (byte)0x10;
    
    public static final byte CID_GETLOGTIME_PRESENT = (byte)0xA0;
    public static final byte CID_GETLOGLAST_PRESENT = (byte)0xA1;
    public static final byte CID_GETLOGID_PRESENT = (byte)0xA2;
    public static final byte CID_GETLOGTIME_PAST = (byte)0xA3;

    public static final byte CID_GETMONTHLY = (byte)0x65;
    public static final byte CID_GETDAILY = (byte)0x66;
    public static final byte CID_GETHOURLY = (byte)0x63;
    public static final byte CID_GETEVENT = (byte)0x67;
    
    public static final int REG_METER_ID = 0x03F2;
    public static final int LEN_METER_ID = 4;
    
    public static final int REG_DATE = 0x03EB;
    public static final int REG_E1 = 0x003C;
    public static final int REG_V1 = 0x0044;
    public static final int REG_T1 = 0x0056;
    public static final int REG_T2 = 0x0057;
    public static final int REG_P1 = 0x005B;
    public static final int REG_M3_T1 = 0x0061;
    public static final int REG_M3_T2 = 0x006E;
    public static final int REG_FLOW1 = 0x004A;
    public static final int REG_INFO = 0x0063;
    
    public static final int REG_CLOCK = 0x03EA;
    public static final int REG_HEAT_ENERGY_Y = 0x0060;

    public static long SLEEPTIME = 1 * 1000;

    /**
     * constructor
     */
    public KAMSTRUP601_DataConstants()
    {
    }
    
    public static char gencrc_16(char i)
    {
    	char k;
    	char crc;
	    k = (char)(i << 8);
	    crc = 0;
	    for ( int j = 0 ; j < 8 ; j++ ) {
		    if ( (( crc ^ k ) & 0x8000)!=0 )
		    	crc = (char)(( crc << 1 ) ^ 0x1021);
		    else
			    crc <<= 1;
			    k <<= 1;
	    }
	    return(crc);
    }
    
    public static char CalculateCharacterCRC16(char crc, byte c )
    {
    	return (char)( ( crc << 8 ) ^ gencrc_16((char)(((crc >> 8 ) ^ c ) & 0xff) ) );
    }   
}
