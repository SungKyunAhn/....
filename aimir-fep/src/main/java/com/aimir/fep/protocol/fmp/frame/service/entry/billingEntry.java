package com.aimir.fep.protocol.fmp.frame.service.entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.datatype.*;
import com.aimir.fep.protocol.fmp.frame.service.Entry;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "billingEntry", propOrder = {
    "groupName",
    "billingDate",
    "meteringDate",
    "deadLine",
    "realCharge",
    "totalCharge",
    "totalUsage",
    "usageCharge",
    "basicCharge",
    "discountCharge",
    "addtionalCharge",
    "useMiles",
    "saveMiles",
    "remainMiles",
    "vat",
    "cummulativeCo2",
    "cummulativeCo2Reduction",
    "co2",
    "co2Reduction",
    "mileageUnits"
})
public class billingEntry extends Entry {
	private OCTET groupName = new OCTET();
	private TIMESTAMP billingDate = new TIMESTAMP();
	private TIMESTAMP meteringDate = new TIMESTAMP();
	private TIMESTAMP deadLine = new TIMESTAMP();
	private OCTET realCharge = new OCTET();
	private OCTET totalCharge = new OCTET();
	private OCTET totalUsage = new OCTET();
	private OCTET usageCharge = new OCTET();
	private OCTET basicCharge = new OCTET();
	private OCTET discountCharge = new OCTET();
	private OCTET addtionalCharge = new OCTET();
	private OCTET useMiles = new OCTET();
	private OCTET saveMiles = new OCTET();
	private OCTET remainMiles = new OCTET();
	private OCTET vat = new OCTET();
	private OCTET cummulativeCo2 = new OCTET();
	private OCTET cummulativeCo2Reduction = new OCTET();
	private OCTET co2 = new OCTET();
	private OCTET co2Reduction = new OCTET();
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
	 * @return the meteringDate
	 */
	public TIMESTAMP getMeteringDate() {
		return meteringDate;
	}
	/**
	 * @param meteringDate the meteringDate to set
	 */
	public void setMeteringDate(TIMESTAMP meteringDate) {
		this.meteringDate = meteringDate;
	}
	/**
	 * @return the deadLine
	 */
	public TIMESTAMP getDeadLine() {
		return deadLine;
	}
	/**
	 * @param deadLine the deadLine to set
	 */
	public void setDeadLine(TIMESTAMP deadLine) {
		this.deadLine = deadLine;
	}
	/**
	 * @return the realCharge
	 */
	public OCTET getRealCharge() {
		return realCharge;
	}
	/**
	 * @param realCharge the realCharge to set
	 */
	public void setRealCharge(OCTET realCharge) {
		this.realCharge = realCharge;
	}
	/**
	 * @return the totalCharge
	 */
	public OCTET getTotalCharge() {
		return totalCharge;
	}
	/**
	 * @param totalCharge the totalCharge to set
	 */
	public void setTotalCharge(OCTET totalCharge) {
		this.totalCharge = totalCharge;
	}
	/**
	 * @return the totalUsage
	 */
	public OCTET getTotalUsage() {
		return totalUsage;
	}
	/**
	 * @param totalUsage the totalUsage to set
	 */
	public void setTotalUsage(OCTET totalUsage) {
		this.totalUsage = totalUsage;
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
	 * @return the basicCharge
	 */
	public OCTET getBasicCharge() {
		return basicCharge;
	}
	/**
	 * @param basicCharge the basicCharge to set
	 */
	public void setBasicCharge(OCTET basicCharge) {
		this.basicCharge = basicCharge;
	}
	/**
	 * @return the discountCharge
	 */
	public OCTET getDiscountCharge() {
		return discountCharge;
	}
	/**
	 * @param discountCharge the discountCharge to set
	 */
	public void setDiscountCharge(OCTET discountCharge) {
		this.discountCharge = discountCharge;
	}
	/**
	 * @return the addtionalCharge
	 */
	public OCTET getAddtionalCharge() {
		return addtionalCharge;
	}
	/**
	 * @param addtionalCharge the addtionalCharge to set
	 */
	public void setAddtionalCharge(OCTET addtionalCharge) {
		this.addtionalCharge = addtionalCharge;
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
	 * @return the vat
	 */
	public OCTET getVat() {
		return vat;
	}
	/**
	 * @param vat the vat to set
	 */
	public void setVat(OCTET vat) {
		this.vat = vat;
	}
	/**
	 * @return the cummulativeCo2
	 */
	public OCTET getCummulativeCo2() {
		return cummulativeCo2;
	}
	/**
	 * @param cummulativeCo2 the cummulativeCo2 to set
	 */
	public void setCummulativeCo2(OCTET cummulativeCo2) {
		this.cummulativeCo2 = cummulativeCo2;
	}
	/**
	 * @return the cummulativeCo2Reduction
	 */
	public OCTET getCummulativeCo2Reduction() {
		return cummulativeCo2Reduction;
	}
	/**
	 * @param cummulativeCo2Reduction the cummulativeCo2Reduction to set
	 */
	public void setCummulativeCo2Reduction(OCTET cummulativeCo2Reduction) {
		this.cummulativeCo2Reduction = cummulativeCo2Reduction;
	}
	/**
	 * @return the co2
	 */
	public OCTET getCo2() {
		return co2;
	}
	/**
	 * @param co2 the co2 to set
	 */
	public void setCo2(OCTET co2) {
		this.co2 = co2;
	}
	/**
	 * @return the co2Reduction
	 */
	public OCTET getCo2Reduction() {
		return co2Reduction;
	}
	/**
	 * @param co2Reduction the co2Reduction to set
	 */
	public void setCo2Reduction(OCTET co2Reduction) {
		this.co2Reduction = co2Reduction;
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
		return "billingEntry [addtionalCharge=" + addtionalCharge
				+ ", basicCharge=" + basicCharge + ", billingDate="
				+ billingDate + ", co2=" + co2 + ", co2Reduction="
				+ co2Reduction + ", cummulativeCo2=" + cummulativeCo2
				+ ", cummulativeCo2Reduction=" + cummulativeCo2Reduction
				+ ", deadLine=" + deadLine + ", discountCharge="
				+ discountCharge + ", groupName=" + groupName
				+ ", meteringDate=" + meteringDate + ", mileageUnits="
				+ mileageUnits + ", realCharge=" + realCharge
				+ ", remainMiles=" + remainMiles + ", saveMiles=" + saveMiles
				+ ", totalCharge=" + totalCharge + ", totalUsage=" + totalUsage
				+ ", usageCharge=" + usageCharge + ", useMiles=" + useMiles
				+ ", vat=" + vat + "]";
	}
	
}
