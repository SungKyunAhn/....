package com.aimir.fep.trap.common;

import java.lang.reflect.Field;
/**
 * Request Originator Constants
 * 
 * @author Y.S. Kim
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class ReqConstants
{
    public static int UNKNOWN       = 0;
    public static int FEP       = 1;
    public static int MCU    = 2;
    public static int OAMPC_Serial   = 3;
    public static int OAMPC_RF   = 4;
    public static int Mobile  = 5;

    /**
     * constructor
     */
    public ReqConstants()
    {
    }

    public static String getTypeString(int code) throws Exception
    {
        Class<ReqConstants> clazz = ReqConstants.class;
        Field[] fs = clazz.getFields();
        //log.debug("Field length ["+fs.length+"]");
        for (int i = 0; i < fs.length; i++)
        {
            String name = fs[i].getName();
            int value = fs[i].getInt(name);
            //log.debug("CodeName="+name+", CodeValue="+value);
            if (code == value)
            {
                return name;
            }
        }
        return "UKNOWN";
    }
}
