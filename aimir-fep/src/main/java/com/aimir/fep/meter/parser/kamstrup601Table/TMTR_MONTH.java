/* 
 * @(#)TMTR_MONTH.java       1.0 2008-06-02 *
 * 
 * Meter Month Data
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
package com.aimir.fep.meter.parser.kamstrup601Table;

import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.HMData;
import com.aimir.fep.util.Util;

public class TMTR_MONTH implements java.io.Serializable{    

    public static final String TABLE_KIND = "MONTH";
    public static final int TABLE_CODE = 2;
    public static final int OFS_DATA = 0;
	private byte[] rawData = null;
	private String table_kind = null;
    protected HMData[] hmData = null;
    protected ArrayList hmDataArray = null;
    DecimalFormat dformat = new DecimalFormat("#0.000000");
    
    private static Log log = LogFactory.getLog(TMTR_MONTH.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public TMTR_MONTH(byte[] rawData, String table_kind) {
        this.rawData = rawData;
        this.table_kind = table_kind;
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
        DataBlock db = null;
        while(offset < rawData.length){
        	db = new DataBlock(rawData,offset);
        	dbList.add(db);
        	offset += db.getLength();
        }

        Iterator it = dbList.iterator();

        ArrayList dtList = new ArrayList();
        ArrayList ch1 = new ArrayList();
        ArrayList ch2 = new ArrayList();
        ArrayList ch3 = new ArrayList();
        ArrayList ch4 = new ArrayList();
        ArrayList ch5 = new ArrayList();
        ArrayList ch6 = new ArrayList();
        ArrayList ch7 = new ArrayList();
        ArrayList ch8 = new ArrayList();
        ArrayList ch9 = new ArrayList();
        ArrayList stat = new ArrayList();
        ArrayList count = new ArrayList();
        /*
        String date = null;
        String time = null;
        String datetime = null;
        */
        ArrayList date = null;
        ArrayList time = null;
        ArrayList datetime = null;
        
        while(it.hasNext()){

            db = (DataBlock)it.next();
            int ridcode = db.getRIDCode();
            count.add(new Integer(db.getCount()));
            
            switch(ridcode){
            case RegisterIDTable.DATE : date = db.getValue();break;
            case RegisterIDTable.E1 : ch2 = db.getValue(); 
        	break;
        	case RegisterIDTable.E2 : break;
        	case RegisterIDTable.E3 : break;
        	case RegisterIDTable.E4 : break;
        	case RegisterIDTable.E5 : break;
        	case RegisterIDTable.E6 : break;
        	case RegisterIDTable.E7 : break;
        	case RegisterIDTable.E8 : ch6 = db.getValue();break;
        	case RegisterIDTable.E9 : ch7 = db.getValue();break;
        	case RegisterIDTable.TA2 : break;
        	case RegisterIDTable.TA3 : break;
        	case RegisterIDTable.V1 : ch4 = db.getValue();break;
        	case RegisterIDTable.V2 : break;
        	case RegisterIDTable.VA : break;
        	case RegisterIDTable.VB : break;
        	case RegisterIDTable.M1 : break;
        	case RegisterIDTable.M2 : break;
        	case RegisterIDTable.HR : break;
        	case RegisterIDTable.INFOEVENT : break;
        	case RegisterIDTable.CLOCK : time = db.getValue();
       // 	log.debug("time : "+time);
        	break;
        	case RegisterIDTable.INFO : break;
        	case RegisterIDTable.T1 : ch8 = db.getValue();break;
        	case RegisterIDTable.T2 : ch9 = db.getValue();break;
        	case RegisterIDTable.T3 : break;
        	case RegisterIDTable.T4 : break;
        	case RegisterIDTable.T1_T2 : break;
        	case RegisterIDTable.P1 : ch5 = db.getValue();break;
        	case RegisterIDTable.P2 : break;
        	case RegisterIDTable.FLOW1 : ch3 = db.getValue();break; //
        	case RegisterIDTable.FLOW2 : break;
        	case RegisterIDTable.EFFEKT1 : break;
        	case RegisterIDTable.MAX_FLOW1DATE1 : break;
        	case RegisterIDTable.MAX_FLOW1 : break;
        	case RegisterIDTable.MIN_FLOW1DATE1: break;
        	case RegisterIDTable.MIN_FLOW1 : break;
        	case RegisterIDTable.MAX_EFFEKT1DATE1 : break;
        	case RegisterIDTable.MAX_EFFEKT1 : break;
        	case RegisterIDTable.MIN_EFFEKT1DATE1 : break;
        	case RegisterIDTable.MIN_EFFEKT1 : break;
        	case RegisterIDTable.MAX_FLOW1DATE2 : break;
        	case RegisterIDTable.MAX_FLOW2 : break;
        	case RegisterIDTable.MIN_FLOW1DATE2 : break;
        	case RegisterIDTable.MIN_FLOW2 : break;
        	case RegisterIDTable.MAX_EFFEKT1DATE2 : break;
        	case RegisterIDTable.MAX_EFFEKT2 : break;
        	case RegisterIDTable.MIN_EFFEKT1DATE2 : break;
        	case RegisterIDTable.MIN_EFFEKT2 : break;
        	case RegisterIDTable.AVR_T1 : break;
        	case RegisterIDTable.AVR_T2 : break;
        	case RegisterIDTable.AVR2_T1 : break;
        	case RegisterIDTable.AVR2_T2 : break;
        	case RegisterIDTable.TL2 : break;
        	case RegisterIDTable.TL3 : break;
        	case RegisterIDTable.XDAY : break;
        	case RegisterIDTable.PROG_NO : break;
        	case RegisterIDTable.CONFIG_NO_1 : break;
        	case RegisterIDTable.CONFIG_NO_2 : break;
        	case RegisterIDTable.SERIE_NO : break;
        	case RegisterIDTable.METER_NO_2 : break;
        	case RegisterIDTable.METER_NO_1 : break;
        	case RegisterIDTable.METER_NO_VA : break;
        	case RegisterIDTable.METER_NO_VB : break;
        	case RegisterIDTable.METER_TYPE : break;
        	case RegisterIDTable.CHECK_SUM_1 : break;
        	case RegisterIDTable.HIGH_RES : break;
        	case RegisterIDTable.TOPMODUL_ID : break;
        	case RegisterIDTable.BOTMODUL_ID : break;
            }
            /*
            datetime = date+time;
            if(datetime != null && !"".equals(datetime)){
            	dtList.add(datetime);
            }
            */
        }
        
        //hmData = new HMData[dtList.size()];
        hmDataArray = new ArrayList();
        int cnt = ((Integer)count.get(0)).intValue();
        
        for(int i = 0; i < cnt; i++)
        {
        	/*
            Double ch1Value = null;
            Double ch2Value = null;
            Double ch3Value = null;
            Double ch4Value = null;
            Double ch5Value = null;
            Double ch6Value = null;
            Double ch7Value = null;
            Double ch8Value = null;
            Double ch9Value = null;
            */
         	Double ch1Value = new Double(0.0);
            Double ch2Value = new Double(0.0);
            Double ch3Value = new Double(0.0);
            Double ch4Value = new Double(0.0);
            Double ch5Value = new Double(0.0);
            Double ch6Value = new Double(0.0);
            Double ch7Value = new Double(0.0);
            Double ch8Value = new Double(0.0);
            Double ch9Value = new Double(0.0);
            Double ch10Value = new Double(0.0);
            
            if(ch1 != null && ch1.size() > i)
                ch1Value = new Double(dformat.format(((Double)ch1.get(i)).doubleValue()));
            if(ch2 != null && ch2.size() > i)
                ch2Value = new Double(dformat.format(((Double)ch2.get(i)).doubleValue()));
            if(ch3 != null && ch3.size() > i)
                ch3Value = new Double(dformat.format(((Double)ch3.get(i)).doubleValue()));
            if(ch4 != null && ch4.size() > i)
                ch4Value = new Double(dformat.format(((Double)ch4.get(i)).doubleValue()));
            if(ch5 != null && ch5.size() > i)
                ch5Value = new Double(dformat.format(((Double)ch5.get(i)).doubleValue()));
            if(ch6 != null && ch6.size() > i)
                ch6Value = new Double(dformat.format(((Double)ch6.get(i)).doubleValue()));
            if(ch7 != null && ch7.size() > i)
                ch7Value = new Double(dformat.format(((Double)ch7.get(i)).doubleValue()));   
            if(ch8 != null && ch8.size() > i)
                ch8Value = new Double(dformat.format(((Double)ch8.get(i)).doubleValue()));   
            if(ch9 != null && ch9.size() > i)
                ch9Value = new Double(dformat.format(((Double)ch9.get(i)).doubleValue()));   
            if(ch8 != null && ch8.size() > i && ch9 != null && ch9.size() > i)
            	ch10Value = new Double(dformat.format(ch8Value.doubleValue() - ch9Value.doubleValue()));
            
    		HMData hm = new HMData();
    		
    		hm.setKind(table_kind); 
    		hm.setDate(Util.addDateYyyymmdd((String)date.get(i), 1) );
            
            hm.setChannelCnt(10);
            
            hm.setCh(1,ch1Value);
            hm.setCh(2,ch2Value);
            hm.setCh(3,ch3Value);
            hm.setCh(4,ch4Value);
            hm.setCh(5,ch5Value);
            hm.setCh(6,ch6Value);
            hm.setCh(7,ch7Value);  
            hm.setCh(8,ch8Value);
            hm.setCh(9,ch9Value);
            hm.setCh(10,ch10Value);  
            
            hmDataArray.add(hm);
        }
  
        Object[] obj = hmDataArray.toArray();        
        hmData = new HMData[hmDataArray.size()];
        for(int i = 0; i < obj.length; i++){
        	hmData[i] = (HMData)obj[i];
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
            sb.append("TMTR_MONTH DATA["); 
            for(int i = 0; i < hmData.length; i++){
                sb.append(hmData[i].toString());
            }
            sb.append("]\n");
        }catch(Exception e){
            log.warn("TMTR_MONTH TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }       
}
