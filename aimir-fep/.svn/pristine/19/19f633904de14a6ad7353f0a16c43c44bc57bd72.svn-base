package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * parsing ZEUPLS@ meter data
 *
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class GasPulseVC extends MeterDataParser implements ModemParser, java.io.Serializable
{

	private static final long serialVersionUID = 1677364846427064466L;
	private static Log log = LogFactory.getLog(GasPulseVC.class);
    private byte[] TIMEZONE = new byte[2];
    private byte[] DST = new byte[2];
    private byte[] CURRENTYEAR = new byte[2];
    private byte[] CURRENTMONTH = new byte[1];
    private byte[] CURRENTDAY = new byte[1];
    private byte[] CURRENTHOUR = new byte[1];
    private byte[] CURRENTMINUTE = new byte[1];
    private byte[] CURRENTSECOND = new byte[1];

    private byte[] METERSERIAL = new byte[6];
    private byte[] CONSUMPTIONCURRENT = new byte[2];
    private byte[] OFFSET= new byte[1];
    private byte[] CURRENTPULSE = new byte[4];
    private byte[] LPCHOICE = new byte[1];
    private byte[] LPPERIOD = new byte[1];
    private byte[] LPYEAR = new byte[2];
    private byte[] LPMONTH = new byte[1];
    private byte[] LPDAY = new byte[1];
    private byte[] BASEPULSE = new byte[4];
    private byte[] FWVERSION = new byte[1];
    private byte[] FWBUILD = new byte[1];
    private byte[] HWVERSION = new byte[1];
    private byte[] SWVERSION = new byte[1];
    private byte[] LQI = new byte[1];
    private byte[] RSSI = new byte[1];
    private byte[] NODEKIND = new byte[1];
    private byte[] ALARMFLAG = new byte[1];
    private byte[] NETWORKTYPE = new byte[1];

    private byte[] LP = new byte[2];

    private String currentTime = null;//yyyymmddhhmmss
    private String meterSerial = null;
    private byte[] rawData = null;
    private Double meteringValue = null;
    private int flag = 0;
    private int lpPeriod = 1;
    private String lpDate = null;//yyyymmdd
    private double batteryVolt = 0.0;
    private double consumptionCurrent = 0.0;
    //private int operatingDay = 0;
    //private int activeMinute = 0;
    private double voltOffset = 0.0;
    private ModemLPData[] lpData = null;
    private int lpChoice = 0;
    private String fwVersion = null;
    private String fwBuild = null;
    private String swVersion = null;
    private String hwVersion = null;
    private double lqi = 0.0;
    private int rssi = 0;
    private int nodeKind = 0;
    private int alarmFlag = 0;
    private int networkType = 0;


    private BatteryLog[] batteryLogs;

    //private String mcuRevision = null;
    /**
     * constructor
     */
    public GasPulseVC()
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
        if(rawData == null)
            return 0;

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
        DataUtil.convertEndian(CURRENTYEAR);
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

        System.arraycopy(data, pos, METERSERIAL, 0, METERSERIAL.length);//Byte 6
        pos += METERSERIAL.length;
        meterSerial = DataUtil.getBCDtoBytes(METERSERIAL);
        log.debug("METERSERIAL[" + meterSerial + "]");

        DecimalFormat df = new DecimalFormat("0.####");

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
        log.debug("CURRENTPULSE[" + meteringValue + "]");

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
            System.arraycopy(data, pos, LPYEAR, 0, LPYEAR.length);
            pos += LPYEAR.length;
            System.arraycopy(data, pos, LPMONTH, 0, LPMONTH.length);
            pos += LPMONTH.length;
            System.arraycopy(data, pos, LPDAY, 0, LPDAY.length);
            pos += LPDAY.length;
            DataUtil.convertEndian(LPYEAR);

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

            System.arraycopy(data, pos, LQI, 0, LQI.length);
            pos += LQI.length;
            lqi = DataUtil.getIntToBytes(LQI);

            if (lqi <= 80) {
            }
            else if (lqi < 240) {
                lqi = 80.0 + (lqi-80.0)*0.125;
            }
            else lqi = 100.0;

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
            log.debug("CONSUMPTIONCURRENT[" + consumptionCurrent + "]");

            log.debug("data.length ="+data.length);
            log.debug("pos ="+pos);

            int lpdataCnt = 1;

            lpdataCnt = (data.length - pos) / ((24*lpPeriod*LP.length)); 
            lpData = new ModemLPData[lpdataCnt];
            
            long basePulse = DataUtil.getLongToBytes(BASEPULSE);
            for (int i=0; i < lpdataCnt; i++) {
                lpData[i] = new ModemLPData();

                if(i == 0){
                	lpData[i].setLpDate(TimeUtil.getPreDay(lpDate+"000000", 1).substring(0, 8));
                }else{
                    lpData[i].setLpDate(lpDate);
                }            	

                if (basePulse > meteringValue) {
                    // log.warn("BASEPULSE[" + lpData[i].getBasePulse() + 
                    //        "] greater than CURRENTPULSE[" + lp + "] so set INITPULSE[" + initPulse + "]");
                    // lpData[i].setBasePulse(initPulse);
                    throw new Exception("BASEPULSE[" + lpData[i].getBasePulse() + 
                            "] greater than CURRENTPULSE[" + meteringValue + "]");
                }
                else {
                    lpData[i].setBasePulse(new double[]{basePulse});
                }
                
             // ModemLPData의 lp 배열구조가 변경됨. 주의
                lpData[i].setLp(new double[1][24*lpPeriod]);
                log.debug("LPDATE["+lpData[i].getLpDate()+"]");
                log.debug("BASEPULSE["+lpData[i].getBasePulse()+"]");


                for (int j = 0; j < lpData[i].getLp()[0].length; j++) {
                    System.arraycopy(data, pos, LP, 0, LP.length);
                    pos += LP.length;
                	lpData[i].getLp()[0][j] = DataUtil.getIntTo2Byte(LP);
                	if (meteringValue < basePulse + lpData[i].getLp()[0][j]) {
                	    log.warn("USAGE["+basePulse+ "] and LPDATA[" + lpData[i].getLp()[j]+
                	            " greater than CURRENTPULSE["+meteringValue+"] so set ZERO");
                	    lpData[i].getLp()[0][j] = 0.0;
                	    basePulse += lpData[i].getLp()[0][j];
                	}
                	log.debug("LPDATA["+lpData[i].getLp()[0][j]+"]");
                }
            }
            setLpData(lpData);
            //////////////////
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

        sb.append("GasPulseVC DATA[");
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
    public LinkedHashMap<String, String> getData()
    {
        LinkedHashMap<String, String> res = new LinkedHashMap<String, String>(16,0.75f,false);
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
        
        //res.put("name","GasPulseVC");
        res.put("Current Time", currentTime);
        res.put("Metering Time", lpDate);
        res.put("METERSERIAL", "" + meterSerial);
        res.put("Metering Value", ""+ df3.format(meteringValue));
        res.put("LP Period", ""+lpPeriod);
        res.put("HW Version", hwVersion);
        res.put("FW Version", swVersion);
        res.put("Build", fwBuild);
        res.put("LQI", "" + lqi);
        res.put("RSSI", "" + rssi);
        res.put("NODEKIND", "" + nodeKind);
        res.put("ALARMFLAG", "" + alarmFlag);
        res.put("NETWORKTYPE", "" + networkType);
        int interval = 60 / (lpPeriod != 0? lpPeriod:1);
        int hour=0;
        int minute=0;
        log.debug("lpData.length: "+lpData.length);
        for (int i = 0; i < lpData.length; i++) {
        	for(int j=0;j<lpData[i].getLp().length;j++,minute += interval){
	            if(minute>=60){
	                hour++;
	                minute=0;
	            }
	            for (int ch = 0; ch < lpData[i].getLp().length; ch++) {
    	            log.debug(lpData[i].getLp()[ch][j]);
    	            res.put(lpDate + String.format("%02d%02d", hour, minute)+"ch"+ch, 
    	                    ""+ df3.format(lpData[i].getLp()[ch][j]));
	            }
        	}
        }

        return res;
    }

    public Double getMeteringValue()
    {
        return meteringValue;
    }

    public String getMeterId()
    {
        return this.meterSerial;
    }

    public ModemLPData[] getLpData()
    {
        return lpData;
    }

    public String getMeterSerial()
    {
    	return meterSerial;
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
