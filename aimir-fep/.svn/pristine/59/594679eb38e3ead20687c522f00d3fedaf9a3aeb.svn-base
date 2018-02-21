/* 
 * @(#)TMTR_CURRENT.java       1.0 2008-06-02 *
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
 * @author YK.Park
 */
package com.aimir.fep.meter.parser.kdhTable;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.HMData;
import com.aimir.fep.util.DataFormat;

public class TMTR_CURRENT extends TMTR_LP{     

    public String TABLE_KIND = "CURRENT";
    public static final int TABLE_CODE = 3;
	public static final int LEN_METER_ID = 8;

	public static final int OFS_CURRENT_METER_DATETIME= 0;
	
	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(TMTR_CURRENT.class);
    
    private HMData[] hmData = null;
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public TMTR_CURRENT(byte[] rawData) {
        super();
        this.rawData = rawData;
        try{
            parse();
        }catch(Exception e)
        {
            log.error(e,e);
        }
	}
    
    public void parse()
    throws Exception
    {
        List dbList = new ArrayList();
        int offset = OFS_DATA;
        int len = 0;
        boolean isMore = true;
        
        while(isMore)
        {
            len = new DataBlock(
                                DataFormat.select(rawData,offset,DataBlock.LEN_DRH)).getLength();
            log.debug("len:"+len);
            DataBlock db = new DataBlock(DataFormat.select(rawData,offset,len));
            log.debug(db.toString());
            dbList.add(db);
            offset += len;
            if(offset == rawData.length){
                isMore = false;
            }
        }
        Iterator it = dbList.iterator();
        String datetime = null;
        ArrayList dtList = new ArrayList();
        ArrayList ch1 = new ArrayList();
        ArrayList ch2 = new ArrayList();
        ArrayList ch3 = new ArrayList();
        ArrayList ch4 = new ArrayList();
        ArrayList ch5 = new ArrayList();
        ArrayList ch6 = new ArrayList();
        ArrayList ch7 = new ArrayList();
        ArrayList stat = new ArrayList();
        
        while(it.hasNext()){

            DataBlock db = (DataBlock)it.next();
            int func = db.getDIF().getFunctionField();
            int datafield = db.getDIF().getDataField();
            int vif = db.getVIF().getVIF();
            int vife = db.getVIFE().getVIFE();
            boolean desc = false;
            Object[] obj = db.getActualData();

            for(int i = 0; i < obj.length; i++)
            {
                switch(vif){
                case VIF.FIELD_HEAT_GCAL:
                case VIF.FIELD_HEAT_MWH:
                    if(func == DIF.FUNCTION_CUMM){
                        ch2.add(obj[i]);
                    }else{
                        ch1.add(obj[i]);
                    }
                    break;
                case VIF.FIELD_FLOW_M3:
                case VIF.FIELD_FLOW_LITER:
                    if(func == DIF.FUNCTION_CUMM){
                        ch4.add(obj[i]);
                    }else{
                        ch3.add(obj[i]);
                    }
                    break;
                case VIF.FIELD_SUPPLYPRESSURE:
                    ch5.add(obj[i]);
                    break;
                case VIF.FIELD_SUPPLYTEMPERATURE:
                    ch6.add(obj[i]);
                    break;
                case VIF.FIELD_RETRIVALTEMPERATURE:
                    ch7.add(obj[i]);
                    break;
                }
            }                        
        }   
        
        Double ch1Value = new Double(0);
        Double ch2Value = new Double(0);
        Double ch3Value = new Double(0);
        Double ch4Value = new Double(0);
        Double ch5Value = new Double(0);
        Double ch6Value = new Double(0);
        Double ch7Value = new Double(0);
        Double ch8Value = new Double(0);
        
        hmData = new HMData[1];
        String dt = getMeterTime();
        if(ch1 != null && ch1.size() > 0)
            ch1Value = (Double)ch1.get(0)*100;
        if(ch2 != null && ch2.size() > 0)
            ch2Value = (Double)ch2.get(0)*100;
        if(ch3 != null && ch3.size() > 0)
            ch3Value = (Double)ch3.get(0)/10;
        if(ch4 != null && ch4.size() > 0)
            ch4Value = (Double)ch4.get(0)/10;
        if(ch5 != null && ch5.size() > 0)
            ch5Value = (Double)ch5.get(0);
        if(ch6 != null && ch6.size() > 0)
            ch6Value = (Double)ch6.get(0);
        if(ch7 != null && ch7.size() > 0)
            ch7Value = (Double)ch7.get(0);
        ch8Value = ch6Value - ch7Value;
        hmData[0] = new HMData();
        hmData[0].setKind(TABLE_KIND); 
        hmData[0].setDate(dt.substring(0,8));
        hmData[0].setTime(dt.substring(8,12));
        hmData[0].setChannelCnt(8);
        hmData[0].setCh(1,ch1Value);
        hmData[0].setCh(2,ch2Value);
        hmData[0].setCh(3,ch3Value);
        hmData[0].setCh(4,ch4Value);
        hmData[0].setCh(5,ch5Value);
        hmData[0].setCh(6,ch6Value);
        hmData[0].setCh(7,ch7Value); 
        hmData[0].setCh(8,ch8Value);
    }
    
    public String getMeterTime()
    {
        DATETIME datetime = new DATETIME(rawData,OFS_CURRENT_METER_DATETIME,DATETIME.LENGTH);
        return datetime.getDateTime();
    }
    
    public HMData[] getData()
    {
        return this.hmData;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("TMTR_CURRENT DATA["); 
            sb.append(",current time["+getMeterTime()+"]");
            for(int i = 0; i < hmData.length; i++){
                sb.append(hmData[i].toString());
            }
            sb.append("]\n");
        }catch(Exception e){
            log.warn("TMTR_CURRENT TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}