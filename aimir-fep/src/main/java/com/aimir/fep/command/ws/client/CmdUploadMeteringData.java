
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdUploadMeteringData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdUploadMeteringData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="meterSerial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdUploadMeteringData", propOrder = {
    "meterSerial"
})
public class CmdUploadMeteringData {

    @XmlElement(name = "meterSerial")
    protected String meterSerial;


    /**
     * Gets the value of the meterSerial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeterSerial() {
        return meterSerial;
    }

    /**
     * Sets the value of the meterSerial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeterSerial(String value) {
        this.meterSerial = value;
    }
}
