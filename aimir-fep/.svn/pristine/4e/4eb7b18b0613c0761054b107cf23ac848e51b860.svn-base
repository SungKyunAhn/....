package com.aimir.fep.protocol.mrp.protocol;

/**
 * RequestData Constants
 * 
 * @author Kang, Soyi
 */
public class KDH_DataConstants
{

//  meter_cmd
    public static final byte SOH_SINGLE		=(byte)0xE5;
    public static final byte SOH_SHORT		=(byte)0x10;
    public static final byte SOH_LONG		=(byte)0x68;
    public static final byte EOH_SHORT		=(byte)0x16;
    
    public static final byte REQ_UD1		=(byte)0x4A;
    public static final byte[] CONTROL_GET_LP		= new byte[]{(byte)0x7B, (byte)0x5B};
    public static final byte[] CONTROL_GET_CURRENT		= new byte[]{(byte)0x4B, (byte)0x7B};
    public static final byte METER_PULSE	=(byte)0x01;
    public static final byte CI_CODE_00		=(byte)0x00;
    public static final byte CI_CODE_01		=(byte)0x01;
    public static final byte CI_CODE_02		=(byte)0x02;
    public static final byte CI_CODE_03		=(byte)0x03;
    public static final byte CI_CODE_04		=(byte)0x04;
    public static final byte CI_CODE_05		=(byte)0x05;
    public static final byte CI_CODE_06		=(byte)0x06;
    public static final byte CI_CODE_07		=(byte)0x07;
    
    public static final byte CI_CODE_ERROR  =(byte)0x71;
    
    public static final byte SND_NKE_CTRL = (byte)0x40;
    public static final byte[] SND_UD_CTRL = {(byte)0x43, (byte)0x53, (byte)0x73};
    public static final byte[] REQ_UD2_CTRL = {(byte)0x4B, (byte)0x5B, (byte)0x7B};
    public static final byte[] REQ_UD1_CTRL = {(byte)0x4A, (byte)0x5A, (byte)0x7A};
    public static final byte[] RSP_CTRL = {(byte)0x08, (byte)0x28};
    
    public static final byte FCV_ENABLE_MASK =(byte)0x10;
    
    public static final byte REP_UD_HAS_DATA = (byte)0x28;
    public static final byte REP_UD_NO_DATA = (byte)0x08;
    
    public static final byte DATA_TYPE_HOURLY = (byte)0x00;
    public static final byte DATA_TYPE_DAILY = (byte)0x01;
    public static final byte DATA_TYPE_MONTHLY = (byte)0x02;
    public static final byte DATA_TYPE_CURRENT = (byte)0x03;
    public static final byte DATA_TYPE_EVENT = (byte)0x0F;
    
    public static final byte ACK = 0x06;
    public static final byte NACK = 0x15;

    public static long SLEEPTIME = 1 * 1000;

    /**
     * constructor
     */
    public KDH_DataConstants()
    {
    }
    
    public static byte checkSum(byte[] baData, int npos, int len )
    {
    	char mid = 0;
    	byte res = 0;
    	for(int i=npos; i<len; i++){
    		mid += (char)(baData[i] & 0xFF);
    	}
    	res = (byte)mid;
   /* 	if ((mid>>8)>0){
    		res++;
    	}
   */ 	return res;
    }
}
