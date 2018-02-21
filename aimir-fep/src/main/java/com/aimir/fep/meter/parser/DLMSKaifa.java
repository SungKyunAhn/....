package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
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

import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.DLMSKepcoTable.LPComparator;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSTable;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.ENERGY_LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.POWER_QUALITY_PROFILE;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.MBUSMASTER_LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.MBUS_DEVICE_TYPE;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.RELAY_STATUS_KAIFA;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.command.conf.DLMSMeta.CONTROL_STATE;

import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

public class DLMSKaifa extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 5198091223934578776L;

	private static Log log = LogFactory.getLog(DLMSKaifa.class);

	LPData[] lpData = null;
	LPData[] MBus1lpData = null;
	LPData[] MBus2lpData = null;
	LPData[] MBus3lpData = null;
	LPData[] MBus4lpData = null;
	
	Double[] MeteringDataChannelData = null;


	LinkedHashMap<String, Map<String, Object>> result = 
            new LinkedHashMap<String, Map<String, Object>>();

    String meterID = "";
    String modemID = "";
    String fwVersion = "";
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
    RELAY_STATUS_KAIFA	relayStatus;
    CONTROL_STATE	loadCtrlState;
    int loadCtrlMode;
    Double		limiterInfo = 0d;
    Double		limiterInfoMin = 0d;
    int modemPort = 0;
       
    
    int meterActiveConstant = 1;
    int meterReActiveConstant = 1;

    double activePulseConstant = 1;
    double reActivePulseConstant = 1;
    
    //Long ctRatio = 1L;
    
    int lpInterval=60;
    int Mbus1lpInterval = 60;
    int Mbus2lpInterval = 60;
    int Mbus3lpInterval = 60;
    int Mbus4lpInterval = 60;
    
    Double meteringValue= null;
    Double ct = 1d;

    String MBus1MeterID = null;
    String MBus2MeterID = null;
    String MBus3MeterID = null;
    String MBus4MeterID = null;
    MBUS_DEVICE_TYPE MBus1MeterType = null;
    MBUS_DEVICE_TYPE MBus2MeterType = null;
    MBUS_DEVICE_TYPE MBus3MeterType = null;
    MBUS_DEVICE_TYPE MBus4MeterType = null;
    
    public enum CHANNEL_IDX {
    	CUMULATIVE_ACTIVEENERGY_IMPORT(1),
    	CUMULATIVE_ACTIVEENERGY_EXPORT(2),
    	CUMULATIVE_REACTIVEENERGY_IMPORT(3),
    	CUMULATIVE_REACTIVEENERGY_EXPORT(4),
    	INSTANTANEOUS_VOLTAGE_L1(5),
    	INSTANTANEOUS_VOLTAGE_L2(6),
    	INSTANTANEOUS_VOLTAGE_L3(7);
    	
        private int index;
      
        
    	CHANNEL_IDX(int index) {
            this.index = index;
        }
        public int getIndex() {
        	return this.index;
        }
        public void setIndex(int index){
        	this.index = index;
        }
    }
    @Override
    public LinkedHashMap<String, Map<String, Object>> getData() {
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
            //locail, If no information is to use the default format.
            decimalf = new DecimalFormat();
            datef14 = new SimpleDateFormat();
        }
        
        for (Iterator i = result.keySet().iterator(); i.hasNext(); ) {
            key = (String)i.next();
            resultSubData = result.get(key);
            // Before the conquest of many recent results typically only put.
//            if (key.startsWith(DLMSVARIABLE.OBIS.POWER_FAILURE.getCode()) || key.startsWith(DLMSVARIABLE.OBIS.POWER_RESTORE.getCode())) {
//                
//            }
//            else {
                if (resultSubData != null) {
                    String idx = "";
                    if (key.lastIndexOf("-") != -1) {
                        idx = key.substring(key.lastIndexOf("-")+1);
                        key = key.substring(0, key.lastIndexOf("-"));
                    }
                    String subkey = null;
                    Object subvalue = null;
                    for (Iterator subi = resultSubData.keySet().iterator(); subi.hasNext();) {
                        subkey = (String)subi.next();
                        if (!subkey.contains(DLMSVARIABLE.UNDEFINED)) {
                            subvalue = resultSubData.get(subkey);
                            if (subvalue instanceof String) {
                                if (((String)subvalue).contains(":date=")) {
                                    try {
                                        data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(((String)subvalue).substring(6)+"00")));
                                    }
                                    catch (Exception e) {
                                        data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, subvalue);
                                    }
                                }
                                else if (subkey.contains("Date") && !((String)subvalue).contains(":date=") && ((String)subvalue).length()==12) {
                                    try {
                                        data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(subvalue+"00")));
                                    }
                                    catch (Exception e) {
                                        data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, subvalue);
                                    }
                                }
                            }
                            else if (subvalue instanceof Number) {
                                if ( modemPort > 0 && (OBIS.getObis(key).getName()+idx).toString().startsWith(OBIS.MBUSMASTER_LOAD_PROFILE.getName().toString()) ) {
                                	if ( subkey.startsWith("Ch"+modemPort) ){
                                    	data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, decimalf.format(subvalue));
                                	}
                                } else {
                                	if ( (OBIS.getObis(key).getName()+idx).toString().startsWith(OBIS.ENERGY_LOAD_PROFILE.getName().toString())  
                                			&& (subkey.startsWith("ActiveEnergyImport") 
                                					|| subkey.startsWith("ActiveEnergyExport") 
                                					|| subkey.startsWith("ReactiveEnergyImport") 
                                					|| subkey.startsWith("ReactiveEnergyExport") ) ){
                                		Double dvalue = ((Number)subvalue).intValue()*0.001;
                                		data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, decimalf.format(((Number) subvalue).intValue()*0.001		));
                                	} else if((OBIS.getObis(key).getName()+idx).toString().startsWith(OBIS.ENERGY_LOAD_PROFILE.getName().toString())  
                                			&& (subkey.startsWith("Status") )){
                                		data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, DLMSTable.getLP_STATUS(new byte[]{((Number)subvalue).byteValue()}) );                                		
                                	} else {
                                		data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, decimalf.format(subvalue));
                                	}
                                }
                            }
                            else {
                            	String valueStr = subvalue.toString();
								if ( valueStr != null && !valueStr.matches("\\p{Print}*") ){
									valueStr = Hex.decode(valueStr.getBytes());
//		                           	log.debug("Key = " +  OBIS.getObis(key).getName()+idx+" : "+subkey +
//		                           			", Class = " + subvalue.getClass().getName() + 
//		                           			", Value = " + valueStr);
								}
								if ( valueStr == null) {
									valueStr = "";
								}
								OBIS _obis = OBIS.getObis(key);
								if (_obis != null)
								    data.put(_obis.getName()+idx+" : "+subkey, valueStr);
								else {
								    log.warn("OBIS[" + key + "] not exist");
								}
                             }
                        }
                    }
                }
            }
//        }
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
        
        DLMSTable dlms = null;
        while (pos < data.length) {
            dlms = new DLMSTable();
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
            //Because there may be to check the value with the same code obis _number tags.
            if (dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.ENERGY_LOAD_PROFILE) {
            	
	        	Map tempMap = dlms.getData();
	        	// UPDATE START 2016/12/26 SP448
	        	boolean existLpInterval = false;
            	if(tempMap.containsKey("LpInterval")){
    	        	Object obj = tempMap.get("LpInterval");
    	        	if (obj != null) {
    	        		lpInterval = ((Long)obj).intValue()/60; //sec -> min
    	        		existLpInterval = true;
    	        	}
    	        	log.debug("LP_INTERVAL[" + lpInterval + "]");
            	}      	
            	if ( existLpInterval == false ){
            		if ( meter.getLpInterval() != null ){
            			lpInterval = meter.getLpInterval();
            			log.debug("LP_INTERVAL[" + lpInterval + "](Set from Meter.lpInterval)");
            		}
            	}
            	// UPDATE END  2016/12/26 SP448
                for (int cnt = 0; ;cnt++) {
                    obisCode = dlms.getDlmsHeader().getObis().getCode() + "-" + cnt;
                    if (!result.containsKey(obisCode)) {
                        result.put(obisCode, dlms.getData());
                        break;
                    }
                }
            }
            else if (dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.RELAY_STATUS) {
            	Map tempMap = dlms.getData();
            	if ( tempMap.containsKey("Relay Status") || tempMap.containsKey("LoadControlStatus") || tempMap.containsKey("LoadControlMode") ){
            		obisCode = dlms.getDlmsHeader().getObis().getCode()+ "-" + dlms.getDlmsHeader().getAttr();
            	}
            	result.put(obisCode, dlms.getData());
           }
            else if ( dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.MBUS_CLIENT_SETUP_CHANNEL1 ){
            	Map<String, Object> tempMap = dlms.getData();
            	if ( tempMap.containsKey("M-Bus1MeterSerial")){
            		result.put("M-Bus1MeterSerial", tempMap);
            	}
            	else if (tempMap.containsKey("M-Bus1MeterType") ){
            		result.put("M-Bus1MeterType", tempMap);
            	}
            	else if(tempMap.containsKey("M-Bus1MeterLpInterval")){
    	        	Object obj = tempMap.get("M-Bus1MeterLpInterval");
    	        	if (obj != null) Mbus1lpInterval = ((Long)obj).intValue()/60; //sec -> min
    	        	log.debug("MBUS1 LP_INTERVAL[" + lpInterval + "]");

            	}
            }
            else if ( dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.MBUS_CLIENT_SETUP_CHANNEL2 ){
            	Map<String, Object> tempMap = dlms.getData();
            	if ( tempMap.containsKey("M-Bus2MeterSerial")){
            		result.put("M-Bus2MeterSerial", tempMap);
            	}
            	else if (tempMap.containsKey("M-Bus2MeterType") ){
            		result.put("M-Bus2MeterType", tempMap);
            	}
            	else if(tempMap.containsKey("M-Bus2MeterLpInterval")){
    	        	Object obj = tempMap.get("M-Bus2MeterLpInterval");
    	        	if (obj != null) Mbus2lpInterval = ((Long)obj).intValue()/60; //sec -> min
    	        	log.debug("MBUS2 LP_INTERVAL[" + lpInterval + "]");

            	}
            }
            else if ( dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.MBUS_CLIENT_SETUP_CHANNEL3 ){
            	Map<String, Object> tempMap = dlms.getData();
            	if ( tempMap.containsKey("M-Bus3MeterSerial")){
            		result.put("M-Bus3MeterSerial", tempMap);
            	}
            	else if (tempMap.containsKey("M-Bus3MeterType") ){
            		result.put("M-Bus3MeterType", tempMap);
            	}
            	else if(tempMap.containsKey("M-Bus3MeterLpInterval")){
    	        	Object obj = tempMap.get("M-Bus3MeterLpInterval");
    	        	if (obj != null) Mbus3lpInterval = ((Long)obj).intValue()/60; //sec -> min
    	        	log.debug("MBUS3 LP_INTERVAL[" + lpInterval + "]");

            	}
            }
            else if ( dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.MBUS_CLIENT_SETUP_CHANNEL4 ){
            	Map<String, Object> tempMap = dlms.getData();
            	if ( tempMap.containsKey("M-Bus4MeterSerial")){
            		result.put("M-Bus4MeterSerial", tempMap);
            	}
            	else if (tempMap.containsKey("M-Bus4MeterType") ){
            		result.put("M-Bus4MeterType", tempMap);
            	}
            	else if(tempMap.containsKey("M-Bus4MeterLpInterval")){
    	        	Object obj = tempMap.get("M-Bus4MeterLpInterval");
    	        	if (obj != null) Mbus4lpInterval = ((Long)obj).intValue()/60; //sec -> min
    	        	log.debug("MBUS4 LP_INTERVAL[" + lpInterval + "]");

            	}
            }
            else result.put(obisCode, dlms.getData());
        }

        log.debug(this.getMeter().getMeterType().getName());
        //EnergyMeter meter = (EnergyMeter)this.getMeter();
        
        MeterType meterType = MeterType.valueOf(this.getMeter().getMeterType().getName());
        switch (meterType) {
	        case EnergyMeter :
	            EnergyMeter meter = (EnergyMeter)this.getMeter();
	            this.ct = 1.0;
	            if (meter != null && meter.getCt() != null && meter.getCt() > 0)
	                ct = meter.getCt();
	            
	            setCt(ct);
	            break;
	        case GasMeter :
	            break;
	        case WaterMeter :
	            break;
        }                
        
        setMeterInfo();
        setLPData();
        setMBUSLPData();
        setMBusMeterInfo();
        setLPChannelData();
        //setPrevBillingData();
        //setCurrBillingData();
        setMeteringValue();
        log.debug("DLMS parse result:" + result);
    }

    @Override
    public void setFlag(int flag) {


    }

    @Override
    public String toString() {

        return null;
    }

    public LPData[] getLPData() {

        return lpData;
    }

    public void setLPData() {
        try {
            List<LPData> lpDataList = new ArrayList<LPData>();
            
            Double lp = 0.0;
            Double lpValue = 0.0;
            Object value = null;
            Map<String, Object> lpMap = null;
            int cnt = 0;
            LPData _lpData = null;

            double activeEnergyImport = 0.0;
            double activeEnergyExport = 0.0;
            double reactiveEnergyImport = 0.0;
            double reactiveEnergyExport = 0.0;
                    
            for (int i = 0; i < result.size(); i++) {
                if (!result.containsKey(OBIS.ENERGY_LOAD_PROFILE.getCode() + "-" + i))
                    break;
                
                lpMap = (Map<String, Object>) result.get(OBIS.ENERGY_LOAD_PROFILE.getCode() + "-" + i);
                cnt = 0;
                while ((value=lpMap.get(ENERGY_LOAD_PROFILE.ActiveEnergyImport.name()+"-"+cnt)) != null) {
                	if (value instanceof OCTET)
                		lp = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                	else if (value instanceof Long)
                		lp = ((Long)value).doubleValue();
                		
                    lpValue = lp / activePulseConstant;
                    //lpValue = lpValue*ct;	// DELETE SP-270
                    
                    activeEnergyImport = lpValue*0.001;
                    lpValue = activeEnergyImport;	// INSERT SP-501
                    value = lpMap.get(ENERGY_LOAD_PROFILE.ActiveEnergyExport.name()+"-"+cnt);
                    if (value != null) {
                    	if (value instanceof OCTET)
                    		activeEnergyExport = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Long)
                    		activeEnergyExport = ((Long)value).doubleValue();
                    	
                    	activeEnergyExport /= activePulseConstant;
                    	//activeEnergyExport *= ct; // DELETE SP-270
                    	activeEnergyExport *= 0.001;
                    }
                    value = lpMap.get(ENERGY_LOAD_PROFILE.ReactiveEnergyImport.name()+"-"+cnt);
                    if (value != null) {
                    	if (value instanceof OCTET)
                    		reactiveEnergyImport = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Long)
                    		reactiveEnergyImport = ((Long)value).doubleValue();
                    	reactiveEnergyImport /= reActivePulseConstant;
                    	//reactiveEnergyImport *= ct;	// DELETE SP-270
                    	reactiveEnergyImport *= 0.001;
                    }
                    value = lpMap.get(ENERGY_LOAD_PROFILE.ReactiveEnergyExport.name()+"-"+cnt);
                    if (value != null) {
                    	if (value instanceof OCTET)
                    		reactiveEnergyExport = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Long)
                    		reactiveEnergyExport = ((Long)value).doubleValue();
                    	reactiveEnergyExport /= reActivePulseConstant;
                    	//reactiveEnergyExport *= ct;	// DELETE SP-270
                    	reactiveEnergyExport *= 0.001;
                    }
                    
                    //Get Meter Time & Operation Time
                    Long lmeteringTime = meteringTime != null ? 
                    		DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meteringTime).getTime() : new Date().getTime(); ;
                    Long lmeterTime = meterTime != null ?
                       		DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime() : lmeteringTime;  
                       		
                       		
                       		//TODO ADD check logic compare meter(mtering time) and lptime
                       		
                    _lpData = new LPData((String) lpMap
                            .get(ENERGY_LOAD_PROFILE.Date.name() + "-" + cnt), lp, lpValue);
                    _lpData.setCh(new Double[]{activeEnergyImport, activeEnergyExport, reactiveEnergyImport, reactiveEnergyExport, lmeterTime.doubleValue(), lmeteringTime.doubleValue() });
                    //_lpData.setCh(new Double[]{activeEnergyImport, activeEnergyExport, reactiveEnergyImport, reactiveEnergyExport });
                    _lpData.setPF(1d);
                    
                    value = lpMap.get(ENERGY_LOAD_PROFILE.Status.name()+"-"+cnt++);
                    if (value != null) {
                       	if (value instanceof OCTET){
                       		_lpData.setFlag((int)DataUtil.getIntToBytes(((OCTET)value).getValue()));
                       	    _lpData.setStatus(String.valueOf(value));
                       	}
                    	else if (value instanceof Integer){
                        	_lpData.setFlag(((Integer)value).intValue());
                        	_lpData.setStatus(String.valueOf(value));
                    	}
                    }
                    
                    if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
                    	lpDataList.add(_lpData);
                    	log.debug(_lpData.toString());
                    }else{
                        try {
                            EventUtil.sendEvent("Meter Value Alarm",
                                    TargetClass.valueOf(meter.getMeterType().getName()),
                                    meter.getMdsId(),
                                    new String[][] {{"message", "Wrong Date LP, DateTime[" + _lpData.getDatetime() + "]"}}
                                    );
                        }
                        catch (Exception ignore) {
                        }
                        
                        //TODO check time format
                    }
                }
            }
            
            Collections.sort(lpDataList,LPComparator.TIMESTAMP_ORDER);   
//          lpData = checkEmptyLP(lpDataList);		// DELETE SP-501
            
            lpDataList = checkDupLPAndWrongLPTime(lpDataList);
            lpData = lpDataList.toArray(new LPData[0]);	// INSERT SP-501 (Uncomment)
            log.debug("########################lpData.length:"+lpData.length);
        } catch (Exception e) {
            log.error(e,e);
        }
    }

    public LPData[] getMBUS1LPData() {
    	return MBus1lpData;
    }
    
    public LPData[] getMBUS2LPData() {
    	return MBus2lpData;
    }
    
    public LPData[] getMBUS3LPData() {
    	return MBus3lpData;
    }
    
    public LPData[] getMBUS4LPData() {
    	return MBus4lpData;
    }
    
//    public void setMBUSLPData() {
//        try {
//            List<LPData> MBus1lpDataList = new ArrayList<LPData>();
//            List<LPData> MBus2lpDataList = new ArrayList<LPData>();
//            List<LPData> MBus3lpDataList = new ArrayList<LPData>();
//            List<LPData> MBus4lpDataList = new ArrayList<LPData>();
//            
//            Double lp = 0.0;
//            Double lpValue = 0.0;
//            Object value = null;
//            Map<String, Object> lpMap = null;
//            int cnt = 0;
//            LPData _lpData = null;
//
//            double capturedValue = 0.0;
//                    
////            for (int i = 0; i < result.size(); i++) {
////                if (!result.containsKey(OBIS.MBUSMASTER1_LOAD_PROFILE.getCode() + "-" + i))
////                    break;
//                
////                lpMap = (Map<String, Object>) result.get(OBIS.MBUSMASTER1_LOAD_PROFILE.getCode() + "-" + i);
//
//          log.debug(result.toString());
//            
//          if (result.containsKey(OBIS.MBUSMASTER1_LOAD_PROFILE.getCode())) {
//                       
//            	lpMap = (Map<String, Object>) result.get(OBIS.MBUSMASTER1_LOAD_PROFILE.getCode() );
//                log.debug(lpMap.toString());
//
//                cnt = 0;
//                while ((value=lpMap.get(MBUSMASTER_LOAD_PROFILE.Ch1CapturedValue.name()+"-"+cnt)) != null) {
//                	if (value instanceof Long)
//                		lp = ((Long)value).doubleValue();
//
//                	lpValue = lp; //if calculate is necessary, do it here
//                    capturedValue = lpValue;
//                    
//                    _lpData = new LPData((String) lpMap
//                            .get(MBUSMASTER_LOAD_PROFILE.Date.name() + "-" + cnt++), lp, lpValue);
//                    _lpData.setCh(new Double[]{capturedValue});
//                    
//                    if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
//                    	MBus1lpDataList.add(_lpData);
//                    	log.debug(_lpData.toString());
//                    }
//                }
//            }
//            
//            Collections.sort(MBus1lpDataList,LPComparator.TIMESTAMP_ORDER);   
//            MBus1lpData = checkEmptyLP(MBus1lpDataList);
//            
//            log.debug("######################## MBus1lpDataList MBus1lpData.length:"+MBus1lpData.length);
//            
//            //for (int i = 0; i < result.size(); i++) {
////                if (!result.containsKey(OBIS.MBUSMASTER2_LOAD_PROFILE.getCode() + "-" + i))
////                    break;
////                
////                lpMap = (Map<String, Object>) result.get(OBIS.MBUSMASTER2_LOAD_PROFILE.getCode() + "-" + i);
//
//            if (result.containsKey(OBIS.MBUSMASTER2_LOAD_PROFILE.getCode())) {
//            	lpMap = (Map<String, Object>) result.get(OBIS.MBUSMASTER2_LOAD_PROFILE.getCode());
//            	log.debug(lpMap.toString());
//            	cnt = 0;
//                while ((value=lpMap.get(MBUSMASTER_LOAD_PROFILE.Ch2CapturedValue.name()+"-"+cnt)) != null) {
//                	if (value instanceof Long)
//                		lp = ((Long)value).doubleValue();
//
//                	lpValue = lp; //if calculate is necessary, do it here
//                    capturedValue = lpValue;
//                    
//                    _lpData = new LPData((String) lpMap
//                            .get(MBUSMASTER_LOAD_PROFILE.Date.name() + "-" + cnt++), lp, lpValue);
//                    _lpData.setCh(new Double[]{capturedValue});
//                    
//                    if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
//                    	MBus2lpDataList.add(_lpData);
//                    	log.debug(_lpData.toString());
//                    }
//                }
//            }
//            
//            Collections.sort(MBus2lpDataList,LPComparator.TIMESTAMP_ORDER);   
//            MBus2lpData = checkEmptyLP(MBus2lpDataList);
//            
//            log.debug("######################## MBus2lpDataList MBus2lpData.length:"+MBus2lpData.length);
//            
////            for (int i = 0; i < result.size(); i++) {
////                if (!result.containsKey(OBIS.MBUSMASTER3_LOAD_PROFILE.getCode() + "-" + i))
////                    break;
////                
////                lpMap = (Map<String, Object>) result.get(OBIS.MBUSMASTER3_LOAD_PROFILE.getCode() + "-" + i);
//            if (result.containsKey(OBIS.MBUSMASTER3_LOAD_PROFILE.getCode())) {
//            	lpMap = (Map<String, Object>) result.get(OBIS.MBUSMASTER3_LOAD_PROFILE.getCode());
//            	log.debug(lpMap.toString());
//                cnt = 0;
//                while ((value=lpMap.get(MBUSMASTER_LOAD_PROFILE.Ch3CapturedValue.name()+"-"+cnt)) != null) {
//                	if (value instanceof Long)
//                		lp = ((Long)value).doubleValue();
//
//                	lpValue = lp; //if calculate is necessary, do it here
//                    capturedValue = lpValue;
//                    
//                    _lpData = new LPData((String) lpMap
//                            .get(MBUSMASTER_LOAD_PROFILE.Date.name() + "-" + cnt++), lp, lpValue);
//                    _lpData.setCh(new Double[]{capturedValue});
//                    
//                    if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
//                    	MBus3lpDataList.add(_lpData);
//                    	log.debug(_lpData.toString());
//                    }
//                }
//            }
//            
//            Collections.sort(MBus3lpDataList,LPComparator.TIMESTAMP_ORDER);   
//            MBus3lpData = checkEmptyLP(MBus3lpDataList);
//            
//            log.debug("######################## MBus3lpDataList MBus3lpData.length:"+MBus3lpData.length);
//            
////            for (int i = 0; i < result.size(); i++) {
////                if (!result.containsKey(OBIS.MBUSMASTER4_LOAD_PROFILE.getCode() + "-" + i))
////                    break;
////                
////                lpMap = (Map<String, Object>) result.get(OBIS.MBUSMASTER4_LOAD_PROFILE.getCode() + "-" + i);
//            if (result.containsKey(OBIS.MBUSMASTER4_LOAD_PROFILE.getCode())){
//            	lpMap = (Map<String, Object>) result.get(OBIS.MBUSMASTER4_LOAD_PROFILE.getCode());
//            	log.debug(lpMap.toString());
//                cnt = 0;
//                while ((value=lpMap.get(MBUSMASTER_LOAD_PROFILE.Ch4CapturedValue.name()+"-"+cnt)) != null) {
//                	if (value instanceof Long)
//                		lp = ((Long)value).doubleValue();
//
//                	lpValue = lp; //if calculate is necessary, do it here
//                    capturedValue = lpValue;
//                    
//                    _lpData = new LPData((String) lpMap
//                            .get(MBUSMASTER_LOAD_PROFILE.Date.name() + "-" + cnt++), lp, lpValue);
//                    _lpData.setCh(new Double[]{capturedValue});
//                    
//                    if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
//                    	MBus4lpDataList.add(_lpData);
//                    	log.debug(_lpData.toString());
//                    }
//                }
//            }
//            
//            Collections.sort(MBus4lpDataList,LPComparator.TIMESTAMP_ORDER);   
//            MBus4lpData = checkEmptyLP(MBus4lpDataList);
//            
//            log.debug("######################## MBus4lpDataList MBus4lpData.length:"+MBus4lpData.length);
//            
//        } catch (Exception e) {
//            log.error(e,e);
//            e.printStackTrace();
//        }
//    }

    public void setMBUSLPData() {
        try {
            List<LPData> MBus1lpDataList = new ArrayList<LPData>();
            List<LPData> MBus2lpDataList = new ArrayList<LPData>();
            List<LPData> MBus3lpDataList = new ArrayList<LPData>();
            List<LPData> MBus4lpDataList = new ArrayList<LPData>();
            
            Double lp = 0.0;
            Double lpValue = 0.0;
            Object value = null;
            Map<String, Object> lpMap = null;
            int cnt = 0;
            LPData _lpData = null;
            int flag=0;

            double capturedValue = 0.0;
                               
          if (result.containsKey(OBIS.MBUSMASTER_LOAD_PROFILE.getCode())) {
                       
            	lpMap = (Map<String, Object>) result.get(OBIS.MBUSMASTER_LOAD_PROFILE.getCode() );
//                log.debug(lpMap.toString());

                cnt = 0;
                while ((value=lpMap.get(MBUSMASTER_LOAD_PROFILE.Ch1CapturedValue.name()+"-"+cnt)) != null) {
                	if (value instanceof Long)
                		lp = ((Long)value).doubleValue();

                	lpValue = lp; //if calculate is necessary, do it here
                    capturedValue = lpValue;

                    value = lpMap.get(MBUSMASTER_LOAD_PROFILE.Ch1DeviceStatus.name()+"-"+cnt);
                    if (value != null) {
                       	if (value instanceof OCTET)
                       		flag = (int)DataUtil.getIntToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Integer)
                    		flag = ((Integer)value).intValue();
                    }                    
                    
                    _lpData = new LPData((String) lpMap
                            .get(MBUSMASTER_LOAD_PROFILE.Date.name() + "-" + cnt++), lp, lpValue);
                    _lpData.setCh(new Double[]{capturedValue});
                    _lpData.setFlag(flag);
                    
                    if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
                    	MBus1lpDataList.add(_lpData);
                    	log.debug(_lpData.toString());
                    }
                }
            
	            Collections.sort(MBus1lpDataList,LPComparator.TIMESTAMP_ORDER);   
	            // UPDATE START SP-736
	            //MBus1lpData = checkEmptyLP(MBus1lpDataList);
	            MBus1lpDataList = checkDupLPAndWrongLPTime(MBus1lpDataList);
	            MBus1lpData = MBus1lpDataList.toArray(new LPData[0]);
	            // UPDATE END SP-736
	            log.debug("######################## MBus1lpDataList MBus1lpData.length:"+MBus1lpData.length);
            
            	cnt = 0;
                while ((value=lpMap.get(MBUSMASTER_LOAD_PROFILE.Ch2CapturedValue.name()+"-"+cnt)) != null) {
                	if (value instanceof Long)
                		lp = ((Long)value).doubleValue();

                	lpValue = lp; //if calculate is necessary, do it here
                    capturedValue = lpValue;

                    value = lpMap.get(MBUSMASTER_LOAD_PROFILE.Ch2DeviceStatus.name()+"-"+cnt);
                    if (value != null) {
                       	if (value instanceof OCTET)
                       		flag = (int)DataUtil.getIntToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Integer)
                    		flag = ((Integer)value).intValue();
                    }                                        
                    
                    _lpData = new LPData((String) lpMap
                            .get(MBUSMASTER_LOAD_PROFILE.Date.name() + "-" + cnt++), lp, lpValue);
                    _lpData.setCh(new Double[]{capturedValue});
                    _lpData.setFlag(flag);
                    
                    if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
                    	MBus2lpDataList.add(_lpData);
                    	log.debug(_lpData.toString());
                    }
                }
                Collections.sort(MBus2lpDataList,LPComparator.TIMESTAMP_ORDER);   
                // UPDATE START SP-736
	            //MBus2lpData = checkEmptyLP(MBus2lpDataList);
                MBus2lpDataList = checkDupLPAndWrongLPTime(MBus2lpDataList);
	            MBus2lpData = MBus2lpDataList.toArray(new LPData[0]);
	            // UPDATE END SP-736
	            
	            log.debug("######################## MBus2lpDataList MBus2lpData.length:"+MBus2lpData.length);
            
                cnt = 0;
                while ((value=lpMap.get(MBUSMASTER_LOAD_PROFILE.Ch3CapturedValue.name()+"-"+cnt)) != null) {
                	if (value instanceof Long)
                		lp = ((Long)value).doubleValue();

                	lpValue = lp; //if calculate is necessary, do it here
                    capturedValue = lpValue;

                    value = lpMap.get(MBUSMASTER_LOAD_PROFILE.Ch3DeviceStatus.name()+"-"+cnt);
                    if (value != null) {
                       	if (value instanceof OCTET)
                       		flag = (int)DataUtil.getIntToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Integer)
                    		flag = ((Integer)value).intValue();
                    }                 
                    
                    _lpData = new LPData((String) lpMap
                            .get(MBUSMASTER_LOAD_PROFILE.Date.name() + "-" + cnt++), lp, lpValue);
                    _lpData.setCh(new Double[]{capturedValue});
                    _lpData.setFlag(flag);
                    
                    if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
                    	MBus3lpDataList.add(_lpData);
                    	log.debug(_lpData.toString());
                    }
                }
            
	            Collections.sort(MBus3lpDataList,LPComparator.TIMESTAMP_ORDER);   
	            // UPDATE START SP-736
	            //MBus3lpData = checkEmptyLP(MBus3lpDataList);
	            MBus3lpDataList = checkDupLPAndWrongLPTime(MBus3lpDataList);
	            MBus3lpData = MBus3lpDataList.toArray(new LPData[0]);
	            // UPDATE END SP-736
	            log.debug("######################## MBus3lpDataList MBus3lpData.length:"+MBus3lpData.length);
            
                cnt = 0;
                while ((value=lpMap.get(MBUSMASTER_LOAD_PROFILE.Ch4CapturedValue.name()+"-"+cnt)) != null) {
                	if (value instanceof Long)
                		lp = ((Long)value).doubleValue();

                	lpValue = lp; //if calculate is necessary, do it here
                    capturedValue = lpValue;

                    value = lpMap.get(MBUSMASTER_LOAD_PROFILE.Ch4DeviceStatus.name()+"-"+cnt);
                    if (value != null) {
                       	if (value instanceof OCTET)
                       		flag = (int)DataUtil.getIntToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Integer)
                    		flag = ((Integer)value).intValue();
                    }                 
                    
                    _lpData = new LPData((String) lpMap
                            .get(MBUSMASTER_LOAD_PROFILE.Date.name() + "-" + cnt++), lp, lpValue);
                    _lpData.setCh(new Double[]{capturedValue});
                    _lpData.setFlag(flag);
                    
                    if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
                    	MBus4lpDataList.add(_lpData);
                    	log.debug(_lpData.toString());
                    }
                }
            
	            Collections.sort(MBus4lpDataList,LPComparator.TIMESTAMP_ORDER);   
	            // UPDATE START SP-736
	           // MBus4lpData = checkEmptyLP(MBus4lpDataList);
	            MBus4lpDataList = checkDupLPAndWrongLPTime(MBus4lpDataList);
	            MBus4lpData = MBus4lpDataList.toArray(new LPData[0]);
	            // UPDATE END SP-736
	            
	            log.debug("######################## MBus4lpDataList MBus4lpData.length:"+MBus4lpData.length);
          }
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
                    
                    if(diffMin > 0 && diffMin <= 1440){ //하루이상 차이가 나지 않을때 빈값으로 채운다. 
                        for(int i = 0; i < (diffMin/lpInterval) ; i++){
                        	
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
        
        Collections.sort(totalList,LPComparator.TIMESTAMP_ORDER);  
        
        return totalList.toArray(new LPData[0]);
    }

    /*
    public void setPulseConstant() {
        try {
            Map<String, Object> activemap =
                    (Map<String, Object>) result.get(OBIS.METER_CONSTANT_ACTIVE.getCode());
            activePulseConstant = 
                    DataUtil.getFloat(((OCTET) activemap.get(METER_CONSTANT.ActiveC.name())).getValue(), 0);
            log.debug("ACTIVE_PULSE_CONSTANT[" + activePulseConstant + "]");
            // 미터 펄스 상수에 넣는다.
            meter.setPulseConstant(activePulseConstant);
            
            Map<String, Object> reactivemap =
                    (Map<String, Object>) result.get(OBIS.METER_CONSTANT_REACTIVE.getCode());
            reActivePulseConstant = 
                    DataUtil.getFloat(((OCTET)reactivemap.get(METER_CONSTANT.ReactiveC.name())).getValue(), 0);
            log.debug("REACTIVE_PULSE_CONSTANT[" + reActivePulseConstant + "]");
        } catch (Exception e) {
            log.error(e, e);
        }

    }
    */

    public void setMeterInfo() {
        try {
            Map<String, Object> map = null;
            map = (Map<String, Object>) result.get(OBIS.DEVICE_INFO.getCode());
            if (map != null) {
            	Object obj = null;            	
            	obj = map.get(OBIS.DEVICE_INFO.getName());
	            if (obj != null) meterID = (String)obj;
	            log.debug("METER_ID[" + meterID + "]");
            }
            map = (Map<String, Object>)result.get(OBIS.METER_TIME.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.METER_TIME.getName());
	        	if (obj != null) {
	        		meterTime = (String)obj;
		        	if(meterTime.length()==12)
		        		meterTime = meterTime+"00";
		        	meter.setLastReadDate(meterTime);
		        	log.debug("METER_TIME[" + meterTime + "]");
	        	}
            }
            map = (Map<String, Object>) result.get(OBIS.METER_VENDOR.getCode());
            if (map != null) {
            	Object obj = null;            	
            	obj = map.get(OBIS.METER_VENDOR.getName());
	            if (obj != null) meterVendor = (String)obj;
	            log.debug("METER_VENDOR[" + meterVendor + "]");
            }
            map = (Map<String, Object>)result.get(OBIS.MANUFACTURE_SERIAL.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.MANUFACTURE_SERIAL.getName());
	        	if (obj != null) manufactureSerial = (String)obj;
	        	log.debug("MANUFACTURE_SERIAL[" + manufactureSerial + "]");
            }           
            map = (Map<String, Object>)result.get(OBIS.METER_MODEL.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.METER_MODEL.getName());
	        	if (obj != null) meterModel = (String)obj;
	        	setMeterModel(meterModel);
	        	log.debug("METER_MODEL[" + meterModel + "]");
            }
            map = (Map<String, Object>)result.get(OBIS.LOGICAL_NUMBER.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.LOGICAL_NUMBER.getName());
	        	if (obj != null) logicalNumber = (String)obj;
	        	log.debug("LOGICAL_NUMBER[" + logicalNumber + "]");
            }
            map = (Map<String, Object>)result.get(OBIS.FW_VERSION.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.FW_VERSION.getName());
	        	if (obj != null) fwVersion = (String)obj;
	        	setFwVersion(fwVersion);
	        	log.debug("FW_VERSION[" + fwVersion + "]");
            } 
            map = (Map<String, Object>)result.get(OBIS.CT_RATIO.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.CT_RATIO.getName());
	        	if ( obj instanceof OCTET ) {
		        	log.debug("CT_RATIO[null]");
		        	ct_ratio = null;
	        	} else {
	        		if (obj != null) ct_ratio = (Long)obj;
		        	log.debug("CT_RATIO[" + ct_ratio + "]");
	        	}
            }
            map = (Map<String, Object>)result.get(OBIS.PT_RATIO.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.PT_RATIO.getName());
	        	if ( obj instanceof OCTET ) {
		        	log.debug("PT_RATIO[null]");
		        	vt_ratio = null;
	        	} else {
	        		if (obj != null) vt_ratio = (Long)obj;
	        		log.debug("PT_RATIO[" + vt_ratio + "]");
	        	}
            }
            map = (Map<String, Object>)result.get(OBIS.ALARM_OBJECT.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.ALARM_OBJECT.getName());
	        	if (obj != null) meterStatus = (Long)obj;
	        	log.debug("METER_STATUS(ALARM_OBJECT[" + meterStatus + "])");
            }
            map = (Map<String, Object>)result.get(OBIS.RELAY_STATUS.getCode()+"-"+DLMSVARIABLE.DLMS_CLASS_ATTR.REGISTER_ATTR02);
            if (map != null) {
            	Object obj = map.get("Relay Status");
	        	if (obj != null) relayStatus = (RELAY_STATUS_KAIFA)obj;
	        	log.debug("RELAY STATUS([" + relayStatus.getCode() + "]");
            }
            map = (Map<String, Object>)result.get(OBIS.RELAY_STATUS.getCode()+"-"+DLMSVARIABLE.DLMS_CLASS_ATTR.REGISTER_ATTR03);
            if (map != null) {
            	Object obj = map.get("LoadControlStatus");
	        	if (obj != null) loadCtrlState = (CONTROL_STATE)obj;
	        	log.debug("RELAY LOAD CONTROL STATE([" + loadCtrlState.getCode() + "]");
            }
            map = (Map<String, Object>)result.get(OBIS.RELAY_STATUS.getCode()+"-"+DLMSVARIABLE.DLMS_CLASS_ATTR.REGISTER_ATTR04);
            if (map != null) {
            	Object obj = map.get("LoadControlMode");
	        	if (obj != null) loadCtrlMode = (int)obj;
	        	log.debug("RELAY LOAD CONTROL MODE([" + loadCtrlMode + "]");
            }
            map = (Map<String, Object>)result.get(OBIS.LIMITER_INFO.getCode());
            if (map != null) {
	        	Object obj = map.get("LimiterInfo");
	        	if (obj != null) limiterInfo = (Double)obj;
	        	log.debug("LimiterInfo([" + limiterInfo + "]");
            }
            map = (Map<String, Object>)result.get(OBIS.LIMITER_INFO.getCode());
            if (map != null) {
	        	Object obj = map.get("LimiterInfoMin");
	        	if (obj != null) limiterInfoMin = (Double)obj;
	        	log.debug("LimiterInfo Min([" + limiterInfoMin + "]");
            }
            
            

            
            /*
            map = (Map<String, Object>)result.get(OBIS.SERVICEPOINT_SERIAL.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.SERVICEPOINT_SERIAL.getName());
	        	if (obj != null) servicePointSerial = (String)obj;
	        	log.debug("SERVICEPOINT_SERIAL[" + servicePointSerial + "]");
            }         
            
            map = (Map<String, Object>)result.get(OBIS.OVERAL_TRANS_NUM.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.OVERAL_TRANS_NUM.getName());
	        	if (obj != null) trans_num = (Long)obj;
	        	log.debug("OVERAL_TRANS_NUM[" + trans_num + "]");
            }
            map = (Map<String, Object>)result.get(OBIS.PHASE_TYPE.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.PHASE_TYPE.getName());
	        	if (obj != null) phaseType = (byte[])obj;
	        	log.debug("PHASE_TYPE[" + Hex.decode(phaseType) + "]");
            }
            */
            
        } catch (Exception e) {
            log.error(e,e);
        }
    }
    
    public void setMBusMeterInfo() {
        try {
            Map<String, Object> map = null;
            map = (Map<String, Object>) result.get("M-Bus1MeterSerial");
            if (map != null) {
            	Object obj = null;            	
            	obj = map.get("M-Bus1MeterSerial");
	            if (obj != null) {
	            	MBus1MeterID = (String)obj;
		            log.debug("M-Bus1 METER_ID[" + MBus1MeterID + "]");
	            }
            }
            map = (Map<String, Object>)result.get("M-Bus1MeterType");
            if (map != null) {
	        	Object obj = map.get("M-Bus1MeterType");
	        	if (obj != null) {
	        		MBus1MeterType = (MBUS_DEVICE_TYPE)obj;
		            log.debug("M-Bus1 METER_TYPE[" +  MBus1MeterType.name() +"]");
	        	}
            }
           
            map = (Map<String, Object>) result.get("M-Bus2MeterSerial");
            if (map != null) {
            	Object obj = null;            	
            	obj = map.get("M-Bus2MeterSerial");
	            if (obj != null) {
	            	MBus2MeterID = (String)obj;
		            log.debug("M-Bus2 METER_ID[" + MBus2MeterID + "]");
	            }
            }
            map = (Map<String, Object>)result.get("M-Bus2MeterType");
            if (map != null) {
	        	Object obj = map.get("M-Bus2MeterType");
	        	if (obj != null) {
	        		MBus2MeterType = (MBUS_DEVICE_TYPE)obj;
	        		log.debug("M-Bus2 METER_TYPE[" +  MBus2MeterType.name() +"]");
	        	}
            }   
             
            map = (Map<String, Object>) result.get("M-Bus3MeterSerial");
            if (map != null) {
            	Object obj = null;            	
            	obj = map.get("M-Bus3MeterSerial");
	            if (obj != null){
	            	MBus3MeterID = (String)obj;
	            	log.debug("M-Bus3 METER_ID[" + MBus3MeterID + "]");
	            }
            }
            map = (Map<String, Object>)result.get("M-Bus3MeterType");
            if (map != null) {
	        	Object obj = map.get("M-Bus3MeterType");
	        	if (obj != null) {
	        		MBus3MeterType = (MBUS_DEVICE_TYPE)obj;
	        		log.debug("M-Bus3 METER_TYPE[" +  MBus3MeterType.name() +"]");
	        	}
            }
            
            map = (Map<String, Object>) result.get("M-Bus4MeterSerial");
            if (map != null) {
            	Object obj = null;            	
            	obj = map.get("M-Bus4MeterSerial");
	            if (obj != null) {
	            	MBus4MeterID = (String)obj;
	            	log.debug("M-Bus4 METER_ID[" + MBus4MeterID + "]");
	            }
            }
            map = (Map<String, Object>)result.get("M-Bus4MeterType");
            if (map != null) {
	        	Object obj = map.get("M-Bus4MeterType");
	        	if (obj != null) {
	        		MBus4MeterType = (MBUS_DEVICE_TYPE)obj;
	        		log.debug("M-Bus4 METER_TYPE[" +  MBus4MeterType.name() +"]");
	        	}
            }
            
        } catch (Exception e) {
            log.error(e,e);
        }
    }
    /**
	 * @return the mBus1MeterID
	 */
	public String getMBus1MeterID() {
		return MBus1MeterID;
	}

	/**
	 * @param mBus1MeterID the mBus1MeterID to set
	 */
	public void setMBus1MeterID(String mBus1MeterID) {
		MBus1MeterID = mBus1MeterID;
	}

	/**
	 * @return the mBus2MeterID
	 */
	public String getMBus2MeterID() {
		return MBus2MeterID;
	}

	/**
	 * @param mBus2MeterID the mBus2MeterID to set
	 */
	public void setMBus2MeterID(String mBus2MeterID) {
		MBus2MeterID = mBus2MeterID;
	}

	/**
	 * @return the mBus3MeterID
	 */
	public String getMBus3MeterID() {
		return MBus3MeterID;
	}

	/**
	 * @param mBus3MeterID the mBus3MeterID to set
	 */
	public void setMBus3MeterID(String mBus3MeterID) {
		MBus3MeterID = mBus3MeterID;
	}

	/**
	 * @return the mBus4MeterID
	 */
	public String getMBus4MeterID() {
		return MBus4MeterID;
	}

	/**
	 * @param mBus4MeterID the mBus4MeterID to set
	 */
	public void setMBus4MeterID(String mBus4MeterID) {
		MBus4MeterID = mBus4MeterID;
	}

	/**
	 * @return the mBus1MeterType
	 */
	public MBUS_DEVICE_TYPE getMBus1MeterType() {
		return MBus1MeterType;
	}

	/**
	 * @param mBus1MeterType the mBus1MeterType to set
	 */
	public void setMBus1MeterType(MBUS_DEVICE_TYPE mBus1MeterType) {
		MBus1MeterType = mBus1MeterType;
	}

	/**
	 * @return the mBus2MeterType
	 */
	public MBUS_DEVICE_TYPE getMBus2MeterType() {
		return MBus2MeterType;
	}

	/**
	 * @param mBus2MeterType the mBus2MeterType to set
	 */
	public void setMBus2MeterType(MBUS_DEVICE_TYPE mBus2MeterType) {
		MBus2MeterType = mBus2MeterType;
	}

	/**
	 * @return the mBus3MeterType
	 */
	public MBUS_DEVICE_TYPE getMBus3MeterType() {
		return MBus3MeterType;
	}

	/**
	 * @param mBus3MeterType the mBus3MeterType to set
	 */
	public void setMBus3MeterType(MBUS_DEVICE_TYPE mBus3MeterType) {
		MBus3MeterType = mBus3MeterType;
	}

	/**
	 * @return the mBus4MeterType
	 */
	public MBUS_DEVICE_TYPE getMBus4MeterType() {
		return MBus4MeterType;
	}

	/**
	 * @param mBus4MeterType the mBus4MeterType to set
	 */
	public void setMBus4MeterType(MBUS_DEVICE_TYPE mBus4MeterType) {
		MBus4MeterType = mBus4MeterType;
	}

    /**
	 * @return the meteringDataChannelData
	 */
	public Double[] getMeteringDataChannelData() {
		return MeteringDataChannelData;
	}

	/**
	 * @param meteringDataChannelData the meteringDataChannelData to set
	 */
	public void setMeteringDataChannelData(Double[] meteringDataChannelData) {
		MeteringDataChannelData = meteringDataChannelData;
	}
	
	public Instrument[] getPowerQuality(){

    	List<Instrument> insList = new ArrayList<Instrument>();
    	
    	//Instrument instrument = new Instrument();
    	Map<String, Object> pqMap = null;
        Object value = null;
    	int cnt = 0;
    	
    	//instrument.setDatetime(meterTime);

 //   	log.debug("getPowerQuality size=[" + result.size() + "]");
    	
//        for (int i = 0; i < result.size(); i++) {
//        	log.debug("result.containsKey(" + OBIS.POWER_QUALITY_PROFILE.getCode() + ")");
//          if (!result.containsKey(OBIS.POWER_QUALITY_PROFILE.getCode()))
//                break;
            
        	log.debug("result.get(" + OBIS.POWER_QUALITY_PROFILE.getCode() + ")");
            pqMap = (Map<String, Object>) result.get(OBIS.POWER_QUALITY_PROFILE.getCode());
            if ( pqMap == null ){
            	Instrument instrument[] = new Instrument[0];
            	return instrument;
            }
            cnt = 0;
            while ((value=pqMap.get(POWER_QUALITY_PROFILE.L1MaxVoltage.name()+"-"+cnt)) != null) {
            	Instrument instrument = new Instrument();
            	//instrument.setDatetime(meterTime);

            	if (value instanceof Long) {
                	instrument.setVOL_ANGLE_A(((Long) value).doubleValue());   // set L1MaxVoltage to VOL_ANGLE_A column
                    log.debug("L1MaxVoltage[" + instrument.getVOL_ANGLE_A() + "]");
            	}
            	
                if ((value=pqMap.get(POWER_QUALITY_PROFILE.L1MinVoltage.name()+"-"+cnt)) != null) {
                	if (value instanceof Long) {
                    	instrument.setVOL_THD_A(((Long) value).doubleValue());   // set L1MinVoltage to VOL_THD_A column
                        log.debug("L1MinVoltage[" + instrument.getVOL_THD_A() + "]");
                	}
                }
                
                if ((value=pqMap.get(POWER_QUALITY_PROFILE.L1AvgVoltage.name()+"-"+cnt)) != null) {
                	if (value instanceof Long) {
                    	instrument.setVOL_A(((Long) value).doubleValue());   // set L1AvgVoltage to VOL_A column
                        log.debug("L1AvgVoltage[" + instrument.getVOL_A() + "]");
                	}
                }

                if ((value=pqMap.get(POWER_QUALITY_PROFILE.L2MaxVoltage.name()+"-"+cnt)) != null) {
                	if (value instanceof Long) {
                    	instrument.setVOL_ANGLE_B(((Long) value).doubleValue());   // set L2MaxVoltage to VOL_ANGLE_B column
                        log.debug("L2MaxVoltage[" + instrument.getVOL_ANGLE_B() + "]");
                	}
                }
                
                if ((value=pqMap.get(POWER_QUALITY_PROFILE.L2MinVoltage.name()+"-"+cnt)) != null) {
                	if (value instanceof Long) {
                    	instrument.setVOL_THD_B(((Long) value).doubleValue());   // set L2MinVoltage to VOL_THD_B column
                        log.debug("L2MinVoltage[" + instrument.getVOL_THD_B() + "]");
                	}
                }
                	
                if ((value=pqMap.get(POWER_QUALITY_PROFILE.L2AvgVoltage.name()+"-"+cnt)) != null) {
                	if (value instanceof Long) {
                    	instrument.setVOL_B(((Long) value).doubleValue());   // set L2AvgVoltage to VOL_B column
                        log.debug("L2AvgVoltage[" + instrument.getVOL_B() + "]");
                	}
                }
                	
                if ((value=pqMap.get(POWER_QUALITY_PROFILE.L3MaxVoltage.name()+"-"+cnt)) != null) {
                	if (value instanceof Long) {
                    	instrument.setVOL_ANGLE_C(((Long) value).doubleValue());   // set L3MaxVoltage to VOL_ANGLE_C column
                        log.debug("L3MaxVoltage[" + instrument.getVOL_ANGLE_C() + "]");
                	}
                }
                
                if ((value=pqMap.get(POWER_QUALITY_PROFILE.L3MinVoltage.name()+"-"+cnt)) != null) {
                	if (value instanceof Long) {
                    	instrument.setVOL_THD_C(((Long) value).doubleValue());   // set L3MinVoltage to VOL_THD_C column
                        log.debug("L3MinVoltage[" + instrument.getVOL_THD_C() + "]");
                	}
                }
                	
                if ((value=pqMap.get(POWER_QUALITY_PROFILE.L3AvgVoltage.name()+"-"+cnt)) != null) {
                	if (value instanceof Long) {
                    	instrument.setVOL_C(((Long) value).doubleValue());   // set L3AvgVoltage to VOL_C column
                        log.debug("L3AvgVoltage[" + instrument.getVOL_C() + "]");
                	}
                }
                
                if ((value=pqMap.get(POWER_QUALITY_PROFILE.Date.name()+"-"+cnt)) != null) {
//                	if (value instanceof OCTET) {
//                		String datetime = "";
//            			byte[] data = ((OCTET)value).getValue();
//            			if (data.length == 12) {
//            				int year;
//            				try {
//            					year = DataFormat.getIntTo2Byte(DataUtil.select(data, 0, 2));    	           
//            					int month = DataFormat.getIntToByte(data[2]);
//            					int day = DataFormat.getIntToByte(data[3]);
//            					int week = DataFormat.getIntToByte(data[4]);
//            					int hour = DataFormat.getIntToByte(data[5]);
//            					int min = DataFormat.getIntToByte(data[6]);
//    	    	            	DecimalFormat df = new DecimalFormat("00");
//    	    	            	datetime = year + df.format(month) + df.format(day)
//    	    	            	+ df.format(hour) + df.format(min);
//            				} catch (Exception e) {
//            					log.warn(e,e);
//            				}
//            			}                		
//                		               		                		
//                    	instrument.setDatetime(datetime);
//                        log.debug("Datetime[" + datetime + "]");
//                	}
                	if (value instanceof String) {
                    	instrument.setDatetime((String)value);
                        log.debug("Datetime[" + value + "]");
                	}
                }
                
                cnt++;
                insList.add(instrument);
            }
//        }
//    	return (new Instrument[]{instrument});
        return insList.toArray(new Instrument[0]);
    }
    
    public Map<String, Object> getEventLog() {
        Map<String, Object> eventLogs = new LinkedHashMap<String, Object>();
        if (result.get(OBIS.STANDARD_EVENT.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.STANDARD_EVENT.getCode()));
        if (result.get(OBIS.TAMPER_EVENT.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.TAMPER_EVENT.getCode()));
        if (result.get(OBIS.POWERFAILURE_LOG.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.POWERFAILURE_LOG.getCode()));
        if (result.get(OBIS.CONTROL_LOG.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.CONTROL_LOG.getCode()));
        if (result.get(OBIS.POWER_QUALITY_LOG.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.POWER_QUALITY_LOG.getCode()));
        if (result.get(OBIS.FIRMWARE_UPGRADE_LOG.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.FIRMWARE_UPGRADE_LOG.getCode()));
        return eventLogs;
    }    
    
    
    public void setMeteringValue() {
        try {
            Double lp = 0.0;
            Double lpValue = 0.0;
            Object value = null;
            Map<String, Object> lpMap = null;
            int cnt = 0;
            double activeEnergyImport = 0.0;

            for (int i = 0; i < result.size(); i++) {
                if (!result.containsKey(OBIS.ENERGY_LOAD_PROFILE.getCode() + "-" + i))
                    break;
                
                lpMap = (Map<String, Object>) result.get(OBIS.ENERGY_LOAD_PROFILE.getCode() + "-" + i);
                cnt = 0;
                while ((value=lpMap.get(ENERGY_LOAD_PROFILE.ActiveEnergyImport.name()+"-"+cnt)) != null) {
                	if (value instanceof OCTET)
                		lp = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                	else if (value instanceof Long)
                		lp = ((Long)value).doubleValue();
                		
                    lpValue = lp / activePulseConstant;
                    //lpValue = lpValue*getCt();// SP-270
                    
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
    
    /*
    public void setPrevBillingData(){
    	
        if (result.get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT.getCode()).get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("LASTMONTH_ACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT.getCode()).get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("LASTMONTH_ACTIVEENERGY_EXPORT[" + val + "]");
            }
        }        
        if (result.get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT.getCode()).get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("LASTMONTH_REACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT.getCode()).get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("LASTMONTH_REACTIVEENERGY_EXPORT[" + val + "]");
            }
        }   
    }   
    */ 
    
    /*
    public void setCurrBillingData(){
    	
        if (result.get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.getCode()).get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.getName());
            if (obj != null) {            	
            	double val = ((Float) obj).doubleValue();
                meteringValue= val*getCt();
                log.debug("METERING_VALUE[" + meteringValue + "]");        
                log.debug("CUMULATIVE_ACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.getCode()).get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.getName());
            if (obj != null) {            	
            	double val = ((Float) obj).doubleValue();
                log.debug("CUMULATIVE_ACTIVEENERGY_EXPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.getCode()).get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.getName());
            if (obj != null) {            	
            	double val = ((Float) obj).doubleValue();
                log.debug("CUMULATIVE_REACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.getCode()).get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.getName());
            if (obj != null) {            	
            	double val = ((Float) obj).doubleValue();
                log.debug("CUMULATIVE_REACTIVEENERGY_EXPORT[" + val + "]");
            }
        }        
        if (result.get(OBIS.TOTAL_ACTIVEENERGY_IMPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_ACTIVEENERGY_IMPORT.getCode()).get(OBIS.TOTAL_ACTIVEENERGY_IMPORT.getName());
            if (obj != null) {            	
            	double val = ((Float) obj).doubleValue();
                log.debug("TOTAL_ACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.TOTAL_ACTIVEENERGY_EXPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_ACTIVEENERGY_EXPORT.getCode()).get(OBIS.TOTAL_ACTIVEENERGY_EXPORT.getName());
            if (obj != null) {            	
            	double val = ((Float) obj).doubleValue();
                log.debug("TOTAL_ACTIVEENERGY_EXPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.TOTAL_MAX_ACTIVEDEMAND_IMPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_MAX_ACTIVEDEMAND_IMPORT.getCode()).get(OBIS.TOTAL_MAX_ACTIVEDEMAND_IMPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("TOTAL_MAX_ACTIVEDEMAND_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.TOTAL_MAX_ACTIVEDEMAND_EXPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_MAX_ACTIVEDEMAND_EXPORT.getCode()).get(OBIS.TOTAL_MAX_ACTIVEDEMAND_EXPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("TOTAL_MAX_ACTIVEDEMAND_EXPORT[" + val + "]");
            }
        }        
        if (result.get(OBIS.TOTAL_MAX_REACTIVEDEMAND_IMPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_MAX_REACTIVEDEMAND_IMPORT.getCode()).get(OBIS.TOTAL_MAX_REACTIVEDEMAND_IMPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("TOTAL_MAX_REACTIVEDEMAND_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.TOTAL_MAX_REACTIVEDEMAND_EXPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_MAX_REACTIVEDEMAND_EXPORT.getCode()).get(OBIS.TOTAL_MAX_REACTIVEDEMAND_EXPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("TOTAL_MAX_REACTIVEDEMAND_EXPORT[" + val + "]");
            }
        }        
        if (result.get(OBIS.TOTAL_CUM_ACTIVEDEMAND_IMPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_CUM_ACTIVEDEMAND_IMPORT.getCode()).get(OBIS.TOTAL_CUM_ACTIVEDEMAND_IMPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("TOTAL_CUM_ACTIVEDEMAND_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.TOTAL_CUM_ACTIVEDEMAND_EXPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_CUM_ACTIVEDEMAND_EXPORT.getCode()).get(OBIS.TOTAL_CUM_ACTIVEDEMAND_EXPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("TOTAL_CUM_ACTIVEDEMAND_EXPORT[" + val + "]");
            }
        }        
        if (result.get(OBIS.TOTAL_CUM_REACTIVEDEMAND_IMPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_CUM_REACTIVEDEMAND_IMPORT.getCode()).get(OBIS.TOTAL_CUM_REACTIVEDEMAND_IMPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("TOTAL_CUM_REACTIVEDEMAND_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.TOTAL_CUM_REACTIVEDEMAND_EXPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_CUM_REACTIVEDEMAND_EXPORT.getCode()).get(OBIS.TOTAL_CUM_REACTIVEDEMAND_EXPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("TOTAL_CUM_REACTIVEDEMAND_EXPORT[" + val + "]");
            }
        }
    }
    */

    public LinkedHashMap<String, Object> getRelayStatus() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        Map<String, Object> disconectCtrlMap = result.get(OBIS.RELAY_STATUS.getCode() + "-" + DLMSVARIABLE.DLMS_CLASS_ATTR.REGISTER_ATTR02);
        if ( disconectCtrlMap!=null && disconectCtrlMap.get("Relay Status") != null ) {
            log.debug("disconectCtrlMap ==>>>>> " + disconectCtrlMap.toString());
        	map.put("Relay Status", disconectCtrlMap.get("Relay Status"));
        }
        
        disconectCtrlMap = result.get(OBIS.RELAY_STATUS.getCode() + "-" + DLMSVARIABLE.DLMS_CLASS_ATTR.REGISTER_ATTR03);
        if ( disconectCtrlMap!=null && disconectCtrlMap.get("LoadControlStatus") != null ){
            log.debug("disconectCtrlMap ==>>>>> " + disconectCtrlMap.toString());
            map.put("LoadControlStatus", disconectCtrlMap.get("LoadControlStatus"));
        }
        
        disconectCtrlMap = result.get(OBIS.RELAY_STATUS.getCode() + "-" + DLMSVARIABLE.DLMS_CLASS_ATTR.REGISTER_ATTR04);
        if ( disconectCtrlMap!=null && disconectCtrlMap.get("LoadControlMode") != null ){
            log.debug("disconectCtrlMap ==>>>>> " + disconectCtrlMap.toString());
            map.put("LoadControlMode", disconectCtrlMap.get("LoadControlMode"));
        }
        return map;
    }

    public Integer getLpInterval() {
        return this.lpInterval;
    }
    
    public Integer getMbus1LpInterval() {
        return this.Mbus1lpInterval;
    }
    
    public Integer getMbus2LpInterval() {
        return this.Mbus2lpInterval;
    }
    
    public Integer getMbus3LpInterval() {
        return this.Mbus3lpInterval;
    }
    
    public Integer getMbus4LpInterval() {
        return this.Mbus4lpInterval;
    }
    
    public Double getActivePulseConstant() {
        return this.activePulseConstant;
    }

    public Double getReActivePulseConstant() {
        return this.reActivePulseConstant;
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
    
    public RELAY_STATUS_KAIFA getRelayStat() {
    	return this.relayStatus;
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
    
    public String getModemId() {
    	return modemID;
    }
    
    public void setModemId(String id) {
    	modemID = id;
    }
    
    public void setModemPort(int address){
    	modemPort = address;
    }
    
    public void setLPChannelData(){
    	
    	Double channel[] = new Double[7];
    	for ( int i = 0; i < channel.length; i++){
    		channel[i] = null;
    	}
    	double cumulativeScale = 0.001; //  scaler_unit = 0,30(wh)  => kwh
    	double instantaneousScale = 0.1; // scaler_unit = -1,35(V)  => V 
        if (result.get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.getCode()).get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.getName());
            if (obj != null && (obj instanceof Long) ) {            	
            	double val = ((Long) obj).doubleValue()* cumulativeScale;
            	channel[CHANNEL_IDX.CUMULATIVE_ACTIVEENERGY_IMPORT.getIndex()-1]=  val ;
                log.debug("CUMULATIVE_ACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.getCode()).get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.getName());
        	 if (obj != null && (obj instanceof Long) ) {           	
            	double val = ((Long) obj).doubleValue()* cumulativeScale ;
               	channel[CHANNEL_IDX.CUMULATIVE_ACTIVEENERGY_EXPORT.getIndex()-1]= val  ;
                log.debug("CUMULATIVE_ACTIVEENERGY_EXPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.getCode()).get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.getName());
        	 if (obj != null && (obj instanceof Long) ) {  
            	double val = ((Long) obj).doubleValue() * cumulativeScale;
            	channel[CHANNEL_IDX.CUMULATIVE_REACTIVEENERGY_IMPORT.getIndex()-1]=  val ;
                log.debug("CUMULATIVE_REACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.getCode()).get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.getName());
        	 if (obj != null && (obj instanceof Long) ) {            	
            	double val = ((Long) obj).doubleValue()  * cumulativeScale;
               	channel[CHANNEL_IDX.CUMULATIVE_REACTIVEENERGY_EXPORT.getIndex()-1]=  val;
                log.debug("CUMULATIVE_REACTIVEENERGY_EXPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.INSTANTANEOUS_VOLTAGE_L1.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.INSTANTANEOUS_VOLTAGE_L1.getCode()).get(OBIS.INSTANTANEOUS_VOLTAGE_L1.getName());
        	 if (obj != null && (obj instanceof Long) ) {          	
            	double val = ((Long) obj).doubleValue() * instantaneousScale;
               	channel[CHANNEL_IDX.INSTANTANEOUS_VOLTAGE_L1.getIndex()-1]=  val ;
                log.debug("INSTANTANEOUS_VOLTAGE_L1[" + val + "]");
            }
        }
        if (result.get(OBIS.INSTANTANEOUS_VOLTAGE_L2.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.INSTANTANEOUS_VOLTAGE_L2.getCode()).get(OBIS.INSTANTANEOUS_VOLTAGE_L2.getName());
        	 if (obj != null && (obj instanceof Long) ) {             	
            	double val = ((Long) obj).doubleValue() * instantaneousScale;
               	channel[CHANNEL_IDX.INSTANTANEOUS_VOLTAGE_L2.getIndex()-1]=  val ;
                log.debug("INSTANTANEOUS_VOLTAGE_L2[" + val + "]");
            }
        }
        if (result.get(OBIS.INSTANTANEOUS_VOLTAGE_L3.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.INSTANTANEOUS_VOLTAGE_L3.getCode()).get(OBIS.INSTANTANEOUS_VOLTAGE_L3.getName());
        	 if (obj != null && (obj instanceof Long) ) {            	
            	double val = ((Long) obj).doubleValue() * instantaneousScale;
               	channel[CHANNEL_IDX.INSTANTANEOUS_VOLTAGE_L3.getIndex()-1]=  val ;
                log.debug("INSTANTANEOUS_VOLTAGE_L3[" + val + "]");
            }
        }
        // check 
        int maxidx = -1;
        for ( int i = 0; i < channel.length; i++){
        	if ( channel[i] != null){
        		maxidx = i;
        	}
        }
        if ( maxidx >= 0) {
        	MeteringDataChannelData  = new Double[maxidx + 1];
        	System.arraycopy(channel, 0, MeteringDataChannelData, 0, maxidx + 1);
        }
    }
    
    private List<LPData> checkDupLPAndWrongLPTime(List<LPData> list) throws Exception
    {
        List<LPData> totalList = list;
        List<LPData> removeList = new ArrayList<LPData>();
        LPData prevLPData = null;
    	
        for(int i = 0; i < list.size(); i++){

        	if(prevLPData!= null && prevLPData.getDatetime() != null && !prevLPData.getDatetime().equals("")){
        		if(list.get(i).getDatetime().equals(prevLPData.getDatetime()) 
        				&& list.get(i).getCh()[0].equals(prevLPData.getCh()[0])){
        			//log.warn("time equls:" +list.get(i).getDatetime());  
        			removeList.add(list.get(i));
                    try {
                        EventUtil.sendEvent("Meter Value Alarm",
                                TargetClass.valueOf(meter.getMeterType().getName()),
                                meter.getMdsId(),
                                new String[][] {{"message", "Duplicate LP, DateTime[" + list.get(i).getDatetime() + "] LP Val[" + list.get(i).getCh()[0] + "]"}}
                                );
                    }
                    catch (Exception ignore) {
                    }

        		}else if(list.get(i).getDatetime().equals(prevLPData.getDatetime()) 
        				&& list.get(i).getCh()[0] > prevLPData.getCh()[0]){
        			System.out.println("time equls:" +list.get(i).getDatetime()); 
        			removeList.add(list.get(i-1));
                    try {
                        EventUtil.sendEvent("Meter Value Alarm",
                                TargetClass.valueOf(meter.getMeterType().getName()),
                                meter.getMdsId(),
                                new String[][] {{"message", "Duplicate LP and Diff Value DateTime[" + list.get(i).getDatetime() + "] LP Val[" + list.get(i).getCh()[0]+"/"+prevLPData.getCh()[0] + "]"}}
                                );
                    }
                    catch (Exception ignore) {
                    }
        	    }else if(list.get(i).getDatetime().equals(prevLPData.getDatetime()) 
        				&& list.get(i).getCh()[0] < prevLPData.getCh()[0]){
        	    	System.out.println("time equls:" +list.get(i).getDatetime()); 
        			removeList.add(list.get(i));
                    try {
                        EventUtil.sendEvent("Meter Value Alarm",
                                TargetClass.valueOf(meter.getMeterType().getName()),
                                meter.getMdsId(),
                                new String[][] {{"message", "Duplicate LP and Diff Value DateTime[" + list.get(i).getDatetime() + "] LP Val[" + list.get(i).getCh()[0]+"/"+prevLPData.getCh()[0] + "]"}}
                                );
                    }
                    catch (Exception ignore) {
                    }
        	    }
        		
        	}
        	prevLPData = list.get(i);
        	
        	if(list.get(i).getDatetime().startsWith("1994") 
        			|| list.get(i).getDatetime().startsWith("2000")
        			|| (list.get(i).getDatetime().startsWith("2057") && !TimeUtil.getCurrentTime().startsWith("205"))){
        		removeList.add(list.get(i));
                try {
                    EventUtil.sendEvent("Meter Value Alarm",
                            TargetClass.valueOf(meter.getMeterType().getName()),
                            meter.getMdsId(),
                            new String[][] {{"message", "Wrong Date LP, DateTime[" + list.get(i).getDatetime() + "]"}}
                            );
                }
                catch (Exception ignore) {
                }
        	}
        	if(meterTime != null && !"".equals(meterTime) 
        			&& meterTime.length() == 14 
        			&& list.get(i).getDatetime().compareTo(meterTime.substring(0, 12)) > 0){
        		removeList.add(list.get(i));
                try {
                    EventUtil.sendEvent("Meter Value Alarm",
                            TargetClass.valueOf(meter.getMeterType().getName()),
                            meter.getMdsId(),
                            new String[][] {{"message", "Wrong Date LP, DateTime[" + list.get(i).getDatetime() + "] Meter Time[" + meterTime + "]"}}
                            );
                }
                catch (Exception ignore) {
                }
        	}        	

            Long lpTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(list.get(i).getDatetime()+"00").getTime();
            Long serverTime = new Date().getTime(); ;
 
        	if(lpTime > serverTime){
                try {
                    EventUtil.sendEvent("Meter Value Alarm",
                            TargetClass.valueOf(meter.getMeterType().getName()),
                            meter.getMdsId(),
                            new String[][] {{"message", "Wrong Date LP, DateTime[" + list.get(i).getDatetime() + "] Current Time[" + TimeUtil.getCurrentTime() + "]"}}
                            );
                }
                catch (Exception ignore) {
                }
        	}

        }

      
        totalList.removeAll(removeList);
        return totalList;
    }

}
