package com.aimir.fep.meter.parser.MX2Table;


import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.BillingData;
import com.aimir.model.mvm.SAP;


/**
 * SAP System 에 정의된 Column 과 1:1 매치되는 Wrapper 역할의 객체<br>
 * 용어 불 일치로 인한 혼동을 줄이고자  SAP, BillingMonthEM, RealTimeBillingEM 필요한 값들만 Wrapping 하는 역할.
 * @author kskim
 *
 */
public class SAPWrap {
	private static Log log = LogFactory.getLog(SAPWrap.class);
	
	//Wrap 대상.
	private SAP sap;
	private BillingData billing;
	private BillingData currBilling;

	/**
	 * Previous Reset Date (DDMMYY)<br>
	 * DDMMYY 포멧 cache
	 */
	private String preDate;
	
	/**
	 * Previous Reset Time (HHMMSS)<br>
	 * HHMMSS 포멧 cache
	 */
	private String preTime;
	
	/**
	 * Current Month Read Date (DDMMYY)
	 * DDMMYY 포멧 cache
	 */
	private String currDate;
	
	public SAP getSap() {
		return sap;
	}
	public void setSap(SAP sap) {
		this.sap = sap;
	}
	public BillingData getBilling() {
		return billing;
	}
	public void setBilling(BillingData billing) {
		this.billing = billing;
	}
	public BillingData getCurrBilling() {
		return currBilling;
	}
	public void setCurrBilling(BillingData currBilling) {
		this.currBilling = currBilling;
	}
	
	//포멧과 1:1매치되는 getter
	
	
	/**
	 * 1. MEA Number
	 * @return
	 */
	public String getMeaNumber(){
		return sap.getMeaNumber();
	}
	
	/**
	 * 2. Previous Reset Date (DDMMYY)
	 * @return
	 */
	public String getPreResetDate(){//8-14
		if(this.preDate == null){//yyyyMMddHHmmss
			String yyyyMMdd = billing.getBillingTimestamp().substring(0, 8);
			String ddMMyy = convDateFormat(yyyyMMdd);
			this.preDate = ddMMyy;
			return this.preDate;
		}
		return this.preDate;
	}
	
	/**
	 * 3. Current Month Read Date (DDMMYY)
	 * @return
	 */
	public String getCurrentMonthReadDate(){
		if(this.currDate == null){
			String yyyyMMdd = currBilling.getBillingTimestamp().substring(0, 8);
			String ddMMyy = convDateFormat(yyyyMMdd);
			this.currDate = ddMMyy;
			return this.currDate;
		}
		return this.currDate;
	}
	
	/**
	 * 4. Current Month kWh Reading (Total)
	 * @return
	 */
	public String getCurrentMonth_kWh_Reading_Total(){
		Double temp = currBilling.getActiveEnergyImportRateTotal();
		if (sap.getPhaseWires() == 2)
		    temp /= 1000.0;
		String value = String.format("%06.0f", Math.floor(temp));
		return value;
	}
	
	/**
	 * 5. Current Month kWh Reading Rate A
	 * @return
	 */
	public String getCurrentMonth_kWh_Reading_Rate_A(){
		Double temp = currBilling.getActiveEnergyImportRate1();
		if (sap.getPhaseWires() == 2)
            temp /= 1000.0;
		String value = String.format("%06.0f", Math.floor(temp));
		return value;
	}
	
	/**
	 * 6. Current Month kWh Reading Rate B
	 * @return
	 */
	public String getCurrentMonth_kWh_Reading_Rate_B(){
		Double temp = currBilling.getActiveEnergyImportRate2();
		if (sap.getPhaseWires() == 2)
            temp /= 1000.0;
		String value = String.format("%06.0f", Math.floor(temp));
		return value;
	}
	
	/**
	 * 7. Current Month kWh Reading Rate C
	 * @return
	 */
	public String getCurrentMonth_kWh_Reading_Rate_C(){
		Double temp = currBilling.getActiveEnergyImportRate3();
		if (sap.getPhaseWires() == 2)
            temp /= 1000.0;
		String value = String.format("%06.0f", Math.floor(temp));
		return value;
	}
	
	private Double formatKWforP2(Double temp) {
	    Double _temp = temp;
	    _temp /= 100.0;
        
        String[] strArray = (_temp.toString()).split("\\.");
        _temp = Double.parseDouble("0." + strArray[1]) * 100.0;
        
        return _temp;
	}
	
	/**
	 * 8. Max kW Rate A (On peak) at Previous Reset Date
	 * @return
	 */
	public String getMax_kW_Rate_A(){
		Double temp = billing.getActivePwrDmdMaxImportRate1();
		if (sap.getPhaseWires() == 2) {
			temp = formatKWforP2(temp);
		}

		String value = insertDecimalPoint(temp);
		return value;
	}
	
	/**
	 * 9. Max kW Rate B (On peak) at Previous Reset Date
	 * @return
	 */
	public String getMax_kW_Rate_B(){
		Double temp = billing.getActivePwrDmdMaxImportRate2();
		if (sap.getPhaseWires() == 2) {
		    temp = formatKWforP2(temp);
		}
		
		String value = insertDecimalPoint(temp);
		return value;
	}
	
	/**
	 * 10. Max kW Rate C (On peak) at Previous Reset Date
	 * @return
	 */
	public String getMax_kW_Rate_C(){
		Double temp = billing.getActivePwrDmdMaxImportRate3();
		if (sap.getPhaseWires() == 2) {
		    temp = formatKWforP2(temp);
		}
		
		String value = insertDecimalPoint(temp);
		return value;
	}
	
	/**
	 * 11. Max kvr Rate A (On peak) at Previous Reset Date
	 * @return
	 */
	public String getMax_kvr_Rate_A(){
		Double temp = billing.getReactivePwrDmdMaxLagImportRate1();
		if (sap.getPhaseWires() == 2) {
		    temp = formatKWforP2(temp);
		}
		
		String value = insertDecimalPoint(temp);
		return value;
	}
	
	/**
	 * 12. Max kvr Rate B (On peak) at Previous Reset Date
	 * @return
	 */
	public String getMax_kvr_Rate_B(){
		Double temp = billing.getReactivePwrDmdMaxLagImportRate2();
		if (sap.getPhaseWires() == 2) {
		    temp = formatKWforP2(temp);
		}
		
		String value = insertDecimalPoint(temp);
		return value;
	}
	
	/**
	 * 13. Max kvr Rate C (On peak) at Previous Reset Date
	 * @return
	 */
	public String getMax_kvr_Rate_C(){
		Double temp = billing.getReactivePwrDmdMaxLagImportRate3();
		if (sap.getPhaseWires() == 2) {
		    temp = formatKWforP2(temp);
		}
		
		String value = insertDecimalPoint(temp);
		return value;
	}
	
	/**
	 * 14. kWh Reading Total at Previous Reset Date
	 * @return
	 */
	public String getkWh_Reading_Total(){
		Double temp = billing.getActiveEnergyImportRateTotal();
		if (sap.getPhaseWires() == 2)
            temp /= 1000.0;
		String value = String.format("%08.0f", Math.floor(temp));//insertDecimalPoint(temp);
		return value;
	}
	
	/**
	 * 15. kWh Rate A at Previous Reset Date
	 * @return
	 */
	public String getkWh_Rate_A(){
		Double temp = billing.getActiveEnergyImportRate1();
		if (sap.getPhaseWires() == 2)
            temp /= 1000.0;
		String value = String.format("%08.0f", Math.floor(temp));
		return value;
	}
	
	/**
	 * 16. kWh Rate B at Previous Reset Date
	 * @return
	 */
	public String getkWh_Rate_B(){
		Double temp = billing.getActiveEnergyImportRate2();
		if (sap.getPhaseWires() == 2)
            temp /= 1000.0;
		String value = String.format("%08.0f", Math.floor(temp));
		return value;
	}
	
	/**
	 * 17. kWh Rate C at Previous Reset Date
	 * @return
	 */
	public String getkWh_Rate_C(){
		Double temp = billing.getActiveEnergyImportRate3();
		if (sap.getPhaseWires() == 2)
            temp /= 1000.0;
		String value = String.format("%08.0f", Math.floor(temp));
		return value;
	}
	
	/**
	 * 18,19,20. Multiplier
	 * @return
	 */
	public String getMultiPlier(){
	    // 3p3w만 1000
	    if (sap.getPhaseWires() == 2)
	        return autoFillZero(6, 1000);
	    else return autoFillZero(6, 1);
	}
	
	/**
	 * 21. Error Code/Error Note
	 * @return
	 */
	public String getErrorCode(){
		return autoFillZero(9, sap.getErrorCode());
	}
	
	/**
	 * 22. Previous Reset Time
	 * @return
	 */
	public String getPreResetTime(){
		if(this.preTime == null){
			String HHmmss = billing.getBillingTimestamp().substring(8, 14);
			this.preTime = HHmmss;
			return this.preTime;
		}
		return this.preTime;
	}
	
	/**
	 * 해당 문자열에 지정된 위치에 소수점(.)을 추가한다. <br>
	 * 소수점 위치 기준은 3p4w 는 2자리(.00), 3p3w(.000)
	 * @param value 문자열
	 * @return
	 */
	private String insertDecimalPoint(Double value){
		if(value==null){
			value=new Double(0);
		}
		
		//3p4w = .00,  3p3w = .000
		int phaseCode = this.sap.getPhaseWires();
		String format = "%06.2f"; 
		switch(phaseCode){
		case 2:
			format = "%06.3f";
			break;
		case 3:
			format = "%06.2f";
			break;
		default :
			format = "%06.2f";
			break;
		}
		
		return String.format(format,value);
	}
	
	/**
	 * Multiplier 가 적용된 값을 다시 미적용 상태로 되돌린다.
	 * @param value
	 * @deprecated 필요없음 나중에 빼야됨 kskim
	 * @return
	 */
	private Double resetValue(Double value){
		if(value==null){
			return 0d;
		}
		//Integer multiplier = sap.getMultiplier();
		Double resetValue = value;
		return resetValue;
	}
	
	/**
	 * @see #autoFillZero(int, String)
	 * @param size 자릿수
	 * @param value 값, 값을 먼저 Multiplier 적용을 해제한다.@see #resetValue(Double)
	 * @return
	 */
	private String autoFillZero(int size,Double value){
		Double dv = resetValue(value);
		
		
		
		//소수점을 뺀 수만 문자열로 변환한다.
		return autoFillZero(size,String.format("%.0f", dv));
	}
	/**
	 * @see #autoFillZero(int, String)
	 * @param size
	 * @param value
	 * @return
	 */
	private String autoFillZero(int size, Integer value) {
		return autoFillZero(size,new Double(value));
	}
	/**
	 * 고정된 자릿수 만큼 모자른 부분을 앞부분부터 0으로 채운다. ex) 1 -> 0..001
	 * @param size
	 * @param value
	 * @return
	 */
	private String autoFillZero(int size, String value) {
		if(value==null){
			value="0";
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i<(size-value.length());i++){
			sb.append("0");
		}
		sb.append(value);
		return sb.toString();
	}
	
	/**
	 * 날짜 형식 "yyyyMMdd" 를 "ddMMyy"포멧으로 변경.
	 * @param yyyyMMdd
	 * @return
	 */
	private String convDateFormat(String yyyyMMdd){
		if(yyyyMMdd==null || yyyyMMdd.length()<8)
			return null;
		
		String ddMMyy = null;
		
		StringBuilder sb = new StringBuilder();
		//dd
		sb.append(yyyyMMdd.substring(6, 8));
		//MM
		sb.append(yyyyMMdd.substring(4, 6));
		//yy
		sb.append(yyyyMMdd.substring(2, 4));
		
		ddMMyy = sb.toString();
		
		return ddMMyy;
	}
	
	/**
	 * MEA Billing File Format 에 맞는 파일을 출력한다.(SAP system)<br>
	 * 출력된 파일 내용을 SAP 객체 outputData 필드에 저장한다.
	 * @deprecated 다른곳에서 파일 출력 기능 구현
	 * @param dirPath
	 * @param fileName
	 * @param ext
	 */
	public void toFile(String dirPath, String fileName, String ext){
		
		// 폴더 생성.
		File dir = new File(dirPath);
		dir.mkdirs();
		
		StringBuilder fullPath = new StringBuilder();
		fullPath.append(dirPath);
		fullPath.append("/");
		fullPath.append(fileName);
		fullPath.append(".");
		fullPath.append(ext);
		
		File file = new File(fullPath.toString());
		
		if(file.exists()){
			//기존 파일은 삭제
			//file.deleteOnExit();
		}
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			String outputFile = this.toSAPFormat();
			log.debug(outputFile);
			fos.write(outputFile.getBytes());
			fos.flush();
			fos.close();
			this.sap.setOutputData(outputFile);
		} catch (Exception e) {
			log.error(e);
			log.error(String.format("fail save file - [%s]", fullPath.toString()));
		}

		
		
	}
	
	/**
	 * 필드 내용을 순서에 맞게 정렬하여 파일 포멧 순서에 맞는 문자열을 구성한다.
	 * @return
	 */
	public String toSAPFormat(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.getMeaNumber());
		sb.append(this.getPreResetDate());
		sb.append(this.getCurrentMonthReadDate());
		sb.append(this.getCurrentMonth_kWh_Reading_Total());
		sb.append(this.getCurrentMonth_kWh_Reading_Rate_A());
		sb.append(this.getCurrentMonth_kWh_Reading_Rate_B());
		sb.append(this.getCurrentMonth_kWh_Reading_Rate_C());
		sb.append(this.getMax_kW_Rate_A());
		sb.append(this.getMax_kW_Rate_B());
		sb.append(this.getMax_kW_Rate_C());
		sb.append(this.getMax_kvr_Rate_A());
		sb.append(this.getMax_kvr_Rate_B());
		sb.append(this.getMax_kvr_Rate_C());
		sb.append(this.getkWh_Reading_Total());
		sb.append(this.getkWh_Rate_A());
		sb.append(this.getkWh_Rate_B());
		sb.append(this.getkWh_Rate_C());
		sb.append(this.getMultiPlier());
		sb.append(this.getMultiPlier());
		sb.append(this.getMultiPlier());
		sb.append(this.getErrorCode());
		sb.append(this.getPreResetTime());
		return sb.toString();
	}
}
