package com.aimir.fep.protocol.fmp.datatype;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * represent Data Type
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class DataType
{

    private static Log log = LogFactory.getLog(DataType.class);
    public static int BOOL = 1;
    public static int BYTE = 2;
    public static int WORD = 3;
    public static int UINT = 4;
    public static int CHAR = 5;
    public static int SHORT = 6;
    public static int INT = 7;
    public static int BCD = 8;
    public static int VER = 9;
    public static int HEX = 10;
    public static int STRING = 11;
    public static int STREAM = 12;
    public static int OPAQUE = 13;
    public static int OID = 14;
    public static int IPADDR = 15;
    public static int SMIVALUE = 16;
    public static int TIMESTAMP = 17;
    public static int TIMEDATE = 18;
    public static int GMTTIME = 19;
    public static int IP6ADDR = 201;
    public static int MACADDR = 202;

    /**
     * constructor
     */
    public DataType()
    {
    }

    /**
     * check whether it is FMPVariable  or FMPNonFixedVariable
     *
     * @param datatype <code>String</code> data type
     * @return result <code>boolean</code>
     */
    public static boolean isFixedType(String datatype)
    {
        String DTPACKAGE = "nuri.aimir.moa.protocol.fmp.datatype.";
        String dt_type = DTPACKAGE + datatype;
        String superclass = null;
        try
        {
            Class clazz = Class.forName(dt_type);
            if (clazz != null)
            {
                superclass = clazz.getSuperclass().getName();
                if ( superclass != null && 
                        "FMPNonFixedVariable".equals(
                            superclass.substring(
                                superclass.lastIndexOf(".")+1, 
                                superclass.length() ) ) )
                {
                    return true;
                }
            }
            return false;
        }
        catch (Exception e)
        {
            log.error("Exception :",e);
            return false;
        }

    }
}
