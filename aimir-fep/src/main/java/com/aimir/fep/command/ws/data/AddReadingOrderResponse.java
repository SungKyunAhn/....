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
 * Response to an AddReadingOrderRequest
 * 
 * <p>
 * Java class for AddReadingOrderResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="AddReadingOrderResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="acceptedDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Administrator
 * 
 */
@XmlType(name = "AddReadingOrderResponse", namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading", propOrder = { "acceptedDate" })
@XmlAccessorType(XmlAccessType.FIELD)
public class AddReadingOrderResponse {

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	@XmlSchemaType(name = "dateTime")
	private XMLGregorianCalendar acceptedDate;

	public AddReadingOrderResponse() {
		GregorianCalendar gcal = new GregorianCalendar();
		try {
			this.acceptedDate = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gcal);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
	}

	public AddReadingOrderResponse(XMLGregorianCalendar acceptedDate) {
		this.acceptedDate = acceptedDate;
	}

	/**
	 * Gets the value of the acceptedDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getAcceptedDate() {
		return acceptedDate;
	}

	/**
	 * Sets the value of the acceptedDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setAcceptedDate(XMLGregorianCalendar value) {
		this.acceptedDate = value;
	}
}