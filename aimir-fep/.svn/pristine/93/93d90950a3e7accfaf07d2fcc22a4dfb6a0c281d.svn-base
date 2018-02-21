package com.aimir.fep.meter.prepaymentForSA.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType (propOrder={
		"rtnStatus",
		"transactionId",
		"errorCode",
		"errorDescription"
		}
)
public class CommonResponseOfWS {

	@XmlElement(required=true)
	boolean rtnStatus;

	@XmlElement(required=false)
	String transactionId;
	
	@XmlElement(required=false)
	String errorCode;
	
	@XmlElement(required=false)
	String errorDescription;

	public boolean isRtnStatus() {
		return rtnStatus;
	}

	public void setRtnStatus(boolean rtnStatus) {
		this.rtnStatus = rtnStatus;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
}
