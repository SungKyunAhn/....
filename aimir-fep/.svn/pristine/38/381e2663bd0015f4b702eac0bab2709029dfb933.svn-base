package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.data.PowerQualityMonitor;
import com.aimir.fep.meter.parser.a1830rlnTable.A1800_CB;
import com.aimir.fep.meter.parser.a1830rlnTable.A1800_EV;
import com.aimir.fep.meter.parser.a1830rlnTable.A1800_IS;
import com.aimir.fep.meter.parser.a1830rlnTable.A1800_LD;
import com.aimir.fep.meter.parser.a1830rlnTable.A1800_MT;
import com.aimir.fep.meter.parser.a1830rlnTable.A1800_PB;
import com.aimir.fep.meter.parser.a1830rlnTable.A1800_PQ;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.TimeLocaleUtil;

public class A1800 extends MeterDataParser implements java.io.Serializable {

	private static final long serialVersionUID = 132524599222472525L;

	private static Log log = LogFactory.getLog(A1800.class);
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
    
    private A1800_CB CB = null;
    private A1800_PB PB = null;
    private A1800_MT MT = null;
    private A1800_LD LD = null;
    private A1800_IS IS = null;
    private A1800_EV EV = null;
    private A1800_PQ PQ = null;

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
                          DataFormat.select(data,offset,TABLE_DATA_LEN));
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
        	this.MT = new A1800_MT(mt);
        	this.meterTime = this.MT.getTimeStamp();
        }
        if(ld != null){
        	this.LD = new A1800_LD(ld, this.MT.getKE(), this.MT.getMeterConstantScale());
        }
        if(is != null){
        	this.IS = new A1800_IS(is, this.MT.getInstrumentScale());
        }
        if(ev != null){
        	this.EV = new A1800_EV(ev);
        }
        if(pq != null){
        	this.PQ = new A1800_PQ(pq);
        }
        if(pb != null){
        	this.PB = new A1800_PB(pb, this.MT.getMeterConstantScale());
        }
        if(cb != null){
        	this.CB = new A1800_CB(cb, this.MT.getKE(), this.MT.getMeterConstantScale());
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
    
    public Vector<PowerAlarmLogData> getPowerAlarmLog(){
		return this.EV.getPowerAlarmLog();
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
    
//    public byte[] getTOUBlk() throws Exception {
//    	return this.CB.parseTOUBlk();
//    }
    
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
		
		DecimalFormat df3=null;
		Format ft = new DecimalFormat("#.######");
		SimpleDateFormat destFormat=null;
		 
		if(meter!=null && meter.getSupplier()!=null){
			Supplier supplier = meter.getSupplier();
			if(supplier !=null){
				String lang = supplier.getLang().getCode_2letter();
				String country = supplier.getCountry().getCode_2letter();
				String datePattern = TimeLocaleUtil.getDateFormat(12, lang, country);
				
				df3 = TimeLocaleUtil.getDecimalFormat(supplier);
				
				destFormat = new SimpleDateFormat(datePattern);
			}
		}else{
			//locail 정보가 없을때는 기본 포멧을 사용한다.
			df3 = new DecimalFormat();
			destFormat = new SimpleDateFormat();
		}
		
		try {
						
			log.debug("--------------------  A1800 getData Start --------------------");
			
			
			
			if (this.MT != null) {
				A1800_MT meterinfo = this.MT;
				
				res.put("<b>[Meter Info]</b>", " ");
				res.put("Meter Model", meterinfo.getModelName()+"");
				res.put("Meter Serial Number", meterinfo.getMeterSerial()+"");
				
				//날짜 포멧팅
				SimpleDateFormat meterTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				Date meterTime = meterTimeFormat.parse(meterinfo.getTimeStamp());
				
				res.put("Meter Time", destFormat.format(meterTime));
				res.put("VT Ratio", meterinfo.getVT()+"");
				res.put("CT Ratio", meterinfo.getCT()+"");				
//				res.put("Meter Elements", meterinfo.getMeterElement()+"");
				res.put("KE", meterinfo.getKE()+"");
				res.put("Meter Log", meterinfo.getMeterLog());
				res.put("","");
			}

			if (this.CB != null) {
				res.put("<b>[Current Billing Data]</b>", " ");
				BillingData cbBillingData = this.CB.getBillingData();


				// 모든 필드를 res에 추가한다.
//				Field[] cbBillingDataFields = A1800_CB.class.getDeclaredFields();
				res.put("(CB)Active Energy Rate Total", 	getDecimalValue(df3, cbBillingData.getActiveEnergyRateTotal())+"");
				res.put("(CB)Active Energy Rate1", 			getDecimalValue(df3, cbBillingData.getActiveEnergyRate1())+"");
				res.put("(CB)Active Energy Rate2", 			getDecimalValue(df3, cbBillingData.getActiveEnergyRate2())+"");
				res.put("(CB)Active Energy Rate3", 			getDecimalValue(df3, cbBillingData.getActiveEnergyRate3())+"");
				
				res.put("(CB)Reactive Energy Rate Total", 	getDecimalValue(df3, cbBillingData.getReactiveEnergyRateTotal())+"");
				res.put("(CB)Reactive Energy Rate1", 		getDecimalValue(df3, cbBillingData.getReactiveEnergyRate1())+"");
				res.put("(CB)Reactive Energy Rate2", 		getDecimalValue(df3, cbBillingData.getReactiveEnergyRate2())+"");
				res.put("(CB)Reactive Energy Rate3", 		getDecimalValue(df3, cbBillingData.getReactiveEnergyRate3())+"");
				
				res.put("(CB)Active Power Max.DemandRate1", getDecimalValue(df3, cbBillingData.getActivePowerMaxDemandRate1())+"");
				res.put("(CB)Active Power Max.DemandRate2", getDecimalValue(df3, cbBillingData.getActivePowerMaxDemandRate2())+"");
				res.put("(CB)Active Power Max.DemandRate3", getDecimalValue(df3, cbBillingData.getActivePowerMaxDemandRate3())+"");

				SimpleDateFormat demandTimeFormat = new SimpleDateFormat("yyyyMMddHHmm");
				Date aDemandDate1 = demandTimeFormat.parse(cbBillingData.getActivePowerDemandMaxTimeRate1());
				Date aDemandDate2 = demandTimeFormat.parse(cbBillingData.getActivePowerDemandMaxTimeRate2());
				Date aDemandDate3 = demandTimeFormat.parse(cbBillingData.getActivePowerDemandMaxTimeRate3());
				
				res.put("(CB)Active Power Demand Max Time Rate1", destFormat.format(aDemandDate1));
				res.put("(CB)Active Power Demand Max Time Rate2", destFormat.format(aDemandDate2));
				res.put("(CB)Active Power Demand Max Time Rate3", destFormat.format(aDemandDate3));
				
				res.put("(CB)Reactive Power Max.Demand Rate1", getDecimalValue(df3, cbBillingData.getReactivePowerMaxDemandRate1())+"");
				res.put("(CB)Reactive Power Max.Demand Rate2", getDecimalValue(df3, cbBillingData.getReactivePowerMaxDemandRate2())+"");
				res.put("(CB)Reactive Power Max.Demand Rate3", getDecimalValue(df3, cbBillingData.getReactivePowerMaxDemandRate3())+"");
				
				Date rDemandDate1 = demandTimeFormat.parse(cbBillingData.getReactivePowerDemandMaxTimeRate1());
				Date rDemandDate2 = demandTimeFormat.parse(cbBillingData.getReactivePowerDemandMaxTimeRate2());
				Date rDemandDate3 = demandTimeFormat.parse(cbBillingData.getReactivePowerDemandMaxTimeRate3());
				
				res.put("(CB)Reactive Power Demand Max Time Rate1", destFormat.format(rDemandDate1));
				res.put("(CB)Reactive Power Demand Max Time Rate2", destFormat.format(rDemandDate2));
				res.put("(CB)Reactive Power Demand Max Time Rate3", destFormat.format(rDemandDate3));
				
				res.put("(CB)Cumulative Active Power Demand Rate1", ft.format(cbBillingData.getCumulativeActivePowerDemandRate1()));
				res.put("(CB)Cumulative Active Power Demand Rate2", ft.format(cbBillingData.getCumulativeActivePowerDemandRate2()));
				res.put("(CB)Cumulative Active Power Demand Rate3", ft.format(cbBillingData.getCumulativeActivePowerDemandRate3()));
				
				res.put("(CB)Cumulative Reactive Power Demand Rate1", getDecimalValue(df3, cbBillingData.getCumulativeReactivePowerDemandRate1())+"");
				res.put("(CB)Cumulative Reactive Power Demand Rate2", getDecimalValue(df3, cbBillingData.getCumulativeReactivePowerDemandRate2())+"");
				res.put("(CB)Cumulative Reactive Power Demand Rate3", getDecimalValue(df3, cbBillingData.getCumulativeReactivePowerDemandRate3())+"");
				res.put("","");
			}
			
			if (this.PB != null) {
                res.put("<b>[Previous Billing Data]</b>", "");
				BillingData pbBillingData = this.PB.getBillingData();

				SimpleDateFormat writeDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
				Date writeDate = writeDateFormat.parse(pbBillingData.getWriteDate());
				
				res.put("(PB)WriteDate", 				destFormat.format(writeDate));
				res.put("(PB)Active Energy Rate Total", getDecimalValue(df3, pbBillingData.getActiveEnergyRateTotal())+"");
				res.put("(PB)Active Energy Rate1", 		getDecimalValue(df3, pbBillingData.getActiveEnergyRate1())+"");
				res.put("(PB)Active Energy Rate2", 		getDecimalValue(df3, pbBillingData.getActiveEnergyRate2())+"");
				res.put("(PB)Active Energy Rate3", 		getDecimalValue(df3, pbBillingData.getActiveEnergyRate3())+"");

				res.put("(PB)Reactive Energy Rate Total", 	getDecimalValue(df3, pbBillingData.getReactiveEnergyRateTotal())+"");
				res.put("(PB)Reactive Energy Rate1", 		getDecimalValue(df3, pbBillingData.getReactiveEnergyRate1())+"");
				res.put("(PB)Reactive Energy Rate2", 		getDecimalValue(df3, pbBillingData.getReactiveEnergyRate1())+"");
				res.put("(PB)Reactive Energy Rate3", 		getDecimalValue(df3, pbBillingData.getReactiveEnergyRate3())+"");
				
				res.put("(PB)Active Power Max Demand Rate1", getDecimalValue(df3, pbBillingData.getActivePowerMaxDemandRate1())+"");
				res.put("(PB)Active Power Max Demand Rate2", getDecimalValue(df3, pbBillingData.getActivePowerMaxDemandRate2())+"");
				res.put("(PB)Active Power Max Demand Rate3", getDecimalValue(df3, pbBillingData.getActivePowerMaxDemandRate3())+"");
				
				SimpleDateFormat demandTimeFormat = new SimpleDateFormat("yyyyMMddHHmm");
				Date aDemandDate1 = demandTimeFormat.parse(pbBillingData.getActivePowerDemandMaxTimeRate1());
				Date aDemandDate2 = demandTimeFormat.parse(pbBillingData.getActivePowerDemandMaxTimeRate2());
				Date aDemandDate3 = demandTimeFormat.parse(pbBillingData.getActivePowerDemandMaxTimeRate3());
				
				res.put("(PB)Active Power Demand Max Time Rate1", destFormat.format(aDemandDate1));
				res.put("(PB)Active Power Demand Max Time Rate2", destFormat.format(aDemandDate2));
				res.put("(PB)Active Power Demand Max Time Rate3", destFormat.format(aDemandDate3));
				
				res.put("(PB)Reactive Power Max Demand Rate1", getDecimalValue(df3, pbBillingData.getReactivePowerMaxDemandRate1())+"");
				res.put("(PB)Reactive Power Max Demand Rate2", getDecimalValue(df3, pbBillingData.getReactivePowerMaxDemandRate2())+"");
				res.put("(PB)Reactive Power Max Demand Rate3", getDecimalValue(df3, pbBillingData.getReactivePowerMaxDemandRate3())+"");
				
				Date rDemandDate1 = demandTimeFormat.parse(pbBillingData.getReactivePowerDemandMaxTimeRate1());
				Date rDemandDate2 = demandTimeFormat.parse(pbBillingData.getReactivePowerDemandMaxTimeRate2());
				Date rDemandDate3 = demandTimeFormat.parse(pbBillingData.getReactivePowerDemandMaxTimeRate3());
				
				res.put("(PB)Reactive Power Demand Max Time Rate1", destFormat.format(rDemandDate1));
				res.put("(PB)Reactive Power Demand Max Time Rate2", destFormat.format(rDemandDate2));
				res.put("(PB)Reactive Power Demand Max Time Rate3", destFormat.format(rDemandDate3));
				
				res.put("(PB)Cumulative Active Power Demand Rate1", getDecimalValue(df3, pbBillingData.getCumulativeActivePowerDemandRate1())+"");
				res.put("(PB)Cumulative Active Power Demand Rate2", getDecimalValue(df3, pbBillingData.getCumulativeActivePowerDemandRate2())+"");
				res.put("(PB)Cumulative Active Power Demand Rate3", getDecimalValue(df3, pbBillingData.getCumulativeActivePowerDemandRate3())+"");
				
				res.put("(PB)Cumulative Reactive Power Demand Rate1", getDecimalValue(df3, pbBillingData.getCumulativeReactivePowerDemandRate1())+"");
				res.put("(PB)Cumulative Reactive Power Demand Rate2", getDecimalValue(df3, pbBillingData.getCumulativeReactivePowerDemandRate2())+"");
				res.put("(PB)Cumulative Reactive Power Demand Rate3", getDecimalValue(df3, pbBillingData.getCumulativeReactivePowerDemandRate3())+"");
				res.put("","");
				// 모든 필드를 res에 추가한다.
//				Field[] pbBillingDataFields = A1800_PB.class.getDeclaredFields();
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
				EventLogData[] eventLogs = this.EV.getEventLogData();
				
				SimpleDateFormat eventLogFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				
				
				if(eventLogs!=null)
				for (int i = 0;i<eventLogs.length;i++) {
					if(i>50)
						break;
					
					Date eventDate = eventLogFormat.parse(eventLogs[i].getDate() + eventLogs[i].getTime());
					res.put(String.format("(%d)Event %s",i, destFormat.format(eventDate)),eventLogs[i].getMsg());
				}			
//				res.put("EnentLog Date", eventLog.getDate() + eventLog.getTime()+"");
//				res.put("EnentLog Message", eventLog.getMsg()+"");
				res.put("","");
			}

			if (this.LD != null) {
				res.put("<b>[LP Data]</b>", "");
				LPData[] lpDatas = this.LD.getLPData();
				
				SimpleDateFormat lpDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				
				if(lpDatas!=null)
					for(int i = 0; i < lpDatas.length;i++){
						
						if(i>50)
							break;
						
						
						LPData lpData = lpDatas[i];
						Date lpDate = lpDateFormat.parse(lpData.getDatetime());
						String key = String.format("(%d)lp %s", i,destFormat.format(lpDate));
						String value = "";
						
						Double[] ch = lpData.getCh();
						Double[] v = lpData.getV();
						
						for(int k = 0; k < ch.length ; k++){
							value += "<span style='margin-right: 40px;'>ch"+(k+1)+"="+df3.format(ch[k])+"</span>";
	                    }
						
						value += "\n";
						
						for(int s = 0; s < v.length ; s++){
							value += String.format("v(%d)=%s, ", s+1,getDecimalValue(df3,v[s]));
	                    }
						res.put(key, value);
						
					}
//				LPData[] lpDatas = this.LD.getLPData();
//				LPData lpData = lpDatas[0];
//
//				res.put("DateTime", convertTimeFormat(destFormat,lpData.getDatetime()));
//				res.put("LP Channel Count",""+lpData.getLPChannelCnt());
////				res.put("LP",""+lpData.getLp());
////				res.put("LP Value",""+lpData.getLpValue());			
//				
//				Double[] ch = lpData.getCh();
//				for (int i = 0; i < ch.length;i++) {
//					res.put(String.format("ch[%d]",i),""+getDecimalValue(df3, ch[i]));
//				}
//				
//				Double[] v = lpData.getV();
//				for (int i = 0; i < v.length;i++) {
//					res.put(String.format("v[%d]",i),""+getDecimalValue(df3, v[i]));
//				}
//				
//				res.put("BasePulse",""+ lpData.getBasePulse());
//				res.put("BaseValue", ""+lpData.getBaseValue());
//				res.put("PF", ""+getDecimalValue(df3, lpData.getPF()));
//				res.put("Flag", ""+lpData.getFlag());
			
				
				res.put("<b>[Instrument]</b>", "");
				Instrument[] instruments = this.IS.getInstruments();
				
				for(int i=0;i<instruments.length;i++){
					
					if(i>50)
						break;
					
					Instrument instrument = instruments[i];
					res.put(String.format("(%d)Voltage(A)", i),getDecimalValue(df3, instrument.getVOL_A())+"");
					res.put(String.format("(%d)Voltage(B)", i),getDecimalValue(df3, instrument.getVOL_B())+"");
					res.put(String.format("(%d)Voltage(C)", i),getDecimalValue(df3, instrument.getVOL_C())+"");
					
					res.put(String.format("(%d)Current(A)", i),getDecimalValue(df3, instrument.getCURR_A())+"");
					res.put(String.format("(%d)Current(B)", i),getDecimalValue(df3, instrument.getCURR_B())+"");
					res.put(String.format("(%d)Current(C)", i),getDecimalValue(df3, instrument.getCURR_C())+"");
					
					res.put(String.format("(%d)Phase Angle(A)", i),getDecimalValue(df3, instrument.getVOL_ANGLE_A())+"");
					res.put(String.format("(%d)Phase Angle(B)", i),getDecimalValue(df3, instrument.getVOL_ANGLE_B())+"");
					res.put(String.format("(%d)Phase Angle(C)", i),getDecimalValue(df3, instrument.getVOL_ANGLE_C())+"");
					
					res.put(String.format("(%d)Current ANGLE(A)", i),getDecimalValue(df3, instrument.getCURR_ANGLE_A())+"");
					res.put(String.format("(%d)Current ANGLE(B)", i),getDecimalValue(df3, instrument.getCURR_ANGLE_B())+"");
					res.put(String.format("(%d)Current ANGLE(C)", i),getDecimalValue(df3, instrument.getCURR_ANGLE_C())+"");
					
					res.put(String.format("(%d)KVA(A)", i),getDecimalValue(df3, instrument.getKVA_A())+"");
					res.put(String.format("(%d)KVA(B)", i),getDecimalValue(df3, instrument.getKVA_B())+"");
					res.put(String.format("(%d)KVA(C)", i),getDecimalValue(df3, instrument.getKVA_C())+"");
				
				}
				
//				Instrument[] instruments = this.IS.getInstruments();
//				Instrument instrument = instruments[0];
//
//		
//				// 모든 필드를 res에 추가한다.
//				Field[] instrumentFields = Instrument.class.getDeclaredFields();
//				res.put("Voltage(A)",getDecimalValue(df3, instrument.getVOL_A())+"");
//				res.put("Voltage(B)",getDecimalValue(df3, instrument.getVOL_B())+"");
//				res.put("Voltage(C)",getDecimalValue(df3, instrument.getVOL_C())+"");
//				
//				res.put("Current(A)",getDecimalValue(df3, instrument.getCURR_A())+"");
//				res.put("Current(B)",getDecimalValue(df3, instrument.getCURR_B())+"");
//				res.put("Current(C)",getDecimalValue(df3, instrument.getCURR_C())+"");
//				
//				res.put("Phase Angle(A)",getDecimalValue(df3, instrument.getVOL_ANGLE_A())+"");
//				res.put("Phase Angle(B)",getDecimalValue(df3, instrument.getVOL_ANGLE_B())+"");
//				res.put("Phase Angle(C)",getDecimalValue(df3, instrument.getVOL_ANGLE_C())+"");
//				
//				res.put("Current ANGLE(A)",getDecimalValue(df3, instrument.getCURR_ANGLE_A())+"");
//				res.put("Current ANGLE(B)",getDecimalValue(df3, instrument.getCURR_ANGLE_B())+"");
//				res.put("Current ANGLE(C)",getDecimalValue(df3, instrument.getCURR_ANGLE_C())+"");
//				
//				res.put("KVA(A)",getDecimalValue(df3, instrument.getKVA_A())+"");
//				res.put("KVA(B)",getDecimalValue(df3, instrument.getKVA_B())+"");
//				res.put("KVA(C)",getDecimalValue(df3, instrument.getKVA_C())+"");
				
//				for (Field field : instrumentFields) {
//					field.setAccessible(true);
//					String fieldName = field.getName();
//					Object fieldValue = field.get(instrument);
//					if(fieldValue == null)
//						fieldValue="null";
//					res.put(fieldName, ""+fieldValue);
//				}

			}
			log.debug("--------------------  A1800 getData End --------------------");
		} catch (Exception e) {
			log.warn("Get Data Error=>", e);
		}

		return res;
	}
	
	/**
	 * 값에 Decimal Format 을 적용하는데 Null일경우 빈(Space)값을 리턴한다.
	 * @param df3
	 * @param d
	 * @return
	 */
	private String getDecimalValue(DecimalFormat df3, Double d) {
		if(d!=null){
			return df3.format(d);
		}else {
			return "";
		}
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
		try{		
			return this.CB.getBillingData().getActiveEnergyRateTotal();}
		catch(Exception e){
		}
		return null;

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

	@Override
	public byte[] getRawData() {
		// TODO Auto-generated method stub
		return this.rawData;
	}
}
