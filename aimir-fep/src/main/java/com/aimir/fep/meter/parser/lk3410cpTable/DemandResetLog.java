/** 
 * @(#)DemandResetLog.java       1.0 07/11/14 *
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataFormat;
/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class DemandResetLog {
	
    public static final int LEN_NBR_DEMAND_RESET = 1;
    public static final int LEN_EVENT_TIME = 7;
    public static final int LEN_DR_CODE = 1;
    public static final int LEN_DEMAND_COUNT = 2;
    public static final int LEN_ERROR_OCC_CODE = 1;
    
    public static final byte SEASON_CHANGED = (byte)0x80;
    public static final byte PROGRAM_UPDATE = (byte)0x40;
    public static final byte SW_DEMAND_RESET = (byte)0x20;
    public static final byte MANUALLY_DEMAND_RESET = (byte)0x10;
    public static final byte DATE_CHANGED = (byte)0x08;
    public static final byte AFTER_DEMAND_RESET  = (byte)0x04;
    public static final byte IRREGULAR_METERING_DATETIME  = (byte)0x02;
    public static final byte REGULAR_METERING_DATETIME  = (byte)0x01;
        
	private byte[] data;
    private int ofs;
    private int cntDemandReset;
    private static Log log = LogFactory.getLog(DemandResetLog.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public DemandResetLog(byte[] data, int ofs) {
		this.data = data;
        this.ofs = ofs;
	}
	
    /**
     * DEMAND_RESET_LOG
     */
    public List getDEMAND_RESET_LOG() throws Exception  {
        
        setNBR_DEMAND_RESET(ofs);
        int cnt = getNBR_DEMAND_RESET();
        ofs+=LEN_NBR_DEMAND_RESET;
        ArrayList evList = new ArrayList();
        for(int i=0; i<cnt; i++){
            
            String datetime = new DateTimeFormat(DataFormat.select(data, ofs, LEN_EVENT_TIME)).getDateTime();
            ofs+=LEN_EVENT_TIME;
            
            byte code = data[ofs];
            if(code== SEASON_CHANGED){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(1);
                ev.setMsg("Season changed");
                evList.add(ev);
            }
            if(code== PROGRAM_UPDATE){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(2);
                ev.setMsg("Program Updated");
                evList.add(ev);
            }
            if(code== SW_DEMAND_RESET){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(3);
                ev.setMsg("SW Demand Reset");
                evList.add(ev);
            }
            if(code== MANUALLY_DEMAND_RESET){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(4);
                ev.setMsg("Manually Demand Reset");
                evList.add(ev);
            }
            if(code== DATE_CHANGED){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(5);
                ev.setMsg("Date Changed");
                evList.add(ev);
            }
            if(code== AFTER_DEMAND_RESET){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(6);
                ev.setMsg("After Demand Reset");
                evList.add(ev);
            }
            if(code== IRREGULAR_METERING_DATETIME){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(7);
                ev.setMsg("Irregular Metering Datetime");
                evList.add(ev);
            }
            if(code== REGULAR_METERING_DATETIME){
                EventLogData ev = new EventLogData();
                ev.setDate(datetime.substring(0,8));
                ev.setTime(datetime.substring(8,14));
                ev.setKind("STE");
                ev.setFlag(8);
                ev.setMsg("Regular Metering Datetime");
                evList.add(ev);
            }
            ofs+=LEN_ERROR_OCC_CODE;
        }
        if (evList == null) {
            return null;
        }       
        return Collections.unmodifiableList(evList);
    }
    
    public void setNBR_DEMAND_RESET(int ofs1){
        this.cntDemandReset = DataFormat.hex2unsigned8(data[ofs1]);
    }
    
    public int  getNBR_DEMAND_RESET(){
        return this.cntDemandReset;
    }
    
    public String getMAXDemandTime() throws Exception  {
        
        int ofsTmp =0;
        int cnt = DataFormat.hex2unsigned8(data[ofsTmp]);
        String max ="00000000000000";
        ofsTmp+=1;
        for(int i=0; i<cnt; i++){
            String datetime = new DateTimeFormat(DataFormat.select(data, ofsTmp, LEN_EVENT_TIME)).getDateTime();
      
            ofsTmp +=8;
            if(Long.parseLong(max) < Long.parseLong(datetime)){
                max = datetime;
            }
        }
        log.debug("max demand time : "+ max);
        return max;
    }

}
