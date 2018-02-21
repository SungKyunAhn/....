package com.aimir.fep.meter.parser.amuKepco_dlmsTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * Demand Reset Time
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 20. 오전 11:52:32$
 */
public class DemandResetTime {

	private static Log log = LogFactory.getLog(DemandResetTime.class);
	
	private static final int OFS_STATUS			= 0;
	private static final int OFS_YEAR 			= 1;
	private static final int OFS_MONTH 			= 3;
	private static final int OFS_DAY 			= 4;
	private static final int OFS_HOUR 			= 5;
	private static final int OFS_MIN 			= 6;
	private static final int OFS_SEC			= 7;
	private static final int OFS_WEEK			= 8;
	
	private static final int LEN_YEAR 			= 2;
	
	private byte[] rawData = null;
	
	/**
	 * Constructor
	 * @param rawData
	 */
	public DemandResetTime(byte[] rawData){
		this.rawData = rawData;
	}
	
	/**
     * get Status
     * @return 
     */
    public int getStatus(){
           
    	int ret = 0;
    	try{
           	ret = DataFormat.hex2signed8(rawData[OFS_STATUS]);

    	}catch(Exception e){
    		log.warn("invalid model->"+e.getMessage());
    	}

        return ret;
    }
	
    /**
     * get Status
     * SR이 발생하는 원인
     * @return 
     */
    public String getStatusDesc(){
        
    	try{
           byte status = rawData[OFS_STATUS];
           
           if((byte)((byte)(status & 0x00)) > 0)  	// 0000 0000 (0bit)
        	   return "Regulary billing(SR)";
           if((byte)((byte)(status & 0x02)) > 0)	// 0000 0010 (1bit)
        	   return "Irregulary bill";
           if((byte)((byte)(status & 0x04)) > 0)	// 0000 0100 (2bit)
        	   return "Manual demand reset";
           if((byte)((byte)(status & 0x08)) > 0)	// 0000 1000 (3bit)
        	   return "Program change";
           if((byte)((byte)(status & 0x10)) > 0)	// 0001 0000 (4bit)
        	   return "Date/Time change";
    	}catch(Exception e){
    		log.warn("invalid model->"+e.getMessage());
    	}
    	
    	return "";
    }
    
    /**
     * get Year
     * @return 
     */
    public int getYear(){
         
       int ret = 0;
       try{
    	   ret = DataFormat.hex2dec(DataFormat.LSB2MSB(
    			   	DataFormat.select(rawData,OFS_YEAR ,LEN_YEAR)));

       }catch(Exception e){
    	   log.warn("invalid model->"+e.getMessage());
       }

       return ret;
    }
      
    /**
     * get Month
     * @return 
     */
    public int getMonth(){
           
    	int ret = 0;
    	try{
           	ret = DataFormat.hex2signed8(rawData[OFS_MONTH]);

    	}catch(Exception e){
    		log.warn("invalid model->"+e.getMessage());
    	}

        return ret;
    }
       
    /**
     * get day
     * @return 
     */
    public int getDay(){
            
    	int ret = 0;
    	try{
    		ret = DataFormat.hex2signed8(rawData[OFS_DAY]);
    	}catch(Exception e){
    		log.warn("invalid model->"+e.getMessage());
    	}

        return ret;
    }
    
    /**
     * get Hour
     * @return 
     */
    public int getHour(){
             
    	int ret = 0;
     	try{
     		ret = DataFormat.hex2signed8(rawData[OFS_HOUR]);
     	}catch(Exception e){
     		log.warn("invalid model->"+e.getMessage());
     	}
     	
     	return ret;
    }
    
    /**
     * get Minute
     * @return 
     */
    public int getMin(){
             
    	int ret = 0;
     	try{
     		ret = DataFormat.hex2signed8(rawData[OFS_MIN]);
     	}catch(Exception e){
     		log.warn("invalid model->"+e.getMessage());
     	}
     	
     	return ret;
    }
    
    /**
     * get Second
     * @return 
     */
    public int getSec(){
             
    	int ret = 0;
     	try{
     		ret = DataFormat.hex2signed8(rawData[OFS_SEC]);
     	}catch(Exception e){
     		log.warn("invalid model->"+e.getMessage());
     	}
     	
     	return ret;
    }
    
    /**
     * get Week
     * @return 
     */
    public int getWeek(){
             
    	int ret = 0;
     	try{
     		ret = DataFormat.hex2signed8(rawData[OFS_WEEK]);
     	}catch(Exception e){
     		log.warn("invalid model->"+e.getMessage());
     	}
     	
     	return ret;
    }
    
    /**
     * get Demand Reset Time
     * @return
     */
    public String getDemandResetTime(){
    	 	
    	int year 	= getYear();
    	int month 	= getMonth();
    	int day 	= getDay();
    	int hour 	= getHour();
    	int min 	= getMin();
    	int sec 	= getSec();
    	int week	= getWeek();
    	
    	StringBuffer ret = new StringBuffer();
        
        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(month),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(day),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hour),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(min),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(sec),2));
        ret.append(Integer.toString(week));
        
    	log.debug("Demand Reset Time + type : " + ret.toString());
    	
		return ret.toString();
    }	
}


