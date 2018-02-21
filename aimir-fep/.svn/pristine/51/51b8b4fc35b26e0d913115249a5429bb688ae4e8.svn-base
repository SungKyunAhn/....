package com.aimir.fep.meter.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.aimir.fep.meter.parser.MX2Table.MX2BillingData;
import com.aimir.fep.meter.parser.MX2Table.MX2EventLog;
import com.aimir.fep.meter.parser.MX2Table.MX2LPData;
import com.aimir.fep.meter.parser.MX2Table.MX2MeterInfo;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.mvm.SAP;
import com.aimir.model.system.Supplier;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * MX2 파서.
 * <p>
 * 미터에서 받은 데이터를 구조체로 파싱하는 역할을하며 완성된 구조체는 세이버에서 DB에 저장될때 사용된다.
 * </p>
 * 
 * @author kskim
 * @see <참고문서><br>
 *      <ul>
 *      <li>MX2_AMR_Communication_Specification-2011-06-16_Signed.pdf</li>
 *      <li>NAMR_P213GP(2011)_Protocol.doc</li>
 *      </ul>
 */
@SuppressWarnings("serial")
public class MX2 extends MeterDataParser implements java.io.Serializable {

	private static Log log = LogFactory.getLog(MX2.class);
	
	// 미터 정보는 단일 정보이기 때문에 벡터사용을 안함.
	private MX2MeterInfo meterInfo = null;

	// 나머지 데이터들은 복수 정보 가능성 때문에 벡터를 사용함.
	private MX2BillingData billingData = null;

	private MX2EventLog eventLog = null;

	private MX2LPData lpData = null;
	
	private SAP sap = null;

	public MX2() {

	}

	/**
	 * ?
	 * 
	 * @see com.aimir.fep.meter.parser.MeterDataParser#getData()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LinkedHashMap<?, ?> getData() {
		LinkedHashMap res = new LinkedHashMap(16, 0.75f, false);
		
		DecimalFormat df3=null;
		SimpleDateFormat destFormat=null;
		 
		if(meter!=null && meter.getSupplier()!=null){
			Supplier supplier = meter.getSupplier();
			if(supplier !=null){
				String lang = supplier.getLang().getCode_2letter();
				String country = supplier.getCountry().getCode_2letter();
				String datePattern = TimeLocaleUtil.getDateFormat(14, lang, country);
				
				df3 = TimeLocaleUtil.getDecimalFormat(supplier);
				
				destFormat = new SimpleDateFormat(datePattern);
			}
		}else{
			//locail 정보가 없을때는 기본 포멧을 사용한다.
			df3 = new DecimalFormat();
			destFormat = new SimpleDateFormat();
		}
			
		
		try {
			res.put("[Meter Configuration Data]", "");
			if (this.meterInfo != null) {
				MX2MeterInfo meterInfo = this.meterInfo;
				res.put("Model", StringUtil.nullToBlank(meterInfo.getMeterModel()));
				res.put("Serial Number", StringUtil.nullToBlank(meterInfo.getMeterSerial()));
				
				//날짜 포멧팅
				SimpleDateFormat meterTimeFormat = new SimpleDateFormat("yyMMddHHmmss");
				Date meterDate = meterTimeFormat.parse(meterInfo.getMeterTime());
				
				res.put("Meter Time", destFormat.format(meterDate));
				
				
				//0 = 1P2W, 1 = 1P3W, 2 = 3P3W, 3 = 3P4W
				String phw = StringUtil.nullToBlank(meterInfo.getPhaseWires());
				String phw_out = null;
				if(phw.equals(""+MX2MeterInfo.n1P2W)){
					phw_out = MX2MeterInfo._1P2W;
				}else if(phw.equals(""+MX2MeterInfo.n1P3W)){
					phw_out = MX2MeterInfo._1P3W;
				}else if(phw.equals(""+MX2MeterInfo.n3P3W)){
					phw_out = MX2MeterInfo._3P3W;
				}else if(phw.equals(""+MX2MeterInfo.n3P4W)){
					phw_out = MX2MeterInfo._3P4W;
				}else{
					phw_out = "";
				}
				res.put("Phase & Wires", phw_out);
				
				
				res.put("Reference Voltage(V)", StringUtil.nullToBlank(meterInfo.getReferenceVoltage()));
				res.put("Reference Frequency(Hz)", StringUtil.nullToBlank(meterInfo
						.getReferenceFrequency()).equals("0")?"50":"60");
				res.put("Basic Current", StringUtil.nullToBlank(meterInfo.getBasicCurrent()));
				res.put("Maximum Current", StringUtil.nullToBlank(meterInfo.getMaximumCurrent()));
				
	              res.put("Voltage Angle(A)",getDecimalValue(df3,meterInfo.getPhaseAngleVa())+"");
	              res.put("Voltage Angle(B)",getDecimalValue(df3,meterInfo.getPhaseAngleVb())+"");
	              res.put("Voltage Angle(C)",getDecimalValue(df3,meterInfo.getPhaseAngleVc())+"");
	              res.put("Current Angle(A)",getDecimalValue(df3,meterInfo.getPhaseAngleIa())+"");
	              res.put("Current Angle(B)",getDecimalValue(df3,meterInfo.getPhaseAngleIb())+"");
	              res.put("Current Angle(C)",getDecimalValue(df3,meterInfo.getPhaseAngleIc())+"");
			}

			if (this.billingData != null) {
				res.put("<b>[Current Billing Data]</b>", "");
				BillingData currbd = this.billingData.getCurrentBillingData();
				
				SimpleDateFormat billDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				Date billDate = billDateFormat.parse(currbd.getWriteDate());
				
				res.put("(1)Billing Date"              ,destFormat.format(billDate) +"");//
                res.put("(1)Total Active Energy(kWh)"              ,getDecimalValue(df3,currbd.getActiveEnergyRateTotal())+"");
                res.put("(1)Total Active Power Max.Demand(kW)"     ,getDecimalValue(df3,currbd.getActivePowerMaxDemandRateTotal())+"");
                //res.put("(1)Total Active Power Max.Demand Time"    ,StringUtil.nullToBlank(currbd.getActivePowerDemandMaxTimeRateTotal()));
                res.put("(1)Total Reactive Energy(kVarh)"          ,getDecimalValue(df3,currbd.getReactiveEnergyRateTotal())+"");
                res.put("(1)Total Reactive Power Max.Demand(kW)"   ,getDecimalValue(df3,currbd.getReactivePowerMaxDemandRateTotal())+"");
                //res.put("(1)Total Reactive Power Max.Demand Time"  ,StringUtil.nullToBlank(currbd.getReactivePowerDemandMaxTimeRateTotal()));


                res.put("(1)Rate 1 Active Energy(kWh)"             ,getDecimalValue(df3,currbd.getActiveEnergyRate1())+"");
                res.put("(1)Rate 1 Active Power Max.Demand(kW)"    ,getDecimalValue(df3,currbd.getActivePowerMaxDemandRate1())+"");
                //res.put("(1)Rate 1 Active Power Max.Demand Time"   ,StringUtil.nullToBlank(currbd.getActivePowerDemandMaxTimeRate1()));
                res.put("(1)Rate 1 Reactive Energy(kVarh)"          ,getDecimalValue(df3,currbd.getReactiveEnergyRate1())+"");
                res.put("(1)Rate 1 Reactive Power Max.Demand(kW)"    ,getDecimalValue(df3,currbd.getReactivePowerMaxDemandRate1())+"");
                //res.put("(1)Rate 1 Reactive Power Max.Demand Time"   ,StringUtil.nullToBlank(currbd.getReactivePowerDemandMaxTimeRate1()));

                res.put("(1)Rate 2 Active Energy(kWh)"             ,getDecimalValue(df3,currbd.getActiveEnergyRate2())+"");
                res.put("(1)Rate 2 Active Power Max.Demand(kW)"    ,getDecimalValue(df3,currbd.getActivePowerMaxDemandRate2())+"");
                //res.put("(1)Rate 2 Active Power Max.Demand Time"   ,StringUtil.nullToBlank(currbd.getActivePowerDemandMaxTimeRate2()));
                res.put("(1)Rate 2 Reactive Energy(kVarh)"          ,getDecimalValue(df3,currbd.getReactiveEnergyRate2())+"");
                res.put("(1)Rate 2 Reactive Power Max.Demand(kW)"    ,getDecimalValue(df3,currbd.getReactivePowerMaxDemandRate2())+"");
                //res.put("(1)Rate 2 Reactive Power Max.Demand Time"   ,StringUtil.nullToBlank(currbd.getReactivePowerDemandMaxTimeRate2()));

                res.put("(1)Rate 3 Active Energy(kWh)"             ,getDecimalValue(df3,currbd.getActiveEnergyRate3())+"");
                res.put("(1)Rate 3 Active Power Max.Demand(kW)"    ,getDecimalValue(df3,currbd.getActivePowerMaxDemandRate3())+"");
                //res.put("(1)Rate 3 Active Power Max.Demand Time"   ,StringUtil.nullToBlank(currbd.getActivePowerDemandMaxTimeRate3()));
                res.put("(1)Rate 3 Reactive Energy(kVarh)"          ,getDecimalValue(df3,currbd.getReactiveEnergyRate3())+"");
                res.put("(1)Rate 3 Reactive Power Max.Demand(kW)"    ,getDecimalValue(df3,currbd.getReactivePowerMaxDemandRate3())+"");
                //res.put("(1)Rate 3 Reactive Power Max.Demand Time"   ,StringUtil.nullToBlank(currbd.getReactivePowerDemandMaxTimeRate3()));
				
				
				
                res.put("<b>[Previous Billing Data]</b>", "");
				BillingData prebd = this.billingData.getBillingData();
				
				SimpleDateFormat preDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				Date preDate = preDateFormat.parse(prebd.getWriteDate());
				
				res.put("(2)Billing Date"              ,destFormat.format(preDate)+"");
                res.put("(2)Total Active Energy(kWh)"              ,getDecimalValue(df3,prebd.getActiveEnergyRateTotal())+"");
                res.put("(2)Total Active Power Max.Demand(kW)"     ,getDecimalValue(df3,prebd.getActivePowerMaxDemandRateTotal())+"");
                //res.put("(2)Total Active Power Max.Demand Time"    ,StringUtil.nullToBlank(prebd.getActivePowerDemandMaxTimeRateTotal()));
                res.put("(2)Total Reactive Energy(kVarh)"          ,getDecimalValue(df3,prebd.getReactiveEnergyRateTotal())+"");
                res.put("(2)Total Reactive Power Max.Demand(kW)"   ,getDecimalValue(df3,prebd.getReactivePowerMaxDemandRateTotal())+"");
                //res.put("(2)Total Reactive Power Max.Demand Time"  ,StringUtil.nullToBlank(prebd.getReactivePowerDemandMaxTimeRateTotal()));


                res.put("(2)Rate 1 Active Energy(kWh)"             ,getDecimalValue(df3,prebd.getActiveEnergyRate1())+"");
                res.put("(2)Rate 1 Active Power Max.Demand(kW)"    ,getDecimalValue(df3,prebd.getActivePowerMaxDemandRate1())+"");
                //res.put("(2)Rate 1 Active Power Max.Demand Time"   ,StringUtil.nullToBlank(prebd.getActivePowerDemandMaxTimeRate1()));
                res.put("(2)Rate 1 Reactive Energy(kVarh)"          ,getDecimalValue(df3,prebd.getReactiveEnergyRate1())+"");
                res.put("(2)Rate 1 Reactive Power Max.Demand(kW)"    ,getDecimalValue(df3,prebd.getReactivePowerMaxDemandRate1())+"");
                //res.put("(2)Rate 1 Reactive Power Max.Demand Time"   ,StringUtil.nullToBlank(prebd.getReactivePowerDemandMaxTimeRate1()));

                res.put("(2)Rate 2 Active Energy(kWh)"             ,getDecimalValue(df3,prebd.getActiveEnergyRate2())+"");
                res.put("(2)Rate 2 Active Power Max.Demand(kW)"    ,getDecimalValue(df3,prebd.getActivePowerMaxDemandRate2())+"");
                //res.put("(2)Rate 2 Active Power Max.Demand Time"   ,StringUtil.nullToBlank(prebd.getActivePowerDemandMaxTimeRate2()));
                res.put("(2)Rate 2 Reactive Energy(kVarh)"          ,getDecimalValue(df3,prebd.getReactiveEnergyRate2())+"");
                res.put("(2)Rate 2 Reactive Power Max.Demand(kW)"    ,getDecimalValue(df3,prebd.getReactivePowerMaxDemandRate2())+"");
                //res.put("(2)Rate 2 Reactive Power Max.Demand Time"   ,StringUtil.nullToBlank(prebd.getReactivePowerDemandMaxTimeRate2()));

                res.put("(2)Rate 3 Active Energy(kWh)"             ,getDecimalValue(df3,prebd.getActiveEnergyRate3())+"");
                res.put("(2)Rate 3 Active Power Max.Demand(kW)"    ,getDecimalValue(df3,prebd.getActivePowerMaxDemandRate3())+"");
                //res.put("(2)Rate 3 Active Power Max.Demand Time"   ,StringUtil.nullToBlank(prebd.getActivePowerDemandMaxTimeRate3()));
                res.put("(2)Rate 3 Reactive Energy(kVarh)"          ,getDecimalValue(df3,prebd.getReactiveEnergyRate3())+"");
                res.put("(2)Rate 3 Reactive Power Max.Demand(kW)"    ,getDecimalValue(df3,prebd.getReactivePowerMaxDemandRate3())+"");
                //res.put("(2)Rate 3 Reactive Power Max.Demand Time"   ,StringUtil.nullToBlank(prebd.getReactivePowerDemandMaxTimeRate3()));
				
			}

			if (this.eventLog != null) {
				res.put("<b>[Event Log]</b>", "");
				EventLogData[] eventLog = this.eventLog.getEventLog();
				
				SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				
				
				for(int i = 0 ; i < eventLog.length; i++){
					String datetime = eventLog[i].getDate() + eventLog[i].getTime();
					
					Date eventDate = eventDateFormat.parse(datetime);
					
					res.put("("+i+")"+destFormat.format(eventDate), eventLog[i].getMsg());
				}

			}

			if (this.lpData != null) {
				res.put("<b>[Instrument Data]</b>", "");
				Instrument[] instruments = this.lpData.getInstrument();
				SimpleDateFormat _lpDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
				
				if(instruments!=null && instruments.length>0){
					Instrument ins = instruments[0];
				//for(Instrument ins:instruments){
					Date _lpDateTime = _lpDateFormat.parse(ins.getDatetime());
					res.put("DateTime",destFormat.format(_lpDateTime)+"");
					res.put("Voltage A(V)",getDecimalValue(df3,ins.getVOL_A())+"");
                    res.put("Voltage B(V)",getDecimalValue(df3,ins.getVOL_B())+"");
                    res.put("Voltage C(V)",getDecimalValue(df3,ins.getVOL_C())+"");
                    res.put("Current A(A)",getDecimalValue(df3,ins.getCURR_A())+"");
                    res.put("Current B(A)",getDecimalValue(df3,ins.getCURR_B())+"");
                    res.put("Current C(A)",getDecimalValue(df3,ins.getCURR_C())+"");
                    res.put("PowerFactor A(%)",getDecimalValue(df3,ins.getPF_A())+"");
                    res.put("PowerFactor B(%)",getDecimalValue(df3,ins.getPF_B())+"");
                    res.put("PowerFactor C(%)",getDecimalValue(df3,ins.getPF_C())+"");
                    res.put("THD Voltage A(%)",getDecimalValue(df3,ins.getVOL_THD_A())+"");
                    res.put("THD Voltage B(%)",getDecimalValue(df3,ins.getVOL_THD_B())+"");
                    res.put("THD Voltage C(%)",getDecimalValue(df3,ins.getVOL_THD_C())+"");
                    res.put("THD Current A(%)",getDecimalValue(df3,ins.getCURR_THD_A())+"");
                    res.put("THD Current B(%)",getDecimalValue(df3,ins.getCURR_THD_B())+"");
                    res.put("THD Current C(%)",getDecimalValue(df3,ins.getCURR_THD_C())+"");
				//}
				}
				
				////////////
				//res.put("Load Profile Data(kWh)", "");

				LPData[] lpDatas = this.lpData.getLPData();
				int nbr_chn = lpDatas[0].getLPChannelCnt();
				ArrayList chartData0 = new ArrayList();//time chart
                ArrayList[] chartDatas = new ArrayList[nbr_chn]; //channel chart(ch1,ch2,...)
                ArrayList lpDataTime = new ArrayList();
                
                for(int k = 0; k < nbr_chn ; k++){
                    chartDatas[k] = new ArrayList();
                }
                
                res.put("[ChannelCount]", nbr_chn+"");
                
                SimpleDateFormat lpDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
				
                
				for(LPData lpdata:lpDatas){
					String datetime = lpdata.getDatetime();
                    String tempDateTime = lpdata.getDatetime();
                    String val = "";
                    Double[] ch = lpdata.getCh();
                    for(int k = 0; k < ch.length ; k++){
                        val += "ch"+(k+1)+"="+getDecimalValue(df3,ch[k])+"  ";
                    }
                    
                    Date lpDate = lpDateFormat.parse(datetime);
                    
                    res.put("LP "+destFormat.format(lpDate), val);
                    
                    chartData0.add(tempDateTime.substring(6,8)
                            +tempDateTime.substring(8,10)
                            +tempDateTime.substring(10,12));
					for(int k = 0; k < ch.length ; k++){
					    chartDatas[k].add(ch[k].doubleValue());
					}
					lpDataTime.add((String)lpdata.getDatetime());
					
				}

			}

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

	public Vector<PowerAlarmLogData> getPowerAlarmLog(){
		if(this.eventLog==null)
			return null;
		
		return this.eventLog.getPowerAlarmLog();
	}
	

	/**
	 * @deprecated 사용안함
	 * @return
	 * @see com.aimir.fep.meter.parser.MeterDataParser#getFlag()
	 */
	@Override
	public int getFlag() {
		return 0;
	}

	public MX2MeterInfo getMeterInfo() {
		return meterInfo;
	}

	public MX2BillingData getBillingData() {
		return billingData;
	}

	public MX2EventLog getEventLog() {
		return eventLog;
	}

	public MX2LPData getLpData() {
		return lpData;
	}

	/**
	 * @deprecated 사용안함
	 * @see com.aimir.fep.meter.parser.MeterDataParser#getLength()
	 */
	@Override
	public int getLength() {
		return 0;
	}

	/**
	 * @deprecated 사용안함
	 * @see com.aimir.fep.meter.parser.MeterDataParser#getMeteringValue()
	 */
	@Override
	public Double getMeteringValue() {
		return null;
	}

	

	public SAP getSap() {
		return sap;
	}

	public void setSap(SAP sap) {
		this.sap = sap;
	}

	/**
	 * <p>
	 * 문서에 정의 된 데이터 포멧을 받아 구조체를 생성하는 역할을한다.
	 * </p>
	 * <p>
	 * 각각 테이블별로 구조체를 생성하는 메소드를 호출하는 역할이다.
	 * </p>
	 * 
	 * @see com.aimir.fep.meter.parser.MeterDataParser#parse(byte[])
	 */
	@Override
	public void parse(byte[] data) throws Exception {
	    log.info(Hex.decode(data));
		ByteArrayInputStream bis = new ByteArrayInputStream(data);	
		
		// 전부 읽을때까지 반복
		while (bis.available() != 0) {
			// 테이블 이름을 읽어온다.
			byte[] tableName = new byte[2];
			bis.read(tableName);
			String sTableName = new String(tableName);

			// 테이블 데이터의 길이를 읽어온다.
			byte[] length = new byte[2];
			bis.read(length);
			int nLength = DataUtil.getIntTo2Byte(length);

			// 테이블 데이터를 길이만큼 읽어온다.
			byte[] bValue = new byte[nLength];
			bis.read(bValue);

			log.debug(String.format("parse table[%s]", sTableName));
			
			if (sTableName.equals("MT")) {
			    parseMT(bValue);
			}
			else if (sTableName.equals("BD")) {
			    parseBD(bValue);
			}
			else if (sTableName.equals("EL")) {
			    parseEL(bValue);
			}
			else if (sTableName.equals("LD")) {
			    parseLD(bValue, Double.parseDouble(meterInfo.getMaximumCurrent()));
			}
			else if (sTableName.equals("SA")) {
			    parseSA(bValue);
			}
			else if (sTableName.equals("MD")) {
			    parseMD(bValue);
			}
			else {
			    log.error(String.format("No such method parse[%s]", sTableName));
			    break;
			}
		}
		
		double energyUnit = 1.0;
		double energyDecimal = 1.0;
		double demandUnit = 1.0;
		double demandDecimal = 1.0;
		
		if (lpData != null) {
		    energyUnit = lpData.getEnergyUnit();
		    energyDecimal = lpData.getEnergyDecimal();
		    demandUnit = lpData.getDemandUnit();
		    demandDecimal = lpData.getDemandDecimal();
		}
		
		if (billingData != null) {
		    billingData.setPresentEnergy(energyUnit, energyDecimal);
		    billingData.setPreviousEnergy(energyUnit, energyDecimal);
		    billingData.setPreviousMaxDemand(demandUnit, demandDecimal);
		}
	}

	/**
	 * Modem Info
	 * 
	 * @param value
	 */
	private void parseMD(byte[] value) {

	}

	/**
	 * Meter Info 파싱.
	 * <p>
	 * 테이블 데이터 포멧을 DB로 저장할수 있도록 중간 저장되는 구조체(Class)로 변환하는 역할이다.
	 * </p>
	 * 
	 * @param meterInfo
	 */
	private void parseMT(byte[] value) {
		meterInfo = new MX2MeterInfo(value);
	}

	/**
	 * Billing Data 파싱.
	 * <p>
	 * 테이블 데이터 포멧을 DB로 저장할수 있도록 중간 저장되는 구조체(Class)로 변환하는 역할이다.
	 * </p>
	 * 
	 * @param meterInfo
	 */
	private void parseBD(byte[] value) {
		billingData = new MX2BillingData(value);
	}

	/**
	 * Event Log 파싱
	 * <p>
	 * 테이블 데이터 포멧을 DB로 저장할수 있도록 중간 저장되는 구조체(Class)로 변환하는 역할이다.
	 * </p>
	 * 
	 * @param value
	 */
	private void parseEL(byte[] value) {
		eventLog = new MX2EventLog(value);
	}

	/**
	 * Load Recent Data 파싱
	 * <p>
	 * 테이블 데이터 포멧을 DB로 저장할수 있도록 중간 저장되는 구조체(Class)로 변환하는 역할이다.
	 * </p>
	 * 
	 * @param value
	 */
	private void parseLD(byte[] value, double lmax) {
		lpData = new MX2LPData(value, lmax);
	}
	
	/**
	 * SAP 관련 정보를 파싱.
	 * @param value
	 */
	private void parseSA(byte[] value) {
		sap = new SAP();
		
		String meaNumber = null;
		String saveTime = null;
		String errorCode = null;
		Integer nMultiplier = null;
		
		
		byte[] charCode3 = new byte[8];
		byte[] longChar = new byte[10];
		byte[] savingDate = new byte[3];
		byte[] bMultiplier = new byte[2];
		byte[] bErrorCode = new byte[9];
		
		ByteArrayInputStream bos = new ByteArrayInputStream(value);
		try {
			// Character Code3
			bos.read(charCode3);
			
			// Long Character Code
			bos.read(longChar);
			
			// Daylight saving present time
			bos.read(savingDate);
			bos.skip(4);//뒤에 4바이트는 사용안하기때문에 버린다.
			
			// Multiplier
			bos.read(bMultiplier);
			
			// ErrorCode
			bos.read(bErrorCode);
		} catch (IOException e) {
			log.error(e, e);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(new String(charCode3)); 
		sb.append(new String(longChar));
		
		String meaTem = sb.toString();//.replaceAll(" ", "0");//일단 space 를 0으로 치환
		
		//뒤 6자리를 잘라 처리한다.
		Integer nSuffix = 0;
		try{
			String strSuffix = meaTem.substring(meaTem.length()-6, meaTem.length());
			nSuffix = Integer.parseInt(strSuffix);
			
			//에러는 무시하고 에러일때는 0값을 갖도록 한다.
		}catch(IndexOutOfBoundsException  e){
			//인덱스 오류
			log.error(e,e);
		}catch(NumberFormatException e){
			//파싱 에러
			log.error(e,e);
		}
		
		meaNumber = meaTem.substring(0,meaTem.length()-6) + String.format("%06d", nSuffix);
					
		saveTime = DataUtil.getBCDtoBytes(savingDate);
		
		nMultiplier = getMultiplier(bMultiplier);
		log.debug("Multiplier[" + nMultiplier + "]");
		
		errorCode = new String(bErrorCode);
		
		sap.setMeaNumber(meaNumber);
		sap.setSaveTime(saveTime);
		sap.setMultiplier(nMultiplier);
		sap.setErrorCode(errorCode.replaceAll(" ", "0")); //space 를 0으로 치환
		sap.setIsExport(false);
	}

	
	/**
	 * Multiplier 를 구한다.
	 * 
	 * @param multiplier
	 * @return
	 */
	private Integer getMultiplier(byte[] multiplier) {
	    /*
		// Exponent : 0 ~ 3 (bit 3 ~ 0), 비트 3번째부터 0번째까지
		byte exponent = multiplier[1];

		// bit 3 ~ 0 까지가 exponent 이다. 0x0f 와 AND 연산을 하면 7 ~ 4 까지의 bit 가 0으로 채워져
		// bit 3 ~ 0 까지 값을 구할수 있다.
		exponent &= 0x0F;

		int nExponent = (int) exponent;

		// significant 001h ~ 999h (bit15 ~ 4)
		byte[] significant = multiplier;
		// significant 를 구하기 위해서는 exponent 비트만큼 전체 비트를 오른쪽으로 시프트 하여 int를 구한다.
		significant[1] = (byte) ((significant[1] >> 4) | (significant[0] << 4));
		significant[0] = (byte) (significant[0] >> 4);

		int nSignificant = DataUtil.getIntTo2Byte(significant);

		// 구하는 식.
		return new Double(nSignificant * Math.pow(10, nExponent)).intValue();
		*/
	    String s = Hex.decode(multiplier);
	    log.debug("Multiplier[" + s + "]");
	    int exponent = Integer.parseInt(s.substring(3));
	    int significant = Integer.parseInt(s.substring(0, 3));
	    return (int) (significant * Math.pow(10, exponent));
	}
	
	/**
	 * @deprecated 사용안함
	 * @see com.aimir.fep.meter.parser.MeterDataParser#setFlag(int)
	 */
	@Override
	public void setFlag(int flag) {

	}

	/**
	 * superclass 정보
	 * 
	 * @see com.aimir.fep.meter.parser.MeterDataParser#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSuperclass().toString();
	}

	public String getMeterTime() {
		String meterTime = meterInfo.getMeterData().getTime();
		Calendar cal = Calendar.getInstance(); // 현재 년도 [yy]yy 중 앞의 두 자리를 가져오기
		// 위함.
		return Integer.toString(cal.get(Calendar.YEAR)).substring(0, 2)
				+ meterTime;
	}

	/**
	 * @deprecated 사용 안함
	 * @see com.aimir.fep.meter.parser.MeterDataParser#getRawData()
	 */
	@Override
	public byte[] getRawData() {
		// TODO Auto-generated method stub
		return null;
	}

}
