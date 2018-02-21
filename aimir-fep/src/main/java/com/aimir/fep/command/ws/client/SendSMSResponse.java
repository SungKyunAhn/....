package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sendSMSResponse", propOrder = {
    "_return"
})

public class SendSMSResponse {
	@XmlElement(name = "return")
    protected String _return;

    public String getReturn() {
        return _return;
    }

    public void setReturn(String value) {
        this._return = value;
    }
}
