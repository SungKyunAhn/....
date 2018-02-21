package com.aimir.fep.meter.parser;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.parser.DLMSKepcoTable.LPComparator;
import com.aimir.fep.meter.parser.DLMSTable.DLMSTable;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.KEPCO_CURRENT_MAX_DEMAND;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.KEPCO_METER_INFO;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.KEPCO_PREVIOUS_MAX_DEMAND;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.METER_CONSTANT;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.MONTHLY_DEMAND_PROFILE;
import com.aimir.fep.meter.parser.DLMSTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

public class DLMSKepco extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 8916023997892795309L;

	private static Log log = LogFactory.getLog(DLMSKepco.class);

	LPData[] lpData = null;
    LinkedHashMap<String, Map<String, Object>> result = 
            new LinkedHashMap<String, Map<String, Object>>();

    int meterConstnt = 0;
    String meterID = "";
    String meterType = "";

    //int meterActiveConstant = 1;
    //int meterReActiveConstant = 1;

    double activePulseConstant = 1;
    double reActivePulseConstant = 1;
    
    Long ctRatio = 0L;
    
    int interval=0;
    
    Double meteringValue= null;
    Double ct = 1d;

    @Override
    public void parse(byte[] data) throws Exception {
        log.debug("DLMS parse:"+Hex.decode(data));

        String vz = "";   // VZ값. 
        String obisCode = "";
        int clazz = 0;
        int attr = 0;

        int pos = 0;
        int len = 0;
        // DLMS Header OBIS(6), CLASS(1), ATTR(1), LENGTH(2)
        // DLMS Tag Tag(1), DATA or LEN/DATA (*)
        
        byte[] METER_TYPE = new byte[1];
        byte[] OBIS_CODE_ID = new byte[2];
        byte[] OBIS = new byte[6];
        byte[] CLAZZ = new byte[2];
        byte[] ATTR = new byte[1];
        byte[] LEN = new byte[2];
        byte[] TAGDATA = null;
        
        System.arraycopy(data, pos, METER_TYPE, 0, METER_TYPE.length);
        pos += METER_TYPE.length;
        log.debug("METER_TYPE[" + Hex.decode(METER_TYPE) + "]");
        meterType = getMeterType(METER_TYPE[0]);
        
        DLMSTable dlms = null;
        while (pos < data.length) {
            System.arraycopy(data, pos, OBIS_CODE_ID, 0, OBIS_CODE_ID.length);
            pos += OBIS_CODE_ID.length;
            log.debug("OBIS_CODE_ID[" + Hex.decode(OBIS_CODE_ID) + "]");
            
            dlms = new DLMSTable();
            System.arraycopy(data, pos, OBIS, 0, OBIS.length);
            pos += OBIS.length;
            obisCode = Hex.decode(OBIS);
            log.debug("OBIS[" + obisCode + "]");

			/* VZ 처리 */
            if(obisCode.equals("0100000102" + vz)){
            	obisCode = DLMSVARIABLE.OBIS.BILLING_DATE.getCode();
            }else if(obisCode.equals("0000620101" + vz)){
				obisCode = DLMSVARIABLE.OBIS.BILLING_ENERGY.getCode();
			}else if(obisCode.equals("0000620102" + vz)){
				obisCode = DLMSVARIABLE.OBIS.BILLING_MAX_DEMAND.getCode();
			}

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
            
            dlms.parseDlmsTag(TAGDATA);
            // 동일한 obis 코드를 가진 값이 있을 수 있기 때문에 검사해서 _number를 붙인다.
            if (dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.LOAD_PROFILE) {
                for (int cnt = 0; ;cnt++) {
                    obisCode = dlms.getDlmsHeader().getObis().getCode() + "-" + cnt;
                    if (!result.containsKey(obisCode)) {
                        result.put(obisCode, dlms.getData());
                        break;
                    }
                }
            }else if(dlms.getDlmsHeader().getObis() == DLMSVARIABLE.OBIS.BILLING_TOTAL_ENERGY){
                for (int cnt = 0; ;cnt++) {
                    obisCode = dlms.getDlmsHeader().getObis().getCode() + "-" + cnt;
                    if (!result.containsKey(obisCode)) {
                        result.put(obisCode, dlms.getData());
                        break;
                    }
                }
            }
            else result.put(obisCode, dlms.getData());
            
            if(obisCode.equals(DLMSVARIABLE.OBIS.VZ.getCode())){
            	int val = ((Integer)dlms.getDlmsTags().get(0).getValue()).intValue();
            	vz = Hex.decode(new byte[]{(byte) (val & 0xFF)});
            }
        }

        EnergyMeter meter = (EnergyMeter)this.getMeter();
        
        this.ct = 1.0;
        if (meter != null && meter.getCt() != null && meter.getCt() > 0)
            ct = meter.getCt();

    	if(meter.getPulseConstant() != null && meter.getPulseConstant() != 0){
    		activePulseConstant = meter.getPulseConstant();
    		reActivePulseConstant = meter.getPulseConstant();
    	}

        setCt(ct);
        
        setMeterInfo();
        setPulseConstant();
        setMeteringValue();
        setLPData();

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

    public void setLPData() {
        try {
            List<LPData> lpDataList = new ArrayList<LPData>();
            
            Double lp = 0.0;
            Double lpValue = 0.0;
            Object value = null;
            Map<String, Object> lpMap = null;
            int cnt = 0;
            LPData _lpData = null;
            double active = 0.0;
            double groundReactive = 0.0;
            double truthReactive = 0.0;
            double apparentReactive = 0.0;
                    
            for (int i = 0; i < result.size(); i++) {
                if (!result.containsKey(OBIS.LOAD_PROFILE.getCode() + "-" + i))
                    break;
                
                lpMap = (Map<String, Object>) result.get(OBIS.LOAD_PROFILE.getCode() + "-" + i);
                cnt = 0;
                while ((value=lpMap.get("Channel[1]"+"-"+cnt)) != null) {
                	if (value instanceof OCTET)
                		lp = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                	else if (value instanceof Long)
                		lp = ((Long)value).doubleValue();
                		
                    lpValue = lp / activePulseConstant;
                    lpValue = lpValue*ct;
                    
                    active = lpValue;
                    value = lpMap.get("Channel[2]"+"-"+cnt);
                    if (value != null) {
                    	if (value instanceof OCTET)
                    		groundReactive = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Long)
                    		groundReactive = ((Long)value).doubleValue();
                    	
                        groundReactive /= reActivePulseConstant;
                        groundReactive *= ct;
                    }
                    value = lpMap.get("Channel[3]"+"-"+cnt);
                    if (value != null) {
                    	if (value instanceof OCTET)
                    		truthReactive = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Long)
                    		truthReactive = ((Long)value).doubleValue();
                        truthReactive /= reActivePulseConstant;
                        truthReactive *= ct;
                    }
                    value = lpMap.get("Channel[4]"+"-"+cnt);
                    if (value != null) {
                    	if (value instanceof OCTET)
                    		apparentReactive = (double)DataUtil.getLongToBytes(((OCTET)value).getValue());
                    	else if (value instanceof Long)
                    		apparentReactive = ((Long)value).doubleValue();
                        apparentReactive /= reActivePulseConstant;
                        apparentReactive *= ct;
                    }
                    
                    String lpTime = (String) lpMap.get("DateTime"+ "-" + cnt++);
                    lpTime = Util.addMinYymmdd(lpTime, -(this.meter.getLpInterval()));
                    // 미터에서 LP시간이 00시00분부터 00시15분 사이의 데이터는 00시 15분으로 저장하기 때문에
                    //서버에서는 해당 기간의 사용데이터는 00시 00분으로 계산하므로 주기만큼 빼야함.
                    
                    _lpData = new LPData(lpTime, lp, lpValue);

                    if(meterType.indexOf("E-Type 1.0") >= 0){
                    	_lpData.setCh(new Double[]{active});
                    } else if(meterType.indexOf("E-Type 1.1") >= 0){
                        	_lpData.setCh(new Double[]{active, groundReactive});
                    }else{
                    	_lpData.setCh(new Double[]{active, groundReactive, truthReactive, apparentReactive});
                    }
                    
                    _lpData.setPF(1d);
                    
                    if (_lpData.getDatetime() != null && !_lpData.getDatetime().substring(0, 4).equals("1792")) {
                    	lpDataList.add(_lpData);
                    	log.debug(_lpData.toString());
                    }
                }
            }
            
            Collections.sort(lpDataList,LPComparator.TIMESTAMP_ORDER);  

            if(meterType.indexOf("E-Type") >= 0){
            	Double prevVal = 0d;
            	for(LPData data : lpDataList){
            		Double val = data.getCh()[0];
            		Double[] ch = new Double[1];
            		ch[0] = val-prevVal;
            		data.setCh(ch);
            		data.setLp(ch[0]);
            		data.setLpValue(ch[0]);
            		prevVal = val;
            		
            	}
            	if(lpDataList != null && !lpDataList.isEmpty()){
                	lpDataList.remove(0);
            	}
            }
            lpData = lpDataList.toArray(new LPData[0]);
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
        	//447A0000
        	if( result.get(OBIS.METER_CONSTANT_ACTIVE.getCode()) == null ||  result.get(OBIS.METER_CONSTANT_ACTIVE.getCode()).isEmpty()){
        		return;
        	}
        	if( result.get(OBIS.METER_CONSTANT_REACTIVE.getCode()) == null ||  result.get(OBIS.METER_CONSTANT_REACTIVE.getCode()).isEmpty()){
        		return;
        	}

            Map<String, Object> activemap =
                    (Map<String, Object>) result.get(OBIS.METER_CONSTANT_ACTIVE.getCode());
            
            if(activemap.get(METER_CONSTANT.ActiveC.name()) != null && activemap.get(METER_CONSTANT.ActiveC.name()) instanceof OCTET){
                activePulseConstant = 
                        DataUtil.getFloat(((OCTET) activemap.get(METER_CONSTANT.ActiveC.name())).getValue(), 0);
            }else if(activemap.get(METER_CONSTANT.ActiveC.name()) != null && activemap.get(METER_CONSTANT.ActiveC.name()) instanceof Float){
                activePulseConstant = 
                       ((Float) activemap.get(METER_CONSTANT.ActiveC.name())).doubleValue();
            }else if(activemap.get(METER_CONSTANT.ActiveC.name()) != null && activemap.get(METER_CONSTANT.ActiveC.name()) instanceof Double){
                activePulseConstant = 
                       ((Double) activemap.get(METER_CONSTANT.ActiveC.name())).doubleValue();
            }

            log.debug("ACTIVE_PULSE_CONSTANT[" + activePulseConstant + "]");
            // 미터 펄스 상수에 넣는다.
            meter.setPulseConstant(activePulseConstant);
            
            Map<String, Object> reactivemap =
                    (Map<String, Object>) result.get(OBIS.METER_CONSTANT_REACTIVE.getCode());
            
            if(reactivemap.get(METER_CONSTANT.ReactiveC.name()) != null && reactivemap.get(METER_CONSTANT.ReactiveC.name()) instanceof OCTET){
                reActivePulseConstant = 
                        DataUtil.getFloat(((OCTET)reactivemap.get(METER_CONSTANT.ReactiveC.name())).getValue(), 0);
            }else if(reactivemap.get(METER_CONSTANT.ReactiveC.name()) != null && reactivemap.get(METER_CONSTANT.ReactiveC.name()) instanceof Float){
                reActivePulseConstant = 
                        ((Float)reactivemap.get(METER_CONSTANT.ReactiveC.name())).doubleValue();
            }else if(reactivemap.get(METER_CONSTANT.ReactiveC.name()) != null && reactivemap.get(METER_CONSTANT.ReactiveC.name()) instanceof Double){
                reActivePulseConstant = 
                		((Double)reactivemap.get(METER_CONSTANT.ReactiveC.name())).doubleValue();
            }
            log.debug("REACTIVE_PULSE_CONSTANT[" + reActivePulseConstant + "]");
        } catch (Exception e) {
            log.error(e, e);
        }

    }

    public void setMeterInfo() {
        try {
            Map<String, Object> map = (Map<String, Object>) result.get(OBIS.KEPCO_METER_INFO.getCode());
            if (map != null) {
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
            }
            map = (Map<String, Object>)result.get(OBIS.METER_TIME.getCode());
            if (map != null) {
	        	Object obj = map.get("MeterTime");
	        	if (obj != null) meterTime = (String)obj;
            }
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    public void setPreviousMaxDemand() {
        Map<String, Object> map = null;
        try {
            map = (Map<String, Object>) result
                    .get(OBIS.KEPCO_PREVIOUS_MAX_DEMAND.getCode());
            long t1PreActive = (Long) map.get(KEPCO_PREVIOUS_MAX_DEMAND.T1PreviousActive.name());
            log.debug("T1_PREVIOUS_ACTIVE[" + t1PreActive + "]");
            long t2PreActive = (Long) map.get(KEPCO_PREVIOUS_MAX_DEMAND.T2PreviousActive.name());
            log.debug("T2_PREVIOUS_ACTIVE[" + t2PreActive + "]");
            long t3PreActive = (Long) map.get(KEPCO_PREVIOUS_MAX_DEMAND.T3PreviousActive.name());
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
                    .get(OBIS.KEPCO_CURRENT_MAX_DEMAND.getCode());
            long t1CurActive = (Long) map.get(KEPCO_CURRENT_MAX_DEMAND.T1CurrentActive.name());
            long t2CurActive = (Long) map.get(KEPCO_CURRENT_MAX_DEMAND.T2CurrentActive.name());
            long t3CurActive = (Long) map.get(KEPCO_CURRENT_MAX_DEMAND.T3CurrentActive.name());
            double currentDemandData = (t1CurActive + t2CurActive + t3CurActive) / this.activePulseConstant;
            log.debug("CURRENT_DEMAND[" + currentDemandData + "]");
        } catch (Exception e) {
            log.error(e);
        }
    }
    
    public Map<String, Object> getPreviousMaxDemand() {
        return (Map<String, Object>) result.get(OBIS.KEPCO_PREVIOUS_MAX_DEMAND.getCode());
    }

    public Map<String, Object> getCurrentMaxDemand() {
        return (Map<String, Object>) result.get(OBIS.KEPCO_CURRENT_MAX_DEMAND.getCode());
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
    
    public void setMeteringValue() {
        try {
        	if(result.get(OBIS.MONTHLY_ENERGY_PROFILE.getCode()) == null || result.get(OBIS.MONTHLY_ENERGY_PROFILE.getCode()).isEmpty()){
        		return;
        	}
        	if(result.get(OBIS.MONTHLY_DEMAND_PROFILE.getCode()) == null || result.get(OBIS.MONTHLY_DEMAND_PROFILE.getCode()).isEmpty()){
        		return;
        	}
            Map<String, Object> map = (Map<String, Object>) result
                    .get(OBIS.MONTHLY_ENERGY_PROFILE.getCode());
            long active = (Long) map
                    .get(MONTHLY_DEMAND_PROFILE.Active.name());
            BigDecimal bd = new BigDecimal(active);
            meteringValue=bd.doubleValue()/ this.activePulseConstant;
            meteringValue= meteringValue*getCt();
            log.debug("METERING_VALUE[" + meteringValue + "]");
        } catch (Exception e) {
            log.error(e);
        }
    }
    
    public BillingData getETypeBillingData(){
    	
    	Double energyTotal = null;
    	String billingTime = null;
    	BillingData bill = null;
    	Map<String, Object> map = null;
        for (int i = 0; i < result.size(); i++) {
            if (!result.containsKey(OBIS.BILLING_TOTAL_ENERGY.getCode() + "-" + i))
                break;
            
            map = (Map<String, Object>) result.get(OBIS.BILLING_TOTAL_ENERGY.getCode() + "-" + i);
            
        	if(map.get("Billing Total Energy") != null){
        		energyTotal = (((Long)map.get("Billing Total Energy")).doubleValue()/ this.activePulseConstant)*getCt();
        	}
        	if(map.get("Billing Date Time") != null){
        		billingTime = ((String)map.get("Billing Date Time")).substring(0,8);
        	}
        }
    	
        if(energyTotal != null && billingTime != null && !"".equals(billingTime)){
        	bill = new BillingData();
            bill.setActiveEnergyRateTotal(energyTotal);  
            bill.setBillingTimestamp(billingTime);
        }

        return bill;
    }

    public  Map<String, Object> getMonthlyEnergyProfile(){
    	return (Map<String, Object>) result.get(OBIS.MONTHLY_ENERGY_PROFILE.getCode());
    }
    
    public  Map<String, Object> getBillingEnergy(){
    	return (Map<String, Object>) result.get(OBIS.BILLING_ENERGY.getCode());
    }

    public  Map<String, Object> getBillingMaxDemand(){
    	return (Map<String, Object>) result.get(OBIS.BILLING_MAX_DEMAND.getCode());
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
        return this.meterID;
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
    
    
    private String getMeterType(byte type){    	
    	//3	2	1	0
    	//G-Type	E-Type 1.1	E-Type 1.0	한전 표준
    	
    	if     ((type & 0x01) > 1) return "Kepco Standard";
    	else if((type & 0x02) > 1) return "E-Type 1.0";
    	else if((type & 0x04) > 1) return "E-Type 1.1";
    	else if((type & 0x08) > 1) return "G-Type";
    	else return "Unknown";
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
            //locail 정보가 없을때는 기본 포멧을 사용한다.
            decimalf = new DecimalFormat();
            datef14 = new SimpleDateFormat();
        }
        
        for (Iterator i = result.keySet().iterator(); i.hasNext(); ) {
            key = (String)i.next();
            resultSubData = result.get(key);
            // 정복전 결과가 많아서 최근적으로만 넣는다.
            if (key.startsWith(DLMSVARIABLE.OBIS.POWER_FAILURE.getCode()) || key.startsWith(DLMSVARIABLE.OBIS.POWER_RESTORE.getCode())) {
                
            }
            else {
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
                                data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, decimalf.format(subvalue));
                            }
                            else data.put(OBIS.getObis(key).getName()+idx+" : "+subkey, subvalue);
                        }
                    }
                }
            }
        }
        return (LinkedHashMap)data;
    }

	public String getMeterType() {
		return meterType;
	}

	public void setMeterType(String meterType) {
		this.meterType = meterType;
	}    
    
}
