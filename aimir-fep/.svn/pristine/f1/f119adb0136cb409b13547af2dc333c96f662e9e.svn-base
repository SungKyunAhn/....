package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.fep.command.conf.DLMSMeta.CONTROL_STATE;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeTable;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.CURRENT_ENERGY_PROFILE;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.EVENT;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.MEASUREMENT_DATE_INFO;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.METER_INFORMATION;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.MONTHLY_DEMAND_PROFILE;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.MONTHLY_ENERGY_PROFILE;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.OBIS;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.POWER_QUALITY_AVG_PROFILE;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.POWER_QUALITY_PROFILE;
import com.aimir.fep.meter.parser.DLMSGtypeOmniTable.DLMSGtypeVARIABLE.TDU_PROFILE;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE;
import com.aimir.fep.meter.parser.DLMSKepcoTable.LPComparator;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

public class DLMSGtypeOmni extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 5198091223934578776L;

	private static Log log = LogFactory.getLog(DLMSGtypeOmni.class);

	private LPData[] lpData = null;	
	private BillingData billingMonthData = null;
	private BillingData currentBillingMonthData = null;
	private BillingData billingDayData = null;	
	private Instrument  powerQualityData = null;
	private List<Instrument>  powerQualityAvgData = null;
	List<EventLogData> eventLogList = null;
	
	LinkedHashMap<String, Map<String, Object>> result = 
            new LinkedHashMap<String, Map<String, Object>>();

	String meterID = "";
	String manufacturerID = "";
	String mesurementDate = "";
	String mesurementDay = "";
	
	String cosemID = ""; // 계기식별, 코유코드
	String fwVersion = ""; // 통신규격버전
	String manufactureDate = ""; // 제조년월일
	
    String modemID = "";
    
    String meterModel = "";
    String logicalNumber = "";
    String manufactureSerial = "";
    String servicePointSerial = "";
    String meterVendor = "";
    Long ct_ratio = 0L;
    Long vt_ratio = 0L;
    Long ct_den = 0L;
    Long vt_den = 0L;
    Long trans_num = 0L;
    byte[] phaseType = null;
    Long	 meterStatus = 0L;    
    CONTROL_STATE	loadCtrlState;
    int loadCtrlMode;
    Double		limiterInfo = 0d;
    Double		limiterInfoMin = 0d;
    int modemPort = 0;
    
    int meterActiveConstant = 1;
    int meterReActiveConstant = 1;
    int meterApprentConstant = 1;
    
    int meterRelayStatus = 0;

    double activePulseConstant = 1;
    double reactivePulseConstant = 1;
    double apparentPulseConstant = 1;
    
    //Long ctRatio = 1L;
    
    int lpInterval=15;
    
    Double meteringValue= null;
    Double ct = 1d;
    
    String maxdemandTime = null;
    Double maxdemand = 0.0;
    
    
    // beforeTime
    String beforeTime = "";
    // aftertime
    String afterTime = "";
    
    @Override
    public LinkedHashMap<String, Object> getData() {
    	
    	Map<String, Object> data = new LinkedHashMap<String, Object>(16, 0.75f, false);
    	Map<String, Object> resultSubData = null;
        String key = null;
        
        DecimalFormat decimalf=null;
        SimpleDateFormat datef14=null;
        
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
        try {
	        for (Iterator i = result.keySet().iterator(); i.hasNext();) {
	        	key = (String)i.next();
	            resultSubData = result.get(key);
	            if (key.equals(OBIS.METER_TIME.getCode())) {
	            	data.put(OBIS.METER_TIME.name(), DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)resultSubData.get("Meter Time")));
	            } else if (key.equals(OBIS.ACTIVEPOWER_CONSTANT.getCode())) {
	                	data.put(OBIS.ACTIVEPOWER_CONSTANT.name(), ((Number) resultSubData.get("ActivePower Constant")).doubleValue());	
	            } else if (key.equals(OBIS.REACTIVEPOWER_CONSTANT.getCode())) {
	            	data.put(OBIS.REACTIVEPOWER_CONSTANT.name(), ((Number) resultSubData.get("ReactivePower Constant")).doubleValue());	
	            } else if (key.equals(OBIS.APPRENTPOWER_CONSTANT.getCode())) {
	            	data.put(OBIS.APPRENTPOWER_CONSTANT.name(), ((Number) resultSubData.get("ApparentPower Constant")).doubleValue());		
	            } else if (key.equals(OBIS.LP_CYCLE.getCode())) {
	            	data.put(OBIS.LP_CYCLE.name(), (int)resultSubData.get("Lp Cycle"));
	            } else if (key.equals(OBIS.METER_RELAY_STATUS.getCode())) {
	            	data.put(OBIS.METER_RELAY_STATUS.name(), (int)resultSubData.get("METER RELAY STAUTS"));
	            } else if (key.equals(OBIS.METER_RELAY_STATUS.getCode())) {
	            	data.put(OBIS.METER_RELAY_STATUS.name(), (int)resultSubData.get("METER RELAY STAUTS"));		
	            } else {            	
	                
	            	if (resultSubData != null) {                	
	                	String idx = "";
	                    if (key.lastIndexOf("-") != -1) {
	                        idx = key.substring(key.lastIndexOf("-")+1);
	                        key = key.substring(0, key.lastIndexOf("-"));
	                    }
	                    else if (key.lastIndexOf(".") != -1) {
	                        idx = key.substring(key.lastIndexOf(".")+1);
	                        key = key.substring(0, key.lastIndexOf("."));
	                    }
	                    String subkey = null;
	                    Object subvalue = null;
	                    
	                    for (Iterator subi = resultSubData.keySet().iterator(); subi.hasNext();) {
	                        subkey = (String)subi.next();
	                        if (!subkey.contains(DLMSGtypeVARIABLE.UNDEFINED) && !subkey.contains("Structure") && !subkey.contains("Customer")) {                        	
	                            subvalue = resultSubData.get(subkey);
	                            if (subvalue instanceof String) {

	                            	if (subkey.contains("Date") && !subkey.contains("Measurement")) {
	                            		data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, subvalue);
	                            	} else {
	                            		data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, subvalue);
	                            	}
	                            }
	                            else if (subvalue instanceof Number) {
	                            	if (subkey.contains("Active") || subkey.contains("Apparent") || subkey.contains("Reactive")) {                            		
	                            		data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, ((Number) subvalue).doubleValue() / activePulseConstant);
	                            	} else {
	                            		data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, subvalue);
	                            	}
	                            }
	                            else data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, subvalue);
	                        }
	                    } // end for
	                    
	                } // end if
	            }  
	        }
        
        } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return (LinkedHashMap)data;
    }

    @Override
    public void parse(byte[] data) throws Exception {
    	
    	log.debug("DLMS parse:"+Hex.decode(data));

        String obisCode = "";
        int clazz = 0;
        int attr = 0;

        int pos = 0;
        int len = 0;
        // DLMS Header OBIS(6), CLASS(1), ATTR(1), LENGTH(2)
        // DLMS Tag Tag(1), DATA or LEN/DATA (*)
        byte[] OBIS = new byte[6];
        byte[] CLAZZ = new byte[2];
        byte[] ATTR = new byte[1];
        byte[] LEN = new byte[2];
        byte[] TAGDATA = null;
        
        DLMSGtypeTable dlms = null;
        while (pos < data.length) {
        	
        	dlms = new DLMSGtypeTable();
            System.arraycopy(data, pos, OBIS, 0, OBIS.length);
            pos += OBIS.length;
            obisCode = Hex.decode(OBIS);
            log.debug("OBIS[" + obisCode + "]");
            dlms.setObis(obisCode);
            
            System.arraycopy(data, pos, CLAZZ, 0, CLAZZ.length);
            pos += CLAZZ.length;
            clazz = DataUtil.getIntTo2Byte(CLAZZ);
            log.debug("CLASS[" + clazz + "]");
            dlms.setClazz(clazz);
            
            if (dlms.getDlmsHeader().getClazz() == null) break;
            
            System.arraycopy(data, pos, ATTR, 0, ATTR.length);
            pos += ATTR.length;
            attr = DataUtil.getIntToBytes(ATTR);
            log.debug("ATTR[" + attr + "]");
            dlms.setAttr(attr);

            System.arraycopy(data, pos, LEN, 0, LEN.length);
            pos += LEN.length;
            len = DataUtil.getIntTo2Byte(LEN);
            log.debug("LENGTH[" + len + "]");
            dlms.setLength(len);

            if (len == 0) continue;

            TAGDATA = new byte[len];
            if (pos + TAGDATA.length <= data.length) {
            	System.arraycopy(data, pos, TAGDATA, 0, TAGDATA.length);
            	pos += TAGDATA.length;
            }
            else {
            	System.arraycopy(data, pos, TAGDATA, 0, data.length-pos);
            	pos += data.length-pos;
            }
            
            log.debug("TAGDATA=["+Hex.decode(TAGDATA)+"]");  
            
            dlms.parseDlmsTag(TAGDATA);
            
            // 동일한 obis 코드를 가진 값이 있을 수 있기 때문에 검사해서 _number를 붙인다.
            // 확인 필요!!
            if (dlms.getDlmsHeader().getObis() == DLMSGtypeVARIABLE.OBIS.LOAD_PROFILE) { // LP     	
                for (int cnt = 0; ;cnt++) {
                    obisCode = dlms.getDlmsHeader().getObis().getCode() + "-" + cnt;
                    if (!result.containsKey(obisCode)) {                    	
                        result.put(obisCode, dlms.getData());
                        break;
                    }
                }
            } else if (dlms.getDlmsHeader().getObis() == DLMSGtypeVARIABLE.OBIS.AVG_CURRENT_VOLTAGE) {  // 평균 전압/전류
            	for (int cnt = 0; ;cnt++) {
                    obisCode = dlms.getDlmsHeader().getObis().getCode() + "-" + cnt;
                    if (!result.containsKey(obisCode)) {                    	
                        result.put(obisCode, dlms.getData());
                        break;
                    }
                }	
            } else if (dlms.getDlmsHeader().getObis() == DLMSGtypeVARIABLE.OBIS.METER_TIME) { // METER_AFTERTIME     	
                for (int cnt = 0; ;cnt++) {
                    obisCode = dlms.getDlmsHeader().getObis().getCode() + "-" + cnt;
                    if (!result.containsKey(obisCode)) {                    	
                        result.put(obisCode, dlms.getData());
                        break;
                    }
                }
            }
            else result.put(obisCode, dlms.getData());        
        }
        
        setMeterInfo(); // meter 정보
        setLPData();	// lp
        setBillingMonthData(); // 전력량(전월검침)
        setCurrentBillingMonthData(); // 전력량(현재검침)
        setBillingDayData(); // 최대수요전력
        setPowerQualityData(); // 순시 전압/전류
        setPowerQualityAVGData(); // 평균 전압/전류
        setEventData(); // 정전/복전 event
        setMeteringValue();
        
        log.debug("DLMS parse result:" + result);
    }
    
    /**
     * setMeterInfo
     */
    public void setMeterInfo() {
    	
        try {        	
        
        	Map<String, Object> map = null;
            
            // 전력량계ID(Manufacturer meter ID)
            map = (Map<String, Object>) result.get(OBIS.MANUFACTURER_METER_ID.getCode());
            if (map != null) {
            	Object obj = null;            	
            	obj = map.get(OBIS.MANUFACTURER_METER_ID.getName());
            	if(obj != null) {            	
            		if (obj != null) manufacturerID = (String)obj;        	
    	            log.debug("MANUFACTURER_METER_ID[" + manufacturerID + "]");
            	}
            }
            
            // 계기 식별자 (COSEM 계기 식별자)
            map = (Map<String, Object>) result.get(OBIS.CUSTOMER_METER_ID.getCode());
            if (map != null) {
            	Object obj = null;            	
            	obj = map.get(OBIS.CUSTOMER_METER_ID.getName());
            	if(obj != null) {            	
            		Map<String, Object> customerMap = (Map<String, Object>)obj;        	
            		
            		cosemID = (String)customerMap.get(METER_INFORMATION.COSEM_ID.name());
            		manufactureDate = (String)customerMap.get(METER_INFORMATION.MANUFACTURE_DATE.name());
            		fwVersion = (String)customerMap.get(METER_INFORMATION.FW_VERSION.name());
            		
            		log.debug("CUSTOMER_METER_ID[" + cosemID + ", "+ manufactureDate + ", "+ fwVersion+ "]");
            	}
            }
            
            // 유효전력량 계기 정수
            map = (Map<String, Object>)result.get(OBIS.ACTIVEPOWER_CONSTANT.getCode());
            if (map != null) {
            	Object obj = null;            	
            	obj = map.get(OBIS.ACTIVEPOWER_CONSTANT.getName());
	            if (obj != null) activePulseConstant = getDoubleFromLongOctet(obj);	            
	            log.debug("activePulseConstant[" + activePulseConstant + "]");
            }
            
            // 무효전력량 계기 정수
            map = (Map<String, Object>)result.get(OBIS.REACTIVEPOWER_CONSTANT.getCode());
            if (map != null) {
            	Object obj = null;            	
            	obj = map.get(OBIS.REACTIVEPOWER_CONSTANT.getName());
	            if (obj != null) reactivePulseConstant = getDoubleFromLongOctet(obj);	            
	            log.debug("reactivePulseConstant[" + reactivePulseConstant + "]");
            }
            
            // 피상전력량 계기 정수
            map = (Map<String, Object>)result.get(OBIS.APPRENTPOWER_CONSTANT.getCode());
            if (map != null) {
            	Object obj = null;            	
            	obj = map.get(OBIS.APPRENTPOWER_CONSTANT.getName());
	            if (obj != null) apparentPulseConstant = getDoubleFromLongOctet(obj);	            
	            log.debug("apparentPulseConstant[" + apparentPulseConstant + "]");
            }
            
            // 전력량계 시간
            map = (Map<String, Object>)result.get(OBIS.METER_TIME.getCode()+"-0");
            if (map != null) {
	        	Object obj = map.get(OBIS.METER_TIME.getName());
	        	if (obj != null) {
	        		meterTime = (String)obj;
		        	if(meterTime.length()==12)
		        		meterTime = meterTime+"00"; 		   
		        			        			        	
		        	//meter.setLastReadDate(meterTime);
		        	log.debug("METER_TIME[" + meterTime + "]");
		        	
		        	beforeTime = meterTime;
	        	}
            }
            
            // 전력량계 시간
            map = (Map<String, Object>)result.get(OBIS.METER_TIME.getCode()+"-1");
            if (map != null) {
	        	Object obj = map.get(OBIS.METER_TIME.getName());
	        	if (obj != null) {
	        		afterTime = (String)obj;
		        	if(afterTime.length()==12)
		        		afterTime = afterTime+"00"; 		   

		        	log.debug("AFTER_TIME[" + afterTime + "]");
	        	}
            }
            
            // LP 수집 주기
            map = (Map<String, Object>)result.get(OBIS.LP_CYCLE.getCode());
            if (map != null) {
            	Object obj = null;            	
            	obj = map.get(OBIS.LP_CYCLE.getName());
	            if (obj != null) lpInterval = (int)obj;
	            log.debug("lpInterval[" + lpInterval + "]");
	            meter.setLpInterval(lpInterval);	            
            }
            
            // 정기검침일
            map = (Map<String, Object>)result.get(OBIS.MEASUREMENT_DATE.getCode());
            if (map != null) {            	            	
            	
            	String date = (String)map.get(MEASUREMENT_DATE_INFO.DATE.name());
            	String time = (String)map.get(MEASUREMENT_DATE_INFO.TIME.name());            	
            	
            	mesurementDay = date.substring(6, 8);
            	mesurementDate = date + time; //현재월 정기검침일시(이란력 -> 그레고리안)
	            log.debug("mesurementDate[" + mesurementDate + "]");
            }
            
            // relay status
            map = (Map<String, Object>)result.get(OBIS.METER_RELAY_STATUS.getCode());
            if (map != null) {            	            	
            	
            	Object obj = null;            	
            	obj = map.get(OBIS.METER_RELAY_STATUS.getName());
	            if (obj != null) meterRelayStatus = (int)obj;
	            log.debug("meterRelayStatus[" + meterRelayStatus + "]");
            }
                        
        } catch (Exception e) {
            log.error(e,e);
        }
    }
    
    public LPData[] getLPData() {
        return lpData;
    }
    
    /** 
     * setLPData
     */
    public void setLPData() {
        
    	try {       
    		
            List<LPData> lpDataList = new ArrayList<LPData>();            
            Double lp = 0.0;
            Double lpValue = 0.0;
            Object value = null;
            Map<String, Object> lpMap = null;
            int cnt = 0;
            LPData _lpData = null;

            double importActiveEnergy = 0.0;            
            double importLagReactive = 0.0;
            double importLeadReactive = 0.0;
            double importApprentEnergy = 0.0;
            String status = "";
                                
            for (int i = 0; i < result.size(); i++) {
               
            	if (!result.containsKey(OBIS.LOAD_PROFILE.getCode() + "-" + i))
                    break;
                
                lpMap = (Map<String, Object>) result.get(OBIS.LOAD_PROFILE.getCode() + "-" + i);
                cnt = 0;
                
                while ((value=lpMap.get(LOAD_PROFILE.ImportActiveEnergy.name()+"-"+cnt)) != null) {
                	if (value instanceof OCTET)
                		lp = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                	else if (value instanceof Long)
                		lp = ((Long)value).doubleValue();
                		
                    lpValue = lp / activePulseConstant;                    
                    importActiveEnergy = lpValue;
                    
                    value = lpMap.get(LOAD_PROFILE.ImportLagReactive.name()+"-"+cnt);
                    if (value != null) {
                    	if (value instanceof OCTET)
                    		importLagReactive = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Long)
                    		importLagReactive = ((Long)value).doubleValue();
                    	importLagReactive /= reactivePulseConstant;                    	
                    }
                    value = lpMap.get(LOAD_PROFILE.ImportLeadReactive.name()+"-"+cnt);
                    if (value != null) {
                    	if (value instanceof OCTET)
                    		importLeadReactive = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Long)
                    		importLeadReactive = ((Long)value).doubleValue();
                    	importLeadReactive /= reactivePulseConstant;
                    }                    
                    value = lpMap.get(LOAD_PROFILE.ImportApprentEnergy.name()+"-"+cnt);
                    if (value != null) {
                    	if (value instanceof OCTET)
                    		importApprentEnergy = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Long)
                    		importApprentEnergy = ((Long)value).doubleValue();
                    	
                    	importApprentEnergy /= apparentPulseConstant;
                    }
                   
                    //Get Meter Time & Operation Time
                    Long lmeteringTime = meteringTime != null ? 
                    		DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meteringTime).getTime() : new Date().getTime();
                    Long lmeterTime = meterTime != null ?
                       		DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime() : lmeteringTime;
                       		
                    Long persianDatetime = Long.valueOf(((String)lpMap.get(LOAD_PROFILE.PersianDate.name()+"-"+cnt)));                       		
                       		
                    _lpData = new LPData((String) lpMap
                            .get(LOAD_PROFILE.Date.name() + "-" + cnt), lp, lpValue);
                    _lpData.setCh(new Double[]{importActiveEnergy, importLagReactive, importLeadReactive, importApprentEnergy, lmeterTime.doubleValue(), lmeteringTime.doubleValue(), persianDatetime.doubleValue()});
                    _lpData.setPF(1d);
                    status = (String)lpMap.get(LOAD_PROFILE.Status.name()+"-"+cnt);                    
                    _lpData.setStatus(status);                  
                    
                    // 확인
                    if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
                    	lpDataList.add(_lpData);
                    	log.debug(_lpData.toString());
                    }
                    
                    cnt++;
                } // end while
                
            }// end for
            Collections.sort(lpDataList, LPComparator.TIMESTAMP_ORDER);
            lpData = checkEmptyLP(lpDataList);
  
            log.debug("########################lpData.length:"+lpData.length);
        } catch (Exception e) {
            log.error(e,e);
            e.printStackTrace();
        }
    }
    
    private LPData[] checkEmptyLP(List<LPData> list) throws Exception
    {
        ArrayList<LPData> emptylist = new ArrayList<LPData>();
        List<LPData> totalList = list;
        
        int channelCount = 4;
        if(list != null && list.size() > 0){
        	channelCount = list.get(0).getCh().length;
        }
        Double[] ch  = new Double[channelCount];
        Double[] v  = new Double[channelCount];
        
        for(int i = 0; i < channelCount; i++){
            ch[i] = new Double(0.0);
            v[i] = new Double(0.0);
        }
        
        String prevTime = "";
        String persianDate = "";
        String currentTime = "";
        Double lp = 0.0;
        Double lpValue = 0.0;

        Iterator<LPData> it = totalList.iterator();
        while(it.hasNext()){
        	
        	LPData prev = (LPData)it.next();
            currentTime = prev.getDatetime();
            lp = prev.getLp();
            lpValue = prev.getLpValue();
            ch = prev.getCh();				// INSERT SP-467            
            if(prevTime != null && !prevTime.equals("")){
                String temp = Util.addMinYymmdd(prevTime, lpInterval);
                if(!temp.equals(currentTime))
                {

                    int diffMin = (int) ((Util.getMilliTimes(currentTime+"00")-Util.getMilliTimes(prevTime+"00"))/1000/60) - lpInterval;
                    
                    if(diffMin > 0 && diffMin <= 1440) { //하루이상 차이가 나지 않을때 빈값으로 채운다. 
                        for(int i = 0; i < (diffMin/lpInterval) ; i++) {
                        	
                            log.debug("empty lp temp : "+ currentTime+", diff Min="+diffMin);
                            
                            LPData data = new LPData();
                            data.setLp(lp);
                            data.setLpValue(lpValue);
                            data.setV(v);
                            data.setCh(ch);
                            data.setFlag(0);
                            data.setPF(1.0);
                            // UPDATE START SP-467
                            //data.setDatetime(Util.addMinYymmdd(prevTime, lpInterval*(i+1)));
                            data.setDatetime((Util.addMinYymmdd(prevTime, lpInterval*(i+1))).substring(0, 12));
                            // UPDATE END SP-467
                            emptylist.add(data);
                        } 
                    }
                }
            }
            prevTime = currentTime;
        }
        
        Iterator<LPData> it2 = emptylist.iterator();
        while(it2.hasNext()){
            totalList.add((LPData)it2.next());
        }
        
        Collections.sort(totalList, LPComparator.TIMESTAMP_ORDER);  
        
        return totalList.toArray(new LPData[0]);
    }
    
    /**
     * 전력량 - 매월정기검침일에 생성된 값이 한시간에 한번씩 같은데이터가 들어옴
     * @return
     */
    public void setBillingMonthData() {
    	
    	BillingData billEm = null;
    	
    	try {
	    	// 전력량
    		Map<String, Object> map = (Map<String, Object>) result.get(OBIS.MONTHLY_ENERGY_PROFILE.getCode());
    		if (map != null) {
    			
        		billEm = new BillingData();
        		
        		//한달에 한번이기때문에 해당월의 정기검침일이 들어간다
        		billEm.setBillingTimestamp(mesurementDate); 
    			
    			// 순방향 유효전력량 total
    			Object value = map.get(MONTHLY_ENERGY_PROFILE.ImportActiveEnergy.name());	    			
    			billEm.setActiveEnergyImportRateTotal(getDoubleFromLongOctet(value) / this.activePulseConstant);	    			
    			// 순방향 피상전력량 total
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportApparentEnergy.name());	    			
    			billEm.setActiveEnergyExportRateTotal(getDoubleFromLongOctet(value) / this.apparentPulseConstant);	    			
    			// 순방향 지상 무효전력량 total
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportLagReactive.name());	    			
    			billEm.setReactiveEnergyLagImportRateTotal(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 진상 무효전력량 total
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportLeadReactive.name());	    			
    			billEm.setReactiveEnergyLeadImportRateTotal(getDoubleFromLongOctet(value) / this.reactivePulseConstant);
    			// 순방향 평균 역률 total
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportPowerFactor.name());
    			billEm.setPf(getDoubleFromLongOctet(value));   
    			
    			// 순방향 유효전력량 A
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportActiveEnergyA.name());	    			
    			billEm.setActiveEnergyImportRate1(getDoubleFromLongOctet(value) / this.activePulseConstant);	    			
    			// 순방향 피상전력량 A
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportApparentEnergyA.name());	    			
    			billEm.setActiveEnergyExportRate1(getDoubleFromLongOctet(value) / this.apparentPulseConstant);	    			
    			// 순방향 지상 무효전력량 A
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportLagReactiveA.name());	    			
    			billEm.setReactiveEnergyLagImportRate1(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 진상 무효전력량 A
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportLeadReactiveA.name());	    			
    			billEm.setReactiveEnergyLeadImportRate1(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 평균 역률 A
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportPowerFactorA.name());	    			
    			billEm.setReactivePowerDemandMaxTimeRate1(String.valueOf(getDoubleFromLongOctet(value))); //역율해당필드없어서 넣음
    				    			
    			// 순방향 유효전력량 B
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportActiveEnergyB.name());	    			
    			billEm.setActiveEnergyImportRate2(getDoubleFromLongOctet(value) / this.activePulseConstant);	    			
    			// 순방향 피상전력량 B
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportApparentEnergyB.name());	    			
    			billEm.setActiveEnergyExportRate2(getDoubleFromLongOctet(value) / this.apparentPulseConstant);	    			
    			// 순방향 지상 무효전력량 B
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportLagReactiveB.name());	    			
    			billEm.setReactiveEnergyLagImportRate2(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 진상 무효전력량 B
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportLeadReactiveB.name());	    			
    			billEm.setReactiveEnergyLeadImportRate2(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 평균 역률 B
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportPowerFactorB.name());
    			billEm.setReactivePowerDemandMaxTimeRate2(String.valueOf(getDoubleFromLongOctet(value))); //역율해당필드없어서 넣음
    			
    			// 순방향 유효전력량 C
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportActiveEnergyC.name());	    			
    			billEm.setActiveEnergyImportRate3(getDoubleFromLongOctet(value) / this.activePulseConstant);	    			
    			// 순방향 피상전력량 C
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportApparentEnergyC.name());	    			
    			billEm.setActiveEnergyExportRate3(getDoubleFromLongOctet(value) / this.apparentPulseConstant);	    			
    			// 순방향 지상 무효전력량 C
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportLagReactiveC.name());	    			
    			billEm.setReactiveEnergyLagImportRate3(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 진상 무효전력량 C
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportLeadReactiveC.name());	    			
    			billEm.setReactiveEnergyLeadImportRate3(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 평균 역률 C
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportPowerFactorC.name());	    			
    			billEm.setReactivePowerDemandMaxTimeRate3(String.valueOf(getDoubleFromLongOctet(value))); //역율해당필드없어서 넣음
    			
    			// 순방향 유효전력량 D
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportActiveEnergyD.name());	    			
    			billEm.setActiveEnergyImportRate4(getDoubleFromLongOctet(value) / this.activePulseConstant);	    			
    			// 순방향 피상전력량 D
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportApparentEnergyD.name());	    			
    			billEm.setActiveEnergyExportRate4(getDoubleFromLongOctet(value) / this.apparentPulseConstant);	    			
    			// 순방향 지상 무효전력량 D
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportLagReactiveD.name());	    			
    			billEm.setReactiveEnergyLagImportRate4(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 진상 무효전력량 D
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportLeadReactiveD.name());	    			
    			billEm.setReactiveEnergyLeadImportRate4(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 평균 역률 D
    			value = map.get(MONTHLY_ENERGY_PROFILE.ImportPowerFactorD.name());	    			
    			billEm.setReactivePowerDemandMaxTimeRate4(String.valueOf(getDoubleFromLongOctet(value))); //역율해당필드없어서 넣음
    		}	    	
    	}
    	catch (Exception e) {
    		log.warn(e, e);
    	}
    	
    	billingMonthData = billEm;
    } 
    
    public BillingData getBillingMonthData() {
    	return billingMonthData;
    }
    
    
    /**
     * 전력량 - 현재검침
     * @return
     */
    public void setCurrentBillingMonthData() {
    	
    	BillingData billEm = null;
    	
    	try {
    		// 전력량
    		Map<String, Object> map = (Map<String, Object>) result.get(OBIS.CURRENT_ENERGY_PROFILE.getCode());
    		if (map != null) {
    			
    			billEm = new BillingData();
        		
        		// 현재검침시간
        		billEm.setBillingTimestamp(meterTime);
    			
    			// 순방향 유효전력량 total
    			Object value = map.get(CURRENT_ENERGY_PROFILE.ImportActiveEnergy.name());	    			
    			billEm.setActiveEnergyImportRateTotal(getDoubleFromLongOctet(value) / this.activePulseConstant);	    			
    			// 순방향 피상전력량 total
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportApparentEnergy.name());	    			
    			billEm.setActiveEnergyExportRateTotal(getDoubleFromLongOctet(value) / this.apparentPulseConstant);	    			
    			// 순방향 지상 무효전력량 total
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportLagReactive.name());	    			
    			billEm.setReactiveEnergyLagImportRateTotal(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 진상 무효전력량 total
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportLeadReactive.name());	    			
    			billEm.setReactiveEnergyLeadImportRateTotal(getDoubleFromLongOctet(value) / this.reactivePulseConstant);
    			// 순방향 평균 역률 total
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportPowerFactor.name());
    			billEm.setPf(getDoubleFromLongOctet(value));   
    			
    			// 순방향 유효전력량 A
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportActiveEnergyA.name());	    			
    			billEm.setActiveEnergyImportRate1(getDoubleFromLongOctet(value) / this.activePulseConstant);	    			
    			// 순방향 피상전력량 A
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportApparentEnergyA.name());	    			
    			billEm.setActiveEnergyExportRate1(getDoubleFromLongOctet(value) / this.apparentPulseConstant);	    			
    			// 순방향 지상 무효전력량 A
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportLagReactiveA.name());	    			
    			billEm.setReactiveEnergyLagImportRate1(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 진상 무효전력량 A
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportLeadReactiveA.name());	    			
    			billEm.setReactiveEnergyLeadImportRate1(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 평균 역률 A
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportPowerFactorA.name());	    			
    			billEm.setReactivePowerDemandMaxTimeRate1(String.valueOf(getDoubleFromLongOctet(value))); //역율해당필드없어서 넣음
    				    			
    			// 순방향 유효전력량 B
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportActiveEnergyB.name());	    			
    			billEm.setActiveEnergyImportRate2(getDoubleFromLongOctet(value) / this.activePulseConstant);	    			
    			// 순방향 피상전력량 B
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportApparentEnergyB.name());	    			
    			billEm.setActiveEnergyExportRate2(getDoubleFromLongOctet(value) / this.apparentPulseConstant);	    			
    			// 순방향 지상 무효전력량 B
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportLagReactiveB.name());	    			
    			billEm.setReactiveEnergyLagImportRate2(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 진상 무효전력량 B
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportLeadReactiveB.name());	    			
    			billEm.setReactiveEnergyLeadImportRate2(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 평균 역률 B
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportPowerFactorB.name());
    			billEm.setReactivePowerDemandMaxTimeRate2(String.valueOf(getDoubleFromLongOctet(value))); //역율해당필드없어서 넣음
    			
    			// 순방향 유효전력량 C
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportActiveEnergyC.name());	    			
    			billEm.setActiveEnergyImportRate3(getDoubleFromLongOctet(value) / this.activePulseConstant);	    			
    			// 순방향 피상전력량 C
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportApparentEnergyC.name());	    			
    			billEm.setActiveEnergyExportRate3(getDoubleFromLongOctet(value) / this.apparentPulseConstant);	    			
    			// 순방향 지상 무효전력량 C
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportLagReactiveC.name());	    			
    			billEm.setReactiveEnergyLagImportRate3(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 진상 무효전력량 C
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportLeadReactiveC.name());	    			
    			billEm.setReactiveEnergyLeadImportRate3(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 평균 역률 C
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportPowerFactorC.name());	    			
    			billEm.setReactivePowerDemandMaxTimeRate3(String.valueOf(getDoubleFromLongOctet(value))); //역율해당필드없어서 넣음
    			
    			// 순방향 유효전력량 D
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportActiveEnergyD.name());	    			
    			billEm.setActiveEnergyImportRate4(getDoubleFromLongOctet(value) / this.activePulseConstant);	    			
    			// 순방향 피상전력량 D
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportApparentEnergyD.name());	    			
    			billEm.setActiveEnergyExportRate4(getDoubleFromLongOctet(value) / this.apparentPulseConstant);	    			
    			// 순방향 지상 무효전력량 D
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportLagReactiveD.name());	    			
    			billEm.setReactiveEnergyLagImportRate4(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 진상 무효전력량 D
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportLeadReactiveD.name());	    			
    			billEm.setReactiveEnergyLeadImportRate4(getDoubleFromLongOctet(value) / this.reactivePulseConstant);	    			
    			// 순방향 평균 역률 D
    			value = map.get(CURRENT_ENERGY_PROFILE.ImportPowerFactorD.name());	    			
    			billEm.setReactivePowerDemandMaxTimeRate4(String.valueOf(getDoubleFromLongOctet(value))); //역율해당필드없어서 넣음
    		}	    	
    	}
    	catch (Exception e) {
    		log.warn(e, e);
    	}
    	
    	currentBillingMonthData = billEm;
    } 
    
    public BillingData getCurrentBillingMonthData() {
    	return currentBillingMonthData;
    }
    
    /**
     * 최대수요전력 - 한시간에 한번씩 데이터 검침해서 한번 올려줌
     * @return
     */
    public void setBillingDayData() {
    	
    	BillingData billEm = null;
    	
    	try {
    		
	     	// 전력량
			Map<String, Object> map = (Map<String, Object>) result.get(OBIS.MONTHLY_DEMAND_PROFILE.getCode());
	     	if (map != null) { 			
	     		
	     		billEm = new BillingData();	    		
	     		
	     		// 하루에 한번 조회일자가 날짜가 된다
	    		billEm.setBillingTimestamp(meterTime);
	    		
	    		Object value = map.get(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergy.name());
				if (value != null) {
					billEm.setActivePwrDmdMaxImportRateTotal(getDoubleFromLongOctet(value) / this.activePulseConstant);
					billEm.setActivePwrDmdMaxTimeImportRateTotal(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergyDate.name())));
					billEm.setCummActivePwrDmdMaxImportRateTotal(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdSumActiveEnergy.name())) / this.activePulseConstant);
					billEm.setMaxDmdkVah1RateTotal(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergy.name())) / this.activePulseConstant);
					billEm.setMaxDmdkVah1TimeRateTotal(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergyDate.name())));
					billEm.setCummkVah1RateTotal(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdSumApparentEnergy.name())) / this.activePulseConstant);
				}
				
				value = map.get(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergyA.name());
				if (value != null) {
					billEm.setActivePwrDmdMaxImportRate1(getDoubleFromLongOctet(value) / this.activePulseConstant);
					billEm.setActivePwrDmdMaxTimeImportRate1(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergyDateA.name())));
					billEm.setCummActivePwrDmdMaxImportRate1(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdSumActiveEnergyA.name())) / this.activePulseConstant);
					billEm.setMaxDmdkVah1Rate1(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergyA.name())) / this.activePulseConstant);
					billEm.setMaxDmdkVah1TimeRate1(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergyDateA.name())));
					billEm.setCummkVah1Rate1(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdSumApparentEnergyA.name())) / this.activePulseConstant);
				}
				
				value = map.get(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergyB.name());
				if (value != null) {
					billEm.setActivePwrDmdMaxImportRate2(getDoubleFromLongOctet(value) / this.activePulseConstant);
					billEm.setActivePwrDmdMaxTimeImportRate2(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergyDateB.name())));
					billEm.setCummActivePwrDmdMaxImportRate2(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdSumActiveEnergyB.name())) / this.activePulseConstant);
					billEm.setMaxDmdkVah1Rate2(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergyB.name())) / this.activePulseConstant);
					billEm.setMaxDmdkVah1TimeRate2(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergyDateB.name())));
					billEm.setCummkVah1Rate2(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdSumApparentEnergyB.name())) / this.activePulseConstant);
				}
				
				value = map.get(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergyC.name());
				if (value != null) {
					billEm.setActivePwrDmdMaxImportRate3(getDoubleFromLongOctet(value) / this.activePulseConstant);
					billEm.setActivePwrDmdMaxTimeImportRate3(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergyDateC.name())));
					billEm.setCummActivePwrDmdMaxImportRate3(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdSumActiveEnergyC.name())) / this.activePulseConstant);
					billEm.setMaxDmdkVah1Rate3(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergyC.name())) / this.activePulseConstant);
					billEm.setMaxDmdkVah1TimeRate3(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergyDateC.name())));
					billEm.setCummkVah1Rate3(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdSumApparentEnergyC.name())) / this.activePulseConstant);
				}
				
				value = map.get(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergyD.name());
				if (value != null) {
					billEm.setActivePwrDmdMaxImportRate4(getDoubleFromLongOctet(value) / this.activePulseConstant);
					billEm.setActivePwrDmdMaxTimeImportRate4(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.ImportDmdActiveEnergyDateD.name())));
					billEm.setCummActivePwrDmdMaxImportRate4(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdSumActiveEnergyD.name())) / this.activePulseConstant);
					billEm.setMaxDmdkVah1Rate4(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergyD.name())) / this.activePulseConstant);
					billEm.setMaxDmdkVah1TimeRate4(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.ImportDmdApparentEnergyDateD.name())));
					billEm.setCummkVah1Rate4(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.ImportDmdSumApparentEnergyD.name())) / this.activePulseConstant);
				}
			}
    	}
    	catch (Exception e) {
    		log.warn(e, e);
    	}
    	
    	billingDayData = billEm;
    }
    
    public BillingData getBillingDayData() {
    	return billingDayData;
    }
    
    /**
     * 순시 전압/전류 - 측정순간의 전압/전류값
     * @return
     */
    public void setPowerQualityData() {
    	
    	Instrument pq = null;
    	
    	try {
			Map<String, Object> map = (Map<String, Object>) result.get(OBIS.CURRENT_VOLTAGE.getCode());
			
	     	if (map != null) {			
	     		
	     		pq = new Instrument();
	     		
            	// 순시 전압/전류 구분 (마땅한 필드가 없음)
            	double system_pf_angle = 1.0;
            	pq.setSYSTEM_PF_ANGLE(system_pf_angle);
            	
            	// 일자시간이 없으므로 미터시간 아니면 평균전압전류 시간으로 등록
            	String datetime = meterTime;	                	
            	pq.setDatetime(datetime);
            	
	     		double current_a = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.CURR_A.name()));	     		
	     		pq.setCURR_A(current_a);	     		
	     		double voltage_a = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.VOL_A.name()));
                pq.setVOL_A(voltage_a);                
                double thd_a = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.VOL_THD_A.name()));
                pq.setVOL_THD_A(thd_a);                
                double pf_a = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.PF_A.name()));
                pq.setPF_A(pf_a);                
                double angle_a = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.VOL_ANGLE_A.name()));
                pq.setVOL_ANGLE_A(angle_a);
                
                double current_b = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.CURR_B.name()));	     		
	     		pq.setCURR_B(current_b);	     		
	     		double voltage_b = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.VOL_B.name()));
                pq.setVOL_B(voltage_b);                
                double thd_b = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.VOL_THD_B.name()));
                pq.setVOL_THD_B(thd_b);                
                double pf_b = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.PF_B.name()));
                pq.setPF_B(pf_b);                
                double angle_b = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.VOL_ANGLE_B.name()));
                pq.setVOL_ANGLE_B(angle_b);
                
                double current_c = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.CURR_C.name()));	     		
	     		pq.setCURR_C(current_c);	     		
	     		double voltage_c = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.VOL_C.name()));
                pq.setVOL_C(voltage_c);                
                double thd_c = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.VOL_THD_C.name()));
                pq.setVOL_THD_C(thd_c);                
                double pf_c = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.PF_C.name()));
                pq.setPF_C(pf_c);                
                double angle_c = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.VOL_ANGLE_C.name()));
                pq.setVOL_ANGLE_C(angle_c);
                
                double ph_fund_vol_a = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.PH_FUND_VOL_A.name())); // 전압 A 전압 B 위상각
                pq.setPH_FUND_VOL_A(ph_fund_vol_a);
                double ph_fund_vol_b = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.PH_FUND_VOL_B.name())); // 전압 A 전압 C 위상각
                pq.setPH_FUND_VOL_B(ph_fund_vol_b);                
                double temperature = getDoubleFromLongOctet(map.get(POWER_QUALITY_PROFILE.TEMPERATURE.name())); // 온도
                pq.setPH_FUND_VOL_C(temperature);
			}
    	}
    	catch (Exception e) {
    		log.warn(e, e);
    	}
    	
    	powerQualityData = pq;
    }
    
    public Instrument[] getPowerQualityData() {
    	
    	int i = (powerQualityData == null) ? 0 : 1;
    	
    	Instrument[] instrumentData = new Instrument[powerQualityAvgData.size() + i];
    	
    	if(powerQualityData != null) {
    		instrumentData[0] = powerQualityData;
    	}
    	
    	for(Instrument instrument : powerQualityAvgData) {
    		instrumentData[i++] = instrument;
    	}
    	
    	return instrumentData;
    }
    
    /**
     * 평균 전압/전류 - 특정 시간 사이의 평균 전압/전류
     * @return
     */
    public void setPowerQualityAVGData() {
    	
    	List<Instrument> pqList = new ArrayList<Instrument>();
    	
    	try {    		
    		for (int i = 0; i < result.size(); i++) {
               
	        	if (!result.containsKey(OBIS.AVG_CURRENT_VOLTAGE.getCode() + "-" + i))
	        		break;
	            	
				Map<String, Object> map = (Map<String, Object>) result.get(OBIS.AVG_CURRENT_VOLTAGE.getCode() + "-" + i);
				
		     	if (map != null) { 
		     		
		     		int cnt = 0;		               
	                while ((map.get(POWER_QUALITY_AVG_PROFILE.Date.name()+"-"+cnt)) != null) {
	                
	                	Instrument pq = new Instrument();
	                	
	                	// 평균 전압/전류 구분(마땅한 필드가 없음)
	                	double system_pf_angle = 2.0;
	                	pq.setSYSTEM_PF_ANGLE(system_pf_angle);
	                	
	                	//일자시간
	                	String datetime = (String)map.get(POWER_QUALITY_AVG_PROFILE.Date.name() + "-" + cnt);	                	
	                	pq.setDatetime(datetime);
	                	
			     		double line_ab = getDoubleFromLongOctet(map.get(POWER_QUALITY_AVG_PROFILE.LINE_AB.name()+"-"+cnt));	     		
			     		pq.setLine_AB(line_ab);	     		
			     		double thd_a = getDoubleFromLongOctet(map.get(POWER_QUALITY_AVG_PROFILE.VOL_THD_A.name()+"-"+cnt));
			     		pq.setVOL_THD_A(thd_a);
			     		double curr_harmnic_a = getDoubleFromLongOctet(map.get(POWER_QUALITY_AVG_PROFILE.CURR_HARMONIC_A.name()+"-"+cnt));
			     		pq.setCURR_HARMONIC_A(curr_harmnic_a);
			     		
			     		double line_bc = getDoubleFromLongOctet(map.get(POWER_QUALITY_AVG_PROFILE.LINE_BC.name()+"-"+cnt));	     		
			     		pq.setLine_BC(line_bc);	     		
			     		double thd_b = getDoubleFromLongOctet(map.get(POWER_QUALITY_AVG_PROFILE.VOL_THD_B.name()+"-"+cnt));
			     		pq.setVOL_THD_B(thd_b);
			     		double curr_harmnic_b = getDoubleFromLongOctet(map.get(POWER_QUALITY_AVG_PROFILE.CURR_HARMONIC_B.name()+"-"+cnt));
			     		pq.setCURR_HARMONIC_B(curr_harmnic_b);
			     		
			     		double line_ca = getDoubleFromLongOctet(map.get(POWER_QUALITY_AVG_PROFILE.LINE_CA.name()+"-"+cnt));	     		
			     		pq.setLine_CA(line_ca);	     		
			     		double thd_c = getDoubleFromLongOctet(map.get(POWER_QUALITY_AVG_PROFILE.VOL_THD_C.name()+"-"+cnt));
			     		pq.setVOL_THD_A(thd_c);
			     		double curr_harmnic_c = getDoubleFromLongOctet(map.get(POWER_QUALITY_AVG_PROFILE.CURR_HARMONIC_C.name()+"-"+cnt));
			     		pq.setCURR_HARMONIC_C(curr_harmnic_c);       
			     		
			     		cnt++;
			     		
			     		pqList.add(pq);
	                } //end while
				}
    		} //end for
    	}
    	catch (Exception e) {
    		log.warn(e, e);
    	}    	
    	
    	powerQualityAvgData = pqList;
    }
   
    
    public void setEventData() {    	
    	
    	eventLogList = new ArrayList<EventLogData>();
    	  
    	// 정전
    	if (result.get(OBIS.POWER_FAILURE.getCode()) != null) {
    		Map<String, Object> map = result.get(OBIS.POWER_FAILURE.getCode());
    		makeEventLog(eventLogList, 1, map);
    	}
    	
    	// 복전
    	if (result.get(OBIS.POWER_RESTORE.getCode()) != null) {
    		Map<String, Object> map = result.get(OBIS.POWER_RESTORE.getCode());
    		makeEventLog(eventLogList, 2, map);
    	}
    }
    
    private void makeEventLog(List<EventLogData> eventLogList, int flag, Map<String, Object> map) {
    	
    	int cnt = 0;		               
        while (map.get(EVENT.EventTime.name()+"-"+cnt) != null) {
        	
        	EventLogData e = new EventLogData();
        	String eventTime = (String)map.get(EVENT.EventTime.name() + "-" + cnt);
        	
        	e.setDate(eventTime.substring(0, 8));
            e.setTime(eventTime.substring(8, 12)+"00");
            e.setFlag(flag);
            eventLogList.add(e);
            cnt++;
        }
    }
    
    public List<EventLogData> getEventLog() {
    	return eventLogList;
    }
       
    private String checkDate(String date) {
    	return date;
    }
    
    private double getDoubleFromLongOctet(Object value) throws Exception {
        if (value instanceof Float) return ((Float)value).doubleValue(); 
        else if (value instanceof Long) return ((Long)value).doubleValue();
    	else if (value instanceof OCTET)
			return DataUtil.getFloat(((OCTET)value).getValue(), 0);
    	
    	return 0;
    }    
    
    /**
     * 최근값 lp?
     */
    public void setMeteringValue() {
        try {
            Double lp = 0.0;
            Double lpValue = 0.0;
            Object value = null;
            Map<String, Object> lpMap = null;
            int cnt = 0;
            double activeEnergyImport = 0.0;

            for (int i = 0; i < result.size(); i++) {
                if (!result.containsKey(OBIS.LOAD_PROFILE.getCode() + "-" + i))
                    break;
                
                lpMap = (Map<String, Object>) result.get(OBIS.LOAD_PROFILE.getCode() + "-" + i);
                cnt = 0;
                while ((value=lpMap.get(LOAD_PROFILE.ImportActiveEnergy.name()+"-"+cnt)) != null) {
                	if (value instanceof OCTET)
                		lp = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                	else if (value instanceof Long)
                		lp = ((Long)value).doubleValue();
                		
                    lpValue = lp / activePulseConstant;
                    activeEnergyImport = lpValue;
                    cnt++;
                }
            }
            
            activeEnergyImport *= 0.001;                
            meteringValue= activeEnergyImport;
            log.debug("METERING_VALUE[" + meteringValue + "]");
        } catch (Exception e) {
            log.error(e);
        }
    }
    

    @Override
    public void setFlag(int flag) {
    }

    @Override
    public String toString() {

        return null;
    }

    public Integer getLpInterval() {
        return this.lpInterval;
    }
    
    public Double getActivePulseConstant() {
        return this.activePulseConstant;
    }

    public Double getReactivePulseConstant() {
        return this.reactivePulseConstant;
    }
    
    public Double getApparentPulseConstant() {
        return this.apparentPulseConstant;
    }

    public String getMeterID() {
        return this.meterID;
    }

    public void setCt(Double ct) {
        this.ct= ct;
    }
    
    public Double getCt() {
        return this.ct;
    }
    
    public Long getCtRatio() {
        return this.ct_ratio;
    }
    
    public Long getVtRatio() {
        return this.vt_ratio;
    }

    public String getFwVersion() {
		return this.fwVersion;
	}
    
    public String getMeterModel() {
    	return this.meterModel;
    }
   
    public Double getLimitInfo() {
    	return this.limiterInfo;
    }
    
    public Double getLimiterInfoMin() {
    	return this.limiterInfoMin;
    }
    
	public void setFwVersion(String fwVersion) {
		this.fwVersion = fwVersion;
	}	
	
	public void setMeterModel(String meterModel){
		this.meterModel = meterModel;
	}

	public String getModemId() {
    	return modemID;
    }
    
    public void setModemId(String id) {
    	modemID = id;
    }
    
    public void setModemPort(int address){
    	modemPort = address;
    }
    
    public String getManufacturerID() {
		return manufacturerID;
	}

	public void setManufacturerID(String manufacturerID) {
		this.manufacturerID = manufacturerID;
	}

	public String getMesurementDate() {
		return mesurementDate;
	}

	public void setMesurementDate(String mesurementDate) {
		this.mesurementDate = mesurementDate;
	}
	
	public String getMesurementDay() {
		return mesurementDay;
	}

	public void setMesurementDay(String mesurementDay) {
		this.mesurementDay = mesurementDay;
	}
	
	public String getBeforeTime() {
		return beforeTime;
	}

	public void setBeforeTime(String beforeTime) {
		this.beforeTime = beforeTime;
	}

	public String getAfterTime() {
		return afterTime;
	}

	public void setAfterTime(String afterTime) {
		this.afterTime = afterTime;
	}
		
	public int getMeterRelayStatus() {
		return meterRelayStatus;
	}

	public void setMeterRelayStatus(int meterRelayStatus) {
		this.meterRelayStatus = meterRelayStatus;
	}

	@Override
    public int getFlag() {
        return 0;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public Double getMeteringValue() {
        return meteringValue;
    }

    @Override
    public byte[] getRawData() {
        return null;
    }
    
}