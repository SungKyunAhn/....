/* 
 * @(#)DATE.java       1.0 2008-06-02 *
 * 
 * Date Information
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

public class DATE {    
	
    public static int LENGTH = 2;
	private byte[] rawData = null;
    
    int year = Calendar.YEAR;
    int month = Calendar.MONTH;
    int day = Calendar.DATE;
    String date = null;
    
    Calendar cal = null;

	/**
	 * Constructor .<p>
	 */
	public DATE(byte[] rawData) {
        this.rawData = rawData;
        parse();
	}
    
    /**
     * Constructor .<p>
     */
    public DATE(byte[] rawData, int offset, int length) {
        
        byte[] temp = new byte[length];
        System.arraycopy(rawData,offset,temp,0,length);        
        this.rawData = temp;
        parse();
    }
    
    public void parse()
    {
        int year_h = rawData[1] >> 4;
        int year_l = rawData[0] >> 5;
        year = year_h << 3;
        year = year | year_l ;
        month = rawData[1] & 0x0F;
        day = rawData[0] & 0x1F;
        year += 2000;
        
        cal = Calendar.getInstance();
        //cal.set(Calendar.YEAR,year);
        //cal.set(Calendar.MONTH,month-1);
        //cal.set(Calendar.DATE,day);

        cal.set(year,month-1,day,0,0,0);
        date = getCalToString(cal);
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
    
    public Calendar getCalendar()
    {
        return this.cal;
    }
    
    public String getDate()
    {
        return date.substring(0,8);
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("Year:"+year);
        sb.append(",month:"+month);
        sb.append(",day:"+day+"\n");
        return sb.toString();
    }

}
