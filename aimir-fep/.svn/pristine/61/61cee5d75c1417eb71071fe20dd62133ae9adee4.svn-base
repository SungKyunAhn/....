package com.aimir.fep.meter.prepaymentForSA.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType (propOrder={
		"commonResponseOfWS",
		"customerName",
		"customerBalance",
		"transactionNumber",
		"transactionTime",
		"availableBalance"
		}
)
public class VendorPaymentInformation {
	@XmlElement(required=true)
	CommonResponseOfWS commonResponseOfWS;
	
	@XmlElement(required=true)
	String customerName;

	@XmlElement(required=true)
	Double customerBalance;

	@XmlElement(required=true)
	Long transactionNumber;
	
	@XmlElement(required=true)
	String transactionTime;
	
	@XmlElement(required=true)
	Double availableBalance;

	public CommonResponseOfWS getCommonResponseOfWS() {
		return commonResponseOfWS;
	}

	public void setCommonResponseOfWS(CommonResponseOfWS commonResponseOfWS) {
		this.commonResponseOfWS = commonResponseOfWS;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Double getCustomerBalance() {
		return customerBalance;
	}

	public void setCustomerBalance(Double customerBalance) {
		this.customerBalance = customerBalance;
	}

	public Long getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(Long transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public String getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

	public Double getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(Double availableBalance) {
		this.availableBalance = availableBalance;
	}


}
