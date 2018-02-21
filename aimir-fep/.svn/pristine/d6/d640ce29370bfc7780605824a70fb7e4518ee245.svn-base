package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * parsing HMU@ meter data
 *
 * @author Y.K Park (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2014-01-20 15:59:15 +0900 $,
 */
public class HMUParser extends MeterDataParser implements ModemParser, java.io.Serializable
{
	private static final long serialVersionUID = 5877126090389582970L;
	private static Log log = LogFactory.getLog(HMUParser.class);
    protected byte[] TIMEZONE = new byte[2];
    protected byte[] DST = new byte[2];
    protected byte[] CURRENTYEAR = new byte[2];
    protected byte[] CURRENTMONTH = new byte[1];
    protected byte[] CURRENTDAY = new byte[1];
    protected byte[] CURRENTHOUR = new byte[1];
    protected byte[] CURRENTMINUTE = new byte[1];
    protected byte[] CURRENTSECOND = new byte[1];

    protected byte[] OPERATINGDAY = new byte[2];
    protected byte[] ACTIVEMINUTE = new byte[2];
    protected byte[] BATTERYVOLT = new byte[2];
    protected byte[] CONSUMPTIONCURRENT = new byte[2];
    protected byte[] OFFSET= new byte[1];
    protected byte[] CURRENTPULSE = new byte[4];
    protected byte[] LPCHOICE = new byte[1];
    protected byte[] LPPERIOD = new byte[1];
    protected byte[] LPYEAR = new byte[2];
    protected byte[] LPMONTH = new byte[1];
    protected byte[] LPDAY = new byte[1];
    protected byte[] FWVERSION = new byte[1];
    protected byte[] FWBUILD = new byte[1];
    protected byte[] HWVERSION = new byte[1];
    protected byte[] SWVERSION = new byte[1];
    protected byte[] LQI = new byte[1];
    protected byte[] RSSI = new byte[1];
    protected byte[] NODEKIND = new byte[1];
    protected byte[] ALARMFLAG = new byte[1];
    protected byte[] NETWORKTYPE = new byte[1];
    protected byte[] BASEPULSE = new byte[4];
    protected byte[] LP = new byte[2];
    protected byte[] ENERGYLEVEL = new byte[1];

    protected String currentTime = null;//yyyymmddhhmmss
    protected byte[] rawData = null;
    protected Double meteringValue = null;
    protected int flag = 0;
    protected int lpPeriod = 1;
    protected String lpDate = null;//yyyymmdd
    protected double batteryVolt = 0.0;
    protected double consumptionCurrent = 0.0;
    protected int operatingDay = 0;
    protected int activeMinute = 0;
    protected double voltOffset = 0.0;
    protected ModemLPData[] lpData = null;
    protected int lpChoice = 0;
    protected String fwVersion = null;
    protected String fwBuild = null;
    protected String swVersion = null;
    protected String hwVersion = null;
    protected double lqi = 0.0;
    protected int rssi = 0;
    protected int nodeKind = 0;
    protected int alarmFlag = 0;
    protected int networkType = 0;
    protected int energyLevel = 0;

    protected BatteryLog[] batteryLogs;

    protected String mcuRevision = null;
    /**
     * constructor
     */
    public HMUParser()
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

	public void setLpData(ModemLPData[] lpData) {
		this.lpData = lpData;
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
     * parse meter mesurement data
     * @param data
     */
    public void parse(byte[] data) throws Exception
    {
        String systemTime = DateTimeUtil.getDateString(new Date());
        
        int pos = 0;

        System.arraycopy(data, pos, TIMEZONE, 0, TIMEZONE.length);//Word 2
        pos += TIMEZONE.length;
        int timeZone = DataUtil.getIntTo2Byte(TIMEZONE);
        log.debug("TIMEZONE[" + timeZone + "]");

        System.arraycopy(data, pos, DST,0, DST.length);//Word 2
        pos += DST.length;
        int dst = DataUtil.getIntTo2Byte(DST);
        log.debug("DST[" + dst + "]");

        System.arraycopy(data, pos, CURRENTYEAR, 0, CURRENTYEAR.length);//Word 2
        pos += CURRENTYEAR.length;
        int currentYear = DataUtil.getIntTo2Byte(CURRENTYEAR);
        log.debug("CURRENTYEAR[" + currentYear + "]");

        System.arraycopy(data, pos, CURRENTMONTH, 0, CURRENTMONTH.length);//Byte 1
        pos += CURRENTMONTH.length;
        int currentMonth = DataUtil.getIntToBytes(CURRENTMONTH);
        log.debug("CURRENTMONTH[" + currentMonth + "]");

        System.arraycopy(data, pos, CURRENTDAY, 0, CURRENTDAY.length);//Byte 1
        pos += CURRENTDAY.length;
        int currentDay = DataUtil.getIntToBytes(CURRENTDAY);
        log.debug("CURRENTDAY[" + currentDay + "]");

        System.arraycopy(data, pos, CURRENTHOUR, 0, CURRENTHOUR.length);//Byte 1
        pos += CURRENTHOUR.length;
        int currentHour = DataUtil.getIntToBytes(CURRENTHOUR);
        log.debug("CURRENTHOUR[" + currentHour + "]");

        System.arraycopy(data, pos, CURRENTMINUTE, 0, CURRENTMINUTE.length);//Byte 1
        pos += CURRENTMINUTE.length;
        int currentMinute = DataUtil.getIntToBytes(CURRENTMINUTE);
        log.debug("CURRENTMINUTE[" + currentMinute + "]");

        System.arraycopy(data, pos, CURRENTSECOND, 0, CURRENTSECOND.length);//Byte 1
        pos += CURRENTSECOND.length;
        int currentSecond = DataUtil.getIntToBytes(CURRENTSECOND);
        log.debug("CURRENTSECOND[" + currentSecond + "]");

        currentTime = Integer.toString(currentYear)
        + (currentMonth < 10? "0"+currentMonth:""+currentMonth)
        + (currentDay < 10? "0"+currentDay:""+currentDay)
        + (currentHour < 10? "0"+currentHour:""+currentHour)
        + (currentMinute < 10? "0"+currentMinute:""+currentMinute)
        + (currentSecond < 10? "0"+currentSecond:""+currentSecond);

        System.arraycopy(data, pos, OPERATINGDAY, 0, OPERATINGDAY.length);//Byte 2
        pos += OPERATINGDAY.length;
        operatingDay = DataUtil.getIntTo2Byte(OPERATINGDAY);
        log.debug("OPERATINGDAY[" + operatingDay + "]");

        System.arraycopy(data, pos, ACTIVEMINUTE, 0, ACTIVEMINUTE.length);//Byte 2
        pos += ACTIVEMINUTE.length;
        activeMinute = DataUtil.getIntTo2Byte(ACTIVEMINUTE);
        log.debug("ACTIVEMINUTE[" + activeMinute + "]");

        DecimalFormat df = new DecimalFormat("0.####");

        System.arraycopy(data, pos, BATTERYVOLT, 0, BATTERYVOLT.length);//Byte 2
        pos += BATTERYVOLT.length;
        batteryVolt =  Double.parseDouble(df.format((double)DataUtil.getIntTo2Byte(BATTERYVOLT) * 0.0001));
        log.debug("BATTERYVOLT[" + batteryVolt + "]");

        System.arraycopy(data, pos, CONSUMPTIONCURRENT, 0, CONSUMPTIONCURRENT.length);//Byte 2
        pos += CONSUMPTIONCURRENT.length;
        int _consumptionCurrent = DataUtil.getIntTo2Byte(CONSUMPTIONCURRENT);

        System.arraycopy(data, pos, OFFSET, 0, OFFSET.length);//Byte 1
        pos += OFFSET.length;
        int _voltOffset = DataUtil.getIntToBytes(OFFSET);
        voltOffset =  Double.parseDouble(df.format((double)_voltOffset * 0.0001));
        log.debug("OFFSET[" + voltOffset + "]");

        System.arraycopy(data, pos, CURRENTPULSE, 0, CURRENTPULSE.length);//Byte 4
        pos += CURRENTPULSE.length;
        meteringValue = new Double(DataUtil.getLongToBytes(CURRENTPULSE));
        
        // 보정하고 펄스 상수로 나누어 실제 검침값으로 변환한다.
        MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());
        Double pulseConst = 1d;
        if(meter.getPulseConstant() != null && meter.getPulseConstant() > 0){
        	pulseConst = meter.getPulseConstant();
        }
        meteringValue = meteringValue / meter.getPulseConstant();
        
        log.debug("CURRENTPULSE[" + meteringValue + "] RAW["+ new Double(DataUtil.getLongToBytes(CURRENTPULSE))+"]");

        System.arraycopy(data, pos, LPCHOICE, 0, LPCHOICE.length);//Byte 1
        pos += LPCHOICE.length;
        lpChoice = DataUtil.getIntToBytes(LPCHOICE);
        log.debug("LPCHOICE[" + lpChoice + "]");

        System.arraycopy(data, pos, LPPERIOD, 0, LPPERIOD.length);//Byte 1
        pos += LPPERIOD.length;
        lpPeriod = DataUtil.getIntToBytes(LPPERIOD);
        log.debug("LPPERIOD[" + lpPeriod + "]");

        // from here, lp
        if (lpPeriod == 0) {
            log.debug("LP Log Data is not exist");
        }
        else {
            mcuRevision = meter.getModem().getMcu().getSysSwRevision();
            System.arraycopy(data, pos, LPYEAR, 0, LPYEAR.length);
            pos += LPYEAR.length;
            System.arraycopy(data, pos, LPMONTH, 0, LPMONTH.length);
            pos += LPMONTH.length;
            System.arraycopy(data, pos, LPDAY, 0, LPDAY.length);
            pos += LPDAY.length;

            lpDate=Integer.toString(DataUtil.getIntTo2Byte(LPYEAR))
                    + (DataUtil.getIntToBytes(LPMONTH) < 10? "0"+DataUtil.getIntToBytes(LPMONTH):""+DataUtil.getIntToBytes(LPMONTH))
                    + (DataUtil.getIntToBytes(LPDAY) < 10? "0"+DataUtil.getIntToBytes(LPDAY):""+DataUtil.getIntToBytes(LPDAY));
            System.arraycopy(data, pos, BASEPULSE, 0, BASEPULSE.length);
            pos += BASEPULSE.length;
            int month = DataUtil.getIntToBytes(LPMONTH);
            int day = DataUtil.getIntToBytes(LPDAY);
            System.arraycopy(data, pos, FWVERSION, 0, FWVERSION.length);
            pos += FWVERSION.length;
            fwVersion = Hex.decode(FWVERSION);
            fwVersion = Double.parseDouble(fwVersion) / 10.0 + "";

            System.arraycopy(data, pos, FWBUILD, 0, FWBUILD.length);
            pos += FWBUILD.length;
            fwBuild = Hex.decode(FWBUILD);

            System.arraycopy(data, pos, HWVERSION, 0, HWVERSION.length);
            pos += HWVERSION.length;
            hwVersion = Hex.decode(HWVERSION);
            hwVersion = Double.parseDouble(hwVersion) / 10.0 + "";

            System.arraycopy(data, pos, SWVERSION, 0, SWVERSION.length);
            pos += SWVERSION.length;
            swVersion = Hex.decode(SWVERSION);
            swVersion = Double.parseDouble(swVersion) / 10.0 + "";

            // add LQI, RSSI
            // from 2008.04.23
            // applied above FW 2.1 and Build 10
            log.debug("mcuRevision["+mcuRevision+"], fwVersion["+fwVersion+"], fwBuild["+fwBuild+"]");

            System.arraycopy(data, pos, LQI, 0, LQI.length);
            pos += LQI.length;
            lqi = DataUtil.getIntToBytes(LQI);

            if (lqi <= 80) {
            }
            else if (lqi < 240) {
                lqi = 80.0 + (lqi-80.0)*0.125;
            }
			else {
				lqi = 100.0;
			}

            System.arraycopy(data, pos, RSSI, 0, RSSI.length);
            pos += RSSI.length;
            rssi = DataUtil.getIntToBytes(RSSI);

            System.arraycopy(data, pos, NODEKIND, 0, NODEKIND.length);
            pos += NODEKIND.length;
            nodeKind = DataUtil.getIntToBytes(NODEKIND);

            System.arraycopy(data, pos, ALARMFLAG, 0, ALARMFLAG.length);
            pos += ALARMFLAG.length;
            alarmFlag = DataUtil.getIntToBytes(ALARMFLAG);
            consumptionCurrent =  Double.parseDouble(df.format((double)_consumptionCurrent / 1000.0));

            System.arraycopy(data, pos, NETWORKTYPE, 0, NETWORKTYPE.length);
            pos += NETWORKTYPE.length;
            networkType = DataUtil.getIntToBytes(NETWORKTYPE);
            log.debug("NETWORKTYPE[" + networkType + "]");
            
            System.arraycopy(data, pos, ENERGYLEVEL, 0, ENERGYLEVEL.length);
            pos += ENERGYLEVEL.length;
            energyLevel = DataUtil.getIntToBytes(ENERGYLEVEL);
            log.debug("ENERGYLEVEL[" + energyLevel + "]");
            
            log.debug("CONSUMPTIONCURRENT[" + consumptionCurrent + "]");

            log.debug("data.length ="+data.length);
            log.debug("pos ="+pos);

            int lpdataCnt = 1;

            lpdataCnt = (data.length - pos) / ((24*lpPeriod*LP.length) + 8); // lpdate(4) + basepulse(4)
            lpData = new ModemLPData[lpdataCnt];

            log.debug("MCU REVISION[" + mcuRevision + "]");
            for (int i=0; i < lpdataCnt; i++) {
                lpData[i] = new ModemLPData();

                System.arraycopy(data, pos, LPYEAR, 0, LPYEAR.length);
                pos += LPYEAR.length;
                System.arraycopy(data, pos, LPMONTH, 0, LPMONTH.length);
                pos += LPMONTH.length;
                System.arraycopy(data, pos, LPDAY, 0, LPDAY.length);
                pos += LPDAY.length;
                System.arraycopy(data, pos, BASEPULSE, 0, BASEPULSE.length);
                pos += BASEPULSE.length;
                month = DataUtil.getIntToBytes(LPMONTH);
                day = DataUtil.getIntToBytes(LPDAY);
                lpData[i].setLpDate(String.format("%4d%02d%02d", DataUtil.getIntTo2Byte(LPYEAR), month, day));
                if (lpData[i].getLpDate().equals("65535255255")) {
                	lpData[i].setLpDate(TimeUtil.getPreDay(currentTime, lpChoice).substring(0, 8));
                }

                lpData[i].setBasePulse(new double[]{DataUtil.getLongToBytes(BASEPULSE)/pulseConst});
                
                if (lpData[i].getBasePulse()[0] > meteringValue) {
                    throw new Exception("BASEPULSE[" + lpData[i].getBasePulse()[0] +
                            "] greater than CURRENTPULSE[" + meteringValue + "]");
                }
                
                // 당일인 경우 현재시간의 한시간 전까지만 생성
                if (systemTime.substring(0,8).equals(lpData[i].getLpDate())) {
                	log.debug("currentHour : "+currentHour);
                	log.debug("currentMinute : "+currentMinute);
                	log.debug("lpPeriod : "+lpPeriod);                	
                    lpData[i].setLp(new double[1][currentHour*lpPeriod + (currentMinute / (60 / lpPeriod))]);
                }
                else {
                    lpData[i].setLp(new double[1][24*lpPeriod]);
                }
                log.debug("LP COUNT[" + lpData[i].getLp().length + "]");
                log.debug("LPDATE["+lpData[i].getLpDate()+"]");
                log.debug("BASEPULSE["+lpData[i].getBasePulse()[0]+"]");

                double usage = lpData[i].getBasePulse()[0];
                for (int j = 0; j < lpData[i].getLp()[0].length; j++) {
                    System.arraycopy(data, pos, LP, 0, LP.length);
                    pos += LP.length;
                    Double pulseConstant = meter.getPulseConstant()==null ? 1 : meter.getPulseConstant();
                	lpData[i].getLp()[0][j] = (double)DataUtil.getIntTo2Byte(LP) / pulseConstant;
                	if (meteringValue < usage + lpData[i].getLp()[0][j]) {
                	    log.warn("USAGE["+usage+ "] and LPDATA[" + lpData[i].getLp()[0][j]+" greater than CURRENTPULSE["+meteringValue+"] so set ZERO");
                	    lpData[i].getLp()[0][j] = 0.0;
                	    usage += lpData[i].getLp()[0][j];
                	}
                	log.debug("LPDATA["+lpData[i].getLp()[0][j]+"]");
                }
            }
            setLpData(lpData);
        }

        if (currentTime != null && currentTime.length() == 14) {
            batteryLogs = new BatteryLog[1];
            batteryLogs[0] = new BatteryLog();
            batteryLogs[0].setYyyymmdd(currentTime.substring(0, 8));
            Object[][] values = {{currentTime.substring(8, 12),
                batteryVolt, consumptionCurrent, voltOffset, 0.0, 0.0, 0.0, 0}};
            batteryLogs[0].setValues(values);
        }
        else {
            log.error("CURRENT TIME[" + currentTime + "] IS WRONG");
        }
    }

    /**
     * get flag
     * @return flag measurement flag
     */
    public int getFlag()
    {
        return this.flag;
    }

    /**
     * set flag
     * @param flag measurement flag
     */
    public void setFlag(int flag)
    {
        this.flag = flag;
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

        sb.append("HMU DATA[");
        sb.append("(meteringValue=").append(meteringValue).append("),");
        sb.append("(lpPeriod=").append(lpPeriod).append("),");
        sb.append("(hwVersion=").append(hwVersion).append("),");
        sb.append("(fwVersion=").append(fwVersion).append("),");
        sb.append("(fwBuild=").append(fwBuild).append("),");
        sb.append("(LQI=").append(lqi).append("),");
        sb.append("(RSSI=").append(rssi).append(')');
        sb.append("(NODEKIND=").append(nodeKind).append(')');
        sb.append("(ALARMFLAG=").append(alarmFlag).append(')');
        sb.append("(NETWORKTYPE=").append(networkType).append(')');
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
			res.put("HW Version", hwVersion);
			res.put("FW Version", swVersion);
			res.put("Build", fwBuild);
			res.put("LQI", "" + lqi);
			res.put("RSSI", "" + rssi);
			res.put("NODEKIND", "" + nodeKind);
			res.put("ALARMFLAG", "" + alarmFlag);
			res.put("NETWORKTYPE", "" + networkType);
			int interval = 60 / (lpPeriod != 0 ? lpPeriod : 1);
			int hour = 0;
			int minute = 0;
			log.debug("lpData.length: " + lpData.length);
			df = new SimpleDateFormat("yyyyMMddHHmm");
			for (int i = 0; i < lpData.length; i++) {
			    for (int ch = 0; ch < lpData[i].getLp().length; ch++) {
    				for (int j = 0; j < lpData[i].getLp()[ch].length; j++, minute += interval) {
    					if (minute >= 60) {
    						hour++;
    						minute = 0;
    					}
    					log.debug(lpData[i].getLp()[ch][j]);
    					res.put(sdf14.format(df.parse(String.format("%s%02d%02d%02d", lpDate, hour, minute, 0))), 
    					        ""+ df3.format(lpData[i].getLp()[ch][j]));
    				}
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

    public ModemLPData[] getLpData()
    {
        return lpData;
    }

    public int getOperatingDay()
    {
        return operatingDay;
    }

    public int getActiveMinute()
    {
        return activeMinute;
    }

    public double getBatteryVolt()
    {
        return batteryVolt;
    }

    public double getConsumptionCurrent()
    {
        return consumptionCurrent;
    }

    public double getVoltOffset()
    {
        return voltOffset;
    }

    public int getLpChoice()
    {
        return lpChoice;
    }

    public String getFwVersion()
    {
        return fwVersion;
    }

    public String getFwBuild()
    {
        return fwBuild;
    }

    public String getHwVersion()
    {
        return hwVersion;
    }

    public String getSwVersion()
    {
        return swVersion;
    }

    public int getLpPeriod()
    {
        return this.lpPeriod;
    }

    public double getLQI()
    {
        return this.lqi;
    }

    public int getRSSI()
    {
        return this.rssi;
    }

    public int getNodeKind()
    {
        return nodeKind;
    }

    public void setNodeKind(int nodeKind)
    {
        this.nodeKind = nodeKind;
    }

    public int getAlarmFlag()
    {
        return alarmFlag;
    }

    public void setAlarmFlag(int alarmFlag)
    {
        this.alarmFlag = alarmFlag;
    }

    public int getNetworkType() {
		return networkType;
	}

	public void setNetworkType(int networkType) {
		this.networkType = networkType;
	}

	public BatteryLog[] getBatteryLogs()
    {
        return batteryLogs;
    }

    public void setBatteryLogs(BatteryLog[] batteryLogs)
    {
        this.batteryLogs = batteryLogs;
    }
}
