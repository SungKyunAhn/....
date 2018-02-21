package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CmdSendEvent2", propOrder = {
    "eventAlertName",
    "activatorType",
    "activatorId",
    "supplierId"
})
public class CmdSendEvent2 {

    @XmlElement(name = "EventAlertName")
    protected String eventAlertName;
    @XmlElement(name = "ActivatorType")
    protected String activatorType;
    @XmlElement(name = "ActivatorId")
    protected String activatorId;    
    @XmlElement(name = "SupplierId")
    protected int supplierId;

    
    public String getEventAlertName() {
        return eventAlertName;
    }

    public void setEventAlertName(String value) {
        this.eventAlertName = value;
    }

    public String getActivatorType() {
        return activatorType;
    }

    public void setActivatorType(String value) {
        this.activatorType = value;
    }
    
    public String getActivatorID() {
        return activatorId;
    }

    public void setActivatorID(String value) {
        this.activatorId = value;
    }
    
    public int getSupplierId() {
    	return supplierId;
    }
    
    public void setSupplierId(int value) {
        this.supplierId = value;
    }
}
