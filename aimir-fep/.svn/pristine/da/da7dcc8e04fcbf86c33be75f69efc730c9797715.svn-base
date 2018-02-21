package com.aimir.fep.meter.saver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.Kamstrup;
import com.aimir.fep.meter.parser.MultiChannelParser.LPData;
import com.aimir.fep.meter.parser.SX2;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * SX2 저장 
 * 현재, Base, LP의 1,2 채널에 대해서 *4를 해야 한다.
 *  
 * @author elevas
 *
 */
@Service
public class SXMDSaver extends AbstractMDSaver {

    @Override
    protected boolean save(IMeasurementData md) throws Exception {
        SX2 parser = (SX2)md.getMeterDataParser();
        
        int interval = 60 / (parser.getPeriod() != 0? parser.getPeriod():1);
        if (parser.getMeter().getLpInterval() == null || 
                interval != parser.getMeter().getLpInterval())
            parser.getMeter().setLpInterval(interval);
        
        String time = parser.getTimestamp();
        /*
         * SX2 에 대한 PQ 정보가 없다.
        Kamstrup kamstrupMeta = parser.getKamstrupMeta();
        Instrument[] instrument = kamstrupMeta.getInstrument();
        
        savePowerQuality(parser.getMeter(), time, instrument, parser.getDeviceType(),
                parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
                */
        
        // 현재검침값에 *4를 한다.
        saveMeteringData(MeteringType.Recovery, md.getTimeStamp().substring(0,8),
                md.getTimeStamp().substring(8, 14), parser.getMeteringValue(),
                parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
        
        /*
        double[] lp = new double[parser.getLpData().length * parser.getLpData()[0].getLp().length];
        
        int lpcnt = 0;
        for (ModemLPData data : parser.getLpData()) {
            for (int i = 0 ; i < data.getLp().length; i++) {
                lp[lpcnt++] = data.getLp()[i];
            }
        }
        */

        int[] flaglist = null;
        for (LPData data : parser.getLpData()) {
            if (data.getLp().length == 0 || data.getLp()[0].length == 0) {
                log.warn("LP size is 0 then skip");
                continue;
            }
            
            flaglist = new int[data.getLp()[0].length];
            for (int flagcnt=0; flagcnt < flaglist.length; flagcnt++) {
                if (data.getLp()[0][flagcnt] == 65535) {
                    flaglist[flagcnt] = MeteringFlag.Fail.getFlag();
                    for (int ch = 0; ch < data.getLp().length; ch++)
                        data.setLp(ch, flagcnt, 0.0);
                }
                else {
                    flaglist[flagcnt] = MeteringFlag.Correct.getFlag();
                }
            }
            
            saveLPData(MeteringType.Recovery, data.getLpDate(), "0000",
                    data.getLp(), flaglist, data.getBasePulse(),
                    parser.getMeter(),  parser.getDeviceType(), parser.getDeviceId(),
                    parser.getMDevType(), parser.getMDevId());
        }
        
        // savePreBill(kamstrupMeta);
        this.saveMeterEventLog(parser.getMeter(), parser.getEventLogData());
        sendSMS(parser.getMeter(), parser.getEventLogData());
        
        return true;
    }
    
    private void sendSMS(Meter meter, EventLogData[] eventLogDatas) {
        String phonelist = FMPProperty.getProperty("sms.phonelist");
        String smsHost = FMPProperty.getProperty("sms.hostname", "127.0.0.1");
        int smsPort = Integer.parseInt(FMPProperty.getProperty("sms.port", "81"));
        String smsPath = FMPProperty.getProperty("sms.path", "getmsg.php");
        String from = FMPProperty.getProperty("sms.from", "MEATH12345");
        
        try {
            if (phonelist != null && !"".equals(phonelist)) {
                String lang = meter.getSupplier().getLang().getCode_2letter();
                String country = meter.getSupplier().getCountry().getCode_2letter();
                SimpleDateFormat datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
                
                StringTokenizer st = new StringTokenizer(phonelist, ",");
                
                HttpClient client = new HttpClient();
                
                HostConfiguration hostConfiguration = new HostConfiguration();
                hostConfiguration.setHost(smsHost, smsPort);
                // TRANSID=BULK&CMD=SENDMSG&FROM=9009000&TO=6618881234&REPORT=N&CHARGE=Y&CODE=TEXT&CTYPE=TEXT&CONTENT=test
                
                GetMethod method = new GetMethod();
                method.setPath(smsPath);
                
                StringBuffer msg = new StringBuffer();
                for (EventLogData el : eventLogDatas) {
                    msg = new StringBuffer();
                    msg.append(datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(el.getDate()+el.getTime())));
                    msg.append(el.getMsg());
                    log.debug("SMS MSG :"+ msg.toString());
                    
                    while (st.hasMoreTokens()) {
                        try {
                            NameValuePair[] params = new NameValuePair[9];
                            params[0] = makeQueryString("TRANSID", "BULK");
                            params[1] = makeQueryString("CMD", "SENDMSG");
                            params[2] = makeQueryString("FROM", from);
                            params[3] = makeQueryString("TO", st.nextToken());
                            params[4] = makeQueryString("REPORT", "N");
                            params[5] = makeQueryString("CHARGE", "Y");
                            params[6] = makeQueryString("CODE", "TEXT");
                            params[7] = makeQueryString("CTYPE", "TEXT");
                            params[8] = makeQueryString("CONTENT", msg.toString());
                            method.setQueryString(params);
                        
                            int result = client.executeMethod(hostConfiguration, method);
                            log.debug("Result[" + result + "] Response[" + method.getResponseBodyAsString() + "]");
                        }
                        catch (Exception e) {
                            log.warn(e);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            log.warn(e);
        }
    }
    
    private NameValuePair makeQueryString(String name, String value) {
        NameValuePair param = new NameValuePair();
        param.setName(name);
        param.setValue(value);
        return param;
    }

    private void savePreBill(Kamstrup parser) throws ParseException {
        Map<String, String[]> billMap = parser.getValue();
        
        BillingData bill = new BillingData();
        
        String[] date = billMap.get("0. Date");
        String preMonth = null;
        if (date != null && date.length > 0) {
            String billDay = date[0].substring(0, 6) + "01";
            bill.setBillingTimestamp(billDay);
            preMonth = DateTimeUtil.getPreDay(billDay).substring(0,6);
            
            for (int i = 1; i < 13; i++) {
                date = billMap.get(i + ". RTC");
                if (date != null && date[0].substring(0,8).equals(billDay))  {
                    bill.setActiveEnergyRate1(Double.parseDouble(billMap.get(i + ". Active energy A14 Tariff 1")[0]));
                    bill.setActiveEnergyRate2(Double.parseDouble(billMap.get(i + ". Active energy A14 Tariff 2")[0]));
                    bill.setActiveEnergyRate4(Double.parseDouble(billMap.get(i + ". Active energy A14 Tariff 4")[0]));
                    bill.setActiveEnergyRateTotal(Double.parseDouble(billMap.get(i + ". Active energy A14")[0]));
                }
                
                date = billMap.get(i + ". Max power P14 RTC");
                if (date != null && date[0].substring(0,6).equals(preMonth)) {
                    bill.setActivePowerMaxDemandRateTotal(Double.parseDouble(billMap.get(i + ". Max power P14")[0]));
                    bill.setCummActivePwrDmdMaxExportRateTotal(Double.parseDouble(billMap.get(i + ". Accumulated max power P14")[0]));
                    bill.setActivePowerDemandMaxTimeRateTotal(date[0]);
                }
            }
            
            saveMonthlyBilling(bill, parser.getMeter(), null, null, parser.getMDevType(), parser.getMDevId());
        }
    }
}
