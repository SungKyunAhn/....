package com.aimir.fep.meter.prepaymentForSA.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType (propOrder={
		"commonResponseOfWS",
		"account",
		"vendorName",
		"availableBalance",
		"cashierName"
		}
)
public class VendorInformation {
	@XmlElement(required=true)
	CommonResponseOfWS commonResponseOfWS;
	
	@XmlElement(required=true)
	String account;

	@XmlElement(required=true)
	String vendorName;

	@XmlElement(required=true)
	Double availableBalance;
	
	@XmlElement(required=true)
	String cashierName;

	public CommonResponseOfWS getCommonResponseOfWS() {
		return commonResponseOfWS;
	}

	public void setCommonResponseOfWS(CommonResponseOfWS commonResponseOfWS) {
		this.commonResponseOfWS = commonResponseOfWS;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public Double getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(Double availableBalance) {
		this.availableBalance = availableBalance;
	}

	public String getCashierName() {
		return cashierName;
	}

	public void setCashierName(String cashierName) {
		this.cashierName = cashierName;
	}

}
