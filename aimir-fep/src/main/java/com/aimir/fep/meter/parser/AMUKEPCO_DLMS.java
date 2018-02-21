package com.aimir.fep.meter.parser;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.amuKepco_dlmsTable.KEPCO_DLMS_CB;
import com.aimir.fep.meter.parser.amuKepco_dlmsTable.KEPCO_DLMS_ERROR;
import com.aimir.fep.meter.parser.amuKepco_dlmsTable.KEPCO_DLMS_INFO;
import com.aimir.fep.meter.parser.amuKepco_dlmsTable.KEPCO_DLMS_LP;
import com.aimir.fep.meter.parser.amuKepco_dlmsTable.KEPCO_DLMS_PB;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing AMU KECPO_DLMS Metering Data
 * 명칭: 한전 DLMS Protocol
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 20. 오전 10:32:42$
 */
public class AMUKEPCO_DLMS extends MeterDataParser implements java.io.Serializable {

	private static Log log = LogFactory.getLog(AMUKEPCO_DLMS.class);
	
	private static final int LEN_TYPE_NAME 			= 1;
	private static final int LEN_TYPE_LENGTH 		= 2;
	
	/* implements requisite  member variable */
	private byte[] 			rawData 				= null;
	private Double 			lp 						= null;
	private Double 			lpValue 				= null;
	private String 			meterId 				= null;
	private int 			flag 					= 0;
	
	KEPCO_DLMS_INFO			kepco_dlms_info			= null;	// Information Field
	KEPCO_DLMS_PB			kepco_dlms_pb			= null;	// Previous Field
	KEPCO_DLMS_CB			kepco_dlms_cb			= null;	// Current Field
	KEPCO_DLMS_LP			kepco_dlms_lp			= null;	// LP DATA Field
	KEPCO_DLMS_ERROR		kepco_dlms_error		= null;	// Error Field
	
	private int 			regK 					= 1;
	private double 			ke 						= 1;
	private int 			LPChannelCount			= 5;
	
	private String 		    sourceAddr				= null;
	
	/**
     * constructor
     */
	public AMUKEPCO_DLMS(){
	}
	
	/**
	 * constructor
	 * @param sourceAddr
	 */
	public AMUKEPCO_DLMS(String sourceAddr){
		this.sourceAddr = sourceAddr;
	}
	
	/**
	 * constructor
	 * @param rawData
	 * @param pulseConst
	 * @throws Exception
	 */
	public AMUKEPCO_DLMS(byte[] rawData , String sourceAddr ) throws Exception{
        this.rawData 	= rawData;
        this.sourceAddr		= sourceAddr;
        try{
        	parse(rawData);
        }catch(Exception e){
        	throw e;
        }
    }
	
	/**
     * get raw Data 
     * @return rawData 
     */
    public byte[] getRawData(){
        return rawData;
    }
    
    /**
     * set raw Data
     * @param rawData
     */
    public void setrawData(byte[] rawData){
    	this.rawData = rawData;
    }
    
    /**
     * get Meter ID
     * @return rawData 
     */
    public String getMeterId() {
		return meterId;
	}
    
    /**
     * set Meter ID
     * @param meterId
     */
    public void setMeterId(String meterId) {
    	this.meterId = meterId;
	}
    
    /**
     * get flag
     * @return flag 
     */
	public int getFlag() {
		return flag;
	}
	
	/**
     * set flag
     * @param flag
     */
	public void setFlag(int flag){
		this.flag = flag;
	}
	
	/**
     * get data length
     * @return length
     */
	public int getLength() {
		if(rawData == null)
            return 0;
        return rawData.length;
	}
	
	/**
	 * get LP
	 * @return lp
	 */
	public Double getLp() {
		return lp;
	}

	/**
	 * get LP Value
	 * @return lpValue
	 */
	public Double getLpValue() {
		return lpValue;
	}
	
	
	/**
	 * get LP Count
	 * @return
	 */
	public int getLPCount(){
    	
		int lpCount = 0;
		try{
			if(kepco_dlms_lp !=null){
				lpCount = kepco_dlms_lp.getLpCount();
			}
		}catch(Exception e){
			log.debug("get LP Count Failed!");
		}
		
		return lpCount;
    }
	
	/**
     * parseing Meter Data of KEPCO DLMS
     * @param data stream of result command
     */
	public void parse(byte[] data) throws Exception {
		log.debug("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));
		int pos = 0;
		int loopCnt = 0;
		
		while( pos < data.length){
			
			/* ********************************************************************
			 *  Loop는 최대 5회 =>  field가 모두 존재 할 경우 
			 *  (Information ,Previous Billing , Current Billing , LP DATA , Error )
			 *  무한 루프를 방지하기 위한 Flag --> loopCnt
			 ******************************************************************** */
			if(loopCnt == 5){
				log.error("Loop Count Over :" + loopCnt);
				break;
			}
			loopCnt++;
			
			// TYPE NAME
			String TypeName	= new String(DataFormat.select(data, pos, LEN_TYPE_NAME));
			pos += LEN_TYPE_NAME;
			// TYPE LENGTH
			byte[] totlen = new byte[LEN_TYPE_LENGTH];
			System.arraycopy(data, pos, totlen, 0, LEN_TYPE_LENGTH);
			pos += LEN_TYPE_LENGTH;
			
			int TypeLen = DataUtil.getIntTo2Byte(totlen);
			
			log.debug("[TYPE : "+TypeName +"]" + "[TYPE LENGTH : "+ TypeLen +"]");
			// field Length > 3 
			if(TypeLen  > LEN_TYPE_NAME + LEN_TYPE_LENGTH){
				
				byte[] iData = new byte[TypeLen-LEN_TYPE_NAME-LEN_TYPE_LENGTH];
				System.arraycopy(data, pos, iData, 0, iData.length);
				pos += iData.length;
				
				
				log.debug("[iData] len=["+(iData.length)+"] data=>"+Util.getHexString(iData));
				
				if("I".equals(TypeName)){
					kepco_dlms_info 	= new KEPCO_DLMS_INFO(iData ,ke);
					this.meterId		= kepco_dlms_info.getMeterSerial();
				}else if("P".equals(TypeName)){
					kepco_dlms_pb		= new KEPCO_DLMS_PB(iData);
				}else if("C".equals(TypeName)){
					kepco_dlms_cb		= new KEPCO_DLMS_CB(iData);
				}else if("L".equals(TypeName)){
					kepco_dlms_lp		= new KEPCO_DLMS_LP(iData, regK , ke);
				}else if("E".equals(TypeName)){
					kepco_dlms_error	= new KEPCO_DLMS_ERROR(iData);
				}else{
					log.error(" [AMU KEPCO DLMS] parse Type Nmae is invalid " + TypeName);
				}
			// field Length < 3 
			}else{
				log.debug("this Field only exist Type Name and Type Length ");
			}		
		}// end of while
	}

	/**
	 * get LP Channel Map
	 * @return
	 */
	public String getLPChannelMap(){
		
        String res ="";

        res+="ch1=Active Energy[kWh],v1=Active Power[kW],";
        res+="ch2=Reactive Energy[kVarh],v2=Lag Reactive Power[kVar],";
        res+="pf=PF";
            
        return res;
    }
    
	/**
     * get LP Channel Count
     * @return
     */
    public int getLPChannelCount(){
        return LPChannelCount;//ch1- ch2
    }
    
    /**
     * set LP Channel Count
     * @param lpCount
     */
    public void setLPChannelCount(int lpChCount){
    	this.LPChannelCount = lpChCount;
    }
    
    /**
     * get Resolution
     * @return
     */
    public int getResolution(){

        int resol = 15;
        try{
            if(kepco_dlms_info != null){
                resol = kepco_dlms_info.getMeterLPInterval();
            }
            else{
            	  return resol;
            }
        } catch(Exception e){
        	log.warn("get Resolution Error " ,e);
        }
        return resol;
    }
    
    
    /**
	 * get Meter Status
	 * @return
	 */
	public String getMeterStatus(){
        
        StringBuffer sb = new StringBuffer();
        if(kepco_dlms_info != null){
            try
            {
                sb.append(kepco_dlms_info.getMeterStatusError().getLog());
                sb.append(kepco_dlms_info.getMeterStatusCaution().getLog());
            }
            catch (Exception e){
            	log.warn("get meter Status Error",e);
            }
        }
        return sb.toString();
    }
	
	/**
	 * get System Status
	 * @return
	 * @throws Exception
	 */
	public String getSystemStatus()throws Exception {
		
		if(kepco_dlms_error != null){
    		try{
    			return kepco_dlms_error.getSystemStatus().getLog();
            }catch (Exception e){
            	log.warn("get System Status error",e);
            }
    	}
		return "";
    }
    
	/**
	 *  get LP Data
	 * @return
	 */
    public LPData[] getLPData(){
        
        try{
            if(kepco_dlms_lp != null){
                return kepco_dlms_lp.parse();
            }else{
            	return null;
            }
                
        }catch(Exception e){
            log.error("lp parse error",e);
        }
        return null;
    }
    
    /**
     * get Previous Billing Data
     * @return
     */
    public TOU_BLOCK[] getPrevBilling(){
    	
    	if(kepco_dlms_pb != null){
            try{
                TOU_BLOCK[] blk = kepco_dlms_pb.getTou_block();
                return blk;  
            }catch(Exception e){
            	log.error("get prevBilling error",e);
                return null;
            }
        }
    	else
    		return null;    	
    }
    
    /**
     * get Current Billing Data
     * @return
     */
    public TOU_BLOCK[] getCurrBilling(){
    	
    	if(kepco_dlms_cb != null){
            try{
                TOU_BLOCK[] blk = kepco_dlms_cb.getTou_block();
                return blk;  
            }catch(Exception e){
            	log.error("get current Billing error",e);
                return null;
            }
        }
    	else
    		return null;    	
    }
    
    
    /**
	 * get MDM Data
	 * @return
	 */
	public HashMap<String, String> getMdmData(){
        
        HashMap<String, String> map = null;
        
        try{
            if(kepco_dlms_info != null){
                map = new HashMap<String, String>();
                map.put("interfaceCode", CommonConstants.Interface.AMU.name());
                map.put("mcuType", "" + CommonConstants.ModemType.IEIU.name());
                
                if(this.kepco_dlms_info.getSwVer().startsWith("NG")){
                    map.put("protocolType","2");
                }else{
                    map.put("protocolType","1");
                }

                map.put("sysPhoneNumber", sourceAddr);
                map.put("id", sourceAddr);
                map.put("networkStatus", "1");
                map.put("csq", "");
                map.put("currentTime" , kepco_dlms_info.getMeterCurrentTime().getCurrnetTime());
                map.put("modemStatus", kepco_dlms_info.getMeterStatusError().getLog()+","+kepco_dlms_info.getMeterStatusCaution().getLog());
               
            }
        }catch(Exception e){
            log.debug("get MDB Data failed");
        }
        return map;
    }
	
	
	/**
     * get Data
     */
	@SuppressWarnings("unchecked")
    public LinkedHashMap getData() {
		
		LinkedHashMap<String, Serializable> res = new LinkedHashMap(16,0.75f,false);
        TOU_BLOCK[] previous_tou_block = null;
		TOU_BLOCK[] current_tou_block = null;
        LPData[] lplist = null;
        
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
		
        try
        {
            log.debug("==================AMU KEPCO DLMS getData start()====================");
            previous_tou_block = getPrevBilling();
            current_tou_block = getCurrBilling();
            lplist = getLPData();
            
			res.put("<b>[Meter Configuration Data]</b>", "");
            if(kepco_dlms_info != null){          
                res.put("REG_K",kepco_dlms_info.getMeterRegK()+"");
                res.put("Billing Day",kepco_dlms_info.getMeterBillingDay());
                res.put("Meter ID", kepco_dlms_info.getMeterNumber());
                res.put("Current Meter Time",kepco_dlms_info.getMeterCurrentTime().getCurrnetTime());
                res.put("Current Program Time",kepco_dlms_info.getMeterCurrentTime().getCurrnetTime());
                res.put("MeterErrorFlag",kepco_dlms_info.getMeterStatusError().getLog());
                res.put("MeterCautionFlag",kepco_dlms_info.getMeterStatusCaution().getLog());
                
            }

            if(current_tou_block != null){
                res.put("<b>[Current Billing Data]</b>", "");
                res.put("T1 Active Energy(kWh)"              ,df3.format(current_tou_block[0].getSummation(0))+"");
                res.put("T1 Reactive Energy(kVarh)"          ,df3.format(current_tou_block[0].getSummation(1))+"");
                res.put("T1 Active Power Max.Demand(kW)"     ,df3.format(current_tou_block[0].getCurrDemand(0))+"");
                res.put("T1 Active Power Max.Demand Time"    ,(String)current_tou_block[0].getEventTime(0));
                res.put("T1 Reactive Power Max.Demand(kVar)" ,df3.format(current_tou_block[0].getCurrDemand(1))+"");
                res.put("T1 Reactive Power Max.Demand Time"  ,(String)current_tou_block[0].getEventTime(1));
                res.put("T1 Active Power Cum.Demand(kW)"     ,df3.format(current_tou_block[0].getCumDemand(0))+"");
                res.put("T1 Reactive Power Cum.Demand(kVar)" ,df3.format(current_tou_block[0].getCumDemand(1))+"");
                    
                lpValue = new Double(df3.format(current_tou_block[1].getSummation(0)));
                res.put("T2 Active Energy(kWh)"             ,df3.format(current_tou_block[1].getSummation(0))+"");                
                res.put("T2 Reactive Energy(kVarh)"         ,df3.format(current_tou_block[1].getSummation(1))+"");
                res.put("T2 Active Power Max.Demand(kW)"    ,df3.format(current_tou_block[1].getCurrDemand(0))+"");
                res.put("T2 Active Power Max.Demand Time"   ,(String)current_tou_block[1].getEventTime(0));
                res.put("T2 Reactive Power Max.Demand(kVar)",df3.format(current_tou_block[1].getCurrDemand(1))+"");
                res.put("T2 Reactive Power Max.Demand Time" ,(String)current_tou_block[1].getEventTime(1));
                res.put("T2 Active Power Cum.Demand(kW)"    ,df3.format(current_tou_block[1].getCumDemand(0))+"");
                res.put("T2 Reactive Power Cum.Demand(kVar)",df3.format(current_tou_block[1].getCumDemand(1))+"");
                    
                res.put("T3 Active Energy(kWh)"             ,df3.format(current_tou_block[2].getSummation(0))+"");
                res.put("T3 Reactive Energy(kVarh)"         ,df3.format(current_tou_block[2].getSummation(1))+"");
                res.put("T3 Active Power Max.Demand(kW)"    ,df3.format(current_tou_block[2].getCurrDemand(0))+"");
                res.put("T3 Active Power Max.Demand Time"   ,(String)current_tou_block[2].getEventTime(0));
                res.put("T3 Reactive Power Max.Demand(kVar)",df3.format(current_tou_block[2].getCurrDemand(1))+"");
                res.put("T3 Reactive Power Max.Demand Time" ,(String)current_tou_block[2].getEventTime(1));
                res.put("T3 Active Power Cum.Demand(kW)"    ,df3.format(current_tou_block[2].getCumDemand(0))+"");
                res.put("T3 Reactive Power Cum.Demand(kVar)",df3.format(current_tou_block[2].getCumDemand(1))+"");
                res.put("T3 Active Power Cont.Demand(kW)"   ,df3.format(current_tou_block[2].getCoincident(0))+"");
                res.put("T3 Reactive Power Cont.Demand(kVar)" ,df3.format(current_tou_block[2].getCoincident(1))+"");
                    
            }
            
            if(lplist != null && lplist.length > 0){
                res.put("<b>[Load Profile Data(kWh)]</b>", "");
                int nbr_chn = 2; 
                if(kepco_dlms_lp!= null){
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
            
            log.debug("==================AMU KEPCO_DLMS getData End()====================");
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

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }
    
    
	
}