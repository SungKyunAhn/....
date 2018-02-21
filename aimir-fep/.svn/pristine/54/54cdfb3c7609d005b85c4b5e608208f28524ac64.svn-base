package com.aimir.fep.meter.parser.amuLsrwRs232Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * Meter Status
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 11. 오후 5:28:16$
 */
public class MeterStatus {
	
	private Log log = LogFactory.getLog(MeterStatus.class);
	/**
	 * 2010.05.19일 수정 
	 */
	public static final int NO_ERROR						= 0x00000000;
	//Error Information                                           
	public static final int POTENTIAL_ERROR					= 0x00100000; 
	public static final int EEPROM_ERROR		         	= 0x00080000; 
	public static final int RAM_ERROR				        = 0x00020000; 
	public static final int BATTERY_MISSING          		= 0x00010000; 
	//Caution Information                                         
	public static final int LOW_BATTERY			         	= 0x00000400; 
	public static final int UNPROGRAM				        = 0x00000200; 
	public static final int DEMAND_OVERFLOW          		= 0x00000100; 
	//Event Information                                           
	public static final int POWER_OUTAGE 	           		= 0x00000010; 
	public static final int MAX_DEMAND			         	= 0x00000008; 
	public static final int DEMAND_RESET		         	= 0x00000004; 
	public static final int TIME_CHANGE			         	= 0x00000002; 
	public static final int PROGRAM_CHANGE           		= 0x00000001; 
    private int dataValue ;
    /**
	 * Constructor .<p>
	 * 
	 * @param data
	 */
	public MeterStatus(byte[] data){
		try{
			dataValue = (int)DataFormat.hex2dec(data);
		}catch (Exception e) {
			log.error("Meter Status Constructor failed!");
		}
		
	}
	
    /**
     * get NO_ERROR
     */
    public boolean getNO_ERROR() {
    	int flag =(int)(dataValue&NO_ERROR);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /* ******************* Error Information  ******************* */
    /**
     * get POTENTIAL_ERROR
     */
    public boolean getPOTENTIAL_ERROR() {
    	int flag =(int)(dataValue&POTENTIAL_ERROR);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /**
     * get EEPROM_ERROR
     */
    public boolean getEEPROM_ERROR() {
    	int flag =(int)(dataValue&EEPROM_ERROR);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    
    /**
     * get RAM_ERROR
     */
    public boolean getRAM_ERROR() {
    	int flag =(int)(dataValue&RAM_ERROR);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /**
     * get BATTERY_MISSING
     */
    public boolean getBATTERY_MISSING() {
    	int flag =(int)(dataValue&BATTERY_MISSING);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /* ******************* Caution Information ******************* */
    /**
     * get LOW_BATTERY
     */
    public boolean getLOW_BATTERY() {
    	int flag =(int)(dataValue&LOW_BATTERY);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /**
     * get UNPROGRAM
     */
    public boolean getUNPROGRAM() {
    	int flag =(int)(dataValue&UNPROGRAM);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /**
     * get DEMAND_OVERFLOW
     */
    public boolean getDEMAND_OVERFLOW() {
    	int flag =(int)(dataValue&DEMAND_OVERFLOW);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /* ******************* Event Information    ******************* */
    
    /**
     * get POWER_OUTAGE
     */
    public boolean getPOWER_OUTAGE() {
    	int flag =(int)(dataValue&POWER_OUTAGE);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /**
     * get MAX_DEMAND
     */
    public boolean getMAX_DEMAND() {
    	int flag =(int)(dataValue&MAX_DEMAND);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    
    /**
     * get DEMAND_RESET
     */
    public boolean getDEMAND_RESET() {
    	int flag =(int)(dataValue&DEMAND_RESET);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    
    /**
     * get TIME_CHANGE
     */
    public boolean getTIME_CHANGE() {
    	int flag =(int)(dataValue&TIME_CHANGE);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    
    /**
     * get PROGRAM_CHANGE
     */
    public boolean getPROGRAM_CHANGE() {
    	int flag =(int)(dataValue&PROGRAM_CHANGE);
        if (flag !=0){
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

            if(getNO_ERROR())
                sb.append("<dt>NO_ERROR</dt>");
            if(getPOTENTIAL_ERROR())
                sb.append("<dt>POTENTIAL_ERROR</dt>");
            if(getEEPROM_ERROR())
                sb.append("<dt>EEPROM_ERROR</dt>");
            if(getRAM_ERROR())
                sb.append("<dt>RAM_ERROR</dt>");
            if(getBATTERY_MISSING())
                sb.append("<dt>BATTERY_MISSING</dt>");
            if(getLOW_BATTERY())
                sb.append("<dt>LOW_BATTERY</dt>");
            if(getUNPROGRAM())
                sb.append("<dt>UNPROGRAM</dt>");
            if(getDEMAND_OVERFLOW())
                sb.append("<dt>DEMAND_OVERFLOW</dt>");
            if(getPOWER_OUTAGE())
                sb.append("<dt>POWER_OUTAGE</dt>");
            if(getMAX_DEMAND())
                sb.append("<dt>MAX_DEMAND</dt>");
            if(getDEMAND_RESET())
                sb.append("<dt>DEMAND_RESET</dt>");
            if(getTIME_CHANGE())
                sb.append("<dt>TIME_CHANGE</dt>");
            if(getPROGRAM_CHANGE())
                sb.append("<dt>PROGRAM_CHANGE</dt>");
 
        }catch(Exception e){
            log.warn("MeterStatus TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}


