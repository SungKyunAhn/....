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
import com.aimir.fep.meter.parser.amuKepco_2_5_0Table.KEPCO_2_5_0_CB;
import com.aimir.fep.meter.parser.amuKepco_2_5_0Table.KEPCO_2_5_0_ERROR;
import com.aimir.fep.meter.parser.amuKepco_2_5_0Table.KEPCO_2_5_0_INFO;
import com.aimir.fep.meter.parser.amuKepco_2_5_0Table.KEPCO_2_5_0_LP;
import com.aimir.fep.meter.parser.amuKepco_2_5_0Table.KEPCO_2_5_0_PB;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing AMU KECPO_2_5_0 Metering Data
 * 명칭: 한전 구 프로토콜 v2.5.0
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 8. 오후 2:42:23$
 */
public class AMUKEPCO_2_5_0 extends MeterDataParser implements java.io.Serializable{
	
	private static Log log = LogFactory.getLog(AMUKEPCO_2_5_0.class);
	
	private static final int LEN_TYPE_NAME 			= 1;
	private static final int LEN_TYPE_LENGTH 		= 2;
	
	/* implements requisite  member variable */
	private byte[] 			rawData 				= null;
	private Double 			lp 						= null;
	private Double 			lpValue 				= null;
	private String 			meterId 				= null;
	private int 			flag 					= 0;
	
	KEPCO_2_5_0_INFO		kepco_2_5_0_info		= null;	// Information Field
	KEPCO_2_5_0_PB			kepco_2_5_0_pb			= null;	// Previous Field
	KEPCO_2_5_0_CB			kepco_2_5_0_cb			= null;	// Current Field
	KEPCO_2_5_0_LP			kepco_2_5_0_lp			= null;	// LP DATA Field
	KEPCO_2_5_0_ERROR		kepco_2_5_0_error		= null;	// Error Field
	
	private int 			regK 					= 1;
	private double 			ke 						= 1;
	private int 			LPChannelCount			= 9;
	
	private String 			sourceAddr				= null;
	
	/**
     * constructor
     */
	public AMUKEPCO_2_5_0(){
	}
	
	/**
	 * constructor
	 * @param mcuId
	 */
	public AMUKEPCO_2_5_0(String sourceAddr){
		this.sourceAddr = sourceAddr;
	}
	
	/**
	 * constructor
	 * 
	 * @param rawData
	 * @param pulseConst
	 * @throws Exception
	 */
    public AMUKEPCO_2_5_0(byte[] rawData , String sourceAddr) throws Exception{
        this.rawData = rawData;
        this.sourceAddr = sourceAddr;
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
			if(kepco_2_5_0_lp !=null){
				lpCount = kepco_2_5_0_lp.getLpCount();
			}
		}catch(Exception e){
			log.debug("get LP Count Failed!");
		}
		
		return lpCount;
    }
	
	/**
     * parseing Meter Data of KEPCO v2.5.0
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
			log.debug("TypeName(Hex) : " + Util.getHexString(DataFormat.select(data, pos, LEN_TYPE_NAME)));
			String TypeName	= new String(DataFormat.select(data, pos, LEN_TYPE_NAME));
			pos += LEN_TYPE_NAME;
			// TYPE LENGTH
			log.debug("TypeLength(Hex) : " + Util.getHexString(DataFormat.select(data, pos, LEN_TYPE_LENGTH)));
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
					kepco_2_5_0_info 	= new KEPCO_2_5_0_INFO(iData ,ke);
					this.meterId		= kepco_2_5_0_info.getMeterSerial();
				}else if("P".equals(TypeName)){
					String resetTime = kepco_2_5_0_info.getMeterDemandResetTime();
					int resetCount	 = kepco_2_5_0_info.getCountOfManualRecovery();
					log.debug("#### resetTime : " + resetTime);
					log.debug("#### resetCount : " + resetCount);
					kepco_2_5_0_pb		= new KEPCO_2_5_0_PB(iData , resetTime ,resetCount);
				}else if("C".equals(TypeName)){
					kepco_2_5_0_cb		= new KEPCO_2_5_0_CB(iData);
				}else if("L".equals(TypeName)){
					kepco_2_5_0_lp		= new KEPCO_2_5_0_LP(iData, regK , ke);
				}else if("E".equals(TypeName)){
					kepco_2_5_0_error	= new KEPCO_2_5_0_ERROR(iData);
				}else{
					log.error(" [AMU KEPCO v2.5.0] parse Type Nmae is invalid " + TypeName);
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
        res+="ch2=Lag Reactive Energy[kVarh],v2=Lag Reactive Power[kVar],";
        res+="ch3=Lead Reactive Energy[kVarh],v3=Lead Reactive Power[kVar],";
        res+="ch4=Phase Energy[kVah],v4=Phase Power[kVA],";
        res+="pf=PF";
        
        return res;
        
    }
	
	 /**
     * get LP Channel Count
     * @return
     */
    public int getLPChannelCount(){
        return LPChannelCount;//ch1- ch4
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
            if(kepco_2_5_0_lp!= null){
                resol = kepco_2_5_0_lp.getINTERVAL_TIME();
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
    	
        try{
            if(kepco_2_5_0_info != null){
                return kepco_2_5_0_info.getMeterStatus().getLog();
            }else{
                return "";
            }
        }catch(Exception e){
            log.debug("get Meter Status Error" ,e);
        }
        return "";
    }
	
	
	/**
	 * get System Status
	 * @return
	 * @throws Exception
	 */
	public String getSystemStatus()throws Exception {
		
		try{
			if(kepco_2_5_0_error != null){
				return kepco_2_5_0_error.getSystemStatus().getLog();
			}else{
                return "";
            }
        }catch (Exception e){
            log.warn("get System Status error",e);
    	}
		return "";
    }
	
    /**
     * get LP Data
     * @return
     */
    public LPData[] getLPData(){
        
        try{
            if(kepco_2_5_0_lp != null){
                return kepco_2_5_0_lp.parse();
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
    	
    	if(kepco_2_5_0_pb != null){
            try{
                TOU_BLOCK[] blk = kepco_2_5_0_pb.getTou_block();
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
    	
    	if(kepco_2_5_0_cb != null){
            try{
                TOU_BLOCK[] blk = kepco_2_5_0_cb.getTou_block();
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
            if(kepco_2_5_0_info != null){
                map = new HashMap<String, String>();
                map.put("interfaceCode", CommonConstants.Interface.AMU.name());
                map.put("mcuType", "" + CommonConstants.ModemType.IEIU.name());
                
                if(this.kepco_2_5_0_info.getSwVer().startsWith("NG")){
                    map.put("protocolType","2");
                }else{
                    map.put("protocolType","1");
                }

                map.put("sysPhoneNumber", sourceAddr);
                map.put("id", sourceAddr);
                map.put("networkStatus", "1");
                map.put("csq", "");
                map.put("currentTime" , kepco_2_5_0_info.getMeterCurrentTime().getCurrnetTime());
                map.put("modemStatus", kepco_2_5_0_info.getMeterStatus().getLog());
               
            }
        }catch(Exception e){
            log.debug("get MDM Data failed");
        }
        return map;
    }
	
    /**
     * get Data
     */
	@Override
	public LinkedHashMap getData() {
		
		LinkedHashMap<String, Serializable> res = new LinkedHashMap(16,0.75f,false);
        TOU_BLOCK[] previous_tou_block = null;
		TOU_BLOCK[] current_tou_block = null;
        LPData[] lplist = null;
        
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
        
        try
        {
            log.debug("==================AMU KEPCO_2_5_0 getData start()====================");
            previous_tou_block = getPrevBilling();
            current_tou_block = getCurrBilling();
            lplist = getLPData();
            
			res.put("<b>[Meter Configuration Data]</b>", "");
            if(kepco_2_5_0_info != null){          
                //res.put("ProgramNameVer",kepco_2_5_0_info.getSwVer());
                res.put("CT_PT",kepco_2_5_0_info.getMeterCtPt()+"");
                res.put("REG_K",kepco_2_5_0_info.getMeterRegK()+"");
                res.put("Billing Day",kepco_2_5_0_info.getMeterBillingDay());
                res.put("Meter ID", kepco_2_5_0_info.getMeterSerial());
                res.put("Current Meter Time",kepco_2_5_0_info.getMeterCurrentTime().getCurrnetTime());
                res.put("Current Program Time",kepco_2_5_0_info.getMeterCurrentTime().getCurrnetTime());
                res.put("MeterStatus",kepco_2_5_0_info.getMeterStatus().getLog());
            }

            if(current_tou_block != null){
                res.put("<b>[Current Billing Data]</b>", "");
                res.put("Total Active Energy(kWh)"					,df3.format(current_tou_block[0].getSummation(0))+"");
                res.put("Total Reactive Energy(kVarh)"				,df3.format(current_tou_block[0].getSummation(1))+"");
                res.put("Total Active Power Max.Demand(kW)"			,df3.format(current_tou_block[0].getCurrDemand(0))+"");
                res.put("Total Active Power Max.Demand Time"		,(String)current_tou_block[0].getEventTime(0));
                res.put("Total Reactive Power Max.Demand(kVar)"		,df3.format(current_tou_block[0].getCurrDemand(1))+"");
                res.put("Total Reactive Power Max.Demand Time"		,(String)current_tou_block[0].getEventTime(1));
                res.put("Total Active Power Cum.Demand(kW)"			,df3.format(current_tou_block[0].getCumDemand(0))+"");
                res.put("Total Reactive Power Cum.Demand(kVar)"		,df3.format(current_tou_block[0].getCumDemand(1))+"");
                    
                lpValue = new Double(df3.format(current_tou_block[1].getSummation(0)));
                res.put("Rate A Active Energy(kWh)"					,df3.format(current_tou_block[1].getSummation(0))+"");                
                res.put("Rate A Reactive Energy(kVarh)"				,df3.format(current_tou_block[1].getSummation(1))+"");
                res.put("Rate A Active Power Max.Demand(kW)"		,df3.format(current_tou_block[1].getCurrDemand(0))+"");
                res.put("Rate A Active Power Max.Demand Time"		,(String)current_tou_block[1].getEventTime(0));
                res.put("Rate A Reactive Power Max.Demand(kVar)"	,df3.format(current_tou_block[1].getCurrDemand(1))+"");
                res.put("Rate A Reactive Power Max.Demand Time"		,(String)current_tou_block[1].getEventTime(1));
                res.put("Rate A Active Power Cum.Demand(kW)"		,df3.format(current_tou_block[1].getCumDemand(0))+"");
                res.put("Rate A Reactive Power Cum.Demand(kVar)"	,df3.format(current_tou_block[1].getCumDemand(1))+"");
                    
                res.put("Rate B Active Energy(kWh)"					,df3.format(current_tou_block[2].getSummation(0))+"");
                res.put("Rate B Reactive Energy(kVarh)"				,df3.format(current_tou_block[2].getSummation(1))+"");
                res.put("Rate B Active Power Max.Demand(kW)"		,df3.format(current_tou_block[2].getCurrDemand(0))+"");
                res.put("Rate B Active Power Max.Demand Time"		,(String)current_tou_block[2].getEventTime(0));
                res.put("Rate B Reactive Power Max.Demand(kVar)"	,df3.format(current_tou_block[2].getCurrDemand(1))+"");
                res.put("Rate B Reactive Power Max.Demand Time"		,(String)current_tou_block[2].getEventTime(1));
                res.put("Rate B Active Power Cum.Demand(kW)"		,df3.format(current_tou_block[2].getCumDemand(0))+"");
                res.put("Rate B Reactive Power Cum.Demand(kVar)"	,df3.format(current_tou_block[2].getCumDemand(1))+"");
                res.put("Rate B Active Power Cont.Demand(kW)"		,df3.format(current_tou_block[2].getCoincident(0))+"");
                res.put("Rate B Reactive Power Cont.Demand(kVar)"	,df3.format(current_tou_block[2].getCoincident(1))+"");
                    
                res.put("Rate C Active Energy(kWh)"					,df3.format(current_tou_block[3].getSummation(0))+"");
                res.put("Rate C Reactive Energy(kVarh)"				,df3.format(current_tou_block[3].getSummation(1))+"");
                res.put("Rate C Active Power Max.Demand(kW)"		,df3.format(current_tou_block[3].getCurrDemand(0))+"");
                res.put("Rate C Active Power Max.Demand Time"		,(String)current_tou_block[3].getEventTime(0));
                res.put("Rate C Reactive Power Max.Demand(kVar)"	,df3.format(current_tou_block[3].getCurrDemand(1))+"");
                res.put("Rate C Reactive Power Max.Demand Time"		,(String)current_tou_block[3].getEventTime(1));
                res.put("Rate C Active Power Cum.Demand(kW)"		,df3.format(current_tou_block[3].getCumDemand(0))+"");
                res.put("Rate C Reactive Power Cum.Demand(kVar)"	,df3.format(current_tou_block[3].getCumDemand(1))+"");
                
                res.put("Rate D Active Energy(kWh)"					,df3.format(current_tou_block[4].getSummation(0))+"");
                res.put("Rate D Reactive Energy(kVarh)"				,df3.format(current_tou_block[4].getSummation(1))+"");
                res.put("Rate D Active Power Max.Demand(kW)"		,df3.format(current_tou_block[4].getCurrDemand(0))+"");
                res.put("Rate D Active Power Max.Demand Time"		,(String)current_tou_block[4].getEventTime(0));
                res.put("Rate D Reactive Power Max.Demand(kVar)"	,df3.format(current_tou_block[4].getCurrDemand(1))+"");
                res.put("Rate D Reactive Power Max.Demand Time"		,(String)current_tou_block[4].getEventTime(1));
                res.put("Rate D Active Power Cum.Demand(kW)"		,df3.format(current_tou_block[4].getCumDemand(0))+"");
                res.put("Rate D Reactive Power Cum.Demand(kVar)"	,df3.format(current_tou_block[4].getCumDemand(1))+"");

            }
            
            if(lplist != null && lplist.length > 0){
                res.put("<b>[Load Profile Data(kWh)]</b>", "");
                int nbr_chn = 4; 
                if(kepco_2_5_0_lp!= null){
                	//TODO FIX HARD CODE
                    nbr_chn = 4;
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
            
            log.debug("==================AMU KEPCO_2_5_0 getData End()====================");
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


