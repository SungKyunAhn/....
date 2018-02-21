package com.aimir.cms.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.MeterEnt;

/**
 * <p>
 * Java class for MeterImportReq complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="MeterImportReq">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AuthCred" type="{http://server.ws.cms.aimir.com/}authCred" minOccurs="0"/>
 *         &lt;element name="MeterEnt" type="{http://server.ws.cms.aimir.com/}meterEnt" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeterImportReq", propOrder = { "authCred","meterEnt"})
public class MeterImportReq {

	@XmlElement(name = "AuthCred")
	protected AuthCred authCred;

	@XmlElement(name = "MeterEnt")
	protected MeterEnt meterEnt;	

	/**
	 * Gets the value of the authCred property.
	 * 
	 * @return possible object is {@link AuthCred }
	 * 
	 */
	public AuthCred getAuthCred() {
		return authCred;
	}

	/**
	 * Sets the value of the authCred property.
	 * 
	 * @param value
	 *            allowed object is {@link AuthCred }
	 * 
	 */
	public void setAuthCred(AuthCred authCred) {
		this.authCred = authCred;
	}

	/**
	 * Gets the value of the meterEnt property.
	 * 
	 * @return possible object is {@link MeterEnt }
	 * 
	 */
	public MeterEnt getMeterEnt() {
		return meterEnt;
	}

	/**
	 * Sets the value of the meterEnt property.
	 * 
	 * @param value
	 *            allowed object is {@link MeterEnt }
	 * 
	 */
	public void setMeterEnt(MeterEnt meterEnt) {
		this.meterEnt = meterEnt;
	}
}
