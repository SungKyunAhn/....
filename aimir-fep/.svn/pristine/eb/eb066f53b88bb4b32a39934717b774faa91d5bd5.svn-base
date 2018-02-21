/** 
 * @(#)BillingData.java       1.0 08/10/22 *
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
 
package com.aimir.fep.meter.parser.a2rlTable;

import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class BillingData {

	private final int OFS_NBR_OF_RESET= 0;
	private final int OFS_RESET_DATETIME= 1;
	
	private final int LEN_NBR_OF_RESET= 1;
	private final int LEN_RESET_DATETIME= 3;
		
	private final int OFS_SUMMATION_TOTAL= 0;
	
	private final int OFS_SUMMATION= 14;
	private final int OFS_DEMAND = 56;
	private final int OFS_MDEMAND_TIME = 59;
	private final int OFS_CUM_DEMAND = 104;
    
	private final int OFS_SUMMATION_TOTAL_KVAR= 7;
	
    private final int OFS_SUMMATION_KVAR=35;
    private final int OFS_DEMAND_KVAR = 80;
    private final int OFS_MDEMAND_TIME_KVAR = 83;
    private final int OFS_CUM_DEMAND_KVAR = 113;
            
    private final int LEN_SUMMATION=7;
    private final int LEN_DEMAND = 3;
    private final int LEN_MDEMAND_TIME = 5;
    private final int LEN_CUM_DEMAND = 3;
    
    private final int NBR_TIERS =2;
    private final int CNT_BLOCK =4; //total, a,b,c
    private final int CNT_RECEIVED_NBR =9;
        
    private byte[] data;
    private byte[] resetDate;
    private Log log = LogFactory.getLog(BillingData.class);
    DecimalFormat dformat = new DecimalFormat("###############0.000000");
    
	private TOU_BLOCK[] tou_block;
	        
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public BillingData(byte[] data, String type) {
		if(type.equals("P")){
			this.data = new byte[data.length-4];
			System.arraycopy(data, 4, this.data, 0, data.length-4);
			this.resetDate = new byte[4];
			System.arraycopy(data, 0, this.resetDate, 0, 4);
		}else
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
		int resetCount = 0;
		for(int i = 0; i < this.CNT_BLOCK; i++){	
						
			tou_block[i] = new TOU_BLOCK(NBR_TIERS, 
                    NBR_TIERS, 
                    NBR_TIERS, 
                    NBR_TIERS, 
                    NBR_TIERS);
			
			if(i==0 && resetDate!=null && resetDate.length>0){
				resetCount = getTotalDemandCnt().intValue();
				if (resetCount<1){
					tou_block = null;
					break;
				}
				else{
			        tou_block[0].setResetTime(getDemandTime(DataFormat.select(resetDate,OFS_RESET_DATETIME,LEN_RESET_DATETIME))+"000000");
			        tou_block[0].setResetCount(resetCount);
			        log.debug("resetTime :"+tou_block[0].getResetTime());
			        log.debug("resetCount :"+resetCount);
				}
	        }						
							
			if(i==0){
	
	            Double a = new Double((double)(DataFormat.bcd2long(data,OFS_SUMMATION_TOTAL,LEN_SUMMATION))*0.000001);
	            tou_block[i].setSummations(0, a);
	                
	            Double a1 = new Double((double)(DataFormat.bcd2long(data,OFS_SUMMATION_TOTAL_KVAR,LEN_SUMMATION))*0.000001);
	            tou_block[i].setSummations(1, a1);
	            
	            continue;
			}
			
			log.debug("i="+i);
			if(i>=1){
	            Double a = new Double(((double)(DataFormat.bcd2long(data,OFS_SUMMATION+((i-1)*LEN_SUMMATION),LEN_SUMMATION)))*0.000001);
	            tou_block[i].setSummations(0, a);
	             
	            Double a1 = new Double(((double)(DataFormat.bcd2long(data,OFS_SUMMATION_KVAR+((i-1)*LEN_SUMMATION),LEN_SUMMATION)))*0.000001);
	            log.debug("summation_kvar :  "+a1.doubleValue());
	            tou_block[i].setSummations(1, a1);
	                                                     
	            tou_block[i].setEventTime(0,getDemandTime(DataFormat.select(data,OFS_MDEMAND_TIME+((i-1)*(LEN_DEMAND+LEN_MDEMAND_TIME)),LEN_MDEMAND_TIME)));
	            tou_block[i].setEventTime(1,getDemandTime(
	            		DataFormat.select(data,OFS_MDEMAND_TIME_KVAR+((i-1)*(LEN_DEMAND+LEN_MDEMAND_TIME)),LEN_MDEMAND_TIME)));
	            			
	            Double b = new Double(((double)(DataFormat.bcd2long(data,OFS_CUM_DEMAND+((i-1)*LEN_CUM_DEMAND),LEN_CUM_DEMAND)))*0.01);
	            log.debug("CumDemand :  "+b.doubleValue()); 
	            tou_block[i].setCumDemand(0,b);
	            Double b1 = new Double(((double)(DataFormat.bcd2long(data,OFS_CUM_DEMAND_KVAR+((i-1)*LEN_CUM_DEMAND),LEN_CUM_DEMAND)))*0.01);
	            log.debug("CumDemand_kvar :  "+b1.doubleValue()); 
	            tou_block[i].setCumDemand(1,b1);
	            
	            Double c = new Double((double)((DataFormat.bcd2long(data,OFS_DEMAND+((i-1)*(LEN_DEMAND+LEN_MDEMAND_TIME)),LEN_DEMAND)))*0.01);
	            log.debug("CumDemand :  "+c.doubleValue()); 
	            tou_block[i].setCurrDemand(0,c);
	            Double c1 = new Double(((double)(DataFormat.bcd2long(data,OFS_DEMAND_KVAR+((i-1)*(LEN_DEMAND+LEN_MDEMAND_TIME)),LEN_DEMAND)))*0.01);
	            log.debug("CumDemand_kvar :  "+c1.doubleValue()); 
	            tou_block[i].setCurrDemand(1,c1);
	                                                                              
	            tou_block[i].setCoincident(0, new Double(0.0));
	            tou_block[i].setCoincident(1, new Double(0.0));  
			}
		}
		
		if (tou_block!=null && tou_block.length>0){
	//		total = t1+t2+t3
			tou_block[0].setCurrDemand(0, ((Double)tou_block[1].getCurrDemand(0)).doubleValue()
											+((Double)tou_block[2].getCurrDemand(0)).doubleValue()
											+((Double)tou_block[3].getCurrDemand(0)).doubleValue());
			tou_block[0].setCurrDemand(1,new Double(0.0));
			
			tou_block[0].setCumDemand(0,((Double)tou_block[1].getCumDemand(0)).doubleValue()
											+((Double)tou_block[2].getCumDemand(0)).doubleValue()
											+((Double)tou_block[3].getCumDemand(0)).doubleValue());
			tou_block[0].setCumDemand(1, new Double(0.0)); 
			
			tou_block[0].setCoincident(0, new Double(0.0));
	        tou_block[0].setCoincident(1, new Double(0.0));  
	        
	        tou_block[0].setEventTime(0, tou_block[1].getEventTime(0)); //max data rate
	        tou_block[0].setEventTime(1, tou_block[1].getEventTime(1));        
		}
		
        log.debug("=================BillingData Parse End=================");
	}

	public String getDemandTime(byte[] datetime) throws Exception {

		log.debug("datetime=>"+Util.getHexString(datetime));
//		String s =  DataFormat.bcd2str(datetime);		
		
		int curryy = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
		int year   = DataFormat.bcd2dec(datetime,0,1);
		year   += curryy;
		int month = DataFormat.bcd2dec(datetime, 1,1);
		int day = DataFormat.bcd2dec(datetime,2,1);
       
		StringBuffer ret = new StringBuffer();
        
        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(month),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(day),2));
        
		if(datetime.length >3){
			int hour = DataFormat.bcd2dec(datetime, 3,1);
			int min = DataFormat.bcd2dec(datetime,4,1);
			ret.append(Util.frontAppendNStr('0',Integer.toString(hour),2));
	        ret.append(Util.frontAppendNStr('0',Integer.toString(min),2));
		}
		
        log.debug("billingData eventTime :"+ ret.toString());
        
        return ret.toString();
	}
	
	private Integer getTotalDemandCnt(){
		int resetCount =0;
		
        try{ 
        	resetCount = (int)DataFormat.bcd2dec(resetDate,OFS_NBR_OF_RESET,LEN_NBR_OF_RESET);
        }catch(Exception e){
         log.warn("METER_ID->"+e.getMessage());
        }		
        return new Integer(resetCount);
	}
}
