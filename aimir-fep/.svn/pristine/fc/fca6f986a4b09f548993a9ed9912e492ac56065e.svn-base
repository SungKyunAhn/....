package com.aimir.fep.meter.parser;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.HMData;
import com.aimir.fep.meter.parser.kdhTable.TMTR_CURRENT;
import com.aimir.fep.meter.parser.kdhTable.TMTR_DAY;
import com.aimir.fep.meter.parser.kdhTable.TMTR_EVENT;
import com.aimir.fep.meter.parser.kdhTable.TMTR_INFO;
import com.aimir.fep.meter.parser.kdhTable.TMTR_LP;
import com.aimir.fep.meter.parser.kdhTable.TMTR_MDM;
import com.aimir.fep.meter.parser.kdhTable.TMTR_MONTH;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing KDH Meter Data
 * implemented in Autrailia 
 *
 * @author Yeon Kyoung Park (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-12-14 12:00:15 +0900 $,
 */
public class KDH extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 2476458444183132983L;
	private static Log log = LogFactory.getLog(KDH.class);
    private byte[] rawData = null;
    private int lpcount;
    private Double lp = null;
    private Double lpValue = null;
    private String meterId = null;
    private int flag = 0;

    private byte[] mdm = null;
    private byte[] rmtr_info = null;
    private byte[] rmtr_lp = null;
    private byte[] rmtr_day = null;
    private byte[] rmtr_month = null; 
    private byte[] rmtr_current = null;
    private byte[] rmtr_event = null;
    
    private TMTR_MDM tmtr_mdm = null;    
    private TMTR_INFO tmtr_info = null;
    private TMTR_LP tmtr_lp = null;
    private TMTR_DAY tmtr_day = null;
    private TMTR_MONTH tmtr_month = null;
    private TMTR_CURRENT tmtr_current = null;
    private TMTR_EVENT tmtr_event = null;

    /**
     * constructor
     */
    public KDH()
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
        return meterId;
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
     * parseing Energy Meter Data of KDH Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
        int offset = 0;
        int LEN_MDM_INFO = 38;
        
        log.debug("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));
        if (data.length < 53) {

            if(data.length == LEN_MDM_INFO)
            {
                byte[] b = new byte[LEN_MDM_INFO];
                System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
                mdm = b;
                log.debug("[KDH_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
                tmtr_mdm = new TMTR_MDM(mdm);
                log.debug(tmtr_mdm);
            }
            else
            {
                log.error("[KDH] Data total length[" + data.length + "] is invalid");
            }
            
            return;
        }
        
        byte[] b = new byte[LEN_MDM_INFO];
        System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
        mdm = b;
        offset += LEN_MDM_INFO;
        log.debug("[KDH_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
        tmtr_mdm = new TMTR_MDM(mdm);
        log.debug(tmtr_mdm);
   
        int totlen = data.length;

        int len =0;
        log.debug("offset=["+offset+"]");

        b = new byte[TMTR_INFO.LEN_TMTR_INFO];
        System.arraycopy(data,offset,b,0,TMTR_INFO.LEN_TMTR_INFO);
        rmtr_info = b;
        offset += TMTR_INFO.LEN_TMTR_INFO;
        log.debug("[TMTR_INFO] len=["+TMTR_INFO.LEN_TMTR_INFO+"] data=>"+Util.getHexString(rmtr_info));
        tmtr_info = new TMTR_INFO(rmtr_info);
        this.meterId = tmtr_info.getMETER_ID();
        this.meterTime = tmtr_info.getDateTime();
        log.debug(tmtr_info);
                
        while(offset < data.length){
            
            log.debug("[Offset] =["+offset+"] data=>"+(int)(data[offset]&0xFF));
            
            switch((int)(data[offset++] & 0xFF)){
            case TMTR_LP.TABLE_CODE:
            	log.debug("[TMTR_LP]");
                int LEN_TMTR_LP = 0;
                LEN_TMTR_LP = DataFormat.hex2dec(data,offset,2);
                log.debug("LEN_TMTR_LP :"+LEN_TMTR_LP);
                offset +=2;
                b = new byte[LEN_TMTR_LP];
                System.arraycopy(data,offset,b,0,LEN_TMTR_LP);
                rmtr_lp = b;
                offset += LEN_TMTR_LP;
                
                log.debug("[TMTR_LP] len=["+LEN_TMTR_LP+"] data=>"+Util.getHexString(rmtr_lp));
                tmtr_lp = new TMTR_LP(rmtr_lp,TMTR_LP.TABLE_KIND);
                log.debug(tmtr_lp);
                break;
            case TMTR_DAY.TABLE_CODE:
                int LEN_TMTR_DAY = 0;
                LEN_TMTR_DAY = DataFormat.hex2dec(data,offset,2);
                offset +=2;
                b = new byte[LEN_TMTR_DAY];
                System.arraycopy(data,offset,b,0,LEN_TMTR_DAY);
                rmtr_day = b;
                offset += LEN_TMTR_DAY;
                
                log.debug("[TMTR_DAY] len=["+LEN_TMTR_DAY+"] data=>"+Util.getHexString(rmtr_day));
                tmtr_day = new TMTR_DAY(rmtr_day,TMTR_DAY.TABLE_KIND);
                log.debug(tmtr_day);       
                break;
            case TMTR_MONTH.TABLE_CODE:
                int LEN_TMTR_MONTH = 0;
                LEN_TMTR_MONTH = DataFormat.hex2dec(data,offset,2);
                offset +=2;
                b = new byte[LEN_TMTR_MONTH];
                System.arraycopy(data,offset,b,0,LEN_TMTR_MONTH);
                rmtr_month = b;
                offset += LEN_TMTR_MONTH;
                
                log.debug("[TMTR_MONTH] len=["+LEN_TMTR_MONTH+"] data=>"+Util.getHexString(rmtr_month));
                tmtr_month = new TMTR_MONTH(rmtr_month,TMTR_MONTH.TABLE_KIND);
                log.debug(tmtr_month);
                break;
            case TMTR_CURRENT.TABLE_CODE:
                int LEN_TMTR_CURRENT = 0;
                LEN_TMTR_CURRENT = DataFormat.hex2dec(data,offset,2);
                offset +=2;
                b = new byte[LEN_TMTR_CURRENT];
                System.arraycopy(data,offset,b,0,LEN_TMTR_CURRENT);
                rmtr_current = b;
                offset += LEN_TMTR_CURRENT;
                
                log.debug("[TMTR_CURRENT] len=["+LEN_TMTR_CURRENT+"] data=>"+Util.getHexString(rmtr_current));
                tmtr_current = new TMTR_CURRENT(rmtr_current);
                log.debug(tmtr_current);
                break;
            case TMTR_EVENT.TABLE_CODE:
                int LEN_TMTR_EVENT = 0;
                LEN_TMTR_EVENT = DataFormat.hex2dec(data,offset,2);
                offset +=2;
                b = new byte[LEN_TMTR_EVENT];
                System.arraycopy(data,offset,b,0,LEN_TMTR_EVENT);
                rmtr_event = b;
                offset += LEN_TMTR_EVENT;
                
                log.debug("[TMTR_EVENT] len=["+LEN_TMTR_EVENT+"] data=>"+Util.getHexString(rmtr_event));
                tmtr_event = new TMTR_EVENT(rmtr_event);
                log.debug(tmtr_event); 
                break;
            }

        }

        log.debug("KDH Data Parse Finished :: DATA["+toString()+"]");
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
       
        sb.append("KDH Meter DATA[");
        sb.append("(meterId=").append(meterId).append("),");
        sb.append("]\n");

        return sb.toString();
    }
    
    public String getLPChannelMap(){
        return "";
    }
    
    public int getLPChannelCount(){
        return 8;//ch1,ch2,v1,v2,pf
    }

    public int getResolution(){
        return 60;
    }
    
    public String getMeterLog(){

        try{
            if(tmtr_info != null){
                return tmtr_info.getMeterCautionFlag().getLog();
            }else{
                return "";
            }
        }catch(Exception e){
            
        }
        return "";
    }
    
    public EventLogData[] getMeterStatusLog(){
        return null;
    }
    
    public EventLogData[] getEventLog(){
        if(tmtr_event != null)
            return this.tmtr_event.getEventLogData();
        else
            return null;
    }
    
    public int getMeterStatusCode() {
        try{
            if (tmtr_info != null) {
                return tmtr_info.getMeterCautionFlag().getStatusCode();
            }
            else {
                return 0;
            }
        }catch(Exception e){
            
        }
        return 0;
    }

    public HMData[] getLPHMData(){

        if(tmtr_lp != null)
            return tmtr_lp.getData();
        else
            return null;
    }
    
    public HMData[] getCurrentData(){
        if(tmtr_current != null){
            log.debug("KDH["+tmtr_current.toString());
            return tmtr_current.getData();
        }
        else
            return null;
    }
    
    public HMData[] getDayData(){
        if(tmtr_day != null){
            log.debug("KDH["+tmtr_day.toString());
            return tmtr_day.getData();
        }
        else
            return null;
    }
    
    public HMData[] getMonthData(){
        if(tmtr_month != null){
            log.debug("KDH["+tmtr_month.toString());
            return tmtr_month.getData();
        }
        else
            return null;
    }

    /**
     * get Data
     */
    @SuppressWarnings("unchecked")
    public LinkedHashMap getData()
    {
        LinkedHashMap<String, Serializable> res = new LinkedHashMap(16,0.75f,false);
        HMData[] lp = null;
        HMData[] current = null;
        HMData[] day = null;
        HMData[] month = null;

        EventLogData[] eventlogdata = null;
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());

        try
        {
            lp = getLPHMData();
            current = getCurrentData();
            day = getDayData();
            month = getMonthData();
            eventlogdata = getEventLog();
			res.put("<b>[Meter Configuration Data]</b>", "");
            if(tmtr_info != null){
                res.put("Meter Type",tmtr_info.getMETER_TYPE_NAME());
                res.put("Meter Log",tmtr_info.getMeterCautionFlag().getLog());
                res.put("Meter Time",tmtr_info.getDateTime());
            }  
            DecimalFormat decimalf=null;
            SimpleDateFormat datef14=null;
            if(lp !=null && lp.length>0){
            	res.put("[LP - Hourly Data]", "");
            	for(int i = 0; i < lp.length; i++){
	            	String datetime = lp[i].getDate()+""+lp[i].getTime();

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
                   
	            	String val = "";
	            	Double[] ch = lp[i].getCh();
	                for(int k = 0; k < ch.length ; k++){
                        val += "<span style='margin-right: 40px;'>ch"+(k+1)+"="+df3.format(ch[k])+"</span>";
	                }
                    res.put("LP"+" "+date, val);
            	}
            }
           
			if(day !=null && day.length>0){
				res.put("[LP - Daily Data]", "");
				for(int i = 0; i < day.length; i++){
	            	String datetime = day[i].getDate();
	            	String val = "";
	            	Double[] ch = day[i].getCh();
	                for(int k = 0; k < ch.length ; k++){
	                    val += "ch"+(k+1)+"="+df3.format(ch[k])+"  ";
	                }
	                res.put("DAY"+datetime, val);
            	}
			}
			
			if(month !=null && month.length>0){
				res.put("[LP - Monthly Data]", "");
				for(int i = 0; i < month.length; i++){
	            	String datetime = month[i].getDate();
	            	String val = "";
	            	Double[] ch = month[i].getCh();
	                for(int k = 0; k < ch.length ; k++){
	                    val += "ch"+(k+1)+"="+df3.format(ch[k])+"  ";
	                }
	                res.put("MONTH"+datetime, val);
            	}
			}
			
			if(current !=null && current.length>0){
				res.put("[Current Data]", "");
            	for(int i = 0; i < current.length; i++){
	            	String datetime = current[i].getDate()+""+current[i].getTime();
	            	String val = "";
	            	Double[] ch = current[i].getCh();
	                for(int k = 0; k < ch.length ; k++){
	                    val += "ch"+(k+1)+"="+df3.format(ch[k])+"  ";
	                }
	                res.put("Current"+datetime, val);
            	}
			}
			
			if(eventlogdata !=null && eventlogdata.length>0){
				res.put("[EVENT LOG]", "");
				for(int i = 0; i < eventlogdata.length; i++)
	            {
		            res.put(""+eventlogdata[i].getDate()+""+eventlogdata[i].getTime(), ""+eventlogdata[i].getMsg());
	            }
			}
        }
        catch (Exception e)
        {
            log.warn("Get Data Error=>",e);
        }

        return res;
    }
    
    public HashMap<String, String> getMdmData(){
        
        HashMap<String, String> map = null;
        
        try{
            if(mdm != null){
                map = new HashMap<String, String>();
                map.put("mcuType","6");
                
                if(this.tmtr_mdm.getFW_VER().startsWith("NG")){
                    map.put("protocolType","2");
                }else{
                    map.put("protocolType","1");
                }

                map.put("sysPhoneNumber", this.tmtr_mdm.getPHONE_NUM());
                map.put("id", this.tmtr_mdm.getPHONE_NUM());
                map.put("swVersion", this.tmtr_mdm.getFW_VER());
                map.put("networkStatus", "1");
                map.put("csq", this.tmtr_mdm.getCSQ_LEVEL()+"");
                map.put("modemStatus",this.tmtr_mdm.getERROR_STATUS_STRING());
            }
        }catch(Exception e){
            
        }
        return map;
    }

	@Override
	public Double getMeteringValue() {
		// TODO Auto-generated method stub
		return null;
	}

}