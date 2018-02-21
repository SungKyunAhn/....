/*
 * @(#)NURI_LP.java       1.0 07/05/02 *
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

package com.aimir.fep.meter.parser.SM300Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class NURI_LP {

    private int energyscale;
	private byte[] rawData = null;
    private int LEN_EXTENDED_INT_STATUS;

    public static final int OFS_MAX_INT_TIME_SET1 = 0;
    public static final int OFS_NBR_CHNS_SET1 = 1;

    public static final int LEN_MAX_INT_TIME_SET1 = 1;
    public static final int LEN_NBR_CHNS_SET1 = 1;

    public static final int LEN_SCALARS_SET1 = 2;
    public static final int LEN_DIVISOR_SET1 = 2;

    public static final int LEN_NBR_LP_BLK = 2;
    public static final int LEN_END_TIME_LP = 5;

    public static final int LEN_INT_DATA = 2;

    private static Log log = LogFactory.getLog(NURI_LP.class);

	/**
	 * Constructor .<p>
	 * @param data - read data (header,crch,crcl)
	 */
	public NURI_LP(byte[] rawData,int energyscale) {
		this.rawData = rawData;
        this.energyscale = energyscale;
	}

    /**
     * The time in minutes LP interval duration ST61
     * LP Interval Time
     * Default 15
     * (0,1,2,3,4,5,6,10,12,15,20,30,60)
     * @return
     */
    public int getINT_TIME_SET1() {
        return DataFormat.hex2unsigned8(rawData[OFS_MAX_INT_TIME_SET1]);
    }

    /**
     * The number of LP channels   ST61
     * LP Channel Size
     * Default 2 (1~20)
     * @return
     */
    public int getNBR_CHNS_SET1() {
        return DataFormat.hex2unsigned8(rawData[OFS_NBR_CHNS_SET1]);
    }

    /**
     * Configuration of LP scalars   ST62
     * @return
     * @throws Exception
     */
    public int[] getSCALAR() throws Exception {

        int chans = getNBR_CHNS_SET1();
        int[] scalar = new int[chans];

        int offset = LEN_MAX_INT_TIME_SET1
                   + LEN_NBR_CHNS_SET1;

        for(int i = 0; i < chans; i++){
            scalar[i] = DataFormat.hex2signed16(
                DataFormat.LSB2MSB(
                    DataFormat.select(rawData,offset,LEN_SCALARS_SET1)));
            offset += LEN_SCALARS_SET1;
        }
        return scalar;
    }

    /**
     * Configuration of LP divisors  ST62
     * @return
     * @throws Exception
     */
    public int[] getDIVISOR() throws Exception {

        int chans = getNBR_CHNS_SET1();
        int[] divisor = new int[chans];

        int offset = LEN_MAX_INT_TIME_SET1
                   + LEN_NBR_CHNS_SET1
                   + LEN_SCALARS_SET1*chans;

        for(int i = 0; i < chans; i++){
            divisor[i] = DataFormat.hex2signed16(
                DataFormat.LSB2MSB(
                    DataFormat.select(rawData,offset,LEN_DIVISOR_SET1)));
            offset += LEN_DIVISOR_SET1;
        }
        return divisor;
    }

    /**
     * Unsigned 16 Number of LP data structure (max 1 day)
     * @return
     * @throws Exception
     */
    public int getNBR_LP_BLK() throws Exception {

        int chans = getNBR_CHNS_SET1();
        int offset = LEN_MAX_INT_TIME_SET1
                   + LEN_NBR_CHNS_SET1
                   + LEN_SCALARS_SET1*chans
                   + LEN_DIVISOR_SET1*chans;

        return DataFormat.hex2unsigned16(
                DataFormat.LSB2MSB(
                    DataFormat.select(
                        rawData,offset,LEN_NBR_LP_BLK )));
    }

    /**
     * Unsigned 40 End date/time of last interval LP data
     * @return
     * @throws Exception
     */
    public String getEND_TIME_LP() throws Exception {

        int chans = getNBR_CHNS_SET1();
        int offset = LEN_MAX_INT_TIME_SET1
                   + LEN_NBR_CHNS_SET1
                   + LEN_SCALARS_SET1*chans
                   + LEN_DIVISOR_SET1*chans
                   + LEN_NBR_LP_BLK;
        String endtime = Util.getQuaterYymmddhhmm(
                             getYymmddhhmm(rawData,offset,LEN_END_TIME_LP),this.getINT_TIME_SET1());
        return endtime;
    }

    @SuppressWarnings("unchecked")
    public LPData[] parse() throws Exception {

        int chans = getNBR_CHNS_SET1();
        this.LEN_EXTENDED_INT_STATUS = (chans / 2) + 1;


        int oneblocksize = LEN_EXTENDED_INT_STATUS
                         + (LEN_INT_DATA*chans);

        log.debug("LEN_EXTENDED_INT_STATUS="+LEN_EXTENDED_INT_STATUS+",BLK_SIZE="+oneblocksize+"CHANSIZE="+chans);

        int offset = LEN_MAX_INT_TIME_SET1
                   + LEN_NBR_CHNS_SET1
                   + LEN_SCALARS_SET1*chans
                   + LEN_DIVISOR_SET1*chans
                   + LEN_NBR_LP_BLK
                   + LEN_END_TIME_LP;

        String endtime = getEND_TIME_LP();

        String starttime = Util.addMinYymmdd(endtime,-getINT_TIME_SET1()*(getNBR_LP_BLK()-1));

        log.debug("ENDTIME="+endtime+",STARTTIME="+starttime+",LPCOUNT="+getNBR_LP_BLK());

        LPData[] interval = new LPData[getNBR_LP_BLK()];

        for(int i = 0; i < getNBR_LP_BLK(); i++){

            byte[] blk = new byte[oneblocksize];

            blk = DataFormat.select(rawData,offset,oneblocksize);
            log.debug(Util.getHexString(blk));
            String lptime = Util.addMinYymmdd(starttime,getINT_TIME_SET1()*i);
            interval[i] = new LPData();
            interval[i] = parseChannel(lptime,blk);
            offset += oneblocksize;
        }

        return interval;
    }

    @SuppressWarnings("unchecked")
    private LPData parseChannel(String lptime, byte[] block) throws Exception{

        LPData lpdata = new LPData();
        int offset = 0;
        byte[] lpstat = DataFormat.select(block,offset,LEN_EXTENDED_INT_STATUS);
        offset += LEN_EXTENDED_INT_STATUS;
        Double[] ch = new Double[getNBR_CHNS_SET1()];
        Double[] v  = new Double[getNBR_CHNS_SET1()];
        Double[] empty_ch = new Double[getNBR_CHNS_SET1()];
        Double[] empty_v  = new Double[getNBR_CHNS_SET1()];

        for(int i = 0; i < getNBR_CHNS_SET1(); i++){
           ch[i] = new Double(getCh(block,offset,LEN_INT_DATA,i));
           v[i] = new Double(getV(block,offset,LEN_INT_DATA,i));
            offset += LEN_INT_DATA;
        }

        for(int i = 0; i < getNBR_CHNS_SET1(); i++){
            empty_ch[i] = new Double(0);
            empty_v[i] = new Double(0);
        }

        if(lpstat[0] == (byte)0xFF && lpstat[1] == (byte)0xFF){
            lpdata.setDatetime(lptime);
            lpdata.setCh(empty_ch);
            lpdata.setV(empty_v);
            lpdata.setFlag(0);
            lpdata.setPF(new Double(getPF(0,0)));
        }else{
            lpdata.setDatetime(lptime);
            lpdata.setCh(ch);
            lpdata.setV(v);
            lpdata.setFlag(0);
            if(getNBR_CHNS_SET1() > 1 ){
                lpdata.setPF(new Double(getPF(ch[0],ch[1])));
            }else {
                lpdata.setPF(new Double(getPF(ch[0],0)));
            }

        }
        return lpdata;
    }

    private String getYymmddhhmm(byte[] b, int offset, int len)
        throws Exception {

        int blen = b.length;
        if(blen-offset < 5) {
			throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
		}
        if(len != 5) {
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);
		}

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


    private double getCh(byte[] b, int start, int len,int ch)
                                            throws Exception{

        int[] scalar = getSCALAR();
        int[] divisor = getDIVISOR();

        int val = DataFormat.hex2dec(
                    DataFormat.LSB2MSB(
                        DataFormat.select(b,start,len)));
        log.debug("val,energyscale,divisor,scalar,realvalue="+val+","+energyscale+","+divisor[ch]+","+scalar[ch]+","+(double)(val*(energyscale*Math.pow(10,-9))*(divisor[ch]/scalar[ch])));

        return (double)(val*(energyscale*Math.pow(10,-9))*(divisor[ch]/scalar[ch]));

    }

    private double getV(byte[] b, int start, int len,int ch)
        throws Exception{

        int[] scalar = getSCALAR();
        int[] divisor = getDIVISOR();

        int val = DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                        DataFormat.select(b,start,len)));

        return (double)(val*(energyscale*Math.pow(10,-9))*(divisor[ch]/scalar[ch]))*60/getINT_TIME_SET1();
    }

    private double getPF(double ch1, double ch2) throws Exception {

        double pf   = (float)(ch1/(Math.sqrt(Math.pow(ch1,2)+Math.pow(ch2,2))));

        if(ch1 == 0.0 && ch2 == 0.0) {
			pf = (double) 1.0;
		}
        /* Parsing Transform Results put Data Class */
        if(pf < 0.0 || pf > 1.0) {
			throw new Exception("BILL PF DATA FORMAT ERROR : "+pf);
		}
        return pf;
     }

}