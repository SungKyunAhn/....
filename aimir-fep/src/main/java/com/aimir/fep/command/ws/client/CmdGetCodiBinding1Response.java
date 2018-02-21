
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.frame.service.entry.codiBindingEntry;


/**
 * <p>Java class for cmdGetCodiBinding1Response complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdGetCodiBinding1Response">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://server.ws.command.fep.aimir.com/}codiBindingEntry" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdGetCodiBinding1Response", propOrder = {
    "_return"
})
public class CmdGetCodiBinding1Response {

    @XmlElement(name = "return")
    protected codiBindingEntry _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link CodiBindingEntry }
     *     
     */
    public codiBindingEntry getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodiBindingEntry }
     *     
     */
    public void setReturn(codiBindingEntry value) {
        this._return = value;
    }

}
