/* 
 * @(#)TMTR_CUMM.java       1.0 2009-03-03 *
 * 
 * Meter CURRENT Data (Instantenous)
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
/**
 * @author Kang, Soyi
 */
package com.aimir.fep.meter.parser.kamstrup601Table;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.HMData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Util;
import com.aimir.util.TimeUtil;

public class TMTR_VALUE implements java.io.Serializable{     

    public static String TABLE_KIND = "VALUE";
    public static final int TABLE_CODE = 6;
	public static final int LEN_METER_ID = 8;

	public static final int OFS_COUNT =0;
	public static final int OFS_DATE =1;
	public static final int OFS_CURR_DATE =5;
	
	public static final int LEN_COUNT =1;
	public static final int LEN_DATE =4;
	public static final int LEN_CURR_DATA =4;
	
	public static final int OFS_CURRENT_METER_DATETIME= 0;
	
	private byte[] rawData = null;
	private int resolution =60;
	private double siEx = 0;
	
	private int CNT_OF_DATA_BY_ONEDAY =24;
	
    private static Log log = LogFactory.getLog(TMTR_VALUE.class);
    
    private HMData[] hmData = null;
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public TMTR_VALUE(byte[] rawData, int resolution, double siEx) {
		this.rawData = rawData;
		this.resolution = resolution;
		this.siEx = siEx;
        try{
        	this.hmData = parse();
        }catch(Exception e)
        {
            log.error(e,e);
        }
	}
        
	public HMData[] parse() throws Exception {
		log.debug("===============LP Parse Start=================");

		CNT_OF_DATA_BY_ONEDAY = 1440/resolution;

		int dayCnt = DataFormat.hex2dec(DataFormat.select(rawData,OFS_COUNT,LEN_COUNT));
        ArrayList<HMData> list = new ArrayList<HMData>();

        int ofs = OFS_DATE;
        for(int i=0; i<dayCnt; i++){
        	String datetime = getDate(DataFormat.select(rawData,ofs,LEN_DATE));
        	ofs+=LEN_DATE;
        	for(int j=0; j<CNT_OF_DATA_BY_ONEDAY; j++){
        		HMData hm = new HMData();
        		datetime = TimeUtil.getPreHour(datetime, -(1*j));
        		
        		hm.setDate(datetime.substring(0,8));
        		hm.setDate(datetime.substring(8,14));
        		hm.setChannelCnt(1);
                hm.setCh(1, new Double(DataUtil.getIntToBytes(DataFormat.LSB2MSB(DataFormat.select(rawData,ofs,LEN_CURR_DATA)))*siEx));
                ofs += LEN_CURR_DATA;
                list.add(hm);
        	}
        }
        
        if(list != null && list.size() > 0){
        	HMData[] data = null;
            Object[] obj = list.toArray();            
            data = new HMData[obj.length];
            for(int i = 0; i < obj.length; i++){
                data[i] = (HMData)obj[i];
            }
            return data;
        }
        else
        {
            return null;
        }
	}
	
	public String getDate(byte[] date){
		try{
			int idx=0;
			int year = DataFormat.hex2unsigned16(DataFormat.select(date, 0, 2));
	        idx =idx+2;
			int mm = DataFormat.hex2unsigned8(date[idx++]);
			int dd = DataFormat.hex2unsigned8(date[idx++]);
			
			StringBuffer ret = new StringBuffer();
			
			ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
			ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
			ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
			ret.append("000000");
			
			return ret.toString();
			
        }catch(Exception e)
        {
            log.error(e,e);
            return null;
        }
	}
	
    
    public HMData[] getData()
    {
        return this.hmData;
    }
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("TMTR_VALUE DATA["); 
            for(int i = 0; i < hmData.length; i++){
                sb.append(hmData[i].toString());
            }
            sb.append("]\n");
        }catch(Exception e){
            log.warn("TMTR_VALUE TO STRING ERR=>"+e.getMessage());
        }
        return sb.toString();
    }
}