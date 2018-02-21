package com.aimir.fep.command.ws.data;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import com.aimir.fep.command.ws.datatype.MeterValueType;
import com.aimir.fep.command.ws.datatype.OrderStatus;

/**
 * Reading order type
 * 
 * <p>
 * Java class for ReadingOrder complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 */
@XmlType(name = "ReadingOrder", namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading", propOrder = {
		"referenceId", "meterSerialNumber", "meterValueDate", "orderStatus",
		"applicationFault" })
@XmlAccessorType(XmlAccessType.FIELD)
public class ReadingOrder {

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	@XmlSchemaType(name = "positiveInteger")
	private BigInteger referenceId = null;

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	private String meterSerialNumber = null;

	@XmlElement(nillable = true, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	@XmlSchemaType(name = "dateTime")
	private XMLGregorianCalendar meterValueDate;

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	private OrderStatus orderStatus;

	@XmlElement(nillable = true, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	private ApplicationFault applicationFault;

	public ReadingOrder() {
	}

	public ReadingOrder(BigInteger referenceId, String meterSerialNumber,
			XMLGregorianCalendar meterValueDate, MeterValueType meterValueType,
			OrderStatus orderStatus, ApplicationFault applicationFault) {
		this.referenceId = referenceId;
		this.meterSerialNumber = meterSerialNumber;
		this.meterValueDate = meterValueDate;
		this.orderStatus = orderStatus;
		this.applicationFault = applicationFault;
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
	public void setReferenceId(BigInteger referenceId) {
		this.referenceId = referenceId;
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
	public void setMeterSerialNumber(String meterSerialNumber) {
		this.meterSerialNumber = meterSerialNumber;
	}

	/**
	 * Gets the value of the meterValueDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getMeterValueDate() {
		return meterValueDate;
	}

	/**
	 * Sets the value of the meterValueDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setMeterValueDate(XMLGregorianCalendar meterValueDate) {
		this.meterValueDate = meterValueDate;
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
	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
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
	public void setApplicationFault(ApplicationFault applicationFault) {
		this.applicationFault = applicationFault;
	}

	@Override
	public String toString() {
		return "ReadingOrder [referenceId=" + referenceId + ", meterValueDate="
				+ meterValueDate + ", orderStatus=" + orderStatus
				+ ", applicationFault=" + applicationFault + "]";
	}
}