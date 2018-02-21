package com.aimir.fep.protocol.mrp.protocol;

/**
 * RequestData Constants
 * 
 * @author Kang, Soyi
 */
public class LGRW3410_DataConstants
{

    public static final byte SOH = (byte)0xEE;

    public static final byte RESERVED = (byte)0x00;
    public static final byte SEQ_NBR = (byte)0x00;
    public static final byte DATA_CTRL_00 = (byte)0x00;
    public static final byte DATA_CTRL_20 = (byte)0x20;
    
    public static final byte DATA_CTRL_R_ACK = 0x06;
    public static final byte DATA_CTRL_R_NACK = 0x15;
    public static final byte OK = 0x00;
    public static final short TABLE_LP =6;
    public static final short TABLE_LP2 =21;
    public static final short TABLE_POWERFAIL =10;
    public static final short TABLE_POWERRECOVER =11;
    public static final short TABLE_CONFIG = 129;
    public static final short TABLE_SELFREADING =4;
    public static final short TABLE_DEMANDINFO =15;
    
    public static final byte DATA_COMMAND_IDENTIFY = 0x20;
    public static final byte DATA_COMMAND_NEGOCIATE = 0x60;
    public static final byte DATA_COMMAND_LOGON = 0x50;
    public static final byte DATA_COMMAND_SECURITY = 0x51;
    public static final byte DATA_COMMAND_LOGOFF = 0x52;
    public static final byte DATA_COMMAND_TERMINATE = 0x21;
    

    public static final int KH_FIRST_PAGE  = 1000;
    public static final int KH_LAST_PAGE   = 2727;
  
    public static final int LG_FIRST_PAGE  = 513;
    public static final int LG_LAST_PAGE   = 2560;
  
    public static final byte[] DATA_CTRL = {(byte)0x00, (byte)0x20};
    public static final byte DATA_COMMAND_READ = 0x30;
    
    public static long SLEEPTIME = 1 * 1000;

    /**
     * constructor
     */
    public LGRW3410_DataConstants()
    {
    }
    
    public static int KH_INC_PAGE(int page){
    	return (page == KH_LAST_PAGE) ? (KH_FIRST_PAGE) : (page++);
    }
    
    public static int KH_DEC_PAGE(int page){
    	return (page == KH_FIRST_PAGE) ? (KH_LAST_PAGE) : (page++);
    }
    
    public static char KH_CRC16( byte[] baTemp, int nPtr, int nLen )
    {
    	
    	char 	crc, d_crc, mem;

    	crc   = 0xFFFF;
    	d_crc = 0;

    	for( int i = 0; i < nLen; i++ ) 
    	{
    		d_crc = (char)((char)(baTemp[i+nPtr] & 0xFF) ^ (crc & 0x00FF));
    		
    		for( int j = 0; j < 8; j++ ) 
    		{
    			mem =  (char)(d_crc & 0x0001);
    			d_crc /= 2;
    			if(mem>0) d_crc ^= (char)0x8408;
    		}
    		
    		crc = (char)((( crc >> 8)&0xFF) ^ (char)d_crc);
    	}

    	crc = (char)((~crc<<8) | (~crc>>8 & 0xFF));
    	return(crc);
    }
}
