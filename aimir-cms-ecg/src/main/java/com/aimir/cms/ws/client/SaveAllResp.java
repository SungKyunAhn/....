package com.aimir.cms.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.cms.model.CMSEnt;
import com.aimir.cms.model.ErrorParam;

/**
 * <p>Java class for SaveAllResp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SaveAllResp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ErrorParam" type="{http://server.ws.cms.aimir.com/}errorParam" minOccurs="0"/>
 *         &lt;element name="CMSEnt" type="{http://server.ws.cms.aimir.com/}cmsEnt" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SaveAllResp", propOrder = {
    "errorParam", "cmsEnt"
})
public class SaveAllResp {

    @XmlElement(name = "ErrorParam")
    protected ErrorParam errorParam;
    
    @XmlElement(name = "CMSEnt")
    protected CMSEnt cmsEnt;

	/**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorParam }
     *     
     */
    public ErrorParam getErrorParam() {
		return errorParam;
	}

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorParam }
     *     
     */
	public void setErrorParam(ErrorParam errorParam) {
		this.errorParam = errorParam;
	}
	
    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link CMSEnt }
     *     
     */
    public CMSEnt getCMSEnt() {
        return cmsEnt;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link CMSEnt }
     *     
     */
    public void setCMSEnt(CMSEnt value) {
        this.cmsEnt = value;
    }

}
