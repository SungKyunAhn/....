package com.aimir.fep.meter.parser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.parser.elsterA1700Table.A1700_BILLING_DATA;
import com.aimir.fep.meter.parser.elsterA1700Table.A1700_BILLING_DATA_CB;
import com.aimir.fep.meter.parser.elsterA1700Table.A1700_EVENT_LOG;
import com.aimir.fep.meter.parser.elsterA1700Table.A1700_LP_DATA;
import com.aimir.fep.meter.parser.elsterA1700Table.A1700_METER_INFO;
import com.aimir.fep.meter.parser.elsterA1700Table.A1700_MODEM_INFO;
import com.aimir.fep.meter.parser.elsterA1700Table.A1700_TEST_DATA;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;

/**
 * parsing ELSTER A1700 Meter Data
 *
 * @author EJ Choi
 */
public class ElsterA1700 extends MeterDataParser implements java.io.Serializable {
    
    private static Log log = LogFactory.getLog(ElsterA1700.class);

    private static final long serialVersionUID = -2883954513071210316L;

    public static final int LEN_HEADER   = 2;
    public static final String HEADER_MD = "MD";
    public static final String HEADER_MT = "MT";
    public static final String HEADER_BD = "BD";
    public static final String HEADER_CB = "CB";
    public static final String HEADER_LP = "LD";
    public static final String HEADER_EL = "EL";
    
    private byte[] rawData = null;
    private String meterId = null;
    private Double meteringValue = null;
    private int flag = 0;    
        
    private byte[] modem_info = null;
    private byte[] meter_info = null;
    private byte[] billing_data = null;
    private byte[] billing_data_cb = null;
    private byte[] lp_data = null;
    private byte[] event_log = null;
    
    private A1700_MODEM_INFO MODEM_INFO = null;
    private A1700_METER_INFO METER_INFO = null;
    private A1700_BILLING_DATA BILLING_DATA = null;
    private A1700_BILLING_DATA_CB BILLING_DATA_CB = null;
    private A1700_LP_DATA LP_DATA = null;
    private A1700_EVENT_LOG EVENT_LOG = null;
    
//    private int resolution = 5;
    
    /**
     * constructor
     */
    public ElsterA1700() { }

    public static void main(String args[]) throws Exception {
        ElsterA1700 elster = new ElsterA1700();
        A1700_TEST_DATA testData = new A1700_TEST_DATA();
        System.out.println(testData.toString());
        elster.parse(testData.getTestDataAll());
        elster.getInstrument();
        
        //System.out.println(elster.toString());
    }

    /**
     * parseing Energy Meter Data of ELSTER A1700 Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception {
        log.debug("[TOTAL] len=[" + data.length + "] data=[" + Hex.decode(data) + "]");
        this.rawData = data;
        
        int offset = 0;
        int totalLength = data.length;
        int dataLength = 0;
        
        while (offset < totalLength) {
            String header = new String(DataFormat.select(data, offset, LEN_HEADER));
            offset += LEN_HEADER;
            
            dataLength = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_HEADER));
            offset += LEN_HEADER;
            
            if (header != "" && header != null && dataLength > 0) {
                if (header.equals(HEADER_MD)) {
                    modem_info = DataFormat.select(data, offset, dataLength);
                    MODEM_INFO = new A1700_MODEM_INFO(modem_info);
                    
                    log.debug("MODEM_INFO[(length=" + dataLength + "), (data=" + Hex.decode(modem_info) + ")]");
                } else if (header.equals(HEADER_MT)) {
                    meter_info = DataFormat.select(data, offset, dataLength);
                    METER_INFO = new A1700_METER_INFO(meter_info);
                    
                    this.meterId = METER_INFO.getMeterSerial();
                    this.meterTime = METER_INFO.getMeterTime();
                    
                    log.debug("METER_INFO[(length=" + dataLength + "), (data=" + Hex.decode(meter_info) + ")]");
                } else if (header.equals(HEADER_BD)) {
                    billing_data = DataFormat.select(data, offset, dataLength);
                    BILLING_DATA = new A1700_BILLING_DATA(billing_data);
                    
                    log.debug("BILLING_DATA[(length=" + dataLength + "), (data=" + Hex.decode(billing_data) + ")]");
                }else if (header.equals(HEADER_CB)) {
                    billing_data_cb = DataFormat.select(data, offset, dataLength);
                    BILLING_DATA_CB = new A1700_BILLING_DATA_CB(billing_data_cb);
                    
                    // 현재 누적값을 가져온다.
                    meteringValue = BILLING_DATA_CB.getBillingData().getActiveEnergyImportRateTotal();
                    
                    log.debug("CURRENT BILLING_DATA[(length=" + dataLength + "), (data=" + Hex.decode(billing_data_cb) + ")]");
                } else if (header.equals(HEADER_LP)) {
                    lp_data = DataFormat.select(data, offset, dataLength);
                    LP_DATA = new A1700_LP_DATA(lp_data);
//                  getLpData();
                    
                    log.debug("LP_DATA[(length=" + dataLength + "), (data=" + Hex.decode(lp_data) + ")]");
                } else if (header.equals(HEADER_EL)) {
                    event_log = DataFormat.select(data, offset, dataLength);
                    EVENT_LOG = new A1700_EVENT_LOG(event_log);
                    
                    log.debug("EVENT_LOG[(length=" + dataLength + "), (data=" + Hex.decode(event_log) + ")]");
                } else {
                    log.debug("Wrong Format !!!");
                    break;
                }
            }
            offset += dataLength;
        }
        log.debug("Finished==============================================");
    }
    
    public LPData[] getLpData() throws Exception {
        LPData[] lpData = null;
        
        if (lp_data != null) {
            lpData = LP_DATA.getLpData();
        }

        return lpData;
    }
    
    public int getResolution() {
        int lpPeriod = 0;
        
        if (lp_data != null) {
            lpPeriod = LP_DATA.getLpPeriod();
        }
        
        return lpPeriod;
    }
    
    public List<EventLogData> getMeterEventLog() throws Exception {
        if (event_log != null) {
            return EVENT_LOG.getMeterEventLog();
        } else {
            return null;
        }
    }
    
    public List<PowerAlarmLogData> getPowerEventLog() throws Exception {
        if (event_log != null) {
            return EVENT_LOG.getPowerAlarmLog();
        } else {
            return null;
        }
    }
    
    public List<PowerAlarmLogData> getLpPowerEventLog() throws Exception {
        if (lp_data != null) {
            return LP_DATA.getLpPowerAlarmLog();
        } else {
            return null;
        }
    }
    
    public List<EventLogData> getLpMeterEventLog() throws Exception {
        if (lp_data != null) {
            return LP_DATA.getLpMeterEventLog();
        } else {
            return null;
        }
    }
    
    public double getCTPrimary() throws Exception {
        if (METER_INFO != null) return Double.parseDouble(METER_INFO.getCTPrimary());
        else return 1.0;
    }
    
    public double getCTSecondary() throws Exception {
        if (METER_INFO != null) return Double.parseDouble(METER_INFO.getCTSecondary());
        else return 1.0;
    }
    
    public double getVTPrimary() throws Exception {
        if (METER_INFO != null) return Double.parseDouble(METER_INFO.getVTPrimary());
        else return 1.0;
    }
    
    public double getVTSecondary() throws Exception {
        if (METER_INFO != null) return Double.parseDouble(METER_INFO.getVTSecondary());
        else return 1.0;
    }
    
    public Instrument[] getInstrument() throws Exception {
        Instrument[] insts = new Instrument[1];
        log.debug("Instrument ========================================================start2 ");
        if (meter_info != null) {
            insts[0] = new Instrument();
            Map<String, Double> value = METER_INFO.getRMSCurrent();
            insts[0].setCURR_A(value.get("phaseA") == null ? 0.0 : value.get("phaseA"));
            log.debug("RMSCurrentPhaseA: "+ insts[0].getCURR_A());
            insts[0].setCURR_B(value.get("phaseB") == null ? 0.0 : value.get("phaseB"));
            log.debug("RMSCurrentPhaseB: "+ insts[0].getCURR_B());
            insts[0].setCURR_C(value.get("phaseC") == null ? 0.0 : value.get("phaseC"));
            log.debug("RMSCurrentPhaseC: "+ insts[0].getCURR_C());
            
            value = METER_INFO.getRMSVoltage();
            insts[0].setVOL_A(value.get("phaseA") == null ? 0.0 : value.get("phaseA"));
            log.debug("RMSVoltagePhaseA: "+ insts[0].getVOL_A());
            insts[0].setVOL_B(value.get("phaseB") == null ? 0.0 : value.get("phaseB"));
            log.debug("RMSVoltagePhaseB: "+ insts[0].getVOL_B());
            insts[0].setVOL_C(value.get("phaseC") == null ? 0.0 : value.get("phaseC"));
            log.debug("RMSVoltagePhaseC: "+ insts[0].getVOL_C());
            
            value = METER_INFO.getPowerFactor();
            insts[0].setPF_A(value.get("phaseA") == null ? 0.0 : value.get("phaseA"));
            log.debug("PowerFactorPhaseA: " + insts[0].getPF_A());
            insts[0].setPF_B(value.get("phaseB") == null ? 0.0 : value.get("phaseB"));
            log.debug("PowerFactorPhaseB: " + insts[0].getPF_B());
            insts[0].setPF_C(value.get("phaseC") == null ? 0.0 : value.get("phaseC"));
            log.debug("PowerFactorPhaseC: " + insts[0].getPF_C());
            insts[0].setPF_TOTAL(value.get("total") == null ? 0.0 : value.get("total"));
            log.debug("PowerFactorTotal: " + insts[0].getPF_TOTAL());
            
            value = METER_INFO.getActivePower();
            insts[0].setKW_A(value.get("phaseA") == null ? 0.0 : value.get("phaseA"));
            log.debug("ActivePowerPhaseA: " + insts[0].getKW_A());
            insts[0].setKW_B(value.get("phaseB") == null ? 0.0 : value.get("phaseB"));
            log.debug("ActivePowerPhaseB: " + insts[0].getKW_B());
            insts[0].setKW_C(value.get("phaseC") == null ? 0.0 : value.get("phaseC"));
            log.debug("ActivePowerPhaseC: " + insts[0].getKW_C());

            value = METER_INFO.getReactivePower();
            insts[0].setKVAR_A(value.get("phaseA") == null ? 0.0 : value.get("phaseA"));
            log.debug("ReactivePowerPhaseA: " + insts[0].getKVAR_A());
            insts[0].setKVAR_B(value.get("phaseB") == null ? 0.0 : value.get("phaseB"));
            log.debug("ReactivePowerPhaseB: " + insts[0].getKVAR_B());
            insts[0].setKVAR_C(value.get("phaseC") == null ? 0.0 : value.get("phaseC"));
            log.debug("ReactivePowerPhaseC: " + insts[0].getKVAR_C());
            
            value = METER_INFO.getApparentPower();
            insts[0].setKVA_A(value.get("phaseA") == null ? 0.0 : value.get("phaseA"));
            log.debug("ApparentPowerPhaseA: " + insts[0].getKVA_A());
            insts[0].setKVA_B(value.get("phaseB") == null ? 0.0 : value.get("phaseB"));
            log.debug("ApparentPowerPhaseB: " + insts[0].getKVA_B());
            insts[0].setKVA_C(value.get("phaseC") == null ? 0.0 : value.get("phaseC"));
            log.debug("ApparentPowerPhaseC: " + insts[0].getKVA_C());
            
            value = METER_INFO.getFrequency();
            insts[0].setLine_frequencyA(value.get("phaseA") == null ? 0.0 : value.get("phaseA"));
            log.debug("FraquencyA: " + insts[0].getLine_frequencyA());
            insts[0].setLine_frequencyB(value.get("phaseB") == null ? 0.0 : value.get("phaseB"));
            log.debug("FraquencyB: " + insts[0].getLine_frequencyB());
            insts[0].setLine_frequencyC(value.get("phaseC") == null ? 0.0 : value.get("phaseC"));
            log.debug("FraquencyC: " + insts[0].getLine_frequencyC());
            insts[0].setLINE_FREQUENCY(value.get("total") == null ? 0.0 : value.get("total"));
            log.debug("FraquencyTotal: " + insts[0].getLINE_FREQUENCY());
            
            value = METER_INFO.getPhaseAngle();
            insts[0].setCURR_ANGLE_A(value.get("phaseA") == null ? 0.0 : value.get("phaseA"));
            log.debug("PhaseAnglePhaseA: " + insts[0].getCURR_ANGLE_A());
            insts[0].setCURR_ANGLE_B(value.get("phaseB") == null ? 0.0 : value.get("phaseB"));
            log.debug("PhaseAnglePhaseB: " + insts[0].getCURR_ANGLE_B());
            insts[0].setCURR_ANGLE_C(value.get("phaseC") == null ? 0.0 : value.get("phaseC"));
            log.debug("PhaseAnglePhaseC: " + insts[0].getCURR_ANGLE_C());
            
            return insts;
        } else {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public HashMap getModemData() throws Exception {
        HashMap<String, String> modemData = new HashMap<String, String>();
        
        if (meter_info != null) {
            modemData.put("protocolType", Protocol.GPRS.name());
            modemData.put("fwVersion"   , MODEM_INFO.getFwVerion());
            modemData.put("fwBuild"     , MODEM_INFO.getFwBuild());
            modemData.put("hwVersion"   , MODEM_INFO.getHwVersion());
//          modemData.put("moduleSerial", MODEM_INFO.getModuleSerial());
            modemData.put("simNumber"   , MODEM_INFO.getSimIMSI());
            modemData.put("rssi"        , MODEM_INFO.getRSSI()+"");
            modemData.put("ber"         , MODEM_INFO.getBER()+"");
            modemData.put("modemStatus" , MODEM_INFO.getModemStatus()+"");
        }
        return modemData;
    }
    
    public BillingData getBillingData() throws Exception {
        if (billing_data != null) {
            return BILLING_DATA.getBillingData();
        } else {
            return null;
        }
    }
    
    public BillingData getCurrentBillingData() throws Exception {
        if (billing_data_cb != null) {
            return BILLING_DATA_CB.getBillingData();
        } else {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public LinkedHashMap getData() {
        LinkedHashMap<String, String> dataMap = new LinkedHashMap<String, String>();
        
        BillingData billingData         = null;
        LPData[] lpData                 = null;
        List<EventLogData> eventLogList = null;
        
        return dataMap;
    }

    @Override
    public int getFlag() {
        return this.flag;
    }

    @Override
    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public int getLength() {
        return this.rawData.length;
    }

    @Override
    public Double getMeteringValue() {
    	//Current Billing Data 사용.
        if (billing_data_cb != null) {
            try {
                BillingData billingData = BILLING_DATA_CB.getBillingData();
                this.meteringValue = billingData.getActiveEnergyRateTotal();//billingData.getActiveEnergyImportRateTotal();
                // this.meteringValue = METER_INFO.getActivePowerTotal();
            } catch (NumberFormatException e) {
                log.error(e);
            } catch (Exception e) {
                log.error(e);
            }
        }
        return this.meteringValue;
    }

    @Override
    public byte[] getRawData() {
        return this.rawData;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("Elster_A1700_DATA[\n");
        sb.append(MODEM_INFO.toString() + "\n");
        log.debug("MODEM_INFO is OK");
        sb.append(METER_INFO.toString() + "\n");
        log.debug("METER_INFO is OK");
        sb.append(BILLING_DATA.toString() + "\n");
        log.debug("BILLING_DATA is OK");
        sb.append(BILLING_DATA_CB.toString() + "\n");
        log.debug("BILLING_DATA_CB is OK");
        sb.append(LP_DATA.toString() + "\n");
        log.debug("LP_DATA is OK");
        sb.append(EVENT_LOG.toString() + "\n");
        log.debug("EVENT_LOG is OK") ;
        sb.append("]");
        
        return sb.toString();
    }
    
    public static String convertTimestamp(String title, byte[] data) {
        byte[] b = data;
        DataFormat.convertEndian(b);
        String timestamp = DateTimeUtil.getDateString(DataFormat.getLongToBytes(b)*1000);
        log.debug(title+"=[" + timestamp + "] RAW[" + Hex.decode(data) + "]");
        return timestamp;
    }
}
