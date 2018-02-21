package com.aimir.fep.meter.parser;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.MeteringFail;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.mvm.ChannelConfig;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.MeterConfig;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing Kamstrup 162 Meter Data
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class Kamstrup162 extends MeterDataParser implements java.io.Serializable
{
    private static final long serialVersionUID = 1974450760100885924L;

    private static Log log = LogFactory.getLog(Kamstrup162.class);

    private byte[] TIMEZONE = new byte[2];
    private byte[] DST = new byte[2];
    private byte[] YEAR = new byte[2];
    private byte[] MONTH = new byte[1];
    private byte[] DAY = new byte[1];
    private byte[] HOUR = new byte[1];
    private byte[] MINUTE = new byte[1];
    private byte[] SECOND = new byte[1];
    private byte[] CURPULSE = new byte[4];
    private byte[] LPPERIOD = new byte[1];
    private byte[] UNIT = new byte[1];
    private byte[] METERCONSTANT = new byte[2];
    private byte[] DATECNT = new byte[2];
    // private byte[] LPPOINTER = new byte[1];
    private byte[] LPYEAR = new byte[2];
    private byte[] LPMONTH = new byte[1];
    private byte[] LPDAY = new byte[1];
    private byte[] BASEPULSE = new byte[4];
    private byte[] LP = new byte[2];
    private byte[] ERRORCODE = new byte[2];

    private byte[] rawData = null;
    private Double meteringValue = null;
    private int flag = 0;
    private String meterId = null;
    private int period = 0;
    private int errorCode = 0;
    private int unit = 0;
    private double meterConstant = 0.0;
    private double curPulse = 0.0;

    private Kamstrup kamstrupMeta = null;
    private EventLogData[] eventlogdata = null;

    private ModemLPData[] lpData = null;

    public Kamstrup162() {
    }

    /**
     * constructor
     */
    public Kamstrup162(String meterId)
    {
        this.meterId = meterId;
    }

    /**
     * getRawData
     */
    public byte[] getRawData()
    {
        return rawData;
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

    public Kamstrup getKamstrupMeta()
    {
        return kamstrupMeta;
    }

    public void setKamstrupMeta(Kamstrup kamstrupMeta)
    {
        this.kamstrupMeta = kamstrupMeta;
    }

    public void parse(byte[] data) throws Exception
    {
        parseModem(data);
    }
    
    /**
     * parse meter mesurement data
     * @param data
     */
    public void parseModem(byte[] data) throws Exception
    {
        int pos = 0;

        System.arraycopy(data, pos, TIMEZONE, 0, TIMEZONE.length);
        pos += TIMEZONE.length;
        int timeZone = DataUtil.getIntTo2Byte(TIMEZONE);
        log.debug("TIMEZONE[" + timeZone + "]");

        System.arraycopy(data, pos, DST,0, DST.length);
        pos += DST.length;
        int dst = DataUtil.getIntTo2Byte(DST);
        log.debug("DST[" + dst + "]");

        System.arraycopy(data, pos, YEAR, 0, YEAR.length);
        pos += YEAR.length;
        DataUtil.convertEndian(YEAR);
        int year = DataUtil.getIntTo2Byte(YEAR);
        log.debug("YEAR[" + year + "]");

        System.arraycopy(data, pos, MONTH, 0, MONTH.length);
        pos += MONTH.length;
        int month = DataUtil.getIntToBytes(MONTH);
        log.debug("MONTH[" + month + "]");

        System.arraycopy(data, pos, DAY, 0, DAY.length);
        pos += DAY.length;
        int day = DataUtil.getIntToBytes(DAY);
        log.debug("DAY[" + day + "]");

        System.arraycopy(data, pos, HOUR, 0, HOUR.length);
        pos += HOUR.length;
        int hour = DataUtil.getIntToBytes(HOUR);
        log.debug("HOUR[" + hour + "]");

        System.arraycopy(data, pos, MINUTE, 0, MINUTE.length);
        pos += MINUTE.length;
        int minute = DataUtil.getIntToBytes(MINUTE);
        log.debug("MINUTE[" + minute + "]");

        System.arraycopy(data, pos, SECOND, 0, SECOND.length);
        pos += SECOND.length;
        int second = DataUtil.getIntToBytes(SECOND);
        log.debug("SECOND[" + second + "]");

        /*timestamp = Integer.toString(year)
        + (month < 10? "0"+month:""+month)
        + (day < 10? "0"+day:""+day)
        + (hour < 10? "0"+hour:""+hour)
        + (minute < 10? "0"+minute:""+minute)
        + (second < 10? "0"+second:""+second);*/

        System.arraycopy(data, pos, CURPULSE, 0, CURPULSE.length);
        pos += CURPULSE.length;
        DataUtil.convertEndian(CURPULSE);
        meteringValue = new Double(DataUtil.getLongToBytes(CURPULSE));
        log.debug("CURPULSE[" + meteringValue + "]");
        // get real pulse
        /*
         * lucky initpulse 0 maxpulse 0 ....
        lp = IUtil.getRealPulseValue(this.meterId, lp);
        log.debug("REALPULSE[" + lp + "]");
         */

        // from here, lp
        System.arraycopy(data, pos, LPPERIOD, 0, LPPERIOD.length);
        pos += LPPERIOD.length;
        period = DataUtil.getIntToBytes(LPPERIOD);
        log.debug("LPPERIOD[" + period + "]");

        if (period == 0) {
            System.arraycopy(data, pos, ERRORCODE, 0, ERRORCODE.length);
            pos += ERRORCODE.length;
            DataUtil.convertEndian(ERRORCODE);
            errorCode = DataUtil.getIntTo2Byte(ERRORCODE);
        }
        else {
            System.arraycopy(data, pos, UNIT, 0, UNIT.length);
            pos += UNIT.length;
            unit = DataUtil.getIntToBytes(UNIT);
            log.debug("UNIT[" + unit + "]");
            System.arraycopy(data, pos, METERCONSTANT, 0, METERCONSTANT.length);
            pos += METERCONSTANT.length;
            DataUtil.convertEndian(METERCONSTANT);
            meterConstant = DataUtil.getIntTo2Byte(METERCONSTANT);
            log.debug("METERCONSTANT[" + meterConstant + "]");
            if (meter != null)
                meter.setPulseConstant(meterConstant);
            curPulse = (double)DataUtil.getLongToBytes(CURPULSE) / meterConstant;
            meteringValue = curPulse;
            log.debug("CURPULSE[" + meteringValue + "]");
            System.arraycopy(data, pos, DATECNT, 0, DATECNT.length);
            pos += DATECNT.length;
            DataUtil.convertEndian(DATECNT);
            int datecnt = DataUtil.getIntTo2Byte(DATECNT);
            log.debug("DATECNT[" + datecnt + "]");
            int lpLength = LPYEAR.length + LPMONTH.length + LPDAY.length + BASEPULSE.length + (48 * period);

            int lpInterval = 60 / period;
            
            byte[] bx = new byte[datecnt * lpLength];
            System.arraycopy(data, pos, bx, 0, bx.length);
            pos += bx.length;

            byte[] km = new byte[data.length - pos];
            System.arraycopy(data, pos, km, 0, km.length);
            kamstrupMeta = new Kamstrup();
            kamstrupMeta.setMeter(meter);
            kamstrupMeta.parse(km);
            meterTime = kamstrupMeta.getMeterTime();

            lpData = new ModemLPData[datecnt];
            pos = 0;

            // 한 주기 만큼 뺀 시간을 가져온다. 당일 검침데이타는 한 주기 전 시간까지의 데이타만 사용한다.
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -1*lpInterval);
            String yyyymmddhhmm = DateTimeUtil.getDateString(cal.getTime()).substring(0, 12);
            String lpdatetime = null;
            List<Double> lplist = null;
            double lp = 0.0;
            for (int i = 0; i < datecnt; i++) {
                lplist = new ArrayList<Double>();
                System.arraycopy(bx, pos, LPYEAR, 0, LPYEAR.length);
                pos += LPYEAR.length;
                System.arraycopy(bx, pos, LPMONTH, 0, LPMONTH.length);
                pos += LPMONTH.length;
                System.arraycopy(bx, pos, LPDAY, 0, LPDAY.length);
                pos += LPDAY.length;
                System.arraycopy(bx, pos, BASEPULSE, 0, BASEPULSE.length);
                pos += BASEPULSE.length;
                year = DataUtil.getIntToBytes(LPYEAR);
                month = DataUtil.getIntToBytes(LPMONTH);
                day = DataUtil.getIntToBytes(LPDAY);

                lpData[i] = new ModemLPData();
                lpData[i].setLpDate(String.format("%04d%02d%02d%02d%02d", year, month, day, 0, 0));
                lpData[i].setBasePulse(new double[]{(double)DataUtil.getLongToBytes(BASEPULSE) / meterConstant});
                for (int h=0, m=0; ;m+=lpInterval) {
                    if (m >= 60) {
                        h++;
                        m = 0;
                    }
                    
                    if (h == 24) break;
                    
                    lpdatetime = lpData[i].getLpDate().substring(0, 8) + String.format("%02d%02d", h, m);
                    if (lpdatetime.compareTo(yyyymmddhhmm) < 0) {
                        System.arraycopy(bx, pos, LP, 0, LP.length);
                        pos += LP.length;
                        lp = (double)DataUtil.getIntTo2Byte(LP);
                        if (lp != 65535) {
                            if (meter != null)
                                lp /= meter.getPulseConstant();
                            else
                                lp /= 100.0;
                        }
                        lplist.add(lp);
                    }
                    else pos += LP.length;
                }
                lpData[i].setLp(new double[1][lplist.size()]);
                for (int j = 0; j < lpData[i].getLp().length; j++) {
                    lpData[i].getLp()[0][j] = lplist.get(j);
                }
                log.debug(lpData[i].toString());
            }
            
            // 2013.05.14
            // 스웨덴과 가나 4채널, 2채널 지원
            if (kamstrupMeta.getModemLPData().length != 0) {
                ModemLPData[] _lpData = kamstrupMeta.getModemLPData();
                
                // 메타 정보 lp 주기가 0이 아니면 모뎀 프로토콜의 값이 이용하면 안된다. 
                if (kamstrupMeta.getLpInterval() != 0) {
                    // meter.setLpInterval(kamstrupMeta.getLpInterval());
                    period = 60 / kamstrupMeta.getLpInterval();
                }
                
                Map<String, ModemLPData> _lpMap = new HashMap<String, ModemLPData>();
                for (ModemLPData l : _lpData) {
                    _lpMap.put(l.getLpDate(), l);
                }
                
                _lpData = _lpMap.values().toArray(new ModemLPData[0]);
                Arrays.sort(_lpData, new Comparator() {

                    @Override
                    public int compare(Object o1, Object o2) {
                        ModemLPData m1 = (ModemLPData)o1;
                        ModemLPData m2 = (ModemLPData)o2;
                        return m1.getLpDate().compareTo(m2.getLpDate());
                    }
                    
                });
                
                // lp를 생성해야 한다.
                if (_lpData.length > 1) {
                 // LP 가 연속이 아닐 수 있다.
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    Map<Integer, List<ModemLPData>> lpMap = new HashMap<Integer, List<ModemLPData>>();
                    cal.setTime(sdf.parse(_lpData[0].getLpDate()));
                    List<ModemLPData> _lplist = new ArrayList<ModemLPData>();
                    int lpmapidx = 0;
                    for (int i = 0; i < _lpData.length; i++) {
                        lpdatetime = sdf.format(cal.getTime());
                        if (lpdatetime.equals(_lpData[i].getLpDate())) {
                            _lplist.add(_lpData[i]);
                        }
                        else {
                            log.info("TURNING DATE[" + lpdatetime + ", " + _lpData[i].getLpDate() + "]");
                            lpMap.put(lpmapidx++, _lplist);
                            log.info("LP MAP SIZE[" + lpMap.size() + "] LP SIZE[" + _lplist.size() + "]");
                            _lplist = new ArrayList<ModemLPData>();
                            _lplist.add(_lpData[i]);
                            cal.setTime(sdf.parse(_lpData[i].getLpDate()));
                        }
                        cal.add(Calendar.MINUTE, kamstrupMeta.getLpInterval());
                    }
                    lpMap.put(lpmapidx++, _lplist);
                    log.info("LP MAP SIZE[" + lpMap.size() + "] LP SIZE[" + _lplist.size() + "]");
                    
                    // Map에 있는 연속된 LP 데이타를 이용하여 채널별 lp를 구한다.
                    lpData = new ModemLPData[lpmapidx];
                    ModemLPData slp1 = null;
                    ModemLPData slp2 = null;
                    for (int i = 0; i < lpmapidx; i++) {
                        _lplist = lpMap.get(i);
                        // 첫번째 데이타에 채널별 사용량을 넣는다.
                        // _lplist의 사이즈가 1이면 스킵한다.
                        if (_lplist.size() == 1) continue;
                        lpData[i] = _lplist.get(0);
                        lpData[i].setLp(new double[lpData[i].getBasePulse().length][_lplist.size()-1]);
                        for (int s = 0; s < _lplist.size()-1; s++) {
                            slp1 = _lplist.get(s);
                            slp2 = _lplist.get(s+1);
                            
                            lpData[i].setBasePulse(slp1.getBasePulse());
                            for (int ch = 0; ch < lpData[i].getBasePulse().length; ch++) {
                                // log.debug("i:"+i+", ch:"+ch+", s:"+s);
                                lpData[i].getLp()[ch][s] = slp2.getBasePulse()[ch] - slp1.getBasePulse()[ch];
                            }
                        }
                        log.debug(lpData[i].toString());
                    }
                }
            }
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
        return period;
    }

    public MeteringFail getMeteringFail() {

        MeteringFail meteringFail = null;
        if(this.errorCode > 0){
             meteringFail = new MeteringFail();
             meteringFail.setModemErrCode(this.errorCode);
             meteringFail.setModemErrCodeName(NURI_T002.getMODEM_ERROR_NAME(this.errorCode));
             return meteringFail;
        }else{
            return null;
        }
    }

    /**
     * get String
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("Kamstrup162 DATA[");
        sb.append("(meteringValue=").append(meteringValue).append("),");
        sb.append("(lp=(");
        for(int i = 0 ; i < 24 ; i++)
        {
            // sb.append("Hour["+i+"]=["+preDayLPValue[i]+"], ");
        }
        sb.append(")\n");
        sb.append("]\n");

        return sb.toString();
    }

    /**
     * get Data
     */
    @SuppressWarnings("unchecked")
    @Override
    public LinkedHashMap getData()
    {
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
        
        LinkedHashMap res = new LinkedHashMap(16,0.75f,false);
        try {
            String meteringTime = null;
            if(super.meteringTime != null)
            	meteringTime = datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(super.meteringTime));
            //res.put("name","ZEUPLS");
            res.put("Metering Time", meteringTime);
            res.put("Metering Value(kWh)",""+decimalf.format(meteringValue));
            // res.put("Unit", unit);
            res.put("Meter Constant", decimalf.format(meterConstant));
            // res.put("Current Pulse(kW)", decimalf.format(curPulse));
    
            int interval = 60 / period;
            String lpTime = "";
            Calendar cal = Calendar.getInstance();
            
            if (lpData != null) {
                for (int i = 0; i < lpData.length; i++) {
                    if (lpData[i] == null) continue;
                    
                    cal.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lpData[i].getLpDate()));
                    res.put(i+") BasePulse Time", datef14.format(cal.getTime()));
                    for (int ch = 0; ch < lpData[i].getBasePulse().length; ch++) {
                        res.put(i+") BasePulse(kWh, kvarh) ch"+ch, decimalf.format(lpData[i].getBasePulse()[ch]));
                    }
                    res.put("", "");
                    res.put(i+") LP Time", "LP Value(kWh, kvarh)");
                    for (int j = 0; j < lpData[i].getLp()[0].length; cal.add(Calendar.MINUTE, interval), j++) {
                        lpTime = datef14.format(cal.getTime());
                        // if (DateTimeUtil.getDateString(cal.getTime()).compareTo(meteringTime) > 0) break;
                        for (int ch = 0; ch < lpData[i].getLp().length; ch++) {
                            res.put(i+") "+lpTime +" ch"+ch,
                                    decimalf.format((lpData[i].getLp()[ch][j]==65535? 0:lpData[i].getLp()[ch][j])));
                        }
                    }
                }
            }
            res.put("Kamstrup Meta Information", "");
            res.putAll(kamstrupMeta.getData(meter));
        }
        catch (Exception e) {
            log.warn(e);
        }
        
        return res;
    }

    public Double getMeteringValue()
    {
        // TODO Auto-generated method stub
        return meteringValue;
    }

    public String getMeterId()
    {
        // TODO Auto-generated method stub
        return meterId;
    }

    public ModemLPData[] getLpData() {
        return lpData;
    }

    public EventLogData[] getEventLog(){
        return eventlogdata;
    }
}
