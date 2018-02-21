package com.aimir.fep.command.ws.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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
 * <p>
 * Exception을 Bean에 담아 사용하는 경우 JAXB(java.lang.StackTraceElement does not have a
 * no-arg default constructor.) Error을 확인 할 수 있다. XmlJavaTypeAdapter(XmlAdapter)을 만들어
 * 이용한다.
 *
 */
@WebFault(name = "ApplicationFault", targetNamespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApplicationFault", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes", propOrder = {
		"faultCode", "summary", "details", "temporary" })
@XmlJavaTypeAdapter(value = ApplicationFaultAdapter.class)
public class ApplicationFaultException extends java.lang.Exception {

	@XmlElement(name = "faultCode", nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
	private java.lang.String faultCode;

	@XmlElement(name = "summary", nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
	private java.lang.String summary;

	@XmlElement(name = "details", nillable = false, required = false, namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
	private java.lang.String details;

	@XmlElement(name = "temporary", nillable = true, required = true, defaultValue = "true", namespace = "http://ws.command.fep.aimir.com/xsd/CommonTypes")
	private java.lang.Boolean temporary;

	public ApplicationFaultException() {
	}

	public ApplicationFaultException(java.lang.String faultCode,
			java.lang.String summary, java.lang.String details,
			Boolean temporary) {
		super("ApplicationFault [faultCode=" + faultCode + ", summary="
				+ summary + ", details=" + details + ", temporary=" + temporary
				+ "]");
		this.faultCode = faultCode;
		this.summary = summary;
		this.details = details;
		this.temporary = temporary;
	}

	public ApplicationFaultException(FaultCode fc) {
		super("ApplicationFault [faultCode=" + fc.getCode() + ", summary="
				+ fc.getSummary() + ", details=" + fc.getDetails()
				+ ", temporary=" + fc.isTemporary() + "]");
		this.faultCode = String.valueOf(fc.getCode());
		this.summary = fc.getSummary();
		this.details = fc.getDetails();
		this.temporary = fc.isTemporary();
	}

	public ApplicationFaultException(FaultCode fc, String details) {
		super("ApplicationFault [faultCode=" + fc.getCode() + ", summary="
				+ fc.getSummary() + ", details=" + details + ", temporary="
				+ fc.isTemporary() + "]");
		this.faultCode = String.valueOf(fc.getCode());
		this.summary = fc.getSummary();
		this.details = details;
		this.temporary = fc.isTemporary();
	}

	public ApplicationFaultException(java.lang.String faultCode,
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
	@XmlTransient
	public String getMessage() {
		return super.getMessage();
	}

	@Override
	public String toString() {
		return "ApplicationFault [faultCode=" + faultCode + ", summary="
				+ summary + ", details=" + details + ", temporary=" + temporary
				+ "]";
	}
}