/** 
 * @(#)KindOfMeteringDataLog.java       1.0 07/11/14 *
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
 
package com.aimir.fep.meter.parser.lk3410cpTable;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataFormat;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class KindOfMeteringDataLog {
	
    public static final int LEN_NBR_ERROR_OCC = 1;
    public static final int LEN_EVENT_TIME = 7;
    public static final int LEN_METERING_CODE = 1;
           
    public static final byte LOW_VOLTAGE_C = (byte)0x80;
    public static final byte LOW_VOLTAGE_B = (byte)0x40;
    public static final byte LOW_VOLTAGE_A = (byte)0x20;
    public static final byte CONNECTION_ERROR = (byte)0x10;
    public static final byte REVERSE_TRANSMISSION = (byte)0x08;
    public static final byte LOW_CURRENT_C = (byte)0x04;
    public static final byte LOW_CURRENT_B = (byte)0x02;
    public static final byte LOW_CURRENT_A = (byte)0x01;
        
	private byte[] data;
    private int ofs;
    private int cntErrorOcc;
    private int kind; //0: occurrence, 1:recovery
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public KindOfMeteringDataLog(byte[] data, int ofs, int kind) {
		this.data = data;
        this.ofs = ofs;
        this.kind = kind;
	}
	
    /**
     * DEMAND_RESET_LOG
     */
    public List getMETERING_ERROR() throws Exception  {
        
        setNBR_ERROR(ofs);
        int cnt = getNBR_ERROR();
        ofs+=LEN_NBR_ERROR_OCC;
        ArrayList<EventLogData> evList = new ArrayList<EventLogData>();
        for(int i=0; i<cnt; i++){
            
            String datetime = new DateTimeFormat(DataFormat.select(data, ofs, LEN_EVENT_TIME)).getDateTime();
            ofs+=LEN_EVENT_TIME;
            int eventValue =27;
            if(kind ==1)
                eventValue=34;
                
      //      byte code = data[ofs];
            MeterErrorFlag errorFlag = new MeterErrorFlag(data[ofs]);
   
            if (errorFlag.getLOW_VOLTAGE_C()){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(eventValue);
                if(kind == 0){
                    ev.setMsg("Low Voltage C Error");
                }else{
                    ev.setMsg("Low Voltage C Recovery");  
                }
                evList.add(ev);
            }
           
            if (errorFlag.getLOW_VOLTAGE_B()){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(eventValue+1);
                if(kind == 0){
                    ev.setMsg("Low Voltage B Error");
                }else{
                    ev.setMsg("Low Voltage B Recovery");  
                }
                evList.add(ev);
            }
            if (errorFlag.getLOW_VOLTAGE_A()){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(eventValue+2);
                if(kind == 0){
                    ev.setMsg("Low Voltage A Error");
                }else{
                    ev.setMsg("Low Voltage A Recovery");  
                }
                evList.add(ev);
            }
           
            if (errorFlag.getCONNECTION_ERROR()){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(eventValue+3);
                if(kind == 0){
                    ev.setMsg("Connection Error");
                }else{
                    ev.setMsg("Connection Error Recovery");  
                }
                evList.add(ev);
            }
            
            if (errorFlag.getREVERSE_TRANSMISSION()){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(eventValue+4);
                if(kind == 0){
                    ev.setMsg("Reverse Transmission");
                }else{
                    ev.setMsg("Reverse Transmission Recovery");  
                }
                evList.add(ev);
            }
            if (errorFlag.getLOW_CURRENT_C()){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(eventValue+5);
                if(kind == 0){
                    ev.setMsg("Low Current C Error");
                }else{
                    ev.setMsg("Low Current C Recovery");  
                }
                evList.add(ev);
            }
            
            if (errorFlag.getLOW_CURRENT_B()){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(eventValue+6);
                if(kind == 0){
                    ev.setMsg("Low Current B Error");
                }else{
                    ev.setMsg("Low Current B Recovery");  
                }
                evList.add(ev);
            }
            if (errorFlag.getLOW_CURRENT_A()){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(eventValue+7);
                if(kind == 0){
                    ev.setMsg("Low Current A Error");
                }else{
                    ev.setMsg("Low Current A Recovery");  
                }
                evList.add(ev);
            }            
            ofs+=LEN_METERING_CODE;
        }
        if (evList == null) {
            return null;
        }       
        return Collections.unmodifiableList(evList);
    }
    
    public void setNBR_ERROR(int ofs1){
        cntErrorOcc= DataFormat.hex2unsigned8(data[ofs1]);
    }
    public int  getNBR_ERROR(){
        return this.cntErrorOcc;
    }

}
