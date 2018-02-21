/** 
 * @(#)MeterManufacture.java       1.0 08/03/31 *
 * 
 * Actual Dimension Register Table.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.LK1210DRBTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class MeterManufacture implements java.io.Serializable{
	
    public static final int OFS_METER_PROTOCOL  = 2;
    public static final int OFS_METER_VENDOR  = 3;
    public static final int OFS_METER_MAKE_TIME  = 4;
    public static final int OFS_METER_SERIAL  = 5;
    
    public static final int LEN_METER_PROTOCOL  = 1;
    public static final int LEN_METER_VENDOR  = 1;
    public static final int LEN_METER_MAKE_TIME  = 1;
    public static final int LEN_METER_SERIAL  = 3;
               
	private byte[] data;
    private static Log log = LogFactory.getLog(MeterManufacture.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MeterManufacture(byte[] data) {
		this.data = data;
	}
	
    /**
     * meter protocol
     */
    public int getMETER_PROTOCOL() {
        return DataFormat.hex2unsigned8(data[OFS_METER_PROTOCOL]);
    }
	
    /**
     * meter vendor
     */
    public int getMETER_VENDOR() {
        return DataFormat.hex2unsigned8(data[OFS_METER_VENDOR]);
    }
    
    /**
     * meter maketime
     */
    public int getMETER_MAKE_TIME() {
        return (DataFormat.hex2unsigned8(data[OFS_METER_MAKE_TIME]));
    }
        
    /**
     * meter serial - big endian
     */
    public String getMETER_SERIAL() {

        String res = new String();
        try{
            res = Integer.toString(
                DataFormat.hex2dec(
                    DataFormat.select(data,OFS_METER_SERIAL,LEN_METER_SERIAL)));

        }catch(Exception e){
            log.warn("METER_SERIAL->"+e.getMessage());
        }
        return res;
    }
    
    /**
     * meter id : METER_MAKE_TIME(2digit)+ METER_SERIAL(8 digit)
     */
    public String getMeterId(){
        return Util.frontAppendNStr('0',Integer.toString(getMETER_MAKE_TIME()),2)+""+
               Util.frontAppendNStr('0',getMETER_SERIAL(),8);
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("MeterManufacture DATA[");        
            sb.append("(METER_PROTOCOL=").append(""+getMETER_PROTOCOL()).append("),");
            sb.append("(METER_VENDOR=").append(""+getMETER_VENDOR()).append("),");
            sb.append("(METER_MAKE_TIME=").append(""+getMETER_MAKE_TIME()).append("),");
            sb.append("(METER_SERIAL()=").append(getMETER_SERIAL()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("MeterManufacture TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }


}
