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
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.DLMSKepcoTable.DLMSKepco_CB;
import com.aimir.fep.meter.parser.DLMSKepcoTable.DLMSKepco_EV;
import com.aimir.fep.meter.parser.DLMSKepcoTable.DLMSKepco_LP;
import com.aimir.fep.meter.parser.DLMSKepcoTable.DLMSKepco_MDM;
import com.aimir.fep.meter.parser.DLMSKepcoTable.DLMSKepco_MTR;
import com.aimir.fep.meter.parser.DLMSKepcoTable.DLMSKepco_PB;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing DLMSKEpco Meter Data
 *
 * @author Kang, Soyi (ksoyi@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2008-03-28 $,
 */
public class CES_LSMeter extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = -200276442876424355L;

	private static Log log = LogFactory.getLog(CES_LSMeter.class);
    
    private byte[] rawData = null;
    private int lpcount;
    private int resolution =15;
    private Double lp = null;
    private Double lpValue = null;
    private String meterId = null;
    private int regK =1;
    private double ke = 0.0005; 
    private int ct =1;
    private int vt =1;
    private int flag = 0;
    private String meterDatetime ="20000101000000";
    private String demandResetDatetime ="20000101000000";
    
    private byte[] mdm = null;
    private byte[] mtr = null;
    private byte[] pb = null;
    private byte[] cb = null;
    private byte[] lpd = null;
    private byte[] ev = null;
    private byte[] ev2 = null;
    
    private DLMSKepco_MDM nuri_mdm = null;
    private DLMSKepco_MTR nuri_mtr = null;
    private DLMSKepco_PB nuri_pb = null;
    private DLMSKepco_CB nuri_cb = null;
    private DLMSKepco_LP nuri_lp = null;
    private DLMSKepco_EV nuri_ev = null;
     
    /**
     * constructor
     */
    public CES_LSMeter()
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
     * parseing Energy Meter Data of DLMSKepco Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
        int offset = 0;
        int LEN_MDM_INFO = 38;
        int LEN_MTR_INFO = 43;
        int LEN_METER_STATUS =2;
        int OFS_METER_STATUS =49;
        
        log.debug("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));
        if (data.length < 53) {

            if(data.length == 38)
            {
                byte[] b = new byte[LEN_MDM_INFO];
                System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
                mdm = b;
                log.debug("[DLMSKepco_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
                nuri_mdm = new DLMSKepco_MDM(mdm);
                log.debug(nuri_mdm);
            }
            else
            {
                log.error("[DLMSKepco] Data total length[" + data.length + "] is invalid");
            }
            
            return;
        }
        
        byte[] b = new byte[LEN_MDM_INFO];
        System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
        mdm = b;
        offset += LEN_MDM_INFO;
        log.debug("[DLMSKepco_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
                
        byte tbName = data[offset];
        offset += 1;
   
        int totlen = data.length;

        int len =0;
        log.debug("offset=["+offset+"]");

        b = new byte[LEN_MTR_INFO];
        System.arraycopy(data,offset,b,0,LEN_MTR_INFO);
        mtr = b;
        offset += LEN_MTR_INFO;
        log.debug("[DLMSKepco_MTR] len=["+LEN_MTR_INFO+"] data=>"+Util.getHexString(mtr));
        nuri_mtr = new DLMSKepco_MTR(mtr);
        log.debug(nuri_mtr);
        
        b = new byte[LEN_METER_STATUS];
        System.arraycopy(data,OFS_METER_STATUS,b,0,LEN_METER_STATUS);
        ev2 =b;
        log.debug("[DLMSKepco_METER_STATUS] len=["+LEN_METER_STATUS+"] data=>"+Util.getHexString(ev2));
        
	    if(tbName == (byte)0xA6)
	    {
	        int hasPreviousBilling = data[offset];
	        log.debug("hasPreviousBilling :"+Util.getHexString((char)data[offset]));
	        offset++;
	        int cntOfPowerFailure = data[offset];
	        log.debug("cntOfPowerFailure : "+Util.getHexString((char)data[offset]));
	        offset++;
	        	        
	    	//current billing
	    	len = 63;//msb ->lsb
	        b = new byte[len];
	        System.arraycopy(data,offset,b,0,len);
	        offset += len;
	        cb = b;
	        log.debug("[NURI_CB] len=["+len+"] data=>"+Util.getHexString(cb));
	        
	        if(hasPreviousBilling>0){
		        //previous billing
		        b = new byte[len];
		        System.arraycopy(data,offset,b,0,len);
		        offset += len;
		        pb = b;
		        log.debug("[NURI_PB] len=["+len+"] data=>"+Util.getHexString(pb));
	        }else{
	        	offset += len;
	        	log.debug("hasPreviousBilling : "+hasPreviousBilling);
	        }
	        
	        if(cntOfPowerFailure>0){
		        // event
		        len = cntOfPowerFailure*2*7;//totlen - offset;
		        b = new byte[len];
		        System.arraycopy(data,offset,b,0,len);
		        offset += len;
		        ev = b;
		        log.debug("[NURI_EV] len=["+len+"] data=>"+Util.getHexString(ev));
		        offset += len;
	        }
	    }else if(tbName == (byte)0x80 || tbName == (byte)0x81){
	    	len = totlen - offset;
	        if(len>=24)	{
	    	b = new byte[len];
		        System.arraycopy(data,offset,b,0,len);
		        offset += len;
		        lpd = b;
		        log.debug("[NURI_LP] len=["+len+"] data=>"+Util.getHexString(lpd));
	        }else{
	        	log.debug("LP data Packet Length Error. Len= "+len);
	        }
	        offset += len;
	    }
	    
	    //Circuit LP
	    if(offset<totlen && data[offset]==0x81){
	    	len = totlen - offset;
	        if(len>=24)	{
	    	b = new byte[len];
		        System.arraycopy(data,offset,b,0,len);
		        offset += len;
		        lpd = b;
		        log.debug("[NURI_LP_Circuit] len=["+len+"] data=>"+Util.getHexString(lpd));
	        }else{
	        	log.debug("LP Circuit data Length Error. Len= "+len);
	        }
	        offset += len;
	    }
        
        nuri_mdm = new DLMSKepco_MDM(mdm);
        log.debug(nuri_mdm);
        
        if(mdm != null){
            
            
        }
        if(mtr != null){
        	nuri_mtr = new DLMSKepco_MTR(mtr);
            this.meterId = nuri_mtr.getMETER_ID();
            this.meterDatetime = nuri_mtr.getCURRENT_METER_DATETIME();
            this.demandResetDatetime = nuri_mtr.getLAST_DEMAND_RESET_DATETIME();
            this.resolution = nuri_mtr.getResolution();
            this.regK = nuri_mtr.getREG_K();
            this.ke = (double) 1.0/regK;
            this.ct = nuri_mtr.getCT();
            this.vt = nuri_mtr.getVT();
            this.meterTime =  nuri_mtr.getCURRENT_METER_DATETIME();
        }
        
        if(pb != null){
            nuri_pb = new DLMSKepco_PB(pb, demandResetDatetime, regK);
        }

        if(cb != null){
            nuri_cb = new DLMSKepco_CB(cb, meterDatetime, regK);
        }
        
        if(lpd != null){
            nuri_lp = new DLMSKepco_LP(lpd, regK, resolution, ct*vt);
        }
        
        if(ev != null || ev2 !=null ){
            nuri_ev = new DLMSKepco_EV(ev, ev2);
        }
        log.debug("NURI_DLMSKepco Data Parse Finished :: DATA["+toString()+"]");
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
       
        sb.append("NURI_DLMSKepco Meter DATA[");
        sb.append("(meterId=").append(meterId).append("),");
        sb.append("]\n");

        return sb.toString();
    }
    
    public String getLPChannelMap(){
        String res ="";

            res+="ch1=Active Energy[kWh]<br>";
            res+="ch2=Lag Reactive Energy[kVarh]<br>";
        return res;
        
    }
    
    public int getLPChannelCount(){

        return 5;//
    }

    public int getResolution(){

        int resol = 15;
        try{
            if(mtr!= null){
                resol = nuri_mtr.getResolution();
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
                sb.append(nuri_mtr.getMeterErrorFlag().getLog());
                sb.append(nuri_mtr.getMeterCautionFlag().getLog());
            }
            catch (Exception e){log.warn("get meter status",e);}
        }
        log.debug("getMeterLog ["+sb.toString()+"]");
        return sb.toString();
    }
    
    public int getMeterStatusCode() {
        return this.nuri_mdm.getERROR_STATUS();
    }
    
    public String getBillingDay() throws Exception {
    	 String billday = "";
    	 if(mtr != null){
             try
             {
            	 billday =  ""+nuri_mtr.getBillDay();
             } catch (Exception e){log.warn("get BillingDay error",e);}
    	 }
    	 log.debug("geBillingDay() :"+billday);
         return billday;
    }
    
    public int getVT() throws Exception {
   	 int vt = 1;
   	 if(mtr != null){
            try
            {
           	 vt =  nuri_mtr.getVT();
            } catch (Exception e){log.warn("get VT error",e);}
   	 }
   	 log.debug("getVT() :"+vt);
        return vt;
   }
    
    public int getCT() throws Exception {
      	 int vt = 1;
      	 if(mtr != null){
               try
               {
              	 vt =  nuri_mtr.getCT();
               } catch (Exception e){log.warn("get CT error",e);}
      	 }
      	 log.debug("getCT() :"+vt);
           return vt;
      }
    
    public int getREG_K() throws Exception {
     	 int vt = 1;
     	 if(mtr != null){
              try
              {
             	 vt =  nuri_mtr.getREG_K();
              } catch (Exception e){log.warn("get REG_K error",e);}
     	 }
     	 log.debug("getREG_K() :"+vt);
          return vt;
     }
    
    public double getKE() throws Exception {
    	 double ke = 0.01;
    	 if(mtr != null){
             try
             {
            	 int regk =  nuri_mtr.getREG_K();
            	 ke = (double) 1.0/regk;
             } catch (Exception e){log.warn("get KE error",e);}
    	 }
    	 log.debug("getKE() :"+ke);
         return ke;
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
    
    public EventLogData[] getEventLog(){
    	if(ev != null){
    		return nuri_ev.getEvent();
    	}else{
    		return null;
    	}
    }
    
    public TOU_BLOCK[] getPrevBilling(){
    	
    	if(pb != null){
            try{
                TOU_BLOCK[] blk = nuri_pb.getTOU_BLOCK();
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
    	if(cb != null){
            try{
                TOU_BLOCK[] blk = nuri_cb.getTOU_BLOCK();
                return blk;  
            }catch(Exception e){
                log.error("curr error",e);
                return null;
            }
        }
    	else
    		return null;    	   	
    }
    
    public HashMap<String, String> getMdmData(){
        
        HashMap<String, String> map = null;
        
        try{
            if(mdm != null){
                map = new HashMap<String, String>();
                map.put("mcuType","6");
                
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
    public LinkedHashMap getData()
    {
        LinkedHashMap<String, Serializable> res = new LinkedHashMap<String, Serializable>(16,0.75f,false);
        TOU_BLOCK[] current_tou_block = null;
        TOU_BLOCK[] previous_tou_block = null;
        LPData[] lplist = null;
             
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
        Supplier supplier = meter.getSupplier();
        String lang = "en";
        String country = "us";
        try{
            lang = supplier.getLang().getCode_2letter();
            country = supplier.getCountry().getCode_2letter();
        }catch(Exception e){     	
        }

        
        try
        {
            log.debug("==================DLMSKepco getData start()====================");
            current_tou_block = getCurrBilling();
            previous_tou_block = getPrevBilling();
            lplist = getLPData();
            
			res.put("<b>[Meter Configuration Data]</b>", "");
            if(nuri_mtr != null){          
              //  res.put("ProgramNameVer",nuri_mtr.getProgramNameVer().toString());
                res.put("CT",nuri_mtr.getCT()+"");
                res.put("PT",nuri_mtr.getCT()+"");
                res.put("Billing Day",nuri_mtr.getBillDay());
                res.put("Meter ID",nuri_mtr.getMETER_ID());
                res.put("Current Meter Time",TimeLocaleUtil.getLocaleDate(nuri_mtr.getCURRENT_METER_DATETIME(), lang, country));
                res.put("MeterErrorFlag",nuri_mtr.getMeterErrorFlag().getLog());
                res.put("MeterCautionFlag",nuri_mtr.getMeterCautionFlag().getLog());
            }

            if(current_tou_block != null){
                res.put("Current Billing Data", "-");
                res.put("Total Active Energy(kWh)"              ,df3.format(current_tou_block[0].getSummation(0))+"");
                res.put("Total Reactive Energy(kVarh)"            ,df3.format(current_tou_block[0].getSummation(1))+"");
           //     res.put("Total Active Power Max.Demand(kW)"     ,df3.format(current_tou_block[0].getCurrDemand(0))+"");
           //     res.put("Total Active Power Max.Demand Time"    ,(String)current_tou_block[0].getEventTime(0));
           //     res.put("Total Active Power Cum.Demand(kW)"     ,df3.format(current_tou_block[0].getCumDemand(0))+"");
                    
                lpValue = new Double(df3.format(current_tou_block[1].getSummation(0)));
                res.put("Rate A Active Energy(kWh)"             ,df3.format(current_tou_block[1].getSummation(0))+"");                
                res.put("Rate A Reactive Energy(kVarh)"           ,df3.format(current_tou_block[1].getSummation(1))+"");
                res.put("Rate A Active Power Max.Demand(kW)"    ,df3.format(current_tou_block[1].getCurrDemand(0))+"");
                res.put("Rate A Active Power Max.Demand Time"   ,(String)current_tou_block[1].getEventTime(0));
                res.put("Rate A Active Power Cum.Demand(kW)"    ,df3.format(current_tou_block[1].getCumDemand(0))+"");
                    
                res.put("Rate B Active Energy(kWh)"             ,df3.format(current_tou_block[2].getSummation(0))+"");
                res.put("Rate B Reactive Energy(kVarh)"           ,df3.format(current_tou_block[2].getSummation(1))+"");
                res.put("Rate B Active Power Max.Demand(kW)"    ,df3.format(current_tou_block[2].getCurrDemand(0))+"");
                res.put("Rate B Active Power Max.Demand Time"   ,(String)current_tou_block[2].getEventTime(0));
                res.put("Rate B Active Power Cum.Demand(kW)"    ,df3.format(current_tou_block[2].getCumDemand(0))+"");
                
                res.put("Rate C Active Energy(kWh)"             ,df3.format(current_tou_block[3].getSummation(0))+"");
                res.put("Rate C Reactive Energy(kVarh)"           ,df3.format(current_tou_block[3].getSummation(1))+"");
                res.put("Rate C Active Power Max.Demand(kW)"    ,df3.format(current_tou_block[3].getCurrDemand(0))+"");
                res.put("Rate C Active Power Max.Demand Time"   ,(String)current_tou_block[3].getEventTime(0));
                res.put("Rate C Active Power Cum.Demand(kW)"    ,df3.format(current_tou_block[3].getCumDemand(0))+"");
                
            }
            
            if(lplist != null && lplist.length > 0){
                res.put("Load Profile Data(kWh)", "-");
                int nbr_chn = 2;//ch1,ch2
                if(nuri_lp!= null){
                    nbr_chn = 2;//TODO FIX HARD CODE
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
                    if(supplier !=null){
                        
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
            
            res.put("LP Channel Information", getLPChannelMap());
            
            log.debug("==================DLMSKepco getData End()====================");
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
