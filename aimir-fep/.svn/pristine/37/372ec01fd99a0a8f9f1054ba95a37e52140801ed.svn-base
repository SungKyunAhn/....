package com.aimir.fep.meter.parser;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.fep.meter.data.MultiPulseSensorData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

public class MultiPulseSensor extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = -5286427662315475754L;

	private static Log log = LogFactory.getLog(MultiPulseSensor.class);
    
    private byte[] rawData = null;
    private byte[] DATA_TYPE = new byte[1];
    private byte[] LP_PERIOD = new byte[1];
    private byte[] BASE_TIME = new byte[6];
    private byte[] LP_COUNT = new byte[1];
    private byte[] LP_DATA = null;
    
    private List<MultiPulseSensorData> envDataList = null;
    private int channelCount = 1;
    private int dataType = 0;
    private int lpPeriod = 0;
    private Double meterValue = 0d;
    
	@Override
	public byte[] getRawData() {
        return rawData;
	}
	
	@Override
	public int getLength() {
		
        if(rawData == null)
            return 0;

        return rawData.length;
	}
	
	@Override
	public void parse(byte[] data) throws Exception {
        int pos = 0;
        rawData = data;
        System.arraycopy(data, pos, DATA_TYPE, 0, DATA_TYPE.length);
        pos += DATA_TYPE.length;
        dataType = DataUtil.getIntToBytes(DATA_TYPE);
        log.debug("DATA_TYPE[" + dataType + "] "+MeterType.getValue(dataType));
        
        System.arraycopy(data, pos, LP_PERIOD, 0, LP_PERIOD.length);
        pos += LP_PERIOD.length;
        lpPeriod = DataUtil.getIntToBytes(LP_PERIOD);
        if(lpPeriod > 0){
        	lpPeriod = 60/lpPeriod;
        }
        log.debug("LP_PERIOD[" + lpPeriod + "]");
        
        System.arraycopy(data, pos, BASE_TIME, 0, BASE_TIME.length);
        pos += BASE_TIME.length;       
        
        String baseTime = getLpTime(BASE_TIME);            
        
        log.debug("BASE_TIME[" + baseTime + "]");
        
        System.arraycopy(data, pos, LP_COUNT, 0, LP_COUNT.length);
        pos += LP_COUNT.length;
        int lpCount = DataUtil.getIntToBytes(LP_COUNT);        
        log.debug("LP_COUNT[" + lpCount + "]");        
        
        if(MeterType.getValue(dataType).equals(MeterType.TemperatureHumiditySensor)){ //temperature, humidity
        	channelCount = 2;
        }
        
        if(MeterType.getValue(dataType).equals(MeterType.TemperatureHumiditySensor)){ //temperature, humidity
        	LP_DATA = new byte[2]; //set data length 2
        }else{
        	LP_DATA = new byte[4]; //set data length 4
        }

        envDataList = new ArrayList<MultiPulseSensorData>();
        
        Double prevPulse = null;
        String lpTime = baseTime;
        
        for(int i = 0; i < lpCount; i++){ 
            
        	MultiPulseSensorData envData = new MultiPulseSensorData();
        	Double pulse = 0d;
        	Double pulseValue = 0d;
            double temperature = 0d; 
            double humidity = 0d; 
            boolean isIgnoreData = false;
            Double[] channel = new Double[channelCount];            
            
            String rawLpStr = Hex.decode(DataUtil.select(data, pos, LP_DATA.length*channelCount)).toUpperCase();
            if(rawLpStr.startsWith("FFFFFFFF")){
            	isIgnoreData = true;
            }
        	for(int x = 0; x < channelCount; x++){
                System.arraycopy(data, pos, LP_DATA, 0, LP_DATA.length);
                pos += LP_DATA.length;
                log.debug("LPDATA[" + Hex.decode(LP_DATA) + "]");
                
                if(channelCount ==2){
                	
                    int lpData = DataFormat.hex2signed16(LP_DATA);                    
                    
                    if(x == 0){
                    	temperature = getTemperature(lpData); 
                        channel[x] = temperature;
                    }
                    if(x == 1){
                    	humidity = getHumidity(lpData);
                        channel[x] = humidity;
                    }
                }else{
                    long lpData = DataFormat.hex2long(LP_DATA);
                    pulse = (double) lpData;
                    pulseValue = pulse/100;                    
                    if(i == 0){
                    	isIgnoreData = true;
                    }else{
                    	channel[x] = (double) pulseValue - prevPulse;
                    }
                    prevPulse = pulseValue;
                }
        	}           	
        	
        	try{
        		
            	lpTime = Util.addMinYymmdd(lpTime, lpPeriod);
                envData.setDatetime(lpTime);
                envData.setCh(channel);
                envData.setPulse(pulse);
                envData.setPulseValue(pulseValue);
                envData.setChannelCnt(channelCount);
                
                if(i == (lpCount-1)){
                	this.meterValue = pulseValue;
                }
                
                if(pulse < 0 || pulse == 65535){
                	envData.setFlag(MeteringFlag.NotValid.getFlag());
                }else{
                	envData.setFlag(MeteringFlag.Correct.getFlag());
                }
                
                if(!isIgnoreData){
                    envDataList.add(envData);
                    log.debug("TIME="+lpTime+", CHANNEL=["+channel[0]+"]");
                }
        	}catch(Exception e){
        		log.warn(e,e);
        	}
 
        }
	}
	
	
	public String getLpTime(byte[] time){
		
		String basetime = "";
	    byte[] YEAR = new byte[1];
	    byte[] MONTH = new byte[1];
	    byte[] DAY = new byte[1];
	    byte[] HOUR = new byte[1];
	    byte[] MINUTE = new byte[1];
	    byte[] SEC = new byte[1];
	    int pos = 0;
	    
        System.arraycopy(time, pos, YEAR, 0, YEAR.length);
        pos += YEAR.length;
        int yy = DataUtil.getIntToBytes(YEAR);
        
		int currcen = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
	
		int year   = yy;
		if(year != 0){
			year = yy + currcen;
		}
        System.arraycopy(time, pos, MONTH, 0, MONTH.length);
        pos += MONTH.length;
        int month = DataUtil.getIntToBytes(MONTH);
        
        System.arraycopy(time, pos, DAY, 0, DAY.length);
        pos += DAY.length;
        int day = DataUtil.getIntToBytes(DAY);
        
        System.arraycopy(time, pos, HOUR, 0, HOUR.length);
        pos += HOUR.length;
        int hour = DataUtil.getIntToBytes(HOUR);

        System.arraycopy(time, pos, MINUTE, 0, MINUTE.length);
        pos += MINUTE.length;
        int min = DataUtil.getIntToBytes(MINUTE);
        
        System.arraycopy(time, pos, SEC, 0, SEC.length);
        pos += SEC.length;
        int sec = DataUtil.getIntToBytes(SEC);

        basetime = ""+ year + 
	            (month < 10? "0"+month : month) + 
	            (day < 10? "0"+day : day) +
	            (hour < 10? "0"+hour : hour) +
	            (min < 10? "0"+min : min);
        
        return basetime;
	}
	
	
	private double getTemperature(int value){		
		double temperature = 0d;
		temperature = value/100;
		return temperature;
	}
	
	private double getHumidity(int value){		
		double rh = 0d;
		rh = value/100;
		return rh;
	}
	
	public List<MultiPulseSensorData> getLpData(){
		return envDataList;		
	}		
	
	public int getPeriod(){
        int lpPeriod = DataUtil.getIntToBytes(LP_PERIOD);
        return lpPeriod;
	}
	
	@Override
	public Double getMeteringValue() {
		return this.meterValue;
	}
	@Override
	public String toString() {
		return null;
	}

	@Override
	public int getFlag() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void setFlag(int flag) {

	}

    public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	
    /**
     * get Data
     */
    @Override
    public LinkedHashMap<String, String> getData() {

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
            datef14 = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        }
        
        LinkedHashMap<String, String> res = new LinkedHashMap<String, String>(16,0.75f,false);
        try {
            String meteringTime = null;
            if(super.meteringTime != null) {
            	meteringTime = datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(super.meteringTime));
            }
            res.put("Metering Time", meteringTime);
            if(envDataList != null && envDataList.size() > 0){
                res.put("Metering Value(kWh)",""+decimalf.format(envDataList.get(envDataList.size()-1).getPulseValue()));
            }

            res.put("Load Profile Interval", lpPeriod+"");
            res.put("Meter Type", MeterType.getValue(dataType)+"");
            
            if(MeterType.getValue(dataType).equals(MeterType.TemperatureHumiditySensor)){
                if (envDataList != null && envDataList.size() > 0) {
                    for (MultiPulseSensorData lpData : envDataList) {
                    	
                    	String key = datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lpData.getDatetime()));
                    	String value = "Temperature[℃]=["+lpData.getCh()[0]+"] Humidity[%]=["+lpData.getCh()[1]+"]";                    			
                    	res.put(key, value);
                    }
                }
            }else{
                if (envDataList != null && envDataList.size() > 0) {
                    for (MultiPulseSensorData lpData : envDataList) {
                    	String key = datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lpData.getDatetime()));
                    	String value = "Pulse=["+lpData.getPulse()+"] Lp=["+lpData.getCh()[0]+"]";                    			
                    	res.put(key, value);
                    }
                }
            }
        }
        catch (Exception e) {
            log.warn(e);
        }
        
        return res;
    }
    
    
    public enum MeterType {

    	Unknown(0),
        Gas(1),
        Water(2),
        TemperatureHumiditySensor(3);
        
        int code;
        
        MeterType(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public static MeterType getValue(int code) {
            for (MeterType a : MeterType.values()) {
                if (a.getCode() == code)
                    return a;
            }
            return Unknown;
        }
    }

	public static void main(String[] args)
    {
    	MultiPulseSensor ev = new MultiPulseSensor();
        try {
            
            System.out.println(ev.getTemperature(24112));
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
