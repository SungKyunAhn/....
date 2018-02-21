package com.aimir.cms.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.cms.model.DebtEnt;
import com.aimir.cms.model.ErrorParam;

/**
 * <p>Java class for AddDebtResp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddDebtResp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ErrorParam" type="{http://server.ws.cms.aimir.com/}errorParam" minOccurs="0"/>
 *         &lt;element name="DebtEnt" type="{http://server.ws.cms.aimir.com/}debtEnt" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddDebtResp", propOrder = {
    "errorParam", "debtEnt"
})
public class AddDebtResp {

    @XmlElement(name = "ErrorParam")
    protected ErrorParam errorParam;
    
    @XmlElement(name = "DebtEnt")
    protected DebtEnt debtEnt;

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
     *     {@link DebtEnt }
     *     
     */
    public DebtEnt getDebtEnt() {
        return debtEnt;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link DebtEnt }
     *     
     */
    public void setDebtEnt(DebtEnt value) {
        this.debtEnt = value;
    }

}
