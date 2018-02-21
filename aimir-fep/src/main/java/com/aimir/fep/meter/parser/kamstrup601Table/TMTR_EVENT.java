/* 
 * @(#)TMTR_EVENT.java       1.0 2009-02-25 *
 * 
 * Meter Month Data
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
/**
 * @author Kang, Soyi
 */
package com.aimir.fep.meter.parser.kamstrup601Table;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;

public class TMTR_EVENT implements java.io.Serializable{    

    public static final String TABLE_KIND = "EVENT";
    public static final int TABLE_CODE = 4;
    public static final int OFS_DATA = 0;
	private byte[] rawData = null;
	private String table_kind = null;
    
    private EventLogData[] eventdata;
    protected ArrayList eventDataArray = null;
    private Map map = new LinkedHashMap(16,0.74f,false);
    
    private static Log log = LogFactory.getLog(TMTR_EVENT.class);
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public TMTR_EVENT(byte[] rawData, String table_kind) {
        this.rawData = rawData;
        this.table_kind = table_kind;
        try{
        	map.put(new Integer(0), "No Irregularities");
        	map.put(new Integer(1), "Supply Voltage has been cut off");
        	map.put(new Integer(8), "Temp. Sensor T1 outside measuring rang");
        	map.put(new Integer(4), "Temp. Sensor T2 outside measuring rang");
        	map.put(new Integer(32), "Temp. Sensor T3 outside measuring range");
        	map.put(new Integer(64), "Leak in the cold-water system");
        	map.put(new Integer(256), "Leak in the heating system");
        	map.put(new Integer(512), "Burst in the heating system");
        	map.put(new Integer(16), "Flow sensor V1 datacomm error, signal too low or wrong flow direction");
        	map.put(new Integer(1024), "Flow sensor V2 datacomm error, signal too low or wrong flow direction");
        	map.put(new Integer(2048), "Flow sensor V1 Wrong meter factor");
        	map.put(new Integer(128), "Flow sensor V2 Wrong meter factor");
        	map.put(new Integer(4096), "Flow sensor V1 Signal too low(Air)");
        	map.put(new Integer(8192), "Flow sensor V2 Signal too low(Air)");
        	map.put(new Integer(16384), "Flow sensor V1 Wrong flow direction");
        	map.put(new Integer(32768), "Flow sensor V2 Wrong flow direction");
        	
            parse();
        }catch(Exception e)
        {
            log.error(e,e);
        }
	}
	
    public void parse()
    throws Exception
    {
        List dbList = new ArrayList();
        int offset = OFS_DATA;
        int len = 0;
        DataBlock db = null;
        while(offset < rawData.length){
        	db = new DataBlock(rawData,offset);
        	dbList.add(db);
        	offset += db.getLength();
        }

        Iterator it = dbList.iterator();

        ArrayList count = new ArrayList();

        ArrayList date = null;
        ArrayList time = null;
        ArrayList event = null;

        while(it.hasNext()){

            db = (DataBlock)it.next();
            int ridcode = db.getRIDCode();
            count.add(new Integer(db.getCount()));
            
            switch(ridcode){
        	case RegisterIDTable.DATE : date = db.getValue();break;
        	case RegisterIDTable.E1 : break;
        	case RegisterIDTable.E2 : break;
        	case RegisterIDTable.E3 : break;
        	case RegisterIDTable.E4 : break;
        	case RegisterIDTable.E5 : break;
        	case RegisterIDTable.E6 : break;
        	case RegisterIDTable.E7 : break;
        	case RegisterIDTable.E8 : break;
        	case RegisterIDTable.E9 : break;
        	case RegisterIDTable.TA2 : break;
        	case RegisterIDTable.TA3 : break;
        	case RegisterIDTable.V1 : break;
        	case RegisterIDTable.V2 : break;
        	case RegisterIDTable.VA : break;
        	case RegisterIDTable.VB : break;
        	case RegisterIDTable.M1 : break;
        	case RegisterIDTable.M2 : break;
        	case RegisterIDTable.HR : break;
        	case RegisterIDTable.INFOEVENT : break;
        	case RegisterIDTable.CLOCK : time = db.getValue();break;
        	case RegisterIDTable.INFO : event =  db.getValue();break;
        	case RegisterIDTable.T1 : break;
        	case RegisterIDTable.T2 : break;
        	case RegisterIDTable.T3 : break;
        	case RegisterIDTable.T4 : break;
        	case RegisterIDTable.T1_T2 : break;
        	case RegisterIDTable.P1 : break;
        	case RegisterIDTable.P2 : break;
        	case RegisterIDTable.FLOW1 : break;
        	case RegisterIDTable.FLOW2 : break;
        	case RegisterIDTable.EFFEKT1 : break;
        	case RegisterIDTable.MAX_FLOW1DATE1 : break;
        	case RegisterIDTable.MAX_FLOW1 : break;
        	case RegisterIDTable.MIN_FLOW1DATE1: break;
        	case RegisterIDTable.MIN_FLOW1 : break;
        	case RegisterIDTable.MAX_EFFEKT1DATE1 : break;
        	case RegisterIDTable.MAX_EFFEKT1 : break;
        	case RegisterIDTable.MIN_EFFEKT1DATE1 : break;
        	case RegisterIDTable.MIN_EFFEKT1 : break;
        	case RegisterIDTable.MAX_FLOW1DATE2 : break;
        	case RegisterIDTable.MAX_FLOW2 : break;
        	case RegisterIDTable.MIN_FLOW1DATE2 : break;
        	case RegisterIDTable.MIN_FLOW2 : break;
        	case RegisterIDTable.MAX_EFFEKT1DATE2 : break;
        	case RegisterIDTable.MAX_EFFEKT2 : break;
        	case RegisterIDTable.MIN_EFFEKT1DATE2 : break;
        	case RegisterIDTable.MIN_EFFEKT2 : break;
        	case RegisterIDTable.AVR_T1 : break;
        	case RegisterIDTable.AVR_T2 : break;
        	case RegisterIDTable.AVR2_T1 : break;
        	case RegisterIDTable.AVR2_T2 : break;
        	case RegisterIDTable.TL2 : break;
        	case RegisterIDTable.TL3 : break;
        	case RegisterIDTable.XDAY : break;
        	case RegisterIDTable.PROG_NO : break;
        	case RegisterIDTable.CONFIG_NO_1 : break;
        	case RegisterIDTable.CONFIG_NO_2 : break;
        	case RegisterIDTable.SERIE_NO : break;
        	case RegisterIDTable.METER_NO_2 : break;
        	case RegisterIDTable.METER_NO_1 : break;
        	case RegisterIDTable.METER_NO_VA : break;
        	case RegisterIDTable.METER_NO_VB : break;
        	case RegisterIDTable.METER_TYPE : break;
        	case RegisterIDTable.CHECK_SUM_1 : break;
        	case RegisterIDTable.HIGH_RES : break;
        	case RegisterIDTable.TOPMODUL_ID : break;
        	case RegisterIDTable.BOTMODUL_ID : break;
            }
        }
        
        eventDataArray = new ArrayList();
        int cnt = ((Integer)count.get(0)).intValue();
        
        for(int i = 0; i < cnt; i++)
        {
        	int code = (int)((Double)event.get(i)).doubleValue();
        	log.debug("(String)date.get(i).substring(4) :"+ ((String)date.get(i)).substring(4));
        	if(!(((String)date.get(i)).substring(4)).equals("0000")){
	        	EventLogData event1 = new EventLogData();
	        	event1.setDate((String)date.get(i));
	        	event1.setTime("000000");
	        	event1.setKind("STE");
	        	event1.setFlag(code);
	        	event1.setMsg((String)map.get(new Integer(code)));
	        	
	        	eventDataArray.add(event1);
        	}
        }
        
        Object[] obj = eventDataArray.toArray();        
        eventdata = new EventLogData[eventDataArray.size()];
        for(int i = 0; i < obj.length; i++){
        	eventdata[i] = (EventLogData)obj[i];
        }
    }
    
    public EventLogData[] getData()
    {
        return this.eventdata;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("TMTR_EVENT DATA["); 
            for(int i = 0; i < eventdata.length; i++){
                sb.append(eventdata[i].toString());
            }
            sb.append("]\n");
        }catch(Exception e){
            log.warn("TMTR_EVENT TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }       
}
