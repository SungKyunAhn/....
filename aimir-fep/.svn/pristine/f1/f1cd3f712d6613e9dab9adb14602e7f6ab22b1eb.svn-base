package com.aimir.fep.meter.parser.amuPulseTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * Current Time Field
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 3. 오후 2:32:21$
 */
public class CurrentTimeData {
	
	private static Log log = LogFactory.getLog(CurrentTimeData.class);
	
	private static final int OFS_TIME_ZONE 		= 0;
	private static final int OFS_DST_VALUE	 	= 2;
	private static final int OFS_YEAR 			= 4;
	private static final int OFS_MONTH 			= 6;
	private static final int OFS_DAY 			= 7;
	private static final int OFS_HOUR 			= 8;
	private static final int OFS_MIN	 		= 9;
	private static final int OFS_SEC	 		= 10;
	
	private static final int LEN_TIME_ZONE 		= 2;
	private static final int LEN_DST_VALUE 		= 2;
	private static final int LEN_YEAR 			= 2;
	
	// Current Time Data
	private byte[] rawData = null;
	
	/**
	 * Constructor
	 * @param timeData
	 */
	public CurrentTimeData(byte[] rawData){
		this.rawData = rawData;
	}
	
	/**
     * get Type Name
     * @return 
     */
    public int getTimeZone(){
        
        int ret =0;
        try{
        	ret = DataFormat.hex2signed16(
    				DataFormat.select(rawData, OFS_TIME_ZONE,LEN_TIME_ZONE));

        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }

        return ret;
    }
	
    /**
     * get DST Value
     * @return 
     */
    public int getDstValue(){
         
        int ret = 0;
        try{
        	ret = DataFormat.hex2signed16(
    				DataFormat.select(rawData, OFS_DST_VALUE,LEN_DST_VALUE));

        }catch(Exception e){
             log.warn("invalid model->"+e.getMessage());
        }

        return ret;
    }
     
    /**
     * get Year
     * @return 
     */
    public int getYear(){
         
       int ret = 0;
       try{
    	   ret = DataFormat.hex2signed16(
   				DataFormat.select(rawData, OFS_YEAR,LEN_YEAR));

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
    
    public String getTimeStamp(){
    	
    	
    	int year 	= getYear();
    	int month 	= getMonth();
    	int day 	= getDay();
    	int hour 	= getHour();
    	int min 	= getMin();
    	int sec 	= getSec();
    	/*
    	String timestamp = Integer.toString(year)
        + (month < 10? "0"+month:""+month)
        + (day < 10? "0"+day:""+day)
        + (hour < 10? "0"+hour:""+hour)
        + (min < 10? "0"+min:""+min)
        + (sec < 10? "0"+sec:""+sec);
    	*/
    	
    	StringBuffer ret = new StringBuffer();
        
        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(month),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(day),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hour),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(min),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(sec),2));

    	log.debug("Time Stamp : " + ret.toString());
    	
		return ret.toString();
    }
}


