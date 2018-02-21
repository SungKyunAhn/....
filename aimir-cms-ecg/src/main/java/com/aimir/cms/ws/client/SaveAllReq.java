package com.aimir.cms.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.cms.model.AuthCred;
import com.aimir.cms.model.CMSEnt;

/**
 * <p>
 * Java class for SaveAllReq complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="SaveAllReq">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AuthCred" type="{http://server.ws.cms.aimir.com/}authCred" minOccurs="0"/>
 *         &lt;element name="CMSEnt" type="{http://server.ws.cms.aimir.com/}cmsEnt" minOccurs="0"/>
 *         &lt;element name="SaveAllType" type="{http://server.ws.cms.aimir.com/}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SaveAllReq", propOrder = { "authCred","cmsEnt","saveAllType"})
public class SaveAllReq {

	@XmlElement(name = "AuthCred")
	protected AuthCred authCred;

	@XmlElement(name = "CMSEnt")
	protected CMSEnt cmsEnt;
	
	@XmlElement(name = "SaveAllType")
	protected String saveAllType;

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
	 * Gets the value of the cmsEnt property.
	 * 
	 * @return possible object is {@link CMSEnt }
	 * 
	 */
	public CMSEnt getCmsEnt() {
		return cmsEnt;
	}

	/**
	 * Sets the value of the cmsEnt property.
	 * 
	 * @param value
	 *            allowed object is {@link CMSEnt }
	 * 
	 */
	public void setCmsEnt(CMSEnt cmsEnt) {
		this.cmsEnt = cmsEnt;
	}

    public String getSaveAllType() {
        return saveAllType;
    }

    public void setSaveAllType(String saveAllType) {
        this.saveAllType = saveAllType;
    }
}
