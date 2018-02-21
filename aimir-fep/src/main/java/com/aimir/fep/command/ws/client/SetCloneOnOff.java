package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "setCloneOnOff", propOrder = {
        "modemId",
        "count"
})
public class SetCloneOnOff {
    @XmlElement(name = "ModemId")
    protected String modemId;

    @XmlElement(name = "Count")
    protected int count;


    public String getModemId() {
        return modemId;
    }

    public void setModemId(String modemId) {
        this.modemId = modemId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
