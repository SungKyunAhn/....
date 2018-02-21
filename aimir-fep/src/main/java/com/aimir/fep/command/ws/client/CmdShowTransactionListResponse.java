
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.frame.service.entry.trInfoEntry;


/**
 * <p>Java class for cmdShowTransactionListResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdShowTransactionListResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://server.ws.command.fep.aimir.com/}trInfoEntry" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdShowTransactionListResponse", propOrder = {
    "_return"
})
public class CmdShowTransactionListResponse {

    @XmlElement(name = "return")
    protected trInfoEntry _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link TrInfoEntry }
     *     
     */
    public trInfoEntry getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrInfoEntry }
     *     
     */
    public void setReturn(trInfoEntry value) {
        this._return = value;
    }

}
