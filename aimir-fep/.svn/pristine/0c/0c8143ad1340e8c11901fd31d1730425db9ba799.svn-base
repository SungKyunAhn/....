package com.aimir.fep.meter.parser;

import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_CB;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_EV;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_IS;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_LP;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_MDM;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_MTR;
import com.aimir.fep.meter.parser.a2rlTable.A2RL_PB;
import com.aimir.fep.meter.parser.a2rlTable.MeterErrorFlag;
import com.aimir.fep.meter.parser.a2rlTable.MeterStatusFlag;
import com.aimir.fep.meter.parser.a2rlTable.MeterWarningFlag;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

public class A1RLQ extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = -2361927097420001289L;
	private static Log log = LogFactory.getLog(A1RLQ.class);
    private Double lpValue = null;
    private String meterId = null;
    private Double ke =1.0;
    private Double ct = 1.0;
    private Double vt = 1.0;
    
    private byte[] mdm = null;
    private byte[] mtr = null;
    private byte[] cb = null;
    private byte[] pb = null;
    private byte[] lpd = null;
    private byte[] ev = null;
    private byte[] is = null;
    //private byte[] pq = null;
    
    private A2RL_MDM nuri_mdm = null;
    private A2RL_MTR nuri_mtr = null;
    private A2RL_CB nuri_cb = null;
    private A2RL_PB nuri_pb = null;
    private A2RL_LP nuri_lp = null;
    private A2RL_EV nuri_ev = null;
    private A2RL_IS nuri_is = null;
    //private A2RL_PQ nuri_pq = null;
    
	@Override
	public void parse(byte[] data) throws Exception {

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
                lpd = b;
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
            //else if(tbName.equals("PQM"))
            //{
            //    pq = b;
            //    log.debug("[A2RL_PQ] len=["+len+"] data=>"+Util.getHexString(b));
            //}
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
            this.meterTime = nuri_mtr.getDateTime(); 
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

        if(lpd != null){
            nuri_lp = new A2RL_LP(lpd, ke.doubleValue(), ct.doubleValue(), vt.doubleValue());
        }
      
        if(is != null){
            nuri_is = new A2RL_IS(is);
        }
        
        //if(pq != null){
        //    nuri_pq = new A2RL_PQ(pq);
        //}
        log.debug("NURI_A2RL Data Parse Finished :: DATA["+toString()+"]");
		
	}
	
    public Double getLpValue()
    {
    	return this.lpValue;
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
	
    public LPData[] getLPData(){
        
        try{
            if(lpd != null)
                return nuri_lp.parse();
            else
                return null;
        }catch(Exception e){
            log.error("lp parse error",e);
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
	
	@Override
	public LinkedHashMap<?, ?> getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFlag() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Double getMeteringValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getRawData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFlag(int flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
