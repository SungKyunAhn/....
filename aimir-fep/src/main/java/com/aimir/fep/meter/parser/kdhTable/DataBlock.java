/* 
 * @(#)DataBlock.java       1.0 2008-06-02 *
 * 
 * Meter Information
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
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
package com.aimir.fep.meter.parser.kdhTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;


public class DataBlock {    

    public static final int LEN_DRH = 4;
	public static final int LEN_DIF = 1;
	public static final int LEN_DIFE = 1;
	public static final int LEN_VIF = 1;
	public static final int LEN_VIFE = 1;

	public static final int OFS_DIF = 0;
    public static final int OFS_DIFE = 1;
    protected int OFS_VIF = 2;
    protected int OFS_VIFE = 3;
    protected int OFS_ACTUAL_DATA = 4;
	
	private byte[] rawData = null;

    private static Log log = LogFactory.getLog(DataBlock.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public DataBlock(byte[] rawData) {
        this.rawData = rawData;
	}
    
    /**
     * Constructor .<p>
     * 
     * @param data - read data (header,crch,crcl)
     */
    public DataBlock(byte[] data, int offset, int len) {
        byte[] temp = new byte[len];
        System.arraycopy(data,offset,temp,0,len);        
        this.rawData = temp;
    }
    
    public DIF getDIF()
    {
        int offset = LEN_DRH- (LEN_DIF+LEN_DIFE+LEN_VIF+LEN_VIFE);
        DIF dif = new DIF(rawData[offset]);
        return dif;
    }
    
    public DIFE getDIFE()
    {
        DIFE dife = null;
        int offset = LEN_DRH-(LEN_DIFE+LEN_VIF+LEN_VIFE);
        if(getDIF().isDIFE()){
            dife = new DIFE(rawData[offset]);
        }else{
            dife = new DIFE((byte)1);//default1
        }        
        return dife;
    }
    
    public VIF getVIF()
    {

        int offset = LEN_DRH-(LEN_VIF+LEN_VIFE);
        if(!getDIF().isDIFE()){
            --offset;
        }
        VIF vif = new VIF(rawData[offset]);
        return vif;
    }
    
    public VIFE getVIFE()
    {
        VIFE vife = null;
        int offset = LEN_DRH-(LEN_VIFE);
        if(!getDIF().isDIFE()){
            --offset;
        }
        if(getVIF().isVIFE()){
            vife = new VIFE(rawData[offset]);
        }else{
            vife = new VIFE((byte)0);
        }
        return vife;
    }
    
    public Object[] getActualData() throws Exception
    {
        Object[] object = null;
        
        int count = getDIFE().getCount();
        int len = getDIF().getDataFieldLength();
        int dif = getDIF().getDataField();
        VIF vif = getVIF();
        int vife = getVIFE().getVIFE();
        
        int offset = LEN_DRH;
        if(!getDIF().isDIFE()){
            --offset;
        }
        if(!getVIF().isVIFE()){
            --offset;
        }
        
        object = new Object[count];
        
        if((dif == DIF.DATA_UNKNOWN ||
            dif == DIF.DATA_UNKNOWN ||
            dif == DIF.DATA_NODATA)  && 
           (vif.getVIF() != VIF.FIELD_DATE &&
            vif.getVIF() != VIF.FIELD_DATETIME))
        {
            return null;
        }
        int LEN_ACTUAL_DATA = 0;
        
        switch(dif)
        {
            case DIF.DATA_8BIT_INTEGER  : LEN_ACTUAL_DATA = 1;
                break;
            case DIF.DATA_16BIT_INTEGER : LEN_ACTUAL_DATA = 2;
                break;
            case DIF.DATA_24BIT_INTEGER : LEN_ACTUAL_DATA = 3; 
                break;  
            case DIF.DATA_32BIT_INTEGER : LEN_ACTUAL_DATA = 4;
                break;
            case DIF.DATA_2DIGIT_BCD    : LEN_ACTUAL_DATA = 1;
                break;
            case DIF.DATA_4DIGIT_BCD    : LEN_ACTUAL_DATA = 2;
                break;
            case DIF.DATA_6DIGIT_BCD    : LEN_ACTUAL_DATA = 3;
                break;
            case DIF.DATA_8DIGIT_BCD    : LEN_ACTUAL_DATA = 4;
                break;
        }
        if(vif.getVIF() == VIF.FIELD_DATE){
            LEN_ACTUAL_DATA = 2;
        }
        if(vif.getVIF() == VIF.FIELD_DATETIME){
            LEN_ACTUAL_DATA = 4;
        }
 
        for(int i = 0; i < count; i++){
            object[i] = new Object();
            object[i] = getObject(dif,vif,offset,LEN_ACTUAL_DATA);
            offset += LEN_ACTUAL_DATA;
        }

        return object;
    }

    private Object getObject(int dif,VIF vif, int offset, int len) throws Exception
    {
        //log.debug("offset="+offset+",len="+len);
        Object obj = new Object();
        if(vif.getVIF() == VIF.FIELD_DATE)
        {
            obj = new DATE(rawData,offset,len);
        }
        else if(vif.getVIF() == VIF.FIELD_DATETIME)
        {
            obj = new DATETIME(rawData,offset,len);
        }
        else
        {
            switch(dif)
            {
                case DIF.DATA_8BIT_INTEGER  : obj = new Double(DataFormat.hex2dec(rawData,offset,len)*vif.getMultiply());
                    break;
                case DIF.DATA_16BIT_INTEGER : obj = new Double(DataFormat.hex2dec(rawData,offset,len)*vif.getMultiply());
                    break;
                case DIF.DATA_24BIT_INTEGER : obj = new Double(DataFormat.hex2dec(rawData,offset,len)*vif.getMultiply()); 
                    break;  
                case DIF.DATA_32BIT_INTEGER : obj = new Double(DataFormat.hex2dec(rawData,offset,len)*vif.getMultiply());
                    break;
                case DIF.DATA_2DIGIT_BCD    : obj = new Double(DataFormat.bcd2dec(rawData,offset,len)*vif.getMultiply());
                    break;
                case DIF.DATA_4DIGIT_BCD    : obj = new Double(DataFormat.bcd2dec(rawData,offset,len)*vif.getMultiply());
                    break;
                case DIF.DATA_6DIGIT_BCD    : obj = new Double(DataFormat.bcd2dec(rawData,offset,len)*vif.getMultiply());
                    break;
                case DIF.DATA_8DIGIT_BCD    : obj = new Double(DataFormat.bcd2dec(rawData,offset,len)*vif.getMultiply());
                    break;
            }
        }

        return obj;
    }
    
    public int getLength()
    {
        //log.debug("data=>"+Util.getHexString(this.rawData));
        //log.debug("count["+getDIFE().getCount()+"],length["+getDIF().getDataFieldLength()+"]");
        int len = 0;
        len +=  LEN_DIF;
        if(getDIF().isDIFE()){
            len += LEN_DIFE;
            len += getDIFE().getCount()*getDIF().getDataFieldLength();
        }else{
            if(getVIF().getVIF() == VIF.FIELD_DATE){
                len += DATE.LENGTH;
            }
            else if(getVIF().getVIF() == VIF.FIELD_DATETIME){
                len += DATETIME.LENGTH;
            }
            if(getDIF().getDataField() != DIF.DATA_NODATA){
                len += getDIFE().getCount()*getDIF().getDataFieldLength();
            }            
        }
        len += LEN_VIF;
        if(getVIF().isVIFE()){
            len += LEN_VIFE;
        }
        return len;
    }

    public String toString()
    {
        log.debug(Util.getHexString(rawData));
        StringBuffer sb = new StringBuffer();
        sb.append(getDIF().toString()+"\n");
        sb.append(getDIFE().toString()+"\n");
        sb.append(getVIF().toString()+"\n");
        sb.append(getVIFE().toString()+"\n");
        
        try{
            Object[] obj = null;
            obj = getActualData();
            for(int i = 0; i < obj.length; i++){
                sb.append(obj[i].toString()+"\n");
            }
        }catch(Exception e){
            log.error(e,e);
        }
        
        return sb.toString();
    }
}