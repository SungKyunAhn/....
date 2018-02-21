package com.aimir.fep.protocol.mrp.protocol;

/**
 * RequestData Constants
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class DataConstants
{
    protected static final byte[] MD = new byte[]{'M','D'};
    protected static final byte[] MT = new byte[]{'M','T'};
    protected static final byte[] PB = new byte[]{'P','B'};
    protected static final byte[] CB = new byte[]{'C','B'};
    protected static final byte[] LD = new byte[]{'L','D'};
    protected static final byte[] IS = new byte[]{'I','S'};
    protected static final byte[] EL = new byte[]{'E','L'};
    protected static final byte[] PQ = new byte[]{'P','Q'};
    
    protected static final byte[] MTI = new byte[]{'M','T','I'};
    protected static final byte[] TPB = new byte[]{'T','P','B'};
    protected static final byte[] TCB = new byte[]{'T','C','B'};
    protected static final byte[] LPD = new byte[]{'L','P','D'};
    protected static final byte[] IST = new byte[]{'I','S','T'};
    protected static final byte[] ELD = new byte[]{'E','L','D'};
    protected static final byte[] PQM = new byte[]{'P','Q','M'};
    
    
    protected static final byte[] LP_IEIU = new byte[]{(byte)0x81};
    protected static final byte[] PB_IEIU = new byte[]{(byte)0xA6};
    protected static final byte[] FAIL_IEIU = new byte[]{(byte)0x90};

    protected static final byte[] HOURLY = new byte[]{(byte)0x00};
    protected static final byte[] DAILY = new byte[]{(byte)0x01};
    protected static final byte[] MONTHLY = new byte[]{(byte)0x02};
    protected static final byte[] CURRENT = new byte[]{(byte)0x03};
    protected static final byte[] EVENT = new byte[]{(byte)0x04};
    protected static final byte[] BASEPULSE = new byte[]{(byte)0x05};
    protected static final byte[] CUMMDATA = new byte[]{(byte)0x06};
        
    /**
     * constructor
     */
    public DataConstants()
    {
    }
}
