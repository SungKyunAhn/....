package com.aimir.fep.command.ws.data;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import com.aimir.fep.command.ws.datatype.PowerOperation;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddPowerOnOffOrderRequest", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff", propOrder = {
		"referenceId", "meterSerialNumber", "powerOperation",
		"powerOperationDate", "userReference", "userCreateDate" })
public class AddPowerOnOffOrderRequest {

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	@XmlSchemaType(name = "positiveInteger")
	protected BigInteger referenceId;
	@XmlElement(required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	protected String meterSerialNumber;
	@XmlElement(required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	protected PowerOperation powerOperation;
	@XmlElement(required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar powerOperationDate;
	@XmlElement(required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	protected String userReference;
	@XmlElement(required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar userCreateDate;

	public AddPowerOnOffOrderRequest() {
	}

	public AddPowerOnOffOrderRequest(BigInteger referenceId,
			String meterSerialNumber, PowerOperation powerOperation,
			XMLGregorianCalendar powerOperationDate, String userReference,
			XMLGregorianCalendar userCreateDate) {
		this.referenceId = referenceId;
		this.meterSerialNumber = meterSerialNumber;
		this.powerOperation = powerOperation;
		this.powerOperationDate = powerOperationDate;
		this.userReference = userReference;
		this.userCreateDate = userCreateDate;
	}

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
	 * 
	 */
	public void setMeterSerialNumber(String value) {
		this.meterSerialNumber = value;
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
}
