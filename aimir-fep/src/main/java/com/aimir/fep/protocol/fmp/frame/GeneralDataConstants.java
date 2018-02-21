package com.aimir.fep.protocol.fmp.frame;

import com.aimir.fep.util.FMPProperty;

/**
 * GeneralData Constants
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class GeneralDataConstants
{
    public static byte SOH = (byte)0x5E;

    // field length
    public static int HEADER_LEN = 8;
    public static int SOH_LEN = 1;
    public static int SEQ_LEN = 1;
    public static int ATTR_LEN = 1;
    public static int LENGTH_POS = SOH_LEN+SEQ_LEN+ATTR_LEN;
    public static int LENGTH_LEN = 4;
    public static int SVC_LEN = 1;

    public static int TAIL_LEN = 2;

    public static int FRAME_MAX_LEN = Integer.parseInt(
            FMPProperty.getProperty("frame.maxlen","4096")); 
    //public static int FRAME_MAX_LEN = Integer.MAX_VALUE;
    public static int FRAME_MAX_SEQ = 255;
    public static int FRAME_WINSIZE = Integer.parseInt( 
            FMPProperty.getProperty("frame.window.size","1")); 
    
    // AUTH attr
    public static int SC_LEN = 1;
    public static int IC_LEN = 4;
    public static int AUTH_TAG_LEN = 12;
    
    // Attr
    public static byte ATTR_FRAME = (byte)0x80;
    public static byte ATTR_ACK = (byte)0x10;
    public static byte ATTR_CRYPT = (byte)0x08;
    public static byte ATTR_COMPRESS = (byte)0x04;
    public static byte ATTR_START = (byte)0x02;
    public static byte ATTR_END = (byte)0x01;

    // Svc
    public static byte SVC_C = (byte)'C';
    public static byte SVC_M = (byte)'M';
    public static byte SVC_N = (byte)'N';
    public static byte SVC_A = (byte)'A';
    public static byte SVC_E = (byte)'E';
    public static byte SVC_F = (byte)'F';
    public static byte SVC_T = (byte)'T';
    public static byte SVC_L = (byte)'L';
    public static byte SVC_S = (byte)'S';
    public static byte SVC_P = (byte)'P';
    public static byte SVC_D = (byte)'D';
    public static byte SVC_R = (byte)'R';   // 2012.04.23
    public static byte SVC_B = (byte)'B';   // 2014.08.27
    public static byte SVC_G = (byte)'G';   // 2014.08.27


    // Status
    public static int ENQ_RCVWAIT = 1;
    public static int ENQ_SNDWAIT = 2;
    public static int NAK_SNDWAIT = 3;

    // Compress
    public static byte COMP_TYPE_NONE = (byte)0x00;
    public static byte COMP_TYPE_ZLIB = (byte)0x01;
    public static byte COMP_TYPE_GZIP = (byte)0x02;


    // sleep time
    public static long WAITTIME_SEND_FRAMES= Long.parseLong(
            FMPProperty.getProperty("protocol.waittime.send.frames",
                "10")); 
    public static long WAITTIME_AFTER_SEND_FRAME= Long.parseLong(
            FMPProperty.getProperty(
                "protocol.waittime.after.send.frame","1000")); 

    public static boolean LITTLE_ENDIAN= 
        "little".toLowerCase().equals(FMPProperty.getProperty(
                "protocol.peer.byteorder","little")); 
    /**
     * constructor
     */
    public GeneralDataConstants()
    {
    }
}
