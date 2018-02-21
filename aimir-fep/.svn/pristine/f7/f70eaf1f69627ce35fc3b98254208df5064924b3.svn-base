/** 
 * @(#)LGRW3410_PB.java       1.0 08/03/31 *
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
 
package com.aimir.fep.meter.parser.lgrw3410Table;

import java.text.DecimalFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class LGRW3410_PB {

	private final int LEN_METER_ID=8;
	private final int LEN_METER_STATUS=4;
	private final int LEN_METER_DEMAND_INTERVAL=1;
	private final int LEN_CT_PT=4;
	
	private final int LEN_ENERGY_FORMAT=1;
	private final int LEN_DEMAND_FORMAT=1;
	private final int LEN_BILLDAY=6;
	
	private final int LEN_COUNT_OF_MANUALLY_RECOVERY=1;
	private final int LEN_METER_DATETIME=8;
	
	private final int LEN_SELF_READING_DATETIME=12;
	
	private final int OFS_METER_ID=0;
	private final int OFS_METER_STATUS = 8;
	private final int OFS_METER_DEMAND_INTERVAL = 12;
	private final int OFS_CT_PT = 13;
	
	private final int OFS_ENERGY_FORMAT= 17;
	private final int OFS_DEMAND_FORMAT= 18;
	private final int OFS_BILLDAY= 19;
	
	private final int OFS_COUNT_OF_MANUALLY_RECOVERY= 25;
	private final int OFS_METER_DATETIME= 26;
	
	private final int OFS_SELF_READING_DATETIME= 34;

	private final int OFS_ENERGY_KW = 94;
	private final int OFS_ENERGY_KVAR = 152;
	
	private final int OFS_RATE_A_IN_BILLING = 4;
	private final int OFS_RATE_B_IN_BILLING = 22;
	private final int OFS_RATE_C_IN_BILLING = 40;
	
	private final int LEN_ENERGY = 4;
	private final int LEN_MAX_POWER = 2;
	private final int LEN_CUMM_ENERGY = 4;
	private final int LEN_MAX_POWER_DATETIME = 8;

	private final int LEN_RATE = 18;
	
    private final int NBR_TIERS =2;
    private final int CNT_BLOCK =4; //total, a,b,c
   
    private double KE = 0.05;
    private byte[] data;
    private static Log log = LogFactory.getLog(LGRW3410_PB.class);
    DecimalFormat dformat = new DecimalFormat("###############0.000000");
    
	private TOU_BLOCK[] tou_block;
	        
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public LGRW3410_PB(byte[] data, double ke) {
		this.data = data;
		this.KE = ke;
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

	public MeterManufacture getMeterManufacture() throws Exception {
	        return new MeterManufacture(
	            DataFormat.select(
	            		data,OFS_METER_ID, LEN_METER_ID));
	}
	
	public MeterStatus getMeterSatus() throws Exception {
        return new MeterStatus(
            DataFormat.select(
            		data,OFS_METER_STATUS, LEN_METER_STATUS));
	}
    public int getMETER_DEMAND_INTERVAL() throws Exception {
        return DataFormat.hex2dec(DataFormat.select(
                    data,OFS_METER_DEMAND_INTERVAL,LEN_METER_DEMAND_INTERVAL));
    }
    public int getCT_PT() throws Exception {
        return DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
                    data,OFS_CT_PT,LEN_CT_PT)));
    }
    public int getEnergyFormat() throws Exception {
        return DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
                    data,OFS_ENERGY_FORMAT,LEN_ENERGY_FORMAT)));
    }
    public int getDemandFormat() throws Exception {
        return DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
                    data,OFS_DEMAND_FORMAT,LEN_DEMAND_FORMAT)));
    }
    public String getMeterDateTime() throws Exception {
    	return parseYyyymmddhhmmss2(DataFormat.select(data, OFS_METER_DATETIME, LEN_METER_DATETIME));
    }
    public String geBillingDay() throws Exception {
    	return (parseYyyymmddhhmmss(DataFormat.select(data, OFS_BILLDAY, LEN_BILLDAY))).substring(0,8);
    }
    public String geBillingDay2() throws Exception {
    	return (parseYyyymmddhhmmss(DataFormat.select(data, OFS_BILLDAY, LEN_BILLDAY)));
    }
    public int getCountOfManualRecovery() throws Exception {
        return DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
                    data,OFS_COUNT_OF_MANUALLY_RECOVERY,LEN_COUNT_OF_MANUALLY_RECOVERY)));
    }
	public String getLastSelfReadingDate() throws Exception {
	    //Map map = new LinkedHashMap(16,0.74f,false);
		String datetime = "";
		SelfReadingDateTime time = new SelfReadingDateTime(DataFormat.select(
	            		data,OFS_SELF_READING_DATETIME, LEN_SELF_READING_DATETIME*5));
		datetime = time.getSelfReadingDateTime();

		log.debug("getLastSelfReadingDate() :"+datetime);
        return datetime;
	}
	
	private void parseData() throws Exception {
		
        log.debug("===============BillingData Parse Start=================");

		int ofs_kw =OFS_ENERGY_KW;
		int ofs_kvar =OFS_ENERGY_KVAR;
		
		for(int i = 0; i < this.CNT_BLOCK; i++){			
			
			tou_block[i] = new TOU_BLOCK(NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS);
		}
		for(int i = 0; i < this.CNT_BLOCK; i++){

            Double a = new Double(dformat.format((double) (DataFormat.hex2signeddec(
                    DataFormat.select(data,ofs_kw,LEN_ENERGY)))*KE*0.001));
		
            Double a1 = new Double(dformat.format((double) (DataFormat.hex2signeddec(
                    DataFormat.select(data,ofs_kvar,LEN_ENERGY)))*KE*0.001));

            ofs_kw += LEN_ENERGY;
            ofs_kvar  += LEN_ENERGY;
            
            if(i==0){
            	tou_block[0].setResetTime(getLastSelfReadingDate());
            	tou_block[0].setSummations(0, a);
            	tou_block[0].setSummations(1, a1);
            	tou_block[i].setCumDemand(0, new Double(0.0));
            	tou_block[i].setCumDemand(1, new Double(0.0));
            	tou_block[i].setCurrDemand(0, new Double(0.0));
            	tou_block[i].setCurrDemand(1, new Double(0.0));
            	tou_block[i].setCoincident(0, new Double(0.0));
            	tou_block[i].setCoincident(1, new Double(0.0));
            	tou_block[i].setEventTime(0, new String(""));
	            tou_block[i].setEventTime(1, new String(""));
	            
            }else{
            	int k=i;
            	
            	tou_block[k].setSummations(0, a);
            	tou_block[k].setSummations(1, a1);
            	
	            Double b = new Double(((double)DataFormat.hex2signeddec(
	                                     DataFormat.select(data,ofs_kw,LEN_MAX_POWER)))*KE*0.001);
	
	            tou_block[k].setCurrDemand(0,b);
	
	            Double b1 = new Double(((double)DataFormat.hex2signeddec(
	                                 DataFormat.select(data,ofs_kvar,LEN_MAX_POWER)))*KE*0.001);
	
	            tou_block[k].setCurrDemand(1,b1);
	                                                
	            ofs_kw += LEN_MAX_POWER;
	            ofs_kvar += LEN_MAX_POWER;
	            
	            tou_block[k].setEventTime(0, new String(parseYyyymmddhhmmss(DataFormat.select(data, ofs_kw, LEN_MAX_POWER_DATETIME))));
	            tou_block[k].setEventTime(1, new String(parseYyyymmddhhmmss(DataFormat.select(data, ofs_kvar, LEN_MAX_POWER_DATETIME))));
	            
	            ofs_kw += LEN_MAX_POWER_DATETIME;
	            ofs_kvar += LEN_MAX_POWER_DATETIME;
	            
	            Double c = new Double(((double)DataFormat.hex2signeddec(
	                                     DataFormat.select(data,ofs_kw,LEN_CUMM_ENERGY)))*KE*0.001);
	
	            tou_block[k].setCumDemand(0,c);
	            Double c1 = new Double(((double)DataFormat.hex2signeddec(
	            DataFormat.select(data,ofs_kvar,LEN_CUMM_ENERGY)))*KE*0.001);
	
	            tou_block[k].setCumDemand(1,c1);
	                                 
	            ofs_kw += LEN_CUMM_ENERGY;
	            ofs_kvar += LEN_CUMM_ENERGY;
	            
	            tou_block[k].setCoincident(0, new Double(0.0));
	            tou_block[k].setCoincident(1, new Double(0.0));           
	            
            }
		}
        log.debug("=================BillingData Parse End=================");
	}

	private String parseYyyymmddhhmmss(byte[] b)
						throws Exception {

        int len = b.length;
        if(len != 8 && len != 6)
            throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+len);
        
        log.debug(Util.getHexString(b));
        
        int LEN_YYYY =2;
        if(len==6){
        	LEN_YYYY =1;
        }
        int idx = 0;

        int year = DataFormat.hex2dec(DataFormat.select(b, idx, LEN_YYYY));
        if(len==6){
        	year+=2000;
        }
    	idx +=LEN_YYYY;
        int mm = DataFormat.hex2unsigned8(b[idx++]);
        int dd = DataFormat.hex2unsigned8(b[idx++]);
        int hh = DataFormat.hex2unsigned8(b[idx++]);
        int MM = DataFormat.hex2unsigned8(b[idx++]);
        int ss = DataFormat.hex2unsigned8(b[idx++]);

        StringBuffer ret = new StringBuffer();
                
        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
        
        log.debug(" eventTime :"+ ret.toString());
        return ret.toString();
	}
	
	private String parseYyyymmddhhmmss2(byte[] b) throws Exception {

		int len = b.length;
		if(len != 8 )
		throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+len);
		
		log.debug(Util.getHexString(b));
		 
		int LEN_YYYY =2;
		int idx = 0;

		int year = DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(b, idx, LEN_YYYY)));

		idx +=LEN_YYYY;
		int mm = DataFormat.hex2unsigned8(b[idx++]);
		int dd = DataFormat.hex2unsigned8(b[idx++]);
		int hh = DataFormat.hex2unsigned8(b[idx++]);
		int MM = DataFormat.hex2unsigned8(b[idx++]);
		int ss = DataFormat.hex2unsigned8(b[idx++]);
		
		StringBuffer ret = new StringBuffer();
		
		ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
		
		log.debug(" eventTime :"+ ret.toString());
		return ret.toString();
	}
	
}
