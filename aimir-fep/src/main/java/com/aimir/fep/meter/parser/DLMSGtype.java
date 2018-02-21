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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.DLMSGtypeTable.DLMSGtypeVARIABLE.MONTHLY_ENERGY_PROFILE;
import com.aimir.fep.meter.parser.DLMSKepcoTable.LPComparator;
import com.aimir.fep.meter.parser.DLMSGtypeTable.DLMSGtypeTable;
import com.aimir.fep.meter.parser.DLMSGtypeTable.DLMSGtypeVARIABLE;
import com.aimir.fep.meter.parser.DLMSGtypeTable.DLMSGtypeVARIABLE.CURRENT_MAX_DEMAND;
import com.aimir.fep.meter.parser.DLMSGtypeTable.DLMSGtypeVARIABLE.EVENT;
import com.aimir.fep.meter.parser.DLMSGtypeTable.DLMSGtypeVARIABLE.MONTHLY_DEMAND_PROFILE;
import com.aimir.fep.meter.parser.DLMSGtypeTable.DLMSGtypeVARIABLE.POWER_QUALITY;
import com.aimir.fep.meter.parser.DLMSGtypeTable.DLMSGtypeVARIABLE.PREVIOUS_MAX_DEMAND;
import com.aimir.fep.meter.parser.DLMSGtypeTable.DLMSGtypeVARIABLE.LOAD_PROFILE;
import com.aimir.fep.meter.parser.DLMSGtypeTable.DLMSGtypeVARIABLE.METER_CONSTANT;
import com.aimir.fep.meter.parser.DLMSGtypeTable.DLMSGtypeVARIABLE.OBIS;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.mvm.PowerQuality;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

public class DLMSGtype extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 8916023997892795309L;

	private static Log log = LogFactory.getLog(DLMSGtype.class);

	LPData[] lpData = null;
    LinkedHashMap<String, Map<String, Object>> result = 
            new LinkedHashMap<String, Map<String, Object>>();

    int meterConstnt = 0;
    String meterID = "";

    int meterActiveConstant = 0;
    int meterReActiveConstant = 0;

    double activePulseConstant = 0;
    double reActivePulseConstant = 0;
    
    Long ctRatio = 0L;
    
    int interval=0;
    
    Double meteringValue= null;
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
            if (key.equals(OBIS.METER_TIME.getCode())) {
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
                        if (!subkey.contains(DLMSGtypeVARIABLE.UNDEFINED)) {
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
                            	if ((subkey.contains("Reactive")||subkey.contains("Apparent")) && !subkey.contains("ReactiveC"))
                            		data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, decimalf.format(((Number) subvalue).doubleValue() / reActivePulseConstant));
                            	else if (OBIS.getObis(key) != OBIS.METER_CONSTANT_ACTIVE && 
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
        // DLMS Header OBIS(6), CLASS(1), ATTR(1), LENGTH(2)
        // DLMS Tag Tag(1), DATA or LEN/DATA (*)
        byte[] OBIS = new byte[6];
        byte[] CLAZZ = new byte[1];
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
            DataUtil.convertEndian(LEN);
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
            if (dlms.getDlmsHeader().getObis() == DLMSGtypeVARIABLE.OBIS.LOAD_PROFILE) {
            	if (dlms.getData().get("LpInterval") != null) {
            		interval = (int)((Long)dlms.getData().get("LpInterval") / 60);
            		meter.setLpInterval(interval);
            	}
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

        EnergyMeter meter = (EnergyMeter)this.getMeter();
        
        this.ct = 1.0;
        if (meter != null && meter.getCt() != null && meter.getCt() > 0)
            ct = meter.getCt();
        
        setCt(ct);
        
        setMeterInfo();
        setPulseConstant();
        // setMeteringValue();
        // setLPData();

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
            boolean isExportEnergy = false;
            double exportEnergy = 0.0;
            String status = null;
            
            for (int i = 0; i < result.size(); i++) {
                if (!result.containsKey(OBIS.LOAD_PROFILE.getCode() + "-" + i))
                    break;
                
                lpMap = (Map<String, Object>) result.get(OBIS.LOAD_PROFILE.getCode() + "-" + i);
                cnt = 0;
                while ((value=lpMap.get(LOAD_PROFILE.ImportActive.name()+"-"+cnt)) != null) {
                    isExportEnergy = false;
                    
                	if (value instanceof Long) {
                		lp = ((Long)value).doubleValue();
                	}
                	else if (value instanceof OCTET) {
                		lp = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                	}
                	lpValue = lp / activePulseConstant;
                    lpValue = lpValue*ct;
                    
                    active = lpValue;
                    value = lpMap.get(LOAD_PROFILE.ImportLaggingReactive.name()+"-"+cnt);
                    if (value != null) {
                    	if (value instanceof OCTET) {
                    		laggingReactive = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                    	}
                    	else if (value instanceof Long) {
                    		laggingReactive = ((Long)value).doubleValue();
                    	}
                        laggingReactive /= reActivePulseConstant;
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
                        leadingReactive /= reActivePulseConstant;
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
                    	apparentEnergy /= reActivePulseConstant;
                    	apparentEnergy *= ct;
                    }
                    value = lpMap.get(LOAD_PROFILE.ExportActive.name()+"-"+cnt);
                    if (value != null) {
                        isExportEnergy = true;
                        if (value instanceof OCTET) {
                            exportEnergy = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                        }
                        else if (value instanceof Long) {
                            exportEnergy = ((Long)value).doubleValue();
                        }
                        exportEnergy /= activePulseConstant;
                        exportEnergy *= ct;
                    }
                    
                    _lpData = new LPData((String) lpMap
                            .get(LOAD_PROFILE.Date.name() + "-" + cnt), lp, lpValue);
                    
                    if (isExportEnergy)
                        _lpData.setCh(new Double[]{active, laggingReactive, leadingReactive, apparentEnergy, exportEnergy});
                    else
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
            
            List<LPData> lpDataList = new ArrayList<LPData>();
            String dateTime = null;
            
            if (lpData.length > 0) {
            	_lpData = new LPData();
            	// BeanUtils.copyProperties(lpData[0], _lpData);
            	if (lpData[0].getLp() instanceof Double) {
	            	_lpData.setLp(lpData[0].getLp());
	            	_lpData.setLpValue(lpData[0].getLpValue());
            	}
            	if (lpData[0].getCh().length == 5) {
                	_lpData.setCh(new Double[]{0.0, lpData[0].getCh()[0], lpData[0].getCh()[1],
                			lpData[0].getCh()[2], lpData[0].getCh()[3], 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, lpData[0].getCh()[4]});
            	}
            	else {
            	    _lpData.setCh(new Double[]{0.0, lpData[0].getCh()[0], lpData[0].getCh()[1],
                            lpData[0].getCh()[2], lpData[0].getCh()[3], 1.0, 1.0, 1.0, 0.0, 0.0, 0.0});
            	}
            	
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
                        if ((lpData[i+1].getCh()[0] != 0.0 && lpData[i].getCh()[0] != 0.0) && (lpData[i+1].getCh()[0] > lpData[i].getCh()[0]*2)) {
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
                    
                    if (lpData[i+1].getCh().length == 5 && lpData[i].getCh().length == 5) {
                        if (lpData[i+1].getCh()[4] != null && lpData[i].getCh()[4] != null) {
                            if (lpData[i+1].getCh()[4] < lpData[i].getCh()[4]) {
                                exportEnergy = lpData[i+1].getCh()[4] + diffMaxValue(lpData[i].getCh()[4]);
                            }
                            else {      
                                exportEnergy = lpData[i+1].getCh()[4] - lpData[i].getCh()[4];
                            }
                        }
                    }
	            	
	            	if (laggingReactive < 0 || leadingReactive < 0 || apparentEnergy < 0) {
	            		lpData = null;
	            		return;
	            	}
	            	
	            	double laggingPowerFactor = 1;
	                if (active != 0 || laggingReactive != 0) {
	                	laggingPowerFactor = active / Math.sqrt(active*active + laggingReactive*laggingReactive);
	                }
	                double leadingPowerFactor = 1;
	                if (active != 0 || leadingReactive != 0) {
	                	leadingPowerFactor = active / Math.sqrt(active*active + leadingReactive*leadingReactive);
	                }
	                
	                double pf = 1.0;
	                if (active == 0 && laggingReactive == 0 && leadingReactive == 0)
	                	pf = 1.0;
	                else
	                	pf = active / Math.sqrt(active*active + (laggingReactive+leadingReactive)*(laggingReactive+leadingReactive));
	                
	                if (lpData[i+1].getCh().length == 5) {
    	                _lpData.setCh(new Double[]{active, lpData[i+1].getCh()[0], lpData[i+1].getCh()[1], lpData[i+1].getCh()[2],
    	                		lpData[i+1].getCh()[3], laggingPowerFactor, leadingPowerFactor, pf,
    	                		laggingReactive, leadingReactive, apparentEnergy, exportEnergy, lpData[i+1].getCh()[4]});
	                }
	                else {
	                    _lpData.setCh(new Double[]{active, lpData[i+1].getCh()[0], lpData[i+1].getCh()[1], lpData[i+1].getCh()[2],
                                lpData[i+1].getCh()[3], laggingPowerFactor, leadingPowerFactor, pf,
                                laggingReactive, leadingReactive, apparentEnergy});
	                }
	                
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
	            for (LPData l : lpData) {
	            	log.debug(l.toString());
	            }
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
	            
	            Map<String, Object> reactivemap =
	                    (Map<String, Object>) result.get(OBIS.METER_CONSTANT_REACTIVE.getCode());
	            if (reactivemap != null) {
		            obj = reactivemap.get(METER_CONSTANT.ReactiveC.name());
		            if (obj instanceof Float)
		            	reActivePulseConstant = (Float)obj;
		            else if (obj instanceof OCTET)
		                reActivePulseConstant = DataUtil.getFloat(((OCTET)reactivemap.get(METER_CONSTANT.ReactiveC.name())).getValue(), 0);
	            }
	            else {
	            	log.warn("Reactive Pulse not exist");
	            	reActivePulseConstant = activePulseConstant;
	            }
	            
	            log.debug("REACTIVE_PULSE_CONSTANT[" + reActivePulseConstant + "]");
            }
        } catch (Exception e) {
            log.error(e, e);
        }

    }

    public void setMeterInfo() {
        try {
        	/*
            Map<String, Object> map = (Map<String, Object>) result.get(OBIS.METER_INFO.getCode());
            Object obj = map.get(KEPCO_METER_INFO.MeterDate.name());
            if (obj != null) meterTime = (String)obj;
            log.debug("METER_TIME[" + meterTime + "]");
            
            obj = map.get(KEPCO_METER_INFO.MeterId.name());
            if (obj != null) meterID = ((OCTET)obj).toString();
            log.debug("METER_ID[" + meterID + "]");
            
            obj = map.get(KEPCO_METER_INFO.LoadProfileInterval.name());
            if (obj != null) interval = (Integer)obj;
            log.debug("INTERVAL[" + interval + "]");
            
            obj = map.get(KEPCO_METER_INFO.CTRatio.name());
            if (obj != null) ctRatio = (Long)obj;
            log.debug("CTRatio[" + ctRatio + "]");
            */
        	Map<String, Object> map = (Map<String, Object>)result.get(OBIS.METER_TIME.getCode());
        	Object obj = map.get("MeterTime");
        	if (obj != null) meterTime = (String)obj;
        	map = (Map<String, Object>)result.get(OBIS.METER_TIME.getCode()+".1");
        	if (map != null) {
	        	obj = map.get("MeterTime");
	        	if (obj != null) {
	        		beforeTime = meterTime;
	        		afterTime = (String)obj;
	        		meterTime = (String)afterTime;
	        	}
        	}
        	log.debug("METER_TIME[" + meterTime + "] BEFORE_TIME[" + beforeTime + "] AFTER_TIME[" + afterTime + "]");
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public Instrument getInstrument() {
        Map<String, Object> map = null;
        try {
            map = (Map<String, Object>) result.get(OBIS.POWER_QUALITY.getCode());
            if (map != null) {
                double current_a = getDoubleFromLongOctet(map.get(POWER_QUALITY.CurrentA.name()));
                log.debug("CURRENT_A[" + current_a + "]");
                double voltage_a = getDoubleFromLongOctet(map.get(POWER_QUALITY.VoltageA.name()));
                log.debug("VOLTAGE_A[" + voltage_a + "]");
                double thd_a = getDoubleFromLongOctet(map.get(POWER_QUALITY.THD_A.name()));
                log.debug("THD_A[" + thd_a + "]");
                double pf_a = getDoubleFromLongOctet(map.get(POWER_QUALITY.PowerFactorA.name())) * 0.01;
                log.debug("POWER_FACTOR_A[" + pf_a + "]");
                double angle_a = getDoubleFromLongOctet(map.get(POWER_QUALITY.AngleA.name()));
                log.debug("ANGLE_A[" + angle_a + "]");
                
                double current_b = getDoubleFromLongOctet(map.get(POWER_QUALITY.CurrentB.name()));
                log.debug("CURRENT_B[" + current_b + "]");
                double voltage_b = getDoubleFromLongOctet(map.get(POWER_QUALITY.VoltageB.name()));
                log.debug("VOLTAGE_B[" + voltage_b + "]");
                double thd_b = getDoubleFromLongOctet(map.get(POWER_QUALITY.THD_B.name()));
                log.debug("THD_B[" + thd_b + "]");
                double pf_b = getDoubleFromLongOctet(map.get(POWER_QUALITY.PowerFactorB.name())) * 0.01;
                log.debug("POWER_FACTOR_B[" + pf_b + "]");
                double angle_b = getDoubleFromLongOctet(map.get(POWER_QUALITY.AngleB.name()));
                log.debug("ANGLE_B[" + angle_b + "]");
                
                double current_c = getDoubleFromLongOctet(map.get(POWER_QUALITY.CurrentC.name()));
                log.debug("CURRENT_C[" + current_c + "]");
                double voltage_c = getDoubleFromLongOctet(map.get(POWER_QUALITY.VoltageC.name()));
                log.debug("VOLTAGE_C[" + voltage_c + "]");
                double thd_c = getDoubleFromLongOctet(map.get(POWER_QUALITY.THD_C.name()));
                log.debug("THD_C[" + thd_c + "]");
                double pf_c = getDoubleFromLongOctet(map.get(POWER_QUALITY.PowerFactorC.name())) * 0.01;
                log.debug("POWER_FACTOR_C[" + pf_c + "]");
                double angle_c = getDoubleFromLongOctet(map.get(POWER_QUALITY.AngleC.name()));
                log.debug("ANGLE_C[" + angle_c + "]");
                
                double angle_ab = getDoubleFromLongOctet(map.get(POWER_QUALITY.VoltageAngleAB.name()));
                log.debug("VOLTAGE_ANGLE_AB[" + angle_ab + "]");
                double angle_ac = getDoubleFromLongOctet(map.get(POWER_QUALITY.VoltageAngleAC.name()));
                log.debug("VOLTAGE_ANGLE_AC[" + angle_ac + "]");
                
                Instrument pq = new Instrument();
                pq.setCURR_A(current_a);
                pq.setVOL_A(voltage_a);
                pq.setVOL_THD_A(thd_a);
                pq.setPF_A(pf_a);
                pq.setVOL_ANGLE_A(angle_a);
                
                pq.setCURR_B(current_b);
                pq.setVOL_B(voltage_b);
                pq.setVOL_THD_B(thd_b);
                pq.setPF_B(pf_b);
                pq.setVOL_ANGLE_B(angle_b);
                
                pq.setCURR_C(current_c);
                pq.setVOL_C(voltage_c);
                pq.setVOL_THD_C(thd_c);
                pq.setPF_C(pf_c);
                pq.setVOL_ANGLE_C(angle_c);
                
                pq.setLine_AB(angle_ab);
                pq.setLine_CA(angle_ac);
                
                return pq;
            }
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }
    
    public void setPreviousMaxDemand() {
        Map<String, Object> map = null;
        try {
            map = (Map<String, Object>) result
                    .get(OBIS.PREVIOUS_MAX_DEMAND.getCode());
            long t1PreActive = (Long) map.get(PREVIOUS_MAX_DEMAND.T1PreviousActive.name());
            log.debug("T1_PREVIOUS_ACTIVE[" + t1PreActive + "]");
            long t2PreActive = (Long) map.get(PREVIOUS_MAX_DEMAND.T2PreviousActive.name());
            log.debug("T2_PREVIOUS_ACTIVE[" + t2PreActive + "]");
            long t3PreActive = (Long) map.get(PREVIOUS_MAX_DEMAND.T3PreviousActive.name());
            log.debug("T3_PREVIOUS_ACTIVE[" + t3PreActive + "]");
            double previousDemandData = ((t1PreActive + t2PreActive + t3PreActive) / this.activePulseConstant);
            log.debug("PREVIOUS_DEMAND[" + previousDemandData + "]");
        } catch (Exception e) {
            log.error(e);
        }
    }

    public void setCurrentMaxDemand() {
        Map<String, Object> map = null;
        try {
            map = (Map<String, Object>) result
                    .get(OBIS.CURRENT_MAX_DEMAND.getCode());
            long t1CurActive = (Long) map.get(CURRENT_MAX_DEMAND.T1CurrentActive.name());
            long t2CurActive = (Long) map.get(CURRENT_MAX_DEMAND.T2CurrentActive.name());
            long t3CurActive = (Long) map.get(CURRENT_MAX_DEMAND.T3CurrentActive.name());
            double currentDemandData = (t1CurActive + t2CurActive + t3CurActive) / this.activePulseConstant;
            log.debug("CURRENT_DEMAND[" + currentDemandData + "]");
        } catch (Exception e) {
            log.error(e);
        }
    }
    
    public Map<String, Object> getPreviousMaxDemand() {
        return (Map<String, Object>) result.get(OBIS.PREVIOUS_MAX_DEMAND.getCode());
    }

    public Map<String, Object> getCurrentMaxDemand() {
        return (Map<String, Object>) result.get(OBIS.CURRENT_MAX_DEMAND.getCode());
    }
    
    public EventLogData[] getMeterEvent() {
        List<EventLogData> elist = new ArrayList<EventLogData>();
        Map<String, Object> sub = null;
        if ((sub = result.get(OBIS.POWER_FAILURE.getCode())) != null) {
            elist.addAll(makeEventLog(17, sub));
        }
        if ((sub = result.get(OBIS.POWER_RESTORE.getCode())) != null) {
            elist.addAll(makeEventLog(117, sub));
        }
        if ((sub = result.get(OBIS.SAG.getCode())) != null) {
            elist.addAll(makeEventLog(8, sub));
        }
        if ((sub = result.get(OBIS.SWELL.getCode())) != null) {
            elist.addAll(makeEventLog(108, sub));
        }
        
        return elist.toArray(new EventLogData[0]);
    }
    
    private List<EventLogData> makeEventLog(int flag, Map<String, Object> sub) {
        List<EventLogData> elist = new ArrayList<EventLogData>();
        String eventTime = null;
        for (int i = 0; ;i++) {
            eventTime = (String)sub.get(EVENT.EventTime.name()+"-"+i);
            if (eventTime == null)
                break;
            else {
                EventLogData e = new EventLogData();
                e.setDate(eventTime.substring(6, 14));
                e.setTime(eventTime.substring(14, 18)+"00");
                e.setFlag(flag);
                log.debug(e.toString());
                elist.add(e);
            }
        }
        return elist;
    }
    
    public Map<String, Object> getEventLog() {
        Map<String, Object> eventLogs = new LinkedHashMap<String, Object>();
        if (result.get(OBIS.POWER_FAILURE.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.POWER_FAILURE.getCode()));
        if (result.get(OBIS.POWER_RESTORE.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.POWER_RESTORE.getCode()));
        if (result.get(OBIS.TIME_CHANGE_FROM.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.TIME_CHANGE_FROM.getCode()));
        if (result.get(OBIS.TIME_CHANGE_TO.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.TIME_CHANGE_TO.getCode()));
        if (result.get(OBIS.DEMAND_RESET.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.DEMAND_RESET.getCode()));
        if (result.get(OBIS.MANUAL_DEMAND_RESET.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.MANUAL_DEMAND_RESET.getCode()));
        if (result.get(OBIS.SELF_READ.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.SELF_READ.getCode()));
        if (result.get(OBIS.PROGRAM_CHANGE.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.PROGRAM_CHANGE.getCode()));
        
        return eventLogs;
    }
    
    private double getDoubleFromLongOctet(Object value) throws Exception {
        if (value instanceof Float) return ((Float)value).doubleValue();
        else if (value instanceof Long) return ((Long)value).doubleValue();
    	else if (value instanceof OCTET)
			return DataUtil.getFloat(((OCTET)value).getValue(), 0);
    	
    	return 0;
    }
    
    public BillingData[] getCurrentMonthly() {
    	BillingData[] billEm = new BillingData[2];
    	
    	try {
	    	Map<String, Object> map = (Map<String, Object>)result.get(OBIS.METER_TIME.getCode());
	    	billEm[0] = new BillingData();
	    	Object obj = map.get("MeterTime");
	    	if (obj != null) {
	    		meterTime = (String)obj;
	    		billEm[0].setBillingTimestamp(meterTime+"00");
	    		
	    		map = (Map<String, Object>) result.get(OBIS.MONTHLY_ENERGY_PROFILE.getCode());
	    		if (map != null) {
		    		// 전체 순방향 유효전력량
		    		Object value = map.get(MONTHLY_ENERGY_PROFILE.ActiveEnergy.name());
		    		billEm[0].setActiveEnergyImportRateTotal(getDoubleFromLongOctet(value) / this.activePulseConstant);
		    		
		    		// 전체 순방향 피상전력량을 무효전력량
		    		value = map.get(MONTHLY_ENERGY_PROFILE.ApparentEnergy.name());
		    		billEm[0].setCummkVah1RateTotal(getDoubleFromLongOctet(value) / this.activePulseConstant);
		    		// 전체 순방향 지상 무효전력량을 
		    		value = map.get(MONTHLY_ENERGY_PROFILE.LaggingReactiveEnergy.name());
		    		billEm[0].setReactiveEnergyLagImportRateTotal(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
		    		// 전체 순방향 진상 무효전력량
		    		value = map.get(MONTHLY_ENERGY_PROFILE.LeadingReactiveEnergy.name());
		    		billEm[0].setReactiveEnergyLeadImportRateTotal(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
		    		// 전체 순방향 평균 역률
		    		value = map.get(MONTHLY_ENERGY_PROFILE.AveragePowerFactor.name());
		    		billEm[0].setPf(getDoubleFromLongOctet(value));
		    		
		    		// T1 순방향 유효전력량
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T1ActiveEnergy.name());
		    		billEm[0].setActiveEnergyImportRate1(getDoubleFromLongOctet(value) / this.activePulseConstant);
		    		// T1 순방향 피상전력량을 무효전력량
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T1ApparentEnergy.name());
		    		billEm[0].setCummkVah1Rate1(getDoubleFromLongOctet(value) / this.activePulseConstant);
		    		// T1 순방향 지상 무효전력량을 
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T1LaggingReactiveEnergy.name());
		    		billEm[0].setReactiveEnergyLagImportRate1(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
		    		// T1 순방향 진상 무효전력량
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T1LeadingReactiveEnergy.name());
		    		billEm[0].setReactiveEnergyLeadImportRate1(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
		    		
		    		// T2 순방향 유효전력량
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T2ActiveEnergy.name());
		    		billEm[0].setActiveEnergyImportRate2(getDoubleFromLongOctet(value) / this.activePulseConstant);
		    		// T2 순방향 피상전력량을 무효전력량
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T2ApparentEnergy.name());
		    		billEm[0].setCummkVah1Rate2(getDoubleFromLongOctet(value) / this.activePulseConstant);
		    		// T2 순방향 지상 무효전력량을 
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T2LaggingReactiveEnergy.name());
		    		billEm[0].setReactiveEnergyLagImportRate2(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
		    		// T2 순방향 진상 무효전력량
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T2LeadingReactiveEnergy.name());
		    		billEm[0].setReactiveEnergyLeadImportRate2(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
		    		
		    		// T3 순방향 유효전력량
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T3ActiveEnergy.name());
		    		billEm[0].setActiveEnergyImportRate3(getDoubleFromLongOctet(value) / this.activePulseConstant);
		    		// T3 순방향 피상전력량을 무효전력량
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T3ApparentEnergy.name());
		    		billEm[0].setCummkVah1Rate3(getDoubleFromLongOctet(value) / this.activePulseConstant);
		    		// T3 순방향 지상 무효전력량을 
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T3LaggingReactiveEnergy.name());
		    		billEm[0].setReactiveEnergyLagImportRate3(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
		    		// T3 순방향 진상 무효전력량
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T3LeadingReactiveEnergy.name());
		    		billEm[0].setReactiveEnergyLeadImportRate3(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
		    		
		    		// T4 순방향 유효전력량
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T4ActiveEnergy.name());
		    		billEm[0].setActiveEnergyImportRate4(getDoubleFromLongOctet(value) / this.activePulseConstant);
		    		// T4 순방향 피상전력량을 무효전력량
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T4ApparentEnergy.name());
		    		billEm[0].setCummkVah1Rate4(getDoubleFromLongOctet(value) / this.activePulseConstant);
		    		// T4 순방향 지상 무효전력량을 
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T4LaggingReactiveEnergy.name());
		    		billEm[0].setReactiveEnergyLagImportRate4(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
		    		// T4 순방향 진상 무효전력량
		    		value = map.get(MONTHLY_ENERGY_PROFILE.T4LeadingReactiveEnergy.name());
		    		billEm[0].setReactiveEnergyLeadImportRate4(getDoubleFromLongOctet(value) / this.reActivePulseConstant);
	    		}
	    		
	    		map = (Map<String, Object>) result.get(OBIS.MONTHLY_DEMAND_PROFILE.getCode());
	    		if (map != null && map.size() > 0) {
	    			Object value = map.get(MONTHLY_DEMAND_PROFILE.Active.name());
	    			if (value != null) {
	    				billEm[0].setActivePwrDmdMaxImportRateTotal(getDoubleFromLongOctet(value) / this.activePulseConstant);
	    				billEm[0].setActivePwrDmdMaxTimeImportRateTotal(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.ActiveDate.name())));
	    				billEm[0].setCummActivePwrDmdMaxImportRateTotal(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.CummlativeActive.name())) / this.activePulseConstant);
	    				billEm[0].setMaxDmdkVah1RateTotal(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.Apparent.name())) / this.activePulseConstant);
	    				billEm[0].setMaxDmdkVah1TimeRateTotal(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.ApparentDate.name())));
	    				billEm[0].setCummkVah1RateTotal(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.CummlativeApparent.name())) / this.activePulseConstant);
	    			}
	    			value = map.get(MONTHLY_DEMAND_PROFILE.T1Active.name());
	    			if (value != null) {
	    				billEm[0].setActivePwrDmdMaxImportRate1(getDoubleFromLongOctet(value) / this.activePulseConstant);
	    				billEm[0].setActivePwrDmdMaxTimeImportRate1(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.T1ActiveDate.name())));
	    				billEm[0].setCummActivePwrDmdMaxImportRate1(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T1CummlativeActive.name())) / this.activePulseConstant);
	    				billEm[0].setMaxDmdkVah1Rate1(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T1Apparent.name())) / this.activePulseConstant);
	    				billEm[0].setMaxDmdkVah1TimeRate1(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.T1ApparentDate.name())));
	    				billEm[0].setCummkVah1Rate1(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T1CummlativeApparent.name())) / this.activePulseConstant);
	    			}
	    			value = map.get(MONTHLY_DEMAND_PROFILE.T2Active.name());
	    			if (value != null) {
	    				billEm[0].setActivePwrDmdMaxImportRate2(getDoubleFromLongOctet(value) / this.activePulseConstant);
	    				billEm[0].setActivePwrDmdMaxTimeImportRate2(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.T2ActiveDate.name())));
	    				billEm[0].setCummActivePwrDmdMaxImportRate2(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T2CummlativeActive.name())) / this.activePulseConstant);
	    				billEm[0].setMaxDmdkVah1Rate2(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T2Apparent.name())) / this.activePulseConstant);
	    				billEm[0].setMaxDmdkVah1TimeRate2(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.T2ApparentDate.name())));
	    				billEm[0].setCummkVah1Rate2(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T2CummlativeApparent.name())) / this.activePulseConstant);
	    			}
	    			value = map.get(MONTHLY_DEMAND_PROFILE.T3Active.name());
	    			if (value != null) {
	    				billEm[0].setActivePwrDmdMaxImportRate3(getDoubleFromLongOctet(value) / this.activePulseConstant);
	    				billEm[0].setActivePwrDmdMaxTimeImportRate3(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.T3ActiveDate.name())));
	    				billEm[0].setCummActivePwrDmdMaxImportRate3(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T3CummlativeActive.name())) / this.activePulseConstant);
	    				billEm[0].setMaxDmdkVah1Rate3(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T3Apparent.name())) / this.activePulseConstant);
	    				billEm[0].setMaxDmdkVah1TimeRate3(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.T3ApparentDate.name())));
	    				billEm[0].setCummkVah1Rate3(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T3CummlativeApparent.name()))/ this.activePulseConstant);
	    			}
	    			value = map.get(MONTHLY_DEMAND_PROFILE.T4Active.name());
	    			if (value != null) {
	    				billEm[0].setActivePwrDmdMaxImportRate4(getDoubleFromLongOctet(value) / this.activePulseConstant);
	    				billEm[0].setActivePwrDmdMaxTimeImportRate4(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.T4ActiveDate.name())));
	    				billEm[0].setCummActivePwrDmdMaxImportRate4(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T4CummlativeActive.name())) / this.activePulseConstant);
	    				billEm[0].setMaxDmdkVah1Rate4(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T4Apparent.name())) / this.activePulseConstant);
	    				billEm[0].setMaxDmdkVah1TimeRate4(checkDate((String)map.get(MONTHLY_DEMAND_PROFILE.T4ApparentDate.name())));
	    				billEm[0].setCummkVah1Rate4(getDoubleFromLongOctet(map.get(MONTHLY_DEMAND_PROFILE.T4CummlativeApparent.name())) / this.activePulseConstant);
	    			}
	    		}
	    	}
	    	
	    	if (meterTime != null) {
	    		billEm[1] = new BillingData();
	    		billEm[1].setBillingTimestamp(meterTime);
	    		// 익월 1일로 변경한다.
	    		Calendar cal = Calendar.getInstance();
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	    		cal.setTime(sdf.parse(billEm[1].getBillingTimestamp()));
	    		cal.add(Calendar.MONTH, 1);
	    		billEm[1].setBillingTimestamp(sdf.format(cal.getTime()).substring(0, 6) + "01");
	    		
	    		LPData lastLP = lpData[lpData.length - 1];
	    		// 전체 순방향 유효전력량
	    		billEm[1].setActiveEnergyImportRateTotal(lastLP.getCh()[1]);
	    		billEm[1].setActiveEnergyRateTotal(lastLP.getCh()[1]);
	    		
	    		// 전체 순방향 피상전력량을 무효전력량
	    		billEm[1].setCummkVah1RateTotal(lastLP.getCh()[4]);
	    		// 전체 순방향 지상 무효전력량을 
	    		billEm[1].setReactiveEnergyLagImportRateTotal(lastLP.getCh()[2]);
	    		// 전체 순방향 진상 무효전력량
	    		billEm[1].setReactiveEnergyLeadImportRateTotal(lastLP.getCh()[3]);
	    		if (lastLP.getCh().length == 5) {
	    		    billEm[1].setActiveEnergyExportRateTotal(lastLP.getCh()[4]);
	    		}
	    		
	    		// 15분 사용량에 4를 곱하면 수요 전력으로 변경됨.
				billEm[1].setActivePwrDmdMaxImportRateTotal(maxdemand);
				billEm[1].setActivePwrDmdMaxTimeImportRateTotal(maxdemandTime);
	    	}
    	}
    	catch (Exception e) {
    		log.warn(e, e);
    	}
    	return billEm;
    }
    
    private String checkDate(String date) {
    	if (date != null && date.contains(":date"))
    		return date.substring(6);
    	return date;
    }
    
    public void setMeteringValue() {
        try {
            Map<String, Object> map = (Map<String, Object>) result
                    .get(OBIS.MONTHLY_ENERGY_PROFILE.getCode());
            if (map != null) {
	            long active = (Long) map
	                    .get(MONTHLY_ENERGY_PROFILE.ActiveEnergy.name());
	            BigDecimal bd = new BigDecimal(active);
	            meteringValue=bd.doubleValue()/ this.activePulseConstant;
	            meteringValue= meteringValue*getCt();
            }
            else {
            	// 현월 누적 사용량을 가져오지 않는 경우 LP의 마지막 누적 사용량을 가져온다.
            	if (lpData != null && lpData.length > 0)
            		meteringValue = lpData[lpData.length - 1].getCh()[1];
            	else
            		meteringValue = 0.0;
            }
            log.debug("METERING_VALUE[" + meteringValue + "]");
        } catch (Exception e) {
            log.error(e, e);
        }
    }
    
    public LinkedHashMap<String, Object> getRelayStatus() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        Map<String, Object> loadControlMap = result.get(OBIS.LOAD_CONTROL_STATUS.getCode());
        Map<String, Object> outputSignalMap = result.get(OBIS.OUTPUT_SIGNAL.getCode());
        
        log.debug("LoadControlStatus : "+ loadControlMap.get("LoadControlStatus"));
        log.debug("OutSignal : "+outputSignalMap.get("OutputSignal"));
        
        map.put("LoadControlStatus", loadControlMap.get("LoadControlStatus"));
        map.put("OutputSignal", outputSignalMap.get("OutputSignal"));
        return map;
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

    public Double getReActivePulseConstant() {
        return this.reActivePulseConstant;
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
