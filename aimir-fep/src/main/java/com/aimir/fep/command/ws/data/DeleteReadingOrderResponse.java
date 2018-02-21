package com.aimir.fep.command.ws.data;

import java.util.GregorianCalendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Response to a DeleteReadingOrderRequest
 * 
 * <p>
 * Java class for DeleteReadingOrderResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 */
@XmlType(name = "DeleteReadingOrderResponse", namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading", propOrder = { "deletedDate" })
@XmlAccessorType(XmlAccessType.FIELD)
public class DeleteReadingOrderResponse {

	@XmlElement(nillable = false, required = true)
	@XmlSchemaType(name = "dateTime")
	private XMLGregorianCalendar deletedDate;

	public DeleteReadingOrderResponse() {
		GregorianCalendar gcal = new GregorianCalendar();
		try {
			this.deletedDate = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gcal);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
	}

	public DeleteReadingOrderResponse(XMLGregorianCalendar deletedDate) {
		this.deletedDate = deletedDate;
	}

	/**
	 * Gets the value of the deletedDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getDeletedDate() {
		return deletedDate;
	}

	/**
	 * Sets the value of the deletedDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDeletedDate(XMLGregorianCalendar deletedDate) {
		this.deletedDate = deletedDate;
	}

	@Override
	public String toString() {
		return "DeleteReadingOrderResponse [deletedDate=" + deletedDate + "]";
	}
}