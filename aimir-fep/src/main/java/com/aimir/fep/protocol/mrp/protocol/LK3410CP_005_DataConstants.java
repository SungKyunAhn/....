package com.aimir.fep.protocol.mrp.protocol;

import com.aimir.fep.protocol.fmp.datatype.OCTET;

/**
 * RequestData Constants
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class LK3410CP_005_DataConstants
{

    public static final OCTET SOH = new OCTET(new byte[]{0x2F,0x02});

    public static final byte DATA_CTRL_R_ACK = 0x00;
    public static final byte DATA_CTRL_R_NACK = 0x01;
    public static final byte DATA_CTRL_R_AUTA = 0x02;
    public static final byte DATA_CTRL_R_AUTR = 0x03;
    public static final byte DATA_CTRL_R_LINKSTATE = 0x0B;
    public static final byte DATA_CTRL_R_ACK2 = 0x10;
    public static final byte DATA_CTRL_R_NACK2 = 0x11;
    public static final byte DATA_CTRL_R_LINKSTATEREPLY = 0x1B;
    public static final byte DATA_CTRL_R_RESETLINK = 0x40;
    public static final byte DATA_CTRL_R_UNCONFIRMEDUSERDATA = 0x44;
    public static final byte DATA_CTRL_R_LINKSTATEREQUEST = 0x49;
    public static final byte DATA_CTRL_R_AUTHENTICATION = 0x51;
    public static final byte DATA_CTRL_R_TESTLINK = 0x52;
    public static final byte DATA_CTRL_R_CONFIRMEDUSERDATA = 0x53;
    public static final byte DATA_CTRL_R_RESETLINK2 = 0x60;
    public static final byte DATA_CTRL_R_UNCONFIRMEDUSERDATA2 = 0x64;    
    public static final byte DATA_CTRL_R_LINKSTATEREQUEST2 = 0x69;
    public static final byte DATA_CTRL_R_AUTHENTICATION2 = 0x71;
    public static final byte DATA_CTRL_R_TESTLINK2 = 0x72;
    public static final byte DATA_CTRL_R_CONFIRMEDUSERDATA2 = 0x73;

    public static long SLEEPTIME = 1 * 1000;

    /**
     * constructor
     */
    public LK3410CP_005_DataConstants()
    {
    }
}
