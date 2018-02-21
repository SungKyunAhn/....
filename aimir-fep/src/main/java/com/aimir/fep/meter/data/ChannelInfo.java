/** 
 * @(#)ChannelInfo.java       1.0 2007.09.20 *
 * 
 * ChannelInfo Class.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
package com.aimir.fep.meter.data;

import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Park Yeon Kyoung
 */
public class ChannelInfo {
    
    private static Log log = LogFactory.getLog(ChannelInfo.class);

	private String channelName ;
	private String channelCode ;
	private String unit;
    
    public static String POWER_W    = "W";
    public static String ENERGY_WH   = "Wh";
    public static String POWER_VAR  = "var";
    public static String ENERGY_VARH = "varh";
    public static String POWER_VA   = "va";
    public static String ENERGY_VAH  = "vah";
    
    public static String POWER_KW    = "kW";
    public static String ENERGY_KWH   = "kWh";
    public static String POWER_KVAR  = "kvar";
    public static String ENERGY_KVARH = "kvarh";
    public static String POWER_KVA   = "kva";
    public static String ENERGY_KVAH  = "kvah";
    
    public ChannelInfo() 
    {

    }

	public ChannelInfo (String channelCode, String channelName, String unit) 
    {
        this.channelCode = channelCode;
		this.channelName = channelName;
		this.unit        = unit;
	}
	
	public void setChannelName(String channelName)
    {
		this.channelName = channelName;
	}
	
	public void setChannelCode(String channelCode)
    {
		this.channelCode = channelCode;
	}
	
	public void setUnit(String unit)
    {
		this.unit = unit;
	}
	
	public String getChannelName()
    {
		return this.channelName;
	}

	public String getChannelCode()
    {
		return this.channelCode;
	}
	
    public String getUnit()
    {
        return this.unit;
    }
    
    public String getChannelString(int index)
    {
        String str = new String();

        String unit2 = "";
        try
        {
            Class clazz = ChannelInfo.class;
            Field field = null;
            if(unit.toUpperCase().endsWith("H")){
                field = clazz.getDeclaredField("POWER_"+unit.toUpperCase().replaceAll("H",""));
                unit2 = (String)field.get(field.getName());
                str = "ch"+index+"="+channelName+" Energy["+unit+"]<br>";
            }else if(unit.toUpperCase().endsWith("V")){
                str = "ch"+index+"="+channelName+" ["+unit+"]<br>";
            }
            else {
                field = clazz.getDeclaredField("ENERGY_"+unit.toUpperCase().replaceAll("H",""));
                unit2 = (String)field.get(field.getName());
                str = "ch"+index+"="+channelName+" Power["+unit+"]<br>";
            }
        }
        catch (SecurityException e)
        {
            log.warn(e);
        }
        catch (NoSuchFieldException e)
        {
            log.warn(e);
        }
        catch (IllegalAccessException e)
        {
            log.warn(e);
        }
        return str;
    }
}