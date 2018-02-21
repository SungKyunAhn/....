package com.aimir.fep.meter.parser;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.kV2cTable.NURI_MDM;
import com.aimir.fep.meter.parser.lk3410cpTable.LK3410CP_CB;
import com.aimir.fep.meter.parser.lk3410cpTable.LK3410CP_EV;
import com.aimir.fep.meter.parser.lk3410cpTable.LK3410CP_IS;
import com.aimir.fep.meter.parser.lk3410cpTable.LK3410CP_LP;
import com.aimir.fep.meter.parser.lk3410cpTable.LK3410CP_MTR;
import com.aimir.fep.meter.parser.lk3410cpTable.LK3410CP_PB;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * parsing LK3410CP_005 Meter Data
 *
 * @author Yeon Kyoung Park (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2007-05-06 12:00:15 +0900 $,
 */
public class LK3410CP_005 extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 4148592757477875913L;

	private static Log log = LogFactory.getLog(LK3410CP_005.class);
    
    private byte[] rawData = null;
    private int lpcount;
    private Double lp = null;
    private Double lpValue = null;
    private String meterId = null;
    private int regK =1;
    private int flag = 0;
    
    private byte[] mdm = null;
    private byte[] mtr = null;
    private byte[] cb = null;
    private byte[] pb = null;
    private byte[] LPD = null;
    private byte[] ev = null;
    private byte[] is = null;
    
    private NURI_MDM nuri_mdm = null;
    private LK3410CP_MTR nuri_mtr = null;
    private LK3410CP_CB nuri_cb = null;
    private LK3410CP_PB nuri_pb = null;
    private LK3410CP_LP nuri_lp = null;
    private LK3410CP_EV nuri_ev = null;
    private LK3410CP_IS nuri_is = null;
    
    /**
     * constructor
     */
    public LK3410CP_005()
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
     * parseing Energy Meter Data of LK3410CP_005 Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
        int offset = 0;
        int LEN_MDM_INFO = 30;
        log.debug("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));
        if (data.length < 83) {

            if(data.length == 30)
            {
                byte[] b = new byte[LEN_MDM_INFO];
                System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
                mdm = b;
                log.debug("[NURI_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
                nuri_mdm = new NURI_MDM(mdm);
                log.debug(nuri_mdm);
            }
            else
            {
                log.error("[NURI_LK3410CP_005] Data total length[" + data.length + "] is invalid");
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
        offset += 2;
        offset += 1;
        int len = 47;//msb ->lsb
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
            else 
            {
                log.debug("unknown table=>"+tbName);
            }
        }
        
        nuri_mdm = new NURI_MDM(mdm);
        log.debug(nuri_mdm);
        nuri_mtr = new LK3410CP_MTR(mtr);
        log.debug(nuri_mtr);
        
        
        if(mtr != null){
            this.meterId = nuri_mtr.getMETER_ID();
            this.meterTime =  nuri_mtr.getDateTime();
            this.regK = nuri_mtr.getREG_K();
        }
        
        if(cb!= null){
            nuri_cb = new LK3410CP_CB(cb); 
        }

        if(ev != null){
            nuri_ev = new LK3410CP_EV(ev); 
        }
        
        if(pb != null){
            nuri_pb = new LK3410CP_PB(pb);
        }

        if(LPD != null){
            nuri_lp = new LK3410CP_LP(LPD, regK);
        }
      
        if(is != null){
            nuri_is = new LK3410CP_IS(is);
        }

        log.debug("NURI_LK3410CP_005 Data Parse Finished :: DATA["+toString()+"]");
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
       
        sb.append("NURI_LK3410CP_005 Meter DATA[");
        sb.append("(meterId=").append(meterId).append("),");
        sb.append("]\n");

        return sb.toString();
    }
    
    public String getLPChannelMap(){
        String res ="";
        /*
        try{
            if(LPD != null){
                res = nuri_lp.getChannelMap();     
            }
        }catch(Exception e){
            log.warn(e);
        }
        log.debug("channel map : "+ res);
        */
        
            res+="ch1=Active Energy[kWh],v1=Active Power[kW],";

            res+="ch2=Lag Reactive Energy[kVarh],v2=Lag Reactive Power[kVar],";

            res+="ch3=Lead Reactive Energy[kVarh],v3=Lead Reactive Power[kVar],";

            res+="ch4=Phase Energy[kVah],v4=Phase Power[kVA],";
            res+="pf=PF";
        return res;
        
    }
    
    public int getLPChannelCount(){
        try{
            if(LPD != null){
                return 4*2+1;
            }
            else{
                return 9;//
            }
        } catch(Exception e){
        }
        return 9;//
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
    
    public String getSwVersion(){
    	
    	 String version = "";
    	 if(mtr != null){
	    	 try
	         {
	    		 version=  nuri_mtr.getProgramNameVer().getProgramVersion();
	         }catch(Exception e){log.warn("get meter sw version ",e);}
    	 }
         log.debug("getSwVersion() :"+version);
         return version;
    }
    
    public String getSwName(){
    	
    	String swname ="";
    	if(mtr != null){
	    	 try
	         {
	    		 swname =  nuri_mtr.getProgramNameVer().getProgramName();
	         }catch(Exception e){log.warn("get meter sw name ",e);}
    	}
         log.debug("getSwName() :"+swname);
         return swname;
    }

    public String getMeterLog(){
        
        StringBuffer sb = new StringBuffer();
        if(mtr != null){
            try
            {
                sb.append(nuri_mtr.getMeterCautionFlag().getLog());
                sb.append(nuri_mtr.getMeterErrorFlag().getLog());
                sb.append(nuri_mtr.getMeterEventFlag().getLog());
            }
            catch (Exception e){log.warn("get meter log error",e);}
        }
        log.debug("getMeterLog ["+sb.toString()+"]");
        return sb.toString();
    }
    
    public String getCurrentProgramDate() throws Exception {
  	  String meterTime = "";
  	 if(mtr != null){
           try
           {
          	 meterTime =  nuri_mtr.getCURRENT_PROGRAM_DATE();
           }catch (Exception e){log.warn("get getCURRENT_PROGRAM_DATE error",e);}
  	 }
  	 log.debug("getCurrentProgramDate() :"+meterTime);
       return meterTime;
    }
        
    public int getMeterStatusCode() {
        return this.nuri_mdm.getERROR_STATUS();
    }
    
    public String getBillingDay() throws Exception {
    	 String billday = "";
    	 if(mtr != null){
             try
             {
            	 billday =  ""+nuri_mtr.getMETERING_DATE();
             } catch (Exception e){log.warn("get BillingDay error",e);}
    	 }
    	 log.debug("getMETERING_DATE() :"+billday);
         return billday;
    }
        
    @SuppressWarnings("unchecked")
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
    
    public TOU_BLOCK[] getPrevBilling(){
    	
    	if(pb != null){
            try{
                TOU_BLOCK[] blk = nuri_pb.getTOU_BLOCK();
                blk[0].setResetTime(nuri_ev.getDemandTime());
                blk[0].setResetCount(nuri_ev.getTotalDemandCnt());
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
    	
    	if(cb != null)
    		return nuri_cb.getTOU_BLOCK();
    	else
    		return null;    	
    }
    
    public Instrument[] getInstrument(){

        Instrument[] insts = new Instrument[1];
        try {

            if(is != null)
            {
                log.debug(nuri_is);
                insts[0] = new Instrument();
                insts[0].setVOL_A(nuri_is.getVOLTAGE_PHA());
                insts[0].setVOL_B(nuri_is.getVOLTAGE_PHB());
                insts[0].setVOL_C(nuri_is.getVOLTAGE_PHC());
                insts[0].setCURR_A(nuri_is.getCURRENT_PHA());
                insts[0].setCURR_B(nuri_is.getCURRENT_PHB());
                insts[0].setCURR_C(nuri_is.getCURRENT_PHC());
                insts[0].setVOL_THD_A(nuri_is.getVOLTAGE_THD_PHA());
                insts[0].setVOL_THD_B(nuri_is.getVOLTAGE_THD_PHB());
                insts[0].setVOL_THD_C(nuri_is.getVOLTAGE_THD_PHC());
                insts[0].setCURR_THD_A(nuri_is.getCURRENT_THD_PHA());
                insts[0].setCURR_THD_B(nuri_is.getCURRENT_THD_PHB());
                insts[0].setCURR_THD_C(nuri_is.getCURRENT_THD_PHC());
                insts[0].setPF_A(nuri_is.getPF_PHA());
                insts[0].setPF_B(nuri_is.getPF_PHB());
                insts[0].setPF_C(nuri_is.getPF_PHC());
                insts[0].setKW_A(nuri_is.getPOSITIVE_W_PHA());
                insts[0].setKW_B(nuri_is.getPOSITIVE_W_PHB());
                insts[0].setKW_C(nuri_is.getPOSITIVE_W_PHC());
                insts[0].setKVAR_A(nuri_is.getLAG_VAR_PHA());
                insts[0].setKVAR_B(nuri_is.getLAG_VAR_PHB());
                insts[0].setKVAR_C(nuri_is.getLAG_VAR_PHC());
                insts[0].setKVA_A(nuri_is.getVA_PHA());
                insts[0].setKVA_B(nuri_is.getVA_PHB());
                insts[0].setKVA_C(nuri_is.getVA_PHC());
                insts[0].setLINE_FREQUENCY(nuri_is.getFREQUENCY());
                
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
    	if(ev != null){
    		return nuri_ev.getEvent();
    	}else{
    		return null;
    	}
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
    public LinkedHashMap getData()
    {
        LinkedHashMap<String, Serializable> res = new LinkedHashMap(16,0.75f,false);
        TOU_BLOCK[] tou_block = null;
        LPData[] lplist = null;
        EventLogData[] evlog = null;
        Instrument[] inst = null;
             
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
        
        try
        {
            log.debug("==================LP3410CP_005 getData start()====================");
            tou_block = getCurrBilling();
            lplist = getLPData();
            evlog = getEventLog();
            inst = getInstrument();
            
			res.put("<b>[Meter Configuration Data]</b>", "");
            if(nuri_mtr != null){          
                res.put("ProgramNameVer",nuri_mtr.getProgramNameVer().toString());
                res.put("CT_PT",nuri_mtr.getCT_PT()+"");
                res.put("REG_K",nuri_mtr.getREG_K()+"");
                res.put("SCALE_FACTOR",nuri_mtr.getSCALE_FACTOR()+"");
                res.put("PULSE_INITIATOR",nuri_mtr.getPULSE_INITIATOR()+"");
                //res.put("DataFormatForMetering",nuri_mtr.getDataFormatForMetering().toString());
                res.put("Billing Day",nuri_mtr.getMETERING_DATE());
                //res.put("MeterManufacture",nuri_mtr.getMeterManufacture().toString());
                res.put("Meter ID",nuri_mtr.getMeterManufacture().getMeterId());
                res.put("Current Meter Time",nuri_mtr.getCURRENT_METER_DATETIME());
                res.put("Current Program Time",nuri_mtr.getCURRENT_PROGRAM_DATE());
                res.put("MeterErrorFlag",nuri_mtr.getMeterErrorFlag().toString());
                res.put("MeterEventFlag",nuri_mtr.getMeterEventFlag().toString());
                res.put("MeterCautionFlag",nuri_mtr.getMeterCautionFlag().toString());
            }

            if(tou_block != null){
                res.put("[Current Billing Data]", "");
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
                
                res.put("Rate D Active Energy(kWh)"             ,df3.format(tou_block[4].getSummation(0))+"");
                res.put("Rate D Reactive Energy(kVarh)"           ,df3.format(tou_block[4].getSummation(1))+"");
                res.put("Rate D Active Power Max.Demand(kW)"    ,df3.format(tou_block[4].getCurrDemand(0))+"");
                res.put("Rate D Active Power Max.Demand Time"   ,(String)tou_block[4].getEventTime(0));
                res.put("Rate D Reactive Power Max.Demand(kVar)"  ,df3.format(tou_block[4].getCurrDemand(1))+"");
                res.put("Rate D Reactive Power Max.Demand Time" ,(String)tou_block[4].getEventTime(1));
                res.put("Rate D Active Power Cum.Demand(kW)"    ,df3.format(tou_block[4].getCumDemand(0))+"");
                res.put("Rate D Reactive Power Cum.Demand(kVar)"  ,df3.format(tou_block[4].getCumDemand(1))+"");

            }
            
            if(lplist != null && lplist.length > 0){
                res.put("[Load Profile Data(kWh)]", "");
                int nbr_chn = 4;//ch1,ch2
                if(nuri_lp!= null){
                    nbr_chn = 4;//TODO FIX HARD CODE
                }
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
                    res.put("LP"+" "+date, val);

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
            
            if(evlog != null && evlog.length > 0){
                res.put("[Event Log]", "");
                int idx = 0;
                for(int i = 0; i < evlog.length; i++){
                    String datetime = evlog[i].getDate() + evlog[i].getTime();
                    if(!datetime.startsWith("0000") && !datetime.equals("")){
                        res.put("("+i+")"+datetime+"00", evlog[i].getMsg());
                    }
                }
            }
            
            if(inst != null && inst.length > 0){
                res.put("[Instrument Data]", "");
                for(int i = 0; i < inst.length; i++){                  
                    res.put("Voltage(A)",df3.format(inst[i].getVOL_A())+"");
                    res.put("Voltage(B)",df3.format(inst[i].getVOL_B())+"");
                    res.put("Voltage(C)",df3.format(inst[i].getVOL_C())+"");
                    res.put("Current(A)",df3.format(inst[i].getCURR_A())+"");
                    res.put("Current(B)",df3.format(inst[i].getCURR_B())+"");
                    res.put("Current(C)",df3.format(inst[i].getCURR_C())+"");
                    res.put("Voltage THD(A)",df3.format(inst[i].getVOL_THD_A())+"");
                    res.put("Voltage THD(B)",df3.format(inst[i].getVOL_THD_B())+"");
                    res.put("Voltage THD(C)",df3.format(inst[i].getVOL_THD_C())+"");
                    res.put("Current THD(A)",df3.format(inst[i].getCURR_THD_A())+"");
                    res.put("Current THD(B)",df3.format(inst[i].getCURR_THD_B())+"");
                    res.put("Current THD(C)",df3.format(inst[i].getCURR_THD_C())+"");
                    res.put("PF(A)",df3.format(inst[i].getPF_A())+"");
                    res.put("PF(B)",df3.format(inst[i].getPF_B())+"");
                    res.put("PF(C)",df3.format(inst[i].getPF_C())+"");
                    res.put("KW(A)",df3.format(inst[i].getKW_A())+"");
                    res.put("KW(B)",df3.format(inst[i].getKW_B())+"");
                    res.put("KW(C)",df3.format(inst[i].getKW_C())+"");
                    res.put("KVAR(A)",df3.format(inst[i].getKVAR_A())+"");
                    res.put("KVAR(B)",df3.format(inst[i].getKVAR_B())+"");
                    res.put("KVAR(C)",df3.format(inst[i].getKVAR_C())+"");
                    res.put("KVA(A)",df3.format(inst[i].getKVA_A())+"");
                    res.put("KVA(B)",df3.format(inst[i].getKVA_B())+"");
                    res.put("KVA(C)",df3.format(inst[i].getKVA_C())+"");
                    res.put("Line Frequency",inst[i].getLINE_FREQUENCY()+"");
                }
            }
            
            res.put("LP Channel Information", getLPChannelMap());
            
            log.debug("==================LP3410CP_005 getData End()====================");
        }
        catch (Exception e)
        {
            log.warn("Get Data Error=>",e);
        }

        return res;
    }

	@Override
	public Double getMeteringValue() {
		// TODO Auto-generated method stub
		return null;
	}
}
