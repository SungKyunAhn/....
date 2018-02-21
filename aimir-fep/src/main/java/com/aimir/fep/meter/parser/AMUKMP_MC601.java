package com.aimir.fep.meter.parser;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.HMData;
import com.aimir.fep.meter.parser.amuKmpMc601Table.KMPMC601_CURRENT;
import com.aimir.fep.meter.parser.amuKmpMc601Table.KMPMC601_DAY;
import com.aimir.fep.meter.parser.amuKmpMc601Table.KMPMC601_EVENT;
import com.aimir.fep.meter.parser.amuKmpMc601Table.KMPMC601_INFO;
import com.aimir.fep.meter.parser.amuKmpMc601Table.KMPMC601_LP;
import com.aimir.fep.meter.parser.amuKmpMc601Table.KMPMC601_MONTH;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * parsing AMU KMP MC601 Meter Data
 * 참고 Source : nuri.aimir.service.hdm.em.parser.NURI_Kamstrup601
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 18. 오전 9:21:38$
 */
public class AMUKMP_MC601 extends MeterDataParser implements java.io.Serializable {

	private static Log log = LogFactory.getLog(AMUKMP_MC601.class);
	
	private static final int 	LEN_TYPE_NAME 		= 1;
	private static final int 	LEN_TYPE_LENGTH 	= 2;
	
	/* implements requisite  member variable */
	private byte[] 				rawData 			= null;
	private Double 				lp 					= null;
	private Double 				lpValue 			= null;
	private String 				meterId 			= null;
	private int 				flag 				= 0;
	
	private KMPMC601_INFO		kmpMc601_info		= null;	// Information Field
    private KMPMC601_CURRENT	kmpMc601_current	= null;	// Current Data Field
    private KMPMC601_LP			kmpMc601_lp			= null;	// Hour Data Field
    private KMPMC601_DAY		kmpMc601_day		= null;	// Day Data Field
    private KMPMC601_MONTH		kmpMc601_month		= null;	// Month Data Field
    private KMPMC601_EVENT		kmpMc601_event		= null;	// Event Data Field
    
    public String 				chUnitName 			= null;
    private String 				sourceAddr			= null;
    
    private int 				Resolution			= 60;
    private int 				LPChannelCount		= 10;
    DecimalFormat 				dformat 			= new DecimalFormat("#0.000000");
    
    
    /**
     * constructor
     */
    public AMUKMP_MC601(){
    }
    
   /**
    * constructor
    * @param sourceAddr
    */
    public AMUKMP_MC601(String sourceAddr){
    	this.sourceAddr = sourceAddr;
    }
    
    /**
	 * constructor
	 * 
	 * @param rawData
	 * @param pulseConst
	 * @throws Exception
	 */
    public AMUKMP_MC601(byte[] rawData , String sourceAddr) throws Exception{
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
     *  parseing Meter Data of KMP MC601 Meter
     *  @param data stream of result command
     */
    public void parse(byte[] data) throws Exception {
    	
    	log.debug("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));	
		int pos = 0;
		int loopCnt = 0;
		
		while(pos < data.length){
			/* ********************************************************************
			 *  Loop는 최대 6회 =>  field가 모두 존재 할 경우 
			 *  (Information , Current , Hour , Day , Month , Event Data )
			 *  무한 루프를 방지하기 위한 Flag --> loopCnt
			 ******************************************************************** */
			if(loopCnt == 6){
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
			if( TypeLen  > LEN_TYPE_NAME + LEN_TYPE_LENGTH){
				
				byte[] iData = new byte[TypeLen-LEN_TYPE_NAME-LEN_TYPE_LENGTH];
				System.arraycopy(data, pos, iData, 0, iData.length);
				pos += iData.length;
				
				log.debug("[iData] len=["+(iData.length)+"] data=>"+Util.getHexString(iData));
				
				if("I".equals(TypeName)){			// Information Field
					kmpMc601_info 		= new KMPMC601_INFO(iData);
					this.meterId 		= kmpMc601_info.getMeterSerial();
				}else if("C".equals(TypeName)){		// Current Data Field
					kmpMc601_current	= new KMPMC601_CURRENT(iData , KMPMC601_CURRENT.TABLE_KIND);
				}else if("H".equals(TypeName)){		// Hour Data Field
					kmpMc601_lp	= 	new KMPMC601_LP(iData ,KMPMC601_LP.TABLE_KIND);
				}else if("D".equals(TypeName)){		// Day Data Field
					kmpMc601_day		= new KMPMC601_DAY(iData , KMPMC601_DAY.TABLE_KIND);
				}else if("M".equals(TypeName)){		// Month Data Field
					kmpMc601_month		= new KMPMC601_MONTH(iData , KMPMC601_MONTH.TABLE_KIND);
				}else if("E".equals(TypeName)){		// Event Data Field
					kmpMc601_event		= new KMPMC601_EVENT(iData);
				}else{
					log.error(" [ AMU KMP MC601 ] parse Type Nmae is invalid " + TypeName);
				}
			// field Length < 3 
			}else{
				log.debug("this Field only exist Type Name and Type Length ");
			}
			
		}// end of while
	}
    
    /**
     * get Sign & Exponent
     * @param byteSiEx
     * @return
     */
    public double getSignEX(byte byteSiEx){
    	double siEx = 0.0;
    	try{
		    double signInt=(byteSiEx & 128)/128;
		    double signExp=(byteSiEx & 64)/64;
		    double exp=((byteSiEx&32) + (byteSiEx&16) + (byteSiEx&8) + (byteSiEx&4) + (byteSiEx&2) + (byteSiEx&1));
		    siEx=Math.pow(-1, signInt)*Math.pow(10, Math.pow(-1, signExp)*exp);//-1^SI*-1^SE*exponent
		    return siEx;
    	}catch(Exception e){
    		log.error("get Sign EX=>",e);
    		return siEx;
    	}
    }
    
    /**
     * get LP Channel Map
     * @return
     */
    public String getLPChannelMap(){
    	
    	if(chUnitName==null && kmpMc601_lp!=null)
    		chUnitName = kmpMc601_lp.getChUnitName();
    	
    	log.debug("chUnitName :"+chUnitName);
    	
    	if(chUnitName!=null && chUnitName.length()>0)
    		return "ch1=HourlyHeat["+chUnitName+"],ch2=CumulativeHeat["+chUnitName+"],ch3=InstantFlow[m3],ch4=CumulativeFlow[m3]," +
        		"ch5=InstantPressure,ch6=m3T1,ch7=m3T2,ch8=SupplyTemperature,ch9=RetrieveTemperature,ch10=TemperatureDifference";
    	else 
    		return "";
    }
    
    /**
     * get Meter Unit
     * @return
     */
    public String getMeteringUnit(){
    	
    	if(this.chUnitName==null && this.kmpMc601_lp!=null)
    		this.chUnitName = kmpMc601_lp.getChUnitName();
    	else 
    		this.chUnitName = "";
    	
    	return chUnitName;
    }
    
    /**
     * get LP Channel Count
     * @return
     */
    public int getLPChannelCount(){
        return LPChannelCount;//ch1- ch10
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
        return this.Resolution;
    }
    
    /**
     * set Resolution
     * @param resolution
     */
    public void setResolution(int resolution){
        this.Resolution = resolution;
    }
    
    /**
     * get MeterStatusLog
     * @return
     */
    public EventLogData[] getMeterStatusLog(){
        return null;
    }
    
	/**
	 * get System Status
	 * @return
	 * @throws Exception
	 */
	public String getSystemStatus()throws Exception {
		
		if(kmpMc601_info != null){
    		try{
    			return kmpMc601_info.getSystemStatus().getLog();
            }catch (Exception e){
            	log.warn("get System Status error",e);
            }
    	}
		return "";
    }
    
    /**
     * get Current Data 
     * @return
     */
    public HMData[] getCurrentData(){
    	
    	if(kmpMc601_current != null){
            return kmpMc601_current.getData();
    	}else{
    		log.debug("kmpMc601_current is null");
    		return null;
    	}
            
    }
    
    /**
     * get Day Data
     * @return
     */
    public HMData[] getDayData(){
    	
    	if(kmpMc601_day != null){
            return kmpMc601_day.getData();
        }
    	else{
    		log.debug("kmpMc601_day is null");
    		return null;
    	}
    }
    
    /**
     * get Month Day
     * @return
     */
    public HMData[] getMonthData(){
    	
    	if(kmpMc601_month != null){
            return kmpMc601_month.getData();
        }
    	else{
    		log.debug("kmpMc601_month is null");
    		return null;
    	}
    }
    
    
    /**
     * get Event Data
     * @return
     */
    public EventLogData[] getEventData(){
    	if(kmpMc601_event != null){
    		return kmpMc601_event.getData();
    	}else{
    		log.debug("kmpMc601_event is null");
    		return null;
    	}
    }
    
    /**
     * get LPHMData
     * @return
     */
    public HMData[] getLPHMData(){
    	
    	HMData[] lp = null;
    	int chCnt =  1;
    	try{
	        
    		if(kmpMc601_lp != null){
	        	
	        	lp =  kmpMc601_lp.getData();
	        	log.debug("kmpMc601_lp getData lenght : " + lp.length);
	        	/* 2010.06.07 delete
	        	if(kmpMc601_day != null){
	        		HMData hDay = kmpMc601_day.getData()[0];
	        		String cummDateTime = (String) hDay.getDate()+"000000";
	        		log.debug("cummDateTime :"+ cummDateTime);
	        		
	        		int idxOf0am = -1;
	        		//find the lp data at 0 a.m
	        		for(int i=0; i<lp.length; i++){
	        			if(((String)lp[i].getDate()+""+(String)lp[i].getTime()).equals(cummDateTime)){
	        				idxOf0am =i;
	        				break;
	        			}
	        		}
	        		log.debug("idx Of 0 am :"+idxOf0am);
	        		
	        		if(idxOf0am < 0){
						return null;
					}
	        		
	        		if(idxOf0am >=0){
	        			
		        		Double[] currentCh = hDay.getCh();
		        		chCnt = currentCh.length;
		        		log.debug("current Cout : " + chCnt);
		        		
		        		double basePulse2 = 0d;
		        		double basePulse1 = currentCh[0].doubleValue();
		        		if(chCnt == 2){
			        		basePulse2 = currentCh[1].doubleValue();
			        		log.debug("basePulse1="+basePulse1);
			        		log.debug("basePulse2="+basePulse2);
		        		}
		        		for(int i=(idxOf0am); i>=0; i--){
		        			
		        			if(((String)lp[i].getDate()+""+(String)lp[i].getTime()).equals(TimeUtil.getPreHour(cummDateTime, (i-idxOf0am)))){
		        				Double[] lpData = lp[i].getCh();
		        				lp[i].setCh(2, new Double(dformat.format(basePulse1)));
								basePulse1 += lpData[0].doubleValue();
		        				if(chCnt == 2){
			        				lp[i].setCh(4, new Double(dformat.format(basePulse2)));
									basePulse2 += lpData[2].doubleValue();
		        				}
		        			}
		        		}		        		

		        		basePulse1 = currentCh[0].doubleValue();
		        		if(chCnt == 2){
			        		basePulse2 = currentCh[1].doubleValue();
		        		}
		        		log.debug("lp.length : "+ lp.length);
		        		
		        		for(int i=idxOf0am+1; i<lp.length; i++){
		        			if(((String)lp[i].getDate()+""+(String)lp[i].getTime()).equals(TimeUtil.getPreHour(cummDateTime, (i-idxOf0am)))){
		        				Double[] lpData = lp[i].getCh();
		        				lp[i].setCh(2, new Double(dformat.format(basePulse1)));
								basePulse1 -= lpData[0].doubleValue();
		        				if(chCnt == 2){
			        				lp[i].setCh(4, new Double(dformat.format(basePulse2)));
									basePulse2 -= lpData[2].doubleValue();
		        				}
		        			}
		        		}
	        		}
	        	}
    			*/
	        	
	        	//temporary souce
	        	if(chCnt == 1 && kmpMc601_current != null){		
	        		HMData curr = kmpMc601_current.getData()[0];
	        		String currDateTime = (String) curr.getDate()+curr.getTime().substring(0,2)+"0000";
	        		log.debug("currDateTime :"+ currDateTime);
	        		
	        		int idxOf0am =-1;
	        		//find the lp data at 0 a.m
	        		for(int i=0; i<lp.length; i++){
	        			if(((String)lp[i].getDate()+""+(String)lp[i].getTime()).equals(currDateTime)){
	        				idxOf0am =i;
	        				break;
	        			}
	        		}
	        		log.debug("idx Of 0am :"+idxOf0am);
	        		
	        		if(idxOf0am >=0){
	        			
		        		Double[] currentCh = curr.getCh();
		        		double nowCurrent = currentCh[3].doubleValue();
		        		log.debug("nowCurrent : " + nowCurrent );
		        		for(int i=(idxOf0am); i>=0; i--){
		        			
		        			if(((String)lp[i].getDate()+""+(String)lp[i].getTime()).equals(TimeUtil.getPreHour(currDateTime, (i-idxOf0am)))){
		        				Double[] lpData = lp[i].getCh();
		        				lp[i].setCh(4, new Double(dformat.format(nowCurrent)));
		        				nowCurrent += lpData[2].doubleValue();
		        			}
		        		} 

		        		nowCurrent = currentCh[3].doubleValue();

		        		for(int i=idxOf0am+1; i< lp.length; i++){
		        			if(((String)lp[i].getDate()+""+(String)lp[i].getTime()).equals(TimeUtil.getPreHour(currDateTime, (i-idxOf0am)))){
		        				Double[] lpData = lp[i].getCh();
		        				nowCurrent -= lpData[2].doubleValue();
		        				lp[i].setCh(4, new Double(dformat.format(nowCurrent)));
		        			}
		        		} 
	        		}// end of if (idxOf0am >=0)
	        	} // end of if => (temporary souce) 
	        }// end of if => if(kmpMc601_lp != null) 
    		log.debug("getLPHMData length " + lp.length);
	        return lp;
    	}catch(Exception e){
    		log.warn("AMU KMP MC601 GET LP HMData Hourly Data Error=>",e);
    		return null;
    	}        
    }
    
    /**
     * get MDM Data
     * @return
     */
    public HashMap<String, String> getMdmData(){
        
        HashMap<String, String> map = null;
        
        try{
            if(kmpMc601_info != null){
                map = new HashMap<String, String>();
                map.put("interfaceCode", CommonConstants.Interface.AMU.name());
                map.put("mcuType", ""+CommonConstants.ModemType.IEIU.name());
                
                if(this.kmpMc601_info.getSwVer().startsWith("NG")){
                    map.put("protocolType","2");
                }else{
                    map.put("protocolType","1");
                }
                map.put("sysPhoneNumber", sourceAddr);
                map.put("id", sourceAddr);
                map.put("networkStatus", "1");
                map.put("csq", "");
                map.put("currentTime", kmpMc601_info.getMeterCurrentTime().getCurrnetTime());
                map.put("modemStatus",kmpMc601_info.getSystemStatus().getLog());
                
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
            eventlogdata = getEventData();
            
			res.put("<b>[Meter Configuration Data]</b>", "");
            if(kmpMc601_info != null){
                res.put("Meter ID", kmpMc601_info.getMeterSerial());
            	res.put("Current Meter Time",kmpMc601_info.getMeterCurrentTime().getCurrnetTime());
                res.put("Current Program Time",kmpMc601_info.getMeterCurrentTime().getCurrnetTime());
                res.put("MeterStatus",kmpMc601_info.getSystemStatus().getLog());
            }  
            
            DecimalFormat decimalf=null;
            SimpleDateFormat datef14=null;            
            if(lp !=null && lp.length>0){
            	res.put("<b>[LP - Hourly Data]</b>", "");
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
			
			if(month !=null && month.length>0){
				res.put("<b>[LP - Monthly Data]</b>", "");
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
			
			if(day !=null && day.length>0){
				res.put("<b>[Day Data]</b>", "");
            	for(int i = 0; i < day.length; i++){
	            	String datetime = day[i].getDate();
	            	String val = "";
	            	Double[] ch = day[i].getCh();
	                for(int k = 0; k < ch.length ; k++){
	                    val += "ch"+(k+1)+"="+df3.format(ch[k])+"  ";
	                }
	                res.put("Day"+datetime, val);
            	}
			}
			
			if(current !=null && current.length>0){
				res.put("<b>[Current Data]</b>", "");
            	for(int i = 0; i < current.length; i++){
	            	String datetime = current[i].getDate()+""+current[i].getTime();
	            	this.meterTime = datetime;
	            	String val = "";
	            	Double[] ch = current[i].getCh();
	                for(int k = 0; k < ch.length ; k++){
	                    val += "ch"+(k+1)+"="+df3.format(ch[k])+"  ";
	                }
	                res.put("Current"+datetime, val);
            	}
			}
			if(eventlogdata !=null && eventlogdata.length>0){
				res.put("<b>[EVENT LOG]</b>", "");
				for(int i = 0; i < eventlogdata.length; i++)
	            {
		            res.put(""+eventlogdata[i].getDate()+""+eventlogdata[i].getTime(), ""+eventlogdata[i].getMsg());
	            }
			}
        }
        catch (Exception e){
            log.error("AMU KMP MC601 Get Data Error=>");
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


