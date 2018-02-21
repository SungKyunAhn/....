
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdGetMeterVersion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdGetMeterVersion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ModemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Mask" type="{http://www.w3.org/2001/XMLSchema}byte" minOccurs="0"/>
 *         &lt;element name="CP" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="MeterSerialNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CmdKDSetMeterConfig", propOrder = {
    "modemId",
    "mask",
    "cp",
    "meterId"
})
public class CmdKDSetMeterConfig {

    @XmlElement(name = "ModemId")
    protected String modemId;
    
    @XmlElement(name = "Mask")
    protected byte mask;
    
    @XmlElement(name = "CP")
    protected int cp;
    
    @XmlElement(name = "MeterSerialNo")
    protected String meterId;
    
    /**
     * Gets the value of the modemId property.
     * 
     * @return
     *     possible object is
     *     {@link byte }
     *     
     */
    public byte getMask() {
		return mask;
	}

    /**
     * Sets the value of the modemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link byte }
     *     
     */
	public void setMask(byte mask) {
		this.mask = mask;
	}

	/**
     * Gets the value of the modemId property.
     * 
     * @return
     *     possible object is
     *     {@link int }
     *     
     */
	public int getCp() {
		return cp;
	}

	/**
     * Sets the value of the modemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link int }
     *     
     */
	public void setCp(int cp) {
		this.cp = cp;
	}

	/**
     * Gets the value of the modemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public String getMeterId() {
		return meterId;
	}

	/**
     * Sets the value of the modemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}

	/**
     * Gets the value of the modemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModemId() {
        return modemId;
    }

    /**
     * Sets the value of the modemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModemId(String value) {
        this.modemId = value;
    }

}
