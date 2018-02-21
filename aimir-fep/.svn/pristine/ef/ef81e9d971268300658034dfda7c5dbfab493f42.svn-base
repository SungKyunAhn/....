package com.aimir.fep.meter.parser.amuKepco_2_5_0Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * Current Time
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 8. 오후 2:50:06$
 */
public class CurrentTime {
	
	private static Log log = LogFactory.getLog(CurrentTime.class);
	
	private static final int OFS_YEAR 			= 0;
	private static final int OFS_MONTH 			= 2;
	private static final int OFS_DAY 			= 3;
	private static final int OFS_HOUR 			= 4;
	private static final int OFS_MIN 			= 5;
	private static final int OFS_SEC			= 6;
	
	private static final int LEN_YEAR 			= 2;
	// Current Time Data
	private byte[] rawData = null;
	
	/**
	 * Constructor
	 * @param rawData
	 */
	public CurrentTime(byte[] rawData){
		this.rawData = rawData;
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
           	ret = DataFormat.hex2dec(DataFormat.select(rawData, OFS_MONTH, 1));

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
    		ret = DataFormat.hex2dec(DataFormat.select(rawData, OFS_DAY, 1));
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
     		ret = DataFormat.hex2dec(DataFormat.select(rawData, OFS_HOUR, 1));
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
     		DataFormat.hex2dec(DataFormat.select(rawData, OFS_MIN, 1));
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
     		ret = DataFormat.hex2dec(DataFormat.select(rawData, OFS_SEC, 1));
     	}catch(Exception e){
     		log.warn("invalid model->"+e.getMessage());
     	}
     	
     	return ret;
    }
    
    
    /**
     * get Current Time
     * @return
     */
    public String getCurrnetTime(){
    	 	
    	int year 	= getYear();
    	int month 	= getMonth();
    	int day 	= getDay();
    	int hour 	= getHour();
    	int min 	= getMin();
    	int sec 	= getSec();
    	
    	StringBuffer ret = new StringBuffer();
        
        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(month),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(day),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hour),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(min),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(sec),2));
    	log.debug("Current Time : " + ret.toString());
    	
		return ret.toString();
    }	
}