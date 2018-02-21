/* 
 * @(#)LK3410CP_LP.java       1.0 07/11/12 *
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
 
package com.aimir.fep.meter.parser.lk3410cpTable;

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
public class LK3410CP_LP {
    
    private int energyscale;
	private byte[] rawData = null;
    private int LEN_EXTENDED_INT_STATUS;
    private int regK=1;
    private int resolution=60;
    
    public static final int LEN_CHNS_CONFIG = 4;
    public static final int LEN_INTERVAL_TIME = 1;
    public static final int LEN_NBR_LP_DATA = 2;
    public static final int LEN_LP_DATA_SET = 14;
    
    public static final int OFS_CHNS_CONFIG = 0;
    public static final int OFS_INTERVAL_TIME = 4;
    public static final int OFS_NBR_LP_DATA = 5;
    public static final int OFS_LP_DATA_SET = 7;
       
    DecimalFormat dformat = new DecimalFormat("###############0.000000");
        
    private static Log log = LogFactory.getLog(LK3410CP_LP.class);
    
	/**
	 * Constructor .<p> 
	 * @param data - read data (header,crch,crcl)
	 */
	public LK3410CP_LP(byte[] rawData, int regK) {
		this.rawData = rawData;
        this.regK = regK;
      //  this.energyscale = energyscale;
	}
    
    /**
     * The time in minutes LP interval duration ST61
     * LP Interval Time
     * Default 15
     * (0,1,2,3,4,5,6,10,12,15,20,30,60)
     * @return
     */
    public int getINTERVAL_TIME() {
        return DataFormat.hex2unsigned8(rawData[OFS_INTERVAL_TIME]);
    }
    
    public int getNBR_LP_DATA() throws Exception {
        return DataFormat.hex2unsigned16( DataFormat.LSB2MSB(DataFormat.select(
                    rawData,OFS_NBR_LP_DATA,LEN_NBR_LP_DATA)));
    }
    
    public int[] getCHNS_CONFIG() throws Exception {
        byte CH1_ON = (byte)0x10;
        byte CH2_ON = (byte)0x20;
        byte CH3_ON = (byte)0x40;
        byte CH4_ON = (byte)0x80;
        
        int[] channel_config = new int[4];
        for(int i=0; i<4; i++){
            byte conf = rawData[OFS_NBR_LP_DATA+i];
            log.debug(" raw data "+ i+" :"+ conf);
            int flag1 =(int)((conf&CH1_ON) >> 4);
            if (flag1 ==1 ){
                channel_config[0]= (int) (conf&0x0F);
            }
            int flag2 =(int)((conf&CH2_ON)  >> 5);
            if (flag2 ==1 ){
               channel_config[1]= (int) (conf&0x0F);
            }
            int flag3 =(int)((conf&CH3_ON) >> 6);
            if (flag3 ==1 ){
               channel_config[2]= (int) (conf&0x0F);
            }
            int flag4 =(int)((conf&CH4_ON) >> 7);
            if (flag4 ==1 ){
               channel_config[3]= (int) (conf&0x0F);
            }
        }
  
        return channel_config;
    }
    
    public String getChannelMap() throws Exception{
        StringBuffer res = new StringBuffer();
        int[] chlist = getCHNS_CONFIG();
        for(int i=0; i<4; i++){
            if(chlist[i]==1){
                res.append("ch"+(i+1)+"=Active Energy[kWh],v"+(i+1)+"Active Power[kW]");
            }else if(chlist[i]==4){
                res.append("ch"+(i+1)+"=Lag Reactive Energy[kVarh],v"+(i+1)+"Lag Reactive Power[kVar]");
            }else if(chlist[i]==5){
                res.append("ch"+(i+1)+"=Lead Reactive Energy[kVarh],v"+(i+1)+"Lead Reactive Power[kVar],");
            }else if(chlist[i]==9){
                res.append("ch"+(i+1)+"=Phase Energy[kVah],v"+(i+1)+"Phase Power[kVa]");
            }
            if(i!=3)
                res.append(',');
        }
        return res.toString();
    }
        
    @SuppressWarnings("unchecked")
    public LPData[] parse() throws Exception {
        
       // this.LEN_EXTENDED_INT_STATUS = (LEN_NBR_CHNS_SET1 / 2) + 1;
        ArrayList<LPData> list = new ArrayList<LPData>();
        LPData[] interval = new LPData[getNBR_LP_DATA()];
        log.debug("interval.length ="+interval.length);
        resolution = getINTERVAL_TIME();
        for(int i = 0; i < interval.length; i++){
            
            byte[] blk = new byte[LEN_LP_DATA_SET];
            
            blk =DataFormat.select(rawData,OFS_LP_DATA_SET+(i*LEN_LP_DATA_SET),LEN_LP_DATA_SET);
            list.add(parseChannel(blk));       
        }
        
        Collections.sort(list,LPComparator.TIMESTAMP_ORDER);        
        return checkEmptyLP(list);
    }  
    
    @SuppressWarnings({ "unused", "unchecked" })
    private LPData[] checkEmptyLP(ArrayList<LPData> list) throws Exception
    {
        int interval = getINTERVAL_TIME();
        ArrayList<LPData> emptylist = new ArrayList<LPData>();
        Double[] ch  = new Double[LEN_CHNS_CONFIG];
        Double[] v  = new Double[LEN_CHNS_CONFIG];
        
        for(int i = 0; i < LEN_CHNS_CONFIG; i++){
            ch[i] = new Double(0.0);
            v[i] = new Double(0.0);
        }
        
        String prevTime = "";
        String currentTime = "";

        Iterator it = list.iterator();
        while(it.hasNext()){
            currentTime = ((LPData)it.next()).getDatetime();

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
        
        Iterator it2 = emptylist.iterator();
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
    
    @SuppressWarnings("unchecked")
    private LPData parseChannel(byte[] block) throws Exception{
        
        int OFS_INTERVAL_TIME = 10;
        int LEN_INTERVAL_TIME = 4;
        
        LPData lpdata = new LPData();
        
        Double[] ch  = new Double[LEN_CHNS_CONFIG];
        Double[] v  = new Double[LEN_CHNS_CONFIG];
        
        for(int i = 0; i < LEN_CHNS_CONFIG; i++){
            v[i] =  new Double(dformat.format((double)DataFormat.hex2dec( DataFormat.select(
                       block,2*i,2))/regK));
            ch[i] = new Double(dformat.format(v[i]*((double)resolution/60)));
        }
        
        lpdata.setV(v);
        lpdata.setCh(ch);
        lpdata.setFlag(0);
       // lpdata.setPF(0.0);
        lpdata.setPF(getPF(ch[0], ch[1]));
        lpdata.setDatetime(new LPIntervalTime(DataFormat.select(
                            block,OFS_INTERVAL_TIME, LEN_INTERVAL_TIME)).getIntervalTime());
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