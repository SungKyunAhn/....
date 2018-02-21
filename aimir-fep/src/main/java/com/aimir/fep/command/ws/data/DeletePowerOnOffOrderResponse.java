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
 * Response to a DeletePowerOnOffOrderRequest
 * 
 * <p>
 * Java class for DeletePowerOnOffOrderResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="DeletePowerOnOffOrderResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="deletedDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeletePowerOnOffOrderResponse", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
public class DeletePowerOnOffOrderResponse {

	@XmlElement(required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar deletedDate;

	public DeletePowerOnOffOrderResponse() {
		GregorianCalendar gcal = new GregorianCalendar();
		try {
			this.deletedDate = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gcal);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
	}

	public DeletePowerOnOffOrderResponse(XMLGregorianCalendar deletedDate) {
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
	public void setDeletedDate(XMLGregorianCalendar value) {
		this.deletedDate = value;
	}

}