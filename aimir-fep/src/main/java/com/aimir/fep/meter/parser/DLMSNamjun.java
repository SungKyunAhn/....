package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.DLMSKepcoTable.LPComparator;
import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSTable;
import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSVARIABLE;
import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSVARIABLE.OBIS;

import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

public class DLMSNamjun extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 5198091223934578776L;

	private static Log log = LogFactory.getLog(DLMSNamjun.class);

	LPData[] lpData = null;
	
	Map<String, Object> data = new LinkedHashMap<String, Object>(16, 0.75f, false);
    LinkedHashMap<String, Map<String, Object>> result = 
            new LinkedHashMap<String, Map<String, Object>>();

    String meterID = "";
    String fwVersion = "";
    String meterModel = "";
    String logicalNumber = "";
    String manufactureSerial = "";
    String servicePointSerial = "";
    Long ct_num = 0L;
    Long vt_num = 0L;
    Long ct_den = 0L;
    Long vt_den = 0L;
    Long trans_num = 0L;
    byte[] phaseType = null;
    byte[] meterStatus = null;
    
    int meterActiveConstant = 1;
    int meterReActiveConstant = 1;

    double activePulseConstant = 1;
    double reActivePulseConstant = 1;
    
    public BillingData getCurrBill() {
		return currBill;
	}

	public BillingData getPreviousMonthBill() {
		return previousMonthBill;
	}

	private BillingData currBill = null;
	
	private BillingData previousMonthBill = null;
    
    Long ctRatio = 1L;
    
    int lpInterval=30;
    
    Double meteringValue= 0d;
    Double ct = 1d;

    @Override
    public LinkedHashMap<String, Map<String, Object>> getData() {
        
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
        
    	String clock = meterTime;
		try {
			clock = datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
        data.put("Meter Clock", clock);

        for (Iterator i = result.keySet().iterator(); i.hasNext(); ) {
            key = (String)i.next();
            resultSubData = result.get(key);

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
                                    data.put(getKey(OBIS.getObis(key).getName(),idx,subkey), datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(((String)subvalue).substring(6)+"00")));
                                }
                                catch (Exception e) {
                                    data.put(getKey(OBIS.getObis(key).getName(),idx,subkey), subvalue);
                                }
                            }
                            else if (subkey.contains("Date") && !((String)subvalue).contains(":date=") && ((String)subvalue).length()==12) {
                                try {
                                    data.put(getKey(OBIS.getObis(key).getName(),idx,subkey), datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(subvalue+"00")));
                                }
                                catch (Exception e) {
                                    data.put(getKey(OBIS.getObis(key).getName(),idx,subkey), subvalue);
                                }
                            }
                        }
                        else if (subvalue instanceof Number) {
                        	
                        	if(subvalue instanceof Long 
                        			&& !OBIS.getObis(key).getName().endsWith("Number") 
                        			&& !OBIS.getObis(key).getName().endsWith("Den")
                        			&& !OBIS.getObis(key).getName().endsWith("num)")
                        			&& !subkey.equals("LpInterval")){
                        		data.put(getKey(OBIS.getObis(key).getName(),idx,subkey), decimalf.format(new Double(((Long)subvalue)*0.001)));                               
                        	}else{
                                data.put(getKey(OBIS.getObis(key).getName(),idx,subkey), decimalf.format(subvalue));
                        	}

                        }else if(subvalue instanceof OCTET){
                        	data.put(getKey(OBIS.getObis(key).getName(),idx,subkey), ((OCTET)subvalue).toHexString());
                        }
                        else data.put(getKey(OBIS.getObis(key).getName(),idx,subkey), subvalue);
                    }
                }
            }
        }

        return (LinkedHashMap)data;
    }
    
    
    private String getKey(String mainKey, String idx, String subKey){

    	if(mainKey.equals(subKey) && "".equals(idx)){
    		return mainKey+idx;
    	}else{
    		return mainKey+idx+" : "+subKey;
    	}
    }

    @Override
    public void parse(byte[] data) throws Exception {
        // log.debug("DLMS parse:"+Hex.decode(data));

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
            
            log.debug("TAGDATA=["+Hex.decode(TAGDATA)+"]");
            
            dlms.parseDlmsTag(TAGDATA);
            // 동일한 obis 코드를 가진 값이 있을 수 있기 때문에 검사해서 _number를 붙인다.
            if (dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.ENERGY_LOAD_PROFILE) {
            	
            	/*
	        	Map tempMap = dlms.getData();
            	if(tempMap.containsKey("LpInterval")){
    	        	Object obj = tempMap.get("LpInterval");
    	        	if (obj != null) lpInterval = ((Long)obj).intValue()/60; //sec -> min
    	        	log.debug("LP_INTERVAL[" + lpInterval + "]");

            	}
            	*/
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

        EnergyMeter meter = (EnergyMeter)this.getMeter();
        
        this.ct = 1.0;
        if (meter != null && meter.getCt() != null && meter.getCt() > 0){
            ct = meter.getCt();
            if(meter.getLpInterval() != null && meter.getLpInterval() > 0){
            	lpInterval = meter.getLpInterval();
            }            
        }
        
        setCt(ct);
        
        setMeterInfo();
        setLPData();
        setPrevBillingData();
        setDailyBillingData();
        setCurrBillingData();
        
        //for(String str : result.keySet()){
        //	log.debug("Key:"+str);
        //}
    }

    private void setDailyBillingData() {
		// TODO Auto-generated method stub
		
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
                while ((value=lpMap.get("Channel[1]"+"-"+cnt)) != null) {
                	
                	log.debug("ActiveEnergyImport VALUE RAWDATA="+value.toString());
                	if (value instanceof OCTET)
                		lp = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                	else if (value instanceof Long)
                		lp = ((Long)value).doubleValue();
                	else if (value instanceof Float)
                		lp = ((Float)value).doubleValue();
                	
                	lp *= 0.001;

                    lpValue = lp / activePulseConstant;
                    lpValue = lpValue*ct;
                    
                    activeEnergyImport = lpValue;
                    value = lpMap.get("Channel[2]"+"-"+cnt);
                    if (value != null) {
                    	if (value instanceof OCTET)
                    		activeEnergyExport = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Long)
                    		activeEnergyExport = ((Long)value).doubleValue();
                    	else if (value instanceof Float)
                    		activeEnergyExport = ((Float)value).doubleValue();
                    	
                    	activeEnergyExport /= activePulseConstant;
                    	activeEnergyExport *= ct;
                    	activeEnergyExport *= 0.001;
                    }
                    value = lpMap.get("Channel[3]"+"-"+cnt);
                    if (value != null) {
                    	if (value instanceof OCTET)
                    		reactiveEnergyImport = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Long)
                    		reactiveEnergyImport = ((Long)value).doubleValue();
                    	else if (value instanceof Float)
                    		reactiveEnergyImport = ((Float)value).doubleValue();
                    	reactiveEnergyImport /= reActivePulseConstant;
                    	reactiveEnergyImport *= ct;
                    	reactiveEnergyImport *= 0.001;
                    }
                    value = lpMap.get("Channel[4]"+"-"+cnt);
                    if (value != null) {
                    	if (value instanceof OCTET)
                    		reactiveEnergyExport = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Long)
                    		reactiveEnergyExport = ((Long)value).doubleValue();
                    	else if (value instanceof Float)
                    		reactiveEnergyExport = ((Float)value).doubleValue();
                    	reactiveEnergyExport /= reActivePulseConstant;
                    	reactiveEnergyExport *= ct;
                    	reactiveEnergyExport *= 0.001;
                    }
                    
                    String lpTime = (String) lpMap.get("DateTime"+ "-" + cnt++);
                    lpTime = Util.addMinYymmdd(lpTime, -(this.meter.getLpInterval()));
                    // 미터에서 LP시간이 00시00분부터 00시15분 사이의 데이터는 00시 15분으로 저장하기 때문에
                    //서버에서는 해당 기간의 사용데이터는 00시 00분으로 계산하므로 주기만큼 빼야함.
                    
                    _lpData = new LPData(lpTime, lp, lpValue);
                    _lpData.setCh(new Double[]{activeEnergyImport, activeEnergyExport, reactiveEnergyImport, reactiveEnergyExport});
                    _lpData.setPF(1d);
                    
                    if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
                    	lpDataList.add(_lpData);
                    	log.debug(_lpData.toString());
                    }
                }
            }
            
            Collections.sort(lpDataList,LPComparator.TIMESTAMP_ORDER);        
            lpData = checkEmptyLP(lpDataList);
            
            //lpData = lpDataList.toArray(new LPData[0]);
            log.debug("########################lpData.length:"+lpData.length);
        } catch (Exception e) {
            log.error(e);
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
                            data.setDatetime(Util.addMinYymmdd(prevTime, lpInterval*(i+1)));
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

	        	data.put("Meter Serial", meterID);
            }
            map = (Map<String, Object>)result.get(OBIS.METER_TIME.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.METER_TIME.getName());
	        	if (obj != null) meterTime = (String)obj;
	        	if(meterTime.length() != 14){
	        		meterTime = meterTime+"00";
	        	}	            
	        	//data.put("Meter Clock", meterTime);
	        	log.debug("METER_TIME[" + meterTime + "]");
            }        
            map = (Map<String, Object>)result.get(OBIS.MANUFACTURE_SERIAL.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.MANUFACTURE_SERIAL.getName());
	        	if (obj != null) manufactureSerial = (String)obj;
	        	log.debug("MANUFACTURE_SERIAL[" + manufactureSerial + "]");
            } 
            map = (Map<String, Object>)result.get(OBIS.SERVICEPOINT_SERIAL.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.SERVICEPOINT_SERIAL.getName());
	        	if (obj != null) servicePointSerial = (String)obj;
	        	log.debug("SERVICEPOINT_SERIAL[" + servicePointSerial + "]");
            }         
            map = (Map<String, Object>)result.get(OBIS.FW_VERSION1.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.FW_VERSION1.getName());
	        	if (obj != null) fwVersion = (String)obj;
	        	log.debug("FW_VERSION[" + fwVersion + "]");
            }  
            map = (Map<String, Object>)result.get(OBIS.FW_VERSION2.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.FW_VERSION2.getName());
	        	if (obj != null) fwVersion = (String)obj;
	        	log.debug("FW_VERSION[" + fwVersion + "]");
            } 
            map = (Map<String, Object>)result.get(OBIS.METER_MODEL.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.METER_MODEL.getName());
	        	if (obj != null) meterModel = (String)obj;
	        	log.debug("METER_MODEL[" + meterModel + "]");
            }
            map = (Map<String, Object>)result.get(OBIS.LOGICAL_NUMBER.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.LOGICAL_NUMBER.getName());
	        	if (obj != null) logicalNumber = (String)obj;
	        	log.debug("LOGICAL_NUMBER[" + logicalNumber + "]");
            }
            map = (Map<String, Object>)result.get(OBIS.CT_RATIO_NUM.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.CT_RATIO_NUM.getName());
	        	if (obj != null) ct_num = (Long)obj;
	        	log.debug("CT_RATIO_NUM[" + ct_num + "]");
            }
            map = (Map<String, Object>)result.get(OBIS.VT_RATIO_NUM.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.VT_RATIO_NUM.getName());
	        	if (obj != null) vt_num = (Long)obj;
	        	log.debug("VT_RATIO_NUM[" + vt_num + "]");
            }            
            map = (Map<String, Object>)result.get(OBIS.CT_RATIO_DEN.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.CT_RATIO_DEN.getName());
	        	if (obj != null) ct_den = (Long)obj;
	        	log.debug("CT_RATIO_DEN[" + ct_den + "]");
            }
            map = (Map<String, Object>)result.get(OBIS.VT_RATIO_DEN.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.VT_RATIO_DEN.getName());
	        	if (obj != null) vt_den = (Long)obj;
	        	log.debug("VT_RATIO_DEN[" + vt_den + "]");
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
            map = (Map<String, Object>)result.get(OBIS.METER_STATUS.getCode());
            if (map != null) {
	        	Object obj = map.get(OBIS.METER_STATUS.getName());
	        	if (obj != null) meterStatus = (byte[])obj;
	        	log.debug("METER_STATUS[" + Hex.decode(meterStatus) + "]");
            }
            
        } catch (Exception e) {
            log.error(e,e);
        }
    }
    
    public Instrument[] getPowerQuality(){
    	
    	Instrument instrument = new Instrument();
    	
    	instrument.setDatetime(meterTime);

        if (result.get(OBIS.VOLTAGE_L1.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.VOLTAGE_L1.getCode()).get(OBIS.VOLTAGE_L1.getName());
            if (obj != null) {       
            	if(obj instanceof Long){
            		instrument.setVOL_A(((Long) obj).doubleValue()*0.001);
            	}else if(obj instanceof Float){
            		instrument.setVOL_A(((Float) obj).doubleValue());
            	}
                log.debug("VOLTAGE_L1[" + instrument.getVOL_A() + "]");
            }
        }
        if (result.get(OBIS.VOLTAGE_L2.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.VOLTAGE_L2.getCode()).get(OBIS.VOLTAGE_L2.getName());
            if (obj != null) {
            	if(obj instanceof Long){
            		instrument.setVOL_B(((Long) obj).doubleValue()*0.001);
            	}else if(obj instanceof Float){
            		instrument.setVOL_B(((Float) obj).doubleValue());
            	}
                log.debug("VOLTAGE_L2[" + instrument.getVOL_B() + "]");
            }
        }
        if (result.get(OBIS.VOLTAGE_L3.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.VOLTAGE_L3.getCode()).get(OBIS.VOLTAGE_L3.getName());
            if (obj != null) {    
            	if(obj instanceof Long){
            		instrument.setVOL_C(((Long) obj).doubleValue()*0.001);
            	}else if(obj instanceof Float){
            		instrument.setVOL_C(((Float) obj).doubleValue());
            	}            	
                log.debug("VOLTAGE_L3[" + instrument.getVOL_C() + "]");
            }
        }           
        if (result.get(OBIS.CURRENT_L1.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CURRENT_L1.getCode()).get(OBIS.CURRENT_L1.getName());
            if (obj != null) {    
            	if(obj instanceof Long){
            		instrument.setCURR_A(((Long) obj).doubleValue()*0.001);
            	}else if(obj instanceof Float){
            		instrument.setCURR_A(((Float) obj).doubleValue());
            	}           
                log.debug("CURRENT_L1[" + instrument.getCURR_A() + "]");
            }
        }
        if (result.get(OBIS.CURRENT_L2.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CURRENT_L2.getCode()).get(OBIS.CURRENT_L2.getName());
            if (obj != null) {    
            	if(obj instanceof Long){
            		instrument.setCURR_B(((Long) obj).doubleValue()*0.001);
            	}else if(obj instanceof Float){
            		instrument.setCURR_B(((Float) obj).doubleValue());
            	}   
                log.debug("CURRENT_L2[" + instrument.getCURR_B() + "]");
            }
        }
        if (result.get(OBIS.CURRENT_L3.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CURRENT_L3.getCode()).get(OBIS.CURRENT_L3.getName());
            if (obj != null) { 
            	if(obj instanceof Long){
            		instrument.setCURR_C(((Long) obj).doubleValue()*0.001);
            	}else if(obj instanceof Float){
            		instrument.setCURR_C(((Float) obj).doubleValue());
            	}             	
                log.debug("CURRENT_L3[" + instrument.getCURR_C() + "]");
            }
        }        
        
        if (result.get(OBIS.POWER_FACTOR_L1.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.POWER_FACTOR_L1.getCode()).get(OBIS.POWER_FACTOR_L1.getName());
            if (obj != null) { 
            	if(obj instanceof Long){
            		instrument.setPF_A(((Long) obj).doubleValue()*0.001);
            	}else if(obj instanceof Float){
            		instrument.setPF_A(((Float) obj).doubleValue());
            	}             	
                log.debug("POWER_FACTOR_L1[" + instrument.getPF_A() + "]");
            }
        }
        
        if (result.get(OBIS.POWER_FACTOR_L2.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.POWER_FACTOR_L2.getCode()).get(OBIS.POWER_FACTOR_L2.getName());
            if (obj != null) { 
            	if(obj instanceof Long){
            		instrument.setPF_B(((Long) obj).doubleValue()*0.001);
            	}else if(obj instanceof Float){
            		instrument.setPF_B(((Float) obj).doubleValue());
            	}             	
                log.debug("POWER_FACTOR_L2[" + instrument.getPF_B() + "]");
            }
        }
        
        if (result.get(OBIS.POWER_FACTOR_L3.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.POWER_FACTOR_L3.getCode()).get(OBIS.POWER_FACTOR_L3.getName());
            if (obj != null) { 
            	if(obj instanceof Long){
            		instrument.setPF_C(((Long) obj).doubleValue()*0.001);
            	}else if(obj instanceof Float){
            		instrument.setPF_C(((Float) obj).doubleValue());
            	}             	
                log.debug("POWER_FACTOR_L3[" + instrument.getPF_C() + "]");
            }
        }
        
    	return (new Instrument[]{instrument});
    }
    
    public Map<String, Object> getEventLog() {
        Map<String, Object> eventLogs = new LinkedHashMap<String, Object>();
        if (result.get(OBIS.POWER_FAILURE.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.POWER_FAILURE.getCode()));
        if (result.get(OBIS.POWER_RESTORE.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.POWER_RESTORE.getCode()));
        if (result.get(OBIS.TIME_CHANGE_BEFORE.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.TIME_CHANGE_BEFORE.getCode()));
        if (result.get(OBIS.TIME_CHANGE_AFTER.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.TIME_CHANGE_AFTER.getCode()));
        if (result.get(OBIS.MANUAL_DEMAND_RESET.getCode()) != null)
            eventLogs.putAll(result.get(OBIS.MANUAL_DEMAND_RESET.getCode()));
        //if (result.get(OBIS.SELF_READ.getCode()) != null)
        //    eventLogs.putAll(result.get(OBIS.SELF_READ.getCode()));
        //if (result.get(OBIS.PROGRAM_CHANGE.getCode()) != null)
        //    eventLogs.putAll(result.get(OBIS.PROGRAM_CHANGE.getCode()));
        
        return eventLogs;
    }    
    
    public void setPrevBillingData(){
    	
    	this.previousMonthBill = new BillingData();
        if (result.get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT1.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT1.getCode()).get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT1.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
            	this.previousMonthBill.setActiveEnergyImportRateTotal(val);
                log.debug("LASTMONTH_ACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT2.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT2.getCode()).get(OBIS.LASTMONTH_ACTIVEENERGY_IMPORT2.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
            	
            	this.previousMonthBill.setActiveEnergyImportRateTotal(val);
                log.debug("LASTMONTH_ACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT1.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT1.getCode()).get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT1.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
            	
            	this.previousMonthBill.setActiveEnergyExportRateTotal(val);
                log.debug("LASTMONTH_ACTIVEENERGY_EXPORT[" + val + "]");
            }
        }   
        if (result.get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT2.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT2.getCode()).get(OBIS.LASTMONTH_ACTIVEENERGY_EXPORT2.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
            	
            	this.previousMonthBill.setActiveEnergyExportRateTotal(val);
                log.debug("LASTMONTH_ACTIVEENERGY_EXPORT[" + val + "]");
            }
        } 
        if (result.get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT1.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT1.getCode()).get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT1.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("LASTMONTH_REACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT2.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT2.getCode()).get(OBIS.LASTMONTH_REACTIVEENERGY_IMPORT2.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("LASTMONTH_REACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT1.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT1.getCode()).get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT1.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("LASTMONTH_REACTIVEENERGY_EXPORT[" + val + "]");
            }
        }  
        if (result.get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT2.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT2.getCode()).get(OBIS.LASTMONTH_REACTIVEENERGY_EXPORT2.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
                log.debug("LASTMONTH_REACTIVEENERGY_EXPORT[" + val + "]");
            }
        }
    }    
    
    public void setCurrBillingData(){    	
    	
    	this.currBill = new BillingData();
    	
        if (result.get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT1.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT1.getCode()).get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT1.getName());
            if (obj != null) {     
            	double val = 0d;
            	if(obj instanceof Long){
            		val = ((Long) obj).doubleValue()*getCt()*0.001;
            	}else if(obj instanceof Float){
            		val = ((Float) obj).doubleValue()*getCt();
            	}            	
                meteringValue= val;                

                this.currBill.setActiveEnergyImportRateTotal(val);
                log.debug("METERING_VALUE[" + meteringValue + "]");        
                log.debug("CUMULATIVE_ACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT2.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT2.getCode()).get(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT2.getName());
            if (obj != null) {     
            	double val = 0d;
            	if(obj instanceof Long){
            		val = ((Long) obj).doubleValue()*getCt()*0.001;
            	}else if(obj instanceof Float){
            		val = ((Float) obj).doubleValue()*getCt();
            	}            	
                meteringValue= val;                

                this.currBill.setActiveEnergyImportRateTotal(val);
                log.debug("METERING_VALUE[" + meteringValue + "]");        
                log.debug("CUMULATIVE_ACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT1.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT1.getCode()).get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT1.getName());
            if (obj != null) {            	
            	double val = 0d;
            	if(obj instanceof Long){
            		val = ((Long) obj).doubleValue()*getCt()*0.001;
            	}else if(obj instanceof Float){
            		val = ((Float) obj).doubleValue()*getCt();
            	}  
                this.currBill.setActiveEnergyExportRateTotal(val);
                log.debug("CUMULATIVE_ACTIVEENERGY_EXPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT2.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT2.getCode()).get(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT2.getName());
            if (obj != null) {            	
            	double val = 0d;
            	if(obj instanceof Long){
            		val = ((Long) obj).doubleValue()*getCt()*0.001;
            	}else if(obj instanceof Float){
            		val = ((Float) obj).doubleValue()*getCt();
            	}  
                this.currBill.setActiveEnergyExportRateTotal(val);
                log.debug("CUMULATIVE_ACTIVEENERGY_EXPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT1.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT1.getCode()).get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT1.getName());
            if (obj != null) {            	
            	double val = 0d;
            	if(obj instanceof Long){
            		val = ((Long) obj).doubleValue()*getCt()*0.001;
            	}else if(obj instanceof Float){
            		val = ((Float) obj).doubleValue()*getCt();
            	}  
                log.debug("CUMULATIVE_REACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT2.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT2.getCode()).get(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT2.getName());
            if (obj != null) {            	
            	double val = 0d;
            	if(obj instanceof Long){
            		val = ((Long) obj).doubleValue()*getCt()*0.001;
            	}else if(obj instanceof Float){
            		val = ((Float) obj).doubleValue()*getCt();
            	}  
                log.debug("CUMULATIVE_REACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT1.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT1.getCode()).get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT1.getName());
            if (obj != null) {            	
            	double val = 0d;
            	if(obj instanceof Long){
            		val = ((Long) obj).doubleValue()*getCt()*0.001;
            	}else if(obj instanceof Float){
            		val = ((Float) obj).doubleValue()*getCt();
            	}  
                log.debug("CUMULATIVE_REACTIVEENERGY_EXPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT2.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT2.getCode()).get(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT2.getName());
            if (obj != null) {            	
            	double val = 0d;
            	if(obj instanceof Long){
            		val = ((Long) obj).doubleValue()*getCt()*0.001;
            	}else if(obj instanceof Float){
            		val = ((Float) obj).doubleValue()*getCt();
            	}  
                log.debug("CUMULATIVE_REACTIVEENERGY_EXPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.TOTAL_ACTIVEENERGY_IMPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_ACTIVEENERGY_IMPORT.getCode()).get(OBIS.TOTAL_ACTIVEENERGY_IMPORT.getName());
            if (obj != null) {            	
            	double val = 0d;
            	if(obj instanceof Long){
            		val = ((Long) obj).doubleValue()*getCt()*0.001;
            	}else if(obj instanceof Float){
            		val = ((Float) obj).doubleValue()*getCt();
            	}            	
                log.debug("TOTAL_ACTIVEENERGY_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.TOTAL_ACTIVEENERGY_EXPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_ACTIVEENERGY_EXPORT.getCode()).get(OBIS.TOTAL_ACTIVEENERGY_EXPORT.getName());
            if (obj != null) {            	
            	double val = 0d;
            	if(obj instanceof Long){
            		val = ((Long) obj).doubleValue()*getCt()*0.001;
            	}else if(obj instanceof Float){
            		val = ((Float) obj).doubleValue()*getCt();
            	}            	
                log.debug("TOTAL_ACTIVEENERGY_EXPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.TOTAL_MAX_ACTIVEDEMAND_IMPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_MAX_ACTIVEDEMAND_IMPORT.getCode()).get(OBIS.TOTAL_MAX_ACTIVEDEMAND_IMPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
            	
            	this.currBill.setActivePwrDmdMaxImportRateTotal(val);
                log.debug("TOTAL_MAX_ACTIVEDEMAND_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.TOTAL_MAX_ACTIVEDEMAND_EXPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_MAX_ACTIVEDEMAND_EXPORT.getCode()).get(OBIS.TOTAL_MAX_ACTIVEDEMAND_EXPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
            	
            	this.currBill.setActivePwrDmdMaxExportRateTotal(val);
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
            	
            	this.currBill.setCummActivePwrDmdMaxImportRateTotal(val);
                log.debug("TOTAL_CUM_ACTIVEDEMAND_IMPORT[" + val + "]");
            }
        }
        if (result.get(OBIS.TOTAL_CUM_ACTIVEDEMAND_EXPORT.getCode()) != null) {
        	Object obj = null;            	
        	obj = result.get(OBIS.TOTAL_CUM_ACTIVEDEMAND_EXPORT.getCode()).get(OBIS.TOTAL_CUM_ACTIVEDEMAND_EXPORT.getName());
            if (obj != null) {            	
            	double val = ((Long) obj).longValue()*0.001;
            	this.currBill.setCummActivePwrDmdMaxExportRateTotal(val);
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
    
    public LinkedHashMap<String, Object> getRelayStatus() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        Map<String, Object> loadControlMap = result.get(OBIS.RELAY_STATUS.getCode());
        Map<String, Object> outputSignalMap = result.get(OBIS.EXTERNAL_RELAY_STATUS.getCode());
        
        log.debug("LoadControlStatus : "+ loadControlMap.get("LoadControlStatus"));
        log.debug("OutSignal : "+outputSignalMap.get("OutputSignal"));
        
        map.put("LoadControlStatus", loadControlMap.get("LoadControlStatus"));
        map.put("OutputSignal", outputSignalMap.get("OutputSignal"));
        return map;
    }
    
    public Double getLQISNRValue(){
    	
    	Object obj = null;    
    	if(result.get(OBIS.WEAK_LQI_VALUE.getCode()) != null){
        	obj = result.get(OBIS.WEAK_LQI_VALUE.getCode()).get(OBIS.WEAK_LQI_VALUE.getName());
        	if (obj != null) {   
        		log.debug("LQI SNR[" + obj + "]");   
        		if(obj instanceof Double){
        			return (Double)obj;
        		}
        	}
    	}

    	return null;
    }

    public Integer getLpInterval() {
        return this.lpInterval;
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

    public String getFwVersion() {
		return fwVersion;
	}

	public void setFwVersion(String fwVersion) {
		this.fwVersion = fwVersion;
	}	

	public String getMeterModel() {
		if(meterModel == null || "".equals(meterModel)){
			return null;
		}
		if(meterModel.indexOf("CT") > 0)
			return "LSIQ-3PCT";
		if(meterModel.indexOf("CV") > 0 || meterModel.indexOf("CP") > 0)
			return "LSIQ-3PCV";
		if(meterModel.indexOf("3") > 0)
			return "LSIQ-3P";
		if(meterModel.indexOf("1210") > 0)
			return "LSIQ-1P";
		return meterModel;
	}

	public void setMeterModel(String meterModel) {
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
}
