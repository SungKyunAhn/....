package com.aimir.fep.meter.prepaymentForSA.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType (propOrder={
		"commonResponseOfWS",
		"customerNumber",
		"customerName",
		"contractNumber"
		}
)
public class CustomerInformation {
	@XmlElement(required=true)
	CommonResponseOfWS commonResponseOfWS;
	
	@XmlElement(required=true)
	String customerNumber;

	@XmlElement(required=true)
	String customerName;

	@XmlElement(required=true)
	String contractNumber;

	public CommonResponseOfWS getCommonResponseOfWS() {
		return commonResponseOfWS;
	}

	public void setCommonResponseOfWS(CommonResponseOfWS commonResponseOfWS) {
		this.commonResponseOfWS = commonResponseOfWS;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

}
