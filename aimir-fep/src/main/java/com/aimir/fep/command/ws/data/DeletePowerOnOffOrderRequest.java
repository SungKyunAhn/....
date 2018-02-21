package com.aimir.fep.command.ws.data;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * Request order type
 * 
 * <p>
 * Java class for DeletePowerOnOffOrderRequest complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeletePowerOnOffOrderRequest", namespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff", propOrder = { "referenceId" })
public class DeletePowerOnOffOrderRequest {
	@XmlElement(nillable = false, required = true)
	@XmlSchemaType(name = "positiveInteger")
	private BigInteger referenceId;

	public DeletePowerOnOffOrderRequest() {
	}

	public DeletePowerOnOffOrderRequest(BigInteger referenceId) {
		this.referenceId = referenceId;
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

}