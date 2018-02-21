/* 
 * @(#)A2RL_IS.java       1.0 08/10/24 *
 * 
 * Instrument.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a2rlTable;

import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author Kang, Soyi
 */
public class A2RL_IS {

	private byte[] rawData = null;
    
    public static final int LEN_DATA = 4;
     
     ////////////
    public static final int OFS_VOLTAGE_INSTANCE_A = 0;
    public static final int OFS_VOLTAGE_INSTANCE_B = 4;
    public static final int OFS_VOLTAGE_INSTANCE_C = 8;
     
    public static final int OFS_CURRENT_INSTANCE_A = 12;
    public static final int OFS_CURRENT_INSTANCE_B = 16;
    public static final int OFS_CURRENT_INSTANCE_C = 20;
     
    public static final int OFS_VOLTAGE_ANGLE_INSTANCE_A = 24;
    public static final int OFS_VOLTAGE_ANGLE_INSTANCE_B = 28;
    public static final int OFS_VOLTAGE_ANGLE_INSTANCE_C = 32;
     
    public static final int OFS_CURRENT_ANGLE_INSTANCE_A = 36;
    public static final int OFS_CURRENT_ANGLE_INSTANCE_B = 40;
    public static final int OFS_CURRENT_ANGLE_INSTANCE_C = 44;
     
	private Log log = LogFactory.getLog(A2RL_IS.class);
	DecimalFormat dformat = new DecimalFormat("###############0.000000");
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public A2RL_IS(byte[] rawData) {
		this.rawData = rawData;
	}
    
    public double getVOLTAGE_INSTANCE_A() throws Exception {
        return getValue(
                DataFormat.select(rawData,OFS_VOLTAGE_INSTANCE_A,LEN_DATA));
    }
    public double getVOLTAGE_INSTANCE_B() throws Exception {
    	return getValue(
                DataFormat.select(rawData,OFS_VOLTAGE_INSTANCE_B,LEN_DATA));
    }
    public double getVOLTAGE_INSTANCE_C() throws Exception {
    	return getValue(
                DataFormat.select(rawData,OFS_VOLTAGE_INSTANCE_C,LEN_DATA));
    }
    
    public double getCURRENT_INSTANCE_A() throws Exception {
        return getValue(
                DataFormat.select(rawData,OFS_CURRENT_INSTANCE_A,LEN_DATA));
    }
    public double getCURRENT_INSTANCE_B() throws Exception {
        return getValue(
                DataFormat.select(rawData,OFS_CURRENT_INSTANCE_B,LEN_DATA));
    }
    public double getCURRENT_INSTANCE_C() throws Exception {
        return getValue(
                DataFormat.select(rawData,OFS_CURRENT_INSTANCE_C,LEN_DATA));
    }
    
    public double getVOLTAGE_ANGLE_INSTANCE_A() throws Exception {
        return getValue(
                DataFormat.select(rawData,OFS_VOLTAGE_ANGLE_INSTANCE_A,LEN_DATA));
    }
    public double getVOLTAGE_ANGLE_INSTANCE_B() throws Exception {
        return getValue(
                DataFormat.select(rawData,OFS_VOLTAGE_ANGLE_INSTANCE_B,LEN_DATA));
    }
    public double getVOLTAGE_ANGLE_INSTANCE_C() throws Exception {
        return getValue(
                DataFormat.select(rawData,OFS_VOLTAGE_ANGLE_INSTANCE_C,LEN_DATA));
    }
    
    public double getCURRENT_ANGLE_INSTANCE_A() throws Exception {
        return getValue(
                DataFormat.select(rawData,OFS_CURRENT_ANGLE_INSTANCE_A,LEN_DATA));
    }
    public double getCURRENT_ANGLE_INSTANCE_B() throws Exception {
        return getValue(
                DataFormat.select(rawData,OFS_CURRENT_ANGLE_INSTANCE_B,LEN_DATA));
    }
    public double getCURRENT_ANGLE_INSTANCE_C() throws Exception {
        return getValue(
                DataFormat.select(rawData,OFS_CURRENT_ANGLE_INSTANCE_C,LEN_DATA));
    }
    
    public double getPF_A() throws Exception{
    	return getPF(getVOLTAGE_ANGLE_INSTANCE_A(), getCURRENT_ANGLE_INSTANCE_A());
    }
    public double getPF_B() throws Exception{
    	return getPF(getVOLTAGE_ANGLE_INSTANCE_B(), getCURRENT_ANGLE_INSTANCE_B());
    }
    public double getPF_C() throws Exception{
    	return getPF(getVOLTAGE_ANGLE_INSTANCE_C(), getCURRENT_ANGLE_INSTANCE_C());
    }
    
    private double getValue(byte[] data){
    	if(data.length!=4){
    		return new Double(0.0);
    	}
    	if(data[0]==0xff && data[1]==0xff){
    		return new Double(0.0);
    	}
    	try{
    		double value = (double) DataFormat.hex2unsigned16(DataFormat.select(data,0,2));
    		return Double.parseDouble(dformat.format(value+ ((double)(DataFormat.hex2unsigned16(DataFormat.select(data,2,2)))) /65536));
    	}catch(Exception e){
    		return 0.0;
    	}
    }
    
    private double getPF(double vol_angle, double curr_angle){
    	if(curr_angle >= vol_angle)
    		return Math.cos(curr_angle - vol_angle);
    	else
    		return Math.cos(360 - (vol_angle - curr_angle));
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("A2RL_IS Meter DATA[");        
            sb.append("(VOLTAGE_INSTANCE_A=").append(""+getVOLTAGE_INSTANCE_A()).append("),");
            sb.append("(VOLTAGE_INSTANCE_B=").append(""+getVOLTAGE_INSTANCE_B()).append("),");
            sb.append("(VOLTAGE_INSTANCE_C=").append(""+getVOLTAGE_INSTANCE_C()).append("),");
            sb.append("(CURRENT_INSTANCE_A=").append(""+getCURRENT_INSTANCE_A()).append("),");
            sb.append("(CURRENT_INSTANCE_B=").append(""+getCURRENT_INSTANCE_B()).append("),");
            sb.append("(CURRENT_INSTANCE_C=").append(""+getCURRENT_INSTANCE_C()).append("),");
            sb.append("(VOLTAGE_ANGLE_INSTANCE_A=").append(""+getVOLTAGE_ANGLE_INSTANCE_A()).append("),");
            sb.append("(VOLTAGE_ANGLE_INSTANCE_B=").append(""+getVOLTAGE_ANGLE_INSTANCE_B()).append("),");
            sb.append("(VOLTAGE_ANGLE_INSTANCE_C=").append(""+getVOLTAGE_ANGLE_INSTANCE_C()).append("),");
            sb.append("(PF_PHA=").append(""+getPF_A()).append("),");
            sb.append("(PF_PHB=").append(""+getPF_B()).append("),");
            sb.append("(PF_PHC=").append(""+getPF_C()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("A2RL_IS TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }

}
