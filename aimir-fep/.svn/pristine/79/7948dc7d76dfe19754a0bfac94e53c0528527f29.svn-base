/* 
 * @(#)TMTR_LP.java       1.0 2008-06-02 *
 * 
 * Meter LP Data (Hourly)
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

public class TMTR_LP implements java.io.Serializable{    

    public static final String TABLE_KIND = "LP";
    public static final int TABLE_CODE = 0;
	public static final int LEN_METER_ID = 8;
    public static final int LEN_DATETIME = 4;
    public static final int OFS_DATA = 4;
    protected String table_kind = null;

	public static final int OFS_CURRENT_METER_DATETIME= 11;
	
	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(TMTR_LP.class);
    
    protected HMData[] hmData = null;
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public TMTR_LP(byte[] rawData, String table_kind) {
        this.rawData = rawData;
        this.table_kind = table_kind;
        try{
            parse();
        }catch(Exception e)
        {
            log.error(e,e);
        }
	}
    
    /**
     * Constructor .<p>
     * 
     * @param data - read data (header,crch,crcl)
     */
    public TMTR_LP() {

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
                                DataFormat.select(rawData,offset,rawData.length-offset)).getLength();
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
                if(obj[i] instanceof DATETIME)
                {
                    datetime = ((DATETIME)obj[i]).getDateTime();
                    dtList.add(datetime);
                }else if(obj[i] instanceof DATE)
                {
                    datetime = ((DATE)obj[i]).getDate();
                    dtList.add(datetime);
                }
                else
                {
                    //log.debug("vif["+vif+"],data=["+obj[i].toString()+"]");
                    switch(vif){
                    case VIF.FIELD_HEAT_GCAL:
                        if(func == DIF.FUNCTION_CUMM){
                            ch2.add(obj[i]);
                        }else{
                            ch1.add(obj[i]);
                        }
                        break;
                    case VIF.FIELD_HEAT_MWH:
                        break;
                    case VIF.FIELD_FLOW_M3:
                        if(func == DIF.FUNCTION_CUMM){
                            ch4.add(obj[i]);
                        }else{
                            ch3.add(obj[i]);
                        }
                        break;
                    case VIF.FIELD_FLOW_LITER:
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
        }   
        hmData = new HMData[dtList.size()];
        for(int i = 0; i < dtList.size(); i++)
        {
            Double ch1Value = new Double(0);
            Double ch2Value = new Double(0);
            Double ch3Value = new Double(0);
            Double ch4Value = new Double(0);
            Double ch5Value = new Double(0);
            Double ch6Value = new Double(0);
            Double ch7Value = new Double(0);
            Double ch8Value = new Double(0);
            String dt = (String)dtList.get(i);
            log.debug("datetime["+dt+"],"+ table_kind);
            
            if(ch1 != null && ch1.size() > i)
                ch1Value = (Double)ch1.get(i)*10;
            if(ch2 != null && ch2.size() > i)
                ch2Value = (Double)ch2.get(i)*100;
            if(ch3 != null && ch3.size() > i)
                ch3Value = (Double)ch3.get(i)*10;
            if(ch4 != null && ch4.size() > i)
                ch4Value = (Double)ch4.get(i)*100;
            if(ch5 != null && ch5.size() > i)
                ch5Value = (Double)ch5.get(i);
            if(ch6 != null && ch6.size() > i)
                ch6Value = (Double)ch6.get(i);
            if(ch7 != null && ch7.size() > i)
                ch7Value = (Double)ch7.get(i);
            ch8Value = ch6Value - ch7Value;
            hmData[i] = new HMData();
            hmData[i].setKind(table_kind); 
            hmData[i].setDate(dt.substring(0,8));
            if(table_kind.equals("LP") || table_kind.equals("CURRENT")){
                hmData[i].setTime(dt.substring(8,12));
            }

            hmData[i].setChannelCnt(8);
            hmData[i].setCh(1,ch1Value);
            hmData[i].setCh(2,ch2Value);
            hmData[i].setCh(3,ch3Value);
            hmData[i].setCh(4,ch4Value);
            hmData[i].setCh(5,ch5Value);
            hmData[i].setCh(6,ch6Value);
            hmData[i].setCh(7,ch7Value);     
            hmData[i].setCh(8,ch8Value);
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
            sb.append("TMTR_LP DATA["); 
            for(int i = 0; i < hmData.length; i++){
                sb.append(hmData[i].toString());
            }
            sb.append("]\n");
        }catch(Exception e){
            log.warn("TMTR_LP TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}