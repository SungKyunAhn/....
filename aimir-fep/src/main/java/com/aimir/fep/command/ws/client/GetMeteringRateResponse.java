package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMeteringRateResponse", propOrder = {
        "_return"
})
public class GetMeteringRateResponse {
    @XmlElement(name = "return")
    protected String _return;

    public String get_return() {
        return _return;
    }

    public void set_return(String value) {
        this._return = value;
    }
}
