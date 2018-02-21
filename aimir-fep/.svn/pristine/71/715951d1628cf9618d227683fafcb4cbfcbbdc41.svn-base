/*
 * @(#)ST064.java       1.0 06/12/14 *
 *
 * Load Profile.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.meter.parser.SM110Table;

import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.fep.meter.data.ChannelInfo;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class ST064 implements java.io.Serializable {

	private static final long serialVersionUID = 558914077741912672L;

	private int totpulsecnt;

    private int NBR_BLKS_SET1;
    private int NBR_BLK_INTS_SET1;
    private int NBR_CHANS_SET1;
    private int INT_TIME_SET1;

    private int energyscale;
    private int powerscale;
    private int displayscale;
    private int dispmult;
    private ST062 st062 = null;
    private ST063 st063 = null;
    private ST012 st012 = null;

    private final int LEN_BLK_END_TIME = 5;
    private int LEN_END_READINGS;
    private int LEN_NBR_INTERVAL_RECORD;
    private int LEN_EXT_INTERVAL_STATUS;
    private int LEN_INTERVAL_DATA;

    private byte[] data;
    private String lpstarttime;
    private String lpendtime;
    private String blockendtime = null;
    private int emptylpcount;
    private LPData[] lpdata;
    private String meterId;

    private static Log log = LogFactory.getLog(ST064.class);

    public ST064() {}

    /**
     * Constructor .<p>
     *
     * @param data - read data (header,crch,crcl)
     */
    public ST064(byte[] data,
                    String meterId,
                    int nbr_blks_set1,
                    int nbr_blk_ints_set1,
                    int nbr_chans_set1,
                    int int_time_set1,
                    int energyscale,
                    int powerscale,
                    int displayscale,
                    int dispmult,
                    ST063 st063,
                    ST062 st062,
                    ST012 st012) {
        this.data = data;
        this.NBR_BLKS_SET1     = nbr_blks_set1;
        this.NBR_BLK_INTS_SET1 = nbr_blk_ints_set1;
        this.NBR_CHANS_SET1    = nbr_chans_set1;
        this.INT_TIME_SET1     = int_time_set1;
        this.energyscale = energyscale;
        this.powerscale = powerscale;
        this.displayscale = displayscale;
        this.dispmult = dispmult;
        this.st062 = st062;
        this.st063 = st063;
        this.st012 = st012;
        this.meterId = meterId;

        try{
        	log.debug("NBR_BLKS_SET1="+NBR_BLKS_SET1);
        	log.debug("NBR_BLK_INTS_SET1="+NBR_BLK_INTS_SET1);
        	log.debug("NBR_CHANS_SET1="+NBR_CHANS_SET1);
        	log.debug("INT_TIME_SET1="+INT_TIME_SET1);
            log.debug("scalar0=>"+st062.getSCALAR(0)+" scalar1=>"+st062.getSCALAR(1));
            log.debug("divisor0=>"+st062.getDIVISOR(0)+" divisor1=>"+st062.getDIVISOR(1));
            log.debug("Block Order=>"+getBlockOrderString()+", Interval Order=>"+getIntervalOrderString());
            log.debug("Is LP Active Mode=>"+st063.isActiveMode());
        }catch(Exception e){log.warn(e);}

        this.LEN_END_READINGS = nbr_chans_set1*6;
        this.LEN_NBR_INTERVAL_RECORD = nbr_blk_ints_set1*(1+nbr_chans_set1*5/2);
        this.LEN_EXT_INTERVAL_STATUS = 1+(nbr_chans_set1/2);
        this.LEN_INTERVAL_DATA = nbr_chans_set1*2;
        
    	log.debug("LEN_END_READINGS="+LEN_END_READINGS);
    	log.debug("LEN_NBR_INTERVAL_RECORD="+LEN_NBR_INTERVAL_RECORD);
    	log.debug("LEN_EXT_INTERVAL_STATUS="+LEN_EXT_INTERVAL_STATUS);
    	log.debug("LEN_INTERVAL_DATA="+LEN_INTERVAL_DATA);

        try{
            parseLP();
        }catch(Exception e){
            log.warn("parse LP Failed=>"+e);
        }
    }

	public LPData[] getLPData()
    {
        return this.lpdata;
    }

    public int getTotpulseCount()
    {
        if(lpdata != null)
            return this.lpdata.length;
        else
            return 0;
    }

    public void parseLP()
    {
        int empty_blk_cnt = 0;

        int oneblocksize = LEN_BLK_END_TIME
                         + LEN_END_READINGS
                         + this.LEN_NBR_INTERVAL_RECORD;

        log.debug("datalen:"+data.length+ " oneblksize=>"+oneblocksize);
        int datalen = data.length;
        int block_count = data.length/oneblocksize;
        log.debug("blkcnt:"+block_count);
        int lpcount = (block_count)*this.NBR_BLK_INTS_SET1;

        ArrayList[] lists = new ArrayList[block_count];

        String endtime = new String();

        byte[] lp = null;
        byte[] endReadings = null;
        int x = 0;

        try{
            int offset = 0;

            for(int i = 0; i < block_count; i++){

                byte[] blk = new byte[oneblocksize];
                blk = DataFormat.select(data,offset,oneblocksize);

                endtime = Util.getQuaterYymmddhhmm(
                            getYymmddhhmm(blk,0,this.LEN_BLK_END_TIME),this.INT_TIME_SET1);
                endtime = Util.addMinYymmdd(endtime, -this.INT_TIME_SET1);
                // 미터에서 LP시간이 00시00분부터 00시15분 사이의 데이터는 00시 15분으로 저장하기 때문에
                //서버에서는 해당 기간의 사용데이터는 00시 00분으로 계산하므로 주기만큼 빼야함.
                
                endReadings = DataFormat.select(blk,
                		this.LEN_BLK_END_TIME,
                		LEN_END_READINGS);                

                lp = DataFormat.select(blk,
                                        this.LEN_BLK_END_TIME+LEN_END_READINGS,
                                        this.NBR_BLK_INTS_SET1*(this.LEN_EXT_INTERVAL_STATUS+this.LEN_INTERVAL_DATA));
                log.debug("["+meterId+"] METER LP BLK=["+i+"] "+",ENDTIME=["+endtime+"], End Reading["+Hex.decode(endReadings)+"]"+Util.getHexString(lp));
                lists[x] = new ArrayList();
                lists[x].add(0,new Integer(this.NBR_BLK_INTS_SET1));
                lists[x].add(1,endtime);
                lists[x].add(2,lp);
                lists[x].add(3,endReadings);
                x++;

                offset += oneblocksize;

            }

            ArrayList biglst = new ArrayList();
            int total_lp_cnt = 0;
            for(int i = 0; i < lists.length; i++){
                log.debug("parselpcn=>"+i);
                LPData[] lst = parseLP(lists[i]);
                    if(lst != null){
                        biglst.add(i,lst);
                        total_lp_cnt += lst.length;
                }
            }

            LPData[] lplist = new LPData[total_lp_cnt];
            int idx = 0;
            for(int i = 0; i < biglst.size(); i++){
                LPData[] temp = (LPData[])biglst.get(i);
                int j = 0;
                while(j < temp.length){
                    lplist[idx++] = temp[j++];
                }
            }

            this.lpdata = new LPData[total_lp_cnt];
            this.lpdata = lplist;
            //this.lpdata = checkEmptyLP(lplist);

        }catch(Exception e){
            log.warn(e);
        }
    }

    /*
    protected ArrayList checkEmptyLP(ArrayList[] lists) throws Exception {

        int len = lists.length;

        ArrayList newlist = new ArrayList();
        ArrayList emptytime = new ArrayList();
        String prevtime = new String();

        try{
            int idx = 0;
            for(int i = 0; i < len; i++){
                String nowtime = (String)lists[i].get(0);
                if(i == 0){
                    newlist.add(idx++,lists[i]);
                }else {

                    int emptycnt = getEmptyCount(prevtime,nowtime);
                    int chn = 0;
                    String time = prevtime;
                    for(int j = 0; j < emptycnt; j++){
                        time = Util.addMinYymmdd(time,this.INT_TIME_SET1);
                        ArrayList temp = new ArrayList();
                        temp.add(chn++,time);
                        for(int k = 0; k < this.NBR_CHANS_SET1; k++){
                            temp.add(chn++,new Double(0));
                            temp.add(chn++,new Double(0));
                        }

                        temp.add(chn++,new Integer(0));
                        temp.add(chn++,new Double(0));
                        newlist.add(idx++,temp);
                    }
                    newlist.add(idx++,lists[i]);
                }
                prevtime = nowtime;
            }

            return newlist;
        }catch(Exception e){
            throw new Exception("check empty lp : "+e);
        }

    }
    */


    protected LPData[] orderby(LPData[] lists)
    {

        int size = lists.length;
        LPData[] newlists = new LPData[size];

        for(int i = 0; i < size; i++){
            newlists[i] = lists[size-i-1];
        }
        return newlists;

    }

    protected LPData[] orderby(LPData[] lists, int emptycnt)
    {

        int size = lists.length-emptycnt;
        LPData[] newlists = new LPData[size];

        for(int i = 0; i < size; i++){
            newlists[i] = lists[size-i-1];
        }
        return newlists;

    }

    protected LPData[] parseLP(ArrayList list)
        throws Exception
    {
        LPData[] interval = null;
        LPData[] ordered = null;
        try{
            Integer lpcount = (Integer)list.get(0);
            String endtime  = (String) list.get(1);
            byte[] lps      = (byte[]) list.get(2);
            byte[] endReadings = (byte[]) list.get(3);
            int emptycnt    = 0;

            interval = new LPData[lpcount.intValue()];
            ordered  = new LPData[lpcount.intValue()];

            int ofs = lps.length - (this.LEN_EXT_INTERVAL_STATUS+this.LEN_INTERVAL_DATA);
            int idx = 0;
            log.debug("["+meterId+"] meter lpcount->"+lpcount.intValue());
            int timecnt = 0;

            Double[] ch = new Double[this.NBR_CHANS_SET1];
            Double[] v = new Double[this.NBR_CHANS_SET1];
            boolean doStart = false;
            Double endReadValue = 0d;//length 6 per channel
            endReadValue = Double.valueOf(getEndReadingValue(endReadings,0,6,0));            
            
            log.debug("EndReading 1 channel=["+endReadValue+"]"+", RAWDATA=["+Hex.decode(endReadings)+"]");
            Double lpValue = endReadValue;

            for(int i = 0; i < lpcount.intValue(); i++){

                ch= new Double[this.NBR_CHANS_SET1];
                v = new Double[this.NBR_CHANS_SET1];

                byte[] lpstat = DataFormat.select(lps,ofs,this.LEN_EXT_INTERVAL_STATUS);
                ofs += this.LEN_EXT_INTERVAL_STATUS;

                for(int k = 0; k < this.NBR_CHANS_SET1;k++){
                    ch[k] = new Double(getCh(lps,ofs,this.LEN_INTERVAL_DATA/this.NBR_CHANS_SET1,k));
                    v[k] = new Double(getV(lps,ofs,this.LEN_INTERVAL_DATA/this.NBR_CHANS_SET1,k));
                    ofs += this.LEN_INTERVAL_DATA/this.NBR_CHANS_SET1;
                }

                double ch1 = 0;
                double v1 = 0;
                double ch2 = 0;
                double v2 = 0;

                if(this.NBR_CHANS_SET1 >=2 )
                {
                    ch1 = ch[0].doubleValue();
                    v1 = v[0].doubleValue();
                    ch2 = ch[1].doubleValue();
                    v2 = v[1].doubleValue();
                }
                else if(this.NBR_CHANS_SET1 == 1)
                {
                    ch1 = ch[0].doubleValue();
                    v1 = v[0].doubleValue();
                }

                boolean isPowerfail = false;
                for(int n = 0; n < lpstat.length;n++){
                    if(lpstat[n] == (byte)0xFF)
                    {
                        isPowerfail = true;
                    }
                    else
                    {
                        isPowerfail = false;
                    }
                }
                
                int chn = 0;
                if(isPowerfail)
                {
                    if(doStart){
                        String lptime = Util.addMinYymmdd(endtime,-this.INT_TIME_SET1*timecnt);
                        //log.debug(lptime + " timecnt="+timecnt+" metering fail   ch=" + ch[0] + " v=" +v[0] + " flag=" + Hex.getHexDump(lpstat));
                        interval[idx] = new LPData();
                        interval[idx].setDatetime(lptime);
                        interval[idx].setCh(new Double[]{0d});
                        interval[idx].setV(new Double[]{0d});
                        interval[idx].setFlag(-1);
                        interval[idx].setPF(new Double(0d));
                        interval[idx].setLp(lpValue);
                        interval[idx].setLpValue(lpValue);
                        interval[idx].setBaseValue(lpValue);
                        idx++;
                        timecnt++;
                        doStart = true;
                    } else {
                        emptycnt++;
                    }
                }
                else
                {
                    lpValue = lpValue - ch1;
                    String lptime = Util.addMinYymmdd(endtime,-this.INT_TIME_SET1*timecnt);
                    //log.debug(lptime + " timecnt="+timecnt+" metering correct   ch=" + ch[0] + " v=" +v[0] + " flag=" + Hex.getHexDump(lpstat));
                    interval[idx] = new LPData();
                    interval[idx].setDatetime(lptime);
                    interval[idx].setCh(ch);
                    interval[idx].setV(v);
                    interval[idx].setFlag(0);
                    interval[idx].setPF(new Double(getPF(ch1,ch2)));
                    interval[idx].setLp(lpValue);
                    interval[idx].setLpValue(lpValue);
                    interval[idx].setBaseValue(lpValue);
                    //log.debug("["+meterId+"] lpdata=>"+interval[idx].toString());
                    idx++;
                    timecnt++;
                    doStart = true;
                }
                ofs = lps.length - (i+2)*(this.LEN_EXT_INTERVAL_STATUS+this.LEN_INTERVAL_DATA);
                ch = null;
                v= null;
            }
            log.debug("Lp Last Value=["+endReadValue+"]"+", Lp First Value=["+lpValue+"]");
            //log.debug("before orderby emptycnt=>"+emptycnt);
            ordered = orderby(interval,emptycnt);
        }catch(Exception e){
            log.warn("parselp err->", e);
        }      

        //log.debug("after orderby");
        return ordered;

    }

    public String getEndTime()
    {
        try
        {
            return this.lpendtime;
        }
        catch (Exception e)
        {
            log.warn(e.getMessage());
        }
        return null;
    }

    public String getStartTime()
    {
        try
        {
            return this.lpstarttime;
        }
        catch (Exception e)
        {
            log.warn(e.getMessage());
        }
        return null;
    }
    

    /**
     * LP Channel data parsing.<p>
     * @param s
     * @param len
     * @return
     */
    private double getEndReadingValue(byte[] b, int start, int len,int ch)
                                            throws Exception{
        
        long val = DataFormat.hex2signeddec(
                DataFormat.LSB2MSB(
                		DataFormat.select(b,start,len)));
        return new Double(val*energyscale*dispmult/Math.pow(10,(9+displayscale)));
    }

    /**
     * LP Channel data parsing.<p>
     * @param s
     * @param len
     * @return
     */
    private double getCh(byte[] b, int start, int len,int ch)
                                            throws Exception{
        int val = DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                    DataFormat.select(b,start,len)));        

        ChannelInfo chInfo = getLPChannel(ch);
        //if(chInfo != null){
        //    log.debug("ChannelInfo="+chInfo.getChannelCode()+","+chInfo.getChannelName()+","+chInfo.getUnit());
        //}

        if(chInfo != null && (chInfo.getUnit().equals("V"))){
        	return (val*0.1);
        }else{
        	return (val*(energyscale*Math.pow(10,-9))*(st062.getDIVISOR(ch)/st062.getSCALAR(ch)));
        }
    }

    private double getV(byte[] b, int start, int len,int ch)
        throws Exception{

        int val = DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                        DataFormat.select(b,start,len)));
        ChannelInfo chInfo = getLPChannel(ch);
        if(chInfo != null && (chInfo.getUnit().equals("V"))){
        	return (val*0.1);
        }else{
        	return (val*(energyscale*Math.pow(10,-9))*(st062.getDIVISOR(ch)/st062.getSCALAR(ch)))*60/this.INT_TIME_SET1;
        }        
    }

    public double getPF(double ch1, double ch2) throws Exception {

        double pf   = (float)(ch1/(Math.sqrt(Math.pow(ch1,2)+Math.pow(ch2,2))));

        if(ch1 == 0.0 && ch2 == 0.0)
            pf = 1.0;
        /* Parsing Transform Results put Data Class */
        if(pf < 0.0 || pf > 1.0)
            throw new Exception("BILL PF DATA FORMAT ERROR : "+pf);
        return pf;
    }

    private String getYymmddhhmm(byte[] b, int offset, int len)
                            throws Exception {

        int blen = b.length;
        if(blen-offset < 5)
            throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
        if(len != 5)
            throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);

        int idx = offset;

        int yy = DataFormat.hex2unsigned8(b[idx++]);
        int mm = DataFormat.hex2unsigned8(b[idx++]);
        int dd = DataFormat.hex2unsigned8(b[idx++]);
        int hh = DataFormat.hex2unsigned8(b[idx++]);
        int MM = DataFormat.hex2unsigned8(b[idx++]);

        StringBuffer ret = new StringBuffer();

        int currcen = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;

        int year   = yy;
        if(year != 0){
            year = yy + currcen;
        }

        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));

        return ret.toString();

    }


    private String getQuaterYymmddhhmm(String yymmddHHMM) throws Exception {

        String dateString = "";

        try{
            if(yymmddHHMM == null || yymmddHHMM.equals(""))
                throw new Exception("INPUT NULL");
            if(yymmddHHMM.length() < 12)
                throw new Exception("Length Error");

            int yy  = Integer.parseInt(yymmddHHMM.substring(0,4));
            int mm  = Integer.parseInt(yymmddHHMM.substring(4,6));
            int day = Integer.parseInt(yymmddHHMM.substring(6,8));
            int HH  = Integer.parseInt(yymmddHHMM.substring(8,10));
            int MM  = Integer.parseInt(yymmddHHMM.substring(10,12));

            if(((MM !=0) && (MM%this.INT_TIME_SET1!=0))){
                MM = (MM) - (MM)%this.INT_TIME_SET1;
            }

            dateString =  "" + yy +
            (mm < 10? "0" + mm:"" + mm) +
            (day < 10? "0" + day:"" + day) +
            (HH < 10? "0" + HH: "" + HH) +
            (MM < 10? "0" + MM: "" + MM);

        }catch(NumberFormatException e){
            throw new Exception("Util.getQuaterYymmdd() : "+e.getMessage());
        }
        return dateString;

    }

    private int getEmptyCount(String time1, String time2) {

        int ret = 0;
        long sec = 0;
        try {
            sec = Util.getDuration(time1,time2);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        ret = (int)(sec/this.INT_TIME_SET1);
        if(ret < 0) ret = 0;
        return  ret;
    }
    
    public ChannelInfo getLPChannel(int channel){

        try{
            UNIT_OF_MTR unit_of_mtr = new UNIT_OF_MTR();
            if(st062 != null && st012!= null){
                int[] sel_select = st062.getLP_SEL_SET1();
                String[] uom_code = st012.getUOM_CODE(sel_select);
                
                //for(int i = 0; uom_code !=null && i < uom_code.length; i++){
                //	log.debug("uom_code by channel="+uom_code[i]+","+channel);
                //}
                ChannelInfo[] info = unit_of_mtr.getChannelInfos(uom_code);
                return info[channel];
            }
        }catch(Exception e){
            log.warn(e);
        }
        return null;
    }
    
    public String getBlockOrderString(){
    	
    	if(st063 != null){
        	if(st063.isBlockOrderDescending()){
        		return "Decending";
        	}else{
        		return "Acending";
        	}
    	}else{
    		return "Data is null";
    	}

    }
    
    public String getIntervalOrderString(){
    	if(st063 != null){
        	if(st063.isIntervalOrderDescending()){
        		return "Decending";
        	}else{
        		return "Acending";
        	}
    	}else{
    		return "Data is null";
    	}
    }

}
