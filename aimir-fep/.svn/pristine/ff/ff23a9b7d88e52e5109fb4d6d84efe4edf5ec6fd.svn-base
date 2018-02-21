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
 * Search for power on/off orders
 * 
 * <p>
 * Java class for SearchPowerOnOffOrdersRequest complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchPowerOnOffOrdersRequest", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff", propOrder = {
		"referenceId", "meterSerialNumber", "powerOperation",
		"powerOperationFromDate", "powerOperationToDate", "userReference",
		"userCreateFromDate", "userCreateToDate", "orderStatus" })
public class SearchPowerOnOffOrdersRequest {

	@XmlElement(name = "referenceId", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	@XmlSchemaType(name = "positiveInteger")
	protected BigInteger referenceId;

	@XmlElement(name = "meterSerialNumber", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	protected String meterSerialNumber;

	@XmlElement(name = "powerOperation", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	protected PowerOperation powerOperation;

	@XmlElement(name = "powerOperationFromDate", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar powerOperationFromDate;

	@XmlElement(name = "powerOperationToDate", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar powerOperationToDate;

	@XmlElement(name = "userReference", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	protected String userReference;

	@XmlElement(required = true, name = "userCreateFromDate", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar userCreateFromDate;

	@XmlElement(name = "userCreateToDate", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar userCreateToDate;

	@XmlElement(name = "orderStatus", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	protected OrderStatus orderStatus;

	public SearchPowerOnOffOrdersRequest() {
	}

	public SearchPowerOnOffOrdersRequest(BigInteger referenceId,
			String meterSerialNumber,
			XMLGregorianCalendar powerOperationFromDate,
			XMLGregorianCalendar powerOperationToDate, String userReference,
			XMLGregorianCalendar userCreateFromDate,
			XMLGregorianCalendar userCreateToDate,OrderStatus orderStatus) {
		this.referenceId = referenceId;
		this.meterSerialNumber = meterSerialNumber;
		this.powerOperationFromDate = powerOperationFromDate;
		this.powerOperationToDate = powerOperationToDate;
		this.userReference = userReference;
		this.userCreateFromDate = userCreateFromDate;
		this.userCreateToDate = userCreateToDate;
		this.orderStatus = orderStatus;
	}

	/**
	 * Gets the value of the referenceId property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getReferenceId() {
		return referenceId;
	}

	/**
	 * Sets the value of the referenceId property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
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
	 * 
	 */
	public void setMeterSerialNumber(String value) {
		this.meterSerialNumber = value;
	}

	/**
	 * Gets the value of the powerOperation property.
	 * 
	 * @return possible object is {@link PowerOperation }
	 * 
	 */
	public PowerOperation getPowerOperation() {
		return powerOperation;
	}

	/**
	 * Sets the value of the powerOperation property.
	 * 
	 * @param value
	 *            allowed object is {@link PowerOperation }
	 * 
	 */
	public void setPowerOperation(PowerOperation value) {
		this.powerOperation = value;
	}

	/**
	 * Gets the value of the powerOperationFromDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getPowerOperationFromDate() {
		return powerOperationFromDate;
	}

	/**
	 * Sets the value of the powerOperationFromDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setPowerOperationFromDate(XMLGregorianCalendar value) {
		this.powerOperationFromDate = value;
	}

	/**
	 * Gets the value of the powerOperationToDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getPowerOperationToDate() {
		return powerOperationToDate;
	}

	/**
	 * Sets the value of the powerOperationToDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setPowerOperationToDate(XMLGregorianCalendar value) {
		this.powerOperationToDate = value;
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
	 * Gets the value of the userCreateFromDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getUserCreateFromDate() {
		return userCreateFromDate;
	}

	/**
	 * Sets the value of the userCreateFromDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setUserCreateFromDate(XMLGregorianCalendar value) {
		this.userCreateFromDate = value;
	}

	/**
	 * Gets the value of the userCreateToDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getUserCreateToDate() {
		return userCreateToDate;
	}

	/**
	 * Sets the value of the userCreateToDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setUserCreateToDate(XMLGregorianCalendar value) {
		this.userCreateToDate = value;
	}

	/**
	 * Gets the value of the orderStatus property.
	 * 
	 * @return possible object is {@link OrderStatus }
	 * 
	 */
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	/**
	 * Sets the value of the orderStatus property.
	 * 
	 * @param value
	 *            allowed object is {@link Short }
	 * 
	 */
	public void setOrderStatus(OrderStatus value) {
		this.orderStatus = value;
	}

}
