package com.aimir.fep.protocol.fmp.frame.service.entry;

import com.aimir.fep.protocol.fmp.datatype.*;
import com.aimir.fep.protocol.fmp.frame.service.Entry;


public class dailyBillingEntry extends Entry {
	private OCTET groupName = new OCTET();
	private TIMESTAMP billingDate = new TIMESTAMP();
	private OCTET billingData = new OCTET();
	private OCTET usageCharge = new OCTET();
	private OCTET usage = new OCTET();
	private OCTET cummulativeUsage = new OCTET();
	private OCTET cummulativeCharge = new OCTET();
	private OCTET totalCummulativeUsage = new OCTET();
	private OCTET useMiles = new OCTET();
	private OCTET saveMiles = new OCTET();
	private OCTET remainMiles = new OCTET();
	private OCTET mileageUnits = new OCTET();
	/**
	 * @return the groupName
	 */
	public OCTET getGroupName() {
		return groupName;
	}
	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(OCTET groupName) {
		this.groupName = groupName;
	}
	/**
	 * @return the billingDate
	 */
	public TIMESTAMP getBillingDate() {
		return billingDate;
	}
	/**
	 * @param billingDate the billingDate to set
	 */
	public void setBillingDate(TIMESTAMP billingDate) {
		this.billingDate = billingDate;
	}
	/**
	 * @return the billingData
	 */
	public OCTET getBillingData() {
		return billingData;
	}
	/**
	 * @param billingData the billingData to set
	 */
	public void setBillingData(OCTET billingData) {
		this.billingData = billingData;
	}
	/**
	 * @return the usageCharge
	 */
	public OCTET getUsageCharge() {
		return usageCharge;
	}
	/**
	 * @param usageCharge the usageCharge to set
	 */
	public void setUsageCharge(OCTET usageCharge) {
		this.usageCharge = usageCharge;
	}
	/**
	 * @return the usage
	 */
	public OCTET getUsage() {
		return usage;
	}
	/**
	 * @param usage the usage to set
	 */
	public void setUsage(OCTET usage) {
		this.usage = usage;
	}
	/**
	 * @return the cummulativeUsage
	 */
	public OCTET getCummulativeUsage() {
		return cummulativeUsage;
	}
	/**
	 * @param cummulativeUsage the cummulativeUsage to set
	 */
	public void setCummulativeUsage(OCTET cummulativeUsage) {
		this.cummulativeUsage = cummulativeUsage;
	}
	/**
	 * @return the cummulativeCharge
	 */
	public OCTET getCummulativeCharge() {
		return cummulativeCharge;
	}
	/**
	 * @param cummulativeCharge the cummulativeCharge to set
	 */
	public void setCummulativeCharge(OCTET cummulativeCharge) {
		this.cummulativeCharge = cummulativeCharge;
	}
	/**
	 * @return the totalCummulativeUsage
	 */
	public OCTET getTotalCummulativeUsage() {
		return totalCummulativeUsage;
	}
	/**
	 * @param totalCummulativeUsage the totalCummulativeUsage to set
	 */
	public void setTotalCummulativeUsage(OCTET totalCummulativeUsage) {
		this.totalCummulativeUsage = totalCummulativeUsage;
	}
	/**
	 * @return the useMiles
	 */
	public OCTET getUseMiles() {
		return useMiles;
	}
	/**
	 * @param useMiles the useMiles to set
	 */
	public void setUseMiles(OCTET useMiles) {
		this.useMiles = useMiles;
	}
	/**
	 * @return the saveMiles
	 */
	public OCTET getSaveMiles() {
		return saveMiles;
	}
	/**
	 * @param saveMiles the saveMiles to set
	 */
	public void setSaveMiles(OCTET saveMiles) {
		this.saveMiles = saveMiles;
	}
	/**
	 * @return the remainMiles
	 */
	public OCTET getRemainMiles() {
		return remainMiles;
	}
	/**
	 * @param remainMiles the remainMiles to set
	 */
	public void setRemainMiles(OCTET remainMiles) {
		this.remainMiles = remainMiles;
	}
	/**
	 * @return the mileageUnits
	 */
	public OCTET getMileageUnits() {
		return mileageUnits;
	}
	/**
	 * @param mileageUnits the mileageUnits to set
	 */
	public void setMileageUnits(OCTET mileageUnits) {
		this.mileageUnits = mileageUnits;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "dailyBillingEntry [billingData=" + billingData
				+ ", billingDate=" + billingDate + ", cummulativeCharge="
				+ cummulativeCharge + ", cummulativeUsage=" + cummulativeUsage
				+ ", groupName=" + groupName + ", mileageUnits=" + mileageUnits
				+ ", remainMiles=" + remainMiles + ", saveMiles=" + saveMiles
				+ ", totalCummulativeUsage=" + totalCummulativeUsage
				+ ", usage=" + usage + ", usageCharge=" + usageCharge
				+ ", useMiles=" + useMiles + "]";
	}
	
}
