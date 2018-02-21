/* 
 * @(#)DLMSKepco_LP.java       1.0 08/04/17 *
 * 
 * Load Profile.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.DLMSKepcoTable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class DLMSKepco_LP implements java.io.Serializable{

	private static final long serialVersionUID = -2475871613076018709L;
	private byte[] rawData = null;
    private int regK=1;
    private int resolution=15;
    private int ctvt=1;
    
    public static final int LEN_CNT_TOTAL_LP = 4;
    public static final int LEN_CNT_GET_LP = 2;
        
    public static final int LEN_LP_DATA =12;
    
    public static final int LEN_PULSE = 2;
    public static final int LEN_LP_DATE = 7;
    public static final int LEN_LP_ERROR_STATUS = 1;
    
    
    public static final int OFS_CNT_TOTAL_LP = 0;
    public static final int OFS_CNT_GET_LP = 4;
    
    public static final int OFS_LP_DATA = 6;
    
    public static final int OFS_PULSE = 0;
    public static final int OFS_LP_DATE = 4;
        
    DecimalFormat dformat = new DecimalFormat("###############0.000000");
        
    private static Log log = LogFactory.getLog(DLMSKepco_LP.class);
    
	/**
	 * Constructor .<p> 
	 * @param data - read data (header,crch,crcl)
	 */
	public DLMSKepco_LP(byte[] rawData, int regK, int resolution, int ctvt) {
		this.rawData = rawData;
        this.regK = regK;
        this.resolution = resolution;
        this.ctvt = ctvt;
      //  this.energyscale = energyscale;
	}
    
    /**
     * The time in minutes LP interval duration ST61
     * LP Interval Time
     * Default 15
     * (0,1,2,3,4,5,6,10,12,15,20,30,60)
     * @return
     */

    public int getNBR_LP_DATA() throws Exception {
        //return DataFormat.hex2dec(DataFormat.select(
        //            rawData,OFS_CNT_GET_LP,LEN_CNT_GET_LP));
    	int interval = 0;
    	interval = (rawData.length - (LEN_CNT_TOTAL_LP-LEN_CNT_GET_LP))/LEN_LP_DATA;
    	return interval;
    }
    
    public String getChannelMap() throws Exception{
        return "ch1=Active Energy[kWh],v=Active Power[kW],ch2=Lag Reactive Energy[kVarh],v=Lag Reactive Power[kVar]";
    }
        
    public LPData[] parse() throws Exception {

        ArrayList<LPData> list = new ArrayList<LPData>();
        LPData[] interval = new LPData[getNBR_LP_DATA()];
        log.debug("interval.length ="+interval.length);
        for(int i = 0; i < interval.length; i++){
            
            byte[] blk = new byte[LEN_LP_DATA];
            
            blk =DataFormat.select(rawData,OFS_LP_DATA+(i*LEN_LP_DATA),LEN_LP_DATA);
            list.add(parseChannel(blk));       
        }
        
        Collections.sort(list,LPComparator.TIMESTAMP_ORDER);        
        return checkEmptyLP(list);
    }  
    
    private LPData[] checkEmptyLP(ArrayList<LPData> list) throws Exception
    {
        int interval = resolution;
        ArrayList<LPData> emptylist = new ArrayList<LPData>();
        Double[] ch  = new Double[2];
        Double[] v  = new Double[2];
        
        for(int i = 0; i < 2; i++){
            ch[i] = new Double(0.0);
            v[i] = new Double(0.0);
        }
        
        String prevTime = "";
        String currentTime = "";

        Iterator<LPData> it = list.iterator();
        while(it.hasNext()){
            currentTime = ((LPData)it.next()).getDatetime();
            log.debug("currentTime : "+ currentTime);
            if(prevTime != null && !prevTime.equals("")){
                String temp = Util.addMinYymmdd(prevTime, interval);
                if(!temp.equals(currentTime))
                {
                    int diffMin = (int) ((Util.getMilliTimes(prevTime+"00")-
                    Util.getMilliTimes(currentTime+"00"))/1000/60);
                    
                    for(int i = 0; i < (diffMin/interval) ; i++){
                        LPData data = new LPData();
                        data.setV(v);
                        data.setCh(ch);
                        data.setFlag(0);
                        data.setPF(1.0);
                        data.setDatetime(Util.addMinYymmdd(prevTime, interval*(i+1)));
                        emptylist.add(data);
                    }                
                }
            }
            prevTime = currentTime;

        }
        
        Iterator<LPData> it2 = emptylist.iterator();
        while(it2.hasNext()){
            list.add((LPData)it2.next());
        }
        
        Collections.sort(list,LPComparator.TIMESTAMP_ORDER);  
        
        if(list != null && list.size() > 0){
            LPData[] data = null;
            Object[] obj = list.toArray();            
            data = new LPData[obj.length];
            for(int i = 0; i < obj.length; i++){
                data[i] = (LPData)obj[i];
            }
            return data;
        }
        else
        {
            return null;
        }
    }

    private LPData parseChannel(byte[] block) throws Exception{

        LPData lpdata = new LPData();
        
        Double[] ch  = new Double[2];
        Double[] v  = new Double[2];
        
        for(int i = 0; i < 2; i++){
        	
            ch[i] =  new Double(dformat.format((double) DataFormat.hex2dec( DataFormat.select(
                    block,OFS_PULSE+(i*LEN_PULSE),LEN_PULSE)) *(1.0/regK)*ctvt));
            v[i] = new Double(dformat.format(ch[i]*((double)60/resolution)));
        }
        
        lpdata.setV(v);
        lpdata.setCh(ch);
        lpdata.setFlag(0);
        lpdata.setPF(getPF(ch[0], ch[1]));
        lpdata.setDatetime(new DateTimeFormat(DataFormat.select(
                            block,OFS_LP_DATE, LEN_LP_DATE)).getDateTime());
        return lpdata;
    }
    
    private double getPF(double ch1, double ch2) throws Exception {

        double pf   = (float)(ch1/(Math.sqrt(Math.pow(ch1,2)+Math.pow(ch2,2))));

        if(ch1 == 0.0 && ch2 == 0.0)
            pf = (double) 1.0;
        /* Parsing Transform Results put Data Class */
        if(pf < 0.0 || pf > 1.0)
            throw new Exception("BILL PF DATA FORMAT ERROR : "+pf);
        return pf;
     }
}