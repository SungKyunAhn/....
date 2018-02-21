package com.aimir.fep.protocol.fmp.frame;

import com.aimir.fep.protocol.fmp.datatype.BYTE;

/**
 * ServiceData Constants
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class ServiceDataConstants
{
    public static byte SVC_CMD=(byte)'C';
    public static byte SVC_MMD=(byte)'M';
    public static byte SVC_NMD=(byte)'N';
    public static byte SVC_ALM=(byte)'A';
    public static byte SVC_EVT=(byte)'E';
    public static byte SVC_EVT2=(byte)'B';
    public static byte SVC_FTS=(byte)'F';
    public static byte SVC_TEL=(byte)'T';

    public static int HEADER_LEN = 4;

    // Command Service Constants
    public static BYTE C_ATTR_REQUEST = new BYTE((byte)0x81);
    public static BYTE C_ATTR_RESPONSE = new BYTE((byte)0x00);

    // Event Service Constants
    public static BYTE E_SRCTYPE_UNKNOWN = new BYTE((byte)0x00);
    public static BYTE E_SRCTYPE_FEP = new BYTE((byte)0x01);
    public static BYTE E_SRCTYPE_MCU = new BYTE((byte)0x02);
    public static BYTE E_SRCTYPE_OAMPC = new BYTE((byte)0x03);
    public static BYTE E_SRCTYPE_OAMPDA = new BYTE((byte)0x04);
    public static BYTE E_SRCTYPE_MOBILE = new BYTE((byte)0x05);
    public static BYTE E_SRCTYPE_SINK = new BYTE((byte)0x06);
    public static BYTE E_SRCTYPE_ZRU = new BYTE((byte)0x07);
    public static BYTE E_SRCTYPE_ZMU = new BYTE((byte)0x08);
    public static BYTE E_SRCTYPE_ZEU = new BYTE((byte)0x09);
    public static BYTE E_SRCTYPE_UNIT = new BYTE((byte)0x0A);
    public static BYTE E_SRCTYPE_MMIU = new BYTE((byte)0x0B);
    public static BYTE E_SRCTYPE_CODI = new BYTE((byte)0x0C);
    public static BYTE E_SRCTYPE_IEIU = new BYTE((byte)0x0D);    
    public static BYTE E_SRCTYPE_PLC = new BYTE((byte)0x0E);    
    public static BYTE E_SRCTYPE_MMIU2 = new BYTE((byte)0x0F);
    public static BYTE E_SRCTYPE_SERIAL = new BYTE((byte)0x10);
    public static BYTE E_SRCTYPE_CONVERTER = new BYTE((byte)0x13);    
    public static BYTE E_SRCTYPE_SUBGIGA = new BYTE((byte)0x65);

    /**
     * constructor
     */
    public ServiceDataConstants()
    {
    }
}
