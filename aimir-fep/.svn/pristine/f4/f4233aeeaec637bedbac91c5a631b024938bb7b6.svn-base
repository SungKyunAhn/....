/* 
 * @(#)SCE8711_LP.java       1.0 12/05/17 *
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
 
package com.aimir.fep.meter.parser.actarisSCE8711Table;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.model.device.Meter;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class SCE8711_LP {
    
    private int energyscale;
	private byte[] rawData = null;
    private int[] chanScale= null;
    private int[] chanUnit= null;
    private int cntCH=0;
    private int resolution=5;
    private String meterId="";

    private double ke= 0.0001;    
    
    public static final int OFS_CH_CNT = 0;
    public static final int OFS_CH_INFO = 1;
    public static final int OFS_LP_CH_DATA = 4;
    
    public static final int LEN_CH_CNT = 1;
    public static final int LEN_CH_INFO = 2;
    public static final int LEN_DATETIME_LP_DATA = 12;
    public static final int LEN_DATE_LP_DATA = 5;
    public static final int LEN_TIME_LP_DATA = 7;
    
    public static final int LEN_ERRORSTAT_LP_DATA = 4;
    public static final int LEN_LP_CH_DATA = 2;
    public static final int LEN_NBR_LP_DATA = 2;
    
    public static final int OFS_LP_BLOCK = 3;
       
    DecimalFormat dformat = new DecimalFormat("#0.000000");
        
    private Log log = LogFactory.getLog(SCE8711_LP.class);
    
	/**
	 * Constructor .<p> 
	 * @param data - read data (header,crch,crcl)
	 */
	public SCE8711_LP(byte[] rawData, int resolution, Meter meter) {
		this.rawData = rawData;
		this.resolution = resolution;
	//	this.ke =ke;
        try{
        	this.ke = meter.getPulseConstant();
        }catch(Exception e){
        	log.error(e,e);
        }
	}
    
    /**
     * The time in minutes LP interval duration ST61
     * LP Interval Time
     * Default 15
     * (0,1,2,3,4,5,6,10,12,15,20,30,60)
     * @return
     */

	public int getChannelCnt() throws Exception {
		//cntCH = DataFormat.hex2unsigned16(DataFormat.select(rawData,OFS_CH_CNT,LEN_CH_CNT ));
		cntCH =  DataFormat.hex2unsigned8(rawData[OFS_CH_CNT]);
		return cntCH;
    }
    	
	public void getChannelInfo(byte[] data, int channelCnt) throws Exception {
		
		int ofs =0;
		chanScale = new int[channelCnt];
		chanUnit = new int[channelCnt];
		for(int i=0; i<channelCnt; i++){
			chanScale[i] = DataFormat.hex2signed8(data[ofs]);
			chanUnit[i]  = DataFormat.hex2unsigned8(data[ofs+1]);
			ofs+=2;
		}
		DataFormat.hex2unsigned8(rawData[OFS_CH_CNT]);
    //    return DataFormat.hex2unsigned8(rawData[OFS_CH_CNT]);
    }
	
    public int getNBR_DATE(int ofs) throws Exception {
        return DataFormat.hex2unsigned8(rawData[ofs]);
    }
    
    public int getNBR_LP_DATA(byte[] data) throws Exception {
        return DataFormat.hex2unsigned16(data);
    }
    
    public int getNBR_LP_CHAN(byte cnt) throws Exception {
        return cntCH;
    }
    
    @SuppressWarnings("unchecked")
    public LPData[] parse() throws Exception {
        
        ArrayList<LPData> list = new ArrayList<LPData>();
        getChannelCnt();
        getChannelInfo(DataFormat.select(rawData,OFS_CH_INFO, cntCH*LEN_CH_INFO), cntCH);
        int offset = OFS_CH_INFO+cntCH*LEN_CH_INFO;
        int nbrDATE = getNBR_DATE(offset++);                
        
        for(int j=0; j<nbrDATE; j++){
        	byte[] date = DataFormat.select(rawData, offset, LEN_DATETIME_LP_DATA);
        	offset+=LEN_DATETIME_LP_DATA;
        	int nbrLP = getNBR_LP_DATA(DataFormat.select(rawData, offset,LEN_NBR_LP_DATA));
        	offset+=LEN_NBR_LP_DATA;
        	
	        if(nbrLP>0){
		        
		        int LEN_LP_DATA_SET = cntCH*LEN_LP_CH_DATA;
		        LPData[] interval = new LPData[nbrLP];
		        
		        String entryDateTime = new DLMSDateTime(date).getDateTime();
		        if(entryDateTime.length()==8)
		        	entryDateTime+="000000";
		/*        if(Integer.parseInt(entryDateTime.substring(10,12))%5 !=0){
		        	log.error("lp intervalTime error : "+entryDateTime);
		        	return null;
		        }
		*/      
		        for(int i = 0; i < interval.length; i++){
		            String lpDate=Util.addMinYymmdd(entryDateTime.substring(0,12), resolution*i);
		            LPData lp_blk =null;
		            lp_blk = parseChannel(DataFormat.select(rawData,offset,LEN_LP_DATA_SET), cntCH, lpDate);
		            offset +=LEN_LP_DATA_SET;
		            if(lp_blk!=null)
		            	list.add(lp_blk);
		        }
		  //      Collections.sort(list,LPComparator.TIMESTAMP_ORDER);
	        }
        }
      //  return checkEmptyLP(list);
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
    
    @SuppressWarnings({ "unused", "unchecked" })
    private LPData[] checkEmptyLP(ArrayList<LPData> list) throws Exception
    {
        int interval = this.resolution;
        ArrayList<LPData> emptylist = new ArrayList<LPData>();
        
        int  LEN_CHNS_CONFIG = this.cntCH; 
        Double[] ch  = new Double[LEN_CHNS_CONFIG];
        
        for(int i = 0; i < LEN_CHNS_CONFIG; i++){
            ch[i] = new Double(0.0);
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
                        data.setCh(ch);                        
                        data.setFlag(14);
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
        
 //       Collections.sort(list,LPComparator.TIMESTAMP_ORDER);  
        
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
    private LPData parseChannel(byte[] block, int cntCH, String datetime) throws Exception{

        LPData lpdata = new LPData();
        
        Double[] ch  = new Double[cntCH];

        int flag =0;        
        
        for(int i = 0; i < cntCH; i++){
            ch[i] =  new Double(dformat.format((double)DataFormat.hex2unsigned16( DataFormat.select(
                       block, LEN_LP_CH_DATA*i, LEN_LP_CH_DATA))*Math.pow(10, chanScale[i])));
            log.debug("ch["+i+"]"+ch[i].doubleValue());
        }
        
        lpdata.setCh(ch);
       
      //  lpdata.setForward(new Double(dformat.format(ch[0].doubleValue()*ke*10000*transFactor)));
      //  lpdata.setReverse(new Double(dformat.format(ch[1].doubleValue()*ke*10000*transFactor)));
        lpdata.setFlag(flag);        
        lpdata.setPF(getPF(ch[0].doubleValue(), ch[1].doubleValue()));
        lpdata.setDatetime(datetime);
        
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
    
    private String getDateTime(byte[] date)  throws Exception{
    	int blen = date.length;
		if(blen != 4)
			throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+blen);
		
		int MASK_YEAR 	= 0x00000FFF;
		int MASK_MONTH 	= 0x0000F000;
		int MASK_DAY 	= 0x001F0000;
		int MASK_HOUR 	= 0x03e00000;
		int MASK_MIN 	= 0xFC000000;
		
		int dateInt =(int)DataFormat.hex2unsigned32(date);

		int yy = dateInt & MASK_YEAR;
		int mm = (dateInt & MASK_MONTH) >> 12;
		int dd = (dateInt & MASK_DAY)   >> 16;
		int hh = (dateInt & MASK_HOUR)  >> 21;
		int MM = (dateInt & MASK_MIN)   >> 26;
		int ss = 0;

		StringBuffer ret = new StringBuffer();
				
		ret.append(Util.frontAppendNStr('0',Integer.toString(yy),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
		
		log.debug("getDateTime :"+ret.toString());
		return ret.toString();
    } 
    
}