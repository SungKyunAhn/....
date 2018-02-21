package com.aimir.fep.command.ws.data;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import com.aimir.fep.command.ws.datatype.OrderStatus;

/**
 * Search for reading orders
 * 
 * <p>
 * Java class for SearchReadingOrdersRequest complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 */
@XmlType(name = "SearchReadingOrdersRequest", namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading", propOrder = {
		"referenceId", "meterSerialNumber", "readingOrderFromDate",
		"readingOrderToDate", "orderStatus" })
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchReadingOrdersRequest {
	@XmlElement(nillable = false, required = false, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	@XmlSchemaType(name = "positiveInteger")
	private BigInteger referenceId;

	@XmlElement(nillable = false, required = false, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	private String meterSerialNumber;

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	@XmlSchemaType(name = "dateTime")
	private XMLGregorianCalendar readingOrderFromDate;

	@XmlElement(nillable = false, required = false, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	@XmlSchemaType(name = "dateTime")
	private XMLGregorianCalendar readingOrderToDate;

	@XmlElement(nillable = false, required = false, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	private OrderStatus orderStatus;

	public SearchReadingOrdersRequest() {
	}

	public SearchReadingOrdersRequest(BigInteger referenceId,
			String meterSerialNumber,
			XMLGregorianCalendar readingOrderFromDate,
			XMLGregorianCalendar readingOrderToDate, OrderStatus orderStatus) {
		this.referenceId = referenceId;
		this.meterSerialNumber = meterSerialNumber;
		this.readingOrderFromDate = readingOrderFromDate;
		this.readingOrderToDate = readingOrderToDate;
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
	public void setMeterSerialNumber(String value) {
		this.meterSerialNumber = value;
	}

	/**
	 * Gets the value of the readingOrderFromDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getReadingOrderFromDate() {
		return readingOrderFromDate;
	}

	/**
	 * Sets the value of the readingOrderFromDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setReadingOrderFromDate(
			XMLGregorianCalendar readingOrderFromDate) {
		this.readingOrderFromDate = readingOrderFromDate;
	}

	/**
	 * Gets the value of the readingOrderToDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getReadingOrderToDate() {
		return readingOrderToDate;
	}

	/**
	 * Sets the value of the readingOrderToDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setReadingOrderToDate(XMLGregorianCalendar value) {
		this.readingOrderToDate = value;
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
	 *            allowed object is {@link OrderStatus }
	 * 
	 */
	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	@Override
	public String toString() {
		return "SearchReadingOrdersRequest [referenceId=" + referenceId
				+ ", meterSerialNumber=" + meterSerialNumber
				+ ", readingOrderFromDate=" + readingOrderFromDate
				+ ", readingOrderToDate=" + readingOrderToDate
				+ ", orderStatus=" + orderStatus + "]";
	}
}