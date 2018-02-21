package com.aimir.fep.protocol.fmp.frame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.util.FMPProperty;

/**
 * ControlData Constants
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class ControlDataConstants
{
    private static Log log = LogFactory.getLog(ControlDataConstants.class);
    
    public static byte CODE_ENQ = 0x01;
    public static byte CODE_ACK = 0x02;
    public static byte CODE_NAK = 0x03;
    public static byte CODE_CAN = 0x04;
    public static byte CODE_EOT = 0x05;
    public static byte CODE_AUT = 0x06;
    public static byte CODE_WCK = 0x07;    
    public static byte CODE_REDIR = 0x08;
    public static byte CODE_RETRY = 0x09;
    public static byte CODE_NEG = 0x0A;
    public static byte CODE_NEGR = 0x0B;
    
    public static byte NEG_R_NOERROR = 0;
    public static byte NEG_R_UNSUPPORTED_VERSION = 1;
    public static byte NEG_R_UNKNOWN_NAMESPACE = 2;
    public static byte NEG_R_INVALID_SIZE = 3;
    public static byte NEG_R_INVALID_COUNT = 4;
    
    private static byte[] WINDOW_SIZE = (new WORD(Integer.parseInt(
            FMPProperty.getProperty("frame.maxlen")))).encode();
    private static byte[] WINDOW_COUNT = (new BYTE(Integer.parseInt( 
            FMPProperty.getProperty("frame.window.size")))).encode();
    
    public static OCTET CODE_ENQ_ARG = null;
    public static OCTET CODE_NEG_ARG = null;
    
    static {
        String version = FMPProperty.getProperty("protocol.version");
        log.info("version[" + version+"]");
        
        byte[] bver = new byte[] {0x01, 0x00};
        bver[0] = (byte)Integer.parseInt(version.substring(0, 2));
        bver[1] = (byte)Integer.parseInt(version.substring(2));
        
        boolean useExt = Boolean.parseBoolean(FMPProperty.getProperty("protocol.enq.ext.used"));
        log.info("useExt[" + useExt+"]");
        if (useExt)
            CODE_ENQ_ARG = new OCTET(new byte[] {bver[0],bver[1],WINDOW_SIZE[0],WINDOW_SIZE[1], WINDOW_COUNT[0]});
        else
            CODE_ENQ_ARG = new OCTET(new byte[] {bver[0],bver[1]});
        
        CODE_NEG_ARG = new OCTET(new byte[] {bver[0],bver[1],WINDOW_SIZE[0],WINDOW_SIZE[1], WINDOW_COUNT[0], 0x00, 0x00});
    };
    
    public static OCTET CODE_ACK_ARG = new OCTET(new byte[] {0x00});
    public static OCTET CODE_NAK_ARG = new OCTET(new byte[] {0x00});
    public static OCTET CODE_CAN_ARG = new OCTET(new byte[] {0x00});
    public static OCTET CODE_EOT_ARG = new OCTET(new byte[] {0x00});
    public static OCTET CODE_AUT_ARG = new OCTET(new byte[] {0x00});
    public static OCTET CODE_WCK_ARG = new OCTET(new byte[] {0x00});
    public static OCTET CODE_NEGR_ARG = new OCTET(new byte[] {0x00});
    public static OCTET CODE_REDIR_ARG = new OCTET(new byte[] {0x00});
    public static OCTET OCDE_RETRY_ARG = new OCTET(new byte[] {0x00});

    public static long SLEEPTIME = 1 * 1000;

    /**
     * constructor
     */
    public ControlDataConstants()
    {
    }
}
