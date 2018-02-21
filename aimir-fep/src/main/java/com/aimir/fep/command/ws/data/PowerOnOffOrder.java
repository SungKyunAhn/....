package com.aimir.fep.command.ws.data;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import com.aimir.fep.command.ws.datatype.OrderStatus;
import com.aimir.fep.command.ws.datatype.PowerOperation;

/**
 * Power On/Off order type
 * 
 * <p>
 * Java class for PowerOnOffOrder complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PowerOnOffOrder", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff", propOrder = {
		"referenceId", "meterSerialNumber", "powerOperation",
		"powerOperationDate", "userReference", "userCreateDate", "orderStatus",
		"applicationFault" })
public class PowerOnOffOrder {

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	@XmlSchemaType(name = "positiveInteger")
	protected BigInteger referenceId;

	@XmlElement(required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	protected String meterSerialNumber;

	@XmlElement(namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	protected PowerOperation powerOperation;

	@XmlElement(required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar powerOperationDate;

	@XmlElement(required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	protected String userReference;

	@XmlElement(required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar userCreateDate;

	@XmlElement(namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	protected OrderStatus orderStatus;

	@XmlElement(required = true, nillable = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	protected ApplicationFault applicationFault;

	/**
	 * Gets the value of the referenceId property.
	 * 
	 * @return possible object is {@link BigInteger }
	 */
	public BigInteger getReferenceId() {
		return referenceId;
	}

	/**
	 * Sets the value of the referenceId property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 */
	public void setReferenceId(BigInteger value) {
		this.referenceId = value;
	}

	/**
	 * Gets the value of the meterSerialNumber property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMeterSerialNumber() {
		return meterSerialNumber;
	}

	/**
	 * Sets the value of the meterSerialNumber property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setMeterSerialNumber(String meterSerialNumber) {
		this.meterSerialNumber = meterSerialNumber;
	}

	/**
	 * Gets the value of the powerOperation property.
	 * 
	 * @return possible object is {@link PowerOperation }
	 */
	public PowerOperation getPowerOperation() {
		return powerOperation;
	}

	/**
	 * Sets the value of the powerOperation property.
	 * 
	 * @param value
	 *            allowed object is {@link PowerOperation }
	 */
	public void setPowerOperation(PowerOperation value) {
		this.powerOperation = value;
	}

	/**
	 * Gets the value of the powerOperationDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getPowerOperationDate() {
		return powerOperationDate;
	}

	/**
	 * Sets the value of the powerOperationDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setPowerOperationDate(XMLGregorianCalendar value) {
		this.powerOperationDate = value;
	}

	/**
	 * Gets the value of the userReference property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUserReference() {
		return userReference;
	}

	/**
	 * Sets the value of the userReference property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUserReference(String value) {
		this.userReference = value;
	}

	/**
	 * Gets the value of the userCreateDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getUserCreateDate() {
		return userCreateDate;
	}

	/**
	 * Sets the value of the userCreateDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setUserCreateDate(XMLGregorianCalendar value) {
		this.userCreateDate = value;
	}

	/**
	 * Gets the value of the orderStatus property.
	 * 
	 * @return possible object is {@link OrderStatus }
	 */
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	/**
	 * Sets the value of the orderStatus property.
	 * 
	 * @param value
	 *            allowed object is {@link OrderStatus }
	 */
	public void setOrderStatus(OrderStatus value) {
		this.orderStatus = value;
	}

	/**
	 * Gets the value of the applicationFault property.
	 * 
	 * @return possible object is {@link ApplicationFault }
	 * 
	 */
	public ApplicationFault getApplicationFault() {
		return applicationFault;
	}

	/**
	 * Sets the value of the applicationFault property.
	 * 
	 * @param value
	 *            allowed object is {@link ApplicationFault }
	 * 
	 */
	public void setApplicationFault(ApplicationFault value) {
		this.applicationFault = value;
	}

	@Override
	public String toString() {
		return "PowerOnOffOrder [referenceId=" + referenceId
				+ ", meterSerialNumber=" + meterSerialNumber
				+ ", powerOperation=" + powerOperation
				+ ", powerOperationDate=" + powerOperationDate
				+ ", userReference=" + userReference + ", userCreateDate="
				+ userCreateDate + ", orderStatus=" + orderStatus
				+ ", applicationFault=" + applicationFault + "]";
	}
}
