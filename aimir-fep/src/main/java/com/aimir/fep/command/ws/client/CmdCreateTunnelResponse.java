package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdCreateTunnelResponse", propOrder = {
    "_return"
})

public class CmdCreateTunnelResponse {
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
    public ResponseMap getReturn() {
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
    public void setReturn(ResponseMap value) {
        this._return = value;
    }
}
