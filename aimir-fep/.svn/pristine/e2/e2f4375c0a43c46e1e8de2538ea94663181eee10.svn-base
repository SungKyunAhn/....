package com.aimir.fep.meter.parser.amuKepco_dlmsTable;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * System Status
 * Error Field : Error Code Status
 * 
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 5. 13. 오전 11:45:02$
 */
public class SystemStatus {

	private Log log = LogFactory.getLog(SystemStatus.class);
		
	//Meter communication error
	public static final byte[] ERROR_METER_NAK	  				= new byte[]{(byte)0x07 , (byte)0x00};
    public static final byte[] ERROR_METER_MAX_BUFFER_OVERFLOW	= new byte[]{(byte)0x07 , (byte)0x01};
    public static final byte[] ERROR_METER_CRC			  		= new byte[]{(byte)0x07 , (byte)0x02};
    public static final byte[] ERROR_METER_HEADER_MISMATCH		= new byte[]{(byte)0x07 , (byte)0x03};
    public static final byte[] ERROR_METER_SERIAL_TIMEOUT		= new byte[]{(byte)0x07 , (byte)0x04};
    public static final byte[] ERROR_METER_RESET				= new byte[]{(byte)0x07 , (byte)0x05};
    public static final byte[] ERROR_METER_AUTHENTICATION		= new byte[]{(byte)0x07 , (byte)0x06};
    public static final byte[] ERROR_METER_USER_DATA			= new byte[]{(byte)0x07 , (byte)0x07};
    public static final byte[] ERROR_METER_RESPONSE				= new byte[]{(byte)0x07 , (byte)0x08};
    public static final byte[] ERROR_METER_BUFFER_LENGTH		= new byte[]{(byte)0x07 , (byte)0x09};
    
    
    private byte[] data;
    
    /**
     * Constructor
     * @param data
     */
    public SystemStatus(byte[] data){
    	this.data =  data;
    }
    
    /**
     * get ERROR_METER_NAK
     * @return
     */
    public boolean getErrorMeterNak(){
    	return Arrays.equals(data, ERROR_METER_NAK);	
    }
    
    /**
     * get ERROR_METER_MAX_BUFFER_OVERFLOW
     * @return
     */
    public boolean getErrorMeterMaxBufferOver(){
    	return Arrays.equals(data, ERROR_METER_MAX_BUFFER_OVERFLOW);	
    }
    
    /**
     * get ERROR_METER_CRC
     * @return
     */
    public boolean getErrorMeterCrc(){
    	return Arrays.equals(data, ERROR_METER_CRC);	
    }
    
    /**
     * get ERROR_METER_HEADER_MISMATCH
     * @return
     */
    public boolean getErrorMeterHeaderMisMatch(){
    	return Arrays.equals(data, ERROR_METER_HEADER_MISMATCH);	
    }
    
    /**
     * get ERROR_METER_SERIAL_TIMEOUT
     * @return
     */
    public boolean getErrorMeterSerialTimeOut(){
    	return Arrays.equals(data, ERROR_METER_SERIAL_TIMEOUT);	
    }
    
    /**
     * get ERROR_METER_RESET
     * @return
     */
    public boolean getErrorMeterReset(){
    	return Arrays.equals(data, ERROR_METER_RESET);	
    }
    
    /**
     * get ERROR_METER_AUTHENTICATION
     * @return
     */
    public boolean getErrorMeterAuthentication(){
    	return Arrays.equals(data, ERROR_METER_AUTHENTICATION);	
    }
    
    
    /**
     * get ERROR_METER_USER_DATA
     * @return
     */
    public boolean getErrorMeterUserData(){
    	return Arrays.equals(data, ERROR_METER_USER_DATA);	
    }
    
    /**
     * get ERROR_METER_RESPONSE
     * @return
     */
    public boolean getErrorMeterResponse(){
    	return Arrays.equals(data, ERROR_METER_RESPONSE);	
    }
    		
    /**
     * get ERROR_METER_BUFFER_LENGTH	
     * @return
     */
    public boolean getErrorMeterBufferLength(){
    	return Arrays.equals(data, ERROR_METER_BUFFER_LENGTH	);	
    }
    	
    /**
     * get Log
     * @return
     */
    public String getLog()
    {
        StringBuffer sb = new StringBuffer();
        try{  

            if(getErrorMeterNak())
                sb.append("<dt>ERROR_METER_NAK</dt>");
            if(getErrorMeterMaxBufferOver())
                sb.append("<dt>ERROR_METER_MAX_BUFFER_OVERFLOW</dt>");
            if(getErrorMeterCrc())
                sb.append("<dt>ERROR_METER_CRC</dt>");
            if(getErrorMeterHeaderMisMatch())
                sb.append("<dt>ERROR_METER_HEADER_MISMATCH</dt>");
            if(getErrorMeterSerialTimeOut())
                sb.append("<dt>ERROR_METER_SERIAL_TIMEOUT</dt>");
            if(getErrorMeterReset())
                sb.append("<dt>ERROR_METER_RESET</dt>");
            if(getErrorMeterAuthentication())
                sb.append("<dt>ERROR_METER_AUTHENTICATION</dt>");
            if(getErrorMeterUserData())
                sb.append("<dt>ERROR_METER_USER_DATA</dt>");
            if(getErrorMeterResponse())
                sb.append("<dt>ERROR_METER_RESPONSE</dt>");
            if(getErrorMeterBufferLength())
                sb.append("<dt>ERROR_METER_BUFFER_LENGTH</dt>");
           
        }catch(Exception e){
            log.warn("Error Status TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}