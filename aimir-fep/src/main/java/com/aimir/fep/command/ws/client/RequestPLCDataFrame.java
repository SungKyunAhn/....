
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.meter.parser.plc.PLCDataFrame;


/**
 * <p>Java class for requestPLCDataFrame complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="requestPLCDataFrame">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PLCDataFrame" type="{http://server.ws.command.fep.aimir.com/}plcDataFrame" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "requestPLCDataFrame", propOrder = {
    "mcuId",
    "plcDataFrame"
})
public class RequestPLCDataFrame {

    @XmlElement(name = "McuId")
    protected String mcuId;
    @XmlElement(name = "PLCDataFrame")
    protected PLCDataFrame plcDataFrame;

    /**
     * Gets the value of the mcuId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMcuId() {
        return mcuId;
    }

    /**
     * Sets the value of the mcuId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMcuId(String value) {
        this.mcuId = value;
    }

    /**
     * Gets the value of the plcDataFrame property.
     * 
     * @return
     *     possible object is
     *     {@link PlcDataFrame }
     *     
     */
    public PLCDataFrame getPLCDataFrame() {
        return plcDataFrame;
    }

    /**
     * Sets the value of the plcDataFrame property.
     * 
     * @param value
     *     allowed object is
     *     {@link PlcDataFrame }
     *     
     */
    public void setPLCDataFrame(PLCDataFrame value) {
        this.plcDataFrame = value;
    }

}
