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
 * Response to an AddPowerOnOffOrderRequest
 * 
 * <p>
 * Java class for AddPowerOnOffOrderResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="AddPowerOnOffOrderResponse">
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
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddPowerOnOffOrderResponse", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
public class AddPowerOnOffOrderResponse {

	@XmlElement(required = true, namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar acceptedDate;

	public AddPowerOnOffOrderResponse() {
		GregorianCalendar gcal = new GregorianCalendar();
		try {
			this.acceptedDate = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gcal);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
	}

	public AddPowerOnOffOrderResponse(XMLGregorianCalendar acceptedDate) {
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

	@Override
	public String toString() {
		return "AddPowerOnOffOrderResponse [acceptedDate=" + acceptedDate + "]";
	}

}