
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdSendEventByFepResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdSendEventByFepResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdSendEventByFepResponse", propOrder = {
        "_return"
})
public class CmdSendEventByFepResponse {
    @XmlElement(name = "return")
    protected String _return;

    public String getReturn() {
        return _return;
    }

    public void setReturn(String _return) {
        this._return = _return;
    }
}
