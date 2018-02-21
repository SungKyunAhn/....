/* 
 * @(#)DataBlock.java       1.0 2009-01-19 *
 * 
 * Kamstrup RID Data Block
 * Copyright (c) 2009-2010 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
/**
 * @author YK.Park
 */
package com.aimir.fep.meter.parser.kamstrup601Table;

import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Util;
import com.aimir.util.TimeUtil;


public class DataBlock {    
	
	private byte[] data = null;

    private static Log log = LogFactory.getLog(DataBlock.class);
    
    private byte[] RID = new byte[2];
    private byte[] UNIT = new byte[1];
    private byte[] LEN_REGVAL = new byte[1];
    private byte[] SIGNEXP = new byte[1];
    private byte[] COUNT = new byte[1];
    private byte[] DATA = null;
    
    private int ridcode = 0;
    private String rid = "";
    private String unit = "";
    private int count = 0;
    private int lenRegVal = 0;
    private int byteSiEx = 0;
    private int signInt = 0;
    private int signExp = 0;
    private int exp = 0;
    private double siEx = 0;
    private ArrayList regVal = null;
    
    private int pos = 0;
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public DataBlock(byte[] data) {
        this.data = data;
	}
    
    /**
     * Constructor .<p>
     * 
     * @param data - read data (header,crch,crcl)
     */
    public DataBlock(byte[] data, int offset) {
        byte[] temp = new byte[data.length - offset];
        System.arraycopy(data,offset,temp,0,temp.length);        
        this.data = temp;
        parse();
    }
    
    /**
     * Constructor .<p>
     * 
     * @param data - read data (header,crch,crcl)
     */
    public DataBlock(byte[] data, int offset, int len) {
        byte[] temp = new byte[len];
        System.arraycopy(data,offset,temp,0,len);        
        this.data = temp;
        parse();
    }
    
    public void parse()
    {
    	pos = 0;
        System.arraycopy(data, pos, RID, 0, RID.length);
        pos += RID.length;
        System.arraycopy(data, pos, UNIT, 0, UNIT.length);
        pos += UNIT.length;
        System.arraycopy(data, pos, LEN_REGVAL, 0, LEN_REGVAL.length);
        pos += LEN_REGVAL.length;
        System.arraycopy(data, pos, SIGNEXP, 0, SIGNEXP.length);
        pos += SIGNEXP.length;
        System.arraycopy(data, pos, COUNT, 0, COUNT.length);
        pos += COUNT.length;
        
        
        
        ridcode = DataUtil.getIntTo2Byte(RID);
        rid=RegisterIDTable.getRid(ridcode);//getRid
        
        if(RID[0] == (byte)0xFF || RID[1] == (byte)0xFF || 
        	UNIT[0] == (byte)0xFF || 
        	LEN_REGVAL[0] == (byte)0xFF || 
        	SIGNEXP[0] == (byte)0xFF || 
        	COUNT[0] == (byte)0xFF){
        	return;
        }
        unit=UnitTable.getUnit(DataUtil.getIntToBytes(UNIT));//getUnit
        lenRegVal=DataUtil.getIntToBytes(LEN_REGVAL);//getValueLength            
        byteSiEx= DataUtil.getIntToBytes(SIGNEXP);
        count= DataUtil.getIntToBytes(COUNT);
        signInt=(byteSiEx & 128)/128;
        signExp=(byteSiEx & 64)/64;
        exp=((byteSiEx&32) + (byteSiEx&16) + (byteSiEx&8) + (byteSiEx&4) + (byteSiEx&2) + (byteSiEx&1));
        siEx=Math.pow(-1, signInt)*Math.pow(10, Math.pow(-1, signExp)*exp);//-1^SI*-1^SE*exponent                
                           
        regVal = new ArrayList();
        
        log.debug("count : "+count);
        for(int i=0; i<count; i++){
	        DATA = new byte[lenRegVal];            
	        System.arraycopy(data, pos, DATA, 0, DATA.length);
	        pos += DATA.length;
	
	        if(DataUtil.getIntToBytes(UNIT) == UnitTable.clock || 
	        		DataUtil.getIntToBytes(UNIT) == UnitTable.date1){
	        	
	        	String regValue = Util.frontAppendNStr('0',Integer.toString(DataUtil.getIntToBytes(DATA)),6);
	        	
	        	if(DataUtil.getIntToBytes(UNIT) == UnitTable.date1){
	        		try{
		        		int curryy = (Integer.parseInt(TimeUtil.getCurrentTime().substring(0,4))/100)*100;
		        		int year  = curryy+Integer.parseInt(((String)regValue).substring(0,2));
		        		regVal.add(i, new String(""+year+""+regValue.substring(2)));
	        		}catch(Exception e){
	                    log.error(e);
	                }
	        	}
	        	else {
	        		regVal.add(i, new String(regValue));
	        		log.debug("CLOCK : "+ regValue);
	        	}
	        	
	        }else if(DataUtil.getIntToBytes(UNIT) == UnitTable.date3) {
	        	regVal.add(i, new String(Util.frontAppendNStr('0',Integer.toString(DataUtil.getIntToBytes(DATA)),4)));
	        }else {
	        	////
	        	regVal.add(i, new Double(DataUtil.getIntToBytes(DATA)*siEx));
	        }
        }
    }

	public byte getSIGNEXP(){
		return this.SIGNEXP[0];
	}
    
    public int getLength(){
    	return this.pos;
    }

    public ArrayList getValue()
    {
    	return this.regVal;
    }
    
    public String getRID()
    {
    	return this.rid;
    }
    
    public int getRIDCode()
    {
    	return this.ridcode;
    }
    
    public String getUnit()
    {
    	return this.unit;
    }
    public int getCount()
    {
    	return this.count;
    }
    
    public String toString()
    {
        log.debug(Util.getHexString(data));
        StringBuffer sb = new StringBuffer();
        sb.append("RID=["+rid+"]\n");
        sb.append("RegVal=["+regVal+"]\n");
        sb.append("Unit=["+unit+"]\n");
        sb.append("Count=["+count+"]\n");
        
        return sb.toString();
    }
}