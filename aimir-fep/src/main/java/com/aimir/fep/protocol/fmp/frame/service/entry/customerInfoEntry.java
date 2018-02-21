package com.aimir.fep.protocol.fmp.frame.service.entry;

import com.aimir.fep.protocol.fmp.datatype.*;
import com.aimir.fep.protocol.fmp.frame.service.Entry;


public class customerInfoEntry extends Entry {
	private OCTET customerAccount = new OCTET();
	private BYTE serviceType = new BYTE();
	private UINT contractCapacity = new UINT();
	private CHAR capacityExponent = new CHAR();
	private OCTET contractCapacityUnit = new OCTET();
	private INT tariffType = new INT();
	private OCTET tariffName = new OCTET();
	/**
	 * @return the customerAccount
	 */
	public OCTET getCustomerAccount() {
		return customerAccount;
	}
	/**
	 * @param customerAccount the customerAccount to set
	 */
	public void setCustomerAccount(OCTET customerAccount) {
		this.customerAccount = customerAccount;
	}
	/**
	 * @return the serviceType
	 */
	public BYTE getServiceType() {
		return serviceType;
	}
	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(BYTE serviceType) {
		this.serviceType = serviceType;
	}
	/**
	 * @return the contractCapacity
	 */
	public UINT getContractCapacity() {
		return contractCapacity;
	}
	/**
	 * @param contractCapacity the contractCapacity to set
	 */
	public void setContractCapacity(UINT contractCapacity) {
		this.contractCapacity = contractCapacity;
	}
	/**
	 * @return the capacityExponent
	 */
	public CHAR getCapacityExponent() {
		return capacityExponent;
	}
	/**
	 * @param capacityExponent the capacityExponent to set
	 */
	public void setCapacityExponent(CHAR capacityExponent) {
		this.capacityExponent = capacityExponent;
	}
	/**
	 * @return the contractCapacityUnit
	 */
	public OCTET getContractCapacityUnit() {
		return contractCapacityUnit;
	}
	/**
	 * @param contractCapacityUnit the contractCapacityUnit to set
	 */
	public void setContractCapacityUnit(OCTET contractCapacityUnit) {
		this.contractCapacityUnit = contractCapacityUnit;
	}
	/**
	 * @return the tariffType
	 */
	public INT getTariffType() {
		return tariffType;
	}
	/**
	 * @param tariffType the tariffType to set
	 */
	public void setTariffType(INT tariffType) {
		this.tariffType = tariffType;
	}
	/**
	 * @return the tariffName
	 */
	public OCTET getTariffName() {
		return tariffName;
	}
	/**
	 * @param tariffName the tariffName to set
	 */
	public void setTariffName(OCTET tariffName) {
		this.tariffName = tariffName;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "customerInfoEntry [capacityExponent=" + capacityExponent
				+ ", contractCapacity=" + contractCapacity
				+ ", contractCapacityUnit=" + contractCapacityUnit
				+ ", customerAccount=" + customerAccount + ", serviceType="
				+ serviceType + ", tariffName=" + tariffName + ", tariffType="
				+ tariffType + "]";
	}
	
}
