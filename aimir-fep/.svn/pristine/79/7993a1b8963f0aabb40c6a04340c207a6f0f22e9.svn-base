package com.aimir.fep.meter.parser.amuKepco_dlmsTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * KEPCO DLMS Meter Status Caution
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 5. 13. 오후 12:04:59$
 */
public class MeterStatusCaution {
	
	private Log log = LogFactory.getLog(MeterStatusCaution.class);
	
	public static final byte NO_CAUTION		  	= (byte)0x00;
	public static final byte NOT_PROGRAMMED  	= (byte)0x10;
    public static final byte LOW_BATTERY  		= (byte)0x08;
    public static final byte BATTERY_MISSING  	= (byte)0x04;
    public static final byte MEMORY_ERROR  		= (byte)0x02;
    public static final byte RTC_RESET  		= (byte)0x01;
    
	private byte data;
	 
	/**
	 * Constructor
	 * @param data
	 */
	public MeterStatusCaution(byte data) {
		this.data = data;
	}
	
	
	
	/**
	 * get Meter Status Caution 
	 * @return
	 */
	public int getMeterStatusCaution() {
		int ret =0;
		try{
			return DataFormat.hex2unsigned8(data);
		}catch(Exception e){
			log.warn("invalid model->"+e.getMessage());
		}
		return ret;
	}
	
	    
    /**
     * NO CAUTION
     */
    public boolean getNo_Caution() {
    	byte flag =(byte)(data&NO_CAUTION) ;
        if (flag != 0){
            return true;
        }
        return false;
    }
    
    /**
     * Not Programmed into meter
     */
    public boolean getNotProgrammedMeter_CAUTION() {
    	 byte flag =(byte)(data&NOT_PROGRAMMED) ;
         if (flag != 0){
             return true;
         }
         return false;
    }
    
    /**
     * Low voltage of battery circuit
     */
    public boolean getLowVoltageBattery_CAUTION(){
    	 
    	byte flag =(byte)(data&LOW_BATTERY) ;
         if (flag != 0){
             return true;
         }
         return false;
    }
    
    /**
     * Missing Of Battery
     */
    public boolean getMissingOfBattery_CAUTION() {
    	int flag =(int)(data &BATTERY_MISSING);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    
    /**
     * Memory Error
     */
    public boolean getMemoryError_CAUTION() {
    	byte flag =(byte)(data&MEMORY_ERROR) ;
        if (flag != 0){
            return true;
        }
        return false;
    }
    
    /**
     * RTC Reset
     */
    public boolean getRTCReset_CAUTION() {
    	byte flag =(byte)(data&RTC_RESET) ;
        if (flag != 0){
            return true;
        }
        return false;
    }
    
	/**
     * get Log
     * @return
     */
    public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{  

            if(getNo_Caution())
                sb.append("<dt>NO_CAUTION</dt>");
            if(getNotProgrammedMeter_CAUTION())
                sb.append("<dt>Not Programmed Meter CAUTION</dt>");
            if(getLowVoltageBattery_CAUTION())
                sb.append("<dt>Low Voltag eBattery CAUTION</dt>");
            if(getMissingOfBattery_CAUTION())
                sb.append("<dt>Missing Of Battery CAUTION</dt>");
            if(getMemoryError_CAUTION())
                sb.append("<dt>Memory Error CAUTION</dt>");
            if(getRTCReset_CAUTION())
                sb.append("<dt>RTC Reset CAUTION</dt>");
        }catch(Exception e){
            log.warn("MeterStatusCaution TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}