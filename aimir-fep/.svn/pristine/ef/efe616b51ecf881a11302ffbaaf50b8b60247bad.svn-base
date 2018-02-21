package com.aimir.fep.meter.parser.amuKepco_dlmsTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 *  KEPCO DLMS Meter Status Error
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 20. 오후 1:56:04$
 */
public class MeterStatusError {

	private Log log = LogFactory.getLog(MeterStatusError.class);
	
	public static final byte NO_ERROR		  		= (byte)0x00;
	public static final byte CURRENT_DISCONNECTED 	= (byte)0x20;
	public static final byte ACTIVE_POWER_FLOW 		= (byte)0x10;
    public static final byte WRONG_CONNECTION		= (byte)0x08;
    public static final byte LOW_VOLTAGE_PHASE3  	= (byte)0x04;
    public static final byte LOW_VOLTAGE_PHASE2		= (byte)0x02;
    public static final byte LOW_VOLTAGE_PHASE1  	= (byte)0x01;
    
	private byte data;
	 
	/**
	 * Constructor
	 * @param data
	 */
	public MeterStatusError(byte data) {
		this.data = data;
	}
	
	/**
	 * get Meter Status Error
	 * @return
	 */
	public int getMeterStatusError() {
		int ret =0;
		try{
			return DataFormat.hex2unsigned8(data);
		}catch(Exception e){
			log.warn("invalid model->"+e.getMessage());
		}
		return ret;
	}
	
	/**
     * NO Error
     */
    public boolean getNoError() {
    	byte flag =(byte)(data&NO_ERROR) ;
        if (flag != 0){
            return true;
        }
        return false;
    }
    
	/**
     * Current circuit disconnected
     */
    public boolean getCurrentCircuitDisconneted_ERROR() {
    	byte flag =(byte)(data&CURRENT_DISCONNECTED) ;
        if (flag != 0){
            return true;
        }
        return false;
    }
    
    /**
     * Active Power Flow
     */
    public boolean getActivePowerFlow_ERROR() {
    	byte flag =(byte)(data&ACTIVE_POWER_FLOW) ;
        if (flag != 0){
            return true;
        }
        return false;
    }
    
    /**
     * Neutral line Wrong connection
     */
    public boolean getNeutralLineWrongConnection_ERROR(){
    	int flag =(int)(data & WRONG_CONNECTION);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /**
     * Low voltage for phase3
     */
    public boolean getLowVoltagePhase3_ERROR() {
    	int flag =(int)(data & LOW_VOLTAGE_PHASE3);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /**
     * Low voltage for phase2
     */
    public boolean getLowVoltagePhase2_ERROR() {
    	int flag =(int)(data & LOW_VOLTAGE_PHASE2);
        if (flag !=0){
            return true;
        }
        return false;
    }
    
    /**
     * Low voltage for phase1
     */
    public boolean getLowVoltagePhase1_ERROR() {
    	int flag =(int)(data & LOW_VOLTAGE_PHASE1);
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

            if(getNoError())
                sb.append("<dt>NO_ERROR</dt>");
            if(getCurrentCircuitDisconneted_ERROR())
                sb.append("<dt>Current Circuit Disconneted ERROR</dt>");
            if(getActivePowerFlow_ERROR())
                sb.append("<dt>Active Power [Q2+Q3] Flow ERROR</dt>");
            if(getNeutralLineWrongConnection_ERROR())
                sb.append("<dt>Neutral Line Wrong Connection ERROR</dt>");
            if(getLowVoltagePhase3_ERROR())
                sb.append("<dt>Low Voltage Phase3 ERROR</dt>");
            if(getLowVoltagePhase2_ERROR())
                sb.append("<dt>Low Voltage Phase2 ERROR</dt>");
            if(getLowVoltagePhase1_ERROR())
                sb.append("<dt>Low Voltage Phase1 ERROR</dt>");
            
        }catch(Exception e){
            log.warn("MeterStatusError TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
    
}


