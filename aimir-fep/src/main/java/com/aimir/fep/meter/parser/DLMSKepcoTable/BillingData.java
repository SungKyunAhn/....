/** 
 * @(#)BillingData.java       1.0 08/04/16 *
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
 
package com.aimir.fep.meter.parser.DLMSKepcoTable;

import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.util.DataFormat;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class BillingData implements java.io.Serializable{

	private static final long serialVersionUID = -7285856225556686319L;
	private final int OFS_ACTIVE_ENERGY = 0;
	private final int OFS_REACTIVE_ENERGY = 4;
    private final int OFS_ACTIVE_MAX_POWER = 8;
    private final int OFS_ACTIVE_CUMMULSTIVE_POWER = 10;
    private final int OFS_ACTIVE_MAX_POWER_DATETIME = 14;
        
    private final int LEN_ACTIVE_ENERGY = 4;
	private final int LEN_REACTIVE_ENERGY = 4;
    private final int LEN_ACTIVE_MAX_POWER = 2;
    private final int LEN_ACTIVE_CUMMULSTIVE_POWER = 4;
    private final int LEN_ACTIVE_MAX_POWER_DATETIME = 7;

    private final int NBR_TIERS =2;
    private final int CNT_BLOCK =4; //total, a,b, c
    
    private int regK=1;
    
    private byte[] data;
    private String datetime;
    private static Log log = LogFactory.getLog(BillingData.class);
    DecimalFormat dformat = new DecimalFormat("###############0.000000");
    
	private TOU_BLOCK[] tou_block;
	        
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public BillingData(byte[] data, String datetime, int regK) { //previous : resettime, current : current meter time
		this.data = data;
		this.datetime = datetime;
		this.regK = regK;
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

		for(int i = 0; i < this.CNT_BLOCK; i++){			
			
			tou_block[i] = new TOU_BLOCK(NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS);
		}
		for(int i = 0; i < (this.CNT_BLOCK -1); i++){	
			tou_block[0].setResetTime(datetime); ////////////////////////////////////
			
			Double a = null;
            a =new Double(((double)DataFormat.hex2signeddec(
                    DataFormat.select(data,OFS_ACTIVE_ENERGY+(i*21),LEN_ACTIVE_ENERGY)))*(1.0/regK));
            tou_block[i+1].setSummations(0, a);
                
            Double a1 = null;
            a1 =new Double(((double)DataFormat.hex2signeddec(
                    DataFormat.select(data,OFS_REACTIVE_ENERGY+(i*21),LEN_REACTIVE_ENERGY)))*(1.0/regK));
            tou_block[i+1].setSummations(1, a1);
                                                    
	        Double b = new Double(((double)DataFormat.hex2signeddec(
	                                     DataFormat.select(data,OFS_ACTIVE_MAX_POWER+(i*21),LEN_ACTIVE_MAX_POWER)))*(1.0/regK));
	        tou_block[i+1].setCurrDemand(0,b);
	        tou_block[i+1].setCurrDemand(1,new Double(0.0));
	            
            Double c = new Double(((double)DataFormat.hex2signeddec(
	                                     DataFormat.select(data,OFS_ACTIVE_CUMMULSTIVE_POWER+(i*21),LEN_ACTIVE_CUMMULSTIVE_POWER)))*(1.0/regK));
	        
            tou_block[i+1].setCumDemand(0,new Double(c.doubleValue() - b.doubleValue()));
	        tou_block[i+1].setCumDemand(1,new Double(0.0));
           
	        tou_block[i+1].setCoincident(0, new Double(0.0));
	        tou_block[i+1].setCoincident(1, new Double(0.0));  

	        tou_block[i+1].setEventTime(0, new String(new DateTimeFormat(
	        		DataFormat.select(data, OFS_ACTIVE_MAX_POWER_DATETIME+(i*21), LEN_ACTIVE_MAX_POWER_DATETIME)).getDateTime()));
	        tou_block[i+1].setEventTime(1, new String("00000000000000"));
		}
		
		//total = t1+t2+t3
		tou_block[0].setSummations(0, ((Double)tou_block[1].getSummation(0)).doubleValue()
										+((Double)tou_block[2].getSummation(0)).doubleValue()
										+((Double)tou_block[3].getSummation(0)).doubleValue());
		tou_block[0].setSummations(1, ((Double)tou_block[1].getSummation(1)).doubleValue()
										+((Double)tou_block[2].getSummation(1)).doubleValue()
										+((Double)tou_block[3].getSummation(1)).doubleValue());
		
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
        
        tou_block[0].setEventTime(0, tou_block[1].getEventTime(0));
        tou_block[0].setEventTime(1, tou_block[1].getEventTime(1));
        log.debug("=================BillingData Parse End=================");
	}
}
