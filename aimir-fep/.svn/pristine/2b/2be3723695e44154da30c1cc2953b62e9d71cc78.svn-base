package com.aimir.fep.meter.parser;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.DLMSKepcoTable.LPComparator;
import com.aimir.fep.meter.parser.DLMSStypeTable.DLMSStypeTable;
import com.aimir.fep.meter.parser.DLMSStypeTable.DLMSStypeVARIABLE;
import com.aimir.fep.meter.parser.DLMSStypeTable.DLMSStypeVARIABLE.LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSStypeTable.DLMSStypeVARIABLE.METER_CONSTANT;
import com.aimir.fep.meter.parser.DLMSStypeTable.DLMSStypeVARIABLE.OBIS;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

public class DLMSStype extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 8916023997892795309L;

	private static Log log = LogFactory.getLog(DLMSStype.class);

	LPData[] lpData = null;
    LinkedHashMap<String, Map<String, Object>> result = 
            new LinkedHashMap<String, Map<String, Object>>();

    int meterConstnt = 0;
    String meterID = "";

    int meterActiveConstant = 1;
    double activePulseConstant = 1;
    
    int interval=0;
    
    Double meteringValue= 0.0;
    Double ct = 1d;
    
    Object beforeTime = null;
    Object afterTime = null;
    
    String maxdemandTime = null;
    Double maxdemand = 0.0;

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
            //locail 정보가 없을때는 기본 포멧을 사용한다.
            decimalf = new DecimalFormat();
            datef14 = new SimpleDateFormat();
        }
        
        for (Iterator i = result.keySet().iterator(); i.hasNext(); ) {
            key = (String)i.next();
            resultSubData = result.get(key);
            // 정복전 결과가 많아서 최근적으로만 넣는다.
            if (key.equals(OBIS.TIME.getCode())) {
            	log.debug("METER_TIME[" + (String)resultSubData.get("MeterTime") + "]");
            	try {
            		data.put(OBIS.getObis(key).getName(),
            		        datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)resultSubData.get("MeterTime"))));
            	}
            	catch (Exception e) {};
            }
            else {
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
                        if (!subkey.contains(DLMSStypeVARIABLE.UNDEFINED)) {
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
                                else data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, subvalue);
                            }
                            else if (subvalue instanceof Number) {
                            	if (OBIS.getObis(key) != OBIS.METER_CONSTANT_ACTIVE && 
                            	        OBIS.getObis(key) != OBIS.METER_CONSTANT_REACTIVE && 
                            	        !subkey.contains("PowerFactor") && !subkey.contains("Interval") &&
                            	        OBIS.getObis(key) != OBIS.POWER_QUALITY && !subkey.contains("Count"))
                            		data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, decimalf.format(((Number) subvalue).doubleValue() / activePulseConstant));
                            	else data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, decimalf.format(subvalue));
                            }
                            else data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, subvalue);
                        }
                    }
                }
            }
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
        // DLMS Header OBIS(6), CLASS(2), ATTR(1), LENGTH(2)
        // DLMS Tag Tag(1), DATA or LEN/DATA (*)
        byte[] OBIS = new byte[6];
        byte[] CLAZZ = new byte[2];
        byte[] ATTR = new byte[1];
        byte[] LEN = new byte[2];
        byte[] TAGDATA = null;
        
        DLMSStypeTable dlms = null;
        while ((pos+OBIS.length) < data.length) {
            log.debug("POS[" + pos + "] Data.LEN[" + data.length + "]");
            dlms = new DLMSStypeTable();
            System.arraycopy(data, pos, OBIS, 0, OBIS.length);
            pos += OBIS.length;
            obisCode = Hex.decode(OBIS);
            log.debug("OBIS[" + obisCode + "]");
            dlms.setObis(obisCode);
            
            System.arraycopy(data, pos, CLAZZ, 0, CLAZZ.length);
            pos += CLAZZ.length;
            clazz = DataUtil.getIntToBytes(CLAZZ);
            log.debug("CLASS[" + clazz + "]");
            dlms.setClazz(clazz);
            
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

            TAGDATA = new byte[len];
            if (pos + TAGDATA.length <= data.length) {
            	System.arraycopy(data, pos, TAGDATA, 0, TAGDATA.length);
            	pos += TAGDATA.length;
            }
            else {
            	System.arraycopy(data, pos, TAGDATA, 0, data.length-pos);
            	pos += data.length-pos;
            }
            
            
            dlms.parseDlmsTag(TAGDATA);
            
            // 동일한 obis 코드를 가진 값이 있을 수 있기 때문에 검사해서 _number를 붙인다.
            if (dlms.getDlmsHeader().getObis() == DLMSStypeVARIABLE.OBIS.LOAD_PROFILE) {
                for (int cnt = 0; ;cnt++) {
                    obisCode = dlms.getDlmsHeader().getObis().getCode() + "-" + cnt;
                    if (!result.containsKey(obisCode)) {
                        result.put(obisCode, dlms.getData());
                        break;
                    }
                }
            }
            else {
            	if (result.containsKey(obisCode)) {
            		result.put(obisCode+".1", dlms.getData());
            	}
            	else {
            		result.put(obisCode, dlms.getData());
            	}
            }
        }

        if (this.getMeter() instanceof EnergyMeter) {
            EnergyMeter meter = (EnergyMeter)this.getMeter();
            
            this.ct = 1.0;
            if (meter != null && meter.getCt() != null && meter.getCt() > 0)
                ct = meter.getCt();
            
            setCt(ct);
        }
        
        setMeterInfo();
        setPulseConstant();
        setMeteringValue();
        setLPData(null);

        //log.debug("DLMS parse result:" + result);
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

    public void setLPData(LPData lastLp) {
        try {
            Map<String, LPData> lpDataMap = new HashMap<String, LPData>();
            if (lastLp != null && lastLp.getDatetime() != null && !lastLp.getDatetime().equals("")) {
            	lpDataMap.put(lastLp.getDatetime(), lastLp);
            	log.debug(lastLp.toString());
            }
            
            Double lp = 0.0;
            Double lpValue = 0.0;
            Object value = null;
            Map<String, Object> lpMap = null;
            int cnt = 0;
            LPData _lpData = null;
            double active = 0.0;
            double laggingReactive = 0.0;
            double leadingReactive = 0.0;
            double apparentEnergy = 0.0;
            String status = null;
            
            for (int i = 0; i < result.size(); i++) {
                if (!result.containsKey(OBIS.LOAD_PROFILE.getCode() + "-" + i))
                    break;
                
                lpMap = (Map<String, Object>) result.get(OBIS.LOAD_PROFILE.getCode() + "-" + i);
                cnt = 0;
                while ((value=lpMap.get(LOAD_PROFILE.ImportActive.name()+"-"+cnt)) != null) {
                	if (value instanceof Long) {
                		lp = ((Long)value).doubleValue();
                	}
                	else if (value instanceof OCTET) {
                		lp = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                	}
                	lpValue = lp / activePulseConstant;
                    
                    active = lpValue;
                    value = lpMap.get(LOAD_PROFILE.ImportLaggingReactive.name()+"-"+cnt);
                    if (value != null) {
                        if (value instanceof OCTET) {
                            laggingReactive = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                        }
                        else if (value instanceof Long) {
                            laggingReactive = ((Long)value).doubleValue();
                        }
                        laggingReactive /= activePulseConstant;
                        laggingReactive *= ct;
                    }
                    value = lpMap.get(LOAD_PROFILE.ImportLeadingReactive.name()+"-"+cnt);
                    if (value != null) {
                        if (value instanceof OCTET) {
                            leadingReactive = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                        }
                        else if (value instanceof Long) {
                            leadingReactive = ((Long)value).doubleValue();
                        }
                        leadingReactive /= activePulseConstant;
                        leadingReactive *= ct;
                    }
                    value = lpMap.get(LOAD_PROFILE.ImportApparentEnergy.name()+"-"+cnt);
                    if (value != null) {
                        if (value instanceof OCTET) {
                            apparentEnergy = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                        }
                        else if (value instanceof Long) {
                            apparentEnergy = ((Long)value).doubleValue();
                        }
                        apparentEnergy /= activePulseConstant;
                        apparentEnergy *= ct;
                    }
                    _lpData = new LPData((String) lpMap
                            .get(LOAD_PROFILE.Date.name() + "-" + (cnt++)), lp, lpValue);
                    
                    _lpData.setCh(new Double[]{active, laggingReactive, leadingReactive, apparentEnergy});
                    
                    value = lpMap.get(LOAD_PROFILE.Status.name()+"-"+cnt++);
                    if (value != null)
                        status = (String)value;
                    
                    _lpData.setStatus(status);
                    
                    if (_lpData.getDatetime() == null)
                    	continue;
                    
                    lpDataMap.put(_lpData.getDatetime(), _lpData);
                    // log.debug(_lpData.toString());
                }
            }
            
            lpData = lpDataMap.values().toArray(new LPData[0]);

            Arrays.sort(lpData, LPComparator.TIMESTAMP_ORDER);        
            // lpData = checkEmptyLP(lpDataList);
            
            /*
            List<LPData> lpDataList = new ArrayList<LPData>();
            String dateTime = null;
            
            if (lpData.length > 0) {
            	_lpData = new LPData();
            	// BeanUtils.copyProperties(lpData[0], _lpData);
            	if (lpData[0].getLp() instanceof Double) {
	            	_lpData.setLp(lpData[0].getLp());
	            	_lpData.setLpValue(lpData[0].getLpValue());
            	}
        	    _lpData.setCh(new Double[]{0.0, lpData[0].getCh()[0], lpData[0].getCh()[1],
                        lpData[0].getCh()[2], lpData[0].getCh()[3]});
            	
            	_lpData.setDatetime(checkLpTime(lpData[0].getDatetime()));
            	dateTime = lpData[0].getDatetime();
            	lpDataList.add(_lpData);
            
	            Calendar cal = Calendar.getInstance();
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	            cal.setTime(sdf.parse(dateTime));
	            
	            for (int i = 0; i < lpData.length; i++) {
	            	if (i+1 == lpData.length) break;
	            	
	            	_lpData = new LPData();
	            	BeanUtils.copyProperties(_lpData, lpData[i+1]);
	            
	            	// 데이타가 깨지는 경우가 발생하는 것으로 보임.
	            	cal.add(Calendar.MINUTE, interval);
	            	if (lpData[i+1].getDatetime().equals(sdf.format(cal.getTime()))) {
                        if ((lpData[i+1].getCh()[0] != 0.0 && lpData[i].getCh()[0] != 0.0) && 
                                (lpData[i+1].getCh()[0] > lpData[i].getCh()[0]*2)) {
                            lpData = null;
                            return;
                        }
	            	}
	            	cal.add(Calendar.MINUTE, interval*-1);
                    
                    // Turn over 계량기의 유효전력량이 최대치가 최면 0부터 다시 시작함.
                    if (lpData[i+1].getCh()[0] < lpData[i].getCh()[0]) {
                    	active = lpData[i+1].getCh()[0] + diffMaxValue(lpData[i].getCh()[0]);
                    }
                    else {
                    	active = lpData[i+1].getCh()[0] - lpData[i].getCh()[0];
                    }
                    if (lpData[i+1].getCh()[1] < lpData[i].getCh()[1]) {
                        laggingReactive = lpData[i+1].getCh()[1] + diffMaxValue(lpData[i].getCh()[1]);
                    }
                    else {
                        laggingReactive = lpData[i+1].getCh()[1] - lpData[i].getCh()[1];
                    }
                    
                    if (lpData[i+1].getCh()[2] < lpData[i].getCh()[2]) {
                        leadingReactive = lpData[i+1].getCh()[2] + diffMaxValue(lpData[i].getCh()[2]);
                    }
                    else {
                        leadingReactive = lpData[i+1].getCh()[2] - lpData[i].getCh()[2];
                    }
                    
                    if (lpData[i+1].getCh()[3] < lpData[i].getCh()[3]) {
                        apparentEnergy = lpData[i+1].getCh()[3] + diffMaxValue(lpData[i].getCh()[3]);
                    }
                    else {      
                        apparentEnergy = lpData[i+1].getCh()[3] - lpData[i].getCh()[3];
                    }
                    
	                _lpData.setCh(new Double[]{active, lpData[i+1].getCh()[0],lpData[i+1].getCh()[1], lpData[i+1].getCh()[2],
                            lpData[i+1].getCh()[3]});
	                _lpData.setDatetime(checkLpTime(_lpData.getDatetime()));
	            	
	                lpDataList.add(_lpData);
	                
	                if (_lpData.getDatetime().equals(sdf.format(cal.getTime()))) {
		                // Max Demand를 설정한다.
		                if (maxdemand <= active * 4) {
		                	maxdemand = active * 4;
		                	maxdemandTime= _lpData.getDatetime();
		                }
	                }
	                else {
	                	cal.setTime(sdf.parse(_lpData.getDatetime()));
	                }
	                cal.add(Calendar.MINUTE, interval);
	            }
	            lpData = lpDataList.toArray(new LPData[0]);
            }
            */
            for (LPData l : lpData) {
                log.debug(l.toString());
            }
            log.info("########################lpData.length:"+lpData.length);
        } catch (Exception e) {
            log.error(e, e);
        }
    }
    
    private double diffMaxValue(double value) {
    	String strValue = ""+value;
    	strValue = strValue.substring(0, strValue.indexOf("."));
    	double maxvalue = 1.0;
    	for (int i = 0; i < strValue.length(); i++) {
    		maxvalue *= 10;
    	}
    	return maxvalue - value;
    }
    
    private String checkLpTime(String lpTime) {
    	// 시간을 체크한다. 시간 변경시 00, 15, 30, 45로 떨어지지 않는다.
    	int mm = Integer.parseInt(lpTime.substring(10, 12));
    	if (mm > 0 && mm < 15) mm = 0;
        else if (mm > 15 && mm < 30) mm = 15;
        else if (mm > 30 && mm < 45) mm = 30;
        else if (mm > 45) mm = 45;
    	return String.format("%s%02d", lpTime.substring(0, 10), mm);
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

            if(prevTime != null && !prevTime.equals("")){
                String temp = Util.addMinYymmdd(prevTime, interval);
                if(!temp.equals(currentTime))
                {

                    int diffMin = (int) ((Util.getMilliTimes(currentTime+"00")-Util.getMilliTimes(prevTime+"00"))/1000/60) - interval;
                    
                    if(diffMin > 0 && diffMin <= 1440){ //하루이상 차이가 나지 않을때 빈값으로 채운다. 
                        for(int i = 0; i < (diffMin/interval) ; i++){
                        	
                            log.debug("empty lp temp : "+ currentTime+", diff Min="+diffMin);
                            
                            LPData data = new LPData();
                            data.setLp(lp);
                            data.setLpValue(lpValue);
                            data.setV(v);
                            data.setCh(ch);
                            data.setFlag(0);
                            data.setPF(1.0);
                            data.setDatetime(Util.addMinYymmdd(prevTime, interval*(i+1)));
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

    public void setPulseConstant() {
        try {
            Map<String, Object> activemap =
                    (Map<String, Object>) result.get(OBIS.METER_CONSTANT_ACTIVE.getCode());
            if(activemap != null) {
            	Object obj = activemap.get(METER_CONSTANT.ActiveC.name());
            	if (obj instanceof Float)
            		activePulseConstant = (Float)obj;
            	else if (obj instanceof OCTET)
            		activePulseConstant = DataUtil.getFloat(((OCTET)obj).getValue(), 0);
	            log.debug("ACTIVE_PULSE_CONSTANT[" + activePulseConstant + "]");
	            // 미터 펄스 상수에 넣는다.
	            meter.setPulseConstant(activePulseConstant);
            }
        } catch (Exception e) {
            log.error(e, e);
        }

    }

    public void setMeterInfo() {
        try {
            for (Iterator i = result.keySet().iterator(); i.hasNext();) {
                log.debug (i.next());
            }
            Map<String, Object> map = (Map<String, Object>) result.get(OBIS.MANUFACTURER_METER_ID.getCode());
            
            Object obj = map.get("ManufacturerMeterId");
            if (obj != null) {
                String manufacture_meterid = (String)obj;
                log.debug("MANUFACTURE_METER_ID[" + manufacture_meterid + "]");
                meterID = manufacture_meterid.substring(3);
            }
            
            map = (Map<String, Object>) result.get(OBIS.CUSTOMER_METER_ID.getCode());
            obj = map.get(OBIS.CUSTOMER_METER_ID.getName());
            if (obj != null) {
                String customer_meterid = (String)obj;
                log.debug("CUSTOMER_METER_ID[" + customer_meterid + "]");
                meterID += customer_meterid;
            }
            log.debug("METER_ID[" + meterID + "]");
            
            map = (Map<String, Object>) result.get(OBIS.LP_INTERVAL.getCode());
            obj = map.get(OBIS.LP_INTERVAL.getName());
            if (obj != null) interval = (Integer)obj;
            log.debug("LP_INTERVAL[" + interval + "]");
            meter.setLpInterval(interval);
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    private double getDoubleFromLongOctet(Object value) throws Exception {
        if (value instanceof Float) return ((Float)value).doubleValue();
        else if (value instanceof Long) return ((Long)value).doubleValue();
    	else if (value instanceof OCTET)
			return DataUtil.getFloat(((OCTET)value).getValue(), 0);
    	
    	return 0;
    }
    
    public void setMeteringValue() {
        try {
            Map<String, Object> map = (Map<String, Object>) result
                    .get(OBIS.IMPORT_ACTIVE_ENERGY.getCode());
            if (map != null) {
	            long active = (Long) map
	                    .get(OBIS.IMPORT_ACTIVE_ENERGY.getName());
	            BigDecimal bd = new BigDecimal(active);
	            meteringValue=bd.doubleValue()/ this.activePulseConstant;
            }
            log.debug("METERING_VALUE[" + meteringValue + "]");
        } catch (Exception e) {
            log.error(e, e);
        }
    }
    
    public LinkedHashMap<String, Object> getMeterSyncTime() {
    	LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("beforeTime", beforeTime);
        map.put("afterTime", afterTime);
        return map;
    }

    public Integer getInterval() {
        return this.interval;
    }
    
    public Double getActivePulseConstant() {
        return this.activePulseConstant;
    }

    public String getMeterID() {
        return this.meter.getMdsId();
    }

    public void setCt(Double ct) {
        this.ct= ct;
    }
    
    public Double getCt() {
        return this.ct;
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
