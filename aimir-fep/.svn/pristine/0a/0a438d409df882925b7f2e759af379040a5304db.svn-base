package com.aimir.fep.command.ws.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebFault;

import com.aimir.fep.command.ws.datatype.FaultCode;

/**
 * <p>
 * Java class for ApplicationFault complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="ApplicationFault">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="faultCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="summary" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="details" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="temporary" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
@WebFault(name = "ApplicationFault", targetNamespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApplicationFault", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes", propOrder = {
		"faultCode", "summary", "details", "temporary" })
public class ApplicationFault {

	@XmlElement(name = "faultCode", nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
	private java.lang.String faultCode;

	@XmlElement(name = "summary", nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
	private java.lang.String summary;

	@XmlElement(name = "details", nillable = false, required = false, namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
	private java.lang.String details;

	@XmlElement(name = "temporary", nillable = true, required = true, defaultValue = "true", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
	private java.lang.Boolean temporary;

	public ApplicationFault() {
	}

	public ApplicationFault(java.lang.String faultCode,
			java.lang.String summary, java.lang.String details,
			Boolean temporary) {
		this.faultCode = faultCode;
		this.summary = summary;
		this.details = details;
		this.temporary = temporary;
	}

	public ApplicationFault(FaultCode fc) {
		this.faultCode = String.valueOf(fc.getCode());
		this.summary = fc.getSummary();
		this.details = fc.getDetails();
		this.temporary = fc.isTemporary();
	}

	public ApplicationFault(FaultCode fc, String details) {
		this.faultCode = String.valueOf(fc.getCode());
		this.summary = fc.getSummary();
		this.details = details;
		this.temporary = fc.isTemporary();
	}

	public ApplicationFault(java.lang.String faultCode,
			java.lang.String summary, java.lang.String details) {
		this(faultCode, summary, details, true);
	}

	/**
	 * Gets the value of the faultCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public java.lang.String getFaultCode() {
		return faultCode;
	}

	/**
	 * Sets the value of the faultCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setFaultCode(java.lang.String faultCode) {
		this.faultCode = faultCode;
	}

	/**
	 * Gets the value of the summary property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public java.lang.String getSummary() {
		return summary;
	}

	/**
	 * Sets the value of the summary property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSummary(java.lang.String summary) {
		this.summary = summary;
	}

	/**
	 * Gets the value of the details property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public java.lang.String getDetails() {
		return details;
	}

	/**
	 * Sets the value of the details property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDetails(java.lang.String details) {
		this.details = details;
	}

	/**
	 * Gets the value of the temporary property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public Boolean isTemporary() {
		return temporary;
	}

	/**
	 * Sets the value of the temporary property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setTemporary(Boolean temporary) {
		this.temporary = temporary;
	}

	@Override
	public String toString() {
		return "ApplicationFault [faultCode=" + faultCode + ", summary="
				+ summary + ", details=" + details + ", temporary=" + temporary
				+ "]";
	}
}