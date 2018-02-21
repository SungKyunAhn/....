package com.aimir.fep.meter.parser.elsterA1140Table;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;

/**
 * 
 * @author choiEJ
 *
 */
public class A1140_METER_INFO {    
	private Log log = LogFactory.getLog(A1140_METER_INFO.class);
    
    public static final int OFS_METER_MODEL    = 0;
    public static final int OFS_METER_SERIAL   = 12;
    public static final int OFS_METER_TIME 	   = 28;
    public static final int OFS_ACTIVE_POWER   = 35;
    public static final int OFS_REACTIVE_POWER = 51;
    public static final int OFS_APPARENT_POWER = 67;    
    public static final int OFS_RMS_CURRENT    = 83;
    public static final int OFS_FREQUENCY      = 95;
    public static final int OFS_RMS_VOLTAGE    = 99;    
    public static final int OFS_POWER_FACTOR   = 105;
    public static final int OFS_PHASE_ANGLE    = 113;
    public static final int OFS_CT_PRIMARY     = 125;
    public static final int OFS_CT_SECONDARY   = 129;
    
    public static final int LEN_METER_MODEL    = 12;
    public static final int LEN_METER_SERIAL   = 16;
    public static final int LEN_METER_TIME 	   = 7;
  
    public static final int LEN_IS_DATA        = 16;
    public static final int LEN_RMS_CURRENT    = 12;
    public static final int LEN_FREQUENCY      = 4;
    public static final int LEN_RMS_VOLTAGE    = 6;
    public static final int LEN_RMS_VOLTAGE_DATA    = 2;
    public static final int LEN_POWER_FACTOR   = 8;
    public static final int LEN_POWER_FACTOR_DATA    = 2;
    public static final int LEN_PHASE_ANGLE    = 12;
    
    public static final int LEN_CT_PRIMARY  = 4;
    public static final int LEN_CT_SECONDARY   = 2;
    
    public static final int LEN_IS_DATA_VALUE  = 4;
  
	private byte[] rawData = null;
    
	private String meterModel;
	
	private String meterSerial;
	
	private String meterTime;
	
	private Double rmsCurrentA;
	
	private Double rmsCurrentB;
	
	private Double rmsCurrentC;
	
	private Double rmsVoltageA;
	
	private Double rmsVoltageB;
	
	private Double rmsVoltageC;
	
	private Double powerFactorTotal;
	
	private Double powerFactorA;
	
	private Double powerFactorB;
	
	private Double powerFactorC;
	
	private Double activePowerTotal;
	
	private Double activePowerA;
	
	private Double activePowerB;
	
	private Double activePowerC;
	
	private Double reactivePowerTotal;
	
	private Double reactivePowerA;
	
	private Double reactivePowerB;
	
	private Double reactivePowerC;
	
	private Double apparentPowerTotal;
	
	private Double apparentPowerA;
	
	private Double apparentPowerB;
	
	private Double apparentPowerC;
	
	private Double frequencyA;
	
	private Double frequencyTotal;
	
	private Double angleA;
	
	private Double angleB;
	
	private Double angleC;
	
	private String ctPrimary;
	
	private String ctSecondary;
	
	/**
	 * Constructor
	 */
	public A1140_METER_INFO(byte[] rawData) {
        this.rawData = rawData;
        
        try {
            parse();
        }
        catch (Exception e) {
            log.error(e, e);
        }
	}
	
	private void parse() throws Exception {
	    meterModel = new String(DataFormat.select(rawData, OFS_METER_MODEL, LEN_METER_MODEL));
        log.debug("METER_MODEL=[" + meterModel + "]");
        
        String meterSerial = new String(DataFormat.select(rawData, OFS_METER_SERIAL, LEN_METER_SERIAL)).trim();
        log.debug("METER_SERIAL=[" + meterSerial + "]");
        
        byte[] time = DataFormat.select(rawData, OFS_METER_TIME, LEN_METER_TIME);
        byte[] temp = new byte[1];

        int sec   = Integer.parseInt(Hex.decode(DataFormat.select(time, 0, 1)));
        int min   = Integer.parseInt(Hex.decode(DataFormat.select(time, 1, 1)));
        int hour  = Integer.parseInt(Hex.decode(DataFormat.select(time, 2, 1)));
        temp[0]   = (byte) (DataFormat.select(time, 3, 1)[0] & 0x3F);
        int day   = Integer.parseInt(Hex.decode(temp));
        temp[0]   = (byte) (DataFormat.select(time, 4, 1)[0] & 0x1F);
        int month = Integer.parseInt(Hex.decode(temp));
        int year  = Integer.parseInt(Hex.decode(DataFormat.select(time, 6, 1)));
        
        Calendar cal = Calendar.getInstance();  // 현재 년도 [yy]yy 중 앞의 두 자리를 가져오기 위함. 
        
        meterTime = Integer.toString(cal.get(Calendar.YEAR)).substring(0, 2) + year + "" +
                    (month < 10? "0"+month : month) + 
                    (day < 10? "0"+day : day) +
                    (hour < 10? "0"+hour : hour) +
                    (min < 10? "0"+min : min) +
                    (sec < 10? "0"+sec : sec);

        log.debug("METER_TIME=[" + meterTime + "]");
        
        parseRMSCurrent();
        parseRMSVoltage();
        parsePowerFactor();
        parseActivePower();
        parseReactivePower();
        parseApparentPower();
        
        frequencyA = (double)DataFormat.getIntTo4Byte(DataFormat.select(rawData, OFS_FREQUENCY, LEN_FREQUENCY))*0.1;
        frequencyTotal = (double)DataFormat.getIntTo4Byte(DataFormat.select(rawData, OFS_FREQUENCY, LEN_FREQUENCY))*0.1;
        
        parseAngle();
        
        byte[] b = DataFormat.select(rawData, OFS_CT_PRIMARY, LEN_CT_PRIMARY);
        // big endian
        ctPrimary = Hex.decode(b);
        log.debug("CT Primary=[" + ctPrimary + "]");
        
        b = DataFormat.select(rawData, OFS_CT_SECONDARY, LEN_CT_SECONDARY);
        // big endian
        ctSecondary = Hex.decode(b);
        log.debug("CT Secondary=[" + ctSecondary + "]");
	}
	
	public static void main(String args[]) {
    	A1140_TEST_DATA testData = new A1140_TEST_DATA();
    	A1140_METER_INFO elster = new A1140_METER_INFO(testData.getTestData_meter());
    	System.out.println(elster.toString());
	}
	
	public String getMeterModel() throws Exception {
		return meterModel;
	}
	
	public String getMeterSerial() throws Exception {
		return meterSerial;
	}
	
	public String getMeterTime() throws Exception {
		return meterTime;
	}
	
	private void parseRMSCurrent() throws Exception {
	    Map<String, Double> rmsCurrent = getABC4(DataFormat.select(rawData, OFS_RMS_CURRENT, LEN_RMS_CURRENT)); 
		rmsCurrentA = rmsCurrent.get("A");
		rmsCurrentB = rmsCurrent.get("B");
		rmsCurrentC = rmsCurrent.get("C");
	}
	
	public Double getRMSCurrentA() throws Exception {
		return rmsCurrentA;
	}
	
	public Double getRMSCurrentB() throws Exception {
		return rmsCurrentB;
	}
	
	public Double getRMSCurrentC() throws Exception {
		return rmsCurrentC;
	}
	
	private void parseRMSVoltage() throws Exception {
		Map<String, Double> rmsVoltage = getABC2(DataFormat.select(rawData, OFS_RMS_VOLTAGE, LEN_RMS_VOLTAGE));
		rmsVoltageA = rmsVoltage.get("A");
		rmsVoltageB = rmsVoltage.get("B");
		rmsVoltageC = rmsVoltage.get("C");
	}
	
	public Double getRMSVoltageA() throws Exception {
		return rmsVoltageA;
	}
	
	public Double getRMSVoltageB() throws Exception {
		return rmsVoltageB;
	}
	
	public Double getRMSVoltageC() throws Exception {
		return rmsVoltageC;
	}
	
	private void parsePowerFactor() throws Exception {
		Map<String, Double> powerFactor = getTotalABC2(DataFormat.select(rawData, OFS_POWER_FACTOR, LEN_POWER_FACTOR));
		powerFactorTotal = powerFactor.get("total");
		powerFactorA = powerFactor.get("A");
		powerFactorB = powerFactor.get("B");
		powerFactorC = powerFactor.get("C");
	}
	
	public Double getPowerFactorTotal() throws Exception {
		return powerFactorTotal;
	}
		
	public Double getPowerFactorA() throws Exception {
		return powerFactorA;
	}
	
	public Double getPowerFactorB() throws Exception {
		return powerFactorB;
	}
	
	public Double getPowerFactorC() throws Exception {
		return powerFactorC;
	}
	
	private void parseActivePower() throws Exception {
		Map<String, Double> activePower = getTotalABC4(DataFormat.select(rawData, OFS_ACTIVE_POWER, LEN_IS_DATA));
		activePowerTotal = activePower.get("total");
		activePowerA = activePower.get("A");
		activePowerB = activePower.get("B");
		activePowerC = activePower.get("C");
	}
	
	public Double getActivePowerTotal() throws Exception {
		return activePowerTotal;
	}
	
	public Double getActivePowerA() throws Exception {
		return activePowerA;
	}
	
	public Double getActivePowerB() throws Exception {
		return activePowerB;
	}
	
	public Double getActivePowerC() throws Exception {
		return activePowerC;
	}
	
	private void parseReactivePower() throws Exception {
		Map<String, Double> reactivePower = getTotalABC4(DataFormat.select(rawData, OFS_REACTIVE_POWER, LEN_IS_DATA));
		reactivePowerTotal = reactivePower.get("total");
		reactivePowerA = reactivePower.get("A");
		reactivePowerB = reactivePower.get("B");
		reactivePowerC = reactivePower.get("C");
	}
	
	public Double getReactivePowerTotal() throws Exception {
		return reactivePowerTotal;
	}
	
	public Double getReactivePowerA() throws Exception {
		return reactivePowerA;
	}
	
	public Double getReactivePowerB() throws Exception {
		return reactivePowerB;
	}
	
	public Double getReactivePowerC() throws Exception {
		return reactivePowerC;
	}
	
	private void parseApparentPower() throws Exception {
		Map<String, Double> apparentPower = getTotalABC4(DataFormat.select(rawData, OFS_APPARENT_POWER, LEN_IS_DATA));
		apparentPowerTotal = apparentPower.get("total");
		apparentPowerA = apparentPower.get("A");
		apparentPowerB = apparentPower.get("B");
		apparentPowerC = apparentPower.get("C");
	}
	
	public Double getApparentPowerTotal() throws Exception {
		return apparentPowerTotal;
	}
	
	public Double getApparentPowerA() throws Exception {
		return apparentPowerA;
	}
	
	public Double getApparentPowerB() throws Exception {
		return apparentPowerB;
	}
	
	public Double getApparentPowerC() throws Exception {
		return apparentPowerC;
	}
	
	public Double getFrequencyA() throws Exception {
		return frequencyA;
		
	}
	public Double getFrequencyTotal() throws Exception {
		return frequencyTotal;
	}
	
	private void parseAngle() throws Exception {
	    Map<String, Double> angle = getABC4(DataFormat.select(rawData, OFS_PHASE_ANGLE, LEN_PHASE_ANGLE));
	    angleA = angle.get("A");
	    angleB = angle.get("B");
	    angleC = angle.get("C");
	}
	
	public Double getAngleA() throws Exception {
		return angleA;
	}
	
	public Double getAngleB() throws Exception {
		return angleB;
	}
	
	public Double getAngleC() throws Exception {
		return angleC;
	}
	
	public String getCTPrimary() throws Exception {
		return ctPrimary;
	}
	
	public String getCTSecondary() throws Exception {
		return ctSecondary;
	}
	
	private double convertDouble4(byte[] data) {
	    byte[] b = data;
	    if (b[0] != 0xFF && b[1] == 0xFF)
	        return 0.0;
	    else {
	        return (double)DataFormat.getIntToBytes(b);
	    }
	}
	
	private double convertDouble2(byte[] data) {
	    return (double)DataFormat.getIntTo2Byte(data);
	}
	
	public Map<String,Double> getTotalABC4(byte[] instrument) throws Exception {
		Map<String,Double> data = new HashMap<String, Double>();
		int offset = 0;
		
		data.put("total", convertDouble4(DataFormat.select(instrument, offset, LEN_IS_DATA_VALUE))*0.001);
		offset += LEN_IS_DATA_VALUE;

		data.put("A", convertDouble4(DataFormat.select(instrument, offset, LEN_IS_DATA_VALUE))*0.001);
		offset += LEN_IS_DATA_VALUE;

		data.put("B", convertDouble4(DataFormat.select(instrument, offset, LEN_IS_DATA_VALUE))*0.001);
		offset += LEN_IS_DATA_VALUE;
		
		data.put("C", convertDouble4(DataFormat.select(instrument, offset, LEN_IS_DATA_VALUE))*0.001);
		
		return data;
	}
	
	public Map<String, Double> getABC4(byte[] instrument) throws Exception {
		Map<String,Double> data = new HashMap<String, Double>();
		int offset = 0;
	
		data.put("A", convertDouble4(DataFormat.select(instrument, offset, LEN_IS_DATA_VALUE))*0.1);
		offset += LEN_IS_DATA_VALUE;

		data.put("B", convertDouble4(DataFormat.select(instrument, offset, LEN_IS_DATA_VALUE))*0.1);
		offset += LEN_IS_DATA_VALUE;
		
		data.put("C", convertDouble4(DataFormat.select(instrument, offset, LEN_IS_DATA_VALUE))*0.1);
		
		return data;
	}
	
	public Map<String, Double> getABC2(byte[] instrument) throws Exception {
		Map<String, Double> data = new HashMap<String, Double>();
		int offset = 0;
	
		data.put("A", convertDouble2(DataFormat.select(instrument, offset, LEN_RMS_VOLTAGE_DATA))*0.1);
		offset += LEN_RMS_VOLTAGE_DATA;

		data.put("B", convertDouble2(DataFormat.select(instrument, offset, LEN_RMS_VOLTAGE_DATA))*0.1);
		offset += LEN_RMS_VOLTAGE_DATA;
		
		data.put("C", convertDouble2(DataFormat.select(instrument, offset, LEN_RMS_VOLTAGE_DATA))*0.1);
		
		return data;
	}
	
	
	public Map<String, Double> getTotalABC2(byte[] instrument) throws Exception {
		Map<String, Double> data = new HashMap<String, Double>();
		int offset = 0;
		
		data.put("total", convertDouble2(DataFormat.select(instrument, offset, LEN_POWER_FACTOR_DATA))*0.1);
		offset += LEN_RMS_VOLTAGE_DATA;
	
		data.put("A", convertDouble2(DataFormat.select(instrument, offset, LEN_POWER_FACTOR_DATA))*0.1);
		offset += LEN_RMS_VOLTAGE_DATA;

		data.put("B", convertDouble2(DataFormat.select(instrument, offset, LEN_POWER_FACTOR_DATA))*0.1);
		offset += LEN_RMS_VOLTAGE_DATA;
		
		data.put("C", convertDouble2(DataFormat.select(instrument, offset, LEN_POWER_FACTOR_DATA))*0.1);
		
		return data;
	}
	
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        try {
            sb.append("A1140_METER_INFO[\n")
              .append("  (METER_MODEL="   ).append(getMeterModel()).append("),\n")
              .append("  (METER_SERIAL="  ).append(getMeterSerial()).append("),\n")
              .append("  (METER_TIME="    ).append(getMeterTime()).append("),\n")
              .append("  (ACTIVE_POWER_TOTAL="  ).append(getActivePowerTotal().toString()).append("),\n")
              .append("  (ACTIVE_POWER_A=").append(getActivePowerA()).append("),\n")
              .append("  (ACTIVE_POWER_B=").append(getActivePowerB()).append("),\n")
              .append("  (ACTIVE_POWER_C=").append(getActivePowerC()).append("),\n")
              .append("  (REACTIVE_POWER_TOTAL=").append(getReactivePowerTotal().toString()).append("),\n")
              .append("  (REACTIVE_POWER_A=").append(getReactivePowerA().toString()).append("),\n")
              .append("  (REACTIVE_POWER_B=").append(getReactivePowerB().toString()).append("),\n")
              .append("  (REACTIVE_POWER_C=").append(getReactivePowerC().toString()).append("),\n")
              .append("  (APPARENT_POWER_TOTAL=").append(getApparentPowerTotal().toString()).append("),\n")
              .append("  (APPARENT_POWER_A=").append(getApparentPowerA().toString()).append("),\n")
              .append("  (APPARENT_POWER_B=").append(getApparentPowerB().toString()).append("),\n")
              .append("  (APPARENT_POWER_C=").append(getApparentPowerC().toString()).append("),\n")
              .append("  (RMS_CURRENT_A="   ).append(getRMSCurrentA().toString()).append("),\n")
              .append("  (RMS_CURRENT_B="   ).append(getRMSCurrentB().toString()).append("),\n")
              .append("  (RMS_CURRENT_C="   ).append(getRMSCurrentC().toString()).append("),\n")
              .append("  (RMS_VOLTAGE_A="   ).append(getRMSVoltageA().toString()).append("),\n")
              .append("  (RMS_VOLTAGE_B="   ).append(getRMSVoltageB().toString()).append("),\n")
              .append("  (RMS_VOLTAGE_C="   ).append(getRMSVoltageC().toString()).append("),\n")
              .append("  (FREQUENCY="     ).append(getFrequencyA().toString()).append("),\n")
              .append("  (POWER_FACTOR_TOTAL="  ).append(getPowerFactorTotal().toString()).append("),\n")
              .append("  (POWER_FACTOR_A="  ).append(getPowerFactorA().toString()).append("),\n")
              .append("  (POWER_FACTOR_B="  ).append(getPowerFactorB().toString()).append("),\n")
              .append("  (POWER_FACTOR_C="  ).append(getPowerFactorC().toString()).append("),\n")
              .append("  (PHASE_ANGLE_A="   ).append(getAngleA().toString()).append("),\n")
              .append("  (PHASE_ANGLE_B="   ).append(getAngleB().toString()).append("),\n")
              .append("  (PHASE_ANGLE_C="   ).append(getAngleC().toString()).append("),\n")
              .append("  (CT_PRIMARY="   ).append(getCTPrimary()).append("),\n")
              .append("  (CT_SECONDARY="   ).append(getCTSecondary()).append("),\n")
              .append("]\n");
        } catch (Exception e) {
            log.error("A1140_MODEM_INFO ERR => " + e.getMessage(),e);
        }
        return sb.toString();
    }
}
