package com.aimir.fep.meter.parser;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PowerQualityMonitor;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_CB;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_EV;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_IS;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_LP;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_MDM;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_MTR;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_PB;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_PQ;
import com.aimir.fep.meter.parser.a2rlTable.MeterErrorFlag;
import com.aimir.fep.meter.parser.a2rlTable.MeterStatusFlag;
import com.aimir.fep.meter.parser.a2rlTable.MeterWarningFlag;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing A2RL Meter Data
 *
 * @author Kang, Soyi
 * @param <K>
 * @$Date: 2008-10-21 12:00:15 +0900 $,
 */
public class A2RL<K> extends MeterDataParser implements java.io.Serializable 
{
	private static final long serialVersionUID = 1223975222452888314L;

	private static Log log = LogFactory.getLog(A2RL.class);
    
    private byte[] rawData = null;
    private int lpcount;
    private Double lp = null;
    private Double lpValue = null;
    private String meterId = null;
    private Double ke =1.0;
    private Double ct = 1.0;
    private Double vt = 1.0;
    private int flag = 0;
    
    private byte[] mdm = null;
    private byte[] mtr = null;
    private byte[] cb = null;
    private byte[] pb = null;
    private byte[] LPD = null;
    private byte[] ev = null;
    private byte[] is = null;
    private byte[] pq = null;
    
    private A2RL_MDM nuri_mdm = null;
    private A2RL_MTR nuri_mtr = null;
    private A2RL_CB nuri_cb = null;
    private A2RL_PB nuri_pb = null;
    private A2RL_LP nuri_lp = null;
    private A2RL_EV nuri_ev = null;
    private A2RL_IS nuri_is = null;
    private A2RL_PQ nuri_pq = null;
    
    /**
     * constructor
     */
    public A2RL()
    {
    }

    /**
     * get LP
     */
    public Double getLp()
    {
        return this.lp;
    }

    /**
     * get LP Value ( lp * pulse divider )
     */
    public Double getLpValue()
    {
    	if(this.lpValue ==null)
    		lpValue = getCurrentActive();
       
    	return this.lpValue;
    }

    /**
     * get meter id
     * @return meter id
     */
    public String getMeterId()
    {
        return this.meterId;
    }

    /**
     * get raw Data
     */
    public byte[] getRawData()
    {
        return this.rawData;
    }

    public int getLength()
    {
        return this.rawData.length;
    }
    
    public int getLPCount(){
    	return this.lpcount;
    }

    /**
     * parseing Energy Meter Data of A2RL Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
        int offset = 0;
        int LEN_MDM_INFO = 30;
        log.debug("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));
        if (data.length < 68) { //MDM +MTI

            if(data.length == 30)
            {
                byte[] b = new byte[LEN_MDM_INFO];
                System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
                mdm = b;
                log.debug("[NURI_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
                nuri_mdm = new A2RL_MDM(mdm);
                log.debug(nuri_mdm);
            }
            else
            {
                log.error("[NURI_A2RL] Data total length[" + data.length + "] is invalid");
            }
            
            return;
        }
            
        byte[] b = new byte[LEN_MDM_INFO];
        System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
        mdm = b;
        offset += LEN_MDM_INFO;
        log.debug("[NURI_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
        String tbName = new String(data,offset,3);
        offset += 3;
        log.debug("[NURI_MTR] len :"+Util.getHexString(DataFormat.select(data,offset,2)));
        int len = DataFormat.hex2unsigned16(
                DataFormat.LSB2MSB(
                    DataFormat.select(data,offset,2)));
        log.debug("MTI length : "+len);
        offset += 2;
        offset += 1;
        len -=6;
        b = new byte[len];
        System.arraycopy(data,offset,b,0,len);
        offset += len;
        if(tbName.equals("MTI"))
        {
            mtr = b;
            log.debug("[NURI_MTR] len=["+len+"] data=>"+Util.getHexString(mtr));
        }
        int totlen = data.length;

        while(offset < totlen){
        	if(totlen-offset <5){
        		break;
        	}
            tbName = new String(data,offset,3);
            offset += 3;
            len = 0;//msb ->lsb
            len = DataFormat.hex2unsigned16(
                      DataFormat.LSB2MSB(
                          DataFormat.select(data,offset,2)));
            len -= 5;
            offset += 2;
            log.debug("offset=["+offset+"]");
            log.debug("len=["+len+"]");

            b = new byte[len];
            System.arraycopy(data,offset,b,0,len);
            offset += len;

            if(tbName.equals("TPB"))
            {
                pb = b;
                log.debug("[NURI_PB] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("TCB"))
            {
                cb = b;
                log.debug("[NURI_CB] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("LPD"))
            {
                LPD = b;
                log.debug("[NURI_LP] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("IST"))
            {
                is = b;
                log.debug("[NURI_IS] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("ELD"))
            {
                ev = b;
                log.debug("[NURI_EV] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("PQM"))
            {
                pq = b;
                log.debug("[A2RL_PQ] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else 
            {
                log.debug("unknown table=>"+tbName);
            }
        }
        
        nuri_mdm = new A2RL_MDM(mdm);
        log.debug(nuri_mdm);
        nuri_mtr = new A2RL_MTR(mtr);
        log.debug(nuri_mtr);
        
        if(mtr != null){
            this.meterId = nuri_mtr.getMETER_ID();
            this.ke = new Double(nuri_mtr.getMETER_CONSTANT());
            this.ct = nuri_mtr.getCT_RATIO();
            this.vt = nuri_mtr.getVT_RATIO();
            this.meterTime =  nuri_mtr.getDateTime();
        }
        
        if(cb!= null){
            nuri_cb = new A2RL_CB(cb); 
        }

        if(ev != null){
            nuri_ev = new A2RL_EV(ev); 
        }
        
        if(pb != null){
            nuri_pb = new A2RL_PB(pb);
        }

        if(LPD != null){
            nuri_lp = new A2RL_LP(LPD, ke.doubleValue(), ct.doubleValue(), vt.doubleValue());
        }
      
        if(is != null){
            nuri_is = new A2RL_IS(is);
        }
        
        if(pq != null){
            nuri_pq = new A2RL_PQ(pq);
        }
        log.debug("NURI_A2RL Data Parse Finished :: DATA["+toString()+"]");
    }

    /**
     * get flag
     * @return flag measurement flag
     */
    public int getFlag()
    {
        return this.flag;
    }

    /**
     * set flag
     * @param flag measurement flag
     */
    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
       
        sb.append("NURI_A2RL Meter DATA[");
        sb.append("(meterId=").append(meterId).append("),");
        sb.append("]\n");

        return sb.toString();
    }
    
    public String getLPChannelMap(){
        String res ="";

        res+="ch1=Active Energy[kWh],v1=Active Power[kW],";
        res+="ch2=Reactive Energy[kVarh],v2=Reactive Power[kVar],";
        res+="pf=PF";
        
        return res;
    }
    
    public int getLPChannelCount(){
        try{
            if(LPD != null){
                return 2*2+1;
            }
            else{
                return 5;//
            }
        } catch(Exception e){
        }
        return 5;//
    }

    public int getResolution(){

        int resol = 0;
        try{
            if(LPD!= null){
                resol = nuri_lp.getINTERVAL_TIME();
            }
            else{
                return resol;
            }
        } catch(Exception e){
            
        }
        return resol;
    }
    
    public String getMeterLog(){
        
        StringBuffer sb = new StringBuffer();
        if(mtr != null){
            try
            {
            	MeterWarningFlag war = nuri_mtr.getMeterWarningFlag();
            	MeterErrorFlag err = nuri_mtr.getMeterErrorFlag();
            	MeterStatusFlag sta = nuri_mtr.getMeterStatusFlag();
            	
                sb.append(nuri_mtr.getMeterWarningFlag().getLog());
                sb.append(nuri_mtr.getMeterErrorFlag().getLog());
                sb.append(nuri_mtr.getMeterStatusFlag().getLog());
            }
            catch (Exception e){log.warn("get meter log error",e);}
        }
        return sb.toString();
    }
    
    public String getBillingDay() throws Exception {
    	 String billday = "";
         return billday;
    }
    
    public double getKe() throws Exception{
        return this.ke.doubleValue();
    }
        
  //  @SuppressWarnings("unchecked")
    public LPData[] getLPData(){
        
        try{
            if(LPD != null)
                return nuri_lp.parse();
            else
                return null;
        }catch(Exception e){
            log.error("lp parse error",e);
        }
        return null;
    }
    
	public Double getCurrentActive(){
        try{
            if(LPD != null)
                return new Double(nuri_lp.getCurrentActive());
            else
                return null;
        }catch(Exception e){
            log.error("lp getCurrentActive error",e);
        }
        return null;
	}
	
	public Double getCurrentReactive(){
        try{
            if(LPD != null)
                return new Double(nuri_lp.getCurrentReactive());
            else
                return null;
        }catch(Exception e){
            log.error("lp getCurrentReactive error",e);
        }
        return null;
	}
    
	public TOU_BLOCK[] getPrevBilling(){
    	
    	if(pb != null){
            try{
                TOU_BLOCK[] blk = nuri_pb.getTOU_BLOCK();
             //   blk[0].setResetTime(nuri_ev.getDemandTime());
             //   blk[0].setResetCount(nuri_ev.getTotalDemandCnt());
                return blk;  
            }catch(Exception e){
                log.error("prev error",e);
                return null;
            }
        }
    	else
    		return null;    	
    }
    
    public PowerQualityMonitor getPowerQuality(){
    	
        try{
            if(pq != null){         
                return nuri_pq.getData();
            }else{
                return null;
            }
        }catch(Exception e){
            log.error("power quality parse error",e);
        }
        return null;
    }
    
    public TOU_BLOCK[] getCurrBilling(){
    	
    	try{
    	if(cb != null){
    		TOU_BLOCK[] tou_block = nuri_cb.getTOU_BLOCK();
    		if(tou_block!=null)
    			tou_block[0].setResetTime(getMeterTime());
    		return tou_block;
    	}
    	else
    		return null;    	
    	}catch(Exception e){
    		return null;
    	}
    }
    
    public Instrument[] getInstrument(){

        Instrument[] insts = new Instrument[1];
        try {

            if(is != null)
            {
              //  log.debug(nuri_is);
                insts[0] = new Instrument();
                insts[0].setVOL_A(nuri_is.getVOLTAGE_INSTANCE_A());
                insts[0].setVOL_B(nuri_is.getVOLTAGE_INSTANCE_B());
                insts[0].setVOL_C(nuri_is.getVOLTAGE_INSTANCE_C());
                
                insts[0].setVOL_ANGLE_A(nuri_is.getVOLTAGE_ANGLE_INSTANCE_A());
                insts[0].setVOL_ANGLE_B(nuri_is.getVOLTAGE_ANGLE_INSTANCE_B());
                insts[0].setVOL_ANGLE_C(nuri_is.getVOLTAGE_ANGLE_INSTANCE_C());
                
                insts[0].setCURR_A(nuri_is.getCURRENT_INSTANCE_A());
                insts[0].setCURR_B(nuri_is.getCURRENT_INSTANCE_B());
                insts[0].setCURR_C(nuri_is.getCURRENT_INSTANCE_C());
                
                insts[0].setCURR_ANGLE_A(nuri_is.getCURRENT_ANGLE_INSTANCE_A());
                insts[0].setCURR_ANGLE_B(nuri_is.getCURRENT_ANGLE_INSTANCE_B());
                insts[0].setCURR_ANGLE_C(nuri_is.getCURRENT_ANGLE_INSTANCE_C());
                
                insts[0].setPF_A(nuri_is.getPF_A());
                insts[0].setPF_B(nuri_is.getPF_B());
                insts[0].setPF_C(nuri_is.getPF_C());
                
                return insts;
            }
            else
            {
                return null;
            }
        } catch(Exception e){
            log.warn("transform instrument error: "+e.getMessage());
        }
        return null;
    }
        
    public EventLogData[] getEventLog(){
    	EventLogData[] eventlog = null;
	    
    	try{
	    	if(ev != null){	    		
	    		eventlog = nuri_ev.getEvent();
	    	}else{
	    		log.debug("ev is null");
	    	}
	    }catch(Exception e){
	        log.error("event log parse error",e);
	    }
	    
	    return eventlog;
    }
    
    public HashMap<String, String> getMdmData(){
        
        HashMap<String, String> map = null;
        
        try{
            if(mdm != null){
                map = new HashMap<String, String>();
                map.put("mcuType","5");
                
                if(this.nuri_mdm.getFW_VER().startsWith("NG")){
                    map.put("protocolType","2");
                }else{
                    map.put("protocolType","1");
                }

                map.put("sysPhoneNumber", this.nuri_mdm.getPHONE_NUM());
                map.put("id", this.nuri_mdm.getPHONE_NUM());
                map.put("swVersion", this.nuri_mdm.getFW_VER());
                map.put("networkStatus", "1");
                map.put("csq", this.nuri_mdm.getCSQ_LEVEL()+"");
            }
        }catch(Exception e){
            
        }
        return map;
    }
    

    /**
     * get Data
     */
    @SuppressWarnings("unchecked")
	@Override
	public LinkedHashMap getData() {
    {
        LinkedHashMap<String, Serializable> res = new LinkedHashMap(16,0.75f,false);
        TOU_BLOCK[] tou_block = null;
        TOU_BLOCK[] prev_block = null;
        LPData[] lplist = null;
        EventLogData[] evlog = null;
        Instrument[] inst = null;      
        PowerQualityMonitor pq = null;
        
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());

        try
        {
            log.debug("==================A2RL getData start()====================");
            tou_block = getCurrBilling();
            prev_block = getPrevBilling();
            lplist = getLPData();
            evlog = getEventLog();
            inst = getInstrument();
            pq = getPowerQuality();
            
			res.put("<b>[Meter Configuration Data]</b>", "");
            if(nuri_mtr != null){          
                res.put("CT",nuri_mtr.getCT_RATIO()+"");
                res.put("VT",nuri_mtr.getVT_RATIO()+"");
                res.put("Meter Constant",nuri_mtr.getMETER_CONSTANT()+"");
                this.ke = new Double(nuri_mtr.getMETER_CONSTANT());
                //res.put("Billing Day",nuri_mtr.getMETERING_DATE());
                res.put("Meter ID",nuri_mtr.getMeterSerial());
                res.put("Current Meter Time",nuri_mtr.getDateTime());
                res.put("MeterWarningFlag",nuri_mtr.getMeterWarningFlag().getLog());
                res.put("MeterErrorFlag",nuri_mtr.getMeterErrorFlag().getLog());
                res.put("MeterStatusFlag",nuri_mtr.getMeterStatusFlag().getLog());
            }
            
            if(prev_block != null){
                res.put("<b>[Previous Billing Data]</b>", "");
                res.put("Total Active Energy(kWh)"              ,df3.format(prev_block[0].getSummation(0))+"");
                res.put("Total Reactive Energy(kVarh)"            ,df3.format(prev_block[0].getSummation(1))+"");
                res.put("Total Active Power Max.Demand(kW)"     ,df3.format(prev_block[0].getCurrDemand(0))+"");
                res.put("Total Active Power Max.Demand Time"    ,(String)prev_block[0].getEventTime(0));
                res.put("Total Reactive Power Max.Demand(kVar)"   ,df3.format(prev_block[0].getCurrDemand(1))+"");
                res.put("Total Reactive Power Max.Demand Time"  ,(String)prev_block[0].getEventTime(1));
                res.put("Total Active Power Cum.Demand(kW)"     ,df3.format(prev_block[0].getCumDemand(0))+"");
                res.put("Total Reactive Power Cum.Demand(kVar)"   ,df3.format(prev_block[0].getCumDemand(1))+"");
                    
                res.put("Rate A Active Energy(kWh)"             ,df3.format(prev_block[1].getSummation(0))+"");                
                res.put("Rate A Reactive Energy(kVarh)"           ,df3.format(prev_block[1].getSummation(1))+"");
                res.put("Rate A Active Power Max.Demand(kW)"    ,df3.format(prev_block[1].getCurrDemand(0))+"");
                res.put("Rate A Active Power Max.Demand Time"   ,(String)prev_block[1].getEventTime(0));
                res.put("Rate A Reactive Power Max.Demand(kVar)"  ,df3.format(prev_block[1].getCurrDemand(1))+"");
                res.put("Rate A Reactive Power Max.Demand Time" ,(String)prev_block[1].getEventTime(1));
                res.put("Rate A Active Power Cum.Demand(kW)"    ,df3.format(prev_block[1].getCumDemand(0))+"");
                res.put("Rate A Reactive Power Cum.Demand(kVar)"  ,df3.format(prev_block[1].getCumDemand(1))+"");
                    
                res.put("Rate B Active Energy(kWh)"             ,df3.format(prev_block[2].getSummation(0))+"");
                res.put("Rate B Reactive Energy(kVarh)"           ,df3.format(prev_block[2].getSummation(1))+"");
                res.put("Rate B Active Power Max.Demand(kW)"    ,df3.format(prev_block[2].getCurrDemand(0))+"");
                res.put("Rate B Active Power Max.Demand Time"   ,(String)prev_block[2].getEventTime(0));
                res.put("Rate B Reactive Power Max.Demand(kVar)"  ,df3.format(prev_block[2].getCurrDemand(1))+"");
                res.put("Rate B Reactive Power Max.Demand Time" ,(String)prev_block[2].getEventTime(1));
                res.put("Rate B Active Power Cum.Demand(kW)"    ,df3.format(prev_block[2].getCumDemand(0))+"");
                res.put("Rate B Reactive Power Cum.Demand(kVar)"  ,df3.format(prev_block[2].getCumDemand(1))+"");
                res.put("Rate B Active Power Cont.Demand(kW)"   ,df3.format(prev_block[2].getCoincident(0))+"");
                res.put("Rate B Reactive Power Cont.Demand(kVar)" ,df3.format(prev_block[2].getCoincident(1))+"");
                    
                res.put("Rate C Active Energy(kWh)"             ,df3.format(prev_block[3].getSummation(0))+"");
                res.put("Rate C Reactive Energy(kVarh)"           ,df3.format(prev_block[3].getSummation(1))+"");
                res.put("Rate C Active Power Max.Demand(kW)"    ,df3.format(prev_block[3].getCurrDemand(0))+"");
                res.put("Rate C Active Power Max.Demand Time"   ,(String)prev_block[3].getEventTime(0));
                res.put("Rate C Reactive Power Max.Demand(kVar)"  ,df3.format(prev_block[3].getCurrDemand(1))+"");
                res.put("Rate C Reactive Power Max.Demand Time" ,(String)prev_block[3].getEventTime(1));
                res.put("Rate C Active Power Cum.Demand(kW)"    ,df3.format(prev_block[3].getCumDemand(0))+"");
                res.put("Rate C Reactive Power Cum.Demand(kVar)"  ,df3.format(prev_block[3].getCumDemand(1))+"");
            }

            if(tou_block != null){
                res.put("<b>[Current Billing Data]</b>", "Current Billing Data");
                res.put("Total Active Energy(kWh)"              ,df3.format(tou_block[0].getSummation(0))+"");
                res.put("Total Reactive Energy(kVarh)"            ,df3.format(tou_block[0].getSummation(1))+"");
                res.put("Total Active Power Max.Demand(kW)"     ,df3.format(tou_block[0].getCurrDemand(0))+"");
                res.put("Total Active Power Max.Demand Time"    ,(String)tou_block[0].getEventTime(0));
                res.put("Total Reactive Power Max.Demand(kVar)"   ,df3.format(tou_block[0].getCurrDemand(1))+"");
                res.put("Total Reactive Power Max.Demand Time"  ,(String)tou_block[0].getEventTime(1));
                res.put("Total Active Power Cum.Demand(kW)"     ,df3.format(tou_block[0].getCumDemand(0))+"");
                res.put("Total Reactive Power Cum.Demand(kVar)"   ,df3.format(tou_block[0].getCumDemand(1))+"");
                    
                lpValue = new Double(df3.format(tou_block[1].getSummation(0)));
                res.put("Rate A Active Energy(kWh)"             ,df3.format(tou_block[1].getSummation(0))+"");                
                res.put("Rate A Reactive Energy(kVarh)"           ,df3.format(tou_block[1].getSummation(1))+"");
                res.put("Rate A Active Power Max.Demand(kW)"    ,df3.format(tou_block[1].getCurrDemand(0))+"");
                res.put("Rate A Active Power Max.Demand Time"   ,(String)tou_block[1].getEventTime(0));
                res.put("Rate A Reactive Power Max.Demand(kVar)"  ,df3.format(tou_block[1].getCurrDemand(1))+"");
                res.put("Rate A Reactive Power Max.Demand Time" ,(String)tou_block[1].getEventTime(1));
                res.put("Rate A Active Power Cum.Demand(kW)"    ,df3.format(tou_block[1].getCumDemand(0))+"");
                res.put("Rate A Reactive Power Cum.Demand(kVar)"  ,df3.format(tou_block[1].getCumDemand(1))+"");
                    
                res.put("Rate B Active Energy(kWh)"             ,df3.format(tou_block[2].getSummation(0))+"");
                res.put("Rate B Reactive Energy(kVarh)"           ,df3.format(tou_block[2].getSummation(1))+"");
                res.put("Rate B Active Power Max.Demand(kW)"    ,df3.format(tou_block[2].getCurrDemand(0))+"");
                res.put("Rate B Active Power Max.Demand Time"   ,(String)tou_block[2].getEventTime(0));
                res.put("Rate B Reactive Power Max.Demand(kVar)"  ,df3.format(tou_block[2].getCurrDemand(1))+"");
                res.put("Rate B Reactive Power Max.Demand Time" ,(String)tou_block[2].getEventTime(1));
                res.put("Rate B Active Power Cum.Demand(kW)"    ,df3.format(tou_block[2].getCumDemand(0))+"");
                res.put("Rate B Reactive Power Cum.Demand(kVar)"  ,df3.format(tou_block[2].getCumDemand(1))+"");
                res.put("Rate B Active Power Cont.Demand(kW)"   ,df3.format(tou_block[2].getCoincident(0))+"");
                res.put("Rate B Reactive Power Cont.Demand(kVar)" ,df3.format(tou_block[2].getCoincident(1))+"");
                    
                res.put("Rate C Active Energy(kWh)"             ,df3.format(tou_block[3].getSummation(0))+"");
                res.put("Rate C Reactive Energy(kVarh)"           ,df3.format(tou_block[3].getSummation(1))+"");
                res.put("Rate C Active Power Max.Demand(kW)"    ,df3.format(tou_block[3].getCurrDemand(0))+"");
                res.put("Rate C Active Power Max.Demand Time"   ,(String)tou_block[3].getEventTime(0));
                res.put("Rate C Reactive Power Max.Demand(kVar)"  ,df3.format(tou_block[3].getCurrDemand(1))+"");
                res.put("Rate C Reactive Power Max.Demand Time" ,(String)tou_block[3].getEventTime(1));
                res.put("Rate C Active Power Cum.Demand(kW)"    ,df3.format(tou_block[3].getCumDemand(0))+"");
                res.put("Rate C Reactive Power Cum.Demand(kVar)"  ,df3.format(tou_block[3].getCumDemand(1))+"");
                
            }
            
            if(evlog != null && evlog.length > 0){
                res.put("<b>[Event Log]</b>", "");
                int idx = 0;
                for(int i = 0; i < evlog.length; i++){
                    String datetime = evlog[i].getDate() + evlog[i].getTime();
                    if(!datetime.startsWith("0000") && !datetime.equals("")){
                        res.put("("+i+")"+datetime, evlog[i].getMsg());
                    }
                }
            }
            
            if(inst != null && inst.length > 0){
                res.put("<b>[Instrument Data]<b>", "");
                for(int i = 0; i < inst.length; i++){      
                	
                	inst[0].setVOL_A(nuri_is.getVOLTAGE_INSTANCE_A());
                    inst[0].setVOL_B(nuri_is.getVOLTAGE_INSTANCE_B());
                    inst[0].setVOL_C(nuri_is.getVOLTAGE_INSTANCE_C());
                    
                    inst[0].setVOL_ANGLE_A(nuri_is.getVOLTAGE_ANGLE_INSTANCE_A());
                    inst[0].setVOL_ANGLE_B(nuri_is.getVOLTAGE_ANGLE_INSTANCE_B());
                    inst[0].setVOL_ANGLE_C(nuri_is.getVOLTAGE_ANGLE_INSTANCE_C());
                    
                    inst[0].setCURR_A(nuri_is.getCURRENT_INSTANCE_A());
                    inst[0].setCURR_B(nuri_is.getCURRENT_INSTANCE_B());
                    inst[0].setCURR_C(nuri_is.getCURRENT_INSTANCE_C());
                    
                    inst[0].setCURR_ANGLE_A(nuri_is.getCURRENT_ANGLE_INSTANCE_A());
                    inst[0].setCURR_ANGLE_B(nuri_is.getCURRENT_ANGLE_INSTANCE_B());
                    inst[0].setCURR_ANGLE_C(nuri_is.getCURRENT_ANGLE_INSTANCE_C());
                    
                    inst[0].setPF_A(nuri_is.getPF_A());
                    inst[0].setPF_B(nuri_is.getPF_B());
                    inst[0].setPF_C(nuri_is.getPF_C());
                    
                    res.put("Voltage(A)",df3.format(inst[i].getVOL_A())+"");
                    res.put("Voltage(B)",df3.format(inst[i].getVOL_B())+"");
                    res.put("Voltage(C)",df3.format(inst[i].getVOL_C())+"");
                    res.put("Current(A)",df3.format(inst[i].getCURR_A())+"");
                    res.put("Current(B)",df3.format(inst[i].getCURR_B())+"");
                    res.put("Current(C)",df3.format(inst[i].getCURR_C())+"");
                    res.put("Voltage_Angle(A)",df3.format(inst[i].getVOL_ANGLE_A())+"");
                    res.put("Voltage_Angle(B)",df3.format(inst[i].getVOL_ANGLE_B())+"");
                    res.put("Voltage_Angle(C)",df3.format(inst[i].getVOL_ANGLE_C())+"");
                    res.put("Current_Angle(A)",df3.format(inst[i].getCURR_ANGLE_A())+"");
                    res.put("Current_Angle(B)",df3.format(inst[i].getCURR_ANGLE_B())+"");
                    res.put("Current_Angle(C)",df3.format(inst[i].getCURR_ANGLE_C())+"");
                    res.put("PF(A)",df3.format(inst[i].getPF_A())+"");
                    res.put("PF(B)",df3.format(inst[i].getPF_B())+"");
                    res.put("PF(C)",df3.format(inst[i].getPF_C())+"");
                }
            }
            if(pq != null){
                res.put("<b>[PQ Data]</b>", "");    
            
                res.put("SERVICE_VOLTAGE_CNT",pq.getSERVICE_VOL_CNT());
                res.put("SERVICE_VOLTAGE_DUR",pq.getSERVICE_VOL_DUR());
                res.put("SERVICE_VOLTAGE_STAT",pq.getSERVICE_VOL_STAT());
                
                res.put("LOW_VOLTAGE_CNT",pq.getLOW_VOL_CNT());
                res.put("LOW_VOLTAGE_DUR",pq.getLOW_VOL_DUR());
                res.put("LOW_VOLTAGE_STAT",pq.getLOW_VOL_STAT());
                
                res.put("HIGH_VOLTAGE_CNT",pq.getHIGH_VOL_CNT());
                res.put("HIGH_VOLTAGE_DUR",pq.getHIGH_VOL_DUR());  
                res.put("HIGH_VOLTAGE_STAT",pq.getHIGH_VOL_STAT());

                res.put("REVERSE_POWER_CNT",pq.getREVERSE_PWR_CNT());
                res.put("REVERSE_POWER_DUR",pq.getREVERSE_PWR_DUR());
                res.put("REVERSE_POWER_STAT",pq.getREVERSE_PWR_STAT());
                
                res.put("LOW_CURRENT_CNT",pq.getLOW_CURR_CNT());
                res.put("LOW_CURRENT_DUR",pq.getLOW_CURR_DUR());
                res.put("LOW_CURRENT_STAT",pq.getLOW_CURR_STAT());
                                
                res.put("POWER_FACTOR_CNT",pq.getPFACTOR_CNT());
                res.put("POWER_FACTOR_DUR",pq.getPFACTOR_DUR());
                res.put("POWER_FACTOR_STAT",pq.getPFACTOR_STAT());
                
                res.put("THD_CURRENT_CNT",pq.getTHD_CURR_CNT());
                res.put("THD_CURRENT_DUR",pq.getTHD_CURR_DUR());
                res.put("THD_CURRENT_STAT",pq.getTHD_CURR_STAT());
                
                res.put("THD_VOLTAGE_CNT",pq.getTHD_VOL_CNT());
                res.put("THD_VOLTAGE_DUR",pq.getTHD_VOL_DUR());
                res.put("THD_VOLTAGE_STAT",pq.getTHD_VOL_STAT());
                
                res.put("SAG_COUNT_=",pq.getVOL_A_SAG_CNT());
                res.put("SAG_DUR_=",pq.getVOL_A_SAG_DUR());
                res.put("SAG_COUNT_=",pq.getVOL_B_SAG_CNT());
                res.put("SAG_DUR_=",pq.getVOL_B_SAG_DUR());
                res.put("SAG_COUNT_=",pq.getVOL_C_SAG_CNT());
                res.put("SAG_DUR_=",pq.getVOL_C_SAG_DUR());
                
            }
            if(lplist != null && lplist.length > 0){
                res.put("<b>[Load Profile Data(kWh)]</b>", "");
                int nbr_chn = 2;//ch1,ch2
                if(nuri_lp!= null){
                    nbr_chn = 2;//TODO FIX HARD CODE
                }
                lpValue = getCurrentActive();
                ArrayList chartData0 = new ArrayList();//time chart
                ArrayList[] chartDatas = new ArrayList[nbr_chn]; //channel chart(ch1,ch2,...)
                for(int k = 0; k < nbr_chn ; k++){
                    chartDatas[k] = new ArrayList();                    
                }
                
                DecimalFormat decimalf=null;
                SimpleDateFormat datef14=null;
                ArrayList lpDataTime = new ArrayList();
                
                for(int i = 0; i < lplist.length; i++){
                    String datetime = lplist[i].getDatetime();
                    
                    if(meter!=null && meter.getSupplier()!=null){
                    Supplier supplier = meter.getSupplier();
                    if(supplier !=null){
                        String lang = supplier.getLang().getCode_2letter();
                        String country = supplier.getCountry().getCode_2letter();
                        
                        decimalf = TimeLocaleUtil.getDecimalFormat(supplier);
                        datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
                    }
                	}else{
                    //locail 정보가 없을때는 기본 포멧을 사용한다.
                    decimalf = new DecimalFormat();
                    datef14 = new SimpleDateFormat();
                }
                    String date;
                	date = datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(datetime+"00"));

                	String tempDateTime = lplist[i].getDatetime();
                    String val = "";
                    Double[] ch = lplist[i].getCh();
                    
                    for(int k = 0; k < ch.length ; k++){
                        val += "<span style='margin-right: 40px;'>ch"+(k+1)+"="+df3.format(ch[k])+"</span>";
                    }
                    
                    res.put("LP"+" "+ date, val);

                    chartData0.add(tempDateTime.substring(6,8)
                                  +tempDateTime.substring(8,10)
                                  +tempDateTime.substring(10,12));
                    for(int k = 0; k < ch.length ; k++){                        
                        chartDatas[k].add(ch[k].doubleValue());
                    }
                    lpDataTime.add((String)lplist[i].getDatetime());
                }
                
                //res.put("chartData0", chartData0);
                //for(int k = 0; k < chartDatas.length ; k++){
                //    res.put("chartData"+(k+1), chartDatas[k]);
                //}
                //res.put("lpDataTime", lpDataTime);
                //res.put("chartDatas", chartDatas);
                res.put("[ChannelCount]", nbr_chn+"");
            }
            
            res.put("LP Channel Information", getLPChannelMap());
            log.debug("==================A2RL getData End()====================");
        }
        catch (Exception e)
        {
            log.warn("Get Data Error=>",e);
        }

        return res;
    }
}

	@Override
	public Double getMeteringValue() {
		return null;
	}

}