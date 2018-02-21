
package com.aimir.fep.command.ws.client;

import java.util.List;

import javax.jws.WebParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "icmpPing", propOrder = {
    "commands"
})

public class IcmpPing {
	
    @XmlElement(name = "Commands")
    protected List<String> commands;
    
    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }
}
