/* 
 * @(#)DATETIME.java       1.0 2008-06-02 *
 * 
 * Data Information
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
/**
 * @author YK.Park
 */
package com.aimir.fep.meter.parser.kdhTable;

import java.util.Calendar;

public class DATETIME {    
	
    public static int LENGTH = 4;
	private byte[] rawData = null;
    
    int year = Calendar.YEAR;
    int month = Calendar.MONTH;
    int day = Calendar.DATE;
    int hour = Calendar.HOUR;
    int min = Calendar.MINUTE;
    String datetime = null;
    
    Calendar cal = null;

	/**
	 * Constructor .<p>
	 */
	public DATETIME(byte[] rawData) {
        this.rawData = rawData;
        parse();
	}
    
    /**
     * Constructor .<p>
     */
    public DATETIME(byte[] rawData, int offset, int length) {
        
        byte[] temp = new byte[length];
        System.arraycopy(rawData,offset,temp,0,length);        
        this.rawData = temp;
        parse();
    }
    
    public void parse()
    {
        int year_h = rawData[3] >> 4;
        int year_l = rawData[2] >> 5;
        year = year_h << 3;
        year = year | year_l;
        month = rawData[3] & 0x0F;
        day = rawData[2] & 0x1F;
        hour = rawData[1] & 0x1F;
        min = rawData[0] & 0x3F;
        year += 2000;
        
        cal = Calendar.getInstance();
        //cal.set(Calendar.YEAR,year);
        //cal.set(Calendar.MONTH,month-1);
        //cal.set(Calendar.DATE,day);
        //cal.set(Calendar.HOUR,hour);
        //cal.set(Calendar.MINUTE,min);
        //cal.set(Calendar.SECOND,0);
        
        cal.set(year,month-1,day,hour,min,0);
        datetime = getCalToString(cal);
    }
    
    private String getCalToString(Calendar cal) {

        String result = "";
        String tmp = "";

        tmp += cal.get(Calendar.YEAR);
        if(tmp.length() < 4) {
            while(tmp.length() < 4)
            {
                tmp = "0" + tmp;
            }
        }
        result += tmp;
        tmp = "";

        tmp += cal.get(Calendar.MONTH)+1;
        if(tmp.length() == 1) {
            result += "0";
        }
        result += tmp;
        tmp = "";

        tmp += cal.get(Calendar.DAY_OF_MONTH);
        if(tmp.length() == 1) {
            result += "0";
        }
        result += tmp;
        tmp = "";

        tmp += cal.get(Calendar.HOUR_OF_DAY);
        if(tmp.length() == 1) {
            result += "0";
        }
        result += tmp;
        tmp = "";

        tmp += cal.get(Calendar.MINUTE);
        if(tmp.length() == 1) {
            result += "0";
        }
        result += tmp;
        tmp = "";
        
        tmp += cal.get(Calendar.SECOND);
        if(tmp.length() == 1) {
            result += "0";
        }
        result += tmp;
        tmp = "";

        return result;

    }
    
    public int getYear()
    {
       return year;
    }
    
    public int getMonth()
    {
       return month;
    }
    
    public int getDay()
    {
        return day;
    }
    
    public int getHour()
    {
        return hour;
    }
    
    public Calendar getCalendar()
    {
        return this.cal;
    }
    
    public String getDate()
    {
        return datetime.substring(0,8);
    }
    
    public String getTime()
    {
        return datetime.substring(8,12);
    }
    
    public String getDateTime()
    {
        return datetime;
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("Year:"+year);
        sb.append(",month:"+month);
        sb.append(",day:"+day);
        sb.append(",hour:"+hour);
        sb.append(",min:"+min+"\n");
        return sb.toString();
    }

}
