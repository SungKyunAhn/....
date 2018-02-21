
package com.aimir.cms.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.cms.model.ErrorParam;


/**
 * <p>Java class for Exception complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Exception">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ErrorParam" type="{http://server.ws.cms.aimir.com/}errorParam" minOccurs="0"/>    
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Exception", propOrder = {
    "errorParam"
})
public class Exception {

	@XmlElement(name = "ErrorParam")
    protected ErrorParam errorParam;

	/**
     * Gets the value of the message property.
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
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorParam }
     *     
     */
	public void setErrorParam(ErrorParam errorParam) {
		this.errorParam = errorParam;
	}

}
