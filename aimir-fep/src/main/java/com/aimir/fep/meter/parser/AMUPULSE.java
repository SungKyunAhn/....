package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.meter.parser.amuPulseTable.PULSE_INFO;
import com.aimir.fep.meter.parser.amuPulseTable.PULSE_LP;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Util;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing AMU Pulse Interface Meter Data
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 3. 오후 1:35:18$
 */
public class AMUPULSE extends MeterDataParser implements ModemParser , java.io.Serializable{

	private static Log log 	= LogFactory.getLog(AMUPULSE.class);

	private static final int 	LEN_TYPE_NAME 		= 1;
	private static final int 	LEN_TYPE_LENGTH 	= 2;
	
	/* implements requisite  member variable */
	private byte[] 				rawData 			= null;
	private Double 				lp 					= null;
	private Double 				lpValue 			= null;
	private String 				meterId 			= null;
	private int 				flag 				= 0;
	// pulseLp.getPeriod() 메소드를 사용해서 가져올수 있으나 편의상 멤버변수로 선언
	private int 				period 				= 0;
	private ModemLPData[] 		lpData 				= null;
	
	PULSE_INFO 					pulse_info			= null;
	PULSE_LP 					pulse_lp			= null;
	
	private String 				mcuId				= null;
	
	/**
	 * constructor
	 */
	public AMUPULSE(){
	}
	
	/**
	 * constructor
	 * @param mcuId
	 */
	public AMUPULSE(String mcuId){
		this.mcuId = mcuId;
	}
	/**
	 * constructor
	 * 
	 * @param rawData
	 * @param pulseConst
	 * @throws Exception
	 */
    public AMUPULSE(byte[] rawData) throws Exception{
        this.rawData = rawData;
        
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
    public byte[] getRawData()
    {
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
	
	/* ****************   implements SensorParser   ******************/
	/**
	 * get LP DATA
	 * @return lpData
	 */
	public ModemLPData[] getLpData() {
		
		return lpData;
	}

	/**
	 * get Period
	 * @return period
	 */
	public int getPeriod() {
		
		return period;
	}
	
	/**
	 * get LP Count
	 * @return
	 */
	public int getLPCount(){
    	
		int lpCount = 0;
		try{
			if(pulse_lp !=null){
				lpCount = pulse_lp.getLpDataCnt();
			}
		}catch(Exception e){
			log.debug("get LP Count Failed!");
		}
		
		return lpCount;
    }
	
	/* *********************************************************************** */
	
	/**
     * parse meter mesurement data
     * @param data 
     * @param pulseConst
     */
	public void parse(byte[] data) throws Exception {
		
		log.debug("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));	
		int pos = 0;
		int loopCnt = 0;
		while(pos < data.length){
			/* ********************************************************************
			 *  Loop는 최대 3회 =>  field가 모두 존재 할 경우 (Information , LP DATA )
			 *  무한 루프를 방지하기 위한 Flag --> loopCnt
			 ******************************************************************** */
			if(loopCnt == 2){
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
					pulse_info 	= new PULSE_INFO(iData);
					this.meterId = pulse_info.getMeterSerial();
				}else if("L".equals(TypeName)){
					pulse_lp	 = new PULSE_LP(iData);					
				}else{
					log.error(" [AMU Pulse Interface ] parse Type Nmae is invalid " + TypeName);
				}
			// field Length < 3 
			}else{
				log.debug("this Field only exist Type Name and Type Length ");
				//pos += LEN_TYPE_NAME + LEN_TYPE_LENGTH;
			}
			
			/* **********************  get ModemLPData[]  *********************** */
			lpData = pulse_lp.getLpData();
			
			this.lp = pulse_info.getCurrent_Pulse();
	        this.lpValue = new Double(this.lp.doubleValue() / meter.getPulseConstant());
	        this.period = pulse_lp.getPeriod();
	        log.debug("LP [" + lp + "]");
	        log.debug("LPVALUE[" + lpValue + "]");
			
		}// end of while
		
	}
	
	/**
	 * get MDM Data
	 * @return
	 */
	public HashMap<String, String> getMdmData(){
        
        HashMap<String, String> map = null;
        
        try{
            if(pulse_info != null){
                map = new HashMap<String, String>();
                map.put("interfaceCode", CommonConstants.Interface.AMU.name());
                map.put("mcuType", "" + CommonConstants.ModemType.IEIU.name());
                
                if(this.pulse_info.getSW_VER().startsWith("NG")){
                    map.put("protocolType","2");
                }else{
                    map.put("protocolType","1");
                }

                map.put("sysPhoneNumber", mcuId.substring(1));
                map.put("id", mcuId);
                map.put("networkStatus", "1");
                map.put("csq", "");
                map.put("currentTime" , pulse_info.getCurrentTime().getTimeStamp());
                map.put("modemStatus", pulse_lp.getLpStatusDesc());
             
            }
        }catch(Exception e){
            log.debug("get MDM Data failed");
        }
        return map;
    }
	
	/* implements EnergyMeterDataParser */
	@Override
    public LinkedHashMap getData()
    {
		LinkedHashMap res = new LinkedHashMap(16,0.75f,false);
		DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
		
		try{
			
			log.debug("==================AMU PULSE Interface getData start()====================");
			
			if(pulse_info !=null){
				res.put("Meter ID"		, pulse_info.getMeterSerial());
				res.put("Metering Time"	, pulse_info.getCurrentTime().getTimeStamp());
			}
			
			if(pulse_lp != null){
				
				res.put("Period"		, ""+ period);
		        res.put("LP"			, ""+"<span style='margin-right: 40px;'>" +df3.format(lp)+"</span>");
		        res.put("LP Value"		, ""+ df3.format(lpValue));
			
		        for (int i = 0 ; i < lpData.length; i++) {
		            
		            res.put("BasePulse Date:" + i, lpData[i].getLpDate());
		            res.put("BasePulse:" + i, df3.format(lpData[i].getBasePulse()));
		            
		            for (int j = 0; j < lpData[i].getLp().length; j++) {
		            	String recTime = Util.addMinYymmdd(lpData[i].getLpDate(), (period *i));
		            	/*
		            	 * 기존 adion5530 에서는 년월일  시간분 형식으로 KEY값을 저장  
		            	 * day과 hour 사이에 " " 존재 꼭 공백이 존재해야되는것인지는 추후 확인할것 
		            	*/
		                res.put( recTime , df3.format(lpData[i].getLp()[j]));
		            }
		        }
			}
		}catch(Exception e){
			log.error("AMU Pulse getData Failed : " , e);
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


