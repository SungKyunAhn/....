/** 
 * @(#)BillingData.java       1.0 07/11/12 *
 * 
 * Previous Billing Data Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.lk3410cpTable;

import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class BillingData {

	private final int OFS_SUMMATION=0;
	private final int OFS_DEMAND = 180;
    private final int OFS_CUM_DEMAND = 360;
    private final int OFS_MDEMAND_TIME = 540;
    
    private final int OFS_SUMMATION_KVAR=12;
    private final int OFS_DEMAND_KVAR = 192;
    private final int OFS_CUM_DEMAND_KVAR = 372;
    private final int OFS_MDEMAND_TIME_KVAR = 561;
        
    private final int LEN_SUMMATION=4;
    private final int LEN_DEMAND = 4;
    private final int LEN_CUM_DEMAND = 4;
    private final int LEN_MDEMAND_TIME = 7;

    private final int NBR_TIERS =2;
    private final int CNT_BLOCK =5; //total, a,b,c,d
    private final int CNT_RECEIVED_NBR =9;
    
    private byte[] data;
    private static Log log = LogFactory.getLog(BillingData.class);
    DecimalFormat dformat = new DecimalFormat("###############0.000000");
    
	private TOU_BLOCK[] tou_block;
	        
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public BillingData(byte[] data) {
		this.data = data;

		this.tou_block = new TOU_BLOCK[this.CNT_BLOCK];
		try {
			parseData();
		} catch (Exception e) {
            log.warn("BillingData Parse Error :",e);
		}
	}
	
	public TOU_BLOCK[] getTOU_BLOCK(){
		return this.tou_block;
	}

	private void parseData() throws Exception {
		
        log.debug("===============BillingData Parse Start=================");
		int offset = 0;

		for(int i = 0; i < this.CNT_BLOCK; i++){			
			
			tou_block[i] = new TOU_BLOCK(NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS);
            
            long t = DataFormat.hex2signeddec(DataFormat.LSB2MSB(
                      DataFormat.select(data,OFS_SUMMATION+(i*36),LEN_SUMMATION)));
//            log.debug("summation1 :  "+t);
            double aa = t*0.001;
//            log.debug("summation2 :  "+aa/1000);
//            log.debug("*0.001 : "+ t*0.001);
            Double a = new Double(dformat.format(aa));
   //         log.debug("summation3 :  "+a.doubleValue());
  //                      
            tou_block[i].setSummations(0, a);
                
            Double a1 = new Double(dformat.format((double) (DataFormat.hex2signeddec(DataFormat.LSB2MSB(
                                   DataFormat.select(data,OFS_SUMMATION_KVAR+(i*36),LEN_SUMMATION)))*0.001)));
    //        log.debug("summation_kvar :  "+a1.doubleValue());
            tou_block[i].setSummations(1, a1);
                                                                               
            tou_block[i].setEventTime(0,(new DateTimeFormat(DataFormat.select(data,OFS_MDEMAND_TIME+(i*63),LEN_MDEMAND_TIME))).getDateTime());
            tou_block[i].setEventTime(1,(new DateTimeFormat(DataFormat.select(data,OFS_MDEMAND_TIME_KVAR+(i*63),LEN_MDEMAND_TIME))).getDateTime());
            
            Double b = new Double(dformat.format((double) (DataFormat.hex2signeddec(DataFormat.LSB2MSB(
                                     DataFormat.select(data,OFS_CUM_DEMAND+(i*36),LEN_CUM_DEMAND)))*0.001)));
      //      log.debug("CumDemand :  "+b.doubleValue()); 
            tou_block[i].setCumDemand(0,b);

            Double b1 = new Double(dformat.format((double) (DataFormat.hex2signeddec(DataFormat.LSB2MSB(
                                 DataFormat.select(data,OFS_CUM_DEMAND_KVAR+(i*36),LEN_CUM_DEMAND)))*0.001)));
  //          log.debug("CumDemand_kvar :  "+b1.doubleValue()); 
            tou_block[i].setCumDemand(1,b1);
                                                                              
            Double c = new Double(dformat.format((double) (DataFormat.hex2signeddec(DataFormat.LSB2MSB(
                                     DataFormat.select(data,OFS_DEMAND+(i*36),LEN_DEMAND)))*0.001)));
    //        log.debug("CurrDemand :  "+c.doubleValue());
            tou_block[i].setCurrDemand(0,c);
            Double c1 = new Double(dformat.format((double) (DataFormat.hex2signeddec(DataFormat.LSB2MSB(
            DataFormat.select(data,OFS_DEMAND_KVAR+(i*36),LEN_DEMAND)))*0.001)));
      //      log.debug("CurrDemand_kvar :  "+c1.doubleValue());
            tou_block[i].setCurrDemand(1,c1);
                                                                              
            tou_block[i].setCoincident(0, new Double(0.0));
            tou_block[i].setCoincident(1, new Double(0.0));  
         
		}
        log.debug("=================BillingData Parse End=================");
	}

	private String parseYyyymmddhhmmss(byte[] b, int offset, int len)
						throws Exception {

        int blen = b.length;
        if(blen-offset < 7)
            throw new Exception("YYYYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
        if(len != 7)
            throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+len);
        
        int idx = offset;
        byte[] b1 = new byte[blen];
        b1 = DataFormat.LSB2MSB(b);
        int year = DataFormat.hex2dec(DataFormat.select(b1, idx, 2));
        idx =idx+2;
        int mm = DataFormat.hex2unsigned8(b1[idx++]);
        int dd = DataFormat.hex2unsigned8(b1[idx++]);
        int hh = DataFormat.hex2unsigned8(b1[idx++]);
        int MM = DataFormat.hex2unsigned8(b1[idx++]);
        int ss = DataFormat.hex2unsigned8(b1[idx++]);

        StringBuffer ret = new StringBuffer();
                
        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
        
        log.debug("ofs :"+ offset);
        log.debug("billingData eventTime :"+ ret.toString());
        return ret.toString();
	}
}
