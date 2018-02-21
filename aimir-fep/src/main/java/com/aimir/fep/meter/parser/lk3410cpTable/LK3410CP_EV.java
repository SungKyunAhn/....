/* 
 * @(#)LK3410CP_EV.java       1.0 07/11/08 *
 * 
 * Event Log.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.lk3410cpTable;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataFormat;

/**
 * @author Kang Soyi ksoyi@nuritelecom.com
 */
public class LK3410CP_EV {
    
    public static final int OFS_NBR_EVT_LOG = 0;    
    public static final int LEN_NBR_EVT_LOG = 1;

    public static final int LEN_DEMAND_RESET_EVENT =8;
    public static final int LEN_ERROR_OCC =8;
    public static final int LEN_POWER_EVENT =7;
        
    private EventLogData[] eventdata;
    private String starttime;
    private String endtime;
    private int totalDemandCount;
    
	private byte[] rawData = null;
    
    private static Log log = LogFactory.getLog(LK3410CP_EV.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public LK3410CP_EV(byte[] rawData) {
		this.rawData = rawData;
        parseEvent();
	}
    
    public EventLogData[] getEvent() {
        return this.eventdata;
    }
    
    public int getTotalDemandCnt() throws Exception{
        return this.totalDemandCount;
    }
    public String getDemandTime() throws Exception{
        return new DemandResetLog(rawData, 0).getMAXDemandTime();
    }

    private void parseEvent(){
        
        try {
            int ofs=0;
            //DEMAND_RESET
            DemandResetLog demandLog = new DemandResetLog(rawData, ofs);
            List list = demandLog.getDEMAND_RESET_LOG();
            EventLogData[] demand = null;
            if(list != null && list.size()>0){
                int cnt = list.size();
                demand = new EventLogData[cnt];
                Iterator iter = list.iterator();
                int i=0;
                while ( iter.hasNext() )
                {
                    demand[i] = (EventLogData) iter.next();
                    i++;
                }
            }
  
            ofs += 1;
            ofs += LEN_DEMAND_RESET_EVENT*demandLog.getNBR_DEMAND_RESET();
            log.debug(" DEMAND_RESET ofs :"+ ofs);
            
            totalDemandCount = DataFormat.hex2unsigned16(DataFormat.LSB2MSB(DataFormat.select(rawData, ofs, 2)));
            
            ofs+=2;
            //ERROR_OCC
            KindOfMeteringDataLog meteringLog = new KindOfMeteringDataLog(rawData, ofs, 0);
            list = meteringLog.getMETERING_ERROR();
            EventLogData[] metering = null;
            if(list != null && list.size()>0){
                int cnt = list.size();
                metering = new EventLogData[cnt];
                
                Iterator iter = list.iterator();
                int i=0;
                while ( iter.hasNext() ){
                    metering[i] = (EventLogData) iter.next();
                    i++;
                }
            }
            ofs += 1;
            ofs += LEN_ERROR_OCC*meteringLog.getNBR_ERROR();
            log.debug(" ERROR_OCC ofs :"+ ofs);
            //ERROR_RECOVERY
            KindOfMeteringDataLog meteringLog2 = new KindOfMeteringDataLog(rawData, ofs, 1);
            list = meteringLog2.getMETERING_ERROR();
            EventLogData[] metering2 = null;
            if(list != null && list.size()>0){
                int cnt = list.size();
                metering2 = new EventLogData[cnt];
                Iterator iter = list.iterator();
                int i=0;
                while ( iter.hasNext() ){
                    metering2[i] = (EventLogData) iter.next();
                    i++;
                }
            }
            ofs += 1;
            ofs += LEN_ERROR_OCC*meteringLog2.getNBR_ERROR();
            log.debug(" ERROR_rec ofs :"+ ofs);  
            //Missing phase
            int eventValue = 19;
            EventLogData[][] phaseLog = new EventLogData[6][];
            log.debug("phaseLog.length() :"+phaseLog.length);
            for (int i=0; i<6; i++){
                MissingPhaseLog phase = new MissingPhaseLog(rawData, ofs, eventValue++);
                phaseLog[i] = phase.getMISSING_PHASE();
                if(phaseLog[i]!=null){
                    log.debug("phaseLog["+i+"].length :"+ phaseLog[i].length);
                }else{
                    log.debug("phaseLog["+i+"] is null");
                }
                ofs+=1;
                ofs += LEN_POWER_EVENT*phase.getNBR_MISSING_PHASE();
            }
            log.debug(" phaseLog ofs :"+ ofs);  
            
            //Last Outage_time
            LastOutageTime lastOutage = new LastOutageTime(rawData, ofs);
            EventLogData[] lastOutageEvent = lastOutage.getLastOutageTime();
                        
            ofs += LEN_POWER_EVENT;
            ofs += LEN_POWER_EVENT;
            log.debug(" POWER_EVENT ofs :"+ ofs);  
            
            //OUTAGE, Recovery
            eventValue = 25;
            EventLogData[][] outageLog = new EventLogData[2][];
            for (int i=0; i<2; i++){
                MissingPhaseLog outage = new MissingPhaseLog(rawData, ofs, eventValue++);
                outageLog[i] = outage.getMISSING_PHASE();
                
                ofs+=1;
                ofs += LEN_POWER_EVENT*outage.getNBR_MISSING_PHASE();
            }
            log.debug(" POWER_EVENT ofs :"+ ofs);  
            //TOTAL CONCAT
            int totalEventCount = 0;
            if(demand!=null )
                totalEventCount += demand.length;
            if(metering!=null)
                totalEventCount += metering.length;
            if(metering2!=null)
                totalEventCount += metering2.length;
            for(int i=0; i<phaseLog.length; i++){
                if(phaseLog[i]!=null && phaseLog[i].length>0)
                    totalEventCount += phaseLog[i].length;
            }
            if(lastOutageEvent!=null)
            	totalEventCount += lastOutageEvent.length;
            for(int i=0; i<outageLog.length; i++){
                if(outageLog[i]!=null && outageLog[i].length>0)
                    totalEventCount += outageLog[i].length;
            }
            
            log.debug("totalEventCount :"+ totalEventCount);
            if(totalEventCount>0){
                EventLogData[] ev = new EventLogData[totalEventCount];
                int offs =0;
                if(demand!=null){
                    System.arraycopy(demand, 0, ev, offs, demand.length);
                    offs+=demand.length;
                }
                if(metering!=null){
                    System.arraycopy(metering, 0, ev, offs, metering.length);
                    offs+=metering.length;
                }
                if(metering2!=null){
                    System.arraycopy(metering2, 0, ev, offs, metering2.length);
                    offs+=metering2.length;
                }
                for(int i=0; i<phaseLog.length; i++){
                    if(phaseLog[i]!=null && phaseLog[i].length>0){
                        System.arraycopy(phaseLog[i], 0, ev, offs, phaseLog[i].length);
                        offs+= phaseLog[i].length;
                    }
                }
                if(lastOutageEvent !=null && lastOutageEvent.length>0){
                	System.arraycopy(lastOutageEvent, 0, ev, offs, lastOutageEvent.length);
                    offs+=lastOutageEvent.length;
                }
                for(int i=0; i<outageLog.length; i++){
                    if(outageLog[i]!=null && outageLog[i].length>0){
                        System.arraycopy(outageLog[i], 0, ev, offs, outageLog[i].length);
                        offs+= outageLog[i].length;
                    }
                }
                eventdata = ev;
            }
            
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

}
