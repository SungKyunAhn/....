package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.system.Supplier;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing TS pulse meter data
 *
 * Namespace TS의 펄스식 미터 파서
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2015-8-25 15:59:15 +0900 $,
 */
@Service
public class TS_PulseMeter extends MeterDataParser implements java.io.Serializable
{

    private static final long serialVersionUID = -3057333349317098600L;

    private static Log log = LogFactory.getLog(ZEUPLS.class);
    
    protected byte[] BATTERYVOLT = new byte[2];
    protected byte[] BATTERYCURRENT = new byte[4];
    protected byte[] CURRENT_PULSE = new byte[4];
    protected byte[] LPPERIOD = new byte[1];
    protected byte[] MDCOUNT = new byte[1];
    
    protected byte[] OFFSET= new byte[1];
    protected byte[] BASETIME = new byte[4];
    protected byte[] BASEPULSE = new byte[4];
    protected byte[] LP = new byte[2];

    protected byte[] rawData = null;
    protected Double meteringValue = null;
    protected int flag = 0;
    protected int lpPeriod = 1;
    protected String lpDate = null;//yyyymmdd
    protected double batteryVolt = 0.0;
    protected double batteryCurrent = 0.0;
    protected ModemLPData[] lpData = null;
    protected BatteryLog[] batteryLogs;
    
    /**
     * constructor
     */
    public TS_PulseMeter()
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

    /**
     * parse meter mesurement data
     * @param data
     */
    public void parse(byte[] data) throws Exception
    {
        log.debug(Hex.decode(data));
        
        Double pulseConst = 1d;
        if(meter.getPulseConstant() != null && meter.getPulseConstant() > 0){
            pulseConst = meter.getPulseConstant();
        }
        
        int pos = 0;

        System.arraycopy(data, pos, BATTERYVOLT, 0, BATTERYVOLT.length);
        pos += BATTERYVOLT.length;
        batteryVolt = DataUtil.getIntTo2Byte(BATTERYVOLT);
        log.debug("BATTERY_VOLT[" + batteryVolt + "]");

        System.arraycopy(data, pos, BATTERYCURRENT,0, BATTERYCURRENT.length);
        pos += BATTERYCURRENT.length;
        batteryCurrent = DataUtil.getIntTo4Byte(BATTERYCURRENT);
        log.debug("BATTERY_CURRENT[" + batteryCurrent + "]");
        
        System.arraycopy(data, pos, CURRENT_PULSE, 0, CURRENT_PULSE.length);
        pos += CURRENT_PULSE.length;
        meteringValue = DataUtil.getIntTo4Byte(CURRENT_PULSE) / pulseConst;
        log.debug("CURRENT_PULSE[" + meteringValue + "]");

        System.arraycopy(data, pos, LPPERIOD, 0, LPPERIOD.length);
        pos += LPPERIOD.length;
        lpPeriod = DataUtil.getIntToBytes(LPPERIOD);
        log.debug("LP_PERIOD[" + lpPeriod + "]");

        System.arraycopy(data, pos, MDCOUNT, 0, MDCOUNT.length);
        pos += MDCOUNT.length;
        int mdCount = DataUtil.getIntToBytes(MDCOUNT);
        log.debug("MD_COUNT[" + mdCount + "]");

        lpData = new ModemLPData[mdCount];
        
        String baseTime = null;
        double basePulse = 0.0;
        int lpCount = lpPeriod * 24;
        int offset = 0;
        for (int i = 0; i < mdCount; i++) {
            lpData[i] = new ModemLPData();
            
            System.arraycopy(data, pos, OFFSET, 0, OFFSET.length);
            pos += OFFSET.length;
            log.debug("OFFSET[" + offset + "]");
            
            if (offset > DataUtil.getIntToBytes(OFFSET)) {
                // meteringValue를 만들기 이해 필요함.
                // recent = i;
                offset = DataUtil.getIntToBytes(OFFSET);
            }
            
            System.arraycopy(data, pos, BASETIME, 0, BASETIME.length);
            pos += BASETIME.length;
            
            baseTime = String.format("%4d%02d%02d", 
                    DataUtil.getIntTo2Byte(new byte[]{BASETIME[0], BASETIME[1]}),
                    DataUtil.getIntToByte(BASETIME[2]), DataUtil.getIntToByte(BASETIME[3]));
            lpData[i].setLpDate(baseTime);
            log.debug("BASE_TIME_RAW[" + Hex.decode(BASETIME) + "] [" + baseTime + "]");
            
            System.arraycopy(data, pos, BASEPULSE, 0, BASEPULSE.length);
            pos += BASEPULSE.length;
            basePulse = (double)DataUtil.getLongToBytes(BASEPULSE) / pulseConst;
            lpData[i].setBasePulse(new double[]{basePulse});
            log.debug("BASE_PULSE[" + basePulse + "]");
            
            lpData[i].setLp(new double[2][lpCount]);
            // 첫번째 채널은 지침값, 두번째 채널은 사용량으로 변경
            for (int j = 0; j < lpCount; j++) {
                System.arraycopy(data, pos, LP, 0, LP.length);
                pos += LP.length;
                if ((byte)LP[0] == (byte)0xFF || (byte)LP[1] == (byte)0xFF) {
                    lpData[i].getLp()[1][j] = -1;
                }
                else {
                    lpData[i].getLp()[1][j] = (double)DataUtil.getIntTo2Byte(LP) / pulseConst;
                }
                
                if (j == 0) {
                    if (lpData[i].getLp()[1][j] != -1)
                        lpData[i].getLp()[0][j] = basePulse + lpData[i].getLp()[1][j];
                    else
                        lpData[i].getLp()[0][j] = basePulse;
                }
                else {
                    if (lpData[i].getLp()[1][j] != -1)
                        lpData[i].getLp()[0][j] = lpData[i].getLp()[0][j-1] + lpData[i].getLp()[1][j];
                    else
                        lpData[i].getLp()[0][j] = lpData[i].getLp()[0][j-1];
                }
            }
        }

        /*
        meteringValue = lpData[recent].getBasePulse()[0];
        for (int i = 0; i < lpCount; i++) {
            if (lpData[recent].getLp()[0][i] != -1) {
                meteringValue += lpData[recent].getLp()[0][i];
            }
        }
        */
        
        batteryLogs = new BatteryLog[1];
        batteryLogs[0] = new BatteryLog();
        batteryLogs[0].setYyyymmdd(meteringTime.substring(0, 8));
        Object[][] values = {{meteringTime.substring(8, 12),
            batteryVolt, batteryCurrent, 0.0, 0.0, 0.0, 0.0, 0}};
        batteryLogs[0].setValues(values);
        
        setLpData(lpData);
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

        sb.append("TS_PULSEMETER DATA[");
        sb.append("(meteringValue=").append(meteringValue).append("),");
        sb.append("(lpPeriod=").append(lpPeriod).append("),");
        sb.append("(batteryVolt=").append(batteryVolt).append(')');
        sb.append("(batteryCurrent=").append(batteryCurrent).append(')');
        for (int i = 0; i < lpData.length; i++) {
            sb.append(lpData[i].toString());
        }
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
        
        
        //res.put("name","ZEUPLS2");
        try {
            
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            res.put("Metering Time", sdf14.format(df.parse(meteringTime))); // yyyymmddhhmmss
            df = new SimpleDateFormat("yyyyMMdd");
            res.put("Metering Value", "" + df3.format(meteringValue));
            res.put("LP Period", "" + lpPeriod);
            res.put("Battery Volt", ""+batteryVolt);
            res.put("Battery Current", ""+batteryCurrent);
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
                        res.put(sdf14.format(df.parse(String.format("%s%02d%02d%02d",
                                lpData[i].getLpDate(), hour, minute, 0))), 
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

    public double getBatteryVolt()
    {
        return batteryVolt;
    }

    public double getBatteryCurrent()
    {
        return batteryCurrent;
    }

    public int getLpPeriod()
    {
        return this.lpPeriod;
    }
    
    public BatteryLog[] getBatteryLog() {
        return this.batteryLogs;
    }
}
