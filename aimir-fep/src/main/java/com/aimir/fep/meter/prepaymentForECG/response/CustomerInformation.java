package com.aimir.fep.meter.prepaymentForECG.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType (propOrder={
		"commonResponseOfWS",
		"customerName",
		"mdsId"
		}
)
public class CustomerInformation {
	@XmlElement(required=true)
	CommonResponseOfWS commonResponseOfWS;

	@XmlElement(required=true)
	String customerName;

	@XmlElement(required=true)
	String mdsId;

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

	public String getMdsId() {
		return mdsId;
	}

	public void setMdsId(String mdsId) {
		this.mdsId = mdsId;
	}

}
