package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.system.Supplier;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing ZEUPLS@ meter data
 *
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class PG200 extends MeterDataParser implements java.io.Serializable
{
	private static final long serialVersionUID = -8231071997242559649L;
	private static Log log = LogFactory.getLog(PG200.class);
	
	final private double PSI = 0.014504;
	
	public static enum FRAME_TYPE {
        MeteringDataFrame,
        EventFrame,
        CommandFrame;
    }
	
	private FRAME_TYPE frameType = null;
	
	private byte[] FRAMETYPE = new byte[1];
	/*
	 *  MeteringDataFrame
	 *  Battery Info
	 *  Network Info
	 *  Period
	 *  Metering Data #
	 */
	private byte[] VOLTAGE = new byte[2];
	private byte[] CURRENT = new byte[4];
	private byte[] CSQ = new byte[1];
	private byte[] PERIOD = new byte[1];
	private byte[] OFFSET = new byte[1];
	private byte[] BASEPULSE = new byte[4];
	private byte[] LP = new byte[2];
	private byte[] PRESSURE = new byte[2];
	
	/*
	 * EventDataFrame
	 * Event Count
	 * EV_DATA #
	 */
	private byte[] EVENTCOUNT = new byte[1];
	private byte[] EVENTTIME = new byte[7];
	private byte[] EVENTCODE = new byte[1];
	
	/*
	 * CommandDataFrame
	 * CommandType
	 * CommandCode
	 * Data
	 */
	private byte[] COMMANDTYPE = new byte[1];
	private byte[] COMMANDCODE = new byte[1];
	
	public enum COMMAND {
	    Ondemand,
	    PressureThresholdSet,
	    PressureSmaplingTimeSet,
	    PressurePeriodSet,
	    PulseLpPeriodSet,
	    MeteringUploadTimeSet,
	    OTA,
	    ModemEventLogReq,
	    ModemInformationReq,
	    MeterSerialSet,
	    CurrentPulseSet;
	}

    private String currentTime = null;//yyyymmddhhmmss
    private byte[] rawData = null;
    private Double meteringValue = null;
    private int lpPeriod = 1;
    private int pressurePeriod = 1;
    private String lpDate = null;//yyyymmdd
    private double batteryVolt = 0.0;
    private double batteryCurrent = 0.0;
    private int csq = 0;

    Map<String, Double> basepulselist = new HashMap<String, Double>();
    Map<String, List<Double>> lplist = new HashMap<String, List<Double>>();
    Map<String, List<Double>> pressurelist = new HashMap<String, List<Double>>();
    Map<Integer, List<String>> eventlog = new HashMap<Integer, List<String>>();
    
    /**
     * constructor
     */
    public PG200()
    {
    }

    /**
     * getRawData
     */
    public byte[] getRawData()
    {
        return rawData;
    }

    public String getLpDate() {
		return lpDate;
	}

	public void setLpDate(String lpDate) {
		this.lpDate = lpDate;
	}

	/**
     * get data length
     * @return length
     */
    public int getLength()
    {
        if(rawData == null) {
			return 0;
		}

        return rawData.length;
    }

    public String getCurrentTime()
    {
        return this.currentTime;
    }

    /**
     * parse meter data
     * @param data
     */
    public void parse(byte[] data) throws Exception
    {
        int pos = 0;

        System.arraycopy(data, pos, FRAMETYPE, 0, FRAMETYPE.length);//Word 2
        pos += FRAMETYPE.length;
        log.debug("FRMAMETYPE[" + Hex.decode(FRAMETYPE) + "]");
        
        switch (FRAME_TYPE.values()[(int)FRAMETYPE[0]]) {
        case MeteringDataFrame :
            frameType = FRAME_TYPE.MeteringDataFrame;
            parseMeteringData(pos, data);
            break;
        case EventFrame :
            frameType = FRAME_TYPE.EventFrame;
            parseEventLog(pos, data);
            break;
        case CommandFrame :
            frameType = FRAME_TYPE.CommandFrame;
        }
    }
    
    private void parseMeteringData(int pos, byte[] data) {
        System.arraycopy(data, pos, VOLTAGE, 0, VOLTAGE.length);
        pos += VOLTAGE.length;
        batteryVolt = DataUtil.getIntTo2Byte(VOLTAGE);
        
        System.arraycopy(data, pos, CURRENT, 0, CURRENT.length);
        pos += CURRENT.length;
        batteryCurrent = DataUtil.getLongToBytes(CURRENT);
        
        System.arraycopy(data, pos, CSQ, 0, CSQ.length);
        pos += CSQ.length;
        csq = DataUtil.getIntToBytes(CSQ);
        
        System.arraycopy(data, pos, PERIOD, 0, PERIOD.length);
        pos += PERIOD.length;
        
        // LP Period : 7 ~ 4 bit
        // Pressure Period : 3 ~ 0 bit
        pressurePeriod = PERIOD[0] & 0x0F;
        lpPeriod = PERIOD[0] >> 4;
        log.debug("PERIOD[" + Hex.decode(PERIOD) + "] LP_PERIOD[" + lpPeriod + 
                "] PRESSURE_PERIOD[" + pressurePeriod + "]");
        
        int lpCount = 24 * lpPeriod;
        int pressureCount = 24 * pressurePeriod;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        String basedate = null;
        
        int offset = 0;
        double basePulse = 0.0;
        double lp = 0.0;
        
        // N개의 검침데이타
        while (pos < data.length) {
            System.arraycopy(data, pos, OFFSET, 0, OFFSET.length);
            pos += OFFSET.length;
            offset = DataUtil.getIntToBytes(OFFSET);
            
            System.arraycopy(data, pos, BASEPULSE, 0, BASEPULSE.length);
            pos += BASEPULSE.length;
            
            basePulse = (double)DataUtil.getLongToBytes(BASEPULSE)/100.0;
            
            // offset이 0인 경우 최근값
            if (offset == 0) {
                currentTime = sdf.format(cal.getTime()) + "000000";
                meteringValue = basePulse;
            }
            
            // offset 0이 어제
            cal.add(Calendar.DAY_OF_MONTH, (offset*-1) - 1);
            basedate = sdf.format(cal.getTime());
            
            basepulselist.put(basedate, basePulse);
            List<Double> lps = new ArrayList<Double>();
            
            for (int i = 0; i < lpCount; i++) {
                System.arraycopy(data, pos, LP, 0, LP.length);
                pos += LP.length;
                lp = ((double)(DataUtil.getIntTo2Byte(LP)))/100.0;
                
                if (offset == 0)
                    meteringValue += lp;
                
                lps.add(lp);
            }
            lplist.put(basedate, lps);
            
            int pressure;
            lps = new ArrayList<Double>();
            
            for (int i = 0; i < pressureCount; i++) {
                System.arraycopy(data, pos, PRESSURE, 0, PRESSURE.length);
                pos += PRESSURE.length;
                
                // 음수
                if ((PRESSURE[0] & 0x80) > 0) {
                    // 보수를 취한 후 1을 더하고 -로 변경하면 된다.
                    pressure = DataUtil.getIntTo2Byte(new byte[]{(byte)(PRESSURE[0] ^ 0xFF), (byte)(PRESSURE[1] ^ 0xFF)}) + 1;
                    pressure *= -1;
                }
                else pressure = DataUtil.getIntTo2Byte(PRESSURE);
                lps.add((double)pressure/10.0*PSI);
            }
            pressurelist.put(basedate, lps);
        }
    }
    
    private void parseEventLog(int pos, byte[] data) {
        System.arraycopy(data, pos, EVENTCOUNT, 0, EVENTCOUNT.length);
        pos += EVENTCOUNT.length;
        int eventCnt = DataUtil.getIntToBytes(EVENTCOUNT);
        
        String eventtime = null;
        int eventcode = 0;
        
        for (int i = 0; i < eventCnt; i++) {
            System.arraycopy(data, pos, EVENTTIME, 0, EVENTTIME.length);
            pos += EVENTTIME.length;
            
            System.arraycopy(data, pos, EVENTCODE, 0, EVENTCODE.length);
            pos += EVENTCODE.length;
            
            eventtime = String.format("%4d%02d%02d%02d%02d%02d", 
                    DataUtil.getIntTo2Byte(new byte[]{EVENTTIME[0], EVENTTIME[1]}),
                    DataUtil.getIntToBytes(new byte[]{EVENTTIME[2]}),
                    DataUtil.getIntToBytes(new byte[]{EVENTTIME[3]}),
                    DataUtil.getIntToBytes(new byte[]{EVENTTIME[4]}),
                    DataUtil.getIntToBytes(new byte[]{EVENTTIME[5]}),
                    DataUtil.getIntToBytes(new byte[]{EVENTTIME[6]}));
            eventcode = DataUtil.getIntToBytes(EVENTCODE);
            
            List<String> eventtimelist = null;
            if (!eventlog.containsKey(eventcode)) {
                eventtimelist = new ArrayList<String>();
            }
            else eventtimelist = eventlog.get(eventcode);
            
            eventtimelist.add(eventtime);
            eventlog.put(eventcode, eventtimelist);
        }
    }
    
    private void parseCommand(int pos, byte[] data) {
        System.arraycopy(data, pos, COMMANDTYPE, 0, COMMANDTYPE.length);
        pos += COMMANDTYPE.length;
        
        System.arraycopy(data, pos, COMMANDCODE, 0, COMMANDCODE.length);
        pos += COMMANDCODE.length;
        
        byte[] bx = new byte[data.length - 2];
        System.arraycopy(data, pos, bx, 0, bx.length);
        pos += bx.length;
    }
    
    public int getPeriod() {
        return lpPeriod;
    }

    /**
     * get String
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("PG200 DATA[");
        sb.append("(meteringValue=").append(meteringValue).append("),");
        sb.append("(lpPeriod=").append(lpPeriod).append("),");
        sb.append("(pressurePeriod=").append(pressurePeriod).append("),");
        sb.append("(csq=").append(csq).append("),");
        /*
        if(lpPeriod!=0){
            sb.append("(lp=(");

            int interval = 60;
            if(lpPeriod == 1)
                interval = 60;
            else if (lpPeriod == 2)
                interval = 30;
            else if (lpPeriod == 4)
                interval = 15;
            int hour=0;
            int minute=0;
            String strHour="";
            String strMinute="";
            for (int i = 0; i < lpData.length; i++) {
                if(minute>=60){
                    hour++;
                    minute=0;
                }
                if(hour<10){
                    strHour="0"+hour;
                } else {
                    strHour=hour+"";
                }
                if(minute<10){
                    strMinute="0"+minute;
                } else {
                    strMinute=minute+"";
                }
                sb.append("TIME["+strHour+strMinute +"] = ["+ lpData[i]+"], ");
                minute+=interval;
            }
        }
        */

        sb.append(")\n");
        sb.append("]\n");

        return sb.toString();
    }

    /**
     * get Data
     */
    @Override
    public LinkedHashMap<String, String> getData()
    {
        LinkedHashMap<String, String> res = new LinkedHashMap<String, String>(16,0.75f,false);
        DecimalFormat df3 = null;
        
        SimpleDateFormat sdf14=null;
        SimpleDateFormat sdf12=null;
        SimpleDateFormat sdf8=null;
		 
		if(meter!=null && meter.getSupplier()!=null){
			Supplier supplier = meter.getSupplier();
			if(supplier !=null){
				String lang = supplier.getLang().getCode_2letter();
				String country = supplier.getCountry().getCode_2letter();
				
				df3 = TimeLocaleUtil.getDecimalFormat(supplier);
				sdf14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
				sdf12 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(12, lang, country));
				sdf8 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(8, lang, country));
			}
		} else{
			//locail 정보가 없을때는 기본 포멧을 사용한다.
			df3 = new DecimalFormat();
			sdf14 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			sdf12 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			sdf8 = new SimpleDateFormat("yyyy/MM/dd");
		}
        
		try {
		    
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			res.put("Current Time", sdf14.format(df.parse(currentTime))); // yyyymmddhhmmss
			df = new SimpleDateFormat("yyyyMMdd");
			res.put("Metering Time", sdf8.format(df.parse(lpDate)));
			res.put("Metering Value", "" + df3.format(meteringValue));
			res.put("LP Period", "" + lpPeriod);
			res.put("Pressure Period", "" + pressurePeriod);
			res.put("CSQ", "" + csq);
			Calendar cal = Calendar.getInstance();
			df = new SimpleDateFormat("yyyyMMddHHmm");
			String basedate = null;
			int idx = 0;
			for (Iterator<String> i = basepulselist.keySet().iterator(); i.hasNext(); ) {
			    basedate = i.next();
			    cal.setTime(df.parse(basedate+"0000"));
			    res.put("BaseDate"+idx, sdf8.format(cal.getTime()));
			    res.put("BasePulse"+idx, df3.format(basepulselist.get(basedate)));
			    res.put("LPDate"+idx, "LP");
			    List<Double> lp = lplist.get(basedate);
			    List<Double> pressure = pressurelist.get(basedate);
			    for (int j = 0; j < lp.size(); j++) {
			        res.put(sdf12.format(cal.getTime()), "lp="+df3.format(lp.get(j))+", pressure="+df3.format(pressure.get(j)));
			    }
			}
		} catch (Exception e) {
			log.warn("Get Data Error=>", e);
		}
        return res;
    }

    public Double getMeteringValue()
    {
        return meteringValue;
    }

    public String getMeterId()
    {
        return meter.getMdsId();
    }

    public double getBatteryVolt()
    {
        return batteryVolt;
    }

    public int getCsq()
    {
        return csq;
    }

    public int getLpPeriod()
    {
        return this.lpPeriod;
    }

	public double getBatteryVoltage()
    {
        return batteryVolt;
    }

    public double getBatteryCurrent()
    {
        return batteryCurrent;
    }

    @Override
    public int getFlag() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setFlag(int flag) {
        // TODO Auto-generated method stub
        
    }
    
    public Map<String, Double> listBasePulse() {
        return basepulselist;
    }
    
    public Map<String, List<Double>> listLp() {
        return lplist;
    }
    
    public Map<String, List<Double>> listPressure() {
        return pressurelist;
    }
    
    public Map<Integer, List<String>> listEventLog() {
        return eventlog;
    }
    
    public int getPressurePeriod() {
        return this.pressurePeriod;
    }
    
    public FRAME_TYPE getFrameType() {
        return this.frameType;
    }
}
