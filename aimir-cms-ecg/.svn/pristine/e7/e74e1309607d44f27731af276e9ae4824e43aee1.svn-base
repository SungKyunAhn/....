package com.aimir.cms.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.cms.model.ErrorParam;
import com.aimir.cms.model.MeterEnt;

/**
 * <p>Java class for MeterCheckResp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MeterCheckResp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ErrorParam" type="{http://server.ws.cms.aimir.com/}errorParam" minOccurs="0"/>
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
@XmlType(name = "MeterCheckResp", propOrder = {
    "errorParam", "meterEnt"
})
public class MeterCheckResp {

    @XmlElement(name = "ErrorParam")
    protected ErrorParam errorParam;
    
    @XmlElement(name = "MeterEnt")
    protected MeterEnt meterEnt;
    
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
     *     {@link MeterEnt }
     *     
     */
    public MeterEnt getMeterEnt() {
        return meterEnt;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link MeterEnt }
     *     
     */
    public void setMeterEnt(MeterEnt value) {
        this.meterEnt = value;
    }

}
