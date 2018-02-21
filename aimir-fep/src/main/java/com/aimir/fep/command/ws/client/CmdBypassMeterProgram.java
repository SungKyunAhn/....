
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.constants.CommonConstants.MeterProgramKind;


/**
 * <p>Java class for cmdBypassMeterProgram complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdBypassMeterProgram">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MeterSerialNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MeterProgramKind" type="{http://server.ws.command.fep.aimir.com/}meterProgramKind" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdBypassMeterProgram", propOrder = {
    "meterSerialNo",
    "meterProgramKind"
})
public class CmdBypassMeterProgram {

    @XmlElement(name = "MeterSerialNo")
    protected String meterSerialNo;
    @XmlElement(name = "MeterProgramKind")
    protected MeterProgramKind meterProgramKind;

    /**
     * Gets the value of the meterSerialNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeterSerialNo() {
        return meterSerialNo;
    }

    /**
     * Sets the value of the meterSerialNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeterSerialNo(String value) {
        this.meterSerialNo = value;
    }

    /**
     * Gets the value of the meterProgramKind property.
     * 
     * @return
     *     possible object is
     *     {@link MeterProgramKind }
     *     
     */
    public MeterProgramKind getMeterProgramKind() {
        return meterProgramKind;
    }

    /**
     * Sets the value of the meterProgramKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link MeterProgramKind }
     *     
     */
    public void setMeterProgramKind(MeterProgramKind value) {
        this.meterProgramKind = value;
    }

}
