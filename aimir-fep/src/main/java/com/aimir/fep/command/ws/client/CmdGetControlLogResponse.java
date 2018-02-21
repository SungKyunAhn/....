package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for CmdGetControlLogResponse complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="cmdGetControlLogResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://server.ws.command.fep.aimir.com/}responseMap" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdGetControlLogResponse", propOrder = {
        "_return"
})
public class CmdGetControlLogResponse {
    @XmlElement(name = "return")
    protected ResponseMap _return;

    /**
     * Gets the value of the return property.
     *
     * @return
     *     possible object is
     *     {@link ResponseMap }
     *
     */
    public ResponseMap get_return() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     *
     * @param value
     *     allowed object is
     *     {@link ResponseMap }
     *
     */
    public void set_return(ResponseMap value) {
        this._return = value;
    }
}