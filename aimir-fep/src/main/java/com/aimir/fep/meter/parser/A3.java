package com.aimir.fep.meter.parser;

import java.util.LinkedHashMap;
import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.data.PowerQualityMonitor;
import com.aimir.fep.meter.parser.a3rlnqTable.A3_CB;
import com.aimir.fep.meter.parser.a3rlnqTable.A3_EV;
import com.aimir.fep.meter.parser.a3rlnqTable.A3_IS;
import com.aimir.fep.meter.parser.a3rlnqTable.A3_LD;
import com.aimir.fep.meter.parser.a3rlnqTable.A3_MT;
import com.aimir.fep.meter.parser.a3rlnqTable.A3_PB;
import com.aimir.fep.meter.parser.a3rlnqTable.A3_PQ;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author BugLab
 *
 */
public class A3 extends MeterDataParser implements java.io.Serializable {

	/**
	 * A3.java : serialVersionUID = -4839804380877406347L
	 */
	private static final long serialVersionUID = -4839804380877406347L;
	private static Log log = LogFactory.getLog(A3.class);
	
    private byte[] rawData = null;
    private static int TABLE_DATA_LEN = 2;
    
    public static final int LEN_HEADER   = 2;
    public static final String HEADER_MD = "MD";
    public static final String HEADER_MT = "MT";
    public static final String HEADER_PB = "PB";
    public static final String HEADER_CB = "CB";
    public static final String HEADER_LD = "LD";
    public static final String HEADER_EL = "EL";
    public static final String HEADER_IS = "IS";
    public static final String HEADER_PQ = "PQ";

    private byte[] md = null;
    private byte[] mt = null;
    private byte[] cb = null;
    private byte[] pb = null;
    private byte[] ld = null;
    private byte[] ev = null;
    private byte[] is = null;
    private byte[] pq = null;
    
    private A3_CB CB = null;
    private A3_PB PB = null;
    private A3_MT MT = null;
    private A3_LD LD = null;
    private A3_IS IS = null;
    private A3_EV EV = null;
    private A3_PQ PQ = null;
    

	@Override
	public void parse(byte[] data) throws Exception {

        this.rawData = data;
        int offset = 0;
        String tbName = "";
        log.debug("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));
      
        
        int totlen = data.length;
        int len = 0;
        byte[] b = new byte[0];
        while(offset < totlen){
            tbName = new String(data,offset,LEN_HEADER);
            offset += LEN_HEADER;
            len = 0;//msb ->lsb
            len = DataFormat.hex2unsigned16(
                      DataFormat.LSB2MSB(
                          DataFormat.select(data,offset,TABLE_DATA_LEN)));
//            len -= LEN_HEADER + TABLE_DATA_LEN;
            offset += TABLE_DATA_LEN;
            log.debug("len=["+len+"]");
            b = new byte[len];
            System.arraycopy(data,offset,b,0,len);
            offset += len;

            if(tbName.equals(HEADER_MD))
            {
                md = b;
                log.debug("[MD] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals(HEADER_MT))
            {
                mt = b;
                log.debug("[MT] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals(HEADER_PB))
            {
                pb = b;
                log.debug("[PB] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals(HEADER_CB))
            {
                cb = b;
                log.debug("[CB] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals(HEADER_LD))
            {
                ld = b;
                log.debug("[LD] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals(HEADER_IS))
            {
                is = b;
                log.debug("[IS] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals(HEADER_EL))
            {
                ev = b;
                log.debug("[EV] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals(HEADER_PQ))
            {
                pq = b;
                log.debug("[PQ] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else 
            {
                log.debug("unknown table=>"+tbName);
            }
        }

        if(mt != null) {
        	this.MT = new A3_MT(mt);
        	this.meterTime = this.MT.getTimeStamp();
        }
        if(ld != null){
        	this.LD = new A3_LD(ld, this.MT.getKE(), this.MT.getMeterConstantScale());
        }
        if(is != null){
        	this.IS = new A3_IS(is, this.MT.getInstrumentScale());
        }
        if(ev != null){
        	this.EV = new A3_EV(ev);
        }
        if(pq != null){
        	this.PQ = new A3_PQ(pq);
        }
        if(pb != null){
        	this.PB = new A3_PB(pb, this.MT.getMeterConstantScale());
        }
        if(cb != null){
        	this.CB = new A3_CB(cb, this.MT.getKE(), this.MT.getMeterConstantScale());
        }		
	}

	public String getMeterLog(){
		return this.MT.getMeterLog();
	}
    
    public LPData[] getLpData() throws Exception {
        return this.LD.getLPData();
    }
    
    public int getResolution() {
        return this.LD.getLpInterval();
    }
    
    public EventLogData[] getMeterEventLog() throws Exception {
        return this.EV.getEventLogData();
    }

    public PowerAlarmLogData[] getPowerEventLog() throws Exception {
        return this.EV.getPowerAlarmLogData();
    }
    
    public Instrument[] getInstrument() throws Exception {
    	return this.IS.getInstruments();
    }

    public BillingData getPreviousBillingData() throws Exception {
        return this.PB.getBillingData();
    }
    
    public BillingData getCurrentBillingData() throws Exception {
        return this.CB.getBillingData();
    }
    
    public PowerQualityMonitor getPowerQualityData() throws Exception {
    	return this.PQ.getPowerQualityMonitor();
    }
    
	public Double getLpValue() {
		// TODO Auto-generated method stub
		return null;
	}
 
	@SuppressWarnings("unchecked")
	@Override
	public LinkedHashMap<?, ?> getData() {
		LinkedHashMap res = new LinkedHashMap(16, 0.75f, false);

		// DecimalFormat df3 = TimeLocaleUtil
		// .getDecimalFormat(meter.getSupplier());

		try {
			
			if (this.MT != null) {
				A3_MT meterinfo = this.MT;
				res.put("<b>[Meter Info]</b>", "");
				res.put("Model", meterinfo.getModelName());
				res.put("Serial Number", meterinfo.getMeterSerial());
				res.put("Meter Time", meterinfo.getTimeStamp());
				res.put("VT Ratio", meterinfo.getVT()+"");
				res.put("CT Ratio", meterinfo.getCT()+"");				
				res.put("Meter Elements", meterinfo.getMeterElement()+"");
				res.put("KE", meterinfo.getKE()+"");
				res.put("Meter Log", meterinfo.getMeterLog());
			}

			if (this.CB != null) {
				res.put("<b>[Current Billing Data]</b>", "");
				BillingData cbBillingData = this.CB.getBillingData();


				// 모든 필드를 res에 추가한다.
//				Field[] cbBillingDataFields = A3_CB.class.getDeclaredFields();
				res.put("ActiveEnergyRateTotal", cbBillingData.getActiveEnergyRateTotal()+"");
				res.put("ActiveEnergyRate1", cbBillingData.getActiveEnergyRate1()+"");
				res.put("ActiveEnergyRate2", cbBillingData.getActiveEnergyRate2()+"");
				res.put("ActiveEnergyRate3", cbBillingData.getActiveEnergyRate3()+"");
				
				res.put("ReactiveEnergyRateTotal", cbBillingData.getReactiveEnergyRateTotal()+"");
				res.put("ReactiveEnergyRate1", cbBillingData.getReactiveEnergyRate1()+"");
				res.put("ReactiveEnergyRate2", cbBillingData.getReactiveEnergyRate2()+"");
				res.put("ReactiveEnergyRate3", cbBillingData.getReactiveEnergyRate3()+"");
				
				res.put("ActivePowerMaxDemandRate1", cbBillingData.getActivePowerMaxDemandRate1()+"");
				res.put("ActivePowerMaxDemandRate2", cbBillingData.getActivePowerMaxDemandRate2()+"");
				res.put("ActivePowerMaxDemandRate3", cbBillingData.getActivePowerMaxDemandRate3()+"");
				
				res.put("ActivePowerDemandMaxTimeRate1", cbBillingData.getActivePowerDemandMaxTimeRate1()+"");
				res.put("ActivePowerDemandMaxTimeRate2", cbBillingData.getActivePowerDemandMaxTimeRate2()+"");
				res.put("ActivePowerDemandMaxTimeRate3", cbBillingData.getActivePowerDemandMaxTimeRate3()+"");
				
				res.put("ReactivePowerMaxDemandRate1", cbBillingData.getReactivePowerMaxDemandRate1()+"");
				res.put("ReactivePowerMaxDemandRate2", cbBillingData.getReactivePowerMaxDemandRate2()+"");
				res.put("ReactivePowerMaxDemandRate3", cbBillingData.getReactivePowerMaxDemandRate3()+"");
				
				res.put("ReactivePowerDemandMaxTimeRate1", cbBillingData.getReactivePowerDemandMaxTimeRate1()+"");
				res.put("ReactivePowerDemandMaxTimeRate2", cbBillingData.getReactivePowerDemandMaxTimeRate2()+"");
				res.put("ReactivePowerDemandMaxTimeRate3", cbBillingData.getReactivePowerDemandMaxTimeRate3()+"");
				
				res.put("CumulativeActivePowerDemandRate1", cbBillingData.getCumulativeActivePowerDemandRate1()+"");
				res.put("CumulativeActivePowerDemandRate2", cbBillingData.getCumulativeActivePowerDemandRate2()+"");
				res.put("CumulativeActivePowerDemandRate3", cbBillingData.getCumulativeActivePowerDemandRate3()+"");
				
				res.put("CumulativeReactivePowerDemandRate1", cbBillingData.getCumulativeReactivePowerDemandRate1()+"");
				res.put("CumulativeReactivePowerDemandRate2", cbBillingData.getCumulativeReactivePowerDemandRate2()+"");
				res.put("CumulativeReactivePowerDemandRate3", cbBillingData.getCumulativeReactivePowerDemandRate3()+"");
			}
			
			if (this.PB != null) {
                res.put("<b>[Previous Billing Data]</b>", "");
				BillingData pbBillingData = this.PB.getBillingData();

				res.put("WriteDate", pbBillingData.getWriteDate()+"");
				res.put("ActiveEnergyRateTotal", pbBillingData.getActiveEnergyRateTotal()+"");
				res.put("ActiveEnergyRate1", pbBillingData.getActiveEnergyRate1()+"");
				res.put("ActiveEnergyRate2", pbBillingData.getActiveEnergyRate2()+"");
				res.put("ActiveEnergyRate3", pbBillingData.getActiveEnergyRate3()+"");

				res.put("ReactiveEnergyRateTotal", pbBillingData.getReactiveEnergyRateTotal()+"");
				res.put("ReactiveEnergyRate1", pbBillingData.getReactiveEnergyRate1()+"");
				res.put("ReactiveEnergyRate2", pbBillingData.getReactiveEnergyRate1()+"");
				res.put("ReactiveEnergyRate3", pbBillingData.getReactiveEnergyRate3()+"");
				
				res.put("ActivePowerMaxDemandRate1", pbBillingData.getActivePowerMaxDemandRate1()+"");
				res.put("ActivePowerMaxDemandRate2", pbBillingData.getActivePowerMaxDemandRate2()+"");
				res.put("ActivePowerMaxDemandRate3", pbBillingData.getActivePowerMaxDemandRate3()+"");
				
				res.put("ActivePowerDemandMaxTimeRate1", pbBillingData.getActivePowerDemandMaxTimeRate1()+"");
				res.put("ActivePowerDemandMaxTimeRate2", pbBillingData.getActivePowerDemandMaxTimeRate2()+"");
				res.put("ActivePowerDemandMaxTimeRate3", pbBillingData.getActivePowerDemandMaxTimeRate3()+"");
				
				res.put("ReactivePowerMaxDemandRate1", pbBillingData.getReactivePowerMaxDemandRate1()+"");
				res.put("ReactivePowerMaxDemandRate2", pbBillingData.getReactivePowerMaxDemandRate2()+"");
				res.put("ReactivePowerMaxDemandRate3", pbBillingData.getReactivePowerMaxDemandRate3()+"");
				
				res.put("ReactivePowerDemandMaxTimeRate1", pbBillingData.getReactivePowerDemandMaxTimeRate1()+"");
				res.put("ReactivePowerDemandMaxTimeRate2", pbBillingData.getReactivePowerDemandMaxTimeRate2()+"");
				res.put("ReactivePowerDemandMaxTimeRate3", pbBillingData.getReactivePowerDemandMaxTimeRate3()+"");
				
				res.put("CumulativeActivePowerDemandRate1", pbBillingData.getCumulativeActivePowerDemandRate1()+"");
				res.put("CumulativeActivePowerDemandRate2", pbBillingData.getCumulativeActivePowerDemandRate2()+"");
				res.put("CumulativeActivePowerDemandRate3", pbBillingData.getCumulativeActivePowerDemandRate3()+"");
				
				res.put("CumulativeReactivePowerDemandRate1", pbBillingData.getCumulativeReactivePowerDemandRate1()+"");
				res.put("CumulativeReactivePowerDemandRate2", pbBillingData.getCumulativeReactivePowerDemandRate2()+"");
				res.put("CumulativeReactivePowerDemandRate3", pbBillingData.getCumulativeReactivePowerDemandRate3()+"");
				
				// 모든 필드를 res에 추가한다.
//				Field[] pbBillingDataFields = A3_PB.class.getDeclaredFields();
//				
//				for (Field field : pbBillingDataFields) {
//					field.setAccessible(true);
//					String fieldName = field.getName();
//					Object fieldValue = field.get(pbBillingData);
//					res.put(fieldName, ""+fieldValue);
//				}
			}

			if (this.EV != null) {
				res.put("<b>[Event log]</b>", "");
				EventLogData eventLog = this.EV.getEventLogData()[0];
				res.put(eventLog.getDate() + eventLog.getTime(), eventLog.getMsg());

			}

			if (this.LD != null) {
				res.put("<b>[LP Data]</b>", "");
				
				LPData[] lpDatas = this.LD.getLPData();
				LPData lpData = lpDatas[0];

				res.put("datetime", lpData.getDatetime());
				res.put("lpChannelCnt",""+lpData.getLPChannelCnt());
				res.put("lp",""+lpData.getLp());
				res.put("lpValue",""+lpData.getLpValue());
				
				Double[] ch = lpData.getCh();
				for (int i = 0; i < ch.length;i++) {
					res.put(String.format("<span style='margin-right: 40px;'>ch[%d]",i),""+ch[i]+"</span>");
				}
				
				Double[] v = lpData.getV();
				for (int i = 0; i < v.length;i++) {
					res.put(String.format("v[%d]",i),""+v[i]);
				}
				
				res.put("basePulse",""+ lpData.getBasePulse());
				res.put("baseValue", ""+lpData.getBaseValue());
				res.put("pf", ""+lpData.getPF());
				res.put("flag", ""+lpData.getFlag());
			
				
				res.put("<b>[Instrument]</b>", "");
				Instrument[] instruments = this.IS.getInstruments();
				Instrument instrument = instruments[0];

				
				// 모든 필드를 res에 추가한다.
				Field[] instrumentFields = Instrument.class.getDeclaredFields();
				for (Field field : instrumentFields) {
					field.setAccessible(true);
					String fieldName = field.getName();
					Object fieldValue = field.get(instrument);
					if(fieldValue == null)
						fieldValue="null";
					res.put(fieldName, ""+fieldValue);
				}

			}

		} catch (Exception e) {
			log.warn("Get Data Error=>", e);
		}

		return res;
	}

	@Override
	public int getFlag() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLength() {
		return this.rawData.length;
	}

	@Override
	public Double getMeteringValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getRawData() {
		return this.rawData;
	}

	@Override
	public void setFlag(int flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
