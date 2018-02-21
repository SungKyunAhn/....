package com.aimir.fep.meter.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EnvData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;

public class EnvSensor extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = -5286427662315475754L;

	private static Log log = LogFactory.getLog(EnvSensor.class);
    
    private byte[] rawData = null;
    
    private byte[] DATA_NAME = new byte[4];
    private byte[] SENSOR_COUNT = new byte[1];
    private byte[] LP_PERIOD = new byte[1];
    private byte[] CHANNEL_CONFIGURATION = new byte[1];
    
    private byte[] EUI64 = new byte[8];
    private byte[] LP_COUNT = new byte[1];
    private byte[] BASE_TIME = new byte[6];
    private byte[] LP_DATA = new byte[2];
    
    private List<EnvData> envDataList = null;
    private static int BLOCK_COUNT = 6;
    
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
        System.arraycopy(data, pos, DATA_NAME, 0, DATA_NAME.length);
        pos += DATA_NAME.length;
        String dataName = new String(DATA_NAME);
        log.debug("DATANAME[" + dataName + "]");
        
        System.arraycopy(data, pos, SENSOR_COUNT, 0, SENSOR_COUNT.length);
        pos += SENSOR_COUNT.length;
        int sensorCount = DataUtil.getIntToBytes(SENSOR_COUNT);
        log.debug("SENSOR_COUNT[" + sensorCount + "]");
        
        System.arraycopy(data, pos, LP_PERIOD, 0, LP_PERIOD.length);
        pos += LP_PERIOD.length;
        int lpPeriod = DataUtil.getIntToBytes(LP_PERIOD);
        if(lpPeriod > 0){
        	lpPeriod = 60/lpPeriod;
        }
        log.debug("LP_PERIOD[" + lpPeriod + "]");
        
        System.arraycopy(data, pos, CHANNEL_CONFIGURATION, 0, CHANNEL_CONFIGURATION.length);
        pos += CHANNEL_CONFIGURATION.length;
        int channelConfiguration = DataUtil.getIntToBytes(CHANNEL_CONFIGURATION);
        int channelCount = 0;        
        
        if((channelConfiguration & 0x01) > 0){
        	channelCount++;
        }
        if((channelConfiguration & 0x02) > 0){
        	channelCount++;
        }
        if((channelConfiguration & 0x04) > 0){
        	channelCount++;
        }
        
        log.debug("CHANNEL_CONFIGURATION[" + channelConfiguration + "]");
        log.debug("CHANNEL_COUNT[" + channelCount + "]");  
        
        envDataList = new ArrayList<EnvData>();
        
        for(int i = 0; i < sensorCount; i++){

            System.arraycopy(data, pos, EUI64, 0, EUI64.length);
            pos += EUI64.length;      
            String eui64 = Hex.decode(EUI64);
            
            log.debug("EUI64[" + eui64 + "]");
            
            System.arraycopy(data, pos, LP_COUNT, 0, LP_COUNT.length);
            pos += LP_COUNT.length;
            int lpCount = DataUtil.getIntToBytes(LP_COUNT);
            
            log.debug("LP_COUNT[" + lpCount + "]");
            
            for(int k = 0; k < lpCount; k++){
                System.arraycopy(data, pos, BASE_TIME, 0, BASE_TIME.length);
                pos += BASE_TIME.length;       
                
                String baseTime = getLpTime(BASE_TIME);            
                
                log.debug("BASE_TIME[" + baseTime + "]");
                
                String lpTime = baseTime;
                
                for(int j = 0; j < BLOCK_COUNT; j++){
                	
                	EnvData envData = new EnvData();
                    double temperature = 0d; 
                    double humidity = 0d; 
                    double co2 = 0d; 
                    boolean isIgnoreData = false;
                    Double[] channel = new Double[channelCount];
                    
                    String rawLpStr = Hex.decode(DataUtil.select(data, pos, LP_DATA.length*channelCount)).toUpperCase();
                    if(rawLpStr.equals("FFFFFFFFFFFF")){
                    	isIgnoreData = true;
                    }
                	for(int x = 0; x < channelCount; x++){
                        System.arraycopy(data, pos, LP_DATA, 0, LP_DATA.length);
                        pos += LP_DATA.length;
                        log.debug("LPDATA[" + Hex.decode(LP_DATA) + "]");
                        int lpData = DataUtil.getIntTo2Byte(LP_DATA);

                        if((channelConfiguration & 0x01) > 0 && x == 0){
                        	temperature = getTemperature(lpData); 
                            channel[x] = temperature;
                        }
                        if((channelConfiguration & 0x02) > 0 && x == 1){
                        	humidity = getHumidity(lpData);
                            channel[x] = humidity;
                        }
                        if((channelConfiguration & 0x04) > 0 && x == 2){
                        	co2 = getCo2(lpData); 
                            channel[x] = co2;
                        }
                	}           	
                	
                	try{
                    	lpTime = Util.addMinYymmdd(lpTime, lpPeriod);
                        envData.setDatetime(lpTime);
                        envData.setCh(channel);
                        envData.setChannelCnt(channelCount);
                        envData.setSensorId(eui64);
                        if(!isIgnoreData){
                            envDataList.add(envData);
                            log.debug("SENSOR_ID="+eui64+", TIME="+lpTime+", CHANNEL=["+channel[0]+"]["+channel[1]+"]["+channel[2]+"]");
                        }
                	}catch(Exception e){
                		log.warn(e,e);
                	}
                }  
            }
 
        }
	}
	
	
	public String getLpTime(byte[] time){
		
		String basetime = "";
	    byte[] YEAR = new byte[2];
	    byte[] MONTH = new byte[1];
	    byte[] DAY = new byte[1];
	    byte[] HOUR = new byte[1];
	    byte[] MINUTE = new byte[1];
	    int pos = 0;
	    
        System.arraycopy(time, pos, YEAR, 0, YEAR.length);
        pos += YEAR.length;
        int year = DataUtil.getIntTo2Byte(YEAR);
        
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

        basetime = ""+ year + 
	            (month < 10? "0"+month : month) + 
	            (day < 10? "0"+day : day) +
	            (hour < 10? "0"+hour : hour) +
	            (min < 10? "0"+min : min);
        
        return basetime;
	}
	
	
	private double getTemperature(int value){		
		double temperature = 0d;
		temperature = ((175.72 * value) / 65536) - 46.85;
		return temperature;
	}
	
	private double getHumidity(int value){		
		double rh = 0d;
		rh = ((125 * value) / 65536) - 6;
		return rh;
	}
	
	private double getCo2(int value){
		return value;
	}
	
	public List<EnvData> getLpData(){
		return envDataList;		
	}		
	
	public int getPeriod(){
        int lpPeriod = DataUtil.getIntToBytes(LP_PERIOD);
        return lpPeriod;
	}
	
	@Override
	public Double getMeteringValue() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String toString() {
		return null;
	}
	@Override
	public LinkedHashMap<?, ?> getData() {
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
	
	
    public static void main(String[] args)
    {
    	EnvSensor ev = new EnvSensor();
        try {
            
            System.out.println(ev.getTemperature(24112));
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
