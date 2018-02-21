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
import com.aimir.fep.meter.parser.lgrw3410Table.LGRW3410_EV;
import com.aimir.fep.meter.parser.lgrw3410Table.LGRW3410_LP;
import com.aimir.fep.meter.parser.lgrw3410Table.LGRW3410_LP_Circuit;
import com.aimir.fep.meter.parser.lgrw3410Table.LGRW3410_MDM;
import com.aimir.fep.meter.parser.lgrw3410Table.LGRW3410_PB;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing LGRW3410 Meter Data
 *
 * @author Kang, Soyi (ksoyi@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2008-03-28 $,
 */
public class LGRW3410 extends MeterDataParser implements java.io.Serializable {
	
	private static final long serialVersionUID = 8853953301723914819L;

	private static Log log = LogFactory.getLog(LGRW3410.class);
    
    private byte[] rawData = null;
    private int lpcount;
    private Double lp = null;
    private Double lpValue = null;
    private String meterId = null;
    private int regK =1;
    private int flag = 0;
    
    private double ke =0.05;
    
    private byte[] mdm = null;
    private byte[] pb = null;
    private byte[] lpd = null;
    private byte[] lpd2 = null;
    private byte[] ev = null;
    
    private LGRW3410_MDM nuri_mdm = null;
    private LGRW3410_PB nuri_pb = null;
    private LGRW3410_LP nuri_lp = null;
    private LGRW3410_LP_Circuit nuri_lp_circuit = null;
    private LGRW3410_EV nuri_ev = null;
    
    /**
     * constructor
     */
    public LGRW3410()
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
     * parseing Energy Meter Data of LGRW3410 Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
        int offset = 0;
        int LEN_MDM_INFO = 38;
        log.debug("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));
        if (data.length < 53) {

            if(data.length == 38){
                byte[] b = new byte[LEN_MDM_INFO];
                System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
                mdm = b;
                log.debug("[LGRW3410_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
                nuri_mdm = new LGRW3410_MDM(mdm);
                log.debug(nuri_mdm);
            }
            else{
                log.error("[LGRW3410] Data total length[" + data.length + "] is invalid");
            }
            
            return;
        }
        
        byte[] b = new byte[LEN_MDM_INFO];
        System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
        mdm = b;
        offset += LEN_MDM_INFO;
        log.debug("[LGRW3410_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
                
        byte tbName = data[offset];
        offset += 1;
   
        int totlen = data.length;

        int len =0;
        log.debug("offset=["+offset+"]");

	    if(tbName == (byte)0xA6){
	    	len = 210;//msb ->lsb
	        b = new byte[len];
	        System.arraycopy(data,offset,b,0,len);
	        offset += len;
	        pb = b;
	        ev =b;
	        log.debug("[NURI_PB] len=["+len+"] data=>"+Util.getHexString(pb));
		    
	        if(data.length> (offset+1)){
		        offset += 1;
			    tbName = data[offset];   
	        }
	    }

	    if(tbName == (byte)0x80){
	    	len = totlen - offset;
	        if(len>=24)	{
	    	b = new byte[len];
		        System.arraycopy(data,offset,b,0,len);
		        offset += len;
		        lpd = b;
		        ev =b;
		        log.debug("[NURI_LP] len=["+len+"] data=>"+Util.getHexString(lpd));
	        }else{
	        	log.debug("LP data Packet Length Error. Len= "+len);
	        }
	    }
	    
	    if(tbName == (byte)0x81){
	    	len = totlen - offset;
	        if(len>=24)	{
	    	b = new byte[len];
		        System.arraycopy(data,offset,b,0,len);
		        offset += len;
		        lpd2 = b;
		        ev =b;
		        log.debug("[NURI_LP_Circuit] len=["+len+"] data=>"+Util.getHexString(lpd2));
	        }else{
	        	log.debug("LP Circuit data Packet Length Error. Len= "+len);
	        }
	    }
        
        nuri_mdm = new LGRW3410_MDM(mdm);
        log.debug(nuri_mdm);
        
        if(mdm != null){
            
          //  this.regK = nuri_mdm.getREG_K();
        }
        
        if(pb != null){
            nuri_pb = new LGRW3410_PB(pb, ke);
            this.meterId = nuri_pb.getMeterManufacture().getMeterId();
            this.meterTime =  nuri_pb.getMeterDateTime();
        }

        if(lpd != null){
            nuri_lp = new LGRW3410_LP(lpd, regK, ke);
            this.meterId = nuri_lp.getMeterManufacture().getMeterId();
        }
        if(lpd2 != null){
            nuri_lp_circuit = new LGRW3410_LP_Circuit(lpd2, regK, ke);
            this.meterId = nuri_lp_circuit.getMeterManufacture().getMeterId();
        }
        
        if(ev !=null){
        	nuri_ev = new LGRW3410_EV(ev);
        }

        log.debug("NURI_LGRW3410 Data Parse Finished :: DATA["+toString()+"]");
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
       
        sb.append("NURI_LGRW3410 Meter DATA[");
        sb.append("(meterId=").append(meterId).append("),");
        sb.append("]\n");

        return sb.toString();
    }
    
    public String getLPChannelMap(){
        String res ="";

            res+="ch1=Active Energy[kWh],v1=Active Power[kW],";
            res+="ch2=Lag Reactive Energy[kVarh],v2=Lag Reactive Power[kVar],";
            res+="pf=PF";
        return res;
        
    }
    
    public int getLPChannelCount(){

        return 5;//
    }

    public int getResolution(){

        int resol = 15;
        try{
            if(lpd!= null){
                resol = nuri_lp.getINTERVAL_TIME();
            }
            else if(lpd2!= null){
            	resol = nuri_lp_circuit.getINTERVAL_TIME();
            }else{
            	  return resol;
            }
        } catch(Exception e){
            
        }
        return resol;
    }
    
    public String getMeterLog(){
        
        StringBuffer sb = new StringBuffer();
        if(pb != null){
            try
            {
                sb.append(nuri_pb.getMeterSatus().getLog());
            }
            catch (Exception e){log.warn("get meter status",e);}
        }
        log.debug("getMeterLog ["+sb.toString()+"]");
        return sb.toString();
    }
    
    public int getTrans() throws Exception {
     	 int vt = 1;
     	 if(pb != null){
              try
              {
             	 vt =  nuri_pb.getCT_PT();
              } catch (Exception e){log.warn("get trans error",e);}
     	 }
     	 log.debug("getCT_PT() :"+vt);
          return vt;
     }
    
    
    public int getMeterStatusCode() {
        return this.nuri_mdm.getERROR_STATUS();
    }
    
    public String getBillingDay() throws Exception {
    	 String billday = "";
    	 if(pb != null){
             try
             {
            	 billday =  ""+nuri_pb.geBillingDay();
             } catch (Exception e){log.warn("get BillingDay error",e);}
    	 }
    	 log.debug("geBillingDay() :"+billday);
         return billday;
    }
        
    @SuppressWarnings("unchecked")
    public LPData[] getLPData(){
        
        try{
            if(lpd != null)
                return nuri_lp.parse();
            else if(lpd2 != null)
            	return nuri_lp_circuit.parse();
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
                map.put("modemStatus",this.nuri_mdm.getERROR_STATUS_STRING());
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
             
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
        
        try
        {
            log.debug("==================LGRW3410 getData start()====================");
            tou_block = getPrevBilling();
            lplist = getLPData();
            
			res.put("<b>[Meter Configuration Data]</b>", "");
            if(nuri_pb != null){          
                res.put("CT_PT",nuri_pb.getCT_PT()+"");
                res.put("Billing Day",nuri_pb.geBillingDay());
                res.put("Current Meter Time",nuri_pb.getMeterDateTime());
                res.put("MeterStatus",nuri_pb.getMeterSatus().toString());
            }
            if(lpd!=null){
            	res.put("MeterStatus",nuri_lp.getMeterSatus().toString());
            }
            if(lpd2!=null){
            	res.put("MeterStatus",nuri_lp_circuit.getMeterSatus().toString());
            }
            if(tou_block != null){
                res.put("<b>[Previous Billing Data]</b>", "");
                res.put("Total Active Energy(kWh)"              ,df3.format(tou_block[0].getSummation(0))+"");
                res.put("Total Reactive Energy(kVarh)"            ,df3.format(tou_block[0].getSummation(1))+"");
                res.put("Total Active Power Max.Demand(kW)"     ,df3.format(tou_block[0].getCurrDemand(0))+"");
                res.put("Total Active Power Max.Demand Time"    ,(String)tou_block[0].getEventTime(0));
                res.put("Total Reactive Power Max.Demand(kVar)"   ,df3.format(tou_block[0].getCurrDemand(1))+"");
                res.put("Total Reactive Power Max.Demand Time"  ,(String)tou_block[0].getEventTime(1));
   
                lpValue = new Double(df3.format(tou_block[1].getSummation(0)));
                res.put("Rate A Active Energy(kWh)"             ,df3.format(tou_block[1].getSummation(0))+"");                
                res.put("Rate A Reactive Energy(kVarh)"           ,df3.format(tou_block[1].getSummation(1))+"");
                res.put("Rate A Active Power Max.Demand(kW)"    ,df3.format(tou_block[1].getCurrDemand(0))+"");
                res.put("Rate A Active Power Max.Demand Time"   ,(String)tou_block[1].getEventTime(0));
                res.put("Rate A Reactive Power Max.Demand(kVar)"  ,df3.format(tou_block[1].getCurrDemand(1))+"");
                res.put("Rate A Reactive Power Max.Demand Time" ,(String)tou_block[1].getEventTime(1));
    
                res.put("Rate B Active Energy(kWh)"             ,df3.format(tou_block[2].getSummation(0))+"");
                res.put("Rate B Reactive Energy(kVarh)"           ,df3.format(tou_block[2].getSummation(1))+"");
                res.put("Rate B Active Power Max.Demand(kW)"    ,df3.format(tou_block[2].getCurrDemand(0))+"");
                res.put("Rate B Active Power Max.Demand Time"   ,(String)tou_block[2].getEventTime(0));
                res.put("Rate B Reactive Power Max.Demand(kVar)"  ,df3.format(tou_block[2].getCurrDemand(1))+"");
                res.put("Rate B Reactive Power Max.Demand Time" ,(String)tou_block[2].getEventTime(1));
   
                res.put("Rate C Active Energy(kWh)"             ,df3.format(tou_block[3].getSummation(0))+"");
                res.put("Rate C Reactive Energy(kVarh)"           ,df3.format(tou_block[3].getSummation(1))+"");
                res.put("Rate C Active Power Max.Demand(kW)"    ,df3.format(tou_block[3].getCurrDemand(0))+"");
                res.put("Rate C Active Power Max.Demand Time"   ,(String)tou_block[3].getEventTime(0));
                res.put("Rate C Reactive Power Max.Demand(kVar)"  ,df3.format(tou_block[3].getCurrDemand(1))+"");
                res.put("Rate C Reactive Power Max.Demand Time" ,(String)tou_block[3].getEventTime(1));

            }
            
            if(lplist != null && lplist.length > 0){
                res.put("[Load Profile Data(kWh)]", "");
                int nbr_chn = 2;//ch1,ch2
                
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
            
            res.put("LP Channel Information", getLPChannelMap());
            
            log.debug("==================LGRW3410 getData End()====================");
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
