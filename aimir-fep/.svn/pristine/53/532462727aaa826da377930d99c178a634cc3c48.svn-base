package com.aimir.fep.meter.parser.a3rlnqTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.BillingData;

public class A3_PB implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1208737592734801325L;
	private Log log = LogFactory.getLog(A3_PB.class);
	private byte[] rawData = null;

	byte[] s21 = new byte[10];
	byte[] s25 = new byte[0];
	private ST21 st21 = null;
	private ST25 st25 = null;

	public A3_PB(byte[] rawData, int MeterConstantScale) {
        this.rawData = rawData;
		parse(MeterConstantScale);
	}
	
	public void parse(int MeterConstantScale) {
		
		int offset = 0;
		int meterConstantScale = MeterConstantScale;
        System.arraycopy(rawData,offset,s21,0,s21.length);
        offset += s21.length;
        s25 = new byte[rawData.length - offset];
        System.arraycopy(rawData,offset,s25,0,s25.length);
        offset += s25.length;
        this.st21 = new ST21(s21);
		this.st25 = new ST25(s25, st21.getNBR_TIERS(), st21.getNBR_SUMMATIONS(), st21.getNBR_DEMANDS(),st21.getNBR_COINCIDENT(), meterConstantScale);
	}
	 public void setBillingData(BillingData bd){
		 this.st25 = null;
	 }
	
	public BillingData getBillingData(){
		
		BillingData bill = new BillingData();


		try {
			bill.setWriteDate(st25.getResetTime());
			bill.setActiveEnergyRateTotal(st25.getAWHTOT());
			bill.setActiveEnergyRate1(st25.getARATE1WH());
			bill.setActiveEnergyRate2(st25.getARATE2WH());
			bill.setActiveEnergyRate3(st25.getARATE3WH());
			
			bill.setReactiveEnergyRateTotal(st25.getRWHTOT());		
			bill.setReactiveEnergyRate1(st25.getRRATE1WH());
			bill.setReactiveEnergyRate2(st25.getRRATE2WH());
			bill.setReactiveEnergyRate3(st25.getRRATE3WH());

			bill.setActivePowerMaxDemandRate1(st25.getARATE1MAXW());
			bill.setActivePowerMaxDemandRate2(st25.getARATE2MAXW());
			bill.setActivePowerMaxDemandRate3(st25.getARATE3MAXW());
			
			bill.setActivePowerDemandMaxTimeRate1(st25.getARATE1MAXWTIME());
			bill.setActivePowerDemandMaxTimeRate2(st25.getARATE2MAXWTIME());
			bill.setActivePowerDemandMaxTimeRate3(st25.getARATE3MAXWTIME());

			bill.setReactivePowerMaxDemandRate1(st25.getRRATE1MAXW());
			bill.setReactivePowerMaxDemandRate2(st25.getRRATE2MAXW());
			bill.setReactivePowerMaxDemandRate3(st25.getRRATE3MAXW());
			
			bill.setReactivePowerDemandMaxTimeRate1(st25.getRRATE1MAXWTIME());
			bill.setReactivePowerDemandMaxTimeRate2(st25.getRRATE2MAXWTIME());
			bill.setReactivePowerDemandMaxTimeRate3(st25.getRRATE3MAXWTIME());
			
			bill.setCumulativeActivePowerDemandRate1(st25.getARATE1CUMW());
			bill.setCumulativeActivePowerDemandRate2(st25.getARATE2CUMW());
			bill.setCumulativeActivePowerDemandRate3(st25.getARATE3CUMW());
			
			bill.setCumulativeReactivePowerDemandRate1(st25.getRRATE1CUMW());
			bill.setCumulativeReactivePowerDemandRate2(st25.getRRATE2CUMW());
			bill.setCumulativeReactivePowerDemandRate3(st25.getRRATE3CUMW());
		} catch (Exception e) {
			e.printStackTrace();
			bill = null;
		}


		return bill;
	}
	
	
}