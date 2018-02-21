package com.aimir.fep.meter.prepaymentForSA.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
  
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addBalanceForSAWS", propOrder = {
    "supplierName",
    "dateTime",
    "contractNumber",
    "mdsId",
    "accountId",
    "amount",
    "source",
    "encryptionKey",
    "authCode",
    "municipalityCode",
    "transactionId"
})  
public class AddBalanceForSAWSClient {
	
	@XmlElement(name="supplierName")
	protected String supplierName;
	 
	@XmlElement(name="dateTime")
	protected String dateNumber;
	
	@XmlElement(name="contractNumber")
	protected String contractNumber;
	
	@XmlElement(name="mdsId")
	protected String mdsId;
	
	@XmlElement(name="accountId")
	protected String accountId;	
	
	@XmlElement(name="amount")
	protected String amount;
	
	@XmlElement(name="powerLimit")
	protected String powserLimit;
	
	@XmlElement(name="source")
	protected String source;
	
	@XmlElement(name="encryptionKey")
	protected String encryptionKey;	
	
	@XmlElement(name="authCode")
	protected String authCode;
	
	@XmlElement(name="municipalityCode")
	protected String municipalityCode;	
	
	@XmlElement(name="transactionId")
	protected String transactionId;

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getDateNumber() {
		return dateNumber;
	}

	public void setDateNumber(String dateNumber) {
		this.dateNumber = dateNumber;
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

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPowserLimit() {
		return powserLimit;
	}

	public void setPowserLimit(String powserLimit) {
		this.powserLimit = powserLimit;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getMunicipalityCode() {
		return municipalityCode;
	}

	public void setMunicipalityCode(String municipalityCode) {
		this.municipalityCode = municipalityCode;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}		
}
