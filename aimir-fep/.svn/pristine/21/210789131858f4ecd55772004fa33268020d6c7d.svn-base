/* 
 * @(#)A2RL_PQ.java       1.0 08/10/23 *
 * 
 * Power Quality Data.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a2rlTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.PowerQualityMonitor;
import com.aimir.fep.util.DataFormat;

/**
 * @author Kang, Soyi
 */
public class A2RL_PQ {    
    
    public static final int OFS_SERVICE_VOLTAGE_CNT = 0;
    public static final int OFS_SERVICE_VOLTAGE_DUR = 2;
    public static final int OFS_LOW_VOLTAGE_CNT = 6;
    public static final int OFS_LOW_VOLTAGE_DUR = 8;
    public static final int OFS_HIGH_VOLTAGE_CNT = 12;
    public static final int OFS_HIGH_VOLTAGE_DUR = 14;
    public static final int OFS_REVERSE_POWER_CNT = 18;
    public static final int OFS_REVERSE_POWER_DUR = 20;
    public static final int OFS_LOW_CURRENT_CNT = 24;
    public static final int OFS_LOW_CURRENT_DUR = 26;
    public static final int OFS_POWER_FACTOR_CNT = 30;
    public static final int OFS_POWER_FACTOR_DUR = 32;
    public static final int OFS_2ND_HARMONIC_CURRENT_CNT = 36;
    public static final int OFS_2ND_HARMONIC_CURRENT_DUR = 38;
    public static final int OFS_THD_CURRENT_CNT = 42;
    public static final int OFS_THD_CURRENT_DUR = 44;
    public static final int OFS_THD_VOLTAGE_CNT = 48;
    public static final int OFS_THD_VOLTAGE_DUR = 50;
    
    public static final int OFS_SAG_LOG_CNT_A = 54;
    public static final int OFS_SAG_LOG_CNT_B = 56;
    public static final int OFS_SAG_LOG_CNT_C = 58;
    public static final int OFS_SAG_LOG_DURL_A = 60;
    public static final int OFS_SAG_LOG_DURL_B = 62;
    public static final int OFS_SAG_LOG_DURL_C = 64;
    public static final int OFS_SAG_LOG_DURH_A = 66;
    public static final int OFS_SAG_LOG_DURH_B = 68;
    public static final int OFS_SAG_LOG_DURH_C = 70;
    
    public static final int OFS_SAG_LOG_DUR = 44;    
    
    public static final int LEN_COUNT =2;
    public static final int LEN_TIMER =4;
    
    public static final char MASK_PRESENT_STAT = 0x8000;
    public static final char MASK_COUNT = 0x7FFF;
    
	private byte[] rawData = null;

    private Log log = LogFactory.getLog(A2RL_PQ.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public A2RL_PQ(byte[] rawData) {
		this.rawData = rawData;
	}
    
    public PowerQualityMonitor getData() throws Exception {
        
        PowerQualityMonitor pq = new PowerQualityMonitor();       
        
        try{
            pq.setSERVICE_VOL_CNT(getSERVICE_VOLTAGE_CNT());
            pq.setSERVICE_VOL_DUR(getSERVICE_VOLTAGE_DUR());
            pq.setSERVICE_VOL_STAT(getSERVICE_VOLTAGE_STAT());
            
            pq.setLOW_VOL_CNT(getLOW_VOLTAGE_CNT());
            pq.setLOW_VOL_DUR(getLOW_VOLTAGE_DUR());
            pq.setLOW_VOL_STAT(getLOW_VOLTAGE_STAT());
            
            pq.setHIGH_VOL_CNT(getHIGH_VOLTAGE_CNT());
            pq.setHIGH_VOL_DUR(getHIGH_VOLTAGE_DUR());
            pq.setHIGH_VOL_STAT(getHIGH_VOLTAGE_STAT());
            
            pq.setREVERSE_PWR_CNT(getREVERSE_POWER_CNT());
            pq.setREVERSE_PWR_DUR(getREVERSE_POWER_DUR());
            pq.setREVERSE_PWR_STAT(getREVERSE_POWER_STAT());
            
            pq.setLOW_CURR_CNT(getLOW_CURRENT_CNT());
            pq.setLOW_CURR_DUR(getLOW_CURRENT_DUR());
            pq.setLOW_CURR_STAT(getLOW_CURRENT_STAT());
            
            pq.setPFACTOR_CNT(getPOWER_FACTOR_CNT());
            pq.setPFACTOR_DUR(getPOWER_FACTOR_DUR());
            pq.setPFACTOR_STAT(getPOWER_FACTOR_STAT());
                        
            pq.setTHD_CURR_CNT(getTHD_CURRENT_CNT());
            pq.setTHD_CURR_DUR(getTHD_CURRENT_DUR());
            pq.setTHD_CURR_STAT(getTHD_CURRENT_STAT());
            
            pq.setTHD_VOL_CNT(getTHD_VOLTAGE_CNT());
            pq.setTHD_VOL_DUR(getTHD_VOLTAGE_DUR());
            pq.setTHD_VOL_STAT(getTHD_VOLTAGE_STAT());
            
            pq.setVOL_A_SAG_CNT(getSAG_COUNT_A());
            pq.setVOL_B_SAG_CNT(getSAG_COUNT_B());
            pq.setVOL_C_SAG_CNT(getSAG_COUNT_C());
            
            pq.setVOL_A_SAG_DUR(getSAG_DUR_A());
            pq.setVOL_B_SAG_DUR(getSAG_DUR_B());
            pq.setVOL_C_SAG_DUR(getSAG_DUR_C());
            
            log.debug(toString());

            return pq;  
        }catch(Exception e){
            log.warn("pq transfrom error: "+e.getMessage());
            throw new Exception("pq transfrom error: ",e);
        }
    }

	public int getSERVICE_VOLTAGE_CNT() throws Exception {
        return  DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_SERVICE_VOLTAGE_CNT, LEN_COUNT))& MASK_COUNT;
    }
	public int getSERVICE_VOLTAGE_STAT() throws Exception {
        return  (DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_SERVICE_VOLTAGE_CNT, LEN_COUNT))& MASK_PRESENT_STAT) >> 15;
    }
	public int getSERVICE_VOLTAGE_DUR() throws Exception {
        return  (int)DataFormat.hex2unsigned32(DataFormat.select(rawData, OFS_SERVICE_VOLTAGE_DUR, LEN_TIMER)) ;
    }
	
	public int getLOW_VOLTAGE_CNT() throws Exception {
        return  DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_LOW_VOLTAGE_CNT, LEN_COUNT))& MASK_COUNT;
    }
	public int getLOW_VOLTAGE_STAT() throws Exception {
        return  (DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_LOW_VOLTAGE_CNT, LEN_COUNT))& MASK_PRESENT_STAT) >> 15;
    }
	public int getLOW_VOLTAGE_DUR() throws Exception {
        return  (int)DataFormat.hex2unsigned32(DataFormat.select(rawData, OFS_LOW_VOLTAGE_DUR, LEN_TIMER)) ;
    }
	
	public int getHIGH_VOLTAGE_CNT() throws Exception {
        return  DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_HIGH_VOLTAGE_CNT, LEN_COUNT))& MASK_COUNT;
    }
	public int getHIGH_VOLTAGE_STAT() throws Exception {
        return  (DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_HIGH_VOLTAGE_CNT, LEN_COUNT))& MASK_PRESENT_STAT) >> 15;
    }
	public int getHIGH_VOLTAGE_DUR() throws Exception {
        return  (int)DataFormat.hex2unsigned32(DataFormat.select(rawData, OFS_HIGH_VOLTAGE_DUR, LEN_TIMER)) ;
    }
	
	public int getREVERSE_POWER_CNT() throws Exception {
        return  DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_REVERSE_POWER_CNT, LEN_COUNT))& MASK_COUNT;
    }
	public int getREVERSE_POWER_STAT() throws Exception {
        return  (DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_REVERSE_POWER_CNT, LEN_COUNT))& MASK_PRESENT_STAT) >> 15;
    }
	public int getREVERSE_POWER_DUR() throws Exception {
        return  (int)DataFormat.hex2unsigned32(DataFormat.select(rawData, OFS_REVERSE_POWER_DUR, LEN_TIMER)) ;
    }
	
	public int getLOW_CURRENT_CNT() throws Exception {
        return  DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_LOW_CURRENT_CNT, LEN_COUNT))& MASK_COUNT;
    }
	public int getLOW_CURRENT_STAT() throws Exception {
        return  (DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_LOW_CURRENT_CNT, LEN_COUNT))& MASK_PRESENT_STAT) >> 15;
    }
	public int getLOW_CURRENT_DUR() throws Exception {
        return  (int)DataFormat.hex2unsigned32(DataFormat.select(rawData, OFS_LOW_CURRENT_DUR, LEN_TIMER)) ;
    }
	
	public int getPOWER_FACTOR_CNT() throws Exception {
        return  DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_POWER_FACTOR_CNT, LEN_COUNT))& MASK_COUNT;
    }
	public int getPOWER_FACTOR_STAT() throws Exception {
        return  (DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_POWER_FACTOR_CNT, LEN_COUNT))& MASK_PRESENT_STAT) >> 15;
    }
	public int getPOWER_FACTOR_DUR() throws Exception {
        return  (int)DataFormat.hex2unsigned32(DataFormat.select(rawData, OFS_POWER_FACTOR_DUR, LEN_TIMER)) ;
    }
	
	public int getTHD_CURRENT_CNT() throws Exception {
        return  DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_THD_CURRENT_CNT, LEN_COUNT))& MASK_COUNT;
    }
	public int getTHD_CURRENT_STAT() throws Exception {
        return  (DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_THD_CURRENT_CNT, LEN_COUNT))& MASK_PRESENT_STAT) >> 15;
    }
	public int getTHD_CURRENT_DUR() throws Exception {
        return  (int)DataFormat.hex2unsigned32(DataFormat.select(rawData, OFS_THD_CURRENT_DUR, LEN_TIMER)) ;
    }
	
	public int getTHD_VOLTAGE_CNT() throws Exception {
        return  DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_THD_VOLTAGE_CNT, LEN_COUNT))& MASK_COUNT;
    }
	public int getTHD_VOLTAGE_STAT() throws Exception {
        return  (DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_THD_VOLTAGE_CNT, LEN_COUNT))& MASK_PRESENT_STAT) >> 15;
    }
	public int getTHD_VOLTAGE_DUR() throws Exception {
        return  (int)DataFormat.hex2unsigned32(DataFormat.select(rawData, OFS_THD_VOLTAGE_DUR, LEN_TIMER)) ;
    }

	public int getSAG_COUNT_A() throws Exception {
		return DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_SAG_LOG_CNT_A, LEN_COUNT));
	}
	public int getSAG_COUNT_B() throws Exception {
		return DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_SAG_LOG_CNT_B, LEN_COUNT));
	}
	public int getSAG_COUNT_C() throws Exception {
		return DataFormat.hex2unsigned16(DataFormat.select(rawData, OFS_SAG_LOG_CNT_C, LEN_COUNT));
	}
	
	public int getSAG_DUR_A() throws Exception {
		
		long duration = 0;
		duration = 
		    (long)(DataFormat.hex2dec(rawData,OFS_SAG_LOG_DURL_A,2)*7.8125)
		  + (long)(DataFormat.hex2dec(rawData,OFS_SAG_LOG_DURH_A,2)*512*1000);
		return (int) duration;
	}
	
	public int getSAG_DUR_B() throws Exception {
		
		long duration = 0;
		
		duration = 
		    (long)(DataFormat.hex2dec(rawData,OFS_SAG_LOG_DURL_B,2)*7.8125)
		  + (long)(DataFormat.hex2dec(rawData,OFS_SAG_LOG_DURH_B,2)*512*1000);
		return (int) duration;
	}
	
	public int getSAG_DUR_C() throws Exception {
		
		long duration = 0;
		
		duration = 
		    (long)(DataFormat.hex2dec(rawData,OFS_SAG_LOG_DURL_C,2)*7.8125)
		  + (long)(DataFormat.hex2dec(rawData,OFS_SAG_LOG_DURH_C,2)*512*1000);
		return (int) duration;
	}
	
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("A2RL_PQ Meter DATA[");        
            
            sb.append("(SERVICE_VOLTAGE_CNT=").append(getSERVICE_VOLTAGE_CNT()).append("),");
            sb.append("(SERVICE_VOLTAGE_DUR=").append(getSERVICE_VOLTAGE_DUR()).append("),");
            sb.append("(SERVICE_VOLTAGE_STAT=").append(getSERVICE_VOLTAGE_STAT()).append("),");
            
            sb.append("(LOW_VOLTAGE_CNT=").append(getLOW_VOLTAGE_CNT()).append("),");
            sb.append("(LOW_VOLTAGE_DUR=").append(getLOW_VOLTAGE_DUR()).append("),");
            sb.append("(LOW_VOLTAGE_STAT=").append(getLOW_VOLTAGE_STAT()).append("),");
            
            sb.append("(HIGH_VOLTAGE_CNT=").append(getHIGH_VOLTAGE_CNT()).append("),");
            sb.append("(HIGH_VOLTAGE_DUR=").append(getHIGH_VOLTAGE_DUR()).append("),");  
            sb.append("(HIGH_VOLTAGE_STAT=").append(getHIGH_VOLTAGE_STAT()).append("),");
            
            sb.append("(REVERSE_POWER_CNT=").append(getREVERSE_POWER_CNT()).append("),");
            sb.append("(REVERSE_POWER_DUR=").append(getREVERSE_POWER_DUR()).append("),");
            sb.append("(REVERSE_POWER_STAT=").append(getREVERSE_POWER_STAT()).append("),");
            
            sb.append("(LOW_CURRENT_CNT=").append(getLOW_CURRENT_CNT()).append("),");
            sb.append("(LOW_CURRENT_DUR=").append(getLOW_CURRENT_DUR()).append("),");
            sb.append("(LOW_CURRENT_STAT=").append(getLOW_CURRENT_STAT()).append("),");
            
            sb.append("(POWER_FACTOR_CNT=").append(getPOWER_FACTOR_CNT()).append("),");
            sb.append("(POWER_FACTOR_DUR=").append(getPOWER_FACTOR_DUR()).append("),");
            sb.append("(POWER_FACTOR_STAT=").append(getPOWER_FACTOR_STAT()).append("),");
            
            sb.append("(THD_CURRENT_CNT=").append(getTHD_CURRENT_CNT()).append("),");
            sb.append("(THD_CURRENT_DUR=").append(getTHD_CURRENT_DUR()).append("),");
            sb.append("(THD_CURRENT_STAT=").append(getTHD_CURRENT_STAT()).append("),");
            
            sb.append("(THD_VOLTAGE_CNT=").append(getTHD_VOLTAGE_CNT()).append("),");
            sb.append("(THD_VOLTAGE_DUR=").append(getTHD_VOLTAGE_DUR()).append("),");
            sb.append("(THD_VOLTAGE_STAT=").append(getTHD_VOLTAGE_STAT()).append("),");
            
            sb.append("(SAG_COUNT_A=").append(getSAG_COUNT_A()).append("),");
            sb.append("(SAG_DUR_A=").append(getSAG_DUR_A()).append("),");
            sb.append("(SAG_COUNT_B=").append(getSAG_COUNT_B()).append("),");
            sb.append("(SAG_DUR_B=").append(getSAG_DUR_B()).append("),");
            sb.append("(SAG_COUNT_C=").append(getSAG_COUNT_C()).append("),");
            sb.append("(SAG_DUR_C=").append(getSAG_DUR_C()).append(')');
                        
            sb.append("]\n");
        }catch(Exception e){
            log.warn("A2RL_PQ TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}
