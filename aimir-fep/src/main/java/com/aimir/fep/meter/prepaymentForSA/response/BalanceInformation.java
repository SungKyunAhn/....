package com.aimir.fep.meter.prepaymentForSA.response;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType (propOrder={
		"commonResponseOfWS",
		"supplierName",
		"contractNumber",
		"mdsId",
		"paymentMode",
		"currentCredit",
		"switchStatus"
		}
)
public class BalanceInformation {
	@XmlElement(required=true)
	CommonResponseOfWS commonResponseOfWS;

	@XmlElement(required=false)
	String supplierName;

	@XmlElement(required=false)
	String contractNumber;

	@XmlElement(required=false)
	String mdsId;

	@XmlElement(required=false)
	String paymentMode;	

	@XmlElement(required=false)
	Double currentCredit;

	@XmlElement(required=false)
	String switchStatus;

	public CommonResponseOfWS getCommonResponseOfWS() {
		return commonResponseOfWS;
	}

	public void setCommonResponseOfWS(CommonResponseOfWS commonResponseOfWS) {
		this.commonResponseOfWS = commonResponseOfWS;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getMdsId() {
		return mdsId;
	}

	public void setMdsId(String mdsId) {
		this.mdsId = mdsId;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public Double getCurrentCredit() {
		return currentCredit;
	}

	public void setCurrentCredit(Double currentCredit) {
		this.currentCredit = currentCredit;
	}

	public String getSwitchStatus() {
		return switchStatus;
	}

	public void setSwitchStatus(String switchStatus) {
		this.switchStatus = switchStatus;
	}
}
